/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {reactPlusCETClassicPageTest} from '../../../../frontend-editor-ckeditor5-sample-web/fixtures/classicPageTest';

export const test = mergeTests(
	reactPlusCETClassicPageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPS-178052': {enabled: true},
	}),
	loginTest()
);

test(
	'Editor configuration is applied, merging existing and client extension customizations',
	{tag: '@LPD-11235'},
	async ({classicPage}) => {
		const expectedButtons = [
			'Undo',
			'Redo',
			'Bold',
			'Italic',
			'Link',
			'Bookmark',
			'Image',
			'Video',
		];

		const availableButtons =
			await classicPage.toolbar.buttonLabels.allInnerTexts();

		for (const button of expectedButtons) {
			expect(availableButtons).toContain(button);
		}
	}
);

test(
	'WordCount plugin loaded via CX tracks word and character counts',
	{tag: '@LPD-89734'},
	async ({classicPage}) => {
		await expect(classicPage.wordCountContainer).toBeVisible();
	}
);

test(
	'"Timestamp" custom plugin added via client extension has SVG icon',
	{tag: '@LPD-89578'},
	async ({classicPage}) => {
		const timestampButton = classicPage.toolbar.container.getByRole(
			'button',
			{
				name: 'Timestamp',
			}
		);

		await expect(timestampButton.locator('svg')).toBeAttached();
	}
);

test(
	'Styles dropdown includes custom style added via client extension',
	{tag: '@LPD-65979'},
	async ({classicPage, page}) => {
		await classicPage.toolbar.container
			.getByRole('button', {name: 'Styles'})
			.click();

		await expect(
			page.getByRole('option', {
				exact: true,
				name: 'Featured Content',
			})
		).toBeVisible();
	}
);

test(
	'"Select Document" button appears in link dialog via DocumentLinkSelector plugin',
	{tag: '@LPD-52631'},
	async ({classicPage, page}) => {
		await classicPage.editable.click();
		await page.keyboard.press('Control+k');

		await expect(page.getByRole('button', {name: 'Insert'})).toBeVisible();
	}
);
