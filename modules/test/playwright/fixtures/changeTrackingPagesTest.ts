/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests, test} from '@playwright/test';

import {ApiHelpers} from '../helpers/ApiHelpers';
import {ChangeTrackingInstanceSettingsPage} from '../pages/change-tracking-web/ChangeTrackingInstanceSettingsPage';
import {ChangeTrackingPage} from '../pages/change-tracking-web/ChangeTrackingPage';
import {ChangeTrackingTemplatesPage} from '../pages/change-tracking-web/ChangeTrackingTemplatesPage';
import getRandomString from '../utils/getRandomString';
import {loginTest} from './loginTest';

const changeTrackingPages = test.extend<{
	ChangeTrackingInstanceSettingsPage: ChangeTrackingInstanceSettingsPage;
	changeTrackingPage: ChangeTrackingPage;
	changeTrackingTemplatesPage: ChangeTrackingTemplatesPage;
	ctCollection;
}>({
	ChangeTrackingInstanceSettingsPage: async ({page}, use) => {
		await use(new ChangeTrackingInstanceSettingsPage(page));
	},
	changeTrackingPage: async ({page}, use) => {
		await use(new ChangeTrackingPage(page));
	},
	changeTrackingTemplatesPage: async ({page}, use) => {
		await use(new ChangeTrackingTemplatesPage(page));
	},
	ctCollection: [
		async ({page}, use) => {
			await page.goto('/');

			const apiHelpers = new ApiHelpers(page);

			let ctCollection;

			try {

				// Create ctCollection

				ctCollection =
					await apiHelpers.headlessChangeTracking.createCTCollection(
						getRandomString()
					);

				await use(ctCollection);
			}
			catch {
				throw new Error(`Could not checkout ctCollection`);
			}
			finally {

				// Delete ctCollection

				if (ctCollection && ctCollection.body) {
					try {
						await apiHelpers.headlessChangeTracking.deleteCTCollection(
							ctCollection.body.id
						);
					}
					catch (error) {
						console.error('Error deleting CT Collection:', error);
					}
				}
			}
		},
		{auto: true},
	],
});

test.afterEach(async ({page}) => {
	const apiHelpers = new ApiHelpers(page);

	await apiHelpers.headlessChangeTracking.checkoutCTCollection(0);
});

const changeTrackingPagesTest = mergeTests(loginTest(), changeTrackingPages);

export {changeTrackingPagesTest};
