/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs';
import path from 'path';

import calculateFileHash from '../../calculateFileHash.mjs';
import project from '../project.mjs';

/**
 * before:build:war
 *
 * Adds a content hash to the name of every CSS and JS file of the build so
 * that browsers can cache them forever.
 */
export default async function buildHashify() {
	const {pathBuild} = project.options;

	for (const filePath of [
		...collectFiles('.css', pathBuild),
		...collectFiles('.js', pathBuild),
	]) {
		await hashify(filePath);
	}
}

function collectFiles(extension, dir) {
	const files = [];

	for (const dirent of fs.readdirSync(dir, {withFileTypes: true})) {
		if (dirent.isDirectory()) {
			files.push(...collectFiles(extension, path.join(dir, dirent.name)));
		}
		else if (dirent.name.endsWith(extension)) {
			files.push(path.join(dir, dirent.name));
		}
	}

	return files;
}

async function hashify(filePath) {
	const content = fs.readFileSync(filePath, 'utf-8');

	const hash = await calculateFileHash(content);

	const fileName = path.basename(filePath);

	const i = fileName.lastIndexOf('.');

	const hashedFileName =
		fileName.substring(0, i) + `.(${hash})` + fileName.substring(i);

	fs.renameSync(filePath, `${path.dirname(filePath)}/${hashedFileName}`);
}
