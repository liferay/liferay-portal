/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

export async function clickMenuItem(
	menuitem: string,
	page,
	objectName?: string
) {
	await expect(async () => {
		if (!objectName) {
			await page.getByLabel('Actions').click();

			await page
				.getByRole('menuitem', {
					exact: true,
					name: menuitem,
				})
				.click();
		}
		else {
			await (await getTableRowByText(page, objectName))
				.getByRole('button', {name: 'Actions'})
				.click();

			await handleClickMenuItem(menuitem, page);
		}
	}).toPass({timeout: 10000});
}

export async function createSpace(page, spaceName: string) {
	await page.getByLabel('Add Space').first().click();
	await page.getByLabel('Space Name').fill(spaceName);
	await page.getByRole('button', {name: 'Continue'}).click();
	await page.getByRole('button', {name: 'Continue'}).click();
}

export async function deleteSpace(page, spaceName: string) {
	await expect(async () => {
		await clickMenuItem('Delete', page, spaceName);

		await page.getByRole('button', {name: 'Delete'}).click();
	}).toPass({timeout: 5000});

	await waitForAlert(page, `${spaceName} was successfully deleted.`);
}

export async function getTableRowByText(page, text: string) {
	return page.locator('table.table tbody tr', {hasText: text}).first();
}

export async function goToAllSpaces(page) {
	await expect(async () => {
		await page.goto(PORTLET_URLS.cmsAllSpaces);

		await expect(
			page.getByRole('heading', {exact: true, name: 'All Spaces'})
		).toBeVisible();
	}).toPass({timeout: 10000});
}

export async function handleClickMenuItem(menuitem: string, page) {
	await expect(async () => {
		if (menuitem.includes('Permissions')) {
			await page
				.getByRole('menuitem', {
					exact: true,
					name: 'Permissions',
				})
				.and(page.locator('[aria-haspopup="true"]'))
				.click({timeout: 1000});

			await page
				.getByRole('menuitem', {
					exact: true,
					name: menuitem,
				})
				.and(page.locator(':not([aria-haspopup="true"])'))
				.click();
		}
		else {
			await page
				.getByRole('menuitem', {
					exact: true,
					name: menuitem,
				})
				.click();
		}
	}).toPass({timeout: 5000});
}

export async function updateSpaceExternalReferenceCode(
	apiHelpers,
	spaceName: string,
	externalReferenceCode: string
) {
	const assetLibraries =
		await apiHelpers.headlessAssetLibrary.getAssetLibrariesPage(
			`type eq 'Space'`
		);

	const assetLibrary = assetLibraries.find(({name}) => name === spaceName);

	await apiHelpers.headlessAssetLibrary.patchAssetLibrary(
		assetLibrary.externalReferenceCode,
		{externalReferenceCode}
	);
}
