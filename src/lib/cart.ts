import { MAX_ITEM_QUANTITY } from '../config';

export { MAX_ITEM_QUANTITY };
import { Product } from '../types';

const CART_STORAGE_KEY = 'fernandina_cart_v1';

export interface CartItem {
  productId: string;
  name: string;
  priceValue: number;
  image: string;
  quantity: number;
}

export function loadCart(): CartItem[] {
  try {
    const raw = localStorage.getItem(CART_STORAGE_KEY);
    if (!raw) return [];
    const parsed = JSON.parse(raw);
    if (!Array.isArray(parsed)) return [];
    return parsed
      .filter(
        (item): item is CartItem =>
          Boolean(item) &&
          typeof item.productId === 'string' &&
          typeof item.name === 'string' &&
          Number.isFinite(Number(item.priceValue)) &&
          Number.isFinite(Number(item.quantity)),
      )
      .map((item) => ({
        productId: item.productId,
        name: item.name,
        priceValue: Number(item.priceValue),
        image: typeof item.image === 'string' ? item.image : '',
        quantity: Math.min(Math.max(Math.trunc(Number(item.quantity)), 1), MAX_ITEM_QUANTITY),
      }));
  } catch (error) {
    console.error('No se pudo leer el carrito guardado', error);
    return [];
  }
}

export function saveCart(items: CartItem[]): void {
  try {
    if (items.length === 0) {
      localStorage.removeItem(CART_STORAGE_KEY);
      return;
    }
    localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(items));
  } catch (error) {
    console.error('No se pudo guardar el carrito', error);
  }
}

export function addToCart(items: CartItem[], product: Product, quantity = 1): CartItem[] {
  const existing = items.find((item) => item.productId === product.id);
  if (existing) {
    return items.map((item) =>
      item.productId === product.id
        ? { ...item, quantity: Math.min(item.quantity + quantity, MAX_ITEM_QUANTITY) }
        : item,
    );
  }
  return [
    ...items,
    {
      productId: product.id,
      name: product.name,
      priceValue: product.priceValue,
      image: product.image,
      quantity: Math.min(Math.max(quantity, 1), MAX_ITEM_QUANTITY),
    },
  ];
}

export function updateCartQuantity(items: CartItem[], productId: string, quantity: number): CartItem[] {
  if (quantity <= 0) return removeFromCart(items, productId);
  return items.map((item) =>
    item.productId === productId
      ? { ...item, quantity: Math.min(Math.trunc(quantity), MAX_ITEM_QUANTITY) }
      : item,
  );
}

export function removeFromCart(items: CartItem[], productId: string): CartItem[] {
  return items.filter((item) => item.productId !== productId);
}

export function cartTotal(items: CartItem[]): number {
  return items.reduce((total, item) => total + item.priceValue * item.quantity, 0);
}

export function cartCount(items: CartItem[]): number {
  return items.reduce((total, item) => total + item.quantity, 0);
}

/** Convierte soles a céntimos (el formato que exige Culqi). */
export function toCents(soles: number): number {
  return Math.round(soles * 100);
}

export function formatSoles(soles: number): string {
  return `S/ ${soles.toFixed(2)}`;
}

export function toOrderItems(items: CartItem[]): { id: string; quantity: number }[] {
  return items.map((item) => ({ id: item.productId, quantity: item.quantity }));
}
