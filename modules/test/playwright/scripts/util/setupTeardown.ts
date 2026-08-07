/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable no-console */

import {existsSync, readFileSync, readdirSync} from 'fs';
import {basename, join, resolve} from 'path';

import tasks from './tasks';

function setupTeardown(setup: boolean, projectName: string) {
	if (!projectName) {
		console.error(
			`❌ Please provide the name of a project to ${
				setup ? 'setup' : 'teardown'
			}. For example: 'portal-language-override-web/client-extension'.`
		);

		process.exit(1);
	}

	const testDir = resolve(__dirname, '..', '..', 'tests', projectName);

	if (!existsSync(testDir)) {
		console.error(`❌ Invalid project name: ${projectName}`);

		process.exit(1);
	}

	const {
		copyFile,
		deployClientExtension,
		deployOSGiModule,
		tweakPortalExtProperties,
	} = setup ? tasks.setup : tasks.teardown;
	const labelSettingTearing = setup ? 'Setting up' : 'Tearing down';
	const labelSetTear = setup ? 'Setup' : 'Teardown';

	const portalSourceDir = resolve(__dirname, '..', '..', '..', '..', '..');
	const bundlesDir = resolve(portalSourceDir, '..', 'bundles');

	console.log(`💡 ${labelSettingTearing} local DXP for tests...`);

	// Deploy base and test projects

	[resolve(__dirname, '..', '..'), testDir].forEach((projectDir) => {
		const projectName = basename(projectDir);

		console.log(`⚙️ ${labelSettingTearing} project ${projectName}:`);

		const envDir = join(projectDir, 'env');
		const portalExtPropertiesFile = join(envDir, 'portal-ext.properties');

		copyFile(
			bundlesDir,
			portalExtPropertiesFile,
			`portal-ext.${projectName}.properties`
		);

		const deployDir = join(envDir, 'deploy');

		if (existsSync(deployDir)) {
			const fileNames = readdirSync(deployDir);

			fileNames.forEach((fileName) =>
				copyFile(
					bundlesDir,
					join(deployDir, fileName),
					join('deploy', fileName)
				)
			);
		}

		const osgiModulesListFile = join(envDir, 'osgi-modules.list');

		if (existsSync(osgiModulesListFile)) {
			const projectDirs = parseListFile(osgiModulesListFile);

			projectDirs.forEach((projectDir) =>
				deployOSGiModule(portalSourceDir, projectDir)
			);
		}

		const clientExtensionsListFile = join(envDir, 'client-extensions.list');

		if (existsSync(clientExtensionsListFile)) {
			const projectNames = parseListFile(clientExtensionsListFile);

			projectNames.forEach((projectName) =>
				deployClientExtension(portalSourceDir, bundlesDir, projectName)
			);
		}
	});

	tweakPortalExtProperties(bundlesDir, ['playwright', basename(testDir)]);

	console.log(`🎉 ${labelSetTear} complete!`);
}

function parseListFile(listFile: string): string[] {
	return readFileSync(listFile, 'utf-8')
		.split('\n')
		.map((line) => line.trim())
		.filter((line) => !!line.length);
}

export default setupTeardown;
