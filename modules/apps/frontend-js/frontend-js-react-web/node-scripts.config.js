/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

module.exports = {
	exports: [
		'classnames',
		'formik',
		'prop-types',
		'react',
		'react-16',
		'react-18',
		'react-dnd-html5-backend',
		'react-dnd',
		'react-dom',
		'react-dom/client',
		'react-dom-16',
		'react-dom-18',
		'react-dom-18/client',
	],
	main: './src/main/resources/META-INF/resources/js/index.ts',
	symbols: {
		'prop-types': ['*', 'default'],
		'react': ['*', 'default'],
		'react-dnd': ['*', 'default'],
		'react-dom': ['*', 'default'],
	},
};
