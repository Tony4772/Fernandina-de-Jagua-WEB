/**
 * Punto de entrada de Cloud Functions.
 *
 * Toda la lógica vive en `app.js` (testeable) y aquí solo se conecta con
 * Firestore y con los secretos de Secret Manager.
 *
 * Reglas de oro:
 *  1. La llave secreta de Culqi NUNCA sale del servidor.
 *  2. El monto del pedido se recalcula en `app.js` con `catalog.json`; se
 *     ignora cualquier precio que envíe el navegador.
 *  3. Cada pedido se reserva en Firestore dentro de una transacción antes de
 *     crear el cargo, para que un doble clic no cobre dos veces.
 */
import { initializeApp } from 'firebase-admin/app';
import { getFirestore } from 'firebase-admin/firestore';
import { onRequest } from 'firebase-functions/v2/https';
import { createApp } from './app.js';
import { CULQI_SECRET_KEY, CULQI_WEBHOOK_PASS, CULQI_WEBHOOK_USER, CMS_ADMIN_PASSWORD } from './secrets.js';
import { createCmsApp } from './cms.js';

initializeApp();

const SECRETS = {
  CULQI_SECRET_KEY,
  CULQI_WEBHOOK_USER,
  CULQI_WEBHOOK_PASS,
};

const app = createApp({
  db: getFirestore(),
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
