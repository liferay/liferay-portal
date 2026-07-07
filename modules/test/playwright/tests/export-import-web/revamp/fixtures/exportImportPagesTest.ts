/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {ExportImportDataSelectionPage} from '../pages/ExportImportDataSelectionPage';
import {ExportImportPage} from '../pages/ExportImportPage';

const exportImportPagesTest = test.extend<{
	exportImportDataSelectionPage: ExportImportDataSelectionPage;
	exportImportPage: ExportImportPage;
}>({
	exportImportDataSelectionPage: async ({page}, use) => {
		await use(new ExportImportDataSelectionPage(page));
	},
	exportImportPage: async ({page}, use) => {
		await use(new ExportImportPage(page));
	},
});
export {exportImportPagesTest};
