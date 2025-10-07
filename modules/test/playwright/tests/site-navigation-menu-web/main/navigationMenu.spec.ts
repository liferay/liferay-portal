/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {customFieldsPagesTest} from '../../../fixtures/customFieldsPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {TCustomField} from '../../../helpers/CustomFieldTypesHelper';
import {ProductMenuPage} from '../../../pages/product-navigation-control-menu-web/ProductMenuPage';
import getRandomString from '../../../utils/getRandomString';
import performLoginViaApi, {performLogout} from '../../../utils/performLogin';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {pagesPagesTest} from '../../layout-admin-web/main/fixtures/pagesPagesTest';
import {sitesAdminPagesTest} from '../../site-admin-web/main/fixtures/sitesAdminPagesTest';
import {navigationMenusPagesTest} from '../../site-navigation-admin-web/main/fixtures/navigationMenusPagesTest';
import {NavigationMenusPage} from '../../site-navigation-admin-web/main/pages/NavigationMenusPage';
import {templatesPageTest} from '../../template-web/main/fixtures/templatesPageTest';
import {navigationMenuWidgetPagesTest} from './fixtures/navigationMenuWidgetPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	customFieldsPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	formsPagesTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	navigationMenusPagesTest,
	pagesAdminPagesTest,
	pagesPagesTest,
	productMenuPageTest,
	templatesPageTest,
	pageViewModePagesTest,
	sitesAdminPagesTest,
	navigationMenuWidgetPagesTest
);

async function deleteGlobalNavigationMenus(
	navigationMenusPage: NavigationMenusPage,
	productMenuPage: ProductMenuPage
) {
	await navigationMenusPage.gotoGlobalSiteNavigationMenuPortlet();

	await productMenuPage.page
		.getByLabel('Select All Items on the Page')
		.check();

	await productMenuPage.page.getByRole('button', {name: 'Delete'}).click();

	await productMenuPage.page
		.getByLabel('Delete Navigation Menus')
		.getByRole('button', {name: 'Delete'})
		.click();
}

test(
	'Add URL type Navigation Menu Item with "open in a new tab" checkbox unchecked',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		navigationMenusPage,
		page,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await navigationMenusPage.goto(site.friendlyUrlPath);

		const navigationMenuName = getRandomString();

		await navigationMenusPage.createNavigationMenu(navigationMenuName);

		const urlItemName = getRandomString();

		await navigationMenusPage.addURLItem(urlItemName);

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await navigationMenuWidgetPage.selectCustomNavigationMenu(
			navigationMenuName
		);

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		await page.getByText(urlItemName).click();

		const currentURL = page.url();

		expect(currentURL).toContain('https://www.liferay.com');
	}
);

test(
	'Add URL type Navigation Menu Item with "open in a new tab" checkbox checked',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		navigationMenusPage,
		page,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await navigationMenusPage.goto(site.friendlyUrlPath);

		const navigationMenuName = getRandomString();

		await navigationMenusPage.createNavigationMenu(navigationMenuName);

		const urlItemName = getRandomString();

		await navigationMenusPage.addURLItem(urlItemName, undefined, true);

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await navigationMenuWidgetPage.selectCustomNavigationMenu(
			navigationMenuName
		);

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		await page.getByText(urlItemName).click();

		const [newPage] = await Promise.all([
			await page.context().waitForEvent('page'),
			await page.getByText(urlItemName).click(),
		]);

		await newPage.waitForLoadState();

		const newTabURL = newPage.url();

		expect(newTabURL).toContain('https://www.liferay.com');
	}
);

