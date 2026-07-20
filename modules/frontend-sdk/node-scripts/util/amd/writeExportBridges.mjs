/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs/promises';
import path from 'path';

import getFlatName from '../getFlatName.mjs';
import {BUILD_RESOURCES_PATH} from '../locations.mjs';
import projectScopeRequire from '../projectScopeRequire.mjs';
import getNamespacedPackageName from './util/getNamespacedPackageName.mjs';
import hashPathForVariable from './util/hashPathForVariable.mjs';
import splitProjectExport from './util/splitProjectExport.mjs';

export default async function writeExportBridges(
	projectDescription,
	projectExports,
	projectWebContextPath
) {
	await Promise.all([
		writePackageJsons(projectDescription, projectExports),
		...projectExports.map((projectExport) =>
			writeExportBridge(
				projectDescription,
				projectExport,
				projectWebContextPath
			)
		),
	]);
}

async function writePackageJsons(projectDescription, projectExports) {

	// Sometimes we export more than one path for a single package but we only need to write one
	// package.json for those.

	const uniquePackageNames = projectExports
		.map(splitProjectExport)
		.reduce((uniquePackageNames, {name, scope}) => {
			const packageName = `${scope ? `${scope}/` : ''}${name}`;

			uniquePackageNames[packageName] = true;

			return uniquePackageNames;
		}, {});

	for (const packageName of Object.keys(uniquePackageNames)) {
		const {version} = projectScopeRequire(`${packageName}/package.json`);
		const namespacedPackageName = getNamespacedPackageName(
			packageName,
			projectDescription.name
		);
		const namespacedPackageId = `${namespacedPackageName}@${version}`;

		const json = {
			dependencies: {},
			main: './index.js',
			name: namespacedPackageName,
			version,
		};

		const filePath = path.join(
			BUILD_RESOURCES_PATH,
			'node_modules',
			namespacedPackageId.replaceAll('/', '%2F'),
			'package.json'
		);

		await fs.mkdir(path.dirname(filePath), {recursive: true});
		await fs.writeFile(filePath, JSON.stringify(json, null, '\t'), 'utf-8');
	}
}

async function writeExportBridge(
	projectDescription,
	projectExport,
	projectWebContextPath
) {
	const {modulePath, name, scope} = splitProjectExport(projectExport);
	const {version} = projectScopeRequire(
		`${scope ? `${scope}/` : ''}${name}/package.json`
	);
	const namespacedPackageName = getNamespacedPackageName(
		projectExport,
		projectDescription.name
	);
	const namespacedPackageId = `${namespacedPackageName}@${version}`;

	// Compose bridge code

	const importPath =
		getPathToRoot(projectExport, modulePath) +
		projectWebContextPath +
		'/__liferay__/exports/' +
		getFlatName(projectExport, 'js');

	const hashedImportPath = hashPathForVariable(importPath);

	const code = `
import * as ${hashedImportPath} from "${importPath}";

Liferay.Loader.define(
	"${namespacedPackageId}${modulePath}",
	['module'],
	function (module) {
		module.exports = {
			...${hashedImportPath},
			__esModule: true,
			default:
				${hashedImportPath}.default !== undefined
					? ${hashedImportPath}.default
					: ${hashedImportPath},
		};
	}
);
`;

	// Write file

	const filePath = path.join(
		BUILD_RESOURCES_PATH,
		'node_modules',
		namespacedPackageId.replaceAll('/', '%2F'),
		`${modulePath}.js`
	);

	await fs.mkdir(path.dirname(filePath), {recursive: true});
	await fs.writeFile(filePath, code, 'utf-8');
}

function getPathToRoot(projectExport, modulePath) {

	//
	// Compute the relative position of the bridge related to the real ES
	// module.
	//
	// Note that AMD modules are server under `/o/js/resolved-module/...`
	// whereas ESM are under `/o/my-context-path/__liferay__/exports/...`.
	//
	// Also, depending for npm-scoped scoped packages, and additional folder
	// level appears under `/o/js/resolved-module/...`.
	//
	// Also, for internal module paths, we must add more `../`s.
	//

	let pathToRoot = '../..';

	if (projectExport.startsWith('@')) {
		pathToRoot += '/..';
	}

	const hopsToAdd = modulePath.split('/').length - 1;

	for (let i = 0; i < hopsToAdd; i++) {
		pathToRoot += '/..';
	}

	return pathToRoot;
}
