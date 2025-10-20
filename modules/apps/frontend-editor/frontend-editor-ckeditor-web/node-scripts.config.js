/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

module.exports = {
	alias: {
		'@ckeditor/ckeditor5-icons/dist/index.js':
			'./src/main/resources/META-INF/resources/js/ckeditor5/icons.ts',
	},
	exports: ['ckeditor5/ckeditor5.css'],
	main: './src/main/resources/META-INF/resources/js/index.ts',
};