test(
	'Cannot view global Navigation Menu in Navigation Menu Widget without permission',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		navigationMenusPage,
		page,
		productMenuPage,
		site,
		widgetPagePage,
	}) => {
		try {
			const navigationMenuName = getRandomString();

			await navigationMenusPage.addNavigationMenuToGlobalSite(
				navigationMenuName
			);

			const urlMenuItemName = getRandomString();

			await navigationMenusPage.addURLItem(urlMenuItemName);

			await page.waitForLoadState();

			await navigationMenusPage.gotoGlobalSiteNavigationMenuPortlet();

			await page
				.getByRole('row', {name: navigationMenuName})
				.getByLabel('Show Actions')
				.click();

			await page.getByRole('menuitem', {name: 'Permissions'}).click();

			const permissionsModal = page.frameLocator(
				'iframe[title="Permissions"]'
			);

			await page.waitForTimeout(1500);

			await permissionsModal.locator('#guest_ACTION_VIEW').uncheck();

			await page.waitForTimeout(1500);

			await permissionsModal.getByRole('button', {name: 'Save'}).click();

			await page
				.locator('.modal')
				.getByLabel('Close', {exact: true})
				.click();

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: getRandomString(),
			});

			await widgetPagePage.goto(layout, site.friendlyUrlPath);

			await navigationMenuWidgetPage.openConfigurationModal(
				layout.nameCurrentValue
			);

			await navigationMenuWidgetPage.selectCustomNavigationMenu(
				navigationMenuName
			);

			await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

			await performLogout(page);

			await widgetPagePage.goto(layout, site.friendlyUrlPath);

			await expect(
				page.getByRole('menuitem', {name: urlMenuItemName})
			).not.toBeVisible();
		}
		finally {
			await performLoginViaApi(page, 'test');

			await deleteGlobalNavigationMenus(
				navigationMenusPage,
				productMenuPage
			);
		}
	}
);

test(
	'Configure Display Template of Navigation Menu Widget',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		page,
		pageConfigurationPage,
		pagesAdminPage,
		site,
		widgetPagePage,
	}) => {
		const layoutName1 = getRandomString();

		const layout1 = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: layoutName1,
		});

		await widgetPagePage.goto(layout1, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(layoutName1);

		await navigationMenuWidgetPage.selectDisplayTemplate(
			'Split Button Dropdowns'
		);

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		await expect(page.getByRole('link', {name: layoutName1})).toBeVisible();

		const layoutName2 = getRandomString();

		const layout2 = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: layoutName2,
		});

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pageConfigurationPage.goToSection(layoutName2, 'General');

		const newLayoutName = getRandomString();

		await pageConfigurationPage.fillName(newLayoutName);

		await pageConfigurationPage.save();

		await widgetPagePage.goto(layout2, site.friendlyUrlPath);

		await widgetPagePage.addPortlet('Blogs');

		await page.locator('header').filter({hasText: 'Blogs'}).hover();

		await page
			.locator(
				'#portlet-topper-toolbar_com_liferay_blogs_web_portlet_BlogsPortlet'
			)
			.getByLabel('Options')
			.click();

		await page
			.getByRole('menuitem', {name: 'Look and Feel Configuration'})
			.click();

		const lookAndFeelIFrame = page.frameLocator(
			'iframe[title="Look and Feel Configuration"]'
		);

		await lookAndFeelIFrame.getByLabel('Use Custom Title').check();

		await lookAndFeelIFrame
			.locator(
				'[id="_com_liferay_portlet_configuration_css_web_portlet_PortletConfigurationCSSPortlet_customTitle"]'
			)
			.fill('Blogs Custom');

		await lookAndFeelIFrame.getByRole('button', {name: 'Save'}).click();
	}
);

test(
	'Edit global Navigation Menu via Navigation Menu Widget in normal site',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		navigationMenusPage,
		page,
		productMenuPage,
		site,
		widgetPagePage,
	}) => {
		try {
			const navigationMenuName = getRandomString();

			await navigationMenusPage.addNavigationMenuToGlobalSite(
				navigationMenuName
			);

			const urlMenuItemName = getRandomString();

			await navigationMenusPage.addURLItem(urlMenuItemName);

			const layoutName = getRandomString();

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: layoutName,
			});

			await widgetPagePage.goto(layout, site.friendlyUrlPath);

			await navigationMenuWidgetPage.openConfigurationModal(layoutName);

			await navigationMenuWidgetPage.selectCustomNavigationMenu(
				navigationMenuName
			);

			await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

			await expect(
				page.getByRole('menuitem', {name: urlMenuItemName})
			).toBeVisible();

			await page.getByRole('button', {name: 'Edit'}).click();

			await page.getByRole('menuitem', {name: 'Edit'}).click();

			await page.waitForTimeout(1500);

			expect(page.url()).toContain(PORTLET_URLS.navigationMenus);
		}
		finally {
			await deleteGlobalNavigationMenus(
				navigationMenusPage,
				productMenuPage
			);
		}
	}
);

