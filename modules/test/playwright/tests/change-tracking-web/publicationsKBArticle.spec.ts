/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../fixtures/changeTrackingPagesTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {knowledgeBasePages} from '../../fixtures/knowledgeBasePagesTest';
import {loginTest} from '../../fixtures/loginTest';
import getRandomString from '../../utils/getRandomString';
import {waitForAlert} from '../../utils/waitForAlert';
import {KnowledgeBaseUrls} from '../knowledge-base-web/utils/knowledgeBaseUrls';

const test = mergeTests(
	apiHelpersTest,
	changeTrackingPagesTest,
	isolatedSiteTest,
	knowledgeBasePages,
	loginTest()
);

test('Can publish a Publication containing an added KBArticle', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	knowledgeBaseEditArticlePage,
	knowledgeBasePage,
	page,
	site,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	const content = getRandomString();
	const title = getRandomString();

	await knowledgeBaseEditArticlePage.goto(site.friendlyUrlPath);
	await knowledgeBaseEditArticlePage.publishNewKnowledgeBaseArticle(
		content,
		title
	);

	await waitForAlert(page, `Success:${title} was successfully published.`);

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await page.reload();

	await knowledgeBasePage.goto(site.friendlyUrlPath);

	await expect(page.getByRole('link', {name: title})).toBeVisible();
});

test('Can publish a Publication containing a deleted KBArticle', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	knowledgeBaseEditArticlePage,
	knowledgeBasePage,
	knowledgeBaseViewArticlePage,
	page,
	site,
}) => {
	const content = getRandomString();
	const title = getRandomString();

	await knowledgeBaseEditArticlePage.goto(site.friendlyUrlPath);
	await knowledgeBaseEditArticlePage.publishNewKnowledgeBaseArticle(
		content,
		title
	);

	await waitForAlert(page, `Success:${title} was successfully published.`);

	await changeTrackingPage.workOnPublication(ctCollection);

	await knowledgeBaseViewArticlePage.goto(site.friendlyUrlPath, title);
	await knowledgeBaseViewArticlePage.deleteKnowledgeBaseArticle();

	await expect(
		page.locator(
			'[id="_com_liferay_knowledge_base_web_portlet_AdminPortlet_recycleBinAlert"]'
		)
	).toBeVisible();

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await page.reload();

	await knowledgeBasePage.goto(site.friendlyUrlPath);

	await expect(page.getByRole('link', {name: title})).toBeHidden();
});

test('Can publish a Publication containing an edited KBArticle', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	knowledgeBaseEditArticlePage,
	knowledgeBasePage,
	page,
	site,
}) => {
	const content = getRandomString();
	const title = getRandomString();

	const kbArticle =
		await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
			articleBody: content,
			siteId: site.id,
			title,
		});

	await changeTrackingPage.workOnPublication(ctCollection);

	const knowledgeBaseUrls = new KnowledgeBaseUrls(site.friendlyUrlPath);

	await page.goto(knowledgeBaseUrls.getEditKBArticleUrl(kbArticle.id));

	await knowledgeBaseEditArticlePage.publishNewKnowledgeBaseArticle(
		`${content} edit`,
		`${title} edit`
	);

	await waitForAlert(
		page,
		`Success:${title} edit was successfully published.`
	);

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await page.reload();

	await knowledgeBasePage.goto(site.friendlyUrlPath);

	await expect(page.getByRole('link', {name: `${title} edit`})).toBeVisible();
});
