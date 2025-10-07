/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs/promises';
import path from 'path';
import Sonda from 'sonda/esbuild';

import {
	BUILD_LANGUAGE_JSON_PATH,
	BUILD_MAIN_EXPORTS_PATH,
	BUNDLE_REPORTS_PATH,
} from '../../util/constants.mjs';
import objectSF from '../../util/objectSF.mjs';
import getCssLoaderPlugin from './plugins/getCssLoaderPlugin.mjs';
import getExactAliasPlugin from './plugins/getExactAliasPlugin.mjs';
import getExternalsPlugin from './plugins/getExternalsPlugin.mjs';
import getImportBridgesPlugin from './plugins/getImportBridgesPlugin.mjs';
import getLiferayLanguageGetPlugin from './plugins/getLiferayLanguageGetPlugin.mjs';
import getRuntimeLinkerPlugin from './plugins/getRuntimeLinkerPlugin.mjs';
import getScssLoaderPlugin from './plugins/getScssLoaderPlugin.mjs';
import relocateSourcemap from './relocateSourcemap.mjs';
import runEsbuild from './runEsbuild.mjs';

export default async function bundleJavaScriptMain(
	globalImports,
	languageJSON,
	overridenPackageSymbols,
	projectAlias,
	projectDescription,
	projectEntryPoints,
	projectWebContextPath
) {
	const {main: mainEntryPoint, submodules = {}} = projectEntryPoints;

	if (!mainEntryPoint) {
		return;
	}

	const esbuildConfig = {
		alias: projectAlias,
		bundle: true,
		entryPoints: [
			...Object.keys(submodules).map((submoduleName) => ({
				in: path.resolve(submodules[submoduleName]),
				out: submoduleName,
			})),
			{in: path.resolve(mainEntryPoint), out: 'index'},
		],
		format: 'esm',
		loader: {
			'.js': 'jsx',
			'.png': 'empty',
			'.scss': 'css',
		},
		outdir: BUILD_MAIN_EXPORTS_PATH,
		plugins: [
			getCssLoaderPlugin(globalImports, 'main'),
			getExactAliasPlugin(globalImports, 'main'),
			getExternalsPlugin(),
			getImportBridgesPlugin(globalImports, overridenPackageSymbols),
			getLiferayLanguageGetPlugin(projectWebContextPath, languageJSON),
			getRuntimeLinkerPlugin(
				mainEntryPoint,
				projectDescription,
				submodules
			),
			getScssLoaderPlugin(projectWebContextPath),
		],
		sourcemap: true,
		target: ['es2022'],
	};

	if (process.env.CREATE_BUNDLE_REPORTS) {
		esbuildConfig.plugins.push(
			Sonda({
				brotli: false,
				detailed: false,
				enabled: true,
				filename: path.join(BUNDLE_REPORTS_PATH, `index.js.html`),
				format: 'html',
				gzip: true,
				open: false,
				sources: false,
			}),
			Sonda({
				brotli: false,
				detailed: false,
				enabled: true,
				filename: path.join(BUNDLE_REPORTS_PATH, `index.js.json`),
				format: 'json',
				gzip: true,
				open: false,
			})
		);
	}

	await runEsbuild(esbuildConfig, 'main');

	await Promise.all([
		relocateSourcemap(
			path.join(BUILD_MAIN_EXPORTS_PATH, 'index.js.map'),
			projectWebContextPath
		),
		...Object.keys(submodules).map((submodule) =>
			relocateSourcemap(
				path.join(BUILD_MAIN_EXPORTS_PATH, `${submodule}.js.map`),
				projectWebContextPath
			)
		),
		writeLanguageJSON(languageJSON),
	]);
}

async function writeLanguageJSON(languageJSON) {
	if (!languageJSON.keys.length) {
		return;
	}

	// Dedupe language keys before writing them

	languageJSON.keys = [...new Set(languageJSON.keys)];

	await fs.writeFile(
		BUILD_LANGUAGE_JSON_PATH,
		objectSF(languageJSON),
		'utf-8'
	);
}
