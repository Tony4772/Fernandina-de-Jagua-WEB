/**
 * Punto de entrada de Cloud Functions.
 *
 * Toda la lógica vive en `app.js` (testeable) y aquí solo se conecta con
 * Firestore y con los secretos de Secret Manager.
 *
 * Reglas de oro:
 *  1. La llave secreta de Culqi NUNCA sale del servidor.
 *  2. El monto del pedido se recalcula en `app.js` con el catálogo CMS de
 *     Firestore; se ignora cualquier precio que envíe el navegador.
 *  3. Cada pedido se reserva en Firestore dentro de una transacción antes de
 *     crear el cargo, para que un doble clic no cobre dos veces.
 */
import { initializeApp } from 'firebase-admin/app';
import { getFirestore } from 'firebase-admin/firestore';
import { onRequest } from 'firebase-functions/v2/https';
import { createApp, PRODUCTS as FALLBACK_PRODUCTS } from './app.js';
import { CULQI_SECRET_KEY, CULQI_WEBHOOK_PASS, CULQI_WEBHOOK_USER, CMS_ADMIN_PASSWORD } from './secrets.js';
import { createCmsApp } from './cms.js';

initializeApp();
const db = getFirestore();

async function getCmsProducts() {
  const snapshot = await db.collection('cms').doc('site').get();
  if (!snapshot.exists) return FALLBACK_PRODUCTS;

  const rawProducts = snapshot.data()?.products;
  if (!Array.isArray(rawProducts)) {
    throw new Error('El catálogo CMS no tiene una lista de productos válida.');
  }

  const products = new Map();
  for (const raw of rawProducts) {
    const id = typeof raw?.id === 'string' ? raw.id.trim() : '';
    const name = typeof raw?.name === 'string' ? raw.name.trim() : '';
    const priceValue = Number(raw?.priceValue);
    if (!id || !name || !Number.isFinite(priceValue) || priceValue <= 0) {
      throw new Error(`Producto CMS inválido: ${id || 'sin-id'}`);
    }
    products.set(id, {
      id,
      name,
      netWeight: typeof raw.netWeight === 'string' ? raw.netWeight : '',
      priceValue,
      amountInCents: Math.round(priceValue * 100),
    });
  }
  return products;
}

const SECRETS = {
  CULQI_SECRET_KEY,
  CULQI_WEBHOOK_USER,
  CULQI_WEBHOOK_PASS,
};

const app = createApp({
  db,
  getProducts: getCmsProducts,
  getSecret: (name) => {
    const param = SECRETS[name];
    if (!param) return '';
    try {
      return param.value() ?? '';
    } catch {
      return '';
    }
  },
});

export const payments = onRequest(
  {
    region: 'us-central1',
    memory: '256MiB',
    timeoutSeconds: 60,
    maxInstances: 10,
    invoker: 'public',
    secrets: [CULQI_SECRET_KEY, CULQI_WEBHOOK_USER, CULQI_WEBHOOK_PASS],
  },
  app,
);

const cmsApp = createCmsApp({
  getSecret: (name) => {
    const param = { CMS_ADMIN_PASSWORD }[name];
    if (!param) return '';
    try {
      return param.value() ?? '';
    } catch {
      return '';
    }
  },
});

export const cms = onRequest(
  {
    region: 'us-central1',
    memory: '256MiB',
    timeoutSeconds: 30,
    maxInstances: 10,
    invoker: 'public',
    secrets: [CMS_ADMIN_PASSWORD],
  },
  cmsApp,
);
