/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {searchAdminPageTest} from '../../../fixtures/searchAdminPageTest';
import {reindexAllSearchIndexes} from '../utils/reindexAllSearchIndexes';
import {
	UpgradedPartition,
	viewUpgradedPartition,
} from '../utils/viewUpgradedPartition';

const test = mergeTests(loginTest(), searchAdminPageTest);

const ABLE_PARTITION: UpgradedPartition = {
	documentTitle: 'DM Document1 Title',
	emailAddress: 'test1@www.able.com',
	name: 'Test1',
	roleTitle: 'Roles Regrole1 Name',
	screenName: 'test1',
	virtualHostName: 'www.able.com',
	webContentContent: 'WC WebContent1 Content',
	webContentTitle: 'WC WebContent1 Title',
};

const BAKER_PARTITION: UpgradedPartition = {
	documentTitle: 'DM Document2 Title',
	emailAddress: 'test2@www.baker.com',
	name: 'Test2',
	roleTitle: 'Roles Regrole2 Name',
	screenName: 'test2',
	virtualHostName: 'www.baker.com',
	webContentContent: 'WC WebContent2 Content',
	webContentTitle: 'WC WebContent2 Title',
};

test.describe('View database partitioning upgrade', () => {
	test(
		'Can view upgraded content in each partition and not across them',
		{tag: '@LPD-104394'},
		async ({browser, searchAdminPage}) => {
			await test.step('Reindex all search indexes', async () => {
				await reindexAllSearchIndexes({searchAdminPage});
			});

			await viewUpgradedPartition({
				absentPartition: BAKER_PARTITION,
				browser,
				partition: ABLE_PARTITION,
			});

			await viewUpgradedPartition({
				absentPartition: ABLE_PARTITION,
				browser,
				partition: BAKER_PARTITION,
			});
		}
	);
});
