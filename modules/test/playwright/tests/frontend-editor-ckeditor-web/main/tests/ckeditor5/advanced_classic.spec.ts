/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import getRandomString from '../../../../../utils/getRandomString';
import {advancedClassicPageTest} from '../../../../frontend-editor-ckeditor5-sample-web/fixtures/classicPageTest';

export const test = mergeTests(
	advancedClassicPageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPS-178052': {enabled: true},
	}),
	loginTest()
);

test(
	'Editor configuration is applied',
	{tag: '@LPD-11235'},
	async ({classicPage, page}) => {
		await test.step('Initial data is set', async () => {
			await expect(
				page.getByText('Lorem ipsum dolor sit amet')
			).toBeVisible();
		});

		await test.step('Placeholder is set', async () => {
			await expect(
				page.locator(
					'p[data-placeholder="This placeholder is set from EditorConfigContributor."]'
				)
			).toBeAttached();
		});

		await test.step('Toolbar contains advanced preset controls', async () => {
			const advancedPresetControls = [
				'Accessibility help',
				'Undo',
				'Redo',
				'Styles',
				'Normal',
				'Bold',
				'Italic',
				'Underline',
				'Strikethrough',
				'Font Color',
				'Font Background Color',
				'Remove Format',
				'Numbered List',
				'Bulleted List',
				'Increase indent',
				'Decrease indent',
				'Block quote',
				'Link',
				'Insert table',
				'Image',
				'Video',
				'Horizontal line',
				'Text alignment',
				'Source',
			];

			const controls =
				await classicPage.toolbar.buttonLabels.allInnerTexts();

			expect(controls).toEqual(advancedPresetControls);
		});

		await test.step('Toolbar buttons have Clay icons', async () => {
			await expect(
				classicPage.toolbar.container.locator('svg use[href*="/clay/"]')
			).toHaveCount(22);
		});
	}
);

test(
	'Link with relative URL does not trigger SPA navigation',
	{tag: '@LPD-60975'},
	async ({classicPage, page}) => {
		let requestWasMade = false;
		const sampleRelativeURL = '/sample-relative-url';

		page.on('request', (request) => {
			if (request.url().includes(sampleRelativeURL)) {
				requestWasMade = true;
			}
		});

		const link = classicPage.editable.locator(
			`a[href*="${sampleRelativeURL}"]`
		);

		await link.click();

		// Wait small amout of time. It is reasonable to expect that request
		// would have been made in this time after click.

		await page.waitForTimeout(200);

		expect(requestWasMade).toBe(false);
	}
);

test(
	'Select image from document library',
	{tag: '@LPD-11235'},
	async ({classicPage}) => {
		await classicPage.toolbar.container
			.getByRole('button', {name: 'Image'})
			.click();

		const itemSelectorFrame = classicPage.itemSelectorFrame;

		// Attached droppable area is an indicator that loading in iframe is done.

		const droppableArea = itemSelectorFrame.getByText(
			'Drag & Drop Your Images'
		);

		await expect(droppableArea).toBeAttached();

		await itemSelectorFrame
			.getByRole('link', {name: 'Sites and Libraries'})
			.click();

		await itemSelectorFrame.getByLabel('Liferay').click();

		await expect(droppableArea).toBeAttached();

		await itemSelectorFrame
			.getByRole('link', {name: 'Provided by Liferay'})
			.click();

		await itemSelectorFrame
			.locator('.image-card[data-title="moon.png"]')
			.click();

		await expect(
			classicPage.editable.locator('img[src*="moon-png"]')
		).toBeVisible();
	}
);

test(
	'Can add a hyperlink to existing text',
	{tag: '@LPS-110663'},
	async ({classicPage, page}) => {
		await test.step('Type some text in the editor', async () => {
			await classicPage.editable.fill(getRandomString());
		});

		await test.step('Select text and add hyperlink', async () => {
			await classicPage.editable.selectText();

			await classicPage.toolbar.container
				.getByRole('button', {name: 'Link'})
				.click();

			const urlInput = page.getByLabel('Link URL');

			await expect(urlInput).toBeVisible({timeout: 3000});

			await urlInput.fill('https://www.liferay.com');

			await page.getByLabel('Insert', {exact: true}).click();
		});

		await expect(
			classicPage.editable.locator('a[href*="https://www.liferay.com"]')
		).toBeVisible();
	}
);

