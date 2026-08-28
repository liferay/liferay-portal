/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MODULES_DIR} from '../../locations.mjs';
import print from '../../print.mjs';
import ALLOWED_ROOT_PACKAGE_JSON_RESOLUTIONS from './ALLOWED_ROOT_PACKAGE_JSON_RESOLUTIONS.mjs';

const SELECTIVE_RESOLUTIONS_URL =
	'https://classic.yarnpkg.com/lang/en/docs/selective-version-resolutions/';

export default async function formatGlobalPackageJSONResolutions(packageJSONs) {
	let checksPassed = true;

	print(
		1,
		print.subTitle(
			`> Checking 'modules/package.json' only declares allowed resolutions...\n`
		)
	);

	const rootPkg = packageJSONs.find(({dir}) => dir === MODULES_DIR).pkg;

	const resolutions = rootPkg.resolutions || {};

	const globalKeys = [];
	const undocumentedKeys = [];

	for (const key of Object.keys(resolutions)) {
		if (!isSelective(key)) {
			globalKeys.push(key);

			continue;
		}

		if (!isDocumented(key)) {
			undocumentedKeys.push(key);
		}
	}

	const obsoleteKeys = Object.keys(
		ALLOWED_ROOT_PACKAGE_JSON_RESOLUTIONS
	).filter((key) => !(key in resolutions));

	if (globalKeys.length) {
		print(
			2,
			print.error('ERROR:'),
			"'modules/package.json' declares resolutions that are not selective:"
		);

		for (const key of globalKeys.sort()) {
			print(3, key);
		}

		print(
			2,
			`Name the path to the dependency through at least one of its parents, for example`,
			print.underline(`'liferay-theme-tasks/**/websocket-driver'`),
			`instead of`,
			print.underline(`'websocket-driver'`),
			`(see ${SELECTIVE_RESOLUTIONS_URL})\n`
		);

		checksPassed = false;
	}

	if (undocumentedKeys.length) {
		print(
			2,
			print.error('ERROR:'),
			"'modules/package.json' declares resolutions that are not documented:"
		);

		for (const key of undocumentedKeys.sort()) {
			print(3, key);
		}

		print(
			2,
			`Add an entry explaining why each one is needed to`,
			print.underline(`'ALLOWED_ROOT_PACKAGE_JSON_RESOLUTIONS.mjs'`),
			'\n'
		);

		checksPassed = false;
	}

	if (obsoleteKeys.length) {
		print(
			2,
			print.error('ERROR:'),
			"'ALLOWED_ROOT_PACKAGE_JSON_RESOLUTIONS.mjs' documents resolutions that no longer exist:"
		);

		for (const key of obsoleteKeys.sort()) {
			print(3, key);
		}

		print(
			2,
			`Remove them so the file keeps describing what 'modules/package.json' actually declares\n`
		);

		checksPassed = false;
	}

	return checksPassed;
}

/**
 * Tells whether a resolution key carries a non empty explanation.
 */
function isDocumented(key) {
	const reason = ALLOWED_ROOT_PACKAGE_JSON_RESOLUTIONS[key];

	return typeof reason === 'string' && !!reason.trim();
}

/**
 * Tells whether a resolution key is selective, that is, whether it names the path to the resolved
 * dependency through at least one concrete parent. Bare package names and paths made only of
 * wildcards apply to the whole dependency tree, so they do not qualify.
 */
function isSelective(key) {
	const segments = getSegments(key);

	if (segments.length < 2) {
		return false;
	}

	return segments
		.slice(0, -1)
		.some((segment) => segment !== '*' && segment !== '**');
}

/**
 * Splits a resolution key into package name segments, keeping scoped package names together since
 * they contain a slash themselves.
 */
function getSegments(key) {
	const parts = key.split('/');

	const segments = [];

	for (let index = 0; index < parts.length; index++) {
		if (parts[index].startsWith('@') && index + 1 < parts.length) {
			segments.push(`${parts[index]}/${parts[index + 1]}`);

			index++;
		}
		else {
			segments.push(parts[index]);
		}
	}

	return segments;
}
