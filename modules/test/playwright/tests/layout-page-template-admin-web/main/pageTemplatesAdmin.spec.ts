/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageTemplatesPagesTest} from '../../../fixtures/pageTemplatesPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {zipFolder} from '../../../utils/zip';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

export const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-76864': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pagesAdminPagesTest,
	pageEditorPagesTest,
	pageTemplatesPagesTest
);

test.describe('Convert content pages', () => {
	test(
		'Can add a page template set during convert to page template when the site have a page template set',
		{
			tag: ['@LPS-140483', '@LPS-166207'],
		},
		async ({apiHelpers, page, pageEditorPage, site}) => {

			// Creates a page template collection

			const layoutPageTemplateCollectionName = getRandomString();

			await apiHelpers.jsonWebServicesLayoutPageTemplateCollection.addLayoutPageTemplateCollection(
				{
					groupId: site.id,
					name: layoutPageTemplateCollectionName,
				}
			);

			// Creates a content page

			const headingId = getRandomString();

			const headingFragmentDefinition = getFragmentDefinition({
				id: headingId,
				key: 'BASIC_COMPONENT-heading',
			});

			const layoutTitle = getRandomString();

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([headingFragmentDefinition]),
				siteId: site.id,
				title: layoutTitle,
			});

			// Go to edit mode of page and convert to page template

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await pageEditorPage.clickPageAction('Convert to Page Template');

			// Save in a new page template set

			await page
				.getByRole('combobox', {name: 'Page Template Set'})
				.selectOption({label: layoutPageTemplateCollectionName});

			await page
				.getByRole('dialog')
				.getByRole('button', {exact: true, name: 'Save'})
				.click();

			await waitForAlert(
				page,
				'The page template was created successfully. You can view it here: See in Page Templates.',
				{autoClose: false}
			);

			// Assert page template where created correctly

			await page
				.getByRole('link', {name: 'See in Page Templates'})
				.click();

			const pageTemplateName = `${layoutTitle} - Page Template`;

			const card = page
				.locator('.card-type-asset')
				.filter({hasText: pageTemplateName});

			await expect(card.getByText('Draft')).toBeVisible();
		}
	);

	test(
		'Can add a page template set during convert to page template when the site does not have a page template set',
		{
			tag: ['@LPS-140483', '@LPS-166207'],
		},
		async ({apiHelpers, page, pageEditorPage, pageTemplatesPage, site}) => {

			// Creates a content page

			const headingId = getRandomString();

			const headingFragmentDefinition = getFragmentDefinition({
				id: headingId,
				key: 'BASIC_COMPONENT-heading',
			});

			const widgetId = getRandomString();

			const widgetDefinition = getWidgetDefinition({
				id: widgetId,
				widgetName:
					'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet',
			});

			const layoutTitle = getRandomString();

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					headingFragmentDefinition,
					widgetDefinition,
				]),
				siteId: site.id,
				title: layoutTitle,
			});

			// Go to edit mode of page

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			// Edit translation

			await pageEditorPage.switchLanguage('es-ES');

			await pageEditorPage.editTextEditable(
				headingId,
				'element-text',
				'Texto Editado'
			);

			// Convert to page template

			await pageEditorPage.clickPageAction('Convert to Page Template');

			// Save page template

			await page
				.getByRole('dialog')
				.getByRole('button', {exact: true, name: 'Save'})
				.click();

			await waitForAlert(
				page,
				'The page template was created successfully. You can view it here: See in Page Templates.'
			);

			// Assert page template where created correctly

			await pageTemplatesPage.goto(site.friendlyUrlPath);

			await expect(
				page.getByRole('heading', {name: 'Untitled Set'})
			).toBeVisible();

			const pageTemplateName = `${layoutTitle} - Page Template`;

			const card = page
				.locator('.card-type-asset')
				.filter({hasText: pageTemplateName});

			await expect(card.getByText('Draft')).toBeVisible();

			// Edit page template

			await page
				.getByRole('link', {exact: true, name: pageTemplateName})
				.click();

			// Assert page template elements

			await expect(page.getByText('Heading Example')).toBeVisible();

			// Assert page template translations

			await expect(
				page.locator('.portlet-asset-publisher')
			).toBeVisible();

			await pageEditorPage.switchLanguage('es-ES');

			await expect(page.getByText('Texto Editado')).toBeVisible();
		}
	);
});

