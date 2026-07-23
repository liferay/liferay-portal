/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {createCategories} from '../../../../helpers/CreateCategories';
import {checkAccessibility} from '../../../../utils/checkAccessibility';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {categorizationPagesTest} from '../fixtures/categorizationPagesTest';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';
import {ContentsPage} from '../pages/ContentsPage';

const test = mergeTests(
	categorizationPagesTest,
	cmsPagesTest,
	dataApiHelpersTest,
	loginTest()
);

const createdVocabularyNames: string[] = [];

test.afterEach(async ({vocabulariesPage}) => {
	if (!createdVocabularyNames.length) {
		return;
	}

	await vocabulariesPage.goto();

	for (const name of createdVocabularyNames) {
		await vocabulariesPage.deleteVocabulary(name);
	}

	createdVocabularyNames.length = 0;
});

const projectVocabularyTest = mergeTests(
	categorizationPagesTest,
	cmsPagesTest,
	dataApiHelpersTest,
	loginTest()
);

const systemVocabularyTest = mergeTests(
	categorizationPagesTest,
	cmsPagesTest,
	dataApiHelpersTest,
	loginTest()
);

const createScopedVocabularyAndContent = async ({
	apiHelpers,
	assetLibraries,
	assetTypes,
	categoryName,
	contentsPage,
	page,
	siteId,
}: {
	apiHelpers: ApiHelpers;
	assetLibraries: AssetLibrary[];
	assetTypes: AssetType[];
	categoryName: string;
	contentsPage: ContentsPage;
	page: Page;
	siteId: string;
}) => {
	await createCategories({
		apiHelpers,
		assetLibraries,
		assetTypes,
		categoryNames: [{name: categoryName}],
		siteId,
		vocabularyName: getRandomString(),
	});

	await contentsPage.goto();

	await contentsPage.createContent('Basic Web Content');

	const title = getRandomString();

	await page.getByPlaceholder('New Basic Web Content').fill(title);

	await contentsPage.publishButton.click();

	await page.locator('.table-list-title a', {hasText: title}).click();
};

test(
	'Assert can delete vocabulary from dropdown actions',
	{tag: '@LPD-32750'},
	async ({editVocabularyPage, page, vocabulariesPage}) => {
		editVocabularyPage.goto();

		const name = `Vocabulary${getRandomInt()}`;

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		await clickAndExpectToBeVisible({
			target: page.getByText(
				`Success:${name} was published successfully.`
			),
			trigger: editVocabularyPage.saveButton,
		});

		await vocabulariesPage.execItemAction({
			action: 'Delete',
			filter: name,
		});

		await expect(
			page.getByRole('heading', {name: `Delete "${name}"`})
		).toBeVisible();

		await expect(page.locator('.liferay-modal .modal-dialog')).toHaveClass(
			/modal-dialog-centered/
		);

		await clickAndExpectToBeVisible({
			target: page.getByText(
				'Success:Your request completed successfully.'
			),
			trigger: page.getByRole('button', {name: 'Delete'}),
		});

		await expect(vocabulariesPage.getItem(name)).toBeHidden();

		await checkAccessibility({
			page: vocabulariesPage.page,
			selectors: ['.content'],
			selectorsToExclude: [
				'.control-menu-container',
				'.fds',
				'.sidebar-container',
				'.top-bar',
			],
		});
	}
);

test(
	'Assert can edit vocabulary from dropdown actions',
	{tag: '@LPD-32750'},
	async ({editVocabularyPage, page, vocabulariesPage}) => {
		const name = `Vocabulary${getRandomInt()}`;

		createdVocabularyNames.push(name);

		await editVocabularyPage.goto();

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		await clickAndExpectToBeVisible({
			target: page.getByText(
				`Success:${name} was published successfully.`
			),
			trigger: editVocabularyPage.saveButton,
		});

		await vocabulariesPage.execItemAction({
			action: 'Edit',
			filter: name,
		});

		await expect(page.getByText(`Edit ${name}`)).toBeVisible();
	}
);

