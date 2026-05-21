/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageTemplatesPagesTest} from '../../../fixtures/pageTemplatesPagesTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {hoverAndExpectToBeVisible} from '../../../utils/hoverAndExpectToBeVisible';
import {waitForAlert} from '../../../utils/waitForAlert';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-76864': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pageTemplatesPagesTest,
	pagesAdminPagesTest,
	pageViewModePagesTest
);

const testWithPrivatePages = mergeTests(
	test,
	featureFlagsTest({
		'LPD-38869': {enabled: true},
		'LPS-178052': {enabled: true},
	})
);

test.describe('Keyboard movement and navigation', () => {
	test(
		'Keyboard movement works as expected',
		{tag: ['@LPD-35221']},
		async ({apiHelpers, page, pagesAdminPage, site}) => {

			// Create five pages and go to admin page

			for (const i of Array(5).keys()) {
				await apiHelpers.headlessDelivery.createSitePage({
					siteId: site.id,
					title: `Page ${i}`,
				});
			}

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await page
				.locator('.miller-columns-item', {hasText: 'Page 1'})
				.waitFor();

			// Check keyboard movement behavior
			// Move item 3 on top of item 2

			const getItem = (index: number) =>
				page.locator('.miller-columns-item', {
					hasText: `Page ${index}`,
				});

			const enableMovement = async (index: number) =>
				await page.getByLabel(`Move Page ${index}`).press('Enter');

			await expect(async () => {
				await enableMovement(3);

				await expect(
					page.locator('.drag-preview__content', {hasText: 'Page 3'})
				).toBeVisible({timeout: 1000});

				await expect(getItem(3)).toHaveClass(/drop-bottom/, {
					timeout: 1000,
				});
			}).toPass();

			await expect(async () => {
				await page.keyboard.press('ArrowUp');

				await expect(getItem(2)).toHaveClass(/drop-middle/, {
					timeout: 500,
				});
			}).toPass();

			await page.keyboard.press('ArrowUp');

			await expect(getItem(2)).toHaveClass(/drop-top/);

			await page.keyboard.press('Enter');

			await expect(
				page.locator('.miller-columns-item').nth(2)
			).toContainText('Page 3');

			// Check moved item keeps its handler focused

			await expect(getItem(3).locator('.drag-handler')).toBeFocused();

			// Move item 0 inside item 1

			await enableMovement(0);

			await expect(async () => {
				await page.keyboard.press('ArrowDown');

				await expect(getItem(1)).toHaveClass(/drop-middle/, {
					timeout: 500,
				});
			}).toPass();

			await page.keyboard.press('Enter');

			await expect(
				page
					.locator('.miller-columns-col')
					.nth(1)
					.locator('.miller-columns-item')
			).toContainText('Page 0');

			// Check source item is skipped when changing target

			await enableMovement(3);

			await expect(async () => {
				await page.keyboard.press('ArrowUp');

				await expect(getItem(1)).toHaveClass(/drop-middle/, {
					timeout: 500,
				});
			}).toPass();

			await page.keyboard.press('ArrowDown');

			await expect(getItem(2)).toHaveClass(/drop-middle/);

			// Check Escape cancels the movement

			await page.keyboard.press('Escape');

			await expect(getItem(2)).not.toHaveClass(/drop-middle/);

			// Check we can target source parent if the source item has disappeared

			await enableMovement(0);

			await page.keyboard.press('ArrowLeft');

			await page.keyboard.press('ArrowDown');

			await expect(getItem(0)).not.toBeVisible();

			await page.keyboard.press('ArrowUp');

			await page.keyboard.press('ArrowUp');

			await expect(getItem(0)).toBeVisible();

			await page.keyboard.press('Escape');

			// Check it's possible to move several items at a time

			await page.getByLabel('Select Page 1').click();
			await page.getByLabel('Select Page 3').click();

			await enableMovement(1);

			const page1Styles = await getItem(1).evaluate((element) =>
				getComputedStyle(element)
			);
			const page3Styles = await getItem(3).evaluate((element) =>
				getComputedStyle(element)
			);

			expect(page1Styles.opacity).toBe('0.4');
			expect(page3Styles.opacity).toBe('0.4');
		}
	);

	test(
		'Keyboard navigation works as expected',
		{tag: ['@LPD-35946']},
		async ({apiHelpers, page, pagesAdminPage, site}) => {

			// Create five pages and go to admin page

			for (const i of Array(5).keys()) {
				await apiHelpers.headlessDelivery.createSitePage({
					siteId: site.id,
					title: `Page ${i}`,
				});
			}

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await page
				.locator('.miller-columns-item', {hasText: 'Page 1'})
				.waitFor();

			// Focus until reach the first item

			const getItem = (index: number) =>
				page.locator('.miller-columns-item', {
					hasText: `Page ${index}`,
				});

			await expect(async () => {
				await page.keyboard.press('Tab');

				await expect(
					getItem(0).locator('.miller-columns-item-mask')
				).toBeFocused({timeout: 500});
			}).toPass();

			// Check we can go to last item with End key

			await page.keyboard.press('End');

			await expect(
				getItem(4).locator('.miller-columns-item-mask')
			).toBeFocused();

			// Check we can go to first item with Home key

			await page.keyboard.press('Home');

			await expect(
				getItem(0).locator('.miller-columns-item-mask')
			).toBeFocused();

			// Move to second and third item and check the focus moves well

			await page.keyboard.press('ArrowDown');

			await expect(
				getItem(1).locator('.miller-columns-item-mask')
			).toBeFocused();

			await page.keyboard.press('ArrowDown');

			await expect(
				getItem(2).locator('.miller-columns-item-mask')
			).toBeFocused();

			// Check we can move to item content with tab

			await page.keyboard.press('Tab');

			await expect(page.getByLabel('Move Page 2')).toBeFocused();

			// Move item 4 inside item 3

			const enableMovement = async (index: number) =>
				await page.getByLabel(`Move Page ${index}`).press('Enter');

			await enableMovement(4);

			await expect(async () => {
				await page.keyboard.press('ArrowUp');

				await expect(getItem(3)).toHaveClass(/drop-middle/, {
					timeout: 500,
				});
			}).toPass();

			await page.keyboard.press('Enter');

			await expect(
				page
					.locator('.miller-columns-col')
					.nth(1)
					.locator('.miller-columns-item')
			).toContainText('Page 4');

			// Check we can come back to parent with left arrow

			await page.keyboard.press('ArrowLeft');

			await expect(
				getItem(3).locator('.miller-columns-item-mask')
			).toBeFocused();
		}
	);

	test(
		'Accessibility checks',
		{tag: ['@LPD-35946']},
		async ({apiHelpers, page, pagesAdminPage, site}) => {

			// Create two pages at first level

			const firstPage = await apiHelpers.headlessDelivery.createSitePage({
				siteId: site.id,
				title: 'Page 1-1',
			});

			await apiHelpers.headlessDelivery.createSitePage({
				siteId: site.id,
				title: 'Page 1-2',
			});

			// Create two pages at second level as child of Page 1-1

			await apiHelpers.headlessDelivery.createSitePage({
				parentSitePage: {
					friendlyUrlPath: firstPage.friendlyUrlPath,
				},
				siteId: site.id,
				title: 'Page 2-1',
			});

			await apiHelpers.headlessDelivery.createSitePage({
				parentSitePage: {
					friendlyUrlPath: firstPage.friendlyUrlPath,
				},
				siteId: site.id,
				title: 'Page 2-2',
			});

			// Go to pages admin

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await page
				.locator('.miller-columns-item', {hasText: 'Page 1-1'})
				.waitFor();

			// Focus until reach Page 1-1 item drag handler

			const getItem = (title: string) =>
				page.locator('.miller-columns-item', {hasText: title});

			await expect(async () => {
				await page.keyboard.press('Tab');

				await expect(
					getItem('Page 1-1').locator('.drag-handler')
				).toBeFocused({timeout: 500});
			}).toPass();

			// Enable and cancel movement and check drag handler keeps focus

			await page.getByLabel('Move Page 1-1').press('Enter');

			await expect(getItem('Page 1-1')).toHaveClass(/dragging/);

			await page.keyboard.press('Escape');

			await expect(getItem('Page 1-1')).not.toHaveClass(/dragging/);

			await expect(
				getItem('Page 1-1').locator('.drag-handler')
			).toBeFocused();

			// Focus the anchor again and press Enter to check item keeps focus after navigate

			await page.keyboard.press('Shift+Tab');

			await expect(
				getItem('Page 1-1').locator('.miller-columns-item-mask')
			).toBeFocused();

			await page.keyboard.press('Enter');

			await expect(getItem('Page 2-2')).toBeVisible();

			await expect(
				getItem('Page 1-1').locator('.miller-columns-item-mask')
			).toBeFocused();

			// Check loading children with Arrow Right

			await getItem('Page 1-2')
				.locator('.miller-columns-item-mask')
				.press('Enter');

			await expect(getItem('Page 2-1')).not.toBeVisible();

			await getItem('Page 1-2').waitFor();

			await page.keyboard.press('ArrowUp');

			await expect(
				getItem('Page 1-1').locator('.miller-columns-item-mask')
			).toBeFocused();

			await page.keyboard.press('ArrowRight');

			await expect(
				getItem('Page 2-1').locator('.miller-columns-item-mask')
			).toBeFocused();

			// Check title link works well

			await expect(async () => {
				await page.keyboard.press('Tab');

				await expect(
					getItem('Page 2-1').getByRole('link', {name: 'Page 2-1'})
				).toBeFocused({timeout: 500});
			}).toPass();

			await page.keyboard.press('Enter');

			await expect(getItem('Page 1-1')).not.toBeVisible();

			// Come back to pages admin

			await clickAndExpectToBeVisible({
				target: getItem('Page 1-1'),
				trigger: page.getByTitle('Go to Pages'),
			});

			// Check item actions dropdown works well

			await getItem('Page 1-1').getByLabel('Add Child Page').focus();

			await page.keyboard.press('Tab');

			await expect(
				getItem('Page 1-1').getByLabel('Open Page Options Menu')
			).toBeFocused();

			await page.keyboard.press('Enter');

			await expect(
				page.getByRole('menuitem', {name: 'Edit'})
			).toBeVisible();

			// Move to Convert to Page Template option and press Enter

			await expect(async () => {
				await page.keyboard.press('ArrowDown');

				await expect(
					page.getByRole('menuitem', {
						name: 'Convert to Page Template',
					})
				).toBeFocused({timeout: 500});
			}).toPass();

			await page.keyboard.press('Enter');

			await expect(page.getByText('Add Page Template Set')).toBeVisible();
		}
	);
});

