/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {$} from 'execa';
import path from 'path';
import resolve from 'resolve';

import fileExists from '../../fileExists.mjs';
import {PORTAL_DIR, SRC_TSCONFIG_PATH} from '../../locations.mjs';
import print from '../../print.mjs';
import runConcurrentTasks from '../../runConcurrentTasks.mjs';

const TSC_PATH = resolve.sync('typescript/bin/tsc', {basedir: '.'});

export default async function checkProjects(projectDirs) {
	let checksPassed = true;

	if (projectDirs.length === 1) {
		if (!(await checkProject(projectDirs[0], {}))) {
			checksPassed = false;
		}
	}
	else {
		const errors = {};

		await runConcurrentTasks(
			projectDirs.map((projectDir) => async () => {
				if (!(await checkProject(projectDir, errors))) {
					checksPassed = false;
				}
			})
		);
	}

	return checksPassed;
}

/**
 *
 * @param projectDir
 *
 * @param {object} errors
 * Persistent map of message errors indexed by `filePath` first, then by
 * `position`.
 *
 * This map must be hold by the caller and should be shared by all invocations
 * in the same checking so that errors that have already been displayed are not
 * shown again to the user.
 *
 * Each error entry contains the following fields:
 *
 *   - displayed: a boolean that is set to false when created and set to true
 *     when the error is displayed to the user to avoid showing it again.
 *   - label: the error|warning label plus the error id.
 *   - message: the error message.
 *   - hints: the hints to show below the error message.
 *
 * @return {Promise<boolean>}
 */
async function checkProject(projectDir, errors) {
	const configPaths = [];

	if (await fileExists(path.join(projectDir, SRC_TSCONFIG_PATH))) {
		configPaths.push(SRC_TSCONFIG_PATH);
	}
	else if (await fileExists(path.join(projectDir, 'tsconfig.json'))) {
		configPaths.push('tsconfig.json');
	}

	const testConfigPath = path.join('test', 'tsconfig.json');

	if (await fileExists(path.join(projectDir, testConfigPath))) {
		configPaths.push(testConfigPath);
	}

	let output = '';

	for (const configPath of configPaths) {
		const {all} = await $({
			all: true,
			cwd: projectDir,
			reject: false,
			stdout: 'pipe',
		})`${TSC_PATH} -b ${configPath}`;

		output += '\n' + all;
	}

	output = output.trim();

	if (!output) {
		return true;
	}

	// Error parsing

	let currentError;

	output.split('\n').forEach((line) => {
		if (line.startsWith(' ') || line.startsWith('\t')) {
			if (currentError) {
				currentError.hints += `  ${line.trim()}\n`;
			}

			return;
		}

		currentError = undefined;

		const [filePosition, label, ...messageParts] = line.split(':');

		const i = filePosition.lastIndexOf('(');
		const file = filePosition.substring(0, i);
		const position = filePosition.substring(i).trim();

		const filePath = path.relative(
			PORTAL_DIR,
			path.resolve(projectDir, file)
		);

		if (!errors[filePath]) {
			errors[filePath] = {};
		}

		if (errors[filePath][position]) {
			return;
		}

		currentError = errors[filePath][position] = {
			displayed: false,
			hints: '',
			label: label.trim(),
			message: messageParts.join(':').trim(),
		};
	});

	// Error display

	for (const filePath of Object.keys(errors)) {
		if (
			!Object.values(errors[filePath]).some((error) => !error.displayed)
		) {
			continue;
		}

		print(
			2,
			print.error('ERROR:'),
			'File',
			print.underline(filePath),
			'has TypeScript errors'
		);

		for (const [position, error] of Object.entries(errors[filePath])) {
			if (error.displayed) {
				continue;
			}

			error.displayed = true;

			const hints = error.hints.trimEnd();

			print(
				3,
				print.bold(position),
				print.red(`(${error.label})`),
				error.message,
				hints.length ? `\n${hints}` : '',
				'\n'
			);
		}
	}

	return false;
}
