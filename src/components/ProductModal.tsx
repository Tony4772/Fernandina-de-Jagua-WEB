import React from 'react';
import { BrandConfig, Product } from '../types';

interface ProductModalProps {
  product: Product | null;
  onClose: () => void;
  onGoToDetail: (product: Product) => void;
  brand: BrandConfig;
  onAddToCart?: (product: Product) => void;
}

export const ProductModal: React.FC<ProductModalProps> = ({
  product,
  onClose,
  onGoToDetail,
  brand,
  onAddToCart,
}) => {
  if (!product) return null;

  const whatsappMessage = encodeURIComponent(
    `Hola, quisiera pedir la ${product.name} (${product.price}).`
  );
  const whatsappUrl = `${brand.whatsappUrl}?text=${whatsappMessage}`;

  return (
    <div
      className="fixed inset-0 z-50 bg-primary-container/70 backdrop-blur-sm flex items-center justify-center p-margin animate-in fade-in duration-200"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className="bg-surface-container-lowest max-w-lg w-full rounded-xl shadow-2xl overflow-hidden animate-in zoom-in-95 duration-200 flex flex-col border border-surface-container-high">
        {/* Modal Image Header */}
        <div className="relative w-full aspect-[16/9] overflow-hidden bg-surface-container">
          <img
            src={product.image}
            alt={product.name}
            className="w-full h-full object-cover"
            referrerPolicy="no-referrer"
          />
          <div className="absolute top-3 left-3 bg-primary-container/90 backdrop-blur-sm text-surface-bright px-2.5 py-1 rounded font-label-sm text-label-sm uppercase tracking-widest">
            {product.badge}
          </div>
          <button
            onClick={onClose}
            className="absolute top-3 right-3 w-8 h-8 rounded-full bg-surface-container-lowest/80 backdrop-blur-md flex items-center justify-center text-on-surface hover:bg-surface-container-lowest transition-colors cursor-pointer"
            aria-label="Cerrar modal"
          >
            <span className="material-symbols-outlined text-[20px]">close</span>
          </button>
        </div>

        <div className="p-space-lg flex flex-col gap-space-sm">
          <div className="flex items-start justify-between">
            <div className="flex flex-col">
              <span className="font-label-sm text-label-sm uppercase tracking-widest text-on-tertiary-container mb-1">
                {product.subtitle} · {product.categoryLabel}
              </span>
              <h3 className="font-headline-md text-headline-md text-on-primary-fixed">
                {product.name}
              </h3>
            </div>
          </div>

          <div className="flex items-center justify-between py-space-xs bg-surface-container-low px-space-md rounded-lg">
            <span className="font-label-md text-label-md text-on-surface-variant uppercase tracking-wider">
              Precio Sugerido
            </span>
            <span className="font-headline-sm text-headline-sm text-secondary font-bold">
              {product.price}
            </span>
          </div>

          <p className="font-body-md text-body-md text-on-surface-variant leading-relaxed">
            {product.fullDesc}
          </p>

          {/* Quick Sensory Summary */}
          <div className="grid grid-cols-2 gap-2 p-space-sm bg-surface-container-low rounded-lg text-xs font-body-sm">
            <div>
              <span className="text-on-surface-variant font-medium block">Peso neto:</span>
              <span className="text-on-surface font-semibold">{product.netWeight}</span>
            </div>
            <div>
              <span className="text-on-surface-variant font-medium block">Perfil aromático:</span>
              <span className="text-on-surface">{product.aromaProfile}</span>
            </div>
          </div>

          <div className="p-space-sm bg-surface-container rounded-lg flex items-start gap-space-xs mt-space-xs">
            <span className="material-symbols-outlined text-secondary text-[20px] shrink-0 mt-0.5">
              check_circle
            </span>
            <span className="font-body-sm text-body-sm text-on-surface-variant">
              Agrega esta pieza al carrito para pagar en línea con tarjeta o Yape, o escríbenos al WhatsApp {brand.whatsappNumber} para coordinar tu pedido.
            </span>
          </div>

          <div className="pt-space-xs flex flex-wrap items-center justify-between gap-space-sm">
            <button
              onClick={() => {
                onClose();
                onGoToDetail(product);
              }}
              className="inline-flex items-center gap-1 font-label-md text-label-md uppercase tracking-wider text-secondary hover:text-on-tertiary-container transition-colors cursor-pointer"
            >
              <span>Ver ficha completa</span>
              <span className="material-symbols-outlined text-[16px]">arrow_forward</span>
            </button>

            <div className="flex items-center gap-space-xs">
              <button
                className="px-space-md py-space-xs rounded font-label-md text-label-md uppercase tracking-wider text-on-surface-variant hover:text-on-primary-fixed transition-colors cursor-pointer"
                onClick={onClose}
                type="button"
              >
                Cerrar
              </button>
              {onAddToCart && (
                <button
                  className="inline-flex items-center gap-space-xs px-space-md py-space-xs rounded border border-secondary text-secondary hover:bg-secondary hover:text-on-secondary font-label-md text-label-md uppercase tracking-wider transition-colors cursor-pointer"
                  onClick={() => onAddToCart(product)}
                  type="button"
                >
                  <span className="material-symbols-outlined text-[16px]">add_shopping_cart</span>
                  <span>Agregar</span>
                </button>
              )}
              <a
                className="inline-flex items-center gap-space-xs px-space-md py-space-xs rounded bg-secondary text-on-secondary hover:bg-on-tertiary-container font-label-md text-label-md uppercase tracking-wider transition-colors shadow-sm cursor-pointer"
                href={whatsappUrl}
                rel="noopener noreferrer"
                target="_blank"
              >
                <span className="material-symbols-outlined text-[16px]">chat</span>
                <span>Pedir por WhatsApp</span>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
