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
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
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
	pageEditorPagesTest
);

const testWithCKEditor4 = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
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
			.getByText('Edit', {exact: true})
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
			.getByText('Edit', {exact: true});

		await editButton.waitFor();
		await editButton.click();

		await expect(page.getByText('Papa')).toBeVisible();
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
			.getByText('Edit', {exact: true})
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

		const toolbar = page.locator('.ck-toolbar');

		await toolbar.waitFor();

		await page.keyboard.press('Backspace');

		await page.keyboard.type('Papa');

		// Leave page while editing

		await page.getByLabel('Control Menu').locator('.lfr-back-link').click();

		// Go back to edit mode and check value was saved

		const editButton = page
			.getByLabel('Control Menu')
			.getByText('Edit', {exact: true});

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
