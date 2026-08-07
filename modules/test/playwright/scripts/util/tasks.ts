/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable no-console */

import {spawnSync} from 'child_process';
import {
	copyFileSync,
	existsSync,
	readFileSync,
	readdirSync,
	unlinkSync,
	writeFileSync,
} from 'fs';
import {dirname, join, sep} from 'path';

const _IGNORED_DIR_NAMES = new Set([
	'.git',
	'.gradle',
	'.npmscripts',
	'.releng',
	'build',
	'dist',
	'node_modules',
]);

const _MAX_SEARCH_DEPTH = 4;

function copyFile(
	setup: boolean,
	bundlesDir: string,
	from: string,
	to: string
) {
	if (setup && !existsSync(from)) {
		return;
	}

	const toPath = join(bundlesDir, to);

	if (!setup && !existsSync(toPath)) {
		return;
	}

	if (setup) {
		process.stdout.write(`      Deploying file: ${to}`);

		copyFileSync(from, toPath);
	}
	else {
		process.stdout.write(`      Undeploying file: ${to}`);

		unlinkSync(toPath);
	}

	console.log(' ✅');
}

function deployClientExtension(
	setup: boolean,
	portalSourceDir: string,
	bundlesDir: string,
	projectPath: string
) {
	const projectName = projectPath.split('/').pop();

	if (setup) {
		process.stdout.write(
			`      Deploying client extension: ${projectName}`
		);

		const projectDir = resolveProjectDir(
			[join(portalSourceDir, 'workspaces')],
			projectPath
		);

		runCommand(projectDir, findGradlew(projectDir), [
			'deploy',
			`-Pliferay.workspace.home.dir=${bundlesDir}`,
		]);
	}
	else {
		process.stdout.write(
			`      Undeploying client extension: ${projectName}`
		);

		unlinkSync(
			join(bundlesDir, 'osgi', 'client-extensions', `${projectName}.zip`)
		);
	}

	console.log(' ✅');
}

function deployOSGiModule(
	setup: boolean,
	portalSourceDir: string,
	projectPath: string
) {
	process.stdout.write(
		`      ${setup ? 'Deploying' : 'Undeploying'} module: ${projectPath}`
	);

	const projectDir = resolveProjectDir(
		[
			join(portalSourceDir, 'modules', 'apps'),
			join(portalSourceDir, 'modules', 'dxp', 'apps'),
		],
		projectPath
	);

	runCommand(projectDir, findGradlew(projectDir), [
		setup ? 'deploy' : 'clean',
	]);

	console.log(' ✅');
}

function findGradlew(projectDir: string) {
	let dir = projectDir;

	while (dir !== dirname(dir)) {
		const gradlew = join(dir, 'gradlew');

		if (existsSync(gradlew)) {
			return gradlew;
		}

		dir = dirname(dir);
	}

	throw new Error(`Unable to find gradlew above ${projectDir}`);
}

function findProjectDirs(rootDir: string, projectPath: string) {
	if (!existsSync(rootDir)) {
		return [];
	}

	const projectDirs: string[] = [];
	const suffix = sep + projectPath.split('/').join(sep);

	const walk = (dir: string, depth: number) => {
		readdirSync(dir, {withFileTypes: true}).forEach((dirent) => {
			if (!dirent.isDirectory() || _IGNORED_DIR_NAMES.has(dirent.name)) {
				return;
			}

			const childDir = join(dir, dirent.name);

			if (childDir.endsWith(suffix)) {
				projectDirs.push(childDir);
			}
			else if (depth < _MAX_SEARCH_DEPTH) {
				walk(childDir, depth + 1);
			}
		});
	};

	walk(rootDir, 1);

	return projectDirs;
}

function resolveProjectDir(rootDirs: string[], projectPath: string) {
	const projectDirs = rootDirs
		.flatMap((rootDir) => findProjectDirs(rootDir, projectPath))
		.sort();

	if (!projectDirs.length) {
		throw new Error(
			`Unable to find project "${projectPath}" in ${rootDirs.join(', ')}`
		);
	}

	if (projectDirs.length > 1) {
		console.log(
			`\n      ⚠️ Duplicate projects were found for "${projectPath}". ` +
				`Using the first one:\n${projectDirs
					.map((projectDir) => `         ${projectDir}`)
					.join('\n')}`
		);
	}

	return projectDirs[0];
}

