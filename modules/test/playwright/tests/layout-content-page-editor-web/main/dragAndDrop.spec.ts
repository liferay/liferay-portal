/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {masterPagesPagesTest} from '../../../fixtures/masterPagesPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import dragAndDropElement from '../../../utils/dragAndDropElement';
import getRandomString from '../../../utils/getRandomString';
import {ANIMALS_COLLECTION_NAME} from '../../setup/page-management-site/main/constants/animals';
import {getObjectERC} from '../../setup/page-management-site/main/utils/getObjectERC';
import getCollectionDefinition from './utils/getCollectionDefinition';
import getContainerDefinition from './utils/getContainerDefinition';
import getFormContainerDefinition from './utils/getFormContainerDefinition';
import getFragmentDefinition from './utils/getFragmentDefinition';
import getGridDefinition from './utils/getGridDefinition';
import getPageDefinition from './utils/getPageDefinition';
import getWidgetDefinition from './utils/getWidgetDefinition';

const test = mergeTests(
	apiHelpersTest,
	collectionsPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	masterPagesPagesTest,
	pageEditorPagesTest,
	pageManagementSiteTest
);

test('Checks that a widget can be added and dragged to another part of the page', async ({
	apiHelpers,
	page,
	pageEditorPage,
	site,
}) => {

	// Create a content page with a grid

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getGridDefinition({
				columns: [{pageElements: [], size: 4}, {size: 4}, {size: 4}],
				id: getRandomString(),
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	// Go to edit mode, add the Sort widget in the first column of the grid and publish the page

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	const gridColumn = page.locator('.page-editor__col__border');

	await pageEditorPage.addWidget('Commerce', 'Sort', gridColumn.first());

	// Check that the Sort widget is selected

	const widgetId = await pageEditorPage.getFragmentId('Sort');

	expect(await pageEditorPage.isActive(widgetId)).toBe(true);

	await pageEditorPage.publishPage();

	// Edit the page and move the widget to the third column of the grid

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await dragAndDropElement({
		dragTarget: page.locator('[data-name="Sort"]'),
		dropTarget: gridColumn.nth(2),
		page,
	});

	expect(gridColumn.nth(2).locator('[data-name="Sort"]')).toBeVisible();
});

test('Checks that the drag target and drop target have the correct classes when dragged', async ({
	apiHelpers,
	page,
	pageEditorPage,
	site,
}) => {
	const containerDefinition = getContainerDefinition({
		id: getRandomString(),
	});

	const headingDefinition = getFragmentDefinition({
		id: getRandomString(),
		key: 'BASIC_COMPONENT-heading',
	});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			containerDefinition,
			headingDefinition,
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	// Go to edit mode and drag the heading fragment into the container fragment

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	const dragTarget = page.locator('[data-name="Heading"]');

	const dropTarget = page.locator('.page-editor__container');

	// Drag and drop starts

	await dragTarget.hover();

	await page.mouse.down();

	await dropTarget.hover();

	await expect(dragTarget).toHaveClass(/dragged/);

	const boundingClientRect = await dropTarget.evaluate((element) =>
		element.getBoundingClientRect()
	);

	await dropTarget.hover({
		position: {
			x: boundingClientRect.width / 2,
			y: boundingClientRect.height / 2,
		},
	});

	const dropTargetTopper = page.locator('[data-name]', {
		has: dropTarget,
	});

	await expect(dropTargetTopper).toHaveClass(/highlighted/);

	await page.mouse.up();

	// Check if the drag and drop is done

	expect(dropTarget.locator('[data-name="Heading"]')).toBeVisible();
});

test(
	'Check that multiple items can be dragged at once',
	{
		tag: '@LPD-30901',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {
		const headingId = getRandomString();
		const headingDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const buttonId = getRandomString();
		const buttonDefinition = getFragmentDefinition({
			id: buttonId,
			key: 'BASIC_COMPONENT-button',
		});

		const containerDefinition = getContainerDefinition({
			id: getRandomString(),
			pageElements: [],
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				buttonDefinition,
				headingDefinition,
				containerDefinition,
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Go to edit mode of page and select multiple fragments

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.selectFragment(headingId);

		await page.keyboard.down('Control');

		await pageEditorPage.selectFragment(buttonId);

		await page.keyboard.up('Control');

		// Move multiple fragments into the container

		const dropTarget = page.locator('[data-name="Container"]');

		await dragAndDropElement({
			dragTarget: page.locator('[data-name="Button"]'),
			dropTarget,
			page,
		});

		// Check that the fragment have been moved

		expect(dropTarget.locator('[data-name="Heading"]')).toBeVisible();
		expect(dropTarget.locator('[data-name="Button"]')).toBeVisible();
	}
);

test(
	'Check drag and drop from Page Structure Tree',
	{tag: ['@LPS-118271', '@LPS-106776']},
	async ({
		apiHelpers,
		collectionsPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Add a content page with a fragment, a collection display and a widget

		const heading = getFragmentDefinition({
			id: getRandomString(),
			key: 'BASIC_COMPONENT-heading',
		});

		const animalsClassPK = await collectionsPage.getCollectionClassPK(
			ANIMALS_COLLECTION_NAME,
			pageManagementSite.friendlyUrlPath
		);

		const collectionId = getRandomString();

		const collection = getCollectionDefinition({
			classPK: animalsClassPK,
			id: collectionId,
		});

		const assetPublisher = getWidgetDefinition({
			id: getRandomString(),
			widgetName:
				'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				heading,
				collection,
				assetPublisher,
			]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Go to edit mode and to Browser panel

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.goToSidebarTab('Browser');

		// Reorder fragments and check it works

		await pageEditorPage.dragTreeNode({
			position: 'top',
			source: {label: 'Asset Publisher'},
			target: {label: 'Heading'},
		});

		await expect(
			page.locator('.treeview > .treeview-item').nth(0)
		).toContainText('Asset Publisher');

		await pageEditorPage.dragTreeNode({
			position: 'bottom',
			source: {label: 'Asset Publisher'},
			target: {label: 'Heading'},
		});

		await expect(
			page.locator('.treeview > .treeview-item').nth(1)
		).toContainText('Asset Publisher');

		// Drag the asset publisher inside the collection item

		await pageEditorPage.selectFragment(collectionId);

		await page
			.locator('.page-editor__page-structure__tree-node')
			.filter({hasText: 'Collection Item'})
			.waitFor();

		await pageEditorPage.dragTreeNode({
			source: {label: 'Asset Publisher'},
			target: {label: 'Collection Item'},
		});

		expect(
			page
				.locator('[data-name="Collection Display"]')
				.locator('[data-name="Asset Publisher"]')
				.first()
		).toBeVisible();
	}
);

test(
	'Drag multiple form elements from one step to another',
	{tag: '@LPD-37828'},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Get the id of Potato object from the site initializer

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {className: objectDefinitionClassName} = (
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				getObjectERC('Potato')
			)
		).body;

		// Create a form with two steps and a stepper

		const stepperId = getRandomString();

		const stepperFragment = getFragmentDefinition({
			fragmentConfig: {
				numberOfSteps: 2,
			},
			id: stepperId,
			key: 'INPUTS-stepper',
		});

		const firstInputId = getRandomString();

		const firstInputDefinition = getFragmentDefinition({
			id: firstInputId,
			key: 'INPUTS-text-input',
		});

		const secondInputId = getRandomString();

		const secondInputDefinition = getFragmentDefinition({
			id: secondInputId,
			key: 'INPUTS-text-input',
		});

		const formDefinition = getFormContainerDefinition({
			id: getRandomString(),
			objectDefinitionClassName,
			pageElements: [stepperFragment],
			steps: [[firstInputDefinition, secondInputDefinition], []],
		});

		// Create page and go to edit mode

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Move multiple fragments into Step 2

		await pageEditorPage.goToSidebarTab('Browser');

		await pageEditorPage.selectFragment(firstInputId);

		await page.keyboard.down('Control');

		await pageEditorPage.selectFragment(secondInputId);

		await page.keyboard.up('Control');

		await pageEditorPage.dragTreeNode({
			source: {label: 'Text'},
			target: {label: 'Step 2'},
		});

		await pageEditorPage.waitForChangesSaved();

		// Check inputs have been moved to second step

		const firstStep = page.locator('.page-editor__form-step').nth(0);
		const secondStep = page.locator('.page-editor__form-step').nth(1);

		await expect(firstStep.locator('[data-name="Text"]')).toHaveCount(0);
		await expect(secondStep.locator('[data-name="Text"]')).toHaveCount(2);
	}
);

test(
	'Check correct item is selected when dragging into a collection item from tree',
	{tag: ['@LPD-41382']},
	async ({
		apiHelpers,
		collectionsPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Add a content page with a fragment and a collection display

		const heading = getFragmentDefinition({
			id: getRandomString(),
			key: 'BASIC_COMPONENT-heading',
		});

		const animalsClassPK = await collectionsPage.getCollectionClassPK(
			ANIMALS_COLLECTION_NAME,
			pageManagementSite.friendlyUrlPath
		);

		const collectionId = getRandomString();

		const collection = getCollectionDefinition({
			classPK: animalsClassPK,
			id: collectionId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([heading, collection]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Go to edit mode and to Browser panel

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.goToSidebarTab('Browser');

		// Expand Collection

		await clickAndExpectToBeVisible({
			target: page.locator('.page-editor__page-structure__tree-node', {
				hasText: 'Collection Item',
			}),
			trigger: page.locator('.page-editor__page-structure__tree-node', {
				hasText: 'Collection',
			}),
		});

		// Add fragment to collection item

		await pageEditorPage.dragTreeNode({
			position: 'middle',
			source: {label: 'Heading'},
			target: {label: 'Collection Item'},
		});

		await expect(
			page.locator('.page-editor__page-structure__tree-node').nth(2)
		).toContainText('Heading');

		// Check fragment is selected

		await expect(
			page.locator('.page-editor__topper__title', {hasText: 'Heading'})
		).toBeVisible();

		// Drag it outside again and check it's selected

		await pageEditorPage.dragTreeNode({
			position: 'top',
			source: {label: 'Heading'},
			target: {label: 'Collection Display'},
		});

		await expect(
			page.locator('.page-editor__page-structure__tree-node').nth(0)
		).toContainText('Heading');

		await expect(
			page.locator('.page-editor__topper__title', {hasText: 'Heading'})
		).toBeVisible();
	}
);

test(
	'Check correct item is selected when dragging into a collection item from layout',
	{tag: ['@LPD-41382']},
	async ({
		apiHelpers,
		collectionsPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Add a content page with a fragment and a collection display

		const headingId = getRandomString();

		const headingDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const animalsClassPK = await collectionsPage.getCollectionClassPK(
			ANIMALS_COLLECTION_NAME,
			pageManagementSite.friendlyUrlPath
		);

		const collectionId = getRandomString();

		const collection = getCollectionDefinition({
			classPK: animalsClassPK,
			id: collectionId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([headingDefinition, collection]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Go to edit mode

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		const heading = pageEditorPage.getFragment(headingId);

		await heading.waitFor();

		// Drag heading into collection item

		await expect(async () => {
			await heading.dragTo(
				page.locator('.page-editor__collection-item').nth(0)
			);

			const topper = page
				.locator(`.lfr-layout-structure-item-topper-${headingId}`)
				.nth(0);

			await expect(topper).toHaveClass(/active/, {timeout: 2000});
		}).toPass();
	}
);

test(
	'Check correct feedback is shown when dragging over an unmapped collection',
	{tag: ['@LPD-44466']},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Add a content page with a collection display and go to edit mode

		const collectionId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getCollectionDefinition({
					id: collectionId,
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await page.getByText('Select a Page Element', {exact: true}).waitFor();

		// Go to Components sidebar panel

		await pageEditorPage.goToSidebarTab('Components');

		// Check we can drag a fragment from the list on top of the Collection

		const heading = page.locator(
			'.page-editor__fragments-widgets__tab-list-item',
			{
				hasText: 'Heading',
			}
		);

		await pageEditorPage.dragToFragment({
			position: 'top',
			source: heading,
			targetId: collectionId,
		});

		// Check we can drag a fragment from the list on bottom of the Collection

		await pageEditorPage.dragToFragment({
			position: 'bottom',
			source: heading,
			targetId: collectionId,
		});

		// Check we can not drag a fragment from the list on middle of the Collection

		await expect(async () => {
			await pageEditorPage.dragToFragment({
				position: 'middle',
				source: heading,
				targetId: collectionId,
			});
		}).rejects.toThrow();
	}
);

test(
	'Grid topper title is shown when dragging into a column',
	{tag: '@LPD-45979'},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a content page with a grid and go to edit mode

		const firstColumnId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getGridDefinition({
					columns: [
						{id: firstColumnId, size: 4},
						{size: 4},
						{size: 4},
					],
					id: getRandomString(),
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Drag a button into a column and check grid is highlighted

		await pageEditorPage.goToSidebarTab('Components');

		// Check we can drag a fragment from the list on top of the Collection

		const heading = page.locator(
			'.page-editor__fragments-widgets__tab-list-item',
			{
				hasText: 'Heading',
			}
		);

		await pageEditorPage.dragToFragment({
			drop: false,
			position: 'middle',
			source: heading,
			targetId: firstColumnId,
		});

		await expect(
			page.locator('.page-editor__topper__title', {hasText: 'Grid'})
		).toBeVisible();
	}
);

test(
	'Show error alert when dropping fragments inside master page fragments dropzone',
	{tag: '@LPD-47053'},
	async ({apiHelpers, masterPagesPage, page, pageEditorPage, site}) => {

		// Create a master page

		const layoutPageTemplateEntryName = getRandomString();

		const masterPage =
			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
				{
					groupId: site.id,
					name: layoutPageTemplateEntryName,
					type: 'master-layout',
				}
			);

		await masterPagesPage.goto(site.friendlyUrlPath);

		// Add and image fragment and publish

		await masterPagesPage.editMaster(layoutPageTemplateEntryName);

		await pageEditorPage.addFragment('Basic Components', 'Image');

		await pageEditorPage.publishPage();

		// Create a new content page based on master page

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			masterLayoutPlid: masterPage.plid,
			options: {type: 'content'},
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Drag and drop a fragment inside master page fragments area, should throw an error

		await dragAndDropElement({
			dragTarget: page.getByRole('menuitem', {
				name: 'Add Button',
			}),
			dropTarget: page.locator('.page-editor__fragment-content--master'),
			force: true,
			page,
		});

		await expect(page.locator('.alert-danger')).toHaveText(
			'Error:Fragments and widgets cannot be placed inside this area.'
		);
	}
);

test(
	'Elevation in fragment drop zones works well',
	{
		tag: '@LPD-56606',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create page with a Tabs fragment and go to edit mode

		const tabsDefinition = getFragmentDefinition({
			fragmentConfig: {
				numberOfTabs: 2,
			},
			fragmentFields: [
				{
					id: 'title1',
					value: {},
				},
				{
					id: 'title2',
					value: {},
				},
			],
			id: getRandomString(),
			key: 'BASIC_COMPONENT-tabs',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([tabsDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Add a heading inside the tab

		await pageEditorPage.addFragment(
			'Basic Components',
			'Heading',
			page
				.getByLabel('Tab 1', {exact: true})
				.getByText('Drag and drop fragments or widgets here')
		);

		// Try to add another fragment on top of the Heading

		const paragraph = page.getByRole('menuitem', {name: 'Paragraph'});

		const heading = page.locator(
			'.lfr-layout-structure-item-basic-component-heading'
		);

		const headingRect = await heading.evaluate((element) =>
			element.getBoundingClientRect()
		);

		const headingTopper = page.locator(
			'.page-editor__topper[data-name="Heading"]'
		);

		const tabsTopper = page.locator(
			'.page-editor__topper[data-name="Tabs"]'
		);

		await paragraph.hover();

		await page.mouse.down();

		await page.mouse.move(
			headingRect.left + headingRect.width / 2,
			headingRect.top + headingRect.height / 2
		);

		let y = headingRect.top + headingRect.height / 2;

		// Check the drop-container class appears, what means the fragment
		// is going to be added inside the Tab properly

		await expect(async () => {
			await page.mouse.move(headingRect.left + headingRect.width / 2, y);

			y = y - 1;

			await expect(page.locator('.drop-container')).toBeVisible({
				timeout: 500,
			});

			await expect(headingTopper).toHaveClass(/drag-over-top/, {
				timeout: 500,
			});
		}).toPass();

		// Now continue going up until targeting Tabs fragment and check
		// the fragment drop zone is not targeted during the process

		let fragmentDropzoneIsTargeted = false;

		await expect(async () => {
			await page.mouse.move(headingRect.left + headingRect.width / 2, y);

			y = y - 5;

			if (
				await page
					.locator('.page-editor__root.drag-over-top')
					.isVisible()
			) {
				fragmentDropzoneIsTargeted = true;
			}

			await expect(tabsTopper).toHaveClass(/drag-over-top/, {
				timeout: 500,
			});
		}).toPass();

		if (fragmentDropzoneIsTargeted) {
			throw new Error('Fragment drop zone was targeted');
		}
	}
);
