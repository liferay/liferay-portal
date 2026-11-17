/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * "@types" packages that are allowed to declare a major.minor different from the one of the
 * package they describe because no version with the right major.minor is published that supports
 * our TypeScript version. They are still checked for being needed and for supporting our
 * TypeScript version.
 */
export default [

	// The newest published types are for 4.20 while we use 4.22

	'@types/ckeditor4',

	// The newest published types are for 5.60 while we use 5.65

	'@types/codemirror',

	// No types are published for 4.x while we use 4.0

	'@types/warning',

	// The types for 3.x need TypeScript 5.3 and there are none for 3.2

	'@types/yauzl',
];
