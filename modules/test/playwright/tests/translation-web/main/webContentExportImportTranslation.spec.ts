/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import * as path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {getZipEntryNames} from '../../../utils/zip';
import {translationPagesTest} from './fixtures/translationPagesTest';
import {TranslationValues as Values} from './utils/translationFiles';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	translationPagesTest
);

async function addWebContent(
	apiHelpers: ApiHelpers,
	site: Site,
	{content, description, title}: Values
) {
	await apiHelpers.jsonWebServicesJournal.addWebContent({
		content,
		ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
		descriptionMap: {en_US: description},
		groupId: site.id,
		titleMap: {en_US: title},
	});
}

test(
	'Exporting a web content creates one translation file per selected language',
	{tag: '@LPD-110482'},
	async ({apiHelpers, exportImportTranslationPage, site}) => {
		const title = getRandomString();

		await addWebContent(apiHelpers, site, {
			content: 'Web Content Content',
			description: 'Description',
			title,
		});

		await exportImportTranslationPage.gotoExport(
			site.friendlyUrlPath,
			title
		);

		// The original language cannot be chosen and nothing is exported yet

		await expect(
			exportImportTranslationPage.languageCheckbox(
				'English (United States)'
			)
		).toBeDisabled();

		await expect(exportImportTranslationPage.exportButton).toBeDisabled();

		// Export every available language

		const languages = {
			'Arabic (Saudi Arabia)': 'ar_SA',
			'Catalan (Spain)': 'ca_ES',
			'Chinese (China)': 'zh_CN',
			'Dutch (Netherlands)': 'nl_NL',
			'Finnish (Finland)': 'fi_FI',
			'French (France)': 'fr_FR',
			'German (Germany)': 'de_DE',
			'Hungarian (Hungary)': 'hu_HU',
			'Japanese (Japan)': 'ja_JP',
			'Portuguese (Brazil)': 'pt_BR',
			'Spanish (Spain)': 'es_ES',
			'Swedish (Sweden)': 'sv_SE',
		};

		const zipPath = await exportImportTranslationPage.export({
			languages: Object.keys(languages),
		});

		// The zip holds one file per language

		expect(path.basename(zipPath)).toBe(`${title}-en_US.zip`);

		expect((await getZipEntryNames(zipPath)).sort()).toEqual(
			Object.values(languages)
				.map((languageId) => `${title}-en_US-${languageId}.xlf`)
				.sort()
		);
	}
);

test(
	'Exporting several web contents bundles a translation file for each one',
	{tag: '@LPD-110482'},
	async ({apiHelpers, exportImportTranslationPage, site}) => {
		const titles = [1, 2, 3].map((index) => `${getRandomString()}${index}`);

		for (const title of titles) {
			await addWebContent(apiHelpers, site, {
				content: 'Web Content',
				description: 'Description',
				title,
			});
		}

		// Export the first and third web contents together

		await exportImportTranslationPage.gotoBulkExport(site.friendlyUrlPath, [
			titles[0],
			titles[2],
		]);

		const zipPath = await exportImportTranslationPage.export({
			languages: [
				'French (France)',
				'Japanese (Japan)',
				'Spanish (Spain)',
			],
		});

		// The zip holds one file per web content and language

		expect(path.basename(zipPath)).toBe(
			'Web Content Article Translations-en_US.zip'
		);

		const fileNames = [];

		for (const title of [titles[0], titles[2]]) {
			for (const languageId of ['es_ES', 'fr_FR', 'ja_JP']) {
				fileNames.push(`${title}-en_US-${languageId}.xlf`);
			}
		}

		expect((await getZipEntryNames(zipPath)).sort()).toEqual(
			fileNames.sort()
		);
	}
);
