/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {messageBoardsPagesTest} from '../../../fixtures/messageBoardsTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';

const test = mergeTests(
	featureFlagsTest({
		'LPD-105225': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	messageBoardsPagesTest
);

const testWithMessageBoardsDeprecation = mergeTests(
	featureFlagsTest({
		'LPD-105225': {enabled: false},
	}),
	loginTest(),
	productMenuPageTest
);

test(
	'Add deprecation flag Message Boards with FF enabled',
	{
		tag: '@LPD-105226',
	},
	async ({messageBoardsPage, page, site}) => {
		await messageBoardsPage.goto(site.friendlyUrlPath);

		await expect(messageBoardsPage.newThreadButton).toBeVisible();

		await expect(page.getByText('Deprecated')).toBeVisible();
	}
);

testWithMessageBoardsDeprecation(
	'Add deprecation flag Message Boards with FF disabled',
	{
		tag: '@LPD-105226',
	},
	async ({page, productMenuPage}) => {
		await productMenuPage.openProductMenuIfClosed();

		await expect(
			page.getByRole('menuitem', {
				name: 'Content & Data',
			})
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {
				name: 'Message Boards',
			})
		).not.toBeVisible();
	}
);
