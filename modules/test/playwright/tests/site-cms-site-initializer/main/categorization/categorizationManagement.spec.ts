/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../../utils/getRandomString';
import {categorizationPagesTest} from '../fixtures/categorizationPagesTest';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(
	categorizationPagesTest,
	cmsPagesTest,
	dataApiHelpersTest,
	loginTest()
);

test(
	'Vocabulary category hierarchy is visible in Categorization and available when tagging content',
	{tag: ['@LPD-95543', '@LPD-95543/TC-20.a']},
	async ({
		apiHelpers,
		categoriesPage,
		contentsPage,
		editCategoryPage,
		page,
	}) => {
		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const vocabularyName = getRandomString();
		const parentCategoryName = getRandomString();
		const childCategoryName = getRandomString();
		const subCategoryName = getRandomString();

		const vocabularyId = await apiHelpers.headlessAdminTaxonomy
			.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1}],
				assetTypes: [
					{
						required: false,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name: vocabularyName,
				siteId,
				visibilityType: 'PUBLIC',
			})
			.then((response) => String(response.id));

		await test.step('Create categories and a subcategory', async () => {
			await categoriesPage.goto(vocabularyId, vocabularyName);

			await categoriesPage.clickCreateNewCategoryButton();
			await editCategoryPage.fillName(parentCategoryName);
			await editCategoryPage.clickSaveAndAddAnother();
			await editCategoryPage.fillName(childCategoryName);
			await editCategoryPage.clickSave();

			await categoriesPage.execItemAction({
				action: 'Add Subcategory',
				filter: parentCategoryName,
			});
			await editCategoryPage.fillName(subCategoryName);
			await editCategoryPage.clickSave();
		});

		await test.step('Verify the full category hierarchy appears in Categorization', async () => {
			await categoriesPage.goto(vocabularyId, vocabularyName);

			await expect(
				categoriesPage.getItem(parentCategoryName)
			).toBeVisible();
			await expect(
				categoriesPage.getItem(childCategoryName)
			).toBeVisible();

			await page
				.getByRole('link', {exact: true, name: parentCategoryName})
				.click();

			const parentCategoryId = new URL(page.url()).searchParams.get(
				'categoryId'
			);

			if (!parentCategoryId) {
				throw new Error(
					'categoryId not found in URL after opening the parent category'
				);
			}

			await categoriesPage.gotoSubcategories(
				parentCategoryId,
				parentCategoryName,
				vocabularyId,
				vocabularyName
			);
			await expect(categoriesPage.getItem(subCategoryName)).toBeVisible();
		});

		await test.step('Verify the categories are available when tagging a content entry', async () => {
			await contentsPage.goto();
			await contentsPage.createContent('Basic Web Content', space.name);

			await contentsPage.fillData([
				{label: 'Title', value: getRandomString()},
			]);

			await contentsPage.openSidePanel('Categorization');

			const categoriesAutocomplete =
				page.getByPlaceholder('Add category');

			for (const categoryName of [
				parentCategoryName,
				childCategoryName,
				subCategoryName,
			]) {
				await expect(async () => {
					await categoriesAutocomplete.click();
					await categoriesAutocomplete.fill('');
					await categoriesAutocomplete.pressSequentially(
						categoryName,
						{delay: 50}
					);

					await expect(
						page.getByRole('option', {name: categoryName})
					).toBeVisible({timeout: 3000});
				}).toPass({timeout: 120000});

				await categoriesAutocomplete.fill('');
			}
		});
	}
);

test(
	'Renaming a vocabulary and adding or deleting its categories is reflected in the content tag selector',
	{tag: ['@LPD-95543', '@LPD-95543/TC-20.b']},
	async ({
		apiHelpers,
		categoriesPage,
		contentsPage,
		editCategoryPage,
		editVocabularyPage,
		page,
		vocabulariesPage,
	}) => {
		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const vocabularyName = getRandomString();
		const categoryToDeleteName = getRandomString();

		const vocabularyId = await apiHelpers.headlessAdminTaxonomy
			.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1}],
				assetTypes: [
					{
						required: false,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name: vocabularyName,
				siteId,
				visibilityType: 'PUBLIC',
			})
			.then((response) => response.id);

		await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
			{
				name: categoryToDeleteName,
				vocabularyId,
			}
		);

		const newVocabularyName = getRandomString();
		const newCategoryName1 = getRandomString();
		const newCategoryName2 = getRandomString();

		await test.step('Rename the vocabulary, add two categories, and delete one', async () => {
			await categoriesPage.goto(vocabularyId, vocabularyName);

			await categoriesPage.clickCreateNewCategoryButton();
			await editCategoryPage.fillName(newCategoryName1);
			await editCategoryPage.clickSaveAndAddAnother();
			await editCategoryPage.fillName(newCategoryName2);
			await editCategoryPage.clickSave();

			await categoriesPage.execItemAction({
				action: 'Delete',
				filter: categoryToDeleteName,
			});
			await categoriesPage.handleDeleteConfirmationModal(true);

			await expect(
				categoriesPage.getItem(categoryToDeleteName)
			).toBeHidden();

			await vocabulariesPage.goto();
			await vocabulariesPage.execItemAction({
				action: 'Edit',
				filter: vocabularyName,
			});

			await editVocabularyPage.changeGeneralInfo({
				name: newVocabularyName,
			});

			await clickAndExpectToBeVisible({
				target: page.getByText(
					`Success:${newVocabularyName} was updated successfully.`
				),
				trigger: editVocabularyPage.saveButton,
			});
		});

		await test.step('Verify the content tag selector reflects the updated vocabulary', async () => {
			await contentsPage.goto();
			await contentsPage.createContent('Basic Web Content', space.name);

			await contentsPage.fillData([
				{label: 'Title', value: getRandomString()},
			]);

			await contentsPage.openSidePanel('Categorization');

			const categoriesAutocomplete =
				page.getByPlaceholder('Add category');

			await expect(async () => {
				await categoriesAutocomplete.click();
				await categoriesAutocomplete.fill('');
				await categoriesAutocomplete.pressSequentially(
					newCategoryName1,
					{delay: 50}
				);

				await expect(
					page.getByRole('option', {name: newCategoryName1})
				).toBeVisible({timeout: 3000});
			}).toPass({timeout: 120000});
			await categoriesAutocomplete.fill('');

			await expect(async () => {
				await categoriesAutocomplete.click();
				await categoriesAutocomplete.fill('');
				await categoriesAutocomplete.pressSequentially(
					newCategoryName2,
					{delay: 50}
				);

				await expect(
					page.getByRole('option', {name: newCategoryName2})
				).toBeVisible({timeout: 3000});
			}).toPass({timeout: 120000});
			await categoriesAutocomplete.fill('');

			await categoriesAutocomplete.click();
			await categoriesAutocomplete.pressSequentially(
				categoryToDeleteName,
				{delay: 50}
			);
			await expect(
				page.getByRole('option', {name: categoryToDeleteName})
			).toBeHidden();
		});
	}
);

