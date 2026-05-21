/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {customFieldsPagesTest} from '../../../fixtures/customFieldsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {masterPagesPagesTest} from '../../../fixtures/masterPagesPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageTemplatesPagesTest} from '../../../fixtures/pageTemplatesPagesTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {styleBookPageTest} from '../../../fixtures/styleBookPageTest';
import {TCustomField} from '../../../helpers/CustomFieldTypesHelper';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import dragAndDropElement from '../../../utils/dragAndDropElement';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {templatesPageTest} from '../../template-web/main/fixtures/templatesPageTest';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	changeTrackingPagesTest,
	customFieldsPagesTest,
	featureFlagsTest({
		'LPD-39304': {enabled: true},
		'LPD-76864': {enabled: true},
	}),
	isolatedSiteTest,
	masterPagesPagesTest,
	pagesAdminPagesTest,
	pageEditorPagesTest,
	pageTemplatesPagesTest,
	styleBookPageTest,
	templatesPageTest,
	pageViewModePagesTest
);

const testWithPrivatePages = mergeTests(
	test,
	featureFlagsTest({
		'LPD-38869': {enabled: true},
	})
);

test('Add and apply content template', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
	pageTemplatesPage,
	pagesAdminPage,
	site,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	// Create page template collection

	await pageTemplatesPage.goto(site.friendlyUrlPath);

	const pageTemplateCollectionName = getRandomString();

	await pageTemplatesPage.addPageTemplateCollection(
		pageTemplateCollectionName
	);

	// Create content page template with heading fragment

	const contentPageTemplateName = getRandomString();

	await pageTemplatesPage.addContentPageTemplate(contentPageTemplateName);

	await pageEditorPage.addFragment('Basic Components', 'Heading');

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
		template: contentPageTemplateName,
	});

	await pageEditorPage.publishPage();

	// Review publication changes and publish

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await changeTrackingPage.viewChanges({
		changed: 'Added',
		site: site.name,
		title: contentPageTemplateName,
		type: 'Layout Page Template Entry',
	});

	await page
		.getByRole('link', {exact: true, name: contentPageTemplateName})
		.click();
	await expect(
		page.locator(
			'//td[contains(@class,"publications-render-view-content")]'
		)
	).toBeVisible();

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await changeTrackingPage.assertStatus('Published', ctCollection.body.name);

	// Verify that the fragment is present

	await pagesAdminPage.goto(site.friendlyUrlPath);
	await pagesAdminPage.clickOnAction('Edit', layoutTitle);
	await expect(page.getByText('Heading Example')).toBeVisible();

	// Delete page template collection

	await pageTemplatesPage.goto(site.friendlyUrlPath);

	await pageTemplatesPage.deletePageTemplateCollection(
		pageTemplateCollectionName
	);
});

test(
	'Add and apply display page template',
	{tag: '@LPD-60041'},
	async ({
		apiHelpers,
		changeTrackingPage,
		ctCollection,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		await changeTrackingPage.workOnPublication(ctCollection);

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		// Create a display page template for Basic Web Content

		await displayPageTemplatesPage.createTemplate({
			contentSubtype: 'Basic Web Content',
			contentType: 'Web Content Article',
			name: displayPageTemplateName,
		});

		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		// Add heading fragment to the template

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		await pageEditorPage.publishPage();

		// Review publication changes

		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await changeTrackingPage.viewChanges({
			changed: 'Added',
			site: site.name,
			title: displayPageTemplateName,
			type: 'Layout Page Template Entry',
		});

		await page
			.getByRole('link', {exact: true, name: displayPageTemplateName})
			.click();
		await expect(
			page.locator(
				'//td[contains(@class,"publications-render-view-content")]'
			)
		).toBeVisible();

		await apiHelpers.headlessChangeTracking.publishCTCollection(
			ctCollection.body.id
		);

		await changeTrackingPage.assertStatus(
			'Published',
			ctCollection.body.name
		);

		// Verify that the fragment is present

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);
		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);
		await expect(page.getByText('Heading Example')).toBeVisible();

		// Delete display page template

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		await displayPageTemplatesPage.deleteAllDisplayPageTemplates();
	}
);

