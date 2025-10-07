/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {waitForEditor} from '../../../../../utils/waitFor';
import {ckeditorSamplePageTest} from '../../fixtures/ckeditorSamplePageTest';
import {classicPageTest} from './fixtures/classicPageTest';

export const test = mergeTests(
	ckeditorSamplePageTest,
	classicPageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

test.beforeEach(async ({ckeditorSamplePage, page, site}) => {
	await ckeditorSamplePage.createAndGotoSitePage({site});

	await ckeditorSamplePage.selectTab('CKEditor 5');
	await ckeditorSamplePage.selectTab('Advanced Classic');

	await waitForEditor({page});
});

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
				'AI Creator',
				'Source',
			];

			const controls =
				await classicPage.toolbar.buttonLabels.allInnerTexts();

			expect(controls).toEqual(advancedPresetControls);
		});

		await test.step('Toolbar buttons have Clay icons', async () => {
			await expect(
				classicPage.toolbar.container.locator('svg use[href*="/clay/"]')
			).toHaveCount(23);
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

test('Open AI Creator popover', async ({classicPage, page}) => {
	const AICreatorButton = classicPage.toolbar.container.getByRole('button', {
		name: 'Create AI Content',
	});

	await AICreatorButton.click();

	await expect(page.getByText('Configure OpenAI')).toBeVisible();
});

test(
	'Opening source editing disables all custom controls',
	{tag: '@LPD-11235'},
	async ({classicPage}) => {
		const AICreatorButton = classicPage.toolbar.container.getByRole(
			'button',
			{
				name: 'Create AI Content',
			}
		);

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

		await expect(AICreatorButton).toBeDisabled();
		await expect(imageButton).toBeDisabled();
		await expect(videoButton).toBeDisabled();

		await sourceButton.click();

		await expect(AICreatorButton).toBeEnabled();
		await expect(imageButton).toBeEnabled();
		await expect(videoButton).toBeEnabled();
	}
);