test.describe('General', () => {
	test('Add, rename and delete a content page template', async ({
		page,
		pageTemplatesPage,
		site,
	}) => {

		// Go to page template administration

		await pageTemplatesPage.goto(site.friendlyUrlPath);

		// Create page template collection

		const pageTemplateCollectionName = getRandomString();

		await pageTemplatesPage.addPageTemplateCollection(
			pageTemplateCollectionName
		);

		// Create content page template

		const pageTemplateName = getRandomString();

		await pageTemplatesPage.addContentPageTemplate(pageTemplateName);

		// Assert content page template

		await pageTemplatesPage.goto(site.friendlyUrlPath);

		await expect(
			page.getByRole('link', {exact: true, name: pageTemplateName})
		).toBeVisible();

		// Change thumbnail

		const fileChooserPromise = page.waitForEvent('filechooser');

		await pageTemplatesPage.clickAction(
			'Change Thumbnail',
			pageTemplateName
		);

		const iframe = page.frameLocator(
			'iframe[title="Page Template Thumbnail"]'
		);

		await expect(
			iframe.getByText('Drag & Drop Your Files or Browse to Upload')
		).toBeVisible();

		await iframe
			.getByText('Drag & Drop Your Files or Browse to Upload')
			.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			path.join(__dirname, '/dependencies/image.jpg')
		);

		await iframe.getByRole('button', {exact: true, name: 'Add'}).click();

		await expect(
			page
				.locator('.card-type-asset')
				.filter({hasText: pageTemplateName})
				.locator('img')
		).toBeAttached();

		// Rename content page template

		await pageTemplatesPage.clickAction('Rename', pageTemplateName);

		const newPageTemplateName = getRandomString();

		await page
			.getByPlaceholder('Name', {exact: true})
			.fill(newPageTemplateName);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page);

		await expect(
			page.getByRole('link', {exact: true, name: newPageTemplateName})
		).toBeVisible();

		// Delete content page template

		await pageTemplatesPage.deletePageTemplate(newPageTemplateName);

		await expect(
			page.getByRole('link', {exact: true, name: newPageTemplateName})
		).not.toBeVisible();
	});

	test('Add and delete a widget page template', async ({
		page,
		pageTemplatesPage,
		site,
	}) => {

		// Go to page template administration in global site

		await pageTemplatesPage.goto(site.friendlyUrlPath);

		// Create page template collection

		const pageTemplateCollectionName = getRandomString();

		await pageTemplatesPage.addPageTemplateCollection(
			pageTemplateCollectionName
		);

		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: pageTemplateCollectionName,
			})
		).toBeVisible();

		// Create widget page template

		const pageTemplateName = getRandomString();

		await pageTemplatesPage.addWidgetPageTemplate(pageTemplateName);

		// Assert page template

		await pageTemplatesPage.goto(site.friendlyUrlPath);

		await expect(
			page.getByRole('link', {exact: true, name: pageTemplateName})
		).toBeVisible();

		// Delete page template

		await pageTemplatesPage.deletePageTemplate(pageTemplateName);

		await expect(
			page.getByRole('link', {exact: true, name: pageTemplateName})
		).not.toBeVisible();
	});

	test('Add, rename and delete a page template collection', async ({
		page,
		pageTemplatesPage,
		site,
	}) => {

		// Go to page template administration in global site

		await pageTemplatesPage.goto(site.friendlyUrlPath);

		// Create page template collection

		const pageTemplateCollectionName = getRandomString();

		await pageTemplatesPage.addPageTemplateCollection(
			pageTemplateCollectionName
		);

		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: pageTemplateCollectionName,
			})
		).toBeVisible();

		// Rename page template collection

		await pageTemplatesPage.clickPageTemplateCollectionAction(
			'Edit',
			pageTemplateCollectionName
		);

		const newPageTemplateCollectionName = getRandomString();

		await page.getByLabel('Name').fill(newPageTemplateCollectionName);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page);

		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: newPageTemplateCollectionName,
			})
		).toBeVisible();

		// Delete page template collection

		await pageTemplatesPage.deletePageTemplateCollection(
			newPageTemplateCollectionName
		);

		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: newPageTemplateCollectionName,
			})
		).not.toBeVisible();
	});

	test('Create a page based on a page template', async ({
		page,
		pageEditorPage,
		pageTemplatesPage,
		pagesAdminPage,
		site,
	}) => {

		// Go to page template administration

		await pageTemplatesPage.goto(site.friendlyUrlPath);

		// Create page template collection

		const pageTemplateCollectionName = getRandomString();

		await pageTemplatesPage.addPageTemplateCollection(
			pageTemplateCollectionName
		);

		// Create content page template

		const pageTemplateName = getRandomString();

		await pageTemplatesPage.addContentPageTemplate(pageTemplateName);

		// Add heading fragment and publish

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		const headingId = await pageEditorPage.getFragmentId('Heading');

		await pageEditorPage.editTextEditable(
			headingId,
			'element-text',
			'Edited'
		);

		await pageEditorPage.publishButton.click();

		await waitForAlert(
			page,
			'Success:The page template was published successfully.'
		);

		// Add a new content page base on content page template

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.gotoSelectTemplates(pageTemplateCollectionName);

		const layoutTitle = getRandomString();

		await pagesAdminPage.addPage({
			name: layoutTitle,
			template: pageTemplateName,
		});

		// Assert new content page in view mode

		await page.goto(`/web${site.friendlyUrlPath}/${layoutTitle}`);

		await expect(page.getByText('Edited')).toBeVisible();
	});
});