function tweakPortalExtProperties(
	setup: boolean,
	bundlesDir: string,
	fileQualifiers: string[]
) {
	console.log(`⚙️ Tweaking portal-ext.properties:`);

	const portalExtPropertiesFile = join(bundlesDir, 'portal-ext.properties');
	const portalExtPropertiesFileExists = existsSync(portalExtPropertiesFile);

	if (!setup && !portalExtPropertiesFileExists) {
		return;
	}

	const lines = portalExtPropertiesFileExists
		? readFileSync(portalExtPropertiesFile, 'utf-8').split('\n')
		: [];

	if (setup) {
		fileQualifiers.forEach((fileQualifier) => {
			const fileName = `portal-ext.${fileQualifier}.properties`;

			if (!existsSync(join(bundlesDir, fileName))) {
				return;
			}

			const property = `include-and-override=${fileName}`;

			const found = lines
				.map((line) => line.trim())
				.find((line) => line === property);

			if (found) {
				return;
			}

			console.log(`      Adding ${fileName} as include-and-override ✅`);

			lines.push(property);
		});
	}
	else {
		fileQualifiers.forEach((fileQualifier) => {
			const fileName = `portal-ext.${fileQualifier}.properties`;

			const property = `include-and-override=${fileName}`;

			const index = lines
				.map((line) => line.trim())
				.findIndex((line) => line === property);

			if (index === -1) {
				return;
			}

			console.log(
				`      Removing ${fileName} as include-and-override ✅`
			);

			lines.splice(index, 1);
		});
	}

	writeFileSync(portalExtPropertiesFile, lines.join('\n'), 'utf-8');
}

function runCommand(workDir: string, cmd: string, args: string[]) {
	const {error, status, stderr, stdout} = spawnSync(cmd, args, {
		cwd: workDir,
		stdio: 'pipe',
	});

	if (error) {
		throw new Error(
			`Failed to run command '${cmd} ${args.join(
				' '
			)}' (at ${workDir}}:\n\n` + `${error.toString()}`
		);
	}

	if (status !== 0) {
		throw new Error(
			`Failed to run command '${cmd} ${args.join(
				' '
			)}' (at ${workDir}}:\n\n` +
				`STDOUT:\n${stdout.toString()}\n\nSTDERR:\n${stderr.toString()}`
		);
	}

	return {stderr, stdout};
}

export default {
	setup: {
		copyFile: (bundlesDir: string, from: string, to: string) =>
			copyFile(true, bundlesDir, from, to),
		deployClientExtension: (
			portalSourceDir: string,
			bundlesDir: string,
			projectName: string
		) =>
			deployClientExtension(
				true,
				portalSourceDir,
				bundlesDir,
				projectName
			),
		deployOSGiModule: (portalSourceDir: string, projectDir: string) =>
			deployOSGiModule(true, portalSourceDir, projectDir),
		tweakPortalExtProperties: (
			bundlesDir: string,
			fileQualifiers: string[]
		) => tweakPortalExtProperties(true, bundlesDir, fileQualifiers),
	},
	teardown: {
		copyFile: (bundlesDir: string, from: string, to: string) =>
			copyFile(false, bundlesDir, from, to),
		deployClientExtension: (
			portalSourceDir: string,
			bundlesDir: string,
			projectName: string
		) =>
			deployClientExtension(
				false,
				portalSourceDir,
				bundlesDir,
				projectName
			),
		deployOSGiModule: (portalSourceDir: string, projectDir: string) =>
			deployOSGiModule(false, portalSourceDir, projectDir),
		tweakPortalExtProperties: (
			bundlesDir: string,
			fileQualifiers: string[]
		) => tweakPortalExtProperties(false, bundlesDir, fileQualifiers),
	},
};
