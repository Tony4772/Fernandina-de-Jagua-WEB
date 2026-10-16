import React, { useState } from 'react';
import { Product, SiteData } from '../types';

interface CatalogViewProps {
  siteData: SiteData;
  onSelectProduct: (product: Product) => void;
  onOpenProductModal: (product: Product) => void;
  onOpenAndroid: () => void;
}

export const CatalogView: React.FC<CatalogViewProps> = ({
  siteData,
  onSelectProduct,
  onOpenProductModal,
  onOpenAndroid,
}) => {
  const { brand, catalog, products } = siteData;
  const [activeCategory, setActiveCategory] = useState<string>('all');

  const filteredProducts =
    activeCategory === 'all'
      ? products
      : products.filter((p) => p.category === activeCategory);

  const categories: { id: string; label: string }[] = [
    { id: 'all', label: 'Todos' },
    { id: 'puras', label: 'Barras Puras' },
    { id: 'bombones', label: 'Bombones & Trufas' },
    { id: 'frutas', label: 'Cacao & Frutas' },
    { id: 'especialidades', label: 'Especialidades' },
  ];

  return (
    <div className="flex flex-col w-full">
      {/* Top Banner / Editorial Introduction */}
      <section className="w-full max-w-[1320px] mx-auto px-margin md:px-margin-tablet lg:px-margin-desktop pt-space-lg pb-space-md">
        <div className="flex flex-col items-center text-center max-w-3xl mx-auto">
          <div className="inline-flex items-center gap-space-xs mb-space-xs">
            <span className="w-8 h-[1px] bg-secondary"></span>
            <span className="font-label-sm text-label-sm uppercase tracking-widest text-on-tertiary-container">
              {catalog.catalogOverline}
            </span>
            <span className="w-8 h-[1px] bg-secondary"></span>
          </div>
          <h1 className="font-headline-lg text-headline-lg md:font-display-xl md:text-display-xl text-on-primary-fixed mb-space-sm tracking-tight">
            {catalog.catalogTitle}
          </h1>

          {/* Aclaración destacada de pedido */}
          <div className="mt-space-xs p-space-md bg-surface-container rounded-xl shadow-sm max-w-2xl border border-surface-container-high/60">
            <div className="flex items-start justify-center gap-space-xs">
              <span className="material-symbols-outlined text-secondary text-[22px] mt-0.5 shrink-0">
                info
              </span>
              <p className="font-body-md text-body-md text-on-surface-variant text-center">
                {catalog.catalogNotice}
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Visual Category Filter Bar */}
      <section className="w-full max-w-[1320px] mx-auto px-margin md:px-margin-tablet lg:px-margin-desktop py-space-sm">
        <div className="flex items-center justify-center gap-space-xs sm:gap-space-sm flex-wrap">
          {categories.map((cat) => {
            const isActive = activeCategory === cat.id;
            return (
              <button
                key={cat.id}
                onClick={() => setActiveCategory(cat.id)}
                className={`px-space-md py-space-xs rounded-full font-label-md text-label-md uppercase tracking-wider transition-all duration-200 cursor-pointer ${
                  isActive
                    ? 'bg-secondary text-on-secondary shadow-sm font-semibold'
                    : 'bg-surface-container-low text-on-surface hover:bg-secondary-fixed hover:text-on-secondary-fixed'
                }`}
                type="button"
              >
                {cat.label}
              </button>
            );
          })}
        </div>
      </section>

      {/* Product Gallery Grid */}
      <section className="w-full max-w-[1320px] mx-auto px-margin md:px-margin-tablet lg:px-margin-desktop py-space-lg">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-gutter-desktop">
          {filteredProducts.map((product) => (
            <article
              key={product.id}
              className="product-card group flex flex-col bg-surface-container-lowest rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-all duration-300 border border-surface-container-high/40"
            >
              <div
                onClick={() => onSelectProduct(product)}
                className="relative w-full aspect-[4/3] overflow-hidden bg-surface-container cursor-pointer"
              >
                <img
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                  alt={product.name}
                  src={product.image}
                  referrerPolicy="no-referrer"
                />
                <div
                  className={`absolute top-3 left-3 px-2.5 py-1 rounded font-label-sm text-label-sm uppercase tracking-widest ${
                    product.badgeColor === 'secondary'
                      ? 'bg-secondary text-on-secondary'
                      : 'bg-primary-container text-surface-bright'
                  }`}
                >
                  {product.badge}
                </div>
              </div>

              <div className="p-space-md flex flex-col flex-1 justify-between bg-surface-container-lowest">
                <div>
                  <span className="font-label-sm text-label-sm uppercase tracking-widest text-on-tertiary-container block mb-1">
                    {product.subtitle}
                  </span>
                  <h2
                    onClick={() => onSelectProduct(product)}
                    className="font-headline-sm text-headline-sm text-on-primary-fixed mb-space-xs group-hover:text-secondary transition-colors cursor-pointer"
                  >
                    {product.name}
                  </h2>
                  <p className="font-body-sm text-body-sm text-on-surface-variant line-clamp-2 mb-space-sm">
                    {product.shortDesc}
                  </p>
                </div>

                <div className="pt-space-xs flex items-center justify-between border-t border-surface-container-high/40">
                  <span className="font-title-md text-title-md text-on-primary-fixed font-bold">
                    {product.price}
                  </span>
                  <button
                    onClick={() => onOpenProductModal(product)}
                    className="inline-flex items-center gap-1 font-label-md text-label-md text-secondary hover:text-on-tertiary-container transition-colors uppercase tracking-wider cursor-pointer"
                    type="button"
                  >
                    <span>Ver detalle</span>
                    <span className="material-symbols-outlined text-[16px]">arrow_forward</span>
                  </button>
                </div>
              </div>
            </article>
          ))}
        </div>
      </section>

      {/* Editorial Separator */}
      <div className="w-full max-w-[1320px] mx-auto px-margin md:px-margin-tablet lg:px-margin-desktop py-space-sm">
        <div className="flex items-center justify-center gap-space-sm">
          <span className="h-[1px] flex-1 bg-surface-container-highest"></span>
          <span className="font-headline-sm text-headline-sm text-secondary">❖</span>
          <span className="h-[1px] flex-1 bg-surface-container-highest"></span>
        </div>
      </div>

      {/* Assistance & Order Channels Banner */}
      <section className="w-full max-w-[1320px] mx-auto px-margin md:px-margin-tablet lg:px-margin-desktop py-space-lg mb-space-xl">
        <div className="bg-primary-container text-surface-bright rounded-xl p-space-lg md:p-space-xl shadow-md border border-[#3a251e]">
          <div className="grid grid-cols-1 md:grid-cols-12 gap-gutter items-center">
            <div className="md:col-span-8 flex flex-col gap-space-xs">
              <div className="flex items-center gap-space-xs">
                <span className="material-symbols-outlined text-secondary-fixed text-[20px]">
                  room_service
                </span>
                <span className="font-label-sm text-label-sm uppercase tracking-widest text-secondary-fixed">
                  Atención Personalizada
                </span>
              </div>
              <h2 className="font-headline-md text-headline-md text-surface-bright">
                {catalog.orderBannerTitle}
              </h2>
              <p className="font-body-md text-body-md text-primary-fixed-dim max-w-2xl mt-space-xs leading-relaxed">
                {catalog.orderBannerDesc}
              </p>
            </div>
            <div className="md:col-span-4 flex flex-col sm:flex-row md:flex-col gap-space-sm justify-center md:items-end">
              <a
                className="w-full sm:w-auto inline-flex items-center justify-center gap-space-xs px-space-lg py-space-sm rounded-lg bg-secondary text-on-secondary hover:bg-on-tertiary-container hover:text-on-primary font-label-md text-label-md uppercase tracking-wider transition-all duration-200 shadow-sm text-center cursor-pointer"
                href={brand.whatsappUrl}
                rel="noopener noreferrer"
                target="_blank"
              >
                <span className="material-symbols-outlined text-[18px]">chat</span>
                <span>WhatsApp: {brand.whatsappNumber}</span>
              </a>
              <button
                onClick={onOpenAndroid}
                className="w-full sm:w-auto inline-flex items-center justify-center gap-space-xs px-space-lg py-space-sm rounded-lg bg-surface-container-high text-on-primary-fixed font-label-md text-label-md uppercase tracking-wider transition-colors shadow-sm text-center hover:bg-surface-variant cursor-pointer"
              >
                <span className="material-symbols-outlined text-[18px] text-secondary">
                  android
                </span>
                <span>Disponible en App Android</span>
              </button>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};
