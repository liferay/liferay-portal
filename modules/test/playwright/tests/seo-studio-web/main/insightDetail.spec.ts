/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {InsightType, PageData, Scan} from '../../../helpers/SEOStudioApiHelper';
import getRandomString from '../../../utils/getRandomString';
import {SEO_STUDIO_FRIENDLY_URL} from './constants/site';
import {seoStudioPagesTest} from './fixtures/seoStudioPagesTest';

const test = mergeTests(loginTest(), dataApiHelpersTest, seoStudioPagesTest);

let insightType: InsightType;
let insightTypeInput: InsightType & {pageURLs: PageData[]};
let scan: Scan;

test.beforeEach(async ({apiHelpers, onPagePage}) => {
	insightTypeInput = {
		category: 'metadata',
		description: 'Pages without meta description.',
		fixHint: 'Add a unique meta description to each page.',
		name: 'missingMetaDescription',
		pageURLs: [
			{
				author: 'Alice',
				pageURL: 'https://example.com/alpha',
				title: 'Alpha',
				type: 'Web Content',
			},
			{
				author: 'Bob',
				pageURL: 'https://example.com/beta',
				title: 'Beta',
				type: 'Document',
			},
		],
		severity: '3',
	};

	scan = await apiHelpers.seoStudio.createScan('crawler');

	[insightType] = await apiHelpers.seoStudio.createInsights(scan, [
		insightTypeInput,
	]);

	await onPagePage.goto(SEO_STUDIO_FRIENDLY_URL);
});

test.afterEach(async () => {
	await scan.teardown();
});

test(
	'Renders the breadcrumb, title, and content sections on the insight detail screen',
	{tag: '@LPD-91182'},
	async ({insightDetailPage, onPagePage, page}) => {
		await onPagePage.selectInsight(insightTypeInput.name);

		await expect(insightDetailPage.onPageBreadcrumbLink).toBeVisible();

		await expect(
			page.getByRole('heading', {
				level: 2,
				name: `${insightTypeInput.name} from ${insightTypeInput.pageURLs.length} pages`,
			})
		).toBeVisible();

		await expect(insightDetailPage.descriptionSectionTitle).toBeVisible();
		await expect(
			page.getByText(insightTypeInput.description)
		).toBeVisible();

		await expect(insightDetailPage.suggestionSectionTitle).toBeVisible();
		await expect(page.getByText(insightTypeInput.fixHint)).toBeVisible();
	}
);

test(
	'Renders the affected pages table with one row per scan insight',
	{tag: '@LPD-91182'},
	async ({insightDetailPage, onPagePage}) => {
		await onPagePage.selectInsight(insightTypeInput.name);

		await expect(insightDetailPage.affectedPagesHeading).toContainText(
			`(${insightTypeInput.pageURLs.length})`
		);

		for (const pageInput of insightTypeInput.pageURLs) {
			const row = insightDetailPage.getAffectedPageRow(pageInput.title);

			await expect(row).toBeVisible();
			await expect(row).toContainText(pageInput.author);
			await expect(row).toContainText(pageInput.type);
			await expect(row).toContainText(pageInput.pageURL);
		}

		await expect(insightDetailPage.getTitleHeader()).toBeVisible();
		await expect(insightDetailPage.getTypeHeader()).toBeVisible();
	}
);

test(
	'Navigates back to the On-Page screen from the breadcrumb',
	{tag: '@LPD-91182'},
	async ({insightDetailPage, onPagePage}) => {
		await onPagePage.selectInsight(insightTypeInput.name);

		await expect(insightDetailPage.onPageBreadcrumbLink).toBeVisible();

		await insightDetailPage.onPageBreadcrumbLink.click();

		await expect(onPagePage.onPageHeading).toBeVisible();
	}
);

test(
	'Lists only pending pages and excludes fixed pages from the affected pages table and count',
	{tag: '@LPD-95129'},
	async ({apiHelpers, insightDetailPage, onPagePage, page}) => {
		const fixedPages: PageData[] = [
			{
				author: getRandomString(),
				pageURL: `https://example.com/${getRandomString()}`,
				state: 0,
				title: 'FixedOne',
				type: getRandomString(),
			},
		];

		await apiHelpers.seoStudio.addPages(scan, insightType.id, fixedPages);

		await onPagePage.goto(SEO_STUDIO_FRIENDLY_URL);

		await onPagePage.selectInsight(insightTypeInput.name);

		await expect(insightDetailPage.affectedPagesHeading).toContainText(
			`(${insightTypeInput.pageURLs.length})`
		);

		await expect(
			page.getByRole('heading', {
				level: 2,
				name: `${insightTypeInput.name} from ${insightTypeInput.pageURLs.length} pages`,
			})
		).toBeVisible();

		for (const pageInput of insightTypeInput.pageURLs) {
			await expect(
				insightDetailPage.getAffectedPageRow(pageInput.title)
			).toBeVisible();
		}

		for (const pageInput of fixedPages) {
			await expect(
				insightDetailPage.getAffectedPageRow(pageInput.title)
			).not.toBeVisible();
		}
	}
);
