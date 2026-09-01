/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {DataApiHelpers} from '../helpers/ApiHelpers';
import {trackPendingApiRequests} from '../utils/trackPendingApiRequests';
import {BackendPage, backendPageTest} from './backendPageTest';

const test = mergeTests(backendPageTest);

const dataApiHelpersTest = test.extend<{
	apiHelpers: DataApiHelpers;
	backendPage: BackendPage;
}>({
	apiHelpers: async ({backendPage, page}, use) => {
		const dataApiHelpers = new DataApiHelpers(page);

		const waitForPendingApiRequests = trackPendingApiRequests(page);

		try {
			await use(dataApiHelpers);
		}
		finally {
			await waitForPendingApiRequests();

			// @ts-ignore

			const adminDataApiHelpers = new DataApiHelpers(backendPage);

			adminDataApiHelpers.setData(dataApiHelpers.data);

			await adminDataApiHelpers.clearData();
		}
	},
});
export {dataApiHelpersTest};
