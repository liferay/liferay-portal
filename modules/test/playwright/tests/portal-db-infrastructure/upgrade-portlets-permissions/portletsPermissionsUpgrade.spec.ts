/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {searchAdminPageTest} from '../../../fixtures/searchAdminPageTest';
import {getHeader} from '../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../liferay.config';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';

const test = mergeTests(
	featureFlagsTest({
		'LPD-105225': {enabled: true},
	}),
	loginTest(),
	searchAdminPageTest
);

const NO_PERMISSION_MESSAGE =
	'You do not have the roles required to access this portlet.';

async function assertNoPermission(page: Page) {
	await expect(
		page.locator('.alert').getByText(NO_PERMISSION_MESSAGE, {exact: true})
	).toBeVisible();
}

async function assertPortletOption(page: Page, optionsName: string) {
	await clickAndExpectToBeVisible({
		target: page.getByRole('menuitem', {exact: true, name: optionsName}),
		timeout: 5000,
		trigger: page.locator('.portlet-options').first(),
	});
}

async function signIn(page: Page, emailPrefix: string, screenName: string) {
	await performLogout(page);

	await performLoginViaApi({
		page,
		password: liferayConfig.environment.password,
		screenName: emailPrefix,
	});

	const response = await page.request.get(
		'/o/headless-admin-user/v1.0/my-user-account',
		{headers: await getHeader(page)}
	);

	expect(response.status()).toBe(200);

	const {alternateName} = await response.json();

	expect(alternateName).toBe(screenName);
}

test.describe.serial('View portlets permissions upgrade', () => {
	test(
		'Can view the portlet permissions the first user kept after upgrade',
		{tag: ['@LPD-104389']},
		async ({page, searchAdminPage}) => {
			await test.step('Reindex all search indexes', async () => {
				await searchAdminPage.goto();

				await searchAdminPage.goToIndexActionsTab();

				await searchAdminPage.reindexAllSearchIndexes();

				const reindexAllSearchIndexes =
					await searchAdminPage.getIndexActionsItem(
						'All Search Indexes'
					);

				await expect(reindexAllSearchIndexes).toBeVisible();

				await expect(
					reindexAllSearchIndexes.locator('.progress')
				).toBeHidden({timeout: 120 * 1000});
			});

			await test.step('Sign in as the first upgraded user', async () => {
				await signIn(page, 'userea1', 'usersn1');
			});

			await test.step('View the message boards thread', async () => {
				await page.goto('/web/site-name-1/message-boards-page');

				const threadLink = page.getByRole('link', {
					name: 'Message Boards Subject',
				});

				await expect(threadLink).toBeVisible();

				const threadRow = page
					.getByTestId('row')
					.filter({hasText: 'Message Boards Subject'});

				await expect(
					threadRow.locator('.lfr-portal-tooltip[title="0 Replies"]')
				).toBeVisible();

				await expect(threadRow.getByText('Test Test')).toBeVisible();

				const threadURL = await threadLink.getAttribute('href');

				expect(threadURL).not.toBeNull();

				await page.goto(threadURL as string);

				await expect(
					page.getByRole('heading', {
						name: 'Message Boards Subject',
					})
				).toBeVisible();

				await expect(
					page.getByText('Message Boards Body', {exact: true})
				).toBeVisible();
			});

			await test.step('View no portlet options on message boards', async () => {
				await page.goto('/web/site-name-1/message-boards-page');

				await expect(page.locator('.portlet-options')).toBeHidden();
			});

			await test.step('View the web content', async () => {
				await page.goto('/web/site-name-1/web-content-page');

				await expect(
					page.getByText('Web Content Title', {exact: true})
				).toBeVisible();

				await expect(
					page.getByText('Web Content Content', {exact: true})
				).toBeVisible();
			});

			await test.step('View no permission on the second site blogs', async () => {
				await page.goto('/web/site-name-2/blogs-page');

				await assertNoPermission(page);
			});
		}
	);

	test(
		'Can view the portlet permissions the second user kept after upgrade',
		{tag: ['@LPD-104389']},
		async ({page}) => {
			await test.step('Sign in as the second upgraded user', async () => {
				await signIn(page, 'userea2', 'usersn2');
			});

			await test.step('View the wiki front page', async () => {
				await page.goto('/web/site-name-2/wiki-page');

				await expect(
					page.getByRole('heading', {name: 'FrontPage'})
				).toBeVisible();

				await expect(
					page.getByText('Wiki Front Page Content', {exact: true})
				).toBeVisible();

				await assertPortletOption(page, 'Configuration');
			});

			await test.step('View the document', async () => {
				await page.goto('/web/site-name-2/documents-and-media-page');

				await expect(
					page.getByRole('link', {name: 'Document1'})
				).toBeVisible();

				await assertPortletOption(page, 'Permissions');
			});

			await test.step('View no permission on the second site blogs', async () => {
				await page.goto('/web/site-name-2/blogs-page');

				await assertNoPermission(page);
			});
		}
	);
});
