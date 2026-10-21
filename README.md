# Fernandina de Jagua · Chocolates

Sitio web de la chocolatería artesanal **Fernandina de Jagua** (San Miguel, Lima, Perú).
Construido con **React 19 + TypeScript + Vite + Tailwind CSS 4** y desplegado en **Firebase Hosting**.

- Proyecto Firebase: `fernandina-de-jagua-web`
- Sitio de Hosting: `fernandina-de-jagua-web` → `https://fernandina-de-jagua-web.web.app`
- Directorio publicado: `dist/` (salida de `vite build`)
- Cobros en línea: **Culqi** (tarjeta y Yape) con Cloud Functions + Firestore → ver [`docs/culqi.md`](docs/culqi.md)

## Requisitos

- Node.js 22 o superior
- Acceso al proyecto de Firebase (rol *Firebase Hosting Admin*)

## Desarrollo local

```bash
npm install
npm run dev        # http://localhost:3000
```

## Compilar y verificar

```bash
npm run build        # sincroniza el catálogo de pagos y genera dist/
npm run preview      # sirve dist/ localmente
npm run lint         # comprobación de tipos (tsc --noEmit)
npm run catalog:sync # regenera functions/catalog.json desde src/data/products.ts
npm --prefix functions test   # pruebas del backend de pagos (sin red)
```

## Despliegue

### Opción A — desde tu máquina (recomendado para el primer deploy)

```bash
npx firebase login              # una sola vez, abre el navegador
npx firebase use fernandina-de-jagua-web

# Antes del primer deploy, carga la llave secreta de Culqi (ver docs/culqi.md):
npx firebase functions:secrets:set CULQI_SECRET_KEY

npm run deploy                  # functions + hosting + reglas de Firestore
```

El proyecto debe estar en plan **Blaze** para usar Cloud Functions y Secret Manager.

El primer `deploy` puede pedirte confirmación sobre el sitio `fernandina-de-jagua-web`
(el sitio ya está declarado en `firebase.json`, así que no hace falta crearlo).

Comandos de despliegue parcial:

```bash
npm run deploy:functions   # solo el backend de pagos
npm run deploy:hosting     # solo el sitio
npm run deploy:rules       # solo reglas de Firestore
```

### Opción B — canal de preview (sin tocar producción)

```bash
npm run deploy:preview
```

Publica en una URL temporal tipo
`https://fernandina-de-jagua-web--preview-xxxx.web.app`, válida 7 días.

### Opción C — automático desde GitHub (GitHub Actions)

Las definiciones están en **`docs/github-actions/`** como plantillas (no se pudieron subir
directamente a `.github/workflows/` por permisos del token que las creó). Para activarlas:

```bash
mkdir -p .github/workflows
cp docs/github-actions/*.yml .github/workflows/
git add .github/workflows && git commit -m "Activar deploy de Firebase Hosting"
git push
```

| Plantilla | Disparador | Resultado |
|---|---|---|
| `firebase-hosting-merge.yml` | push a `main` | despliegue a producción |
| `firebase-hosting-pull-request.yml` | pull request a `main` | canal de preview + comentario con la URL |

Hace falta un único secreto en el repositorio
(**Settings → Secrets and variables → Actions → New repository secret**):

