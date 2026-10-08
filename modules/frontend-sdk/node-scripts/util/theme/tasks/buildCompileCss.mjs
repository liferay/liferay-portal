/**
 * SPDX-FileCopyrightText: © 2017 Liferay, Inc. <https://liferay.com>
 * SPDX-License-Identifier: MIT
 */

'use strict';

const colors = require('ansi-colors');
const log = require('fancy-log');
const fs = require('fs');
const postcss = require('gulp-postcss');
const _ = require('lodash');
const path = require('path');

const project = require('../../../lib/project');
const themeUtil = require('../../../lib/util');
const {createBourbonFile} = require('../../lib/bourbon_dependencies');

module.exports = function () {
	const {gulp, options} = project;
	const {pathBuild} = options;

	const handleScssError = (error) => {
		if (project.watching) {
			log(error);

			this.emit('end');
		}
		else {
			throw error;
		}
	};

	gulp.task('build:compile-css', (callback) => {
		const {options} = project;

		const sassOptions = getSassOptions(options.sassOptions, {
			dartSass: true,
			includePaths: getSassIncludePaths(),
			sourceMap: true,
		});

		let nodeSass;

		if (!sassOptions.dartSass) {
			try {
				nodeSass = require('node-sass');
			}
			catch (_error) {
				console.error(
					'`node-sass` is not found. Try installing it first `npm install node-sass`.'
				);
			}
		}

		const gulpIf = require('gulp-if');
		const gulpSass = sassOptions.dartSass
			? require('./sass')
			: require('gulp-sass')(nodeSass);
		const gulpSourceMaps = require('gulp-sourcemaps');

		const postCSSOptions = getPostCSSOptions(options.postcss);

		const cssBuild = pathBuild.join('_css');

		const srcPath = cssBuild.join('!(_)*.scss');

		gulp.src(srcPath.asPosix)
			.pipe(gulpIf(sassOptions.sourceMap, gulpSourceMaps.init()))
			.pipe(gulpSass(sassOptions))
			.pipe(
				gulpIf(postCSSOptions.enabled, postcss(postCSSOptions.plugins))
			)
			.on('error', handleScssError)
			.pipe(gulpIf(sassOptions.sourceMap, gulpSourceMaps.write('.')))
			.pipe(gulp.dest(cssBuild.asNative))
			.on('end', callback);
	});
};

function concatBourbonIncludePaths(includePaths) {
	return includePaths.concat(createBourbonFile());
}

/**
 * Returns the string `file` if a file with that name exists, and
 * `false` otherwise.
 *
 * Example:
 *
 *     > exists('package.json')
 *     'package.json'
 */
function exists(file) {
	return fs.existsSync(file) && file;
}

/**
 * Returns a string indicating which of the standard methods for
 * configuring PostCSS applies to the current theme, if any. Otherwise,
 * returns `false`.
 */
function getPostCSSRC() {
	const pkgJson = project.pkgJson;

	return (
		(Object.prototype.hasOwnProperty.call(pkgJson, 'postcss') &&
			'package.json "postcss"') ||
		exists('.postcssrc') ||
		exists('.postcssrc.js') ||
		exists('.postcssrc.json') ||
		exists('.postcssrc.yml') ||
		exists('postcss.config.js')
	);
}

function getPostCSSOptions(config) {
	const postCSSOptions = {
		enabled: false,
	};

	const rc = getPostCSSRC();

	if (_.isArray(config) && !!config.length) {
		if (rc) {
			throw new Error(
				'PostCSS must be configured in only one place but it was ' +
					'configured in both "liferayTheme" properties of the ' +
					`package.json and also in ${rc}`
			);
		}

		postCSSOptions.enabled = true;

		postCSSOptions.plugins = config.map((pluginDependency) =>
			// eslint-disable-next-line @liferay/no-dynamic-require
			require(pluginDependency)
		);
	}
	else if (rc) {
		postCSSOptions.enabled = true;
	}

	return postCSSOptions;
}

function getSassIncludePaths() {
	let includePaths = [
		themeUtil.getCustomDependencyPath('liferay-frontend-common-css') ||
			path.dirname(require.resolve('liferay-frontend-common-css')),
	];

	includePaths = concatBourbonIncludePaths(includePaths);
	includePaths.push(path.dirname(require.resolve('compass-mixins')));

	const argv = themeUtil.getArgv();
	if (argv['sass-include-paths']) {
		const customPaths = argv['sass-include-paths']
			.split(',')
			.map((item) => path.resolve(item));
		log(
			'using custom SASS include paths:',
			colors.magenta(customPaths.join(', '))
		);
		includePaths.push(...customPaths);
	}

	const themeNodeModules = path.resolve('node_modules');
	if (fs.existsSync(themeNodeModules)) {
		includePaths.push(themeNodeModules);
	}

	if (project.workspaceDir) {
		includePaths.push(project.workspaceDir.join('node_modules').asNative);
	}

	return includePaths;
}

function getSassOptions(sassOptions, defaults) {
	if (_.isFunction(sassOptions)) {
		sassOptions = sassOptions(defaults);
	}
	else {
		sassOptions = _.assign(defaults, sassOptions);
	}

	return sassOptions;
}
