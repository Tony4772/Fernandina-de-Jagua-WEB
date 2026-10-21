import React, { useEffect, useState } from 'react';
import { BrandConfig } from '../types';
import { CULQI_PUBLIC_KEY, isCulqiConfigured, isCulqiTestMode } from '../config';
import { CartItem, cartTotal, formatSoles } from '../lib/cart';
import { createOrder, payOrder, PaymentApiError } from '../lib/api';
import { openCulqiCheckout } from '../lib/culqi';

interface CheckoutModalProps {
  isOpen: boolean;
  items: CartItem[];
  brand: BrandConfig;
  onClose: () => void;
  onPaid: (orderNumber: string) => void;
}

type Step = 'form' | 'paying' | 'paid' | 'error';

interface CustomerForm {
  name: string;
  email: string;
  phone: string;
  address: string;
  district: string;
  notes: string;
}

const EMPTY_FORM: CustomerForm = {
  name: '',
  email: '',
  phone: '',
  address: '',
  district: '',
  notes: '',
};

export const CheckoutModal: React.FC<CheckoutModalProps> = ({
  isOpen,
  items,
  brand,
  onClose,
  onPaid,
}) => {
  const [step, setStep] = useState<Step>('form');
  const [form, setForm] = useState<CustomerForm>(EMPTY_FORM);
  const [error, setError] = useState<string | null>(null);
  const [statusText, setStatusText] = useState('');
  const [paidInfo, setPaidInfo] = useState<{ orderNumber: string; amount: number } | null>(null);

  const total = cartTotal(items);

  useEffect(() => {
    if (!isOpen) {
      setStep('form');
      setError(null);
      setStatusText('');
      setPaidInfo(null);
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const update = (field: keyof CustomerForm) => (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setForm((current) => ({ ...current, [field]: event.target.value }));
  };

  const validate = (): string | null => {
    if (form.name.trim().length < 3) return 'Ingresa tu nombre completo.';
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(form.email.trim())) return 'Ingresa un correo electrónico válido.';
    if (form.phone.replace(/\D/g, '').length < 6) return 'Ingresa un teléfono de contacto válido.';
    if (items.length === 0) return 'Tu carrito está vacío.';
    return null;
  };

  const handlePay = async (event: React.FormEvent) => {
    event.preventDefault();
    const validationError = validate();
    if (validationError) {
      setError(validationError);
      return;
    }

    if (!isCulqiConfigured) {
      setError('El pago en línea aún no está configurado. Escríbenos por WhatsApp para completar tu pedido.');
      return;
    }

    setError(null);
    setStep('paying');
    setStatusText('Preparando tu pedido…');

    const [firstName, ...rest] = form.name.trim().split(' ');
    const lastName = rest.join(' ');

    try {
      // El monto definitivo lo calcula el servidor con su catálogo.
      const order = await createOrder(items, {
        name: form.name.trim(),
        email: form.email.trim(),
        phone: form.phone.trim(),
        address: form.address.trim(),
        district: form.district.trim(),
        notes: form.notes.trim(),
      });

      setStatusText('Completa el pago en la ventana de Culqi…');

      await openCulqiCheckout({
        publicKey: CULQI_PUBLIC_KEY,
        amountInCents: order.amount,
        title: 'Fernandina de Jagua',
        email: form.email.trim(),
        firstName,
        lastName: lastName || firstName,
        onToken: async (tokenId) => {
          setStatusText('Confirmando tu pago…');
          try {
            const paid = await payOrder(order.orderId, tokenId);
            setPaidInfo({ orderNumber: paid.orderNumber, amount: paid.amount });
            setStep('paid');
            onPaid(paid.orderNumber);
          } catch (paymentError) {
            setError(
              paymentError instanceof PaymentApiError
                ? paymentError.message
                : 'No pudimos confirmar el pago. Escríbenos por WhatsApp antes de reintentar.',
            );
            setStep('error');
          }
        },
        onError: (message) => {
          setError(message);
          setStep('error');
        },
      });
    } catch (checkoutError) {
      setError(
        checkoutError instanceof PaymentApiError
          ? checkoutError.message
          : checkoutError instanceof Error
            ? checkoutError.message
            : 'No se pudo iniciar el pago. Intenta nuevamente.',
      );
      setStep('error');
    }
  };

  return (
    <div
      className="fixed inset-0 z-[70] bg-primary-container/70 backdrop-blur-sm flex items-center justify-center p-margin overflow-y-auto"
      onClick={(event) => {
        if (event.target === event.currentTarget && step !== 'paying') onClose();
      }}
    >
      <div className="bg-surface-container-lowest max-w-lg w-full rounded-xl shadow-2xl overflow-hidden my-auto border border-surface-container-high">
        <header className="bg-primary-container text-surface-bright px-space-md py-space-sm flex items-center justify-between border-b border-[#3b2721]">
          <div className="flex items-center gap-space-xs">
            <span className="material-symbols-outlined text-secondary-fixed text-[20px]">
              {step === 'paid' ? 'check_circle' : 'lock'}
            </span>
            <h2 className="font-headline-sm text-headline-sm">
              {step === 'paid' ? 'Pago confirmado' : 'Pago en línea'}
            </h2>
          </div>
          <button
            type="button"
            onClick={onClose}
            disabled={step === 'paying'}
            className="w-8 h-8 rounded-full flex items-center justify-center hover:bg-white/10 transition-colors disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
            aria-label="Cerrar"
          >
            <span className="material-symbols-outlined text-[20px]">close</span>
          </button>
        </header>

        {step === 'paid' && paidInfo ? (
          <div className="p-space-lg flex flex-col items-center text-center gap-space-sm">
            <div className="w-14 h-14 rounded-full bg-secondary/15 flex items-center justify-center">
              <span className="material-symbols-outlined text-secondary text-[32px]">check_circle</span>
            </div>
            <h3 className="font-headline-md text-headline-md text-on-primary-fixed">¡Gracias por tu compra!</h3>
            <p className="font-body-md text-body-md text-on-surface-variant">
              Tu pago de <strong>{formatSoles(paidInfo.amount / 100)}</strong> fue aprobado.
            </p>
            <div className="w-full mt-space-xs p-space-sm rounded-lg bg-surface-container text-left">
              <div className="flex items-center justify-between gap-space-sm">
                <span className="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant">
                  Número de pedido
                </span>
                <span className="font-title-md text-title-md text-on-primary-fixed">{paidInfo.orderNumber}</span>
              </div>
              <div className="flex items-center justify-between gap-space-sm mt-1">
                <span className="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant">
                  Enviado a
                </span>
                <span className="font-body-sm text-body-sm text-on-surface truncate">{form.email}</span>
              </div>
            </div>
            <p className="font-body-sm text-body-sm text-on-surface-variant">
              Te contactaremos para coordinar la entrega. También puedes escribirnos con tu número de pedido.
            </p>
            <div className="w-full flex flex-col sm:flex-row gap-space-xs mt-space-xs">
              <a
                className="flex-1 inline-flex items-center justify-center gap-space-xs px-space-md py-space-xs rounded-lg bg-secondary text-on-secondary hover:bg-on-tertiary-container font-label-md text-label-md uppercase tracking-wider transition-colors"
                href={`${brand.whatsappUrl}?text=${encodeURIComponent(
                  `Hola, acabo de pagar el pedido ${paidInfo.orderNumber}. Quisiera coordinar la entrega.`,
                )}`}
                target="_blank"
                rel="noopener noreferrer"
              >
                <span className="material-symbols-outlined text-[18px]">chat</span>
                Coordinar entrega
              </a>
              <button
                type="button"
                onClick={onClose}
                className="flex-1 px-space-md py-space-xs rounded-lg border border-surface-container-high font-label-md text-label-md uppercase tracking-wider text-on-surface-variant hover:text-on-primary-fixed transition-colors cursor-pointer"
              >
                Seguir comprando
              </button>
            </div>
          </div>
        ) : step === 'paying' ? (
          <div className="p-space-lg flex flex-col items-center text-center gap-space-sm">
            <span className="material-symbols-outlined text-secondary text-[40px] animate-pulse">credit_card</span>
            <h3 className="font-title-md text-title-md text-on-primary-fixed">Procesando tu pago</h3>
            <p className="font-body-md text-body-md text-on-surface-variant">{statusText}</p>
            <p className="font-body-sm text-body-sm text-on-surface-variant">
              No cierres esta ventana. Si la ventana de Culqi se cerró, puedes volver e intentarlo de nuevo.
            </p>
            <button
              type="button"
              onClick={() => {
                setStep('form');
                setError(null);
              }}
              className="mt-space-xs px-space-md py-space-xs rounded-lg border border-surface-container-high font-label-md text-label-md uppercase tracking-wider text-on-surface-variant hover:text-on-primary-fixed transition-colors cursor-pointer"
            >
              Volver
            </button>
          </div>
        ) : (
          <form onSubmit={handlePay} className="p-space-md flex flex-col gap-space-sm">
            <div className="flex items-center justify-between p-space-sm rounded-lg bg-surface-container">
              <div className="flex flex-col">
                <span className="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant">
                  Total a pagar
                </span>
                <span className="font-headline-sm text-headline-sm text-on-primary-fixed font-bold">
                  {formatSoles(total)}
                </span>
              </div>
              <div className="flex flex-col items-end">
                <span className="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant">
                  {items.reduce((sum, item) => sum + item.quantity, 0)} artículo(s)
                </span>
                {isCulqiTestMode && (
                  <span className="text-[11px] font-label-md uppercase tracking-wider text-error">
                    Modo de prueba
                  </span>
                )}
              </div>
            </div>

            {error && (
              <div className="flex items-start gap-space-xs p-space-sm rounded-lg bg-error-container/60 text-on-error-container">
                <span className="material-symbols-outlined text-[18px] mt-0.5 shrink-0">error</span>
                <p className="font-body-sm text-body-sm">{error}</p>
              </div>
            )}

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-space-xs">
              <label className="flex flex-col gap-1 sm:col-span-2">
                <span className="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant">
                  Nombre completo *
                </span>
                <input
                  required
                  value={form.name}
                  onChange={update('name')}
                  className="w-full p-2.5 rounded-lg bg-surface-container-low border border-surface-container-high text-on-surface text-sm focus:outline-none focus:border-secondary"
                  autoComplete="name"
                />
              </label>

              <label className="flex flex-col gap-1">
                <span className="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant">
                  Correo *
                </span>
                <input
                  required
                  type="email"
                  value={form.email}
                  onChange={update('email')}
                  className="w-full p-2.5 rounded-lg bg-surface-container-low border border-surface-container-high text-on-surface text-sm focus:outline-none focus:border-secondary"
                  autoComplete="email"
                />
              </label>

              <label className="flex flex-col gap-1">
                <span className="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant">
                  Teléfono / WhatsApp *
                </span>
                <input
                  required
                  type="tel"
                  value={form.phone}
                  onChange={update('phone')}
                  placeholder="999 999 999"
                  className="w-full p-2.5 rounded-lg bg-surface-container-low border border-surface-container-high text-on-surface text-sm focus:outline-none focus:border-secondary"
                  autoComplete="tel"
                />
              </label>

              <label className="flex flex-col gap-1 sm:col-span-2">
                <span className="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant">
                  Dirección de entrega (opcional)
                </span>
                <input
                  value={form.address}
                  onChange={update('address')}
                  placeholder="Av. / Calle, número, referencia"
                  className="w-full p-2.5 rounded-lg bg-surface-container-low border border-surface-container-high text-on-surface text-sm focus:outline-none focus:border-secondary"
                  autoComplete="street-address"
                />
              </label>

              <label className="flex flex-col gap-1 sm:col-span-2">
                <span className="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant">
                  Distrito (opcional)
                </span>
                <input
                  value={form.district}
                  onChange={update('district')}
                  placeholder="San Miguel, Lima"
                  className="w-full p-2.5 rounded-lg bg-surface-container-low border border-surface-container-high text-on-surface text-sm focus:outline-none focus:border-secondary"
                />
              </label>

              <label className="flex flex-col gap-1 sm:col-span-2">
                <span className="font-label-sm text-label-sm uppercase tracking-wider text-on-surface-variant">
                  Notas del pedido (opcional)
                </span>
                <textarea
                  value={form.notes}
                  onChange={update('notes')}
                  rows={2}
                  placeholder="Mensaje para regalo, horario de entrega, etc."
                  className="w-full p-2.5 rounded-lg bg-surface-container-low border border-surface-container-high text-on-surface text-sm focus:outline-none focus:border-secondary resize-none"
                />
              </label>
            </div>

            <button
              type="submit"
              className="w-full inline-flex items-center justify-center gap-space-xs px-space-md py-space-sm rounded-lg bg-secondary text-on-secondary hover:bg-on-tertiary-container hover:text-on-primary font-label-md text-label-md uppercase tracking-wider transition-colors shadow-sm cursor-pointer"
            >
              <span className="material-symbols-outlined text-[18px]">credit_card</span>
              <span>Pagar {formatSoles(total)} con Culqi</span>
            </button>

            <p className="font-body-sm text-body-sm text-on-surface-variant text-center">
              Aceptamos tarjetas de crédito/débito y Yape. Serás dirigido al checkout seguro de Culqi; no
              almacenamos los datos de tu tarjeta.
            </p>
          </form>
        )}
      </div>
    </div>
  );
};
