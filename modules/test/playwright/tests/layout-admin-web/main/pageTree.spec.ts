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
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {liferayConfig} from '../../../liferay.config';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import createUserWithPermissions from '../../../utils/createUserWithPermissions';
import dragAndDropElement from '../../../utils/dragAndDropElement';
import getRandomString from '../../../utils/getRandomString';
import {hoverAndExpectToBeVisible} from '../../../utils/hoverAndExpectToBeVisible';
import {performUserSwitch} from '../../../utils/performLogin';
import {openProductMenu} from '../../../utils/productMenu';
import {waitForAlert} from '../../../utils/waitForAlert';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {pagesPagesTest} from './fixtures/pagesPagesTest';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pagesAdminPagesTest,
	pagesPagesTest
);

const testWithPrivatePages = mergeTests(
	test,
	featureFlagsTest({
		'LPD-38869': {enabled: true},
		'LPS-178052': {enabled: true},
	})
);

test(
	'Add child page',
	{
		tag: ['@LPS-103104', '@LPS-102544'],
	},
	async ({apiHelpers, page, pageTreePage, pagesAdminPage, site}) => {

		// Create a new page

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: layoutTitle,
		});

		await page.goto(
			`${liferayConfig.environment.baseUrl}/en/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Open the Product Menu

		await openProductMenu(page);

		// Open tree if it's not already open

		await pageTreePage.open();

		// Add child page

		await page.getByRole('link', {name: layoutTitle}).hover();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Add Child Page'}),
			trigger: page
				.getByRole('treeitem')
				.filter({hasText: layoutTitle})
				.locator('button.dropdown-toggle'),
		});

		const childLayoutTitle = getRandomString();

		await pagesAdminPage.addPage({
			name: childLayoutTitle,
			template: 'Widget Page',
		});

		// Assert child page in page tree

		await page.goto(
			`${liferayConfig.environment.baseUrl}/en/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await openProductMenu(page);

		await pageTreePage.open();

		await expect(
			page.getByRole('link', {name: childLayoutTitle})
		).toBeVisible();
	}
);

test(
	'Check page actions, copy page, permissions and delete',
	{
		tag: '@LPS-107774',
	},
	async ({apiHelpers, page, pageTreePage, site}) => {

		// Add content page

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: layoutTitle,
		});

		// Open the Product Menu

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await openProductMenu(page);

		// Open tree if it's not already open

		await pageTreePage.open();

		// Update permissions

		await page.getByRole('link', {name: layoutTitle}).hover();

		const dropdown = page
			.getByRole('treeitem')
			.filter({hasText: layoutTitle})
			.locator('button.dropdown-toggle');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Permissions'}),
			trigger: dropdown,
		});

		const permissionsFrame = page.frameLocator(
			'iframe[title="Permissions"]'
		);

		const guestActionViewCheckBox =
			permissionsFrame.locator('#guest_ACTION_VIEW');

		await guestActionViewCheckBox.uncheck({trial: true});

		await guestActionViewCheckBox.uncheck();

		await permissionsFrame
			.getByRole('button', {exact: true, name: 'Save'})
			.click();

		await waitForAlert(permissionsFrame);

		await expect(guestActionViewCheckBox).not.toBeChecked();

		await page
			.locator('.modal-header')
			.getByLabel('Close', {exact: true})
			.click();

		// Copy page

		await page.getByRole('link', {name: layoutTitle}).hover();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Copy Page'}),
			trigger: dropdown,
		});

		const copyPageFrame = page.frameLocator('iframe[title="Copy Page"]');

		const copiedPage = getRandomString();

		await copyPageFrame.getByPlaceholder('Add Page Name').fill(copiedPage);

		await copyPageFrame.getByRole('button', {name: 'Add'}).click();

		await expect(page.getByRole('link', {name: copiedPage})).toBeVisible();

		// Delete page

		await page.getByRole('link', {name: layoutTitle}).hover();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Delete'}),
			trigger: dropdown,
		});

		await page.getByRole('button', {name: 'Delete'}).click();

		await expect(
			page.getByRole('link', {name: layoutTitle})
		).not.toBeVisible();
	}
);

