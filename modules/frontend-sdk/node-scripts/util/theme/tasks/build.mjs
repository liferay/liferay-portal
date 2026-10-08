/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Buffer} from 'buffer';
import fastGlob from 'fast-glob';
import fs from 'fs';
import path from 'path';
import {minify} from 'terser';

import print from '../../print.mjs';
import getBaseThemeDependencies from '../getBaseThemeDependencies.mjs';
import * as lookAndFeelUtil from '../lookAndFeelUtil.mjs';
import project from '../project.mjs';
import r2 from '../r2.mjs';
import {initSourceMap, writeSourceMap} from '../sourceMaps.mjs';
import {getLanguageProperties} from '../themeUtil.mjs';
import buildCompileCss from './buildCompileCss.mjs';
import buildHashify from './buildHashify.mjs';
import pluginVersion from './pluginVersion.mjs';
import pluginWar from './pluginWar.mjs';

const UTF8_BOM = Buffer.from([0xef, 0xbb, 0xbf]);

/**
 * build
 *
 * @param {object} options
 * @param {string} [options.cssCommonPath]
 * path to use for `liferay-frontend-common-css`
 * @param {string} [options.sassIncludePaths]
 * comma separated list of extra Sass include paths
 * @param {string} [options.styledPath]
 * path to use for `liferay-frontend-theme-styled`
 * @param {string} [options.unstyledPath]
 * path to use for `liferay-frontend-theme-unstyled`
 */
export default async function build(options = {}) {
	project.init(options);

	await runTask('build:clean', buildClean);
	await runTask('build:base', buildBase);
	await runTask('build:src', buildSrc);
	await runTask('build:web-inf', buildWebInf);
	await runTask('build:liferay-look-and-feel', buildLiferayLookAndFeel);
	await runTask('build:hook', buildHook);
	await runTask('build:rename-css-dir', buildRenameCssDir);
	await runTask('build:compile-css', buildCompileCss);
	await runTask('build:fix-url-functions', buildFixUrlFunctions);
	await runTask('build:move-compiled-css', buildMoveCompiledCss);
	await runTask('build:remove-old-css-dir', buildRemoveOldCssDir);
	await runTask('build:fix-at-directives', buildFixAtDirectives);
	await runTask('build:r2', buildR2);
	await runTask('build:minify:js', buildMinifyJs);
	await runTask('build:war', buildWar);
}

async function buildClean() {
	fs.rmSync(project.options.pathBuild, {force: true, recursive: true});
}

async function buildBase() {
	const {pathBuild} = project.options;

	for (const {cwd, glob} of getBaseThemeDependencies()) {
		await copyGlob(cwd, glob, pathBuild);
	}
}

async function buildSrc() {
	const {pathBuild, pathSrc} = project.options;

	await copyGlob(pathSrc, '**/*', pathBuild);
}

async function buildWebInf() {
	const {pathBuild} = project.options;

	await copyGlob(
		path.join(pathBuild, 'WEB-INF', 'src'),
		'**/*',
		path.join(pathBuild, 'WEB-INF', 'classes')
	);
}

async function buildLiferayLookAndFeel() {
	const themePath = project.dir;

	const lookAndFeelJSON = await lookAndFeelUtil.getLookAndFeelJSON(themePath);

	if (!lookAndFeelJSON) {
		return;
	}

	const themeName =
		lookAndFeelUtil.getNameFromPluginPackageProperties(themePath);

	lookAndFeelUtil.correctJSONIdentifiers(lookAndFeelJSON, themeName);

	let doctypeElement = lookAndFeelUtil.getLookAndFeelDoctype(themePath);

	if (!doctypeElement) {
		doctypeElement = lookAndFeelUtil.getLookAndFeelDoctypeByVersion(
			project.themeConfig.version
		);
	}

	const xml = lookAndFeelUtil.buildXML(lookAndFeelJSON, doctypeElement);

	fs.writeFileSync(
		path.join(
			themePath,
			project.options.pathBuild,
			'WEB-INF',
			'liferay-look-and-feel.xml'
		),
		xml
	);
}

async function buildHook() {
	const {pathBuild, pathSrc} = project.options;

	const liferayHookXmlPath = path.join(
		pathBuild,
		'WEB-INF',
		'liferay-hook.xml'
	);

	if (!fs.existsSync(liferayHookXmlPath)) {
		return;
	}

	const languageProperties = getLanguageProperties(pathSrc);

	replaceInFile(liferayHookXmlPath, [
		{
			match: /<language-properties>content\/Language\*\.properties<\/language-properties>/,
			replacement: () => languageProperties.join('\n\t'),
		},
	]);
}

async function buildRenameCssDir() {
	const {pathBuild} = project.options;

	fs.renameSync(path.join(pathBuild, 'css'), path.join(pathBuild, '_css'));
}

// Temp fix for libSass compilation issue with empty url() functions

