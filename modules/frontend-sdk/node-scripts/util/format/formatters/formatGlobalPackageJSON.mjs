/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MODULES_DIR} from '../../locations.mjs';
import print from '../../print.mjs';
import FORBIDDEN_ROOT_PACKAGE_JSON_SECTIONS from './FORBIDDEN_ROOT_PACKAGE_JSON_SECTIONS.mjs';

export default async function formatGlobalPackageJSON(packageJSONs) {
	let checksPassed = true;

	print(
		1,
		print.subTitle(
			`> Checking 'modules/package.json' does not include forbidden sections...\n`
		)
	);

	const rootPkg = packageJSONs.find(({dir}) => dir === MODULES_DIR).pkg;

	for (const [section, hint] of Object.entries(
		FORBIDDEN_ROOT_PACKAGE_JSON_SECTIONS
	)) {
		if (!rootPkg[section]) {
			continue;
		}

		print(
			2,
			print.error('ERROR:'),
			`'modules/package.json' must not declare '${section}' section${
				hint ? ` (${hint})` : ''
			}\n`
		);

		checksPassed = false;
	}

	return checksPassed;
}
