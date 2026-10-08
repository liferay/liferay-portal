/**
 * SPDX-FileCopyrightText: © 2021 Liferay, Inc. <https://liferay.com>
 * SPDX-License-Identifier: MIT
 */

const chalk = require('chalk');
const path = require('path');
const PluginError = require('plugin-error');
const sass = require('sass');
const through = require('through2');

const PLUGIN_NAME = 'sass';

function replaceExtension(filePath, newExt) {
	const newFileName =
		path.basename(filePath, path.extname(filePath)) + newExt;

	return path.join(path.dirname(filePath), newFileName);
}

module.exports = (options) =>
	through.obj((file, enc, callback) => {
		if (file.isNull()) {
			return callback(null, file);
		}

		if (file.isStream()) {
			return callback(
				new PluginError(PLUGIN_NAME, 'Streaming not supported')
			);
		}

		if (path.basename(file.path).indexOf('_') === 0) {
			return callback();
		}

		if (!file.contents.length) {
			file.path = replaceExtension(file.path, '.css');

			return callback(null, file);
		}

		try {
			const includePaths = [
				path.dirname(file.path),
				...options.includePaths,
			];

			const outFile = replaceExtension(file.path, '.css');

			const {css} = sass.renderSync({
				file: file.path,
				includePaths,
				outFile,
				outputStyle:
					process.env.NODE_ENV === 'production'
						? 'compressed'
						: 'expanded',
				sourceMap: options.sourceMap,
			});

			file.contents = css;
			file.path = outFile;

			if (file.stat) {
				file.stat.atime = file.stat.mtime = file.stat.ctime = new Date();
			}

			callback(null, file);
		}
		catch (error) {
			const filePath =
				(error.file === 'stdin' ? file.path : error.file) || file.path;
			const relativePath = path.relative(process.cwd(), filePath);
			const message = [
				chalk.underline(relativePath),
				error.formatted,
			].join('\n');

			error.messageFormatted = message;
			error.messageOriginal = error.message;
			error.message = message;
			error.relativePath = relativePath;

			return callback(new PluginError(PLUGIN_NAME, error));
		}
	});
