/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {Scan} from '../../../helpers/SEOStudioApiHelper';
import {SEO_STUDIO_FRIENDLY_URL} from './constants/site';
import {seoStudioPagesTest} from './fixtures/seoStudioPagesTest';

const test = mergeTests(loginTest(), dataApiHelpersTest, seoStudioPagesTest);

test(
	'Renders the "no scans yet" empty state on the On-Page screen',
	{tag: '@LPD-91406'},
	async ({onPagePage}) => {
		await onPagePage.goto(SEO_STUDIO_FRIENDLY_URL);

		await expect(onPagePage.onPageHeading).toBeVisible();

		await expect(onPagePage.lastScanLabel).toContainText(
			'Last Scan: Never'
		);

		await expect(onPagePage.runScanNowButton).toBeVisible();

		await onPagePage.emptyStateIsVisible('no-scans');
	}
);

test(
	'Renders the "no insights found" empty state when the latest scan has no insights',
	{tag: '@LPD-91406'},
	async ({apiHelpers, onPagePage}) => {
		const scan = await apiHelpers.seoStudio.createScan('crawler');

		try {
			await onPagePage.goto(SEO_STUDIO_FRIENDLY_URL);

			await onPagePage.emptyStateIsVisible('no-insights');
		}
		finally {
			await scan.teardown();
		}
	}
);

test(
	'Renders the Insights table rows when the latest scan has insights',
	{tag: '@LPD-91406'},
	async ({apiHelpers, onPagePage}) => {
		const insightTypeInputs = [
			{
				category: 'metadata',
				name: 'missingMetaDescription',
				pageURLs: [
					'https://example.com/a',
					'https://example.com/b',
					'https://example.com/c',
				],
				severity: '3',
			},
			{
				category: 'images',
				name: 'missingAltText',
				pageURLs: ['https://example.com/d', 'https://example.com/e'],
				severity: '2',
			},
			{
				category: 'linksAndURLs',
				name: 'brokenInternalLink',
				pageURLs: [
					'https://example.com/f',
					'https://example.com/g',
					'https://example.com/h',
					'https://example.com/i',
				],
				severity: '3',
			},
		];

		const scan = await apiHelpers.seoStudio.createScan('crawler');

		try {
			await apiHelpers.seoStudio.createInsights(scan, insightTypeInputs);

			await onPagePage.goto(SEO_STUDIO_FRIENDLY_URL);

			for (const insightTypeInput of insightTypeInputs) {
				const row = onPagePage.getInsightRow(insightTypeInput.name);

				await expect(row).toBeVisible();

				await expect(row).toContainText(
					String(insightTypeInput.pageURLs.length)
				);
			}
		}
		finally {
			await scan.teardown();
		}
	}
);

test.describe('Filter and Sort Insights tests', () => {
	let scan: Scan;

	test.beforeEach(async ({apiHelpers, onPagePage}) => {
		scan = await apiHelpers.seoStudio.createScan('crawler');

		await apiHelpers.seoStudio.createInsights(scan, [
			{
				category: 'images',
				name: 'missingAltTextOnImages',
				pageURLs: ['https://example.com/a'],
				severity: '3',
			},
			{
				category: 'images',
				name: 'altTextTooLong',
				pageURLs: ['https://example.com/b'],
				severity: '2',
			},
			{
				category: 'images',
				name: 'brokenImageURLs',
				pageURLs: ['https://example.com/c'],
				severity: '1',
			},
		]);

		await onPagePage.goto(SEO_STUDIO_FRIENDLY_URL);
	});

	test.afterEach(async () => {
		await scan.teardown();
	});

	test(
		'Sorts the insights by impact from high to low by default',
		{tag: '@LPD-91408'},
		async ({onPagePage}) => {
			await expect(
				onPagePage.getInsightRow('Missing Alt Text on Images')
			).toBeVisible();

			await expect(async () => {
				expect(await onPagePage.getInsightNamesInOrder()).toEqual([
					'Missing Alt Text on Images',
					'Alt Text Too Long (>125 chars)',
					'Broken Image URLs (404s)',
				]);
			}).toPass({timeout: 10000});
		}
	);

	test(
		'Sorts the insights by a column header and reflects the sort in the URL',
		{tag: '@LPD-91408'},
		async ({onPagePage, page}) => {
			await expect(
				onPagePage.getInsightRow('Missing Alt Text on Images')
			).toBeVisible();

			await onPagePage.sortByColumn('Impact');

			await expect(page).toHaveURL(/fdsConfig/);

			await expect(async () => {
				expect(await onPagePage.getInsightNamesInOrder()).toEqual([
					'Broken Image URLs (404s)',
					'Alt Text Too Long (>125 chars)',
					'Missing Alt Text on Images',
				]);
			}).toPass({timeout: 10000});

			await onPagePage.sortByColumn('Impact');

			await expect(async () => {
				expect(await onPagePage.getInsightNamesInOrder()).toEqual([
					'Missing Alt Text on Images',
					'Alt Text Too Long (>125 chars)',
					'Broken Image URLs (404s)',
				]);
			}).toPass({timeout: 10000});
		}
	);

	test(
		'Filters the insights by impact and reflects the filter in the URL',
		{tag: '@LPD-91408'},
		async ({onPagePage, page}) => {
			await onPagePage.applyFilter('Impact', ['High']);

			await expect(
				onPagePage.activeFilterChip('Impact: High')
			).toBeVisible();

			await expect(
				onPagePage.getInsightRow('Missing Alt Text on Images')
			).toBeVisible();
			await expect(
				onPagePage.getInsightRow('Broken Image URLs (404s)')
			).not.toBeVisible();
			await expect(
				onPagePage.getInsightRow('Alt Text Too Long (>125 chars)')
			).not.toBeVisible();

			await expect(page).toHaveURL(/fdsConfig/);
		}
	);

	test(
		'Shows the filtered empty state when no insights match the filters',
		{tag: '@LPD-91408'},
		async ({onPagePage}) => {
			await onPagePage.applyFilter('Category', ['AEO Readiness']);

			await expect(onPagePage.filteredEmptyStateTitle).toBeVisible();
		}
	);

	test(
		'Preserves the active filter when returning via the insight detail breadcrumb',
		{tag: '@LPD-91408'},
		async ({insightDetailPage, onPagePage, page}) => {
			await onPagePage.applyFilter('Impact', ['High']);

			await expect(
				onPagePage.activeFilterChip('Impact: High')
			).toBeVisible();
			await expect(
				onPagePage.getInsightRow('Broken Image URLs (404s)')
			).not.toBeVisible();

			await onPagePage.selectInsight('Missing Alt Text on Images');

			await expect(page).toHaveURL(/objectEntryExternalReferenceCode=/);

			await insightDetailPage.onPageBreadcrumbLink.click();

			await expect(onPagePage.onPageHeading).toBeVisible();

			await expect(
				onPagePage.activeFilterChip('Impact: High')
			).toBeVisible();
			await expect(
				onPagePage.getInsightRow('Broken Image URLs (404s)')
			).not.toBeVisible();
		}
	);
});
