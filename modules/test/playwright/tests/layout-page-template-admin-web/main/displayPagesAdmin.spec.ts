/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {performLogout} from '../../../utils/performLogin';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';

const test = mergeTests(
	blogsPagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	documentLibraryPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest
);

async function addBasicJournalArticleWithSpecificDisplayPageTemplate(
	apiHelpers: ApiHelpers,
	journalArticleTitle: string,
	layoutPageTemplateEntryName: string,
	site: Site
) {
	const className = await apiHelpers.jsonWebServicesClassName.fetchClassName(
		'com.liferay.journal.model.JournalArticle'
	);

	const contentStructureId = await getBasicWebContentStructureId(apiHelpers);

	const webContent = await apiHelpers.jsonWebServicesJournal.addWebContent({
		ddmStructureId: contentStructureId,
		groupId: site.id,
		titleMap: {en_US: journalArticleTitle},
	});

	const layoutPageTemplateEntry =
		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.fetchLayoutPageTemplateEntry(
			{
				groupId: site.id,
				name: layoutPageTemplateEntryName,
				type: 'display-page',
			}
		);

	await apiHelpers.jsonWebServicesAssetDisplayPageEntry.addAssetDisplayPageEntry(
		{
			classNameId: className.classNameId,
			classPK: String(webContent.resourcePrimKey),
			groupId: site.id,
			layoutPageTemplateEntryId:
				layoutPageTemplateEntry.layoutPageTemplateEntryId,
			type: 'specific',
		}
	);
}

async function addDefaultJournalArticleDisplayPageLayoutPageTemplateEntry(
	apiHelpers: ApiHelpers,
	contentStructureId: string,
	displayPageTemplateName: string,
	site: Site
) {
	const className = await apiHelpers.jsonWebServicesClassName.fetchClassName(
		'com.liferay.journal.model.JournalArticle'
	);

	const displayPage =
		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
			{
				classNameId: className.classNameId,
				classTypeId: contentStructureId,
				groupId: site.id,
				name: displayPageTemplateName,
			}
		);

	await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
		{
			layoutPageTemplateEntryId: displayPage.layoutPageTemplateEntryId,
		}
	);
}

