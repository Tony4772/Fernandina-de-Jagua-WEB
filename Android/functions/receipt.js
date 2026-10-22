const { HttpsError } = require("firebase-functions/v2/https");

const BUCKET = "fernandina-de-jagua.firebasestorage.app";
const COMPANY = {
  ruc: "20614121042",
  razonSocial: "EBYZOM E.I.R.L",
  nombreComercial: "Fernandina de Jagua Chocolates",
  address: {
    direccion: "JR. COMANDANTE JUAN GUILLERMO MOORE 296",
    provincia: "LIMA",
    departamento: "LIMA",
    distrito: "SAN MIGUEL",
    ubigueo: "150136",
  },
};

const UNITS = ["", "UNO", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE"];
const TEENS = ["DIEZ", "ONCE", "DOCE", "TRECE", "CATORCE", "QUINCE", "DIECISEIS", "DIECISIETE", "DIECIOCHO", "DIECINUEVE"];
const TENS = ["", "", "VEINTE", "TREINTA", "CUARENTA", "CINCUENTA", "SESENTA", "SETENTA", "OCHENTA", "NOVENTA"];
const HUNDREDS = ["", "CIENTO", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS", "QUINIENTOS", "SEISCIENTOS", "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS"];

function belowThousand(value) {
  if (value === 0) return "";
  if (value === 100) return "CIEN";
  const hundreds = Math.floor(value / 100);
  const rest = value % 100;
  let tens = "";
  if (rest > 0 && rest < 10) tens = UNITS[rest];
  else if (rest >= 10 && rest < 20) tens = TEENS[rest - 10];
  else if (rest === 20) tens = "VEINTE";
  else if (rest > 20 && rest < 30) tens = `VEINTI${UNITS[rest - 20]}`;
  else if (rest >= 30) {
    tens = TENS[Math.floor(rest / 10)];
    if (rest % 10) tens += ` Y ${UNITS[rest % 10]}`;
  }
  return [HUNDREDS[hundreds], tens].filter(Boolean).join(" ");
}

function amountInWords(total) {
  const whole = Math.floor(total + 1e-6);
  const cents = String(Math.round((total - whole) * 100)).padStart(2, "0");
  const words = whole === 0 ? "CERO" : belowThousand(whole).replace(/UNO$/, "UN");
  return `SON ${words} CON ${cents}/100 SOLES`;
}

function limaNow() {
  const parts = new Intl.DateTimeFormat("en-CA", {
    timeZone: "America/Lima",
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hourCycle: "h23",
  }).formatToParts(new Date());
  const value = (type) => parts.find((part) => part.type === type).value;
  return `${value("year")}-${value("month")}-${value("day")}T${value("hour")}:${value("minute")}:${value("second")}-05:00`;
}

function round2(value) {
  return Math.round(value * 100) / 100;
}

function apiMessage(body) {
  if (!body || typeof body !== "object") return "APISperu no emitió el comprobante.";
  const sunat = body.sunatResponse;
  const nested = sunat && sunat.error && (sunat.error.message || sunat.error.code);
  const cdr = sunat && sunat.cdrResponse && sunat.cdrResponse.description;
  return String(body.message || body.error || nested || cdr || "APISperu no emitió el comprobante.").slice(0, 180);
}

async function apisperu(token, path, payload) {
  const response = await fetch(`https://facturacion.apisperu.com/api/v1${path}`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
      Accept: "application/json, application/pdf",
    },
    body: JSON.stringify(payload),
  });
  const bytes = Buffer.from(await response.arrayBuffer());
  const type = response.headers.get("content-type") || "";
  const looksLikePdf = bytes.slice(0, 4).toString() === "%PDF";
  if (type.includes("pdf") || looksLikePdf) {
    if (!response.ok || bytes.length < 100) {
      throw new HttpsError("failed-precondition", "APISperu no devolvió el PDF.");
    }
    return { pdf: bytes };
  }
  let body = {};
  try {
    body = JSON.parse(bytes.toString() || "{}");
  } catch (error) {
    body = {};
  }
  if (!response.ok) {
    throw new HttpsError("failed-precondition", apiMessage(body));
  }
  return { body };
}

function bucket() {
  const { Storage } = require("@google-cloud/storage");
  return new Storage().bucket(BUCKET);
}

