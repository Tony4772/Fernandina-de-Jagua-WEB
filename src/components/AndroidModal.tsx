import React, { useState } from 'react';
import { BRAND_INFO } from '../data/products';

interface AndroidModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const AndroidModal: React.FC<AndroidModalProps> = ({ isOpen, onClose }) => {
  const [downloading, setDownloading] = useState(false);
  const [downloadComplete, setDownloadComplete] = useState(false);

  if (!isOpen) return null;

  const handleDownload = () => {
    setDownloading(true);
    setTimeout(() => {
      setDownloading(false);
      setDownloadComplete(true);
      // Simulate file download trigger
      const element = document.createElement('a');
      const file = new Blob(
        [
          `Fernandina de Jagua Chocolates Android Application Package\nVersión: 2.4.1\nAtelier San Miguel, Lima, Perú\nEBYZOM E.I.R.L.`
        ],
        { type: 'application/vnd.android.package-archive' }
      );
      element.href = URL.createObjectURL(file);
      element.download = 'FernandinaDeJagua_v2.4.1.apk';
      document.body.appendChild(element);
      element.click();
      document.body.removeChild(element);
    }, 1200);
  };

  return (
    <div
      className="fixed inset-0 z-50 bg-primary-container/70 backdrop-blur-sm flex items-center justify-center p-margin animate-in fade-in duration-200"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className="bg-surface-container-lowest max-w-md w-full rounded-xl shadow-2xl overflow-hidden animate-in zoom-in-95 duration-200 flex flex-col border border-surface-container-high">
        {/* Header */}
        <div className="bg-primary-container text-surface-bright p-space-md flex items-center justify-between">
          <div className="flex items-center gap-space-sm">
            <div className="w-10 h-10 rounded-lg bg-secondary flex items-center justify-center text-on-secondary shadow-sm">
              <span className="material-symbols-outlined text-[24px]">android</span>
            </div>
            <div>
              <h3 className="font-headline-sm text-[19px] leading-tight text-surface-bright">
                App Oficial Android
              </h3>
              <span className="font-label-sm text-label-sm uppercase tracking-wider text-secondary-fixed">
                Fernandina de Jagua v2.4.1
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
          <p className="font-body-md text-body-md text-on-surface-variant">
            Nuestra aplicación oficial para Android le permite consultar lotes frescos del día, reservar piezas exclusivas de temporada y coordinar sus entregas directas en Lima.
          </p>

          {/* Installation steps */}
          <div className="space-y-3 bg-surface-container-low p-space-md rounded-xl text-xs font-body-sm text-on-surface-variant">
            <div className="flex items-start gap-2.5">
              <span className="w-5 h-5 rounded-full bg-secondary text-on-secondary flex items-center justify-center font-bold text-[10px] shrink-0 mt-0.5">
                1
              </span>
              <span>
                <strong className="text-on-surface">Descarga el APK:</strong> Pulsa el botón inferior para guardar el archivo instalador seguro en tu móvil.
              </span>
            </div>
            <div className="flex items-start gap-2.5">
              <span className="w-5 h-5 rounded-full bg-secondary text-on-secondary flex items-center justify-center font-bold text-[10px] shrink-0 mt-0.5">
                2
              </span>
              <span>
                <strong className="text-on-surface">Habilitar orígenes:</strong> Si Android solicita permisos, selecciona &quot;Permitir desde esta fuente&quot;.
              </span>
            </div>
            <div className="flex items-start gap-2.5">
              <span className="w-5 h-5 rounded-full bg-secondary text-on-secondary flex items-center justify-center font-bold text-[10px] shrink-0 mt-0.5">
                3
              </span>
              <span>
                <strong className="text-on-surface">Explora el atelier:</strong> Accede a fichas sensoriales, novedades y chat preferencial con nuestros chocolateros.
              </span>
            </div>
          </div>

          {/* Download button or confirmation */}
          <div className="pt-space-xs flex flex-col gap-space-xs">
            <button
              onClick={handleDownload}
              disabled={downloading}
              className="w-full flex items-center justify-center gap-space-xs py-3 px-space-md rounded-lg bg-primary-container text-surface-bright hover:bg-secondary hover:text-on-secondary transition-all font-label-md text-label-md uppercase tracking-wider shadow-md cursor-pointer disabled:opacity-75"
            >
              {downloading ? (
                <>
                  <span className="animate-spin material-symbols-outlined text-[18px]">
                    progress_activity
                  </span>
                  <span>Generando descarga...</span>
                </>
              ) : downloadComplete ? (
                <>
                  <span className="material-symbols-outlined text-[18px] text-secondary-fixed">
                    check_circle
                  </span>
                  <span>Descargar de nuevo (APK)</span>
                </>
              ) : (
                <>
                  <span className="material-symbols-outlined text-[18px] text-secondary-fixed">
                    download
                  </span>
                  <span>Descargar APK (34.2 MB)</span>
                </>
              )}
            </button>

            {downloadComplete && (
              <p className="text-xs text-center text-secondary font-medium animate-in fade-in">
                ✓ Archivo APK transferido. Revisa las descargas de tu dispositivo.
              </p>
            )}

            <div className="text-center pt-2">
              <a
                href={BRAND_INFO.whatsappUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="text-xs text-on-surface-variant hover:text-secondary underline transition-colors"
              >
                ¿Prefieres pedir sin instalar? Escríbenos directamente por WhatsApp
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
