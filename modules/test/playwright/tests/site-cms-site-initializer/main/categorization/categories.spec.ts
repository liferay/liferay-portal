/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {checkAccessibility} from '../../../../utils/checkAccessibility';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {categorizationPagesTest} from '../fixtures/categorizationPagesTest';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';
import {DataSetPage} from '../pages/DataSetPage';

const test = mergeTests(
	categorizationPagesTest,
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	loginTest(),
	isolatedSiteTest
);

let vocabularyName: string;
let vocabularyId: number;

test.beforeEach('Create Vocabulary via API', async ({apiHelpers, site}) => {
	vocabularyName = getRandomString();

	vocabularyId = await apiHelpers.headlessAdminTaxonomy
		.postSiteTaxonomyVocabulary({
			name: vocabularyName,
			siteId: site.id,
		})
		.then((response) => response.id);
});

test.afterEach(async ({apiHelpers}) => {
	await apiHelpers.headlessAdminTaxonomy.deleteTaxonomyVocabulary(
		vocabularyId
	);
});

test.describe('Category tests that focus on creation', () => {
	test(
		'Categories can be created within a Vocabulary with both the "Save and Add Another" and "Save" buttons',
		{tag: '@LPD-32753'},
		async ({categoriesPage, editCategoryPage}) => {
			await categoriesPage.goto(vocabularyId, vocabularyName);

			await categoriesPage.clickCreateNewCategoryButton();

			const categoryName1: string = getRandomString();

			await editCategoryPage.fillName(categoryName1);
			await editCategoryPage.fillDescription(getRandomString());

			await checkAccessibility({
				page: editCategoryPage.page,
				selectors: ['.cms-section'],
				selectorsToExclude: ['.control-menu-container'],
			});

			await editCategoryPage.clickSaveAndAddAnother();

			const categoryName2: string = getRandomString();

			await editCategoryPage.fillName(categoryName2);
			await editCategoryPage.fillDescription(getRandomString());

			await editCategoryPage.clickSave();

			await categoriesPage.assertBreadcrumbItemText(0, 'Categorization');

			await checkAccessibility({
				page: categoriesPage.page,
				selectors: ['.content'],
				selectorsToExclude: [
					'.control-menu-container',
					'.fds',
					'.sidebar-container',
					'.top-bar',
				],
			});

			await expect(categoriesPage.getItem(categoryName1)).toBeVisible();
			await expect(categoriesPage.getItem(categoryName2)).toBeVisible();
		}
	);

	test(
		'Validate the create Category form inputs when saving',
		{tag: '@LPD-32753'},
		async ({editCategoryPage, page}) => {
			await editCategoryPage.gotoCreateCategory(vocabularyId);

			// Shouldn't be able to save if Name field is empty

			await clickAndExpectToBeVisible({
				target: page.getByText('The Name field is required'),
				trigger: editCategoryPage.saveButton,
			});
		}
	);

	test(
		'Create a Category with non-default permissions',
		{tag: '@LPD-54328'},
		async ({categoriesPage, editCategoryPage, page}) => {
			await categoriesPage.goto(vocabularyId, vocabularyName);

			await categoriesPage.clickCreateNewCategoryButton();

			const categoryName: string = getRandomString();

			await editCategoryPage.fillName(categoryName);

			await editCategoryPage.setViewableByPermissions('Guest');
			await editCategoryPage.assertDefaultViewableByPermissions('Guest');

			await editCategoryPage.setViewableByPermissions('Site Member');
			await editCategoryPage.assertDefaultViewableByPermissions(
				'Site Member'
			);

			await editCategoryPage.setViewableByPermissions('Owner');
			await editCategoryPage.assertDefaultViewableByPermissions('Owner');

			await editCategoryPage.setViewableByPermissions('Guest');

			await editCategoryPage.tickPermissionCheckbox('Guest', 'Delete');

			await editCategoryPage.clickSave();

			await categoriesPage.assertBreadcrumbItemText(0, 'Categorization');

			await expect(categoriesPage.getItem(categoryName)).toBeVisible();

			await categoriesPage.execItemAction({
				action: 'Permissions',
				filter: categoryName,
			});

			await expect(
				page.getByRole('heading', {name: 'Permissions'})
			).toBeVisible();

			await categoriesPage.assertPermissions([
				{enabled: true, locator: '#guest_ACTION_DELETE'},
				{enabled: false, locator: '#guest_ACTION_UPDATE'},
				{enabled: true, locator: '#guest_ACTION_VIEW'},
				{enabled: false, locator: '#site-member_ACTION_DELETE'},
			]);
		}
	);
});

