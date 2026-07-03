/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {AICreatorInstanceSettingsPage} from '../../../../pages/product-navigation-applications-menu/AICreatorSettingsPage';
import {GogoShellPage} from '../../../../pages/product-navigation-applications-menu/GogoShellPage';
import {SiteSettingsPage} from '../../../../pages/site-admin-web/SiteSettingsPage';

const _MOCK_AI_CREATOR_OPENAI_CLIENT =
	'com.liferay.ai.creator.openai.web.internal.client.MockAICreatorOpenAIClient';

const aiCreatorPagesTest = test.extend<{
	aiCreatorInstanceSettingsPage: AICreatorInstanceSettingsPage;
	enableMockAICreatorOpenAIClient: () => Promise<void>;
	gogoShellPage: GogoShellPage;
	siteSettingsPage: SiteSettingsPage;
}>({
	aiCreatorInstanceSettingsPage: async ({page}, use) => {
		await use(new AICreatorInstanceSettingsPage(page));
	},
	enableMockAICreatorOpenAIClient: async ({gogoShellPage}, use) => {
		let enabled = false;

		await use(async () => {
			await gogoShellPage.addCommand(
				`scr:enable ${_MOCK_AI_CREATOR_OPENAI_CLIENT}`
			);

			enabled = true;
		});

		if (enabled) {
			await gogoShellPage.addCommand(
				`scr:disable ${_MOCK_AI_CREATOR_OPENAI_CLIENT}`
			);
		}
	},
	gogoShellPage: async ({page}, use) => {
		await use(new GogoShellPage(page));
	},
	siteSettingsPage: async ({page}, use) => {
		await use(new SiteSettingsPage(page));
	},
});

export {aiCreatorPagesTest};