testWithPrivatePages(
	'Can navigate to pages through pages hierarchy and navigation menus',
	{
		tag: ['@LPS-102544', '@LPS-133709'],
	},
	async ({apiHelpers, page, pageTreePage, site}) => {

		// Add navigation menu

		const siteNavigationMenuName = getRandomString();

		await apiHelpers.jsonWebServicesSiteNavigationMenu.addSiteNavigationMenu(
			site.id,
			siteNavigationMenuName
		);

		// Create a public page and a private page

		const publicLayoutTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: publicLayoutTitle,
		});

		const privateLayoutTitle = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			privateLayout: 'true',
			title: privateLayoutTitle,
		});

		await page.goto(
			`${liferayConfig.environment.baseUrl}/en/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Open the Product Menu

		await openProductMenu(page);

		// Open tree if it's not already open

		await pageTreePage.open();

		// Assert private page

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'Private Pages'}),
			trigger: page.getByLabel('Pages Type'),
		});

		await expect(
			page.getByRole('link', {name: publicLayoutTitle})
		).not.toBeVisible();

		await expect(
			page.getByRole('link', {name: privateLayoutTitle})
		).toBeVisible();

		// Assert navigation menu

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: siteNavigationMenuName}),
			trigger: page.getByLabel('Pages Type'),
		});

		await expect(
			page.getByRole('link', {name: publicLayoutTitle})
		).not.toBeVisible();

		await expect(
			page.getByRole('link', {name: privateLayoutTitle})
		).not.toBeVisible();

		// Assert public page

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'Public Pages'}),
			trigger: page.getByLabel('Pages Type'),
		});

		await expect(
			page.getByRole('link', {name: publicLayoutTitle})
		).toBeVisible();

		await expect(
			page.getByRole('link', {name: privateLayoutTitle})
		).not.toBeVisible();
	}
);

test(
	'Check back button',
	{
		tag: ['@LPS-112992', '@LPS-116618', '@LPS-148241'],
	},
	async ({apiHelpers, page, pageTreePage, site}) => {

		// Create a new page

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: layoutTitle,
		});

		await page.goto(
			`${liferayConfig.environment.baseUrl}/en/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Open the Product Menu

		await openProductMenu(page);

		// Open tree if it's not already open

		await pageTreePage.open();

		// Configure page

		await page.getByRole('link', {name: layoutTitle}).hover();

		await expect(async () => {
			await page
				.getByRole('treeitem')
				.filter({hasText: layoutTitle})
				.locator('button.dropdown-toggle')
				.click({timeout: 500});

			await expect(
				page.getByRole('menuitem', {name: 'Configure'})
			).toBeVisible({timeout: 500});

			await page
				.getByRole('menuitem', {name: 'Configure'})
				.click({timeout: 500});

			await expect(
				page.locator('.nav-item').getByText('General')
			).toBeVisible({timeout: 2000});
		}).toPass();

		// Click back button

		await clickAndExpectToBeVisible({
			target: page.getByRole('heading', {name: layoutTitle}),
			trigger: page.getByRole('link', {name: `Go to ${layoutTitle}`}),
		});

		// Configure pages

		await clickAndExpectToBeVisible({
			target: page.locator('.nav-item').getByText('Design'),
			trigger: page.getByLabel('Configure Pages'),
		});

		// Click back button

		await clickAndExpectToBeVisible({
			target: page.getByRole('heading', {name: layoutTitle}),
			trigger: page.getByRole('link', {exact: true, name: 'Go to Pages'}),
		});
	}
);