test.describe("Category tests that don't focus on creation", () => {
	let categoryName: string;
	let categoryId: number;

	test.beforeEach('Create Category via API', async ({apiHelpers}) => {
		categoryName = getRandomString();

		categoryId = await apiHelpers.headlessAdminTaxonomy
			.postTaxonomyVocabularyTaxonomyCategory({
				name: categoryName,
				vocabularyId,
			})
			.then((response) => response.id);
	});

	test(
		"Edit a Vocabulary's Category",
		{tag: '@LPD-53252'},
		async ({categoriesPage, editCategoryPage, page}) => {
			await categoriesPage.goto(vocabularyId, vocabularyName);

			await page.getByRole('link', {name: categoryName}).click();

			await expect(page.getByText(`Edit ${categoryName}`)).toBeVisible();

			const newCategoryName = getRandomString();
			const newCategoryDescription = getRandomString();

			await editCategoryPage.fillName(newCategoryName);
			await editCategoryPage.fillDescription(newCategoryDescription);

			await editCategoryPage.clickSave();
			await editCategoryPage.handleEditConfirmationModal(true);

			await categoriesPage.assertBreadcrumbItemText(0, 'Categorization');

			await expect(categoriesPage.getItem(newCategoryName)).toBeVisible();

			await editCategoryPage.gotoEditCategory(categoryId);

			await expect(page.getByText(newCategoryDescription)).toBeVisible();
		}
	);

	test(
		"Visit the edit page of a Vocabulary's Category from dropdown actions",
		{tag: '@LPD-53252'},
		async ({categoriesPage, page}) => {
			await categoriesPage.goto(vocabularyId, vocabularyName);

			await categoriesPage.execItemAction({
				action: 'Edit',
				filter: categoryName,
			});

			await expect(page.getByText(`Edit ${categoryName}`)).toBeVisible();
		}
	);

	test(
		"Delete a Vocabulary's Category from dropdown actions",
		{tag: '@LPD-53252'},
		async ({categoriesPage, page}) => {
			await categoriesPage.goto(vocabularyId, vocabularyName);

			await categoriesPage.execItemAction({
				action: 'Delete',
				filter: categoryName,
			});

			await categoriesPage.handleDeleteConfirmationModal(true);

			await expect(
				page.getByText('Success:Your request completed successfully.')
			).toBeVisible();
			await expect(categoriesPage.getItem(categoryName)).toBeHidden();
		}
	);

	test(
		"Edit a Category's permissions from dropdown actions",
		{tag: '@LPD-53252'},
		async ({categoriesPage, page}) => {
			await categoriesPage.goto(vocabularyId, vocabularyName);

			await categoriesPage.execItemAction({
				action: 'Permissions',
				filter: categoryName,
			});

			await expect(
				page.getByRole('heading', {name: 'Permissions'})
			).toBeVisible();
		}
	);

	test(
		"Edit a Category's properties",
		{tag: '@54213'},
		async ({categoriesPage, editCategoryPage, page}) => {
			await categoriesPage.goto(vocabularyId, vocabularyName);

			await page.getByRole('link', {name: categoryName}).click();

			await expect(page.getByText(`Edit ${categoryName}`)).toBeVisible();

			await checkAccessibility({
				page: editCategoryPage.page,
				selectors: ['.categorization-section'],
				selectorsToExclude: ['.control-menu-container'],
			});

			await editCategoryPage.clickSidebarTab('Properties');

			await editCategoryPage.fillProperties([
				{key: 'key1', value: 'value1'},
				{key: 'key2', value: 'value2'},
			]);
			await editCategoryPage.assertProperties([
				{key: 'key1', value: 'value1'},
				{key: 'key2', value: 'value2'},
			]);

			await editCategoryPage.deleteNthPropertyRow(0);
			await editCategoryPage.assertProperties([
				{key: 'key2', value: 'value2'},
			]);

			// Add an empty property row to test that we handle basic linting of empty property row data

			await editCategoryPage.addPropertyRow();
			await editCategoryPage.assertProperties([
				{key: 'key2', value: 'value2'},
				{key: '', value: ''},
			]);

			await editCategoryPage.addPropertyRow('key3', 'value3');
			await editCategoryPage.assertProperties([
				{key: 'key2', value: 'value2'},
				{key: '', value: ''},
				{key: 'key3', value: 'value3'},
			]);

			await page.waitForTimeout(2000);

			await checkAccessibility({
				page: editCategoryPage.page,
				selectors: ['.cms-section'],
				selectorsToExclude: [
					'categorization-vertical-nav',
					'.control-menu-container',
				],
			});

			await editCategoryPage.clickSave();
			await editCategoryPage.handleEditConfirmationModal(true);

			await categoriesPage.assertBreadcrumbItemText(0, 'Categorization');

			await expect(categoriesPage.getItem(categoryName)).toBeVisible();

			await page.getByRole('link', {name: categoryName}).click();

			await expect(page.getByText(`Edit ${categoryName}`)).toBeVisible();

			await editCategoryPage.clickSidebarTab('Properties');

			await editCategoryPage.assertProperties([
				{key: 'key2', value: 'value2'},
				{key: 'key3', value: 'value3'},
			]);
		}
	);

	test(
		"View a Category's usages",
		{tag: '@LPD-54560'},
		async ({apiHelpers, categoriesPage, page}) => {
			await categoriesPage.goto(vocabularyId, vocabularyName);

			await categoriesPage.execItemAction({
				action: 'View Usages',
				filter: categoryName,
			});

			await expect(page.getByText('No Results Found')).toBeVisible();

			const basicWebContentObjectEntry = {
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				taxonomyCategoryIds: [categoryId],
				title: getRandomString(),
			};

			await apiHelpers.objectEntry.postObjectEntry(
				basicWebContentObjectEntry,
				'cms/basic-web-contents/scopes/Default'
			);

			await categoriesPage.goto(vocabularyId, vocabularyName);

			await categoriesPage.execItemAction({
				action: 'View Usages',
				filter: categoryName,
			});

			const dataSetPage = new DataSetPage(page);

			await checkAccessibility({
				page: dataSetPage.page,
				selectors: ['.content'],
				selectorsToExclude: [
					'.control-menu-container',
					'.sidebar-container',
					'.top-bar',
				],
			});

			await expect(
				dataSetPage.getRow(basicWebContentObjectEntry.title)
			).toBeVisible();
		}
	);

	test(
		'Validate that a UI error appears when attempting to create a category with an existing name',
		{tag: '@LPD-57497'},
		async ({editCategoryPage, page}) => {
			await editCategoryPage.gotoCreateCategory(vocabularyId);

			await editCategoryPage.fillName(categoryName);

			await editCategoryPage.clickSave();

			await clickAndExpectToBeVisible({
				target: page.getByText(
					'Please enter a unique name. This one is already in use.'
				),
				trigger: editCategoryPage.saveButton,
			});
		}
	);
});

