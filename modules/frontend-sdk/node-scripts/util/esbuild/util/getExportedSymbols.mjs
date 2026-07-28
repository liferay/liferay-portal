/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Parser} from 'acorn';
import tsPlugin from 'acorn-typescript';
import estraverse from 'estraverse';
import fs from 'fs/promises';
import resolve from 'resolve';

import projectScopeRequire from '../../projectScopeRequire.mjs';

export default async function getExportedSymbols(
	overridenPackageSymbols,
	moduleName
) {
	let symbols;

	try {
		if (overridenPackageSymbols[moduleName]) {
			symbols = {};

			overridenPackageSymbols[moduleName].forEach((symbol) => {
				symbols[symbol] = true;
			});

			if (symbols['*']) {
				delete symbols['*'];

				const loadedSymbols = await loadSymbols(moduleName);

				Object.keys(loadedSymbols).forEach((symbol) => {
					symbols[symbol] = true;
				});
			}
		}
		else {
			symbols = await loadSymbols(moduleName);

			// A CommonJS module has no `default` symbol of its own, so mimic
			// what Babel and webpack do and make `module.exports` the default
			// export. Modules tagged with `__esModule` are left alone because
			// they already export `default` when they have one.

			if (!symbols.__esModule) {
				symbols.default = true;
			}
		}
	}
	catch (error) {
		throw new Error(
			`Cannot infer exported symbols for ${moduleName}: ${error}`
		);
	}

	return symbols;
}

async function loadSymbols(moduleName) {
	let module;

	try {
		module = projectScopeRequire(moduleName);
	}
	catch (_error) {
		module = await parseESMExports(moduleName);
	}

	const symbols = Object.keys(module).reduce((symbols, key) => {
		symbols[key] = true;

		return symbols;
	}, {});

	// Some modules config __esModule as non-enumerable, so we explicitly check
	// for it.
	//
	// Node.js 20.19 and above can require() native ES modules, returning a
	// module namespace object that carries no __esModule symbol. We detect
	// those through their @@toStringTag so that they are treated the same way
	// no matter which Node.js version runs the build.

	if (module.__esModule || module[Symbol.toStringTag] === 'Module') {
		symbols.__esModule = true;
	}

	return symbols;
}

async function parseESMExports(moduleName, projectDir = '.') {
	const modulePath = resolve.sync(moduleName, {basedir: projectDir});

	const ast = Parser.extend(tsPlugin()).parse(
		await fs.readFile(modulePath, 'utf-8'),
		{
			ecmaVersion: 2022,
			sourceType: 'module',
		}
	);

	const symbols = {};

	estraverse.traverse(ast, {
		enter: (node) => {
			switch (node.type) {
				case 'ExportAllDeclaration':
					throw new Error('Cannot infer symbols if export * is used');

				case 'ExportDefaultDeclaration':
					symbols['default'] = true;
					break;

				case 'ExportNamedDeclaration':
					if (node.exportKind !== 'type') {
						for (const specifier of node.specifiers) {
							symbols[specifier.exported.name] = true;
						}
					}
					break;

				default:
					break;
			}
		},

		fallback: 'iteration',
	});

	symbols.__esModule = true;

	return symbols;
}
