/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {getWithBasicAuth} from './getWithBasicAuth';

const test = mergeTests(dataApiHelpersTest, isolatedSiteTest, loginTest());

const APPLICATION_NAME = 'cms/basic-web-contents';

const ENTRY_COUNT = 12;

const PAGE_SIZE = 10;

test(
	'A paginated request returns the second page with accurate pagination metadata',
	{tag: ['@LPD-95549', '@LPD-95549/TC-24.g']},
	async ({apiHelpers, browser, site}) => {
		const spaceName = `Space ${getRandomString()}`;
		const titlePrefix = `Page Entry ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode,
			{searchable: true}
		);

		const titles: string[] = [];

		for (let i = 1; i <= ENTRY_COUNT; i++) {
			const title = `${titlePrefix} ${String(i).padStart(2, '0')}`;

			titles.push(title);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title,
				},
				APPLICATION_NAME,
				spaceName
			);
		}

		const {body, status} = await getWithBasicAuth(
			browser,
			`/o/${APPLICATION_NAME}/scopes/${encodeURIComponent(spaceName)}?page=2&pageSize=${PAGE_SIZE}&sort=id:asc`,
			'test@liferay.com'
		);

		expect(status).toBe(200);

		expect(body?.page).toBe(2);
		expect(body?.pageSize).toBe(PAGE_SIZE);
		expect(body?.totalCount).toBe(ENTRY_COUNT);
		expect(body?.lastPage).toBe(Math.ceil(ENTRY_COUNT / PAGE_SIZE));

		expect(
			((body?.items as {title: string}[]) || []).map((item) => item.title)
		).toEqual(titles.slice(PAGE_SIZE));
	}
);