test.describe('Move category tests', () => {
	let categoryName: string;

	test.beforeEach('Create Category via API', async ({apiHelpers}) => {
		categoryName = getRandomString();

		await apiHelpers.headlessAdminTaxonomy
			.postTaxonomyVocabularyTaxonomyCategory({
				name: categoryName,
				vocabularyId,
			})
			.then((response) => response.id);
	});

	test(
		'Can move a category to another vocabulary',
		{tag: '@LPD-56092'},
		async ({
			categoriesPage,
			editVocabularyPage,
			page,
			vocabulariesPage,
		}) => {
			const vocabularyName2 = await editVocabularyPage.createVocabulary();

			await categoriesPage.goto(vocabularyId, vocabularyName);

			await categoriesPage.execItemAction({
				action: 'Move',
				filter: categoryName,
			});

			await expect(categoriesPage.getItem(categoryName)).toBeVisible();

			await page
				.getByRole('treeitem', {name: vocabularyName2})
				.locator('span')
				.nth(1)
				.click();

			await checkAccessibility({
				page,
				selectors: ['.category-selector-modal'],
			});

			await page.getByRole('button', {name: 'move'}).click();

			await expect(
				categoriesPage.getItem(categoryName)
			).not.toBeVisible();

			await page.goto(PORTLET_URLS.cmsVocabularies);

			await vocabulariesPage.execItemAction({
				action: 'View Categories',
				filter: vocabularyName2,
			});

			await expect(categoriesPage.getItem(categoryName)).toBeVisible();

			await page.goto(PORTLET_URLS.cmsVocabularies);

			await vocabulariesPage.execItemAction({
				action: 'Delete',
				filter: vocabularyName2,
			});

			await expect(
				page.getByRole('heading', {name: `Delete "${vocabularyName2}"`})
			).toBeVisible();

			await clickAndExpectToBeVisible({
				target: page.getByText(
					'Success:Your request completed successfully.'
				),
				trigger: page.getByRole('button', {name: 'Delete'}),
			});
		}
	);
});