test('Add and apply information template', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
	site,
	templatesPage,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	// Add a basic web content

	const journalName = getRandomString();

	const basicWebContentStructureId =
		await getBasicWebContentStructureId(apiHelpers);

	await apiHelpers.jsonWebServicesJournal.addWebContent({
		ddmStructureId: basicWebContentStructureId,
		groupId: site.id,
		titleMap: {en_US: journalName},
	});

	// Create information template

	await templatesPage.goto(site.friendlyUrlPath);

	const informationTemplateName = getRandomString();

	await templatesPage.createInformationTemplate({
		itemSubtype: 'Basic Web Content',
		itemType: 'Web Content Article',
		name: informationTemplateName,
	});

	await page.getByRole('button', {name: 'Title'}).click();
	await templatesPage.saveTemplate(informationTemplateName);

	// Add a page with an HTML fragment

	const layoutTitle = getRandomString();

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		options: {type: 'content'},
		title: layoutTitle,
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.addFragment('Basic Components', 'HTML');

	// Map fragment to template

	const htmlFragmentId = await pageEditorPage.getFragmentId('HTML');

	await pageEditorPage.selectEditable(htmlFragmentId, 'element-html');

	await pageEditorPage.setMappedItem({
		entity: 'Web Content',
		entry: journalName,
		field: informationTemplateName,
	});

	await pageEditorPage.waitForChangesSaved();

	await pageEditorPage.publishPage();

	// Review publication changes and publish

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await changeTrackingPage.viewChanges({
		changed: 'Added',
		site: site.name,
		title: informationTemplateName,
		type: 'Dynamic Data Mapping Template',
	});

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await changeTrackingPage.assertStatus('Published', ctCollection.body.name);

	// Assert changes are published and template is mapped in production

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);
	await expect(page.getByText(journalName)).toBeVisible();

	await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.plid);
});

test('Add and apply widget template', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	site,
	templatesPage,
	widgetPagePage,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	// Create widget template

	await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);

	const widgetTemplateName = getRandomString();

	await templatesPage.createWidgetTemplate(
		widgetTemplateName,
		'Language Selector Template'
	);

	await templatesPage.editTemplate(widgetTemplateName);

	await page.getByRole('button', {name: 'Locale'}).click();

	await templatesPage.saveTemplate(widgetTemplateName);

	// Create a widget page layout using the template

	const layoutTitle = getRandomString();

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: layoutTitle,
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	// Add a language selector widget configured with widget template

	await widgetPagePage.addPortlet('Language Selector');

	await widgetPagePage.clickOnAction('Language Selector', 'Configuration');

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

	// Review publication changes and publish

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await changeTrackingPage.viewChanges({
		changed: 'Added',
		site: site.name,
		title: widgetTemplateName,
		type: 'Dynamic Data Mapping Template',
	});

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await changeTrackingPage.assertStatus('Published', ctCollection.body.name);

	// Assert template usage

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);
	await expect(page.getByText('en_US')).toBeVisible();

	await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.plid);
});

test('Add new page with master template', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	masterPagesPage,
	page,
	pageEditorPage,
	site,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	// Add a master page template

	const masterPageTemplateEntryName = getRandomString();

	const masterPageTemplate =
		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
			{
				groupId: site.id,
				name: masterPageTemplateEntryName,
				type: 'master-layout',
			}
		);

	// Add header and footer fragments to master page

	await masterPagesPage.goto(site.friendlyUrlPath);

	await masterPagesPage.editMaster(masterPageTemplateEntryName);

	await pageEditorPage.addFragment('Footers', 'Footer Nav Dark');

	await pageEditorPage.publishPage();

	// Create a new layout based on template

	const layoutTitle = getRandomString();

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		masterLayoutPageTemplateEntryERC:
			masterPageTemplate.externalReferenceCode,
		options: {type: 'content'},
		title: layoutTitle,
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.publishPage();

	// Review publication changes and publish

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await changeTrackingPage.viewChanges({
		changed: 'Added',
		site: site.name,
		title: masterPageTemplateEntryName,
		type: 'Fragment Entry Link',
	});

	await page
		.getByRole('link', {exact: true, name: masterPageTemplateEntryName})
		.click();
	await expect(
		page.locator(
			'//td[contains(@class,"publications-render-view-content")]'
		)
	).toBeVisible();

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await changeTrackingPage.assertStatus('Published', ctCollection.body.name);

	// Verify that the fragment is present

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);
	await expect(page.locator('.navbar-dark')).toBeVisible();

	await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.plid);
});

