/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MODULES_DIR} from '../../locations.mjs';
import print from '../../print.mjs';
import ALLOWED_PACKAGE_VERSION_DIVERGENCES from './ALLOWED_PACKAGE_VERSION_DIVERGENCES.mjs';
import ALLOWED_ROOT_PACKAGE_JSON_DEPENDENCIES from './ALLOWED_ROOT_PACKAGE_JSON_DEPENDENCIES.mjs';

export default async function formatGlobalPackageJSONDependencies(
	packageJSONs
) {
	let checksPassed = true;

	print(
		1,
		print.subTitle(
			`> Checking 'modules/package.json' only declares allowed dependencies...\n`
		)
	);

	const rootPkg = packageJSONs.find(({dir}) => dir === MODULES_DIR).pkg;

	const unneeded = [];

	for (const name of Object.keys(rootPkg.devDependencies || {})) {
		if (
			name.startsWith('@types/') ||
			name in ALLOWED_PACKAGE_VERSION_DIVERGENCES ||
			ALLOWED_ROOT_PACKAGE_JSON_DEPENDENCIES.includes(name)
		) {
			continue;
		}

		unneeded.push(name);
	}

	if (unneeded.length) {
		print(
			2,
			print.error('ERROR:'),
			"'modules/package.json' declares non allowed dependencies:"
		);

		for (const name of unneeded.sort()) {
			print(3, name);
		}

		print(2, '');

		checksPassed = false;
	}

	return checksPassed;
}
