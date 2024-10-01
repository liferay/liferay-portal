/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {knowledgeBasePages} from '../../fixtures/knowledgeBasePagesTest';
import {loginTest} from '../../fixtures/loginTest';
import {liferayConfig} from '../../liferay.config';
import {KnowledgeBaseEditArticlePage} from '../../pages/knowledge-base-web/KnowledgeBaseEditArticlePage';
import getLoggedInPage from '../../utils/getLoggedInPage';
import getRandomString from '../../utils/getRandomString';
import {performLogout} from '../../utils/performLogin';
import {waitForAlert} from '../../utils/waitForAlert';
import {KnowledgeBaseUrls} from './utils/knowledgeBaseUrls';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	knowledgeBasePages,
	featureFlagsTest({
		'LPD-11003': true,
	}),
	loginTest()
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

	await expect(page.getByText('Knowledge Base Article')).toBeVisible();
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
