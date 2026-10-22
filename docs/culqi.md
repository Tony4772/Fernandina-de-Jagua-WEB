# Cobros con Culqi (Tarjeta + Yape)

Checkout de la web: **carrito → datos del cliente → Culqi Checkout (tarjeta o Yape) → cargo en el backend → pedido pagado**.

```
Navegador                     Firebase Hosting            Cloud Function `payments`        Culqi API
─────────                     ────────────────            ─────────────────────────         ─────────
carrito (localStorage)
  │
  ├─ POST /api/payments/orders ──────────────▶ crea pedido `pending`
  │                                            recalcula monto (catálogo CMS en Firestore)
  │◀───────────── {orderId, amount} ───────────┤
  │
  ├─ Culqi Checkout (tokeniza tarjeta/Yape) ──────────────────────────────────────────────▶ token
  │   tarjeta → tkn_…    Yape → ype_… (otp + celular)
  ├─ POST /orders/:id/pay {token} ───────────▶ reserva el pedido (transacción)
  │                                            POST /v2/charges (sk_…) ──────────────────▶ cargo
  │◀───────────── {status: paid} ──────────────┤
  │
  └─ (async) Culqi webhook ──────────────────▶ re-consulta el cargo y actualiza Firestore
```

Puntos clave del diseño:

- **La llave secreta (`sk_live_…`) vive solo en Secret Manager.** El navegador solo usa la llave
  pública (`pk_live_…`) para tokenizar. Los datos de la tarjeta nunca pasan por la web ni por el
  backend.
- **El precio lo manda el servidor.** El backend lee el catálogo actual del CMS en Firestore,
  convierte el `priceValue` publicado a céntimos e ignora cualquier precio que envíe el navegador.
- **Un pedido se cobra una sola vez.** Antes de llamar a Culqi se reserva el pedido en Firestore
  (`pending` → `processing`) dentro de una transacción.
- **El webhook no se cree a ciegas.** Culqi no firma los webhooks: se valida Basic Auth (si está
  activado) y siempre se re-consulta el cargo/orden en la API antes de marcar `paid`.
- **Yape es token nativo.** El token Yape es `ype_live_…` / `ype_test_…` y el backend valida su formato,
  el límite de S/ 2,000 por operación y lo cobra igual que una tarjeta (`source_id = ype_…`).

---

## 1. Requisitos

- Proyecto Firebase en plan **Blaze** (Cloud Functions y Secret Manager lo requieren; hay capa
  gratuita y el uso de esta web es mínimo).
- Llaves de Culqi: `pk_live_…` + `sk_live_…` (o las de prueba `pk_test_…` / `sk_test_…`).
- Firestore habilitado en el proyecto `fernandina-de-jagua-web`.
- Yape activo en CulqiPanel → **Configuración → Medios de pago → Yape** (requiere validación de Culqi).

## 2. Llave pública en el frontend

`src/config.ts` usa `VITE_CULQI_PUBLIC_KEY`; si no existe, cae al valor por defecto del archivo.
Puedes fijarla en un `.env` local (ignorado por Git):

```bash
VITE_CULQI_PUBLIC_KEY="pk_live_xxxxxxxxxxxxxxxx"
```

Las llaves públicas son seguras de exponer: solo permiten crear tokens (`tkn_…` y `ype_…`).

## 3. Llave secreta en Secret Manager

```bash
firebase login
firebase use fernandina-de-jagua-web
firebase functions:secrets:set CULQI_SECRET_KEY      # pega sk_live_... (o sk_test_...)
firebase functions:secrets:set CULQI_WEBHOOK_USER    # usuario que usarás en el panel de Culqi
firebase functions:secrets:set CULQI_WEBHOOK_PASS    # contraseña que usarás en el panel de Culqi
```

> ⚠️ **Regenera la llave secreta.** Si alguna vez se compartió la `sk_live_…` por chat, correo o
> un repositorio, revócala en **CulqiPanel → Desarrollo → API Keys → Regenerar** y guarda la nueva
> únicamente en Secret Manager. Nunca la pongas en `src/`, `.env` versionado ni en el CMS.

