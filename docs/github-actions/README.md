# Despliegue automático con GitHub Actions

Estas dos definiciones están aquí como **plantillas** porque el token de la sesión que las creó
no tenía permiso para escribir en `.github/workflows/`. Para activarlas:

```bash
mkdir -p .github/workflows
cp docs/github-actions/*.yml .github/workflows/
git add .github/workflows && git commit -m "Activar despliegue de Firebase Hosting con GitHub Actions"
git push
```

O deja que la CLI lo haga todo por ti (crea el workflow y el secreto):

```bash
npx firebase init hosting:github
```

| Plantilla | Disparador | Resultado |
|---|---|---|
| `firebase-hosting-merge.yml` | push a `main` / manual | despliegue a producción |
| `firebase-hosting-pull-request.yml` | pull request a `main` | canal de preview (7 días) + comentario en el PR |

Requisito: el secreto `FIREBASE_SERVICE_ACCOUNT_FERNANDINA_DE_JAGUA_WEB` con el JSON de una
cuenta de servicio con rol **Firebase Hosting Admin**. Mientras no exista, los workflows
compilan el sitio y se saltan el paso de despliegue mostrando un aviso.
