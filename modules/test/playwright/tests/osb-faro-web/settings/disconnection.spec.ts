/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {
	connectToAnalyticsCloud,
	disconnectFromAnalyticsCloud,
	goPreviousStep,
	goToAnalyticsCloudInstanceSettings,
} from '../../analytics-settings-web/main/utils/analytics-settings';
import {
	checkDataSourceStatus,
	createDataSource,
	findDataSource,
	gotoLatestLiferayDXPDataSource,
	renameDataSource,
} from '../main/utils/data-source';
import {waitForLoading} from '../main/utils/loading';
import {ACPage, navigateToACSettingsViaURL} from '../main/utils/navigation';

export const test = mergeTests(
	apiHelpersTest,
	isolatedChannelTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

test(
	'Disconnect Workspace in DXP and then check in Analytics Cloud if Data Source status is DISCONNECTED',

	{
		tag: '@LPD-44493',
	},
	async ({page, project}) => {
		const dataSourceName = 'Data Source ' + getRandomString();

		const {token} = await createDataSource(page);

		await test.step('Go to DXP --> Instance Settings --> Analytics Cloud and disconnect the workspace', async () => {
			await goToAnalyticsCloudInstanceSettings(page);

			const disconnectButton = page.getByRole('button', {
				name: 'Disconnect',
			});

			if (await disconnectButton.isVisible()) {
				await disconnectFromAnalyticsCloud(page);
			}

			await connectToAnalyticsCloud(page, {token});

			await expect(
				page.getByRole('heading', {name: 'Property Assignment'})
			).toBeVisible();

			await goPreviousStep(page);

			await disconnectFromAnalyticsCloud(page);
		});

		await test.step('Go back to Analytics Cloud settings and rename Data Source previously added', async () => {
			await gotoLatestLiferayDXPDataSource(page, project);

			await renameDataSource({
				newDataSourceName: dataSourceName,
				page,
			});
		});

		await test.step('Go back to Analytics Cloud settings and search for the renamed Data Source', async () => {
			await navigateToACSettingsViaURL({
				acPage: ACPage.dataSourcePage,
				page,
				projectID: project.groupId,
			});

			const dataSource = await findDataSource({
				dataSourceName,
				page,
			});

			if (dataSource) {
				await dataSource.click();
			}
			else {
				throw new Error('Data Source not found');
			}
		});

		await test.step('Check if Data Source status is DISCONNECTED', async () => {
			await waitForLoading(page);

			await checkDataSourceStatus({
				dataSourceName,
				dataSourceStatus: 'Disconnected',
				page,
			});
		});
	}
);
