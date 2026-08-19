/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const fs = require('fs');
const path = require('path');

const RESOURCES_PATH = path.join(
	__dirname,
	'../src/main/resources/META-INF/resources'
);

const LEGACY_PATH = path.join(RESOURCES_PATH, 'js/legacy');

const LANGUAGE_GET_REGEXP = /Liferay\.Language\.get\(\s*(['"`])(.*?)\1/gs;

const MODULE_PATH_REGEXP = /path:\s*'([^']+\.js)'/g;

function getAUIModuleFileNames() {
	const config = fs.readFileSync(
		path.join(LEGACY_PATH, 'config.js'),
		'utf-8'
	);

	return [...config.matchAll(MODULE_PATH_REGEXP)].map((match) => match[1]);
}

function getLanguageKeys(fileName) {
	const source = fs.readFileSync(path.join(LEGACY_PATH, fileName), 'utf-8');

	return [...source.matchAll(LANGUAGE_GET_REGEXP)].map((match) => match[2]);
}

describe('calendar-web language keys', () => {
	it('declares every key the AUI modules read, so none renders as its own key', () => {
		const {keys} = JSON.parse(
			fs.readFileSync(path.join(RESOURCES_PATH, 'language.json'), 'utf-8')
		);

		const undeclaredKeys = {};

		for (const fileName of getAUIModuleFileNames()) {
			const missing = getLanguageKeys(fileName).filter(
				(key) => !keys.includes(key)
			);

			if (missing.length) {
				undeclaredKeys[fileName] = [...new Set(missing)].sort();
			}
		}

		expect(undeclaredKeys).toEqual({});
	});
});
