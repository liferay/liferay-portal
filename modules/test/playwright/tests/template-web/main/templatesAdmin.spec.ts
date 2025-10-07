/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'node:path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {liferayConfig} from '../../../liferay.config';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import fillAndClickOutside from '../../../utils/fillAndClickOutside';
import getGlobalSite from '../../../utils/getGlobalSite';
import getRandomString from '../../../utils/getRandomString';
import {data} from './dependencies/payload_page_with_21_breadcrumbs';
import {templatesPageTest} from './fixtures/templatesPageTest';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	featureFlagsTest({
		'LPD-35443': {enabled: true},
	}),
	loginTest(),
	templatesPageTest,
	pageViewModePagesTest
);

test(
	'Add an information template via script file',
	{
		tag: '@LPS-124478',
	},
	async ({page, site, templatesPage}) => {

		// Go to templates administration

		await templatesPage.goto(site.friendlyUrlPath);

		// Create information template

		const informationTemplateName = getRandomString();

		await templatesPage.createInformationTemplate({
			itemType: 'Blogs Entry',
			name: informationTemplateName,
		});

		// Import from script file

		await templatesPage.importInformationTemplate(
			__dirname,
			'information_template_blogs.ftl'
		);

		await templatesPage.saveTemplate(informationTemplateName);

		// View the script content is shown in code mirror

		await templatesPage.editTemplate(informationTemplateName);

		await expect(page.locator('.ddm_template_editor__App')).toContainText(
			'coverImage.getData()'
		);
		await expect(page.locator('.ddm_template_editor__App')).toContainText(
			'description.getData()'
		);
		await expect(page.locator('.ddm_template_editor__App')).toContainText(
			'displayDate.getData()'
		);
		await expect(page.locator('.ddm_template_editor__App')).toContainText(
			'title.getData()'
		);
	}
);

test(
	'Can add, copy and delete an information template',
	{
		tag: '@LPS-124478',
	},
	async ({page, site, templatesPage}) => {

		// Go to templates administration

		await templatesPage.goto(site.friendlyUrlPath);

		// Create information template

		const informationTemplateName = getRandomString();

		await templatesPage.createInformationTemplate({
			itemSubtype: 'Basic Web Content',
			itemType: 'Web Content Article',
			name: informationTemplateName,
		});

		// Copy information template

		await templatesPage.goto(site.friendlyUrlPath);
		await templatesPage.copyInformationTemplate(informationTemplateName);

		await expect(
			page.getByRole('link', {
				exact: true,
				name: `${informationTemplateName} (Copy)`,
			})
		).toBeVisible();

		// Delete information template

		await templatesPage.deleteInformationTemplate(
			`${informationTemplateName} (Copy)`
		);

		await expect(
			page.getByRole('link', {
				exact: true,
				name: `${informationTemplateName} (Copy)`,
			})
		).not.toBeVisible();
	}
);

test(
	'Edit an information template',
	{
		tag: '@LPS-124478',
	},
	async ({page, site, templatesPage}) => {

		// Go to templates administration

		await templatesPage.goto(site.friendlyUrlPath);

		// Create information template

		const informationTemplateName = getRandomString();

		await templatesPage.createInformationTemplate({
			itemSubtype: 'Basic Web Content',
			itemType: 'Web Content Article',
			name: informationTemplateName,
		});

		// Add title and description to script

		await page.getByRole('button', {name: 'Title'}).click();
		await page.getByRole('button', {name: 'Description'}).click();

		// Check properties tab

		await page.getByLabel('Properties').click();

		await expect(
			page.getByText('Web Content Article', {exact: true})
		).toBeVisible();

		await expect(
			page.locator('p').filter({hasText: 'Basic Web Content'})
		).toBeVisible();

		await expect(page.getByLabel('Template Key')).toBeVisible();
		await expect(page.getByLabel('URL', {exact: true})).toBeVisible();

		await expect(
			page.getByLabel('WebDAV URL', {exact: true})
		).toBeVisible();

		// Save information template

		await templatesPage.saveTemplate(informationTemplateName);

		// View the script content is shown in code mirror

		await templatesPage.editTemplate(informationTemplateName);

		await expect(page.locator('.ddm_template_editor__App')).toContainText(
			'${JournalArticle_title.getData()}'
		);
		await expect(page.locator('.ddm_template_editor__App')).toContainText(
			'${JournalArticle_description.getData()}'
		);
	}
);

