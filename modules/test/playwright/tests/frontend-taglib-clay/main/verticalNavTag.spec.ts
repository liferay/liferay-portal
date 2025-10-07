/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {claySamplePageTest} from './fixtures/claySamplePageTest';
import {TabName} from './pages/ClaySamplePage';

export const test = mergeTests(
	claySamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	})
);

test.beforeEach('Select Vertical Nav tab', async ({claySamplePage}) => {
	await claySamplePage.selectTab(TabName.VERTICAL_NAV);
});

test.describe('Vertical Nav apostrophes are not displayed correctly on vocabularies names', () => {
	test('Label is being escaped', {tag: '@LPD-30368'}, async ({page}) => {
		await test.step('Check that alert did not pop up', async () => {
			let alertText = '';

			page.on('dialog', (dialog) => {
				alertText = dialog.message();
				dialog.dismiss();
			});

			expect(alertText).toEqual('');
		});
	});
});
