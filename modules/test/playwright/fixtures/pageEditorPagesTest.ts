/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {ElementVariationsPage} from '../pages/layout-content-page-editor-web/ElementVariationsPage';
import {PageEditorPage} from '../pages/layout-content-page-editor-web/PageEditorPage';

const pageEditorPagesTest = test.extend<{
	elementVariationsPage: ElementVariationsPage;
	pageEditorPage: PageEditorPage;
}>({
	elementVariationsPage: async ({page}, use) => {
		await use(new ElementVariationsPage(page));
	},
	pageEditorPage: async ({page}, use) => {
		await use(new PageEditorPage(page));
	},
});

export {pageEditorPagesTest};