test.describe('Configuration', () => {
	test(
		'User can configure header and footer in a display page',
		{
			tag: ['@LPS-86191', '@LPS-96438'],
		},
		async ({apiHelpers, displayPageTemplatesPage, page, site}) => {

			// Create a display page template for Basic Web Content and mark as default

			const contentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			const displayPageTemplateName = getRandomString();

			await addDefaultJournalArticleDisplayPageLayoutPageTemplateEntry(
				apiHelpers,
				String(contentStructureId),
				displayPageTemplateName,
				site
			);

			// Go to configuration

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.clickMoreActions(
				displayPageTemplateName,
				'Edit'
			);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page
					.locator('.dropdown-menu')
					.getByRole('menuitem', {name: 'Configure'}),
				trigger: page
					.locator('.control-menu-nav-item')
					.getByLabel('Options', {exact: true}),
			});

			// Configure theme

			await page
				.locator('nav.menubar .nav-link', {
					has: page.getByText('Design'),
				})
				.click();

			await page
				.getByLabel('Define a custom theme for this page.', {
					exact: true,
				})
				.check();

			await page.getByRole('checkbox', {name: 'Show Footer'}).uncheck();

			await page
				.getByRole('checkbox', {exact: true, name: 'Show Header'})
				.uncheck();

			await displayPageTemplatesPage.saveConfiguration();

			await page.getByTitle(`Go to ${displayPageTemplateName}`).click();

			await displayPageTemplatesPage.publishTemplate();

			// Create a Basic Web Content

			const journalArticleTitle = getRandomString();

			await apiHelpers.headlessDelivery.postStructuredContent({
				contentStructureId,
				datePublished: null,
				siteId: site.id,
				title: journalArticleTitle,
				viewableBy: 'Anyone',
			});

			// Assert header and footer are not visible

			await page.goto(
				`web${site.friendlyUrlPath}/w/${journalArticleTitle}`
			);

			await expect(page.locator('[id="banner"]')).not.toBeAttached();

			await expect(page.locator('[id="footer"]')).not.toBeAttached();
		}
	);

	test('User can map a web content to open graph meta tags in a display page', async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		site,
	}) => {

		// Create a display page template for Basic Web Content and mark as default

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const displayPageTemplateName = getRandomString();

		await addDefaultJournalArticleDisplayPageLayoutPageTemplateEntry(
			apiHelpers,
			String(contentStructureId),
			displayPageTemplateName,
			site
		);

		// Go to configuration

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		await displayPageTemplatesPage.clickMoreActions(
			displayPageTemplateName,
			'Edit'
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Configure'}),
			trigger: page
				.locator('.control-menu-nav-item')
				.getByLabel('Options', {exact: true}),
		});

		await page
			.locator('.portlet-body li', {has: page.getByText('Open Graph')})
			.click();

		// Map HTML Title

		await page.getByLabel('Title', {exact: true}).fill('');

		await displayPageTemplatesPage.mapConfiguration({
			field: 'Title',
			mappingField: 'Title',
		});

		await expect(page.getByLabel('Title', {exact: true})).toHaveValue(
			'${title:Title}'
		);

		await page.getByLabel('Description', {exact: true}).fill('');

		await displayPageTemplatesPage.mapConfiguration({
			field: 'Description',
			mappingField: 'Description',
		});

		await expect(page.getByLabel('Description', {exact: true})).toHaveValue(
			'${description:Description}'
		);

		await displayPageTemplatesPage.mapConfiguration({
			field: 'Image',
			mappingField: 'Author Profile Image',
		});

		await expect(page.getByLabel('Image', {exact: true})).toHaveValue(
			'Basic Web Content: Author Profile Image'
		);

		await displayPageTemplatesPage.mapConfiguration({
			field: 'Image Alt Description',
			mappingField: 'Title',
		});

		await expect(
			page.getByLabel('Image Alt Description', {exact: true})
		).toHaveValue('${title:Title}');

		await page.getByTitle(`Go to ${displayPageTemplateName}`).click();

		await displayPageTemplatesPage.publishTemplate();

		// Create a Basic Web Content

		const journalArticleTitle = getRandomString();

		const journalArticleDescription = getRandomString();

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentStructureId,
			datePublished: null,
			description: journalArticleDescription,
			siteId: site.id,
			title: journalArticleTitle,
			viewableBy: 'Anyone',
		});

		// Assert open graph tags

		await performLogout(page);

		await page.goto(`web${site.friendlyUrlPath}/w/${journalArticleTitle}`);

		await expect(
			page.locator(
				`meta[property="og:title"][content="${journalArticleTitle}"]`
			)
		).toBeAttached();

		await expect(
			page.locator(
				`meta[property="og:description"][content="${journalArticleDescription}"]`
			)
		).toBeAttached();

		await expect(
			page.locator(
				`meta[property="og:image"][content*="/image/user_portrait"]`
			)
		).toBeAttached();

		await expect(
			page.locator(
				`meta[property="og:image:alt"][content="${journalArticleTitle}"]`
			)
		).toBeAttached();
	});

	test('User can map a web content to SEO meta tags in a display page', async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		site,
	}) => {

		// Create a display page template for Basic Web Content and mark as default

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const displayPageTemplateName = getRandomString();

		await addDefaultJournalArticleDisplayPageLayoutPageTemplateEntry(
			apiHelpers,
			String(contentStructureId),
			displayPageTemplateName,
			site
		);

		// Go to configuration

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		await displayPageTemplatesPage.clickMoreActions(
			displayPageTemplateName,
			'Edit'
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Configure'}),
			trigger: page
				.locator('.control-menu-nav-item')
				.getByLabel('Options', {exact: true}),
		});

		await page.locator('nav.menubar', {has: page.getByText('SEO')}).click();

		// Map HTML Title

		await page.getByLabel('HTML Title', {exact: true}).fill('');

		await displayPageTemplatesPage.mapConfiguration({
			field: 'HTML Title',
			mappingField: 'Title',
		});

		await expect(page.getByLabel('HTML Title', {exact: true})).toHaveValue(
			'${title:Title}'
		);

		await expect(page.getByLabel('Description', {exact: true})).toHaveValue(
			'${description}'
		);

		await page.getByTitle(`Go to ${displayPageTemplateName}`).click();

		await displayPageTemplatesPage.publishTemplate();

		// Create a Basic Web Content

		const journalArticleTitle = getRandomString();

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentStructureId,
			datePublished: null,
			siteId: site.id,
			title: journalArticleTitle,
			viewableBy: 'Anyone',
		});

		// Assert SEO HTML Title

		await performLogout(page);

		await page.goto(`web${site.friendlyUrlPath}/w/${journalArticleTitle}`);

		await expect(
			page.locator(
				`meta[property="og:title"][content="${journalArticleTitle}"]`
			)
		).toBeAttached();
	});

	test(
		'User can see friendly url and sitemap configuration in a default display page',
		{
			tag: ['@LPS-191986', '@LPS-193213'],
		},
		async ({apiHelpers, displayPageTemplatesPage, page, site}) => {

			// Create a display page template for Basic Web Content and mark as default

			const contentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			const displayPageTemplateName = getRandomString();

			await addDefaultJournalArticleDisplayPageLayoutPageTemplateEntry(
				apiHelpers,
				String(contentStructureId),
				displayPageTemplateName,
				site
			);

			// Go to configuration

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.clickMoreActions(
				displayPageTemplateName,
				'Configure'
			);

			// Assert general configuration

			await expect(page.getByLabel('Friendly URL')).toBeAttached();

			// Assert sitemap configuration

			await page
				.locator('nav.menubar', {has: page.getByText('SEO')})
				.click();

			await expect(page.getByPlaceholder('Robots')).toBeAttached();

			await expect(page.getByLabel('Include')).toBeAttached();

			await expect(page.getByLabel('Page Priority')).toBeAttached();

			await expect(page.getByLabel('Change Frequency')).toBeAttached();
		}
	);
});

