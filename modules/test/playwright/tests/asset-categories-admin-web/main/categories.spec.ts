/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {createCategories} from '../../../helpers/CreateCategories';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../utils/waitForAlert';
import {assetCategoriesPagesTest} from './fixtures/assetCategoriesAdminPagesTest';

const test = mergeTests(
	apiHelpersTest,
	assetCategoriesPagesTest,
	isolatedSiteTest,
	loginTest()
);

test('User can add, edit, delete a category and add a subcategory.', async ({
	apiHelpers,
	assetCategoriesAdminPage,
	assetCategoriesEditPage,
	page,
	site,
}) => {
	const categoryName = 'category-1';
	const vocabularyName = 'test vocabulary';

	await test.step('add', async () => {
		await createCategories({
			apiHelpers,
			categoryNames: [{name: categoryName}],
			siteId: site.id,
			vocabularyName,
		});
	});

	await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

	const categoryNameChanged = 'category-1-changed';
	const categoryERCChanged = 'category-1-erc-changed';

	await test.step('edit', async () => {
		await assetCategoriesEditPage.goto(categoryName);

		await assetCategoriesEditPage.fillName(categoryNameChanged);
		await assetCategoriesEditPage.fillExternalReferenceCode(
			categoryERCChanged
		);
		await assetCategoriesEditPage.save(`Success:${categoryNameChanged}`);

		await expect(
			page.getByRole('link', {name: categoryNameChanged})
		).toBeVisible();
	});

	await test.step('add a category with duplicate external code reference', async () => {
		await assetCategoriesAdminPage.gotoAction(
			'Add Subcategory',
			categoryNameChanged
		);

		const subcategoryName = 'Subcategory name';

		await assetCategoriesEditPage.fillName(subcategoryName);
		await assetCategoriesEditPage.fillExternalReferenceCode(
			categoryERCChanged
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: assetCategoriesEditPage.saveButton,
			trigger: page.getByText(
				'Please enter a unique external reference code.',
				{
					exact: true,
				}
			),
		});

		await assetCategoriesAdminPage.goto(site.friendlyUrlPath);
	});

	await test.step('add a subcategory', async () => {
		await assetCategoriesAdminPage.gotoAction(
			'Add Subcategory',
			categoryNameChanged
		);

		const subcategoryName = 'Subcategory name';

		await assetCategoriesEditPage.fillName(subcategoryName);

		await assetCategoriesEditPage.save(`Success:${subcategoryName}`);

		await expect(
			page.getByRole('link', {name: subcategoryName})
		).toBeVisible();
	});

	await test.step('delete', async () => {
		await assetCategoriesAdminPage.gotoVocabulary(vocabularyName);

		await assetCategoriesAdminPage.gotoAction(
			'Delete',
			categoryNameChanged
		);

		await assetCategoriesEditPage.deleteButton.click();
		await waitForAlert(page);

		await expect(
			page.getByRole('link', {name: categoryNameChanged})
		).not.toBeVisible();
	});
});

test('User can move a category and subcategory.', async ({
	apiHelpers,
	assetCategoriesAdminPage,
	assetCategoriesEditPage,
	page,
	site,
}) => {
	let categories = [];
	const categoryName1 = 'category-1';
	const categoryName2 = 'category-2';
	const vocabularyName1 = 'vocabulary one';
	const vocabularyName2 = 'vocabulary two';

	await test.step('add two vocabularies', async () => {
		categories = await createCategories({
			apiHelpers,
			categoryNames: [{name: categoryName1}, {name: categoryName2}],
			siteId: site.id,
			vocabularyName: vocabularyName1,
		});

		await createCategories({
			apiHelpers,
			categoryNames: [],
			siteId: site.id,
			vocabularyName: vocabularyName2,
		});
	});

	await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

	await test.step('move category to vocabulary two', async () => {
		await assetCategoriesAdminPage.gotoAction('Move', categoryName1);

		await assetCategoriesEditPage.moveCategory({
			categoryName: categoryName1,
			targetName: vocabularyName2,
		});

		await assetCategoriesAdminPage.gotoVocabulary(vocabularyName2);

		await expect(
			page.getByRole('link', {name: categoryName1})
		).toBeVisible();
	});

	await test.step('move subcategory to another category', async () => {
		const subcategoryName = 'subcategory test';
		await apiHelpers.headlessAdminTaxonomy.postTaxonomyCategoryTaxonomyCategory(
			{
				name: subcategoryName,
				parentTaxonomyCategoryId: categories[0].id,
			}
		);

		await page.getByRole('link', {name: categoryName1}).click();

		await assetCategoriesAdminPage.gotoAction('Move', subcategoryName);

		await assetCategoriesEditPage.moveCategory({
			categoryName: subcategoryName,
			expandNames: [vocabularyName1],
			targetName: categoryName2,
		});

		await assetCategoriesAdminPage.gotoVocabulary(vocabularyName1);
		await page.getByRole('link', {name: categoryName2}).click();

		await expect(
			page.getByRole('row', {name: subcategoryName})
		).toBeVisible();
	});
});

