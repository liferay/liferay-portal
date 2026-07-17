/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {knowledgeBasePages} from '../../../fixtures/knowledgeBasePagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {liferayConfig} from '../../../liferay.config';
import {KnowledgeBaseEditArticlePage} from '../../../pages/knowledge-base-web/KnowledgeBaseEditArticlePage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getLoggedInPage from '../../../utils/getLoggedInPage';
import getRandomString from '../../../utils/getRandomString';
import {performLogout} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {KnowledgeBaseUrls} from './utils/knowledgeBaseUrls';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	knowledgeBasePages,
	featureFlagsTest({
		'LPD-11003': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	loginTest()
);

const SAMPLE_IMAGE = path.join(
	__dirname,
	'../../frontend-js-item-selector-web/main/dependencies/sample_image.png'
);

test('LPD-27537: Article should be shown to guest users', async ({
	apiHelpers,
	page,
	site,
}) => {
	const content = getRandomString();
	const title = getRandomString();

	const knowledgeBaseArticle =
		await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
			articleBody: content,
			siteId: site.id,
			title,
			viewableBy: 'Anyone',
		});

	await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getWidgetDefinition({
				id: getRandomString(),
				widgetName:
					'com_liferay_knowledge_base_web_portlet_DisplayPortlet',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await performLogout(page);

	await page.goto(
		liferayConfig.environment.baseUrl +
			'/c/knowledge_base/find_kb_article?resourcePrimKey=' +
			knowledgeBaseArticle.id
	);

	await expect(
		page.getByText('Error:Your request failed to complete.')
	).toBeHidden();

	await expect(page.getByRole('heading', {name: title})).toBeVisible();
});

test('LPD-23801 error message is shown when an admin user tries to publish an article that an admin is currently editing', async ({
	apiHelpers,
	browser,
	knowledgeBaseEditArticlePage,
	page,
	site,
}) => {
	const content = getRandomString();
	const title = getRandomString();

	const knowledgeBaseArticle =
		await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
			articleBody: content,
			siteId: site.id,
			title,
		});
	const knowledgeBaseUrls = new KnowledgeBaseUrls(site.friendlyUrlPath);

	await page.goto(
		knowledgeBaseUrls.getEditKBArticleUrl(knowledgeBaseArticle.id)
	);

	await expect(page.getByPlaceholder('Untitled Article')).toHaveValue(title);

	const browserContext = await browser.newContext();

	try {
		const otherUserPage = await getLoggedInPage(
			browserContext,
			'demo.company.admin'
		);

		await otherUserPage.goto(
			knowledgeBaseUrls.getEditKBArticleUrl(
				knowledgeBaseArticle.id,
				true,
				knowledgeBaseUrls.home
			)
		);

		await expect(
			otherUserPage.getByPlaceholder('Untitled Article')
		).toHaveValue(title);

		await knowledgeBaseEditArticlePage.publishNewKnowledgeBaseArticle(
			`${content} test`,
			`${title} test`
		);

		await expect(
			page.getByText('Your changes cannot be saved.')
		).toBeVisible();

		const otherUserKnowledgeBaseEditArticlePage =
			new KnowledgeBaseEditArticlePage(otherUserPage);

		await otherUserKnowledgeBaseEditArticlePage.cancel();
	}
	finally {
		await browserContext.close();
	}
});

test('can publish and delete an article', async ({
	knowledgeBaseEditArticlePage,
	knowledgeBaseViewArticlePage,
	page,
	site,
}) => {
	const content = getRandomString();
	const title = getRandomString();

	const kbArticle = page.getByRole('link', {name: title});

	await knowledgeBaseEditArticlePage.goto(site.friendlyUrlPath);
	await knowledgeBaseEditArticlePage.publishNewKnowledgeBaseArticle(
		content,
		title
	);

	await waitForAlert(page, `Success:${title} was successfully published.`);

	await expect(kbArticle).toBeVisible();
	await expect(page.locator('.workflow-status-approved')).toBeVisible();

	await knowledgeBaseViewArticlePage.goto(site.friendlyUrlPath, title);
	await knowledgeBaseViewArticlePage.deleteKnowledgeBaseArticle();

	await expect(
		page.locator(
			'[id="_com_liferay_knowledge_base_web_portlet_AdminPortlet_recycleBinAlert"]'
		)
	).toBeVisible();
	await expect(kbArticle).toBeHidden();
});

