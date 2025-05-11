/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import fs from 'fs/promises';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11232': {enabled: true},
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

test(
	'Document can be downloaded from the Files section and saved correctly',
	{tag: '@LPD-54566'},
	async ({apiHelpers, filesPage, page}) => {
		const applicationName = 'cms/basic-documents';
		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'R0lGODlhAQABAAAAACw=',
					name: `file_${getRandomString()}.png`,
				},
				title: `title ${getRandomString()}`,
			},
			applicationName,
			'Default'
		);

		try {
			apiHelpers.data.push({
				id: objectEntry.file.id,
				type: 'document',
			});

			await filesPage.goto();
			await filesPage.changeVisualizationMode('Table');

			const downloadPromise = page.waitForEvent('download');
			await filesPage.execItemAction({
				action: 'Download',
				filter: objectEntry.title,
			});

			const download = await downloadPromise;
			expect(download.suggestedFilename()).toBe(objectEntry.file.name);

			const downloadStat = await fs.stat(await download.path());
			expect(downloadStat.size).toBeGreaterThan(10);
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry.id)
			);
		}
	}
);
