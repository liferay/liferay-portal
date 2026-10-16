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
import {changeXliffModelId, translateXliff} from '../../../utils/xliff';
import {getZipEntryNames} from '../../../utils/zip';
import {translationPagesTest} from './fixtures/translationPagesTest';
import {
	TranslationValues as Values,
	expectNotTranslated,
	expectTranslated,
	getTranslations,
	readXliff,
	writeFiles,
	writeZip,
} from './utils/translationFiles';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	translationPagesTest
);

const GERMAN = {
	content: 'Internetinhalt',
	description: 'Beschreibung',
	title: 'TiteldesWebinhalts',
};

const SPANISH = {
	content: 'Contenidoweb',
	description: 'Descripcion',
	title: 'Titulodelcontenidoweb',
};

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

const IMPORT_CASES: {
	base?: Partial<Values>;
	fileName?: (title: string, languageId: string) => string;
	format?: string;
	language: string;
	languageId: string;
	name: string;
	tags: string[];
	title: () => string;
	translated: (title: string) => Values;
}[] = [
	{
		language: 'Spanish (Spain)',
		languageId: 'es_ES',
		name: 'an XLIFF 2.0 file',
		tags: ['@LPD-110482'],
		title: getRandomString,
		translated: () => SPANISH,
	},
	{
		format: 'XLIFF 1.2',
		language: 'Spanish (Spain)',
		languageId: 'es_ES',
		name: 'an XLIFF 1.2 file',
		tags: ['@LPD-110482'],
		title: getRandomString,
		translated: () => SPANISH,
	},
	{
		fileName: (title, languageId) => `${title}-en_US-${languageId}.xliff`,
		language: 'German (Germany)',
		languageId: 'de_DE',
		name: 'a file with the .xliff extension',
		tags: ['@LPD-110482'],
		title: getRandomString,
		translated: () => GERMAN,
	},
	{
		language: 'Japanese (Japan)',
		languageId: 'ja_JP',
		name: 'a file with Japanese text',
		tags: ['@LPD-110482'],
		title: getRandomString,
		translated: () => ({
			content: 'Webコンテンツ',
			description: '説明',
			title: 'Webコンテンツのタイトル',
		}),
	},
	{
		base: {
			content: 'WebContentContent@!#',
			description: 'WebContentDescription@!#',
		},
		language: 'Spanish (Spain)',
		languageId: 'es_ES',
		name: 'a file with special characters',
		tags: ['@LPD-110482', '@LPS-136980'],
		title: () => `WebContentTitle@!&#${getRandomString()}`,
		translated: () => ({
			content: 'Contenidoweb@!#',
			description: 'Descripcion@!#',
			title: 'Titulodelcontenidoweb@!&#',
		}),
	},
	{
		language: 'Spanish (Spain)',
		languageId: 'es_ES',
		name: 'a file with long inputs',
		tags: ['@LPD-110482', '@LPS-136979'],
		title: () =>
			`LongInputsForWebContentTitleToCheckTheTranslationsExportingAndImportingProcess${getRandomString()}`,
		translated: (title) => ({
			...SPANISH,
			title: `${title}-Titulodelcontenidoweb`,
		}),
	},
];

for (const importCase of IMPORT_CASES) {
	test(
		`Importing ${importCase.name} translates the web content`,
		{tag: importCase.tags},
		async ({apiHelpers, exportImportTranslationPage, page, site}) => {
			const title = importCase.title();

			const base = {
				content: 'WebContent',
				description: 'Description',
				...importCase.base,
				title,
			};

			const translated = importCase.translated(title);

			await addWebContent(apiHelpers, site, base);

			// Export the web content and translate the exported file

			const zipPath = await exportImportTranslationPage.exportArticle(
				site.friendlyUrlPath,
				title,
				[importCase.language],
				importCase.format
			);

			const xliff = await readXliff(
				zipPath,
				title,
				importCase.languageId
			);

			const fileName = importCase.fileName
				? importCase.fileName(title, importCase.languageId)
				: `${title}-en_US-${importCase.languageId}.xlf`;

			const [filePath] = writeFiles({
				[fileName]: translateXliff(
					xliff,
					getTranslations(base, translated)
				),
			});

			// Import the translated file

			await exportImportTranslationPage.gotoImport(
				site.friendlyUrlPath,
				title
			);

			await exportImportTranslationPage.upload([filePath]);

			await exportImportTranslationPage.submit('Publish');

			await expect(exportImportTranslationPage.successTitle).toHaveText(
				'1 File Published'
			);

			await expect(exportImportTranslationPage.failureTitle).toBeHidden();

			// The web content shows the imported translation

			await expectTranslated(
				apiHelpers,
				page,
				site,
				title,
				importCase.languageId.replace('_', '-'),
				translated
			);
		}
	);
}

