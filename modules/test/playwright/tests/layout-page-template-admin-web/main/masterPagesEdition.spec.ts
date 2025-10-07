/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {masterPagesPagesTest} from '../../../fixtures/masterPagesPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {goToObjectEntity} from '../../setup/page-management-site/main/utils/goToObjectEntity';

export const test = mergeTests(
	apiHelpersTest,
	pagesAdminPagesTest,
	isolatedSiteTest,
	loginTest(),
	masterPagesPagesTest,
	pageEditorPagesTest,
	pageManagementSiteTest
);

test(
	'Categories field can be used in a master page',
	{
		tag: '@LPS-161638',
	},
	async ({
		apiHelpers,
		masterPagesPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create new master page

		const layoutPageTemplateEntryName = getRandomString();

		const masterPage =
			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
				{
					groupId: pageManagementSite.id,
					name: layoutPageTemplateEntryName,
					type: 'master-layout',
				}
			);

		// Edit master page

		await masterPagesPage.goto(pageManagementSite.friendlyUrlPath);

		await masterPagesPage.editMaster(layoutPageTemplateEntryName);

		// Add a form container and map it

		await pageEditorPage.addFragment('Form Components', 'Form Container');

		const fragmentId = await pageEditorPage.getFragmentId('Form Container');

		await pageEditorPage.mapFormFragment(fragmentId, 'Lemon', [
			'Lemon Size',
		]);

		// Add categories form component

		await pageEditorPage.addFragment(
			'Form Components',
			'Categories',
			page.locator('.page-editor__form .page-editor__container')
		);

		// Publish master page

		await pageEditorPage.publishPage();

		// Create a layout with created master page

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: pageManagementSite.id,
			masterLayoutPlid: masterPage.plid,
			title: getRandomString(),
		});

		// Go to view mode

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyURL}`
		);

		// Select categories and submit the form

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('heading', {name: 'Select Animals'}),
			trigger: page.getByLabel('Select Animals'),
		});

		const iframe = page.frameLocator('iframe[title="Select Animals"]');

		await iframe
			.locator('li')
			.filter({hasText: 'Cats'})
			.getByRole('checkbox')
			.check();
		await iframe
			.locator('li')
			.filter({hasText: 'Dogs'})
			.getByRole('checkbox')
			.check();

		await page.getByRole('button', {name: 'Done'}).click();

		await page.locator('input[name="ObjectField_lemonSize"]').fill('Small');

		await page.getByRole('button', {name: 'Submit'}).click();

		// Assert success message

		await expect(
			page.getByText(
				'Thank you. Your information was successfully received.'
			)
		).toBeVisible();

		// Go to custom object admin

		await goToObjectEntity({
			entityName: 'Lemon',
			page,
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				exact: true,
				name: 'View',
			}),
			trigger: page
				.locator('.fds tbody .cell-item-actions .dropdown-toggle')
				.first(),
		});

		await expect(page.getByText('Cats')).toBeVisible();
		await expect(page.getByText('Dogs')).toBeVisible();
	}
);

test('Items containing the drop zone cannot be duplicated or copied', async ({
	masterPagesPage,
	page,
	pageEditorPage,
	site,
}) => {

	// Create new master page

	const name = getRandomString();

	await masterPagesPage.goto(site.friendlyUrlPath);

	await masterPagesPage.createNewMaster(name);

	// Edit it, and place the drop zone inside a container

	await masterPagesPage.editMaster(name);

	await pageEditorPage.addFragment('Layout Elements', 'Container');

	await pageEditorPage.dragTreeNode({
		position: 'middle',
		source: {label: 'Drop Zone'},
		target: {label: 'Container'},
	});

	// Check the container only have Rename action and not Duplicate or Copy

	await page.getByLabel('Select Container').click();

	await page
		.locator('.treeview-link', {hasText: 'Container'})
		.getByLabel('Options')
		.click();

	await expect(page.getByText('Rename')).toBeVisible();

	await expect(page.getByText('Duplicate')).not.toBeVisible();
	await expect(page.getByText('Copy')).not.toBeVisible();
});