test.describe('UI', () => {
	test(
		'Assert warning message when user change the content type',
		{
			tag: '@LPS-192722',
		},
		async ({displayPageTemplatesPage, page, site}) => {

			// Create a display page template for Basic Web Content

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentSubtype: 'Basic Web Content',
				contentType: 'Web Content Article',
				name: displayPageTemplateName,
			});

			// Assert warning message

			await displayPageTemplatesPage.clickMoreActions(
				displayPageTemplateName,
				'Change Content Type'
			);

			await expect(
				page.getByText(
					'Changing the content type may cause some elements of the display page template to lose their previous mapping.'
				)
			).toBeVisible();

			// Dismiss warning message

			await page
				.locator('.alert-dismissible')
				.getByLabel('Close', {exact: true})
				.click();

			await page
				.locator('.modal-header')
				.getByLabel('Close', {exact: true})
				.click();

			// Assert warning message

			await displayPageTemplatesPage.clickMoreActions(
				displayPageTemplateName,
				'Change Content Type'
			);

			await expect(
				page.getByText(
					'Changing the content type may cause some elements of the display page template to lose their previous mapping.'
				)
			).toBeVisible();
		}
	);

	test('Checks that the card checkbox has the correct aria label', async ({
		displayPageTemplatesPage,
		page,
		site,
	}) => {

		// Go to display pages administration

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		// Create new DPT and check checkbox aria-label

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentSubtype: 'Basic Web Content',
			contentType: 'Web Content Article',
			name: displayPageTemplateName,
		});

		await expect(
			page.getByLabel(`Select ${displayPageTemplateName}`)
		).toBeVisible();
	});

	test(
		'User can copy a display page',
		{
			tag: '@LPS-192724',
		},
		async ({displayPageTemplatesPage, page, pageEditorPage, site}) => {

			// Create a display page template for Blogs Entry

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentType: 'Blogs Entry',
				name: displayPageTemplateName,
			});

			// Add fragment and select editable

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			await pageEditorPage.addFragment('Basic Components', 'Heading');

			const headingId = await pageEditorPage.getFragmentId('Heading');

			await pageEditorPage.selectEditable(headingId, 'element-text');

			await page.getByLabel('Field').selectOption('Title');

			await displayPageTemplatesPage.publishTemplate();

			// Copy display page template

			await displayPageTemplatesPage.copyTemplate(
				displayPageTemplateName
			);

			// Assert copy display page template

			await expect(
				page.getByRole('link', {
					exact: true,
					name: `${displayPageTemplateName} (Copy)`,
				})
			).toBeVisible();

			// Go to copied display page template edit mode

			await displayPageTemplatesPage.editTemplate(
				`${displayPageTemplateName} (Copy)`
			);

			const copyHeadingId = await pageEditorPage.getFragmentId('Heading');

			await pageEditorPage.selectEditable(copyHeadingId, 'element-text');

			await expect(page.getByLabel('Field')).toHaveValue(
				'BlogsEntry_title'
			);
		}
	);

	test('User can delete default display page template', async ({
		displayPageTemplatesPage,
		page,
		site,
	}) => {

		// Create a display page template for Basic Web Content and mark as default

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentSubtype: 'Basic Web Content',
			contentType: 'Web Content Article',
			name: displayPageTemplateName,
		});

		await displayPageTemplatesPage.markAsDefault(displayPageTemplateName);

		// Delete default display page template

		await displayPageTemplatesPage.deleteTemplate(displayPageTemplateName);

		await expect(
			page.getByText(displayPageTemplateName, {exact: true})
		).not.toBeVisible();
	});

	test('User can rename a display page', async ({
		displayPageTemplatesPage,
		page,
		site,
	}) => {

		// Create a display page template for Blogs Entry

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: 'Blogs Entry',
			name: displayPageTemplateName,
		});

		// Rename display page template

		const newDisplayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.renameTemplate(
			newDisplayPageTemplateName,
			displayPageTemplateName
		);

		await expect(
			page.getByText(newDisplayPageTemplateName, {exact: true})
		).toBeVisible();
	});

	test(
		'Can add form container and remap it',
		{
			tag: '@LPS-192722',
		},
		async ({
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			pageManagementSite,
		}) => {

			// Create a display page template for Object

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentType: 'Lemon Basket',
				name: displayPageTemplateName,
			});

			// Edit display page template

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			// Add and map form container

			await pageEditorPage.addFragment(
				'Content Display',
				'Form Container'
			);

			await pageEditorPage.mapFormFragment(
				await pageEditorPage.getFragmentId('Form Container'),
				'Lemon Basket (Default)'
			);

			await pageEditorPage.addFragment('Basic Components', 'Heading');

			await pageEditorPage.selectEditable(
				await pageEditorPage.getFragmentId('Heading'),
				'element-text'
			);

			await pageEditorPage.setMappingConfiguration({
				mapping: {
					field: 'Lemon Basket Color',
				},
				source: 'structure',
			});

			await displayPageTemplatesPage.publishTemplate();

			// Change content type

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			await displayPageTemplatesPage.clickMoreActions(
				displayPageTemplateName,
				'Change Content Type'
			);

			await page
				.getByLabel('Content Type', {exact: true})
				.selectOption('All Fields');

			await page.getByRole('button', {name: 'Save'}).click();

			// Assert that the form container is mapped to the new content type

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			await pageEditorPage.selectFragment(
				await pageEditorPage.getFragmentId('Form Container')
			);

			await expect(
				page
					.getByLabel('Content Type', {exact: true})
					.getByRole('option', {selected: true})
			).toHaveText('All Fields (Default)');

			await pageEditorPage.selectEditable(
				await pageEditorPage.getFragmentId('Heading'),
				'element-text'
			);

			expect(
				await page
					.getByRole('combobox', {name: 'Field'})
					.evaluate(
						(element: HTMLSelectElement) =>
							element.selectedOptions[0].innerText
					)
			).toBe('-- Unmapped --');
		}
	);

	test(
		'Content type modal is displayed when mapped object definition does not exist anymore',
		{
			tag: '@LPD-51605',
		},
		async ({apiHelpers, displayPageTemplatesPage, page, site}) => {

			// Create Object definition

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body: objectDefinition} =
				await objectDefinitionAPIClient.postObjectDefinition({
					active: true,
					externalReferenceCode: 'stockERC',
					label: {
						en_US: 'stock',
					},
					name: 'Stock',
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'nameERC',
							indexed: true,
							indexedAsKeyword: true,
							label: {
								en_US: 'name',
							},
							name: 'name',
							required: true,
						},
					],
					pluralLabel: {
						en_US: 'stocks',
					},
					portlet: true,
					scope: 'company',
					status: {
						code: 0,
					},
				});

			// Create display page template for object

			const className =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					objectDefinition.className
				);

			const displayPageTemplateName = getRandomString();

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
				{
					classNameId: className.classNameId,
					groupId: site.id,
					name: displayPageTemplateName,
				}
			);

			// Delete object definition

			await objectDefinitionAPIClient.deleteObjectDefinition(
				objectDefinition.id
			);

			// Check that content type modal is displayed when trying to edit display page template

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.clickMoreActions(
				displayPageTemplateName,
				'Edit'
			);

			await expect(
				page.getByLabel('Select Content Type', {exact: true})
			).toBeVisible();
		}
	);
});

