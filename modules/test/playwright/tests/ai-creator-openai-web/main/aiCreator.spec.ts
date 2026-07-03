/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {aiCreatorPagesTest} from './fixtures/aiCreatorPagesTest';

const LEARN_MORE_HREF =
	'https://learn.liferay.com/w/dxp/content-authoring-and-management/web-content/web-content-articles/generating-text-content-using-ai';

const test = mergeTests(
	aiCreatorPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest()
);

test(
	'Access the OpenAI configuration page from the Instance Settings and the Site Settings',
	{tag: '@LPS-179484'},
	async ({aiCreatorInstanceSettingsPage, page, site, siteSettingsPage}) => {

		// The OpenAI settings are reachable from the Instance Settings

		await aiCreatorInstanceSettingsPage.goto();

		await expect(
			page.getByLabel('Enable ChatGPT to Create Content')
		).toBeChecked();
		await expect(aiCreatorInstanceSettingsPage.apiKeyInput).toHaveValue('');

		// The OpenAI settings are reachable from the Site Settings

		await siteSettingsPage.goToSiteSetting(
			'AI Creator',
			'OpenAI',
			site.friendlyUrlPath
		);

		await expect(
			page.getByLabel('Enable ChatGPT to Create Content')
		).toBeChecked();
		await expect(aiCreatorInstanceSettingsPage.apiKeyInput).toHaveValue('');

		// The How do I get an API key link points to the OpenAI docs

		await expect(
			page.getByRole('link', {name: 'How do I get an API key?'})
		).toHaveAttribute(
			'href',
			'https://platform.openai.com/docs/api-reference/authentication'
		);
	}
);

test(
	'Cannot enable OpenAI from the Site Settings after disabling it from the Instance Settings',
	{tag: '@LPS-179484'},
	async ({aiCreatorInstanceSettingsPage, page, site, siteSettingsPage}) => {
		try {
			await aiCreatorInstanceSettingsPage.disableChatGPTCreateContent();

			await siteSettingsPage.goToSiteSetting(
				'AI Creator',
				'OpenAI',
				site.friendlyUrlPath
			);

			await expect(
				page.getByLabel('Enable ChatGPT to Create Content')
			).toBeDisabled();

			await expect(
				page.getByText(
					'To enable ChatGPT for this site, first enable it for your instance.'
				)
			).toBeVisible();
		}
		finally {
			await aiCreatorInstanceSettingsPage.enableChatGPTCreateContent();
		}
	}
);

test(
	'Configure the API key and see an error message when a generic error happens',
	{tag: ['@LPS-179485', '@LPS-188490']},
	async ({
		aiCreatorInstanceSettingsPage,
		enableMockAICreatorOpenAIClient,
		page,
	}) => {
		await enableMockAICreatorOpenAIClient();

		try {

			// A valid API key is saved successfully

			await aiCreatorInstanceSettingsPage.addApiKey();

			await expect(aiCreatorInstanceSettingsPage.apiKeyInput).toHaveValue(
				'VALID_API_KEY'
			);

			// A generic error while validating the API key is surfaced

			await aiCreatorInstanceSettingsPage.apiKeyInput.fill(
				'OPENAI_API_IOEXCEPTION'
			);
			await aiCreatorInstanceSettingsPage.saveButton.click();

			await expect(
				page.getByText(
					'An unexpected error occurred while validating the API key.'
				)
			).toBeVisible();

			await expect(aiCreatorInstanceSettingsPage.apiKeyInput).toHaveValue(
				'OPENAI_API_IOEXCEPTION'
			);
		}
		finally {
			await aiCreatorInstanceSettingsPage.removeApiKey();
		}
	}
);

test(
	'Generate content with AI Creator and append it to the existing content',
	{tag: ['@LPS-179485', '@LPS-187651']},
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

			const modal = page.frameLocator('iframe[title="AI Creator"]');

			// The Learn more about OpenAI integration link is visible

			await expect(
				modal.getByRole('link', {
					name: 'Learn more about OpenAI integration.',
				})
			).toHaveAttribute('href', LEARN_MORE_HREF);

			// The modal shows the default values

			await expect(
				modal.getByPlaceholder('Write something...')
			).toBeVisible();
			await expect(
				modal.getByLabel('Tone').locator('option:checked')
			).toHaveText('Neutral');
			await expect(modal.getByLabel('Word Count')).toHaveValue('100');

			// A slow generation shows the loading message

			await modal
				.getByLabel('Description')
				.fill('USER_CONTENT_SLEEP_MILLIS_5000');
			await modal.getByRole('button', {name: 'Create'}).click();

			await expect(modal.getByText('Creating content...')).toBeVisible();
			await expect(
				modal.getByText('This process may take a while.')
			).toBeVisible();

			// The generated content can be added to the Content field

			await expect(modal.getByLabel('Content')).toHaveValue(
				'OPENAI_API_COMPLETION_RESPONSE_CONTENT'
			);
			await modal.getByRole('button', {name: 'Add'}).click();

			await expect(journalPage.articleContentTextBox).toContainText(
				'OPENAI_API_COMPLETION_RESPONSE_CONTENT'
			);

			// A second generation is appended to the existing content

			await page.getByRole('button', {name: 'Create AI Content'}).click();

			await modal.getByLabel('Description').fill('USER_CONTENT');
			await modal.getByRole('button', {name: 'Create'}).click();

			await expect(modal.getByLabel('Content')).toHaveValue(
				'OPENAI_API_COMPLETION_RESPONSE_CONTENT'
			);
			await modal.getByRole('button', {name: 'Add'}).click();

			await expect(journalPage.articleContentTextBox).toContainText(
				'OPENAI_API_COMPLETION_RESPONSE_CONTENTOPENAI_API_COMPLETION_RESPONSE_CONTENT'
			);
		}
		finally {
			await aiCreatorInstanceSettingsPage.removeApiKey();
		}
	}
);

test(
	'View error messages when errors happen on generating content',
	{tag: '@LPS-188490'},
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

			await journalPage.goto(site.friendlyUrlPath);
			await journalPage.goToCreateArticle('Basic Web Content');

			await page.getByRole('button', {name: 'Create AI Content'}).click();

			const modal = page.frameLocator('iframe[title="AI Creator"]');

			// An OpenAI server error 429 is surfaced

			await modal
				.getByLabel('Description')
				.fill(
					'OPENAI_API_You exceeded your current quota, please check your plan and billing details._ERROR_MESSAGE'
				);
			await modal.getByRole('button', {name: 'Create'}).click();

			await expect(
				modal.getByText(
					'You exceeded your current quota, please check your plan and billing details. Check this link for further information about OpenAI issues.'
				)
			).toBeVisible();

			// An OpenAI server error 500 is surfaced

			await modal
				.getByLabel('Description')
				.fill(
					'OPENAI_API_The server had an error while processing your request._ERROR_MESSAGE'
				);
			await modal.getByRole('button', {name: 'Create'}).click();

			await expect(
				modal.getByText(
					'The server had an error while processing your request. Check this link for further information about OpenAI issues.'
				)
			).toBeVisible();

			// A generic error is surfaced

			await modal
				.getByLabel('Description')
				.fill('OPENAI_API_IOEXCEPTION');
			await modal.getByRole('button', {name: 'Create'}).click();

			await expect(
				modal.getByText('An unexpected error occurred.')
			).toBeVisible();

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
