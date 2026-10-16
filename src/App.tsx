import React, { useState, useEffect } from 'react';
import { PageRoute, Product, SiteData } from './types';
import { loadStoredSiteData, saveStoredSiteData, resetStoredSiteData } from './data/cmsStore';
import { Header } from './components/Header';
import { Footer } from './components/Footer';
import { HomeView } from './views/HomeView';
import { CatalogView } from './views/CatalogView';
import { ProductDetailView } from './views/ProductDetailView';
import { VisitUsView } from './views/VisitUsView';
import { AdminView } from './views/AdminView';
import { ProductModal } from './components/ProductModal';
import { AndroidModal } from './components/AndroidModal';
import { ConciergeModal } from './components/ConciergeModal';
import { PrivacyModal } from './components/PrivacyModal';
import { AdminLoginModal } from './components/AdminLoginModal';

export default function App() {
  const [siteData, setSiteData] = useState<SiteData>(() => loadStoredSiteData());
  const [currentRoute, setCurrentRoute] = useState<PageRoute>('inicio');
  const [selectedProduct, setSelectedProduct] = useState<Product>(() => siteData.products[0]);
  const [modalProduct, setModalProduct] = useState<Product | null>(null);
  const [androidModalOpen, setAndroidModalOpen] = useState(false);
  const [conciergeModalOpen, setConciergeModalOpen] = useState(false);
  const [privacyModalOpen, setPrivacyModalOpen] = useState(false);
  const [adminLoginModalOpen, setAdminLoginModalOpen] = useState(false);
  const [isAdminLoggedIn, setIsAdminLoggedIn] = useState(() => {
    return sessionStorage.getItem('fernandina_admin_session') === 'active';
  });

  // Save changes to localStorage whenever siteData updates
  const handleUpdateSiteData = (newData: SiteData) => {
    setSiteData(newData);
    saveStoredSiteData(newData);
    // Keep selected product in sync if updated
    if (selectedProduct) {
      const refreshed = newData.products.find((p) => p.id === selectedProduct.id);
      if (refreshed) {
        setSelectedProduct(refreshed);
      } else if (newData.products.length > 0) {
        setSelectedProduct(newData.products[0]);
      }
    }
  };

  const handleResetData = () => {
    const defaultData = resetStoredSiteData();
    setSiteData(defaultData);
    if (defaultData.products.length > 0) {
      setSelectedProduct(defaultData.products[0]);
    }
  };

  const handleRouteChange = (route: PageRoute) => {
    setCurrentRoute(route);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleSelectProduct = (product: Product) => {
    setSelectedProduct(product);
    setCurrentRoute('producto-detalle');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleOpenProductModal = (product: Product) => {
    setModalProduct(product);
  };

  const handleOpenAdmin = () => {
    if (isAdminLoggedIn) {
      setCurrentRoute('admin');
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } else {
      setAdminLoginModalOpen(true);
    }
  };

  const handleLoginSuccess = () => {
    setIsAdminLoggedIn(true);
    sessionStorage.setItem('fernandina_admin_session', 'active');
    setAdminLoginModalOpen(false);
    setCurrentRoute('admin');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleLogout = () => {
    setIsAdminLoggedIn(false);
    sessionStorage.removeItem('fernandina_admin_session');
    setCurrentRoute('inicio');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // If on admin view and logged in, render the full CMS panel
  if (currentRoute === 'admin' && isAdminLoggedIn) {
    return (
      <AdminView
        siteData={siteData}
        onUpdateSiteData={handleUpdateSiteData}
        onResetData={handleResetData}
        onExitAdmin={() => handleRouteChange('inicio')}
        onLogout={handleLogout}
      />
    );
  }

  return (
    <div className="min-h-screen flex flex-col bg-surface font-body-md text-on-surface antialiased selection:bg-secondary/20 selection:text-secondary">
      {/* Header bar */}
      <Header
        currentRoute={currentRoute}
        onRouteChange={handleRouteChange}
        onOpenConcierge={() => setConciergeModalOpen(true)}
        onOpenAdmin={handleOpenAdmin}
        isAdminLoggedIn={isAdminLoggedIn}
        brand={siteData.brand}
      />

      {/* Main viewport */}
      <main className="w-full pt-20 bg-surface flex-1">
        {currentRoute === 'inicio' && (
          <HomeView
            siteData={siteData}
            onRouteChange={handleRouteChange}
            onSelectProduct={handleSelectProduct}
            onOpenAndroid={() => setAndroidModalOpen(true)}
          />
        )}

        {currentRoute === 'productos' && (
          <CatalogView
            siteData={siteData}
            onSelectProduct={handleSelectProduct}
            onOpenProductModal={handleOpenProductModal}
            onOpenAndroid={() => setAndroidModalOpen(true)}
          />
        )}

        {currentRoute === 'producto-detalle' && (
          <ProductDetailView
            product={selectedProduct}
            siteData={siteData}
            onRouteChange={handleRouteChange}
            onSelectProduct={handleSelectProduct}
            onOpenAndroid={() => setAndroidModalOpen(true)}
          />
        )}

        {currentRoute === 'visitanos' && (
          <VisitUsView
            siteData={siteData}
            onOpenAndroid={() => setAndroidModalOpen(true)}
          />
        )}
      </main>

      {/* Footer */}
      <Footer
        onRouteChange={handleRouteChange}
        onOpenPrivacy={() => setPrivacyModalOpen(true)}
        onOpenAdmin={handleOpenAdmin}
        brand={siteData.brand}
      />

      {/* Floating Admin Toolbar when session is active */}
      {isAdminLoggedIn && currentRoute !== 'admin' && (
        <div className="fixed bottom-4 left-4 z-40 bg-[#241713] text-surface-bright px-3.5 py-2.5 rounded-xl shadow-2xl border border-secondary/50 flex items-center gap-3 animate-in fade-in slide-in-from-bottom-2">
          <div className="flex items-center gap-1.5 text-xs text-secondary font-medium">
            <span className="w-2 h-2 rounded-full bg-emerald-400"></span>
            <span>CMS Activo</span>
          </div>
          <button
            onClick={() => handleRouteChange('admin')}
            className="px-2.5 py-1 rounded bg-secondary text-on-secondary text-xs font-label-md uppercase tracking-wider hover:bg-on-tertiary-container transition-colors cursor-pointer"
          >
            Editar Sitio
          </button>
          <button
            onClick={handleLogout}
            className="text-xs text-primary-fixed-dim hover:text-surface cursor-pointer"
            title="Cerrar sesión de administrador"
          >
            <span className="material-symbols-outlined text-[16px]">logout</span>
          </button>
        </div>
      )}

      {/* Modals */}
      <ProductModal
        product={modalProduct}
        onClose={() => setModalProduct(null)}
        onGoToDetail={(prod) => {
          handleSelectProduct(prod);
        }}
        brand={siteData.brand}
      />

      <AndroidModal
        isOpen={androidModalOpen}
        onClose={() => setAndroidModalOpen(false)}
      />

      <ConciergeModal
        isOpen={conciergeModalOpen}
        onClose={() => setConciergeModalOpen(false)}
        onOpenAndroid={() => setAndroidModalOpen(true)}
        brand={siteData.brand}
      />

      <PrivacyModal
        isOpen={privacyModalOpen}
        onClose={() => setPrivacyModalOpen(false)}
      />

      <AdminLoginModal
        isOpen={adminLoginModalOpen}
        onClose={() => setAdminLoginModalOpen(false)}
        onSuccess={handleLoginSuccess}
      />
    </div>
  );
}
