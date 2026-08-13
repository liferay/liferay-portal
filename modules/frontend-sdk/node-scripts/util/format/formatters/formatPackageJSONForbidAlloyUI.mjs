/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import print from '../../print.mjs';
import ALLOWED_AUI_DEPENDENT_PROJECTS from './ALLOWED_AUI_DEPENDENT_PROJECTS.mjs';

const ALLOY_UI = 'alloy-ui';

export default async function formatPackageJSONForbidAlloyUI(packageJSONs) {
	let checksPassed = true;

	print(
		1,
		print.subTitle(
			`> Checking 'package.json' files do not depend on 'alloy-ui'...\n`
		)
	);

	const offendingFiles = [];

	for (const {pkg, projectRelPath, relPath} of packageJSONs) {
		if (ALLOWED_AUI_DEPENDENT_PROJECTS.includes(projectRelPath)) {
			continue;
		}

		const dependsOnAlloyUI = ['dependencies', 'devDependencies'].some(
			(section) => pkg[section] && ALLOY_UI in pkg[section]
		);

		if (dependsOnAlloyUI) {
			offendingFiles.push(relPath);
		}
	}

	if (offendingFiles.length) {
		print(
			2,
			print.error('ERROR:'),
			'Dependency',
			print.underline(ALLOY_UI),
			'must not be declared in:'
		);

		for (const file of offendingFiles.sort()) {
			print(3, file);
		}

		print(2, '');

		checksPassed = false;
	}

	return checksPassed;
}
