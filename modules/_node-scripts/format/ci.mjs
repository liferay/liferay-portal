/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import path from 'path';

import runPreflight from '../preflight/runPreflight.mjs';
import checkProjects from '../tsc/checkProjects.mjs';
import extractProjectDirs from '../tsc/extractProjectDirs.mjs';
import visitOutdatedTsconfigFiles from '../tsconfig/visitOutdatedTsconfigFiles.mjs';
import {getRootDir} from '../util/constants.mjs';
import getFilePaths from '../util/getFilePaths.mjs';
import getNamedArguments from '../util/getNamedArguments.mjs';
import gitUtil from '../util/gitUtil.mjs';
import format from './format.mjs';

export default async function main() {
	const {all, ignorePreflight, ignoreTypescript} = getNamedArguments({
		all: '--all',
		ignorePreflight: '--ignore-preflight',
		ignoreTypescript: '--ignore-typescript',
	});

	const rootDir = await getRootDir();

	let files;

	if (all) {
		console.log(`⚠️ Formatting all files`);

		files = await getFilePaths(rootDir);
	}
	else {
		console.log(`⚠️ Formatting files on current branch`);

		files = await gitUtil('current-branch');
	}

	let checksPassed = true;

	// Preflight checks

	if (!ignorePreflight) {
		console.log('\n⚙️ Running preflight checks...');

		const preflightErrors = await runPreflight();

		if (preflightErrors.length) {
			console.error(
				preflightErrors.map((error) => `   · ${error}`).join('\n')
			);

			checksPassed = false;
		}
	}

	if (!ignoreTypescript) {

		// Tsconfig checks

		console.log('\n⚙️ Checking tsconfig.json files ...');

		await visitOutdatedTsconfigFiles((filePath) => {
			console.error(
				`   · ${path.relative(rootDir, filePath)} is outdated`
			);

			checksPassed = false;
		});

		// TypeScript checks

		console.log('\n⚙️ Running TypeScript checks on files...');

		const tscProjectDirs = await extractProjectDirs(files);

		if (!(await checkProjects(tscProjectDirs, false))) {
			checksPassed = false;
		}
	}

	// SF checks

	console.log('\n⚙️ Running format on files...');

	const formatOutput = await format(true, files);

	if (formatOutput) {
		console.error(
			formatOutput
				.split('\n')
				.map((line) => `   ${line}`)
				.join('\n')
		);
		checksPassed = false;
	}
	else {
		console.log('ℹ️ Nothing needed to be formatted (no changes detected).');
	}

	// Finalization

	if (!checksPassed) {
		console.error('❌ format:ci checks failed.');
		process.exit(1);
	}
}