test.describe('Usages', () => {
	test(
		'Can assign usage to default even if the default display page template does not exist',
		{
			tag: '@LPS-121199',
		},
		async ({apiHelpers, displayPageTemplatesPage, page, site}) => {

			// Create a display page template for Basic Web Content

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageTemplateName =
				'basicWebContentDpt' + getRandomInt();

			await displayPageTemplatesPage.createTemplate({
				contentSubtype: 'Basic Web Content',
				contentType: 'Web Content Article',
				name: displayPageTemplateName,
			});

			// Create a Basic Web Content and set the template as its specific display page template

			const journalArticleTitle = getRandomString();

			await addBasicJournalArticleWithSpecificDisplayPageTemplate(
				apiHelpers,
				journalArticleTitle,
				displayPageTemplateName,
				site
			);

			// View display page template usages and check that option 'Assign to Default' is displayed

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.viewUsages(displayPageTemplateName);

			await expect(page.getByText(journalArticleTitle)).toBeVisible();

			const firstRowCheckbox = page
				.getByRole('row')
				.getByRole('checkbox')
				.first();

			await firstRowCheckbox.click();

			await page.getByRole('button', {name: 'Actions'}).click();

			await expect(
				page.getByRole('menuitem', {
					exact: true,
					name: 'Assign to Default',
				})
			).toBeVisible();
		}
	);

	test(
		'Can assign usage to default even if the default display page template exists',
		{
			tag: '@LPS-121199',
		},
		async ({apiHelpers, displayPageTemplatesPage, page, site}) => {

			// Create a display page template for Basic Web Content and mark as default

			const contentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			const defaultDisplayPageTemplateName = getRandomString();

			await addDefaultJournalArticleDisplayPageLayoutPageTemplateEntry(
				apiHelpers,
				String(contentStructureId),
				defaultDisplayPageTemplateName,
				site
			);

			// Create a display page template for Basic Web Content

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageTemplateName =
				'basicWebContentDpt' + getRandomInt();

			await displayPageTemplatesPage.createTemplate({
				contentSubtype: 'Basic Web Content',
				contentType: 'Web Content Article',
				name: displayPageTemplateName,
			});

			// Create a Basic Web Content and set the template as its specific display page template

			const journalArticleTitle = getRandomString();

			await addBasicJournalArticleWithSpecificDisplayPageTemplate(
				apiHelpers,
				journalArticleTitle,
				displayPageTemplateName,
				site
			);

			// View display page template usages and check that option 'Assign to Default (defaultTemplate)' is displayed

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.viewUsages(displayPageTemplateName);

			await expect(page.getByText(journalArticleTitle)).toBeVisible();

			const firstRowCheckbox = page
				.getByRole('row')
				.getByRole('checkbox')
				.first();

			await firstRowCheckbox.click();

			await page.getByRole('button', {name: 'Actions'}).click();

			await expect(
				page.getByRole('menuitem', {
					exact: true,
					name: `Assign to Default (${defaultDisplayPageTemplateName})`,
				})
			).toBeVisible();
		}
	);

	test(
		'Can assign multiple usages to default',
		{
			tag: '@LPS-121199',
		},
		async ({apiHelpers, displayPageTemplatesPage, page, site}) => {

			// Create a display page template for Basic Web Content

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentSubtype: 'Basic Web Content',
				contentType: 'Web Content Article',
				name: displayPageTemplateName,
			});

			// Create three basic web contents and set its specific display page templates

			for (let i = 1; i < 4; i++) {
				const journalArticleTitle = getRandomString();

				await addBasicJournalArticleWithSpecificDisplayPageTemplate(
					apiHelpers,
					journalArticleTitle,
					displayPageTemplateName,
					site
				);
			}

			// View usages of display page template

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.viewUsages(displayPageTemplateName);

			// Assign to default all the usages leaving the usages empty

			for (const rowCheckbox of await page
				.getByRole('row')
				.getByRole('checkbox')
				.all()) {
				await expect(rowCheckbox).toBeVisible();
				await rowCheckbox.click();
			}

			await page.getByRole('button', {name: 'Actions'}).click();

			const assignToDefaultMenuItem = page.getByRole('menuitem', {
				exact: true,
				name: `Assign to Default`,
			});

			await expect(assignToDefaultMenuItem).toBeVisible();

			page.on('dialog', async (dialog) => {
				dialog.accept();
			});

			await assignToDefaultMenuItem.click();

			await expect(
				page.getByText('There are no display page template usages.')
			).toBeVisible();
		}
	);

	test(
		'View usages for blogs entry',
		{
			tag: '@LPS-123480',
		},
		async ({
			blogsEditBlogEntryPage,
			displayPageTemplatesPage,
			page,
			site,
		}) => {

			// Create a display page template for Blogs Entry

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentType: 'Blogs Entry',
				name: displayPageTemplateName,
			});

			// Create a Blogs Entry and set the template as its specific display page template

			await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

			const title = getRandomString();
			const content = getRandomString();

			await blogsEditBlogEntryPage.editBlogEntry({
				content,
				publish: false,
				title,
			});

			await blogsEditBlogEntryPage.selectSpecificDisplayPage(
				displayPageTemplateName
			);

			await blogsEditBlogEntryPage.publishBlogEntry();

			// View usages of display page template

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.viewUsages(displayPageTemplateName);

			const firstRowCheckbox = page
				.getByRole('row')
				.getByRole('checkbox')
				.first();
			await expect(firstRowCheckbox).toBeVisible();
		}
	);

	test(
		'View usages for basic document',
		{
			tag: '@LPS-123480',
		},
		async ({
			displayPageTemplatesPage,
			documentLibraryEditFilePage,
			page,
			site,
		}) => {

			// Create a display page template for Basic Document

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentSubtype: 'Basic Document',
				contentType: 'Document',
				name: displayPageTemplateName,
			});

			// Create a Basic Document and set the template as its specific display page template

			await documentLibraryEditFilePage.goto(site.friendlyUrlPath);

			const title = getRandomString();

			await page.getByLabel('Title').fill(title);

			await documentLibraryEditFilePage.selectSpecificDisplayPage(
				displayPageTemplateName
			);

			await documentLibraryEditFilePage.publishFileEntry();

			// View usages of display page template

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.viewUsages(displayPageTemplateName);

			const firstRowCheckbox = page
				.getByRole('row')
				.getByRole('checkbox')
				.first();
			await expect(firstRowCheckbox).toBeVisible();
		}
	);
});

