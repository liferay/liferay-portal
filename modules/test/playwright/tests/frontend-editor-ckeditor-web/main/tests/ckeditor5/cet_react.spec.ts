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
import {classicPageTest} from './fixtures/classicPageTest';

export const test = mergeTests(
	apiHelpersTest,
	ckeditorSamplePageTest,
	classicPageTest,
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
	await ckeditorSamplePage.selectTab('React + CET');

	await waitForEditor({page});
});

test(
	'Editor configuration is applied, merging existing and client extension customizations',
	{tag: '@LPD-11235'},
	async ({classicPage}) => {
		const expectedButtons = [
			'Accessibility help',
			'Undo',
			'Redo',
			'Text alignment',
			'Text formatting',
			'Lists',
			'Bookmark',
			'Timestamp',
			'Enter fullscreen mode',
			'Hello',
		];

		const availableButtons =
			await classicPage.toolbar.buttonLabels.allInnerTexts();

		expect(availableButtons).toEqual(expectedButtons);
	}
);