## 4. Despliegue

La forma más rápida es el script interactivo, que además configura los secretos:

```bash
npm run go-live           # requisitos + login + secretos + deploy + verificación
npm run go-live -- --check  # solo verificar requisitos
```

O por partes:

```bash
npm run deploy            # instala functions, build, deploy de hosting + functions + rules
npm run deploy:functions
npm run deploy:hosting
npm run deploy:rules
```

Verifica que el backend responde:

```bash
curl https://fernandina-de-jagua-web.web.app/api/payments/health
# {"ok":true,"service":"fernandina-payments","currency":"PEN","mode":"live","products":8}
```

Si `mode` es `sin_configurar`, el secreto no está llegando: revisa el paso 3 y vuelve a desplegar.

## 5. Webhook en el panel de Culqi

**CulqiPanel → Eventos → Webhooks → + Añadir**

| Campo | Valor |
|---|---|
| Evento | `charge.creation.succeeded` **y** `order.status.changed` |
| URL | `https://fernandina-de-jagua-web.web.app/api/payments/webhooks/culqi` |
| Autenticación | Activada, con el usuario/contraseña de `CULQI_WEBHOOK_USER` / `CULQI_WEBHOOK_PASS` |

El webhook es la red de seguridad para pagos que se confirman fuera de la respuesta síncrona
(Yape, reintentos, cortes de red). Sin él, esos pedidos quedarían en `processing`.

## 6. Yape

### Cómo funciona

1. El cliente elige **Yape** en el checkout (`CheckoutModal.tsx` → selector Tarjeta / Yape).
2. Se abre Culqi Checkout Custom con `paymentMethods: { yape: true }`. El iframe de Culqi pide:
   - Número Yape (9 dígitos)
   - Código de aprobación Yape (6 dígitos, vigencia 2 min): se obtiene en **Yape app → Menú ☰ → Código de aprobación**
3. Culqi genera un token `ype_live_…` (o `ype_test_…`) y lo devuelve a `instance.token.id`.
4. El frontend envía `POST /orders/:id/pay { token: "ype_…" }`.
5. El backend valida formato `/^(tkn|ype)_(live|test)_…/`, verifica que el monto no supere **S/ 2,000** (límite Yape/Culqi) y crea el cargo en `POST /v2/charges { source_id: "ype_…" }`.

### Límites y validaciones

| Regla | Dónde se valida | Mensaje |
|---|---|---|
| Solo PEN | `functions/app.js` (`CURRENCY = PEN`) y Culqi Checkout `currency: PEN` | — |
| Máx. S/ 2,000 por Yape | Frontend (`YAPE_MAX_AMOUNT_CENTS`) + Backend (`YAPE_MAX_AMOUNT`) | “Yape permite un máximo de S/ 2,000 por operación.” |
| OTP 6 dígitos, 2 min vigencia | Culqi Checkout (Yape) | Mensaje de Culqi: `user_message` |
| Celular 9 dígitos | Culqi Checkout | Validación Culqi |

Si el carrito supera S/ 2,000, el botón Yape se deshabilita y se sugiere pagar con tarjeta o reducir unidades.

### Archivos relevantes

| Archivo | Rol Yape |
|---|---|
| `src/lib/culqi.ts` | `openCulqiCheckout({ preferredMethod: 'yape' })` ordena pestañas `['yape','tarjeta']`, acepta `ype_…` |
| `src/components/CheckoutModal.tsx` | Selector Tarjeta/Yape, instrucciones Yape, límite S/ 2,000, manejo `isYapeToken` |
| `src/config.ts` | `YAPE_MAX_AMOUNT_CENTS = 200_000` |
| `functions/app.js` | Regex `^(tkn\|ype)_…`, `isYapeToken()`, validación monto, `brand = YAPE` |

### Pruebas Yape

Con llaves **test** de Culqi, usa los números y códigos de prueba que provee CulqiPanel (sección Yape → Pruebas). En `functions/test/smoke.mjs` hay casos:

```bash
npm --prefix functions test   # incluye: token tkn_… y ype_… + límite S/ 2,000
```

En local con emuladores (requiere Java):