async function buildFixUrlFunctions() {
	const cssBuild = path.join(project.options.pathBuild, '_css');

	for (const file of await fastGlob('**/*.css', {cwd: cssBuild})) {
		replaceInFile(path.join(cssBuild, file), [
			{
				match: /url\(url\(\)/g,
				replacement: 'url()',
			},
		]);
	}
}

async function buildMoveCompiledCss() {
	const {pathBuild} = project.options;

	await copyGlob(
		path.join(pathBuild, '_css'),
		'**/*',
		path.join(pathBuild, 'css')
	);
}

async function buildRemoveOldCssDir() {
	fs.rmSync(path.join(project.options.pathBuild, '_css'), {
		force: true,
		recursive: true,
	});
}

async function buildFixAtDirectives() {
	const cssDir = path.join(project.options.pathBuild, 'css');

	for (const file of await fastGlob('*.css', {cwd: cssDir})) {
		replaceInFile(path.join(cssDir, file), getFixAtDirectivesPatterns());
	}
}

async function buildR2() {
	const cssDir = path.join(project.options.pathBuild, 'css');

	let hasCssParseErrors = false;

	for (const file of (await fastGlob('*.css', {cwd: cssDir})).sort()) {
		const filePath = path.join(cssDir, file);

		let css = fs.readFileSync(filePath, 'utf8');

		try {
			css = r2(css);
		}
		catch (error) {
			print(
				1,
				print.error('ERROR:'),
				`${filePath}: ${error.reason || error.message}`
			);

			hasCssParseErrors = true;
		}

		fs.writeFileSync(
			path.join(cssDir, path.basename(file, '.css') + '_rtl.css'),
			css
		);
	}

	if (hasCssParseErrors) {
		print(
			1,
			print.warning('WARNING:'),
			'Some CSS files had incorrect SASS or CSS code. The build will',
			'finish but the RTL CSS will not be generated correctly.\n\n',
			'Please address the issues in your source code to see this error',
			'go away.'
		);
	}
}

async function buildMinifyJs() {
	if (process.env.NODE_ENV !== 'production') {
		return;
	}

	const {pathBuild, terser = {}} = project.options;

	for (const file of await fastGlob('**/*.js', {cwd: pathBuild})) {
		const filePath = path.join(pathBuild, file);

		const code = fs.readFileSync(filePath, 'utf8').replace(/^\uFEFF/, '');

		const sourceMap = initSourceMap(file, code);

		const result = await minify(
			{[sourceMap.file]: code},
			{
				...terser,
				sourceMap: {
					...terser.sourceMap,
					filename: sourceMap.file,
				},
			}
		);

		fs.writeFileSync(
			filePath,
			writeSourceMap(
				pathBuild,
				filePath,
				result.code,
				JSON.parse(result.map)
			)
		);
	}
}

async function buildWar() {
	await runTask('before:build:war', buildHashify);
	await runTask('plugin:version', pluginVersion);
	await runTask('plugin:war', pluginWar);
}

/**
 * Copy all files matching a glob in a directory to another directory,
 * preserving their relative paths and removing UTF-8 BOMs, the way
 * `gulp.src().pipe(gulp.dest())` does.
 */
async function copyGlob(cwd, glob, destDir) {
	const entries = await fastGlob(glob, {
		cwd,
		markDirectories: true,
		onlyFiles: false,
	});

	for (const entry of entries.sort()) {
		const destPath = path.join(destDir, entry);

		if (entry.endsWith('/')) {
			fs.mkdirSync(destPath, {recursive: true});
		}
		else {
			fs.mkdirSync(path.dirname(destPath), {recursive: true});

			const content = fs.readFileSync(path.join(cwd, entry));

			fs.writeFileSync(
				destPath,
				hasBOM(content) ? content.subarray(UTF8_BOM.length) : content
			);
		}
	}
}

function getFixAtDirectivesPatterns() {
	const keyframeRulesReplace = function (match, m1, m2) {
		return (
			m1
				.split(',')
				.map((item) => item.replace(/.*?(from|to|[0-9.]+%)/g, '$1'))
				.join(',') + m2
		);
	};

	return [
		{
			match: /(@font-face|@page|@-ms-viewport)\s*({\n\s*)(.*)\s*({)/g,
			replacement(match, m1, m2, m3, m4) {
				return m3 + m2 + m1 + ' ' + m4;
			},
		},
		{
			match: /(@-ms-keyframes.*{)([\s\S]+?)(}\s})/g,
			replacement(match, m1, m2, m3) {
				m2 = m2.replace(/(.+?)(\s?{)/g, keyframeRulesReplace);

				return m1 + m2 + m3;
			},
		},
		{
			match: /@import\s+url\s*\(\s*['"]?(.+\.css)['"]?/g,
			replacement(match, m1) {
				return '@import url(' + m1 + '?t=' + Date.now();
			},
		},
	];
}

function hasBOM(content) {
	return content.subarray(0, UTF8_BOM.length).equals(UTF8_BOM);
}

function replaceInFile(filePath, patterns) {
	let content = fs.readFileSync(filePath, 'utf8');

	for (const {match, replacement} of patterns) {
		content = content.replace(match, replacement);
	}

	fs.writeFileSync(filePath, content);
}

async function runTask(name, task) {
	const start = Date.now();

	print(1, `Starting '${name}'...`);

	await task();

	print(1, `Finished '${name}' after ${Date.now() - start} ms`);
}
