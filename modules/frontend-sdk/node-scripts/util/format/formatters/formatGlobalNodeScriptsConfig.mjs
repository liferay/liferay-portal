/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs/promises';
import path from 'path';

import createGlobalConfig from '../../createGlobalConfig.mjs';
import {MODULES_DIR} from '../../locations.mjs';
import print from '../../print.mjs';
import projectScopeRequire from '../../projectScopeRequire.mjs';
import formatSourceFile from '../util/formatSourceFile.mjs';

export default async function formatGlobalNodeScriptsConfig(check) {
	let checksPassed = true;

	print(
		1,
		print.subTitle(
			`> ${check ? 'Checking' : 'Formatting'} global 'node-scripts.config.js' file...\n`
		)
	);

	const globalConfig = await projectScopeRequire(
		'./node-scripts.config.js',
		MODULES_DIR
	);

	const {config: newGlobalConfig, hash: newHash} = await createGlobalConfig();

	if (globalConfig.hash !== newHash) {
		if (check) {
			print(
				2,
				print.error('ERROR:'),
				'Global',
				print.underline(`'node-scripts.config.js'`),
				'file is outdated\n'
			);

			checksPassed = false;
		}
		else {
			const globalConfigPath = path.join(
				MODULES_DIR,
				'node-scripts.config.js'
			);

			await fs.writeFile(globalConfigPath, newGlobalConfig, 'utf-8');

			// createGlobalConfig() emits the file in its own layout, so format
			// it here instead of leaving it to formatSourceFiles(): that one
			// only visits the files it was given, and the regeneration may
			// well be the reason this one needs formatting at all.
			//
			// Anything but success means the generated content is not valid
			// JavaScript or breaks a rule nothing can fix automatically, which
			// is a defect in this tool rather than in the formatted branch.

			if (
				!(await formatSourceFile(
					globalConfigPath,
					{eslint: false, prettier: false},
					{check: false}
				))
			) {
				throw new Error(
					`Unable to format ${globalConfigPath}, so createGlobalConfig() produced invalid content`
				);
			}

			print(
				2,
				print.success('SUCCESS:'),
				'Updated global',
				print.underline(`'node-scripts.config.js'`),
				'file\n'
			);
		}
	}

	return checksPassed;
}
