/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getBuildableProjectDirs from './getBuildableProjectDirs.mjs';
import getFileProjectDir from './getFileProjectDir.mjs';

export default async function getTypeScriptProjectDirs(modifiedFiles) {
	modifiedFiles = modifiedFiles.filter(
		(file) =>
			file.endsWith('.ts') ||
			file.endsWith('.tsx') ||
			file.endsWith('package.json') ||
			file.endsWith('tsconfig.json')
	);
	modifiedFiles = modifiedFiles.filter((file) => file.startsWith('modules/'));
	modifiedFiles = modifiedFiles.map((file) => file.substring(8));
	modifiedFiles = modifiedFiles.filter(
		(file) => file.startsWith('apps/') || file.startsWith('dxp/')
	);

	const buildableProjectDirs = new Set(await getBuildableProjectDirs());

	const projectDirs = new Set();

	for (const file of modifiedFiles) {
		const projectDir = await getFileProjectDir(file);

		if (buildableProjectDirs.has(projectDir)) {
			projectDirs.add(projectDir);
		}
	}

	return [...projectDirs];
}
