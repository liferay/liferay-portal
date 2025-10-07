/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import fs from 'fs/promises';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	loginTest()
);

test(
	'Document can be downloaded from the Files section and saved correctly',
	{tag: '@LPD-54566'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';
		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'R0lGODlhAQABAAAAACw=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: `title ${getRandomString()}`,
			},
			applicationName,
			'Default'
		);

		try {
			apiHelpers.data.push({
				id: objectEntry.file.id,
				type: 'document',
			});

			await assetsPage.gotoFiles();
			await assetsPage.changeVisualizationMode('Table');

			const downloadPromise = page.waitForEvent('download');
			await assetsPage.execItemAction({
				action: 'Download',
				filter: objectEntry.title,
			});

			const download = await downloadPromise;
			expect(download.suggestedFilename()).toBe(objectEntry.file.name);

			const downloadStat = await fs.stat(await download.path());
			expect(downloadStat.size).toBeGreaterThan(10);
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry.id)
			);
		}
	}
);

test(
	'The Space selector dialog is not shown when creating a Basic Document when only the default Space exists',
	{tag: '@LPD-57827'},
	async ({apiHelpers, assetsPage, page}) => {
		await test.step('Check number of existing Spaces', async () => {
			const assetLibraries =
				await apiHelpers.headlessAssetLibrary.getAssetLibrariesPage(
					"type eq 'Space'"
				);

			expect(
				assetLibraries.length,
				'Only the default Space should exist'
			).toBe(1);
		});

		await test.step('Create a Basic Document', async () => {
			await assetsPage.gotoFiles();

			await assetsPage.createContent('Single File');
		});

		await test.step('Check the Space selector dialog', async () => {
			await expect(page.getByRole('dialog')).not.toBeVisible();
		});

		await test.step('Check the Space name in the Basic Document creation page', async () => {
			await page
				.getByRole('heading', {name: 'Edit Basic Document'})
				.waitFor();

			const spaceSpan = page.locator(
				'//span[contains(@class,"sticker")]//following-sibling::span[1]'
			);

			await expect(spaceSpan).toContainText('Default');
		});
	}
);

test(
	'The Space selector dialog is shown when creating a Basic Document when multiple Spaces exist',
	{tag: '@LPD-57827'},
	async ({apiHelpers, assetsPage, page}) => {
		const assetLibraryName = getRandomString();

		await test.step('Create a new Space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: assetLibraryName,
				settings: {},
				type: 'Space',
			});
		});

		await test.step('Check number of existing Spaces', async () => {
			const assetLibraries =
				await apiHelpers.headlessAssetLibrary.getAssetLibrariesPage(
					"type eq 'Space'"
				);

			expect(
				assetLibraries.length,
				'At least 2 Spaces should exist'
			).toBeGreaterThan(1);
		});

		await test.step('Create a Basic Document', async () => {
			await assetsPage.gotoFiles();

			await assetsPage.createContent('Single File');
		});

		await test.step('Check the Space selector dialog', async () => {
			await page.getByRole('dialog').waitFor();

			await page.getByLabel('SpaceRequired').click();

			await page.getByRole('option', {name: assetLibraryName}).click();

			await page.getByRole('button', {name: 'Save'}).click();
		});

		await test.step('Check the Space name in the Basic Document creation page', async () => {
			await page
				.getByRole('heading', {name: 'Edit Basic Document'})
				.waitFor();

			const spaceSpan = page.locator(
				'//span[contains(@class,"sticker")]//following-sibling::span[1]'
			);

			await expect(spaceSpan).toContainText(assetLibraryName);
		});
	}
);

