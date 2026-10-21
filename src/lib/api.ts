import { PAYMENTS_API_BASE } from '../config';
import { CartItem, toOrderItems } from './cart';

export interface CustomerInput {
  name: string;
  email: string;
  phone: string;
  address?: string;
  district?: string;
  notes?: string;
}

export interface CreatedOrder {
  orderId: string;
  orderNumber: string;
  amount: number; // céntimos
  currency: string;
  expiresAt?: string;
}

export interface PaidOrder {
  status: 'paid';
  orderNumber: string;
  amount: number;
  currency: string;
  chargeId?: string;
  email?: string;
  message?: string;
}

export interface PaymentFailure {
  status: 'failed';
  orderNumber?: string;
  message: string;
}

export class PaymentApiError extends Error {
  status: number;

  constructor(message: string, status = 0) {
    super(message);
    this.name = 'PaymentApiError';
    this.status = status;
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response;
  try {
    response = await fetch(`${PAYMENTS_API_BASE}${path}`, {
      ...init,
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
        ...(init?.headers ?? {}),
      },
    });
  } catch {
    throw new PaymentApiError(
      'No pudimos conectar con el sistema de pagos. Revisa tu conexión e intenta nuevamente.',
    );
  }

  const text = await response.text();
  let payload: any = null;
  try {
    payload = text ? JSON.parse(text) : null;
  } catch {
    payload = null;
  }

  if (!response.ok) {
    const message =
      payload?.message ||
      (response.status === 404
        ? 'El servicio de pagos no está disponible en este momento.'
        : 'No pudimos procesar el pago. Intenta nuevamente.');
    throw new PaymentApiError(message, response.status);
  }

  return payload as T;
}

export function createOrder(items: CartItem[], customer: CustomerInput): Promise<CreatedOrder> {
  return request<CreatedOrder>('/orders', {
    method: 'POST',
    body: JSON.stringify({ items: toOrderItems(items), customer }),
  });
}

export function payOrder(orderId: string, token: string): Promise<PaidOrder> {
  return request<PaidOrder>(`/orders/${encodeURIComponent(orderId)}/pay`, {
    method: 'POST',
    body: JSON.stringify({ token }),
  });
}

export async function getOrderStatus(
  orderId: string,
  email: string,
): Promise<{ orderNumber: string; status: string; amount: number; currency: string }> {
  return request(`/orders/${encodeURIComponent(orderId)}/status?email=${encodeURIComponent(email)}`);
}
