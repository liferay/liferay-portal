/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

module.exports = {
	globals: {
		YT: true,
		configuration: true,
		fragmentElement: true,
		fragmentElementId: true,
		fragmentEntryLinkNamespace: true,
		fragmentNamespace: true,
		input: true,
		layoutMode: true,
	},
	overrides: [
		{
			files: '**/src/**/*.js',
			rules: {

				// For IE compatibility because JS here doesn't get transpiled.

				'notice/notice': 'off',
				'object-shorthand': 'off',
				'prefer-arrow-callback': 'off',
				'prefer-object-spread': 'off',
			},
		},
	],
};