test.describe('Miller Columns drag and drop', () => {
	const getColumn = (page: Page, index: number) =>
		page.locator('.miller-columns-col').nth(index);

	const checkItemTitle = async ({
		columnIndex,
		itemIndex,
		page,
		title,
	}: {
		columnIndex: number;
		itemIndex: number;
		page: Page;
		title: string;
	}) => {
		await expect(
			getColumn(page, columnIndex)
				.locator('.miller-columns-item')
				.nth(itemIndex)
		).toContainText(title);
	};

	const getItem = (page: Page, title: string) =>
		page.locator('.miller-columns-item', {hasText: title});

	test(
		'Drag and drop with mouse works as expected',
		{tag: ['@LPS-110108', '@LPS-114527', '@LPS-110108']},
		async ({apiHelpers, page, pagesAdminPage, site}) => {

			// Create two pages at first level

			const firstPage = await apiHelpers.headlessDelivery.createSitePage({
				siteId: site.id,
				title: 'Page 1-1',
			});

			await apiHelpers.headlessDelivery.createSitePage({
				siteId: site.id,
				title: 'Page 1-2',
			});

			// Create two pages at second level as child of Page 1-1

			await apiHelpers.headlessDelivery.createSitePage({
				parentSitePage: {
					friendlyUrlPath: firstPage.friendlyUrlPath,
				},
				siteId: site.id,
				title: 'Page 2-1',
			});

			await apiHelpers.headlessDelivery.createSitePage({
				parentSitePage: {
					friendlyUrlPath: firstPage.friendlyUrlPath,
				},
				siteId: site.id,
				title: 'Page 2-2',
			});

			// Go to pages administration and click Page 1-1 to show its children

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await getItem(page, 'Page 1-1').click();

			await expect(getItem(page, 'Page 2-1')).toBeVisible();

			// Drag Page 1-1 to second position of same level

			const itemRect = await getItem(page, 'Page 1-1').evaluate(
				(element) => element.getBoundingClientRect()
			);

			const dragItem = async (
				source: Locator,
				target: Locator,
				position: 'bottom' | 'middle' | 'top'
			) => {
				let targetPosition: {x: number; y: number};

				if (position === 'bottom') {
					targetPosition = {
						x: itemRect.width / 2,
						y: itemRect.height - 2,
					};
				}
				else if (position === 'top') {
					targetPosition = {
						x: itemRect.width / 2,
						y: itemRect.y + 2,
					};
				}
				else {
					targetPosition = {
						x: itemRect.width / 2,
						y: itemRect.height / 2,
					};
				}

				await source.locator('.drag-handler').dragTo(target, {
					targetPosition,
				});
			};

			await dragItem(
				getItem(page, 'Page 1-1'),
				getItem(page, 'Page 1-2'),
				'bottom'
			);

			await checkItemTitle({
				columnIndex: 0,
				itemIndex: 1,
				page,
				title: 'Page 1-1',
			});

			// Move child page to parent level

			await dragItem(
				getItem(page, 'Page 2-1'),
				getItem(page, 'Page 1-2'),
				'bottom'
			);

			await checkItemTitle({
				columnIndex: 0,
				itemIndex: 1,
				page,
				title: 'Page 2-1',
			});

			// Move two pages from different levels

			await getItem(page, 'Page 1-1').click();

			await expect(getItem(page, 'Page 2-2')).toBeVisible();

			await page.getByLabel('Select Page 1-2').click();
			await page.getByLabel('Select Page 2-2').click();

			await dragItem(
				getItem(page, 'Page 1-2'),
				getItem(page, 'Page 1-1'),
				'bottom'
			);

			await checkItemTitle({
				columnIndex: 0,
				itemIndex: 2,
				page,
				title: 'Page 1-2',
			});

			await checkItemTitle({
				columnIndex: 0,
				itemIndex: 3,
				page,
				title: 'Page 2-2',
			});

			// Move two pages from same level

			await page.getByLabel('Select Page 2-1').click();
			await page.getByLabel('Select Page 1-1').click();

			await dragItem(
				getItem(page, 'Page 2-1'),
				getItem(page, 'Page 2-2'),
				'bottom'
			);

			await checkItemTitle({
				columnIndex: 0,
				itemIndex: 2,
				page,
				title: 'Page 2-1',
			});

			await checkItemTitle({
				columnIndex: 0,
				itemIndex: 3,
				page,
				title: 'Page 1-1',
			});
		}
	);

	test(
		'Drag and drop a page to a white space in a column',
		{tag: ['@LPD-62265']},
		async ({apiHelpers, page, pagesAdminPage, site}) => {

			// Create two pages at first level

			const firstPage = await apiHelpers.headlessDelivery.createSitePage({
				siteId: site.id,
				title: 'Page 1',
			});

			await apiHelpers.headlessDelivery.createSitePage({
				siteId: site.id,
				title: 'Page to move',
			});

			// Create one page at second level as child of Page 1

			await apiHelpers.headlessDelivery.createSitePage({
				parentSitePage: {
					friendlyUrlPath: firstPage.friendlyUrlPath,
				},
				siteId: site.id,
				title: 'Page 2',
			});

			// Go to pages administration and click Page 1 to show its children

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await getItem(page, 'Page 1').click();

			await expect(getItem(page, 'Page 2')).toBeVisible();

			// Drag 'Page to move' to second position of second level

			const column = getColumn(page, 1);

			const columnRect = await column.evaluate((element) =>
				element.getBoundingClientRect()
			);

			const targetPosition = {
				x: columnRect.width / 2,
				y: columnRect.height - 2,
			};

			await getItem(page, 'Page to move')
				.locator('.drag-handler')
				.dragTo(column, {targetPosition});

			await checkItemTitle({
				columnIndex: 1,
				itemIndex: 1,
				page,
				title: 'Page to move',
			});
		}
	);
});