test('Apply style book layout', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
	styleBooksPage,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	// Create a style book with times as font family base

	const styleBookName = getRandomString();

	await styleBooksPage.goto();

	await styleBooksPage.create(styleBookName, 'Classic Theme');

	await styleBooksPage.selectTokenCategory('Typography');

	await page.getByRole('button', {name: 'Font Size'}).click();

	await page.waitForTimeout(300);

	await styleBooksPage.updateTokenInput(
		'Font Family Base',
		'times',
		'Font Family'
	);

	await styleBooksPage.waitForAutoSave();

	await styleBooksPage.publish();

	// Create page and apply style book

	const layoutTitle = getRandomString();

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		options: {type: 'content'},
		title: layoutTitle,
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.addFragment('Basic Components', 'HTML');

	await pageEditorPage.goToSidebarTab('Page Design Options');

	await page.getByRole('tab', {name: 'Style Book'}).click();

	await page.getByLabel(styleBookName).click();

	await pageEditorPage.publishPage();

	// Publish publication

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await changeTrackingPage.assertStatus('Published', ctCollection.body.name);

	// Verify the layout font is times

	await page.reload();

	await page.goto('/');

	await page.getByRole('menuitem', {name: layoutTitle}).click();

	await expect(page.getByText('HTML Example')).toHaveCSS(
		'font-family',
		'times'
	);

	await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.plid);
});

test('Create custom fragments', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
	site,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	// Create a custom fragment

	const fragmentCollectionName = getRandomString();

	const {fragmentCollectionId} =
		await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
			{
				groupId: site.id,
				name: fragmentCollectionName,
			}
		);

	const fragmentEntryName = getRandomString();

	await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
		fragmentCollectionId,
		groupId: site.id,
		html: `<lfr-editable id="element-html" type="html">
               		<h1>test html</h1>
               	</lfr-editable>`,
		name: fragmentEntryName,
		type: 'component',
	});

	// Add a layout and add custom fragment

	const layoutTitle = getRandomString();

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		options: {type: 'content'},
		title: layoutTitle,
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.addFragment(fragmentCollectionName, fragmentEntryName);

	await pageEditorPage.waitForChangesSaved();

	await pageEditorPage.publishPage();

	// Review publication changes and publish

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await changeTrackingPage.viewChanges({
		changed: 'Added',
		site: site.name,
		title: fragmentEntryName,
		type: 'Fragment Entry',
	});

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await changeTrackingPage.assertStatus('Published', ctCollection.body.name);

	// Assert changes are published and template is mapped in production

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);
	await expect(page.getByText('test html')).toBeVisible();

	await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.plid);
});

test('Create page with existing page template', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	pageTemplatesPage,
	pagesAdminPage,
	widgetPagePage,
}) => {
	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	// Create page template collection

	await pageTemplatesPage.goto(site.friendlyUrlPath);

	const pageTemplateCollectionName = getRandomString();

	await pageTemplatesPage.addPageTemplateCollection(
		pageTemplateCollectionName
	);

	// Create widget page template with web content display widget

	const widgetPageTemplateName = getRandomString();

	await pageTemplatesPage.addWidgetPageTemplate(widgetPageTemplateName);

	await widgetPagePage.addPortlet('Web Content Display');

	// Add a widget page based on template in a publication

	await changeTrackingPage.workOnPublication(ctCollection);

	await pagesAdminPage.goto(site.friendlyUrlPath);

	await pagesAdminPage.gotoSelectTemplates(pageTemplateCollectionName);

	const layoutTitle = getRandomString();

	await pagesAdminPage.addPage({
		name: layoutTitle,
		template: widgetPageTemplateName,
	});

	// Publish publication

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await changeTrackingPage.assertStatus('Published', ctCollection.body.name);

	// Assert widget page is created based on widget page template

	await page.goto('/');

	await page.getByRole('menuitem', {name: layoutTitle}).click();

	await expect(
		page.getByRole('heading', {name: 'Web Content Display'})
	).toBeVisible();

	await expect(
		page.getByText('This application is not visible to users yet.')
	).toBeVisible();

	// Delete page template collection

	await pagesAdminPage.goto(site.friendlyUrlPath);

	await pagesAdminPage.deletePage(layoutTitle);

	await pageTemplatesPage.goto(site.friendlyUrlPath);

	await pageTemplatesPage.deletePageTemplate(widgetPageTemplateName);

	await expect(
		page.getByRole('link', {exact: true, name: widgetPageTemplateName})
	).not.toBeVisible();

	await pageTemplatesPage.clickPageTemplateCollectionAction(
		'Delete',
		pageTemplateCollectionName
	);

	await page.getByRole('button', {name: 'Delete'}).click();

	await expect(page.getByText('No Page Template Sets yet.')).toBeVisible();
});

