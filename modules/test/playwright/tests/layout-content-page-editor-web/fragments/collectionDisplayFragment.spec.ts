/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getContentSetElementContent} from '../../../utils/getContentSetElementContent';
import getRandomString from '../../../utils/getRandomString';
import {ANIMALS_COLLECTION_NAME} from '../../setup/page-management-site/main/constants/animals';
import getCollectionDefinition from '../main/utils/getCollectionDefinition';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	collectionsPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	pageEditorPagesTest,
	pageViewModePagesTest,
	pageManagementSiteTest
);

const testWithIsolatedSite = mergeTests(test, isolatedSiteTest);

test(
	'Allows adding a Collection Display with a manual collection into another Collection Display with Recent Content',
	{
		tag: '@LPS-127024',
	},
	async ({
		apiHelpers,
		collectionsPage,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create definition for a collection mapped to
		// Recent Content provider with Bordered List style

		const firstCollectionId = getRandomString();

		const firstCollectionDefinition = getCollectionDefinition({
			id: firstCollectionId,
			listStyle: 'Bordered List (Collection Provider)',
			provider: 'Recent Content',
		});

		// Create definition for a collection mapped to Animals collection

		const animalsClassPK = await collectionsPage.getCollectionClassPK(
			ANIMALS_COLLECTION_NAME,
			pageManagementSite.friendlyUrlPath
		);

		const {items: collectionElements} =
			await apiHelpers.headlessDelivery.getContentSetElements(
				animalsClassPK
			);

		const animalsCollection = getCollectionDefinition({
			classPK: animalsClassPK,
			id: getRandomString(),
			listStyle: 'Bulleted List (Journal)',
		});

		// Create definition for another collection mapped to Recent Content provider

		const secondCollectionId = getRandomString();

		const secondCollectionDefinition = getCollectionDefinition({
			id: secondCollectionId,
			pageElements: [animalsCollection],
			provider: 'Recent Content',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				firstCollectionDefinition,
				secondCollectionDefinition,
			]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Go to edit mode of page

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Calculate the number of recent contents

		const firstCollection = pageEditorPage.getFragment(firstCollectionId);

		const count = await firstCollection.locator('.list-group-item').count();

		// Expect second collection to display only Animals element contents that times

		const secondCollection = pageEditorPage.getFragment(secondCollectionId);

		await expect(secondCollection.locator('li')).toHaveCount(
			count * collectionElements.length
		);

		for (const element of collectionElements) {
			await expect(secondCollection.getByText(element.title)).toHaveCount(
				count
			);
		}

		await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.id);
	}
);

test(
	'Can prefilter collection',
	{
		tag: '@LPS-166039',
	},
	async ({
		apiHelpers,
		collectionsPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create definition for a collection mapped to Animals collection

		const animalsClassPK = await collectionsPage.getCollectionClassPK(
			ANIMALS_COLLECTION_NAME,
			pageManagementSite.friendlyUrlPath
		);

		const collectionId = getRandomString();

		const relatedCollectionId = getRandomString();

		const collectionDefinition = getCollectionDefinition({
			classPK: animalsClassPK,
			id: collectionId,
			pageElements: [
				getCollectionDefinition({
					id: relatedCollectionId,
					provider: 'Items with Categories in the Same Vocabularies',
				}),
			],
		});

		// Create a content page and go to edit mode

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([collectionDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Pre-filter collection

		await pageEditorPage.selectFragment(relatedCollectionId);

		await page.locator('.page-editor__disabled-area').first().click();

		await pageEditorPage.selectFragment(relatedCollectionId);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Filter Collection'}),
			trigger: page.getByTitle('View Collection Options'),
		});

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.locator('.dropdown-menu', {hasText: 'Blogs Entry'}),
			trigger: page.getByLabel('Item Type', {exact: true}),
		});

		await page.getByLabel('Blogs Entry').check();

		await page.locator('body').click();

		// Assert empty message in filter collection

		await expect(
			page.getByText('There are 0 results for Blogs Entry.')
		).toBeVisible();

		await page.getByRole('button', {name: 'Save'}).click();

		// Assert empty message in edit mode

		await expect(
			page
				.getByText(
					'The collection is empty. To display your items, add them to the collection or choose a different collection.'
				)
				.first()
		).toBeVisible();
	}
);