test('Changes the permissions of a group of pages', async ({
	apiHelpers,
	page,
	pagesAdminPage,
	site,
}) => {

	// Create two random pages

	const firstName = getRandomString();
	const secondName = getRandomString();

	for (const pageName of [firstName, secondName]) {
		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: pageName,
		});
	}

	// Go to admin page

	await pagesAdminPage.goto(site.friendlyUrlPath);

	// Change permissions for first page

	await pagesAdminPage.changePagesPermissions(
		[firstName],
		['guest_ACTION_VIEW']
	);

	// Select first and second page and open the modal of permissions

	await pagesAdminPage.selectPages([firstName, secondName]);

	await page.getByRole('button', {name: 'Permissions'}).click();

	const permissionsFrame = page.frameLocator('iframe[title="Permissions"]');

	await permissionsFrame
		.getByRole('cell', {exact: true, name: 'Role'})
		.waitFor();

	// Check that the Guest-View permission value for both pages is indeterminate

	const permission = permissionsFrame.locator('#guest_ACTION_VIEW');

	await expect(permission).toHaveValue('indeterminate');

	await page.getByLabel('Close', {exact: true}).click();

	// Change the Guest-View permission for both pages

	await pagesAdminPage.changePagesPermissions(
		[firstName, secondName],
		['guest_ACTION_VIEW']
	);

	// Refresh the admin page

	await pagesAdminPage.goto(site.friendlyUrlPath);

	// Check if the pages are retricted pages

	for (const pageName of [firstName, secondName]) {
		await expect(
			page.getByLabel(`${pageName}. Restricted Page`)
		).toBeVisible();
	}
});

