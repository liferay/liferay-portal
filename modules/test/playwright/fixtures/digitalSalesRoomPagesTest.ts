/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {DSRAnalyticsPage} from '../pages/digital-sales-room-web/DSRAnalyticsPage';
import {DigitalSalesRoomSettingsPage} from '../pages/digital-sales-room-web/DigitalSalesRoomSettingsPage';
import {DigitalSalesRoomUsersPage} from '../pages/digital-sales-room-web/DigitalSalesRoomUsersPage';
import {DigitalSalesRoomsPage} from '../pages/digital-sales-room-web/DigitalSalesRoomsPage';
import {EditDigitalSalesRoomPage} from '../pages/digital-sales-room-web/EditDigitalSalesRoomPage';

const digitalSalesRoomPagesTest = test.extend<{
	digitalSalesRoomSettingsPage: DigitalSalesRoomSettingsPage;
	digitalSalesRoomUsersPage: DigitalSalesRoomUsersPage;
	digitalSalesRoomsPage: DigitalSalesRoomsPage;
	dsrAnalyticsPage: DSRAnalyticsPage;
	editDigitalSalesRoomPage: EditDigitalSalesRoomPage;
}>({
	digitalSalesRoomSettingsPage: async ({page}, use) => {
		await use(new DigitalSalesRoomSettingsPage(page));
	},
	digitalSalesRoomUsersPage: async ({page}, use) => {
		await use(new DigitalSalesRoomUsersPage(page));
	},
	digitalSalesRoomsPage: async ({page}, use) => {
		await use(new DigitalSalesRoomsPage(page));
	},
	dsrAnalyticsPage: async ({page}, use) => {
		await use(new DSRAnalyticsPage(page));
	},
	editDigitalSalesRoomPage: async ({page}, use) => {
		await use(new EditDigitalSalesRoomPage(page));
	},
});

export {digitalSalesRoomPagesTest};
