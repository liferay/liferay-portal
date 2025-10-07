/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../../../fixtures/usersAndOrganizationsPagesTest';
import {EFDSVisualizationMode, waitForFDS} from '../../../../../utils/waitFor';
import {fdsSamplePageTest} from '../../fixtures/fdsSamplePageTest';

const test = mergeTests(
	dataApiHelpersTest,
	fdsSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	usersAndOrganizationsPagesTest
);

let fdsSamplePageURL: string;

test.beforeEach(async ({fdsSamplePage, page, site}) => {
	const {url} = await fdsSamplePage.setupFDSSampleWidget({site});

	fdsSamplePageURL = url;

	await fdsSamplePage.selectTab('Classic');

	await waitForFDS({page, visualizationMode: EFDSVisualizationMode.TABLE});
});

test('Check behavior of conditional item actions', async ({
	apiHelpers,
	fdsSamplePage,
	page,
	usersAndOrganizationsPage,
}) => {
	let managerUserAccount: TUserAccount;
	let guestUserAccount: TUserAccount;
	let managerUserItemActionsButton: Locator;

	let guestUserItemActionsButton: Locator;

	await test.step('Create sample users', async () => {
		managerUserAccount = await apiHelpers.headlessAdminUser.postUserAccount(
			{
				emailAddress: 'manager.user@liferay.com',
				givenName: 'Anne',
			}
		);

		guestUserAccount = await apiHelpers.headlessAdminUser.postUserAccount({
			emailAddress: 'guest.user@liferay.com',
			givenName: 'Lewis',
		});

		managerUserItemActionsButton = fdsSamplePage.selectItemActionsByRow(
			managerUserAccount.emailAddress
		);

		guestUserItemActionsButton = fdsSamplePage.selectItemActionsByRow(
			guestUserAccount.emailAddress
		);

		await page.reload();
	});

	await test.step('Check that the Item Actions dropdown is present in table row', async () => {
		await expect(fdsSamplePage.table.container).toBeVisible();

		expect(
			await fdsSamplePage.table.bodyRows.count()
		).toBeGreaterThanOrEqual(3);

		const itemActionButton = fdsSamplePage.table.itemActionButtons.first();

		await expect(itemActionButton).toBeVisible();

		const dropdownId = await itemActionButton.getAttribute('aria-controls');

		await itemActionButton.click();

		await page
			.locator(`#${dropdownId}`)
			.filter({has: page.getByRole('menu')})
			.waitFor();

		await expect(page.locator(`#${dropdownId}`)).toBeVisible();

		await page.keyboard.press('Escape');
	});

	await test.step('Check that users have different item actions', async () => {
		await expect(managerUserItemActionsButton).toBeVisible();

		const dropdownId =
			await managerUserItemActionsButton.getAttribute('aria-controls');

		await managerUserItemActionsButton.click();

		await page
			.locator(`#${dropdownId}`)
			.filter({has: page.getByRole('menu')})
			.waitFor();

		await expect(
			page.locator(`#${dropdownId}`).getByRole('menuitem')
		).toHaveCount(4);

		await expect(
			page.locator(`#${dropdownId}`).getByRole('menuitem')
		).toHaveText(['Book', 'Job Archive', 'Deactivate', 'Activity']);

		await page.keyboard.press('Escape');

		await expect(guestUserItemActionsButton).toBeVisible();

		const guestUserdropdownId =
			await guestUserItemActionsButton.getAttribute('aria-controls');

		await guestUserItemActionsButton.click();

		await page
			.locator(`#${guestUserdropdownId}`)
			.filter({has: page.getByRole('menu')})
			.waitFor();

		await expect(
			page.locator(`#${guestUserdropdownId}`).getByRole('menuitem')
		).toHaveCount(3);

		await expect(
			page.locator(`#${guestUserdropdownId}`).getByRole('menuitem')
		).toHaveText(['Book', 'Job Archive', 'Deactivate']);

		await page.keyboard.press('Escape');
	});

	await test.step('Deactivate guest user', async () => {

		// Need this listener to auto accept confirm dialog

		page.on('dialog', async (dialog) => await dialog.accept());

		await usersAndOrganizationsPage.goToUsers();

		await usersAndOrganizationsPage.deActivateUsers([
			guestUserAccount.name,
		]);
	});

	await test.step('Check actions for active and inactive users', async () => {
		await page.goto(fdsSamplePageURL);
		await fdsSamplePage.selectTab('Classic');

		await expect(fdsSamplePage.table.container).toBeVisible();

		await expect(managerUserItemActionsButton).toBeVisible();

		const dropdownId =
			await managerUserItemActionsButton.getAttribute('aria-controls');

		await managerUserItemActionsButton.click();

		await page
			.locator(`#${dropdownId}`)
			.filter({has: page.getByRole('menu')})
			.waitFor();

		await expect(
			page.locator(`#${dropdownId}`).getByRole('menuitem')
		).toHaveCount(4);

		await expect(
			page.locator(`#${dropdownId}`).getByRole('menuitem')
		).toHaveText(['Book', 'Job Archive', 'Deactivate', 'Activity']);

		await page.keyboard.press('Escape');

		await expect(guestUserItemActionsButton).toBeVisible();

		const guestUserdropdownId =
			await guestUserItemActionsButton.getAttribute('aria-controls');

		await guestUserItemActionsButton.click();

		await page
			.locator(`#${guestUserdropdownId}`)
			.filter({has: page.getByRole('menu')})
			.waitFor();

		await expect(
			page.locator(`#${guestUserdropdownId}`).getByRole('menuitem')
		).toHaveCount(3);

		await expect(
			page.locator(`#${guestUserdropdownId}`).getByRole('menuitem')
		).toHaveText(['Book', 'Job Archive', 'Activate']);

		await page.keyboard.press('Escape');
	});
});
