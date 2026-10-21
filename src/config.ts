/**
 * Configuración pública del checkout.
 *
 * - `VITE_CULQI_PUBLIC_KEY`: llave pública de Culqi (pk_live_/pk_test_).
 *   Es pública por diseño: solo sirve para tokenizar tarjetas en el navegador.
 *   La llave secreta vive únicamente en el backend (Secret Manager).
 * - `VITE_PAYMENTS_API_BASE`: base del backend de pagos. Por defecto se usa el
 *   rewrite de Firebase Hosting `/api/payments/**`, que evita CORS.
 */

const env = import.meta.env as Record<string, string | undefined>;

const trim = (value: string | undefined) => (value ?? '').trim();

export const CULQI_PUBLIC_KEY = trim(env.VITE_CULQI_PUBLIC_KEY) || 'pk_live_U6Rxa1sMaLcZKpcA';

export const PAYMENTS_API_BASE = (trim(env.VITE_PAYMENTS_API_BASE) || '/api/payments').replace(/\/+$/, '');

export const CURRENCY = 'PEN';

export const MAX_ITEM_QUANTITY = 20;

/** Límite de Yape por operación según Culqi: S/ 2000. */
export const YAPE_MAX_AMOUNT_CENTS = 200_000;

export type YapePaymentMethod = 'card' | 'yape';

export const isCulqiConfigured = CULQI_PUBLIC_KEY.startsWith('pk_live_') || CULQI_PUBLIC_KEY.startsWith('pk_test_');

export const isCulqiTestMode = CULQI_PUBLIC_KEY.startsWith('pk_test_');
