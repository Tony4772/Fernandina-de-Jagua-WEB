/**
 * Prueba de humo del backend de pagos.
 * No llama a Culqi ni a Firestore reales: usa un doble en memoria y un fetch
 * simulado. Ejecuta `npm test` dentro de functions/.
 */
import assert from 'node:assert/strict';
import { createApp } from '../app.js';

// Guardamos el fetch real antes de simular el de Culqi.
const realFetch = globalThis.fetch;

/* ----------------------------- Firestore falso ----------------------------- */
const store = new Map();
let counter = 0;

const docRef = (id) => ({
  id,
  async set(data, options) {
    const next =
      options?.merge && store.has(id) ? { ...store.get(id), ...data } : { ...data };
    store.set(id, next);
  },
  async get() {
    const data = store.get(id);
    return { exists: Boolean(data), data: () => data };
  },
  async update(patch) {
    if (!store.has(id)) throw new Error(`Documento ${id} inexistente`);
    store.set(id, { ...store.get(id), ...patch });
  },
});

const fakeDb = {
  collection: () => ({ doc: (id) => docRef(id ?? `order-${++counter}`) }),
  runTransaction: async (fn) =>
    fn({
      get: (ref) => ref.get(),
      update: (ref, patch) => ref.update(patch),
    }),
};

const secrets = {
  CULQI_SECRET_KEY: 'sk_test_FAKE000000000000',
  CULQI_WEBHOOK_USER: 'culqi',
  CULQI_WEBHOOK_PASS: 'secreto',
};

/* --------------------------------- Culqi ---------------------------------- */
const culqiCalls = [];
let nextChargeResponse = () => ({
  status: 201,
  body: {
    id: 'chr_test_abc123',
    amount: 5000,
    currency_code: 'PEN',
    outcome: { type: 'venta_exitosa', code: '0', merchant_message: 'ok' },
    source: { brand: 'VISA' },
  },
});

globalThis.fetch = async (url, init) => {
  culqiCalls.push({ url, body: init?.body ? JSON.parse(init.body) : null, headers: init?.headers });
  const { status, body } = nextChargeResponse(url);
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
};

/* ------------------------------- Servidor -------------------------------- */
const app = createApp({ db: fakeDb, getSecret: (name) => secrets[name] ?? '' });
const server = app.listen(0);
const { port } = server.address();
const base = `http://127.0.0.1:${port}/api/payments`;

