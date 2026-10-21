/**
 * Carga e invoca Culqi Checkout (Custom Checkout multipago).
 * Documentación: https://docs.culqi.com/es/documentacion/checkout/checkout-custom
 *
 * El checkout tokeniza la tarjeta o el teléfono de Yape directamente contra los
 * servidores de Culqi: los datos sensibles nunca pasan por esta app ni por
 * nuestro backend. Solo recibimos el `token.id`, que enviamos al servidor para
 * crear el cargo con la llave secreta.
 */

const CULQI_SCRIPT_URL = 'https://js.culqi.com/checkout-js';

interface CulqiCheckoutInstance {
  open: () => void;
  close: () => void;
  token?: { id?: string };
  order?: { id?: string };
  error?: { user_message?: string; merchant_message?: string; type?: string };
  culqi?: () => void;
}

interface CulqiCheckoutConstructor {
  new (publicKey: string, config: Record<string, unknown>): CulqiCheckoutInstance;
}

declare global {
  interface Window {
    CulqiCheckout?: CulqiCheckoutConstructor;
  }
}

let scriptPromise: Promise<void> | null = null;

export function loadCulqiCheckout(): Promise<void> {
  if (typeof window === 'undefined') return Promise.resolve();
  if (typeof window.CulqiCheckout === 'function') return Promise.resolve();
  if (scriptPromise) return scriptPromise;

  scriptPromise = new Promise<void>((resolve, reject) => {
    const existing = document.querySelector<HTMLScriptElement>(`script[src="${CULQI_SCRIPT_URL}"]`);
    const script = existing ?? document.createElement('script');

    const handleLoad = () => {
      if (typeof window.CulqiCheckout === 'function') {
        resolve();
      } else {
        scriptPromise = null;
        reject(new Error('Culqi Checkout no se inicializó correctamente.'));
      }
    };

    script.addEventListener('load', handleLoad, { once: true });
    script.addEventListener(
      'error',
      () => {
        scriptPromise = null;
        reject(new Error('No se pudo cargar Culqi Checkout. Revisa tu conexión.'));
      },
      { once: true },
    );

    if (!existing) {
      script.src = CULQI_SCRIPT_URL;
      script.async = true;
      document.body.appendChild(script);
    }
  });

  return scriptPromise;
}

export interface OpenCheckoutOptions {
  publicKey: string;
  amountInCents: number;
  title: string;
  email: string;
  firstName: string;
  lastName: string;
  onToken: (tokenId: string) => void;
  onError: (message: string) => void;
}

export async function openCulqiCheckout(options: OpenCheckoutOptions): Promise<void> {
  await loadCulqiCheckout();

  const CulqiCheckout = window.CulqiCheckout;
  if (typeof CulqiCheckout !== 'function') {
    throw new Error('Culqi Checkout no está disponible en este navegador.');
  }

  const settings = {
    title: options.title,
    currency: 'PEN',
    amount: options.amountInCents,
  };

  const client = {
    email: options.email,
    firstName: options.firstName,
    lastName: options.lastName,
  };

  const paymentMethods = {
    tarjeta: true,
    yape: true,
    billetera: false,
    bancaMovil: false,
    agente: false,
    cuotealo: false,
  };

  const checkoutOptions = {
    lang: 'es',
    installments: false,
    modal: true,
    paymentMethods,
    paymentMethodsSort: Object.keys(paymentMethods).filter(
      (method) => paymentMethods[method as keyof typeof paymentMethods],
    ),
  };

  const appearance = {
    theme: 'default',
    menuType: 'sidebar',
    buttonCardPayText: 'Pagar pedido',
    hiddenEmail: true,
    defaultStyle: {
      bannerColor: '#261814',
      buttonBackground: '#775a00',
      menuColor: '#775a00',
      linksColor: '#775a00',
      buttonTextColor: '#ffffff',
      priceColor: '#261814',
    },
  };

  const instance = new CulqiCheckout(options.publicKey, {
    settings,
    client,
    options: checkoutOptions,
    appearance,
  });

  let handled = false;

  instance.culqi = () => {
    if (handled) return;

    if (instance.token?.id) {
      handled = true;
      const tokenId = instance.token.id;
      try {
        instance.close();
      } catch {
        /* el checkout ya pudo haberse cerrado */
      }
      options.onToken(tokenId);
      return;
    }

    if (instance.order) {
      // No usamos órdenes (PagoEfectivo/billeteras) en este flujo.
      handled = true;
      try {
        instance.close();
      } catch {
        /* noop */
      }
      options.onError('Ese medio de pago no está disponible. Usa tarjeta o Yape.');
      return;
    }

    options.onError(
      instance.error?.user_message ||
        instance.error?.merchant_message ||
        'No se pudo completar el pago. Revisa los datos e intenta nuevamente.',
    );
  };

  instance.open();
}