test('can delete all articles (recycle bin enabled)', async ({
	knowledgeBaseEditArticlePage,
	knowledgeBasePage,
	page,
	site,
}) => {
	await knowledgeBaseEditArticlePage.goto(site.friendlyUrlPath);

	const title = getRandomString();

	await knowledgeBaseEditArticlePage.publishNewKnowledgeBaseArticle(
		getRandomString(),
		title
	);

	await waitForAlert(page, `Success:${title} was successfully published.`);

	await knowledgeBasePage.goto(site.friendlyUrlPath);
	await knowledgeBasePage.deleteAll(true);

	await expect(
		page.locator(
			'[id="_com_liferay_knowledge_base_web_portlet_AdminPortlet_recycleBinAlert"]'
		)
	).toBeVisible();
	await expect(
		page.getByRole('heading', {name: 'Knowledge base is empty.'})
	).toBeVisible();
});

test('can schedule and delete an article', async ({
	knowledgeBaseEditArticlePage,
	knowledgeBaseViewArticlePage,
	page,
	site,
}) => {
	const title = getRandomString();

	const kbArticle = page.getByRole('link', {name: title});

	await knowledgeBaseEditArticlePage.goto(site.friendlyUrlPath);
	await knowledgeBaseEditArticlePage.scheduleNewKnowledgeBaseArticle(
		getRandomString(),
		`${new Date().getFullYear() + 1}-01-01 00:00`,
		title
	);

	await waitForAlert(page, `Success:${title} will be published on`);

	await expect(kbArticle).toBeVisible();
	await expect(page.locator('.workflow-status-scheduled')).toBeVisible();

	await knowledgeBaseViewArticlePage.goto(site.friendlyUrlPath, title);
	await knowledgeBaseViewArticlePage.deleteKnowledgeBaseArticle();

	await expect(
		page.locator(
			'[id="_com_liferay_knowledge_base_web_portlet_AdminPortlet_recycleBinAlert"]'
		)
	).toBeVisible();
	await expect(kbArticle).toBeHidden();
});

test(
	'Can edit an article via toolbar',
	{
		tag: '@LPD-71884',
	},
	async ({
		apiHelpers,
		knowledgeBaseEditArticlePage,
		knowledgeBasePage,
		knowledgeBaseViewArticlePage,
		page,
		site,
	}) => {
		const content = getRandomString();
		const title = getRandomString();

		await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
			articleBody: content,
			siteId: site.id,
			title,
		});

		await knowledgeBaseViewArticlePage.goto(site.friendlyUrlPath, title);

		await page.getByLabel('Edit').click();

		const titleEdited = title + ' Edit';

		await knowledgeBaseEditArticlePage.editKBArticle(
			titleEdited,
			content + ' Edit'
		);

		await knowledgeBasePage.goto(site.friendlyUrlPath);

		await expect(
			page.getByRole('link', {exact: true, name: titleEdited})
		).toBeVisible();

		await expect(
			page.getByRole('link', {exact: true, name: title})
		).not.toBeVisible();
	}
);

test(
	'Search bar is not disabled after zero-result search',
	{tag: '@LPD-86105'},
	async ({apiHelpers, knowledgeBasePage, page, site}) => {
		await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
			articleBody: getRandomString(),
			siteId: site.id,
			title: getRandomString(),
		});

		await knowledgeBasePage.goto(site.friendlyUrlPath);

		const searchInput = page.getByRole('searchbox');

		await searchInput.fill(getRandomString());
		await searchInput.press('Enter');

		await expect(searchInput).not.toBeDisabled();
	}
);

test('Add an image to an article through the Upload Image tab', async ({
	apiHelpers,
	knowledgeBaseEditArticlePage,
	knowledgeBaseViewArticlePage,
	page,
	site,
}) => {
	const title = getRandomString();

	const article =
		await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
			articleBody: getRandomString(),
			siteId: site.id,
			title,
		});

	const knowledgeBaseUrls = new KnowledgeBaseUrls(site.friendlyUrlPath);

	// Open the article editor and upload an image into the content field

	await page.goto(knowledgeBaseUrls.getEditKBArticleUrl(article.id));

	await expect(page.getByPlaceholder('Untitled Article')).toHaveValue(title);

	await page.getByRole('button', {name: 'Image'}).click();

	const itemSelector = page.frameLocator('iframe[title="Select Item"]');

	await itemSelector.getByRole('link', {name: 'Upload Image'}).click();

	await itemSelector
		.locator('input[type="file"]')
		.setInputFiles(SAMPLE_IMAGE);

	await itemSelector.getByRole('button', {exact: true, name: 'Add'}).click();

	// Publish and verify the uploaded image renders in the article

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: knowledgeBaseEditArticlePage.publishMenuItem,
		trigger: knowledgeBaseEditArticlePage.publishButton,
	});

	await waitForAlert(page, `Success:${title} was successfully published.`);

	await knowledgeBaseViewArticlePage.goto(site.friendlyUrlPath, title);

	await expect(page.locator('img[src*="sample_image"]')).toBeVisible();
});
