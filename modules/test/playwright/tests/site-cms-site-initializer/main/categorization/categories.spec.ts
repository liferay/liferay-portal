/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
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
	}),
	loginTest()
);

const systemCategoryTest = mergeTests(
	categorizationPagesTest,
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPD-86291': {enabled: true},
	}),
	loginTest()
);

let vocabularyName: string;
let vocabularyId: number;

test.beforeEach('Create Vocabulary via API', async ({apiHelpers}) => {
	vocabularyName = getRandomString();

	const siteId = await apiHelpers.headlessAdminUser
		.getSiteByFriendlyUrlPath('cms')
		.then((response) => response.id);

	vocabularyId = await apiHelpers.headlessAdminTaxonomy
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
		'Validate category inputs',
		{tag: ['@LPD-32753', '@LPD-69687']},
		async ({editCategoryPage, page}) => {
			await editCategoryPage.gotoCreateCategory(vocabularyId);

			await expect(editCategoryPage.saveButton).toBeDisabled();

			await expect(
				editCategoryPage.saveAndAddAnotherButton
			).toBeDisabled();

			await editCategoryPage.fillName(getRandomString());

			await expect(editCategoryPage.saveButton).not.toBeDisabled();

			await expect(
				editCategoryPage.saveAndAddAnotherButton
			).not.toBeDisabled();

			await editCategoryPage.fillName('');

			await clickAndExpectToBeVisible({
				target: page.getByText('The Name field is required'),
				trigger: page.getByTestId('description-input'),
			});

			await expect(editCategoryPage.saveButton).toBeDisabled();

			await expect(
				editCategoryPage.saveAndAddAnotherButton
			).toBeDisabled();
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
				selectors: ['.cms-section'],
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
					'Please enter a unique name. This one is already in use.',
					{exact: true}
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
				selectors: ['.modal-content'],
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

			await expect(async () => {
				await categoriesPage.gotoSubcategories(
					categoryId,
					categoryName,
					vocabularyId,
					vocabularyName
				);

				await expect(
					categoriesPage.getItem(subcategoryName)
				).toBeVisible({timeout: 5000});
			}).toPass();
		}
	);
});

test.describe('Search category tests', () => {
	let categoryName1: string;
	let categoryName2: string;
	let prefix: string;

	test.beforeEach('Create Categories via API', async ({apiHelpers}) => {
		prefix = `category_name_${getRandomString().replace(/-/g, '')}`;
		categoryName1 = `${prefix}_one`;
		categoryName2 = `${prefix}_two`;

		await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
			{name: categoryName1, vocabularyId}
		);
		await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
			{name: categoryName2, vocabularyId}
		);
	});

	test(
		"Search a Vocabulary's Categories by name and prefix",
		{tag: '@LPD-89731'},
		async ({categoriesPage}) => {
			await categoriesPage.goto(vocabularyId, vocabularyName);

			await categoriesPage.search(categoryName1);

			await expect(categoriesPage.getItem(categoryName1)).toBeVisible();
			await expect(categoriesPage.getItem(categoryName2)).toBeHidden();

			await categoriesPage.search(prefix);

			await expect(categoriesPage.getItem(categoryName1)).toBeVisible();
			await expect(categoriesPage.getItem(categoryName2)).toBeVisible();
		}
	);

	test(
		"Clear search restores all of a Vocabulary's Categories",
		{tag: '@LPD-89731'},
		async ({categoriesPage}) => {
			await categoriesPage.goto(vocabularyId, vocabularyName);

			await categoriesPage.search(categoryName1);

			await expect(categoriesPage.getItem(categoryName1)).toBeVisible();
			await expect(categoriesPage.getItem(categoryName2)).toBeHidden();

			await categoriesPage.clearSearch();

			await expect(categoriesPage.getItem(categoryName1)).toBeVisible();
			await expect(categoriesPage.getItem(categoryName2)).toBeVisible();
		}
	);
});

test(
	'Content can be saved when all asset subtypes are required in a vocabulary',
	{tag: '@LPD-83651'},
	async ({apiHelpers, contentsPage, page}) => {
		const categoryName = getRandomString();

		await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
			{
				name: categoryName,
				vocabularyId,
			}
		);

		await contentsPage.goto();

		await contentsPage.createContent('Basic Web Content');

		const title = getRandomString();

		await contentsPage.fillData([{label: 'Title', value: title}]);

		await contentsPage.openSidePanel('Categorization');

		const categoriesAutocomplete = page.getByPlaceholder('Add category');

		await categoriesAutocomplete.fill(categoryName);

		const option = page.getByRole('option', {name: categoryName});

		await option.waitFor();
		await option.click();

		await contentsPage.saveContent();

		await expect(
			page.getByRole('link', {exact: true, name: `${title}`})
		).toBeVisible();
	}
);

