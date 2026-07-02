/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {ACPage, navigateToACPageViaURL} from './utils/navigation';
import {CardSelectors} from './utils/selectors';
import {changeTimeFilter} from './utils/time-filter';

export const test = mergeTests(
	apiHelpersTest,
	isolatedChannelTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

test(
	'Verify that the time range filters keeps its selected values after clicking the download button in DownloadPDFReport modal',

	{
		tag: '@LPD-44478',
	},
	async ({analyticsChannel: channel, page, project}) => {
		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.sitePage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});
		});

		await test.step('Change time filter in Metrics Card to Last 24 Hours', async () => {
			await changeTimeFilter({
				cardSelector: CardSelectors.Metrics,
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Change time filter in Top Pages Card to Last 7 Days', async () => {
			await changeTimeFilter({
				cardSelector: CardSelectors.TopPages,
				page,
				timeFilterPeriod: 'Last 7 days',
			});
		});

		await test.step('Change time filter in Acquisition Card to Last 90 Days', async () => {
			await changeTimeFilter({
				cardSelector: CardSelectors.Acquisition,
				page,
				timeFilterPeriod: 'Last 90 days',
			});
		});

		await test.step('Change time filter in Search Terms to Custom Range and set a date range', async () => {
			await changeTimeFilter({
				cardSelector: CardSelectors.SearchTerms,
				page,
				timeFilterPeriod: 'Custom Range',
			});

			// Move to the previous month so the picked days are always in the
			// past. The calendar disables the current day and any future date,
			// so hardcoding days from the current month flakes when the test
			// runs on the first days of a month.

			await page.getByTestId('previous-month').first().click();

			await page
				.getByRole('button', {exact: true, name: '2'})
				.first()
				.click();

			await page
				.getByRole('button', {exact: true, name: '3'})
				.first()
				.click();
		});

		await test.step('Open Download Reports Modal and click in the download button', async () => {
			await page.getByRole('button', {name: 'Download Reports'}).click();

			await page.getByTestId('submit').click();
		});

		await test.step('Check if Time Range filters still have the previously selected values', async () => {
			await expect(
				page
					.locator(CardSelectors.Metrics)
					.getByRole('button', {name: 'Last 24 hours'})
			).toBeVisible();

			await expect(
				page
					.locator(CardSelectors.TopPages)
					.getByRole('button', {name: 'Last 7 days'})
			).toBeVisible();

			await expect(
				page
					.locator(CardSelectors.Acquisition)
					.getByRole('button', {name: 'Last 90 days'})
			).toBeVisible();

			await expect(
				page
					.locator(CardSelectors.SearchTerms)
					.getByRole('button', {exact: false, name: '2'})
			).toBeVisible();
		});
	}
);
