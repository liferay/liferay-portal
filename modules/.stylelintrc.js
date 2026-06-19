/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const {defaultConfig} = require('@liferay/stylelint-plugin');

module.exports = {
	plugins: require.resolve('@liferay/stylelint-plugin'),
	rules: {
		...defaultConfig.rules,
		'color-hex-length': null,
		'length-zero-no-unit': null,
		'liferay/no-block-comments': null,
		'liferay/no-hardcoded-colors': true,
		'selector-type-no-unknown': [
			true,
			{
				ignore: 'custom-elements',
			},
		],
	},
};
