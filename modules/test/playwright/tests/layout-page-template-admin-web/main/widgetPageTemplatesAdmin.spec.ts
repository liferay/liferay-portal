/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {widgetPageTemplatesPagesTest} from '../../../fixtures/widgetPageTemplatesPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';

export const test = mergeTests(
	featureFlagsTest({
		'LPD-76864': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageViewModePagesTest,
	pagesAdminPagesTest,
	widgetPageTemplatesPagesTest
);

test('Add, rename and delete a page template in global site', async ({
	page,
	widgetPageTemplatesPage,
}) => {

	// Go to page template administration in global site

	await widgetPageTemplatesPage.goto();

	// Create global page template

	const widgetPageTemplateName = getRandomString();

	await widgetPageTemplatesPage.addGlobalWidgetPageTemplate(
		widgetPageTemplateName
	);

	await expect(
		page.getByText(widgetPageTemplateName, {exact: true})
	).toBeVisible();

	// Rename global page template

	const newWidgetPageTemplateName = getRandomString();

	await widgetPageTemplatesPage.renameGlobalWidgetPageTemplate(
		newWidgetPageTemplateName,
		widgetPageTemplateName
	);

	await expect(
		page.getByText(newWidgetPageTemplateName, {exact: true})
	).toBeVisible();

	// Delete global page template

	await widgetPageTemplatesPage.delete(newWidgetPageTemplateName);

	await expect(
		page.getByText(newWidgetPageTemplateName, {exact: true})
	).not.toBeVisible();
});

test('Add an active page template in global site and deactivate it', async ({
	page,
	pagesAdminPage,
	site,
	widgetPageTemplatesPage,
}) => {

	// Go to page template administration in global site

	await widgetPageTemplatesPage.goto();

	// Create global page template

	const widgetPageTemplateName = getRandomString();

	await widgetPageTemplatesPage.addGlobalWidgetPageTemplate(
		widgetPageTemplateName
	);

	// Check global page template is present in page creation

	await pagesAdminPage.goto(site.friendlyUrlPath);

	await pagesAdminPage.gotoSelectTemplates('Global Templates');

	await expect(
		page.getByText(widgetPageTemplateName, {exact: true})
	).toBeVisible();

	// Disable global page template

	await widgetPageTemplatesPage.goto();

	await widgetPageTemplatesPage.deactivateGlobalWidgetPageTemplate(
		widgetPageTemplateName
	);

	// Check global page template is not present in page creation

	await pagesAdminPage.goto(site.friendlyUrlPath);

	await pagesAdminPage.gotoSelectTemplates('Global Templates');

	await expect(
		page.getByText(widgetPageTemplateName, {exact: true})
	).not.toBeVisible();
});

test(
	'Disable inherit changes and check it works',
	{
		tag: ['@LPS-54099', '@LPS-145264', '@LPS-154130'],
	},
	async ({
		page,
		pagesAdminPage,
		site,
		widgetPagePage,
		widgetPageTemplatesPage,
	}) => {

		// Go to page template administration in global site

		await widgetPageTemplatesPage.goto();

		// Assert portlet title

		await expect(
			page.getByRole('heading', {name: 'Widget Page Templates'})
		).toBeVisible();

		// Create global page template

		const widgetPageTemplateName = getRandomString();

		await widgetPageTemplatesPage.addGlobalWidgetPageTemplate(
			widgetPageTemplateName
		);

		// Create a new page based in global page template

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.gotoSelectTemplates('Global Templates');

		const layoutTitle = getRandomString();

		await pagesAdminPage.addPage({
			name: layoutTitle,
			template: widgetPageTemplateName,
		});

		// Disable inherit changes

		await page.getByLabel('Inherit Changes').uncheck();

		await pagesAdminPage.saveConfiguration();

		// Edit global page template

		await widgetPageTemplatesPage.goto();

		await widgetPageTemplatesPage.clickMoreActions(
			widgetPageTemplateName,
			'Edit'
		);

		await widgetPagePage.addPortlet('Language Selector');

		// Assert portlet can be configured

		await widgetPagePage.clickOnAction(
			'Language Selector',
			'Configuration'
		);

		const configurationIFrame = page.frameLocator(
			'iframe[title*="Language Selector"]'
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: configurationIFrame.getByRole('option', {
				exact: true,
				name: 'Long Text',
			}),
			trigger: configurationIFrame.getByLabel('Display Template'),
		});

		await widgetPagePage.saveAndClose('Language Selector');

		// Assert changes are not inherited

		await page.goto(`/web${site.friendlyUrlPath}/${layoutTitle}`);

		await expect(
			page.getByRole('heading', {name: layoutTitle})
		).toBeAttached();

		await expect(
			page
				.locator('#layout-column_column-1')
				.getByRole('heading', {name: 'Language Selector'})
		).not.toBeVisible();

		// Enable inherit changes

		page.on('dialog', async (dialog) => {
			await dialog.accept();
		});

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.clickOnAction('Configure', layoutTitle);

		await page.getByLabel('Inherit Changes').check();

		await pagesAdminPage.saveConfiguration();

		// Assert changes are inherited

		await page.goto(`/web${site.friendlyUrlPath}/${layoutTitle}`);

		await expect(
			page.getByRole('heading', {name: layoutTitle})
		).toBeAttached();

		await expect(
			page
				.locator('#layout-column_column-1')
				.getByRole('heading', {name: 'Language Selector'})
		).toBeVisible();
	}
);