test(
	'Publishing content requires a category from a vocabulary marked required for that asset type',
	{tag: ['@LPD-95543', '@LPD-95543/TC-20.c']},
	async ({apiHelpers, contentsPage, page}) => {
		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const vocabularyName = getRandomString();
		const categoryName = getRandomString();

		const vocabularyId = await apiHelpers.headlessAdminTaxonomy
			.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1}],
				assetTypes: [
					{
						required: true,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name: vocabularyName,
				siteId,
				visibilityType: 'PUBLIC',
			})
			.then((response) => response.id);

		await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
			{
				name: categoryName,
				vocabularyId,
			}
		);

		await contentsPage.goto();
		await contentsPage.createContent('Basic Web Content', space.name);

		const title = getRandomString();

		await contentsPage.fillData([{label: 'Title', value: title}]);

		await test.step('Attempt to publish without selecting the required category', async () => {
			await contentsPage.publishButton.click();

			await expect(
				page.getByText(
					'Please enter at least one category for all mandatory vocabularies.'
				)
			).toBeVisible();
		});

		await test.step('Select the required category and publish successfully', async () => {
			await contentsPage.openSidePanel('Categorization');

			const categoriesAutocomplete =
				page.getByPlaceholder('Add category');

			await expect(async () => {
				await categoriesAutocomplete.click();
				await categoriesAutocomplete.fill('');
				await categoriesAutocomplete.pressSequentially(categoryName, {
					delay: 50,
				});

				await expect(
					page.getByRole('option', {name: categoryName})
				).toBeVisible({timeout: 3000});
			}).toPass({timeout: 120000});

			await page.getByRole('option', {name: categoryName}).click();

			await contentsPage.saveContent();

			await expect(
				page.getByRole('link', {exact: true, name: title})
			).toBeVisible();
		});
	}
);

test(
	'Deleting a category assigned to content and file entries does not break either entry',
	{tag: ['@LPD-95543', '@LPD-95543/TC-20.e']},
	async ({apiHelpers, assetsPage, categoriesPage, infoPanelPage, page}) => {
		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const vocabularyName = getRandomString();
		const categoryName = getRandomString();

		const vocabularyId = await apiHelpers.headlessAdminTaxonomy
			.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1}],
				assetTypes: [
					{
						required: false,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name: vocabularyName,
				siteId,
				visibilityType: 'PUBLIC',
			})
			.then((response) => response.id);

		const categoryId = await apiHelpers.headlessAdminTaxonomy
			.postTaxonomyVocabularyTaxonomyCategory({
				name: categoryName,
				vocabularyId,
			})
			.then((response) => response.id);

		const contentTitle = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				taxonomyCategoryIds: [categoryId],
				title: contentTitle,
			},
			'cms/basic-web-contents',
			space.name
		);

		const fileBase64 = readFileSync(
			path.join(__dirname, '../dependencies/file_upload_image_1.jpg')
		).toString('base64');

		const fileTitle = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {fileBase64, name: 'file_upload_image_1.jpg'},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				taxonomyCategoryIds: [categoryId],
				title: fileTitle,
			},
			'cms/basic-documents',
			space.name
		);

		await test.step('Delete the category assigned to both entries', async () => {
			await categoriesPage.goto(vocabularyId, vocabularyName);

			await categoriesPage.execItemAction({
				action: 'Delete',
				filter: categoryName,
			});
			await categoriesPage.handleDeleteConfirmationModal(true);

			await expect(categoriesPage.getItem(categoryName)).toBeHidden();
		});

		await test.step('Verify the content entry is unaffected and no longer shows the category', async () => {
			await assetsPage.gotoContents();

			await assetsPage.execItemAction({
				action: 'Show Details',
				filter: contentTitle,
			});

			await expect(
				page.getByRole('heading', {name: contentTitle})
			).toBeVisible();

			await infoPanelPage.selectTab('Categorization').click();

			await expect(
				page.getByText(categoryName, {exact: true})
			).toBeHidden();
		});

		await test.step('Verify the file entry is unaffected and no longer shows the category', async () => {
			await assetsPage.gotoFiles();

			await assetsPage.execCardItemAction({
				action: 'Show Details',
				filter: fileTitle,
			});

			await expect(
				page.getByRole('heading', {name: fileTitle})
			).toBeVisible();

			await infoPanelPage.selectTab('Categorization').click();

			await expect(
				page.getByText(categoryName, {exact: true})
			).toBeHidden();
		});
	}
);
