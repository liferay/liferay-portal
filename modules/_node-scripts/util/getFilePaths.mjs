/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fg from 'fast-glob';
import micromatch from 'micromatch';
import path from 'path';

import {readIgnoreFile} from './readIgnoreFile.mjs';

const PRETTIER_IGNORE_FILE = '.prettierignore';
const ESLINT_IGNORE_FILE = '.eslintignore';
const GIT_IGNORE_FILE = '.gitignore';

const EXTENSIONS = ['graphql', 'js', 'jsp', 'jspf', 'mjs', 'scss', 'ts', 'tsx'];

export default async function getFilePaths(rootDir, filesToFormat) {
	const workspacesDir = path.join(rootDir, '..', 'workspaces');
	const playwrightDir = path.join(rootDir, 'test', 'playwright');

	const [rootIgnored, workspacesIgnored, playwrightIgnored] =
		await Promise.all([
			getIgnoredFiles(rootDir),
			getIgnoredFiles(
				path.join(workspacesDir, 'liferay-sample-workspace')
			),
			getIgnoredFiles(playwrightDir),
		]);

	let filepaths = [];

	if (!filesToFormat) {
		filepaths = (
			await Promise.all([
				getFilesToCheck(rootDir, rootIgnored),
				getFilesToCheck(workspacesDir, workspacesIgnored),
				getFilesToCheck(playwrightDir, playwrightIgnored),
			])
		).flat();
	}
	else {
		for (const file of filesToFormat) {
			if (file.startsWith('modules/test/playwright/')) {
				filepaths.push(
					...micromatch(
						[file],
						EXTENSIONS.map((ext) => `**/*.${ext}`),
						{ignore: playwrightIgnored}
					)
				);
			}

			if (file.startsWith('modules/')) {
				filepaths.push(
					...micromatch(
						[file],
						EXTENSIONS.map((ext) => `**/*.${ext}`),
						{ignore: rootIgnored}
					)
				);
			}

			if (file.startsWith('workspaces/')) {
				filepaths.push(
					...micromatch(
						[file],
						EXTENSIONS.map((ext) => `**/*.${ext}`),
						{ignore: workspacesIgnored}
					)
				);
			}
		}

		filepaths = filepaths.map(

			// make sure the path is absolute

			(filepath) => path.join(rootDir, '..', filepath)
		);
	}

	return filepaths;
}

async function getIgnoredFiles(rootDir) {
	const eslintIgnoreFilePath = path.join(rootDir, ESLINT_IGNORE_FILE);
	const prettierIgnoreFilePath = path.join(rootDir, PRETTIER_IGNORE_FILE);
	const gitIgnoreFilePath = path.join(rootDir, GIT_IGNORE_FILE);

	const [eslintIgnores, prettierIgnores, gitIgnores] = await Promise.all([
		readIgnoreFile(eslintIgnoreFilePath),
		readIgnoreFile(prettierIgnoreFilePath),
		readIgnoreFile(gitIgnoreFilePath),
	]);

	return [
		'**/src/test/**',
		'**/build_gradle/**',
		...gitIgnores,
		...eslintIgnores,
		...prettierIgnores,
	].map((ignore) => {
		if (ignore.startsWith('*') && !ignore.startsWith('**')) {
			ignore = `**/${ignore}`;
		}

		if (!ignore.startsWith('*')) {
			ignore = `**${ignore.startsWith('/') ? '' : '/'}${ignore}`;
		}

		if (!ignore.endsWith('**') && !ignore.includes('.')) {
			ignore = `${ignore}${ignore.endsWith('/') ? '' : '/'}**`;
		}

		return ignore;
	});
}

async function getFilesToCheck(rootDir, ignore = []) {
	const files = await fg(
		[
			'**/*.',
			'*.{graphql,js,mjs,scss,ts,tsx}',
			'**/*.{graphql,js,mjs,scss,ts,tsx}',
			'**/src/**/*.{jsp,jspf}',
		],
		{
			cwd: rootDir,
			dot: true,
			ignore,
		}
	);

	return files.map((filepath) => path.join(rootDir, filepath));
}
