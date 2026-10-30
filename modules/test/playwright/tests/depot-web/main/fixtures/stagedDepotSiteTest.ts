/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import {DepotStagingPage} from '../pages/DepotStagingPage';

export type StagedDepotSite = {
	assetLibrary: any;
	depotFriendlyURL: string;
	livePageURL: string;
	site: Site;
	stage: (options?: {assetLibraries?: any[]}) => Promise<void>;
	stagedPageURL: string;
};

const STAGING_TASK_EXECUTOR_CLASS_NAMES = [
	'com.liferay.exportimport.internal.background.task.LayoutStagingBackgroundTaskExecutor',
	'com.liferay.exportimport.internal.background.task.PortletStagingBackgroundTaskExecutor',
];

const ASSET_PUBLISHER_WIDGET =
	'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet';

export async function disableStaging(page: Page, groupFriendlyURL: string) {
	const stagingConfigurationURL = `/group${groupFriendlyURL}/~/control_panel/manage?p_p_id=com_liferay_staging_configuration_web_portlet_StagingConfigurationPortlet`;

	await page.goto(stagingConfigurationURL);

	const noneRadio = page.getByLabel('None');

	if (await noneRadio.isChecked()) {
		return;
	}

	await noneRadio.check();

	page.once('dialog', (dialog) => dialog.accept());

	await Promise.all([
		page.waitForResponse(
			(response) =>
				response.request().method() === 'POST' &&
				response
					.url()
					.includes(
						'com_liferay_staging_configuration_web_portlet_StagingConfigurationPortlet'
					)
		),
		page.getByRole('button', {name: 'Save'}).click(),
	]);

	await page.goto(stagingConfigurationURL);

	await expect(page.getByLabel('None')).toBeChecked();
}

const stagedDepotSiteTest = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest
).extend<{
	depotStagingPage: DepotStagingPage;
	stagedDepotSite: StagedDepotSite;
}>({
	depotStagingPage: async ({page}, use) => {
		await use(new DepotStagingPage(page));
	},
	stagedDepotSite: async ({apiHelpers, page, site}, use) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		await apiHelpers.headlessAssetLibrary.connectSite(
			assetLibrary.externalReferenceCode,
			site.externalReferenceCode
		);

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: String(site.id),
			title: getRandomString(),
			typeSettings: `layout-template-id=1_column\ncolumn-1=${ASSET_PUBLISHER_WIDGET}_INSTANCE_${getRandomString()}\n`,
		});

		const depotFriendlyURL = `/asset-library-${assetLibrary.id}`;

		const stagedGroupFriendlyURLs: string[] = [];

		const stagedPageURL = `/web${site.friendlyUrlPath}-staging${layout.friendlyURL}`;

		const enableLocalStaging = async (
			groupId: number,
			groupFriendlyURL: string
		) => {
			await apiHelpers.jsonWebServicesStaging.enableLocalStaging({
				groupId,
			});

			stagedGroupFriendlyURLs.unshift(groupFriendlyURL);

			const getBackgroundTasksCount = async (completed: boolean) => {
				let count = 0;

				for (const taskExecutorClassName of STAGING_TASK_EXECUTOR_CLASS_NAMES) {
					count += Number(
						await apiHelpers.post(
							`${liferayConfig.environment.baseUrl}/api/jsonws/backgroundtask.backgroundtask/get-background-tasks-count`,
							{
								data: new URLSearchParams({
									completed: String(completed),
									groupId: String(groupId),
									taskExecutorClassName,
								}).toString(),
								failOnStatusCode: true,
								headers:
									await apiHelpers.getJSONWebServicesHeaders(),
							}
						)
					);
				}

				return count;
			};

			await expect(async () => {
				expect(await getBackgroundTasksCount(false)).toBe(0);
				expect(await getBackgroundTasksCount(true)).toBeGreaterThan(0);
			}).toPass({timeout: 120000});
		};

		await use({
			assetLibrary,
			depotFriendlyURL,
			livePageURL: `/web${site.friendlyUrlPath}${layout.friendlyURL}`,
			site,
			stage: async ({assetLibraries = []} = {}) => {
				for (const {id, siteId} of [assetLibrary, ...assetLibraries]) {
					await enableLocalStaging(
						Number(siteId),
						`/asset-library-${id}`
					);
				}

				await enableLocalStaging(Number(site.id), site.friendlyUrlPath);
			},
			stagedPageURL,
		});

		for (const groupFriendlyURL of stagedGroupFriendlyURLs) {
			await disableStaging(page, groupFriendlyURL);
		}
	},
});

export {stagedDepotSiteTest};
