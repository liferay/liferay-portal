/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs';
import path from 'path';

/**
 * Create an empty source map the way `gulp-sourcemaps` `init()` does.
 */
export function initSourceMap(relativePath, contents) {

	// Keys are in the order gulp-sourcemaps used, so the written source maps
	// stay the same

	/* eslint-disable sort-keys */
	return {
		version: 3,
		names: [],
		mappings: '',
		sources: [toPosix(relativePath)],
		sourcesContent: [contents],
		file: toPosix(relativePath),
	};

	/* eslint-enable sort-keys */
}

/**
 * Write the source map next to the file and return the file contents with a
 * comment pointing to it, the way `gulp-sourcemaps` `write('.')` does.
 */
export function writeSourceMap(baseDir, filePath, contents, sourceMap) {
	const relativePath = path.relative(baseDir, filePath);

	sourceMap.file = toPosix(relativePath);
	sourceMap.sources = sourceMap.sources.map(toPosix);
	sourceMap.sourceRoot = undefined;
	sourceMap.sourcesContent = sourceMap.sourcesContent || [];

	for (let i = 0; i < sourceMap.sources.length; i++) {
		if (!sourceMap.sourcesContent[i]) {
			try {
				sourceMap.sourcesContent[i] = fs.readFileSync(
					path.resolve(baseDir, sourceMap.sources[i]),
					'utf8'
				);
			}
			catch (error) {

				// Leave the source content empty, like gulp-sourcemaps did

			}
		}
	}

	sourceMap.sourceRoot = '/source/';

	const sourceMapFilePath = filePath + '.map';

	fs.writeFileSync(sourceMapFilePath, JSON.stringify(sourceMap));

	const url = path.basename(sourceMapFilePath);

	if (filePath.endsWith('.css')) {
		return contents + `\n/*# sourceMappingURL=${url} */\n`;
	}
	else {
		return contents + `\n//# sourceMappingURL=${url}\n`;
	}
}

function toPosix(filePath) {
	return filePath.split(path.sep).join('/');
}
