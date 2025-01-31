/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../fixtures/changeTrackingPagesTest';
import {dataApiHelpersTest} from '../../fixtures/dataApiHelpersTest';
import {masterPagesPagesTest} from '../../fixtures/masterPagesPagesTest';
import {pageEditorPagesTest} from '../../fixtures/pageEditorPagesTest';
import {pageTemplatesPagesTest} from '../../fixtures/pageTemplatesPagesTest';
import {pageViewModePagesTest} from '../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../fixtures/pagesAdminPagesTest';
import {styleBookPageTest} from '../../fixtures/styleBookPageTest';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../utils/getRandomString';
import getBasicWebContentStructureId from '../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../utils/waitForAlert';
import {templatesPageTest} from '../template-web/fixtures/templatesPageTest';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	changeTrackingPagesTest,
	masterPagesPagesTest,
	pagesAdminPagesTest,
	pageEditorPagesTest,
	pageTemplatesPagesTest,
	styleBookPageTest,
	templatesPageTest,
	pageViewModePagesTest
);

test('Can Add and Apply Information Template in a Publication', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
	templatesPage,
}) => {
	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	// Add a basic web content

	const journalName = getRandomString();

	const basicWebContentStructureId =
		await getBasicWebContentStructureId(apiHelpers);

	await apiHelpers.jsonWebServicesJournal.addWebContent({
		ddmStructureId: basicWebContentStructureId,
		groupId: site.id,
		titleMap: {en_US: journalName},
	});

	// Go to templates administration

	await templatesPage.goto(site.friendlyUrlPath);

	// Create information template

	const informationTemplateName = getRandomString();

	await templatesPage.createInformationTemplate({
		itemSubtype: 'Basic Web Content',
		itemType: 'Web Content Article',
		name: informationTemplateName,
	});

	await page.getByRole('button', {name: 'Title'}).click();
	await templatesPage.saveTemplate();

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

	await changeTrackingPage.reviewChange(informationTemplateName);

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await changeTrackingPage.assertStatus('Published', ctCollection.body.name);

	// Assert changes are published and template is mapped in production

	await page.goto('/');
	await page.getByRole('menuitem', {name: layoutTitle}).click();
	await expect(page.getByText(journalName)).toBeVisible();
});

test('Can Add New Page With Master Template in a Publication', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	masterPagesPage,
	page,
	pageEditorPage,
}) => {
	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

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
		masterLayoutPlid: masterPageTemplate.plid,
		options: {type: 'content'},
		title: layoutTitle,
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.publishPage();

	// Review publication changes and publish

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await changeTrackingPage.reviewChange(masterPageTemplateEntryName);

	await changeTrackingPage.viewChanges({
		changed: 'Added',
		site: site.name,
		title: masterPageTemplateEntryName,
		type: 'Fragment Entry Link',
	});

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await changeTrackingPage.assertStatus('Published', ctCollection.body.name);

	// Verify that the fragment is present

	await page.goto('/');
	await page.getByRole('menuitem', {name: layoutTitle}).click();
	await expect(page.locator('.navbar-dark')).toBeVisible();
});

test('Apply Style Book Layout in a Publication', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
	styleBooksPage,
}) => {
	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	// Create a style book with times as Font Family Base

	const styleBookName = getRandomString();

	await styleBooksPage.goto();

	await styleBooksPage.create(styleBookName);

	await styleBooksPage.selectTokenCategory('Typography');

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

	await page.goto('/');
	await page.getByRole('menuitem', {name: layoutTitle}).click();
	await expect(page.getByText('HTML Example')).toHaveCSS(
		'font-family',
		'times'
	);
});

test('Can Add and Apply Widget Template in a Publication', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	templatesPage,
	widgetPagePage,
}) => {
	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	// Go to widget templates administration

	await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);

	// Create widget template

	const widgetTemplateName = getRandomString();

	await templatesPage.createWidgetTemplate(
		widgetTemplateName,
		'Language Selector Template'
	);

	await templatesPage.editTemplate(widgetTemplateName);

	await page.getByRole('button', {name: 'Locale'}).click();

	await templatesPage.saveTemplate();

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

	await changeTrackingPage.reviewChange(widgetTemplateName);

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

	await page.goto('/');
	await page.getByRole('menuitem', {name: layoutTitle}).click();
	await expect(page.getByText('en_US')).toBeVisible();
});

test('Can Add and Apply Content Template in a Publication', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
	pageTemplatesPage,
	pagesAdminPage,
}) => {
	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	// Go to page template administration

	await pageTemplatesPage.goto(site.friendlyUrlPath);

	// Create page template collection

	const pageTemplateCollectionName = getRandomString();

	await pageTemplatesPage.addPageTemplateCollection(
		pageTemplateCollectionName
	);

	// Create content page template

	const contentPageTemplateName = getRandomString();

	await pageTemplatesPage.addContentPageTemplate(contentPageTemplateName);

	// Add heading fragment and publish

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

	// Review publication changes and publish

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await changeTrackingPage.reviewChange(contentPageTemplateName);

	await changeTrackingPage.viewChanges({
		changed: 'Added',
		site: site.name,
		title: contentPageTemplateName,
		type: 'Layout Page Template Entry',
	});

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

test('Can Create Custom Fragments in a Publication', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
}) => {
	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

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

	await page.goto('/');
	await page.getByRole('menuitem', {name: layoutTitle}).click();
	await expect(page.getByText('test html')).toBeVisible();
});

test('Can Create Page with Existing Page Template in a Publication', async ({
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

	await changeTrackingPage.workOnProduction();

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

	// Add a widget page based on template in Publication

	await changeTrackingPage.workOnPublication(ctCollection);

	await pagesAdminPage.goto(site.friendlyUrlPath);

	await pagesAdminPage.gotoSelectTemplates(pageTemplateCollectionName);

	const layoutTitle = getRandomString();

	await pagesAdminPage.addPage({
		name: layoutTitle,
		template: widgetPageTemplateName,
	});

	// Publish Publication

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

	await pageTemplatesPage.goto(site.friendlyUrlPath);

	await pageTemplatesPage.deletePageTemplateCollection(
		pageTemplateCollectionName
	);
});
