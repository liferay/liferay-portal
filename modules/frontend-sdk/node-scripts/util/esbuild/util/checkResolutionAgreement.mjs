/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs/promises';
import module from 'module';
import path from 'path';
import resolve from 'resolve';

import print from '../../print.mjs';

const require = module.createRequire(import.meta.url);

/**
 * Fail the build when the symbols declared by an export bridge were inferred
 * from a different file than the one esbuild ends up bundling.
 *
 * getExportedSymbols() resolves through Node, which ignores the `browser` field
 * of package.json, knows nothing about esbuild's `alias` and does not see the
 * linker plugin's onResolve hooks, so the two can pick different files for the
 * same specifier. `qrcode` is the canonical case: Node takes `lib/index.js`
 * and its seven server side symbols, esbuild takes `lib/browser.js` and its
 * four.
 *
 * Only CommonJS targets are checked. When esbuild resolves the specifier to a
 * real ES module it link checks the bridge's imports itself, so a symbol the
 * target does not have is already a build error rather than a silent
 * `undefined`. That distinction matters in practice: of the exported packages
 * in this repository that resolve differently under Node and esbuild, the large
 * majority resolve to an ES module build of the same package and are therefore
 * already covered.
 *
 * Divergence alone is not reported either, because a package may ship separate
 * CommonJS builds that expose exactly the same symbols. Only a real difference
 * in the symbol set fails the build.
 *
 * Paths are compared after resolving symlinks, since many packages under
 * node_modules are symlinks into the repository and esbuild reports the real
 * path where Node's resolver keeps the symlinked one.
 */
export default async function checkResolutionAgreement(
	overridenPackageSymbols,
	moduleName,
	metafile,
	projectDir = '.'
) {

	// An override states the symbol set explicitly, so nothing was inferred
	// from a resolved file and there is nothing to disagree about.

	if (overridenPackageSymbols[moduleName]) {
		return;
	}

	const targetPaths = new Set();

	for (const input of Object.values(metafile.inputs)) {
		for (const {original, path: importedPath} of input.imports || []) {
			if (original === moduleName) {
				targetPaths.add(importedPath);
			}
		}
	}

	if (!targetPaths.size) {
		return;
	}

	let nodePath;

	try {
		nodePath = await getRealPath(
			resolve.sync(moduleName, {basedir: projectDir})
		);
	}
	catch (error) {
		throw new Error(
			`Unable to check ${moduleName}: esbuild bundles it but Node cannot ` +
				`resolve it (${error.message}), so its exported symbols cannot ` +
				`be inferred. Add a symbols override for ${moduleName} in ` +
				`node-scripts.config.js.`
		);
	}

	for (const targetPath of targetPaths) {
		if (metafile.inputs[targetPath]?.format !== 'cjs') {
			continue;
		}

		const esbuildPath = await getRealPath(
			path.resolve(projectDir, targetPath)
		);

		if (esbuildPath === nodePath) {
			continue;
		}

		const esbuildSymbols = loadSymbolNames(esbuildPath);
		const nodeSymbols = loadSymbolNames(nodePath);

		if (!nodeSymbols || !esbuildSymbols) {
			print(
				0,
				print.warning('WARNING:'),
				`Unable to compare the exported symbols of ${moduleName}: Node ` +
					`resolves it to ${nodePath} but esbuild bundles ` +
					`${esbuildPath}, and at least one of the two could not be ` +
					`loaded for inspection.`
			);

			continue;
		}

		const missing = esbuildSymbols.filter(
			(symbol) => !nodeSymbols.includes(symbol)
		);
		const extra = nodeSymbols.filter(
			(symbol) => !esbuildSymbols.includes(symbol)
		);

		if (!missing.length && !extra.length) {
			continue;
		}

		throw new Error(
			`Symbol mismatch for ${moduleName}:

Its exported symbols were inferred from	${nodePath}
but esbuild bundles ${esbuildPath}.

Declared but absent from the bundled file:
	${formatSymbols(extra)}

Present in the bundled file but not declared:
	${formatSymbols(missing)}

Both files are CommonJS, so esbuild cannot catch this and the mismatched symbols
would silently be undefined at runtime. Either make both resolve alike or add a
symbols override for ${moduleName} in node-scripts.config.js.

Note that only exported packages are checked: import bridges in consumer projects
link to a URL, so esbuild never resolves the package there.`
		);
	}
}

function formatSymbols(symbols) {
	return symbols.length ? symbols.join('\n	') : '(none)';
}

async function getRealPath(filePath) {
	try {
		return await fs.realpath(filePath);
	}
	catch (_error) {
		return filePath;
	}
}

function loadSymbolNames(filePath) {
	try {
		return Object.keys(require(filePath));
	}
	catch (_error) {
		return null;
	}
}
