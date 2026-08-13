/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MODULES_DIR} from '../../locations.mjs';
import print from '../../print.mjs';
import ALLOWED_PACKAGE_VERSION_DIVERGENCES from './ALLOWED_PACKAGE_VERSION_DIVERGENCES.mjs';

export default async function formatPackageJSONTypesLocation(packageJSONs) {
	let checksPassed = true;

	print(
		1,
		print.subTitle(
			`> Checking 'package.json' files do not depend on '@types/*' dependencies...\n`
		)
	);

	const typesOutsideRoot = new Map();

	for (const {dir, pkg, projectRelPath, relPath} of packageJSONs) {
		if (dir === MODULES_DIR) {
			continue;
		}

		for (const section of ['dependencies', 'devDependencies']) {
			if (!pkg[section]) {
				continue;
			}

			for (const name of Object.keys(pkg[section])) {
				if (
					name.startsWith('@types/') &&
					!(ALLOWED_PACKAGE_VERSION_DIVERGENCES[name] || []).includes(
						projectRelPath
					)
				) {
					if (!typesOutsideRoot.has(name)) {
						typesOutsideRoot.set(name, new Set());
					}

					typesOutsideRoot.get(name).add(relPath);
				}
			}
		}
	}

	const typesEntries = [...typesOutsideRoot.entries()].sort((a, b) =>
		a[0].localeCompare(b[0])
	);

	for (const [name, files] of typesEntries) {
		print(
			2,
			print.error('ERROR:'),
			'Dependency',
			print.underline(name),
			"must only be declared in 'modules/package.json':"
		);

		for (const file of [...files].sort()) {
			print(3, file);
		}

		print(2, '');

		checksPassed = false;
	}

	return checksPassed;
}
