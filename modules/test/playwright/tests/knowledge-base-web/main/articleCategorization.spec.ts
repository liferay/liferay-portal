/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {knowledgeBasePages} from '../../../fixtures/knowledgeBasePagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {KnowledgeBaseUrls} from './utils/knowledgeBaseUrls';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	knowledgeBasePages,
	loginTest()
);

test(
	'Articles require a category of a required vocabulary',
	{tag: ['@LPD-110921', '@LPS-137116', '@LPS-158414']},
	async ({
		apiHelpers,
		knowledgeBaseEditArticlePage,
		knowledgeBaseViewArticlePage,
		page,
		site,
	}) => {
		const vocabulary =
			await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
				assetTypes: [{required: true, type: 'KnowledgeBaseArticle'}],
				name: getRandomString(),
				siteId: site.id,
			});

		const category =
			await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
				{
					name: getRandomString(),
					vocabularyId: vocabulary.id,
				}
			);

		const title = getRandomString();

		await knowledgeBaseEditArticlePage.goto(site.friendlyUrlPath);

		await knowledgeBaseEditArticlePage.titlePlaceholder.fill(title);
		await knowledgeBaseEditArticlePage.contentTextBox.fill(
			getRandomString()
		);

		await test.step('CannotAddArticleWithoutCategory', async () => {
			await knowledgeBaseEditArticlePage.publish();

			await expect(
				page.getByText(
					`Please select at least one category for ${vocabulary.name}.`
				)
			).toBeVisible();
			await expect(
				page.getByText('Your request failed to complete.')
			).toBeVisible();
		});

		await test.step('CanAddArticleWithCategory', async () => {
			await knowledgeBaseEditArticlePage.expandPanel('Categorization');

			const selectCategoryModal = page.getByRole('dialog', {
				name: `Select ${vocabulary.name}`,
			});

			await clickAndExpectToBeVisible({
				target: selectCategoryModal,
				trigger: page.getByRole('button', {
					name: `Select ${vocabulary.name}`,
				}),
			});

			await selectCategoryModal
				.getByRole('treeitem', {name: category.name})
				.getByRole('checkbox')
				.check();

			await selectCategoryModal
				.getByRole('button', {name: 'Done'})
				.click();

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
				page.locator('.taglib-asset-categories-summary')
			).toHaveText(`${vocabulary.name}: ${category.name}`);
			await expect(
				page.locator('.kb-article-assets').getByRole('link', {
					name: category.name,
				})
			).toBeVisible();
		});
	}
);

test(
	'Article categories and tags can be replaced',
	{tag: ['@LPD-110921', '@LPS-154208']},
	async ({apiHelpers, knowledgeBaseEditArticlePage, page, site}) => {
		const vocabulary =
			await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
				name: getRandomString(),
				siteId: site.id,
			});

		const [category1, category2] = await Promise.all(
			[getRandomString(), getRandomString()].map((name) =>
				apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
					{
						name,
						vocabularyId: vocabulary.id,
					}
				)
			)
		);

		const tag1 = getRandomString();

		const knowledgeBaseArticle =
			await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
				articleBody: getRandomString(),
				keywords: [tag1],
				siteId: site.id,
				taxonomyCategoryIds: [category1.id],
				title: getRandomString(),
			});

		const editKBArticleURL = new KnowledgeBaseUrls(
			site.friendlyUrlPath
		).getEditKBArticleUrl(knowledgeBaseArticle.id);

		await page.goto(editKBArticleURL);

		await knowledgeBaseEditArticlePage.expandPanel('Categorization');

		const tag2 = getRandomString();

		await test.step('CanReplaceArticleCategory', async () => {
			const removeCategory1Button = page.getByRole('button', {
				name: `Remove ${category1.name}`,
			});

			await clickAndExpectToBeHidden({
				target: removeCategory1Button,
				trigger: removeCategory1Button,
			});

			await page.keyboard.press('Escape');

			const selectCategoryModal = page.getByRole('dialog', {
				name: `Select ${vocabulary.name}`,
			});

			await clickAndExpectToBeVisible({
				target: selectCategoryModal,
				trigger: page.getByRole('button', {
					name: `Select ${vocabulary.name}`,
				}),
			});

			await selectCategoryModal
				.getByRole('treeitem', {name: category2.name})
				.getByRole('checkbox')
				.check();

			await selectCategoryModal
				.getByRole('button', {name: 'Done'})
				.click();
		});

		await test.step('CanReplaceArticleTag', async () => {
			await page.getByRole('button', {name: `Remove ${tag1}`}).click();

			const tagsInput = page.getByRole('combobox', {name: 'Tags'});

			await tagsInput.fill(tag2);
			await tagsInput.press('Enter');

			await page.keyboard.press('Escape');
		});

		await knowledgeBaseEditArticlePage.publish();

		await waitForAlert(
			page,
			`Success:${knowledgeBaseArticle.title} was successfully published.`
		);

		await page.goto(editKBArticleURL);

		await knowledgeBaseEditArticlePage.expandPanel('Categorization');

		await test.step('CanReplaceArticleCategory', async () => {
			await expect(
				page.getByRole('button', {name: `Remove ${category2.name}`})
			).toBeVisible();
			await expect(
				page.getByRole('button', {name: `Remove ${category1.name}`})
			).toHaveCount(0);
		});

		await test.step('CanReplaceArticleTag', async () => {
			await expect(
				page.getByRole('button', {name: `Remove ${tag2}`})
			).toBeVisible();
			await expect(
				page.getByRole('button', {name: `Remove ${tag1}`})
			).toHaveCount(0);
		});
	}
);
