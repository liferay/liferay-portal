/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {messageBoardsPagesTest} from '../../../../../fixtures/messageBoardsTest';

export const test = mergeTests(
	isolatedSiteTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-105225': {enabled: true},
	}),
	loginTest(),
	messageBoardsPagesTest
);

test(
	'Check bbcode data is formatted',
	{tag: '@LPD-78300'},
	async ({messageBoardsEditThreadPage, page, site}) => {
		await page.route('**/bbcode_parser.js*', async (route) => {
			await new Promise((resolve) => setTimeout(resolve, 1000));

			await route.continue();
		});

		await messageBoardsEditThreadPage.goto(site.friendlyUrlPath);

		const subjectText = page.getByLabel('Subject Required');

		await expect(subjectText).toBeVisible();

		await subjectText.fill('test');

		const sourceButton = page.getByLabel('Source');

		await expect(sourceButton).toBeVisible();

		await sourceButton.click();

		await page
			.getByLabel(
				'Editor, _com_liferay_message_boards_web_portlet_MBAdminPortlet_bodyEditor',
				{exact: true}
			)
			.fill('- [b]BBcode strong / bold[/b]');

		await page.getByRole('button', {name: 'Publish'}).click();

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		const actionButton = page.getByRole('button', {name: 'Actions'});

		await expect(actionButton).toBeVisible();

		await actionButton.click();

		const editButton = page.getByRole('menuitem', {name: 'Edit'});

		await expect(editButton).toBeVisible();

		await editButton.click();

		expect(page.getByText('- BBcode strong / bold')).toBeVisible({
			timeout: 5000,
		});
	}
);
