/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	journalPagesTest,
	changeTrackingPagesTest,
	workflowPagesTest
);

test(
	'Should not be able to approve workflow from production in publication',
	{tag: '@LPD-58247'},
	async ({
		apiHelpers,
		changeTrackingPage,
		ctCollection,
		journalEditArticlePage,
		page,
		workflowPage,
		workflowTasksPage,
	}) => {
		await workflowPage.goto();

		await workflowPage.changeWorkflow(
			'Web Content Article',
			'Single Approver'
		);

		await journalEditArticlePage.goto();

		const title = getRandomString();

		await journalEditArticlePage.submitArticleForWorkflow(title);

		await changeTrackingPage.workOnPublication(ctCollection);

		await workflowTasksPage.goToAssignedToMyRoles();

		const row = await page.getByRole('row').filter({hasText: title});

		await expect(row).toBeVisible();

		await expect(row.locator('.dropdown-toggle')).not.toBeVisible();

		await apiHelpers.headlessChangeTracking.checkoutCTCollection(0);

		await workflowPage.goto();

		await workflowPage.changeWorkflow(
			'Web Content Article',
			'No Workflow',
			{
				disable: true,
			}
		);
	}
);
