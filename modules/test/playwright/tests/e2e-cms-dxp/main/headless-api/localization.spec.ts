/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {ADMIN_EMAIL_ADDRESS, getWithBasicAuth} from './getWithBasicAuth';

const test = mergeTests(dataApiHelpersTest, isolatedSiteTest, loginTest());

const APPLICATION_NAME = 'cms/basic-web-contents';

test(
	'Requesting a translated language returns the translated values for all localizable fields',
	{tag: ['@LPD-95549', '@LPD-95549/TC-24.h']},
	async ({apiHelpers, browser, site}) => {
		const englishBody = `English body ${getRandomString()}`;
		const englishTitle = `English title ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;
		const spanishBody = `Cuerpo español ${getRandomString()}`;
		const spanishTitle = `Título español ${getRandomString()}`;

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
				content_i18n: {
					en_US: `<p>${englishBody}</p>`,
					es_ES: `<p>${spanishBody}</p>`,
				},
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title_i18n: {
					en_US: englishTitle,
					es_ES: spanishTitle,
				},
			},
			APPLICATION_NAME,
			spaceName
		);

		const {body, status} = await getWithBasicAuth(
			browser,
			`/o/${APPLICATION_NAME}/${entry.id}`,
			ADMIN_EMAIL_ADDRESS,
			{language: 'es-ES'}
		);

		expect(status).toBe(200);
		expect(body?.title).toBe(spanishTitle);
		expect(String(body?.content)).toContain(spanishBody);
	}
);

test(
	'Requesting a language with no translation falls back to the default language and shows which locales exist',
	{tag: ['@LPD-95549', '@LPD-95549/TC-24.i']},
	async ({apiHelpers, browser, site}) => {
		const englishBody = `English body ${getRandomString()}`;
		const englishTitle = `English title ${getRandomString()}`;
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
				content: `<p>${englishBody}</p>`,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: englishTitle,
			},
			APPLICATION_NAME,
			spaceName
		);

		const {body, status} = await getWithBasicAuth(
			browser,
			`/o/${APPLICATION_NAME}/${entry.id}`,
			ADMIN_EMAIL_ADDRESS,
			{language: 'fr-FR'}
		);

		expect(status).toBe(200);

		expect(body?.title).toBe(englishTitle);
		expect(String(body?.content)).toContain(englishBody);

		const titleTranslations = body?.title_i18n as Record<string, string>;

		expect(Object.keys(titleTranslations)).toEqual(['en_US']);
	}
);
