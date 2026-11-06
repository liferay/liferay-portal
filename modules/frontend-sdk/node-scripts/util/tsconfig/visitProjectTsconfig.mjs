/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import crypto from 'crypto';
import fs from 'fs/promises';
import path from 'path';

import fileExists from '../fileExists.mjs';
import {
	GLOBAL_D_TS_FILE,
	GLOBAL_NODE_MODULES_TYPES_DIR,
	MODULES_DIR,
	SRC_PATH,
	SRC_TSCONFIG_PATH,
	TEST_PATH,
	TSC_BUILDINFO_DIR,
	TSC_TYPES_DIR,
} from '../locations.mjs';
import objectSF from '../objectSF.mjs';
import baseTsconfig from './baseTsconfig.mjs';

const GENERATED = '@generated';

export default async function visitProjectTsconfig(
	visitorFunction,
	projects,
	projectDependencies,
	projectDescription,
	projectDir = '.',
	testConfig = false
) {
	const srcPath = testConfig
		? path.join(projectDir, TEST_PATH)
		: path.join(projectDir, SRC_PATH);

	const configPath = path.join(srcPath, 'tsconfig.json');

	const currentProject = projects[projectDescription.name];

	const typeScriptFound = testConfig
		? currentProject.typeScriptTests
		: currentProject.typeScriptProject;

	if (!typeScriptFound) {
		if (await fileExists(configPath)) {
			await visitorFunction(configPath, null);
		}

		return;
	}

	const globalDTsFileProjectRelativePath = path.posix.relative(
		srcPath,
		GLOBAL_D_TS_FILE
	);

	const modulesDirProjectRelativePath = path.posix.relative(
		srcPath,
		MODULES_DIR
	);

	const tsBuildInfoFile = path.posix.relative(
		srcPath,
		path.join(
			TSC_BUILDINFO_DIR,
			`${projectDescription.name}${testConfig ? '-test' : ''}.tsbuildinfo`
		)
	);

	const tscTypesDirProjectRelativePath = path.posix.relative(
		srcPath,
		TSC_TYPES_DIR
	);

	const typesDirProjectRelativePath = path.posix.relative(
		srcPath,
		GLOBAL_NODE_MODULES_TYPES_DIR
	);

	const paths = {};
	const references = [];

	if (currentProject.entryPoints.submodules) {
		Object.entries(currentProject.entryPoints.submodules).forEach(
			([submoduleName, subModulePath]) => {
				paths[`${projectDescription.name}/${submoduleName}`] = [
					'./' +
						path.posix.relative(
							srcPath,
							path.join(projectDir, subModulePath)
						),
				];
			}
		);
	}

	for (const dependency of Object.keys(projectDependencies)) {
		const dependencyProject = projects[dependency];

		if (!dependencyProject) {
			continue;
		}

		const projectMainEntryPointPath = path.join(
			MODULES_DIR,
			...`${dependencyProject.dir}/${dependencyProject.entryPoints.main}`.split(
				'/'
			)
		);

		const projectSubmodulesEntryPointsPaths = Object.entries(
			dependencyProject.entryPoints.submodules ?? {}
		).reduce((map, [entryPointName, entryPointPath]) => {
			map[entryPointName] = path.join(
				MODULES_DIR,
				...`${dependencyProject.dir}/${entryPointPath}`.split('/')
			);

			return map;
		}, {});

		paths[dependency] = [
			path.posix.relative(srcPath, projectMainEntryPointPath),
		];

		Object.entries(projectSubmodulesEntryPointsPaths).forEach(
			([entryPointName, entryPointPath]) => {
				paths[`${dependency}/${entryPointName}`] = [
					path.posix.relative(srcPath, entryPointPath),
				];
			}
		);

		if (!dependencyProject.typeScriptProject) {
			continue;
		}

		const projectPath = path.posix.relative(
			srcPath,
			path.join(MODULES_DIR, dependencyProject.dir)
		);

		references.push({path: `${projectPath}/${SRC_TSCONFIG_PATH}`});
	}

	const include = ['**/*.ts', '**/*.tsx', globalDTsFileProjectRelativePath];

	if (testConfig) {
		include.push('../src/**/*.ts', '../src/**/*.tsx');
	}

	const json = {
		...baseTsconfig,
		compilerOptions: {
			...baseTsconfig.compilerOptions,
			declarationDir: tscTypesDirProjectRelativePath,
			paths,
			rootDir: modulesDirProjectRelativePath,
			tsBuildInfoFile,
			typeRoots: [typesDirProjectRelativePath],
		},
		include,
		references,
	};

	json[GENERATED] = hash(json);

	let contents = '';

	if (await fileExists(configPath)) {
		contents = await fs.readFile(configPath, 'utf8');
	}

	const previousConfig = JSON.parse(contents.trim() ? contents : '{}');

	if (
		hash(previousConfig) !== previousConfig[GENERATED] ||
		json[GENERATED] !== previousConfig[GENERATED]
	) {
		await visitorFunction(configPath, objectSF(json));
	}
}

function hash(config) {
	const shasum = crypto.createHash('sha1');

	const configCopy = {
		...config,
	};

	delete configCopy[GENERATED];

	shasum.update(objectSF(configCopy));

	return shasum.digest('hex');
}