1. Genera una cuenta de servicio con rol **Firebase Hosting Admin**:
   [Google Cloud Console → IAM → Cuentas de servicio](https://console.cloud.google.com/iam-admin/serviceaccounts?project=fernandina-de-jagua-web)
   → crear cuenta → *Claves* → *Agregar clave* → JSON.
2. Crea el secreto **`FIREBASE_SERVICE_ACCOUNT_FERNANDINA_DE_JAGUA_WEB`** con el contenido completo del JSON.

Mientras el secreto no exista, los workflows no fallan: compilan el sitio y se saltan el paso de
despliegue mostrando un aviso (`warning`). También puedes dejar que la CLI lo configure todo:

```bash
npx firebase init hosting:github
```

## Dominio personalizado

En la consola de Firebase → **Hosting → Agregar dominio personalizado**, añade el dominio y
crea los registros DNS (`A` / `TXT`) que indique la consola. El certificado TLS se emite
automáticamente cuando el DNS propaga. No requiere cambios en el código.

## Configuración relevante

| Archivo | Función |
|---|---|
| `.firebaserc` | proyecto por defecto (`fernandina-de-jagua-web`) |
| `firebase.json` | Hosting: `public: dist`, `cleanUrls`, rewrite SPA `** → /index.html`, cabeceras de caché y seguridad |
| `vite.config.ts` | build de Vite y proxy de `/api/payments` al emulador |
| `functions/` | backend de pagos (Cloud Functions, Node 22) |
| `functions/catalog.json` | catálogo de precios que usa el servidor para cobrar (generado) |
| `firestore.rules` | Firestore cerrado al cliente: solo accede el Admin SDK |
| `dist/` | salida compilada (ignorada por Git) |

### Caché y SPA

- `/assets/**` se sirve con `Cache-Control: immutable` (los nombres llevan hash, así que es seguro).
- `/` e `/index.html` se sirven con `no-cache` para que cada deploy se vea al instante.
- El rewrite `** → /index.html` permite navegar a rutas internas sin 404.

> `vercel.json` se eliminó: Firebase Hosting es el hosting oficial del proyecto. Vite sigue
> detectando el framework correctamente, así que no hace falta configuración adicional.

## Cobros con Culqi

El sitio tiene carrito y pago en línea real:

- **Carrito** persistente en el navegador (`src/lib/cart.ts`), visible en el header y en cada
  producto.
- **Checkout** (`src/components/CheckoutModal.tsx`): datos del cliente → Culqi Checkout Custom
  (tarjeta y Yape) → cargo en el backend.
- **Backend** (`functions/`): crea el pedido, recalcula el monto con `functions/catalog.json`
  (nunca confía en el precio del navegador), reserva el pedido en Firestore y crea el cargo con la
  llave secreta en Secret Manager.
- **Webhook** (`POST /api/payments/webhooks/culqi`): re-consulta el cargo en Culqi y actualiza el
  pedido, sin confiar en el cuerpo del evento.
- **Firestore** (`orders`): pedidos con estado `pending` / `processing` / `paid` / `failed`.

La puesta en marcha completa (llaves, secretos, webhook, pruebas y límites) está en
**[`docs/culqi.md`](docs/culqi.md)**.

> La llave secreta `sk_…` nunca debe estar en el repositorio, en `.env` versionados ni en el CMS.
> Si se compartió por un canal inseguro, regenérala en CulqiPanel → Desarrollo → API Keys.

## Panel de administración

`/#admin` abre el panel de administración (`src/views/AdminView.tsx`). Actualmente el contenido
y la contraseña se guardan en **`localStorage`** del navegador (`src/data/cmsStore.ts`), por lo que:

- Los cambios **solo se ven en el navegador donde se editaron**.
- Al limpiar los datos del navegador se pierden (usa la pestaña *Respaldo* para exportar el JSON).

Para que el contenido sea compartido entre visitantes, el siguiente paso sería mover `cmsStore`
a **Cloud Firestore**; el archivo está aislado justamente para facilitar esa migración.

> Importante para pagos: si cambias precios desde el panel admin, el backend seguirá cobrando con
> `functions/catalog.json`. Actualiza `src/data/products.ts` y ejecuta `npm run catalog:sync`
> (el build ya lo hace automáticamente) o migra el CMS a Firestore.

## Notas

- `metadata.json` y `.env.example` son restos del entorno donde se generó el proyecto (AI Studio);
  el sitio no hace llamadas a la API de Gemini ni usa variables de entorno en tiempo de ejecución.
- El repo no contiene claves ni credenciales de Firebase: la configuración pública del SDK debe
  añadirse aparte si se integra Firestore/Auth.
