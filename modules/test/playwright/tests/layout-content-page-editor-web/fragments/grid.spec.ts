/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import getRandomString from '../../../utils/getRandomString';
import getContainerDefinition from '../main/utils/getContainerDefinition';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getGridDefinition from '../main/utils/getGridDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	isolatedSiteTest,
	pageEditorPagesTest,
	pageManagementSiteTest
);

test('Grid background image can be customized', async ({
	apiHelpers,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create a grid

	const gridId = getRandomString();

	const grid = getGridDefinition({
		columns: [{pageElements: [], size: 4}, {size: 4}, {size: 4}],
		id: gridId,
	});

	// Create page and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([grid]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Select background image

	await pageEditorPage.selectFragment(gridId);

	await pageEditorPage.goToConfigurationTab('Styles');

	await page.getByLabel('Select Image').click();

	const card = page
		.frameLocator('iframe[title="Select"]')
		.locator('[data-title="liferay_logo.png"]');

	await clickAndExpectToBeHidden({
		target: page.locator('.modal-dialog'),
		trigger: card,
	});

	await pageEditorPage.waitForChangesSaved();

	// Check correct image is used for background

	await page
		.locator('.lfr-layout-structure-item-row[style*="liferay_logo-png"]')
		.waitFor();
});

test('Grid content is also duplicated', async ({
	apiHelpers,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create a grid with a Heading in the first column

	const heading = getFragmentDefinition({
		id: getRandomString(),
		key: 'BASIC_COMPONENT-heading',
	});

	const gridId = getRandomString();

	const grid = getGridDefinition({
		columns: [{pageElements: [heading], size: 4}, {size: 4}, {size: 4}],
		id: gridId,
	});

	// Create page and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([grid]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Check there's one heading

	await expect(page.getByText('Heading Example')).toHaveCount(1);

	// Duplicate row and check there are two headings

	await pageEditorPage.duplicateFragment(gridId);

	await expect(page.getByText('Heading Example')).toHaveCount(2);
});

test('Can nest grids', async ({
	apiHelpers,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create a grid with another grid inside

	const childGrid = getGridDefinition();

	const parentGridId = getRandomString();

	const parentGrid = getGridDefinition({
		columns: [{pageElements: [childGrid], size: 4}, {size: 4}, {size: 4}],
		id: parentGridId,
	});

	// Create page and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([parentGrid]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Check nested grid is rendered properly

	const parentGridTopper = pageEditorPage.getTopper(parentGridId);

	const firstColumn = parentGridTopper.locator('.page-editor__col').first();

	await expect(firstColumn.locator('.page-editor__col')).toHaveCount(3);
});

test('Can configure grid', async ({
	apiHelpers,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create a grid

	const gridId = getRandomString();

	const grid = getGridDefinition({
		id: gridId,
	});

	// Create page and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([grid]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Change grid config and check it's applied

	await pageEditorPage.changeFragmentConfiguration({
		fieldLabel: 'Number of Modules',
		fragmentId: gridId,
		tab: 'General',
		value: '2',
	});

	await pageEditorPage.changeFragmentConfiguration({
		fieldLabel: 'Layout',
		fragmentId: gridId,
		tab: 'General',
		value: '1 Module per Row',
	});

	await expect(page.locator('.page-editor__col.col-12')).toHaveCount(2);
});

test('Can duplicate a grid inside a container', async ({
	apiHelpers,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create a container with a grid inside

	const gridId = getRandomString();

	const grid = getGridDefinition({
		id: gridId,
	});

	const containerId = getRandomString();

	const container = getContainerDefinition({
		id: containerId,
		pageElements: [grid],
	});

	// Create page and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([container]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Duplicate grid and check the copy is added properly inside the container

	await pageEditorPage.duplicateFragment(gridId);

	const containerTopper = pageEditorPage.getTopper(containerId);

	await expect(containerTopper.locator('.page-editor__row')).toHaveCount(2);
});

test('Can resize a grid', async ({
	apiHelpers,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create a container with a grid inside

	const gridId = getRandomString();

	const grid = getGridDefinition({
		id: gridId,
	});

	// Create page and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([grid]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Select grid and resize last column

	await pageEditorPage.selectFragment(gridId);

	const resizer = page.locator('.page-editor__col__resizer').last();

	const targetY = await resizer.evaluate(
		(element) => element.getBoundingClientRect().y
	);

	const targetX = await pageEditorPage
		.getTopper(gridId)
		.evaluate((element) => element.getBoundingClientRect().right);

	await resizer.hover();

	await page.mouse.down();

	await page.mouse.move(targetX, targetY);

	await page.mouse.up();

	await pageEditorPage.waitForChangesSaved();

	// Check correct size is applied

	await expect(page.locator('.page-editor__col.col-12')).toBeVisible();
});

test('Can cut and paste a grid inside a container', async ({
	apiHelpers,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create a container with a grid inside

	const headingDefinition = getFragmentDefinition({
		id: getRandomString(),
		key: 'BASIC_COMPONENT-heading',
	});

	const gridId = getRandomString();

	const gridDefinition = getGridDefinition({
		columns: [{pageElements: [headingDefinition], size: 12}],
		id: gridId,
	});

	const containerId = getRandomString();

	const container = getContainerDefinition({
		id: containerId,
		pageElements: [gridDefinition],
	});

	// Create page and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([container]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Cut grid and check that it has been pasted inside the container

	const grid = page.locator('[data-name="Grid"]');

	await expect(grid).toBeVisible();

	await pageEditorPage.cutFragment(gridId);

	await expect(grid).not.toBeVisible();

	await pageEditorPage.pasteFragment(containerId);

	await expect(
		page.locator('[data-name="Container"]').locator('.page-editor__row')
	).toBeVisible();

	// Only the parent item (Grid) is activated

	const pastedGridId = await pageEditorPage.getFragmentId('Grid');
	const pastedHeadingId = await pageEditorPage.getFragmentId('Heading');

	expect(await pageEditorPage.isActive(pastedGridId)).toBe(true);
	expect(await pageEditorPage.isActive(pastedHeadingId)).toBe(false);
});

test('Can select a grid by clicking the gutter space', async ({
	apiHelpers,
	page,
	pageEditorPage,
	site,
}) => {

	// Create a page with a grid and go to edit mode

	const gridId = getRandomString();

	const gridDefinition = getGridDefinition({
		id: gridId,
	});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([gridDefinition]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	// Click the gutter space and check the grid is selected

	await page
		.locator('.page-editor__col')
		.nth(1)
		.click({
			position: {
				x: 2,
				y: 2,
			},
		});

	await expect(
		page.locator('.page-editor__topper__title', {hasText: 'Grid'})
	).toBeVisible();
});