test(
	'Importing a translated zip applies every language in it',
	{tag: '@LPD-110482'},
	async ({apiHelpers, exportImportTranslationPage, page, site}) => {
		const title = getRandomString();

		const base = {content: 'WebContent', description: 'Description', title};

		await addWebContent(apiHelpers, site, base);

		// Export German and Spanish, and translate both files in the zip

		const zipPath = await exportImportTranslationPage.exportArticle(
			site.friendlyUrlPath,
			title,
			['German (Germany)', 'Spanish (Spain)']
		);

		const translatedZipPath = await writeZip(
			`${title}-en_US_translated.zip`,
			{
				[`${title}-en_US-de_DE.xlf`]: translateXliff(
					await readXliff(zipPath, title, 'de_DE'),
					getTranslations(base, GERMAN)
				),
				[`${title}-en_US-es_ES.xlf`]: translateXliff(
					await readXliff(zipPath, title, 'es_ES'),
					getTranslations(base, SPANISH)
				),
			}
		);

		// Import the translated zip from the web content

		await exportImportTranslationPage.gotoImport(
			site.friendlyUrlPath,
			title
		);

		await exportImportTranslationPage.upload([translatedZipPath]);

		await exportImportTranslationPage.submit('Publish');

		await expect(exportImportTranslationPage.successTitle).toHaveText(
			'All Files Published'
		);

		for (const languageId of ['de_DE', 'es_ES']) {
			await expect(
				exportImportTranslationPage.resultItem(
					`${title}-en_US-${languageId}.xlf`
				)
			).toBeVisible();
		}

		// Both languages are applied

		await expectTranslated(apiHelpers, page, site, title, 'de-DE', GERMAN);
		await expectTranslated(apiHelpers, page, site, title, 'es-ES', SPANISH);
	}
);

test(
	'Importing a zip for several web contents translates only those web contents',
	{tag: '@LPD-110482'},
	async ({apiHelpers, exportImportTranslationPage, page, site}) => {
		const titles = [1, 2, 3].map((index) => `${getRandomString()}${index}`);

		const bases = titles.map((title, index) => ({
			content: `WebContent${index + 1}`,
			description: `Description${index + 1}`,
			title,
		}));

		const translations = bases.map((base, index) => ({
			content: `Contenidoweb${index + 1}`,
			description: `Descripcion${index + 1}`,
			title: `Titulodelcontenidoweb${index + 1}`,
		}));

		for (const base of bases) {
			await addWebContent(apiHelpers, site, base);
		}

		// Export the first and third web contents together and translate them

		await exportImportTranslationPage.gotoBulkExport(site.friendlyUrlPath, [
			titles[0],
			titles[2],
		]);

		const zipPath = await exportImportTranslationPage.export({
			languages: ['Spanish (Spain)'],
		});

		const files: {[fileName: string]: string} = {};

		for (const index of [0, 2]) {
			files[`${titles[index]}-en_US-es_ES.xlf`] = translateXliff(
				await readXliff(zipPath, titles[index], 'es_ES'),
				getTranslations(bases[index], translations[index])
			);
		}

		const translatedZipPath = await writeZip(
			'Web Content Article Translations-en_US_translated.zip',
			files
		);

		// Import the zip from the Web Content admin

		await exportImportTranslationPage.gotoBulkImport(site.friendlyUrlPath);

		await exportImportTranslationPage.upload([translatedZipPath]);

		await exportImportTranslationPage.submit('Publish');

		await expect(exportImportTranslationPage.successTitle).toHaveText(
			'All Files Published'
		);

		for (const fileName of Object.keys(files)) {
			await expect(
				exportImportTranslationPage.resultItem(fileName)
			).toBeVisible();
		}

		// Only the exported web contents are translated

		for (const index of [0, 2]) {
			await expectTranslated(
				apiHelpers,
				page,
				site,
				titles[index],
				'es-ES',
				translations[index]
			);
		}

		await expectNotTranslated(apiHelpers, page, site, titles[1], 'es-ES');
	}
);

