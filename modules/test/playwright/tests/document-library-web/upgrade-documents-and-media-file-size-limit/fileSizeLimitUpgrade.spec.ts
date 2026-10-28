/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {loginTest} from '../../../fixtures/loginTest';
import {DocumentLibraryEditFilePage} from '../../../pages/document-library-web/DocumentLibraryEditFilePage';

const test = mergeTests(loginTest());

test(
	'Keeps the upgraded file size limit',
	{tag: '@LPD-112520'},
	async ({page}) => {
		const {maxFileSize} = test.info().project.use as {
			maxFileSize: string;
		};

		await new DocumentLibraryEditFilePage(page).goto();

		await page
			.locator('input[type=file]')
			.first()
			.setInputFiles(
				path.join(
					__dirname,
					'../../site-cms-site-initializer/main/dependencies/sample_large_square_1500x1500.jpg'
				)
			);

		await expect(
			page.getByText(
				`Please enter a file with a valid file size no larger than ${maxFileSize}`
			)
		).toBeVisible();
	}
);
