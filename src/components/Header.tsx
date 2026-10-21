import React, { useState } from 'react';
import { BrandConfig, PageRoute } from '../types';

interface HeaderProps {
  currentRoute: PageRoute;
  onRouteChange: (route: PageRoute) => void;
  onOpenConcierge: () => void;
  onOpenAdmin: () => void;
  isAdminLoggedIn: boolean;
  brand: BrandConfig;
  cartCount?: number;
  onOpenCart?: () => void;
}

export const Header: React.FC<HeaderProps> = ({
  currentRoute,
  onRouteChange,
  onOpenConcierge,
  onOpenAdmin,
  isAdminLoggedIn,
  brand,
  cartCount = 0,
  onOpenCart,
}) => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleNav = (route: PageRoute) => {
    onRouteChange(route);
    setMobileMenuOpen(false);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const isNavActive = (route: PageRoute) => {
    if (route === 'productos') {
      return currentRoute === 'productos' || currentRoute === 'producto-detalle';
    }
    return currentRoute === route;
  };

  return (
    <header className="fixed top-0 inset-x-0 z-50 bg-surface/90 backdrop-blur-md shadow-[0_1px_8px_rgba(34,21,17,0.05)] border-b border-surface-container-high/40">
      <div className="h-20 max-w-[1320px] mx-auto px-margin md:px-margin-tablet lg:px-margin-desktop flex items-center justify-between">
        {/* Brand identity lockup */}
        <button
          onClick={() => handleNav('inicio')}
          className="flex items-center gap-space-sm text-left focus:outline-none cursor-pointer group"
          aria-label="Ir a Inicio"
        >
          <img
            alt={brand.name}
            className="h-10 w-auto object-contain transition-transform duration-300 group-hover:scale-105"
            src={brand.logoUrl}
            referrerPolicy="no-referrer"
            onError={(event) => {
              event.currentTarget.onerror = null;
              event.currentTarget.src = '/favicon.svg';
            }}
          />
          <div className="flex flex-col">
            <span className="font-headline-sm text-headline-sm tracking-tight text-on-primary-fixed leading-tight">
              {brand.name}
            </span>
            <span className="font-label-sm text-label-sm uppercase tracking-widest text-on-tertiary-container">
              {brand.subName}
            </span>
          </div>
        </button>

        {/* Desktop Navigation Links */}
        <nav className="hidden md:flex items-center gap-space-lg">
          <button
            onClick={() => handleNav('inicio')}
            className={`uppercase transition-colors py-1 cursor-pointer font-label-md text-label-md tracking-wider ${
              isNavActive('inicio')
                ? 'text-on-primary-fixed border-b border-on-tertiary-container font-semibold'
                : 'text-on-surface-variant hover:text-on-primary-fixed'
            }`}
          >
            Inicio
          </button>
          <button
            onClick={() => handleNav('productos')}
            className={`uppercase transition-colors py-1 cursor-pointer font-label-md text-label-md tracking-wider ${
              isNavActive('productos')
                ? 'text-on-primary-fixed border-b border-on-tertiary-container font-semibold'
                : 'text-on-surface-variant hover:text-on-primary-fixed'
            }`}
          >
            Productos
          </button>
          <button
            onClick={() => handleNav('visitanos')}
            className={`uppercase transition-colors py-1 cursor-pointer font-label-md text-label-md tracking-wider ${
              isNavActive('visitanos')
                ? 'text-on-primary-fixed border-b border-on-tertiary-container font-semibold'
                : 'text-on-surface-variant hover:text-on-primary-fixed'
            }`}
          >
            Visítanos
          </button>
        </nav>

        {/* Action Elements */}
        <div className="flex items-center gap-space-md">
          {/* Cart Button */}
          <button
            onClick={onOpenCart}
            className="relative inline-flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-[11px] font-label-md uppercase tracking-wider transition-all cursor-pointer shadow-sm bg-surface-container-high/80 text-on-surface-variant hover:bg-surface-container-highest hover:text-on-primary-fixed border border-outline-variant/40"
            title="Ver carrito de compras"
            aria-label={`Carrito de compras, ${cartCount} artículo(s)`}
            type="button"
          >
            <span className="material-symbols-outlined text-[17px]">shopping_bag</span>
            <span className="hidden sm:inline">Carrito</span>
            {cartCount > 0 && (
              <span className="absolute -top-1.5 -right-1.5 min-w-[18px] h-[18px] px-1 rounded-full bg-secondary text-on-secondary text-[10px] font-bold flex items-center justify-center">
                {cartCount}
              </span>
            )}
          </button>

          {/* Admin CMS Access Button */}
          <button
            onClick={onOpenAdmin}
            className={`inline-flex items-center gap-1 px-2.5 py-1.5 rounded-lg text-[11px] font-label-md uppercase tracking-wider transition-all cursor-pointer shadow-sm ${
              isAdminLoggedIn
                ? 'bg-secondary text-on-secondary font-semibold'
                : 'bg-surface-container-high/80 text-on-surface-variant hover:bg-surface-container-highest hover:text-on-primary-fixed border border-outline-variant/40'
            }`}
            title="Panel de Administración CMS"
          >
            <span className="material-symbols-outlined text-[15px]">
              {isAdminLoggedIn ? 'dashboard' : 'lock'}
            </span>
            <span className="hidden sm:inline">CMS Admin</span>
          </button>

          <a
            className="hidden sm:inline-flex items-center gap-space-xs px-space-md py-space-xs rounded-lg bg-secondary text-on-secondary hover:bg-on-tertiary-container hover:text-on-primary transition-all duration-200 shadow-sm font-label-md text-label-md uppercase tracking-wider cursor-pointer"
            href={brand.whatsappUrl}
            rel="noopener noreferrer"
            target="_blank"
            title="Escribir por WhatsApp"
          >
            <span className="material-symbols-outlined text-[16px]">chat</span>
            <span>Escribir por WhatsApp</span>
          </a>

          <button
            onClick={onOpenConcierge}
            className="w-8 h-8 rounded-full bg-primary flex items-center justify-center cursor-pointer hover:bg-on-tertiary-container transition-colors shadow-sm"
            title="Atelier y Atención Personalizada"
            aria-label="Atelier y Atención Personalizada"
          >
            <span className="material-symbols-outlined text-on-primary text-[18px]">person</span>
          </button>

          {/* Mobile hamburger menu toggle */}
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="md:hidden w-8 h-8 rounded-lg bg-surface-container flex items-center justify-center text-on-surface hover:text-on-primary-fixed cursor-pointer transition-colors"
            aria-label={mobileMenuOpen ? 'Cerrar menú' : 'Abrir menú'}
          >
            <span className="material-symbols-outlined text-[20px]">
              {mobileMenuOpen ? 'close' : 'menu'}
            </span>
          </button>
        </div>
      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-surface-container-low border-b border-surface-container-high px-margin py-space-md flex flex-col gap-space-sm animate-in fade-in slide-in-from-top-2 duration-200 shadow-lg">
          <button
            onClick={() => handleNav('inicio')}
            className={`text-left py-2 px-3 rounded-lg font-label-md text-label-md uppercase tracking-wider transition-colors ${
              isNavActive('inicio')
                ? 'bg-secondary text-on-secondary font-semibold'
                : 'text-on-surface hover:bg-surface-container'
            }`}
          >
            Inicio
          </button>
          <button
            onClick={() => handleNav('productos')}
            className={`text-left py-2 px-3 rounded-lg font-label-md text-label-md uppercase tracking-wider transition-colors ${
              isNavActive('productos')
                ? 'bg-secondary text-on-secondary font-semibold'
                : 'text-on-surface hover:bg-surface-container'
            }`}
          >
            Productos &amp; Catálogo
          </button>
          <button
            onClick={() => handleNav('visitanos')}
            className={`text-left py-2 px-3 rounded-lg font-label-md text-label-md uppercase tracking-wider transition-colors ${
              isNavActive('visitanos')
                ? 'bg-secondary text-on-secondary font-semibold'
                : 'text-on-surface hover:bg-surface-container'
            }`}
          >
            Visítanos en San Miguel
          </button>
          <button
            onClick={() => {
              setMobileMenuOpen(false);
              onOpenAdmin();
            }}
            className="text-left py-2 px-3 rounded-lg font-label-md text-label-md uppercase tracking-wider transition-colors text-secondary hover:bg-surface-container flex items-center gap-2"
          >
            <span className="material-symbols-outlined text-[16px]">admin_panel_settings</span>
            <span>Panel CMS Admin</span>
          </button>
          <a
            className="flex items-center justify-center gap-space-xs py-2.5 px-3 rounded-lg bg-secondary text-on-secondary font-label-md text-label-md uppercase tracking-wider mt-2 shadow-sm text-center"
            href={brand.whatsappUrl}
            rel="noopener noreferrer"
            target="_blank"
          >
            <span className="material-symbols-outlined text-[16px]">chat</span>
            <span>Escribir por WhatsApp</span>
          </a>
        </div>
      )}
    </header>
  );
};