test(
	'Hide Pages',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		page,
		pageConfigurationPage,
		pagesAdminPage,
		site,
		widgetPagePage,
	}) => {
		const layoutName1 = getRandomString();

		const layoutName2 = getRandomString();

		const layout1 = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: layoutName1,
		});

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: layoutName2,
		});

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pageConfigurationPage.goToSection(layoutName2, 'General');

		await page.getByLabel('Hidden from Menu Display').check();

		await pageConfigurationPage.save();

		await widgetPagePage.goto(layout1, site.friendlyUrlPath);

		await expect(
			page.getByRole('menuitem', {name: layoutName1})
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {name: layoutName2})
		).not.toBeVisible();
	}
);

test(
	'Navigate to Pages',
	{
		tag: '@LPD-50258',
	},
	async ({apiHelpers, page, site, widgetPagePage}) => {
		const parentLayoutName = getRandomString();

		const childLayoutName = getRandomString();

		const parentLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			title: parentLayoutName,
		});

		await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			parentLayoutId: parentLayout.layoutId,
			title: childLayoutName,
		});

		await widgetPagePage.goto(parentLayout, site.friendlyUrlPath);

		await expect(
			page.getByRole('heading', {name: parentLayoutName})
		).toBeVisible();

		await page.getByRole('menuitem', {name: parentLayoutName}).hover();

		await page.getByRole('menuitem', {name: childLayoutName}).click();

		await expect(
			page.getByRole('heading', {name: childLayoutName})
		).toBeVisible();
	}
);

test(
	'Select global Navigation Menu in normal site',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		navigationMenusPage,
		page,
		site,
		widgetPagePage,
	}) => {
		try {
			const navigationMenuName = getRandomString();

			await navigationMenusPage.addNavigationMenuToGlobalSite(
				navigationMenuName
			);

			const layoutName = getRandomString();

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: layoutName,
			});

			await widgetPagePage.goto(layout, site.friendlyUrlPath);

			await navigationMenuWidgetPage.openConfigurationModal(layoutName);

			await navigationMenuWidgetPage.selectCustomNavigationMenu(
				navigationMenuName
			);

			await navigationMenuWidgetPage.saveAndCloseConfigurationModal();
		}
		finally {
			await navigationMenusPage.gotoGlobalSiteNavigationMenuPortlet();

			await page.waitForTimeout(1500);

			await page.getByLabel('Select All Items on the Page').check();

			await page.getByRole('button', {name: 'Delete'}).click();

			await page
				.getByLabel('Delete Navigation Menus')
				.getByRole('button', {name: 'Delete'})
				.click();
		}
	}
);

test(
	'Select Navigation Menu from grandparent site',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		navigationMenusPage,
		page,
		widgetPagePage,
	}) => {
		let parentSiteId: string;

		let childSiteId: string;

		let grandchildSiteId: string;

		try {

			// Create sites

			const parentSite = await apiHelpers.headlessSite.createSite({
				name: getRandomString(),
			});

			parentSiteId = parentSite.id;

			const childSite = await apiHelpers.headlessSite.createSite({
				name: getRandomString(),
				parentSiteKey: parentSite.name,
			});

			childSiteId = childSite.id;

			const grandchildSite = await apiHelpers.headlessSite.createSite({
				name: getRandomString(),
				parentSiteKey: childSite.name,
			});

			grandchildSiteId = grandchildSite.id;

			// Add Layout to the grandchild site

			const layoutName = getRandomString();

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: grandchildSite.id,
				title: layoutName,
			});

			// Add Navigation Menu to the Parent site

			await navigationMenusPage.goto(parentSite.friendlyUrlPath);

			const navigationMenuName = getRandomString();

			await navigationMenusPage.createNavigationMenu(navigationMenuName);

			const urlItemName = getRandomString();

			await navigationMenusPage.addURLItem(urlItemName);

			await widgetPagePage.goto(layout, grandchildSite.friendlyUrlPath);

			await navigationMenuWidgetPage.openConfigurationModal(layoutName);

			await navigationMenuWidgetPage.selectCustomNavigationMenu(
				navigationMenuName
			);

			await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

			await expect(
				page.getByRole('menuitem', {name: urlItemName})
			).toBeVisible();
		}
		finally {
			await apiHelpers.headlessSite.deleteSite(grandchildSiteId);

			await apiHelpers.headlessSite.deleteSite(childSiteId);

			await apiHelpers.headlessSite.deleteSite(parentSiteId);
		}
	}
);