test(
	'The Space selector dialog is not shown when creating a Basic Document inside a folder when multiple Spaces exist',
	{tag: '@LPD-57827'},
	async ({apiHelpers, assetsPage, page}) => {
		const assetLibraryName = getRandomString();

		await test.step('Create a new Space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: assetLibraryName,
				settings: {},
				type: 'Space',
			});
		});

		await test.step('Check number of existing Spaces', async () => {
			const assetLibraries =
				await apiHelpers.headlessAssetLibrary.getAssetLibrariesPage(
					"type eq 'Space'"
				);

			expect(
				assetLibraries.length,
				'At least 2 Spaces should exist'
			).toBeGreaterThan(1);
		});

		const folderData = await test.step('Create a folder', async () => {
			return await apiHelpers.objectFolder.createObjectEntryFolder({
				scopeKey: assetLibraryName,
				title: getRandomString(),
			});
		});

		await test.step('Navigate into the folder', async () => {
			const className =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					'com.liferay.object.model.ObjectEntryFolder'
				);

			await page.goto(
				`${PORTLET_URLS.cmsViewFolder}/${className.classNameId}/${folderData.id}`
			);
		});

		await test.step('Create a Basic Document', async () => {
			await assetsPage.createContent('Single File');
		});

		await test.step('Check the Space selector dialog', async () => {
			await expect(page.getByRole('dialog')).not.toBeVisible();
		});

		await test.step('Check the Space name in the Basic Document creation page', async () => {
			await page
				.getByRole('heading', {name: 'Edit Basic Document'})
				.waitFor();

			const spaceSpan = page.locator(
				'//span[contains(@class,"sticker")]//following-sibling::span[1]'
			);

			await expect(spaceSpan).toContainText(assetLibraryName);
		});
	}
);

test(
	'There is a View action for items the Files section',
	{tag: '@LPD-58720'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';
		const imageName = `Image ${getRandomString()}`;

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'R0lGODlhAQABAAAAACw=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: imageName,
			},
			applicationName,
			'Default'
		);

		try {
			apiHelpers.data.push({
				id: objectEntry.file.id,
				type: 'document',
			});

			await assetsPage.gotoFiles();

			await assetsPage.execItemAction({
				action: 'View',
				filter: objectEntry.title,
			});

			await expect(page.getByRole('dialog')).toBeVisible();

			await expect(page.getByText(imageName)).toBeVisible();
			await expect(
				page.getByRole('link', {name: 'Download'})
			).toBeVisible();

			await expect(page.getByText('No preview available')).toBeVisible();
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry.id)
			);
		}
	}
);

test(
	'Can navigate through items in the Files section',
	{tag: '@LPD-59866'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';

		const image1 = `Image ${getRandomString()}`;
		const image2 = `Image ${getRandomString()}`;
		const folder = `Folder ${getRandomString()}`;

		const objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'R0lGODlhAQABAAAAACw=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: image1,
			},
			applicationName,
			'Default'
		);

		const objectEntry2 = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'R0lGODlhAQABAAAAACw=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: image2,
			},
			applicationName,
			'Default'
		);

		const folderData =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				scopeKey: 'Default',
				title: folder,
			});

		try {
			apiHelpers.data.push({
				id: objectEntry1.file.id,
				type: 'document',
			});

			apiHelpers.data.push({
				id: objectEntry2.file.id,
				type: 'document',
			});

			await assetsPage.gotoFiles();

			await assetsPage.execItemAction({
				action: 'View',
				filter: image2,
			});

			await test.step('folders are excluded from the navigation list', async () => {
				await expect(page.getByText('2 of 2')).toBeVisible();
			});

			await test.step('Can navigate to the next item', async () => {
				await expect(
					page.locator('.modal-title').getByText(image2)
				).toBeVisible();
				await page.getByLabel('Next').click();
				await expect(
					page.locator('.modal-title').getByText(image1)
				).toBeVisible();
				await expect(page.getByText('1 of 2')).toBeVisible();
			});

			await test.step('Can open the info panel', async () => {
				await page.getByLabel('Show Details').click();
				await expect(
					page.getByRole('tab', {name: 'More'})
				).toBeInViewport();
				await expect(page.getByText('Metadata')).toBeVisible();
			});

			await test.step('Can add a comment', async () => {
				await page.getByLabel('Show Comments').click();

				await expect(
					page.getByRole('tab', {name: 'Details'})
				).not.toBeVisible();

				const commentText = getRandomString();
				await page.getByRole('paragraph').fill(commentText);
				await page.getByRole('button', {name: 'Save'}).click();

				await expect(page.getByText(commentText)).toBeVisible();
			});
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry1.id)
			);
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry2.id)
			);
			await apiHelpers.objectFolder.deleteObjectEntryFolder(
				folderData.id
			);
		}
	}
);
