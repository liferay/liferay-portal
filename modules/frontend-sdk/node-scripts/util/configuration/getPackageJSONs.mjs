/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs';
import path from 'path';

import getYarnWorkspaceProjects from '../getYarnWorkspaceProjects.mjs';
import {MODULES_DIR, PORTAL_DIR} from '../locations.mjs';

/**
 * Returns the parsed "package.json" of every yarn workspace project plus the
 * global "modules/package.json" (if requested), each paired with its directory
 * and the paths (relative to the portal root).
 *
 * The result is parsed once and cached so the checks that share it do not
 * re-read and re-parse every "package.json".
 */
export default async function getPackageJSONs(includeGlobal = false) {
	const dirs = await getYarnWorkspaceProjects();

	if (includeGlobal) {
		dirs.push(MODULES_DIR);
	}

	const projects = [];

	for (const dir of dirs) {
		const pkgPath = path.join(dir, 'package.json');

		let pkg;

		try {
			pkg = JSON.parse(fs.readFileSync(pkgPath, 'utf-8'));
		}
		catch (error) {
			continue;
		}

		projects.push({
			dir,
			pkg,
			projectRelPath: path.relative(PORTAL_DIR, dir),
			relPath: path.relative(PORTAL_DIR, pkgPath),
		});
	}

	return projects;
}
