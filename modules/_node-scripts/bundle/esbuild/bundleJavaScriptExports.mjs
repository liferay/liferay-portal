/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import path from 'path';
import Sonda from 'sonda/esbuild';

import {
	BUILD_MAIN_EXPORTS_PATH,
	BUNDLE_REPORTS_PATH,
} from '../../util/constants.mjs';
import getFlatName from '../../util/getFlatName.mjs';
import getEntryPoint from './getEntryPoint.mjs';
import getExactAliasPlugin from './plugins/getExactAliasPlugin.mjs';
import getExternalsPlugin from './plugins/getExternalsPlugin.mjs';
import getImportBridgesPlugin from './plugins/getImportBridgesPlugin.mjs';
import relocateSourcemap from './relocateSourcemap.mjs';
import runEsbuild from './runEsbuild.mjs';
import writeExportBridge from './writeExportBridge.mjs';

export default async function bundleJavaScriptExports(
	globalImports,
	overridenPackageSymbols,
	projectAlias,
	projectExports,
	projectWebContextPath
) {
	if (!projectExports.length) {
		return;
	}

	await Promise.all(
		projectExports
			.filter((moduleName) => !moduleName.endsWith('.css'))
			.map((moduleName) =>
				bundle(
					globalImports,
					overridenPackageSymbols,
					projectAlias,
					projectWebContextPath,
					moduleName
				)
			)
	);
}

async function bundle(
	globalImports,
	overridenPackageSymbols,
	projectAlias,
	projectWebContextPath,
	moduleName
) {
	const entryPoint = getEntryPoint(moduleName);

	const esbuildConfig = {
		alias: projectAlias,
		bundle: true,
		entryPoints: [entryPoint],
		format: 'esm',
		outdir: BUILD_MAIN_EXPORTS_PATH,
		plugins: [
			getExactAliasPlugin(globalImports, 'exports', [moduleName]),
			getExternalsPlugin(),
			getImportBridgesPlugin(globalImports, overridenPackageSymbols),
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
				filename: path.join(
					BUNDLE_REPORTS_PATH,
					`${entryPoint.out}.html`
				),
				format: 'html',
				gzip: true,
				open: false,
				sources: false,
			}),
			Sonda({
				brotli: false,
				detailed: false,
				enabled: true,
				filename: path.join(
					BUNDLE_REPORTS_PATH,
					`${entryPoint.out}.json`
				),
				format: 'json',
				gzip: true,
				open: false,
			})
		);
	}

	await writeExportBridge(overridenPackageSymbols, moduleName);

	const flatModuleName = getFlatName(moduleName);

	await runEsbuild(esbuildConfig, flatModuleName);

	await relocateSourcemap(
		path.join(
			BUILD_MAIN_EXPORTS_PATH,
			'exports',
			`${flatModuleName}.js.map`
		),
		projectWebContextPath
	);
}