test(
	'Edit a widget template',
	{
		tag: '@LPS-137903',
	},
	async ({page, site, templatesPage}) => {
		const elements = [
			'Asset Entries*',
			'Asset Entry',
			'Asset Publisher Helper',
			'Current URL',
			'HTTP Request',
			'Locale',
			'Portlet Preferences',
			'Render Request',
			'Render Response',
			'Template ID',
			'Theme Display',
		];

		// Go to widget templates administration

		await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);

		// Create widget template

		const widgetTemplateName = getRandomString();

		await templatesPage.createWidgetTemplate(
			widgetTemplateName,
			'Asset Publisher Template'
		);

		// Edit widget template

		await templatesPage.editTemplate(widgetTemplateName);

		// Assert elements

		for (const element of elements) {
			await expect(page.getByLabel(element)).toBeVisible();
		}

		// Check properties tab

		await page.getByLabel('Properties').click();

		// Assert properties

		await expect(page.getByLabel('Template Key')).toBeVisible();

		await expect(page.getByLabel('URL', {exact: true})).toBeVisible();

		await expect(
			page.getByLabel('WebDAV URL', {exact: true})
		).toBeVisible();

		await expect(
			page.getByTitle('small-image-source', {exact: true})
		).toBeVisible();
	}
);

test(
	'View usages of widget templates',
	{
		tag: '@LPS-169118',
	},
	async ({apiHelpers, page, site, templatesPage, widgetPagePage}) => {

		// Create a page

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		// Go to widget templates administration

		await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);

		// Create widget template

		const widgetTemplateName = getRandomString();

		await templatesPage.createWidgetTemplate(
			widgetTemplateName,
			'Language Selector Template'
		);

		// Go to page

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		// Add an language selector widget configured with new widget template

		await widgetPagePage.addPortlet('Language Selector');

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
				name: widgetTemplateName,
			}),
			trigger: configurationIFrame.getByLabel('Display Template'),
		});

		await widgetPagePage.saveAndClose('Language Selector');

		// Assert usages

		await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);

		await expect(
			page
				.locator('tr')
				.filter({hasText: widgetTemplateName})
				.locator('.lfr-usages-column')
		).toHaveText('1');

		// Assert delete message clicking on dropdown delete action

		await templatesPage.clickAction('Delete', widgetTemplateName);

		await expect(
			page.getByText(
				'This template is being used in 1 pages. Are you sure you want to delete this? It will be deleted immediately.'
			)
		).toBeVisible();

		await page.getByRole('button', {name: 'Cancel'}).click();

		// Assert delete message clicking on management toolbar action

		await page.getByLabel('Select All Items on the Page').check();

		await page.getByRole('button', {name: 'Delete'}).click();

		await expect(
			page.getByText(
				'Some of these templates are being used in pages. Are you sure you want to delete this? It will be deleted immediately.'
			)
		).toBeVisible();
	}
);

test(
	'Pagination of View usages of widget templates',
	{tag: '@LPD-64473'},
	async ({apiHelpers, page, site, templatesPage}) => {
		await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);

		const breadcrumbWidgetTemplateName = getRandomString();

		await templatesPage.createWidgetTemplate(
			breadcrumbWidgetTemplateName,
			'Breadcrumb Template'
		);
		await templatesPage.editTemplate(breadcrumbWidgetTemplateName);
		await templatesPage.importInformationTemplate(
			__dirname,
			'information_template_breadcrumbs.ftl'
		);

		const breadcrumbWidgetTemplateKey =
			await templatesPage.getTemplateKey();

		await templatesPage.saveTemplate(breadcrumbWidgetTemplateName);

		// Payload to create a page with 21 breadcrumbs using the Horizontal Widget Display Template created above

		const customPayload = data(
			'ddmTemplate_' + breadcrumbWidgetTemplateKey,
			site.externalReferenceCode,
			String(site.id)
		);

		const layout = await apiHelpers.headlessAdminSite.createPage(
			site.externalReferenceCode,
			customPayload
		);

		// Assert usages between page created and payload

		expect(layout.externalReferenceCode).toEqual(
			customPayload.externalReferenceCode
		);

		await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);

		// Assert number of usages

		await expect(
			page
				.locator('tr')
				.filter({hasText: breadcrumbWidgetTemplateName})
				.locator('.lfr-usages-column')
		).toHaveText('21');

		await templatesPage.goToViewUsages(breadcrumbWidgetTemplateName);

		// Testing pagination

		await page.getByRole('link', {name: 'Page 2'}).click();

		// Assert redirection is going to the right page
		// Assert page 2 headers

		const headers = page.locator('tr th');
		await expect(headers).toHaveText(['Name', 'Type', 'Instance ID']);
	}
);

