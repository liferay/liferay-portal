/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fastGlob from 'fast-glob';
import fs from 'fs';
import {createRequire} from 'module';
import path from 'path';
import postcss from 'postcss';

import print from '../../print.mjs';
import {createBourbonFile} from '../bourbonDependencies.mjs';
import project from '../project.mjs';
import compileSass from '../sass.mjs';
import {initSourceMap, writeSourceMap} from '../sourceMaps.mjs';
import {resolveDependency} from '../themeUtil.mjs';

const require = createRequire(import.meta.url);

/**
 * build:compile-css
 */
export default async function buildCompileCss() {
	const {options} = project;

	const sassOptions = getSassOptions(options.sassOptions, {
		dartSass: true,
		includePaths: getSassIncludePaths(),
		sourceMap: true,
	});

	if (!sassOptions.dartSass) {
		throw new Error(
			'Compiling themes with node-sass is not supported: remove the ' +
				'dartSass option from liferayTheme.sassOptions in package.json'
		);
	}

	const postCSSConfig = getPostCSSConfig();

	const cssBuild = path.join(options.pathBuild, '_css');

	const files = await fastGlob('!(_)*.scss', {cwd: cssBuild});

	for (const relativePath of files.sort()) {
		const filePath = path.resolve(cssBuild, relativePath);

		const scss = fs.readFileSync(filePath, 'utf8');

		let sourceMap;

		if (sassOptions.sourceMap) {
			sourceMap = initSourceMap(relativePath, scss);
		}

		const compiled = compileSass(filePath, scss, sassOptions);

		const cssFilePath = compiled.filePath;

		let {contents} = compiled;

		if (postCSSConfig) {

			// Sass prepends a BOM to compressed output with non-ASCII chars,
			// which the final CSS loses anyway, so remove it to avoid shifting
			// the source map columns

			const result = await postcss(postCSSConfig.plugins).process(
				contents.replace(/^\uFEFF/, ''),
				{
					...postCSSConfig.options,
					from: cssFilePath,
					map: sourceMap ? {annotation: false} : false,
					to: cssFilePath,
				}
			);

			contents = result.css;

			if (sourceMap) {
				const cssRelativePath = path.relative(cssBuild, cssFilePath);

				sourceMap = result.map.toJSON();

				sourceMap.file = cssRelativePath;
				sourceMap.sources = sourceMap.sources.map((source) =>
					path.join(path.dirname(cssRelativePath), source)
				);
			}
		}

		if (sourceMap) {
			contents = writeSourceMap(
				cssBuild,
				cssFilePath,
				contents,
				sourceMap
			);
		}

		fs.writeFileSync(cssFilePath, contents);
	}
}

function getPostCSSConfig() {
	const configFilePath = path.join(project.dir, 'postcss.config.js');

	if (!fs.existsSync(configFilePath)) {
		return;
	}

	const configRequire = createRequire(configFilePath);

	const config = configRequire(configFilePath);

	let plugins = config.plugins || [];

	if (!Array.isArray(plugins)) {
		plugins = Object.entries(plugins)
			.filter(([, pluginOptions]) => pluginOptions !== false)
			.map(([pluginName, pluginOptions]) => {
				const plugin = configRequire(pluginName);

				return pluginOptions && Object.keys(pluginOptions).length
					? plugin(pluginOptions)
					: plugin();
			});
	}

	return {
		options: config.options || {},
		plugins,
	};
}

function getSassIncludePaths() {
	const includePaths = [
		resolveDependency('liferay-frontend-common-css'),
		createBourbonFile(),
		path.dirname(require.resolve('compass-mixins')),
	];

	const {sassIncludePaths} = project.options;

	if (sassIncludePaths) {
		const customPaths = sassIncludePaths
			.split(',')
			.map((item) => path.resolve(item));

		print(1, `using custom SASS include paths: ${customPaths.join(', ')}`);

		includePaths.push(...customPaths);
	}

	const themeNodeModules = path.resolve('node_modules');

	if (fs.existsSync(themeNodeModules)) {
		includePaths.push(themeNodeModules);
	}

	if (project.workspaceDir) {
		includePaths.push(path.join(project.workspaceDir, 'node_modules'));
	}

	return includePaths;
}

function getSassOptions(sassOptions, defaults) {
	return Object.assign(defaults, sassOptions);
}
