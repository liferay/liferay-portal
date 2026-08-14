/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Browser, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

const APPLICATION_NAME = 'cms/basic-documents';

async function getFileResponse(browser: Browser, url: string) {
	const context = await browser.newContext();

	try {
		const response = await context.request.get(url);

		return {
			body: response.ok() ? await response.text() : '',
			status: response.status(),
		};
	}
	finally {
		await context.close();
	}
}

test(
	'A file is downloadable only with the Download File permission',
	{tag: '@LPD-102629'},
	async ({apiHelpers, browser}) => {
		const fileContent = `File content ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;
		const title = `title ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: Buffer.from(fileContent).toString('base64'),
					name: `file_${getRandomString()}.txt`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title,
			},
			APPLICATION_NAME,
			spaceName
		);

		const fileURL = objectEntry.file.link.href;

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			APPLICATION_NAME,
			objectEntry.id,
			[{actionIds: ['VIEW'], roleName: 'Guest'}]
		);

		const viewOnlyResponse = await getFileResponse(browser, fileURL);

		// The file URL answers 404 rather than 403, so its existence is not
		// leaked to a user who may view the asset but not download it.

		expect(viewOnlyResponse.status).toBe(404);
		expect(viewOnlyResponse.body).not.toContain(fileContent);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			APPLICATION_NAME,
			objectEntry.id,
			[{actionIds: ['DOWNLOAD_FILE', 'VIEW'], roleName: 'Guest'}]
		);

		const downloadResponse = await getFileResponse(browser, fileURL);

		expect(downloadResponse.status).toBe(200);
		expect(downloadResponse.body).toBe(fileContent);
	}
);
