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
	'Content is rendered in source editor when using full screen mode',
	{tag: ['@LPD-62410']},
	async ({page}) => {
		await test.step('Go to source mode and edit in full screen mode', async () => {
			await page.locator('.ae-editable').click();

			const switchModeButton = page.locator('[id$="AlloyEditorSwitch"]');

			await switchModeButton.waitFor({state: 'visible', timeout: 10000});

			switchModeButton.click();

			const fullScreenButton = page.locator(
				'[id$="AlloyEditorFullscreen"]'
			);

			await fullScreenButton.waitFor({state: 'visible', timeout: 10000});

			fullScreenButton.click();

			const fullScreenModal = page.locator(
				'.lfr-fullscreen-source-editor-dialog'
			);

			await fullScreenModal.waitFor({state: 'visible', timeout: 10000});

			await expect(fullScreenModal).toContainText('Lorem ipsum');
		});
	}
);
