/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const path = require('path');

const config = {
	env: {
		es2021: true,
		node: true,
	},
	extends: ['plugin:@liferay/general'],
	parserOptions: {
		ecmaFeatures: {
			jsx: true,
		},
		ecmaVersion: 2023,
	},
	plugins: ['@liferay'],
	root: true,
	rules: {
		'@liferay/portal/no-global-fetch': 'off',
		'notice/notice': [
			'error',
			{
				nonMatchingTolerance: 0.95,
				onNonMatchingHeader: 'replace',
				templateFile: path.join(__dirname, 'copyright.js'),
			},
		],
	},
};

module.exports = config;
