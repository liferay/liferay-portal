/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {ApiHelpers} from '../helpers/ApiHelpers';
import {backendPageTest} from './backendPageTest';

const AC_PID =
	'com.liferay.analytics.settings.configuration.AnalyticsConfiguration';

const AC_URL = `/o/headless-admin-configuration/v1.0/instance-configurations/${AC_PID}`;

const STUB_PROPERTIES = {
	liferayAnalyticsDataSourceId: 'playwright-stub-data-source',
	liferayAnalyticsFaroBackendSecuritySignature: 'playwright-stub-signature',
	liferayAnalyticsFaroBackendURL: 'http://playwright-stub.invalid',
};

async function getACConnection(
	apiHelpers: ApiHelpers
): Promise<Record<string, unknown>> {
	const configuration = await apiHelpers.get(AC_URL);

	return configuration?.properties ?? {};
}

async function setACConnection(
	apiHelpers: ApiHelpers,
	properties: Record<string, unknown>
) {
	await apiHelpers.put(AC_URL, {
		data: {
			externalReferenceCode: AC_PID,
			properties,
		},
		failOnStatusCode: true,
	});
}

async function stubACConnection(apiHelpers: ApiHelpers) {
	await setACConnection(apiHelpers, STUB_PROPERTIES);
}

const test = mergeTests(backendPageTest);

/**
 * This fixture stubs the Analytics Cloud connection so the DXP instance behaves as connected,
 * without a real Analytics Cloud behind.
 *
 * No analytics data is available through this fixture: mock the analytics data endpoints with
 * page.route() to control what the connected UIs render.
 */
const analyticsCloudStubTest = test.extend<{analyticsCloudStub: void}>({
	analyticsCloudStub: [
		async ({backendPage}, use) => {
			const apiHelpers = new ApiHelpers(backendPage);

			const disabled: string[] = [];

			// The instance configuration API used to stub the connection is gated behind
			// LPD-65399, which depends on LPS-155284 (it must be enabled first)

			for (const featureFlag of ['LPS-155284', 'LPD-65399']) {
				const {
					featureFlag: {enabled},
				} =
					await apiHelpers.featureFlag.isFeatureFlagEnabled(
						featureFlag
					);

				if (!enabled) {
					disabled.push(featureFlag);

					await apiHelpers.featureFlag.updateFeatureFlag(
						featureFlag,
						true
					);
				}
			}

			try {
				const originalProperties = await getACConnection(apiHelpers);

				try {
					await stubACConnection(apiHelpers);

					await use();
				}
				finally {
					await setACConnection(apiHelpers, originalProperties);
				}
			}
			finally {
				for (const featureFlag of disabled) {
					await apiHelpers.featureFlag.updateFeatureFlag(
						featureFlag,
						false
					);
				}
			}
		},
		{auto: true},
	],
});

export {analyticsCloudStubTest};
