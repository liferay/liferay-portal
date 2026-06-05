/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {DataMasksPage} from '../pages/mcp-server-web/DataMasksPage';

const mcpServerWebPagesTest = test.extend<{
	dataMasksPage: DataMasksPage;
}>({
	dataMasksPage: async ({page}, use) => {
		await use(new DataMasksPage(page));
	},
});

export {mcpServerWebPagesTest};
