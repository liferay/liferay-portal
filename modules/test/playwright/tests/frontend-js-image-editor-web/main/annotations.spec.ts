/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect} from '@playwright/test';

import {liferayConfig} from '../../../liferay.config';
import {imageEditorSamplePageTest as test} from './fixtures/imageEditorSamplePageTest';
import inspectExportedImage from './utils/inspectExportedImage';

const RED_SQUARE_PNG = Buffer.from(
	'iVBORw0KGgoAAAANSUhEUgAAAAgAAAAICAIAAABLbSncAAAAEklEQVR4nGP4z8CAFWEXHbQSACj/P8Fu7N9hAAAAAElFTkSuQmCC',
	'base64'
);

test(
	'The module serves the emoji catalogue the picker asks for',
	{tag: '@LPD-105474'},
	async ({page}) => {
		const response = await page.request.get(
			`${liferayConfig.environment.baseUrl}/o/frontend-js-image-editor-web/emoji.json`
		);

		expect(response.status()).toBe(200);

		const entries = await response.json();

		expect(entries.length).toBeGreaterThan(1000);
		expect(entries[0]).toHaveProperty('c');
		expect(entries[0]).toHaveProperty('n');
	}
);

test(
	'An uploaded picture reaches the pixels the host receives',
	{tag: '@LPD-105474'},
	async ({imageEditorSamplePage, page}) => {
		const imageEditor = await imageEditorSamplePage.openEditor();

		await imageEditor.addImage('badge.png', RED_SQUARE_PNG);

		await expect(imageEditor.layer('badge')).toBeVisible();

		const exported = await inspectExportedImage(
			page,
			await imageEditor.save(),
			[{x: 0.5, y: 0.5}]
		);

		const [center] = exported.pixels;

		expect(center.red).toBeGreaterThan(200);
		expect(center.blue).toBeLessThan(80);
	}
);

test(
	'A shape annotation reaches the pixels the host receives',
	{tag: '@LPD-105474'},
	async ({imageEditorSamplePage, page}) => {
		const imageEditor = await imageEditorSamplePage.openEditor();

		await imageEditor.addShape('Rectangle');

		await expect(imageEditor.layer('Rectangle')).toBeVisible();

		const exported = await inspectExportedImage(
			page,
			await imageEditor.save(),
			[{x: 0.5, y: 0.5}]
		);

		const [center] = exported.pixels;

		expect(center.blue).toBeGreaterThan(200);
		expect(center.red).toBeLessThan(80);
	}
);
