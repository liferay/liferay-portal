/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {breadcrumbPagesTest} from '../../../fixtures/breadcrumbPagesTest';
import {breadcrumbWidgetPagesTest} from '../../../fixtures/breadcrumbWidgetPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {templatesPageTest} from '../../template-web/main/fixtures/templatesPageTest';

export const test = mergeTests(
	apiHelpersTest,
	breadcrumbPagesTest,
	breadcrumbWidgetPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	pageViewModePagesTest,
	templatesPageTest
);

test(
	'Currently selected page in Breadcrumb widget has aria-current attribute',
	{
		tag: '@LPD-40431',
	},
	async ({breadcrumbWidgetPage, page, site}) => {
		await breadcrumbWidgetPage.addBreadcrumbPortlet(site);

		await expect(
			page.locator('.active.breadcrumb-text-truncate')
		).toHaveAttribute('aria-current', 'page');
	}
);

test('Select widget template in Breadcrumb widget configuration', async ({
	breadcrumbWidgetPage,
	page,
	site,
	templatesPage,
	widgetPagePage,
}) => {
	await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);

	const widgetTemplateName = getRandomString();

	await templatesPage.createWidgetTemplate(
		widgetTemplateName,
		'Breadcrumb Template'
	);

	await breadcrumbWidgetPage.addBreadcrumbPortlet(site);

	await widgetPagePage.clickOnAction('Breadcrumb', 'Configuration');

	const configurationIFrame = page.frameLocator(
		'iframe[title*="Breadcrumb"]'
	);

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: configurationIFrame.getByRole('option', {
			exact: true,
			name: widgetTemplateName,
		}),
		trigger: configurationIFrame.getByLabel('Display Template'),
	});

	await widgetPagePage.saveAndClose('Breadcrumb');

	await widgetPagePage.clickOnAction('Breadcrumb', 'Configuration');

	await configurationIFrame.getByLabel('Display Template').click();

	await expect(
		configurationIFrame.locator('button[aria-selected="true"]')
	).toHaveText(widgetTemplateName);
});

test(
	'Breadcrumb widget configuration remains unchanged without clicking save',
	{
		tag: '@LPS-150908',
	},
	async ({breadcrumbWidgetPage, page, site, widgetPagePage}) => {
		const layout = await breadcrumbWidgetPage.addBreadcrumbPortlet(site);

		await widgetPagePage.clickOnAction('Breadcrumb', 'Configuration');

		const configurationIFrame = page.frameLocator(
			'iframe[title*="Breadcrumb"]'
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: configurationIFrame.getByRole('option', {
				exact: true,
				name: 'Arrows',
			}),
			trigger: configurationIFrame.getByLabel('Display Template'),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.clickOnAction('Breadcrumb', 'Configuration');

		await configurationIFrame.getByLabel('Display Template').click();

		await expect(
			configurationIFrame.locator('button[aria-selected="true"]')
		).toHaveText('Horizontal');
	}
);

test('Configure Show Application in Breadcrumb widget', async ({
	apiHelpers,
	breadcrumbPage,
	breadcrumbWidgetPage,
	page,
	site,
	widgetPagePage,
}) => {
	const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
		site.id
	);
	const layout = await breadcrumbWidgetPage.addBreadcrumbPortlet(site);

	await widgetPagePage.addPortlet('Documents and Media');

	await page.getByRole('link', {name: folder.name}).click();

	await page.waitForTimeout(2000);

	await breadcrumbPage.assertBreadcrumbEntries(4, [
		site.name,
		layout.nameCurrentValue,
		'Home',
		folder.name,
	]);

	await breadcrumbPage.toggleBreadcrumbConfiguration(
		'Show Application Breadcrumb'
	);

	await breadcrumbPage.assertBreadcrumbEntries(2, [
		site.name,
		layout.nameCurrentValue,
	]);
});

test('Configure Show Current Site in Breadcrumb widget', async ({
	breadcrumbPage,
	breadcrumbWidgetPage,
	site,
}) => {
	const layout = await breadcrumbWidgetPage.addBreadcrumbPortlet(site);

	await breadcrumbPage.assertBreadcrumbEntries(2, [
		site.name,
		layout.nameCurrentValue,
	]);

	await breadcrumbPage.toggleBreadcrumbConfiguration('Show Current Site');

	await breadcrumbPage.assertBreadcrumbEntries(1, [layout.nameCurrentValue]);
});