test(
	'Assert can edit vocabulary permissions from dropdown actions',
	{tag: '@LPD-32750'},
	async ({editVocabularyPage, page, vocabulariesPage}) => {
		const name = `Vocabulary${getRandomInt()}`;

		createdVocabularyNames.push(name);

		editVocabularyPage.goto();

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		await clickAndExpectToBeVisible({
			target: page.getByText(
				`Success:${name} was published successfully.`
			),
			trigger: editVocabularyPage.saveButton,
		});

		await vocabulariesPage.execItemAction({
			action: 'Permissions',
			filter: name,
		});

		await expect(
			page.getByRole('heading', {name: 'Permissions'})
		).toBeVisible();
	}
);

test(
	'Add category from vocabulary dropdown actions',
	{tag: '@LPD-69691'},
	async ({
		apiHelpers,
		categoriesPage,
		editCategoryPage,
		page,
		vocabulariesPage,
	}) => {
		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const vocabularyName = getRandomString();

		await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
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
		});

		await vocabulariesPage.goto();

		await vocabulariesPage.execItemAction({
			action: 'Add Category',
			filter: vocabularyName,
		});

		await expect(page.getByText('Basic Info')).toBeVisible();

		const categoryName = getRandomString();

		await editCategoryPage.fillName(categoryName);
		await editCategoryPage.clickSave();

		await categoriesPage.assertBreadcrumbItemText(0, 'Categorization');
		await categoriesPage.assertBreadcrumbItemText(1, vocabularyName);

		await expect(categoriesPage.getItem(categoryName)).toBeVisible();
	}
);

test(
	'Can create and update vocabulary',
	{tag: ['@LPD-32750', '@LPD-66358']},
	async ({editVocabularyPage, page, vocabulariesPage}) => {
		let name = `Vocabulary${getRandomInt()}`;

		createdVocabularyNames.push(name);

		editVocabularyPage.goto();

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		await checkAccessibility({
			page: editVocabularyPage.page,
			selectors: ['.cms-section'],
			selectorsToExclude: ['.control-menu-container'],
		});

		await editVocabularyPage.multiSelectToggle.click();

		await editVocabularyPage.changeVisibility('Private');

		await editVocabularyPage.assetTypesButton.click();

		// Verify that All Asset Types checkbox retains correct state when ticked repeatedly to test LPD-66358

		await editVocabularyPage.assetTypeCheckbox.uncheck();

		await expect(
			page.getByText('The Asset Types field is required.')
		).toBeVisible();

		await editVocabularyPage.assetTypeCheckbox.check();

		await expect(editVocabularyPage.assetTypeSelector).toHaveAttribute(
			'placeholder',
			'All Asset Types'
		);

		await editVocabularyPage.selectAssetTypes('Blog');

		await editVocabularyPage.assetTypeToggle.check();

		await clickAndExpectToBeVisible({
			target: page.getByText(
				`Success:${name} was published successfully.`
			),
			trigger: editVocabularyPage.saveButton,
		});

		const newVocabRow = vocabulariesPage.getItem(name);
		await expect(newVocabRow).toBeVisible();

		const newVocabualry = page.getByRole('link', {name});

		await newVocabualry.click();

		await checkAccessibility({
			page: editVocabularyPage.page,
			selectors: ['.cms-section'],
			selectorsToExclude: ['.control-menu-container'],
		});

		await expect(page.getByText(`Edit ${name}`)).toBeVisible();

		await expect(editVocabularyPage.multiSelectToggle).not.toBeChecked();

		await expect(editVocabularyPage.visibilitySelector).toBeDisabled();

		await expect(editVocabularyPage.visibilitySelector).toContainText(
			'Private'
		);

		const spacesInputLocator = page.getByLabel('Space Selector', {
			exact: true,
		});

		await expect(spacesInputLocator).toHaveAttribute('value', 'All Spaces');

		name = `Vocabulary${getRandomInt()}`;

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		await editVocabularyPage.assetTypesButton.click();

		const assetTypesInputLocator = page.locator('div[role="grid"] > span');

		await expect(assetTypesInputLocator).toContainText(['Blog']);

		await expect(editVocabularyPage.assetTypeToggle).toBeChecked();

		await clickAndExpectToBeVisible({
			target: page.getByText(`Success:${name} was updated successfully.`),
			trigger: editVocabularyPage.saveButton,
		});

		createdVocabularyNames[createdVocabularyNames.length - 1] = name;

		await expect(vocabulariesPage.getItem(name)).toBeVisible();
	}
);

