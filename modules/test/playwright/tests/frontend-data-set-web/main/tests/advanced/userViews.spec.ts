/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import getRandomString from '../../../../../utils/getRandomString';
import {EFDSVisualizationMode, waitForFDS} from '../../../../../utils/waitFor';
import {fdsSamplePageTest} from '../../fixtures/fdsSamplePageTest';

const test = mergeTests(
	apiHelpersTest,
	fdsSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

test.beforeEach(async ({fdsSamplePage, page, site}) => {
	await fdsSamplePage.setupFDSSampleWidget({site});

	await fdsSamplePage.selectTab('Advanced');

	await waitForFDS({page, visualizationMode: EFDSVisualizationMode.TABLE});
});

test(
	'Create, edit and delete user views',
	{
		tag: ['@LPS-130101'],
	},
	async ({fdsSamplePage, page}) => {
		let actionsDropdown: Locator;
		let userViewsDropdown: Locator;
		let columnsVisibilityDropdown: Locator;

		const newUserViewName = getRandomString();
		const userView1Name = getRandomString();
		const userView2Name = getRandomString();

		await test.step('Get dropdown references', async () => {

			// Click on dropdown toggle button adds the aria-controls attribute

			await fdsSamplePage.userViewsActionsButton.click();

			const actionsDropdownId =
				await fdsSamplePage.userViewsActionsButton.getAttribute(
					'aria-controls'
				);

			actionsDropdown = page.locator(`#${actionsDropdownId}`);

			page.keyboard.press('Escape');

			await fdsSamplePage.userViewsSelectorButton.click();

			const userViewsDropdownId =
				await fdsSamplePage.userViewsSelectorButton.getAttribute(
					'aria-controls'
				);

			userViewsDropdown = page.locator(`#${userViewsDropdownId}`);

			page.keyboard.press('Escape');

			await fdsSamplePage.table.manageColumnsVisibilityButton.click();

			const columnsVisibilityDropdownId =
				await fdsSamplePage.table.manageColumnsVisibilityButton.getAttribute(
					'aria-controls'
				);

			columnsVisibilityDropdown = page.locator(
				`#${columnsVisibilityDropdownId}`
			);

			page.keyboard.press('Escape');
		});

		await test.step('Create a user view and set it as the default one', async () => {
			await fdsSamplePage.userViewsActionsButton.click();

			await actionsDropdown
				.filter({has: page.getByRole('menu')})
				.waitFor();

			const menuItem = actionsDropdown.getByRole('menuitem', {
				name: 'Save View As...',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(fdsSamplePage.userViewsSaveModal).toBeInViewport();

			await fdsSamplePage.userViewsSaveModal
				.getByLabel('NameRequired')
				.fill(userView1Name);

			await fdsSamplePage.userViewsSaveModal
				.getByRole('button', {name: 'Save'})
				.click();

			await fdsSamplePage.userViewsActionsButton.click();

			await actionsDropdown
				.filter({has: page.getByRole('menu')})
				.waitFor();

			await actionsDropdown
				.getByRole('menuitem', {name: 'Save View As...'})
				.click();

			await expect(fdsSamplePage.userViewsSaveModal).toBeInViewport();

			await fdsSamplePage.userViewsSaveModal
				.getByLabel('NameRequired')
				.fill(userView2Name);
			await fdsSamplePage.userViewsSaveModal
				.getByRole('button', {name: 'Save'})
				.click();

			await expect(fdsSamplePage.userViewsSelectorButton).toHaveText(
				userView2Name
			);

			await fdsSamplePage.userViewsSelectorButton.click();

			expect(
				await userViewsDropdown.getByRole('option').count()
			).toBeGreaterThanOrEqual(3);
		});

		await test.step('Edit user view, by changing visibility of one column', async () => {
			await expect(fdsSamplePage.table.headerCells).toHaveCount(10);

			await fdsSamplePage.table.manageColumnsVisibilityButton.click();

			await columnsVisibilityDropdown
				.getByRole('menuitem', {name: 'Description'})
				.click();

			page.keyboard.press('Escape');

			await expect(fdsSamplePage.table.headerCells).toHaveCount(9);
		});

		await test.step('Confirm that changes in a user view does not affect Default View', async () => {
			await expect(fdsSamplePage.userViewsSelectorButton).toHaveText(
				`${userView2Name}${userView2Name} Updated`
			);

			await expect(fdsSamplePage.table.headerCells).toHaveCount(9);

			await fdsSamplePage.userViewsSelectorButton.click();

			await userViewsDropdown.waitFor();

			await userViewsDropdown
				.getByRole('option', {name: 'Default View'})
				.click();

			await expect(fdsSamplePage.table.headerCells).toHaveCount(10);
		});

		await test.step('Can change a user view name', async () => {
			await fdsSamplePage.userViewsSelectorButton.click();

			await userViewsDropdown.waitFor();

			await userViewsDropdown
				.getByRole('option', {name: userView2Name})
				.click();

			await fdsSamplePage.userViewsActionsButton.click();

			await actionsDropdown.waitFor();

			const menuItem = actionsDropdown.getByRole('menuitem', {
				name: 'Rename View',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(fdsSamplePage.userViewsSaveModal).toBeInViewport();

			await fdsSamplePage.userViewsSaveModal
				.getByLabel('NameRequired')
				.fill(newUserViewName);

			await fdsSamplePage.userViewsSaveModal
				.getByRole('button', {name: 'Save'})
				.click();

			await expect(fdsSamplePage.userViewsSelectorButton).toHaveText(
				newUserViewName
			);
		});

		await test.step('Delete a user view', async () => {
			await fdsSamplePage.userViewsSelectorButton.click();

			await userViewsDropdown.waitFor();

			await userViewsDropdown
				.getByRole('option', {name: userView1Name})
				.click();

			await fdsSamplePage.userViewsActionsButton.click();

			await actionsDropdown.waitFor();

			const menuItem = actionsDropdown.getByRole('menuitem', {
				name: 'Delete View',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(fdsSamplePage.userViewsDeleteAlert).toBeVisible();

			await fdsSamplePage.userViewsDeleteAlert
				.getByRole('button', {name: 'Delete'})
				.click();

			await fdsSamplePage.userViewsSelectorButton.click();

			await userViewsDropdown.waitFor();

			await expect(
				userViewsDropdown.getByRole('option', {name: userView1Name})
			).not.toBeVisible();

			await userViewsDropdown
				.getByRole('option', {name: newUserViewName})
				.click();

			await fdsSamplePage.userViewsActionsButton.click();

			await actionsDropdown.waitFor();

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(fdsSamplePage.userViewsDeleteAlert).toBeVisible();

			await fdsSamplePage.userViewsDeleteAlert
				.getByRole('button', {name: 'Delete'})
				.click();

			await fdsSamplePage.userViewsSelectorButton.click();

			await userViewsDropdown.waitFor();

			await expect(
				userViewsDropdown.getByRole('option', {name: newUserViewName})
			).not.toBeVisible();
		});
	}
);
