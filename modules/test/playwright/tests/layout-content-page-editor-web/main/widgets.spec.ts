/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {masterPagesPagesTest} from '../../../fixtures/masterPagesPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {performLogout} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {getObjectERC} from '../../setup/page-management-site/main/utils/getObjectERC';
import getFormContainerDefinition from './utils/getFormContainerDefinition';
import getPageDefinition from './utils/getPageDefinition';
import getWidgetDefinition from './utils/getWidgetDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-11131': {enabled: true},
		'LPD-40533': {enabled: true},
		'LPD-40534': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	masterPagesPagesTest,
	pageEditorPagesTest,
	pageManagementSiteTest,
	productMenuPageTest
);

test(
	'Can set permissions for widgets in content page',
	{tag: ['@LPD-106813']},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create page with language selector widget and go to edit mode

		const widgetId = getRandomString();

		const widgetDefinition = getWidgetDefinition({
			id: widgetId,
			widgetName:
				'com_liferay_site_navigation_language_web_portlet_SiteNavigationLanguagePortlet',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Open permissions

		await pageEditorPage.selectFragment(widgetId);

		await page
			.locator('.page-editor__topper__item')
			.getByRole('button', {name: 'Options'})
			.click();

		const dropdown = page.locator('.dropdown-menu.show');

		await dropdown.getByText('Permissions', {exact: true}).click();

		// Removes view permissions

		const permissionsIFrame = page.frameLocator(
			'iframe[title="Permissions"]'
		);

		await permissionsIFrame.locator('#guest_ACTION_VIEW').uncheck();

		await permissionsIFrame.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(permissionsIFrame);

		await page
			.locator('.modal-header')
			.getByLabel('Close', {exact: true})
			.click();

		// Publish

		await pageEditorPage.publishPage();

		// Go to view mode as guest user and assert language selector is not visible

		await performLogout(page);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(
			page
				.getByText(
					'You do not have the roles required to access this portlet.'
				)
				.first()
		).toBeVisible();
	}
);

test(
	'Check widget configuration is displayed in fragments topper and not in overlay',
	{tag: ['@LPD-32047']},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create page with Search Bar widget and go to edit mode

		const widgetId = getRandomString();

		const widgetDefinition = getWidgetDefinition({
			id: widgetId,
			widgetName:
				'com_liferay_portal_search_web_search_bar_portlet_SearchBarPortlet',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Check widget options are accesible from fragments topper

		await pageEditorPage.selectFragment(widgetId);

		await page
			.locator('.page-editor__topper__item')
			.getByRole('button', {name: 'Options'})
			.click();

		const dropdown = page.locator('.dropdown-menu.show');

		await expect(
			dropdown.getByText('Configuration', {exact: true})
		).toBeVisible();

		await expect(dropdown.getByText('Export / Import')).toBeVisible();

		await expect(
			dropdown.getByText('Configuration Templates')
		).toBeVisible();

		await expect(dropdown.getByText('Permissions')).toBeVisible();

		await clickAndExpectToBeHidden({
			target: dropdown.getByText('Permissions'),
			trigger: page.locator(
				'.page-editor__page-structure__item-configuration-tab',
				{
					hasText: 'General',
				}
			),
		});

		// Check widget configuration is not accessible from overlay

		const topper = pageEditorPage.getTopper(widgetId);

		await topper.hover();

		await expect(topper.locator('.portlet-options')).not.toBeVisible();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.page-editor__topper__bar')
				.getByLabel('Options'),
			trigger: topper,
		});
	}
);

test('It is not possible to drag a widget inside a Form Container', async ({
	apiHelpers,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Get the id of Lemon object from the site initializer

	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {className: objectDefinitionClassName} = (
		await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
			getObjectERC('Lemon')
		)
	).body;

	// Create page with Search Bar widget and a Form container

	const widgetId = getRandomString();

	const widgetDefinition = getWidgetDefinition({
		id: widgetId,
		widgetName:
			'com_liferay_portal_search_web_search_bar_portlet_SearchBarPortlet',
	});

	const formId = getRandomString();

	const formDefinition = getFormContainerDefinition({
		id: formId,
		objectDefinitionClassName,
		pageElements: [],
	});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([widgetDefinition, formDefinition]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	// Go to page editor

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Check it's not possible to drag the widget inside the form from topper

	const formDropzone = page.locator(
		'.page-editor__form .page-editor__container .page-editor__no-fragments-state__message'
	);

	await pageEditorPage.selectFragment(widgetId);

	await page.locator('.page-editor__topper__drag-icon').dragTo(formDropzone);

	const alert = page.locator('.alert');

	await expect(
		alert.getByText('Widgets cannot be placed inside a form container')
	).toBeVisible();

	await alert.getByLabel('Close').click();

	// Check it's not possible to drag the widget inside the form from the widget itself

	const widget = pageEditorPage.getFragment(widgetId);

	await widget.dragTo(formDropzone);

	await expect(
		alert.getByText('Widgets cannot be placed inside a form container')
	).toBeVisible();
});

test.describe('Menu Display Widget', () => {
	test('Checks that the Display Menu items have a role link with display style Bar With Links', async ({
		apiHelpers,
		page,
		site,
	}) => {
		const widgetDefinition = getWidgetDefinition({
			id: getRandomString(),
			widgetConfig: {
				displayStyle: 'ddmTemplate_NAVBAR-LINKS-FTL',
			},
			widgetName:
				'com_liferay_site_navigation_menu_web_portlet_SiteNavigationMenuPortlet',
		});

		// Create three pages, one of them with Menu Display widget

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: 'First page',
		});

		const secondLayout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: 'Second page',
		});

		const thirdLayout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			parentSitePage: {
				friendlyUrlPath: secondLayout.friendlyUrlPath,
			},
			siteId: site.id,
			title: 'Third page',
		});

		await page.goto(
			`/web${site.friendlyUrlPath}${thirdLayout.friendlyUrlPath}`
		);

		// Check that the Display Menu items have a role link

		await page.getByRole('link', {name: 'Second page'}).hover();

		await expect(
			page.getByRole('link', {name: 'First page'})
		).toBeVisible();
		await expect(
			page.getByRole('link', {name: 'Second page'})
		).toBeVisible();
		await expect(
			page.getByRole('link', {name: 'Third page'})
		).toBeVisible();
	});
});

