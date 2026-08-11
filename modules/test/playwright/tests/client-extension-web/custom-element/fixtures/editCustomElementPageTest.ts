/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {EditCustomElementPage} from '../pages/EditCustomElementPage';

const editCustomElementPageTest = test.extend<{
	editCustomElementPage: EditCustomElementPage;
}>({
	editCustomElementPage: async ({page}, use) => {
		await use(new EditCustomElementPage(page));
	},
});

export {editCustomElementPageTest};
