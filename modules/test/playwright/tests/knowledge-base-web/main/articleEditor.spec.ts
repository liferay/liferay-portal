/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {knowledgeBasePages} from '../../../fixtures/knowledgeBasePagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	knowledgeBasePages,
	loginTest()
);

test(
	'Articles can be added and edited as drafts',
	{tag: ['@LPD-110921', '@LPS-137116']},
	async ({
		apiHelpers,
		knowledgeBaseEditArticlePage,
		knowledgeBasePage,
		knowledgeBaseViewArticlePage,
		page,
		site,
	}) => {
		await test.step('CanAddArticleAsDraft', async () => {
			const content = getRandomString();
			const title = getRandomString();

			await knowledgeBaseEditArticlePage.goto(site.friendlyUrlPath);

			await knowledgeBaseEditArticlePage.titlePlaceholder.fill(title);
			await knowledgeBaseEditArticlePage.contentTextBox.fill(content);

			await knowledgeBaseEditArticlePage.saveAsDraftButton.click();

			await waitForAlert(
				page,
				`Success:${title} was successfully saved as a draft.`
			);

			await knowledgeBaseViewArticlePage.goto(
				site.friendlyUrlPath,
				title
			);

			await expect(
				knowledgeBaseViewArticlePage.articleBody
			).toContainText(content);

			await knowledgeBaseViewArticlePage.openInfoPanel();

			await expect(
				knowledgeBaseViewArticlePage.getInfoPanelValue('Status')
			).toHaveText('Draft');
		});

		await test.step('CanSaveEditedArticleAsDraft', async () => {
			const title = getRandomString();

			await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
				articleBody: getRandomString(),
				siteId: site.id,
				title,
			});

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.clickRowAction(title, 'Edit');

			await expect(
				page.getByText(
					'A new version is created automatically if this content is modified.'
				)
			).toBeVisible();

			const editedContent = getRandomString();
			const editedTitle = getRandomString();

			await knowledgeBaseEditArticlePage.titlePlaceholder.fill(
				editedTitle
			);
			await knowledgeBaseEditArticlePage.contentTextBox.fill(
				editedContent
			);

			await knowledgeBaseEditArticlePage.saveAsDraftButton.click();

			await waitForAlert(
				page,
				`Success:${editedTitle} was successfully saved as a draft.`
			);

			await knowledgeBaseViewArticlePage.goto(
				site.friendlyUrlPath,
				editedTitle
			);

			await expect(
				knowledgeBaseViewArticlePage.articleBody
			).toContainText(editedContent);

			await knowledgeBaseViewArticlePage.openInfoPanel();

			await expect(
				knowledgeBaseViewArticlePage.getInfoPanelValue('Status')
			).toHaveText('Draft');
		});
	}
);

test(
	'Article friendly URLs can be customized and must be unique',
	{tag: ['@LPD-110921', '@LPS-137116', '@LPS-154208']},
	async ({
		apiHelpers,
		knowledgeBaseEditArticlePage,
		knowledgeBasePage,
		page,
		site,
	}) => {
		const friendlyURLInput = page.getByLabel('Friendly URL');

		await test.step('CanAddArticleWithCustomFriendlyURL', async () => {
			const friendlyURL = getRandomString();
			const title = getRandomString();

			await knowledgeBaseEditArticlePage.goto(site.friendlyUrlPath);

			await knowledgeBaseEditArticlePage.titlePlaceholder.fill(title);
			await knowledgeBaseEditArticlePage.contentTextBox.fill(
				getRandomString()
			);

			await knowledgeBaseEditArticlePage.expandPanel('Configuration');

			await friendlyURLInput.fill(friendlyURL);

			await knowledgeBaseEditArticlePage.publish();

			await waitForAlert(
				page,
				`Success:${title} was successfully published.`
			);

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.clickRowAction(title, 'Edit');

			await knowledgeBaseEditArticlePage.expandPanel('Configuration');

			await expect(friendlyURLInput).toHaveValue(friendlyURL);
			await expect(friendlyURLInput).toBeDisabled();
		});

		await test.step('CannotAddArticleWithDuplicateURL', async () => {
			const title = getRandomString();

			await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
				articleBody: getRandomString(),
				siteId: site.id,
				title,
			});

			await knowledgeBaseEditArticlePage.goto(site.friendlyUrlPath);

			await expect(async () => {
				await knowledgeBaseEditArticlePage.titlePlaceholder.fill(
					title,
					{
						timeout: 1000,
					}
				);

				await expect(friendlyURLInput).toHaveValue(title, {
					timeout: 1000,
				});
			}).toPass();

			await knowledgeBaseEditArticlePage.contentTextBox.fill(
				getRandomString()
			);

			await knowledgeBaseEditArticlePage.publish();

			await waitForAlert(
				page,
				`Success:${title} was successfully published.`
			);

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.clickRowAction(title, 'Edit', 1);

			await knowledgeBaseEditArticlePage.expandPanel('Configuration');

			await expect(friendlyURLInput).toHaveValue(`${title}-1`);

			await knowledgeBaseEditArticlePage.goto(site.friendlyUrlPath);

			await knowledgeBaseEditArticlePage.titlePlaceholder.fill(title);
			await knowledgeBaseEditArticlePage.contentTextBox.fill(
				getRandomString()
			);

			await knowledgeBaseEditArticlePage.expandPanel('Configuration');

			await friendlyURLInput.fill(title);

			await knowledgeBaseEditArticlePage.publish();

			await expect(
				page.getByText('Please enter a unique friendly URL.')
			).toBeVisible();
			await expect(
				page.getByText('Your request failed to complete.')
			).toBeVisible();
		});
	}
);