test(
	'Importing a zip and a loose file together translates every web content in them',
	{tag: '@LPD-110482'},
	async ({apiHelpers, exportImportTranslationPage, page, site}) => {
		const titles = [1, 2].map((index) => `${getRandomString()}${index}`);

		const bases = titles.map((title, index) => ({
			content: `WebContent${index + 1}`,
			description: `Description${index + 1}`,
			title,
		}));

		const japanese = {
			content: 'Webコンテンツ1',
			description: '説明1',
			title: 'Webコンテンツのタイトル1',
		};

		const spanish = {
			content: 'Contenidoweb1',
			description: 'Descripcion1',
			title: 'Titulodelcontenidoweb1',
		};

		const german = {
			content: 'Internetinhalt2',
			description: 'Beschreibung2',
			title: 'TiteldesWebinhalts2',
		};

		for (const base of bases) {
			await addWebContent(apiHelpers, site, base);
		}

		// Translate the first web content into a zip

		const firstZipPath = await exportImportTranslationPage.exportArticle(
			site.friendlyUrlPath,
			titles[0],
			['Japanese (Japan)', 'Spanish (Spain)']
		);

		const translatedZipPath = await writeZip(
			`${titles[0]}-en_US_translated.zip`,
			{
				[`${titles[0]}-en_US-es_ES.xlf`]: translateXliff(
					await readXliff(firstZipPath, titles[0], 'es_ES'),
					getTranslations(bases[0], spanish)
				),
				[`${titles[0]}-en_US-ja_JP.xlf`]: translateXliff(
					await readXliff(firstZipPath, titles[0], 'ja_JP'),
					getTranslations(bases[0], japanese)
				),
			}
		);

		// Translate the second web content into a loose file

		const secondZipPath = await exportImportTranslationPage.exportArticle(
			site.friendlyUrlPath,
			titles[1],
			['German (Germany)']
		);

		const [germanFilePath] = writeFiles({
			[`${titles[1]}-en_US-de_DE.xlf`]: translateXliff(
				await readXliff(secondZipPath, titles[1], 'de_DE'),
				getTranslations(bases[1], german)
			),
		});

		// Import both from the Web Content admin

		await exportImportTranslationPage.gotoBulkImport(site.friendlyUrlPath);

		await exportImportTranslationPage.upload([
			translatedZipPath,
			germanFilePath,
		]);

		await exportImportTranslationPage.submit('Publish');

		await expect(exportImportTranslationPage.successTitle).toHaveText(
			'All Files Published'
		);

		for (const fileName of [
			`${titles[0]}-en_US-es_ES.xlf`,
			`${titles[0]}-en_US-ja_JP.xlf`,
			`${titles[1]}-en_US-de_DE.xlf`,
		]) {
			await expect(
				exportImportTranslationPage.resultItem(fileName)
			).toBeVisible();
		}

		// Every file is applied

		await expectTranslated(
			apiHelpers,
			page,
			site,
			titles[0],
			'es-ES',
			spanish
		);
		await expectTranslated(
			apiHelpers,
			page,
			site,
			titles[0],
			'ja-JP',
			japanese
		);
		await expectTranslated(
			apiHelpers,
			page,
			site,
			titles[1],
			'de-DE',
			german
		);
	}
);

