/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Dependencies that are allowed in the global "modules/package.json" even
 * though they are neither "@types/*" packages nor version divergence entries.
 */
export default [

	// eslint-plugin-react-compiler is referenced from 'modules/.eslintrc.js', which is a global
	// file so it belongs to the global 'package.json'.

	'eslint-plugin-react-compiler',
];
