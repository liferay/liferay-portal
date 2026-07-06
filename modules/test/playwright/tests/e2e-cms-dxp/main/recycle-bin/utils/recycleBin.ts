/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {DataApiHelpers} from '../../../../../helpers/ApiHelpers';
import {addSpaceUser} from '../../../../../utils/addSpaceUser';

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

function rowActionsButton(page: Page, rowText: string) {
	return page
		.locator('tbody tr', {hasText: rowText})
		.first()
		.getByRole('button', {name: `${rowText} Actions`});
}

async function clickRowAction(page: Page, rowText: string, action: string) {
	const menuItem = page.getByRole('menuitem', {exact: true, name: action});

	await expect(async () => {
		if (!(await menuItem.isVisible())) {
			await rowActionsButton(page, rowText).click({timeout: 5000});

			await expect(menuItem).toBeVisible({timeout: 5000});
		}
	}).toPass({timeout: 30000});

	await menuItem.click();
}

export async function applyRecycleBinFilter(
	page: Page,
	category: string,
	optionLabel: string
) {
	await page.locator('button.filters-dropdown-button').click();

	const menu = page.locator('.dropdown-menu.show');

	await menu.getByRole('menuitem', {exact: true, name: category}).click();

	await menu.getByText(optionLabel, {exact: true}).click();

	await menu.getByRole('button', {name: /Show Results|Add Filter/}).click();
}

export async function deleteEntryToRecycleBin(page: Page, title: string) {
	await clickRowAction(page, title, 'Delete');

	const modalDeleteButton = page
		.locator('.modal.show')
		.getByRole('button', {name: 'Delete Entry'});

	await expect(modalDeleteButton).toBeVisible({timeout: 10000});

	await modalDeleteButton.click();

	await expect(page.locator('.alert', {hasText: 'was moved'})).toBeVisible({
		timeout: 15000,
	});
}

export async function restoreEntry(page: Page, title: string) {
	await clickRowAction(page, title, 'Restore');

	await expect(page.locator('.alert', {hasText: 'was restored'})).toBeVisible(
		{timeout: 15000}
	);
}

export async function expectRestoreUnavailable(page: Page, title: string) {
	const actionsButton = rowActionsButton(page, title);

	if (!(await actionsButton.isVisible().catch(() => false))) {
		return;
	}

	await actionsButton.click({timeout: 5000});

	await expect(page.locator('.dropdown-menu.show')).toBeVisible({
		timeout: 5000,
	});

	await expect(
		page.getByRole('menuitem', {exact: true, name: 'Restore'})
	).toBeHidden({timeout: 5000});

	await page.keyboard.press('Escape');
}