test('Checks the correct label for restricted pages in pages administration', async ({
	apiHelpers,
	page,
	pagesAdminPage,
	site,
}) => {

	// Create a page with only one permission

	const pageName = getRandomString();

	await apiHelpers.headlessDelivery.createSitePage({
		pagePermissions: [
			{
				actionKeys: ['VIEW'],
				roleKey: 'Owner',
			},
		],
		siteId: site.id,
		title: pageName,
	});

	// Go to admin page and check if the Restricted Page label is in the Miller Columns item

	await pagesAdminPage.goto(site.friendlyUrlPath);

	await expect(
		page
			.locator('.miller-columns-item')
			.getByLabel(`${pageName}. Restricted Page`)
	).toBeVisible();
});

test('Can add and delete a child page', async ({
	apiHelpers,
	page,
	pagesAdminPage,
	site,
}) => {

	// Create parent page

	const parentPageName = getRandomString();

	await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: parentPageName,
	});

	// Create child page and check it actually appears as child

	const childPageName = getRandomString();

	await pagesAdminPage.goto(site.friendlyUrlPath);

	await pagesAdminPage.createNewPage({
		draft: true,
		name: childPageName,
		parent: parentPageName,
	});

	await pagesAdminPage.goto(site.friendlyUrlPath);

	await page
		.locator('.miller-columns-item', {hasText: parentPageName})
		.click({position: {x: 10, y: 10}});

	await expect(page.getByRole('link', {name: childPageName})).toBeVisible();

	// Check Draft label is shown and we can preview the draft

	await expect(
		page
			.locator('li', {has: page.getByText(childPageName)})
			.getByText('Draft')
	).toBeVisible();

	await clickAndExpectToBeVisible({
		target: page.getByRole('menuitem', {
			name: 'Preview Draft',
		}),
		trigger: page
			.locator('li', {has: page.getByText(childPageName)})
			.getByRole('button', {name: 'Open Page Options Menu'}),
	});

	// Delete child page

	await pagesAdminPage.deletePage(childPageName);

	await expect(
		page.getByRole('link', {name: childPageName})
	).not.toBeVisible();
});