async function reserveNumber(series) {
  const file = bucket().file(`private/invoice-series/${series}.json`);
  for (let attempt = 0; attempt < 6; attempt += 1) {
    let generation = 0;
    let current = 0;
    const [exists] = await file.exists();
    if (exists) {
      const [metadata] = await file.getMetadata();
      generation = Number(metadata.generation);
      const [contents] = await file.download();
      current = Number(JSON.parse(contents.toString()).last) || 0;
    }
    const next = current + 1;
    try {
      await file.save(JSON.stringify({ last: next }), {
        resumable: false,
        contentType: "application/json",
        preconditionOpts: { ifGenerationMatch: generation },
      });
      return String(next);
    } catch (error) {
      if (attempt === 5) {
        throw new HttpsError("aborted", "No se pudo reservar el número del comprobante.");
      }
    }
  }
  throw new HttpsError("aborted", "No se pudo reservar el número del comprobante.");
}

function buildDocument(input, series, number) {
  const details = input.lines.map((line, index) => {
    const valorUnitario = round2(line.unitGross / 1.18);
    const valorVenta = round2(valorUnitario * line.quantity);
    const igv = round2(valorVenta * 0.18);
    return {
      codProducto: `P${String(index + 1).padStart(3, "0")}`,
      unidad: "NIU",
      descripcion: line.description,
      cantidad: line.quantity,
      mtoValorUnitario: valorUnitario,
      mtoValorVenta: valorVenta,
      mtoBaseIgv: valorVenta,
      porcentajeIgv: 18,
      igv,
      tipAfeIgv: 10,
      totalImpuestos: igv,
      mtoPrecioUnitario: round2(line.unitGross),
    };
  });

  let gravadas = round2(details.reduce((sum, line) => sum + line.mtoValorVenta, 0));
  let igv = round2(details.reduce((sum, line) => sum + line.igv, 0));
  const paid = round2(input.lines.reduce((sum, line) => sum + line.unitGross * line.quantity, 0));
  const drift = round2(paid - (gravadas + igv));
  if (Math.abs(drift) > 0.05) {
    throw new HttpsError("failed-precondition", "El IGV del comprobante no cuadra con el pago.");
  }
  if (drift !== 0) {
    const last = details[details.length - 1];
    last.igv = round2(last.igv + drift);
    last.totalImpuestos = last.igv;
    igv = round2(igv + drift);
  }

  return {
    ublVersion: "2.1",
    tipoOperacion: "0101",
    tipoDoc: input.tipoDoc,
    serie: series,
    correlativo: number,
    fechaEmision: limaNow(),
    formaPago: { moneda: "PEN", tipo: "Contado" },
    tipoMoneda: "PEN",
    client: {
      tipoDoc: input.customerType,
      numDoc: input.customerNumber,
      rznSocial: input.customerName,
    },
    company: COMPANY,
    mtoOperGravadas: gravadas,
    mtoIGV: igv,
    valorVenta: gravadas,
    totalImpuestos: igv,
    subTotal: paid,
    mtoImpVenta: paid,
    details,
    legends: [{ code: "1000", value: amountInWords(paid) }],
  };
}

async function loadCharge(secret, chargeId) {
  const response = await fetch(`https://api.culqi.com/v2/charges/${chargeId}`, {
    headers: { Authorization: `Bearer ${secret}` },
  });
  const body = await response.json().catch(() => ({}));
  if (!response.ok || !String(body.id || "").startsWith("chr_")) {
    throw new HttpsError("failed-precondition", "Ese pago no existe.");
  }
  return body;
}

function readLines(data) {
  const items = Array.isArray(data.items) ? data.items : [];
  if (items.length < 1 || items.length > 40) {
    throw new HttpsError("invalid-argument", "El comprobante no tiene un detalle válido.");
  }
  return items.map((item) => {
    const quantity = Number(item.quantity);
    const unitGross = round2(Number(item.unitPrice));
    const description = String(item.description || "").trim().slice(0, 250);
    if (!Number.isInteger(quantity) || quantity < 1 || quantity > 99 || unitGross <= 0 || !description) {
      throw new HttpsError("invalid-argument", "Hay un producto inválido en el comprobante.");
    }
    return { description, quantity, unitGross };
  });
}

