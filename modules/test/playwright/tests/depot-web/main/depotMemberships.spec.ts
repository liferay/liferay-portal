/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	FrameLocator,
	Locator,
	Page,
	expect,
	mergeTests,
} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {SiteMembershipsPage} from '../../../pages/users-admin-web/site-admin-web/SiteMembershipsPage';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(dataApiHelpersTest, loginTest());

const SITE_MEMBERSHIPS_NAMESPACE =
	'_com_liferay_site_memberships_web_portlet_SiteMembershipsPortlet_';

const USERS_ADMIN_NAMESPACE =
	'_com_liferay_users_admin_web_portlet_UsersAdminPortlet_';

async function addDepot(apiHelpers: DataApiHelpers) {
	return apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: getRandomString(),
		type: 'AssetLibrary',
	});
}

async function assignMember(
	page: Page,
	iframeTitle: string,
	getItem: (frameLocator: FrameLocator) => Locator
) {
	await page.getByRole('button', {name: 'Add'}).click();

	await getItem(page.frameLocator(`iframe[title="${iframeTitle}"]`)).check();

	await page.getByRole('button', {name: 'Done'}).click();

	await waitForAlert(page);
}

async function gotoUserMemberships(
	page: Page,
	userId: string,
	languagePath = ''
) {
	await page.goto(
		`${languagePath}/group/control_panel/manage?p_p_id=com_liferay_users_admin_web_portlet_UsersAdminPortlet&${USERS_ADMIN_NAMESPACE}mvcRenderCommandName=%2Fusers_admin%2Fedit_user&${USERS_ADMIN_NAMESPACE}p_u_i_d=${userId}&${USERS_ADMIN_NAMESPACE}screenNavigationCategoryKey=general&${USERS_ADMIN_NAMESPACE}screenNavigationEntryKey=memberships`
	);
}

async function selectDepotInUserProfile(page: Page, depotName: string) {
	await page.getByRole('button', {name: 'Select Asset Libraries'}).click();

	await page
		.frameLocator('iframe[title="Select Asset Library"]')
		.getByText(depotName, {exact: true})
		.click();
}

test(
	'Organizations, users and user groups can be added to and removed from the depot memberships',
	{tag: '@LPD-109572'},
	async ({apiHelpers, page}) => {
		const assetLibrary = await addDepot(apiHelpers);

		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();
		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();
		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		const depotURL = `/asset-library-${assetLibrary.id}`;
		const siteMembershipsPage = new SiteMembershipsPage(page);

		await test.step('Add and remove an organization', async () => {
			await siteMembershipsPage.goto(depotURL);

			await page.getByRole('link', {name: 'Organizations'}).click();

			await assignMember(
				page,
				'Assign Organizations to This Asset Library',
				(frameLocator) => frameLocator.getByLabel(organization.name)
			);

			const row = page
				.getByRole('row')
				.filter({hasText: organization.name});

			await expect(row).toBeVisible();

			await row.getByRole('button').click();

			await page
				.getByRole('menuitem', {name: 'Remove Membership'})
				.click();

			await waitForAlert(page);

			await expect(
				page.getByText(
					'No organization was found that is a member of this asset library.'
				)
			).toBeVisible();
		});

		await test.step('Add and remove a user', async () => {
			await siteMembershipsPage.goto(depotURL);

			await assignMember(
				page,
				'Assign Users to This Asset Library',
				(frameLocator) => frameLocator.getByLabel(userAccount.name)
			);

			await siteMembershipsPage.goto(depotURL);

			const userCard = page.locator(
				`[id="${SITE_MEMBERSHIPS_NAMESPACE}users_${userAccount.alternateName}"]`
			);

			await expect(userCard).toBeVisible();

			page.once('dialog', (dialog) => dialog.accept());

			await userCard.getByLabel('More actions').click();

			await page
				.getByRole('menuitem', {name: 'Remove Membership'})
				.click();

			await waitForAlert(page);

			await expect(userCard).toHaveCount(0);
		});

		await test.step('Add and remove a user group', async () => {
			await siteMembershipsPage.goto(depotURL);

			await page.getByRole('link', {name: 'User Groups'}).click();

			await assignMember(
				page,
				'Assign User Groups to This Asset Library',
				(frameLocator) =>
					frameLocator
						.locator('.card')
						.filter({hasText: userGroup.name})
						.getByRole('checkbox')
			);

			const row = page.getByRole('row').filter({hasText: userGroup.name});

			await expect(row).toBeVisible();

			page.once('dialog', (dialog) => dialog.accept());

			await row.getByRole('button').click();

			await page
				.getByRole('menuitem', {name: 'Remove Membership'})
				.click();

			await waitForAlert(page);

			await expect(
				page.getByText(
					'No user group was found that is a member of this asset library.'
				)
			).toBeVisible();
		});
	}
);

