/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {samplePageTest} from '../../fixtures/samplePageTest';
import {TabName} from '../../pages/SamplePage';

export const test = mergeTests(
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	samplePageTest
);

test(
	'Tooltip should be translated correctly',
	{
		tag: '@LPD-43309',
	},
	async ({page, samplePage}) => {
		await test.step(`Select Fieldset tab`, async () => {
			await samplePage.selectTab(TabName.FIELDSET);
		});

		await test.step('Check tooltip is translated', async () => {
			const svgElement = page
				.locator('svg[aria-label="Help Text"]')
				.first();

			await expect(svgElement).toBeHidden();
		});
	}
);