test(
	'Check that the Sharing, Supported Clients and Scope features have the deprecation badge',
	{
		tag: ['@LPD-41722', '@LPD-41723', '@LPD-45441'],
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page with a Web Content Display Widget

		const widgetId = getRandomString();

		const widgetDefinition = getWidgetDefinition({
			id: widgetId,
			widgetName:
				'com_liferay_journal_content_web_portlet_JournalContentPortlet',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Access to the widget configuration

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.selectFragment(widgetId);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.dropdown-menu.show')
				.getByText('Configuration', {exact: true}),
			trigger: page
				.locator('.page-editor__topper__item')
				.getByRole('button', {name: 'Options'}),
		});

		// Check the deprecated badges

		const configurationIFrame = page.frameLocator(
			'iframe[title="Configuration"]'
		);

		const navigationItem = configurationIFrame.getByRole('link');

		await expect(navigationItem.filter({hasText: 'Sharing'})).toContainText(
			/Deprecated/
		);

		await expect(
			navigationItem.filter({hasText: 'Supported Clients'})
		).toContainText(/Deprecated/);

		await expect(navigationItem.filter({hasText: 'Scope'})).toContainText(
			/Deprecated/
		);
	}
);

test(
	'Widgets inherited from custom master will inherit permissions set in custom master',
	{
		tag: '@LPS-106813',
	},
	async ({apiHelpers, masterPagesPage, page, pageEditorPage, site}) => {

		// Add master page

		const layoutPageTemplateEntryName = getRandomString();

		const masterPage =
			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
				{
					groupId: site.id,
					name: layoutPageTemplateEntryName,
					type: 'master-layout',
				}
			);

		// Go to edit master page

		await masterPagesPage.goto(site.friendlyUrlPath);

		await masterPagesPage.editMaster(layoutPageTemplateEntryName);

		// Add language selector widget

		await pageEditorPage.addWidget('Tools', 'Language Selector');

		// Open permissions

		const widgetId =
			await pageEditorPage.getFragmentId('Language Selector');

		await pageEditorPage.selectFragment(widgetId);

		await page
			.locator('.page-editor__topper__item')
			.getByRole('button', {name: 'Options'})
			.click();

		const dropdown = page.locator('.dropdown-menu.show');

		await dropdown.getByText('Permissions', {exact: true}).click();

		// Removes view permissions

		const permissionsIFrame = page.frameLocator(
			'iframe[title="Permissions"]'
		);

		await permissionsIFrame.locator('#guest_ACTION_VIEW').uncheck();

		await permissionsIFrame.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(permissionsIFrame);

		await page
			.locator('.modal-header')
			.getByLabel('Close', {exact: true})
			.click();

		// Publish

		await pageEditorPage.publishPage();

		// Create a layout

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			masterLayoutPlid: masterPage.plid,
			title: getRandomString(),
		});

		// Go to view mode as guest user and assert language selector is not visible

		await performLogout(page);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await expect(
			page
				.getByText(
					'You do not have the roles required to access this portlet.'
				)
				.first()
		).toBeVisible();
	}
);

test(
	'Check that the scope dropdown in Product Menu have the deprecation badge and that the page is set as scope',
	{
		tag: ['@LPD-46225'],
	},
	async ({apiHelpers, page, pageEditorPage, productMenuPage, site}) => {

		// Create a page with a Web Content Display Widget

		const widgetId = getRandomString();

		const widgetDefinition = getWidgetDefinition({
			id: widgetId,
			widgetName:
				'com_liferay_journal_content_web_portlet_JournalContentPortlet',
		});

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: layoutTitle,
		});

		// Access to the widget configuration

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToWidgetConfiguration(widgetId);

		// Create a new scope and publish the page

		const configurationIFrame = page.frameLocator(
			'iframe[title="Configuration"]'
		);

		await configurationIFrame.getByRole('link', {name: 'Scope'}).click();

		await configurationIFrame
			.getByLabel('Scope', {exact: true})
			.selectOption(layoutTitle + ' (Create New)');

		await configurationIFrame.getByRole('button', {name: 'Save'}).click();

		await page
			.locator('.modal-header')
			.getByLabel('Close', {exact: true})
			.click();

		await pageEditorPage.publishPage();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		// Check the label in the dropdown at the Product Menu

		await productMenuPage.openProductMenuButton.click();

		await productMenuPage.contentAndDataButton.click();

		// Open the dropdown to select the scope

		const dropdownButton = page.getByLabel('Choose Scope');

		const dropdownOption = page
			.locator('.dropdown-menu')
			.getByRole('menuitem', {name: layoutTitle});

		await expect(async () => {
			await dropdownButton.click({timeout: 2000});

			await expect(dropdownOption).toBeVisible({timeout: 1000});
			await expect(dropdownOption).toContainText('deprecated');

			await dropdownOption.click({timeout: 2000});

			await expect(
				page.getByText(`${layoutTitle} (Scope) deprecated`)
			).toBeVisible({timeout: 2000});
		}).toPass();

		// Check that the page is set as scope

	}
);
