/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {getAsGuest} from './getAsGuest';

const test = mergeTests(dataApiHelpersTest, isolatedSiteTest, loginTest());

const APPLICATION_NAME = 'cms/basic-web-contents';

test(
	'An anonymous client can read a published Basic Web Content through the headless API',
	{tag: ['@LPD-95541', '@LPD-95541/TC-18.a']},
	async ({apiHelpers, browser, site}) => {
		const spaceName = `Space ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;
		const bodyValue = `Body ${getRandomString()}`;
		const friendlyUrlPath = `slug-${getRandomString()}`;

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
			APPLICATION_NAME,
			spaceName
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			APPLICATION_NAME,
			entry.id,
			[{actionIds: ['VIEW'], roleName: 'Guest'}]
		);

		const {body, status} = await getAsGuest(
			browser,
			`/o/${APPLICATION_NAME}/${entry.id}`
		);

		expect(status).toBe(200);
		expect(body?.title).toBe(contentTitle);
		expect(String(body?.content)).toContain(bodyValue);
		expect(body?.friendlyUrlPath).toBe(friendlyUrlPath);
	}
);

test(
	'An anonymous client cannot read an unpublished Basic Web Content through the headless API',
	{tag: ['@LPD-95541', '@LPD-95541/TC-18.d']},
	async ({apiHelpers, browser, site}) => {
		const spaceName = `Space ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;

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
				content: `<p>Body ${getRandomString()}</p>`,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				status: {code: 2, label: 'draft'},
				title: contentTitle,
			},
			APPLICATION_NAME,
			spaceName
		);

		const {body, status} = await getAsGuest(
			browser,
			`/o/${APPLICATION_NAME}/${entry.id}`
		);

		expect([401, 403, 404]).toContain(status);
		expect(body?.title).not.toBe(contentTitle);
	}
);
