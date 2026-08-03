/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {getAsGuest} from './getAsGuest';
import {ADMIN_EMAIL_ADDRESS, getWithBasicAuth} from './getWithBasicAuth';

const test = mergeTests(dataApiHelpersTest, isolatedSiteTest, loginTest());

const CONTENTS_APPLICATION_NAME = 'cms/basic-web-contents';

const DOCUMENTS_APPLICATION_NAME = 'cms/basic-documents';

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

test(
	'A deleted content entry and file return 404 to anonymous and authenticated clients alike',
	{tag: ['@LPD-95549', '@LPD-95549/TC-24.j']},
	async ({apiHelpers, browser, site}) => {
		const contentTitle = `Title ${getRandomString()}`;
		const fileTitle = `File ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode,
			{searchable: true}
		);

		const contentEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				content: `<p>Body ${getRandomString()}</p>`,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			CONTENTS_APPLICATION_NAME,
			spaceName
		);

		const fileEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageBase64,
					name: `${getRandomString()}.jpg`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			DOCUMENTS_APPLICATION_NAME,
			spaceName
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			CONTENTS_APPLICATION_NAME,
			contentEntry.id,
			[{actionIds: ['VIEW'], roleName: 'Guest'}]
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			DOCUMENTS_APPLICATION_NAME,
			fileEntry.id,
			[{actionIds: ['DOWNLOAD_FILE', 'VIEW'], roleName: 'Guest'}]
		);

		await test.step('Delete both entries and empty them from the Recycle Bin', async () => {
			for (const [applicationName, id] of [
				[CONTENTS_APPLICATION_NAME, contentEntry.id],
				[DOCUMENTS_APPLICATION_NAME, fileEntry.id],
			] as [string, number][]) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					applicationName,
					String(id)
				);

				await apiHelpers.objectEntry.deleteObjectEntry(
					applicationName,
					String(id)
				);
			}
		});

		await test.step('Anonymous requests return 404 with no data', async () => {
			for (const [applicationName, id, title] of [
				[CONTENTS_APPLICATION_NAME, contentEntry.id, contentTitle],
				[DOCUMENTS_APPLICATION_NAME, fileEntry.id, fileTitle],
			] as [string, number, string][]) {
				const {body, status} = await getAsGuest(
					browser,
					`/o/${applicationName}/${id}`
				);

				expect(status).toBe(404);
				expect(body?.title).not.toBe(title);
			}
		});

		await test.step('Authenticated requests return 404 with no data', async () => {
			for (const [applicationName, id, title] of [
				[CONTENTS_APPLICATION_NAME, contentEntry.id, contentTitle],
				[DOCUMENTS_APPLICATION_NAME, fileEntry.id, fileTitle],
			] as [string, number, string][]) {
				const {body, status} = await getWithBasicAuth(
					browser,
					`/o/${applicationName}/${id}`,
					ADMIN_EMAIL_ADDRESS
				);

				expect(status).toBe(404);
				expect(body?.title).not.toBe(title);
			}
		});
	}
);
