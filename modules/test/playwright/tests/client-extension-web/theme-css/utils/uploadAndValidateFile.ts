/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';
import {resolve} from 'path';

import {EditThemeCSSClientExtensionsPage} from '../pages/EditThemeCSSClientExtensionsPage';

export default async function uploadAndValidateFile(
	fileName: string,
	message: string,
	page: Page,
	editThemeCSSClientExtensionsPage: EditThemeCSSClientExtensionsPage
) {
	await editThemeCSSClientExtensionsPage.uploadFrontendTokenDefinitionFile(
		resolve(__dirname, '..'),
		fileName
	);

	await expect(page.getByText(message)).toBeVisible();
}