test(
	'Select Navigation Menu from parent Site',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		navigationMenusPage,
		page,
		site,
		widgetPagePage,
	}) => {
		let childSiteId: string;

		try {

			// Create Navigation Menu

			await navigationMenusPage.goto(site.friendlyUrlPath);

			const navigationMenuName = getRandomString();

			await navigationMenusPage.createNavigationMenu(navigationMenuName);

			// Create Navigation Menu Items

			const urlItemName = getRandomString();

			await navigationMenusPage.addURLItem(urlItemName);

			const childSiteName = getRandomString();

			const childSite = await apiHelpers.headlessSite.createSite({
				name: childSiteName,
				parentSiteKey: site.name,
			});

			childSiteId = childSite.id;

			const layoutName = getRandomString();

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				externalReferenceCode: getRandomString(),
				groupId: childSite.id,
				title: layoutName,
			});

			await widgetPagePage.goto(layout, childSite.friendlyUrlPath);

			await navigationMenuWidgetPage.openConfigurationModal(
				layout.nameCurrentValue
			);

			await navigationMenuWidgetPage.selectCustomNavigationMenu(
				navigationMenuName
			);

			await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

			await expect(page.getByText(urlItemName)).toBeVisible();
		}
		finally {
			await apiHelpers.headlessSite.deleteSite(childSiteId);
		}
	}
);

test(
	'Select page as root menu item for Navigation Menu widget',
	{
		tag: '@LPD-50258',
	},
	async ({apiHelpers, page, site, widgetPagePage}) => {
		const parentLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		const childLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			parentLayoutId: parentLayout.layoutId,
			title: getRandomString(),
		});

		await widgetPagePage.goto(parentLayout, site.friendlyUrlPath);

		await widgetPagePage.clickOnAction('Menu Display', 'Configuration');

		const configurationIFrame = page.frameLocator(
			'iframe[title*="Menu Display"]'
		);

		await configurationIFrame
			.getByLabel('Start with Menu Items In')
			.selectOption('Select Parent');

		await configurationIFrame
			.getByRole('button', {name: 'Menu Item'})
			.click();

		await configurationIFrame
			.frameLocator('iframe[title="Select Site Navigation Menu Item"]')
			.getByText('Pages Hierarchy')
			.click();
		await configurationIFrame
			.frameLocator('iframe[title="Select Site Navigation Menu Item"]')
			.getByText(parentLayout.nameCurrentValue)
			.click();

		await widgetPagePage.saveAndClose('Menu Display');

		await expect(
			page.getByRole('menuitem', {name: childLayout.nameCurrentValue})
		).toBeVisible();

		await widgetPagePage.clickOnAction('Menu Display', 'Configuration');

		await expect(
			configurationIFrame.getByText(parentLayout.nameCurrentValue)
		).toBeVisible();
	}
);

