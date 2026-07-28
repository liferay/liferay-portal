/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import crypto from 'crypto';

import getProjectDirs from './getProjectDirs.mjs';
import objectSF from './objectSF.mjs';
import projectScopeRequire from './projectScopeRequire.mjs';

export default async function createGlobalConfig() {
	const projectDirs = await getProjectDirs();

	let allDependencies = {};
	let allSymbols = {};
	const allImports = {};

	for (const projectDir of projectDirs) {
		const {
			exports = [],
			submodules,
			symbols,
		} = projectScopeRequire('./node-scripts.config.js', projectDir);

		const {dependencies, name} = projectScopeRequire(
			'./package.json',
			projectDir
		);

		for (const symbolPackage in symbols) {
			if (!exports.includes(symbolPackage)) {
				throw Error(
					`File 'node-scripts.config.js' is invalid. Only declare symbols for packages your module 'exports'. Reading '${symbolPackage}'.`
				);
			}
		}

		allDependencies = {...allDependencies, ...dependencies};
		allImports[name] = exports;

		if (submodules) {
			allImports[name].push(
				...Object.keys(submodules).map((item) => `./${item}`)
			);
		}

		if (symbols) {
			allSymbols = {...allSymbols, ...symbols};
		}
	}

	const sha256 = crypto.createHash('sha256');

	const hash = sha256
		.update(JSON.stringify(allImports) + JSON.stringify(allSymbols))
		.digest('hex');

	return {
		config: template(allImports, allSymbols, hash),
		hash,
	};
}

function template(imports, symbols, hash) {
	return `/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/** AUTO-GENERATED: DO NOT EDIT 
 * 
 * This is generated based on each module's \`node-scripts.config.js\`
 * 
*/

module.exports = {
	hash: '${hash}',
	imports: ${objectSF(imports)},
	
	// The following symbols override the actual symbols that listed packages export. This is
	// sometimes necessary when:
	//
	// 1. The build cannot infer the exported symbols because the package cannot be required from
	//    Node.js (eg: if it references \`window\` or any other browser API not available).
	// 2. The inferred exported symbols are wrong (eg: polymorphic packages).
	// 3. The package needs a \`default\` export that the build cannot infer, or the entry replaces an
	//    inferred set that already contained one. Overrides are exhaustive, so \`default\` must be
	//    listed explicitly whenever the package should have it.
	//
	// For number 3 note that CJS packages not tagged with \`__esModule\` don't need an entry just to
	// get a \`default\` export: the build infers one for them pointing to the exported object, which
	// is what tools like \`babel\` and \`webpack\` do, so that they can be directly imported using ES
	// syntax (eg: \`import React from 'react';\`).
	//
	// Also note that listing \`__esModule\` here has no effect, since the generated bridges always
	// drop that symbol.
	//
	// The way to obtain these symbols is different for each package but it usually starts with a
	// runtime error in the browser and a following investigation on what the package is really
	// exporting when used from a browser.
	//

	symbols: ${objectSF(symbols)},
};`;
}
