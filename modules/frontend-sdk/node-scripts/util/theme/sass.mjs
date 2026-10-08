/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import path from 'path';
import * as sass from 'sass';

function replaceExtension(filePath, newExt) {
	const newFileName =
		path.basename(filePath, path.extname(filePath)) + newExt;

	return path.join(path.dirname(filePath), newFileName);
}

/**
 * Compiles a `.scss` file.
 *
 * @param {string} filePath path of the `.scss` file
 * @param {string} contents contents of the `.scss` file
 * @param {object} options sass options
 * @return {{contents: string, filePath: string}} the compiled CSS and its path
 */
export default function compileSass(filePath, contents, options) {
	const outFile = replaceExtension(filePath, '.css');

	if (!contents.length) {
		return {contents, filePath: outFile};
	}

	const includePaths = [path.dirname(filePath), ...options.includePaths];

	try {
		const {css} = sass.renderSync({
			file: filePath,
			includePaths,
			outFile,
			outputStyle:
				process.env.NODE_ENV === 'production'
					? 'compressed'
					: 'expanded',
			sourceMap: options.sourceMap,
		});

		return {contents: css.toString(), filePath: outFile};
	}
	catch (error) {
		const errorFilePath =
			(error.file === 'stdin' ? filePath : error.file) || filePath;

		error.message = [
			path.relative(process.cwd(), errorFilePath),
			error.formatted,
		].join('\n');

		throw error;
	}
}