test(
	'Can search a child page',
	{tag: ['@LPS-154130', '@LPS-149161', '@LPS-76825']},
	async ({apiHelpers, page, pagesAdminPage, site}) => {

		// Add a child page

		const layoutTitle = 'Parent Layout';

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: layoutTitle,
		});

		const childLayoutTitle = 'Child Layout';

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			parentLayoutId: layout.layoutId,
			title: childLayoutTitle,
		});

		// Go to admin page

		await pagesAdminPage.goto(site.friendlyUrlPath);

		// Search for parent page

		await pagesAdminPage.searchPage('Parent');

		await expect(
			page
				.locator('.lfr-title-column')
				.getByRole('link', {name: childLayoutTitle})
		).not.toBeVisible();

		await expect(
			page
				.locator('.lfr-title-column')
				.getByRole('link', {name: layoutTitle})
		).toBeVisible();

		// Search for child page

		await pagesAdminPage.searchPage('Child');

		await expect(
			page
				.locator('.lfr-title-column')
				.getByRole('link', {name: childLayoutTitle})
		).toBeVisible();

		await expect(
			page
				.locator('.lfr-title-column')
				.getByRole('link', {name: layoutTitle})
		).not.toBeVisible();

		// Order by create date

		await pagesAdminPage.searchPage('Layout');

		const listItem = page.locator('.lfr-title-column');

		await expect(listItem.nth(1)).toHaveText(layoutTitle);
		await expect(listItem.nth(2)).toHaveText(childLayoutTitle);

		// Navigate to page via relative path

		await page
			.locator('.breadcrumb-item')
			.getByRole('link', {name: layoutTitle})
			.click();

		await expect(page.getByText('Search Results')).not.toBeVisible();

		await expect(page.locator('.breadcrumb-item.active')).toHaveText(
			layoutTitle
		);
	}
);

