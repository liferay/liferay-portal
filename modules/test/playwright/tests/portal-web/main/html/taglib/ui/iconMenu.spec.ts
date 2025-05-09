/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../../fixtures/loginTest';
import {samplePageTest} from '../../../../../frontend-taglib/main/fixtures/samplePageTest';

export const test = mergeTests(
	isolatedSiteTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	samplePageTest
);

const linkName = 'Icon Menu';

test(
	'Overlay is removed from DOM after menu is closed',
	{
		tag: '@LPD-53924',
	},
	async ({page, samplePage, site}) => {
		const overlay = page.locator('.overlay');

		await test.step('Add taglib sample to page', async () => {
			await samplePage.setupSampleWidget({
				site,
			});

			await samplePage.selectLink(linkName);
		});

		await test.step('Set viewport for mobile resolution', async () => {
			page.setViewportSize({height: 720, width: 360});
		});

		await test.step('Open and close menu', async () => {
			await page.getByRole('button', {name: 'Sample Add'}).click();

			// Setup the handler to manage overlay

			await page.addLocatorHandler(
				page.getByText('First Menu Option'),
				async () => {
					await expect(overlay).toHaveCount(1);

					// Close menu by clicking anywhere in the overlay

					await page.locator('.yui3-widget-mask').click();
				}
			);
		});

		await test.step('Check overlay has been removed from DOM', async () => {
			await expect(overlay).toHaveCount(0);
		});
	}
);
