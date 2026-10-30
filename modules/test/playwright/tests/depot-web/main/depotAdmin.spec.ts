/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {waitForAlert} from '../../../utils/waitForAlert';
import {DepotAdminPage} from './pages/DepotAdminPage';

const test = mergeTests(dataApiHelpersTest, loginTest());

const DEPOT_ADMIN_NAMESPACE =
	'_com_liferay_depot_web_portlet_DepotAdminPortlet_';

function acceptDeleteConfirmation(page: Page) {
	return new Promise<string>((resolve) => {
		page.once('dialog', async (dialog) => {
			const message = dialog.message();

			await dialog.accept();

			resolve(message);
		});
	});
}

function getCard(page: Page, name: string) {
	return page
		.locator('.card')
		.filter({has: page.getByRole('link', {exact: true, name})});
}

async function gotoDepotAdmin(page: Page, keywords?: string) {
	if (!keywords) {
		await page.goto(`/group/guest${PORTLET_URLS.depotAdmin}`);

		return;
	}

	await page.goto(
		`/group/guest${PORTLET_URLS.depotAdmin}&${DEPOT_ADMIN_NAMESPACE}keywords=${encodeURIComponent(keywords)}`
	);

	await expect(page.getByText(/Results? Found for/)).toBeVisible();
}

async function openAddForm(page: Page) {
	await clickAndExpectToBeVisible({
		target: page.getByRole('textbox', {name: 'Name'}),
		trigger: page.getByRole('button', {exact: true, name: 'Add'}),
	});
}

test(
	'Asset library can be added and renamed from its settings, and its name must stay unique',
	{tag: ['@LPD-109885', '@LPS-146116']},
	async ({apiHelpers, page}) => {
		const name = `${getRandomString()} & Co`;

		let assetLibraryId: string;

		await test.step('Add an asset library with a description', async () => {
			await gotoDepotAdmin(page);

			await expect(
				page
					.getByLabel('Control Menu')
					.getByRole('button', {name: 'Options'})
			).toHaveCount(0);

			await openAddForm(page);

			await page.getByRole('textbox', {name: 'Name'}).fill(name);

			await page.getByRole('button', {exact: true, name: 'Save'}).click();

			await waitForAlert(page, 'Asset Library was added.');

			await expect(
				page.getByLabel('Control Menu').getByText(name, {exact: true})
			).toBeVisible();

			assetLibraryId = await page
				.getByRole('textbox', {name: 'Asset Library ID'})
				.inputValue();

			const group = await apiHelpers.jsonWebServicesGroup.getGroup(
				await page.getByRole('textbox', {name: 'Group ID'}).inputValue()
			);

			apiHelpers.data.push({
				id: group.externalReferenceCode,
				type: 'assetLibrary',
			});

			await page
				.getByPlaceholder('Description')
				.fill('Asset library description');

			await page.getByRole('button', {exact: true, name: 'Save'}).click();

			await waitForAlert(page);
		});

		await test.step('The same name cannot be added again', async () => {
			await gotoDepotAdmin(page);

			await openAddForm(page);

			await page.getByRole('textbox', {name: 'Name'}).fill(name);

			await page.getByRole('button', {exact: true, name: 'Save'}).click();

			await expect(
				page.getByText('Please enter a unique name.')
			).toBeVisible();
		});

		const editedName = getRandomString();

		await test.step('Rename it from its settings', async () => {
			await new DepotAdminPage(page).gotoGeneralSettings(assetLibraryId);

			await expect(page.getByPlaceholder('Description')).toHaveValue(
				'Asset library description'
			);

			await page.getByRole('textbox', {name: 'Name'}).fill(editedName);

			await page.getByRole('button', {exact: true, name: 'Save'}).click();

			await waitForAlert(page);

			await gotoDepotAdmin(page, editedName);

			await expect(getCard(page, editedName)).toBeVisible();
			await expect(getCard(page, name)).toHaveCount(0);
		});
	}
);

test(
	'Asset libraries can be deleted one at a time or selected together',
	{tag: '@LPD-109885'},
	async ({apiHelpers, page}) => {
		const prefix = getRandomString();

		const names = [`${prefix} 1`, `${prefix} 2`, `${prefix} 3`];

		for (const name of names) {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({name});
		}

		const [name1, name2, name3] = names;

		await test.step('Delete one from its actions', async () => {
			await gotoDepotAdmin(page, prefix);

			const confirmationMessage = acceptDeleteConfirmation(page);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					exact: true,
					name: 'Delete',
				}),
				trigger: getCard(page, name1).getByRole('button', {
					name: 'More actions',
				}),
			});

			expect(await confirmationMessage).toContain(
				'Removing an asset library can affect sites that use the contents stored in it.'
			);

			await waitForAlert(page);

			await expect(getCard(page, name1)).toHaveCount(0);
			await expect(getCard(page, name2)).toBeVisible();
		});

		await test.step('Select all, deselect one and delete the others', async () => {
			await gotoDepotAdmin(page, prefix);

			await page
				.getByRole('checkbox', {name: 'Select All Items on the Page'})
				.check();

			await expect(
				page.getByText('All Selected (2 of 2 Items Selected)')
			).toBeVisible();

			await getCard(page, name2).getByRole('checkbox').uncheck();

			await expect(
				page.getByText('1 of 2 Items Selected', {exact: true})
			).toBeVisible();

			const confirmed = acceptDeleteConfirmation(page);

			await page
				.getByRole('button', {exact: true, name: 'Delete'})
				.click();

			await confirmed;

			await waitForAlert(page);

			await expect(getCard(page, name3)).toHaveCount(0);
			await expect(getCard(page, name2)).toBeVisible();
		});

		await test.step('Select the last one again and delete it', async () => {
			await gotoDepotAdmin(page, prefix);

			await getCard(page, name2).getByRole('checkbox').check();

			await expect(
				page.getByText('All Selected (1 of 1 Items Selected)')
			).toBeVisible();

			const confirmed = acceptDeleteConfirmation(page);

			await page
				.getByRole('button', {exact: true, name: 'Delete'})
				.click();

			await confirmed;

			await waitForAlert(page);

			await expect(getCard(page, name2)).toHaveCount(0);
		});
	}
);

test(
	'Asset library cards show how many sites are connected',
	{tag: '@LPD-109885'},
	async ({apiHelpers, page}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		const sites = [];

		for (let i = 0; i < 3; i++) {
			sites.push(
				await apiHelpers.headlessAdminSite.postSite({
					name: getRandomString(),
				})
			);
		}

		const card = getCard(page, assetLibrary.name);

		await gotoDepotAdmin(page, assetLibrary.name);

		await expect(card.getByText('0 Connected Sites')).toBeVisible();

		for (const site of sites) {
			await apiHelpers.headlessAssetLibrary.connectSite(
				assetLibrary.externalReferenceCode,
				site.externalReferenceCode
			);
		}

		await gotoDepotAdmin(page, assetLibrary.name);

		await expect(card.getByText('3 Connected Sites')).toBeVisible();

		for (const site of sites.slice(1)) {
			await apiHelpers.headlessAssetLibrary.disconnectSite(
				assetLibrary.externalReferenceCode,
				site.externalReferenceCode
			);
		}

		await gotoDepotAdmin(page, assetLibrary.name);

		await expect(card.getByText('1 Connected Site')).toBeVisible();
	}
);