test(
	'Copy page with permissions',
	{
		tag: '@LPD-177031',
	},
	async ({apiHelpers, page, pagesAdminPage, site}) => {

		// Add content page

		const layoutTitle = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: layoutTitle,
		});

		// Configure permissions

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.changePagesPermissions(
			[layoutTitle],
			[
				'guest_ACTION_VIEW',
				'site-member_ACTION_CUSTOMIZE',
				'site-member_ACTION_VIEW',
			]
		);

		// Copy page with permissions

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				exact: true,
				name: 'Make a Copy',
			}),
			trigger: page
				.locator('li', {has: page.getByText(layoutTitle)})
				.getByRole('button', {name: 'Open Page Options Menu'}),
		});

		await hoverAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Page With Permissions'}),
			trigger: page.getByRole('menuitem', {name: 'Make a Copy'}),
		});

		const copyPageWithPermissionsFrame = page.frameLocator(
			'iframe[title="Copy Page With Permissions"]'
		);

		const copyPageName = getRandomString();

		await copyPageWithPermissionsFrame
			.getByPlaceholder('Add Page Name')
			.fill(copyPageName);

		await copyPageWithPermissionsFrame
			.getByRole('button', {name: 'Add'})
			.click();

		await waitForAlert(page);

		// Assert copied permissions

		await pagesAdminPage.clickOnAction('Permissions', copyPageName);

		const permissionsFrame = page.frameLocator(
			'iframe[title="Permissions"]'
		);

		await permissionsFrame
			.getByRole('cell', {exact: true, name: 'Role'})
			.waitFor();

		await expect(
			permissionsFrame.locator('#guest_ACTION_VIEW')
		).not.toBeChecked();

		await expect(
			permissionsFrame.locator('#owner_ACTION_VIEW')
		).toBeChecked();

		await expect(
			permissionsFrame.locator('#site-member_ACTION_CUSTOMIZE')
		).not.toBeChecked();

		await expect(
			permissionsFrame.locator('#site-member_ACTION_VIEW')
		).not.toBeChecked();
	}
);

test(
	'Copy page without permissions',
	{
		tag: '@LPD-177031',
	},
	async ({apiHelpers, page, pagesAdminPage, site}) => {

		// Add content page

		const layoutTitle = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: layoutTitle,
		});

		// Configure permissions

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.changePagesPermissions(
			[layoutTitle],
			[
				'guest_ACTION_VIEW',
				'site-member_ACTION_CUSTOMIZE',
				'site-member_ACTION_VIEW',
			]
		);

		// Copy page with permissions

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				exact: true,
				name: 'Make a Copy',
			}),
			trigger: page
				.locator('li', {has: page.getByText(layoutTitle)})
				.getByRole('button', {name: 'Open Page Options Menu'}),
		});

		await hoverAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {exact: true, name: 'Page'}),
			trigger: page.getByRole('menuitem', {name: 'Make a Copy'}),
		});

		const copyPageFrame = page.frameLocator('iframe[title="Copy Page"]');

		const copyPageName = getRandomString();

		await copyPageFrame
			.getByPlaceholder('Add Page Name')
			.fill(copyPageName);

		await copyPageFrame.getByRole('button', {name: 'Add'}).click();

		await waitForAlert(page);

		// Assert copied permissions

		await pagesAdminPage.clickOnAction('Permissions', copyPageName);

		const permissionsFrame = page.frameLocator(
			'iframe[title="Permissions"]'
		);

		await permissionsFrame
			.getByRole('cell', {exact: true, name: 'Role'})
			.waitFor();

		await expect(
			permissionsFrame.locator('#guest_ACTION_VIEW')
		).toBeChecked();

		await expect(
			permissionsFrame.locator('#owner_ACTION_VIEW')
		).toBeChecked();

		await expect(
			permissionsFrame.locator('#site-member_ACTION_CUSTOMIZE')
		).toBeChecked();

		await expect(
			permissionsFrame.locator('#site-member_ACTION_VIEW')
		).toBeChecked();
	}
);

