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

test.beforeEach('Select Panel tab', async ({claySamplePage}) => {
	await claySamplePage.selectTab(TabName.PANEL);
});

test(
	'Check role and aria-orientation is removed',
	{tag: '@LPD-30368'},
	async ({page}) => {
		await test.step('Check that role and aria-orientation is not present', async () => {
			const panelGroup = page.getByRole('heading', {name: 'PANEL GROUP'});

			await panelGroup.waitFor({state: 'visible'});

			const panelGroupElement = page.locator('.panel-group');

			await expect(panelGroupElement).not.toHaveAttribute(
				'role',
				'tablist'
			);

			await expect(panelGroupElement).not.toHaveAttribute(
				'aria-orientation',
				'vertical'
			);
		});
	}
);
