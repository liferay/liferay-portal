/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';
import {zip} from 'zip-a-folder';

import {ApiHelpers, getHeader} from '../../../../helpers/ApiHelpers';
import {createTempDir} from '../../../../utils/createTempFile';
import {getZipEntryNames, readFileFromZip} from '../../../../utils/zip';

export type TranslationValues = {
	content: string;
	description: string;
	title: string;
};

async function getWebContent(
	apiHelpers: ApiHelpers,
	page: Page,
	site: Site,
	title: string
) {
	const {items} = await apiHelpers.get(
		`${apiHelpers.baseUrl}headless-delivery/v1.0/sites/${site.id}/structured-contents?fields=availableLanguages,contentFields,description_i18n,title_i18n&pageSize=100`,
		true,
		{...(await getHeader(page)), 'X-Accept-All-Languages': 'true'}
	);

	const webContent = items.find(
		(item: {title_i18n: {[languageId: string]: string}}) =>
			item.title_i18n['en-US'] === title
	);

	expect(webContent).toBeTruthy();

	return webContent;
}

export async function expectNotTranslated(
	apiHelpers: ApiHelpers,
	page: Page,
	site: Site,
	title: string,
	languageId: string
) {
	const webContent = await getWebContent(apiHelpers, page, site, title);

	expect(webContent.availableLanguages).not.toContain(languageId);
}

export async function expectTranslated(
	apiHelpers: ApiHelpers,
	page: Page,
	site: Site,
	title: string,
	languageId: string,
	translated: TranslationValues
) {
	const webContent = await getWebContent(apiHelpers, page, site, title);

	expect(webContent.availableLanguages).toContain(languageId);
	expect(webContent.title_i18n[languageId]).toBe(translated.title);
	expect(webContent.description_i18n[languageId]).toBe(
		translated.description
	);
	expect(
		webContent.contentFields[0].contentFieldValue_i18n[languageId].data
	).toBe(`<p>${translated.content}</p>`);
}

export function getTranslations(
	base: TranslationValues,
	translated: TranslationValues
) {
	return {
		[`<p>${base.content}</p>`]: `<p>${translated.content}</p>`,
		[base.description]: translated.description,
		[base.title]: translated.title,
	};
}

export async function readXliff(
	zipPath: string,
	title: string,
	languageId: string
) {
	const fileName = `${title}-en_US-${languageId}.xlf`;

	expect(await getZipEntryNames(zipPath)).toContain(fileName);

	return readFileFromZip(
		`^${fileName.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}$`,
		zipPath
	);
}

export function writeFiles(files: {[fileName: string]: string}) {
	const directory = createTempDir();

	return Object.entries(files).map(([fileName, content]) => {
		const filePath = path.join(directory, fileName);

		fs.writeFileSync(filePath, content);

		return filePath;
	});
}

export async function writeZip(
	zipName: string,
	files: {[fileName: string]: string}
) {
	const [filePath] = writeFiles(files);

	const zipPath = path.join(createTempDir(), zipName);

	await zip(path.dirname(filePath), zipPath);

	return zipPath;
}
