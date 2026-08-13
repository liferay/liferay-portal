/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import path from 'path';

import fileExists from './fileExists.mjs';
import getYarnWorkspaceProjects from './getYarnWorkspaceProjects.mjs';

let cachedBuildableProjectDirs;

/**
 * Returns the directories of the yarn workspace projects that node-scripts
 * builds, which are the ones declaring a "node-scripts.config.js".
 *
 * Note that this is not the same as "every project using node-scripts": the
 * "frontend-sdk" packages and the Playwright project run "node-scripts format"
 * without declaring that file, and both "format" and "test" work fine without
 * it. Only "build" requires it, because it reads the project alias, entry
 * points and exports from there.
 */
export default async function getBuildableProjectDirs() {
	if (!cachedBuildableProjectDirs) {
		const projectDirs = await getYarnWorkspaceProjects();

		const buildable = await Promise.all(
			projectDirs.map((projectDir) =>
				fileExists(path.join(projectDir, 'node-scripts.config.js'))
			)
		);

		cachedBuildableProjectDirs = projectDirs.filter(
			(_, index) => buildable[index]
		);
	}

	return cachedBuildableProjectDirs;
}
