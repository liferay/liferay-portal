/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {EEditorType, waitForEditor} from '../../../../../utils/waitFor';
import {ckeditor4PageTest} from '../../fixtures/ckeditor4PageTest';
import {ckeditorSamplePageTest} from '../../fixtures/ckeditorSamplePageTest';

export const test = mergeTests(
	apiHelpersTest,
	ckeditor4PageTest,
	ckeditorSamplePageTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	isolatedSiteTest
);

test.beforeEach(async ({ckeditorSamplePage, page, site}) => {
	await ckeditorSamplePage.createAndGotoSitePage({site});

	await ckeditorSamplePage.selectTab('CKEditor 4');
	await ckeditorSamplePage.selectTab('Classic');

	await waitForEditor({editorType: EEditorType.CKEDITOR4, page});
});

test(
	'Dropdown and context menus are visible when maximized',
	{tag: ['@LPD-33712', '@LPD-38600']},
	async ({page}) => {
		await test.step('Select Maximized toolbar control', async () => {
			await page.getByRole('button', {name: 'Maximize'}).click();
		});

		await test.step('Assert "Styles" dropdown is visible', async () => {
			await page.getByRole('button', {name: 'Styles'}).click();

			const stylesComboZIndex = await page.evaluate(() => {
				const stylesComboElement = document.querySelector(
					'.cke_panel.cke_combopanel.lfr-maximized'
				);

				const stylesComboElementStyles =
					window.getComputedStyle(stylesComboElement);

				return stylesComboElementStyles.getPropertyValue('z-index');
			});

			expect(stylesComboZIndex).toEqual('10000');
		});

		await test.step('Assert context menu is visible', async () => {
			const ckeditorEditorBody = page
				.frameLocator('iframe[title="editor"]')
				.getByRole('heading', {name: 'Classic Editor'});

			await ckeditorEditorBody.click({button: 'right'});

			const contextMenuZIndex = await page.evaluate(() => {
				const stylesComboElement = document.querySelector(
					'.cke_panel.cke_menu_panel'
				);

				const contextMenuElementStyles =
					window.getComputedStyle(stylesComboElement);

				return contextMenuElementStyles.getPropertyValue('z-index');
			});

			expect(contextMenuZIndex).toEqual('10001');
		});
	}
);

test(
	'Able to drag and drop images with the right width',
	{tag: ['@LPD-41443', '@LPD-42473', '@LPD-53880']},
	async ({ckeditor4Page, page}) => {
		const editableFrame = ckeditor4Page.editableFrame;

		await test.step('Drag and drop image', async () => {
			const ckeditorEditorBody = editableFrame.getByRole('heading', {
				name: 'Classic Editor',
			});

			await ckeditorEditorBody.click();

			await page.keyboard.press('Enter');

			await page.getByLabel('Image', {exact: true}).click();

			await ckeditor4Page.selectImageWithItemSelector({
				cardTitle: 'astronaut.png',
			});

			const astronautEditorImage = page
				.getByRole('application', {name: 'Rich Text Editor'})
				.frameLocator('iframe[title="editor"]')
				.locator('img')
				.first();

			await astronautEditorImage.waitFor({state: 'visible'});
			await astronautEditorImage.hover();

			const dragAndDropButton = editableFrame.getByTitle(
				'Click and drag to move'
			);

			await dragAndDropButton.dragTo(ckeditorEditorBody);

			const astronautImageElement = editableFrame.locator(
				'h1 > * > img.cke_widget_element'
			);

			await expect(astronautImageElement).toBeVisible();
		});

		await test.step('Check image is not occupying the whole editor width', async () => {
			const imageElement = editableFrame.locator(
				'h1 > * > img.cke_widget_element'
			);

			const imageElementBoundingBox = await imageElement.boundingBox();
			const imageElementWidth = imageElementBoundingBox.width;

			const imageContainer = editableFrame.locator(
				'h1 > span.cke_widget_wrapper'
			);

			const imageContainerBoundingBox =
				await imageContainer.boundingBox();
			const imageContainerWidth = imageContainerBoundingBox.width;

			expect(imageElementWidth).toBe(imageContainerWidth);
		});
	}
);

test(
	'Change image from context menu, in editor without "adaptivemedia" plugin',
	{tag: ['@LPD-53880']},
	async ({ckeditor4Page}) => {
		await ckeditor4Page.insertHTML(
			'<img src="/documents/d/guest/moon-png" />'
		);

		await ckeditor4Page.editableFrame
			.locator('img[src="/documents/d/guest/moon-png"]')
			.dblclick();

		await ckeditor4Page.contextMenu.getByText('Browse Server').click();

		await ckeditor4Page.selectImageWithItemSelector({
			cardTitle: 'satellite.png',
		});

		await expect(ckeditor4Page.contextMenu.getByLabel('URL')).toHaveValue(
			/satellite-png/
		);

		await ckeditor4Page.contextMenu.getByText('OK').click();

		await expect(
			ckeditor4Page.editableFrame.locator('img[src*="satellite-png"]')
		).toBeVisible();
	}
);

test(
	'Editor voice label is human readable',
	{tag: ['@LPD-53923']},
	async ({page}) => {
		const ckeVoiceLabel = page.locator('span.cke_voice_label').first();

		await expect(ckeVoiceLabel).toHaveText('Rich Text Editor');
	}
);

test(
	'Check focus does not move when interacting with scrollbar',
	{tag: ['@LPD-53923']},
	async ({page}) => {
		const dragButton = page.locator(
			'.cke_resizer.cke_resizer_vertical.cke_resizer_ltr'
		);

		await dragButton.waitFor({state: 'visible'});
		await dragButton.scrollIntoViewIfNeeded();
		await dragButton.hover();

		const dragButtonRect = await dragButton.evaluate((element) => {
			return element.getBoundingClientRect();
		});

		await page.mouse.down();
		await page.mouse.move(dragButtonRect.x, dragButtonRect.y + 1000);
		await page.mouse.up();

		await dragButton.scrollIntoViewIfNeeded();

		const ckeditorElementRect = await page.evaluate(() => {
			return document
				.querySelector('iframe[title="editor"]')
				.getBoundingClientRect();
		});

		await page.mouse.click(
			ckeditorElementRect.right - 2,
			ckeditorElementRect.bottom - 2
		);

		const ckeditorElementRectChange = await page.evaluate(() => {
			return document
				.querySelector('iframe[title="editor"]')
				.getBoundingClientRect();
		});

		expect(ckeditorElementRect).toEqual(ckeditorElementRectChange);
	}
);
