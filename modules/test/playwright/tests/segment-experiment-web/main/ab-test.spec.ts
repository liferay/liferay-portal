/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {liferayConfig} from '../../../liferay.config';
import getRandomString from '../../../utils/getRandomString';
import {
	connectToAnalyticsCloudWithNoSiteSynced,
	syncAnalyticsCloud,
} from '../../analytics-settings-web/main/utils/analytics-settings';
import {faroConfig} from '../../osb-faro-web/main/faro.config';
import {clickOnLink} from '../../osb-faro-web/main/utils/actions';
import {
	createSitePage,
	navigateToDXPandDeleteSite,
	navigateToSitePage,
} from '../../osb-faro-web/main/utils/portal';
import {openABTesSidebar} from './utils/ab-test';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-78863': {enabled: true, system: true},
		'LPS-178052': {enabled: true},
	}),
	loginAnalyticsCloudTest(),
	loginTest()
);

test(
	'Sync button on empty state should redirect the user to Analytics Cloud when site is not synced',
	{
		tag: '@LPD-34179',
	},
	async ({page}) => {
		await connectToAnalyticsCloudWithNoSiteSynced(page);

		await page.goto(liferayConfig.environment.baseUrl);

		await openABTesSidebar(page);

		expect(
			await page.getByText('Sync to Liferay Analytics Cloud')
		).toBeVisible();

		const tagA = await page.locator(
			'#_com_liferay_segments_experiment_web_internal_portlet_SegmentsExperimentPortlet_-segments-experiment-root a'
		);

		const href = (await tagA.getAttribute('href')) || '';

		await page.goto(
			href.replace(
				'http://localhost:8080',
				faroConfig.environment.baseUrl
			)
		);

		const title = await page.locator('h1.title');

		expect(await title.textContent()).toBe('Sites');
	}
);

test(
	'Terminated test with no clear winner variant',
	{
		tag: '@LPD-34179',
	},
	async ({apiHelpers, page}) => {
		const channelName = 'My Property - ' + getRandomString();

		const siteName = 'My Site ' + getRandomString();

		const site = await apiHelpers.headlessSite.createSite({
			name: siteName,
		});

		const pageTitle = 'My Page';

		let channel;
		let project;

		try {
			await createSitePage({
				apiHelpers,
				pageTitle,
				siteName,
			});

			const result = await syncAnalyticsCloud({
				apiHelpers,
				channelName,
				page,
				siteName: site.name,
			});

			channel = result.channel;
			project = result.project;

			await navigateToSitePage({
				page,
				pageName: pageTitle,
				siteName,
			});

			await page.waitForSelector('.segments-experiment-icon');

			await openABTesSidebar(page);

			await page.getByText('Create Test').click();

			await page.getByLabel('Test Name').fill('AB Test');

			await page.getByText('Save').click();

			await page.getByText('Create Variant').click();

			await page.getByLabel('Name').fill('AB Test Variant');

			await page.getByText('Save').click();

			await page.getByText('Review and Run Test').click();

			await page.locator('.modal-item-last').getByText('Run').click();

			await page.locator('.modal-item-last').getByText('OK').click();

			await page.getByText('Terminate Test').click();

			await page
				.locator('.modal-item-last')
				.getByText('Terminate')
				.click();

			expect(
				await page
					.locator('.alert-warning')
					.getByText(
						'The test has not gathered sufficient data to confidently determine a winner. However, variants can still be published.'
					)
			).toBeVisible();

			const segmentExperimentDetails = await page
				.locator('.segments-experiment-details .c-my-2')
				.all();

			expect(segmentExperimentDetails.length).toBe(3);

			expect(await segmentExperimentDetails[0].textContent()).toBe(
				'Segment:Anyone'
			);
			expect(await segmentExperimentDetails[1].textContent()).toBe(
				'Goal:Bounce Rate'
			);
			expect(await segmentExperimentDetails[2].textContent()).toBe(
				'Confidence Level:95%'
			);

			await clickOnLink({
				baseUrl: faroConfig.environment.baseUrl,
				name: 'View Data in Analytics Cloud',
				page,
			});

			await page.waitForTimeout(3000);

			expect(await page.getByText('Test Was Terminated')).toBeVisible();
			expect(
				await page.getByText('There is no clear winner.')
			).toBeVisible();
		}
		finally {
			if (channel && project) {
				await test.step('Delete the property that was used during automation execution', async () => {
					await apiHelpers.jsonWebServicesOSBFaro.deleteChannel(
						`[${channel.id}]`,
						project.groupId
					);
				});
			}

			if (site) {
				await test.step('delete site on DXP side', async () => {
					await navigateToDXPandDeleteSite({apiHelpers, page, site});
				});
			}
		}
	}
);
