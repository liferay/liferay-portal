/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {AssetsPage} from './pages/AssetsPage';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

const APPLICATION_NAME = 'cms/basic-documents';

async function createSpace(apiHelpers: DataApiHelpers) {
	const {name} = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: getRandomString(),
		type: 'Space',
	});

	return name;
}

async function dropFiles({
	fileNames,
	page,
	target,
}: {
	fileNames: string[];
	page: Page;
	target: Locator;
}) {
	const dataTransfer = await page.evaluateHandle((fileNames) => {
		const dataTransfer = new DataTransfer();

		for (const fileName of fileNames) {
			dataTransfer.items.add(
				new File([`Content of ${fileName}.`], fileName, {
					type: 'text/plain',
				})
			);
		}

		return dataTransfer;
	}, fileNames);

	await target.dispatchEvent('dragenter', {dataTransfer});
	await target.dispatchEvent('dragover', {dataTransfer});
	await target.dispatchEvent('drop', {dataTransfer});
}

async function getDocuments({
	apiHelpers,
	spaceName,
	titles,
}: {
	apiHelpers: DataApiHelpers;
	spaceName: string;
	titles: string[];
}) {
	const {items} =
		await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
			APPLICATION_NAME,
			spaceName,
			new URLSearchParams({pageSize: '100'})
		);

	const documents = (items || []).filter((item: {title: string}) =>
		titles.includes(item.title)
	);

	for (const document of documents) {
		apiHelpers.data.push({
			applicationName: APPLICATION_NAME,
			id: String(document.id),
			type: 'objectEntry',
		});
	}

	return documents;
}

async function submitUpload({assetsPage}: {assetsPage: AssetsPage}) {
	await assetsPage.modal.footer
		.getByRole('button', {name: /^Upload/})
		.click();
}

test(
	'Uploads the files dropped into the Files section',
	{tag: '@LPD-102030'},
	async ({apiHelpers, assetsPage, page}) => {
		const spaceName = await createSpace(apiHelpers);

		const titles = [`${getRandomString()}.txt`, `${getRandomString()}.txt`];

		await assetsPage.gotoFiles();

		await dropFiles({
			fileNames: titles,
			page,
			target: page.locator('div.data-set-wrapper').first(),
		});

		// The dropped files arrive already queued in the uploader

		await expect(assetsPage.modal.title).toHaveText(
			'Upload Multiple Files'
		);

		for (const title of titles) {
			await expect(assetsPage.modal.body).toContainText(title);
		}

		await assetsPage.modal.container.getByLabel(/^Space/).click();

		await page.getByRole('option', {name: spaceName}).click();

		await submitUpload({assetsPage});

		await waitForAlert(
			page,
			`2 files were successfully uploaded to ${spaceName} space.`
		);

		await expect(assetsPage.modal.container).toBeHidden();

		expect(
			await getDocuments({apiHelpers, spaceName, titles})
		).toHaveLength(2);
	}
);

test(
	'Uploads a drop into a Space Files section without asking for a Space',
	{tag: '@LPD-102030'},
	async ({apiHelpers, assetsPage, page}) => {
		const spaceName = await createSpace(apiHelpers);

		const title = `${getRandomString()}.txt`;

		const rootFolder =
			await apiHelpers.objectFolder.getObjectEntryFolderByExternalReferenceCode(
				{externalReferenceCode: 'L_FILES', scopeKey: spaceName}
			);

		await assetsPage.gotoFolder(rootFolder.id, rootFolder.title);

		await dropFiles({
			fileNames: [title],
			page,
			target: page.locator('div.data-set-wrapper').first(),
		});

		await expect(assetsPage.modal.title).toHaveText(
			'Upload Multiple Files'
		);

		await expect(
			assetsPage.modal.container.getByLabel(/^Space/)
		).toBeHidden();

		await submitUpload({assetsPage});

		await waitForAlert(
			page,
			`1 file was successfully uploaded to ${spaceName} space.`
		);

		await expect(assetsPage.modal.container).toBeHidden();

		expect(
			await getDocuments({apiHelpers, spaceName, titles: [title]})
		).toHaveLength(1);
	}
);
