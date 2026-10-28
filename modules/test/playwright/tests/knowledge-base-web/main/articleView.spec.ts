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
	'Article info panel, versions and history show every version of the article',
	{tag: ['@LPD-110921', '@LPS-156216', '@LPS-159175', '@LPS-167472']},
	async ({
		apiHelpers,
		knowledgeBasePage,
		knowledgeBaseViewArticlePage,
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

		await apiHelpers.headlessDelivery.postKnowledgeBaseArticleKnowledgeBaseArticle(
			{
				articleBody: getRandomString(),
				parentKnowledgeBaseArticleId: knowledgeBaseArticle.id,
				title: getRandomString(),
			}
		);

		await knowledgeBasePage.goto(site.friendlyUrlPath);

		await test.step('SomeArticleInfoOnlyDisplayInPanel', async () => {
			const row = knowledgeBasePage.getRow(title);

			await expect(row).toContainText('1 Child Articles');
			await expect(row).not.toContainText('Priority');
			await expect(row).not.toContainText('Views');
		});

		await knowledgeBaseViewArticlePage.goto(site.friendlyUrlPath, title);

		await knowledgeBaseViewArticlePage.openInfoPanel();

		const infoPanel = knowledgeBaseViewArticlePage.infoPanel;

		await test.step('CanViewEntryInfo, SomeArticleInfoOnlyDisplayInPanel', async () => {
			await expect(infoPanel.locator('.component-title')).toHaveText(
				title
			);
			await expect(
				infoPanel.locator('.sidebar-header .label-info')
			).toHaveText('Version 1');
			await expect(
				knowledgeBaseViewArticlePage.getInfoPanelValue('Status')
			).toHaveText('Approved');
			await expect(
				knowledgeBaseViewArticlePage.getInfoPanelValue('Child Articles')
			).toHaveText('1 Child Articles');
			await expect(
				knowledgeBaseViewArticlePage.getInfoPanelValue('Views')
			).toHaveText(/^\s*[01]\s*$/);
		});

		await test.step('CanViewEntryInfo', async () => {
			await infoPanel.getByRole('tab', {name: 'Versions'}).click();

			await expect(
				infoPanel.locator('.list-group-item', {hasText: 'Version 1'})
			).toContainText('By Test Test, on');
		});

		const editedContent = `${content} ${getRandomString()}`;
		const editedTitle = getRandomString();

		await apiHelpers.headlessDelivery.putKnowledgeBaseArticle({
			articleBody: editedContent,
			knowledgeBaseArticleId: knowledgeBaseArticle.id,
			title: editedTitle,
		});

		await knowledgeBaseViewArticlePage.goto(
			site.friendlyUrlPath,
			editedTitle
		);

		await knowledgeBaseViewArticlePage.openInfoPanel();

		await test.step('CanViewEntryInfo', async () => {
			await expect(infoPanel.locator('.component-title')).toHaveText(
				editedTitle
			);
			await expect(
				infoPanel.locator('.sidebar-header .label-info')
			).toHaveText('Version 2');
			await expect(
				knowledgeBaseViewArticlePage.getInfoPanelValue('Status')
			).toHaveText('Approved');

			await infoPanel.getByRole('tab', {name: 'Versions'}).click();

			await expect(
				infoPanel.locator('.list-group-item', {hasText: 'Version 2'})
			).toContainText('By Test Test, on');
		});

		await test.step('CanViewKBArticleHistory', async () => {
			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.clickRowAction(editedTitle, 'History');

			await page.locator('input[type="checkbox"][value="2"]').check();
			await page.locator('input[type="checkbox"][value="1"]').check();

			await page.getByRole('button', {name: 'Compare Versions'}).click();

			await expect(page.locator('[id*="added-diff"]')).toHaveText(
				editedContent.replace(`${content} `, '')
			);
		});
	}
);

test(
	'Article view shows the toolbar actions and lists submitted suggestions',
	{tag: ['@LPD-110921', '@LPD-112411', '@LPS-160369', '@LPS-170293']},
	async ({apiHelpers, knowledgeBaseViewArticlePage, page, site}) => {
		const title = getRandomString();

		await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
			articleBody: getRandomString(),
			siteId: site.id,
			title,
		});

		await knowledgeBaseViewArticlePage.goto(site.friendlyUrlPath, title);

		await test.step('CorrectActionsAppearInToolbarAfterSelectingArticle', async () => {
			await expect(page.getByRole('link', {name: 'Edit'})).toBeVisible();
			await expect(
				knowledgeBaseViewArticlePage.infoPanelButton
			).toBeVisible();
			await expect(
				page.getByRole('link', {name: 'Subscribe'})
			).toBeVisible();

			await clickAndExpectToBeVisible({
				target: page.getByRole('menu'),
				trigger: knowledgeBaseViewArticlePage.showActionsButton,
			});

			for (const menuItem of [
				'Add Child Article',
				'Delete',
				'Edit',
				'History',
				'Move',
				'Permissions',
				'Print',
				'Subscribe',
			]) {
				await expect(
					page.getByRole('menuitem', {exact: true, name: menuItem})
				).toBeVisible();
			}

			await page.keyboard.press('Escape');
		});

		await test.step('CanViewArticlesWithSuggestions', async () => {
			const suggestions = [getRandomString(), getRandomString()];

			for (const suggestion of suggestions) {
				await page
					.getByRole('textbox', {name: 'Characters Maximum: 4000'})
					.fill(suggestion);

				await page.getByRole('button', {name: 'Submit'}).click();

				await waitForAlert(page, 'Suggestion was saved successfully.');
			}

			await knowledgeBaseViewArticlePage.goto(
				site.friendlyUrlPath,
				title
			);

			for (const suggestion of suggestions) {
				const suggestionItem = page.locator('.list-group-item', {
					hasText: suggestion,
				});

				await expect(suggestionItem).toContainText('Test Test');
				await expect(suggestionItem).toContainText('New');
			}
		});
	}
);
