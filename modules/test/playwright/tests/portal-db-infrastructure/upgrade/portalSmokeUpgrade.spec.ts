/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {searchAdminPageTest} from '../../../fixtures/searchAdminPageTest';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';

const test = mergeTests(loginTest(), searchAdminPageTest);

async function viewUpgradedPortalContent(page: Page) {
	await test.step('View web content after upgrade', async () => {
		await page.goto('/web/guest/web-content');

		await expect(
			page.getByText('Web Content Title', {exact: true})
		).toBeVisible();

		await expect(
			page.getByText('Web Content Content', {exact: true})
		).toBeVisible();
	});

	await test.step('View document after upgrade', async () => {
		await page.goto('/web/guest/document');

		await page.getByRole('link', {name: 'Document1'}).click();

		await page.locator('a[href*=infoPanel]').click();

		await expect(page.locator('.sidebar-body .username')).toHaveText(
			'Test Test'
		);

		await expect(page.locator('.sidebar-header .label-item')).toHaveText(
			'Version 1.0'
		);

		await expect(
			page.locator('.sidebar-header .workflow-status')
		).toHaveText('Approved');

		const downloadButton = page
			.locator('.sidebar-section')
			.getByRole('link', {name: 'Download'});

		await expect(downloadButton).toHaveAttribute(
			'title',
			'File Size 22 KB'
		);
	});

	await test.step('View message boards after upgrade', async () => {
		await page.goto('/web/guest/message-boards');

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

		const threadURL = await threadLink.getAttribute('href');

		expect(threadURL).not.toBeNull();

		await page.goto(threadURL as string);

		await expect(
			page.getByRole('heading', {name: 'Message Boards Subject'})
		).toBeVisible();

		await expect(
			page.getByText('Message Boards Body', {exact: true})
		).toBeVisible();
	});

	await test.step('View wiki after upgrade', async () => {
		await page.goto('/web/guest/wiki');

		await expect(
			page.getByRole('heading', {name: 'FrontPage'})
		).toBeVisible();

		await expect(
			page.getByText('Wiki Front Page Content', {exact: true})
		).toBeVisible();
	});

	await test.step('View blogs after upgrade', async () => {
		await page.goto('/web/guest/blogs');

		await expect(
			page.getByText('Blogs Entry Title', {exact: true})
		).toBeVisible();

		await expect(
			page.getByText('Blogs Entry Content', {exact: true})
		).toBeVisible();
	});

	await test.step('View site page after upgrade', async () => {
		await page.goto('/web/site-name/site-page');

		await expect(page).toHaveTitle(/^Site Page - Site Name/);
	});
}

test.describe.serial('View portal smoke upgrade', () => {
	test(
		'Can view upgraded portal content as admin',
		{tag: '@LPD-96642'},
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

			await viewUpgradedPortalContent(page);
		}
	);

	test(
		'Can view upgraded portal content as the archive user',
		{tag: '@LPD-96642'},
		async ({page}) => {
			await performLogout(page);

			await performLoginViaApi({page, screenName: 'user'});

			const response = await page.request.get(
				'/o/headless-admin-user/v1.0/my-user-account'
			);

			expect(response.status()).toBe(200);

			const {alternateName} = await response.json();

			expect(alternateName).toBe('usersn');

			await viewUpgradedPortalContent(page);
		}
	);
});
