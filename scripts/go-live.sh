#!/usr/bin/env bash
#
# Publica la web y el backend de pagos en Firebase Hosting.
#
# Uso:
#   ./scripts/go-live.sh            # configurar secretos + desplegar
#   ./scripts/go-live.sh --check    # solo verificar requisitos
#   SKIP_SECRETS=1 ./scripts/go-live.sh   # desplegar sin tocar los secretos
#
# La llave secreta de Culqi se pide por consola (no se muestra) y se guarda
# únicamente en Secret Manager. Nunca se escribe en el repositorio.
#
# Requisitos: proyecto en plan Blaze y Firestore habilitado. El primer deploy
# pide confirmación para habilitar las APIs necesarias (Functions, Firestore).
set -euo pipefail

cd "$(dirname "$0")/.."

PROJECT_ID="fernandina-de-jagua-web"
SITE_URL="https://${PROJECT_ID}.web.app"
CHECK_ONLY=0
[ "${1:-}" = "--check" ] && CHECK_ONLY=1

bold() { printf '\n\033[1m%s\033[0m\n' "$*"; }
info() { printf '  %s\n' "$*"; }
fail() { printf '\n✘ %s\n' "$*" >&2; exit 1; }

bold "1/6 · Comprobando requisitos"

command -v node >/dev/null 2>&1 || fail "Falta Node.js 22 o superior."
NODE_MAJOR="$(node -p 'process.versions.node.split(".")[0]')"
[ "$NODE_MAJOR" -ge 22 ] || fail "Se requiere Node.js 22+ (tienes $(node -v))."

if ! npx --no-install firebase --version >/dev/null 2>&1; then
  info "Instalando dependencias del proyecto…"
  npm install --no-audit --no-fund
fi

if ! npx firebase login:list 2>/dev/null | grep -q '@'; then
  if [ "$CHECK_ONLY" = "1" ]; then
    fail "No hay sesión de Firebase. Ejecuta: npx firebase login"
  fi
  bold "2/6 · Iniciando sesión en Firebase"
  npx firebase login
fi
info "Sesión de Firebase: OK"

npx firebase use "$PROJECT_ID" >/dev/null 2>&1 || {
  [ "$CHECK_ONLY" = "1" ] && fail "Tu cuenta no tiene acceso al proyecto $PROJECT_ID."
  bold "2/6 · Seleccionando el proyecto"
  npx firebase use --add
}
info "Proyecto: $PROJECT_ID"

[ -d functions/node_modules ] || npm --prefix functions install --no-audit --no-fund

if [ "$CHECK_ONLY" = "1" ]; then
  bold "Requisitos verificados. Ejecuta sin --check para publicar."
  exit 0
fi

# ---------------------------------------------------------------------------
# 3. Secretos
# ---------------------------------------------------------------------------
if [ "${SKIP_SECRETS:-0}" != "1" ]; then
  bold "3/6 · Llave secreta de Culqi"
  printf '  Pega la llave SECRETA (sk_live_... o sk_test_...). No se mostrará:\n  '
  read -r -s CULQI_KEY
  printf '\n'

  case "$CULQI_KEY" in
    sk_live_*|sk_test_*) ;;
    *) fail "La llave debe empezar con sk_live_ o sk_test_." ;;
  esac

  # Se envía por stdin: la llave nunca toca el disco ni el historial.
  printf '%s' "$CULQI_KEY" | npx firebase functions:secrets:set CULQI_SECRET_KEY \
    --data-file - --project "$PROJECT_ID" --force

  case "$CULQI_KEY" in
    sk_test_*) info "Aviso: llave de PRUEBA. Los cobros no serán reales." ;;
  esac
  unset CULQI_KEY
  info "Llave guardada en Secret Manager."

  bold "   Credenciales del webhook"
  WEBHOOK_USER="${CULQI_WEBHOOK_USER:-culqi}"
  WEBHOOK_PASS="${CULQI_WEBHOOK_PASS:-$(LC_ALL=C tr -dc 'A-Za-z0-9' </dev/urandom | head -c 24)}"

  printf '%s' "$WEBHOOK_USER" | npx firebase functions:secrets:set CULQI_WEBHOOK_USER \
    --data-file - --project "$PROJECT_ID" --force
  printf '%s' "$WEBHOOK_PASS" | npx firebase functions:secrets:set CULQI_WEBHOOK_PASS \
    --data-file - --project "$PROJECT_ID" --force
  info "Credenciales del webhook guardadas."
else
  bold "3/6 · Secretos intactos (SKIP_SECRETS=1)"
  WEBHOOK_USER="${CULQI_WEBHOOK_USER:-<el que ya tengas>}"
  WEBHOOK_PASS="${CULQI_WEBHOOK_PASS:-<el que ya tengas>}"
fi

# ---------------------------------------------------------------------------
# 4-5. Build y deploy
# ---------------------------------------------------------------------------
bold "4/6 · Compilando"
npm run build

bold "5/6 · Desplegando functions, hosting y reglas de Firestore"
npx firebase deploy --project "$PROJECT_ID"

# ---------------------------------------------------------------------------
# 6. Verificación
# ---------------------------------------------------------------------------
bold "6/6 · Verificando"
HEALTH_URL="${SITE_URL}/api/payments/health"
if command -v curl >/dev/null 2>&1; then
  for _ in 1 2 3 4 5; do
    HEALTH="$(curl -fsS --max-time 15 "$HEALTH_URL" 2>/dev/null || true)"
    [ -n "$HEALTH" ] && break
    sleep 4
  done

  if [ -n "${HEALTH:-}" ]; then
    info "Salud del backend: $HEALTH"
    case "$HEALTH" in
      *'"mode":"sin_configurar"'*) printf '\n⚠  El secreto CULQI_SECRET_KEY no está llegando.\n' ;;
      *'"mode":"live"'*) info "Modo producción (live)." ;;
      *'"mode":"test"'*) printf '\n⚠  Estás en modo de prueba (llaves pk_test_/sk_test_).\n' ;;
    esac
  else
    printf '⚠  No se pudo consultar %s (los rewrites pueden tardar un minuto).\n' "$HEALTH_URL"
  fi
fi

cat <<EOF

──────────────────────────────────────────────────────────────
✅ Publicado: ${SITE_URL}
   Backend:  ${HEALTH_URL}

Falta un paso manual en el panel de Culqi:
   CulqiPanel → Eventos → Webhooks → + Añadir
   · Evento:        charge.creation.succeeded  y  order.status.changed
   · URL:           ${SITE_URL}/api/payments/webhooks/culqi
   · Autenticación: Activada
   · Usuario:       ${WEBHOOK_USER}
   · Contraseña:    ${WEBHOOK_PASS}

Prueba una compra real de S/ 1 y revisa el pedido en
Firestore → orders. Si algo falla:  npx firebase functions:log
──────────────────────────────────────────────────────────────
EOF