test(
	'Folder can be saved when all asset subtypes are required in a vocabulary',
	{tag: '@LPD-83651'},
	async ({apiHelpers, contentsPage, page}) => {
		const categoryName = getRandomString();

		await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
			{
				name: categoryName,
				vocabularyId,
			}
		);

		await contentsPage.goto();

		const folderName = getRandomString();

		await contentsPage.createFolder(folderName, 'Default');

		await expect(
			page.getByRole('link', {exact: true, name: `${folderName}`})
		).toBeVisible();
	}
);

systemCategoryTest.describe('System category tests', () => {
	let systemCategoryId: number;
	let systemCategoryName: string;
	let systemVocabularyId: number;
	let systemVocabularyName: string;

	systemCategoryTest.beforeEach(
		'Create a vocabulary with a system category via API',
		async ({apiHelpers}) => {
			systemCategoryName = getRandomString();
			systemVocabularyName = getRandomString();

			const siteId = await apiHelpers.headlessAdminUser
				.getSiteByFriendlyUrlPath('cms')
				.then((response) => response.id);

			systemVocabularyId = await apiHelpers.headlessAdminTaxonomy
				.postSiteTaxonomyVocabulary({
					assetLibraries: [{id: -1}],
					assetTypes: [
						{
							required: true,
							subtype: 'AllAssetSubtypes',
							type: 'AllAssetTypes',
						},
					],
					name: systemVocabularyName,
					siteId,
					visibilityType: 'PUBLIC',
				})
				.then((response) => response.id);

			systemCategoryId = await apiHelpers.headlessAdminTaxonomy
				.postTaxonomyVocabularyTaxonomyCategory({
					name: systemCategoryName,
					system: true,
					vocabularyId: systemVocabularyId,
				})
				.then((response) => response.id);
		}
	);

	systemCategoryTest.afterEach(async ({apiHelpers}) => {

		// A system category cannot be deleted while LPD-86291 is enabled, so
		// disable it before the vocabulary and its categories are cleaned up.

		await apiHelpers.featureFlag.updateFeatureFlag('LPD-86291', false);
	});

	systemCategoryTest(
		'Mark a system category with a lock icon and hide its edit, move, and delete actions',
		{tag: '@LPD-96625'},
		async ({categoriesPage}) => {
			await categoriesPage.goto(systemVocabularyId, systemVocabularyName);

			// The system category is marked with a lock icon

			await expect(
				categoriesPage.getItemSystemIcon(systemCategoryName)
			).toBeVisible();

			// The edit, move, and delete actions are not offered

			await categoriesPage.expectItemActionHidden({
				action: 'Edit',
				filter: systemCategoryName,
			});
			await categoriesPage.expectItemActionHidden({
				action: 'Move',
				filter: systemCategoryName,
			});
			await categoriesPage.expectItemActionHidden({
				action: 'Delete',
				filter: systemCategoryName,
			});
		}
	);

	systemCategoryTest(
		'Lock the protected fields when editing a system category',
		{tag: '@LPD-96625'},
		async ({editCategoryPage, page}) => {
			await editCategoryPage.gotoEditCategory(systemCategoryId);

			await expect(
				page.getByText(`Edit ${systemCategoryName}`)
			).toBeVisible();

			// The name and description cannot be edited

			await expect(editCategoryPage.nameInput).toBeDisabled();
			await expect(editCategoryPage.descriptionInput).toBeDisabled();
		}
	);

	systemCategoryTest(
		'Add a subcategory to a system category',
		{tag: '@LPD-96625'},
		async ({categoriesPage, editCategoryPage, page}) => {
			await categoriesPage.goto(systemVocabularyId, systemVocabularyName);

			// Subcategories can still be added to a system category

			await categoriesPage.execItemAction({
				action: 'Add Subcategory',
				filter: systemCategoryName,
			});

			await expect(page.getByText('Basic Info')).toBeVisible();

			const subcategoryName = getRandomString();

			await editCategoryPage.fillName(subcategoryName);
			await editCategoryPage.clickSave();

			// The subcategory is created under the system category

			await categoriesPage.gotoSubcategories(
				systemCategoryId,
				systemCategoryName,
				systemVocabularyId,
				systemVocabularyName
			);

			await expect(categoriesPage.getItem(subcategoryName)).toBeVisible();
		}
	);

	systemCategoryTest(
		'Keep regular categories in the same vocabulary editable',
		{tag: '@LPD-96625'},
		async ({apiHelpers, categoriesPage, page}) => {
			const regularCategoryName = getRandomString();

			await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
				{
					name: regularCategoryName,
					vocabularyId: systemVocabularyId,
				}
			);

			await categoriesPage.goto(systemVocabularyId, systemVocabularyName);

			// A regular category is not marked as system

			await expect(
				categoriesPage.getItemSystemIcon(regularCategoryName)
			).toBeHidden();

			// A regular category can still be edited

			await categoriesPage.execItemAction({
				action: 'Edit',
				filter: regularCategoryName,
			});

			await expect(
				page.getByText(`Edit ${regularCategoryName}`)
			).toBeVisible();
		}
	);
});
