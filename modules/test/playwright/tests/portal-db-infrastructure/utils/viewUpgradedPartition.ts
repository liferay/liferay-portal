/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Browser, expect, test} from '@playwright/test';

import {liferayConfig} from '../../../liferay.config';
import {DocumentLibraryPage} from '../../../pages/document-library-web/DocumentLibraryPage';
import {WebContentPage} from '../../../pages/journal-web/WebContentPage';
import {RolesPage} from '../../../pages/roles-admin-web/RolesPage';
import {EditUserPage} from '../../../pages/users-admin-web/EditUserPage';
import {UsersAndOrganizationsPage} from '../../../pages/users-admin-web/UsersAndOrganizationsPage';
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

	const absentScreenName = absentPartition.screenName;

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
				absentScreenName
			);

			await expect(
				usersAndOrganizationsPage.noUsersMessage
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

			const sidebarHeader = page.locator('.sidebar-header');

			await expect(sidebarHeader).toBeVisible();

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

			await expect(contentField).toContainText(webContentContent);
		});
	}
	finally {
		await page.close();
	}
}
