/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs';
import {createRequire} from 'module';
import path from 'path';

import print from '../print.mjs';
import project from './project.mjs';

const CUSTOM_DEP_PATH_OPTION_MAP = {
	'liferay-frontend-common-css': 'cssCommonPath',
	'liferay-frontend-theme-styled': 'styledPath',
	'liferay-frontend-theme-unstyled': 'unstyledPath',
};

export function getCustomDependencyPath(dependency) {
	const option = CUSTOM_DEP_PATH_OPTION_MAP[dependency];

	const customPath = option && project.options[option];

	if (customPath) {
		print(1, `${dependency} using custom path: ${customPath}`);

		validateCustomDependencyPath(customPath);
	}

	return customPath;
}

export function getLanguageProperties(pathSrc) {
	const pathContent = path.join(pathSrc, 'WEB-INF', 'src', 'content');

	const languageKeys = [];

	if (fs.existsSync(pathContent) && fs.statSync(pathContent).isDirectory()) {
		for (const item of fs.readdirSync(pathContent)) {
			if (item.match(/Language.*properties/)) {
				languageKeys.push(
					'<language-properties>content/' +
						item +
						'</language-properties>'
				);
			}
		}
	}

	return languageKeys;
}

export function resolveDependency(dependency) {
	const customPath = getCustomDependencyPath(dependency);

	if (customPath) {
		return customPath;
	}

	const require = createRequire(path.join(project.dir, 'package.json'));

	return path.dirname(require.resolve(dependency));
}

function validateCustomDependencyPath(customPath) {
	const stats = fs.statSync(customPath);

	if (!stats.isDirectory()) {
		throw new Error(customPath + ' is not a directory');
	}
}
