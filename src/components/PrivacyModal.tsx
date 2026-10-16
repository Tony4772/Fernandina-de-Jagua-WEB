import React from 'react';
import { BRAND_INFO } from '../data/products';

interface PrivacyModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const PrivacyModal: React.FC<PrivacyModalProps> = ({ isOpen, onClose }) => {
  if (!isOpen) return null;

  return (
    <div
      className="fixed inset-0 z-50 bg-primary-container/70 backdrop-blur-sm flex items-center justify-center p-margin animate-in fade-in duration-200"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className="bg-surface-container-lowest max-w-2xl w-full max-h-[85vh] rounded-xl shadow-2xl overflow-hidden animate-in zoom-in-95 duration-200 flex flex-col border border-surface-container-high">
        {/* Header */}
        <div className="bg-primary-container text-surface-bright p-space-md flex items-center justify-between border-b border-[#3b2721]">
          <div>
            <h3 className="font-headline-sm text-lg text-surface-bright leading-tight">
              Política de Privacidad y Tratamiento de Datos
            </h3>
            <span className="font-label-sm text-label-sm uppercase tracking-wider text-secondary-fixed">
              {BRAND_INFO.companyName} · {BRAND_INFO.name}
            </span>
          </div>
          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full bg-white/10 flex items-center justify-center text-surface hover:bg-white/20 transition-colors cursor-pointer"
            aria-label="Cerrar modal"
          >
            <span className="material-symbols-outlined text-[18px]">close</span>
          </button>
        </div>

        {/* Scrollable text */}
        <div className="p-space-lg overflow-y-auto space-y-4 text-on-surface-variant font-body-sm text-body-sm leading-relaxed">
          <p>
            <strong>1. Identificación del Titular del Banco de Datos:</strong>{' '}
            El titular del tratamiento de datos personales recopilados a través de este sitio web, la aplicación móvil y los canales oficiales de mensajería es <strong>{BRAND_INFO.companyName}</strong>, con domicilio en {BRAND_INFO.address}, San Miguel, Lima, Perú.
          </p>

          <p>
            <strong>2. Marco Legal:</strong>{' '}
            El tratamiento de datos personales se rige conforme a la Ley N° 29733 (Ley de Protección de Datos Personales del Perú) y su Reglamento aprobado por Decreto Supremo N° 003-2013-JUS.
          </p>

          <p>
            <strong>3. Finalidad del Tratamiento:</strong>{' '}
            Los datos proporcionados voluntariamente por los usuarios (como nombre, número de WhatsApp, preferencias de cata y direcciones para despacho) se destinan exclusivamente para:
          </p>
          <ul className="list-disc pl-5 space-y-1">
            <li>Coordinar la elaboración, reserva y entrega de creaciones chocolateras artesanales.</li>
            <li>Brindar atención personalizada respecto a fechas de tostado y lotes frescos del día.</li>
            <li>Gestionar citas presenciales y retiros en nuestro taller de San Miguel.</li>
            <li>No comercializamos ni cedemos sus datos personales a terceros bajo ninguna circunstancia.</li>
          </ul>

          <p>
            <strong>4. Derechos ARCO:</strong>{' '}
            El usuario podrá ejercer en cualquier momento sus derechos de Acceso, Rectificación, Cancelación y Oposición (ARCO) comunicándose de manera directa a través de nuestro canal de WhatsApp {BRAND_INFO.whatsappNumber} o acudiendo a nuestra sede en San Miguel.
          </p>

          <p>
            <strong>5. Seguridad:</strong>{' '}
            Adoptamos medidas técnicas, organizativas y humanas orientadas a garantizar la confidencialidad e integridad de la información que nos confían nuestros clientes.
          </p>
        </div>

        {/* Footer */}
        <div className="p-space-md bg-surface-container-low border-t border-surface-container-high flex justify-end">
          <button
            onClick={onClose}
            className="px-space-lg py-2 rounded-lg bg-secondary text-on-secondary hover:bg-on-tertiary-container font-label-md text-label-md uppercase tracking-wider transition-colors cursor-pointer shadow-sm"
          >
            Entendido
          </button>
        </div>
      </div>
    </div>
  );
};
