/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest(),
	structureBuilderPagesTest
);

const MAXIMUM_FILE_SIZE_MB = 1;

test(
	'Uploading a file whose extension the file structure does not accept is blocked',
	{tag: ['@LPD-95548', '@LPD-95548/TC-23.d']},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		page,
		structureBuilderPage,
	}) => {
		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		const structureLabel = `Structure${getRandomString()}`;

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			page: structureBuilderPage,
			publish: false,
			spaces: [space.name],
			type: 'file',
		});

		await structureBuilderPage.selectFields([{label: 'File'}]);

		await structureBuilderPage.changeFieldSettings({
			acceptedFileExtensions: 'png',
		});

		await structureBuilderPage.publishStructure();

		const title = `File ${getRandomString()}`;

		await test.step('Upload a text file into a structure that accepts only png', async () => {
			await assetsPage.gotoFiles();

			await contentsPage.createContent(structureLabel, space.name);

			await page.locator('input[type="file"]').setInputFiles({
				buffer: Buffer.from('This is not an image.'),
				mimeType: 'text/plain',
				name: `${title}.txt`,
			});

			await expect(page.getByText(`${title}.txt`)).toBeVisible();

			await page
				.getByRole('textbox', {exact: true, name: 'Title'})
				.fill(title);

			await contentsPage.publishButton.click();
		});

		await test.step('An extension error is shown and the file is not saved', async () => {
			await expect(
				page
					.getByText(
						'Please enter a file with a valid extension (png).'
					)
					.first()
			).toBeVisible();

			await expect(
				page.getByRole('heading', {name: 'New'})
			).toBeVisible();
		});
	}
);

test(
	'Uploading a file larger than the maximum size the file structure allows is blocked',
	{tag: ['@LPD-95548', '@LPD-95548/TC-23.e']},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		page,
		structureBuilderPage,
	}) => {
		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		const structureLabel = `Structure${getRandomString()}`;

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			page: structureBuilderPage,
			publish: false,
			spaces: [space.name],
			type: 'file',
		});

		await structureBuilderPage.selectFields([{label: 'File'}]);

		await structureBuilderPage.changeFieldSettings({
			acceptedFileExtensions: 'png',
			maximumFileSize: MAXIMUM_FILE_SIZE_MB,
		});

		await structureBuilderPage.publishStructure();

		const title = `File ${getRandomString()}`;

		await test.step('Upload a png larger than the maximum size', async () => {
			await assetsPage.gotoFiles();

			await contentsPage.createContent(structureLabel, space.name);

			await page.locator('input[type="file"]').setInputFiles({
				buffer: Buffer.alloc((MAXIMUM_FILE_SIZE_MB + 1) * 1024 * 1024),
				mimeType: 'image/png',
				name: `${title}.png`,
			});
		});

		await test.step('A size error naming the limit is shown and the file is never attached', async () => {
			await expect(
				page
					.getByText(
						`Please enter a file with a valid file size no larger than ${MAXIMUM_FILE_SIZE_MB} MB.`
					)
					.first()
			).toBeVisible();

			await expect(page.getByText(`${title}.png`)).toBeHidden();
		});

		await test.step('The file entry cannot be published', async () => {
			await page
				.getByRole('textbox', {exact: true, name: 'Title'})
				.fill(title);

			await contentsPage.publishButton.click();

			await expect(
				page.getByRole('heading', {name: 'New'})
			).toBeVisible();
		});
	}
);
