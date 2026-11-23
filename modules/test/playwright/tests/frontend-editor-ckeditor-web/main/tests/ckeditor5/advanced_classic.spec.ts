/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {ApiHelpers} from '../../../../../helpers/ApiHelpers';
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
				'Find and replace',
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
		const text = getRandomString();

		await test.step('Type some text in the editor', async () => {
			await classicPage.editable.fill(text);
		});

		await test.step('Select text and add hyperlink', async () => {
			await classicPage.editable.selectText();

			await classicPage.toolbar.container
				.getByRole('button', {name: 'Link'})
				.click();

			const linkDialog = page.getByRole('dialog', {name: 'Link'});

			await expect(linkDialog.getByLabel('Display Text')).toHaveValue(
				text
			);

			await linkDialog
				.getByLabel('Link URL')
				.fill('https://www.liferay.com');

			await linkDialog.getByRole('button', {name: 'Done'}).click();
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

			const linkDialog = page.getByRole('dialog', {name: 'Link'});

			await expect(linkDialog.getByLabel('Display Text')).toHaveCount(0);

			await linkDialog.getByLabel('Link URL').fill(linkURL);

			await linkDialog.getByRole('button', {name: 'Done'}).click();
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

		await expect(classicPage.editable.locator('iframe')).toHaveAttribute(
			'src',
			/youtube\.com\/embed\/2EPZxIC5ogU/
		);
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

test(
	'Style Book text colors are available in the Styles dropdown',
	{tag: '@LPD-11235'},
	async ({classicPage, page}) => {
		await classicPage.toolbar.container
			.getByRole('button', {name: 'Styles'})
			.click();

		const textColors = [
			'Primary',
			'Secondary',
			'Success',
			'Danger',
			'Warning',
			'Info',
			'Dark',
			'Light',
		];

		for (const color of textColors) {
			await expect(
				page.getByRole('option', {exact: true, name: color})
			).toBeVisible();
		}
	}
);

test(
	'Can browse a page from the link dialog',
	{tag: '@LPD-91686'},
	async ({classicPage, page, site}) => {
		const layoutTitle = getRandomString();

		const layout = await new ApiHelpers(
			page
		).headlessDelivery.createSitePage({
			siteId: site.id,
			title: layoutTitle,
		});

		await classicPage.editable.fill(getRandomString());

		await classicPage.editable.selectText();

		await classicPage.toolbar.container
			.getByRole('button', {name: 'Link'})
			.click();

		const linkDialog = page.getByRole('dialog', {name: 'Link'});

		await linkDialog.getByRole('button', {name: 'Browse Server'}).click();

		const itemSelectorFrame = page.frameLocator(
			'iframe[title="Select Web Content Asset"]'
		);

		await itemSelectorFrame.getByRole('link', {name: 'Pages'}).click();

		await itemSelectorFrame
			.getByRole('treeitem', {name: layoutTitle})
			.click();

		const urlInput = linkDialog.getByLabel('Link URL');

		await expect(urlInput).toHaveValue(
			new RegExp(`^/.*${layout.friendlyUrlPath}$`)
		);

		const url = await urlInput.inputValue();

		await linkDialog.getByRole('button', {name: 'Done'}).click();

		await expect(
			classicPage.editable.locator(`a[href="${url}"]`)
		).toBeVisible();
	}
);

test(
	'Link dialog requires a display text and a link URL',
	{tag: '@LPD-91686'},
	async ({classicPage, page}) => {
		await classicPage.editable.fill(getRandomString());

		await classicPage.editable.selectText();

		await classicPage.toolbar.container
			.getByRole('button', {name: 'Link'})
			.click();

		const linkDialog = page.getByRole('dialog', {name: 'Link'});

		await linkDialog.getByLabel('Display Text').fill('');

		await linkDialog.getByRole('button', {name: 'Done'}).click();

		await expect(
			linkDialog.getByText('This field is required.')
		).toHaveCount(2);

		await linkDialog.getByRole('button', {name: 'Cancel'}).click();

		await expect(linkDialog).toBeHidden();

		await expect(classicPage.editable.locator('a')).toHaveCount(0);
	}
);

test(
	'Edit link opens the link dialog with the current URL',
	{tag: '@LPD-91686'},
	async ({classicPage, page}) => {
		const sampleRelativeURL = '/sample-relative-url';
		const updatedURL = '/updated-relative-url';

		await classicPage.editable
			.locator(`a[href*="${sampleRelativeURL}"]`)
			.click();

		await expect(page.getByRole('button', {name: 'Unlink'})).toBeVisible();

		await expect(
			page.getByRole('button', {name: 'Link properties'})
		).toBeHidden();

		await page.getByRole('button', {name: 'Edit link'}).click();

		const linkDialog = page.getByRole('dialog', {name: 'Link'});

		const urlInput = linkDialog.getByLabel('Link URL');

		await expect(urlInput).toHaveValue(sampleRelativeURL);

		await urlInput.fill(updatedURL);

		await linkDialog.getByRole('button', {name: 'Done'}).click();

		await expect(
			classicPage.editable.locator(`a[href="${updatedURL}"]`)
		).toBeVisible();
	}
);

test(
	'Can browse a document from the link dialog',
	{tag: '@LPD-91686'},
	async ({classicPage, page, site}) => {
		const documentTitle = `${getRandomString()}.txt`;

		await new ApiHelpers(page).headlessDelivery.postDocument(
			site.id,
			{
				buffer: Buffer.from(getRandomString()),
				mimeType: 'text/plain',
				name: documentTitle,
			},
			{fileName: documentTitle, title: documentTitle}
		);

		await classicPage.editable.fill(getRandomString());

		await classicPage.editable.selectText();

		await classicPage.toolbar.container
			.getByRole('button', {name: 'Link'})
			.click();

		const linkDialog = page.getByRole('dialog', {name: 'Link'});

		await linkDialog.getByRole('button', {name: 'Browse Server'}).click();

		const documentCard = page
			.frameLocator('iframe[title="Select Web Content Asset"]')
			.locator(`[data-title="${documentTitle}"]`);

		const urlInput = linkDialog.getByLabel('Link URL');

		await expect(async () => {
			if (await documentCard.isVisible()) {
				await documentCard.click({timeout: 1000});
			}

			await expect(urlInput).toHaveValue(/^\/documents\//, {
				timeout: 1000,
			});
		}).toPass();

		const url = await urlInput.inputValue();

		await linkDialog.getByRole('button', {name: 'Done'}).click();

		await expect(
			classicPage.editable.locator(`a[href="${url}"]`)
		).toBeVisible();
	}
);

test(
	'Ctrl+K opens the link dialog and Enter applies the link',
	{tag: '@LPD-91686'},
	async ({classicPage, page}) => {
		const url = '/keyboard-link';

		await classicPage.editable.fill(getRandomString());

		await classicPage.editable.selectText();

		await page.keyboard.press('Control+k');

		const linkDialog = page.getByRole('dialog', {name: 'Link'});

		await linkDialog.getByLabel('Link URL').fill(url);

		await page.keyboard.press('Enter');

		await expect(linkDialog).toBeHidden();

		await expect(
			classicPage.editable.locator(`a[href="${url}"]`)
		).toBeVisible();
	}
);

test(
	'Changing the display text replaces the linked text',
	{tag: '@LPD-91686'},
	async ({classicPage, page}) => {
		const displayText = getRandomString();
		const url = '/display-text-link';

		await classicPage.editable.fill(getRandomString());

		await classicPage.editable.selectText();

		await classicPage.toolbar.container
			.getByRole('button', {name: 'Link'})
			.click();

		const linkDialog = page.getByRole('dialog', {name: 'Link'});

		await linkDialog.getByLabel('Display Text').fill(displayText);
		await linkDialog.getByLabel('Link URL').fill(url);

		await linkDialog.getByRole('button', {name: 'Done'}).click();

		await expect(
			classicPage.editable.locator(`a[href="${url}"]`)
		).toHaveText(displayText);
	}
);

test(
	'Link dialog inserts a new link where the caret is',
	{tag: '@LPD-91686'},
	async ({classicPage, page}) => {
		const displayText = getRandomString();
		const url = '/caret-link';

		await classicPage.editable.fill(getRandomString());

		await page.keyboard.press('End');

		await classicPage.toolbar.container
			.getByRole('button', {name: 'Link'})
			.click();

		const linkDialog = page.getByRole('dialog', {name: 'Link'});

		const displayTextInput = linkDialog.getByLabel('Display Text');

		await expect(displayTextInput).toHaveValue('');

		await displayTextInput.fill(displayText);
		await linkDialog.getByLabel('Link URL').fill(url);

		await linkDialog.getByRole('button', {name: 'Done'}).click();

		await expect(
			classicPage.editable.locator(`a[href="${url}"]`)
		).toHaveText(displayText);
	}
);

test(
	'Escape closes the link dialog without applying a link',
	{tag: '@LPD-91686'},
	async ({classicPage, page}) => {
		await classicPage.editable.fill(getRandomString());

		await classicPage.editable.selectText();

		await classicPage.toolbar.container
			.getByRole('button', {name: 'Link'})
			.click();

		const linkDialog = page.getByRole('dialog', {name: 'Link'});

		await linkDialog.getByLabel('Link URL').fill('/escaped-link');

		await page.keyboard.press('Escape');

		await expect(linkDialog).toBeHidden();

		await expect(classicPage.editable.locator('a')).toHaveCount(0);
	}
);

test(
	'Unlink removes an existing link',
	{tag: '@LPD-91686'},
	async ({classicPage, page}) => {
		const link = classicPage.editable.locator(
			'a[href*="/sample-relative-url"]'
		);

		await link.click();

		await page.getByRole('button', {name: 'Unlink'}).click();

		await expect(link).toHaveCount(0);
	}
);

test(
	'Link dialog sets the link target from the open in a new tab checkbox',
	{tag: '@LPD-91686'},
	async ({classicPage, page}) => {
		const url = '/target-link';

		await classicPage.editable.fill(getRandomString());

		await classicPage.editable.selectText();

		// Create a link that opens in the same tab

		await classicPage.toolbar.container
			.getByRole('button', {name: 'Link'})
			.click();

		const linkDialog = page.getByRole('dialog', {name: 'Link'});

		const openInNewTabCheckbox = linkDialog.getByLabel('Open in a New Tab');

		await expect(openInNewTabCheckbox).not.toBeChecked();

		await linkDialog.getByLabel('Link URL').fill(url);

		await linkDialog.getByRole('button', {name: 'Done'}).click();

		const link = classicPage.editable.locator(`a[href="${url}"]`);

		await expect(link).toHaveAttribute('target', '_self');

		// Make the link open in a new tab

		await link.click();

		await page.getByRole('button', {name: 'Edit link'}).click();

		await openInNewTabCheckbox.check();

		await linkDialog.getByRole('button', {name: 'Done'}).click();

		await expect(link).toHaveAttribute('target', '_blank');

		// Make the link open in the same tab again

		await link.click();

		await page.getByRole('button', {name: 'Edit link'}).click();

		await expect(openInNewTabCheckbox).toBeChecked();

		await openInNewTabCheckbox.uncheck();

		await linkDialog.getByRole('button', {name: 'Done'}).click();

		await expect(link).toHaveAttribute('target', '_self');
	}
);
