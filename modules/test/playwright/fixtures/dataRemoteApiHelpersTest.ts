/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DataApiHelpers} from '../helpers/ApiHelpers';
import {liferayConfig} from '../liferay.config';
import {trackPendingApiRequests} from '../utils/trackPendingApiRequests';

import type {Page, TestType} from '@playwright/test';

function dataRemoteApiHelpersTest(
	test: TestType<{remotePage: Page}, any>,
	port: string
) {
	return test.extend<{
		remoteApiHelpers: DataApiHelpers;
	}>({
		remoteApiHelpers: async ({remotePage}, use) => {
			const remoteBaseUrl = new URL(liferayConfig.environment.baseUrl);

			remoteBaseUrl.port = port;

			const remoteUrl = remoteBaseUrl.origin;

			const dataApiHelpers = new DataApiHelpers(remotePage, remoteUrl);

			const waitForPendingApiRequests =
				trackPendingApiRequests(remotePage);

			try {
				await use(dataApiHelpers);
			}
			finally {
				await waitForPendingApiRequests();

				// @ts-ignore

				const adminDataApiHelpers = new DataApiHelpers(
					remotePage,
					remoteUrl
				);
				adminDataApiHelpers.setData(dataApiHelpers.data);
				await adminDataApiHelpers.clearData();
			}
		},
	});
}

export {dataRemoteApiHelpersTest};
