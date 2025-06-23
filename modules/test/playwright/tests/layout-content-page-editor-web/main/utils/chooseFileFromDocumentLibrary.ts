/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';

export default async function chooseFileFromDocumentLibrary({
	fileName,
	page,
	trigger,
	type = 'file',
}: {
	fileName: string;
	page: Page;
	trigger: Locator;
	type?: 'file' | 'image';
}) {
	const iframe = page.frameLocator('iframe');

	await clickAndExpectToBeVisible({
		target: iframe.getByText(
			`Drag & Drop Your ${type === 'file' ? 'Files' : 'Images'} or Browse to Upload`
		),
		timeout: 2000,
		trigger,
	});

	await clickAndExpectToBeHidden({
		target: iframe.getByText(fileName),
		timeout: 2000,
		trigger: iframe.locator('.card', {hasText: fileName}).locator('img'),
	});
}
