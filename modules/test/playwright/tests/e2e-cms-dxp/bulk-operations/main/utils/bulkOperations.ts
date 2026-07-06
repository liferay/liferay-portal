/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {DataApiHelpers} from '../../../../../helpers/ApiHelpers';
import {addSpaceUser} from '../../../../../utils/addSpaceUser';
import {userData} from '../../../../../utils/performLogin';

export async function execBulkAction(page: Page, action: string) {
	const menu = page.locator('.dropdown-menu.show');

	const menuItem = menu.getByRole('menuitem', {exact: true, name: action});

	await expect(async () => {
		if (!(await menu.isVisible())) {
			await page
				.getByRole('button', {exact: true, name: 'Actions'})
				.click({timeout: 2000});

			await expect(menu).toBeVisible({timeout: 2000});
		}

		await expect(menuItem).toBeVisible({timeout: 5000});
	}).toPass({timeout: 30000});

	await menuItem.click();
}

export async function bulkMoveToFolder(
	page: Page,
	{
		destinationFolder,
		destinationSpace,
	}: {destinationFolder: string; destinationSpace: string}
) {
	const dialog = page.getByRole('dialog', {name: /^Move .+ To$/});

	const folderRadio = dialog.getByRole('radio', {
		name: `Select ${destinationFolder}`,
	});

	await expect(async () => {
		if (!(await dialog.isVisible())) {
			await execBulkAction(page, 'Move To');
		}

		await expect(dialog).toBeVisible({timeout: 5000});

		await dialog.getByLabel(destinationSpace).click({timeout: 5000});

		await expect(folderRadio).toBeVisible({timeout: 5000});

		await folderRadio.click();

		await dialog
			.getByRole('button', {exact: true, name: 'Select'})
			.click({timeout: 5000});

		await expect(dialog).toBeHidden({timeout: 5000});
	}).toPass({timeout: 90000});
}

export async function addCMSAdministratorUser(
	apiHelpers: DataApiHelpers
): Promise<TUserAccount> {
	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	const rolesPage =
		await apiHelpers.headlessAdminUser.getRoles('CMS Administrator');

	const cmsAdministratorRole = rolesPage.items.find(
		(role) => role.name === 'CMS Administrator'
	);

	if (!cmsAdministratorRole) {
		throw new Error('CMS Administrator role was not found');
	}

	await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
		cmsAdministratorRole.id,
		Number(user.id)
	);

	await clearFirstSignInWalls(apiHelpers, user);

	return user;
}

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

	await clearFirstSignInWalls(apiHelpers, user);

	return user;
}

export async function clearFirstSignInWalls(
	apiHelpers: DataApiHelpers,
	user: TUserAccount
) {
	await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
	await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);
}

export async function disconnectSite(
	apiHelpers: DataApiHelpers,
	assetLibraryExternalReferenceCode: string,
	connectedSiteExternalReferenceCode: string
) {
	await apiHelpers.delete(
		`${apiHelpers.baseUrl}headless-asset-library/v1.0/asset-libraries/${assetLibraryExternalReferenceCode}/connected-sites/${connectedSiteExternalReferenceCode}`
	);
}

export async function openFolder(page: Page, folderName: string) {
	await page.getByRole('link', {exact: true, name: folderName}).click();

	await page.waitForURL('**/view-folder/**');

	await page.locator('.fds').waitFor();
}
