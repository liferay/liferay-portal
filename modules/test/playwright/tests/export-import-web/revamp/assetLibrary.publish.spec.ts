/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {disableStaging} from '../../depot-web/main/fixtures/stagedDepotSiteTest';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

export const test = mergeTests(
	dataApiHelpersTest,
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-96689': {enabled: true},
	}),
	loginTest()
);

test(
	'Can publish an asset library from the revamped Staging application',
	{tag: '@LPD-116380'},
	async ({apiHelpers, exportImportPage, page}) => {
		const assetLibrary =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		await apiHelpers.headlessDelivery.postAssetLibraryDocument(
			assetLibrary.id,
			createReadStream(path.join(__dirname, 'dependencies/Document.jpg')),
			{fileName: `${getRandomString()}.jpg`}
		);

		const assetLibraryFriendlyURL = `/asset-library-${assetLibrary.id}`;

		try {
			await apiHelpers.jsonWebServicesStaging.enableLocalStaging({
				groupId: assetLibrary.siteId,
			});

			await page.goto(
				`/group${assetLibraryFriendlyURL}-staging/~/control_panel/manage?p_p_id=com_liferay_staging_processes_web_portlet_StagingProcessesPortlet`
			);

			await expect(
				page.getByRole('link', {exact: true, name: 'Scheduled'})
			).toBeVisible();
			await expect(
				page
					.locator('.cell-status')
					.getByText('Successful', {exact: true})
			).toBeVisible();

			await exportImportPage.clickNew();

			const name = `Publication-${getRandomString()}`;

			await exportImportPage.nameInput.fill(name);

			await page.getByRole('button', {name: 'Publish to Live'}).click();

			await expect(exportImportPage.taskStatusLabel(name)).toBeVisible();
		}
		finally {
			await disableStaging(page, assetLibraryFriendlyURL);
		}
	}
);
