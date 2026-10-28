/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {gotoLatestLiferayDXPDataSource} from '../../osb-faro-web/main/utils/data-source';
import {
	PROPERTY_SITE_COLUMN_INDEX,
	expectPropertyColumn,
	goToSettingsStep,
	syncAnalyticsCloud,
	toggleSiteSync,
} from './utils/analytics-settings';

export const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-20640': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedChannelTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

async function expectSyncedSites({
	itemsSynced,
	page,
	status,
}: {
	itemsSynced: number;
	page: Page;
	status: 'Connected' | 'Disconnected';
}) {
	const sitesCard = page
		.getByRole('listitem')
		.filter({hasText: 'Represents the sites synced'});

	await expect(
		sitesCard.getByText(`${itemsSynced} Items Synced`)
	).toBeVisible();

	await expect(sitesCard.getByText(status, {exact: true})).toBeVisible();
}

test(
	'Modify the sites synchronized in the property review sidebar',
	{
		tag: '@LRAC-11044',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const site1 = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await syncAnalyticsCloud({
			apiHelpers,
			channel,
			page,
			project,
			siteName: site1.name,
		});

		const site2 = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await goToSettingsStep({
			page,
			stepName: 'Properties',
		});

		await expectPropertyColumn({
			channelName: channel.name,
			expectedValue: '1',
			index: PROPERTY_SITE_COLUMN_INDEX,
			page,
		});

		await toggleSiteSync({
			channelName: channel.name,
			page,
			siteName: site2.name,
		});

		await expectPropertyColumn({
			channelName: channel.name,
			expectedValue: '2',
			index: PROPERTY_SITE_COLUMN_INDEX,
			page,
		});
	}
);

test(
	'Assert that synced sites boolean properly reflects on Analytics Cloud after sites sync on DXP',
	{
		tag: '@LPD-69652',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const site1 = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const channelName1 = getRandomString();

		await syncAnalyticsCloud({
			apiHelpers,
			channelName: channelName1,
			page,
			siteName: site1.name,
		});

		await goToSettingsStep({
			page,
			stepName: 'Properties',
		});

		const site2 = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await toggleSiteSync({
			channelName: channel.name,
			page,
			siteName: site2.name,
		});

		await gotoLatestLiferayDXPDataSource(page, project);

		await expectSyncedSites({
			itemsSynced: 2,
			page,
			status: 'Connected',
		});

		await goToSettingsStep({
			page,
			stepName: 'Properties',
		});

		await toggleSiteSync({
			channelName: channel.name,
			page,
			siteName: site2.name,
			synced: false,
		});

		await gotoLatestLiferayDXPDataSource(page, project);

		await expectSyncedSites({
			itemsSynced: 1,
			page,
			status: 'Connected',
		});

		await goToSettingsStep({
			page,
			stepName: 'Properties',
		});

		await toggleSiteSync({
			channelName: channelName1,
			page,
			siteName: site1.name,
			synced: false,
		});

		await gotoLatestLiferayDXPDataSource(page, project);

		await expectSyncedSites({
			itemsSynced: 0,
			page,
			status: 'Disconnected',
		});
	}
);
