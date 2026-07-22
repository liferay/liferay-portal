/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {createDataSource} from '../../osb-faro-web/main/utils/data-source';
import {acceptsCookiesBanner} from '../../osb-faro-web/main/utils/portal';
import {
	connectToAnalyticsCloud,
	disconnectFromAnalyticsCloud,
	findChannel,
	goToAnalyticsCloudInstanceSettings,
} from './utils/analytics-settings';

export const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-35443': {enabled: true},
	}),
	isolatedChannelTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

test.afterEach(async ({apiHelpers}) => {
	const rooms = await apiHelpers.headlessDigitalSalesRoom.getRooms();

	for (const room of rooms.items) {
		await apiHelpers.headlessDigitalSalesRoom.deleteRoom(room.id);
	}
});

test(
	'Digital Sales Room appears in the analytics property Sites tab',
	{tag: '@LPD-88808'},
	async ({analyticsChannel: channel, apiHelpers, page}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const roomName = `Room${getRandomInt()}`;

		await apiHelpers.headlessDigitalSalesRoom.addRoom({
			accountEntryId: account.id,
			name: roomName,
		});

		const {token} = await createDataSource(page);

		await goToAnalyticsCloudInstanceSettings(page);

		await acceptsCookiesBanner(page);

		await disconnectFromAnalyticsCloud(page);

		await connectToAnalyticsCloud(page, {token});

		const channelRow = await findChannel({channelName: channel.name, page});

		await channelRow.locator('button').click();

		await page.getByRole('tab', {name: 'Sites'}).click();

		await page
			.getByText(
				'Sites can only be assigned to a single property at a time'
			)
			.waitFor({state: 'visible'});

		await page.locator('.active').getByPlaceholder('Search').fill(roomName);

		await page
			.locator('.active')
			.getByRole('button', {name: 'Search'})
			.click();

		await expect(page.locator('span[data-testid="loading"]')).toBeHidden();

		await expect(
			page.locator('[data-testid="sites"]').getByRole('cell', {
				exact: true,
				name: roomName,
			})
		).toBeVisible();
	}
);
