/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {addMappingFragment} from '../../../../utils/addMappingFragment';
import getRandomString from '../../../../utils/getRandomString';
import {isImageLoaded} from '../../../../utils/isImageLoaded';
import {performLoginViaApi, userData} from '../../../../utils/performLogin';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

const APPLICATION_NAME = 'cms/basic-documents';

const ENTITY = 'Basic Documents (CMS)';

const FILE_FRAGMENT_HTML = `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><lfr-editable id="image" type="image"><img alt="" src=""/></lfr-editable></div>`;

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

const replacementImageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_medium_tall_600x800.jpg')
).toString('base64');

const pdfBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/test.pdf')
).toString('base64');

test(
	'A CMS image mapped to page fragments renders and live-updates for GUEST and USER',
	{tag: ['@LPD-95525', '@LPD-95525/TC-2.c']},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		test.setTimeout(240000);

		const spaceName = `Space ${getRandomString()}`;
		const fileTitle = `Image ${getRandomString()}`;
		const updatedTitle = `Image ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageBase64,
					name: `${getRandomString()}.jpg`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			APPLICATION_NAME,
			spaceName
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			APPLICATION_NAME,
			entry.id,
			[
				{actionIds: ['VIEW'], roleName: 'Guest'},
				{actionIds: ['VIEW'], roleName: 'User'},
			]
		);

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: FILE_FRAGMENT_HTML,
			pageEditorPage,
			site,
		});

		await test.step('Map the file Title and Preview URL into the page fragment and publish', async () => {
			await pageEditorPage.selectEditable(fragmentId, 'title');

			await pageEditorPage.setMappingConfiguration({
				mapping: {entity: ENTITY, entry: fileTitle, field: 'Title'},
			});

			await pageEditorPage.selectEditable(fragmentId, 'image');

			await page.getByLabel('Source Selection').selectOption('Mapping');

			await pageEditorPage.setMappedItem({
				entity: ENTITY,
				entry: fileTitle,
				field: 'Preview URL',
			});

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();
		});

		await test.step('GUEST sees the image, then the replaced file without re-publishing', async () => {
			const guestContext = await browser.newContext();

			const guestPage = await guestContext.newPage();

			try {
				let initialImageSrc: string;

				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(fileTitle, {exact: true})
					).toBeVisible({timeout: 2000});

					const image = guestPage
						.locator('img[src*="/documents/"]')
						.first();

					expect(await isImageLoaded(image)).toBe(true);

					initialImageSrc = await image.getAttribute('src');
				}).toPass({timeout: 30000});

				await apiHelpers.objectEntry.putObjectEntry(
					{
						file: {
							fileBase64: replacementImageBase64,
							name: `${getRandomString()}.jpg`,
						},
						title: updatedTitle,
					},
					APPLICATION_NAME,
					entry.id
				);

				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(updatedTitle, {exact: true})
					).toBeVisible({timeout: 2000});

					const image = guestPage
						.locator('img[src*="/documents/"]')
						.first();

					expect(await isImageLoaded(image)).toBe(true);

					expect(await image.getAttribute('src')).not.toBe(
						initialImageSrc
					);
				}).toPass({timeout: 30000});
			}
			finally {
				await guestContext.close();
			}
		});

		await test.step('USER sees the updated mapped file', async () => {
			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
			await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

			await apiHelpers.jsonWebServicesUser.addGroupUsers(
				String(site.id),
				[user.id]
			);

			const userContext = await browser.newContext();

			const userPage = await userContext.newPage();

			try {
				await performLoginViaApi({
					page: userPage,
					screenName: user.alternateName,
				});

				await expect(async () => {
					await userPage.goto(viewUrl);

					await expect(
						userPage.getByText(updatedTitle, {exact: true})
					).toBeVisible({timeout: 2000});

					expect(
						await isImageLoaded(
							userPage.locator('img[src*="/documents/"]').first()
						)
					).toBe(true);
				}).toPass({timeout: 30000});
			}
			finally {
				await userContext.close();
			}
		});
	}
);

test(
	'A mapped PDF is downloadable for USER but not GUEST',
	{tag: ['@LPD-95525', '@LPD-95525/TC-2.d']},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		test.setTimeout(240000);

		const spaceName = `Space ${getRandomString()}`;
		const fileTitle = `PDF ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {fileBase64: pdfBase64, name: `${getRandomString()}.pdf`},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			APPLICATION_NAME,
			spaceName
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			APPLICATION_NAME,
			entry.id,
			[
				{actionIds: ['VIEW'], roleName: 'Guest'},
				{actionIds: ['DOWNLOAD_FILE', 'VIEW'], roleName: 'User'},
			]
		);

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: FILE_FRAGMENT_HTML,
			pageEditorPage,
			site,
		});

		const downloadHref = entry.file.link.href;

		await test.step('Map the file Title and Preview URL into the page fragment and publish', async () => {
			await pageEditorPage.selectEditable(fragmentId, 'title');

			await pageEditorPage.setMappingConfiguration({
				mapping: {entity: ENTITY, entry: fileTitle, field: 'Title'},
			});

			await pageEditorPage.selectEditable(fragmentId, 'image');

			await page.getByLabel('Source Selection').selectOption('Mapping');

			await pageEditorPage.setMappedItem({
				entity: ENTITY,
				entry: fileTitle,
				field: 'Preview URL',
			});

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();
		});

		await test.step('GUEST sees the file but cannot download it', async () => {
			const guestContext = await browser.newContext({
				storageState: {cookies: [], origins: []},
			});

			const guestPage = await guestContext.newPage();

			try {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(fileTitle, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});

				// Use redirect: 'manual' so an authentication redirect is
				// not silently followed into a 200. A guest without
				// DOWNLOAD_FILE cannot fetch the file (the object-entry
				// download answers 404), so the requirement is simply that
				// it is never a successful download.

				const guestDownloadStatus = await guestPage.evaluate(
					async (href) =>
						(await fetch(href, {redirect: 'manual'})).status,
					downloadHref
				);

				expect(guestDownloadStatus).not.toBe(200);
			}
			finally {
				await guestContext.close();
			}
		});

		await test.step('USER can download the file', async () => {
			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
			await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

			await apiHelpers.jsonWebServicesUser.addGroupUsers(
				String(site.id),
				[user.id]
			);

			const userContext = await browser.newContext();

			const userPage = await userContext.newPage();

			try {
				await performLoginViaApi({
					page: userPage,
					screenName: user.alternateName,
				});

				await expect(async () => {
					await userPage.goto(viewUrl);

					await expect(
						userPage.getByText(fileTitle, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});

				const download = await userPage.evaluate(async (href) => {
					const response = await fetch(href);

					return {
						contentType: response.headers.get('content-type'),
						ok: response.ok,
					};
				}, downloadHref);

				expect(download.ok).toBe(true);

				expect(download.contentType).toContain('application/pdf');
			}
			finally {
				await userContext.close();
			}
		});
	}
);