async function issueTaxReceipt(request, secrets) {
  if (!request.auth || request.auth.token.firebase.sign_in_provider === "anonymous") {
    throw new HttpsError("unauthenticated", "Entra a tu cuenta para pedir el comprobante.");
  }

  const kind = String(request.data.kind || "");
  const chargeId = String(request.data.chargeId || "");
  const name = String(request.data.customerName || "").trim().slice(0, 150);
  const document = String(request.data.document || "").replace(/\D/g, "");
  if (!/^chr_(live|test)_[A-Za-z0-9]+$/.test(chargeId)) {
    throw new HttpsError("invalid-argument", "El pago del comprobante no es válido.");
  }
  if (name.length < 3) {
    throw new HttpsError("invalid-argument", "Escribe el nombre o la razón social.");
  }

  let tipoDoc = "03";
  let series = "B001";
  let customerType = "0";
  let customerNumber = "00000000";
  if (kind === "boleta") {
    if (document.length === 0) {
      customerType = "0";
      customerNumber = "00000000";
    } else if (/^\d{8}$/.test(document)) {
      customerType = "1";
      customerNumber = document;
    } else {
      throw new HttpsError("invalid-argument", "El DNI de la boleta debe tener 8 dígitos.");
    }
  } else if (kind === "factura") {
    tipoDoc = "01";
    series = "F001";
    if (!/^((10|15|16|17|20)\d{9})$/.test(document)) {
      throw new HttpsError("invalid-argument", "El RUC de la factura debe tener 11 dígitos.");
    }
    customerType = "6";
    customerNumber = document;
  } else {
    throw new HttpsError("invalid-argument", "Elige boleta o factura.");
  }

  const lines = readLines(request.data);
  const paidCents = Math.round(lines.reduce((sum, line) => sum + line.unitGross * line.quantity, 0) * 100);
  const charge = await loadCharge(secrets.culqi, chargeId);
  if (Number(charge.amount) !== paidCents || charge.currency_code !== "PEN") {
    throw new HttpsError("failed-precondition", "El comprobante no coincide con el pago.");
  }
  if (kind === "boleta" && paidCents >= 70000 && customerType !== "1") {
    throw new HttpsError("invalid-argument", "Desde S/ 700 la boleta necesita DNI.");
  }

  const record = bucket().file(`private/invoices/${chargeId}.json`);
  const [already] = await record.exists();
  let payload;
  if (already) {
    const [contents] = await record.download();
    const saved = JSON.parse(contents.toString());
    if (!saved.payload) {
      throw new HttpsError("aborted", "El comprobante de este pago se está emitiendo. Espera un momento.");
    }
    payload = saved.payload;
  } else {
    const number = await reserveNumber(series);
    payload = buildDocument({ lines, tipoDoc, customerType, customerNumber, customerName: name }, series, number);
    try {
      await record.save(JSON.stringify({ status: "pending" }), {
        resumable: false,
        contentType: "application/json",
        preconditionOpts: { ifGenerationMatch: 0 },
      });
    } catch (error) {
      const [contents] = await record.download();
      const saved = JSON.parse(contents.toString());
      if (!saved.payload) {
        throw new HttpsError("aborted", "El comprobante de este pago se está emitiendo. Espera un momento.");
      }
      payload = saved.payload;
    }
    if (payload.correlativo === number) {
      const sent = await apisperu(secrets.apisperu, "/invoice/send", payload);
      const accepted = sent.body && sent.body.sunatResponse && sent.body.sunatResponse.success;
      if (!accepted) {
        await record.delete({ ignoreNotFound: true }).catch(() => {});
        throw new HttpsError("failed-precondition", apiMessage(sent.body));
      }
      await record.save(JSON.stringify({ payload }), {
        resumable: false,
        contentType: "application/json",
      });
    }
  }

  const printed = await apisperu(secrets.apisperu, "/invoice/pdf", payload);
  if (!printed.pdf) {
    throw new HttpsError("failed-precondition", "APISperu no devolvió el PDF.");
  }
  return {
    kind: payload.tipoDoc === "01" ? "factura" : "boleta",
    series: payload.serie,
    number: payload.correlativo,
    pdfBase64: printed.pdf.toString("base64"),
  };
}

module.exports = { issueTaxReceipt };
