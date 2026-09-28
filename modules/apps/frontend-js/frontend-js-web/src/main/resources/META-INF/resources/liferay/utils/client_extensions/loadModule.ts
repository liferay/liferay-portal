/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export async function loadModule(
	importDeclarationOrAMDModule: string
): Promise<any> {
	if (!importDeclarationOrAMDModule.includes(' from ')) {
		return loadAMDModule(importDeclarationOrAMDModule);
	}

	const [moduleName, symbolName] = getModuleAndSymbolNames(
		importDeclarationOrAMDModule
	);

	// @ts-ignore

	const module = await import(moduleName);

	return module[symbolName];
}

function getModuleAndSymbolNames(importDeclaration: string): [string, string] {
	const parts = importDeclaration.split(' from ');

	const moduleName = parts[1]?.trim();
	let symbolName = parts[0]?.trim();

	if (symbolName.startsWith('{') && symbolName.endsWith('}')) {
		symbolName = symbolName.substring(1, symbolName.length - 1).trim();
	}

	return [moduleName, symbolName];
}

function loadAMDModule(moduleName: string): Promise<any> {

	// @ts-ignore

	const Loader = Liferay.Loader;

	if (!Loader) {
		return Promise.reject(
			new Error(
				`Unable to load AMD module "${moduleName}" because the AMD loader is disabled`
			)
		);
	}

	return new Promise((resolve, reject) => {
		Loader.require(
			moduleName,
			(module: any) => resolve(module.default || module),
			reject
		);
	});
}