test(
	'Show more than two sublevels when select custom menu and bar minimally styled',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		navigationMenusPage,
		page,
		site,
		widgetPagePage,
	}) => {

		// Create Layouts in the same level

		const layoutName1 = getRandomString();

		const layoutName2 = getRandomString();

		const layoutName3 = getRandomString();

		const layoutName4 = getRandomString();

		const layoutNames = [layoutName2, layoutName3, layoutName4];

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			title: layoutName1,
		});

		for (let index = 0; index < 3; index++) {
			await apiHelpers.jsonWebServicesLayout.addLayout({
				externalReferenceCode: getRandomString(),
				groupId: site.id,
				title: layoutNames[index],
			});
		}

		// Create Navigation Menu

		await navigationMenusPage.goto(site.friendlyUrlPath);

		const navigationMenuName = getRandomString();

		await navigationMenusPage.createNavigationMenu(navigationMenuName);

		// Create Page Navigation Menu Items on different levels

		await navigationMenusPage.addPageItem([layoutName1]);

		await navigationMenusPage.addChildPage(layoutName1, layoutName2);

		await navigationMenusPage.addChildPage(layoutName2, layoutName3);

		await navigationMenusPage.addChildPage(layoutName3, layoutName4);

		// Select the created Navigation Menu in the Navigation Menu Widget

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await navigationMenuWidgetPage.selectCustomNavigationMenu(
			navigationMenuName
		);

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		// Assert that the Navigation Menu Items are viewable when the mouse hove over the first Page Menu Item

		await expect(
			page.getByRole('menuitem', {name: layoutName1})
		).toBeVisible();

		await page.getByRole('menuitem', {name: layoutName1}).hover();

		await expect(
			page.getByRole('menuitem', {name: layoutName2})
		).toBeVisible();

		await expect(page.getByRole('link', {name: layoutName3})).toBeVisible();

		await expect(page.getByRole('link', {name: layoutName4})).toBeVisible();
	}
);

test(
	'Show more than two sublevels when select Public Pages hierarchy and Bar Minimally Styled',
	{
		tag: '@LPD-50258',
	},
	async ({apiHelpers, page, site, widgetPagePage}) => {
		const layoutName = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			title: layoutName,
		});

		const childLayoutName = getRandomString();

		const childLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			parentLayoutId: layout.layoutId,
			title: childLayoutName,
		});

		const grandChildLayoutName = getRandomString();

		const grandChildLayout =
			await apiHelpers.jsonWebServicesLayout.addLayout({
				externalReferenceCode: getRandomString(),
				groupId: site.id,
				parentLayoutId: childLayout.layoutId,
				title: grandChildLayoutName,
			});

		const greatGrandChildLayoutName = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			parentLayoutId: grandChildLayout.layoutId,
			title: greatGrandChildLayoutName,
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await page.getByRole('menuitem', {name: layoutName}).hover();

		await page.getByText(childLayoutName).click();

		await expect(
			page.getByRole('heading', {name: childLayoutName})
		).toBeVisible();

		await page.getByRole('menuitem', {name: layoutName}).hover();

		await page.getByText(grandChildLayoutName).click();

		await expect(
			page.getByRole('heading', {name: grandChildLayoutName})
		).toBeVisible();

		await page.getByRole('menuitem', {name: layoutName}).hover();

		await page.getByText(greatGrandChildLayoutName).click();

		await expect(
			page.getByRole('heading', {name: greatGrandChildLayoutName})
		).toBeVisible();
	}
);

test(
	'Show more than two sublevels when select Public Pages hierarchy and List Menu',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		page,
		site,
		widgetPagePage,
	}) => {
		const layoutName = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			title: layoutName,
		});

		const childLayoutName = getRandomString();

		const childLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			parentLayoutId: layout.layoutId,
			title: childLayoutName,
		});

		const grandChildLayoutName = getRandomString();

		const grandChildLayout =
			await apiHelpers.jsonWebServicesLayout.addLayout({
				externalReferenceCode: getRandomString(),
				groupId: site.id,
				parentLayoutId: childLayout.layoutId,
				title: grandChildLayoutName,
			});

		const greatGrandChildLayoutName = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			parentLayoutId: grandChildLayout.layoutId,
			title: greatGrandChildLayoutName,
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await page.waitForTimeout(1500);

		await navigationMenuWidgetPage.selectDisplayTemplate('List Menu');

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		await page
			.getByLabel('Site Pages')
			.getByRole('link', {name: childLayoutName})
			.click();

		await expect(
			page.getByRole('heading', {name: childLayoutName})
		).toBeVisible();

		await page
			.getByLabel('Site Pages')
			.getByRole('link', {name: grandChildLayoutName})
			.click();

		await expect(
			page.getByRole('heading', {name: grandChildLayoutName})
		).toBeVisible();

		await page
			.getByLabel('Site Pages')
			.getByRole('link', {name: greatGrandChildLayoutName})
			.click();

		await expect(
			page.getByRole('heading', {name: greatGrandChildLayoutName})
		).toBeVisible();
	}
);