test(
	'Select image by modal URL input and check image link when aligned',
	{tag: ['@LPD-11235', '@LPD-94512']},
	async ({classicPage, page}) => {
		const linkURL = 'https://www.liferay.com';

		await test.step('Insert an image by modal URL input', async () => {
			await classicPage.toolbar.container
				.getByRole('button', {name: 'Image'})
				.click();

			const itemSelectorFrame = classicPage.itemSelectorFrame;

			itemSelectorFrame.getByRole('link', {name: 'URL'}).click();

			const imageURLInput = itemSelectorFrame.getByLabel('URL', {
				exact: true,
			});

			await expect(imageURLInput).toBeEnabled();

			const addButton = itemSelectorFrame.getByRole('button', {
				exact: true,
				name: 'Add',
			});

			await expect(addButton).toBeDisabled();

			await imageURLInput.fill('/documents/d/guest/tree-png');

			await expect(addButton).toBeEnabled();

			await addButton.click();

			await expect(
				classicPage.editable.locator('img[src*="tree-png"]')
			).toBeVisible();
		});

		await test.step('Center align the image', async () => {
			await classicPage.editable.locator('img[src*="tree-png"]').click();

			const centerButton = page.getByRole('button', {
				name: 'Centered image',
			});

			await expect(centerButton).toBeVisible();

			await centerButton.click();

			await expect(centerButton).toHaveAttribute('aria-pressed', 'true');
		});

		await test.step('Add a hyperlink to the centered image', async () => {
			await classicPage.toolbar.container
				.getByRole('button', {name: 'Link'})
				.click();

			const urlInput = page.getByLabel('Link URL');

			await expect(urlInput).toBeVisible({timeout: 3000});

			await urlInput.fill(linkURL);

			await page.getByLabel('Insert', {exact: true}).click();
		});

		await expect(
			classicPage.editable.locator(
				`figure.image a[href*="${linkURL}"] img[src*="tree-png"]`
			)
		).toBeVisible();
	}
);

test(
	'Select video by modal URL input',
	{tag: '@LPD-11235'},
	async ({classicPage}) => {
		await classicPage.toolbar.container
			.getByRole('button', {name: 'Video'})
			.click();

		const itemSelectorFrame = classicPage.itemSelectorFrame;

		const videoURLInput = itemSelectorFrame.getByLabel('Video URL');

		await expect(videoURLInput).toBeEnabled();

		const addButton = itemSelectorFrame.getByRole('button', {
			exact: true,
			name: 'Add',
		});

		await expect(addButton).toBeDisabled();

		await videoURLInput.fill('https://www.youtube.com/watch?v=2EPZxIC5ogU');

		await expect(addButton).toBeEnabled();

		await addButton.click();

		await expect(
			classicPage.editable
				.frameLocator('iframe')
				.getByLabel('YouTube Video Player')
		).toBeVisible();
	}
);

test(
	'Opening source editing disables all custom controls',
	{tag: '@LPD-11235'},
	async ({classicPage}) => {
		const imageButton = classicPage.toolbar.container.getByRole('button', {
			name: 'Image',
		});
		const sourceButton = classicPage.toolbar.container.getByRole('button', {
			name: 'Source',
		});
		const videoButton = classicPage.toolbar.container.getByRole('button', {
			name: 'Video',
		});

		await sourceButton.click();

		await expect(imageButton).toBeDisabled();
		await expect(videoButton).toBeDisabled();

		await sourceButton.click();

		await expect(imageButton).toBeEnabled();
		await expect(videoButton).toBeEnabled();
	}
);

test(
	'Can view HTML in source mode formatted in wysiwyg view',
	{tag: '@LRQA-67229'},
	async ({classicPage}) => {
		const sourceButton = classicPage.toolbar.container.getByRole('button', {
			name: 'Source',
		});

		await sourceButton.click();

		await classicPage.sourceEditable.fill(
			'<h2>Heading Two</h2><p>Paragraph with <i>italic</i> text.</p>'
		);

		await sourceButton.click();

		await expect(classicPage.editable.locator('h2')).toContainText(
			'Heading Two'
		);

		await expect(classicPage.editable.locator('i')).toContainText('italic');
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

		await classicPage.editable.focus();
		await classicPage.editable.press('ControlOrMeta+a');
		await classicPage.editable.press('ControlOrMeta+v');

		await expect(
			classicPage.editable.getByRole('img', {
				name: 'A beautiful redheaded man',
			})
		).toBeVisible();

		await expect(
			classicPage.editable.getByRole('figure', {
				name: 'Top banana importers 1998 (',
			})
		).toBeVisible();
	}
);

test(
	'Form input value correctly syncs with the editor value',
	{tag: '@LPD-71706'},
	async ({classicPage, page}) => {
		const hiddenInput = page.locator(
			'input[name*="advancedClassicEditor"]'
		);

		await test.step('Check that the initial value is set', async () => {
			await expect(hiddenInput).toHaveValue(
				/Lorem ipsum dolor sit amet, consectetur adipiscing elit/
			);
		});

		await test.step('Check that after making changes, the value is updated', async () => {
			await classicPage.editable.focus();
			await classicPage.editable.pressSequentially('New content');

			await expect(hiddenInput).toHaveValue(/New content/);
		});

		await test.step('Check that after clearing the editor, the value is updated', async () => {
			await classicPage.editable.focus();
			await classicPage.editable.press('ControlOrMeta+a');
			await classicPage.editable.press('Backspace');

			await expect(hiddenInput).toHaveValue('');
		});
	}
);

test(
	'Find and Replace button is shown in the toolbar',
	{tag: '@LPD-95091'},
	async ({classicPage}) => {
		await expect(
			classicPage.toolbar.container.getByRole('button', {
				exact: true,
				name: 'Find and replace',
			})
		).toBeVisible();
	}
);