test.describe('View', () => {
	test(
		'View display page',
		{
			tag: [
				'@LPS-86190',
				'@LPS-96438',
				'@LPS-90999',
				'@LPS-121195',
				'@LPS-150919',
			],
		},
		async ({apiHelpers, page, site}) => {

			// Create a content page

			await apiHelpers.headlessDelivery.createSitePage({
				siteId: site.id,
				title: getRandomString(),
			});

			// Create a display page template for Basic Web Content

			const contentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			const displayPageTemplateName =
				'basicWebContentDpt' + getRandomInt();

			await addDefaultJournalArticleDisplayPageLayoutPageTemplateEntry(
				apiHelpers,
				String(contentStructureId),
				displayPageTemplateName,
				site
			);

			// Create a Basic Web Content

			const journalArticleTitle = getRandomString();

			await apiHelpers.headlessDelivery.postStructuredContent({
				contentStructureId,
				datePublished: null,
				siteId: site.id,
				tags: ['Cats', 'Dogs'],
				title: journalArticleTitle,
				viewableBy: 'Anyone',
			});

			const blogsEntryName = getRandomString();

			await apiHelpers.headlessDelivery.postBlog(site.id, {
				headline: blogsEntryName,
			});

			// Go to display page

			await page.goto(
				`web${site.friendlyUrlPath}/w/${journalArticleTitle}`
			);

			await expect(
				page.getByRole('heading', {name: journalArticleTitle})
			).toBeVisible();

			// Can access to edit the web content article or display page while viewing the article through its display page

			await page
				.locator('.control-menu-nav-item')
				.getByLabel('Edit', {exact: true})
				.click();

			await expect(
				page.locator('.dropdown-menu').getByRole('menuitem', {
					name: `Edit ${journalArticleTitle}`,
				})
			).toBeVisible();

			await expect(
				page
					.locator('.dropdown-menu')
					.getByRole('menuitem', {name: 'Edit Display Page Template'})
			).toBeVisible();

			// Assert metadata should appear in page source

			await expect(
				page.locator('meta[content="cats,dogs"]')
			).toBeAttached();

			// Verify guest user can view display page

			await performLogout(page);

			await page.goto(
				`web${site.friendlyUrlPath}/w/${journalArticleTitle}`
			);

			await expect(page.getByText('Page Not Found')).not.toBeVisible();

			await expect(
				page.locator(
					`link[href*="web${site.friendlyUrlPath}/w/${journalArticleTitle}"][rel="canonical"]`
				)
			).toBeAttached();

			await expect(
				page.locator(
					`link[href*="zh/web${site.friendlyUrlPath}/w/${journalArticleTitle}"][hreflang="zh-CN"][rel="alternate"]`
				)
			).toBeAttached();
		}
	);

	test(
		'User can interact with widgets in a display page',
		{
			tag: ['@LPS-106776', '@LPS-120504', '@ LPS-129360'],
		},
		async ({
			apiHelpers,
			displayPageTemplatesPage,
			page,
			pageEditorPage,
			site,
		}) => {

			// Create a display page template for Basic Web Content and mark as default

			const contentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			const displayPageTemplateName = getRandomString();

			await addDefaultJournalArticleDisplayPageLayoutPageTemplateEntry(
				apiHelpers,
				String(contentStructureId),
				displayPageTemplateName,
				site
			);

			// Go to configuration

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.clickMoreActions(
				displayPageTemplateName,
				'Edit'
			);

			// Add heading fragment and map it to title

			await pageEditorPage.addFragment('Basic Components', 'Heading');

			const headingId = await pageEditorPage.getFragmentId('Heading');

			await pageEditorPage.selectEditable(headingId, 'element-text');

			await page.getByLabel('Field').selectOption('Title');

			// Add documents and media widget

			await pageEditorPage.addWidget(
				'Highlighted',
				'Documents and Media'
			);

			await displayPageTemplatesPage.publishTemplate();

			// Create a Basic Web Content

			const journalArticleTitle = getRandomString();

			await apiHelpers.headlessDelivery.postStructuredContent({
				contentStructureId,
				datePublished: null,
				siteId: site.id,
				title: journalArticleTitle,
				viewableBy: 'Anyone',
			});

			// Create a basic document

			const document = await apiHelpers.headlessDelivery.postDocument(
				site.id,
				createReadStream(
					path.join(__dirname, '/dependencies/image.jpg')
				)
			);

			// Assert can change display style of documents and media

			await page.goto(
				`web${site.friendlyUrlPath}/w/${journalArticleTitle}`
			);

			const headingFragment = page.locator('.component-heading');

			await expect(headingFragment).toHaveText(journalArticleTitle);

			await expect(
				page.getByRole('link', {name: document.title})
			).toBeAttached();

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: 'list'}),
				trigger: page.getByLabel('Select View, Currently'),
			});

			await expect(headingFragment).toHaveText(journalArticleTitle);

			await expect(
				page.getByRole('link', {name: document.title})
			).toBeAttached();
		}
	);

	test(
		'View the info panel for a display page and for a folder',
		{
			tag: ['@LPD-34205', '@LPS-189857'],
		},
		async ({displayPageTemplatesPage, page, site}) => {

			// Create a folder

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageTemplateFolderName = getRandomString();
			const displayPageTemplateFolderDescription = getRandomString();

			await displayPageTemplatesPage.createFolder(
				displayPageTemplateFolderName,
				displayPageTemplateFolderDescription
			);

			// Create a display page template for Blogs Entry

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentType: 'Blogs Entry',
				folderName: displayPageTemplateFolderName,
				name: displayPageTemplateName,
			});

			// Check folder info panel

			await page.getByTitle('Toggle Info Panel', {exact: true}).click();

			const infoPanel = page.getByLabel('Info Panel', {exact: true});

			await expect(
				infoPanel.locator('.sidebar-header .component-title')
			).toContainText(displayPageTemplateFolderName);

			await expect(
				infoPanel.locator('.sidebar-header .component-subtitle')
			).toContainText('Folder');

			await expect(
				infoPanel.locator('.sidebar-body .c-mb-4').nth(0)
			).toContainText('Manage Permissions');

			await expect(
				infoPanel.locator('.sidebar-body .c-mb-4').nth(1)
			).toContainText('Location');

			await expect(
				infoPanel.locator('.sidebar-body .c-mb-4').nth(1)
			).toContainText(`Home > ${displayPageTemplateFolderName}`);

			await expect(
				infoPanel.locator('.sidebar-body .c-mb-4').nth(2)
			).toContainText('Number of Items');

			await expect(
				infoPanel.locator('.sidebar-body .c-mb-4').nth(2)
			).toContainText('1');

			await expect(
				infoPanel.locator('.sidebar-body .c-mb-4').nth(3)
			).toContainText('Created');

			await expect(
				infoPanel.locator('.sidebar-body .c-mb-4').nth(4)
			).toContainText('Modified');

			await expect(
				infoPanel.locator('.sidebar-body .c-mb-4').nth(5)
			).toContainText('Description');

			await expect(
				infoPanel.locator('.sidebar-body .c-mb-4').nth(5)
			).toContainText(displayPageTemplateFolderDescription);

			// Check display page info panel

			await page
				.getByLabel(`Select ${displayPageTemplateName}`, {exact: true})
				.check();

			await expect(
				infoPanel.locator('.sidebar-header .component-title')
			).toContainText(displayPageTemplateName);

			await expect(
				infoPanel.locator('.sidebar-header .component-subtitle')
			).toContainText('Display Page Template');

			await expect(
				infoPanel.locator('.sidebar-header .label-item')
			).toContainText('Approved');

			await expect(
				infoPanel.locator('.sidebar-body .c-mb-4').nth(0)
			).toContainText('Manage Permissions');

			await expect(
				infoPanel.locator('.sidebar-body .c-mb-4').nth(1)
			).toContainText('Location');

			await expect(
				infoPanel.locator('.sidebar-body .c-mb-4').nth(1)
			).toContainText(`Home > ${displayPageTemplateFolderName}`);

			await expect(
				infoPanel.locator('.sidebar-body .c-mb-4').nth(2)
			).toContainText('Content Type');

			await expect(
				infoPanel.locator('.sidebar-body .c-mb-4').nth(2)
			).toContainText('Blogs Entry');

			await expect(
				infoPanel.locator('.sidebar-body .c-mb-4').nth(3)
			).toContainText('Created');

			await expect(
				infoPanel.locator('.sidebar-body .c-mb-4').nth(4)
			).toContainText('Modified');
		}
	);

	test(
		'User can copy a display page folder',
		{
			tag: '@LPD-39372',
		},
		async ({displayPageTemplatesPage, page, site}) => {

			// Create two different display page folder

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageFolderNameSource = getRandomString();
			const displayPageFolderNameTarget = getRandomString();

			await displayPageTemplatesPage.createFolder(
				displayPageFolderNameSource
			);
			await displayPageTemplatesPage.createFolder(
				displayPageFolderNameTarget
			);

			// Copy folder source to folder target

			await displayPageTemplatesPage.copyFolderTo(
				displayPageFolderNameSource,
				displayPageFolderNameTarget
			);

			// Assert copy display page collection

			await page
				.getByRole('link', {
					exact: true,
					name: displayPageFolderNameTarget,
				})
				.click();

			await expect(
				page.getByRole('link', {
					exact: true,
					name: displayPageFolderNameSource,
				})
			).toBeVisible();
		}
	);

	test(
		'User can manage permissions from info panel',
		{
			tag: '@LPD-39372',
		},
		async ({displayPageTemplatesPage, page, site}) => {

			// Create a folder

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageTemplateFolderName = getRandomString();
			const displayPageTemplateFolderDescription = getRandomString();

			await displayPageTemplatesPage.createFolder(
				displayPageTemplateFolderName,
				displayPageTemplateFolderDescription
			);

			// Create a display page template for Blogs Entry

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentType: 'Blogs Entry',
				folderName: displayPageTemplateFolderName,
				name: displayPageTemplateName,
			});

			// Open the info panel

			await clickAndExpectToBeVisible({
				target: page.getByLabel('Close Info Panel'),
				trigger: page.getByTitle('Toggle Info Panel', {exact: true}),
			});

			const infoPanel = page.getByLabel('Info Panel', {exact: true});

			await expect(async () => {
				await page
					.getByLabel(`Select ${displayPageTemplateName}`, {
						exact: true,
					})
					.check({timeout: 1000});

				await expect(
					infoPanel.getByRole('button', {name: 'Manage Permissions'})
				).toBeVisible({timeout: 1000});
			}).toPass();

			const iframe = page.frameLocator('iframe[title="Permissions"]');

			await clickAndExpectToBeVisible({
				target: iframe.locator('#guest_ACTION_DELETE'),
				timeout: 3000,
				trigger: infoPanel.getByRole('button', {
					name: 'Manage Permissions',
				}),
			});

			// Change permissions for display page template

			await iframe.locator('#guest_ACTION_DELETE').check();
			await iframe
				.locator('#analytics-administrator_ACTION_DELETE')
				.check();
			await iframe
				.locator('#analytics-administrator_ACTION_PERMISSIONS')
				.check();

			await iframe.getByRole('button', {name: 'Save'}).click();

			await expect(
				iframe.getByText('Success:Your request completed successfully.')
			).toBeVisible();

			await iframe.getByRole('button', {name: 'Cancel'}).click();

			// Open the info panel

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const folderCard = page
				.locator('.card-page-item-directory')
				.filter({hasText: displayPageTemplateFolderName});

			await folderCard.waitFor();

			await clickAndExpectToBeVisible({
				target: page.getByLabel('Close Info Panel'),
				trigger: page.getByTitle('Toggle Info Panel', {exact: true}),
			});

			await expect(async () => {
				await folderCard.locator('input').check({timeout: 1000});

				await expect(
					infoPanel.getByRole('button', {name: 'Manage Permissions'})
				).toBeVisible({timeout: 1000});
			}).toPass();

			await clickAndExpectToBeVisible({
				target: iframe.locator('#guest_ACTION_DELETE'),
				timeout: 3000,
				trigger: infoPanel.getByRole('button', {
					name: 'Manage Permissions',
				}),
			});

			// Change permissions for folder

			await iframe.locator('#guest_ACTION_DELETE').check();
			await iframe
				.locator('#analytics-administrator_ACTION_DELETE')
				.check();
			await iframe
				.locator('#analytics-administrator_ACTION_PERMISSIONS')
				.check();

			await iframe.getByRole('button', {name: 'Save'}).click();

			await expect(
				iframe.getByText('Success:Your request completed successfully.')
			).toBeVisible();

			await iframe.getByRole('button', {name: 'Cancel'}).click();
		}
	);
});