test(
	'Importing several files publishes the valid ones and reports the invalid ones',
	{tag: '@LPD-110482'},
	async ({apiHelpers, exportImportTranslationPage, page, site}) => {
		const titles = [1, 2].map((index) => `${getRandomString()}${index}`);

		const bases = titles.map((title, index) => ({
			content: `WebContent${index + 1}`,
			description: `Description${index + 1}`,
			title,
		}));

		const spanish = {
			content: 'Contenidoweb1',
			description: 'Descripcion1',
			title: 'Titulodelcontenidoweb1',
		};

		const japanese = {
			content: 'Webコンテンツ2',
			description: '説明2',
			title: 'Webコンテンツのタイトル2',
		};

		for (const base of bases) {
			await addWebContent(apiHelpers, site, base);
		}

		// First web content: a valid Spanish file and a Japanese file for another model

		await exportImportTranslationPage.gotoBulkExport(site.friendlyUrlPath, [
			titles[0],
		]);

		const firstZipPath = await exportImportTranslationPage.export({
			languages: ['Japanese (Japan)', 'Spanish (Spain)'],
		});

		const zipName = `${titles[0]}-en_US_translated.zip`;

		const translatedZipPath = await writeZip(zipName, {
			[`${titles[0]}-en_US-es_ES.xlf`]: translateXliff(
				await readXliff(firstZipPath, titles[0], 'es_ES'),
				getTranslations(bases[0], spanish)
			),
			[`${titles[0]}-en_US-ja_JP.xlf`]: changeXliffModelId(
				translateXliff(
					await readXliff(firstZipPath, titles[0], 'ja_JP'),
					getTranslations(bases[0], {
						content: 'Webコンテンツ1',
						description: '説明1',
						title: 'Webコンテンツのタイトル1',
					})
				)
			),
		});

		// Second web content: a valid Japanese file and a Chinese file for another model

		await exportImportTranslationPage.gotoBulkExport(site.friendlyUrlPath, [
			titles[1],
		]);

		const secondZipPath = await exportImportTranslationPage.export({
			languages: ['Chinese (China)', 'Japanese (Japan)'],
		});

		const [chineseFilePath, japaneseFilePath] = writeFiles({
			[`${titles[1]}-en_US-zh_CN.xlf`]: changeXliffModelId(
				translateXliff(
					await readXliff(secondZipPath, titles[1], 'zh_CN'),
					getTranslations(bases[1], {
						content: '网页内容2',
						description: '说明2',
						title: '网页内容标题2',
					})
				)
			),
			[`${titles[1]}-en_US-ja_JP.xlf`]: translateXliff(
				await readXliff(secondZipPath, titles[1], 'ja_JP'),
				getTranslations(bases[1], japanese)
			),
		});

		// Import all of them from the Web Content admin

		await exportImportTranslationPage.gotoBulkImport(site.friendlyUrlPath);

		await exportImportTranslationPage.upload([
			translatedZipPath,
			chineseFilePath,
			japaneseFilePath,
		]);

		await exportImportTranslationPage.submit('Publish');

		// The results list the published and the failed files

		await expect(exportImportTranslationPage.failureTitle).toHaveText(
			'2 files could not be published.'
		);

		await expect(exportImportTranslationPage.successTitle).toHaveText(
			'2 Files Published'
		);

		for (const fileName of [
			`${titles[0]}-en_US-ja_JP.xlf`,
			`${titles[1]}-en_US-zh_CN.xlf`,
		]) {
			await expect(
				exportImportTranslationPage.resultItem(fileName)
			).toContainText(
				'The XLIFF file contains a translation for an invalid model.'
			);
		}

		await expect(
			exportImportTranslationPage.resultItem(
				`${titles[0]}-en_US-ja_JP.xlf`
			)
		).toContainText(zipName);

		// Only the valid files are applied

		await expectTranslated(
			apiHelpers,
			page,
			site,
			titles[0],
			'es-ES',
			spanish
		);
		await expectTranslated(
			apiHelpers,
			page,
			site,
			titles[1],
			'ja-JP',
			japanese
		);
		await expectNotTranslated(apiHelpers, page, site, titles[0], 'ja-JP');
		await expectNotTranslated(apiHelpers, page, site, titles[1], 'zh-CN');
	}
);

