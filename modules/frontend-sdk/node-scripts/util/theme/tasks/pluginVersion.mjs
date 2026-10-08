/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs';
import path from 'path';

import print from '../../print.mjs';
import project from '../project.mjs';

const REGEX_MODULE_VERSION = /module-version=([0-9.]+)/;

/**
 * plugin:version
 */
export default function pluginVersion() {
	const npmPackageVersion = project.pkgJson.version;

	const pluginPackagePropertiesPath = path.join(
		project.options.rootDir,
		'WEB-INF',
		'liferay-plugin-package.properties'
	);

	let result = fs.readFileSync(pluginPackagePropertiesPath, 'utf8');

	const moduleVersion = result.match(REGEX_MODULE_VERSION);

	if (moduleVersion && moduleVersion[1] !== npmPackageVersion) {
		result = result.replace(
			REGEX_MODULE_VERSION,
			'module-version=' + npmPackageVersion
		);

		print(
			1,
			print.warning('WARNING:'),
			'the value of module-version in liferay-plugin-package.properties',
			'does not match version in package.json. Using version from',
			'package.json'
		);
	}
	else if (!moduleVersion) {
		result += '\nmodule-version=' + npmPackageVersion;
	}

	fs.writeFileSync(pluginPackagePropertiesPath, result);
}
