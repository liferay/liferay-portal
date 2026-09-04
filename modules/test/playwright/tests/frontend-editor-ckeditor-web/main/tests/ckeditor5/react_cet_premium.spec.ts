/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {reactPlusCETPremiumClassicPageTest} from '../../../../frontend-editor-ckeditor5-sample-web/fixtures/classicPageTest';

export const test = mergeTests(
	reactPlusCETPremiumClassicPageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPS-178052': {enabled: true},
	}),
	loginTest()
);

// The premium plugins need a licensed installation, which CI does not have
// yet. Skipping their registration keeps them out of Testray altogether,
// rather than leaving a blocked entry behind the way test.skip does.

if (!process.env.CI) {
	test(
		'Enhanced Paste from Office is loaded alongside the standard plugin',
		{tag: ['@LPD-101122', '@LPD-95090']},
		async ({classicPage, page}) => {
			await expect(classicPage.editable).toBeVisible();

			const loadedPlugins = await page.evaluate(() => {
				const editorElement = Array.from(
					document.querySelectorAll('.lfr-ck *')
				).find((element) => (element as any).ckeditorInstance);

				const editor = (editorElement as any)?.ckeditorInstance;

				return {
					pasteFromOffice:
						editor?.plugins.has('PasteFromOffice') ?? false,
					pasteFromOfficeEnhanced:
						editor?.plugins.has('PasteFromOfficeEnhanced') ?? false,
				};
			});

			expect(loadedPlugins).toEqual({
				pasteFromOffice: true,
				pasteFromOfficeEnhanced: true,
			});
		}
	);

	test(
		'Premium plugins are entitled by the license key set for this editor',
		{tag: '@LPD-101122'},
		async ({classicPage, page}) => {
			const licenseErrors: string[] = [];

			const collectLicenseError = (message: string) => {
				if (message.includes('license-key')) {
					licenseErrors.push(message);
				}
			};

			page.on('console', (message) =>
				collectLicenseError(message.text())
			);
			page.on('pageerror', (error) => collectLicenseError(error.message));

			await page.reload();

			await expect(classicPage.editable).toBeVisible();

			// Each premium plugin verifies its own entitlement on a one second
			// interval, so a plugin the license does not cover only reports back
			// once the editor is already running. An unlicensed plugin throws a
			// "license-key-*" error and leaves the editor in read-only mode.

			await page.waitForTimeout(3000);

			expect(licenseErrors).toEqual([]);

			await expect(classicPage.editable).toHaveAttribute(
				'contenteditable',
				'true'
			);
		}
	);

	test(
		'Enhanced source editing opens the source view in a modal',
		{tag: ['@LPD-101122', '@LPD-83978']},
		async ({classicPage, page}) => {
			await classicPage.toolbar.container
				.getByRole('button', {exact: true, name: 'Source'})
				.click();

			await expect(
				page.getByRole('dialog', {name: 'Edit source'})
			).toBeVisible();

			await expect(page.locator('.cm-editor')).toBeVisible();
		}
	);

	test(
		'Content edited in the enhanced source modal is applied to the editor',
		{tag: '@LPD-101122'},
		async ({classicPage}) => {
			await classicPage.toolbar.container
				.getByRole('button', {exact: true, name: 'Source'})
				.click();

			await classicPage.sourceEditingEnhancedDialog.editable.fill(
				'<h2>Heading Two</h2><p>Paragraph with <i>italic</i> text.</p>'
			);

			await classicPage.sourceEditingEnhancedDialog.saveButton.click();

			await expect(classicPage.editable.locator('h2')).toContainText(
				'Heading Two'
			);

			await expect(classicPage.editable.locator('i')).toContainText(
				'italic'
			);
		}
	);
}