test(
	'Default calendar is hidden after adding widget in widget page',
	{tag: '@LPD-49292'},
	async ({
		apiHelpers,
		changeTrackingPage,
		ctCollection,
		page,
		widgetPagePage,
	}) => {
		const site =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
				'guest'
			);

		await changeTrackingPage.workOnPublication(ctCollection);

		// Create a page with an asset publisher

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {
				type: 'portlet',
			},
			title: layoutTitle,
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Asset Publisher');

		// Go to review changes and assert calendar is not present

		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await changeTrackingPage.viewChanges({
			changed: 'Added',
			isVisible: false,
			site: site.name,
			title: site.name,
			type: 'Calendar',
		});

		await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.plid);
	}
);

test(
	'Page preview available when reviewing publication changes',
	{tag: '@LPS-148816'},
	async ({
		apiHelpers,
		changeTrackingPage,
		ctCollection,
		page,
		pageEditorPage,
		site,
	}) => {
		await changeTrackingPage.workOnPublication(ctCollection);

		// Add a page with a fragment and a widget

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'content'},
			title: layoutTitle,
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		await pageEditorPage.addWidget('Tools', 'Sign In');

		await pageEditorPage.publishPage();

		// Review publication changes

		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await changeTrackingPage.viewChanges({
			changed: 'Added',
			site: site.name,
			title: layoutTitle,
			type: 'Page',
		});

		await page.getByRole('link', {exact: true, name: layoutTitle}).click();
		await expect(
			page.locator(
				'//td[contains(@class,"publications-render-view-content")]'
			)
		).toBeVisible();

		await apiHelpers.headlessChangeTracking.scheduleCTCollection(
			ctCollection.body.id
		);

		await changeTrackingPage.goToReviewChangesScheduled(
			ctCollection.body.name
		);

		await changeTrackingPage.viewChanges({
			changed: 'Added',
			site: site.name,
			title: layoutTitle,
			type: 'Page',
		});

		await page.getByRole('link', {exact: true, name: layoutTitle}).click();
		await expect(
			page.locator(
				'//td[contains(@class,"publications-render-view-content")]'
			)
		).toBeVisible();
	}
);

test(
	'Preview fragment before publishing',
	{tag: '@LPS-176197'},
	async ({
		apiHelpers,
		changeTrackingPage,
		ctCollection,
		page,
		pageEditorPage,
	}) => {
		const site =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
				'guest'
			);

		// Add a page with a fragment in production

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'content'},
			title: layoutTitle,
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		await pageEditorPage.publishPage();

		// Edit fragment in publication

		await changeTrackingPage.workOnPublication(ctCollection);

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		const headingId = await pageEditorPage.getFragmentId('Heading');

		await pageEditorPage.editTextEditable(
			headingId,
			'element-text',
			'Edited Text'
		);

		await pageEditorPage.publishPage();

		// Review publication changes

		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await changeTrackingPage.viewChanges({
			changed: 'Modified',
			site: site.name,
			title: layoutTitle,
			type: 'Page',
		});

		await changeTrackingPage.reviewChange(layoutTitle);

		await expect(page.getByText('Heading Example')).toBeVisible({
			timeout: 30000,
		});
		await expect(page.getByText('Edited Text')).toBeVisible({
			timeout: 30000,
		});

		await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.plid);
	}
);