```bash
npm run emulators   # hosting :5000 + functions :5001 + firestore :8080
# Abrir http://localhost:3000 → agregar al carrito → checkout → elegir Yape → usar datos de prueba
```

### UX

- Selector visual **Tarjeta | Yape** en el modal, con estado seleccionado y `aria-pressed`.
- Tarjeta muestra esquemas VISA/MC/AMEX; Yape muestra pasos numerados y alerta de vigencia 2 min.
- Total y contador de ítems siempre visibles; “Modo de prueba” si `pk_test_`.
- Botón principal adapta color y texto según método (`bg-secondary` vs `bg-[#742384]` Yape).
- Mensajes de error de Culqi (OTP expirado, celular inválido) se muestran tal cual y ofrecen fallback WhatsApp.

## 7. Probar antes de cobrar de verdad

Usa llaves `pk_test_` / `sk_test_` y las [tarjetas de prueba de Culqi](https://docs.culqi.com/es/documentacion/pagos-online/tarjetas-de-prueba/):

| Caso | Número de tarjeta |
|---|---|
| Pago aprobado | `4111 1111 1111 1111` |
| Pago rechazado | `4000 0000 0000 0005` |

Para Yape en test, usa el celular y OTP de prueba indicados en **CulqiPanel → Desarrollo → Yape (test)**. El flujo es idéntico a producción pero no genera cobro real.

Con llaves de prueba el checkout muestra "Modo de prueba" en el formulario de pago. En local:

```bash
npm run emulators        # hosting + functions + firestore (requiere Java para Firestore)
```

## 8. Dónde ver los pedidos

- **Firestore → `orders`**: cada pedido con `status`, `items`, `customer`, `charge` y fechas.
  Estados: `pending`, `processing`, `paid`, `failed`. En `charge` se guarda `brand` (`VISA`/`YAPE`) y `paymentMethod` (`card`/`yape`).
- **CulqiPanel → Cargos**: el detalle del cobro, con la referencia `order_number` en la metadata y `source_id` (`tkn_…` o `ype_…`).

Para reembolsar, hazlo desde el panel de Culqi (Cargos → Devolver) y actualiza el pedido a mano en
Firestore si necesitas reflejarlo.

## 9. Límites conocidos (siguiente fase)

- **Sin 3DS.** La autenticación 3-D Secure de Culqi exige un paso adicional de confirmación. Hoy el
  cargo es directo: algunos bancos pueden rechazar tarjetas que la exigen. Si el volumen lo requiere,
  el siguiente paso es integrar Culqi 3DS.
- **Sin control de stock ni cupones.** Todos los productos del catálogo se pueden comprar.
- **Delivery no cobrado en línea.** El envío se coordina por WhatsApp tras el pago (así lo indica el
  checkout y el mensaje de éxito).
- **El catálogo de cobro se lee desde Firestore.** Si agregas, eliminas o cambias un producto en
  el CMS, el siguiente pedido usa esa versión del catálogo. Si el documento CMS aún no existe,
  el backend usa el catálogo incluido como respaldo de arranque.
- **Zona horaria de los números de pedido**: se generan con fecha UTC (`FJ-YYYYMMDD-XXXXXX`).
- **Yape solo PEN y S/ 2,000**: restricción de Culqi/Yape, validada en frontend y backend.

## 10. Checklist rápido de producción

- [ ] Proyecto en plan Blaze y Firestore habilitado.
- [ ] `CULQI_SECRET_KEY` con la llave **live regenerada**.
- [ ] `CULQI_WEBHOOK_USER` y `CULQI_WEBHOOK_PASS` configurados y repetidos en el panel de Culqi.
- [ ] Yape habilitado en CulqiPanel y probado en test (`ype_test_…`).
- [ ] `npm run deploy` ejecutado sin errores.
- [ ] `/api/payments/health` devuelve `"mode":"live"`.
- [ ] Compra de prueba real con S/ 1 por **tarjeta** y otra por **Yape** (ambas con devolución posterior desde CulqiPanel).
- [ ] Reglas de Firestore desplegadas (`npm run deploy:rules`).
