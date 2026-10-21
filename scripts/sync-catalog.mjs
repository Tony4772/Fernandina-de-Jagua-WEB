#!/usr/bin/env node
/**
 * Genera `functions/catalog.json` a partir de `src/data/products.ts`.
 *
 * El servidor de pagos NUNCA confía en los precios que envía el navegador:
 * recalcula el monto con este catálogo. Por eso el catálogo del backend debe
 * coincidir con el que muestra la web.
 *
 * Ejecuta `npm run catalog:sync` cada vez que cambies precios o productos
 * en `src/data/products.ts` (o desde el CMS del admin, si luego se conecta
 * el CMS a Firestore).
 */
import { build } from 'esbuild';
import { mkdir, readFile, writeFile } from 'node:fs/promises';
import { dirname, resolve } from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';

const root = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const entry = resolve(root, 'src/data/products.ts');
const outFile = resolve(root, 'functions/catalog.json');

async function loadProductModule() {
  const result = await build({
    entryPoints: [entry],
    bundle: true,
    format: 'esm',
    platform: 'node',
    write: false,
  });

  const code = result.outputFiles[0].text;
  const tmpFile = resolve(root, 'node_modules/.cache/fernandina-products.mjs');
  await mkdir(dirname(tmpFile), { recursive: true });
  await writeFile(tmpFile, code, 'utf8');
  return import(`${pathToFileURL(tmpFile).href}?t=${Date.now()}`);
}

const { PRODUCTS } = await loadProductModule();

if (!Array.isArray(PRODUCTS) || PRODUCTS.length === 0) {
  throw new Error('No se encontraron productos en src/data/products.ts');
}

const catalog = {
  generatedFrom: 'src/data/products.ts',
  currency: 'PEN',
  products: PRODUCTS.map((product) => {
    const price = Number(product.priceValue);
    if (!Number.isFinite(price) || price <= 0) {
      throw new Error(`El producto "${product.id}" no tiene un priceValue válido.`);
    }
    return {
      id: product.id,
      name: product.name,
      netWeight: product.netWeight ?? '',
      priceValue: price,
      // Monto en céntimos, tal como lo exige la API de Culqi.
      amountInCents: Math.round(price * 100),
    };
  }),
};

await writeFile(outFile, `${JSON.stringify(catalog, null, 2)}\n`, 'utf8');
console.log(`✔ functions/catalog.json actualizado con ${catalog.products.length} productos.`);
