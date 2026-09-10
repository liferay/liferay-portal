/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Browser, Page, expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {liferayConfig} from '../../../liferay.config';
import {DocumentLibraryPage} from '../../../pages/document-library-web/DocumentLibraryPage';
import {WebContentPage} from '../../../pages/journal-web/WebContentPage';
import {SearchAdminPage} from '../../../pages/portal-search-admin-web/SearchAdminPage';
import {RolesPage} from '../../../pages/roles-admin-web/RolesPage';
import {EditUserPage} from '../../../pages/users-admin-web/EditUserPage';
import {UsersAndOrganizationsPage} from '../../../pages/users-admin-web/UsersAndOrganizationsPage';
import {performLoginViaApi} from '../../../utils/performLogin';

const test = mergeTests(loginTest());

async function reindexAllSearchIndexes(page: Page) {
	const searchAdminPage = new SearchAdminPage(page);

	await searchAdminPage.goto();

	await searchAdminPage.goToIndexActionsTab();

	await searchAdminPage.reindexAllSearchIndexes();

	const indexActionsItem =
		await searchAdminPage.getIndexActionsItem('All Search Indexes');

	await expect(indexActionsItem).toBeVisible();

	const progress = indexActionsItem.locator('.progress');

	await expect(progress).toBeVisible();

	await expect(progress).toBeHidden({timeout: 120 * 1000});
}

async function viewUpgradedPartition({
	absentScreenName,
	browser,
	documentTitle,
	presentEmailAddress,
	presentName,
	presentScreenName,
	roleTitle,
	virtualHostName,
	webContentContent,
	webContentTitle,
}: {
	absentScreenName: string;
	browser: Browser;
	documentTitle: string;
	presentEmailAddress: string;
	presentName: string;
	presentScreenName: string;
	roleTitle: string;
	virtualHostName: string;
	webContentContent: string;
	webContentTitle: string;
}) {
	const baseURL = `http://${virtualHostName}:${liferayConfig.environment.port}`;

	const page = await browser.newPage({baseURL});

	try {
		await performLoginViaApi({
			domain: `@${virtualHostName}`,
			loginUrl: baseURL,
			page,
			screenName: 'test',
		});

		await test.step(`View this partition's user on ${virtualHostName}`, async () => {
			const usersAndOrganizationsPage = new UsersAndOrganizationsPage(
				page
			);

			await usersAndOrganizationsPage.goto();

			await usersAndOrganizationsPage.usersDataTable.search(
				presentScreenName
			);

			const usersTableRowLink =
				await usersAndOrganizationsPage.usersTableRowLink(
					presentScreenName
				);

			await usersTableRowLink.click();

			const editUserPage = new EditUserPage(page);

			await expect(editUserPage.screenNameInput).toHaveValue(
				presentScreenName
			);

			await expect(editUserPage.emailAddressInput).toHaveValue(
				presentEmailAddress
			);

			await expect(editUserPage.firstNameInput).toHaveValue(presentName);

			await expect(editUserPage.lastNameInput).toHaveValue(presentName);
		});

		await test.step(`Do not find the other partition's user on ${virtualHostName}`, async () => {
			const usersAndOrganizationsPage = new UsersAndOrganizationsPage(
				page
			);

			await usersAndOrganizationsPage.goto();

			await usersAndOrganizationsPage.usersDataTable.search(
				absentScreenName
			);

			await expect(
				usersAndOrganizationsPage.noResultsMessage
			).toBeVisible();
		});

		await test.step(`View this partition's role on ${virtualHostName}`, async () => {
			const rolesPage = new RolesPage(page);

			await rolesPage.goto();

			await rolesPage.rolesTable.search(roleTitle);

			const {row} = await rolesPage.rolesTable.row(1, roleTitle, true);

			await expect(row).toContainText('Regular');

			const cellLink = await rolesPage.rolesTable.cellLink(roleTitle);

			await cellLink.click();

			await expect(page.getByLabel('Title')).toHaveValue(roleTitle);
		});

		await test.step(`View this partition's document on ${virtualHostName}`, async () => {
			const documentLibraryPage = new DocumentLibraryPage(page);

			await documentLibraryPage.goto();

			await page.getByRole('link', {name: documentTitle}).click();

			await expect(
				page.getByText(documentTitle, {exact: true})
			).toBeVisible();

			await page.locator('a[href*=infoPanel]').click();

			await expect(page.locator('.sidebar-header')).toContainText(
				documentTitle
			);
		});

		await test.step(`View this partition's web content on ${virtualHostName}`, async () => {
			const webContentPage = new WebContentPage(page);

			await webContentPage.goto();

			await page.getByRole('link', {name: webContentTitle}).click();

			await expect(page.locator('input[id$=titleMapAsXML]')).toHaveValue(
				webContentTitle
			);

			const contentField = page.locator('.ddm-field-container', {
				has: page.getByText('content', {exact: true}),
			});

			await expect(contentField).toContainText(webContentContent);
		});
	}
	finally {
		await page.close();
	}
}

test.describe('View database partitioning upgrade', () => {
	test(
		'Can view upgraded content in each partition and not across them',
		{tag: '@LPD-104394'},
		async ({browser, page}) => {
			await test.step('Reindex all search indexes', async () => {
				await reindexAllSearchIndexes(page);
			});

			await viewUpgradedPartition({
				absentScreenName: 'test2',
				browser,
				documentTitle: 'DM Document1 Title',
				presentEmailAddress: 'test1@www.able.com',
				presentName: 'Test1',
				presentScreenName: 'test1',
				roleTitle: 'Roles Regrole1 Name',
				virtualHostName: 'www.able.com',
				webContentContent: 'WC WebContent1 Content',
				webContentTitle: 'WC WebContent1 Title',
			});

			await viewUpgradedPartition({
				absentScreenName: 'test1',
				browser,
				documentTitle: 'DM Document2 Title',
				presentEmailAddress: 'test2@www.baker.com',
				presentName: 'Test2',
				presentScreenName: 'test2',
				roleTitle: 'Roles Regrole2 Name',
				virtualHostName: 'www.baker.com',
				webContentContent: 'WC WebContent2 Content',
				webContentTitle: 'WC WebContent2 Title',
			});
		}
	);
});
