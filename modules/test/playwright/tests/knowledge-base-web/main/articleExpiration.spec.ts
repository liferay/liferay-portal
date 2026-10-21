/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {knowledgeBasePages} from '../../../fixtures/knowledgeBasePagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	knowledgeBasePages,
	loginTest(),
	featureFlagsTest({
		'LPD-39304': {enabled: true},
	})
);

test(
	'Expired articles are hidden from the Knowledge Base Display and Asset Publisher widgets',
	{tag: '@LPS-161758'},
	async ({apiHelpers, knowledgeBasePage, page, site}) => {
		const content = getRandomString();
		const title = getRandomString();

		await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
			articleBody: content,
			siteId: site.id,
			title,
		});

		const widgetPageURLs = [];

		for (const portletId of [
			`com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet_INSTANCE_${getRandomInt()}`,
			'com_liferay_knowledge_base_web_portlet_DisplayPortlet',
		]) {
			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: getRandomString(),
				typeSettings: `layout-template-id=1_column\ncolumn-1=${portletId}\n`,
			});

			widgetPageURLs.push(
				`/web${site.friendlyUrlPath}${layout.friendlyURL}`
			);
		}

		const [assetPublisherURL, displayURL] = widgetPageURLs;

		await test.step('CanExpireAnArticle', async () => {
			await page.goto(displayURL);

			await expect(
				page.getByRole('heading', {name: title})
			).toBeVisible();
			await expect(page.getByText(content)).toBeVisible();

			await page.goto(assetPublisherURL);

			await expect(page.getByText(title)).toBeVisible();

			await knowledgeBasePage.goto(site.friendlyUrlPath);

			await knowledgeBasePage.clickRowAction(title, 'Expire');

			await expect(knowledgeBasePage.getRow(title)).toContainText(
				'Expired'
			);

			await clickAndExpectToBeVisible({
				target: page.getByRole('menu'),
				trigger: knowledgeBasePage
					.getRow(title)
					.getByLabel('Show Actions'),
			});

			await expect(
				page.getByRole('menuitem', {exact: true, name: 'Expire'})
			).toHaveCount(0);

			await page.goto(displayURL);

			await expect(
				page.getByText(
					'Please configure this portlet to make it visible to all users.'
				)
			).toBeVisible();

			await page.goto(assetPublisherURL);

			await expect(page.getByText(title)).toHaveCount(0);
		});
	}
);

test(
	'Approved articles close to their expiration date show the Expiring Soon label',
	{tag: ['@LPS-161758', '@LPS-184220']},
	async ({apiHelpers, knowledgeBasePage, site}) => {
		const workflowDefinition =
			await apiHelpers.headlessAdminWorkflow.getWorkflowDefinitionByName(
				'Single Approver'
			);

		const expirationDate = new Date(Date.now() + 24 * 60 * 60 * 1000);

		const approvedTitle = getRandomString();

		await apiHelpers.jsonWebServicesKnowledgeBase.addKBArticle({
			expirationDate,
			groupId: site.id,
			title: approvedTitle,
		});

		await apiHelpers.headlessAdminWorkflow.postWorkflowDefinitionLink(
			'com.liferay.knowledge.base.model.KBArticle',
			Number(site.id),
			workflowDefinition.id,
			workflowDefinition.name,
			Number(workflowDefinition.version)
		);

		const pendingTitle = getRandomString();

		const pendingKBArticle =
			await apiHelpers.jsonWebServicesKnowledgeBase.addKBArticle({
				expirationDate,
				groupId: site.id,
				title: pendingTitle,
			});

		await knowledgeBasePage.goto(site.friendlyUrlPath);

		await test.step('CanViewExpiredArticle', async () => {
			await expect(knowledgeBasePage.getRow(approvedTitle)).toContainText(
				'Approved'
			);
			await expect(knowledgeBasePage.getRow(approvedTitle)).toContainText(
				'Expiring Soon'
			);
		});

		await test.step('CanViewExpiredArticleWithWorkflowEnabled', async () => {
			const pendingRow = knowledgeBasePage.getRow(pendingTitle);

			await expect(pendingRow).toContainText('Pending');
			await expect(pendingRow).not.toContainText('Expiring Soon');

			let workflowTask: WorkflowTaskDefinition;

			await expect(async () => {
				workflowTask =
					await apiHelpers.headlessAdminWorkflow.getWorkflowTaskByAsset(
						'com.liferay.knowledge.base.model.KBArticle',
						pendingKBArticle.resourcePrimKey
					);

				expect(workflowTask).toBeTruthy();
			}).toPass();

			const myUserAccount =
				await apiHelpers.headlessAdminUser.getMyUserAccount();

			await apiHelpers.headlessAdminWorkflow.postAssignTaskToUser(
				workflowTask.id,
				Number(myUserAccount.id)
			);

			await apiHelpers.headlessAdminWorkflow.postWorkflowTaskChangeTransition(
				workflowTask.id,
				'approve'
			);

			await expect(async () => {
				await knowledgeBasePage.goto(site.friendlyUrlPath);

				await expect(pendingRow).toContainText('Approved', {
					timeout: 2000,
				});
			}).toPass();

			await expect(pendingRow).toContainText('Expiring Soon');
		});
	}
);