const post = async (path, body, headers = {}) => {
  const response = await realFetch(`${base}${path}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', ...headers },
    body: JSON.stringify(body),
  });
  return { status: response.status, body: await response.json() };
};

const get = async (path) => {
  const response = await realFetch(`${base}${path}`);
  return { status: response.status, body: await response.json() };
};

const customer = {
  name: 'Ana Torres',
  email: 'ana@example.com',
  phone: '999888777',
  address: 'Av. La Paz 123',
  district: 'San Miguel',
};

const results = [];
const check = async (name, fn) => {
  try {
    await fn();
    results.push(`✔ ${name}`);
  } catch (error) {
    results.push(`✘ ${name}: ${error.message}`);
    process.exitCode = 1;
  }
};

try {
  await check('health responde con el modo de las llaves', async () => {
    const { status, body } = await get('/health');
    assert.equal(status, 200);
    assert.equal(body.mode, 'test');
    assert.equal(body.products, 8);
  });

  await check('rechaza un carrito vacío', async () => {
    const { status, body } = await post('/orders', { items: [], customer });
    assert.equal(status, 400);
    assert.match(body.message, /carrito/i);
  });

  await check('rechaza un producto inexistente', async () => {
    const { status } = await post('/orders', {
      items: [{ id: 'producto-falso', quantity: 1 }],
      customer,
    });
    assert.equal(status, 400);
  });

  await check('rechaza un correo inválido', async () => {
    const { status } = await post('/orders', {
      items: [{ id: 'barra-70-cacao-origen', quantity: 1 }],
      customer: { ...customer, email: 'no-es-correo' },
    });
    assert.equal(status, 400);
  });

  let orderId;
  await check('crea el pedido con el monto del servidor (ignora precios del cliente)', async () => {
    const { status, body } = await post('/orders', {
      // El precio enviado por el navegador se ignora por completo.
      items: [
        { id: 'barra-70-cacao-origen', quantity: 2, priceValue: 1 },
        { id: 'bombones-artesanales-surtidos', quantity: 1, priceValue: 1 },
      ],
      customer,
    });
    assert.equal(status, 201);
    assert.equal(body.amount, 1800 * 2 + 3200);
    assert.match(body.orderNumber, /^FJ-\d{8}-[0-9A-F]{6}$/);
    orderId = body.orderId;
  });

  await check('rechaza un token con formato inválido', async () => {
    const { status } = await post(`/orders/${orderId}/pay`, { token: 'no-es-token' });
    assert.equal(status, 400);
  });

  await check('cobra con la llave secreta y marca el pedido como pagado', async () => {
    const { status, body } = await post(`/orders/${orderId}/pay`, { token: 'tkn_test_ABC123' });
    assert.equal(status, 200);
    assert.equal(body.status, 'paid');

    const chargeCall = culqiCalls.at(-1);
    assert.equal(chargeCall.url, 'https://api.culqi.com/v2/charges');
    assert.equal(chargeCall.headers.Authorization, `Bearer ${secrets.CULQI_SECRET_KEY}`);
    assert.equal(chargeCall.body.amount, 6800);
    assert.equal(chargeCall.body.source_id, 'tkn_test_ABC123');
    assert.equal(chargeCall.body.metadata.order_id, orderId);

    assert.equal(store.get(orderId).status, 'paid');
    assert.equal(store.get(orderId).charge.id, 'chr_test_abc123');
  });

  await check('no cobra dos veces el mismo pedido', async () => {
    const before = culqiCalls.length;
    const { status } = await post(`/orders/${orderId}/pay`, { token: 'tkn_test_ABC123' });
    assert.equal(status, 409);
    assert.equal(culqiCalls.length, before);
  });

  await check('marca el pedido como fallido cuando el banco rechaza', async () => {
    nextChargeResponse = () => ({
      status: 422,
      body: {
        object: 'error',
        type: 'card_error',
        code: 'card_declined',
        decline_code: 'insufficient_funds',
        user_message: 'Tarjeta sin fondos suficientes.',
      },
    });

    const created = await post('/orders', {
      items: [{ id: 'trufas-cafe-canela', quantity: 1 }],
      customer,
    });
    const { status, body } = await post(`/orders/${created.body.orderId}/pay`, {
      token: 'tkn_test_DECLINED',
    });

    assert.equal(status, 402);
    assert.equal(body.status, 'failed');
    assert.match(body.message, /fondos/i);
    assert.equal(store.get(created.body.orderId).status, 'failed');

    // Restaura la respuesta aprobada para las siguientes pruebas.
    nextChargeResponse = () => ({
      status: 201,
      body: {
        id: 'chr_test_abc123',
        amount: 5000,
        currency_code: 'PEN',
        outcome: { type: 'venta_exitosa', code: '0' },
        source: { brand: 'VISA' },
      },
    });
  });

  await check('el webhook exige credenciales', async () => {
    const { status } = await post('/webhooks/culqi', { type: 'charge.creation.succeeded', data: { id: 'chr_1' } });
    assert.equal(status, 401);
  });

  await check('el webhook marca pagado tras re-consultar el cargo en Culqi', async () => {
    const created = await post('/orders', {
      items: [{ id: 'nibs-cacao-acaramelados', quantity: 1 }],
      customer,
    });
    const order = created.body;

    nextChargeResponse = () => ({
      status: 200,
      body: {
        id: 'chr_test_webhook',
        amount: order.amount,
        currency_code: 'PEN',
        outcome: { type: 'venta_exitosa', code: '0' },
        metadata: { order_id: order.orderId },
        source: { brand: 'VISA' },
      },
    });

    const auth = `Basic ${Buffer.from('culqi:secreto').toString('base64')}`;
    const { status } = await post(
      '/webhooks/culqi',
      { type: 'charge.creation.succeeded', data: JSON.stringify({ id: 'chr_test_webhook' }) },
      { Authorization: auth },
    );

    assert.equal(status, 200);
    assert.equal(store.get(order.orderId).status, 'paid');
  });

  await check('el webhook ignora cargos con monto distinto al pedido', async () => {
    const created = await post('/orders', {
      items: [{ id: 'tableta-sal-de-maras', quantity: 1 }],
      customer,
    });
    nextChargeResponse = () => ({
      status: 200,
      body: {
        id: 'chr_test_mismatch',
        amount: 100,
        currency_code: 'PEN',
        outcome: { type: 'venta_exitosa' },
        metadata: { order_id: created.body.orderId },
      },
    });

    const auth = `Basic ${Buffer.from('culqi:secreto').toString('base64')}`;
    const { status, body } = await post(
      '/webhooks/culqi',
      { type: 'charge.creation.succeeded', data: { id: 'chr_test_mismatch' } },
      { Authorization: auth },
    );

    assert.equal(status, 200);
    assert.equal(body.mismatch, true);
    assert.equal(store.get(created.body.orderId).status, 'pending');
  });

  await check('la consulta de estado exige el email del pedido', async () => {
    const created = await post('/orders', {
      items: [{ id: 'barra-85-cacao-intenso', quantity: 1 }],
      customer,
    });
    const ok = await get(`/orders/${created.body.orderId}/status?email=${customer.email}`);
    assert.equal(ok.status, 200);
    assert.equal(ok.body.status, 'pending');

    const wrong = await get(`/orders/${created.body.orderId}/status?email=otro@example.com`);
    assert.equal(wrong.status, 404);
  });
} finally {
  server.close();
  console.log(results.join('\n'));
  console.log(process.exitCode ? '\n❌ Algunas pruebas fallaron.' : '\n✅ Todas las pruebas pasaron.');
}
