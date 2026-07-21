/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {DataMasksPage} from '../pages/mcp-server-web/DataMasksPage';
import {PromptsPage} from '../pages/mcp-server-web/PromptsPage';

const mcpServerWebPagesTest = test.extend<{
	dataMasksPage: DataMasksPage;
	promptsPage: PromptsPage;
}>({
	dataMasksPage: async ({page}, use) => {
		await use(new DataMasksPage(page));
	},
	promptsPage: async ({page}, use) => {
		await use(new PromptsPage(page));
	},
});

export {mcpServerWebPagesTest};
