/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {EditJSClientExtensionsPage} from '../pages/EditJSClientExtensionsPage';

const editJSClientExtensionsPageTest = test.extend<{
	editJSClientExtensionsPage: EditJSClientExtensionsPage;
}>({
	editJSClientExtensionsPage: async ({page}, use) => {
		await use(new EditJSClientExtensionsPage(page));
	},
});

export {editJSClientExtensionsPageTest};
