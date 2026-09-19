/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {CommerceAdminCatalogsPage} from '../../../../pages/commerce/commerce-catalog-web/commerceAdminCatalogsPage';
import getRandomString from '../../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

const CATALOG_ACTION_LABELS = ['Delete', 'Permissions', 'Update', 'View'];

async function createCatalogUser(
	apiHelpers: DataApiHelpers,
	page: Page,
	catalogActionIds: string[]
) {
	const companyId = await page.evaluate(() =>
		Liferay.ThemeDisplay.getCompanyId()
	);

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: `Catalog Role ${getRandomString()}`,
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_commerce_catalog_web_internal_portlet_CommerceCatalogsPortlet',
				scope: 1,
			},
			{
				actionIds: catalogActionIds,
				primaryKey: companyId,
				resourceName:
					'com.liferay.commerce.product.model.CommerceCatalog',
				scope: 1,
			},
			{
				actionIds: ['VIEW_COMMERCE_CATALOGS'],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.catalog',
				scope: 1,
			},
		],
	});

	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
		role.externalReferenceCode,
		user.id
	);

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	return user;
}

async function openCatalogPermissions(
	commerceAdminCatalogsPage: CommerceAdminCatalogsPage,
	catalogName: string,
	roleName: string,
	openPermissions: () => Promise<void>
) {
	await commerceAdminCatalogsPage.goto();
	await commerceAdminCatalogsPage.search(catalogName);

	await openPermissions();

	await commerceAdminCatalogsPage.permissionsSearchInput.fill(roleName);
	await commerceAdminCatalogsPage.permissionsSearchInput.press('Enter');
}

test(
	'Site roles should not appear in Catalog permissions menus',
	{tag: '@LPD-55197'},
	async ({apiHelpers, commerceAdminCatalogsPage}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		await openCatalogPermissions(
			commerceAdminCatalogsPage,
			catalog.name,
			'Site Member',
			async () => {
				await commerceAdminCatalogsPage
					.catalogActionsButton(catalog.name)
					.click();
				await commerceAdminCatalogsPage.permissionsMenuItem.click();
			}
		);

		await expect(
			commerceAdminCatalogsPage.permissionsFrame.getByText(
				'Site Member',
				{exact: true}
			)
		).toHaveCount(0);
	}
);

test(
	'Catalog permissions can be granted to a role',
	{tag: ['@COMMERCE-6279', '@LPD-106358']},
	async ({apiHelpers, commerceAdminCatalogsPage}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();
		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
		});

		await test.step('Grant every catalog permission to the role', async () => {
			await openCatalogPermissions(
				commerceAdminCatalogsPage,
				catalog.name,
				role.name,
				async () => {
					await commerceAdminCatalogsPage
						.catalogActionsButton(catalog.name)
						.click();
					await commerceAdminCatalogsPage.permissionsMenuItem.click();
				}
			);

			for (const actionLabel of CATALOG_ACTION_LABELS) {
				await commerceAdminCatalogsPage
					.permissionCheckbox(role.name, actionLabel)
					.check();
			}

			await commerceAdminCatalogsPage.permissionsSaveButton.click();

			await waitForAlert(commerceAdminCatalogsPage.permissionsFrame);

			await commerceAdminCatalogsPage.closePermissionsDialog();
		});

		await test.step('The saved permissions survive a reopen', async () => {
			await openCatalogPermissions(
				commerceAdminCatalogsPage,
				catalog.name,
				role.name,
				async () => {
					await commerceAdminCatalogsPage
						.catalogActionsButton(catalog.name)
						.click();
					await commerceAdminCatalogsPage.permissionsMenuItem.click();
				}
			);

			for (const actionLabel of CATALOG_ACTION_LABELS) {
				await expect(
					commerceAdminCatalogsPage.permissionCheckbox(
						role.name,
						actionLabel
					)
				).toBeChecked();
			}
		});
	}
);

test(
	'A user with the Permissions action can edit catalog permissions',
	{tag: '@LPD-106358'},
	async ({apiHelpers, commerceAdminCatalogsPage, page}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();
		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
		});

		const user = await createCatalogUser(apiHelpers, page, [
			'PERMISSIONS',
			'VIEW',
		]);

		await performUserSwitch(page, user.alternateName);

		await openCatalogPermissions(
			commerceAdminCatalogsPage,
			catalog.name,
			role.name,
			async () => {
				await commerceAdminCatalogsPage
					.catalogPermissionsButton(catalog.name)
					.click();
			}
		);

		for (const actionLabel of CATALOG_ACTION_LABELS) {
			await commerceAdminCatalogsPage
				.permissionCheckbox(role.name, actionLabel)
				.check();
		}

		await commerceAdminCatalogsPage.permissionsSaveButton.click();

		await waitForAlert(commerceAdminCatalogsPage.permissionsFrame);

		await commerceAdminCatalogsPage.closePermissionsDialog();

		await openCatalogPermissions(
			commerceAdminCatalogsPage,
			catalog.name,
			role.name,
			async () => {
				await commerceAdminCatalogsPage
					.catalogPermissionsButton(catalog.name)
					.click();
			}
		);

		for (const actionLabel of CATALOG_ACTION_LABELS) {
			await expect(
				commerceAdminCatalogsPage.permissionCheckbox(
					role.name,
					actionLabel
				)
			).toBeChecked();
		}
	}
);

test(
	'A user without add, update and delete permissions cannot add, edit or delete catalogs',
	{tag: '@LPD-106358'},
	async ({apiHelpers, commerceAdminCatalogsPage, page}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const user = await createCatalogUser(apiHelpers, page, ['VIEW']);

		await performUserSwitch(page, user.alternateName);

		await commerceAdminCatalogsPage.goto();
		await commerceAdminCatalogsPage.search(catalog.name);

		await expect(
			commerceAdminCatalogsPage.catalogRow(catalog.name)
		).toBeVisible();

		await expect(commerceAdminCatalogsPage.addCatalogsButton).toHaveCount(
			0
		);
		await expect(
			commerceAdminCatalogsPage.catalogLink(catalog.name)
		).toHaveCount(0);
		await expect(
			commerceAdminCatalogsPage
				.catalogRow(catalog.name)
				.getByRole('button')
		).toHaveCount(0);
	}
);
