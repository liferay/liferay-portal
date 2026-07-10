/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {DataApiHelpers} from '../../../../../helpers/ApiHelpers';
import {addSpaceUser} from '../../../../../utils/addSpaceUser';
import {waitForAlert} from '../../../../../utils/waitForAlert';
import {DataSetPage} from '../../../../site-cms-site-initializer/main/pages/DataSetPage';
import {RecycleBinPage} from '../../../../site-cms-site-initializer/main/pages/RecycleBinPage';

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
	await new DataSetPage(page).execItemAction({
		action: 'Delete',
		filter: title,
	});

	const modalDeleteButton = page
		.locator('.modal.show')
		.getByRole('button', {name: 'Delete Entry'});

	await expect(modalDeleteButton).toBeVisible({timeout: 10000});

	await modalDeleteButton.click();

	await waitForAlert(page, `${title} was moved`, {autoClose: false});
}

export async function restoreEntry(
	recycleBinPage: RecycleBinPage,
	title: string
) {
	await recycleBinPage.execItemAction({action: 'Restore', filter: title});

	await waitForAlert(recycleBinPage.page, `${title} was restored`, {
		autoClose: false,
	});
}

export async function expectRestoreUnavailable(
	recycleBinPage: RecycleBinPage,
	title: string
) {
	const actionsButton = recycleBinPage.dataSetFragmentPage
		.getRow(title)
		.getByRole('button', {name: `${title} Actions`});

	if (!(await actionsButton.isVisible().catch(() => false))) {
		return;
	}

	await recycleBinPage.dataSetFragmentPage.expectItemActionHidden({
		action: 'Restore',
		filter: title,
	});
}
