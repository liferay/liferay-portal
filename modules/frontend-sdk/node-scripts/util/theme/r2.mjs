/**
 * SPDX-FileCopyrightText: © 2017 Liferay, Inc. <https://liferay.com>
 * SPDX-License-Identifier: MIT
 */

'use strict';

const chalk = require('chalk');
const gutil = require('gulp-util');
const path = require('path');
const {StringDecoder} = require('string_decoder');
const through = require('through2');
const rtlcss = require('rtlcss');

const decoder = new StringDecoder('utf8');
const PluginError = gutil.PluginError;

const PLUGIN_NAME = 'gulp-R2-css';

function gulpR2() {
	const task = through.obj(function (file, enc, callback) {
		if (file.isNull()) {
			return callback(null, file);
		}

		if (file.isBuffer()) {
			try {
				file.contents = swapBuffer(file.contents);
			}
			catch (error) {
				this.push(file);

				// eslint-disable-next-line no-console
				console.log(
					chalk.red.bold('[ERROR]'),
					chalk.magenta(`.${path.sep}${error.filename}`),
					chalk.cyan(
						`(${error.position.end.line}:${error.position.end.column})`
					),
					chalk.red.bold(error.description)
				);

				task.hasCssParseErrors = true;
			}

			callback(null, file);
		}

		if (file.isStream()) {
			this.emit(
				'error',
				new PluginError(PLUGIN_NAME, 'Streaming not supported')
			);

			return callback();
		}
	});

	return task;
}

function swapBuffer(buffer) {
	var swapped = rtlcss.process(
		decoder.write(buffer).replaceAll('/* @noflip */', '/*rtl:ignore*/')
	);

	return new Buffer.from(swapped);
}

module.exports = gulpR2;
