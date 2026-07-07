/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import {claySamplePageTest} from './fixtures/claySamplePageTest';
import {TabName as ClaySamplePageTabs} from './pages/ClaySamplePage';

const test = mergeTests(
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	claySamplePageTest
);

test('When accessing all clay sample portlet tabs, then verifies that the components are compliant with axe accessibility.', async ({
	claySamplePage,
	page,
}) => {
	test.slow();

	const tabNames = Object.values(ClaySamplePageTabs);

	for (const tabName of tabNames) {
		await test.step(`Verifying ${tabName} accessibility`, async () => {
			await claySamplePage.selectTab(tabName);

			await checkAccessibility({
				page,
				selectors: [
					'.portlet-body > .lfr-tooltip-scope > .tab-content > .tab-pane.active',
				],
				selectorsToExclude: [
					'span[role="gridcell"].label-item-expand',
					'.card.disabled',
				],
			});
		});
	}
});
