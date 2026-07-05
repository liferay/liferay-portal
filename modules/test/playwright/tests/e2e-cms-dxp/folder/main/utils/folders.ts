/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {DataApiHelpers} from '../../../../../helpers/ApiHelpers';
import {addSpaceUser} from '../../../../../utils/addSpaceUser';
import {clickAndExpectToBeVisible} from '../../../../../utils/clickAndExpectToBeVisible';

export async function addSpaceUserWithSession(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string,
	roleName: string
): Promise<TUserAccount> {
	const user = await addSpaceUser(
		apiHelpers,
		spaceExternalReferenceCode,
		roleName
	);

	await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
	await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

	return user;
}

export async function clickItemAction(
	page: Page,
	itemTitle: string,
	action: string
) {
	const menuItem = page.getByRole('menuitem', {exact: true, name: action});

	await expect(async () => {
		if (!(await menuItem.isVisible())) {
			await page
				.getByRole('button', {name: `${itemTitle} Actions`})
				.click({timeout: 5000});

			await expect(menuItem).toBeVisible({timeout: 5000});
		}
	}).toPass({timeout: 30000});

	await menuItem.click();
}

export async function deletePopulatedFolder(page: Page, folderName: string) {
	await clickItemAction(page, folderName, 'Delete');

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.locator('.alert-success'),
		trigger: page.getByRole('button', {name: 'Delete Folder'}),
	});
}

export async function openFolder(page: Page, folderName: string) {
	await page.getByRole('link', {exact: true, name: folderName}).click();

	await page.waitForURL('**/view-folder/**');

	await page.locator('.fds').waitFor();
}

export async function searchContentList(page: Page, title: string) {
	await expect(async () => {
		await page.getByPlaceholder('Search').fill(title);

		await page.getByPlaceholder('Search').press('Enter');

		await expect(
			page.getByRole('link', {exact: true, name: title})
		).toBeVisible({timeout: 3000});
	}).toPass({timeout: 30000});
}
