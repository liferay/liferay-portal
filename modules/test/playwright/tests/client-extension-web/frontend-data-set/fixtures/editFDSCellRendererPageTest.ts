/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {EditFDSCellRendererPage} from '../pages/EditFDSCellRendererPage';

const editFDSCellRendererPageTest = test.extend<{
	editFDSCellRendererPage: EditFDSCellRendererPage;
}>({
	editFDSCellRendererPage: async ({page}, use) => {
		await use(new EditFDSCellRendererPage(page));
	},
});

export {editFDSCellRendererPageTest};
