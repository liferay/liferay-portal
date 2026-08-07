/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {EditThemeCSSClientExtensionsPage} from '../pages/EditThemeCSSClientExtensionsPage';

const editThemeCSSClientExtensionsPageTest = test.extend<{
	editThemeCSSClientExtensionsPage: EditThemeCSSClientExtensionsPage;
}>({
	editThemeCSSClientExtensionsPage: async ({page}, use) => {
		await use(new EditThemeCSSClientExtensionsPage(page));
	},
});

export {editThemeCSSClientExtensionsPageTest};
