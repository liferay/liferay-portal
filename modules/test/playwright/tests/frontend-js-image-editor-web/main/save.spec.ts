/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect} from '@playwright/test';

import {imageEditorSamplePageTest as test} from './fixtures/imageEditorSamplePageTest';
import inspectExportedImage from './utils/inspectExportedImage';

test(
	'The host receives the picture and the editor closes itself',
	{tag: '@LPD-105474'},
	async ({imageEditorSamplePage, page}) => {
		const imageEditor = await imageEditorSamplePage.openEditor();

		const download = await imageEditor.save();

		expect(download.suggestedFilename()).toBe('sample-edited.jpg');

		const exported = await inspectExportedImage(page, download);

		expect(exported.width).toBe(4032);
		expect(exported.height).toBe(2268);

		await expect(imageEditor.root).toBeHidden();
	}
);
