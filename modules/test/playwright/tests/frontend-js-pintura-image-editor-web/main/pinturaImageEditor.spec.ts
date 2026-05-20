/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';

// Minimal valid 1x1 PNG encoded as base64

const VALID_IMAGE_BASE64 =
	'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

test(
	'Shows Edit Image action in gallery view for image assets',
	{tag: '@LPD-86264'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';
		const fileName = getRandomString();

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: VALID_IMAGE_BASE64,
					name: `${fileName}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: `title ${fileName}`,
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

			await expect(
				assetsPage.galleryPreview.getByRole('img', {
					name: `${fileName}.png`,
				})
			).toBeVisible();

			// Three-dots actions button should appear for image assets

			const actionsButton = assetsPage.galleryPreview.getByRole(
				'button',
				{name: 'actions'}
			);

			await expect(actionsButton).toBeVisible();

			// Open the actions dropdown

			await actionsButton.click();

			// Edit Image option should be visible

			await expect(
				page.getByRole('menuitem', {name: 'Edit Image'})
			).toBeVisible();
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
	'Opens Pintura editor modal with correct title on Edit Image',
	{tag: '@LPD-86264'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';
		const fileName = getRandomString();

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: VALID_IMAGE_BASE64,
					name: `${fileName}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: `title ${fileName}`,
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

			await expect(
				assetsPage.galleryPreview.getByRole('img', {
					name: `${fileName}.png`,
				})
			).toBeVisible();

			// Click three-dots and select Edit Image

			const actionsButton = assetsPage.galleryPreview.getByRole(
				'button',
				{name: 'actions'}
			);

			await actionsButton.click();

			await page.getByRole('menuitem', {name: 'Edit Image'}).click();

			// Modal should open with the correct title

			const modal = page.getByRole('dialog');

			await expect(modal).toBeVisible();

			await expect(modal.getByText(`Edit ${fileName}.png`)).toBeVisible();

			// Pintura editor container should be rendered

			await expect(
				modal.locator('.PinturaEditor, [class*="pintura"]').first()
			).toBeVisible();

			// Cancel closes the modal

			await modal.getByRole('button', {name: 'cancel'}).click();

			await expect(modal).not.toBeVisible();
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
	'Does not show Edit Image action for non-image assets',
	{tag: '@LPD-86264'},
	async ({apiHelpers, assetsPage}) => {
		const applicationName = 'cms/external-videos';
		const fileName = getRandomString();

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: `title ${fileName}`,
				videoURL: 'https://www.youtube.com/watch?v=IqCSx3omX4o',
			},
			applicationName,
			'Default'
		);

		try {
			apiHelpers.data.push({
				id: objectEntry.id,
				type: 'document',
			});

			await assetsPage.gotoFiles();

			// For video assets, the actions button should NOT appear

			const actionsButton = assetsPage.galleryPreview.getByRole(
				'button',
				{name: 'actions'}
			);

			await expect(actionsButton).not.toBeVisible();
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry.id)
			);
		}
	}
);
