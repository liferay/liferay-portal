/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {EditUserPage} from '../../../pages/users-admin-web/EditUserPage';
import {UsersAndOrganizationsPage} from '../../../pages/users-admin-web/UsersAndOrganizationsPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';

/**
 * One virtual instance in the data-archive-virtual-instances archive. The three
 * suffixes are separate fields because the archive uses three different forms
 * for the same instance: the page path takes "-2", the content titles take
 * " 2", and the document title takes "2". A single index cannot express them.
 */
export type UpgradedVirtualInstance = {
	documentTitle: string;
	nameSuffix: string;
	pathSuffix: string;
	userIndex: string;
	webId: string;
};

/**
 * Asserts that every entity family the archive stores for one virtual instance
 * survived the legacy database upgrade, and that the neighbouring instance's
 * content is absent from the same pages. The absent half is what fails when a
 * navigation lands on the wrong instance; the present half passes either way,
 * because all three instances hold content of the same shape.
 */
export async function viewUpgradedVirtualInstance({
	absentInstance,
	instance,
	instanceURL,
	page,
	usersAndOrganizationsPage,
}: {
	absentInstance: UpgradedVirtualInstance;
	instance: UpgradedVirtualInstance;
	instanceURL: string;
	page: Page;
	usersAndOrganizationsPage: UsersAndOrganizationsPage;
}) {
	const {documentTitle, nameSuffix, pathSuffix, userIndex, webId} = instance;

	const absentNameSuffix = absentInstance.nameSuffix;
	const absentUserIndex = absentInstance.userIndex;

	// instanceURL is empty for the default instance, which is reached through
	// the project baseURL, and an absolute origin for the other two, which are
	// reached by virtual host. Every navigation below is prefixed with it so one
	// helper serves all three.

	const sitePath = `${instanceURL}/web/guest`;

	await page.goto(`${sitePath}/web-content${pathSuffix}`);

	await expect(
		page.getByText(`Web Content Title${nameSuffix}`, {exact: true})
	).toBeVisible();

	await expect(
		page.getByText(`Web Content Content${nameSuffix}`, {exact: true})
	).toBeVisible();

	await expect(
		page.getByText(`Web Content Title${absentNameSuffix}`, {exact: true})
	).toBeHidden();

	await page.goto(`${sitePath}/document${pathSuffix}`);

	await page.getByRole('link', {name: documentTitle}).click();

	const downloadLink = page
		.locator('.sidebar-section')
		.getByRole('link', {name: 'Download'});

	await clickAndExpectToBeVisible({
		target: downloadLink,
		timeout: 5000,
		trigger: page.locator('a[href*=infoPanel]'),
	});

	const userName = page.locator('.sidebar-body .username');
	const version = page.locator('.sidebar-header .label-item');
	const workflowStatus = page.locator('.sidebar-header .workflow-status');

	await expect(userName).toBeVisible();
	await expect(userName).toHaveText('Test Test');

	await expect(version).toBeVisible();
	await expect(version).toHaveText('Version 1.0');

	await expect(workflowStatus).toBeVisible();
	await expect(workflowStatus).toHaveText('Approved');

	await page.goto(`${sitePath}/message-boards${pathSuffix}`);

	const threadLink = page.getByRole('link', {
		name: `Message Boards Subject${nameSuffix}`,
	});

	await expect(threadLink).toBeVisible();

	const threadRow = page
		.getByTestId('row')
		.filter({hasText: `Message Boards Subject${nameSuffix}`});

	await expect(
		threadRow.locator('.lfr-portal-tooltip[title="0 Replies"]')
	).toBeVisible();

	await expect(threadRow.getByText('Test Test')).toBeVisible();

	const threadURL = await threadLink.getAttribute('href');

	expect(threadURL).not.toBeNull();

	// The href is relative on the default instance and absolute on a virtual
	// host, so it is resolved against the current page rather than concatenated.

	await page.goto(new URL(threadURL as string, page.url()).toString());

	await expect(
		page.getByRole('heading', {name: `Message Boards Subject${nameSuffix}`})
	).toBeVisible();

	await expect(
		page.getByText(`Message Boards Body${nameSuffix}`, {exact: true})
	).toBeVisible();

	await page.goto(`${sitePath}/wiki${pathSuffix}`);

	await expect(page.getByRole('heading', {name: 'FrontPage'})).toBeVisible();

	await expect(
		page.getByText(`Wiki Front Page Content${nameSuffix}`, {exact: true})
	).toBeVisible();

	await expect(
		page.getByText(`Wiki Front Page Content${absentNameSuffix}`, {
			exact: true,
		})
	).toBeHidden();

	await page.goto(`${sitePath}/blogs${pathSuffix}`);

	await expect(
		page.getByText(`Blogs Entry Title${nameSuffix}`, {exact: true})
	).toBeVisible();

	await expect(
		page.getByText(`Blogs Entry Content${nameSuffix}`, {exact: true})
	).toBeVisible();

	await expect(
		page.getByText(`Blogs Entry Title${absentNameSuffix}`, {exact: true})
	).toBeHidden();

	await usersAndOrganizationsPage.goto();

	const userRow = page
		.getByRole('row')
		.filter({hasText: `usersn${userIndex}`});

	await expect(userRow).toBeVisible();

	await expect(userRow).toContainText(
		`userfn${userIndex} userln${userIndex}`
	);

	await expect(
		page.getByRole('row').filter({hasText: `usersn${absentUserIndex}`})
	).toBeHidden();

	await usersAndOrganizationsPage.usersDataTable.search(`usersn${userIndex}`);

	const usersTableRowLink = await usersAndOrganizationsPage.usersTableRowLink(
		`usersn${userIndex}`
	);

	await usersTableRowLink.click();

	const editUserPage = new EditUserPage(page);

	await expect(editUserPage.emailAddressInput).toHaveValue(
		`userea${userIndex}@${webId}`
	);
}
