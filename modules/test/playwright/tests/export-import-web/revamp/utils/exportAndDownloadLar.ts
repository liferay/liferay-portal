/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect} from '@playwright/test';

import getRandomString from '../../../../utils/getRandomString';
import {ExportImportPage} from '../pages/ExportImportPage';

export async function exportAndDownloadLar(
	exportImportPage: ExportImportPage
): Promise<{folderPath: string; name: string}> {
	const name = `MyExport-${getRandomString()}`;

	await exportImportPage.nameInput.fill(name);

	await exportImportPage.exportButton.click();

	await expect(exportImportPage.taskStatusLabel(name)).toBeVisible();

	return {folderPath: await exportImportPage.download(name), name};
}
