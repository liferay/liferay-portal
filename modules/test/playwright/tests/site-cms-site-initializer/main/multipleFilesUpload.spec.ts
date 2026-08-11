/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import getRandomString from '../../../utils/getRandomString';
import {waitForModal} from '../../../utils/waitFor';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {AssetsPage} from './pages/AssetsPage';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

const APPLICATION_NAME = 'cms/basic-documents';

function textFile(name: string) {
	return {
		buffer: Buffer.from(`Content of ${name}.`),
		mimeType: 'text/plain',
		name,
	};
}

async function createSpace(apiHelpers: DataApiHelpers) {
	const {name} = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: getRandomString(),
		type: 'Space',
	});

	return name;
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

async function openUploadModal({
	assetsPage,
	page,
}: {
	assetsPage: AssetsPage;
	page: Page;
}) {
	await assetsPage.createContent('Multiple Files');

	await waitForModal({page});

	await expect(assetsPage.modal.title).toHaveText('Upload Multiple Files');
}

async function queueFiles({
	assetsPage,
	files,
}: {
	assetsPage: AssetsPage;
	files: ReturnType<typeof textFile>[];
}) {
	await assetsPage.modal.container
		.locator('input[type="file"]')
		.setInputFiles(files);
}

async function selectSpace({
	assetsPage,
	page,
	spaceName,
}: {
	assetsPage: AssetsPage;
	page: Page;
	spaceName: string;
}) {
	await assetsPage.modal.container.getByLabel(/^Space/).click();

	await page.getByRole('option', {name: spaceName}).click();
}

async function submitUpload({assetsPage}: {assetsPage: AssetsPage}) {
	await assetsPage.modal.footer
		.getByRole('button', {name: /^Upload/})
		.click();
}

test(
	'Uploads several files at once to the selected Space',
	{tag: '@LPD-102002'},
	async ({apiHelpers, assetsPage, page}) => {
		const spaceName = await createSpace(apiHelpers);

		const titles = [
			`${getRandomString()}.txt`,
			`${getRandomString()}.txt`,
			`${getRandomString()}.txt`,
		];

		await assetsPage.gotoFiles();

		await openUploadModal({assetsPage, page});

		await queueFiles({assetsPage, files: titles.map(textFile)});

		await selectSpace({assetsPage, page, spaceName});

		await submitUpload({assetsPage});

		await waitForAlert(
			page,
			`3 files were successfully uploaded to ${spaceName} space.`
		);

		await expect(assetsPage.modal.container).toBeHidden();

		expect(
			await getDocuments({apiHelpers, spaceName, titles})
		).toHaveLength(3);
	}
);

test(
	'Requires a Space before uploading',
	{tag: '@LPD-102002'},
	async ({apiHelpers, assetsPage, page}) => {
		const spaceName = await createSpace(apiHelpers);

		const title = `${getRandomString()}.txt`;

		await assetsPage.gotoFiles();

		await openUploadModal({assetsPage, page});

		await queueFiles({assetsPage, files: [textFile(title)]});

		// Submitting without a Space

		await submitUpload({assetsPage});

		await expect(
			assetsPage.modal.container.getByText('This field is required.')
		).toBeVisible();

		await expect(assetsPage.modal.container).toBeVisible();

		expect(
			await getDocuments({apiHelpers, spaceName, titles: [title]})
		).toHaveLength(0);

		// Submitting once a Space is selected

		await selectSpace({assetsPage, page, spaceName});

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

test(
	'Reports the files the server rejects and keeps the valid ones',
	{tag: '@LPD-102002'},
	async ({apiHelpers, assetsPage, page}) => {
		const spaceName = await createSpace(apiHelpers);

		const rejectedTitle = `${'x'.repeat(300)}.txt`;
		const title = `${getRandomString()}.txt`;

		await assetsPage.gotoFiles();

		await openUploadModal({assetsPage, page});

		// The rejected file has to be queued first. LPD-102009 closes the
		// modal on the first success, hiding any failure that follows it.

		await queueFiles({
			assetsPage,
			files: [textFile(rejectedTitle), textFile(title)],
		});

		await selectSpace({assetsPage, page, spaceName});

		await submitUpload({assetsPage});

		await expect(
			assetsPage.modal.container.getByText(/could not be uploaded/)
		).toBeVisible();

		await expect(
			assetsPage.modal.container.getByText(rejectedTitle)
		).toBeVisible();

		await expect(
			assetsPage.modal.container.locator('span.text-danger')
		).toBeVisible();

		await expect(assetsPage.modal.container).toBeVisible();

		expect(
			await getDocuments({
				apiHelpers,
				spaceName,
				titles: [rejectedTitle, title],
			})
		).toHaveLength(1);
	}
);

test(
	'Does not ask for a Space when uploading inside a folder',
	{tag: '@LPD-102002'},
	async ({apiHelpers, assetsPage, page}) => {
		const spaceName = await createSpace(apiHelpers);

		const folderTitle = getRandomString();

		const folder = await apiHelpers.objectFolder.createObjectEntryFolder({
			scopeKey: spaceName,
			title: folderTitle,
		});

		const title = `${getRandomString()}.txt`;

		await assetsPage.gotoFolder(folder.id, folderTitle);

		await openUploadModal({assetsPage, page});

		await expect(
			assetsPage.modal.container.getByLabel(/^Space/)
		).toBeHidden();

		await queueFiles({assetsPage, files: [textFile(title)]});

		await submitUpload({assetsPage});

		await waitForAlert(
			page,
			`1 file was successfully uploaded to ${spaceName} space.`
		);

		await expect(assetsPage.modal.container).toBeHidden();

		const [document] = await getDocuments({
			apiHelpers,
			spaceName,
			titles: [title],
		});

		expect(document.objectEntryFolderExternalReferenceCode).toBe(
			folder.externalReferenceCode
		);
	}
);
