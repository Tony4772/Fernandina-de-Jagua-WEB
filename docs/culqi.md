# Cobros con Culqi

Checkout de la web: **carrito → datos del cliente → Culqi Checkout (tarjeta o Yape) → cargo en el backend → pedido pagado**.

```
Navegador                     Firebase Hosting            Cloud Function `payments`        Culqi API
─────────                     ────────────────            ─────────────────────────         ─────────
carrito (localStorage)
  │
  ├─ POST /api/payments/orders ──────────────▶ crea pedido `pending`
  │                                            recalcula monto (catalog.json)
  │◀───────────── {orderId, amount} ───────────┤
  │
  ├─ Culqi Checkout (tokeniza tarjeta/Yape) ──────────────────────────────────────────────▶ token
  │
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
- **El precio lo manda el servidor.** `functions/catalog.json` se genera desde
  `src/data/products.ts` (`npm run catalog:sync`) y el backend ignora cualquier precio que envíe
  el navegador.
- **Un pedido se cobra una sola vez.** Antes de llamar a Culqi se reserva el pedido en Firestore
  (`pending` → `processing`) dentro de una transacción.
- **El webhook no se cree a ciegas.** Culqi no firma los webhooks: se valida Basic Auth (si está
  activado) y siempre se re-consulta el cargo/orden en la API antes de marcar `paid`.

---

## 1. Requisitos

- Proyecto Firebase en plan **Blaze** (Cloud Functions y Secret Manager lo requieren; hay capa
  gratuita y el uso de esta web es mínimo).
- Llaves de Culqi: `pk_live_…` + `sk_live_…` (o las de prueba `pk_test_…` / `sk_test_…`).
- Firestore habilitado en el proyecto `fernandina-de-jagua-web`.

## 2. Llave pública en el frontend

`src/config.ts` usa `VITE_CULQI_PUBLIC_KEY`; si no existe, cae al valor por defecto del archivo.
Puedes fijarla en un `.env` local (ignorado por Git):

```bash
VITE_CULQI_PUBLIC_KEY="pk_live_xxxxxxxxxxxxxxxx"
```

Las llaves públicas son seguras de exponer: solo permiten crear tokens.

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

```bash
npm run deploy            # instala functions, build, deploy de hosting + functions + rules
# o por partes:
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

## 6. Probar antes de cobrar de verdad

Usa llaves `pk_test_` / `sk_test_` y las [tarjetas de prueba de Culqi](https://docs.culqi.com/es/documentacion/pagos-online/tarjetas-de-prueba/):

| Caso | Número de tarjeta |
|---|---|
| Pago aprobado | `4111 1111 1111 1111` |
| Pago rechazado | `4000 0000 0000 0005` |

Con llaves de prueba el checkout muestra "Modo de prueba" en el formulario de pago. En local:

```bash
npm run emulators        # hosting + functions + firestore (requiere Java para Firestore)
```

## 7. Dónde ver los pedidos

- **Firestore → `orders`**: cada pedido con `status`, `items`, `customer`, `charge` y fechas.
  Estados: `pending`, `processing`, `paid`, `failed`.
- **CulqiPanel → Cargos**: el detalle del cobro, con la referencia `order_number` en la metadata.

Para reembolsar, hazlo desde el panel de Culqi (Cargos → Devolver) y actualiza el pedido a mano en
Firestore si necesitas reflejarlo.

## 8. Límites conocidos (siguiente fase)

- **Sin 3DS.** La autenticación 3-D Secure de Culqi exige un paso adicional de confirmación. Hoy el
  cargo es directo: algunos bancos pueden rechazar tarjetas que la exigen. Si el volumen lo requiere,
  el siguiente paso es integrar Culqi 3DS.
- **Sin control de stock ni cupones.** Todos los productos del catálogo se pueden comprar.
- **Delivery no cobrado en línea.** El envío se coordina por WhatsApp tras el pago (así lo indica el
  checkout y el mensaje de éxito).
- **El CMS sigue en `localStorage`.** Si cambias precios desde el panel admin, el backend seguirá
  usando `functions/catalog.json`: actualiza `src/data/products.ts` y ejecuta
  `npm run catalog:sync` (o migra el CMS a Firestore en una siguiente fase).
- **Zona horaria de los números de pedido**: se generan con fecha UTC (`FJ-YYYYMMDD-XXXXXX`).

## 9. Checklist rápido de producción

- [ ] Proyecto en plan Blaze y Firestore habilitado.
- [ ] `CULQI_SECRET_KEY` con la llave **live regenerada**.
- [ ] `CULQI_WEBHOOK_USER` y `CULQI_WEBHOOK_PASS` configurados y repetidos en el panel de Culqi.
- [ ] `npm run deploy` ejecutado sin errores.
- [ ] `/api/payments/health` devuelve `"mode":"live"`.
- [ ] Compra de prueba real con S/ 1 y devolución posterior desde CulqiPanel.
- [ ] Reglas de Firestore desplegadas (`npm run deploy:rules`).
