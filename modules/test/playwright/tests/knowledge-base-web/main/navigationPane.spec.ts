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
	'Articles can be edited, subscribed and given children and permissions from the navigation pane',
	{tag: ['@LPD-110921', '@LPS-156215']},
	async ({
		apiHelpers,
		knowledgeBaseEditArticlePage,
		knowledgeBasePage,
		knowledgeBaseViewArticlePage,
		page,
		site,
	}) => {
		const title = getRandomString();

		await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
			articleBody: getRandomString(),
			siteId: site.id,
			title,
			viewableBy: 'Owner',
		});

		await knowledgeBasePage.goto(site.friendlyUrlPath);

		await knowledgeBasePage.openFoldersAndArticlesPanel();

		await test.step('CanAddChildArticleFromNavigationPane', async () => {
			await knowledgeBasePage.clickTreeItemAction(
				title,
				'Add Child Article'
			);

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

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.openFoldersAndArticlesPanel();

			await clickAndExpectToBeVisible({
				target: knowledgeBasePage.getTreeItem(childTitle),
				trigger: knowledgeBasePage
					.getTreeItem(title)
					.getByRole('button', {exact: true, name: title}),
			});

			await expect(async () => {
				await knowledgeBasePage
					.getTreeItem(childTitle)
					.getByText(childTitle, {exact: true})
					.click({timeout: 1000});

				await expect(
					knowledgeBaseViewArticlePage.articleTitle
				).toHaveText(childTitle, {timeout: 2000});
			}).toPass();
			await expect(
				knowledgeBaseViewArticlePage.articleBody
			).toContainText(childContent);
		});

		const editedTitle = getRandomString();

		await test.step('CanEditEntryFromNavigationPane', async () => {
			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.openFoldersAndArticlesPanel();

			await knowledgeBasePage.clickTreeItemAction(title, 'Edit');

			await knowledgeBaseEditArticlePage.titlePlaceholder.fill(
				editedTitle
			);

			await knowledgeBaseEditArticlePage.publish();

			await waitForAlert(
				page,
				`Success:${editedTitle} was successfully published.`
			);

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.openFoldersAndArticlesPanel();

			await expect(knowledgeBasePage.getRow(editedTitle)).toBeVisible();
			await expect(
				knowledgeBasePage.getTreeItem(editedTitle)
			).toBeVisible();
		});

		await test.step('CanSubscribeToArticleFromNavigationPane', async () => {
			await knowledgeBasePage.clickTreeItemAction(
				editedTitle,
				'Subscribe'
			);

			await waitForAlert(page);

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.openFoldersAndArticlesPanel();

			await knowledgeBasePage.getTreeItem(editedTitle).hover();

			await clickAndExpectToBeVisible({
				target: page.getByRole('menuitem', {
					exact: true,
					name: 'Unsubscribe',
				}),
				trigger: knowledgeBasePage
					.getTreeItem(editedTitle)
					.getByRole('button', {name: 'Actions'})
					.first(),
			});

			await page.keyboard.press('Escape');
		});

		await test.step('CanEditArticlePermissionsFromNavigationPane', async () => {
			const permissionsFrame = page.frameLocator(
				'iframe[title="Permissions"]'
			);

			const siteMemberViewCheckbox = permissionsFrame.locator(
				'#site-member_ACTION_VIEW'
			);

			await knowledgeBasePage.clickTreeItemAction(
				editedTitle,
				'Permissions'
			);

			await expect(siteMemberViewCheckbox).not.toBeChecked();

			await siteMemberViewCheckbox.check();

			await permissionsFrame.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(permissionsFrame);

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.clickRowAction(editedTitle, 'Permissions');

			await expect(siteMemberViewCheckbox).toBeChecked();
		});
	}
);

test(
	'Articles can be moved and deleted from the navigation pane',
	{tag: ['@LPD-110921', '@LPS-156215']},
	async ({apiHelpers, knowledgeBasePage, page, site}) => {
		const folder =
			await apiHelpers.headlessDelivery.postSiteKnowledgeBaseFolder({
				name: getRandomString(),
				siteId: site.id,
			});

		const title = getRandomString();

		await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
			articleBody: getRandomString(),
			siteId: site.id,
			title,
		});

		await knowledgeBasePage.goto(site.friendlyUrlPath);

		await knowledgeBasePage.openFoldersAndArticlesPanel();

		await test.step('CanMoveArticleFromNavigationPane', async () => {
			await knowledgeBasePage.clickTreeItemAction(title, 'Move');

			await knowledgeBasePage.selectMoveTargetAndSave(title, folder.name);

			await waitForAlert(page);

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.openFoldersAndArticlesPanel();

			await clickAndExpectToBeVisible({
				target: knowledgeBasePage.getTreeItem(title),
				trigger: knowledgeBasePage
					.getTreeItem(folder.name)
					.getByRole('button', {exact: true, name: folder.name}),
			});

			await clickAndExpectToBeVisible({
				target: knowledgeBasePage.getRow(title),
				trigger: page.getByRole('link', {
					exact: true,
					name: folder.name,
				}),
			});
		});

		await test.step('CanDeleteArticleFromNavigationPane', async () => {
			await knowledgeBasePage.clickTreeItemAction(title, 'Delete');

			await expect(knowledgeBasePage.recycleBinAlert).toContainText(
				`The element ${title} was moved to the Recycle Bin.`
			);

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.openFoldersAndArticlesPanel();

			await expect(knowledgeBasePage.getTreeItem(title)).toHaveCount(0);

			await clickAndExpectToBeVisible({
				target: page.getByText('This folder is empty.', {
					exact: true,
				}),
				trigger: page.getByRole('link', {
					exact: true,
					name: folder.name,
				}),
			});
		});
	}
);