test(
	'Validate change asset types when saving',
	{tag: '@LPD-52591'},
	async ({editVocabularyPage, page, vocabulariesPage}) => {
		const name = `Vocabulary${getRandomInt()}`;

		createdVocabularyNames.push(name);

		editVocabularyPage.goto();

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		await clickAndExpectToBeVisible({
			target: page.getByText(
				`Success:${name} was published successfully.`
			),
			trigger: editVocabularyPage.saveButton,
		});

		const newVocabRow = vocabulariesPage.getItem(name);
		await expect(newVocabRow).toBeVisible();

		const newVocabualry = page.getByRole('link', {name});

		await newVocabualry.click();

		await expect(page.getByText(`Edit ${name}`)).toBeVisible();

		await editVocabularyPage.assetTypesButton.click();

		await checkAccessibility({
			page: editVocabularyPage.page,
			selectors: ['.cms-section'],
			selectorsToExclude: [
				'categorization-vertical-nav',
				'.control-menu-container',
			],
		});

		await editVocabularyPage.selectAssetTypes('Blog');

		await clickAndExpectToBeVisible({
			target: page.getByText('Confirm Asset Type Change'),
			trigger: editVocabularyPage.saveButton,
		});

		const modalSaveButton = page.locator('.modal .btn-primary');

		await clickAndExpectToBeVisible({
			target: page.getByText(`Success:${name} was updated successfully.`),
			trigger: modalSaveButton,
		});
	}
);

test(
	'Validate change spaces when saving',
	{tag: '@LPD-52592'},
	async ({editVocabularyPage, page, vocabulariesPage}) => {
		const name = `Vocabulary${getRandomInt()}`;

		createdVocabularyNames.push(name);

		editVocabularyPage.goto();

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		await clickAndExpectToBeVisible({
			target: page.getByText(
				`Success:${name} was published successfully.`
			),
			trigger: editVocabularyPage.saveButton,
		});

		const newVocabRow = vocabulariesPage.getItem(name);
		await expect(newVocabRow).toBeVisible();

		const newVocabualry = page.getByRole('link', {name});

		await newVocabualry.click();

		await expect(page.getByText(`Edit ${name}`)).toBeVisible();

		const spaceName = 'Default';

		await editVocabularyPage.selectSpaces(spaceName);

		await clickAndExpectToBeVisible({
			target: page.getByText('Confirm Space Change'),
			trigger: editVocabularyPage.saveButton,
		});

		const modalSaveButton = page.locator('.modal .btn-primary');

		await clickAndExpectToBeVisible({
			target: page.getByText(`Success:${name} was updated successfully.`),
			trigger: modalSaveButton,
		});
	}
);

