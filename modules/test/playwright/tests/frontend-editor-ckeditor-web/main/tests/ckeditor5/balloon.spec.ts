/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {waitForEditor} from '../../../../../utils/waitFor';
import {ckeditorSamplePageTest} from '../../fixtures/ckeditorSamplePageTest';
import {balloonPageTest} from './fixtures/balloonPageTest';

export const test = mergeTests(
	apiHelpersTest,
	balloonPageTest,
	ckeditorSamplePageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

test.beforeEach(async ({ckeditorSamplePage, page, site}) => {
	await ckeditorSamplePage.createAndGotoSitePage({site});

	await ckeditorSamplePage.selectTab('CKEditor 5');
	await ckeditorSamplePage.selectTab('Balloon');

	await waitForEditor({page});
});

test(
	'Toolbar contains all advanced preset controls',
	{tag: '@LPD-11235'},
	async ({balloonPage}) => {
		await expect(
			balloonPage.editable.getByText('Lorem ipsum dolor sit amet')
		).toBeVisible();

		await balloonPage.editable.selectText();

		await expect(balloonPage.toolbar).toBeVisible();

		const advancedPresetControls = [
			'Accessibility help',
			'Undo',
			'Redo',
			'Styles',
			'Normal',
			'Bold',
			'Italic',
			'Underline',
			'Strikethrough',
			'Font Color',
			'Font Background Color',
			'Remove Format',
			'Numbered List',
			'Bulleted List',
			'Increase indent',
			'Decrease indent',
			'Block quote',
			'Link',
			'Insert table',
			'Image',
			'Video',
			'Horizontal line',
			'Text alignment',
			'AI Creator',
		];

		const controls = await balloonPage.toolbar
			.getByRole('button')
			.locator('.ck-button__label')
			.allInnerTexts();

		expect(controls).toEqual(advancedPresetControls);
	}
);
