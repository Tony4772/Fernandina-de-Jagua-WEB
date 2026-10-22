import React, { useState } from 'react';
import { BrandConfig } from '../types';

interface ConciergeModalProps {
  isOpen: boolean;
  onClose: () => void;
  onOpenAndroid: () => void;
  brand: BrandConfig;
}

export const ConciergeModal: React.FC<ConciergeModalProps> = ({
  isOpen,
  onClose,
  onOpenAndroid,
  brand,
}) => {
  const [inquiryType, setInquiryType] = useState('pedido');
  const [customNotes, setCustomNotes] = useState('');
  const [submitted, setSubmitted] = useState(false);

  if (!isOpen) return null;

  const handleSendToWhatsApp = (e: React.FormEvent) => {
    e.preventDefault();
    const typeLabel =
      inquiryType === 'cata'
        ? 'Reserva de Cata Sensorial'
        : inquiryType === 'corporativo'
        ? 'Regalos Corporativos / Eventos'
        : 'Pedido Especial o Reserva de Colección';

    const text = encodeURIComponent(
      `Hola ${brand.name},\n\nMe comunico para: *${typeLabel}*.\nDetalle: ${customNotes || 'Deseo más información sobre sus lotes artesanales.'}\n\nEnviado desde el Atelier Web en San Miguel.`
    );
    window.open(`${brand.whatsappUrl}?text=${text}`, '_blank');
    setSubmitted(true);
    setTimeout(() => {
      onClose();
      setSubmitted(false);
    }, 1500);
  };

  return (
    <div
      className="fixed inset-0 z-50 bg-primary-container/70 backdrop-blur-sm flex items-center justify-center p-margin animate-in fade-in duration-200"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className="bg-surface-container-lowest max-w-lg w-full rounded-xl shadow-2xl overflow-hidden animate-in zoom-in-95 duration-200 flex flex-col border border-surface-container-high">
        {/* Header */}
        <div className="bg-primary-container text-surface-bright p-space-md flex items-center justify-between border-b border-[#3b2721]">
          <div className="flex items-center gap-space-sm">
            <div className="w-10 h-10 rounded-full bg-secondary/90 flex items-center justify-center text-on-secondary shadow-sm">
              <span className="material-symbols-outlined text-[20px]">person</span>
            </div>
            <div>
              <h3 className="font-headline-sm text-lg text-surface-bright leading-tight">
                Concierge del Atelier
              </h3>
              <span className="font-label-sm text-label-sm uppercase tracking-wider text-secondary-fixed">
                Atención directa personalizada
              </span>
            </div>
          </div>
          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full bg-white/10 flex items-center justify-center text-surface hover:bg-white/20 transition-colors cursor-pointer"
            aria-label="Cerrar modal"
          >
            <span className="material-symbols-outlined text-[18px]">close</span>
          </button>
        </div>

        {/* Content */}
        <div className="p-space-lg flex flex-col gap-space-md">
          <p className="font-body-sm text-body-sm text-on-surface-variant">
            En {brand.name} atendemos cada pedido de forma personalizada. Sin carritos impersonales ni intermediarios: coordine directamente con nuestros artesanos.
          </p>

          <form onSubmit={handleSendToWhatsApp} className="flex flex-col gap-space-sm">
            <div>
              <label className="font-label-sm text-label-sm uppercase tracking-wider text-on-surface block mb-1">
                Tipo de Consulta
              </label>
              <div className="grid grid-cols-3 gap-2">
                <button
                  type="button"
                  onClick={() => setInquiryType('pedido')}
                  className={`py-2 px-2 rounded-lg text-xs font-label-md uppercase tracking-wider transition-colors cursor-pointer text-center ${
                    inquiryType === 'pedido'
                      ? 'bg-secondary text-on-secondary font-semibold'
                      : 'bg-surface-container-low text-on-surface hover:bg-surface-container'
                  }`}
                >
                  Pedido
                </button>
                <button
                  type="button"
                  onClick={() => setInquiryType('cata')}
                  className={`py-2 px-2 rounded-lg text-xs font-label-md uppercase tracking-wider transition-colors cursor-pointer text-center ${
                    inquiryType === 'cata'
                      ? 'bg-secondary text-on-secondary font-semibold'
                      : 'bg-surface-container-low text-on-surface hover:bg-surface-container'
                  }`}
                >
                  Cata Guiada
                </button>
                <button
                  type="button"
                  onClick={() => setInquiryType('corporativo')}
                  className={`py-2 px-2 rounded-lg text-xs font-label-md uppercase tracking-wider transition-colors cursor-pointer text-center ${
                    inquiryType === 'corporativo'
                      ? 'bg-secondary text-on-secondary font-semibold'
                      : 'bg-surface-container-low text-on-surface hover:bg-surface-container'
                  }`}
                >
                  Regalos
                </button>
              </div>
            </div>

            <div>
              <label className="font-label-sm text-label-sm uppercase tracking-wider text-on-surface block mb-1">
                Mensaje o Preferencias (Opcional)
              </label>
              <textarea
                rows={3}
                value={customNotes}
                onChange={(e) => setCustomNotes(e.target.value)}
                placeholder="Ejemplo: Quisiera reservar 3 tabletas al 70% y una caja de bombones para recoger este viernes por la tarde..."
                className="w-full p-space-sm rounded-lg bg-surface-container-low border border-surface-container-high text-on-surface text-sm focus:outline-none focus:border-secondary resize-none"
              ></textarea>
            </div>

            <div className="bg-surface-container-low p-space-sm rounded-lg flex items-center justify-between text-xs">
              <div className="flex items-center gap-2 text-on-surface-variant">
                <span className="material-symbols-outlined text-secondary text-[18px]">
                  smartphone
                </span>
                <span>¿Tienes Android?</span>
              </div>
              <button
                type="button"
                onClick={() => {
                  onClose();
                  onOpenAndroid();
                }}
                className="text-secondary font-semibold hover:underline cursor-pointer uppercase font-label-sm tracking-wider"
              >
                Google Play
              </button>
            </div>

            <div className="pt-2 flex items-center justify-end gap-space-xs">
              <button
                type="button"
                onClick={onClose}
                className="px-space-md py-2 rounded text-on-surface-variant hover:text-on-surface font-label-md text-label-md uppercase tracking-wider cursor-pointer"
              >
                Cancelar
              </button>
              <button
                type="submit"
                className="inline-flex items-center gap-space-xs px-space-lg py-2.5 rounded-lg bg-secondary text-on-secondary hover:bg-on-tertiary-container font-label-md text-label-md uppercase tracking-wider transition-colors shadow-md cursor-pointer"
              >
                <span className="material-symbols-outlined text-[16px]">chat</span>
                <span>{submitted ? 'Abriendo WhatsApp...' : 'Iniciar Conversación'}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};