test('Checks the correct label for restricted page in the Page Tree', async ({
	apiHelpers,
	page,
	pageTreePage,
	site,
}) => {

	// Create a content page with only one permission and open the edit mode

	const pageName = getRandomString();

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pagePermissions: [
			{
				actionKeys: ['VIEW'],
				roleKey: 'Owner',
			},
		],
		siteId: site.id,
		title: pageName,
	});

	await page.goto(
		`${liferayConfig.environment.baseUrl}/en/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
	);

	// Open the Product Menu

	await openProductMenu(page);

	// Open tree if it's not already open

	await pageTreePage.open();

	// Check the correct label for restricted page

	await expect(
		page
			.getByLabel('Product Menu', {exact: true})
			.locator('div', {
				hasText: pageName,
			})
			.getByLabel('Restricted Page')
	).toBeVisible();
});

test(
	'Checks unprivileged users can not add a page via Page Tree',
	{
		tag: '@LPS-129406',
	},
	async ({apiHelpers, page, pageTreePage}) => {
		await page.goto('/');

		// Open the Product Menu

		await openProductMenu(page);

		// Open tree if it's not already open

		await pageTreePage.open();

		// Assert add page button is visible for admin user

		await expect(
			page
				.locator('.page-type-selector')
				.getByTitle('Add Page', {exact: true})
		).toBeVisible();

		// Switch to a new user with update page permissions and without edit segments entry permissions

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const user = await createUserWithPermissions({
			apiHelpers,
			rolePermissions: [
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: company.companyId,
					resourceName:
						'com_liferay_layout_admin_web_portlet_GroupPagesPortlet',
					scope: 1,
				},
				{
					actionIds: ['VIEW_SITE_ADMINISTRATION'],
					primaryKey: company.companyId,
					resourceName: 'com.liferay.portal.kernel.model.Group',
					scope: 1,
				},
			],
		});

		await performUserSwitch(page, user.alternateName);

		// Open the Product Menu

		await page.goto('/');

		await openProductMenu(page);

		// Open tree if it's not already open

		await pageTreePage.open();

		// Assert add page button is not visible

		await expect(
			page
				.locator('.page-type-selector')
				.getByTitle('Add Page', {exact: true})
		).not.toBeVisible();
	}
);

test(
	'Load more button is working while editing a page',
	{
		tag: ['@LPS-90363', '@LPS-168856'],
	},
	async ({apiHelpers, page, pageEditorPage, pageTreePage, site}) => {

		// Add a content page

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition(),
			siteId: site.id,
			title: layoutTitle,
		});

		// Add 20 pages

		for (let i = 0; i < 20; i++) {
			await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition(),
				siteId: site.id,
				title: `Test Content Page ${i}`,
			});
		}

		// Go to edit a content page

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Make sure the sidebars have loaded

		await page.getByLabel('Components', {exact: true}).waitFor();

		// Open the Product Menu

		await openProductMenu(page);

		// Open tree if it's not already open

		await pageTreePage.open();

		// Click load more button

		await page.getByRole('button', {name: 'Load More Results'}).click();

		// Assert last page

		await expect(
			page.getByRole('link', {exact: true, name: 'Test Content Page 19'})
		).toBeVisible();

		// Search pages

		await page
			.getByPlaceholder('Start typing to find a page.')
			.fill(layoutTitle);

		// Assert results

		await expect(
			page.getByRole('link', {name: 'Test Content Page 18'})
		).not.toBeVisible();

		await expect(page.getByRole('link', {name: layoutTitle})).toBeVisible();
	}
);

test(
	'Reorganize pages via page tree',
	{
		tag: '@LPS-116428',
	},
	async ({apiHelpers, page, pageTreePage, site}) => {

		// Add pages

		const firstLayoutTitle = 'first layout';

		const firstLayout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: firstLayoutTitle,
		});

		const secondLayoutTitle = 'second layout';

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: secondLayoutTitle,
		});

		const thirdLayoutTitle = 'third layout';

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: thirdLayoutTitle,
		});

		// Go to view mode

		await page.goto(
			`/web${site.friendlyUrlPath}${firstLayout.friendlyUrlPath}`
		);

		// Open the Product Menu

		await openProductMenu(page);

		// Open tree if it's not already open

		await pageTreePage.open();

		// Assert order

		const treeViewItems = page.getByRole('treeitem');

		await expect(treeViewItems.nth(1)).toContainText(firstLayoutTitle);

		await expect(treeViewItems.nth(2)).toContainText(secondLayoutTitle);

		await expect(treeViewItems.nth(3)).toContainText(thirdLayoutTitle);

		// Reorder pages

		await dragAndDropElement({
			dragTarget: treeViewItems.nth(3),
			dropTarget: treeViewItems.nth(1),
			page,
		});

		// Assert order

		await expect(treeViewItems.nth(1)).toContainText(firstLayoutTitle);

		await expect(treeViewItems.nth(2)).toContainText(thirdLayoutTitle);

		await expect(treeViewItems.nth(3)).toContainText(secondLayoutTitle);
	}
);

test(
	'Users can see the preview draft when the content page at draft status',
	{
		tag: '@LPS-139064',
	},
	async ({apiHelpers, context, page, pageEditorPage, pageTreePage, site}) => {

		// Create a page and go to edit mode

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition(),
			siteId: site.id,
			title: layoutTitle,
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Add heading fragment

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		// Open tree if it's not already open

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await openProductMenu(page);

		await pageTreePage.open();

		// Click on preview draft

		await page.getByRole('link', {name: layoutTitle}).hover();

		const pagePromise = context.waitForEvent('page');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Preview Draft'}),
			trigger: page
				.getByRole('treeitem')
				.filter({hasText: layoutTitle})
				.locator('button.dropdown-toggle'),
		});

		// Assert draft content page

		const newPage = await pagePromise;

		await expect(
			newPage
				.getByLabel('Control Menu')
				.locator('.label-item', {hasText: 'Draft'})
		).toBeVisible();

		await expect(newPage.getByText('Heading Example')).toBeVisible();
	}
);

test(
	'User can show multiple child pages',
	{
		tag: '@LPS-184551',
	},
	async ({apiHelpers, page, pageTreePage, site}) => {

		// Add first child page

		const firstLayoutTitle = 'First Parent Layout';

		const firstLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: firstLayoutTitle,
		});

		const firstChildLayoutTitle = 'First Child Layout';

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			parentLayoutId: firstLayout.layoutId,
			title: firstChildLayoutTitle,
		});

		// Add second child page

		const secondLayoutTitle = 'Second Parent Layout';

		const secondLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: secondLayoutTitle,
		});

		const secondChildLayoutTitle = 'Second Child Layout';

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			parentLayoutId: secondLayout.layoutId,
			title: secondChildLayoutTitle,
		});

		// Open tree if it's not already open

		await page.goto(
			`/web${site.friendlyUrlPath}${firstLayout.friendlyURL}`
		);

		await openProductMenu(page);

		await pageTreePage.open();

		// Assert child pages

		await page
			.getByRole('treeitem', {name: secondLayoutTitle})
			.getByRole('button')
			.first()
			.click();

		await expect(
			page.getByRole('link', {name: firstLayoutTitle})
		).toBeVisible();

		await expect(
			page.getByRole('link', {name: firstChildLayoutTitle})
		).toBeVisible();

		await expect(
			page.getByRole('link', {name: secondLayoutTitle})
		).toBeVisible();

		await expect(
			page.getByRole('link', {name: secondChildLayoutTitle})
		).toBeVisible();
	}
);

test(
	'Users with only View permissions can not see draft options',
	{
		tag: '@LPS-140136',
	},
	async ({apiHelpers, page, pageEditorPage, pageTreePage, site}) => {

		// Create a page and go to edit mode

		const pageName = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition(),
			siteId: site.id,
			title: pageName,
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Add a fragment so we create a draft

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		// Switch to a new user with only View page permission

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const user = await createUserWithPermissions({
			apiHelpers,
			rolePermissions: [
				{
					actionIds: ['VIEW'],
					primaryKey: company.companyId,
					resourceName: 'com.liferay.portal.kernel.model.Layout',
					scope: 1,
				},
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
					primaryKey: company.companyId,
					resourceName:
						'com_liferay_layout_admin_web_portlet_GroupPagesPortlet',
					scope: 1,
				},
				{
					actionIds: ['ADD_LAYOUT', 'VIEW_SITE_ADMINISTRATION'],
					primaryKey: company.companyId,
					resourceName: 'com.liferay.portal.kernel.model.Group',
					scope: 1,
				},
			],
		});

		await performUserSwitch(page, user.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		// Open the Product Menu

		await openProductMenu(page);

		// Open tree if it's not already open

		await pageTreePage.open();

		// Check draft actions are not present

		const treeItem = page.getByRole('treeitem', {name: pageName});

		await hoverAndExpectToBeVisible({
			autoClick: true,
			target: treeItem.locator('.dropdown-toggle'),
			trigger: treeItem,
		});

		await expect(
			page.getByRole('menuitem', {name: 'Preview Draft'})
		).not.toBeVisible();
	}
);
