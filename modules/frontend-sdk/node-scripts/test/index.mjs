/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs/promises';
import path from 'path';

import getNamedArguments from '../util/getNamedArguments.mjs';
import getYarnWorkspaceProjects from '../util/getYarnWorkspaceProjects.mjs';
import runJest from '../util/jest/runJest.mjs';
import {PORTAL_DIR} from '../util/locations.mjs';
import print from '../util/print.mjs';
import runConcurrentTasks from '../util/runConcurrentTasks.mjs';

const SYNC_FLAG = '--sync';

export default async function () {
	const {sync} = getNamedArguments({
		sync: SYNC_FLAG,
	});

	const originalNodeEnv = process.env.NODE_ENV;

	process.env.NODE_ENV = 'test';

	// Every remaining argument is forwarded to jest, which rejects our own flags

	const args = process.argv.slice(3).filter((arg) => arg !== SYNC_FLAG);

	/**
	 * When using 'yarn run ...' it sets the cwd to the nearest package.json
	 */
	let cwd = process.env.INIT_CWD;

	if (!cwd) {
		cwd = process.cwd();
	}

	const projects = await getYarnWorkspaceProjects();

	/**
	 * Map containing the path to the project and the environment variables
	 * to be used when running the tests.
	 */
	const testableProjectsMap = new Map();

	/**
	 * Filter out projects that do not have `node-scripts test`
	 */
	for (const projectPath of projects) {

		// Check if deeply nested passed a project root or check if shallowly
		// nested before several project roots

		if (
			cwd.includes(projectPath) ||
			projectPath.includes(process.env.INIT_CWD)
		) {
			const packageJson = path.join(projectPath, 'package.json');
			const pkgJsonContents = await fs.readFile(packageJson, 'utf8');

			if (pkgJsonContents.includes('node-scripts test')) {
				const pkgJson = JSON.parse(pkgJsonContents);

				testableProjectsMap.set(
					projectPath,
					getEnvVars(pkgJson.scripts.test)
				);
			}
		}
	}

	const totalTestableProjects = testableProjectsMap.size;

	let failed = false;

	if (totalTestableProjects === 1) {
		const [[projectPath, envObj]] = testableProjectsMap.entries();

		({failed} = await runJest({
			cliFlags: args,
			execaConfig: {
				env: envObj,
				stdio: 'inherit',
			},
			projectPath,
		}));
	}
	else {
		print(
			0,
			print.title(
				`\n> Testing ${totalTestableProjects} projects in ${sync ? 'series' : 'parallel'}...\n`
			)
		);

		const asyncItems = [];

		for (const [projectPath, envObj] of testableProjectsMap) {
			asyncItems.push(async () => {
				const projectName = path.relative(PORTAL_DIR, projectPath);

				const {all, failed: projectFailed} = await runJest({
					cliFlags: args,
					execaConfig: {
						all: true,
						env: envObj,
						reject: false,
						stdio: 'pipe',
					},
					projectPath,
				});

				if (projectFailed) {
					print(
						1,
						print.error('FAILED:'),
						print.underline(projectName),
						'\n'
					);
					print(2, `${all}\n`);
				}
				else {
					print(
						1,
						print.success('PASSED:'),
						print.underline(projectName),
						'\n'
					);
				}

				return projectFailed;
			});
		}

		if (sync) {
			for (const task of asyncItems) {
				if (await task()) {
					failed = true;
				}
			}
		}
		else {
			const results = await runConcurrentTasks(asyncItems);

			failed = results.some(Boolean);
		}

		if (failed) {
			print(
				0,
				print.error('ERROR:'),
				'Some projects have failing tests.\n'
			);
		}
	}

	process.env.NODE_ENV = originalNodeEnv;

	process.exitCode = failed ? 1 : 0;
}

function getEnvVars(value) {
	return value
		.split(' ')
		.filter((part) => part.includes('='))
		.reduce((acc, part) => {
			const [key, value] = part.split('=');
			acc[key] = value;

			return acc;
		}, {});
}
