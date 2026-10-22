import React from 'react';
import { ANDROID_APP_URL, GOOGLE_PLAY_BADGE_SRC } from '../lib/appLinks';

interface GooglePlayBadgeProps {
  className?: string;
  imageClassName?: string;
  onClick?: React.MouseEventHandler<HTMLAnchorElement>;
}

export const GooglePlayBadge: React.FC<GooglePlayBadgeProps> = ({
  className = '',
  imageClassName = 'h-12 w-auto',
  onClick,
}) => (
  <a
    href={ANDROID_APP_URL}
    target="_blank"
    rel="noopener noreferrer"
    aria-label="Descargar la app Fernandina de Jagua en Google Play"
    onClick={onClick}
    className={`inline-flex max-w-full items-center justify-center rounded-[16px] transition-transform hover:scale-[1.02] focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-secondary ${className}`}
  >
    <img
      src={GOOGLE_PLAY_BADGE_SRC}
      alt="Disponible en Google Play"
      width={646}
      height={250}
      loading="lazy"
      decoding="async"
      className={`block max-w-full object-contain ${imageClassName}`}
    />
  </a>
);