test(
	'Show the same amount of levels with the number of levels to display',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		page,
		site,
		widgetPagePage,
	}) => {
		const layoutName = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			title: layoutName,
		});

		const childLayoutName = getRandomString();

		const childLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			parentLayoutId: layout.layoutId,
			title: childLayoutName,
		});

		const grandChildLayoutName = getRandomString();

		const grandChildLayout =
			await apiHelpers.jsonWebServicesLayout.addLayout({
				externalReferenceCode: getRandomString(),
				groupId: site.id,
				parentLayoutId: childLayout.layoutId,
				title: grandChildLayoutName,
			});

		const greatGrandChildLayoutName = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			externalReferenceCode: getRandomString(),
			groupId: site.id,
			parentLayoutId: grandChildLayout.layoutId,
			title: greatGrandChildLayoutName,
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await navigationMenuWidgetPage.menuDisplayModal
			.getByLabel('Levels to Display')
			.selectOption('2');

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		await page.getByRole('menuitem', {name: layoutName}).hover();

		await expect(
			page.getByRole('menuitem', {name: layoutName})
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {name: childLayoutName})
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {name: grandChildLayoutName})
		).not.toBeVisible();

		await expect(
			page.getByRole('menuitem', {name: greatGrandChildLayoutName})
		).not.toBeVisible();
	}
);

test(
	'View custom field of Navigation Menu',
	{
		tag: '@LPD-50258',
	},
	async ({
		addCustomFieldPage,
		apiHelpers,
		navigationMenuWidgetPage,
		navigationMenusPage,
		page,
		site,
		templatesPage,
		viewAttributesPage,
		widgetPagePage,
	}) => {
		try {

			// Create Custom Field for Navigation Menu Items

			const customFieldName = 'Subtitle';

			const customField: TCustomField = {
				fieldName: customFieldName,
				fieldType: 'inputField',
				resource: 'Site Navigation Menu Item',
			};

			await addCustomFieldPage.addCustomField(customField);

			// Create layout

			const layoutName = getRandomString();

			const submenuItemName = getRandomString();

			const urlItemName = getRandomString();

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				externalReferenceCode: getRandomString(),
				groupId: site.id,
				title: layoutName,
			});

			// Create Navigation Menu

			await navigationMenusPage.goto(site.friendlyUrlPath);

			const navigationMenuName = getRandomString();

			await navigationMenusPage.createNavigationMenu(navigationMenuName);

			// Create Navigation Menu Items

			await navigationMenusPage.addPageItem([layoutName]);

			await navigationMenusPage.addSubmenuItem(submenuItemName);

			await navigationMenusPage.addURLItem(urlItemName);

			// Fill the Custom Fields of the Navigation Menu Items and store its value

			const value1 =
				await navigationMenusPage.fillNavigationMenuItemCustomField(
					layoutName,
					customFieldName
				);

			const value2 =
				await navigationMenusPage.fillNavigationMenuItemCustomField(
					submenuItemName,
					customFieldName
				);

			const value3 =
				await navigationMenusPage.fillNavigationMenuItemCustomField(
					urlItemName,
					customFieldName
				);

			// Create Widget Template with a custom template script

			await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);

			const widgetTemplateName = getRandomString();

			await templatesPage.createWidgetTemplate(
				widgetTemplateName,
				'Menu Display Template'
			);

			await templatesPage.editTemplate(widgetTemplateName);

			await templatesPage.importInformationTemplate(
				__dirname,
				'custom_field_template.ftl'
			);

			await templatesPage.saveTemplate(widgetTemplateName);

			// Use the created Navigation Menu in the Navigation Menu Widget and apply the created Widget Template

			await widgetPagePage.goto(layout, site.friendlyUrlPath);

			await navigationMenuWidgetPage.openConfigurationModal(
				layout.nameCurrentValue
			);

			await navigationMenuWidgetPage.selectCustomNavigationMenu(
				navigationMenuName
			);

			await navigationMenuWidgetPage.selectDisplayTemplate(
				widgetTemplateName
			);

			await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

			// Verify that the changes where applied

			await expect.soft(page.getByText(value1)).toBeVisible();

			await expect.soft(page.getByText(value2)).toBeVisible();

			await expect.soft(page.getByText(value3)).toBeVisible();
		}
		finally {
			await viewAttributesPage.goto('Site Navigation Menu Item');

			await page.getByLabel('Select All Items on the Page').check();

			await page.getByRole('button', {name: 'Delete'}).click();
		}
	}
);

