/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {clickToChatPagesTest} from '../../site-admin-web/main/fixtures/clickToChatPagesTest';
import {clickToChatConfig} from './clickToChat.config';

export const test = mergeTests(
	clickToChatPagesTest,
	instanceSettingsPagesTest,
	loginTest()
);

test.afterEach(async ({clickToChatSettingsPage, page}) => {
	await clickToChatSettingsPage.gotoInstanceSettings();

	await page.getByLabel('Enable Click to Chat').uncheck();

	await clickToChatSettingsPage.selectChatProvider('');

	await clickToChatSettingsPage.setChatProviderPassword('');
});

test.beforeEach(async ({clickToChatSettingsPage}) => {
	await clickToChatSettingsPage.enableClickToChat();
});

const providers = [
	{
		iconKey: 'chatwootIcon',
		name: 'Chatwoot',
		passwordKey: 'chatwoot',
	},
	{
		iconKey: 'crispIcon',
		name: 'Crisp',
		passwordKey: 'crisp',
	},
	{
		iconKey: 'hubspotIcon',
		name: 'Hubspot',
		passwordKey: 'hubspot',
	},
	{
		iconKey: 'jivoChatIcon',
		name: 'JivoChat',
		passwordKey: 'jivochat',
	},
	{
		iconKey: 'liveChatIcon',
		name: 'LiveChat',
		passwordKey: 'livechat',
		skip: true,
	},
	{
		iconKey: 'livePersonIcon',
		name: 'LivePerson',
		passwordKey: 'liveperson',
		skip: true,
	},
	{
		iconKey: 'smartsuppIcon',
		name: 'Smartsupp',
		passwordKey: 'smartsupp',
	},
	{
		iconKey: 'tawkToIcon',
		name: 'TawkTo',
		passwordKey: 'tawkto',
		skip: true,
	},
	{
		iconKey: 'tidioIcon',
		name: 'Tidio',
		passwordKey: 'tidio',
	},
	{
		iconKey: 'zendeskIcon',
		name: 'Zendesk Web Widget Classic',
		passwordKey: 'zendesk',
		skip: true,
	},
];

for (const provider of providers) {
	const runTest = provider.skip ? test.skip : test;

	runTest(
		`${provider.name} can be enabled and disabled`,
		{
			tag: '@LPS-129042',
		},
		async ({clickToChatSettingsPage, page}) => {
			await clickToChatSettingsPage.selectChatProvider(provider.name);

			await clickToChatSettingsPage.setChatProviderPassword(
				clickToChatConfig.password[provider.passwordKey]
			);

			await expect(
				clickToChatSettingsPage[provider.iconKey].first()
			).toBeAttached({timeout: 1000});

			await page.getByLabel('Enable Click to Chat').uncheck();

			await clickToChatSettingsPage.saveConfiguration();

			await expect(
				clickToChatSettingsPage[provider.iconKey]
			).not.toBeAttached({timeout: 1000});
		}
	);

	runTest(
		`${provider.name} provider keeps enabled after logout and login`,
		{
			tag: '@LPS-133453',
		},
		async ({clickToChatSettingsPage, page}) => {
			await clickToChatSettingsPage.selectChatProvider(provider.name);

			await clickToChatSettingsPage.setChatProviderPassword(
				clickToChatConfig.password[provider.passwordKey]
			);

			await expect(
				clickToChatSettingsPage[provider.iconKey].first()
			).toBeAttached({timeout: 1000});

			await performLogout(page);

			await performLoginViaApi({page, screenName: 'test'});

			await expect(
				clickToChatSettingsPage[provider.iconKey]
			).toBeAttached({timeout: 1000});
		}
	);
}

test(
	'Can hide chat widget in control panel',
	{
		tag: '@LPS-145280',
	},
	async ({clickToChatSettingsPage, page}) => {
		await clickToChatSettingsPage.selectChatProvider('JivoChat');

		await clickToChatSettingsPage.setChatProviderPassword(
			clickToChatConfig.password.jivochat
		);

		await expect(clickToChatSettingsPage.jivoChatIcon).toBeAttached();

		try {
			await page.getByLabel('Hide in Control Panel').check();

			await clickToChatSettingsPage.saveConfiguration();

			await expect(
				clickToChatSettingsPage.jivoChatIcon
			).not.toBeAttached();
		}
		finally {
			await page.getByLabel('Hide in Control Panel').uncheck();

			await clickToChatSettingsPage.saveConfiguration();
		}
	}
);

test.skip(
	'Can hubspot access required scopes',
	{
		tag: '@LPS-137169',
	},
	async ({clickToChatSettingsPage, page}) => {
		await clickToChatSettingsPage.selectChatProvider('Hubspot');

		await clickToChatSettingsPage.setChatProviderPassword(
			'nonexistent/66b6b4b2-d096-45a1-b25d-7dab3f332167'
		);

		await waitForAlert(
			page,
			`Error:This app hasn't been granted all required scopes to make this call.`,
			{type: 'danger'}
		);
	}
);

test(
	'Hide Chat Provider',
	{
		tag: '@LPS-129042',
	},
	async ({clickToChatSettingsPage, page}) => {
		await page
			.getByLabel('Site Settings Strategy')
			.selectOption({label: 'Always Override'});

		await expect(clickToChatSettingsPage.chatProvider).not.toBeVisible();

		await expect(
			clickToChatSettingsPage.chatProviderAccountId
		).not.toBeVisible();
	}
);

test(
	'Is api key invalid for hubspot',
	{
		tag: '@LPS-137169',
	},
	async ({clickToChatSettingsPage, page}) => {
		await clickToChatSettingsPage.selectChatProvider('Hubspot');

		await clickToChatSettingsPage.setChatProviderPassword(
			'19907868/1-d096-45a1-b25d-7dab3f332167'
		);

		await waitForAlert(page, `Error:The API key provided is invalid.`, {
			type: 'danger',
		});
	}
);
