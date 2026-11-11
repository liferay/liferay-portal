/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {faroConfig} from '../faro.config';
import {ACPage, navigateToACSettingsViaURL} from './navigation';

export async function checkDataSourceStatus({
	dataSourceName,
	dataSourceStatus,
	page,
	syncedContactsStatus,
	syncedSitesStatus,
}: {
	dataSourceName?: string;
	dataSourceStatus: string;
	page: Page;
	syncedContactsStatus?: string;
	syncedSitesStatus?: string;
}) {
	await expect(page.getByText('Synced Data', {exact: true})).toBeVisible();

	if (dataSourceName) {
		await expect(
			page.getByText(dataSourceName, {exact: true}).first()
		).toBeVisible();
	}

	await expect(
		page.getByText(`${dataSourceStatus}`, {exact: true}).first()
	).toBeVisible();

	const listItems = page
		.locator('.panel-body')
		.filter({hasText: 'Synced'})
		.getByRole('listitem');

	if (syncedSitesStatus) {
		await expect(listItems.nth(1)).toContainText(`${syncedSitesStatus}`);
	}

	if (syncedContactsStatus) {
		await expect(listItems.nth(2)).toContainText(`${syncedContactsStatus}`);
	}
}

export async function createDataSource(page) {
	await page.goto(faroConfig.environment.baseUrl);

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('link', {name: 'Settings'}),
		trigger: page.getByRole('link', {
			name: 'FARO-DEV-liferay',
		}),
	});

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {name: 'Liferay DXP'}),
		trigger: page.getByRole('button', {name: 'Add Data Source'}),
	});

	const input = page.locator('#value');

	await expect(input).not.toHaveValue('Loading');

	const token = await input.inputValue();

	return {token};
}

export async function disconnectDataSourceFromAC(page: Page) {
	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page
			.getByRole('dialog')
			.getByRole('button', {name: 'Disconnect'}),
		trigger: page.getByRole('button', {name: 'Disconnect Data Source'}),
	});

	await waitForAlert(page, 'Success:Data Source Disconnected', {
		autoClose: false,
	});
}

export async function findDataSource({
	dataSourceName,
	page,
}: {
	dataSourceName: string;
	page: Page;
}) {
	await page.getByRole('textbox', {name: 'Search'}).click();

	await page.getByRole('textbox', {name: 'Search'}).fill(dataSourceName);

	await page.getByRole('textbox', {name: 'Search'}).press('Enter');

	return page.getByRole('link', {exact: true, name: dataSourceName});
}

export async function gotoLatestLiferayDXPDataSource(page, project) {
	await navigateToACSettingsViaURL({
		acPage: ACPage.dataSourcePage,
		page,
		projectID: project.groupId,
	});

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {name: 'Date Added'}),
		trigger: page.getByRole('button', {name: 'Order'}),
	});

	await clickAndExpectToBeHidden({
		target: page.getByText('Order By'),
		trigger: page.getByRole('button', {name: 'Order'}),
	});

	await clickAndExpectToBeVisible({
		target: page.getByText('Synced Data', {exact: true}),
		trigger: page
			.locator('tbody tr')
			.filter({hasText: 'Liferay Portal'})
			.getByRole('link')
			.filter({hasText: /^Liferay(?: \(\d+\))?$/})
			.first(),
	});
}

export async function renameDataSource({
	newDataSourceName,
	page,
}: {
	newDataSourceName: string;
	page: Page;
}) {
	await page.getByRole('button', {name: 'Edit'}).click();

	await page.locator('#dataSourceName').click();

	await page.locator('#dataSourceName').fill(newDataSourceName);

	await page
		.getByRole('button', {name: 'Submit'})
		.waitFor({state: 'visible'});

	await page.getByRole('button', {name: 'Submit'}).click();

	await expect(
		page.getByText(newDataSourceName, {exact: true}).first()
	).toBeVisible();
}