test(
	'If the page has more than one experience, the page template is created from the default experience',
	{
		tag: '@LPD-194263',
	},
	async ({apiHelpers, page, pageEditorPage, pagesAdminPage, site}) => {

		// Create page with a paragraph fragment and go to edit mode

		const layoutTitle = getRandomString();

		const paragraphId = getRandomString();

		const paragraphDefinition = getFragmentDefinition({
			id: paragraphId,
			key: 'BASIC_COMPONENT-paragraph',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([paragraphDefinition]),
			siteId: site.id,
			title: layoutTitle,
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Update editable text

		await pageEditorPage.editTextEditable(
			paragraphId,
			'element-text',
			'Default Text'
		);

		// Create experience

		await pageEditorPage.createExperience('E1');

		await pageEditorPage.editTextEditable(
			paragraphId,
			'element-text',
			'E1 Text'
		);

		await pageEditorPage.publishPage();

		// Convert layout into a page template

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.clickOnAction(
			'Convert to Page Template',
			layoutTitle
		);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			page,
			'Success:The page template was created successfully',
			{autoClose: false}
		);

		await page.getByRole('link', {name: 'See in Page Templates'}).click();

		// Assert page template was created successfully

		await page
			.getByRole('link', {name: `${layoutTitle} - Page Template`})
			.click();

		await expect(page.getByText('Default Text')).toBeVisible();

		await expect(page.getByText('E1 Text')).not.toBeVisible();
	}
);

test(
	'Switch page templates on preview page template modal during page creation',
	{
		tag: '@LPS-172658',
	},
	async ({
		page,
		pageEditorPage,
		pageTemplatesPage,
		pagesAdminPage,
		site,
		widgetPagePage,
	}) => {

		// Go to page template administration in global site

		await pageTemplatesPage.goto(site.friendlyUrlPath);

		// Create page template collection

		const pageTemplateCollectionName = getRandomString();

		await pageTemplatesPage.addPageTemplateCollection(
			pageTemplateCollectionName
		);

		// Create widget page template with web content display widget

		const widgetPageTemplateName = 'Widget Page Template';

		await pageTemplatesPage.addWidgetPageTemplate(widgetPageTemplateName);

		await widgetPagePage.addPortlet('Web Content Display');

		// Create content page template with heading fragment

		await pageTemplatesPage.goto(site.friendlyUrlPath);

		const contentPageTemplateName = 'Content Page Template';

		await pageTemplatesPage.addContentPageTemplate(contentPageTemplateName);

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		await pageEditorPage.publishButton.click();

		await waitForAlert(
			page,
			'Success:The page template was published successfully.'
		);

		// Preview page templates

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.gotoSelectTemplates(pageTemplateCollectionName);

		await page.getByTitle('Preview Page Template').first().click();

		// Assert page templates

		const iframe = page.locator('.modal-dialog').frameLocator('iframe');

		await expect(iframe.getByText('Heading Example')).toBeVisible();

		await expect(
			page.getByRole('banner').getByText(contentPageTemplateName)
		).toBeVisible();

		await expect(page.getByText('1 of 2')).toBeVisible();

		await page.getByLabel('Go to Next Template').click();

		await expect(iframe.getByText('Web Content Display')).toBeVisible();

		await expect(
			page.getByRole('banner').getByText(widgetPageTemplateName)
		).toBeVisible();

		await expect(page.getByText('2 of 2')).toBeVisible();

		// Create page from widget page template

		await page
			.getByRole('button', {name: 'Create Page from This Template'})
			.click();

		const addPageIframe = page.frameLocator('iframe[title="Add Page"]');

		const pageName = getRandomString();

		await addPageIframe.getByPlaceholder('Add Page Name').fill(pageName);

		await addPageIframe.getByRole('button', {name: 'Add'}).click();

		await waitForAlert(page, 'Success:The page was created successfully.');

		// Assert widget page is created based on widget page template

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.clickOnAction('View', pageName);

		await expect(
			page.getByRole('heading', {name: 'Web Content Display'})
		).toBeVisible();

		await expect(
			page.getByText('This application is not visible to users yet.')
		).toBeVisible();
	}
);

test(
	'View the XSS is escaped when store it in widget page name',
	{
		tag: '@LPS-178476',
	},
	async ({apiHelpers, page, pagesAdminPage, site}) => {

		// Add listener with expect so it fails when a browser dialog is shown

		page.on('dialog', async (dialog) => {
			dialog.accept();

			expect(
				dialog.message(),
				'This alert should not be shown'
			).toBeNull();
		});

		// Create page and go to view mode to check dialog is not shown

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: '<script>alert(123);</script>',
		});

		await pagesAdminPage.goto(site.friendlyUrlPath);
	}
);

