/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {execa} from 'execa';
import fs from 'fs/promises';
import path from 'path';

import print from '../util/print.mjs';

export default async function main() {
	const lockFile = path.join(process.cwd(), 'yarn.lock');

	let content;

	try {
		content = await fs.readFile(lockFile, 'utf8');
	}
	catch (error) {
		if (error.code !== 'ENOENT') {
			throw error;
		}

		print(
			0,
			print.error('\nERROR:'),
			`No yarn.lock file was found in ${process.cwd()}. Run this command`,
			`\n       from the directory that holds the yarn.lock file (for`,
			`\n       example, 'modules' or a workspace).\n`
		);

		process.exit(2);
	}

	if (process.stdin.isTTY) {
		print(
			0,
			print.info('\nINFO:'),
			`Paste the yarn.lock entries copied from the 'Lock refresh' tab of`,
			`\n      the report:outdated report, then press Ctrl+D.\n`
		);
	}

	const headers = new Set(
		(await readStdin())
			.split(/\r?\n/)
			.map((line) => line.trim())
			.filter(Boolean)
	);

	if (!headers.size) {
		print(0, print.warning('\nWARNING:'), 'No entries were given.\n');

		return;
	}

	// Entries are separated by blank lines and open with their header line

	const blocks = content.split('\n\n');
	const removedHeaders = new Set();

	const keptBlocks = blocks.filter((block) => {
		const header = block.trimStart().split('\n')[0];

		if (headers.has(header)) {
			removedHeaders.add(header);

			return false;
		}

		return true;
	});

	for (const header of headers) {
		if (!removedHeaders.has(header)) {
			print(
				0,
				print.warning('WARNING:'),
				`Unable to find the yarn.lock entry ${header}.`
			);
		}
	}

	if (!removedHeaders.size) {
		print(0, print.warning('\nWARNING:'), 'No entries were removed.\n');

		return;
	}

	await fs.writeFile(lockFile, keptBlocks.join('\n\n'));

	print(
		0,
		print.info('\nINFO:'),
		`Removed ${removedHeaders.size} entries from yarn.lock. Running`,
		`'yarn install' to resolve them again...\n`
	);

	await execa('yarn', ['install'], {stdio: 'inherit'});
}

async function readStdin() {
	let text = '';

	process.stdin.setEncoding('utf8');

	for await (const chunk of process.stdin) {
		text += chunk;
	}

	return text;
}