test('User can add, edit, delete properties in category.', async ({
	apiHelpers,
	assetCategoriesAdminPage,
	assetCategoriesEditPage,
	page,
	site,
}) => {
	const categoryName = 'category-1';
	const properties = {
		'key 1 - Category Property': 'value 1 - Category Property',
		'key 2 - Category Property': 'value 2 - Category Property',
		'key 3 - Category Property': 'value 3 - Category Property',
	};
	await createCategories({
		apiHelpers,
		categoryNames: [{name: categoryName}],
		siteId: site.id,
		vocabularyName: 'test vocabulary',
	});

	await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

	await test.step('Add', async () => {
		await assetCategoriesEditPage.goto(categoryName);
		await assetCategoriesEditPage.addProperties(properties);

		await assetCategoriesEditPage.goToPropertiesTab(categoryName);

		await expect(page.getByLabel('key').first()).toHaveValue(
			Object.keys(properties)[0]
		);
		await expect(page.getByLabel('value').nth(1)).toHaveValue(
			Object.values(properties)[1]
		);
		await expect(page.getByLabel('value')).toHaveCount(3);
	});

	await test.step('Edit', async () => {
		const editedValue = 'value 2 - EDITED Category Property';
		await page.getByLabel('value').nth(1).fill(editedValue);
		await assetCategoriesEditPage.save();

		await assetCategoriesEditPage.goToPropertiesTab(categoryName);
		await expect(page.getByLabel('value').nth(1)).toHaveValue(editedValue);
	});

	await test.step('Can not duplicate property key', async () => {
		await assetCategoriesEditPage.addProperties(
			{
				[Object.keys(properties)[0]]: 'duplicated key',
			},
			{save: false}
		);
		await assetCategoriesEditPage.saveButton.click();

		await expect(
			page.getByText('Error:Please enter a unique property key.')
		).toBeVisible();
	});

	await test.step('Delete', async () => {
		await page.getByRole('button', {name: 'Remove'}).nth(1).click();
		await page.getByRole('button', {name: 'Remove'}).last().click();
		await assetCategoriesEditPage.save();

		await assetCategoriesEditPage.goToPropertiesTab(categoryName);
		await expect(page.getByLabel('value').nth(1)).toHaveValue(
			Object.values(properties)[2]
		);
		await expect(page.getByLabel('value')).toHaveCount(2);
	});
});

test(
	'User can edit the friendly URL of a category',
	{tag: '@LPD-99566'},
	async ({
		apiHelpers,
		assetCategoriesAdminPage,
		assetCategoriesEditPage,
		page,
		site,
	}) => {
		const categoryName = 'category-1';
		const vocabularyName = 'vocabulary-1';

		await createCategories({
			apiHelpers,
			categoryNames: [{name: categoryName}],
			siteId: site.id,
			vocabularyName,
		});

		await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

		await assetCategoriesEditPage.goToFriendlyURLTab(categoryName);

		// The site URL holds the vocabulary, the Commerce one does not

		await expect(
			page.getByText(`/v/${vocabularyName}/${categoryName}`)
		).toBeVisible();
		await expect(page.getByText(`/g/${categoryName}`)).toBeVisible();

		// Both URLs follow the field as it is edited, already normalized

		await assetCategoriesEditPage.fillFriendlyURL('Winter Sports');

		await expect(
			page.getByText(`/v/${vocabularyName}/winter-sports`)
		).toBeVisible();
		await expect(page.getByText('/g/winter-sports')).toBeVisible();

		// Switching language repoints the URLs at that language's friendly URL

		await assetCategoriesEditPage.selectLanguage('es-ES');

		await expect(assetCategoriesEditPage.friendlyURLInput).toBeEmpty();
		await expect(page.locator('[id$=siteURLTitle]')).toBeEmpty();

		await assetCategoriesEditPage.selectLanguage('en-US');

		// The friendly URL is normalized and kept

		await assetCategoriesEditPage.save();

		await expect(assetCategoriesEditPage.friendlyURLInput).toHaveValue(
			'winter-sports'
		);
	}
);

test(
	'User sees the category ID in the site URL when the friendly URL is too long',
	{tag: '@LPD-102334'},
	async ({apiHelpers, assetCategoriesEditPage, page, site}) => {

		// A friendly URL holds up to 255 characters, so only a deep chain of
		// categories takes the site URL over its maximum length

		const namePadding = 'x'.repeat(240);
		const vocabularyName = 'vocabulary-1';

		const {id: vocabularyId} =
			await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
				name: vocabularyName,
				siteId: site.id,
			});

		let categoryName = `1-${namePadding}`;

		const {id: rootCategoryId} =
			await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
				{
					name: categoryName,
					vocabularyId,
				}
			);

		let categoryId = rootCategoryId;

		for (let i = 2; i <= 9; i++) {
			categoryName = `${i}-${namePadding}`;

			const {id} =
				await apiHelpers.headlessAdminTaxonomy.postTaxonomyCategoryTaxonomyCategory(
					{
						name: categoryName,
						parentTaxonomyCategoryId: categoryId,
					}
				);

			categoryId = id;
		}

		await assetCategoriesEditPage.gotoEditCategory({
			categoryId,
			siteUrl: site.friendlyUrlPath,
			vocabularyId,
		});

		// Leaving the details screen before it finishes wiring leaves the
		// localized input of the next screen without its language dropdown

		await assetCategoriesEditPage.descriptionField.waitFor();
		await assetCategoriesEditPage.friendlyURLTab.click();
		await assetCategoriesEditPage.friendlyURLInput.waitFor();

		// The site URL falls back to the category ID, the Commerce one keeps the
		// friendly URL

		await expect(page.getByText(`/v/${categoryId}`)).toBeVisible();
		await expect(page.locator('[id$=siteURLTitle]')).toHaveCount(0);
		await expect(page.locator('[id$=commerceURLTitle]')).toHaveText(
			categoryName
		);

		// The site URL no longer holds the friendly URL, so it does not follow
		// the field

		await assetCategoriesEditPage.fillFriendlyURL('Winter Sports');

		await expect(page.getByText(`/v/${categoryId}`)).toBeVisible();
		await expect(page.getByText('/g/winter-sports')).toBeVisible();
	}
);
