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
