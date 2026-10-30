/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {knowledgeBasePages} from '../../../fixtures/knowledgeBasePagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {KB_WORKFLOW_ACTION} from '../../../helpers/json-web-services/JSONWebServicesKnowledgeBaseApiHelper';
import {RecycleBinPage} from '../../../pages/trash-web/RecycleBinPage';
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
	'Articles and folders can be restored from the Recycle Bin',
	{tag: ['@LPD-110921', '@LPS-188061']},
	async ({apiHelpers, knowledgeBasePage, page, site}) => {
		const title = getRandomString();

		const knowledgeBaseArticle =
			await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
				articleBody: getRandomString(),
				siteId: site.id,
				title,
			});

		const childTitle = getRandomString();

		await apiHelpers.headlessDelivery.postKnowledgeBaseArticleKnowledgeBaseArticle(
			{
				articleBody: getRandomString(),
				parentKnowledgeBaseArticleId: knowledgeBaseArticle.id,
				title: childTitle,
			}
		);

		const childArticlesLink = knowledgeBasePage
			.getRow(title)
			.getByRole('link', {name: '1 Child Articles'});

		await knowledgeBasePage.goto(site.friendlyUrlPath);

		const recycleBinPage = new RecycleBinPage(page);

		await test.step('CanRestoreArticleFromRecycleBin', async () => {
			await knowledgeBasePage.clickRowAction(title, 'Delete');

			await expect(knowledgeBasePage.recycleBinAlert).toContainText(
				`The element ${title} was moved to the Recycle Bin.`
			);
			await expect(knowledgeBasePage.emptyStateTitle).toBeVisible();

			await knowledgeBasePage.recycleBinAlert
				.getByRole('link', {name: 'Recycle Bin'})
				.click();

			await recycleBinPage.restore(title);

			await waitForAlert(page, 'was restored');

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await clickAndExpectToBeVisible({
				target: knowledgeBasePage.getRow(childTitle),
				trigger: childArticlesLink,
			});
		});

		await test.step('CanRestoreChildArticleFromRecycleBin', async () => {
			await knowledgeBasePage.clickRowAction(childTitle, 'Delete');

			await expect(knowledgeBasePage.recycleBinAlert).toContainText(
				`The element ${childTitle} was moved to the Recycle Bin.`
			);

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await expect(knowledgeBasePage.getRow(title)).toBeVisible();
			await expect(childArticlesLink).toHaveCount(0);

			await recycleBinPage.goto(site.friendlyUrlPath);

			await recycleBinPage.restore(childTitle);

			await waitForAlert(page, 'was restored');

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await clickAndExpectToBeVisible({
				target: knowledgeBasePage.getRow(childTitle),
				trigger: childArticlesLink,
			});
		});

		await test.step('CanRestoreFolderWithContentsFromRecycleBin', async () => {
			const folder =
				await apiHelpers.headlessDelivery.postSiteKnowledgeBaseFolder({
					name: getRandomString(),
					siteId: site.id,
				});

			const folderArticleTitle = getRandomString();

			await apiHelpers.headlessDelivery.postKnowledgeBaseFolderKnowledgeBaseArticle(
				{
					articleBody: getRandomString(),
					knowledgeBaseFolderId: folder.id,
					title: folderArticleTitle,
				}
			);

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.clickRowAction(folder.name, 'Delete');

			await expect(knowledgeBasePage.recycleBinAlert).toContainText(
				`The element ${folder.name} was moved to the Recycle Bin.`
			);
			await expect(knowledgeBasePage.getRow(folder.name)).toHaveCount(0);

			await recycleBinPage.goto(site.friendlyUrlPath);

			await recycleBinPage.restore(folder.name);

			await waitForAlert(page, 'was restored');

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await clickAndExpectToBeVisible({
				target: knowledgeBasePage.getRow(folderArticleTitle),
				trigger: page.getByRole('link', {
					exact: true,
					name: folder.name,
				}),
			});
		});
	}
);

test(
	'Deleted articles can be restored with the undo action',
	{tag: ['@LPD-110921', '@LPS-188061']},
	async ({apiHelpers, knowledgeBasePage, page, site}) => {
		const draftTitle = getRandomString();

		await apiHelpers.jsonWebServicesKnowledgeBase.addKBArticle({
			groupId: site.id,
			title: draftTitle,
			workflowAction: KB_WORKFLOW_ACTION.SAVE_DRAFT,
		});

		await knowledgeBasePage.goto(site.friendlyUrlPath);

		const undoButton = knowledgeBasePage.recycleBinAlert.getByRole(
			'button',
			{name: 'Undo'}
		);

		await test.step('CanRestoreDraftArticleFromRecycleBin', async () => {
			await knowledgeBasePage.clickRowAction(draftTitle, 'Delete');

			await expect(knowledgeBasePage.emptyStateTitle).toBeVisible();

			await undoButton.click();

			await expect(knowledgeBasePage.getRow(draftTitle)).toContainText(
				'Draft'
			);
		});

		await test.step('CanRestoreMultipleArticlesFromRecycleBin', async () => {
			const titles = [getRandomString(), getRandomString()];

			for (const title of titles) {
				await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
					articleBody: getRandomString(),
					siteId: site.id,
					title,
				});
			}

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page
					.locator('.management-bar')
					.getByRole('button', {name: 'Delete'}),
				trigger: knowledgeBasePage.selectAllCheckBox,
			});

			await expect(knowledgeBasePage.recycleBinAlert).toContainText(
				'3 items were moved to the Recycle Bin.'
			);
			await expect(knowledgeBasePage.emptyStateTitle).toBeVisible();

			await undoButton.click();

			for (const title of [...titles, draftTitle]) {
				await expect(knowledgeBasePage.getRow(title)).toBeVisible();
			}
		});
	}
);
