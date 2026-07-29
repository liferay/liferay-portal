/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs/promises';
import path from 'path';
import Sonda from 'sonda/esbuild';

import getFlatName from '../../util/getFlatName.mjs';
import {BUILD_MAIN_EXPORTS_PATH, BUNDLE_REPORTS_PATH} from '../locations.mjs';
import getLinkerPlugin from './plugins/getLinkerPlugin.mjs';
import checkResolutionAgreement from './util/checkResolutionAgreement.mjs';
import getExportBridgeJavaScript from './util/getExportBridgeJavaScript.mjs';
import getExportBridgePath from './util/getExportBridgePath.mjs';
import relocateSourcemap from './util/relocateSourcemap.mjs';
import runEsbuild from './util/runEsbuild.mjs';

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

	// Write export bridge

	const exportBridgePath = getExportBridgePath(moduleName);

	await fs.mkdir(path.dirname(exportBridgePath), {recursive: true});
	await fs.writeFile(
		exportBridgePath,
		await getExportBridgeJavaScript(overridenPackageSymbols, moduleName),
		'utf-8'
	);

	// Run esbuild

	const entryPoint = {
		in: getExportBridgePath(moduleName),
		out: `exports/${getFlatName(moduleName)}`,
	};

	const esbuildConfig = {
		alias: projectAlias,
		bundle: true,
		entryNames: '[dir]/[name].([hash])',
		entryPoints: [entryPoint],
		format: 'esm',
		loader: {
			'.ttf': 'file',
		},
		outdir: BUILD_MAIN_EXPORTS_PATH,
		plugins: [
			getLinkerPlugin(
				globalImports,
				overridenPackageSymbols,
				projectWebContextPath,
				moduleName
			),
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

	const {metafile} = await runEsbuild(esbuildConfig, getFlatName(moduleName));

	await checkResolutionAgreement(
		overridenPackageSymbols,
		moduleName,
		metafile
	);

	const {outputs} = metafile;

	await Promise.all([
		...Object.keys(outputs).map(async (output) => {
			if (output.endsWith('.map')) {
				return relocateSourcemap(
					path.join(output),
					projectWebContextPath
				);
			}
		}),
	]);
}
