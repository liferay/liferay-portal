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
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import chooseFileFromDocumentLibrary from './utils/chooseFileFromDocumentLibrary';
import getFragmentDefinition from './utils/getFragmentDefinition';
import getPageDefinition from './utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest
);

const testWithCKEditor4 = mergeTests(
	test,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPS-178052': {enabled: true},
	})
);

// Remove when the feature flag LPD-11235 is removed

testWithCKEditor4(
	'Saves edited content when leaving page while editing with CKEditor 4',
	{
		tag: ['@LPD-40982', '@LPD-48256'],
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page with a Paragraph fragment and go to view mode

		const paragraphId = getRandomString();
		const paragraphDefinition = getFragmentDefinition({
			id: paragraphId,
			key: 'BASIC_COMPONENT-paragraph',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([paragraphDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		// Go to edit mode

		await page
			.getByLabel('Control Menu')
			.getByTitle('Edit', {exact: true})
			.click();

		await page.locator('.page-editor').waitFor();

		// Write text in editable

		await pageEditorPage.selectFragment(paragraphId);

		await pageEditorPage.selectEditable(paragraphId, 'element-text');

		const editable = pageEditorPage.getEditable({
			editableId: 'element-text',
			fragmentId: paragraphId,
		});

		await editable.click();

		await editable.locator('.cke_editable_inline').waitFor();

		await editable.locator('.cke_editable_inline').click();

		// Clear current content and fill with new one

		await page.keyboard.press('ControlOrMeta+KeyA');

		// Check toolbar appears

		await page.locator('.ae-toolbar-styles').waitFor();

		await page.keyboard.press('Backspace');

		await page.keyboard.type('Papa');

		// Leave page while editing

		await page.getByLabel('Control Menu').locator('.lfr-back-link').click();

		// Go back to edit mode and check value was saved

		const editButton = page
			.getByLabel('Control Menu')
			.getByTitle('Edit', {exact: true});

		await editButton.waitFor();
		await editButton.click();

		await expect(page.getByText('Papa')).toBeVisible();
	}
);

testWithCKEditor4(
	'Checks the functionality of splitting table cells in CKEditor 4',
	{tag: ['@LPD-65783']},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page with a Paragraph fragment containing a 1-cell table and go to edit mode

		const paragraphId = getRandomString();
		const paragraphDefinition = getFragmentDefinition({
			fragmentFields: [
				{
					id: 'element-text',
					value: {
						fragmentLink: {},
						text: {
							value_i18n: {
								en_US: '<table border="1" style="width: 100%;"><tbody><tr><td><br></td></tr></tbody></table>',
							},
						},
					},
				},
			],
			id: paragraphId,
			key: 'BASIC_COMPONENT-paragraph',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([paragraphDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await page
			.getByLabel('Control Menu')
			.getByTitle('Edit', {exact: true})
			.click();

		await page.locator('.page-editor').waitFor();

		// Edit the table and split the cell horizontally

		await pageEditorPage.selectFragment(paragraphId);

		await pageEditorPage.selectEditable(paragraphId, 'element-text');

		const editable = pageEditorPage.getEditable({
			editableId: 'element-text',
			fragmentId: paragraphId,
		});

		await editable.click();

		await editable.locator('.cke_editable_inline').waitFor();

		await editable.locator('.cke_editable_inline').click();

		await page.getByLabel('Cell').click();

		await page.getByLabel('Split Cell Horizontally').click();

		const html = await page.locator('.cke_editable table').innerHTML();

		expect(html).toContain(
			'<tbody><tr><td><br></td><td><br></td></tr></tbody>'
		);
	}
);

test(
	'Saves edited content when leaving page while editing',
	{
		tag: ['@LPD-40982', '@LPD-48256'],
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page with a Paragraph fragment and go to view mode

		const paragraphId = getRandomString();
		const paragraphDefinition = getFragmentDefinition({
			id: paragraphId,
			key: 'BASIC_COMPONENT-paragraph',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([paragraphDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		// Go to edit mode

		await page
			.getByLabel('Control Menu')
			.getByTitle('Edit', {exact: true})
			.click();

		await page.locator('.page-editor').waitFor();

		// Write text in editable

		await pageEditorPage.selectFragment(paragraphId);

		await pageEditorPage.selectEditable(paragraphId, 'element-text');

		const editable = pageEditorPage.getEditable({
			editableId: 'element-text',
			fragmentId: paragraphId,
		});

		await editable.click();

		const editor = editable.locator('[contenteditable="true"]');

		await editor.waitFor();

		await editor.click();

		// Clear current content and fill with new one

		await page.keyboard.press('ControlOrMeta+KeyA');

		// Check toolbar appears

		const toolbar = page.locator('.ck-toolbar', {
			hasText: 'Text alignment',
		});

		await toolbar.waitFor();

		await page.keyboard.press('Backspace');

		await page.keyboard.type('Papa');

		// Leave page while editing

		await page.getByLabel('Control Menu').locator('.lfr-back-link').click();

		// Go back to edit mode and check value was saved

		const editButton = page
			.getByLabel('Control Menu')
			.getByTitle('Edit', {exact: true});

		await editButton.waitFor();
		await editButton.click();

		await expect(page.getByText('Papa')).toBeVisible();
	}
);

test(
	'Value of editable field should be reset when the mapped content is missing',
	{
		tag: '@LPS-110462',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Add web content

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const basicWebContentTitle = getRandomString();

		const {articleId: basicWebContentId} =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: basicWebContentTitle},
			});

		// Add content page

		const headingId = getRandomString();

		const headingDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([headingDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Go to edit mode and map web content

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.selectEditable(headingId, 'element-text');

		await pageEditorPage.setMappedItem({
			entity: 'Web Content',
			entry: basicWebContentTitle,
			field: 'Title',
		});

		await expect(page.locator('.component-heading')).toHaveText(
			basicWebContentTitle
		);

		// Delete web content

		expect(
			await apiHelpers.jsonWebServicesJournal.moveArticleToTrash(
				site.id,
				basicWebContentId
			)
		).toHaveProperty('articleId');

		// Reload edit mode and assert

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await expect(page.locator('.component-heading')).not.toHaveText(
			basicWebContentTitle
		);

		await expect(page.locator('.component-heading')).toHaveText(
			'Heading Example'
		);
	}
);

test(
	'It is not possible to select editables when multiselect is enabled',
	{
		tag: '@LPD-47348',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page with a Heading and a Card fragment

		const headingId = getRandomString();
		const headingDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const cardId = getRandomString();
		const cardDefinition = getFragmentDefinition({
			id: cardId,
			key: 'BASIC_COMPONENT-card',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				headingDefinition,
				cardDefinition,
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Go to edit mode of page

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Go to Browser panel and expand Card

		await pageEditorPage.goToSidebarTab('Browser');

		await clickAndExpectToBeVisible({
			target: page.locator('.page-editor__page-structure__tree-node', {
				hasText: '02-title',
			}),
			trigger: page.locator('.page-editor__page-structure__tree-node', {
				hasText: 'Card',
			}),
		});

		// Select editable

		await clickAndExpectToBeVisible({
			target: page.locator('.breadcrumb-link', {
				hasText: '02-title',
			}),
			trigger: page.locator('.page-editor__page-structure__tree-node', {
				hasText: '02-title',
			}),
		});

		// Enable multiselect

		await page.keyboard.down('Control');

		// Check editable is deselected if we select the heading

		await clickAndExpectToBeHidden({
			target: page.locator('.breadcrumb-link', {
				hasText: '02-title',
			}),
			trigger: page.locator('.page-editor__page-structure__tree-node', {
				hasText: 'Heading',
			}),
		});

		await expect(page.getByText('2 Items Selected')).not.toBeVisible();

		// Now check parent is selected if trying to multiselect the editable

		await clickAndExpectToBeVisible({
			target: page.locator('.page-editor__topper__title', {
				hasText: 'Card',
			}),
			trigger: page.locator('.page-editor__page-structure__tree-node', {
				hasText: '02-title',
			}),
		});

		await expect(page.getByText('2 Items Selected')).toBeVisible();
	}
);

test(
	'Add an image to an editable and check that by default the image has the alt attribute empty',
	{
		tag: '@LPD-56399',
	},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a page with a Paragraph fragment

		const paragraphId = getRandomString();
		const paragraphDefinition = getFragmentDefinition({
			id: paragraphId,
			key: 'BASIC_COMPONENT-paragraph',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([paragraphDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Go to edit mode and add an image inside the paragraph

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.selectEditable(paragraphId, 'element-text');

		const editable = pageEditorPage.getEditable({
			editableId: 'element-text',
			fragmentId: paragraphId,
		});

		await editable.click();

		await editable.locator('[contenteditable="true"]').click();

		const blockToolbarButton = page.locator('.ck-block-toolbar-button', {
			hasText: 'Add',
		});

		await blockToolbarButton.waitFor();

		await expect(blockToolbarButton).toHaveAttribute('draggable', 'false');

		await blockToolbarButton.click();

		const blockToolbar = page.locator('.ck-toolbar');

		await blockToolbar.waitFor();

		await chooseFileFromDocumentLibrary({
			fileName: 'balinese.jpg',
			page,
			trigger: blockToolbar.getByLabel('Image', {exact: true}),
			type: 'image',
		});

		// Check that the image has an empty alt

		const image = page.locator('.component-paragraph img');

		await expect(image).toHaveAttribute('alt', '');
	}
);

test(
	'Check the keyboard interactions inside the editor and the blur behavior',
	{
		tag: '@LPD-56399',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page with a Paragraph and Heading fragments

		const headingId = getRandomString();
		const headingDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const paragraphId = getRandomString();
		const paragraphDefinition = getFragmentDefinition({
			id: paragraphId,
			key: 'BASIC_COMPONENT-paragraph',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				headingDefinition,
				paragraphDefinition,
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Pressing the key introduces line breaks and the escape key destroys
		// the editor for a rich text editable

		await pageEditorPage.selectEditable(paragraphId, 'element-text');

		const richTexteditable = pageEditorPage.getEditable({
			editableId: 'element-text',
			fragmentId: paragraphId,
		});

		await expect(richTexteditable.locator('p')).toHaveCount(0);

		await richTexteditable.click();

		let editor = richTexteditable.locator('[contenteditable="true"]');

		await editor.click();

		await page.keyboard.press('Enter');
		await page.keyboard.press('Enter');
		await page.keyboard.press('Escape');

		await expect(editor).not.toBeAttached();

		await expect(page.locator('.component-paragraph p')).toHaveCount(3);

		// Pressing the key does not introduce line breaks and clicking outside
		// the editor destroys the editor for a text editable

		await pageEditorPage.selectEditable(headingId, 'element-text');

		const texteditable = pageEditorPage.getEditable({
			editableId: 'element-text',
			fragmentId: headingId,
		});

		await expect(texteditable.locator('p')).toHaveCount(0);

		await texteditable.click();

		editor = texteditable.locator('[contenteditable="true"]');

		await editor.click();

		await page.keyboard.press('Enter');
		await page.keyboard.press('Enter');
		await page
			.locator('header.page-editor__disabled-area')
			.click({force: true});

		await expect(editor).not.toBeAttached();

		await expect(page.locator('.component-heading p')).toHaveCount(0);
	}
);

test(
	'Prevent the drag preview from appearing when dragging text inside the editor',
	{
		tag: '@LPD-56399',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page with a Paragraph fragment and select it

		const html =
			'<p><strong>List:</strong></p><ul><li><a href="option1Link">option1</a></li><li>option2</li><li>option3</li></ul>';

		const paragraphId = getRandomString();
		const paragraphDefinition = getFragmentDefinition({
			fragmentFields: [
				{
					id: 'element-text',
					value: {
						fragmentLink: {},
						text: {
							value_i18n: {
								en_US: html,
							},
						},
					},
				},
			],
			id: paragraphId,
			key: 'BASIC_COMPONENT-paragraph',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([paragraphDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		const editable = pageEditorPage.getEditable({
			editableId: 'element-text',
			fragmentId: paragraphId,
		});

		await editable.waitFor();

		const sidebarHeader = page
			.getByLabel('Components Panel')
			.locator('header');

		await expect(async () => {
			await page.keyboard.press('Escape');
			await page.mouse.up();
			await sidebarHeader.click({timeout: 1000});

			// Edit the editable

			await pageEditorPage.selectEditable(paragraphId, 'element-text');

			await editable.click({timeout: 1000});

			const editor = editable.locator('[contenteditable="true"]');

			await editor.waitFor({timeout: 2000});

			await editor.click({timeout: 1000});

			const paragraphFragment = page.locator('.component-paragraph');

			await expect(paragraphFragment).toHaveText(
				'List:option1option2option3',
				{timeout: 1000}
			);

			// Drag the selected text

			const option3 = page.getByText('option3');

			await option3.selectText({timeout: 1000});

			await expect(
				page.getByLabel('Editor contextual toolbar')
			).toBeVisible({
				timeout: 2000,
			});

			await option3.hover({timeout: 1000});

			await page.mouse.down();

			await sidebarHeader.hover({timeout: 1000});

			await page.waitForTimeout(3000);

			await expect(page.locator('.drag-preview')).not.toBeAttached({
				timeout: 1000,
			});
		}).toPass();
	}
);