test(
	'Validate vocabulary inputs',
	{tag: ['@LPD-32750', '@LPD-69687', '@LPD-88757']},
	async ({editVocabularyPage, page}) => {
		editVocabularyPage.goto();

		// Check we can't publish an empty name

		await expect(editVocabularyPage.saveButton).toBeDisabled();

		await editVocabularyPage.fillName('');

		await clickAndExpectToBeVisible({
			target: page.getByText('The Name field is required'),
			trigger: page.getByLabel('Description'),
		});

		const name = `Vocabulary${getRandomInt()}`;

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		await expect(editVocabularyPage.saveButton).not.toBeDisabled();

		// Check we can't publish without selecting a space

		await editVocabularyPage.spaceCheckbox.click();

		await expect(editVocabularyPage.saveButton).toBeDisabled();

		await editVocabularyPage.spaceCheckbox.click();

		// Check we can't publish without selecting an asset type

		await editVocabularyPage.assetTypesButton.click();

		// await editVocabularyPage.assetTypeCheckbox.click();

		await clickAndExpectToBeVisible({
			target: page.getByText('The Asset Types field is required.'),
			trigger: editVocabularyPage.assetTypeCheckbox,
		});

		await expect(editVocabularyPage.saveButton).toBeDisabled();

		// Check the external reference code input shows an error when longer
		// than 75 characters

		await page.getByRole('menuitem', {name: 'General'}).click();

		await editVocabularyPage.changeGeneralInfo({
			externalReferenceCode: 'x'.repeat(80),
		});

		await expect(
			page.getByText(
				'External reference code cannot exceed 75 characters.'
			)
		).toBeVisible();

		await expect(page.getByLabel('External Reference Code')).toHaveValue(
			'x'.repeat(80)
		);
	}
);

test(
	'Hide a Space-restricted vocabulary from content in another Space',
	{tag: '@LPD-89497'},
	async ({apiHelpers, contentsPage, page}) => {
		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const spaceName = getRandomString();

		const {id: spaceId} =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});

		const categoryName = getRandomString();

		await createScopedVocabularyAndContent({
			apiHelpers,
			assetLibraries: [{id: spaceId, name: spaceName}],
			assetTypes: [{required: false, type: 'AllAssetTypes'}],
			categoryName,
			contentsPage,
			page,
			siteId,
		});

		await contentsPage.openSidePanel('Categorization');

		await page.getByPlaceholder('Add category').fill(categoryName);

		await page.waitForTimeout(500);

		await expect(
			page.getByRole('option', {name: categoryName})
		).toBeHidden();
	}
);

test(
	'Hide an asset-type-restricted vocabulary from content of another asset type',
	{tag: '@LPD-89497'},
	async ({apiHelpers, contentsPage, page}) => {
		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const categoryName = getRandomString();

		await createScopedVocabularyAndContent({
			apiHelpers,
			assetLibraries: [{id: -1, name: 'All Spaces'}],
			assetTypes: [{required: false, type: 'BlogPosting'}],
			categoryName,
			contentsPage,
			page,
			siteId,
		});

		await contentsPage.openSidePanel('Categorization');

		await page.getByPlaceholder('Add category').fill(categoryName);

		await page.waitForTimeout(500);

		await expect(
			page.getByRole('option', {name: categoryName})
		).toBeHidden();
	}
);

test(
	'Open categories from the Categories column link',
	{tag: '@LPD-89497'},
	async ({apiHelpers, categoriesPage, vocabulariesPage}) => {
		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const vocabularyName = getRandomString();

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

		const categoryName1 = getRandomString();
		const categoryName2 = getRandomString();

		for (const name of [categoryName1, categoryName2]) {
			await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
				{
					name,
					vocabularyId,
				}
			);
		}

		await vocabulariesPage.goto();

		await vocabulariesPage.clickCategoriesLink(vocabularyName);

		await categoriesPage.assertBreadcrumbItemText(1, vocabularyName);

		await expect(categoriesPage.getItem(categoryName1)).toBeVisible();
		await expect(categoriesPage.getItem(categoryName2)).toBeVisible();
	}
);

test(
	'Search vocabularies by name',
	{tag: '@LPD-89497'},
	async ({apiHelpers, vocabulariesPage}) => {
		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const name1 = getRandomString();
		const name2 = getRandomString();

		for (const name of [name1, name2]) {
			await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1}],
				assetTypes: [
					{
						required: true,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name,
				siteId,
				visibilityType: 'PUBLIC',
			});
		}

		await vocabulariesPage.goto();

		await vocabulariesPage.search(name1);

		await expect(vocabulariesPage.getItem(name1)).toBeVisible();
		await expect(vocabulariesPage.getItem(name2)).toBeHidden();
	}
);

