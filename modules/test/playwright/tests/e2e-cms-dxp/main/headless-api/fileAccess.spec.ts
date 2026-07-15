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

const test = mergeTests(dataApiHelpersTest, isolatedSiteTest, loginTest());

const APPLICATION_NAME = 'cms/basic-documents';

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

test(
	'An anonymous client can read a published file and download it through the headless API',
	{tag: ['@LPD-95541', '@LPD-95541/TC-18.c']},
	async ({apiHelpers, browser, site}) => {
		const spaceName = `Space ${getRandomString()}`;
		const fileTitle = `File ${getRandomString()}`;
		const fileName = `sample_${getRandomString()}.jpg`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode,
			{searchable: true}
		);

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {fileBase64: imageBase64, name: fileName},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			APPLICATION_NAME,
			spaceName
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			APPLICATION_NAME,
			entry.id,
			[{actionIds: ['DOWNLOAD_FILE', 'VIEW'], roleName: 'Guest'}]
		);

		const {body, status} = await getAsGuest(
			browser,
			`/o/${APPLICATION_NAME}/${entry.id}`
		);

		expect(status).toBe(200);
		expect(body?.title).toBe(fileTitle);

		const file = body?.file as {
			extension?: string;
			link?: {href?: string};
			mimeType?: string;
			name?: string;
		};

		expect(file?.name).toBe(fileName);
		expect(file?.extension).toBe('jpg');
		expect(file?.mimeType).toBe('image/jpeg');
		expect(file?.link?.href).toBeTruthy();

		const downloadStatus = await getAsGuest(
			browser,
			String(file?.link?.href)
		);

		expect(downloadStatus.status).toBe(200);
	}
);

test(
	'An anonymous client cannot read an unpublished file through the headless API',
	{tag: ['@LPD-95541', '@LPD-95541/TC-18.e']},
	async ({apiHelpers, browser, site}) => {
		const spaceName = `Space ${getRandomString()}`;
		const fileTitle = `File ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode,
			{searchable: true}
		);

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageBase64,
					name: `sample_${getRandomString()}.jpg`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				status: {code: 2, label: 'draft'},
				title: fileTitle,
			},
			APPLICATION_NAME,
			spaceName
		);

		const {body, status} = await getAsGuest(
			browser,
			`/o/${APPLICATION_NAME}/${entry.id}`
		);

		expect([401, 403, 404]).toContain(status);
		expect(body?.title).not.toBe(fileTitle);
	}
);
