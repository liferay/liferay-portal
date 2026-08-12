/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import merge from 'deepmerge';
import {$} from 'execa';
import fs from 'fs/promises';
import path from 'path';

import fileExists from '../fileExists.mjs';
import onExit from '../onExit.mjs';
import getJestConfig from './getJestConfig.js';
import getJestModuleNameMapper from './getJestModuleNameMapper.mjs';
import getUserConfig from './getUserConfig.mjs';

const CONFIG_NAME = 'TEMP_jest.config.json';
const FORCE_DEBUG_FLAG = '--force-debug';
const SILENT_FLAG = '--silent';

export default async function runJest({
	cliFlags = [],
	projectPath,
	execaConfig = {},
}) {
	const CONFIG_PATH = path.join(projectPath, CONFIG_NAME);

	const hasForceDebug = cliFlags.includes(FORCE_DEBUG_FLAG);

	if (!hasForceDebug) {
		cliFlags = [...cliFlags, SILENT_FLAG];
	}
	else {
		cliFlags = cliFlags.filter((flag) => flag !== FORCE_DEBUG_FLAG);
	}

	let result = false;

	try {
		const config = {
			cwd: projectPath,
			env: {
				...process.env,
				...execaConfig.env,
				NODE_ENV: 'test',
			},
			...execaConfig,
		};

		let userConfig = await getUserConfig('jest', {cwd: projectPath});

		userConfig = JSON.parse(
			JSON.stringify(userConfig).replace('<rootDir>', projectPath)
		);

		await fs.writeFile(
			CONFIG_PATH,
			JSON.stringify(
				merge.all([
					getJestConfig({rootDir: projectPath}),
					{
						moduleNameMapper: await getJestModuleNameMapper({
							cwd: projectPath,
						}),
					},
					userConfig,
				]),
				null,
				4
			)
		);

		onExit(() => fs.unlink(CONFIG_PATH));

		const childProcess = $(
			config
		)`jest --passWithNoTests --projects ${projectPath} --config ${CONFIG_PATH} ${cliFlags}`;

		result = await childProcess;
	}
	catch (error) {
		result = {
			failed: true,
			all: error.toString(),
		};
	}
	finally {
		if (await fileExists(CONFIG_PATH)) {
			await fs.unlink(CONFIG_PATH);
		}
	}

	return result;
}
