/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export async function loadModule(importDeclaration: string): Promise<any> {
	const [moduleName, symbolName] = getModuleAndSymbolNames(importDeclaration);

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
