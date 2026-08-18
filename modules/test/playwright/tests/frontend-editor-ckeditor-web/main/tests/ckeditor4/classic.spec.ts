/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {classicPageTest} from '../../../../frontend-editor-ckeditor4-sample-web/fixtures/classicPageTest';

export const test = mergeTests(
	classicPageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	loginTest()
);

test(
	'Dropdown and context menus are visible when maximized',
	{tag: ['@LPD-33712', '@LPD-38600']},
	async ({classicPage: _classicPage, page}) => {
		await test.step('Select Maximized toolbar control', async () => {
			await page.getByRole('button', {name: 'Maximize'}).click();
		});

		await test.step('Assert "Styles" dropdown is visible', async () => {
			await page.getByRole('button', {name: 'Styles'}).click();

			const stylesComboPanel = page.locator(
				'.cke_panel.cke_combopanel.lfr-maximized'
			);

			await stylesComboPanel.waitFor({state: 'attached'});

			const stylesComboZIndex = await stylesComboPanel.evaluate(
				(element) =>
					window.getComputedStyle(element).getPropertyValue('z-index')
			);

			expect(stylesComboZIndex).toEqual('10000');
		});

		await test.step('Assert context menu is visible', async () => {
			const ckeditorEditorBody = page
				.frameLocator('iframe[title="editor"]')
				.getByRole('heading', {name: 'Classic Editor'});

			await ckeditorEditorBody.click({button: 'right'});

			const contextMenuPanel = page.locator('.cke_panel.cke_menu_panel');

			await contextMenuPanel.waitFor({state: 'attached'});

			const contextMenuZIndex = await contextMenuPanel.evaluate(
				(element) =>
					window.getComputedStyle(element).getPropertyValue('z-index')
			);

			expect(contextMenuZIndex).toEqual('10001');
		});
	}
);

test(
	'Able to drag and drop images with the right width',
	{tag: ['@LPD-41443', '@LPD-42473', '@LPD-53880']},
	async ({classicPage, page}) => {
		const editableFrame = classicPage.editableFrame;

		await test.step('Drag and drop image', async () => {
			const ckeditorEditorBody = editableFrame.getByRole('heading', {
				name: 'Classic Editor',
			});

			await ckeditorEditorBody.click();

			await page.keyboard.press('Enter');

			await page.getByLabel('Image', {exact: true}).click();

			await classicPage.selectImageWithItemSelector({
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
	async ({classicPage}) => {
		await classicPage.insertHTML(
			'<img src="/documents/d/guest/moon-png" />'
		);

		await classicPage.editableFrame
			.locator('img[src="/documents/d/guest/moon-png"]')
			.dblclick();

		await classicPage.contextMenu.getByText('Browse Server').click();

		await classicPage.selectImageWithItemSelector({
			cardTitle: 'satellite.png',
		});

		await expect(classicPage.contextMenu.getByLabel('URL')).toHaveValue(
			/satellite-png/
		);

		await classicPage.contextMenu.getByText('OK').click();

		await expect(
			classicPage.editableFrame.locator('img[src*="satellite-png"]')
		).toBeVisible();
	}
);

test(
	'Editable is named after the field it edits',
	{tag: ['@LPD-34688']},
	async ({classicPage}) => {
		await expect(
			classicPage.editableFrame.getByRole('textbox', {name: 'Content'})
		).toBeVisible();
	}
);

test(
	'Editor voice label is human readable',
	{tag: ['@LPD-53923']},
	async ({classicPage: _classicPage, page}) => {
		const ckeVoiceLabel = page.locator('span.cke_voice_label').first();

		await expect(ckeVoiceLabel).toHaveText('Rich Text Editor');
	}
);

test(
	'Check focus does not move when interacting with scrollbar',
	{tag: ['@LPD-53923']},
	async ({classicPage: _classicPage, page}) => {
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

test(
	'Pasting HTML content works',
	{tag: '@LPD-65963'},
	async ({classicPage, context}) => {
		const newPage = await context.newPage();

		await newPage.goto(
			'http://www.standards-schmandards.com/exhibits/wysiwyg/sampledoc.htm'
		);

		await newPage.locator('body').focus();
		await newPage.locator('html').press('ControlOrMeta+a');
		await newPage.locator('html').press('ControlOrMeta+c');

		const body = classicPage.editableFrame.locator('body');

		await body.focus();
		await body.press('ControlOrMeta+a');
		await body.press('ControlOrMeta+v');

		await expect(
			body.getByRole('img', {name: 'A beautiful redheaded man'})
		).toBeVisible();

		await expect(
			body.locator(
				'table[summary="Sweden was the top importing country by far in 1998."]'
			)
		).toBeVisible();
	}
);

test(
	'Verify source content can be previewed',
	{tag: '@LRQA-67229'},
	async ({classicPage, context}) => {
		const newPage = await context.newPage();

		await newPage.goto(
			'http://www.standards-schmandards.com/exhibits/wysiwyg/sampledoc.htm'
		);

		await newPage.locator('body').focus();
		await newPage.locator('html').press('ControlOrMeta+a');
		await newPage.locator('html').press('ControlOrMeta+c');

		const body = classicPage.editableFrame.locator('body');

		await body.focus();
		await body.press('ControlOrMeta+a');
		await body.press('ControlOrMeta+v');

		await classicPage.toolbarButton('Source').click();
		await classicPage.toolbarButton('Preview').click();

		await expect(
			classicPage.previewFrame.getByRole('heading', {
				name: 'Sample document for editor',
			})
		).toBeVisible();
	}
);

test(
	'Check source mode content displays correctly',
	{tag: '@LPD-79554'},
	async ({classicPage: _classicPage, page}) => {
		const sourceButton = page.getByLabel('Source');

		await sourceButton.click();

		await page.locator('div:nth-child(2) > .CodeMirror-line').click();

		await page.keyboard.press('$');

		await page.keyboard.press('Backspace');

		await sourceButton.click();

		await expect(page.getByText('$')).not.toBeVisible();
	}
);