test(
	'A mapped PDF is downloadable for GUEST when download is granted to Guest',
	{tag: ['@LPD-95525', '@LPD-95525/TC-2.e']},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		test.setTimeout(240000);

		const spaceName = `Space ${getRandomString()}`;
		const fileTitle = `PDF ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {fileBase64: pdfBase64, name: `${getRandomString()}.pdf`},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			APPLICATION_NAME,
			spaceName
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			APPLICATION_NAME,
			entry.id,
			[
				{actionIds: ['DOWNLOAD_FILE', 'VIEW'], roleName: 'Guest'},
				{actionIds: ['DOWNLOAD_FILE', 'VIEW'], roleName: 'User'},
			]
		);

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: FILE_FRAGMENT_HTML,
			pageEditorPage,
			site,
		});

		const downloadHref = entry.file.link.href;

		await test.step('Map the file Title and Preview URL into the page fragment and publish', async () => {
			await pageEditorPage.selectEditable(fragmentId, 'title');

			await pageEditorPage.setMappingConfiguration({
				mapping: {entity: ENTITY, entry: fileTitle, field: 'Title'},
			});

			await pageEditorPage.selectEditable(fragmentId, 'image');

			await page.getByLabel('Source Selection').selectOption('Mapping');

			await pageEditorPage.setMappedItem({
				entity: ENTITY,
				entry: fileTitle,
				field: 'Preview URL',
			});

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();
		});

		await test.step('GUEST can download the file', async () => {
			const guestContext = await browser.newContext({
				storageState: {cookies: [], origins: []},
			});

			const guestPage = await guestContext.newPage();

			try {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(fileTitle, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});

				const download = await guestPage.evaluate(async (href) => {
					const response = await fetch(href);

					return {
						contentType: response.headers.get('content-type'),
						ok: response.ok,
					};
				}, downloadHref);

				expect(download.ok).toBe(true);

				expect(download.contentType).toContain('application/pdf');
			}
			finally {
				await guestContext.close();
			}
		});
	}
);
