import React, { useState } from 'react';
import { verifyAdminPassword } from '../data/cmsStore';

interface AdminLoginModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: (password: string) => void | Promise<void>;
}

export const AdminLoginModal: React.FC<AdminLoginModalProps> = ({
  isOpen,
  onClose,
  onSuccess,
}) => {
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (await verifyAdminPassword(password)) {
      setError('');
      await onSuccess(password);
      setPassword('');
    } else {
      setError('Contraseña incorrecta. Por favor intente nuevamente.');
    }
  };

  return (
    <div
      className="fixed inset-0 z-50 bg-black/75 backdrop-blur-sm flex items-center justify-center p-4 animate-in fade-in duration-200"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className="bg-[#241713] border border-[#3e2821] max-w-md w-full rounded-xl shadow-2xl overflow-hidden animate-in zoom-in-95 duration-200">
        {/* Header */}
        <div className="bg-[#1c120e] p-5 border-b border-[#38221b] flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-lg bg-secondary flex items-center justify-center text-on-secondary shadow-md">
              <span className="material-symbols-outlined text-[20px]">admin_panel_settings</span>
            </div>
            <div>
              <h3 className="font-headline-sm text-base text-surface-bright leading-tight">
                Acceso al Panel CMS
              </h3>
              <span className="font-label-sm text-[10px] text-secondary-fixed uppercase tracking-wider">
                Fernandina de Jagua Chocolates
              </span>
            </div>
          </div>
          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full bg-white/10 flex items-center justify-center text-surface hover:bg-white/20 transition-colors cursor-pointer"
          >
            <span className="material-symbols-outlined text-[18px]">close</span>
          </button>
        </div>

        {/* Content */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <p className="text-xs text-primary-fixed-dim leading-relaxed">
            Ingrese su clave de administrador para editar los productos, descripciones, fotografías, precios y datos de contacto de todo el sitio web.
          </p>

          <div>
            <label className="block text-xs uppercase text-primary-fixed-dim mb-1 font-label-sm">
              Contraseña de Acceso
            </label>
            <input
              type="password"
              autoFocus
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                if (error) setError('');
              }}
              placeholder="Ingrese su contraseña..."
              className="w-full p-2.5 rounded bg-[#160d0a] border border-[#3e2821] text-surface focus:border-secondary focus:outline-none text-sm"
            />
            {error && <p className="text-xs text-red-400 mt-1.5">{error}</p>}
          </div>

          <div className="pt-2 flex items-center justify-end gap-2.5">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 rounded text-primary-fixed-dim hover:text-surface text-xs font-label-md uppercase tracking-wider cursor-pointer"
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="px-5 py-2.5 rounded-lg bg-secondary text-on-secondary hover:bg-on-tertiary-container text-xs font-label-md uppercase tracking-wider font-semibold transition-colors shadow-md cursor-pointer"
            >
              Ingresar al CMS
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