test(
	'Publish page with content display fragment and web content display',
	{tag: '@LPS-185847'},
	async ({
		apiHelpers,
		changeTrackingPage,
		ctCollection,
		page,
		pageEditorPage,
	}) => {
		const site =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
				'guest'
			);

		// Add 2 basic web contents

		const journalName1 = getRandomString();
		const journalName2 = getRandomString();

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			content: journalName1,
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: journalName1},
		});

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			content: journalName2,
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: journalName2},
		});

		// Create a page in a publication

		await changeTrackingPage.workOnPublication(ctCollection);

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'content'},
			title: layoutTitle,
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Configure a web content display widget with a web content

		await pageEditorPage.addWidget(
			'Content Management',
			'Web Content Display'
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				exact: true,
				name: 'Configuration',
			}),
			trigger: page
				.locator('#wrapper')
				.getByRole('button', {name: 'Options'}),
		});

		await page
			.frameLocator('iframe[id="modalIframe"]')
			.getByRole('button', {name: 'Select'})
			.click();

		await page
			.frameLocator('iframe[title="Configuration"]')
			.frameLocator('iframe[title="Select Web Content"]')
			.getByText(journalName1)
			.click();

		await page
			.frameLocator('iframe[id="modalIframe"]')
			.getByRole('button', {name: 'Save'})
			.click();

		// Configure a content display fragment with a web content

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.addFragment('Content Display', 'Content Display');

		await page.getByLabel('Select Item').click();

		await page.waitForTimeout(300);

		await page.getByRole('menuitem', {name: 'Select Item'}).click();

		await page
			.frameLocator('iframe[title="Select"]')
			.getByRole('menuitem', {exact: true, name: 'Web Content'})
			.click();

		await page
			.frameLocator('iframe[title="Select"]')
			.getByText(journalName2)
			.click();

		await pageEditorPage.publishPage();

		await expect(page.getByText(journalName1)).toBeVisible();
		await expect(page.getByText(journalName1)).toBeVisible();

		await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.plid);
	}
);

testWithPrivatePages(
	'Publish private content page with fragment',
	async ({
		apiHelpers,
		changeTrackingPage,
		ctCollection,
		page,
		pageEditorPage,
		pagesAdminPage,
		site,
	}) => {
		await changeTrackingPage.workOnPublication(ctCollection);

		// Create a custom fragment

		const fragmentCollectionName = getRandomString();

		const {fragmentCollectionId} =
			await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
				{
					groupId: site.id,
					name: fragmentCollectionName,
				}
			);

		const fragmentEntryName = getRandomString();

		await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
			fragmentCollectionId,
			groupId: site.id,
			html: `<lfr-editable id="element-html" type="html">
					<h1>test html</h1>
				</lfr-editable>`,
			name: fragmentEntryName,
			type: 'component',
		});

		// Add a private layout and add custom fragment

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

		await pageEditorPage.addFragment(
			fragmentCollectionName,
			fragmentEntryName
		);

		await pageEditorPage.waitForChangesSaved();

		await pageEditorPage.publishPage();

		// Publish publication

		await apiHelpers.headlessChangeTracking.publishCTCollection(
			ctCollection.body.id
		);

		await changeTrackingPage.assertStatus(
			'Published',
			ctCollection.body.name
		);

		// Review published changes

		await changeTrackingPage.goToReviewChangesHistory(
			ctCollection.body.name
		);

		await changeTrackingPage.viewChanges({
			changed: 'Added',
			site: site.name,
			title: fragmentEntryName,
			type: 'Fragment Entry Link',
		});

		// View fragment in private page

		await page.goto(`/group${site.friendlyUrlPath}`);
		await page.getByRole('menuitem', {name: layoutTitle}).click();
		await expect(page.getByText('test html')).toBeVisible();
	}
);

