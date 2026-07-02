/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {syncAnalyticsCloudViaAPI} from '../../analytics-settings-web/main/utils/analytics-settings';
import {switchChannel} from './utils/channel';
import {
	ACPage,
	navigateTo,
	navigateToACPageViaURL,
	navigateToACWorkspace,
} from './utils/navigation';
import {addBreakdownByIndividualAttribute} from './utils/utils';

export const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedChannelTest,
	isolatedSiteTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

test.beforeEach(async ({analyticsChannel, apiHelpers, project, site}) => {
	await syncAnalyticsCloudViaAPI({
		apiHelpers,
		channel: analyticsChannel,
		project,
		siteId: Number(site.id),
	});
});

test(
	'Empty state of no assets with data source and property.',
	{
		tag: '@LRAC-10405',
	},
	async ({analyticsChannel: channel, page, project}) => {
		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACWorkspace({page});
			await switchChannel({
				channelName: channel.name,
				page,
			});
		});

		await test.step('Go to Assets page, check empty state message', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.assetPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await expect(
				page.getByText('There are no assets found.')
			).toBeVisible();
			await expect(
				page.getByText(
					'Check back later to verify if data has been received from your data sources, or you can try a different date range.'
				)
			).toBeVisible();

			await page
				.getByRole('link', {name: 'Learn more about assets.'})
				.click();

			const newPage = await page.waitForEvent('popup');

			const href = newPage.url();

			await expect(href).toContain('learn.liferay.com');
			await expect(href).toContain('/assets-analytics');

			await newPage.close();
		});

		await test.step('Go to Event Analysis, check empty state message', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.eventAnalysisPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await expect(
				page.getByText('There are no analysis found.')
			).toBeVisible();
			await expect(
				page.getByText('Create an analysis to get started.')
			).toBeVisible();

			await page
				.getByRole('link', {
					name: 'Access our documentation to learn more.',
				})
				.click();

			const newPage = await page.waitForEvent('popup');

			const href = newPage.url();

			await expect(href).toContain('learn.liferay.com');
			await expect(href).toContain('/events');

			await newPage.close();
		});

		await test.step('Go to Segments, check empty state message', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.segmentPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await expect(
				page.getByText('There are no segments found.')
			).toBeVisible();
			await expect(
				page.getByText('Create a segment to get started.')
			).toBeVisible();

			await page
				.getByRole('link', {
					name: 'Access our documentation to learn more.',
				})
				.click();

			const newPage = await page.waitForEvent('popup');

			const href = newPage.url();

			await expect(href).toContain('learn.liferay.com');
			await expect(href).toContain('/people/segments');

			await newPage.close();
		});

		await test.step('Go to Individuals Overview', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.individualPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});
		});

		await test.step('Check Individuals card empty state messages', async () => {
			await expect(
				page.getByText('There is no data for active individuals.')
			).toBeVisible();
			await expect(
				page
					.locator(
						'[id="container\\.report\\.activeIndividualsCard"]'
					)
					.getByText('Check back later to verify if')
			).toBeVisible();

			await page
				.getByRole('link', {
					name: 'Learn more about active individuals.',
				})
				.click();

			const newPage = await page.waitForEvent('popup');

			const href = newPage.url();

			await expect(href).toContain('learn.liferay.com');
			await expect(href).toContain(
				'/people/individuals-analytics#active-individuals'
			);

			await newPage.close();
		});

		await test.step('Check Interests card empty state messages', async () => {
			await expect(
				page.getByText('There are no interests found.')
			).toBeVisible();
			await expect(
				page
					.locator(
						'[id="container\\.report\\.topInterestsAsOfYesterdayCard"]'
					)
					.getByText('Check back later to verify if')
			).toBeVisible();

			await page
				.getByRole('link', {name: 'Learn more about interests.'})
				.click();

			const newPage = await page.waitForEvent('popup');

			const href = newPage.url();

			await expect(href).toContain('learn.liferay.com');
			await expect(href).toContain(
				'/people/individuals-analytics#interests'
			);

			await newPage.close();
		});

		await test.step('Add a new breakdown', async () => {
			await addBreakdownByIndividualAttribute({
				breakdownName: 'Breakdown Name',
				individualAttribure: 'screenName',
				page,
			});
		});

		await test.step('Individual Overview distribution empty state message', async () => {
			await expect(
				page.getByText('There are no results found.')
			).toBeVisible();
			await expect(
				page.getByText('Try choosing a different breakdown.')
			).toBeVisible();

			await page
				.getByRole('link', {name: 'Learn more about distribution.'})
				.click();

			const newPage = await page.waitForEvent('popup');

			const href = newPage.url();

			await expect(href).toContain('learn.liferay.com');
			await expect(href).toContain(
				'/people/individuals-analytics#breakdown'
			);

			await newPage.close();
		});

		await test.step('Go to Interests tab, check empty state message', async () => {
			await navigateTo({
				page,
				pageName: 'Interests',
			});

			await expect(
				page.getByText('There are no interests found.')
			).toBeVisible();
			await expect(
				page.getByText(
					'Check back later to verify if data has been received from your data sources.'
				)
			).toBeVisible();

			await page
				.getByRole('link', {name: 'Learn more about interests.'})
				.click();

			const newPage = await page.waitForEvent('popup');

			const href = newPage.url();

			await expect(href).toContain('learn.liferay.com');
			await expect(href).toContain(
				'/people/individuals-analytics#interests'
			);

			await newPage.close();
		});

		await test.step('Go to Distribution tab, check empty state message', async () => {
			await navigateTo({
				page,
				pageName: 'Distribution',
			});

			await expect(
				page.getByText('There are no results found.')
			).toBeVisible();
			await expect(
				page.getByText('Try choosing a different breakdown.')
			).toBeVisible();

			await page
				.getByRole('link', {name: 'Learn more about distribution.'})
				.click();

			const newPage = await page.waitForEvent('popup');

			const href = newPage.url();

			await expect(href).toContain('learn.liferay.com');
			await expect(href).toContain(
				'/people/individuals-analytics#breakdown'
			);

			await newPage.close();
		});

		await test.step('Go to Tests, check empty state message', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.testPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await expect(
				page.getByText('There are no tests found.')
			).toBeVisible();
			await expect(
				page.getByText(
					'Create a new test from Liferay DXP by clicking on the icon in the toolbar when viewing a page in DXP.'
				)
			).toBeVisible();

			await page
				.getByRole('link', {name: 'Learn more about tests.'})
				.click();

			const newPage = await page.waitForEvent('popup');

			const href = newPage.url();

			await expect(href).toContain('learn.liferay.com');
			await expect(href).toContain('/optimization/ab-tests-analytics');

			await newPage.close();
		});
	}
);
