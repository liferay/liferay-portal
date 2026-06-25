/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import fs from 'fs';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {rolesPagesTest} from '../../../fixtures/rolesPagesTest';

export const test = mergeTests(dataApiHelpersTest, loginTest(), rolesPagesTest);

test('LPD-29557 Can export roles via API', async ({apiHelpers, page}) => {
	const exportTaskId = (
		await apiHelpers.headlessAdminUser.postRolesPageExportBatch()
	).id;

	await page.waitForTimeout(1000);

	const exportTask =
		await apiHelpers.headlessBatchEngine.getExportTask(exportTaskId);

	await expect(exportTask.executeStatus).toEqual('COMPLETED');

	const fileName =
		await apiHelpers.headlessBatchEngine.getExportTaskContent(exportTaskId);
	const stats = fs.statSync(fileName);

	await expect(stats.size).toBeGreaterThan(30);

	fs.unlinkSync(fileName);
});
