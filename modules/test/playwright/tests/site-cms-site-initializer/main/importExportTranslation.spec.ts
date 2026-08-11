/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import createTempFile from '../../../utils/createTempFile';
import getRandomString from '../../../utils/getRandomString';
import {waitForModal} from '../../../utils/waitFor';
import {waitForAlert} from '../../../utils/waitForAlert';
import {readFileFromZip} from '../../../utils/zip';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {AssetsPage} from './pages/AssetsPage';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

const APPLICATION_NAME = 'cms/basic-web-contents';

async function createContent(apiHelpers: DataApiHelpers, title: string) {
	const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
		{
			objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			title,
		},
		APPLICATION_NAME,
		'Default'
	);

	apiHelpers.data.push({
		applicationName: APPLICATION_NAME,
		id: String(objectEntry.id),
		type: 'objectEntry',
	});

	return objectEntry;
}

async function exportTranslationFile({
	assetsPage,
	page,
	targetLanguages,
	title,
}: {
	assetsPage: AssetsPage;
	page: Page;
	targetLanguages: string[];
	title: string;
}) {
	await assetsPage.execItemAction({
		action: 'Export for Translation',
		filter: title,
	});

	await waitForModal({page});

	return assetsPage.exportForTranslation(false, targetLanguages);
}

async function getTitleTranslations(
	apiHelpers: DataApiHelpers,
	objectEntryId: number
) {
	const {title_i18n} = await apiHelpers.objectEntry.getObjectEntryById(
		APPLICATION_NAME,
		String(objectEntryId)
	);

	return title_i18n;
}

async function openImportTranslationModal({
	assetsPage,
	page,
	title,
}: {
	assetsPage: AssetsPage;
	page: Page;
	title: string;
}) {
	await assetsPage.execItemAction({
		action: 'Import Translation',
		filter: title,
	});

	await waitForModal({page});

	await expect(assetsPage.modal.container).toContainText(
		'Import Translation'
	);
}

async function submitTranslationFiles({
	assetsPage,
	filePaths,
}: {
	assetsPage: AssetsPage;
	filePaths: string[];
}) {
	await assetsPage.modal.container
		.locator('input[type="file"]')
		.setInputFiles(filePaths);

	await assetsPage.modal.footer
		.getByRole('button', {exact: true, name: 'Import'})
		.click();
}

function translateXliff(xliff: string, translatedTitle: string) {
	const translatedXliff = xliff.replace(
		/(<unit id="ObjectField_title">[\s\S]*?<target>)(<\/target>)/,
		`$1<![CDATA[${translatedTitle}]]>$2`
	);

	expect(translatedXliff).toContain(translatedTitle);

	return translatedXliff;
}

test(
	'Imports a translation file exported from the same content',
	{tag: '@LPD-101976'},
	async ({apiHelpers, assetsPage, page}) => {
		const title = `Basic Web Content ${getRandomString()}`;
		const spanishTitle = `Contenido ${getRandomString()}`;

		const {id: objectEntryId} = await createContent(apiHelpers, title);

		await assetsPage.gotoContents();

		// Export the content, then translate the exported file

		const zipFilePath = await exportTranslationFile({
			assetsPage,
			page,
			targetLanguages: ['Spanish (Spain)'],
			title,
		});

		const xliff = await readFileFromZip('-es_ES.xlf', zipFilePath);

		const translationFilePath = createTempFile(
			`${getRandomString()}-es_ES.xlf`,
			translateXliff(xliff, spanishTitle)
		);

		// Cancelling the import leaves the content untranslated

		await openImportTranslationModal({assetsPage, page, title});

		await assetsPage.modal.container
			.locator('input[type="file"]')
			.setInputFiles(translationFilePath);

		await assetsPage.modal.footer
			.getByRole('button', {exact: true, name: 'Cancel'})
			.click();

		await expect(assetsPage.modal.container).toBeHidden();

		expect(await getTitleTranslations(apiHelpers, objectEntryId)).toEqual({
			en_US: title,
		});

		// Import the translated file

		await openImportTranslationModal({assetsPage, page, title});

		await submitTranslationFiles({
			assetsPage,
			filePaths: [translationFilePath],
		});

		await waitForAlert(
			page,
			`1 file was successfully imported. ${title} is now published with new translations.`
		);

		await expect(assetsPage.modal.container).toBeHidden();

		// Only the imported language changes

		expect(await getTitleTranslations(apiHelpers, objectEntryId)).toEqual({
			en_US: title,
			es_ES: spanishTitle,
		});
	}
);