test(
	'View default selected navigation',
	{
		tag: '@LPD-50258',
	},
	async ({apiHelpers, navigationMenuWidgetPage, site, widgetPagePage}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await expect(
			navigationMenuWidgetPage.menuDisplayModal.getByLabel(
				'Select Navigation'
			)
		).toBeChecked();

		await expect(
			navigationMenuWidgetPage.navigationSelector.locator(
				'option:checked'
			)
		).toHaveText('Pages Hierarchy');
	}
);

test(
	'View warning message in preview window when no navigation available',
	{
		tag: '@LPD-50258',
	},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		navigationMenuWidgetPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const className =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				'com.liferay.journal.model.JournalArticle'
			);

		const displayPageTemplateName = getRandomString();

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
			{
				classNameId: className.classNameId,
				classTypeId: String(
					await getBasicWebContentStructureId(apiHelpers)
				),
				groupId: site.id,
				name: displayPageTemplateName,
			}
		);

		displayPageTemplatesPage.goto(site.friendlyUrlPath);

		displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addWidget('Content Management', 'Menu Display');

		await page
			.locator('ul')
			.filter({hasText: 'Menu Display'})
			.getByLabel('Options')
			.click();

		await page
			.getByRole('menuitem', {exact: true, name: 'Configuration'})
			.click();

		await expect(
			navigationMenuWidgetPage.menuDisplayModal.getByLabel(
				'Select Navigation'
			)
		).toBeChecked();

		await navigationMenuWidgetPage.navigationSelector.selectOption('1');

		await expect(
			navigationMenuWidgetPage.menuDisplayModal.getByRole('alert')
		).toHaveText(
			'Warning:There is no Primary Navigation available for the current site.'
		);

		await page.waitForTimeout(300);

		await navigationMenuWidgetPage.navigationSelector.selectOption('2');

		await expect(
			navigationMenuWidgetPage.menuDisplayModal.getByRole('alert')
		).toHaveText(
			'Warning:There is no Secondary Navigation available for the current site.'
		);

		await page.waitForTimeout(300);

		await navigationMenuWidgetPage.navigationSelector.selectOption('3');

		await expect(
			navigationMenuWidgetPage.menuDisplayModal.getByRole('alert')
		).toHaveText(
			'Warning:There is no Social Navigation available for the current site.'
		);
	}
);

test(
	'Ensure nested submenu with browsable element is displayed',
	{
		tag: '@LPD-64079',
	},
	async ({
		apiHelpers,
		navigationMenuWidgetPage,
		navigationMenusPage,
		page,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await navigationMenusPage.goto(site.friendlyUrlPath);

		const navigationMenuName = getRandomString();

		await navigationMenusPage.createNavigationMenu(navigationMenuName);

		const parentSubmenuItemName = getRandomString();

		await navigationMenusPage.addSubmenuItem(parentSubmenuItemName);

		const childSubmenuItemName = getRandomString();

		await navigationMenusPage.addSubmenuItem(
			childSubmenuItemName,
			parentSubmenuItemName
		);

		const urlItemName = getRandomString();

		await navigationMenusPage.addURLItem(urlItemName);

		const source = page.getByRole('button', {name: 'Move ' + urlItemName});
		const target = page
			.locator('.site_navigation_menu_editor_MenuItem')
			.nth(1);

		const targetRect = await target.evaluate((element) =>
			element.getBoundingClientRect()
		);

		await source.hover();
		await page.mouse.down();
		await page.mouse.move(targetRect.right - 1, targetRect.bottom - 1);
		await page.mouse.up();

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await navigationMenuWidgetPage.openConfigurationModal(
			layout.nameCurrentValue
		);

		await navigationMenuWidgetPage.selectCustomNavigationMenu(
			navigationMenuName
		);

		await navigationMenuWidgetPage.saveAndCloseConfigurationModal();

		await page.getByRole('menuitem', {name: parentSubmenuItemName}).hover();

		await expect(page.getByRole('link', {name: urlItemName})).toBeVisible();
	}
);
