/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fg from 'fast-glob';
import fs from 'fs/promises';
import path from 'path';
import resolve from 'resolve';
import rtlcss from 'rtlcss';

import {
	BUILD_RESOURCES_PATH,
	BUILD_SASS_CACHE_PATH,
	SRC_PATH,
} from '../../util/constants.mjs';
import projectScopeRequire from '../../util/projectScopeRequire.mjs';
import calculateFileHash from '../util/calculateFileHash.mjs';
import runSass from './runSass.mjs';

const CSS_IMPORT_REGEX = /@import\s+url\s*\(\s*['"]?(.+?\.css)/g;
const SASS_EXCLUDE = [
	'**/_diffs/**',
	'**/_sass_cache_*/**',
	'**/_styled/**',
	'**/_unstyled/**',
	'**/.sass-cache*/**',
	'**/build/**',
	'**/classes/**',
	'**/css/aui/**',
	'**/css/clay/**',
	'**/tmp/**',
];

export default async function processSassFiles() {
	const {entryFiles, partials} = await collectSassFiles();

	if (!entryFiles.length) {
		return;
	}

	if (
		!(await isAnyFileModified(
			[...entryFiles, ...partials],
			SRC_PATH,
			BUILD_RESOURCES_PATH
		))
	) {
		console.log(
			'⚠️ No .scss files were modified: skipping Sass compilation'
		);

		return;
	}

	await Promise.all([
		...entryFiles.map(copySassFile),
		...partials.map(copySassFile),
	]);

	const includePaths = getIncludePaths();

	const timestamp = Date.now();

	await Promise.all(
		entryFiles.map(async (entryFile) => {
			const start = performance.now();

			await processSassFile(entryFile, includePaths, timestamp);

			const lapse = performance.now() - start;

			console.log(
				`⌛ Sass for ${entryFile} took: ${(lapse / 1000).toFixed(3)} s`
			);
		})
	);
}

/**
 * Append `t=<timestamp>` query string parameters to all `@import`ed files inside a CSS file.
 *
 * This is done for historical reasons even though the technique is completely flawed and creates a
 * lot of problems with HTTP caching.
 */
function appendTimestamps(css, timestamp) {
	const matches = css.match(CSS_IMPORT_REGEX) || [];

	for (const match of matches) {
		css = css.replace(match, `${match}?t=${timestamp}`);
	}

	return css;
}

async function collectSassFiles() {
	const files = await fg(['**/*.scss'], {
		absolute: true,
		cwd: SRC_PATH,
		ignore: SASS_EXCLUDE,
	});

	return files.reduce(
		(acc, file) => {
			if (path.basename(file).match(/^_.+\.scss$/)) {
				acc.partials.push(file);
			}
			else {
				acc.entryFiles.push(file);
			}

			return acc;
		},
		{entryFiles: [], partials: []}
	);
}

async function copySassFile(filePath) {
	const destFilePath = path.join(
		BUILD_RESOURCES_PATH,
		path.relative(SRC_PATH, filePath)
	);

	await fs.mkdir(path.dirname(destFilePath), {recursive: true});

	await fs.copyFile(filePath, destFilePath);

	const stats = await fs.stat(filePath);

	await fs.utimes(destFilePath, stats.atime, stats.mtime);
}

function getIncludePaths() {
	const includePaths = [
		path.dirname(resolve.sync('bourbon', {basedir: '.'})),
		path.dirname(
			resolve.sync('liferay-frontend-common-css', {basedir: '.'})
		),
	];

	try {
		includePaths.push(...projectScopeRequire('@clayui/css').includePaths);
	}
	catch (error) {
		if (error.code !== 'MODULE_NOT_FOUND') {
			throw error;
		}
	}

	return includePaths;
}

/**
 * @param files
 * Files to check designated by project relative path, for example:
 * src/main/resources/META-INF/resources/js/components/color_picker/ColorPicker.scss
 *
 * @param baseDir
 * The base directory of the files so that it can be used to find the path relative to `outDir`
 */
async function isAnyFileModified(files, baseDir, outDir) {
	const modifieds = await Promise.all(
		files.map(async (file) => {
			const relativeFilePath = path.relative(baseDir, file);
			const outFile = path.join(outDir, relativeFilePath);

			let outStat;

			try {
				outStat = await fs.stat(outFile);
			}
			catch (error) {
				if (error.code !== 'ENOENT') {
					throw error;
				}

				return true;
			}

			const stat = await fs.stat(file);

			return outStat.mtime.getTime() !== stat.mtime.getTime();
		})
	);

	return modifieds.some((modified) => modified);
}

async function processSassFile(filePath, includePaths, timestamp) {

	// Compute paths

	const relFilePath = path.relative(SRC_PATH, filePath);

	const outFilePath = path.join(
		BUILD_SASS_CACHE_PATH,
		relFilePath.replace(/\.scss$/, '.css')
	);

	const {dir, ext, name} = path.parse(outFilePath);

	const outRtlFilePath = path.join(dir, `${name}_rtl${ext}`);

	// Compute CSS

	const {css, map} = await runSass(filePath, includePaths, outFilePath);

	// Remove leftovers

	await safeUnlink(outFilePath);
	await safeUnlink(`${outFilePath}.map`);

	// Apply timestamps to CSS

	const timestampedCss = appendTimestamps(css, timestamp);

	// Apply RTL translation to CSS

	const rtlTimestampedCss = rtlcss.process(timestampedCss);

	// Write stuff

	await fs.mkdir(path.dirname(outFilePath), {recursive: true});

	const hash = await calculateFileHash(timestampedCss);

	const finalExt = `.(${hash}).css`;

	const finalOutFilePath = outFilePath.replace(/\.css$/, finalExt);
	const finalOutRtlFilePath = outRtlFilePath.replace(/\.css$/, finalExt);

	await Promise.all([
		fs.writeFile(finalOutFilePath, timestampedCss, 'utf-8'),
		fs.writeFile(`${finalOutFilePath}.map`, map, 'utf-8'),
		fs.writeFile(finalOutRtlFilePath, rtlTimestampedCss, 'utf-8'),
	]);
}

async function safeUnlink(filePath) {
	try {
		await fs.unlink(filePath);
	}
	catch (error) {
		if (error.code !== 'ENOENT') {
			throw error;
		}
	}
}
