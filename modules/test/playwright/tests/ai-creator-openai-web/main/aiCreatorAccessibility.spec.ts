/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {aiCreatorPagesTest} from './fixtures/aiCreatorPagesTest';

const AI_CREATOR_MODAL = 'iframe[title="AI Creator"]';

const test = mergeTests(
	aiCreatorPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest()
);

test(
	'The AI Creator modal complies with WCAG and traps the focus',
	{tag: '@LPS-179485'},
	async ({
		aiCreatorInstanceSettingsPage,
		enableMockAICreatorOpenAIClient,
		journalPage,
		page,
		site,
	}) => {
		test.slow();

		await enableMockAICreatorOpenAIClient();

		try {
			await aiCreatorInstanceSettingsPage.addApiKey();

			// Open the AI Creator modal in the web content editor

			await journalPage.goto(site.friendlyUrlPath);
			await journalPage.goToCreateArticle('Basic Web Content');

			await page.getByRole('button', {name: 'Create AI Content'}).click();

			const modal = page.frameLocator(AI_CREATOR_MODAL);

			await expect(modal.getByLabel('Description')).toBeVisible();

			// The open modal has no accessibility violations

			await checkAccessibility({page, selectors: [AI_CREATOR_MODAL]});

			// The focus moves through the modal fields in order

			await modal.getByLabel('Description').focus();
			await expect(modal.getByLabel('Description')).toBeFocused();

			await page.keyboard.press('Tab');
			await expect(modal.getByLabel('Tone')).toBeFocused();

			await page.keyboard.press('Tab');
			await expect(modal.getByLabel('Word Count')).toBeFocused();

			await page.keyboard.press('Tab');
			await expect(
				modal.getByRole('link', {
					name: 'Learn more about OpenAI integration.',
				})
			).toBeFocused();

			await page.keyboard.press('Tab');
			await expect(
				modal.getByRole('button', {name: 'Cancel'})
			).toBeFocused();

			await page.keyboard.press('Tab');
			await expect(
				modal.getByRole('button', {name: 'Create'})
			).toBeFocused();

			// The focus returns to the modal after generating content

			await modal.getByLabel('Description').fill('USER_CONTENT');
			await modal.getByRole('button', {name: 'Create'}).click();

			await expect(modal.getByLabel('Content')).toBeVisible();

			// The result state has no accessibility violations

			await checkAccessibility({page, selectors: [AI_CREATOR_MODAL]});

			// The focus returns to the modal after clicking Try Again

			await modal.getByRole('button', {name: 'Try Again'}).click();

			await expect(modal.getByLabel('Description')).toBeVisible();

			// Close the modal so it does not block later navigation

			await page
				.getByRole('dialog', {name: 'AI Creator'})
				.getByRole('button', {name: 'Close'})
				.click();
		}
		finally {
			await aiCreatorInstanceSettingsPage.removeApiKey();
		}
	}
);

test(
	'The screen reader announces the popover message when OpenAI is not configured',
	{tag: ['@LPS-181285', '@LPS-188490', '@LPS-196540']},
	async ({journalPage, page, site}) => {

		// Open the web content editor without configuring OpenAI

		await journalPage.goto(site.friendlyUrlPath);
		await journalPage.goToCreateArticle('Basic Web Content');

		const aiCreatorButton = page.getByRole('button', {
			name: 'Create AI Content',
		});

		// The button exposes its Create AI Content accessible name

		await expect(aiCreatorButton).toBeVisible();

		// The button renders the AI Creator icon

		await expect(
			aiCreatorButton.locator('svg.ck-button__icon')
		).toBeVisible();

		// Clicking the button announces the configuration popover

		await aiCreatorButton.click();

		const popover = page.getByRole('alert');

		await expect(popover).toBeVisible();
		await expect(popover).toBeFocused();
		await expect(popover.getByText('Configure OpenAI')).toBeVisible();
		await expect(
			popover.getByText(
				'Authentication is needed to use this feature. Contact your administrator to add an API key in instance or site settings.'
			)
		).toBeVisible();
	}
);
