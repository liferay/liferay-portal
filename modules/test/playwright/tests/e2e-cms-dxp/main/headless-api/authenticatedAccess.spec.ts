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
import {performLoginViaApi} from '../../../../utils/performLogin';
import {createRecipient} from '../../../../utils/sharingRecipient';
import {getAsGuest} from './getAsGuest';
import {getWithBasicAuth} from './getWithBasicAuth';

const test = mergeTests(dataApiHelpersTest, isolatedSiteTest, loginTest());

const CONTENTS_APPLICATION_NAME = 'cms/basic-web-contents';

const DOCUMENTS_APPLICATION_NAME = 'cms/basic-documents';

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

test(
	'An authenticated client can read a restricted Basic Web Content that an anonymous client cannot',
	{
		tag: ['@LPD-95549', '@LPD-95549/TC-24.a', '@LPD-95549/TC-24.b'],
	},
	async ({apiHelpers, browser, site}) => {
		const bodyValue = `Body ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;
		const friendlyUrlPath = `slug-${getRandomString()}`;
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

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				content: `<p>${bodyValue}</p>`,
				friendlyUrlPath,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			CONTENTS_APPLICATION_NAME,
			spaceName
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			CONTENTS_APPLICATION_NAME,
			entry.id,
			[{actionIds: ['VIEW'], roleName: 'User'}]
		);

		const user = await createRecipient(apiHelpers);

		await test.step('An authenticated request returns 200 with all expected field values', async () => {
			const {body, status} = await getWithBasicAuth(
				browser,
				`/o/${CONTENTS_APPLICATION_NAME}/${entry.id}`,
				user.emailAddress
			);

			expect(status).toBe(200);
			expect(body?.title).toBe(contentTitle);
			expect(String(body?.content)).toContain(bodyValue);
			expect(body?.friendlyUrlPath).toBe(friendlyUrlPath);
		});

		await test.step('An anonymous request for the same entry is denied and leaks no data', async () => {
			const {body, status} = await getAsGuest(
				browser,
				`/o/${CONTENTS_APPLICATION_NAME}/${entry.id}`
			);

			expect([401, 403, 404]).toContain(status);
			expect(body?.title).not.toBe(contentTitle);
		});
	}
);

test(
	'An authenticated client can read a restricted file with its metadata and download it',
	{tag: ['@LPD-95549', '@LPD-95549/TC-24.c']},
	async ({apiHelpers, browser, site}) => {
		const fileName = `restricted_${getRandomString()}.jpg`;
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

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {fileBase64: imageBase64, name: fileName},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			DOCUMENTS_APPLICATION_NAME,
			spaceName
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			DOCUMENTS_APPLICATION_NAME,
			entry.id,
			[{actionIds: ['DOWNLOAD_FILE', 'VIEW'], roleName: 'User'}]
		);

		const user = await createRecipient(apiHelpers);

		const downloadHref =
			await test.step('An authenticated request returns the file metadata and a download URL', async () => {
				const {body, status} = await getWithBasicAuth(
					browser,
					`/o/${DOCUMENTS_APPLICATION_NAME}/${entry.id}`,
					user.emailAddress
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

				return String(file?.link?.href);
			});

		await test.step('The download URL serves the file to the authenticated user', async () => {
			const userContext = await browser.newContext();
			const userPage = await userContext.newPage();

			try {
				await performLoginViaApi({
					page: userPage,
					screenName: user.alternateName,
				});

				const status = await userPage.evaluate(async (href) => {
					const response = await fetch(href, {redirect: 'manual'});

					return response.status;
				}, downloadHref);

				expect(status).toBe(200);
			}
			finally {
				await userContext.close();
			}
		});

		await test.step('An anonymous request for the same file is denied', async () => {
			const {status} = await getAsGuest(
				browser,
				`/o/${DOCUMENTS_APPLICATION_NAME}/${entry.id}`
			);

			expect([401, 403, 404]).toContain(status);
		});
	}
);
