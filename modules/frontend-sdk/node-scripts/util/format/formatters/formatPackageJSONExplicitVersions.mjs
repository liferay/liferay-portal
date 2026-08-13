/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import print from '../../print.mjs';
import ALLOWED_PACKAGE_VERSION_DIVERGENCES from './ALLOWED_PACKAGE_VERSION_DIVERGENCES.mjs';
import ALLOWED_RANGE_VERSIONED_PACKAGES from './ALLOWED_RANGE_VERSIONED_PACKAGES.mjs';

const NON_EXPLICIT_VERSION_RE = /[\^~<>|]|\s|^=/;

export default async function formatPackageJSONExplicitVersions(packageJSONs) {
	let checksPassed = true;

	print(
		1,
		print.subTitle(
			`> Checking 'package.json' files use explicit dependency versions...\n`
		)
	);

	const nonExplicitVersions = new Map();

	for (const {pkg, projectRelPath, relPath} of packageJSONs) {
		for (const section of ['dependencies', 'devDependencies']) {
			if (!pkg[section]) {
				continue;
			}

			for (const [name, version] of Object.entries(pkg[section])) {
				const allowedProjects =
					ALLOWED_PACKAGE_VERSION_DIVERGENCES[name];

				if (
					allowedProjects &&
					allowedProjects.includes(projectRelPath)
				) {
					continue;
				}

				if (NON_EXPLICIT_VERSION_RE.test(version)) {
					if (!nonExplicitVersions.has(name)) {
						nonExplicitVersions.set(name, new Map());
					}

					const filesByVersion = nonExplicitVersions.get(name);

					if (!filesByVersion.has(version)) {
						filesByVersion.set(version, new Set());
					}

					filesByVersion.get(version).add(relPath);
				}
			}
		}
	}

	const nonExplicitEntries = [...nonExplicitVersions.entries()].sort((a, b) =>
		a[0].localeCompare(b[0])
	);

	for (const [name, filesByVersion] of nonExplicitEntries) {
		if (ALLOWED_RANGE_VERSIONED_PACKAGES.includes(name)) {
			continue;
		}

		print(
			2,
			print.error('ERROR:'),
			'Dependency',
			print.underline(name),
			'uses non-explicit version(s):'
		);

		const sortedVersions = [...filesByVersion.entries()].sort(
			(a, b) => b[1].size - a[1].size
		);

		for (const [version, files] of sortedVersions) {
			print(3, `${version} in:`);

			for (const file of [...files].sort()) {
				print(4, file);
			}
		}

		print(2, '');

		checksPassed = false;
	}

	return checksPassed;
}
