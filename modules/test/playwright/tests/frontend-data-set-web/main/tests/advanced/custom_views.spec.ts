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

/**
 * Skip until we refactor custom views in LPD-10683. The tests here are correct,
 * but the feature is broken at least in few ways:
 * - interactions with client extensions are broken
 * - migration to ClayTable broke column visibility logic
 */
test.skip(
	'Create, edit and delete custom views',
	{
		tag: ['@LPS-130101'],
	},
	async ({fdsSamplePage, page}) => {
		let actionsDropdown: Locator;
		let customViewsDropdown: Locator;
		let columnsVisibilityDropdown: Locator;

		const customView1Name = getRandomString();
		const customView2Name = getRandomString();

		await test.step('Get dropdown references', async () => {

			// Click on dropdown toggle button adds the aria-controls attribute

			await fdsSamplePage.customViewsActionsButton.click();

			const actionsDropdownId =
				await fdsSamplePage.customViewsActionsButton.getAttribute(
					'aria-controls'
				);

			actionsDropdown = page.locator(`#${actionsDropdownId}`);

			page.keyboard.press('Escape');

			await fdsSamplePage.customViewsSelectorButton.click();

			const customViewsDropdownId =
				await fdsSamplePage.customViewsSelectorButton.getAttribute(
					'aria-controls'
				);

			customViewsDropdown = page.locator(`#${customViewsDropdownId}`);

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

		await test.step('Create a custom views and set it as the default one', async () => {
			await fdsSamplePage.customViewsActionsButton.click();

			await actionsDropdown
				.filter({has: page.getByRole('menu')})
				.waitFor();

			const menuItem = actionsDropdown.getByRole('menuitem', {
				name: 'Save View As...',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(fdsSamplePage.customViewsSaveModal).toBeInViewport();

			await fdsSamplePage.customViewsSaveModal
				.getByLabel('NameRequired')
				.fill(customView1Name);

			await fdsSamplePage.customViewsSaveModal
				.getByRole('button', {name: 'Save'})
				.click();

			await fdsSamplePage.customViewsActionsButton.click();

			await actionsDropdown
				.filter({has: page.getByRole('menu')})
				.waitFor();

			await actionsDropdown
				.getByRole('menuitem', {name: 'Save View As...'})
				.click();

			await expect(fdsSamplePage.customViewsSaveModal).toBeInViewport();

			await fdsSamplePage.customViewsSaveModal
				.getByLabel('NameRequired')
				.fill(customView2Name);
			await fdsSamplePage.customViewsSaveModal
				.getByRole('button', {name: 'Save'})
				.click();

			await expect(fdsSamplePage.customViewsSelectorButton).toHaveText(
				customView2Name
			);

			await fdsSamplePage.customViewsSelectorButton.click();

			await expect(customViewsDropdown.getByRole('option')).toHaveCount(
				3
			);
		});

		await test.step('Edit custom view, by changing visibility of one column', async () => {
			await expect(fdsSamplePage.table.headerCells).toHaveCount(10);

			await fdsSamplePage.table.manageColumnsVisibilityButton.click();

			await columnsVisibilityDropdown
				.getByRole('menuitem', {name: 'Description'})
				.click();

			page.keyboard.press('Escape');

			await expect(fdsSamplePage.table.headerCells).toHaveCount(9);
		});

		await test.step('Confirm that changes in a custom view does not affect Default View', async () => {
			await expect(fdsSamplePage.customViewsSelectorButton).toHaveText(
				customView2Name
			);

			await expect(fdsSamplePage.table.headerCells).toHaveCount(9);

			await fdsSamplePage.customViewsSelectorButton.click();

			await customViewsDropdown.waitFor();

			await customViewsDropdown
				.getByRole('option', {name: 'Default View'})
				.click();

			await expect(fdsSamplePage.table.headerCells).toHaveCount(10);
		});

		await test.step('Can change a custom view name', async () => {
			await fdsSamplePage.customViewsSelectorButton.click();

			await customViewsDropdown.waitFor();

			await customViewsDropdown
				.getByRole('option', {name: customView2Name})
				.click();

			await fdsSamplePage.customViewsActionsButton.click();

			await actionsDropdown.waitFor();

			const menuItem = actionsDropdown.getByRole('menuitem', {
				name: 'Rename View',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(fdsSamplePage.customViewsSaveModal).toBeInViewport();

			const newCustomViewName = getRandomString();

			await fdsSamplePage.customViewsSaveModal
				.getByLabel('NameRequired')
				.fill(newCustomViewName);

			await fdsSamplePage.customViewsSaveModal
				.getByRole('button', {name: 'Save'})
				.click();

			await expect(fdsSamplePage.customViewsSelectorButton).toHaveText(
				newCustomViewName
			);
		});

		await test.step('Delete a custom view', async () => {
			await fdsSamplePage.customViewsSelectorButton.click();

			await customViewsDropdown.waitFor();

			await customViewsDropdown
				.getByRole('option', {name: customView1Name})
				.click();

			await fdsSamplePage.customViewsActionsButton.click();

			await actionsDropdown.waitFor();

			const menuItem = actionsDropdown.getByRole('menuitem', {
				name: 'Delete View',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(fdsSamplePage.customViewsDeleteAlert).toBeVisible();

			await fdsSamplePage.customViewsDeleteAlert
				.getByRole('button', {name: 'Delete'})
				.click();

			await fdsSamplePage.customViewsSelectorButton.click();

			await customViewsDropdown.waitFor();

			await expect(
				customViewsDropdown.getByRole('option', {name: customView1Name})
			).not.toBeVisible();
		});
	}
);
