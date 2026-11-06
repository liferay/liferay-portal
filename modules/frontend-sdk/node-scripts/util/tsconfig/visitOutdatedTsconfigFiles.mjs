/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import path from 'path';

import getProjectDependencies from '../configuration/getProjectDependencies.mjs';
import getProjectDescription from '../configuration/getProjectDescription.mjs';
import getProjectEntryPoints from '../configuration/getProjectEntryPoints.mjs';
import fileExists from '../fileExists.mjs';
import getBuildableProjectDirs from '../getBuildableProjectDirs.mjs';
import {MODULES_DIR, SRC_PATH, TEST_PATH} from '../locations.mjs';
import hasTypeScriptFiles from './hasTypeScriptFiles.mjs';
import visitProjectTsconfig from './visitProjectTsconfig.mjs';

export default async function visitOutdatedTsconfigFiles(visitorFunction) {
	const projectDirs = await getBuildableProjectDirs();

	const projects = await getProjects(MODULES_DIR, projectDirs);

	await Promise.all([
		...projectDirs.map((projectDir) =>
			processProject(projectDir, projects, visitorFunction)
		),
	]);
}

/**
 * @returns
 * {
 *	 '@liferay/frontend-js-react-web': {
 *		dir: 'modules/apps/frontend-js/frontend-js-react-web',
 *		entryPoints: {
 *			main: 'src/main/resources/META-INF/resources/js/index.ts',
 *			submodules: {
 *				foo: 'src/main/resources/META-INF/resources/js/foo.ts',
 *			}
 *		},
 *		typeScriptProject: true,
 *		typeScriptTests: false,
 *	 },
 *	 ...
 * }
 */
async function getProjects(modulesDir, projectDirs) {
	const projects = {};

	await Promise.all(
		projectDirs.map(async (projectDir) => {
			const {name} = getProjectDescription(projectDir);
			const {typescript} = getProjectEntryPoints(projectDir);

			projects[name] = {
				dir: path.relative(modulesDir, projectDir),
				entryPoints: typescript,
				typeScriptProject: await hasTypeScriptFiles(
					path.join(projectDir, SRC_PATH)
				),
				typeScriptTests: await hasTypeScriptFiles(
					path.join(projectDir, TEST_PATH)
				),
			};
		})
	);

	return projects;
}

async function processProject(projectDir, projects, visitorFunction) {
	if (!(await fileExists(path.join(projectDir, SRC_PATH)))) {
		return;
	}

	const [projectDependencies, projectDescription] = await Promise.all([
		getProjectDependencies(projectDir),
		getProjectDescription(projectDir),
	]);

	await Promise.all([
		visitProjectTsconfig(
			visitorFunction,
			projects,
			projectDependencies,
			projectDescription,
			projectDir
		),
		visitProjectTsconfig(
			visitorFunction,
			projects,
			projectDependencies,
			projectDescription,
			projectDir,
			true
		),
	]);
}