test.describe('Subcategory tests', () => {
	let categoryName: string;
	let categoryId: number;

	test.beforeEach('Create Subcategory via API', async ({apiHelpers}) => {
		categoryName = getRandomString();

		categoryId = await apiHelpers.headlessAdminTaxonomy
			.postTaxonomyVocabularyTaxonomyCategory({
				name: categoryName,
				vocabularyId,
			})
			.then((response) => response.id);
	});

	test(
		'Subcategories can be created within a Category with both the "Save and Add Another" and "Save" buttons',
		{tag: '@LPD-54221'},
		async ({categoriesPage, editCategoryPage}) => {
			await categoriesPage.gotoSubcategories(
				categoryId,
				categoryName,
				vocabularyId,
				vocabularyName
			);

			await categoriesPage.clickCreateNewSubcategoryButton();

			const subcategoryName1: string = getRandomString();

			await editCategoryPage.fillName(subcategoryName1);
			await editCategoryPage.fillDescription(getRandomString());

			await editCategoryPage.clickSaveAndAddAnother();

			const subcategoryName2: string = getRandomString();

			await editCategoryPage.fillName(subcategoryName2);
			await editCategoryPage.fillDescription(getRandomString());

			await editCategoryPage.clickSave();

			await categoriesPage.assertBreadcrumbItemText(2, categoryName);

			await expect(
				categoriesPage.getItem(subcategoryName1)
			).toBeVisible();
			await expect(
				categoriesPage.getItem(subcategoryName2)
			).toBeVisible();
		}
	);

	test(
		'Subcategories can be created within a Category from the dropdown actions',
		{tag: '@LPD-54221'},
		async ({categoriesPage, editCategoryPage}) => {
			await categoriesPage.goto(vocabularyId, vocabularyName);

			await categoriesPage.execItemAction({
				action: 'Add Subcategory',
				filter: categoryName,
			});

			const subcategoryName: string = getRandomString();

			await editCategoryPage.fillName(subcategoryName);
			await editCategoryPage.fillDescription(getRandomString());

			await editCategoryPage.clickSave();

			await categoriesPage.assertBreadcrumbItemText(1, vocabularyName);

			await expect(categoriesPage.getItem('1')).toBeVisible();

			await categoriesPage.getItem('1').click();

			await categoriesPage.assertBreadcrumbItemText(2, categoryName);

			await expect(categoriesPage.getItem(subcategoryName)).toBeVisible();
		}
	);
});
