/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {knowledgeBasePages} from '../../../fixtures/knowledgeBasePagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	knowledgeBasePages,
	loginTest()
);

test(
	'Articles can be added with an attachment and the attachment can be replaced',
	{tag: ['@LPD-110921', '@LPS-137116', '@LPS-154208']},
	async ({
		apiHelpers,
		knowledgeBaseEditArticlePage,
		knowledgeBasePage,
		page,
		site,
	}) => {
		const textFileName = `${getRandomString()}.txt`;

		const textFile = {
			buffer: Buffer.alloc(259, 'a'),
			mimeType: 'text/plain',
			name: textFileName,
		};

		const selectUploadedFile = async () => {
			const checkbox = page.getByRole('checkbox', {name: textFileName});

			await expect(async () => {
				if (
					(await checkbox.isEnabled()) &&
					!(await checkbox.isChecked())
				) {
					await checkbox.check({timeout: 1000});
				}

				await expect(checkbox).toBeChecked({timeout: 1000});
			}).toPass();
		};

		const attachmentsContainer = page.locator(
			'[id="_com_liferay_knowledge_base_web_portlet_AdminPortlet_existingAttachmentsContainer"]'
		);

		await test.step('CanAddArticleWithAttachment', async () => {
			const title = getRandomString();

			await knowledgeBaseEditArticlePage.goto(site.friendlyUrlPath);

			await knowledgeBaseEditArticlePage.titlePlaceholder.fill(title);
			await knowledgeBaseEditArticlePage.contentTextBox.fill(
				getRandomString()
			);

			await knowledgeBaseEditArticlePage.expandPanel('Attachments');

			await page.getByRole('button', {name: 'Select Files'}).waitFor();

			await page.locator('input[type="file"]').setInputFiles(textFile);

			await selectUploadedFile();

			await knowledgeBaseEditArticlePage.publish();

			await waitForAlert(
				page,
				`Success:${title} was successfully published.`
			);

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.clickRowAction(title, 'Edit');

			await knowledgeBaseEditArticlePage.expandPanel('Attachments');

			await expect(
				attachmentsContainer.getByText(`${textFileName} (259 B)`)
			).toBeVisible();
		});

		await test.step('CanReplaceArticleAttachment', async () => {
			const title = getRandomString();

			const knowledgeBaseArticle =
				await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
					articleBody: getRandomString(),
					siteId: site.id,
					title,
				});

			const imageFileName = `${getRandomString()}.jpg`;

			await apiHelpers.headlessDelivery.postKnowledgeBaseArticleKnowledgeBaseAttachment(
				knowledgeBaseArticle.id,
				{
					buffer: Buffer.from(getRandomString()),
					mimeType: 'image/jpeg',
					name: imageFileName,
				}
			);

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.clickRowAction(title, 'Edit');

			await knowledgeBaseEditArticlePage.expandPanel('Attachments');

			page.once('dialog', (dialog) => dialog.accept());

			await attachmentsContainer
				.locator('div', {hasText: imageFileName})
				.getByRole('link', {name: 'Delete'})
				.click();

			await expect(
				attachmentsContainer.getByText(imageFileName)
			).toBeHidden();

			await page.locator('input[type="file"]').setInputFiles(textFile);

			await selectUploadedFile();

			await knowledgeBaseEditArticlePage.publish();

			await waitForAlert(
				page,
				`Success:${title} was successfully published.`
			);

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.clickRowAction(title, 'Edit');

			await knowledgeBaseEditArticlePage.expandPanel('Attachments');

			await expect(
				attachmentsContainer.getByText(`${textFileName} (259 B)`)
			).toBeVisible();
			await expect(
				attachmentsContainer.getByText(imageFileName)
			).toHaveCount(0);
		});
	}
);