test(
	'Validate that a UI error appears when attempting to create a vocabulary with a duplicate name or external reference code',
	{tag: ['@LPD-57497', '@LPD-88752']},
	async ({editVocabularyPage, page}) => {
		const externalReferenceCode = `ERC${getRandomInt()}`;
		const name = `Vocabulary${getRandomInt()}`;

		createdVocabularyNames.push(name);

		await editVocabularyPage.goto();

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			externalReferenceCode,
			name,
		});

		await clickAndExpectToBeVisible({
			target: page.getByText(
				`Success:${name} was published successfully.`
			),
			trigger: editVocabularyPage.saveButton,
		});

		await test.step('Duplicate name shows the name-specific error', async () => {
			await editVocabularyPage.goto();

			await editVocabularyPage.changeGeneralInfo({
				description: getRandomString(),
				externalReferenceCode: `ERC${getRandomInt()}`,
				name,
			});

			await clickAndExpectToBeVisible({
				target: page.getByText(
					'Please enter a unique name. This one is already in use.',
					{exact: true}
				),
				trigger: editVocabularyPage.saveButton,
			});
		});

		await test.step('Duplicate external reference code shows the ERC-specific error', async () => {
			await editVocabularyPage.goto();

			await editVocabularyPage.changeGeneralInfo({
				description: getRandomString(),
				externalReferenceCode,
				name: `Vocabulary${getRandomInt()}`,
			});

			await clickAndExpectToBeVisible({
				target: page.getByText(
					'Please enter a unique external reference code.',
					{exact: true}
				),
				trigger: editVocabularyPage.saveButton,
			});
		});
	}
);

projectVocabularyTest.describe('Project selection tests', () => {
	const createdProjectVocabularyNames: string[] = [];

	projectVocabularyTest.afterEach(async ({vocabulariesPage}) => {
		if (!createdProjectVocabularyNames.length) {
			return;
		}

		await vocabulariesPage.goto();

		for (const name of createdProjectVocabularyNames) {
			await vocabulariesPage.deleteVocabulary(name);
		}

		createdProjectVocabularyNames.length = 0;
	});

	projectVocabularyTest(
		'Validate change projects when saving',
		{tag: '@LPD-96114'},
		async ({apiHelpers, editVocabularyPage, page, vocabulariesPage}) => {
			const projectName = getRandomString();

			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: projectName,
				settings: {},
				type: 'Project',
			});

			const name = `Vocabulary${getRandomInt()}`;

			createdProjectVocabularyNames.push(name);

			editVocabularyPage.goto();

			await editVocabularyPage.changeGeneralInfo({
				description: getRandomString(),
				name,
			});

			await clickAndExpectToBeVisible({
				target: page.getByText(
					`Success:${name} was published successfully.`
				),
				trigger: editVocabularyPage.saveButton,
			});

			const newVocabRow = vocabulariesPage.getItem(name);
			await expect(newVocabRow).toBeVisible();

			await page.getByRole('link', {name}).click();

			await expect(page.getByText(`Edit ${name}`)).toBeVisible();

			await editVocabularyPage.selectProjects(projectName);

			await clickAndExpectToBeVisible({
				target: page.getByText('Confirm Project Change'),
				trigger: editVocabularyPage.saveButton,
			});

			const modalSaveButton = page.locator('.modal .btn-primary');

			await clickAndExpectToBeVisible({
				target: page.getByText(
					`Success:${name} was updated successfully.`
				),
				trigger: modalSaveButton,
			});
		}
	);

	projectVocabularyTest(
		'Validate a project must be selected to publish',
		{tag: '@LPD-96114'},
		async ({editVocabularyPage}) => {
			editVocabularyPage.goto();

			const name = `Vocabulary${getRandomInt()}`;

			await editVocabularyPage.changeGeneralInfo({
				description: getRandomString(),
				name,
			});

			await expect(editVocabularyPage.saveButton).not.toBeDisabled();

			// Unselecting every project blocks publishing

			await editVocabularyPage.projectCheckbox.click();

			await expect(editVocabularyPage.saveButton).toBeDisabled();

			await editVocabularyPage.projectCheckbox.click();

			await expect(editVocabularyPage.saveButton).not.toBeDisabled();
		}
	);
});