test(
	'Articles can be added with a description and HTML content',
	{tag: ['@LPD-110921', '@LPS-137116', '@LPS-172372']},
	async ({
		knowledgeBaseEditArticlePage,
		knowledgeBaseViewArticlePage,
		page,
		site,
	}) => {
		const description = getRandomString();
		const title = getRandomString();

		await knowledgeBaseEditArticlePage.goto(site.friendlyUrlPath);

		await knowledgeBaseEditArticlePage.titlePlaceholder.fill(title);

		await test.step('CanAddArticleWithDescription', async () => {
			await knowledgeBaseEditArticlePage.expandPanel('Configuration');

			await page
				.getByRole('group', {name: 'Configuration'})
				.getByRole('textbox', {name: 'Characters Maximum: 4000'})
				.fill(description);
		});

		await test.step('CanAddArticleWithHTML', async () => {
			await page.getByRole('button', {name: 'Source'}).click();

			await page
				.locator('.CodeMirror')
				.evaluate(
					(element: any, html) => element.CodeMirror.setValue(html),
					'<b>Knowledge Base&rsquo;s Article Content</b>'
				);
		});

		await knowledgeBaseEditArticlePage.publish();

		await waitForAlert(
			page,
			`Success:${title} was successfully published.`
		);

		await knowledgeBaseViewArticlePage.goto(site.friendlyUrlPath, title);

		await test.step('CanAddArticleWithDescription', async () => {
			await expect(
				knowledgeBaseViewArticlePage.articleBody.getByText(description)
			).toBeVisible();
		});

		await test.step('CanAddArticleWithHTML', async () => {
			await expect(
				knowledgeBaseViewArticlePage.articleBody.locator('b')
			).toHaveText('Knowledge Base’s Article Content');
		});
	}
);

test(
	'Articles cannot be added without a title or content',
	{tag: ['@LPD-110921', '@LPS-137116']},
	async ({knowledgeBaseEditArticlePage, knowledgeBasePage, page, site}) => {
		await knowledgeBaseEditArticlePage.goto(site.friendlyUrlPath);

		await test.step('CannotAddArticleWithoutContent', async () => {
			await expect(
				page
					.getByRole('group')
					.filter({hasText: 'Content'})
					.locator('.reference-mark')
			).toBeVisible();

			await knowledgeBaseEditArticlePage.titlePlaceholder.fill(
				getRandomString()
			);

			await knowledgeBaseEditArticlePage.publish();

			await expect(
				page.getByText('The Content field is required.')
			).toBeVisible();
		});

		await test.step('CannotAddArticleWithoutTitle', async () => {
			await knowledgeBaseEditArticlePage.titlePlaceholder.fill('');
			await knowledgeBaseEditArticlePage.contentTextBox.fill(
				getRandomString()
			);

			await knowledgeBaseEditArticlePage.publish();

			await expect(
				knowledgeBaseEditArticlePage.titlePlaceholder
			).toHaveAttribute('aria-invalid', 'true');
		});

		await knowledgeBasePage.goto(site.friendlyUrlPath);

		await expect(knowledgeBasePage.emptyStateTitle).toBeVisible();
	}
);

test(
	'Articles can be added from the empty state button',
	{tag: ['@LPD-110921', '@LPS-158706']},
	async ({
		knowledgeBaseEditArticlePage,
		knowledgeBasePage,
		knowledgeBaseViewArticlePage,
		page,
		site,
	}) => {
		const content = getRandomString();
		const title = getRandomString();

		await knowledgeBasePage.goto(site.friendlyUrlPath);

		await knowledgeBasePage.openFoldersAndArticlesPanel();

		await test.step('CanAddArticleViaCenterPaneButton', async () => {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: knowledgeBasePage.basicArticleMenuItem,
				trigger: page
					.locator('.taglib-empty-result-message')
					.getByRole('button', {name: 'New'}),
			});

			await knowledgeBaseEditArticlePage.titlePlaceholder.fill(title);
			await knowledgeBaseEditArticlePage.contentTextBox.fill(content);

			await knowledgeBaseEditArticlePage.publish();

			await waitForAlert(
				page,
				`Success:${title} was successfully published.`
			);

			await knowledgeBasePage.openFoldersAndArticlesPanel();

			await expect(knowledgeBasePage.getTreeItem(title)).toBeVisible();

			await knowledgeBaseViewArticlePage.goto(
				site.friendlyUrlPath,
				title
			);

			await expect(
				knowledgeBaseViewArticlePage.articleBody
			).toContainText(content);
			await expect(page.getByRole('button', {name: 'New'})).toHaveCount(
				0
			);
		});
	}
);
