/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs';
import path from 'path';
import resolve from 'resolve';

import {GLOBAL_NODE_MODULES_DIR} from '../../locations.mjs';
import print from '../../print.mjs';
import ALLOWED_TYPES_PACKAGE_MISALIGNMENTS from './ALLOWED_TYPES_PACKAGE_MISALIGNMENTS.mjs';

const GLOBALS_TYPES_PACKAGES = ['@types/jest'];

const RUNTIME_TYPES_PACKAGES = ['@types/node'];

const TYPES_PREFIX = '@types/';

export default async function formatPackageJSONTypesPackages(packageJSONs) {
	let checksPassed = true;

	print(
		1,
		print.subTitle(
			`> Checking 'package.json' files declare needed and aligned '@types' packages...\n`
		)
	);

	const typeScriptVersion = readPackageJSON(
		path.join(GLOBAL_NODE_MODULES_DIR, 'typescript')
	).version;

	const errors = [];

	for (const {dir, pkg, relPath} of packageJSONs) {
		for (const section of ['dependencies', 'devDependencies']) {
			if (!pkg[section]) {
				continue;
			}

			for (const typesName of Object.keys(pkg[section])) {
				if (!typesName.startsWith(TYPES_PREFIX)) {
					continue;
				}

				const error = checkTypesPackage(
					dir,
					typesName,
					typeScriptVersion
				);

				if (error) {
					errors.push({error, relPath, typesName});
				}
			}
		}
	}

	errors.sort(
		(a, b) =>
			a.typesName.localeCompare(b.typesName) ||
			a.relPath.localeCompare(b.relPath)
	);

	for (const {error, relPath, typesName} of errors) {
		print(
			2,
			print.error('ERROR:'),
			'Dependency',
			print.underline(typesName),
			`in ${relPath} ${error}`
		);

		checksPassed = false;
	}

	if (errors.length) {
		print(2, '');
	}

	return checksPassed;
}

function checkTypesPackage(dir, typesName, typeScriptVersion) {
	const typesDir = resolvePackageDir(typesName, dir);

	if (!typesDir) {
		return 'is not installed';
	}

	const typesPkg = readPackageJSON(typesDir);

	if (
		typesPkg.typeScriptVersion &&
		compareMajorMinor(typesPkg.typeScriptVersion, typeScriptVersion) > 0
	) {
		return `${typesPkg.version} requires TypeScript ${typesPkg.typeScriptVersion} but ${typeScriptVersion} is used`;
	}

	if (RUNTIME_TYPES_PACKAGES.includes(typesName)) {
		return;
	}

	const name = getDescribedPackageName(typesName);

	const describedDir = resolvePackageDir(name, dir);

	if (!describedDir) {
		return `is not needed because ${name} is not installed`;
	}

	const describedPkg = readPackageJSON(describedDir);

	if (
		!GLOBALS_TYPES_PACKAGES.includes(typesName) &&
		providesTypes(describedDir, describedPkg)
	) {
		return `is not needed because ${name} ${describedPkg.version} provides its own types`;
	}

	if (
		!ALLOWED_TYPES_PACKAGE_MISALIGNMENTS.includes(typesName) &&
		compareMajorMinor(typesPkg.version, describedPkg.version)
	) {
		return `${typesPkg.version} does not match the major.minor of ${name} ${describedPkg.version}`;
	}
}

function compareMajorMinor(version1, version2) {
	const [major1, minor1 = 0] = version1.split('.').map(Number);
	const [major2, minor2 = 0] = version2.split('.').map(Number);

	return major1 - major2 || minor1 - minor2;
}

function getDescribedPackageName(typesName) {
	const name = typesName.substring(TYPES_PREFIX.length);

	if (name.includes('__')) {
		return `@${name.replace('__', '/')}`;
	}

	return name;
}

function hasTypesCondition(exports) {
	if (!exports || typeof exports !== 'object') {
		return false;
	}

	return Object.entries(exports).some(
		([key, value]) => key === 'types' || hasTypesCondition(value)
	);
}

function providesTypes(dir, pkg) {
	if (
		pkg.types ||
		pkg.typings ||
		pkg.typesVersions ||
		hasTypesCondition(pkg.exports)
	) {
		return true;
	}

	const mainPath = path
		.join(dir, pkg.main || 'index.js')
		.replace(/\.[cm]?js$/, '');

	return [
		`${mainPath}.d.ts`,
		`${mainPath}.d.cts`,
		`${mainPath}.d.mts`,
		path.join(mainPath, 'index.d.ts'),
	].some((filePath) => fs.existsSync(filePath));
}

function readPackageJSON(dir) {
	return JSON.parse(fs.readFileSync(path.join(dir, 'package.json'), 'utf8'));
}

function resolvePackageDir(name, basedir) {
	try {
		return path.dirname(resolve.sync(`${name}/package.json`, {basedir}));
	}
	catch (error) {
		return null;
	}
}
