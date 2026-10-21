/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {knowledgeBasePages} from '../../../fixtures/knowledgeBasePagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {KnowledgeBasePage} from '../../../pages/knowledge-base-web/KnowledgeBasePage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getLoggedInPage from '../../../utils/getLoggedInPage';
import getRandomString from '../../../utils/getRandomString';
import {LoginScreenName} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import addSiteUser from './utils/addSiteUser';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	knowledgeBasePages,
	loginTest()
);

test(
	'Article row actions add child articles, subscribe and print',
	{tag: ['@LPD-110921', '@LPS-137116', '@LPS-156216']},
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

		await knowledgeBasePage.goto(site.friendlyUrlPath);

		await test.step('CanAddChildArticle', async () => {
			await knowledgeBasePage.clickRowAction(title, 'Add Child Article');

			const childContent = getRandomString();
			const childTitle = getRandomString();

			await knowledgeBaseEditArticlePage.titlePlaceholder.fill(
				childTitle
			);
			await knowledgeBaseEditArticlePage.contentTextBox.fill(
				childContent
			);

			await knowledgeBaseEditArticlePage.publish();

			await waitForAlert(
				page,
				`Success:${childTitle} was successfully published.`
			);

			await knowledgeBaseViewArticlePage.goto(
				site.friendlyUrlPath,
				title
			);

			const childArticlesRegion = page.getByRole('region', {
				name: childTitle,
			});

			await expect(
				page.getByRole('button', {name: 'Child Articles (1)'})
			).toBeVisible();
			await expect(childArticlesRegion).toContainText(childContent);

			await childArticlesRegion
				.getByRole('link', {name: childTitle})
				.click();

			await expect(childArticlesRegion).toBeHidden();

			await expect(knowledgeBaseViewArticlePage.articleTitle).toHaveText(
				childTitle
			);
			await expect(
				knowledgeBaseViewArticlePage.articleBody
			).toContainText(childContent);
		});

		await test.step('CanSubscribeToArticle', async () => {
			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.clickRowAction(title, 'Subscribe');

			await waitForAlert(page);

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await clickAndExpectToBeVisible({
				target: page.getByRole('menuitem', {
					exact: true,
					name: 'Unsubscribe',
				}),
				trigger: knowledgeBasePage
					.getRow(title)
					.getByLabel('Show Actions'),
			});

			await page.keyboard.press('Escape');
		});

		await test.step('CanPreviewPrintContent', async () => {
			await knowledgeBasePage.clickRowAction(title, 'Print');

			const printFrame = page.getByRole('dialog').frameLocator('iframe');

			await expect(printFrame.locator('.kb-title')).toHaveText(title);
			await expect(printFrame.locator('.kb-entity-body')).toContainText(
				content
			);
		});
	}
);

test(
	'Articles can be moved to a folder from the move modal',
	{tag: ['@LPD-110921', '@LPS-189119']},
	async ({
		apiHelpers,
		knowledgeBasePage,
		knowledgeBaseViewArticlePage,
		page,
		site,
	}) => {
		const folder =
			await apiHelpers.headlessDelivery.postSiteKnowledgeBaseFolder({
				name: getRandomString(),
				siteId: site.id,
			});

		const content = getRandomString();
		const title = getRandomString();

		await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
			articleBody: content,
			siteId: site.id,
			title,
		});

		await knowledgeBasePage.goto(site.friendlyUrlPath);

		await knowledgeBasePage.clickRowAction(title, 'Move');

		await knowledgeBasePage.selectMoveTargetAndSave(title, folder.name);

		await waitForAlert(page);

		await knowledgeBasePage.goto(site.friendlyUrlPath);

		const articleLink = page.getByRole('link', {exact: true, name: title});

		await clickAndExpectToBeVisible({
			target: articleLink,
			trigger: page.getByRole('link', {exact: true, name: folder.name}),
		});

		await expect(async () => {
			await articleLink.click({timeout: 1000});

			await expect(knowledgeBaseViewArticlePage.articleTitle).toHaveText(
				title,
				{timeout: 2000}
			);
		}).toPass();
		await expect(knowledgeBaseViewArticlePage.articleBody).toContainText(
			content
		);
	}
);

test(
	'Articles can be sorted and deleted in the table view',
	{tag: ['@LPD-110921', '@LPS-128095', '@LPS-174036']},
	async ({apiHelpers, browser, site}) => {
		const titlePrefix = getRandomString();

		const titles = ['Apple', 'Bag', 'Cat'].map(
			(name) => `${titlePrefix} ${name}`
		);

		for (const title of titles) {
			await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
				articleBody: getRandomString(),
				siteId: site.id,
				title,
			});
		}

		const user = await addSiteUser({apiHelpers, siteId: site.id});

		const browserContext = await browser.newContext();

		try {
			const userPage = await getLoggedInPage(
				browserContext,
				user.alternateName as LoginScreenName
			);

			const knowledgeBasePage = new KnowledgeBasePage(userPage);

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			const articleTitles = userPage
				.locator('[data-qa-id=row]')
				.getByRole('heading');

			await test.step('CanSortArticles', async () => {
				await expect(articleTitles).toHaveText(titles);

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: userPage.getByRole('menuitem', {
						name: 'Descending',
					}),
					trigger: userPage.getByRole('button', {name: 'Order'}),
				});

				await expect(articleTitles).toHaveText([...titles].reverse());
			});

			await test.step('CanDeleteArticleInTableView', async () => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: userPage.getByRole('menuitem', {name: 'Table'}),
					trigger: userPage.getByRole('button', {
						name: 'Select View, Currently Selected: List',
					}),
				});

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: userPage.getByRole('menuitem', {
						exact: true,
						name: 'Delete',
					}),
					trigger: userPage
						.getByRole('row', {name: titles[0]})
						.getByLabel('Show Actions'),
				});

				await expect(knowledgeBasePage.recycleBinAlert).toContainText(
					`The element ${titles[0]} was moved to the Recycle Bin.`
				);

				await knowledgeBasePage.goto(site.friendlyUrlPath);

				await expect(
					userPage.getByRole('row', {name: titles[1]})
				).toBeVisible();
				await expect(
					userPage.getByRole('row', {name: titles[0]})
				).toHaveCount(0);
			});
		}
		finally {
			await browserContext.close();
		}
	}
);
