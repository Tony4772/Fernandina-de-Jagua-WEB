const { onCall, HttpsError } = require("firebase-functions/v2/https");
const { defineSecret } = require("firebase-functions/params");
const { issueTaxReceipt } = require("./receipt");

const culqiSecret = defineSecret("CULQI_SECRET_KEY");
const apisperuToken = defineSecret("APISPERU_TOKEN");

exports.createCulqiCharge = onCall(
  { secrets: [culqiSecret], region: "us-central1" },
  async (request) => {
    if (!request.auth || request.auth.token.firebase.sign_in_provider === "anonymous") {
      throw new HttpsError("unauthenticated", "Entra a tu cuenta para pagar.");
    }

    const sourceId = String(request.data.sourceId || "");
    const email = String(request.data.email || "").trim();
    const amount = Number(request.data.amount);
    const description = String(request.data.description || "Pedido Fernandina").slice(0, 80);

    if (!/^((tkn|ype)_(live|test)_[A-Za-z0-9]+)$/.test(sourceId)) {
      throw new HttpsError("invalid-argument", "El medio de pago no es válido.");
    }
    if (!email.includes("@") || email.length > 200) {
      throw new HttpsError("invalid-argument", "El correo del pago no es válido.");
    }
    if (!Number.isInteger(amount) || amount < 100 || amount > 10000000) {
      throw new HttpsError("invalid-argument", "El monto del cobro no es válido.");
    }
    if (sourceId.startsWith("ype_") && amount > 200000) {
      throw new HttpsError("invalid-argument", "Yape acepta hasta S/ 2000 por pago.");
    }

    const response = await fetch("https://api.culqi.com/v2/charges", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${culqiSecret.value()}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        amount,
        currency_code: "PEN",
        email,
        source_id: sourceId,
        description,
        capture: true,
      }),
    });

    const body = await response.json().catch(() => ({}));
    const chargeId = body && body.id;
    const paid = response.ok && typeof chargeId === "string" && chargeId.startsWith("chr_");
    if (!paid) {
      const message = body.user_message || body.merchant_message || "Culqi no aprobó el pago.";
      throw new HttpsError("failed-precondition", String(message).slice(0, 180));
    }

    return { chargeId, amount };
  }
);

exports.issueTaxReceipt = onCall(
  { secrets: [culqiSecret, apisperuToken], region: "us-central1", timeoutSeconds: 120 },
  (request) => issueTaxReceipt(request, {
    culqi: culqiSecret.value(),
    apisperu: apisperuToken.value(),
  })
);