test(
	'A depot is assigned from the user profile once, shown translated, and removed',
	{tag: '@LPD-109572'},
	async ({apiHelpers, page}) => {
		const assetLibrary = await addDepot(apiHelpers);

		const translatedName = getRandomString();

		await apiHelpers.headlessAssetLibrary.patchAssetLibrary(
			assetLibrary.externalReferenceCode,
			{
				name_i18n: {
					'en-US': assetLibrary.name,
					'es-ES': translatedName,
				},
			}
		);

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		const depotURL = `/asset-library-${assetLibrary.id}`;
		const depotRows = page
			.locator(
				`[id="${USERS_ADMIN_NAMESPACE}depotGroupsSearchContainer"]`
			)
			.getByRole('row')
			.filter({hasText: assetLibrary.name});
		const siteMembershipsPage = new SiteMembershipsPage(page);
		const userCard = page.locator(
			`[id="${SITE_MEMBERSHIPS_NAMESPACE}users_${userAccount.alternateName}"]`
		);

		await test.step('Assign the depot from the user profile', async () => {
			await gotoUserMemberships(page, userAccount.id);

			await selectDepotInUserProfile(page, assetLibrary.name);

			await expect(depotRows).toHaveCount(1);

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(page);

			await siteMembershipsPage.goto(depotURL);

			await expect(userCard).toBeVisible();
		});

		await test.step('Selecting the depot again does not duplicate it', async () => {
			await gotoUserMemberships(page, userAccount.id);

			await selectDepotInUserProfile(page, assetLibrary.name);

			await expect(depotRows).toHaveCount(1);
		});

		await test.step('The depot name is translated', async () => {
			await gotoUserMemberships(page, userAccount.id, '/es');

			await expect(
				page
					.locator(
						`[id="${USERS_ADMIN_NAMESPACE}depotGroupsSearchContainer"]`
					)
					.getByRole('cell', {exact: true, name: translatedName})
			).toBeVisible();
		});

		await test.step('Remove the depot from the user profile', async () => {

			// The language path is kept for the session, so go back to English

			await gotoUserMemberships(page, userAccount.id, '/en');

			await depotRows.getByRole('button').click();

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(page);

			await siteMembershipsPage.goto(depotURL);

			await expect(userCard).toHaveCount(0);
		});
	}
);

test(
	'The depot memberships paginate users and organizations',
	{tag: '@LPD-109572'},
	async ({apiHelpers, page}) => {
		const assetLibrary = await addDepot(apiHelpers);

		const organizationIds = [];
		const userIds = [];

		for (let i = 0; i < 5; i++) {
			const organization =
				await apiHelpers.headlessAdminUser.postOrganization();

			organizationIds.push(String(organization.id));

			const userAccount =
				await apiHelpers.headlessAdminUser.postUserAccount();

			userIds.push(String(userAccount.id));
		}

		await apiHelpers.jsonWebServicesOrganization.addGroupOrganizations(
			String(assetLibrary.siteId),
			organizationIds
		);
		await apiHelpers.jsonWebServicesUser.addGroupUsers(
			String(assetLibrary.siteId),
			userIds
		);

		for (const [tab, firstPage, secondPage] of [
			[
				'users',
				'Showing 1 to 4 of 6 entries.',
				'Showing 5 to 6 of 6 entries.',
			],
			[
				'organizations',
				'Showing 1 to 4 of 5 entries.',
				'Showing 5 to 5 of 5 entries.',
			],
		]) {
			await page.goto(
				`/group/asset-library-${assetLibrary.id}/~/control_panel/manage?p_p_id=com_liferay_site_memberships_web_portlet_SiteMembershipsPortlet&${SITE_MEMBERSHIPS_NAMESPACE}delta=4&${SITE_MEMBERSHIPS_NAMESPACE}tabs1=${tab}`
			);

			await expect(page.getByText(firstPage)).toBeVisible();

			await page.getByLabel('Page 2').click();

			await expect(page.getByText(secondPage)).toBeVisible();
		}
	}
);
