/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import getRandomString from '../../../../../utils/getRandomString';
import {EFDSVisualizationMode, waitForFDS} from '../../../../../utils/waitFor';
import {waitForAlert} from '../../../../../utils/waitForAlert';
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

test.afterEach(async ({apiHelpers}) => {
	const response = (await apiHelpers.get(
		'/o/data-set-admin/snapshots?page=1&pageSize=100'
	)) as {items?: Array<{id?: number}>};

	for (const snapshot of response?.items || []) {
		if (snapshot.id) {
			await apiHelpers.delete(
				`/o/data-set-admin/snapshots/${snapshot.id}`
			);
		}
	}
});

test(
	'Create, edit and delete user views',
	{
		tag: ['@LPS-130101'],
	},
	async ({fdsSamplePage, page}) => {
		const newUserViewName = getRandomString();
		const userView1Name = getRandomString();
		const userView2Name = getRandomString();

		await test.step('Create a user view and set it as the default one', async () => {
			await fdsSamplePage.userViewsActionsButton.click();

			const menuItem = fdsSamplePage.dropdownMenu.getByRole('menuitem', {
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

			await fdsSamplePage.dropdownMenu
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
				await fdsSamplePage.dropdownMenu.getByRole('menuitem').count()
			).toBeGreaterThanOrEqual(3);
		});

		await test.step('Edit user view, by changing visibility of one column', async () => {
			await expect(fdsSamplePage.table.headerCells).toHaveCount(11);

			await fdsSamplePage.table.manageColumnsVisibilityButton.click();

			await fdsSamplePage.dropdownMenu
				.getByRole('menuitem', {name: 'Description'})
				.click();

			page.keyboard.press('Escape');

			await expect(fdsSamplePage.table.headerCells).toHaveCount(10);
		});

		await test.step('Confirm that changes in a user view does not affect Default View', async () => {
			await expect(fdsSamplePage.userViewsSelectorButton).toHaveText(
				`${userView2Name}${userView2Name} Updated`
			);

			await expect(fdsSamplePage.table.headerCells).toHaveCount(10);

			await fdsSamplePage.userViewsSelectorButton.click();

			await fdsSamplePage.dropdownMenu
				.getByRole('menuitem', {name: 'Default View'})
				.click();

			await expect(fdsSamplePage.table.headerCells).toHaveCount(11);
		});

		await test.step('Can not rename a user view to a blank or duplicate name', async () => {
			await fdsSamplePage.userViewsSelectorButton.click();

			await fdsSamplePage.dropdownMenu.waitFor();

			await fdsSamplePage.dropdownMenu
				.getByRole('menuitem', {name: userView2Name})
				.click();

			await fdsSamplePage.userViewsActionsButton.click();

			await fdsSamplePage.dropdownMenu.waitFor();

			const menuItem = fdsSamplePage.dropdownMenu.getByRole('menuitem', {
				name: 'Rename View',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(fdsSamplePage.userViewsRenameModal).toBeInViewport();

			const nameInput =
				fdsSamplePage.userViewsRenameModal.getByLabel('NameRequired');
			const saveButton = fdsSamplePage.userViewsRenameModal.getByRole(
				'button',
				{name: 'Save'}
			);

			await nameInput.fill('   ');

			await expect(saveButton).toBeDisabled();

			await nameInput.fill(userView1Name);

			await expect(saveButton).toBeEnabled();

			await saveButton.click();

			await expect(
				fdsSamplePage.userViewsRenameModal.getByText(
					'A view with this name already exists.'
				)
			).toBeVisible();

			await fdsSamplePage.userViewsRenameModal
				.getByRole('button', {name: 'Cancel'})
				.click();
		});

		await test.step('Can change a user view name', async () => {
			await fdsSamplePage.userViewsSelectorButton.click();

			await fdsSamplePage.dropdownMenu
				.getByRole('menuitem', {name: userView2Name})
				.click();

			await fdsSamplePage.userViewsActionsButton.click();

			const menuItem = fdsSamplePage.dropdownMenu.getByRole('menuitem', {
				name: 'Rename View',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(fdsSamplePage.userViewsRenameModal).toBeInViewport();

			await fdsSamplePage.userViewsRenameModal
				.getByLabel('NameRequired')
				.fill(newUserViewName);

			await fdsSamplePage.userViewsRenameModal
				.getByRole('button', {name: 'Save'})
				.click();

			await expect(fdsSamplePage.userViewsSelectorButton).toHaveText(
				newUserViewName
			);
		});

		await test.step('Delete a user view', async () => {
			await fdsSamplePage.userViewsSelectorButton.click();

			await fdsSamplePage.dropdownMenu
				.getByRole('menuitem', {name: userView1Name})
				.click();

			await fdsSamplePage.userViewsActionsButton.click();

			const menuItem = fdsSamplePage.dropdownMenu.getByRole('menuitem', {
				name: 'Delete View',
			});

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(fdsSamplePage.userViewsDeleteAlert).toBeVisible();

			await fdsSamplePage.userViewsDeleteAlert
				.getByRole('button', {name: 'Delete'})
				.click();

			await fdsSamplePage.userViewsSelectorButton.click();

			await expect(
				fdsSamplePage.dropdownMenu.getByRole('menuitem', {
					name: userView1Name,
				})
			).not.toBeVisible();

			await fdsSamplePage.dropdownMenu
				.getByRole('menuitem', {name: newUserViewName})
				.click();

			await fdsSamplePage.userViewsActionsButton.click();

			await expect(menuItem).toBeVisible();

			await menuItem.click();

			await expect(fdsSamplePage.userViewsDeleteAlert).toBeVisible();

			await fdsSamplePage.userViewsDeleteAlert
				.getByRole('button', {name: 'Delete'})
				.click();

			await fdsSamplePage.userViewsSelectorButton.click();

			await expect(
				fdsSamplePage.dropdownMenu.getByRole('menuitem', {
					name: newUserViewName,
				})
			).not.toBeVisible();
		});
	}
);

test(
	'Set a user view as the startup view and apply it on reload',
	{
		tag: ['@LPD-75910'],
	},
	async ({fdsSamplePage, page}) => {
		const userViewName = getRandomString();

		await test.step('Create a user view', async () => {
			await fdsSamplePage.userViewsActionsButton.click();

			await fdsSamplePage.dropdownMenu
				.getByRole('menuitem', {name: 'Save View As...'})
				.click();

			await expect(fdsSamplePage.userViewsSaveModal).toBeInViewport();

			await fdsSamplePage.userViewsSaveModal
				.getByLabel('NameRequired')
				.fill(userViewName);

			await fdsSamplePage.userViewsSaveModal
				.getByRole('button', {name: 'Save'})
				.click();

			await expect(fdsSamplePage.userViewsSelectorButton).toHaveText(
				userViewName
			);
		});

		await test.step('Set as Startup View shows a success message and a badge', async () => {
			await fdsSamplePage.userViewsActionsButton.click();

			await fdsSamplePage.dropdownMenu
				.getByRole('menuitem', {name: 'Set as Startup View'})
				.click();

			await waitForAlert(page, 'The user view was set as startup');

			await fdsSamplePage.userViewsSelectorButton.click();

			await expect(
				fdsSamplePage.dropdownMenu.getByText('Startup View')
			).toBeVisible();

			await page.keyboard.press('Escape');
		});

		await test.step('Set as Startup View is hidden once the active view is the startup view', async () => {
			await fdsSamplePage.userViewsActionsButton.click();

			await expect(
				fdsSamplePage.dropdownMenu.getByRole('menuitem', {
					name: 'Set as Startup View',
				})
			).not.toBeVisible();

			await page.keyboard.press('Escape');
		});

		await test.step('The startup view is applied on reload', async () => {
			await page.reload();

			await waitForFDS({
				page,
				visualizationMode: EFDSVisualizationMode.TABLE,
			});

			await expect(fdsSamplePage.userViewsSelectorButton).toHaveText(
				userViewName
			);
		});
	}
);

test(
	'Change the startup view to another user view and apply it on reload',
	{
		tag: ['@LPD-75910'],
	},
	async ({fdsSamplePage, page}) => {
		const firstUserViewName = getRandomString();
		const secondUserViewName = getRandomString();

		const createUserView = async (name: string) => {
			await fdsSamplePage.userViewsActionsButton.click();

			await fdsSamplePage.dropdownMenu
				.getByRole('menuitem', {name: 'Save View As...'})
				.click();

			await expect(fdsSamplePage.userViewsSaveModal).toBeInViewport();

			await fdsSamplePage.userViewsSaveModal
				.getByLabel('NameRequired')
				.fill(name);

			await fdsSamplePage.userViewsSaveModal
				.getByRole('button', {name: 'Save'})
				.click();

			await expect(fdsSamplePage.userViewsSelectorButton).toHaveText(
				name
			);
		};

		const selectUserView = async (name: string) => {
			await fdsSamplePage.userViewsSelectorButton.click();

			await fdsSamplePage.dropdownMenu
				.getByRole('menuitem', {name})
				.click();
		};

		const setAsStartupView = async () => {
			await fdsSamplePage.userViewsActionsButton.click();

			await fdsSamplePage.dropdownMenu
				.getByRole('menuitem', {name: 'Set as Startup View'})
				.click();

			await waitForAlert(page, 'The user view was set as startup');
		};

		await test.step('Set the first user view as the startup view', async () => {
			await createUserView(firstUserViewName);
			await createUserView(secondUserViewName);

			await selectUserView(firstUserViewName);

			await setAsStartupView();
		});

		await test.step('Change the startup view to the second user view', async () => {
			await selectUserView(secondUserViewName);

			await setAsStartupView();
		});

		await test.step('The most recently set startup view is applied on reload', async () => {
			await page.reload();

			await waitForFDS({
				page,
				visualizationMode: EFDSVisualizationMode.TABLE,
			});

			await expect(fdsSamplePage.userViewsSelectorButton).toContainText(
				secondUserViewName
			);
		});
	}
);
