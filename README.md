# Fernandina de Jagua · Chocolates

Sitio web de la chocolatería artesanal **Fernandina de Jagua** (San Miguel, Lima, Perú).
Construido con **React 19 + TypeScript + Vite + Tailwind CSS 4** y desplegado en **Firebase Hosting**.

- Proyecto Firebase: `fernandina-de-jagua-web`
- Sitio de Hosting: `fernandina-de-jagua-web` → `https://fernandina-de-jagua-web.web.app`
- Directorio publicado: `dist/` (salida de `vite build`)

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
npm run build      # genera dist/
npm run preview    # sirve dist/ localmente
npm run lint       # comprobación de tipos (tsc --noEmit)
```

## Despliegue

### Opción A — desde tu máquina (recomendado para el primer deploy)

```bash
npx firebase login              # una sola vez, abre el navegador
npx firebase use fernandina-de-jagua-web
npm run deploy                  # build + firebase deploy --only hosting
```

El primer `deploy` puede pedirte confirmación sobre el sitio `fernandina-de-jagua-web`
(el sitio ya está declarado en `firebase.json`, así que no hace falta crearlo).

### Opción B — canal de preview (sin tocar producción)

```bash
npm run deploy:preview
```

Publica en una URL temporal tipo
`https://fernandina-de-jagua-web--preview-xxxx.web.app`, válida 7 días.

### Opción C — automático desde GitHub (GitHub Actions)

Ya están incluidos dos workflows:

| Workflow | Disparador | Resultado |
|---|---|---|
| `firebase-hosting-merge.yml` | push a `main` | despliegue a producción |
| `firebase-hosting-pull-request.yml` | pull request a `main` | canal de preview + comentario con la URL |

Para activarlos hace falta un único secreto en el repositorio
(**Settings → Secrets and variables → Actions → New repository secret**):

1. Genera una cuenta de servicio con rol **Firebase Hosting Admin**:
   [Google Cloud Console → IAM → Cuentas de servicio](https://console.cloud.google.com/iam-admin/serviceaccounts?project=fernandina-de-jagua-web)
   → crear cuenta → *Claves* → *Agregar clave* → JSON.
2. Crea el secreto **`FIREBASE_SERVICE_ACCOUNT_FERNANDINA_DE_JAGUA_WEB`** con el contenido completo del JSON.

También puedes generarlo con la CLI:

```bash
npx firebase init hosting:github
```

Mientras el secreto no exista, los workflows **no fallan**: compilan el sitio y se saltan el paso de
despliegue mostrando un aviso (`warning`). Así se pueden fusionar los cambios antes de configurar
las credenciales.

## Dominio personalizado

En la consola de Firebase → **Hosting → Agregar dominio personalizado**, añade el dominio y
crea los registros DNS (`A` / `TXT`) que indique la consola. El certificado TLS se emite
automáticamente cuando el DNS propaga. No requiere cambios en el código.

## Configuración relevante

| Archivo | Función |
|---|---|
| `.firebaserc` | proyecto por defecto (`fernandina-de-jagua-web`) |
| `firebase.json` | Hosting: `public: dist`, `cleanUrls`, rewrite SPA `** → /index.html`, cabeceras de caché y seguridad |
| `vite.config.ts` | build de Vite |
| `dist/` | salida compilada (ignorada por Git) |

### Caché y SPA

- `/assets/**` se sirve con `Cache-Control: immutable` (los nombres llevan hash, así que es seguro).
- `/` e `/index.html` se sirven con `no-cache` para que cada deploy se vea al instante.
- El rewrite `** → /index.html` permite navegar a rutas internas sin 404.

> `vercel.json` se eliminó: Firebase Hosting es el hosting oficial del proyecto. Vite sigue
> detectando el framework correctamente, así que no hace falta configuración adicional.

## Panel de administración

`/#admin` abre el panel de administración (`src/views/AdminView.tsx`). Actualmente el contenido
y la contraseña se guardan en **`localStorage`** del navegador (`src/data/cmsStore.ts`), por lo que:

- Los cambios **solo se ven en el navegador donde se editaron**.
- Al limpiar los datos del navegador se pierden (usa la pestaña *Respaldo* para exportar el JSON).

Para que el contenido sea compartido entre visitantes, el siguiente paso sería mover `cmsStore`
a **Cloud Firestore**; el archivo está aislado justamente para facilitar esa migración.

## Notas

- `metadata.json` y `.env.example` son restos del entorno donde se generó el proyecto (AI Studio);
  el sitio no hace llamadas a la API de Gemini ni usa variables de entorno en tiempo de ejecución.
- El repo no contiene claves ni credenciales de Firebase: la configuración pública del SDK debe
  añadirse aparte si se integra Firestore/Auth.