testWithIsolatedSite(
	'Check collection display pagination',
	{
		tag: '@LPS-146171',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Add 25 blogs

		for (let i = 0; i < 25; i++) {
			await apiHelpers.headlessDelivery.postBlog(site.id);
		}

		// Create a content page

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition(),
			siteId: site.id,
			title: getRandomString(),
		});

		// Go to edit mode and add collection display with heading fragment

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.addFragment(
			'Content Display',
			'Collection Display'
		);

		await pageEditorPage.selectFragment(
			await pageEditorPage.getFragmentId('Collection Display')
		);

		await pageEditorPage.chooseCollectionDisplayCollection(
			'Collection Providers',
			'Highest Rated Assets'
		);

		await pageEditorPage.waitForChangesSaved();

		await pageEditorPage.addFragment(
			'Basic Components',
			'Heading',
			page.locator('.page-editor__collection-item.empty').first()
		);

		// Assert pagination is visible by default

		await expect(page.locator('.pagination-bar')).toBeVisible();

		// Assert numeric is the default option

		const collectionId =
			await pageEditorPage.getFragmentId('Collection Display');

		await pageEditorPage.selectFragment(collectionId);

		const collectionStyle = page.getByLabel('CollectionStyle');

		await expect(collectionStyle.getByLabel('Pagination')).toHaveValue(
			'numeric'
		);

		// Assert display all pages is checked by default and display all collection items is not visible

		await expect(
			collectionStyle.getByLabel('Display All Pages')
		).toBeChecked();

		await expect(
			collectionStyle.getByLabel('Display All Collection Items')
		).not.toBeVisible();

		// Assert default value for maximum number of items per page

		await expect(
			collectionStyle.getByLabel('Maximum Number of Items per Page')
		).toHaveValue('20');

		// Assert performance message

		await pageEditorPage.changeConfiguration({
			fieldLabel: 'Maximum Number of Items per Page',
			tab: 'General',
			value: '21',
		});

		await expect(
			page.getByText(
				'In edit mode, the number of elements displayed is limited to 20 due to performance.'
			)
		).toBeVisible();

		// Assert maximum number of pages to display

		await pageEditorPage.changeConfiguration({
			fieldLabel: 'Maximum Number of Items per Page',
			tab: 'General',
			value: '3',
		});

		await expect(page.getByLabel('Go to page, 1')).toBeVisible();

		await expect(page.getByLabel('Go to page, 2')).toBeVisible();

		await expect(page.getByLabel('Go to page, 3')).toBeVisible();

		await expect(page.getByLabel('Go to page, 4')).not.toBeVisible();

		await expect(page.getByLabel('Go to page, 5')).not.toBeVisible();

		await expect(page.getByLabel('Go to page, 9')).toBeVisible();

		// Go to view mode and check the pagination keyboard navigation

		await pageEditorPage.publishPage();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await page.getByLabel('Go to page, 2').focus();

		await page.keyboard.press('Tab');

		await expect(page.getByLabel('Go to page, 3')).toBeFocused();

		await page.keyboard.press('Enter');

		await page.waitForURL(({href}) =>
			href.includes(`page_number_${collectionId}=3`)
		);

		// Go back to edit mode and continue modifying the configuration

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.selectFragment(
			await pageEditorPage.getFragmentId('Collection Display')
		);

		await pageEditorPage.changeConfiguration({
			fieldLabel: 'Display All Pages',
			tab: 'General',
			value: 'false',
		});

		await expect(
			collectionStyle.getByLabel('Maximum Number of Pages to Display')
		).toHaveValue('5');

		await expect(page.getByLabel('Go to page, 1')).toBeVisible();

		await expect(page.getByLabel('Go to page, 2')).toBeVisible();

		await expect(page.getByLabel('Go to page, 3')).toBeVisible();

		await expect(page.getByLabel('Go to page, 4')).toBeVisible();

		await expect(page.getByLabel('Go to page, 5')).toBeVisible();

		await pageEditorPage.changeConfiguration({
			fieldLabel: 'Maximum Number of Pages to Display',
			tab: 'General',
			value: '2',
		});

		await expect(page.getByLabel('Go to page, 1')).toBeVisible();

		await expect(page.getByLabel('Go to page, 2')).toBeVisible();

		await expect(page.getByLabel('Go to page, 3')).not.toBeVisible();

		await expect(page.getByLabel('Go to page, 4')).not.toBeVisible();

		await expect(page.getByLabel('Go to page, 5')).not.toBeVisible();

		// Assert minimun value of maximum number of pagest to display

		await pageEditorPage.changeConfiguration({
			fieldLabel: 'Maximum Number of Pages to Display',
			tab: 'General',
			value: '-1',
		});

		await expect(
			collectionStyle.getByLabel('Maximum Number of Pages to Display')
		).toHaveValue('1');

		// Change pagination configuration to none

		await pageEditorPage.changeConfiguration({
			fieldLabel: 'Pagination',
			tab: 'General',
			value: 'None',
		});

		// Assert pagination is not visible

		await expect(page.locator('.pagination-bar')).not.toBeVisible();

		// Assert default value for maximun number of items to display

		await expect(
			collectionStyle.getByLabel('Maximum Number of Items to Display')
		).toHaveValue('5');

		await pageEditorPage.changeConfiguration({
			fieldLabel: 'Maximum Number of Items to Display',
			tab: 'General',
			value: '50',
		});

		await expect(
			collectionStyle.getByText('This collection has 25 items.')
		).toBeVisible();

		// Assert display all pages is not visible and display all collection items by default is disabled

		await expect(
			collectionStyle.getByLabel('Display All Pages')
		).not.toBeVisible();

		await expect(
			collectionStyle.getByLabel('Display All Collection Items')
		).not.toBeChecked();
	}
);

