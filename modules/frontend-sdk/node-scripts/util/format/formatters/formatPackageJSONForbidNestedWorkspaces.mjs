/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MODULES_DIR} from '../../locations.mjs';
import print from '../../print.mjs';

export default async function formatPackageJSONForbidNestedWorkspaces(
	packageJSONs
) {
	let checksPassed = true;

	print(
		1,
		print.subTitle(
			`> Checking 'package.json' files do not declare nested 'workspaces'...\n`
		)
	);

	const offendingFiles = [];

	for (const {dir, pkg, relPath} of packageJSONs) {
		if (dir === MODULES_DIR) {
			continue;
		}

		if (pkg.workspaces) {
			offendingFiles.push(relPath);
		}
	}

	if (offendingFiles.length) {
		print(
			2,
			print.error('ERROR:'),
			"Only 'modules/package.json' may declare",
			print.underline('workspaces'),
			'but it is also declared in:'
		);

		for (const file of offendingFiles.sort()) {
			print(3, file);
		}

		print(2, '');

		checksPassed = false;
	}

	return checksPassed;
}
