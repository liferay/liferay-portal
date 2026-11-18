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

	const removedHeaders = new Set(
		getEntries(content)
			.map(({header}) => header)
			.filter((header) => headers.has(header))
	);

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

	await fs.writeFile(lockFile, removeEntries(content, removedHeaders));

	print(
		0,
		print.info('\nINFO:'),
		`Removed ${removedHeaders.size} entries from yarn.lock. Running`,
		`'yarn install' to resolve them again...\n`
	);

	await execa('yarn', ['install'], {stdio: 'inherit'});

	await deduplicate(lockFile, getPackageVersions(content));
}

/**
 * Merges the entries of the packages that got a new version in this run into
 * that version when their ranges allow it. Each older entry is deleted and
 * resolved again, and the change is kept only if no package gets a version
 * that the yarn.lock file did not hold before, so nothing outside this run is
 * refreshed.
 */
async function deduplicate(lockFile, originalPackageVersions) {
	const entries = getEntries(await fs.readFile(lockFile, 'utf8'));

	const newPackageVersions = getNewPackageVersions(
		entries,
		originalPackageVersions
	);

	const headers = entries
		.filter(
			({name, version}) =>
				originalPackageVersions.get(name)?.has(version) &&
				newPackageVersions
					.get(name)
					?.some(
						(newVersion) =>
							getReleaseLine(newVersion) ===
							getReleaseLine(version)
					)
		)
		.map(({header}) => header);

	if (!headers.length) {
		return;
	}

	print(
		0,
		print.info('\nINFO:'),
		`Trying to merge ${headers.length} duplicated entries into the`,
		`versions this run resolved...\n`
	);

	for (const header of headers) {
		const content = await fs.readFile(lockFile, 'utf8');

		const entry = getEntries(content).find(
			(entry) => entry.header === header
		);

		if (!entry) {
			continue;
		}

		await fs.writeFile(lockFile, removeEntries(content, new Set([header])));

		await execa('yarn', ['install', '--silent']);

		const resolvedEntries = getEntries(await fs.readFile(lockFile, 'utf8'));

		const newPackageVersions = getNewPackageVersions(
			resolvedEntries,
			getPackageVersions(content)
		);

		const resolvedVersion = resolvedEntries.find(({specifiers}) =>
			specifiers.includes(entry.specifiers[0])
		)?.version;

		if (newPackageVersions.size) {
			await fs.writeFile(lockFile, content);

			await execa('yarn', ['install', '--silent']);

			print(
				0,
				print.warning('WARNING:'),
				`Unable to merge ${entry.name} ${entry.version} without`,
				`refreshing other entries, so it was kept.`
			);
		}
		else if (resolvedVersion === entry.version) {
			print(
				0,
				print.warning('WARNING:'),
				`Unable to merge ${entry.name} ${entry.version} because its`,
				`ranges do not allow a newer version, so it was kept.`
			);
		}
		else {
			print(
				0,
				print.info('INFO:'),
				`Merged ${entry.name} ${entry.version} into ${resolvedVersion}.`
			);
		}
	}
}

// Entries are separated by blank lines and open with their header line, which
// lists the "<name>@<range>" specifiers that resolve to the entry's version

function getEntries(content) {
	return content.split('\n\n').flatMap((block) => {
		const lines = block.trimStart().split('\n');

		const header = lines[0];
		const version = /\n {2}version "([^"]+)"/.exec(block)?.[1];

		if (!header.endsWith(':') || header.startsWith('#') || !version) {
			return [];
		}

		const specifiers = header
			.slice(0, -1)
			.split(', ')
			.map((specifier) => specifier.replace(/^"|"$/g, ''));

		return [
			{
				header,
				name: specifiers[0].substring(0, specifiers[0].indexOf('@', 1)),
				specifiers,
				version,
			},
		];
	});
}

/**
 * Returns the versions of each package that the entries resolve to and the
 * given package versions do not hold.
 */
function getNewPackageVersions(entries, packageVersions) {
	const newPackageVersions = new Map();

	for (const {name, version} of entries) {
		if (packageVersions.get(name)?.has(version)) {
			continue;
		}

		if (!newPackageVersions.has(name)) {
			newPackageVersions.set(name, []);
		}

		newPackageVersions.get(name).push(version);
	}

	return newPackageVersions;
}

function getPackageVersions(content) {
	const packageVersions = new Map();

	for (const {name, version} of getEntries(content)) {
		if (!packageVersions.has(name)) {
			packageVersions.set(name, new Set());
		}

		packageVersions.get(name).add(version);
	}

	return packageVersions;
}

/**
 * Returns the part of a version that a caret range keeps fixed, which is the
 * major version, or the major and minor versions for 0.x releases.
 */
function getReleaseLine(version) {
	const [major, minor] = version.split('.');

	return major === '0' ? `${major}.${minor}` : major;
}

async function readStdin() {
	let text = '';

	process.stdin.setEncoding('utf8');

	for await (const chunk of process.stdin) {
		text += chunk;
	}

	return text;
}

function removeEntries(content, headers) {
	return content
		.split('\n\n')
		.filter((block) => !headers.has(block.trimStart().split('\n')[0]))
		.join('\n\n');
}