test(
	'The CSV error report lists the files that could not be imported',
	{tag: '@LPD-110482'},
	async ({apiHelpers, exportImportTranslationPage, site}) => {
		const title = getRandomString();

		const base = {content: 'WebContent', description: 'Description', title};

		await addWebContent(apiHelpers, site, base);

		// Export Japanese and Spanish, and point the Japanese file to another model

		const zipPath = await exportImportTranslationPage.exportArticle(
			site.friendlyUrlPath,
			title,
			['Japanese (Japan)', 'Spanish (Spain)']
		);

		const zipName = `${title}-en_US_translated.zip`;

		const translatedZipPath = await writeZip(zipName, {
			[`${title}-en_US-es_ES.xlf`]: translateXliff(
				await readXliff(zipPath, title, 'es_ES'),
				getTranslations(base, {
					content: 'WebContentSpanish',
					description: 'DescriptionSpanish',
					title: 'WebContentTitleSpanish',
				})
			),
			[`${title}-en_US-ja_JP.xlf`]: changeXliffModelId(
				translateXliff(
					await readXliff(zipPath, title, 'ja_JP'),
					getTranslations(base, {
						content: 'WebContentJapanese',
						description: 'DescriptionJapanese',
						title: 'WebContentTitleJapanese',
					})
				)
			),
		});

		// Import the zip from the web content

		await exportImportTranslationPage.gotoImport(
			site.friendlyUrlPath,
			title
		);

		await exportImportTranslationPage.upload([translatedZipPath]);

		await exportImportTranslationPage.submit('Publish');

		// The CSV report names the failed file and not the published one

		const report =
			await exportImportTranslationPage.downloadCSVErrorReport();

		expect(report).toContain(
			`${title}-en_US-ja_JP.xlf,The translation file does not correspond to this web content.,${zipName}`
		);
		expect(report).not.toContain(`${title}-en_US-es_ES`);
	}
);

test(
	'Translation files can be replaced before publishing and more files uploaded after',
	{tag: '@LPD-110482'},
	async ({apiHelpers, exportImportTranslationPage, page, site}) => {
		const title = getRandomString();

		const base = {content: 'WebContent', description: 'Description', title};

		await addWebContent(apiHelpers, site, base);

		const zipPath = await exportImportTranslationPage.exportArticle(
			site.friendlyUrlPath,
			title,
			['German (Germany)', 'Spanish (Spain)']
		);

		const germanXliff = translateXliff(
			await readXliff(zipPath, title, 'de_DE'),
			getTranslations(base, GERMAN)
		);

		const [germanFilePath, spanishFilePath] = writeFiles({
			[`${title}-en_US-de_DE.xlf`]: germanXliff,
			[`${title}-en_US-es_ES.xlf`]: translateXliff(
				await readXliff(zipPath, title, 'es_ES'),
				getTranslations(base, SPANISH)
			),
		});

		const [invalidFilePath] = writeFiles({
			[`${title}-en_US-de_DE-invalid.xlf`]:
				changeXliffModelId(germanXliff),
		});

		// Replace the selected file before publishing

		await exportImportTranslationPage.gotoBulkImport(site.friendlyUrlPath);

		await exportImportTranslationPage.upload([spanishFilePath]);

		await exportImportTranslationPage.upload(
			[invalidFilePath],
			exportImportTranslationPage.replaceFilesButton
		);

		await expect(
			page.getByText(path.basename(spanishFilePath), {exact: true})
		).toBeHidden();

		// Publish only the invalid file

		await exportImportTranslationPage.submit('Publish');

		await expect(exportImportTranslationPage.failureTitle).toHaveText(
			'1 file could not be published.'
		);

		await expect(exportImportTranslationPage.successTitle).toBeHidden();

		await expectNotTranslated(apiHelpers, page, site, title, 'de-DE');

		// Upload the valid files from the results

		await exportImportTranslationPage.uploadAnotherFile();

		await exportImportTranslationPage.upload([
			germanFilePath,
			spanishFilePath,
		]);

		await exportImportTranslationPage.submit('Publish');

		await expect(exportImportTranslationPage.successTitle).toHaveText(
			'All Files Published'
		);

		await expectTranslated(apiHelpers, page, site, title, 'de-DE', GERMAN);
		await expectTranslated(apiHelpers, page, site, title, 'es-ES', SPANISH);
	}
);
