#!/usr/bin/env node
/**
 * Genera una copia publicable del sitio en `docs/` para GitHub Pages.
 *
 * Se usa como espejo temporal mientras el deploy oficial (Firebase Hosting)
 * no se puede ejecutar. GitHub Pages sirve la carpeta `docs/` de la rama
 * configurada, y el sitio queda en https://<usuario>.github.io/<repo>/
 * por lo que el `base` de Vite debe incluir el nombre del repositorio.
 *
 * Uso: npm run build:pages
 */
import { cp, mkdir, readdir, rm, writeFile } from 'node:fs/promises';
import { existsSync } from 'node:fs';
import { dirname, join, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import { build } from 'vite';

const root = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const docsDir = join(root, 'docs');
const distDir = join(root, 'dist');
const base = '/fernandinadejagua/';

// 1. Build con el base correcto para un project page.
await build({
  root,
  base,
  build: { outDir: 'dist', emptyOutDir: true },
  logLevel: 'warn',
});

// 2. Limpia solo artefactos de builds anteriores dentro de docs/.
for (const entry of ['index.html', 'favicon.svg', 'assets']) {
  await rm(join(docsDir, entry), { recursive: true, force: true });
}

// 3. Copia el build.
await mkdir(docsDir, { recursive: true });
for (const entry of await readdir(distDir)) {
  await cp(join(distDir, entry), join(docsDir, entry), { recursive: true });
}

// 4. Evita que Jekyll procese los archivos (nombres con hash y guiones).
await writeFile(join(docsDir, '.nojekyll'), '');

if (!existsSync(join(docsDir, 'index.html'))) {
  throw new Error('No se generó docs/index.html');
}

console.log(`✔ Sitio copiado a docs/ con base ${base}`);
