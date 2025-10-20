/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {journalPagesTest} from './fixtures/journalPagesTest';

const scheduleTest = mergeTests(
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	workflowPagesTest
);

scheduleTest(
	'Change permission of a web content in edition mode',
	async ({journalEditArticlePage, journalPage, page, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const title = getRandomString();

		await journalEditArticlePage.fillTitle(title);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'Publish With Permissions',
			}),
			trigger: page.getByRole('button', {
				name: 'Select and Confirm Publish Settings',
			}),
		});

		await page.getByRole('button', {exact: true, name: 'Publish'}).click();

		await waitForAlert(page, `Success:${title} was created successfully.`);

		await journalPage.changeView('list');

		await page.getByLabel(`Actions for ${title}`).waitFor();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				exact: true,
				name: 'Edit',
			}),
			trigger: page.getByLabel(`Actions for ${title}`, {
				exact: true,
			}),
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'Permissions',
			}),
			trigger: page.getByRole('button', {
				name: 'Options',
			}),
		});

		await journalPage.setPermissions(['#power-user_ACTION_DELETE']);

		await journalPage.goto(site.friendlyUrlPath);

		await journalPage.assertJournalArticlePermissions(title, [
			{enabled: true, locator: '#power-user_ACTION_DELETE'},
		]);
	}
);

scheduleTest(
	'Create a web content scheduled',
	async ({journalEditArticlePage, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const articleTitle = getRandomString();
		const expirationDate = '01/01/9999';
		const publishDate = '9987-11-26 13:35';
		const reviewDate = '01/01/9999';

		await journalEditArticlePage.scheduleArticle(
			articleTitle,
			publishDate,
			undefined,
			expirationDate,
			reviewDate
		);

		await journalEditArticlePage.assertScheduledArticleDates(
			articleTitle,
			publishDate,
			undefined,
			expirationDate,
			reviewDate
		);
	}
);

scheduleTest(
	'Create a web content scheduled with workflow activated',
	async ({
		journalEditArticlePage,
		journalPage,
		site,
		workflowPage,
		workflowTasksPage,
	}) => {
		await workflowPage.goto(site.friendlyUrlPath);

		await workflowPage.changeWorkflow(
			'Web Content Article',
			'Single Approver'
		);

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const articleTitle = getRandomString();
		const articleDate = '9987-11-26 13:00';

		await journalEditArticlePage.scheduleArticle(
			articleTitle,
			articleDate,
			{workflow: true}
		);

		await workflowTasksPage.goToAssignedToMyRoles(site.friendlyUrlPath);

		await workflowTasksPage.assignToMe(articleTitle);

		await workflowTasksPage.approve(articleTitle);

		await journalPage.goto(site.friendlyUrlPath);

		await journalEditArticlePage.assertScheduledArticleDates(
			articleTitle,
			articleDate,
			{workflow: true}
		);
	}
);

scheduleTest(
	'Web content can be saved as draft',
	{
		tag: '@LPD-37754',
	},
	async ({journalEditArticlePage, journalPage, page, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const articleTitle = 'Web Content Draft Title';

		await journalEditArticlePage.saveAsDraftWithPermissions(articleTitle);

		await journalPage.goto(site.friendlyUrlPath);

		await expect(
			page.getByRole('link', {exact: true, name: articleTitle})
		).toBeVisible();

		await expect(
			page.locator(
				'[id="_com_liferay_journal_web_portlet_JournalPortlet_articles_1"] span.label-item'
			)
		).toHaveText('Draft');
	}
);
