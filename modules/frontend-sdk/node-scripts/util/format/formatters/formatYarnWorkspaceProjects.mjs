/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import childProcess from 'child_process';

import {MODULES_DIR, YARN_SCRIPT_FILE} from '../../locations.mjs';
import print from '../../print.mjs';
import EXPECTED_YARN_WORKSPACE_PROJECTS from './EXPECTED_YARN_WORKSPACE_PROJECTS.mjs';

function getCurrentProjects() {

	// The "yarn" binary cannot be invoked directly, so run the bundled
	// "yarn-<version>.js" with the current node executable instead.

	const stdout = childProcess.execFileSync(
		process.execPath,
		[YARN_SCRIPT_FILE, 'workspaces', 'info'],
		{cwd: MODULES_DIR, encoding: 'utf-8'}
	);

	// "yarn workspaces info" prints a header line, the JSON object, and a
	// "Done in ..." footer; keep only the top-level JSON object.

	const lines = stdout.split('\n');

	return Object.keys(
		JSON.parse(
			lines
				.slice(
					lines.findIndex((line) => line.trim() === '{'),
					lines.lastIndexOf('}') + 1
				)
				.join('\n')
		)
	);
}

export default async function formatYarnWorkspaceProjects() {
	let checksPassed = true;

	print(
		1,
		print.subTitle(
			`> Checking 'modules/package.json' workspaces match the recorded snapshot...\n`
		)
	);

	const currentProjects = getCurrentProjects();

	const expectedProjects = new Set(EXPECTED_YARN_WORKSPACE_PROJECTS);
	const currentProjectsSet = new Set(currentProjects);

	const added = currentProjects
		.filter((name) => !expectedProjects.has(name))
		.sort();
	const removed = EXPECTED_YARN_WORKSPACE_PROJECTS.filter(
		(name) => !currentProjectsSet.has(name)
	).sort();

	if (added.length || removed.length) {
		print(
			2,
			print.error('ERROR:'),
			"The yarn workspace projects changed; update 'EXPECTED_YARN_WORKSPACE_PROJECTS.mjs' after reviewing:"
		);

		for (const name of added) {
			print(3, `+ ${name}`);
		}

		for (const name of removed) {
			print(3, `- ${name}`);
		}

		print(2, '');

		checksPassed = false;
	}

	return checksPassed;
}
