/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {ExportImportStagingInstanceSettingsPage} from '../../../staging-configuration-web/main/pages/ExportImportStagingInstanceSettingsPage';
import {ExportImportStagingSystemSettingsPage} from '../../../staging-configuration-web/main/pages/ExportImportStagingSystemSettingsPage';
import {ExportImportFramePage} from '../pages/ExportImportFramePage';
import {ExportImportPage} from '../pages/ExportImportPage';
import {UploadServletRequestSystemSettingsPage} from '../pages/UploadServletRequestSystemSettingsPage';

const exportImportPagesTest = test.extend<{
	exportImportFramePage: ExportImportFramePage;
	exportImportPage: ExportImportPage;
	exportImportStagingInstanceSettingsPage: ExportImportStagingInstanceSettingsPage;
	exportImportStagingSystemSettingsPage: ExportImportStagingSystemSettingsPage;
	uploadServletRequestSystemSettingsPage: UploadServletRequestSystemSettingsPage;
}>({
	exportImportFramePage: async ({page}, use) => {
		await use(new ExportImportFramePage(page));
	},
	exportImportPage: async ({page}, use) => {
		await use(new ExportImportPage(page));
	},
	exportImportStagingInstanceSettingsPage: async ({page}, use) => {
		await use(new ExportImportStagingInstanceSettingsPage(page));
	},
	exportImportStagingSystemSettingsPage: async ({page}, use) => {
		await use(new ExportImportStagingSystemSettingsPage(page));
	},
	uploadServletRequestSystemSettingsPage: async ({page}, use) => {
		await use(new UploadServletRequestSystemSettingsPage(page));
	},
});
export {exportImportPagesTest};