systemVocabularyTest.describe('System vocabulary tests', () => {
	let systemVocabularyName: string;

	// A system vocabulary cannot be deleted once created, so it is left behind
	// on the site. Each test creates a uniquely named vocabulary and searches
	// for it, so the leftover data does not interfere with the assertions.

	systemVocabularyTest.beforeEach(
		'Create a system vocabulary via API',
		async ({apiHelpers}) => {
			systemVocabularyName = getRandomString();

			const siteId = await apiHelpers.headlessAdminUser
				.getSiteByFriendlyUrlPath('cms')
				.then((response) => response.id);

			await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1}],
				assetTypes: [
					{
						required: false,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name: systemVocabularyName,
				siteId,
				system: true,
				visibilityType: 'PUBLIC',
			});
		}
	);

	systemVocabularyTest(
		'Hide the delete action for a system vocabulary',
		{tag: '@LPD-93225'},
		async ({vocabulariesPage}) => {
			await vocabulariesPage.goto();

			await vocabulariesPage.search(systemVocabularyName);

			// The delete action is not offered for a system vocabulary

			await vocabulariesPage.expectItemActionHidden({
				action: 'Delete',
				filter: systemVocabularyName,
			});
		}
	);

	systemVocabularyTest(
		'Lock the protected fields and hide the asset types tab when editing a system vocabulary',
		{tag: '@LPD-93225'},
		async ({editVocabularyPage, page, vocabulariesPage}) => {
			await vocabulariesPage.goto();

			await vocabulariesPage.search(systemVocabularyName);

			// Open the system vocabulary edit page

			await page.getByRole('link', {name: systemVocabularyName}).click();

			await expect(
				page.getByText(`Edit ${systemVocabularyName}`)
			).toBeVisible();

			// Name, external reference code and description cannot be edited

			await expect(editVocabularyPage.nameInput).toBeDisabled();
			await expect(
				editVocabularyPage.externalReferenceCodeInput
			).toBeDisabled();
			await expect(editVocabularyPage.descriptionInput).toBeDisabled();

			// The spaces the vocabulary is available in cannot be changed

			await expect(editVocabularyPage.spaceCheckbox).toBeDisabled();

			// Allowing multiple categories remains editable

			await expect(editVocabularyPage.multiSelectToggle).toBeEnabled();

			// The associated asset types tab is not available

			await expect(editVocabularyPage.assetTypesButton).toBeHidden();
		}
	);

	systemVocabularyTest(
		'Add a category to a system vocabulary',
		{tag: '@LPD-93225'},
		async ({categoriesPage, editCategoryPage, page, vocabulariesPage}) => {
			await vocabulariesPage.goto();

			await vocabulariesPage.search(systemVocabularyName);

			// Categories can still be added to a system vocabulary

			await vocabulariesPage.execItemAction({
				action: 'Add Category',
				filter: systemVocabularyName,
			});

			await expect(page.getByText('Basic Info')).toBeVisible();

			const categoryName = getRandomString();

			await editCategoryPage.fillName(categoryName);
			await editCategoryPage.clickSave();

			await categoriesPage.assertBreadcrumbItemText(
				1,
				systemVocabularyName
			);

			await expect(categoriesPage.getItem(categoryName)).toBeVisible();
		}
	);
});
