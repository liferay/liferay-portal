/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {EEditorType, waitForEditor} from '../../../utils/waitFor';
import {ckeditorSamplePageTest} from '../../frontend-editor-ckeditor-web/main/fixtures/ckeditorSamplePageTest';

export const test = mergeTests(
	apiHelpersTest,
	ckeditorSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

test.beforeEach(async ({ckeditorSamplePage, page, site}) => {
	await ckeditorSamplePage.createAndGotoSitePage({site});

	await ckeditorSamplePage.selectTab('CKEditor 4');
	await ckeditorSamplePage.selectTab('Alloy');

	await waitForEditor({editorType: EEditorType.ALLOYEDITOR, page});
});

test(
	'AlloyEditor table allows multiple selection',
	{tag: ['@LPD-66079']},
	async ({page}) => {
		const addButton = page
			.locator('.ae-container')
			.getByRole('button', {name: 'Add'});

		const tableButton = page
			.locator('.ae-container')
			.getByRole('button', {name: 'Insert Table'});

		const editorTable = page.locator('.alloy-editor-container table');

		await test.step('Add a table inside AlloyEditor and open context menu for a cell', async () => {
			await page.locator('.ae-editable').click();

			await addButton.waitFor({state: 'visible', timeout: 1000});

			await addButton.click();

			await tableButton.waitFor({state: 'visible', timeout: 1000});

			await tableButton.click();

			await page.getByLabel('Confirm').click();

			await expect(editorTable).toBeVisible();
		});

		await test.step('Open table context menu for a cell and assert is tableSelection plugin menu', async () => {
			editorTable.click({button: 'right'});

			const tableMenu = page.locator('.cke_menu_panel');

			await expect(tableMenu).toBeVisible();
		});
	}
);
