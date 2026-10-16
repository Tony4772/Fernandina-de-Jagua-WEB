import React from 'react';
import { BrandConfig, PageRoute } from '../types';

interface FooterProps {
  onRouteChange: (route: PageRoute) => void;
  onOpenPrivacy: () => void;
  onOpenAdmin: () => void;
  brand: BrandConfig;
}

export const Footer: React.FC<FooterProps> = ({
  onRouteChange,
  onOpenPrivacy,
  onOpenAdmin,
  brand,
}) => {
  const handleNav = (route: PageRoute) => {
    onRouteChange(route);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <footer className="w-full bg-primary-container text-surface-container-low border-t border-[#3a2620]">
      <div className="max-w-[1320px] mx-auto px-margin md:px-margin-tablet lg:px-margin-desktop py-space-xl">
        <div className="grid grid-cols-1 md:grid-cols-12 gap-gutter-desktop items-start pb-space-lg">
          {/* Brand & Manifesto */}
          <div className="md:col-span-5 flex flex-col gap-space-xs">
            <span className="font-headline-sm text-headline-sm text-surface-bright">
              {brand.name}
            </span>
            <span className="font-label-sm text-label-sm text-on-tertiary-container uppercase tracking-widest">
              Atelier de Chocolates
            </span>
            <p className="font-body-sm text-body-sm text-primary-fixed-dim mt-space-xs max-w-sm leading-relaxed">
              Elaboración esmerada y expresión genuina del cacao en San Miguel, Lima, Perú.
              Experiencias sensitivas y notas de cata con devoción al detalle.
            </p>
          </div>

          {/* Atelier & Contact */}
          <div className="md:col-span-4 flex flex-col gap-space-xs">
            <span className="font-label-md text-label-md uppercase text-on-tertiary-container tracking-wider">
              Atelier y Contacto
            </span>
            <div className="flex flex-col gap-space-xs mt-space-xs font-body-sm text-body-sm text-primary-fixed-dim">
              <span className="text-surface-bright font-medium">{brand.companyName}</span>
              <span>{brand.address}</span>
              <span>{brand.district}</span>
              <div className="flex items-center gap-space-xs pt-space-xs">
                <span className="material-symbols-outlined text-on-tertiary-container text-[16px]">
                  phone
                </span>
                <a
                  className="text-surface-bright hover:text-secondary-fixed transition-colors"
                  href={brand.whatsappUrl}
                  rel="noopener noreferrer"
                  target="_blank"
                >
                  WhatsApp: {brand.whatsappNumber}
                </a>
              </div>
            </div>
          </div>

          {/* Navigation */}
          <div className="md:col-span-3 flex flex-col gap-space-xs">
            <span className="font-label-md text-label-md uppercase text-on-tertiary-container tracking-wider">
              Navegación
            </span>
            <div className="flex flex-col gap-space-xs mt-space-xs font-body-sm text-body-sm">
              <button
                onClick={() => handleNav('inicio')}
                className="text-left text-primary-fixed-dim hover:text-surface-bright transition-colors cursor-pointer"
              >
                Inicio
              </button>
              <button
                onClick={() => handleNav('productos')}
                className="text-left text-primary-fixed-dim hover:text-surface-bright transition-colors cursor-pointer"
              >
                Nuestras Colecciones
              </button>
              <button
                onClick={() => handleNav('visitanos')}
                className="text-left text-primary-fixed-dim hover:text-surface-bright transition-colors cursor-pointer"
              >
                Visítanos en San Miguel
              </button>
              <button
                onClick={onOpenPrivacy}
                className="text-left text-primary-fixed-dim hover:text-surface-bright transition-colors cursor-pointer"
              >
                Política de Privacidad
              </button>
              <button
                onClick={onOpenAdmin}
                className="text-left text-secondary-fixed hover:text-surface-bright transition-colors cursor-pointer font-medium flex items-center gap-1.5 mt-1"
              >
                <span className="material-symbols-outlined text-[14px]">lock</span>
                <span>Administración CMS</span>
              </button>
            </div>
          </div>
        </div>

        {/* Bottom copyright line */}
        <div className="pt-space-md border-t border-[#38221b] flex flex-col sm:flex-row items-center justify-between gap-space-sm font-label-sm text-label-sm text-on-primary-container">
          <p>© {brand.companyName} {brand.name}. Todos los derechos reservados.</p>
          <div className="flex items-center gap-space-md">
            <button
              onClick={onOpenPrivacy}
              className="hover:text-surface-bright transition-colors cursor-pointer"
            >
              Política de Privacidad
            </button>
            <span>·</span>
            <span>San Miguel, Lima</span>
            <span>·</span>
            <button
              onClick={onOpenAdmin}
              className="hover:text-surface-bright transition-colors cursor-pointer text-secondary"
            >
              CMS
            </button>
          </div>
        </div>
      </div>
    </footer>
  );
};
