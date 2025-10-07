/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {EFDSVisualizationMode, waitForFDS} from '../../../../../utils/waitFor';
import {fdsSamplePageTest} from '../../fixtures/fdsSamplePageTest';

const test = mergeTests(
	apiHelpersTest,
	fdsSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

test.beforeEach(async ({fdsSamplePage, page, site}) => {
	await fdsSamplePage.setupFDSSampleWidget({site});

	await fdsSamplePage.selectTab('Advanced');

	await waitForFDS({page, visualizationMode: EFDSVisualizationMode.TABLE});
});

test(
	'actionLink renderer',
	{tag: ['@LPD-56306']},
	async ({fdsSamplePage, page}) => {
		await test.step('Check that if the configuration defines an actionId, that action URL is rendered', async () => {
			await test.step('Check that the "ID" cell on the first 5 rows renders with the link "#test-pencil"', async () => {
				for (let i = 0; i < 5; i++) {
					const idCell = fdsSamplePage.table.bodyRows
						.nth(i)
						.locator('.cell-id');

					await expect(idCell.locator('a')).toHaveAttribute(
						'href',
						'#test-pencil'
					);
				}
			});
		});

		await test.step('Check that if the configuration does not define an actionId, the first visible link is rendered', async () => {
			await test.step('Check that the "Title" cell with color "Blue" renders with the link "#"', async () => {
				const blueRow = fdsSamplePage.table.bodyRows
					.filter({
						has: page.locator('td.cell-color', {hasText: 'Blue'}),
					})
					.first();

				const titleCell = blueRow.locator('.cell-title');

				await expect(titleCell.locator('a')).toHaveAttribute(
					'href',
					'#'
				);
			});

			await test.step('Check that the "Title" cell with color "Yellow" renders with the link "#test-visibility-filter"', async () => {
				const yellowRow = fdsSamplePage.table.bodyRows
					.filter({
						has: page.locator('td.cell-color', {
							hasText: 'Yellow',
						}),
					})
					.first();

				const titleCell = yellowRow.locator('.cell-title');

				await expect(titleCell.locator('a')).toHaveAttribute(
					'href',
					'#test-visibility-filter'
				);
			});
		});
	}
);