test.describe('Import page templates', () => {
	test(
		'Import content page templates works when success and error occurs',
		{
			tag: '@LPS-173150',
		},
		async ({page, pageTemplatesPage, site}) => {

			// Go to page templates administration

			await pageTemplatesPage.goto(site.friendlyUrlPath);

			// Open import view

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page
					.locator('.dropdown-menu')
					.getByRole('menuitem', {name: 'Import'}),
				trigger: page
					.locator('.control-menu-nav-item')
					.getByLabel('Options', {exact: true}),
			});

			// Import master page

			const fileChooserPromise = page.waitForEvent('filechooser');

			await page
				.getByRole('button', {exact: true, name: 'Select File'})
				.click();

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles(
				await zipFolder(
					path.join(
						__dirname,
						'/dependencies/page-templates-with-invalid-value.zip'
					)
				)
			);

			await page
				.getByText('page-templates-with-invalid-value.zip')
				.waitFor();

			await expect(
				page.getByRole('button', {name: 'Replace File'})
			).toBeVisible();

			await page.getByRole('button', {name: 'Import'}).click();

			// Assert error and success messages

			await expect(
				page.getByRole('button', {name: '1 item was imported.'})
			).toBeVisible();

			await expect(
				page.getByText('1 item could not be imported.', {exact: true})
			).toBeVisible();

			// Assert imported entries

			await pageTemplatesPage.goto(site.friendlyUrlPath);

			await expect(
				page.getByRole('link', {
					name: 'Content Page Template With Edited Inline Text',
				})
			).toBeVisible();

			await expect(
				page.getByRole('link', {
					name: 'Content Page Template With Invalid Value',
				})
			).not.toBeVisible();
		}
	);
});