test('Publish with asset publisher configuration', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	widgetPagePage,
}) => {
	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	// Create a page with an asset publisher

	const layoutTitle = getRandomString();

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: layoutTitle,
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Asset Publisher');

	// Create asset in asset publisher

	await changeTrackingPage.workOnPublication(ctCollection);

	await widgetPagePage.clickOnAction('Asset Publisher', 'Configuration');

	await page
		.frameLocator('iframe[title*="Configuration"]')
		.getByLabel('Dynamic')
		.click();

	await page.locator('.modal').getByLabel('Close', {exact: true}).click();

	await page.reload();

	await expect(page.getByText('Asset Publisher Basic')).toBeVisible();

	await page.waitForTimeout(300);

	await page
		.getByTestId('addButton')
		.getByRole('button', {name: 'Add'})
		.click();

	await page.getByRole('menuitem', {name: 'Basic Document'}).click();

	const fileName = getRandomString();

	await page.getByLabel('Title Required').fill(fileName);

	await page.waitForTimeout(300);

	await page.getByRole('button', {name: 'Publish'}).click();

	await page.waitForLoadState('load');

	// Review publication changes

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await changeTrackingPage.viewChanges({
		changed: 'Modified',
		site: site.name,
		title: layoutTitle,
		type: 'Page',
	});

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await changeTrackingPage.assertStatus('Published', ctCollection.body.name);

	// Assert changes are published

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);
	await expect(page.getByText(fileName)).toBeVisible();

	await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.plid);
});

test(
	'Publish with updated custom field values',
	{tag: '@LPS-186466'},
	async ({
		addCustomFieldPage,
		apiHelpers,
		changeTrackingPage,
		ctCollection,
		page,
		pagesAdminPage,
	}) => {
		const site =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
				'guest'
			);

		// Add a page

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: layoutTitle,
		});

		// Add a custom field

		const customField: TCustomField = {
			fieldName: getRandomString(),
			fieldType: 'inputField',
			resource: 'Page',
		};

		await addCustomFieldPage.addCustomField(customField);

		// Update the custom field in the page in production

		await changeTrackingPage.workOnProduction();

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.clickOnAction('Configure', layoutTitle);

		await page.getByRole('link', {name: 'Design'}).click();

		await page.getByRole('tab', {name: 'Advanced'}).click();

		const productionText = getRandomString();

		await page
			.locator(`.field[name*="${customField.fieldName}"]`)
			.fill(productionText);

		await page.getByRole('button', {name: 'Save'}).click();

		// Update the custom field in the page in a publication

		await changeTrackingPage.workOnPublication(ctCollection);

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.clickOnAction('Configure', layoutTitle);

		await page.getByRole('link', {name: 'Design'}).click();

		await page.getByRole('tab', {name: 'Advanced'}).click();

		const publicationText = getRandomString();

		await page
			.locator(`.field[name*="${customField.fieldName}"]`)
			.fill(publicationText);

		await page.getByRole('button', {name: 'Save'}).click();

		// Publish publication

		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await apiHelpers.headlessChangeTracking.publishCTCollection(
			ctCollection.body.id
		);

		await changeTrackingPage.assertStatus(
			'Published',
			ctCollection.body.name
		);

		// Assert custom field shows value from publication

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.clickOnAction('Configure', layoutTitle);

		await page.getByRole('link', {name: 'Design'}).click();

		await page.getByRole('tab', {name: 'Advanced'}).click();

		await expect(
			page.locator(`.field[name*="${customField.fieldName}"]`)
		).toHaveValue(publicationText);

		await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.plid);
	}
);

test(
	'Style content page',
	{tag: '@LPS-164162'},
	async ({
		apiHelpers,
		changeTrackingPage,
		ctCollection,
		page,
		pageEditorPage,
	}) => {
		const site =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
				'guest'
			);

		// Create page

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'content'},
			title: layoutTitle,
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Add a grid fragment with a button inside

		await pageEditorPage.addFragment('Layout Elements', 'Grid');

		await pageEditorPage.addFragment('Basic Components', 'Button');

		const middleGridColumn = page
			.locator('.page-editor__col__border')
			.nth(1);

		await dragAndDropElement({
			dragTarget: page.locator('[data-name="Button"]'),
			dropTarget: middleGridColumn,
		});

		await expect(
			middleGridColumn.locator('[data-name="Button"]')
		).toBeVisible();

		const buttonFragmentId = await pageEditorPage.getFragmentId('Button');

		await pageEditorPage.publishPage();

		// In publication change the style of the button

		await changeTrackingPage.workOnPublication(ctCollection);

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.changeFragmentSpacing(
			buttonFragmentId,
			'Margin Top',
			'2'
		);

		await pageEditorPage.publishPage();

		// Assert style is shown

		expect(
			await pageEditorPage.getFragmentStyle({
				fragmentId: buttonFragmentId,
				isTopperStyle: true,
				style: 'marginTop',
			})
		).toBe('8px');

		await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.plid);
	}
);

