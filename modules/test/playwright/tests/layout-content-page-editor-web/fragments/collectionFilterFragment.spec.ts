/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import getRandomString from '../../../utils/getRandomString';
import addApprovedStructuredContent from '../../../utils/structured-content/addApprovedStructuredContent';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {ANIMALS_COLLECTION_NAME} from '../../setup/page-management-site/main/constants/animals';
import createPageWithCollectionAndFilterCollection from '../main/utils/createPageWithCollectionAndFilterCollection';
import getCollectionDefinition from '../main/utils/getCollectionDefinition';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';

const FRAGMENT_FIELDS = [
	{
		id: 'element-text',
		value: {
			text: {
				mapping: {
					fieldKey: 'JournalArticle_title',
					itemReference: {
						contextSource: 'CollectionItem',
					},
				},
			},
		},
	},
];

const test = mergeTests(
	apiHelpersTest,
	collectionsPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	journalPagesTest,
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest
);

const testWithIsolatedSite = mergeTests(test, isolatedSiteTest);

const configureFilter = async (page: Page, option: 'category' | 'keywords') => {
	await page.getByLabel('Select', {exact: true}).click();

	await page.getByLabel(ANIMALS_COLLECTION_NAME).check();

	await page.getByLabel('Filter', {exact: true}).selectOption(option);

	if (option === 'category') {
		await page.getByLabel('Select Source').click();

		await page
			.frameLocator('iframe[title="Select"]')
			.getByRole('link', {name: ANIMALS_COLLECTION_NAME})
			.click();

		await expect(
			page
				.frameLocator('iframe[title="Select"]')
				.getByRole('button', {name: 'Select This Level'})
		).toBeEnabled();

		await clickAndExpectToBeHidden({
			target: page.locator('.modal-dialog'),
			trigger: page
				.frameLocator('iframe[title="Select"]')
				.getByRole('button', {name: 'Select This Level'}),
		});

		await expect(page.getByLabel('Source', {exact: true})).toHaveValue(
			ANIMALS_COLLECTION_NAME
		);
	}
};

const selectFilter = async (page: Page, categories: string[]) => {
	await page.getByRole('button', {name: 'Select'}).click();

	for (const category of categories) {
		await page.getByLabel(category).check({trial: true});
		await page.getByLabel(category).check({timeout: 1000});
	}

	await page.getByRole('button', {name: 'Apply'}).click();

	await page.waitForURL(/(.)filter_category(.)/);
};

