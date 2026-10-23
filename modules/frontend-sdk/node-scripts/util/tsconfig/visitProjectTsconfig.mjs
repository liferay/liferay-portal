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
	TSC_BUILDINFO_DIR,
	TSC_TYPES_DIR,
} from '../locations.mjs';
import objectSF from '../objectSF.mjs';
import baseTsconfig from './baseTsconfig.mjs';
import hasTypeScriptFiles from './hasTypeScriptFiles.mjs';

const GENERATED = '@generated';

export default async function visitProjectTsconfig(
	visitorFunction,
	projectsEntryPoints,
	projectDependencies,
	projectDescription,
	projectDir = '.',
	testConfig = false
) {
	const srcPath = testConfig
		? path.join(projectDir, 'test')
		: path.join(projectDir, SRC_PATH);

	const configPath = path.join(srcPath, 'tsconfig.json');

	if (!(await hasTypeScriptFiles(srcPath))) {
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

	const currentProject = projectsEntryPoints[projectDescription.name];

	if (currentProject.path.submodules) {
		Object.entries(currentProject.path.submodules).forEach(
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
		const projectEntryPoint = projectsEntryPoints[dependency];

		if (!projectEntryPoint) {
			continue;
		}

		const projectMainEntryPointPath = path.join(
			MODULES_DIR,
			...`${projectEntryPoint.dir}/${projectEntryPoint.path.main}`.split(
				'/'
			)
		);

		const projectSubmodulesEntryPointsPaths = Object.entries(
			projectEntryPoint.path.submodules ?? {}
		).reduce((map, [entryPointName, entryPointPath]) => {
			map[entryPointName] = path.join(
				MODULES_DIR,
				...`${projectEntryPoint.dir}/${entryPointPath}`.split('/')
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

		if (!projectEntryPoint.hasTypeScript) {
			continue;
		}

		const projectPath = path.posix.relative(
			srcPath,
			path.join(MODULES_DIR, projectEntryPoint.dir)
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