test(
	'Imports translation files for several languages at once',
	{tag: '@LPD-101976'},
	async ({apiHelpers, assetsPage, page}) => {
		const title = `Basic Web Content ${getRandomString()}`;
		const chineseTitle = `Nei Rong ${getRandomString()}`;
		const spanishTitle = `Contenido ${getRandomString()}`;

		const {id: objectEntryId} = await createContent(apiHelpers, title);

		await assetsPage.gotoContents();

		// Export the content for two languages, then translate both files

		const zipFilePath = await exportTranslationFile({
			assetsPage,
			page,
			targetLanguages: ['Chinese (China)', 'Spanish (Spain)'],
			title,
		});

		const translationFilePaths = [];

		for (const [languageId, translatedTitle] of [
			['zh_CN', chineseTitle],
			['es_ES', spanishTitle],
		]) {
			const xliff = await readFileFromZip(
				`-${languageId}.xlf`,
				zipFilePath
			);

			translationFilePaths.push(
				createTempFile(
					`${getRandomString()}-${languageId}.xlf`,
					translateXliff(xliff, translatedTitle)
				)
			);
		}

		// Import both files in a single upload

		await openImportTranslationModal({assetsPage, page, title});

		await submitTranslationFiles({
			assetsPage,
			filePaths: translationFilePaths,
		});

		await waitForAlert(page, '2 files were successfully imported.');

		await expect(assetsPage.modal.container).toBeHidden();

		expect(await getTitleTranslations(apiHelpers, objectEntryId)).toEqual({
			en_US: title,
			es_ES: spanishTitle,
			zh_CN: chineseTitle,
		});
	}
);

test(
	'Rejects a translation file exported from a different content',
	{tag: '@LPD-101976'},
	async ({apiHelpers, assetsPage, page}) => {
		const exportedTitle = `Basic Web Content ${getRandomString()}`;
		const targetTitle = `Basic Web Content ${getRandomString()}`;

		await createContent(apiHelpers, exportedTitle);

		const {id: targetObjectEntryId} = await createContent(
			apiHelpers,
			targetTitle
		);

		await assetsPage.gotoContents();

		const zipFilePath = await exportTranslationFile({
			assetsPage,
			page,
			targetLanguages: ['Spanish (Spain)'],
			title: exportedTitle,
		});

		const xliff = await readFileFromZip('-es_ES.xlf', zipFilePath);

		const fileName = `${getRandomString()}-es_ES.xlf`;

		const translationFilePath = createTempFile(
			fileName,
			translateXliff(xliff, `Contenido ${getRandomString()}`)
		);

		// Import the file into a different content

		await openImportTranslationModal({
			assetsPage,
			page,
			title: targetTitle,
		});

		await submitTranslationFiles({
			assetsPage,
			filePaths: [translationFilePath],
		});

		// The modal reports the rejected file and stays open

		await expect(assetsPage.modal.container).toContainText(fileName);
		await expect(assetsPage.modal.container).toContainText(
			'The translation file does not correspond to this web content.'
		);
		await expect(
			assetsPage.modal.footer.getByRole('button', {
				exact: true,
				name: 'Import Another File',
			})
		).toBeVisible();

		expect(
			await getTitleTranslations(apiHelpers, targetObjectEntryId)
		).toEqual({en_US: targetTitle});
	}
);
