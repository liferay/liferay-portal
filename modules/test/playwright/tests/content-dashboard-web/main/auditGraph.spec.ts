/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import addGlobalCategories from '../../../utils/addGlobalCategories';
import getRandomString from '../../../utils/getRandomString';
import {contentDashboardPagesTest} from './fixtures/contentDashboardPagesTest';

const test = mergeTests(
	contentDashboardPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'The audit graph shows the categories of the assets and filters the table by the selected bar',
	{tag: '@LPD-112477'},
	async ({apiHelpers, contentDashboardPage, page, site}) => {
		const [[audienceCategory], [stageCategory]] = await Promise.all([
			addGlobalCategories({
				apiHelpers,
				names: [getRandomString().slice(0, 8)],
				vocabularyName: 'Audience',
			}),
			addGlobalCategories({
				apiHelpers,
				names: [getRandomString().slice(0, 8)],
				vocabularyName: 'Stage',
			}),
		]);

		const categorizedBlogPosting =
			await apiHelpers.headlessDelivery.postBlog(site.id, {
				taxonomyCategoryIds: [audienceCategory.id, stageCategory.id],
			});

		const uncategorizedBlogPosting =
			await apiHelpers.headlessDelivery.postBlog(site.id);

		await contentDashboardPage.goto(undefined, {scopeId: site.id});

		await expect(
			page
				.locator('.xAxis .recharts-text')
				.filter({hasText: audienceCategory.name})
		).toBeVisible();

		await expect(
			contentDashboardPage.getRow(uncategorizedBlogPosting.headline)
		).toBeVisible();

		await page.locator('.recharts-bar-rectangle path').first().click();

		await expect(
			contentDashboardPage.getRow(categorizedBlogPosting.headline)
		).toBeVisible();
		await expect(
			contentDashboardPage.getRow(uncategorizedBlogPosting.headline)
		).toHaveCount(0);
	}
);
