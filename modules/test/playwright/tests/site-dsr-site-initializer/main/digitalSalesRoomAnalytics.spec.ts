/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {digitalSalesRoomPagesTest} from '../../../fixtures/digitalSalesRoomPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';

export const test = mergeTests(
	dataApiHelpersTest,
	digitalSalesRoomPagesTest,
	featureFlagsTest({
		'LPD-35443': {enabled: true},
		'LPD-66359': {enabled: true},
	}),
	loginTest()
);

test.afterEach(async ({apiHelpers}) => {
	const rooms = await apiHelpers.headlessDigitalSalesRoom.getRooms();

	for (const room of rooms.items) {
		await apiHelpers.headlessDigitalSalesRoom.deleteRoom(room.id);
	}
});

async function createRoom({
	apiHelpers,
	digitalSalesRoomsPage,
	editDigitalSalesRoomPage,
}: {
	apiHelpers: any;
	digitalSalesRoomsPage: any;
	editDigitalSalesRoomPage: any;
}): Promise<string> {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		type: 'business',
	});

	const roomName = `A${getRandomInt()}`;

	await digitalSalesRoomsPage.goToRoomsPage();
	await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();
	await editDigitalSalesRoomPage.addDigitalSalesRoom({
		accountName: account.name,
		roomName,
	});

	return roomName;
}

test(
	'Navigate between Overview and Timeline analytics tabs',
	{tag: '@LPD-86504'},
	async ({dsrAnalyticsPage, page}) => {
		await dsrAnalyticsPage.goToOverview();

		await expect(dsrAnalyticsPage.analyticsHeading).toBeVisible();
		await expect(dsrAnalyticsPage.overviewTab).toBeVisible();
		await expect(dsrAnalyticsPage.timelineTab).toBeVisible();

		await expect(dsrAnalyticsPage.roomStatistics).toBeVisible();
		await expect(dsrAnalyticsPage.visitsFrequencyFrame).toBeVisible();
		await expect(dsrAnalyticsPage.mostActiveVisitorsFrame).toBeVisible();

		await dsrAnalyticsPage.timelineTab.click();
		await page.waitForURL(/view-timeline/);

		await expect(dsrAnalyticsPage.analyticsHeading).toBeVisible();
		await expect(dsrAnalyticsPage.engagementTimelineFrame).toBeVisible();
		await expect(dsrAnalyticsPage.activityLogFrame).toBeVisible();
		await expect(dsrAnalyticsPage.visitsFrequencyFrame).not.toBeVisible();

		await dsrAnalyticsPage.overviewTab.click();
		await page.waitForURL(/view-overview/);

		await expect(dsrAnalyticsPage.roomStatistics).toBeVisible();
		await expect(
			dsrAnalyticsPage.engagementTimelineFrame
		).not.toBeVisible();
	}
);

test(
	'Room dropdown lists All Rooms plus existing rooms and reflects selection',
	{tag: '@LPD-86504'},
	async ({
		apiHelpers,
		digitalSalesRoomsPage,
		dsrAnalyticsPage,
		editDigitalSalesRoomPage,
	}) => {
		const roomName = await createRoom({
			apiHelpers,
			digitalSalesRoomsPage,
			editDigitalSalesRoomPage,
		});

		await dsrAnalyticsPage.goToOverview();

		await expect(dsrAnalyticsPage.roomFilterTrigger).toHaveText(
			/All Rooms/
		);

		await dsrAnalyticsPage.openRoomFilter();

		await expect(
			dsrAnalyticsPage.roomFilterOption('All Rooms')
		).toBeVisible();
		await expect(dsrAnalyticsPage.roomFilterOption(roomName)).toBeVisible();

		await dsrAnalyticsPage.roomFilterOption(roomName).click();

		await expect(dsrAnalyticsPage.roomFilterTrigger).toHaveText(
			new RegExp(roomName)
		);

		await dsrAnalyticsPage.selectRoom('All Rooms');

		await expect(dsrAnalyticsPage.roomFilterTrigger).toHaveText(
			/All Rooms/
		);
	}
);

test(
	'Timeline chart exposes date range preset and custom range controls',
	{tag: '@LPD-86504'},
	async ({dsrAnalyticsPage}) => {
		await dsrAnalyticsPage.goToTimeline();

		await expect(dsrAnalyticsPage.filtersToolbar).toBeVisible();
		await expect(dsrAnalyticsPage.dateRangePresetSelect).toBeVisible();
		await expect(dsrAnalyticsPage.dateRangePicker).toBeVisible();

		const optionCount = await dsrAnalyticsPage.dateRangePresetSelect
			.locator('option')
			.count();

		expect(optionCount).toBeGreaterThan(1);

		const lastPreset = await dsrAnalyticsPage.dateRangePresetSelect
			.locator('option')
			.last()
			.getAttribute('value');

		if (lastPreset) {
			await dsrAnalyticsPage.selectDateRangePreset(lastPreset);

			await expect(dsrAnalyticsPage.dateRangePresetSelect).toHaveValue(
				lastPreset
			);
		}

		await dsrAnalyticsPage.dateRangePicker.fill('2025-10-01 - 2025-10-07');
		await dsrAnalyticsPage.dateRangePicker.blur();

		await expect(dsrAnalyticsPage.dateRangePicker).toHaveValue(
			'2025-10-01 - 2025-10-07'
		);
	}
);

test(
	'Timeline chart user filter depends on selected room',
	{tag: '@LPD-86504'},
	async ({
		apiHelpers,
		digitalSalesRoomsPage,
		dsrAnalyticsPage,
		editDigitalSalesRoomPage,
	}) => {
		const roomName = await createRoom({
			apiHelpers,
			digitalSalesRoomsPage,
			editDigitalSalesRoomPage,
		});

		await dsrAnalyticsPage.goToTimeline();

		await expect(dsrAnalyticsPage.userFilterTrigger).toBeVisible();
		await expect(dsrAnalyticsPage.userFilterTrigger).toHaveText(
			/All Users/
		);

		await dsrAnalyticsPage.openUserFilter();

		await expect(
			dsrAnalyticsPage.userFilterOption('All Users')
		).toBeVisible();

		await dsrAnalyticsPage.page.keyboard.press('Escape');

		await dsrAnalyticsPage.selectRoom(roomName);

		await expect(dsrAnalyticsPage.roomFilterTrigger).toHaveText(
			new RegExp(roomName)
		);

		await dsrAnalyticsPage.openUserFilter();

		await expect(
			dsrAnalyticsPage.userFilterOption('All Users')
		).toBeVisible();
	}
);
