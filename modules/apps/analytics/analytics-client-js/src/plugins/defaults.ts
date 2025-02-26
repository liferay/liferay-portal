/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import blogs from './blogs';
import custom from './custom';
import documents from './documents';
import documentsFragment from './documentsFragment';
import dxp from './dxp';
import forms from './forms';
import read from './read';
import scrolling from './scrolling';
import timing from './timing';
import visibility from './visibility';
import webContents from './web-contents';

export {
	blogs,
	documents,
	documentsFragment,
	dxp,
	forms,
	read,
	scrolling,
	timing,
	webContents,
};
export default [

	// Dxp should be before other events plugins, because it can dispose analytics

	dxp,

	blogs,
	custom,
	documents,
	documentsFragment,
	forms,
	read,
	scrolling,
	timing,
	visibility,
	webContents,
];
