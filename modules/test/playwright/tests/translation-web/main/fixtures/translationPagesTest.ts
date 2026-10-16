/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {ExportImportTranslationPage} from '../pages/ExportImportTranslationPage';
import {TranslationsAdminPage} from '../pages/TranslationsAdminPage';
import {WebContentTranslationPage} from '../pages/WebContentTranslationPage';

const translationPagesTest = test.extend<{
	exportImportTranslationPage: ExportImportTranslationPage;
	translationsAdminPage: TranslationsAdminPage;
	webContentTranslationPage: WebContentTranslationPage;
}>({
	exportImportTranslationPage: async ({page}, use) => {
		await use(new ExportImportTranslationPage(page));
	},
	translationsAdminPage: async ({page}, use) => {
		await use(new TranslationsAdminPage(page));
	},
	webContentTranslationPage: async ({page}, use) => {
		await use(new WebContentTranslationPage(page));
	},
});

export {translationPagesTest};
