/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Browser, expect, test} from '@playwright/test';

import {liferayConfig} from '../../../liferay.config';
import {DocumentLibraryPage} from '../../../pages/document-library-web/DocumentLibraryPage';
import {DocumentLibraryViewFileEntryPage} from '../../../pages/document-library-web/DocumentLibraryViewFileEntryPage';
import {WebContentPage} from '../../../pages/journal-web/WebContentPage';
import {RolesPage} from '../../../pages/roles-admin-web/RolesPage';
import {EditUserPage} from '../../../pages/users-admin-web/EditUserPage';
import {UsersAndOrganizationsPage} from '../../../pages/users-admin-web/UsersAndOrganizationsPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {performLoginViaApi} from '../../../utils/performLogin';

/**
 * One partition in the data-archive-portal-partition archive. Each partition is
 * reached by its own virtual host and holds one user, role, document and web
 * content, all named after the partition so that content served from the wrong
 * one is detectable.
 */
export type UpgradedPartition = {
	documentTitle: string;
	emailAddress: string;
	name: string;
	roleTitle: string;
	screenName: string;
	virtualHostName: string;
	webContentContent: string;
	webContentTitle: string;
};

/**
 * Asserts that every entity family the archive stores for one partition survived
 * the legacy database upgrade, and that the neighbouring partition's user is
 * absent from the same list. The absent half is what fails when partitioning
 * leaks; the present half passes either way, because both partitions hold
 * content of the same shape.
 */
export async function viewUpgradedPartition({
	absentPartition,
	browser,
	partition,
}: {
	absentPartition: UpgradedPartition;
	browser: Browser;
	partition: UpgradedPartition;
}) {
	const {
		documentTitle,
		emailAddress,
		name,
		roleTitle,
		screenName,
		virtualHostName,
		webContentContent,
		webContentTitle,
	} = partition;

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

			await usersAndOrganizationsPage.usersDataTable.search(screenName);

			const usersTableRowLink =
				await usersAndOrganizationsPage.usersTableRowLink(screenName);

			await usersTableRowLink.click();

			const editUserPage = new EditUserPage(page);

			await expect(editUserPage.screenNameInput).toHaveValue(screenName);

			await expect(editUserPage.emailAddressInput).toHaveValue(
				emailAddress
			);

			await expect(editUserPage.firstNameInput).toHaveValue(name);

			await expect(editUserPage.lastNameInput).toHaveValue(name);
		});

		await test.step(`Do not find the other partition's user on ${virtualHostName}`, async () => {
			const usersAndOrganizationsPage = new UsersAndOrganizationsPage(
				page
			);

			await usersAndOrganizationsPage.goto();

			await usersAndOrganizationsPage.usersDataTable.search(
				absentPartition.screenName
			);

			await expect(
				usersAndOrganizationsPage.noUsersMessage
			).toBeVisible();
		});

		await test.step(`View this partition's role on ${virtualHostName}`, async () => {
			const rolesPage = new RolesPage(page);

			await rolesPage.goto();

			await rolesPage.rolesTable.search(roleTitle);

			await expect(
				page.getByText(`1 Result Found for "${roleTitle}"`, {
					exact: true,
				})
			).toBeVisible();

			const roleLink = rolesPage.rolesTable.valueLink(roleTitle);

			await expect(roleLink).toBeVisible();

			await roleLink.click();

			await expect(rolesPage.rolePage.titleInput).toHaveValue(roleTitle);
		});

		await test.step(`View this partition's document on ${virtualHostName}`, async () => {
			const documentLibraryPage = new DocumentLibraryPage(page);

			await documentLibraryPage.goto();

			await page.getByRole('link', {name: documentTitle}).click();

			const headerTitle = page.getByTestId('headerTitle');

			await expect(headerTitle).toBeVisible();

			await expect(headerTitle).toHaveText(documentTitle);

			const documentLibraryViewFileEntryPage =
				new DocumentLibraryViewFileEntryPage(page);

			const sidebarHeader =
				documentLibraryViewFileEntryPage.infoPanel.locator(
					'.sidebar-header'
				);

			await clickAndExpectToBeVisible({
				target: sidebarHeader,
				timeout: 5000,
				trigger: documentLibraryViewFileEntryPage.infoButton,
			});

			await expect(sidebarHeader).toContainText(documentTitle);
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

			const contentEditor = contentField
				.frameLocator('iframe[title="editor"]')
				.getByRole('textbox');

			await expect(contentEditor).toBeVisible();

			await expect(contentEditor).toContainText(webContentContent);
		});
	}
	finally {
		await page.close();
	}
}
