/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {syncAnalyticsCloudViaAPI} from '../../analytics-settings-web/main/utils/analytics-settings';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {
	ACPage,
	navigateToACPageViaURL,
} from '../../osb-faro-web/main/utils/navigation';
import {
	checkEmptyStateOnDXPSide,
	createABTest,
	createVariant,
	openABTesSidebar,
	runTest,
	terminateTest,
} from './utils/ab-test';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-78863': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedChannelTest,
	isolatedSiteTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

// Creates a synced content page and opens the A/B Test sidebar on it

async function setUpABTestPage({
	apiHelpers,
	channel,
	page,
	project,
	site,
}: {
	apiHelpers: any;
	channel: any;
	page: Page;
	project: any;
	site: any;
}) {
	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-heading',
			}),
		]),
		siteId: site.id,
		title: 'My Page',
	});

	await syncAnalyticsCloudViaAPI({
		apiHelpers,
		channel,
		project,
		siteId: Number(site.id),
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

	await page.waitForSelector('.segments-experiment-icon');

	await openABTesSidebar(page);
}

test(
	'A terminated AB test deleted on the DXP side clears the test list in both DXP and Analytics Cloud',
	{
		tag: '@LRAC-14544',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: 'My Page',
		});

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel,
			project,
			siteId: Number(site.id),
		});

		const testName = 'AB Test ' + getRandomString();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await page.waitForSelector('.segments-experiment-icon');

		await openABTesSidebar(page);

		await createABTest({name: testName, page});

		await createVariant({name: 'Variant name', page});

		await runTest(page);

		// The running test appears in the Analytics Cloud Tests list

		await navigateToACPageViaURL({
			acPage: ACPage.testPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await expect(page.getByText(testName)).toBeVisible();

		// Back on DXP, terminate and delete the test

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await openABTesSidebar(page);

		await terminateTest(page);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('button', {name: 'Delete'}),
			trigger: page.getByTestId('delete-variant'),
		});

		await waitForAlert(page);

		await checkEmptyStateOnDXPSide(page);

		// The Analytics Cloud Tests list is empty again

		await navigateToACPageViaURL({
			acPage: ACPage.testPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await expect(page.getByText('There are no tests found.')).toBeVisible();
	}
);

test(
	'Terminating an AB test and creating a new one reflects both statuses in Analytics Cloud',
	{
		tag: '@LRAC-14543',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project, site}) => {
		await setUpABTestPage({apiHelpers, channel, page, project, site});

		const firstTestName = 'AB Test ' + getRandomString();

		await createABTest({name: firstTestName, page});

		await createVariant({name: 'Variant name', page});

		await runTest(page);

		await terminateTest(page);

		// Create a second test from the terminated panel

		await page.getByText('Create New Test').click();

		const modal = page.locator('.modal-content');

		const secondTestName = 'AB Test 2 ' + getRandomString();

		await modal.getByLabel('Test Name').fill(secondTestName);

		await modal.getByText('Save').click();

		await waitForAlert(page);

		await createVariant({name: 'Variant name 2', page});

		await runTest(page);

		// Analytics Cloud lists the first test terminated and the second running

		await navigateToACPageViaURL({
			acPage: ACPage.testPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await expect(
			page
				.getByRole('row', {name: firstTestName})
				.getByText(/terminated/i)
		).toBeVisible();

		await expect(
			page.getByRole('row', {name: secondTestName}).getByText(/running/i)
		).toBeVisible();
	}
);
