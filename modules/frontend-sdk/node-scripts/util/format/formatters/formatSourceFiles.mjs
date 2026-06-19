/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import childProcess from 'child_process';
import micromatch from 'micromatch';
import os from 'os';
import path from 'path';
import resolve from 'resolve';

import ResolvablePromise from '../../ResolvablePromise.mjs';
import getNamedArguments from '../../getNamedArguments.mjs';
import {MODULES_DIR, PORTAL_DIR} from '../../locations.mjs';
import print, {printDuration} from '../../print.mjs';
import runGitLsFiles from '../../runGitLsFiles.mjs';
import formatSourceFile from '../util/formatSourceFile.mjs';
import readIgnorePatterns from '../util/readIgnorePatterns.mjs';

const EXTENSIONS = ['graphql', 'js', 'jsp', 'jspf', 'mjs', 'scss', 'ts', 'tsx'];

/**
 * @param {boolean} check whether to check or fix
 * @param {string[]} files list of portal relative path of files to check
 * @return {Promise<boolean>} true if all files are correctly formatted
 */
export default async function formatSourceFiles(check, files) {
	const start = Date.now();

	const {emitSuppressed, emitSuppressedCss} = getNamedArguments({
		emitSuppressed: '--emit-suppressed',
		emitSuppressedCss: '--emit-suppressed-css',
	});

	const filePaths = await getFilePaths(files);

	// Gather read only info to be reused in all tasks

	const ignorePatterns = await getIgnorePatterns();
	const options = {
		check,
		emitSuppressed,
		emitSuppressedCss,
	};

	// Do not spawn workers if there's only one file

	if (filePaths.length === 1) {
		const [filePath] = filePaths;

		return formatSourceFile(
			filePath,
			getSkip(filePath, ignorePatterns),
			options
		);
	}

	print(
		1,
		print.subTitle(
			`> ${check ? 'Checking' : 'Formatting'} ${filePaths.length} source files with SF...\n`
		)
	);

	// Task synchronization state

	let nextFileIndex = 0;
	const resultPromises = filePaths.map(() => ResolvablePromise.new());

	// Create worker child processes

	const workers = os.cpus().map(() =>
		childProcess.fork(
			resolve.sync('../util/formatSourceFilesWorker.mjs', {
				basedir: import.meta.dirname,
			})
		)
	);

	// Deliver tasks to worker processes and handle results

	const sendTask = (worker) => {
		if (nextFileIndex >= filePaths.length) {
			return;
		}

		const fileIndex = nextFileIndex++;
		const filePath = filePaths[fileIndex];

		worker.send({
			fileIndex,
			filePath,
			options,
			skip: getSkip(filePath, ignorePatterns),
		});
	};

	workers.forEach((worker) => {
		worker.on('message', ({fileIndex, result}) => {
			resultPromises[fileIndex].resolve(result);

			sendTask(worker);
		});

		sendTask(worker);
	});

	// Wait for results and check

	const results = await Promise.all(resultPromises);

	printDuration(start, 1, 'Formatting with SF');

	return !results.some((result) => !result);
}

/**
 *
 * @param {string[]} files portal relative path of files to check
 * @return {Promise<string[]>} absolute paths of files to check
 */
async function getFilePaths(files) {
	let filePaths;

	if (!files) {
		filePaths = await runGitLsFiles(
			[
				...EXTENSIONS.map((ext) => `*.${ext}`),
				...EXTENSIONS.map((ext) => `**/*.${ext}`),
			],
			process.cwd()
		);
	}
	else {
		filePaths = files.filter((file) => {
			for (const ext of EXTENSIONS) {
				if (file.endsWith(`.${ext}`)) {
					return true;
				}
			}

			return false;
		});
	}

	filePaths = filePaths.map((file) => path.resolve(PORTAL_DIR, file));

	return filePaths;
}

async function getIgnorePatterns() {
	const [
		eslintIgnorePatterns,
		prettierIgnorePatterns,
		stylelintIgnorePatterns,
	] = await Promise.all([
		readIgnorePatterns(path.resolve(MODULES_DIR, '.eslintignore')),
		readIgnorePatterns(path.resolve(MODULES_DIR, '.prettierignore')),
		readIgnorePatterns(path.resolve(MODULES_DIR, '.stylelintignore')),
	]);

	return {
		eslint: eslintIgnorePatterns,
		prettier: prettierIgnorePatterns,
		stylelint: stylelintIgnorePatterns,
	};
}

function getSkip(filePath, ignorePatterns) {
	const modulesRelativeFilePath = path.relative(MODULES_DIR, filePath);

	const isIncluded = (ignorePatterns) =>
		!micromatch.isMatch(modulesRelativeFilePath, ignorePatterns, {
			dot: true,
		});

	return {
		eslint: !isIncluded(ignorePatterns.eslint),
		prettier: !isIncluded(ignorePatterns.prettier),
		stylelint: !isIncluded(ignorePatterns.stylelint),
	};
}