test(
	'toastData parameter is escaped to avoid Javascript execution',
	{
		tag: '@LPD-35827',
	},
	async ({page}) => {

		// Add listener with expect so it fails when a browser dialog is shown

		page.on('dialog', async (dialog) => {
			dialog.accept();

			expect(
				dialog.message(),
				'This alert should not be shown'
			).toBeNull();
		});

		const data = {
			message: '<img src=x onerror=alert(123)>',
			title: 'test',
		};

		const url = page.url();

		await page.goto(
			`${url}?toastData=${encodeURIComponent(JSON.stringify(data))}`
		);
	}
);

test(
	'The sort button for pages is not shown',
	{
		tag: '@LPD-36041',
	},
	async ({apiHelpers, page, pagesAdminPage, site}) => {

		// Create a page

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		// Go to admin page

		await pagesAdminPage.goto(site.friendlyUrlPath);

		// Check the button is not shown

		await expect(
			page.getByLabel('Reverse Order Direction:')
		).not.toBeAttached();
	}
);

test(
	'Can resize columns',
	{
		tag: '@LPD-36861',
	},
	async ({apiHelpers, page, pagesAdminPage, site}) => {

		// Create a page

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		// Go to admin page

		await pagesAdminPage.goto(site.friendlyUrlPath);

		// Check the column have the default layout

		await expect(page.locator('.miller-columns-col')).toHaveClass(
			/col-lg-4 col-md-6 col-11/
		);

		// Check that we can resize the column

		const resizeColumn = page.getByLabel('Resize column');

		await resizeColumn.focus();

		await expect(resizeColumn).toBeFocused();

		await page.keyboard.press('Home');

		await expect(page.locator('.miller-columns-col')).not.toHaveClass(
			/col-lg-4 col-md-6 col-11/
		);

		await expect(page.locator('.miller-columns-col')).toHaveAttribute(
			'style',
			'max-width: 286px; min-width: 286px; width: 286px;'
		);

		await page.keyboard.press('ArrowRight');

		await expect(page.locator('.miller-columns-col')).toHaveAttribute(
			'style',
			'max-width: 306px; min-width: 306px; width: 306px;'
		);

		await page.keyboard.press('End');

		await expect(page.locator('.miller-columns-col')).toHaveAttribute(
			'style',
			'max-width: 672px; min-width: 672px; width: 672px;'
		);

		await page.keyboard.press('ArrowLeft');

		await expect(page.locator('.miller-columns-col')).toHaveAttribute(
			'style',
			'max-width: 652px; min-width: 652px; width: 652px;'
		);
	}
);

testWithPrivatePages(
	'Restricted icon in show in private pages layout set',
	{
		tag: '@LPD-61197',
	},
	async ({apiHelpers, page, pagesAdminPage, site}) => {

		// Add on private and one public page

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await page
			.locator('li')
			.filter({hasText: 'New'})
			.getByRole('button')
			.click();

		await page.getByRole('menuitem', {name: 'Private Page'}).click();

		const layoutTitle = getRandomString();

		await pagesAdminPage.addPage({
			name: layoutTitle,
		});

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		// Check that the icon is only displayed in private pages

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await expect(
			page
				.getByRole('menuitem', {name: 'Public Pages Add Child Page'})
				.locator('.lexicon-icon-password-policies')
		).not.toBeVisible();

		await expect(
			page
				.getByRole('menuitem', {name: 'Private Pages Add Child Page'})
				.locator('.lexicon-icon-password-policies')
		).toBeVisible();
	}
);
