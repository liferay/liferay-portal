/**
 * SPDX-FileCopyrightText: © 2017 Liferay, Inc. <https://liferay.com>
 * SPDX-License-Identifier: MIT
 */

'use strict';

const zip = require('gulp-zip');

const project = require('../../lib/project');

module.exports = function () {
	const {gulp} = project;
	const {runSequence} = gulp;

	gulp.task('plugin:war', () => {
		const {options} = project;

		return gulp
			.src(options.rootDir.join('**/*').asPosix)
			.pipe(zip(options.distName + '.war'))
			.pipe(gulp.dest(options.pathDist.asNative));
	});

	gulp.task('build', (done) => {
		runSequence('plugin:version', 'plugin:war', done);
	});
};
