/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {searchAdminPageTest} from '../../../fixtures/searchAdminPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {liferayConfig} from '../../../liferay.config';
import {performLoginViaApi} from '../../../utils/performLogin';
import {viewUpgradedCustomObject} from '../utils/viewUpgradedCustomObject';
import {
	UpgradedVirtualInstance,
	viewUpgradedVirtualInstance,
} from '../utils/viewUpgradedVirtualInstance';

const test = mergeTests(
	loginTest(),
	searchAdminPageTest,
	usersAndOrganizationsPagesTest
);

const DEFAULT_INSTANCE: UpgradedVirtualInstance = {
	documentTitle: 'Document1',
	nameSuffix: '',
	pathSuffix: '',
	userIndex: '1',
	webId: 'liferay.com',
};

const SECOND_INSTANCE: UpgradedVirtualInstance = {
	documentTitle: 'Document2',
	nameSuffix: ' 2',
	pathSuffix: '-2',
	userIndex: '2',
	webId: 'www.able.com',
};

const THIRD_INSTANCE: UpgradedVirtualInstance = {
	documentTitle: 'Document3',
	nameSuffix: ' 3',
	pathSuffix: '-3',
	userIndex: '3',
	webId: 'www.baker.com',
};

// Only the 7.4.13.u33 archive carries custom objects, and the three projects
// share this spec, so the project config opts in rather than the spec branching
// on a version string.

function assertsCustomObjects() {
	const {assertCustomObjects} = test.info().project.use as {
		assertCustomObjects?: boolean;
	};

	return Boolean(assertCustomObjects);
}

test.describe.serial('View virtual instances upgrade', () => {
	test(
		'Can view upgraded content on the default instance',
		{tag: ['@LPD-104392']},
		async ({page, searchAdminPage, usersAndOrganizationsPage}) => {

			// The Users and Organizations list is search backed and the upgrade
			// does not index what it restores, so the archive users are absent
			// until this runs. The Poshi testcase reindexes in its setUp for the
			// same reason.

			await test.step('Reindex all search indexes', async () => {
				await searchAdminPage.goto();

				await searchAdminPage.goToIndexActionsTab();

				await searchAdminPage.reindexAllSearchIndexes();

				const reindexAllSearchIndexes =
					await searchAdminPage.getIndexActionsItem(
						'All Search Indexes'
					);

				await expect(reindexAllSearchIndexes).toBeVisible();

				await expect(
					reindexAllSearchIndexes.locator('.progress')
				).toBeHidden({timeout: 120 * 1000});
			});

			await viewUpgradedVirtualInstance({
				absentInstance: SECOND_INSTANCE,
				instance: DEFAULT_INSTANCE,
				instanceURL: '',
				page,
				usersAndOrganizationsPage,
			});

			if (assertsCustomObjects()) {
				await viewUpgradedCustomObject({
					absentNameSuffix: SECOND_INSTANCE.nameSuffix,
					instanceURL: '',
					nameSuffix: DEFAULT_INSTANCE.nameSuffix,
					page,
				});
			}
		}
	);

	test(
		'Can view upgraded content on the second instance',
		{tag: ['@LPD-104392']},
		async ({page, usersAndOrganizationsPage}) => {
			const instanceURL = `http://${SECOND_INSTANCE.webId}:${liferayConfig.environment.port}`;

			await performLoginViaApi({
				domain: `@${SECOND_INSTANCE.webId}`,
				loginUrl: instanceURL,
				page,
				screenName: 'test',
			});

			await viewUpgradedVirtualInstance({
				absentInstance: THIRD_INSTANCE,
				instance: SECOND_INSTANCE,
				instanceURL,
				page,
				usersAndOrganizationsPage,
			});

			if (assertsCustomObjects()) {
				await viewUpgradedCustomObject({
					absentNameSuffix: THIRD_INSTANCE.nameSuffix,
					instanceURL,
					nameSuffix: SECOND_INSTANCE.nameSuffix,
					page,
				});
			}
		}
	);

	test(
		'Can view upgraded content on the third instance',
		{tag: ['@LPD-104392']},
		async ({page, usersAndOrganizationsPage}) => {
			const instanceURL = `http://${THIRD_INSTANCE.webId}:${liferayConfig.environment.port}`;

			await performLoginViaApi({
				domain: `@${THIRD_INSTANCE.webId}`,
				loginUrl: instanceURL,
				page,
				screenName: 'test',
			});

			await viewUpgradedVirtualInstance({
				absentInstance: SECOND_INSTANCE,
				instance: THIRD_INSTANCE,
				instanceURL,
				page,
				usersAndOrganizationsPage,
			});

			if (assertsCustomObjects()) {
				await viewUpgradedCustomObject({
					absentNameSuffix: SECOND_INSTANCE.nameSuffix,
					instanceURL,
					nameSuffix: THIRD_INSTANCE.nameSuffix,
					page,
				});
			}
		}
	);
});
