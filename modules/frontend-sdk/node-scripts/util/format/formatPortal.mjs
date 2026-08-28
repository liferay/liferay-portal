/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getPackageJSONs from '../configuration/getPackageJSONs.mjs';
import getBuildableProjectDirs from '../getBuildableProjectDirs.mjs';
import getTypeScriptProjectDirs from '../getTypeScriptProjectDirs.mjs';
import {PLAYWRIGHT_DIR} from '../locations.mjs';
import formatAPISubmodules from './formatters/formatAPISubmodules.mjs';
import formatConfigFileNames from './formatters/formatConfigFileNames.mjs';
import formatGlobalNodeScriptsConfig from './formatters/formatGlobalNodeScriptsConfig.mjs';
import formatGlobalPackageJSON from './formatters/formatGlobalPackageJSON.mjs';
import formatGlobalPackageJSONDependencies from './formatters/formatGlobalPackageJSONDependencies.mjs';
import formatGlobalPackageJSONResolutions from './formatters/formatGlobalPackageJSONResolutions.mjs';
import formatIgnoreFilePatterns from './formatters/formatIgnoreFilePatterns.mjs';
import formatNodeScriptsHash from './formatters/formatNodeScriptsHash.mjs';
import formatPackageJSONExplicitVersions from './formatters/formatPackageJSONExplicitVersions.mjs';
import formatPackageJSONFiles from './formatters/formatPackageJSONFiles.mjs';
import formatPackageJSONForbidAlloyUI from './formatters/formatPackageJSONForbidAlloyUI.mjs';
import formatPackageJSONForbidNestedWorkspaces from './formatters/formatPackageJSONForbidNestedWorkspaces.mjs';
import formatPackageJSONTypesLocation from './formatters/formatPackageJSONTypesLocation.mjs';
import formatPackageJSONVersionAlignment from './formatters/formatPackageJSONVersionAlignment.mjs';
import formatSourceFiles from './formatters/formatSourceFiles.mjs';
import formatTsconfigFiles from './formatters/formatTsconfigFiles.mjs';
import formatTypeScript from './formatters/formatTypeScript.mjs';
import formatYarnLock from './formatters/formatYarnLock.mjs';
import formatYarnWorkspaceProjects from './formatters/formatYarnWorkspaceProjects.mjs';

export default async function formatPortal(check, files) {
	let checksPassed = true;

	if (!(await formatConfigFileNames())) {
		checksPassed = false;
	}

	if (!(await formatIgnoreFilePatterns())) {
		checksPassed = false;
	}

	if (
		(!files ||
			!!files.find((file) => file.endsWith('/node-scripts.config.js'))) &&
		!(await formatGlobalNodeScriptsConfig(check))
	) {
		checksPassed = false;
	}

	if (
		(!files || !!files.find((file) => file.endsWith('/package.json'))) &&
		!(await formatTsconfigFiles(check))
	) {
		checksPassed = false;
	}

	if (!(await formatSourceFiles(check, files))) {
		checksPassed = false;
	}

	if (
		(!files || !!files.find((file) => file.includes('/node-scripts/'))) &&
		!(await formatNodeScriptsHash(check))
	) {
		checksPassed = false;
	}

	if (
		(!files ||
			!!files.find((file) => file.endsWith('/node-scripts.config.js'))) &&
		!(await formatAPISubmodules())
	) {
		checksPassed = false;
	}

	if (
		!files ||
		!!files.find((file) => file.endsWith('/package.json')) ||
		!!files.find((file) => file.endsWith('/node-scripts.config.js'))
	) {
		const packageJSONs = await getPackageJSONs(true);

		if (!(await formatGlobalPackageJSON(packageJSONs))) {
			checksPassed = false;
		}

		if (!(await formatGlobalPackageJSONDependencies(packageJSONs))) {
			checksPassed = false;
		}

		if (!(await formatGlobalPackageJSONResolutions(packageJSONs))) {
			checksPassed = false;
		}

		if (!(await formatYarnWorkspaceProjects())) {
			checksPassed = false;
		}

		if (!(await formatPackageJSONForbidNestedWorkspaces(packageJSONs))) {
			checksPassed = false;
		}

		if (!(await formatPackageJSONTypesLocation(packageJSONs))) {
			checksPassed = false;
		}

		if (!(await formatPackageJSONForbidAlloyUI(packageJSONs))) {
			checksPassed = false;
		}

		if (!(await formatPackageJSONExplicitVersions(packageJSONs))) {
			checksPassed = false;
		}

		if (!(await formatPackageJSONVersionAlignment(packageJSONs))) {
			checksPassed = false;
		}

		if (!(await formatPackageJSONFiles())) {
			checksPassed = false;
		}
	}

	if (
		(!files || !!files.find((file) => file.endsWith('/yarn.lock'))) &&
		!(await formatYarnLock())
	) {
		checksPassed = false;
	}

	if (
		!files ||
		!!files.find(
			(file) =>
				file.endsWith('/node-scripts.config.js') ||
				file.endsWith('/package.json') ||
				file.endsWith('.ts') ||
				file.endsWith('.tsx')
		)
	) {
		let projectDirs;

		if (files) {
			projectDirs = await getTypeScriptProjectDirs(files);
		}
		else {
			projectDirs = await getBuildableProjectDirs();
		}

		// We check all projects no matter if formatting current branch, local
		// changes or everything because a change in one project may break
		// others

		if (!(await formatTypeScript([...projectDirs, PLAYWRIGHT_DIR]))) {
			checksPassed = false;
		}
	}

	return checksPassed;
}
