/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {searchAdminPageTest} from '../../../fixtures/searchAdminPageTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';

const test = mergeTests(
	featureFlagsTest({
		'LPD-105225': {enabled: true},
	}),
	loginTest(),
	searchAdminPageTest
);

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

		const downloadButton = page
			.locator('.sidebar-section')
			.getByRole('link', {name: 'Download'});

		// The info panel does not always render on the first click, and its
		// contents attach to the DOM either way, so the Download link can be
		// present but hidden — getByRole does not match a hidden element, which is
		// why only the title assertion below fails. Measured on a live 6.1.30
		// upgrade: hidden after one click, visible after a re-expand.
		// DMDocument.expandInfo guards its own click the same way. The 5s timeout
		// gives the first click room on a freshly upgraded portal.

		await clickAndExpectToBeVisible({
			target: downloadButton,
			timeout: 5000,
			trigger: page.locator('a[href*=infoPanel]'),
		});

		// Assert visibility before text on each of these. toHaveText waits for
		// attachment rather than visibility, so all three pass against a panel
		// that never opened -- they were satisfiable by the very state this step
		// exists to detect. Gating above fixes the order; asserting here fixes
		// the assertions, so they keep their meaning if the block ever moves.

		const username = page.locator('.sidebar-body .username');
		const version = page.locator('.sidebar-header .label-item');
		const workflowStatus = page.locator('.sidebar-header .workflow-status');

		await expect(username).toBeVisible();
		await expect(username).toHaveText('Test Test');

		await expect(version).toBeVisible();
		await expect(version).toHaveText('Version 1.0');

		await expect(workflowStatus).toBeVisible();
		await expect(workflowStatus).toHaveText('Approved');

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
		{tag: ['@LPD-96642', '@LPD-104520']},
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
		{tag: ['@LPD-96642', '@LPD-104520']},
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