test(
	'Filters a web content collection by single and multiple categories',
	{
		tag: '@LPS-124226',
	},
	async ({
		apiHelpers,
		collectionsPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create a definition for a Collection Filter

		const collectionFilterId = getRandomString();

		const collectionFilterDefinition = getFragmentDefinition({
			id: collectionFilterId,
			key: 'com.liferay.fragment.renderer.collection.filter.internal.CollectionFilterFragmentRenderer',
		});

		// Create definition for a collection mapped to Animals collection

		const animalsClassPK = await collectionsPage.getCollectionClassPK(
			ANIMALS_COLLECTION_NAME,
			pageManagementSite.friendlyUrlPath
		);

		const collectionDefinition = getCollectionDefinition({
			classPK: animalsClassPK,
			id: getRandomString(),
			pageElements: [
				getFragmentDefinition({
					fragmentFields: FRAGMENT_FIELDS,
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			],
		});

		// Create a content page and go to edit mode

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				collectionFilterDefinition,
				collectionDefinition,
			]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Go to edit mode of the created page and select the Collection Filter fragment

		await pageEditorPage.selectFragment(collectionFilterId);

		// Set Filter configuration for categories

		await configureFilter(page, 'category');

		// Check the option to show the label with the selected vocabulary

		await page.getByLabel('Show Label').check();

		await pageEditorPage.publishPage();

		// Go to view mode of the created page

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Both should be visible initially

		await expect(
			page.getByText('Animal 01 - Dogs and Cats categories')
		).toBeVisible();
		await expect(page.getByText('Animal 02 - Dogs category')).toBeVisible();

		await expect(
			page.getByText(ANIMALS_COLLECTION_NAME, {exact: true})
		).toBeVisible();

		// Select category filter: Cats

		await selectFilter(page, ['cats']);

		await expect(
			page.getByText('Animal 01 - Dogs and Cats categories')
		).toBeVisible();

		await expect(
			page.getByText('Animal 02 - Dogs category')
		).not.toBeVisible();

		// Select category filter: Cats and Dogs

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await selectFilter(page, ['dogs', 'cats']);

		await expect(
			page.getByText('Animal 01 - Dogs and Cats categories')
		).toBeVisible();

		await expect(page.getByText('Animal 02 - Dogs category')).toBeVisible();

		await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.id);
	}
);

testWithIsolatedSite(
	'Filters a web content collection by single and multiple tags',
	async ({apiHelpers, collectionsPage, page, pageEditorPage, site}) => {

		// Create two tags

		const tags = [];

		for (const tagName of ['Dogs', 'Cats']) {
			tags.push(
				await apiHelpers.headlessAdminTaxonomy.postSiteKeyword({
					name: tagName,
					siteId: site.id,
				})
			);
		}

		// Create two Web Contents with tags

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);
		const webContents = [
			{
				name: 'Web content with the tag Dogs',
				tags: ['Dogs'],
			},
			{
				name: 'Web content with the tags of Dogs and Cats',
				tags: ['Dogs', 'Cats'],
			},
			{
				name: 'Web content without tags',
			},
		];

		for (const {name, tags} of webContents) {
			await addApprovedStructuredContent({
				apiHelpers,
				contentStructureId,
				siteId: site.id,
				tags,
				title: name,
			});
		}

		// Create a dynamic collection with the previous Web Contents

		const collectionName = 'Animal Collection';

		await collectionsPage.goto(site.friendlyUrlPath);

		const {classPK} =
			await collectionsPage.createWebContentDynamicCollection(
				collectionName,
				site.friendlyUrlPath
			);

		// Create a page with Collection Display and Collection Filter fragments

		const collectionFilterId = getRandomString();

		const layout = await createPageWithCollectionAndFilterCollection({
			apiHelpers,
			classPK,
			collectionFilterId,
			siteId: site.id,
		});

		// Go to edit mode of the created page and select the Collection Filter fragment

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.selectFragment(collectionFilterId);

		// Set Filter configuration for tags

		await page.getByLabel('Select', {exact: true}).click();

		await page.getByLabel(collectionName).check();

		await page.getByLabel('Filter', {exact: true}).selectOption('tags');

		// Publish the page and go to the view mode

		await pageEditorPage.publishPage();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		for (const {name} of webContents) {
			await expect(page.getByText(name)).toBeVisible();
		}

		// Select tag filter: Cats

		await page.getByLabel('', {exact: true}).click();

		await page.getByRole('option', {name: 'Cats'}).click();

		await expect(page.getByText(webContents[0].name)).not.toBeVisible();
		await expect(page.getByText(webContents[1].name)).toBeVisible();
		await expect(page.getByText(webContents[2].name)).not.toBeVisible();

		// Select tag filter: Cats and Dogs

		await page.getByLabel('', {exact: true}).click();

		await page.getByRole('option', {name: 'Dogs'}).click();

		await expect(page.getByText(webContents[0].name)).toBeVisible();
		await expect(page.getByText(webContents[1].name)).toBeVisible();
		await expect(page.getByText(webContents[2].name)).not.toBeVisible();
	}
);

