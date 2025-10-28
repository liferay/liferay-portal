/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {parse} from 'acorn';
import estraverse from 'estraverse';
import fs from 'fs/promises';
import resolve from 'resolve';

import projectScopeRequire from './projectScopeRequire.mjs';

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
	catch (error) {
		if (error.code === 'ERR_REQUIRE_ESM') {
			module = await parseESMExports(moduleName);
		}
	}

	const symbols = Object.keys(module).reduce((symbols, key) => {
		symbols[key] = true;

		return symbols;
	}, {});

	// Some modules config __esModule as non-enumerable, so we explicitly check for it

	if (module.__esModule) {
		symbols.__esModule = true;
	}

	return symbols;
}

async function parseESMExports(moduleName, projectDir = '.') {
	const modulePath = resolve.sync(moduleName, {basedir: projectDir});

	const ast = parse(await fs.readFile(modulePath, 'utf-8'), {
		ecmaVersion: 2022,
		sourceType: 'module',
	});

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
					for (const specifier of node.specifiers) {
						symbols[specifier.exported.name] = true;
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
