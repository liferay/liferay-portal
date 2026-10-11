/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {createCategories} from '../../../helpers/CreateCategories';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitchViaApi, userData} from '../../../utils/performLogin';
import {assetCategoriesPagesTest} from './fixtures/assetCategoriesAdminPagesTest';

const test = mergeTests(
	assetCategoriesPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest()
);

const CATEGORIES_EDITOR_PERMISSIONS = [
	{
		actionIds: ['ADD_CATEGORY', 'ADD_VOCABULARY'],
		resourceName: 'com.liferay.asset.categories',
	},
	{
		actionIds: ['ADD_CATEGORY', 'VIEW'],
		resourceName: 'com.liferay.asset.kernel.model.AssetCategory',
	},
	{
		actionIds: ['VIEW'],
		resourceName: 'com.liferay.asset.kernel.model.AssetVocabulary',
	},
	{
		actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
		resourceName:
			'com_liferay_asset_categories_admin_web_portlet_AssetCategoriesAdminPortlet',
	},
	{
		actionIds: ['VIEW_SITE_ADMINISTRATION'],
		resourceName: 'com.liferay.portal.kernel.model.Group',
	},
];

test(
	'A site role can add vocabularies and categories but cannot delete categories without permission',
	{tag: '@LPD-109100'},
	async ({
		apiHelpers,
		assetCategoriesAdminPage,
		assetCategoriesEditPage,
		page,
		site,
		vocabulariesEditPage,
	}) => {
		const categoryName = getRandomString();
		const vocabularyName = getRandomString();

		await createCategories({
			apiHelpers,
			categoryNames: [{name: categoryName}],
			siteId: site.id,
			vocabularyName,
		});

		// Assign a site role without the delete permission to a new user

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: CATEGORIES_EDITOR_PERMISSIONS.map(
				({actionIds, resourceName}) => ({
					actionIds,
					primaryKey: '0',
					resourceName,
					scope: 3,
				})
			),
			roleType: 'site',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.jsonWebServicesUser.assignUsersToSite(
			site.id,
			user.id
		);

		await apiHelpers.headlessAdminUser.assignUserToSite(
			role.id,
			site.id,
			user.id
		);

		await performUserSwitchViaApi(page, user.alternateName);

		await test.step('Add a vocabulary and a category', async () => {
			await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

			const newCategoryName = getRandomString();
			const newVocabularyName = getRandomString();

			await assetCategoriesAdminPage.newVocabularyButton.click();

			await vocabulariesEditPage.add({name: newVocabularyName});

			await expect(
				page.getByRole('heading', {name: newVocabularyName})
			).toBeVisible();

			await page.getByRole('link', {name: 'Add Category'}).click();

			await assetCategoriesEditPage.fillName(newCategoryName);

			await assetCategoriesEditPage.save(`Success:${newCategoryName}`);

			await expect(
				page.getByRole('row').filter({hasText: newCategoryName})
			).toBeVisible();
		});

		await test.step('Cannot delete a category without permission', async () => {
			await assetCategoriesAdminPage.gotoVocabulary(vocabularyName);

			const row = page.getByRole('row').filter({hasText: categoryName});

			await row.getByRole('checkbox').check();

			await expect(
				page.getByRole('button', {name: 'Delete'})
			).toBeDisabled();
		});
	}
);
