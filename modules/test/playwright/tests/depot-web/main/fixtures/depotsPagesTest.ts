/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore

import {test} from '@playwright/test';

import {DocumentLibraryEditFilePage} from '../../../../pages/document-library-web/DocumentLibraryEditFilePage';
import {DepotAdminPage} from '../pages/DepotAdminPage';
import {DepotItemSelectorPage} from '../pages/DepotItemSelectorPage';

const depotsPagesTest = test.extend<{
	depotAdminPage: DepotAdminPage;
	depotItemSelectorPage: DepotItemSelectorPage;
	documentLibraryEditFilePage: DocumentLibraryEditFilePage;
}>({
	depotAdminPage: async ({page}, use) => {
		await use(new DepotAdminPage(page));
	},
	depotItemSelectorPage: async ({page}, use) => {
		await use(new DepotItemSelectorPage(page));
	},
	documentLibraryEditFilePage: async ({page}, use) => {
		await use(new DocumentLibraryEditFilePage(page));
	},
});

export {depotsPagesTest};
