/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, mergeTests} from '@playwright/test';

import {backendPageTest} from './backendPageTest';

interface FeatureFlagValue {
	enabled: boolean;
}

export interface FeatureFlagsOptions {
	[key: string]: FeatureFlagValue;
}

export interface FeatureFlag {
	readonly enabled?: boolean;
	readonly key: string;
}

export interface FeatureFlags {
	featureFlags: FeatureFlag[];
}

const test = mergeTests(backendPageTest);

/**
 * Declare the FF needs of a test.
 *
 * This fixture sets the given FFs to the requested state, then restores them when the test
 * finishes, no matter if it failed or succeeded. The fixture takes care of restoring dependent FFs
 * too.
 *
 * Note that you can use this fixture several times in the same test file according to your needs,
 * you don't need to use the same FFs across one single test file (see the example below for a way
 * to do it).
 *
 * @param options the list of FFs and the requested state for them when running this test
 *
 * @example
 * export const testWithoutFF = mergeTests(
 *   loginTest
 * );
 *
 * export const testWithFF = mergeTests(
 *   featureFlagsTest({
 *     'LPS-148856': {enabled: true},
 *   }),
 *   loginTest
 * );
 *
 * testWithoutFF('something', ...);
 * testWithFF('another thing', ...);
 */
function featureFlagsTest(options: FeatureFlagsOptions) {
	return test.extend<FeatureFlags>({
		featureFlags: [
			async ({backendPage, page}, use) => {

				// Save original state of FFs

				const originalFeatureFlags: FeatureFlagResult[] = [];

				for (const [key, value] of Object.entries(options)) {
					const reply = await invokeServer<IsEnabledResult>(
						backendPage,
						'/o/com-liferay-feature-flag-web/is-enabled',
						{
							...value,
							key,
						}
					);

					const {result} = reply;

					originalFeatureFlags.push(result.featureFlag);
					originalFeatureFlags.push(...result.dependentFeatureFlags);
				}

				try {

					// Set requested state of FFs

					for (const [key, value] of Object.entries(options)) {
						await invokeServer<SetEnabledResult>(
							backendPage,
							'/o/com-liferay-feature-flag-web/set-enabled',
							{
								...value,
								key,
							}
						);
					}

					// Reload page to account for FF changes (eg: update Liferay.FeatureFlags)

					await page.reload();

					// Run test

					await use(
						Object.entries(options).reduce(
							(featureFlags: FeatureFlag[], [key, value]) => {
								featureFlags.push({
									...value,
									key,
								});

								return featureFlags;
							},
							[]
						)
					);
				}
				finally {

					// Restore original state of FFs

					originalFeatureFlags.reverse();

					for (const featureFlag of originalFeatureFlags) {
						await invokeServer<SetEnabledResult>(
							backendPage,
							'/o/com-liferay-feature-flag-web/set-enabled',
							{
								enabled: featureFlag.enabled,
								key: featureFlag.key,
							}
						);
					}
				}
			},
			{auto: true},
		],
	});
}

interface IsEnabledResult {
	dependentFeatureFlags: FeatureFlagResult[];
	featureFlag: FeatureFlagResult;
}

interface SetEnabledResult {
	dependentFeatureFlags: FeatureFlagResult[];
}

interface ServerReply<T> {
	error?: string;
	result?: T;
}

interface FeatureFlagResult {
	companyId: number;
	dependenciesFulfilled: boolean;
	dependencyKeys: string[];
	description: string;
	enabled: boolean;
	featureFlagType: 'BETA' | 'DEPRECATION' | 'DEV' | 'RELEASE';
	key: string;
	title: string;
}

async function invokeServer<T>(
	page: Page,
	url: string,
	partialBody: FeatureFlag
): Promise<ServerReply<T>> {
	return await page.evaluate(
		async ({partialBody, url}) =>
			new Promise((resolve, reject) => {
				Liferay.Util.fetch(url, {
					body: Liferay.Util.objectToFormData(partialBody),
					method: 'POST',
				})
					.then(async (response) => {
						if (!response?.ok) {
							if (response?.status === 404) {
								reject(
									`Feature flag "${partialBody.key}" is not available`
								);
							}
							else if (response?.status === 500) {
								reject('An unexpected error has occurred');
							}
							else {
								reject(
									`Unable to fetch feature flag "${partialBody.key}"`
								);
							}
						}

						const json = JSON.parse(await response.text());

						resolve({
							result: json,
						});
					})
					.catch(reject);
			}),
		{
			partialBody,
			url,
		}
	);
}

export {featureFlagsTest};