test('Enables search field in dropdown list of Collection Filter', async ({
	apiHelpers,
	collectionsPage,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create a definition for a Collection Filter

	const collectionFilterId = getRandomString();

	const collectionFilterDefinition = getFragmentDefinition({
		id: collectionFilterId,
		key: 'com.liferay.fragment.renderer.collection.filter.internal.CollectionFilterFragmentRenderer',
	});

	// Create definition for a collection mapped to Animals collection

	const animalsClassPK = await collectionsPage.getCollectionClassPK(
		ANIMALS_COLLECTION_NAME,
		pageManagementSite.friendlyUrlPath
	);

	const collectionDefinition = getCollectionDefinition({
		classPK: animalsClassPK,
		id: getRandomString(),
		pageElements: [
			getFragmentDefinition({
				fragmentFields: FRAGMENT_FIELDS,
				id: getRandomString(),
				key: 'BASIC_COMPONENT-heading',
			}),
		],
	});

	// Create a content page and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			collectionFilterDefinition,
			collectionDefinition,
		]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	// Go to edit mode of the created page and select the Collection Filter fragment

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	await pageEditorPage.selectFragment(collectionFilterId);

	// Set Filter configuration for categories

	await configureFilter(page, 'category');

	await page.getByLabel('Include Search Field').check();

	await pageEditorPage.publishPage();

	await page.goto(
		`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
	);

	// Check the categories that appear in the dropdown

	await page.getByRole('button', {name: 'Select'}).click();

	await expect(page.getByText('Dogs', {exact: true})).toBeVisible();
	await expect(page.getByText('Cats', {exact: true})).toBeVisible();

	await page.getByRole('textbox').fill('dogs');

	await expect(page.getByText('Dogs', {exact: true})).toBeVisible();
	await expect(page.getByText('Cats', {exact: true})).not.toBeVisible();

	await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.id);
});

test(
	'Filters the collection content by keywords using two filters',
	{
		tag: '@LPS-131947',
	},
	async ({
		apiHelpers,
		collectionsPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create a definition for a Collection Filter

		const firstCollectionFilterId = getRandomString();
		const secondCollectionFilterId = getRandomString();

		const firstCollectionFilterDefinition = getFragmentDefinition({
			id: firstCollectionFilterId,
			key: 'com.liferay.fragment.renderer.collection.filter.internal.CollectionFilterFragmentRenderer',
		});

		const secondFilterDefinition = getFragmentDefinition({
			id: secondCollectionFilterId,
			key: 'com.liferay.fragment.renderer.collection.filter.internal.CollectionFilterFragmentRenderer',
		});

		// Create definition for a collection mapped to Animals collection

		const animalsClassPK = await collectionsPage.getCollectionClassPK(
			ANIMALS_COLLECTION_NAME,
			pageManagementSite.friendlyUrlPath
		);

		const collectionDefinition = getCollectionDefinition({
			classPK: animalsClassPK,
			id: getRandomString(),
			pageElements: [
				getFragmentDefinition({
					fragmentFields: FRAGMENT_FIELDS,
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			],
		});

		// Create a content page and go to edit mode

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				firstCollectionFilterDefinition,
				secondFilterDefinition,
				collectionDefinition,
			]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Go to edit mode

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Configure the first filter by keywords

		await pageEditorPage.selectFragment(firstCollectionFilterId);

		await configureFilter(page, 'keywords');
		await pageEditorPage.waitForChangesSaved();

		// Configure the second filter by keywords

		await pageEditorPage.selectFragment(secondCollectionFilterId);

		await configureFilter(page, 'keywords');
		await pageEditorPage.waitForChangesSaved();

		// Publish the page

		await pageEditorPage.publishPage();

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Filter by keywords

		const firstFilter = page
			.getByPlaceholder('Search', {exact: true})
			.first();

		await firstFilter.fill('category categories');
		await firstFilter.press('Enter');

		await expect(
			page.getByText('Animal 01 - Dogs and Cats categories')
		).toBeVisible();
		await expect(page.getByText('Animal 02 - Dogs category')).toBeVisible();

		const secondFilter = page
			.getByPlaceholder('Search', {exact: true})
			.nth(1);

		await secondFilter.fill('Animal');
		await secondFilter.press('Enter');

		await expect(
			page.getByText('Animal 01 - Dogs and Cats categories')
		).toBeVisible();
		await expect(page.getByText('Animal 02 - Dogs category')).toBeVisible();

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(async () => {
			await firstFilter.fill('', {timeout: 1000});
			await firstFilter.fill('rabbit', {timeout: 1000});

			await firstFilter.press('Enter', {timeout: 1000});

			await expect(page.getByText('No Results Found')).toBeVisible({
				timeout: 1000,
			});
		}).toPass();

		await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.id);
	}
);

test('Reset collection filter using applied filters', async ({
	apiHelpers,
	collectionsPage,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create a definition for a Collection Filter

	const collectionFilterId = getRandomString();

	const collectionFilterDefinition = getFragmentDefinition({
		id: collectionFilterId,
		key: 'com.liferay.fragment.renderer.collection.filter.internal.CollectionFilterFragmentRenderer',
	});

	// Create a definition for a Collection Applied Filter

	const collectionAppliedFilterId = getRandomString();

	const collectionAppliedFilterDefinition = getFragmentDefinition({
		id: collectionAppliedFilterId,
		key: 'com.liferay.fragment.renderer.collection.filter.internal.CollectionAppliedFiltersFragmentRenderer',
	});

	// Create definition for a collection mapped to Animals collection

	const animalsClassPK = await collectionsPage.getCollectionClassPK(
		ANIMALS_COLLECTION_NAME,
		pageManagementSite.friendlyUrlPath
	);

	const collectionDefinition = getCollectionDefinition({
		classPK: animalsClassPK,
		id: getRandomString(),
		pageElements: [
			getFragmentDefinition({
				fragmentFields: FRAGMENT_FIELDS,
				id: getRandomString(),
				key: 'BASIC_COMPONENT-heading',
			}),
		],
	});

	// Create a content page and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			collectionAppliedFilterDefinition,
			collectionFilterDefinition,
			collectionDefinition,
		]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Go to edit mode of the created page and select the Collection Filter fragment

	await pageEditorPage.selectFragment(collectionFilterId);

	// Set Filter configuration for categories

	await configureFilter(page, 'category');

	// Check the option to show the label with the selected vocabulary

	await page.getByLabel('Show Label').check();

	// Go to edit mode of the created page and select the Collection Applied Filter fragment

	await expect(page.getByText('No Active Filters')).toBeVisible();

	await pageEditorPage.selectFragment(collectionAppliedFilterId);

	await expect(
		page.getByText(
			'You will see this fragment on the page only after applying a filter.'
		)
	).toBeVisible();

	await page.getByLabel('Select', {exact: true}).click();

	await page.getByLabel(ANIMALS_COLLECTION_NAME).check();

	await page
		.getByText(
			'You will see this fragment on the page only after applying a filter.'
		)
		.click();

	// Check Include Clear Filters Option

	await page
		.getByLabel('Include Clear Filters Option', {exact: true})
		.check();

	await pageEditorPage.publishPage();

	// Go to view mode of the created page

	await page.goto(
		`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
	);

	// Both should be visible initially

	await expect(
		page.getByText('Animal 01 - Dogs and Cats categories')
	).toBeVisible();
	await expect(page.getByText('Animal 02 - Dogs category')).toBeVisible();

	await expect(
		page.getByText(ANIMALS_COLLECTION_NAME, {exact: true})
	).toBeVisible();

	await expect(page.locator('div[id*="filterList"]')).not.toBeVisible();

	// Select category filter: Cats

	await selectFilter(page, ['cats']);

	await expect(
		page.getByText('Animal 01 - Dogs and Cats categories')
	).toBeVisible();

	await expect(page.getByText('Animal 02 - Dogs category')).not.toBeVisible();

	await expect(page.locator('div[id*="filterList"]')).toContainText('Cats');

	// Remove filter

	await expect(async () => {
		await page.getByLabel('Remove Filter', {exact: true}).click();

		await expect(
			page.getByText('Animal 01 - Dogs and Cats categories')
		).toBeVisible();

		await expect(page.getByText('Animal 02 - Dogs category')).toBeVisible();
	}).toPass();

	// Select category filter: Cats

	await selectFilter(page, ['cats']);

	await expect(
		page.getByText('Animal 01 - Dogs and Cats categories')
	).toBeVisible();

	await expect(page.getByText('Animal 02 - Dogs category')).not.toBeVisible();

	await expect(page.locator('div[id*="filterList"]')).toContainText('Cats');

	// Clear filter

	await expect(async () => {
		await page.getByRole('button', {name: 'Clear Filters'}).click();

		await expect(
			page.getByText('Animal 01 - Dogs and Cats categories')
		).toBeVisible();

		await expect(page.getByText('Animal 02 - Dogs category')).toBeVisible();
	}).toPass();

	// Clean up

	await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.id);
});
