/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import crypto from 'crypto';
import fg from 'fast-glob';
import fs from 'fs/promises';
import path from 'path';

import {NODE_SCRIPTS_DIR} from './locations.mjs';
import {SASS_BINARY_CACHE_DIR} from './sass/util/installSassBinary.mjs';

// The Sass compiler is downloaded into the node-scripts tree, so it must be
// skipped: otherwise a release of the compiler shipping any JavaScript would
// make this hash depend on whether the binary happens to be installed.

const SASS_BINARY_CACHE_GLOB =
	path
		.relative(NODE_SCRIPTS_DIR, SASS_BINARY_CACHE_DIR)
		.split(path.sep)
		.join('/') + '/**';

export default async function digestNodeScripts() {
	const sha256 = crypto.createHash('sha256');

	let files = await fg(['**/*.mjs', '**/*.js'], {
		absolute: true,
		cwd: NODE_SCRIPTS_DIR,
		ignore: [SASS_BINARY_CACHE_GLOB, 'node_modules/**'],
	});

	files = files.filter((file) => !path.basename(file).startsWith('.'));

	files.sort();

	const fileContents = await Promise.all(
		files.map(async (file) => {
			return await fs.readFile(file, 'utf-8');
		})
	);

	for (const fileContent of fileContents) {
		sha256.update(fileContent);
	}

	return sha256.digest('hex');
}
