/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MODULES_DIR} from '../../locations.mjs';
import print from '../../print.mjs';
import ALLOWED_PACKAGE_VERSION_DIVERGENCES from './ALLOWED_PACKAGE_VERSION_DIVERGENCES.mjs';

export default async function formatPackageJSONVersionAlignment(packageJSONs) {
	let checksPassed = true;

	print(
		1,
		print.subTitle(
			`> Checking 'package.json' files dependency versions are all aligned...\n`
		)
	);

	const rootPkg = packageJSONs.find(({dir}) => dir === MODULES_DIR).pkg;

	const rootDependencies = {
		...rootPkg.dependencies,
		...rootPkg.devDependencies,
	};

	for (const name of Object.keys(ALLOWED_PACKAGE_VERSION_DIVERGENCES)) {
		if (!(name in rootDependencies)) {
			print(
				2,
				print.error('ERROR:'),
				'Dependency',
				print.underline(name),
				"is allowed to diverge but has no entry in 'modules/package.json'"
			);
			print(2, '');

			checksPassed = false;
		}
	}

	const versionsByName = new Map();

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

				if (!versionsByName.has(name)) {
					versionsByName.set(name, new Map());
				}

				const filesByVersion = versionsByName.get(name);

				if (!filesByVersion.has(version)) {
					filesByVersion.set(version, new Set());
				}

				filesByVersion.get(version).add(relPath);
			}
		}
	}

	const violations = [];

	for (const [name, filesByVersion] of versionsByName) {
		if (filesByVersion.size > 1) {
			violations.push([name, filesByVersion]);
		}
	}

	violations.sort((a, b) => a[0].localeCompare(b[0]));

	for (const [name, filesByVersion] of violations) {
		print(
			2,
			print.error('ERROR:'),
			'Dependency',
			print.underline(name),
			'has misaligned versions across modules:'
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
