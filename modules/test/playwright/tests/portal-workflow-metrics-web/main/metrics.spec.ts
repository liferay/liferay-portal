/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	loginTest(),
	workflowPagesTest
);

test.beforeEach(async ({apiHelpers, configurationTabPage, page}) => {
	test.slow();
	await configurationTabPage.goTo();

	await page.getByLabel('Items per Page').click();

	await page.getByRole('option').filter({hasText: '40'}).click();

	await configurationTabPage.assignWorkflowToAssetType(
		'Single Approver',
		'Web Content Article'
	);

	const basicWebContentStructureId =
		await getBasicWebContentStructureId(apiHelpers);

	const site = await apiHelpers.headlessSite.getSiteByERC('L_GUEST');

	for (let i = 1; i <= 21; i++) {
		const webContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: `Web content ${i}`},
			});

		apiHelpers.data.push({
			id: `${site.id}_${webContent.articleId}`,
			type: 'webContent',
		});
	}
});

test('Workflow metrics', async ({metricsPage, page}) => {
	await test.step('displays the correct number of entries based on the selected entries per page option', async () => {
		await metricsPage.goTo();

		await page
			.getByRole('cell', {name: 'Single Approver'})
			.getByRole('link')
			.click();

		await page
			.getByRole('link')
			.filter({hasText: 'Total Pending'})
			.first()
			.click();

		await expect(
			page.getByRole('row').filter({hasText: 'Web content'})
		).toHaveCount(20);

		await page.getByLabel('Go to the next page').click();

		await expect(
			page.getByRole('row').filter({hasText: 'Web content'})
		).toHaveCount(1);
	});

	await test.step('preserves ascending Creation Date sorting when the user changes the pagination', async () => {
		await page.getByLabel('Items per Page').click();

		await page.getByRole('option').filter({hasText: '40'}).click();

		await page.getByRole('link', {name: 'Creation Date'}).dblclick();

		for (let i = 1; i <= 21; i++) {
			await expect(
				page
					.getByRole('cell', {
						exact: true,
						name: `Web Content Article: Web content ${i}`,
					})
					.last()
			).toBeVisible();
		}
	});
});
