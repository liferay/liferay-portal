/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect} from '@playwright/test';

import {imageEditorSamplePageTest as test} from './fixtures/imageEditorSamplePageTest';
import inspectExportedImage from './utils/inspectExportedImage';

test(
	'A ratio crop travels to the exported picture',
	{tag: '@LPD-105474'},
	async ({imageEditorSamplePage, page}) => {
		const imageEditor = await imageEditorSamplePage.openEditor();

		await imageEditor.selectRatio('1:1');

		const exported = await inspectExportedImage(
			page,
			await imageEditor.save()
		);

		expect(exported.width).toBe(2268);
		expect(exported.height).toBe(2268);
	}
);

test(
	'A filter reaches the pixels the host receives',
	{tag: '@LPD-105474'},
	async ({imageEditorSamplePage, page}) => {
		const imageEditor = await imageEditorSamplePage.openEditor();

		await imageEditor.selectFilter('grayscale');

		const exported = await inspectExportedImage(
			page,
			await imageEditor.save(),
			[
				{x: 0.25, y: 0.25},
				{x: 0.75, y: 0.75},
			]
		);

		for (const {blue, green, red} of exported.pixels) {
			expect(Math.abs(red - green)).toBeLessThanOrEqual(4);
			expect(Math.abs(green - blue)).toBeLessThanOrEqual(4);
		}
	}
);
