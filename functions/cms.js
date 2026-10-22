import express from 'express';
import { getFirestore } from 'firebase-admin/firestore';

const app = express();
app.use(express.json({ limit: '3mb' }));

const siteRef = () => getFirestore().collection('cms').doc('site');

function getAdminPassword(getSecret) {
  try {
    return String(getSecret('CMS_ADMIN_PASSWORD') || '').trim();
  } catch {
    return '';
  }
}

function isAuthorized(req, getSecret) {
  const configured = getAdminPassword(getSecret);
  const provided = String(req.header('x-cms-password') || '').trim();
  return Boolean(configured && provided && configured === provided);
}

export function createCmsApp({ getSecret }) {
  const cmsApp = app;

  cmsApp.get('/site', async (_req, res) => {
    try {
      const snapshot = await siteRef().get();
      if (!snapshot.exists) {
        return res.status(404).json({ success: false, error: 'El CMS todavía no tiene datos compartidos.' });
      }
      return res.status(200).json({ success: true, data: snapshot.data() });
    } catch (error) {
      console.error('Error leyendo CMS:', error);
      return res.status(503).json({ success: false, error: 'No se pudo cargar el catálogo compartido.' });
    }
  });

  cmsApp.post('/auth', (req, res) => {
    const password = String(req.body?.password || '').trim();
    const configured = getAdminPassword(getSecret);
    if (!configured || password !== configured) {
      return res.status(401).json({ success: false, error: 'Contraseña administrativa incorrecta.' });
    }
    return res.status(200).json({ success: true });
  });

  cmsApp.put('/site', async (req, res) => {
    if (!isAuthorized(req, getSecret)) {
      return res.status(401).json({ success: false, error: 'No autorizado para modificar el CMS.' });
    }

    const data = req.body?.data;
    if (!data || !data.brand || !Array.isArray(data.products)) {
      return res.status(400).json({ success: false, error: 'La estructura del catálogo no es válida.' });
    }

    try {
      await siteRef().set({ ...data, updatedAt: new Date().toISOString() });
      return res.status(200).json({ success: true });
    } catch (error) {
      console.error('Error guardando CMS:', error);
      return res.status(503).json({ success: false, error: 'No se pudo guardar el catálogo compartido.' });
    }
  });

  return cmsApp;
}