testWithIsolatedSite(
	'Checks the error message when trying to drag a fragment to an unmapped collection',
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a content page with an empty collection display

		const collectionDefinition = getCollectionDefinition({
			id: getRandomString(),
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([collectionDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Try to drag fragment to collection and check error alert

		await page
			.getByRole('menuitem', {
				name: 'Add Button',
			})
			.dragTo(page.getByText('Select a collection to display.'));

		await expect(page.locator('.alert-danger')).toHaveText(
			'Error:Fragments cannot be placed inside an unmapped collection display fragment.'
		);
	}
);

test('Checks Content Flags, Content Ratings and Content Display are compatible with Collection Display', async ({
	apiHelpers,
	collectionsPage,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create definition for a collection mapped to Animals collection with Content Flags, Content Ratings and Display Content fragments.

	const animalsClassPK = await collectionsPage.getCollectionClassPK(
		ANIMALS_COLLECTION_NAME,
		pageManagementSite.friendlyUrlPath
	);

	const {items: collectionElements} =
		await apiHelpers.headlessDelivery.getContentSetElements(animalsClassPK);

	const collectionDefinition = getCollectionDefinition({
		classPK: animalsClassPK,
		id: getRandomString(),
		pageElements: [
			getFragmentDefinition({
				id: getRandomString(),
				key: 'com.liferay.fragment.internal.renderer.ContentFlagsFragmentRenderer',
			}),
			getFragmentDefinition({
				id: getRandomString(),
				key: 'com.liferay.fragment.internal.renderer.ContentRatingsFragmentRenderer',
			}),
			getFragmentDefinition({
				fragmentConfig: {
					itemSelector: {
						template: {
							infoItemRendererKey:
								'com.liferay.journal.web.internal.info.item.renderer.JournalArticleFullContentInfoItemRenderer',
						},
					},
				},
				id: getRandomString(),
				key: 'com.liferay.fragment.internal.renderer.ContentObjectFragmentRenderer',
			}),
		],
	});

	// Create a content page and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([collectionDefinition]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	// Go to edit mode of the created page

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Check that the Content Display shows the content in each item

	await expect(
		page.locator('.page-editor').getByText('Content', {exact: true})
	).toHaveCount(collectionElements.length);

	for (const element of collectionElements) {
		const content = getContentSetElementContent(element);

		await expect(page.getByText(content)).toBeVisible();
	}

	// Check that the Content Display shows Default Template by default

	const content = getContentSetElementContent(collectionElements[0]);

	await page.getByText(content).click();

	await expect(page.getByLabel('Template', {exact: true})).toHaveValue(
		'Default Template'
	);

	// Close sidebar

	await clickAndExpectToBeHidden({
		target: page.locator('header', {hasText: 'Components'}),
		trigger: page.getByRole('tab', {
			exact: true,
			name: 'Components',
		}),
	});

	// Check that the Content Ratings and Content Flags are shown for every item

	await expect(page.getByLabel('Vote', {exact: true})).toHaveCount(
		collectionElements.length
	);
	await expect(page.locator('button', {hasText: 'Report'})).toHaveCount(
		collectionElements.length
	);
});

test('Modifies inline text on all collection items', async ({
	apiHelpers,
	collectionsPage,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create definition for a collection mapped to Animals collection

	const animalsClassPK = await collectionsPage.getCollectionClassPK(
		ANIMALS_COLLECTION_NAME,
		pageManagementSite.friendlyUrlPath
	);

	const {totalCount: collectionElementsCount} =
		await apiHelpers.headlessDelivery.getContentSetElements(animalsClassPK);

	const headingId = getRandomString();

	const collectionDefinition = getCollectionDefinition({
		classPK: animalsClassPK,
		id: getRandomString(),
		pageElements: [
			getFragmentDefinition({
				id: headingId,
				key: 'BASIC_COMPONENT-heading',
			}),
		],
	});

	// Create a content page and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([collectionDefinition]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Fill new content

	await pageEditorPage.editTextEditable(
		headingId,
		'element-text',
		'New Content'
	);

	// Check that the inline text changes in all items of the collection

	await expect(
		page.locator('.page-editor').getByText('New Content')
	).toHaveCount(collectionElementsCount);
});

test(
	'Checks the different styles for the Display Collection',
	{
		tag: '@LPS-114727',
	},
	async ({
		apiHelpers,
		collectionsPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {
		const checkStyleDisplay = async () => {
			const listItem = page.locator(
				'.lfr-layout-structure-item-collection ul'
			);

			// Check the Bordered List style

			await expect(listItem.first()).toHaveClass('list-group');
			await expect(listItem.first().locator('li').first()).toHaveClass(
				'list-group-item'
			);

			// Check the Bulleted List style

			await expect(listItem.nth(1)).toHaveAttribute('class', '');
			await expect(listItem.nth(1).locator('li').first()).toHaveAttribute(
				'class',
				''
			);

			// Check the Inline List style

			await expect(listItem.nth(2)).toHaveClass('d-flex list-inline');
			await expect(listItem.nth(2).locator('li').first()).toHaveClass(
				'flex-grow-1'
			);

			// Check the Numbered List style

			const orderedListItem = page.locator(
				'.lfr-layout-structure-item-collection ol'
			);

			await expect(orderedListItem).not.toHaveAttribute('class');
			await expect(
				orderedListItem.locator('li').first()
			).not.toHaveAttribute('class');

			// Check the Unstyled List style

			await expect(listItem.nth(3)).toHaveClass('list-unstyled');
			await expect(listItem.nth(3).locator('li').first()).toHaveAttribute(
				'class',
				''
			);
		};

		// Create several definitions with different Style Display

		const animalsClassPK = await collectionsPage.getCollectionClassPK(
			ANIMALS_COLLECTION_NAME,
			pageManagementSite.friendlyUrlPath
		);

		const borderedListCollection = getCollectionDefinition({
			classPK: animalsClassPK,
			id: getRandomString(),
			listStyle: 'Bordered List (Journal)',
			pageElements: [
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			],
		});

		const bulletedListCollection = getCollectionDefinition({
			classPK: animalsClassPK,
			id: getRandomString(),
			listStyle: 'Bulleted List (Journal)',
			pageElements: [
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			],
		});

		const inlineListCollection = getCollectionDefinition({
			classPK: animalsClassPK,
			id: getRandomString(),
			listStyle: 'Inline List',
			pageElements: [
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			],
		});

		const numberedListCollection = getCollectionDefinition({
			classPK: animalsClassPK,
			id: getRandomString(),
			listStyle: 'Numbered List',
			pageElements: [
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			],
		});

		const unstyledListCollection = getCollectionDefinition({
			classPK: animalsClassPK,
			id: getRandomString(),
			listStyle: 'Unstyled List',
			pageElements: [
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			],
		});

		// Create a content page and go to edit mode

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				borderedListCollection,
				bulletedListCollection,
				inlineListCollection,
				numberedListCollection,
				unstyledListCollection,
			]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Check the Style Display in edit mode

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await checkStyleDisplay();

		// Check the Style Display in view mode

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await checkStyleDisplay();
	}
);

test('Checks that fragment ids used within a display collection are not repeated even if they are nested elements', async ({
	apiHelpers,
	collectionsPage,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {
	const checkNonRepeatedFragmentIds = async () => {

		// Wait for the fragments to load

		await page.getByText('Heading Example').first().waitFor();

		// Get all fragments with Heading Example text

		const fragments = await page
			.getByText('Heading Example', {exact: true})
			.locator('..')
			.all();

		// Check that the fragment ids are not repeated

		const fragmentIds = [];

		for (const fragment of fragments) {
			fragmentIds.push(fragment.getAttribute('id'));
		}

		expect(Array.from(new Set(fragmentIds))).toHaveLength(fragments.length);
	};

	const animalsClassPK = await collectionsPage.getCollectionClassPK(
		ANIMALS_COLLECTION_NAME,
		pageManagementSite.friendlyUrlPath
	);

	// Create a first collection definition with headings

	const headingDefinition = getFragmentDefinition({
		id: getRandomString(),
		key: 'BASIC_COMPONENT-heading',
	});

	const collectionWithHeadings = getCollectionDefinition({
		classPK: animalsClassPK,
		id: getRandomString(),
		pageElements: [headingDefinition],
	});

	// Create a second collection definition with tabs and headings inside

	const collectionWithTabs = getCollectionDefinition({
		classPK: animalsClassPK,
		id: getRandomString(),
		pageElements: [
			getFragmentDefinition({
				fragmentConfig: {
					numberOfTabs: 1,
				},
				fragmentFields: [
					{
						id: 'title1',
						value: {
							fragmentLink: {},
						},
					},
				],
				id: getRandomString(),
				key: 'BASIC_COMPONENT-tabs',
				pageElements: [
					{
						id: getRandomString(),
						pageElements: [headingDefinition],
						type: 'FragmentDropZone',
					},
				],
			}),
		],
	});

	// Create a content page with the two collections and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			collectionWithHeadings,
			collectionWithTabs,
		]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	// Check that the fragment ids are not repeated in edit mode

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	await checkNonRepeatedFragmentIds();

	// Check that the fragment ids are not repeated in view mode

	await page.goto(
		`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
	);

	await checkNonRepeatedFragmentIds();
});

test(
	'Displays correct layout in other viewports',
	{
		tag: '@LPS-111561',
	},
	async ({
		apiHelpers,
		collectionsPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create definition for a collection mapped to Animals collection

		const animalsClassPK = await collectionsPage.getCollectionClassPK(
			ANIMALS_COLLECTION_NAME,
			pageManagementSite.friendlyUrlPath
		);

		const headingId = getRandomString();

		const collectionId = getRandomString();

		const collectionDefinition = getCollectionDefinition({
			classPK: animalsClassPK,
			id: collectionId,
			pageElements: [
				getFragmentDefinition({
					id: headingId,
					key: 'BASIC_COMPONENT-heading',
				}),
			],
		});

		// Create a content page and go to edit mode

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([collectionDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Change layout to 4 columns in Desktop

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Layout',
			fragmentId: collectionId,
			tab: 'General',
			value: '4 Columns',
		});

		await pageEditorPage.publishPage();

		// Go to view mode and check correct layout is displayed on each viewport

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		const row = page.locator('.lfr-layout-structure-item-collection .row');

		for (const col of await row.locator('.col').all()) {
			await expect(col).toHaveClass(/col-lg-3/);
			await expect(col).toHaveClass(/col-md-12/);
			await expect(col).toHaveClass(/col-sm-12/);
		}

		// Edit the page again and change layout to 2 columns in Tablet and Mobile

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.switchViewport('Tablet');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Layout',
			fragmentId: collectionId,
			isDesktop: false,
			tab: 'General',
			value: '2 Columns',
		});

		await pageEditorPage.switchViewport('Portrait Phone');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Layout',
			fragmentId: collectionId,
			isDesktop: false,
			tab: 'General',
			value: '2 Columns',
		});

		await pageEditorPage.switchViewport('Desktop');

		// Go to view mode again and check correct layout is displayed on each viewport

		await pageEditorPage.publishPage();

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(
			page.locator('.lfr-layout-structure-item-collection .row')
		).toHaveCount(1);

		for (const col of await row.locator('.col').all()) {
			await expect(col).toHaveClass(/col-lg-3/);
			await expect(col).toHaveClass(/col-md-6/);
			await expect(col).toHaveClass(/col-sm-12/);
			await expect(col).toHaveClass(/col-6/);
		}
	}
);

test('Activate the first element when a fragment is added to a Collection Display and activates the Collection Display when the fragment is deleted', async ({
	apiHelpers,
	collectionsPage,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create a page with a Collection Display and go to edit mode

	const animalsClassPK = await collectionsPage.getCollectionClassPK(
		ANIMALS_COLLECTION_NAME,
		pageManagementSite.friendlyUrlPath
	);

	const collectionWithHeadings = getCollectionDefinition({
		classPK: animalsClassPK,
		id: getRandomString(),
	});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([collectionWithHeadings]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Check that the first item is active when a Heading is added to the Collection Display

	await pageEditorPage.addFragment(
		'Basic Components',
		'Heading',
		page.locator('.page-editor__collection-item.empty').first()
	);

	const headingId = await pageEditorPage.getFragmentId('Heading');

	expect(await pageEditorPage.isActive(headingId)).toBe(true);

	// Check that the Collection Display is active when the Heading is removed

	await page.keyboard.press('Backspace');

	await expect(
		page.locator('.page-editor__topper__title', {
			hasText: 'Collection Display',
		})
	).toBeVisible();
});

test(
	'Content display title view in collection display',
	{
		tag: '@LPS-114727',
	},
	async ({
		apiHelpers,
		collectionsPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {
		const animalsClassPK = await collectionsPage.getCollectionClassPK(
			ANIMALS_COLLECTION_NAME,
			pageManagementSite.friendlyUrlPath
		);

		const {items: collectionElements} =
			await apiHelpers.headlessDelivery.getContentSetElements(
				animalsClassPK
			);

		// Create a page with a collection display and go to edit mode

		const collectionDefinition = getCollectionDefinition({
			id: getRandomString(),
			pageElements: [
				getFragmentDefinition({
					id: getRandomString(),
					key: 'com.liferay.fragment.internal.renderer.ContentObjectFragmentRenderer',
				}),
			],
			provider: 'Highest Rated Assets',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([collectionDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Assert items in edit mode

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		for (const element of collectionElements) {
			await expect(page.getByText(element.title)).toBeVisible();
		}

		// Assert items in view mode

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		for (const element of collectionElements) {
			await expect(page.getByText(element.title)).toBeVisible();
		}
	}
);

testWithIsolatedSite(
	'View collection display alert',
	{
		tag: '@LPS-160243',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page with a collection display and go to edit mode

		const collectionId = getRandomString();

		const collectionDefinition = getCollectionDefinition({
			id: collectionId,
			pageElements: [
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			],
			provider: 'Highest Rated Assets',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([collectionDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Assert alert message in edit mode

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await expect(
			page.getByText(
				'The collection is empty. To display your items, add them to the collection or choose a different collection.'
			)
		).toBeVisible();

		await pageEditorPage.switchLanguage('es-ES');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Empty Collection Alert',
			fragmentId: collectionId,
			tab: 'General',
			value: 'No se encontraron resultados',
		});

		await pageEditorPage.publishPage();

		// Assert alert message in view mode

		await page.goto(
			`/es/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(
			page.getByText('No se encontraron resultados')
		).toBeVisible();

		await page.goto(
			`/en/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(page.getByText('No Results Found')).toBeVisible();

		// Disable alert message in edit mode

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Show Empty Collection Alert',
			fragmentId: collectionId,
			tab: 'General',
			value: false,
		});

		await pageEditorPage.publishPage();

		// Assert alert message in view mode

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByText('No Results Found')).not.toBeVisible();
	}
);

testWithIsolatedSite(
	'Only first collection item is interactable',
	{
		tag: '@LPD-45724',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create definition for a collection with a heading inside it

		const headingId = getRandomString();

		const collectionId = getRandomString();

		const collectionDefinition = getCollectionDefinition({
			id: collectionId,
			pageElements: [
				getFragmentDefinition({
					id: headingId,
					key: 'BASIC_COMPONENT-heading',
				}),
			],
			provider: 'Highest Rated Assets',
		});

		// Create a content page and go to edit mode

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([collectionDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await page
			.getByText('Select a Page Element', {
				exact: true,
			})
			.waitFor();

		// Select Collection Display

		await pageEditorPage.selectFragment(collectionId);

		// Click all headings from the second one to confirm they are not selectable

		const headings = await page
			.locator(`.lfr-layout-structure-item-${headingId}`)
			.all();

		for (let i = 1; i < headings.length; i++) {
			await headings[i].click({force: true});

			await expect(
				page.locator('.page-editor__topper__title', {
					hasText: 'Heading',
				})
			).not.toBeVisible();
		}

		// Click first heading and confirm it's selectable

		await clickAndExpectToBeVisible({
			target: page.locator('.page-editor__topper__title', {
				hasText: 'Heading',
			}),
			trigger: headings[0],
		});
	}
);

testWithIsolatedSite(
	'Can select collection from the layout',
	{
		tag: '@LPD-45724',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create definition for a collection

		const collectionId = getRandomString();

		const collectionDefinition = getCollectionDefinition({
			id: collectionId,
		});

		// Create a content page and go to edit mode

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([collectionDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await page
			.getByText('Select a Page Element', {
				exact: true,
			})
			.waitFor();

		// Assert collection can be selected from layout

		await expect(
			page.getByRole('button', {name: 'Select Collection'})
		).toBeVisible();

		await clickAndExpectToBeVisible({
			target: page.locator('.modal-title', {hasText: 'Select'}),
			timeout: 1000,
			trigger: page.getByRole('button', {name: 'Select Collection'}),
		});
	}
);
