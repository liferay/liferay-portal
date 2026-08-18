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

	// The jsdoc Gradle task injects a jsdoc dependency at build time and relies on the install
	// resolving its transitive tree. On a developer machine yarn fetches the tree from the
	// registry, so the task passes and hides the problem; CI installs offline from a mirror keyed
	// to the lockfile, and the lockfile no longer carries the tree, so jsdoc lands without its
	// dependencies and node dies on the first require.
	//
	// See https://github.com/brianchandotcom/liferay-portal/pull/180382 for more information.

	'jsdoc',
];
