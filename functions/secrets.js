import { defineSecret } from 'firebase-functions/params';

/**
 * Secretos de Google Secret Manager.
 * Se configuran con `firebase functions:secrets:set NOMBRE` y nunca se
 * commitean al repositorio.
 */
export const CULQI_SECRET_KEY = defineSecret('CULQI_SECRET_KEY');
export const CULQI_WEBHOOK_USER = defineSecret('CULQI_WEBHOOK_USER');
export const CULQI_WEBHOOK_PASS = defineSecret('CULQI_WEBHOOK_PASS');