test('Configure Show Page in Breadcrumb widget', async ({
	breadcrumbPage,
	breadcrumbWidgetPage,
	site,
}) => {
	const layout = await breadcrumbWidgetPage.addBreadcrumbPortlet(site);

	await breadcrumbPage.assertBreadcrumbEntries(2, [
		site.name,
		layout.nameCurrentValue,
	]);

	await breadcrumbPage.toggleBreadcrumbConfiguration('Show Page');

	await breadcrumbPage.assertBreadcrumbEntries(1, [site.name]);
});

test('Configure Show Parent Sites in Breadcrumb widget', async ({
	apiHelpers,
	breadcrumbPage,
	breadcrumbWidgetPage,
	site,
}) => {
	const childSite = await apiHelpers.headlessAdminSite.postSite({
		name: getRandomString(),
		parentSiteExternalReferenceCode: site.externalReferenceCode,
	});

	const layout = await breadcrumbWidgetPage.addBreadcrumbPortlet(childSite);

	await breadcrumbPage.assertBreadcrumbEntries(3, [
		site.name,
		childSite.name,
		layout.nameCurrentValue,
	]);

	await breadcrumbPage.toggleBreadcrumbConfiguration('Show Parent Sites');

	await breadcrumbPage.assertBreadcrumbEntries(2, [
		childSite.name,
		layout.nameCurrentValue,
	]);
});

test('Configure Show Guest Site in Breadcrumb widget', async ({
	breadcrumbPage,
	breadcrumbWidgetPage,
	site,
}) => {
	const layout = await breadcrumbWidgetPage.addBreadcrumbPortlet(site);

	await breadcrumbPage.assertBreadcrumbEntries(2, [
		site.name,
		layout.nameCurrentValue,
	]);

	await breadcrumbPage.toggleBreadcrumbConfiguration('Show Guest Site');

	await breadcrumbPage.assertBreadcrumbEntries(3, [
		'Liferay',
		site.name,
		layout.nameCurrentValue,
	]);
});

test('Preview pane reloads in Breadcrumb widget configuration', async ({
	breadcrumbPage,
	breadcrumbWidgetPage,
	page,
	site,
	widgetPagePage,
}) => {
	const layout = await breadcrumbWidgetPage.addBreadcrumbPortlet(site);

	await widgetPagePage.clickOnAction('Breadcrumb', 'Configuration');

	const configurationIFrame = page.frameLocator(
		'iframe[title*="Breadcrumb"]'
	);

	await page.waitForTimeout(1000);

	await breadcrumbPage.assertBreadcrumbEntries(
		2,
		[site.name, layout.nameCurrentValue],
		configurationIFrame
	);

	await configurationIFrame.getByLabel('Show Guest Site').click();

	await breadcrumbPage.assertBreadcrumbEntries(
		3,
		['Liferay', site.name, layout.nameCurrentValue],
		configurationIFrame
	);
});

test(
	'Breadcrumb widget configuration preview renders display templates like the page',
	{
		tag: '@LPD-41078',
	},
	async ({breadcrumbPage, breadcrumbWidgetPage, site, widgetPagePage}) => {
		await breadcrumbWidgetPage.addBreadcrumbPortlet(site);

		const displayTemplates = [
			{
				expectedStyles: ['40px', '23px'],
				name: 'Arrows',
				properties: ['line-height', 'padding-left'],
				selector: '.breadcrumb-arrows .entry',
			},
			{
				expectedStyles: ['inline-block', 'center'],
				name: 'Vertical',
				properties: ['display', 'text-align'],
				selector: '.breadcrumb-vertical',
			},
		];

		for (const {
			expectedStyles,
			name,
			properties,
			selector,
		} of displayTemplates) {
			const configurationIFrame =
				await breadcrumbPage.selectDisplayTemplate(name);

			await breadcrumbPage.assertBreadcrumbStyles({
				expectedStyles,
				parent: configurationIFrame,
				properties,
				selector,
			});

			await widgetPagePage.saveAndClose('Breadcrumb');

			await breadcrumbPage.assertBreadcrumbStyles({
				expectedStyles,
				properties,
				selector,
			});
		}
	}
);
