/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

const APPLICATION_NAME = 'cms/basic-documents';

const FIXTURES = {
	largeSquare: readFileSync(
		path.join(__dirname, 'dependencies/sample_large_square_1500x1500.jpg')
	).toString('base64'),
	mediumTall: readFileSync(
		path.join(__dirname, 'dependencies/sample_medium_tall_600x800.jpg')
	).toString('base64'),
	smallWide: readFileSync(
		path.join(__dirname, 'dependencies/sample_small_wide_400x300.jpg')
	).toString('base64'),
};

test(
	'File info panel buckets resolution and aspect ratio for image assets',
	{tag: '@LPD-85195'},
	async ({apiHelpers, assetsPage, infoPanelPage, page}) => {
		const spaceName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		const cases = [
			{
				aspectRatio: 'Wide',
				fileBase64: FIXTURES.smallWide,
				resolution: 'Small (Up to 400x300)',
				suffix: 'small-wide',
			},
			{
				aspectRatio: 'Tall',
				fileBase64: FIXTURES.mediumTall,
				resolution: 'Medium (Up to 1024x768)',
				suffix: 'medium-tall',
			},
			{
				aspectRatio: 'Square',
				fileBase64: FIXTURES.largeSquare,
				resolution: 'Large (Bigger Than 1024)',
				suffix: 'large-square',
			},
		];

		for (const {aspectRatio, fileBase64, resolution, suffix} of cases) {
			const title = `${suffix}-${getRandomString()}`;

			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64,
						name: `${title}.jpg`,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title,
				},
				APPLICATION_NAME,
				spaceName
			);

			await assetsPage.gotoAll();

			await assetsPage.execItemAction({
				action: 'Show Details',
				filter: title,
			});

			await test.step(`asserts ${resolution} + ${aspectRatio} for ${suffix}`, async () => {
				await infoPanelPage.expectMetadata('Extension', 'jpg');
				await infoPanelPage.expectMetadata(
					'Size',
					/\d+(\.\d+)?\s?(B|KB|MB)/
				);
				await infoPanelPage.expectMetadata('Resolution', resolution);
				await infoPanelPage.expectMetadata('Aspect Ratio', aspectRatio);
			});

			await page.keyboard.press('Escape');
		}
	}
);

test(
	'Resolution and Aspect Ratio rows are hidden for non-image files',
	{tag: '@LPD-85195'},
	async ({apiHelpers, assetsPage, infoPanelPage}) => {
		const spaceName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		const title = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64:
						Buffer.from(getRandomString()).toString('base64'),
					name: `${title}.txt`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title,
			},
			APPLICATION_NAME,
			spaceName
		);

		await assetsPage.gotoAll();

		await assetsPage.execItemAction({
			action: 'Show Details',
			filter: title,
		});

		await infoPanelPage.expectMetadata('Extension', 'txt');
		await infoPanelPage.expectMetadataHidden('Resolution');
		await infoPanelPage.expectMetadataHidden('Aspect Ratio');
	}
);

test(
	'File metadata buckets are localized when the user switches to Spanish',
	{tag: '@LPD-85195'},
	async ({apiHelpers, assetsPage, infoPanelPage, page}) => {
		const spaceName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		const title = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: FIXTURES.smallWide,
					name: `${title}.jpg`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title,
			},
			APPLICATION_NAME,
			spaceName
		);

		try {
			await assetsPage.gotoAll();
			await assetsPage.execItemAction({
				action: 'Show Details',
				filter: title,
			});

			await infoPanelPage.expectMetadata(
				'Resolution',
				'Small (Up to 400x300)'
			);
			await infoPanelPage.expectMetadata('Aspect Ratio', 'Wide');

			await page.keyboard.press('Escape');

			await apiHelpers.headlessAdminUser.patchMyUserAccountLanguage(
				'es-ES'
			);

			await page.goto(`/es/${PORTLET_URLS.cmsAll}`);
			await page.getByRole('heading', {name: 'Todos'}).waitFor();

			const localizedRow = page
				.locator('.fds table tbody tr')
				.filter({hasText: title});

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					exact: true,
					name: 'Mostrar detalles',
				}),
				trigger: localizedRow.getByRole('button', {
					name: `${title} Acciones`,
				}),
			});

			await infoPanelPage.expectMetadata('Resolución', /.+/);
			await infoPanelPage.expectMetadata('Relación de aspecto', /.+/);
		}
		finally {
			await apiHelpers.headlessAdminUser.patchMyUserAccountLanguage(
				'en-US'
			);
		}
	}
);
