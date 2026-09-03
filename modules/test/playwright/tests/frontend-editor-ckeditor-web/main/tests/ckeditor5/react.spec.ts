/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {waitForFDS} from '../../../../../utils/waitFor';
import {reactClassicPageTest} from '../../../../frontend-editor-ckeditor5-sample-web/fixtures/classicPageTest';

export const test = mergeTests(
	reactClassicPageTest,
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
				classicPage.editable.getByText('Lorem ipsum dolor sit amet')
			).toBeVisible();
		});

		await test.step('Toolbar contains custom toobar configuration, including added custom and official plugins', async () => {
			const expectedButtons = [
				'Undo',
				'Redo',
				'Bold',
				'Italic',
				'Link',
				'Bookmark',
				'Image',
				'Video',
				'Styles',
				'Timestamp',
			];

			const availableButtons =
				await classicPage.toolbar.buttonLabels.allInnerTexts();

			for (const button of expectedButtons) {
				expect(availableButtons).toContain(button);
			}
		});

		await test.step('Toolbar does not contain removed plugin', async () => {
			await expect(
				classicPage.toolbar.buttonLabels.getByLabel('Underline')
			).toBeHidden();
		});

		await test.step('Item selector controls open item selector modal', async () => {
			const imageButton = classicPage.toolbar.container.getByRole(
				'button',
				{
					name: 'Image',
				}
			);

			await imageButton.click();

			await expect(page.getByText('Select Image')).toBeVisible();

			await waitForFDS({empty: true, page});

			const modal = page.locator('.modal');

			const cancelButton = modal.getByRole('button', {
				name: 'Cancel',
			});

			await cancelButton.click();

			await expect(page.getByText('Select Image')).not.toBeAttached();

			const videoButton = classicPage.toolbar.container.getByRole(
				'button',
				{
					name: 'Video',
				}
			);

			await videoButton.click();

			await expect(page.getByText('Select Video')).toBeVisible();

			await cancelButton.click();

			await expect(page.getByText('Select Video')).not.toBeAttached();
		});
	}
);

test(
	'Editor can be disabled/enabled',
	{tag: '@LPD-80293'},
	async ({classicPage, page}) => {
		const editorContainer = page.locator('.lfr-ck');
		const externalDisableButton = page.getByRole('button', {
			name: 'Toggle disabled prop',
		});
		const imageToolbarButton = classicPage.toolbar.container.getByRole(
			'button',
			{
				name: 'Image',
			}
		);
		const internalDisableButton = page.getByRole('button', {
			name: 'Toggle internal read-only mode',
		});

		async function assertEditorDisabled() {
			await expect(imageToolbarButton).not.toBeEnabled();

			await expect(editorContainer).toHaveClass(/lfr-ck-disabled/);
		}

		async function assertEditorEnabled() {
			await expect(imageToolbarButton).toBeEnabled();

			await expect(editorContainer).not.toHaveClass(/lfr-ck-disabled/);
		}

		await test.step('Initially, editor is enabled', async () => {
			await assertEditorEnabled();
		});

		await test.step('Disable editor through prop', async () => {
			await externalDisableButton.click();

			await assertEditorDisabled();

			await externalDisableButton.click();

			await assertEditorEnabled();
		});

		await test.step('Disable editor by changing internal read-only mode', async () => {
			await internalDisableButton.click();

			await assertEditorDisabled();

			await internalDisableButton.click();

			await assertEditorEnabled();
		});
	}
);