test('View widget template based on script file applied and with corrupt script applied to rss publisher widget', async ({
	apiHelpers,
	page,
	site,
	templatesPage,
	widgetPagePage,
}) => {

	// Create a page

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	// Go to widget templates administration

	await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);

	// Create information template

	const widgetTemplateName = getRandomString();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('button', {name: 'More'}),
		trigger: page.getByRole('button', {name: 'New'}),
	});

	await page
		.getByRole('menuitem', {
			name: 'RSS Publisher',
		})
		.click();

	// Wait until the editor is loaded

	await page.locator('.ddm_template_editor__App').waitFor();

	await fillAndClickOutside(
		page,
		page.getByPlaceholder('Untitled Template'),
		widgetTemplateName
	);

	// Import from script file

	await templatesPage.importInformationTemplate(
		__dirname,
		'rss_publisher_corrupt_script.ftl'
	);

	await templatesPage.saveTemplate(widgetTemplateName);

	// Go to page

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	// Add rss publisher widget and open configuration

	await widgetPagePage.addPortlet('RSS Publisher');

	await widgetPagePage.clickOnAction('RSS Publisher', 'Configuration');

	const configurationIFrame = page.frameLocator(
		'iframe[title*="RSS Publisher"]'
	);

	// Add url

	await configurationIFrame
		.getByLabel('Title', {exact: true})
		.fill('LA Times: Technology News');

	const file = await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(path.join(__dirname, '/dependencies/rss2.0.xml'))
	);

	await configurationIFrame
		.getByLabel('URL')
		.fill(liferayConfig.environment.baseUrl + file.contentUrl);

	await widgetPagePage.save('RSS Publisher');

	// Configure display template

	await configurationIFrame
		.getByRole('tab', {name: 'Display Settings'})
		.click();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: configurationIFrame.getByRole('option', {
			exact: true,
			name: widgetTemplateName,
		}),
		trigger: configurationIFrame.getByLabel('Display Template'),
	});

	await widgetPagePage.saveAndClose('RSS Publisher');

	// Assert error message

	await expect(
		page.getByText('An error occurred while processing the template.')
	).toBeVisible();

	await expect(
		page.getByText('Unexpected end of file reached.')
	).toBeVisible();
});

test(
	'Check widget template is properly escaped and unescaped',
	{tag: '@LPD-62889'},
	async ({apiHelpers, page, site, templatesPage}) => {

		// Go to widget templates administration

		await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);

		// Create widget template

		const name = getRandomString();
		const content = '<!-- WRONG COMMENT LINE-- > <script ></script>';

		await templatesPage.createWidgetTemplate(
			name,
			'Asset Publisher Template',
			content
		);

		// Edit it again and check it's shown properly

		await templatesPage.editTemplate(name);

		await expect(page.getByText(content)).toBeVisible();

		// Now edit Rich Summary global template and check is properly unescaped

		const globalSite = await getGlobalSite(apiHelpers);

		await templatesPage.gotoWidgetTemplates(globalSite.friendlyURL);

		await templatesPage.editWidgetTemplate('Rich Summary');

		await expect(page.getByText('<#if').first()).toBeVisible();

		await expect(page.getByText('&#34')).not.toBeVisible();
	}
);