test(
	'Can edit content page from review changes screen',
	{tag: '@LPD-70037'},
	async ({
		apiHelpers,
		changeTrackingPage,
		ctCollection,
		page,
		pageEditorPage,
		site,
	}) => {
		await changeTrackingPage.workOnPublication(ctCollection);

		// Add a page with an HTML fragment

		const layoutTitle = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'content'},
			title: layoutTitle,
		});

		// Go to edit content page template from review changes

		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await changeTrackingPage.viewChanges({
			changed: 'Added',
			click: true,
			site: site.name,
			title: layoutTitle,
			type: 'Page',
		});

		await changeTrackingPage.gotoEditChanges(ctCollection.body.name);

		// Edit content page template and save

		await pageEditorPage.addFragment('Basic Components', 'HTML');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.locator(
				'//td[contains(@class,"publications-render-view-content")]'
			),
			trigger: pageEditorPage.publishButton,
		});
	}
);

test(
	'Can edit content page template from review changes screen',
	{tag: '@LPD-70037'},
	async ({
		changeTrackingPage,
		ctCollection,
		page,
		pageEditorPage,
		pageTemplatesPage,
		site,
	}) => {
		await changeTrackingPage.workOnPublication(ctCollection);

		// Create page template collection

		await pageTemplatesPage.goto(site.friendlyUrlPath);

		const pageTemplateCollectionName = getRandomString();

		await pageTemplatesPage.addPageTemplateCollection(
			pageTemplateCollectionName
		);

		// Create content page template

		const contentPageTemplateName = getRandomString();

		await pageTemplatesPage.addContentPageTemplate(contentPageTemplateName);

		await pageEditorPage.publishButton.click();

		await waitForAlert(
			page,
			'Success:The page template was published successfully.'
		);

		// Go to edit content page template from review changes

		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await changeTrackingPage.viewChanges({
			changed: 'Added',
			click: true,
			site: site.name,
			title: contentPageTemplateName,
			type: 'Layout Page Template Entry',
		});

		await changeTrackingPage.gotoEditChanges(ctCollection.body.name);

		// Edit content page template and save

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.locator(
				'//td[contains(@class,"publications-render-view-content")]'
			),
			trigger: pageEditorPage.publishButton,
		});
	}
);

test(
	'Can edit widget page template from review changes screen',
	{tag: '@LPD-70037'},
	async ({
		changeTrackingPage,
		ctCollection,
		page,
		pageTemplatesPage,
		site,
		widgetPagePage,
	}) => {
		await changeTrackingPage.workOnPublication(ctCollection);

		// Create page template collection

		await pageTemplatesPage.goto(site.friendlyUrlPath);

		const pageTemplateCollectionName = getRandomString();

		await pageTemplatesPage.addPageTemplateCollection(
			pageTemplateCollectionName
		);

		// Create widget page template

		const widgetPageTemplateName = getRandomString();

		await pageTemplatesPage.addWidgetPageTemplate(widgetPageTemplateName);

		// Go to edit content page template from review changes

		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await changeTrackingPage.viewChanges({
			changed: 'Added',
			click: true,
			site: site.name,
			title: widgetPageTemplateName,
			type: 'Layout Page Template Entry',
		});

		await changeTrackingPage.gotoEditChanges(ctCollection.body.name);

		// Edit widget page template

		await widgetPagePage.addPortlet('Web Content Display');

		await page.getByRole('link', {exact: true, name: 'Back'}).click();

		await expect(
			page.locator(
				'//td[contains(@class,"publications-render-view-content")]'
			)
		).toBeVisible();
	}
);
