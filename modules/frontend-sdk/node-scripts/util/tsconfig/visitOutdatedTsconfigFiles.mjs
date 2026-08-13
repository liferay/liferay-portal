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
import {MODULES_DIR, SRC_PATH} from '../locations.mjs';
import visitProjectTsconfig from './visitProjectTsconfig.mjs';

export default async function visitOutdatedTsconfigFiles(visitorFunction) {
	const projectDirs = await getBuildableProjectDirs();

	const projectsEntryPoints = await getProjectsEntryPoints(
		projectDirs,
		MODULES_DIR
	);

	await Promise.all([
		...projectDirs.map((projectDir) =>
			processProject(projectDir, projectsEntryPoints, visitorFunction)
		),
	]);
}

/**
 * @returns
 * {
 *	 '@liferay/frontend-js-react-web': {
 *		dir: 'modules/apps/frontend-js/frontend-js-react-web',
 *		path: {
 *			main: 'src/main/resources/META-INF/resources/js/index.ts',
 *			submodules: {
 *				foo: 'src/main/resources/META-INF/resources/js/foo.ts',
 *			}
 *		}
 *	 },
 *	 ...
 * }
 */
async function getProjectsEntryPoints(projectDirs, modulesDir) {
	return projectDirs.reduce((projectsEntryPoints, projectDir) => {
		const {name} = getProjectDescription(projectDir);
		const {typescript} = getProjectEntryPoints(projectDir);

		projectsEntryPoints[name] = {
			dir: path.relative(modulesDir, projectDir),
			path: typescript,
		};

		return projectsEntryPoints;
	}, {});
}

async function processProject(
	projectDir,
	projectsEntryPoints,
	visitorFunction
) {
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
			projectsEntryPoints,
			projectDependencies,
			projectDescription,
			projectDir
		),
		visitProjectTsconfig(
			visitorFunction,
			projectsEntryPoints,
			projectDependencies,
			projectDescription,
			projectDir,
			true
		),
	]);
}
