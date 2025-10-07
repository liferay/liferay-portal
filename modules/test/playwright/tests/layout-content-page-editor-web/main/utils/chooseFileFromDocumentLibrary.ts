/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';

export default function chooseFileFromDocumentLibrary(options: {
	filePath: string;
	page: Page;
	trigger: Locator;
	type?: 'file' | 'image';
}): Promise<void>;

export default function chooseFileFromDocumentLibrary(options: {
	fileName: string;
	page: Page;
	trigger: Locator;
	type?: 'file' | 'image';
}): Promise<void>;

/**
 * Allow selecting a file from document library. We should pass
 * fileName if it's a file that is already uploaded and filePath
 * if we want to upload a new file to the document library
 */

export default async function chooseFileFromDocumentLibrary({
	fileName,
	filePath,
	page,
	trigger,
	type = 'file',
}: {
	fileName?: string;
	filePath?: string;
	page: Page;
	trigger: Locator;
	type?: 'file' | 'image';
}) {
	const iframe = page.frameLocator('iframe');

	await clickAndExpectToBeVisible({
		target: iframe.getByText(
			`Drag & Drop Your ${type === 'file' ? 'Files' : 'Images'} or Browse to Upload`
		),
		timeout: 3500,
		trigger,
	});

	if (filePath) {
		const fileChooserPromise = page.waitForEvent('filechooser', {
			timeout: 2000,
		});

		await expect(async () => {
			await iframe
				.getByText(
					`Drag & Drop Your ${type === 'file' ? 'Files' : 'Images'} or Browse to Upload`
				)
				.click({timeout: 1000});

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles(filePath, {timeout: 1000});
		}).toPass();

		await iframe.getByRole('button', {name: 'Add'}).waitFor();

		await clickAndExpectToBeHidden({
			target: iframe.getByRole('button', {name: 'Add'}),
			timeout: 2000,
			trigger: iframe.getByRole('button', {name: 'Add'}),
		});
	}
	else if (fileName) {
		await clickAndExpectToBeHidden({
			target: iframe.getByText(fileName),
			timeout: 2000,
			trigger: iframe
				.locator('.card', {hasText: fileName})
				.locator('img'),
		});
	}
}
