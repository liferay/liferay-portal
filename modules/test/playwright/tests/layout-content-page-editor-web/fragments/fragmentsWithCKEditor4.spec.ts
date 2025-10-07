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
import getRandomString from '../../../utils/getRandomString';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPD-39304': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

test(
	'Can edit text editable with CKEditor 4',
	{tag: ['@LPS-127732']},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create page with a paragraph fragment and go to edit mode

		const fragmentId = getRandomString();

		const fragment = getFragmentDefinition({
			id: fragmentId,
			key: 'BASIC_COMPONENT-paragraph',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([fragment]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Check paragraph editable can be edited

		await pageEditorPage.editTextEditable(
			fragmentId,
			'element-text',
			'New editable fragment text'
		);

		await expect(
			page.getByText('New editable fragment text')
		).toBeAttached();
	}
);

test(
	'Can use CKEditor options when editing a rich text editable with CKEditor 4',
	{tag: ['@LPS-127732']},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create page with a paragraph fragment and go to edit mode

		const fragmentId = getRandomString();

		const fragment = getFragmentDefinition({
			id: fragmentId,
			key: 'BASIC_COMPONENT-paragraph',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([fragment]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Open editor options

		await pageEditorPage.selectEditable(fragmentId, 'element-text');

		const editable = pageEditorPage.getEditable({
			editableId: 'element-text',
			fragmentId,
		});

		await editable.click();

		await editable.locator('.cke_editable_inline').click();

		await page.keyboard.press('ControlOrMeta+KeyA');

		// Check that the button is visible and works

		await expect(page.getByTitle('Right')).toBeVisible();

		await page.getByTitle('Right').click();

		expect(
			await page
				.locator('.ae-editable p')
				.evaluate((element) => element.style.textAlign)
		).toBe('right');
	}
);

test(
	'Editor config contributor client extension is applied with CKEditor 4',
	{tag: ['@LPD-54262']},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create page with a paragraph fragment and go to edit mode

		const fragmentId = getRandomString();

		const fragment = getFragmentDefinition({
			id: fragmentId,
			key: 'BASIC_COMPONENT-paragraph',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([fragment]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Open editor options

		await pageEditorPage.selectEditable(fragmentId, 'element-text');

		const editable = pageEditorPage.getEditable({
			editableId: 'element-text',
			fragmentId,
		});

		await editable.dblclick();

		await editable.locator('.cke_editable_inline').dblclick();

		// Assert "Insert Video" button is visible as provided by the CX

		await page.getByText('A paragraph').selectText();

		await expect(page.getByTitle('Insert Video')).toBeInViewport();
	}
);
