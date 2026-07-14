/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs/promises';
import path from 'path';

import {MODULES_DIR} from './locations.mjs';

const IGNORED_PROJECT_DIRS = ['modules'];

export const NO_RECURSE_PROJECT_DIRS = [
	'clay',
	'frontend-sdk',
	'build',
	'classes',
	'node_modules',
	'sdk',
	'test',
	'vercel',
];

let cachedProjectDirs;

export default async function getProjectDirs(dir = undefined) {
	if (dir === undefined) {
		if (!cachedProjectDirs) {
			cachedProjectDirs = await getProjectDirs(MODULES_DIR);
		}

		return cachedProjectDirs;
	}

	const projectDirs = [];

	for (const dirent of await fs.readdir(dir, {withFileTypes: true})) {
		if (
			dirent.name === 'package.json' &&
			!IGNORED_PROJECT_DIRS.includes(path.basename(dir))
		) {
			projectDirs.push(path.resolve(dir));
			break;
		}
		else if (NO_RECURSE_PROJECT_DIRS.includes(dirent.name)) {
			continue;
		}
		else if (dirent.isDirectory()) {
			for (const childDir of await getProjectDirs(
				path.resolve(dir, dirent.name)
			)) {
				projectDirs.push(childDir);
			}
		}
	}

	return projectDirs;
}
