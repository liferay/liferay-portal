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
	'Articles can be added with a related asset and the related asset can be replaced',
	{tag: ['@LPD-110921', '@LPS-137116', '@LPS-154208']},
	async ({
		apiHelpers,
		knowledgeBaseEditArticlePage,
		knowledgeBasePage,
		knowledgeBaseViewArticlePage,
		page,
		site,
	}) => {
		const blogContent = getRandomString();
		const blogTitle = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			articleBody: blogContent,
			headline: blogTitle,
		});

		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			{
				buffer: Buffer.from(getRandomString()),
				mimeType: 'text/plain',
				name: `${documentTitle}.txt`,
			},
			{title: documentTitle}
		);

		const selectRelatedAsset = async (assetTitle: string) => {
			await knowledgeBaseEditArticlePage.expandPanel('Related Assets');

			const selectButton = page
				.getByRole('dialog')
				.getByRole('button', {exact: true, name: 'Select'});

			await clickAndExpectToBeVisible({
				target: selectButton,
				trigger: page.getByLabel('Select Items'),
			});

			await page
				.getByRole('checkbox', {name: `Select ${assetTitle}`})
				.check();

			await selectButton.click();

			await expect(page.getByRole('dialog')).toBeHidden();
		};

		const title = getRandomString();

		const relatedAssetsList = page.locator('.sidebar-list-group');

		await test.step('CanReplaceArticleRelatedAsset', async () => {
			await knowledgeBaseEditArticlePage.goto(site.friendlyUrlPath);

			await knowledgeBaseEditArticlePage.titlePlaceholder.fill(title);
			await knowledgeBaseEditArticlePage.contentTextBox.fill(
				getRandomString()
			);

			await selectRelatedAsset(documentTitle);

			await knowledgeBaseEditArticlePage.publish();

			await waitForAlert(
				page,
				`Success:${title} was successfully published.`
			);

			await knowledgeBaseViewArticlePage.goto(
				site.friendlyUrlPath,
				title
			);

			await expect(
				relatedAssetsList.getByRole('link', {name: documentTitle})
			).toBeVisible();

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.clickRowAction(title, 'Edit');

			await knowledgeBaseEditArticlePage.expandPanel('Related Assets');

			await page
				.getByRole('row', {name: documentTitle})
				.getByRole('button', {name: 'Remove'})
				.click();

			await expect(
				page.getByRole('row', {name: documentTitle})
			).toBeHidden();
		});

		await test.step('CanAddArticleWithRelatedBlog, CanReplaceArticleRelatedAsset', async () => {
			await selectRelatedAsset(blogTitle);

			await knowledgeBaseEditArticlePage.publish();

			await waitForAlert(
				page,
				`Success:${title} was successfully published.`
			);

			await knowledgeBaseViewArticlePage.goto(
				site.friendlyUrlPath,
				title
			);

			await expect(page.getByText('Related Assets')).toBeVisible();
			await expect(
				relatedAssetsList.getByRole('link', {name: documentTitle})
			).toHaveCount(0);

			await relatedAssetsList
				.getByRole('link', {name: blogTitle})
				.click();

			await expect(
				page.getByRole('heading', {name: blogTitle})
			).toBeVisible();
			await expect(page.getByText(blogContent)).toBeVisible();
		});
	}
);
