/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../fixtures/loginTest';
import {searchPageTest} from '../../../fixtures/searchPageTest';

const ROBOTS_META_SELECTOR = 'meta[name="robots"][content="noindex, nofollow"]';

export const test = mergeTests(
	featureFlagsTest({
		'LPD-71164': {enabled: true},
	}),
	isolatedLayoutTest({type: 'portlet'}),
	loginTest(),
	searchPageTest
);

test.describe('Search Widget SEO', () => {
	test('Adds disallow entries to robots.txt when indexing is disabled @LPD-86136', async ({
		layout,
		page,
		searchPage,
	}) => {
		await test.step('Add Category Facet widget to the page', async () => {
			await page.goto('/web/guest' + layout.friendlyURL);

			await searchPage.addPortlet('Category Facet', 'Search');
		});

		await test.step('Disable web crawler indexing on the Category Facet widget', async () => {
			await searchPage.openSearchPortletConfiguration('Category Facet');

			await searchPage.selectPortletConfigurationsCheckbox([
				{
					label: 'Enable Web Crawler Indexing',
					value: false,
				},
			]);

			await searchPage.savePortletConfiguration();
		});

		await test.step('Fetch robots.txt and assert disallow entries for the page', async () => {
			const response = await page.request.get('/robots.txt');

			expect(response.ok()).toBe(true);

			const body = await response.text();

			expect(body).toContain(
				`Disallow: ${layout.friendlyURL}*?category=`
			);
			expect(body).toContain(
				`Disallow: ${layout.friendlyURL}*&category=`
			);
		});
	});

	test('Adds noindex robots meta tag when indexing is disabled and a category is selected @LPD-86136', async ({
		layout,
		page,
		searchPage,
	}) => {
		await test.step('Add Category Facet widget to the page', async () => {
			await page.goto('/web/guest' + layout.friendlyURL);

			await searchPage.addPortlet('Category Facet', 'Search');
		});

		await test.step('Disable web crawler indexing on the Category Facet widget', async () => {
			await searchPage.openSearchPortletConfiguration('Category Facet');

			await searchPage.selectPortletConfigurationsCheckbox([
				{
					label: 'Enable Web Crawler Indexing',
					value: false,
				},
			]);

			await searchPage.savePortletConfiguration();
		});

		await test.step('Visit the page without category param and assert no noindex meta', async () => {
			await page.goto('/web/guest' + layout.friendlyURL);

			await expect(page.locator(ROBOTS_META_SELECTOR)).toHaveCount(0);
		});

		await test.step('Visit the page with category param and assert noindex meta is present', async () => {
			await page.goto('/web/guest' + layout.friendlyURL + '?category=1');

			await expect(page.locator(ROBOTS_META_SELECTOR)).toHaveCount(1);
		});
	});

	test('Combines contributed parameters from multiple widgets in the canonical URL @LPD-86136', async ({
		browser,
		layout,
		page,
		searchPage,
	}) => {
		await test.step('Add Category Facet and Tag Facet widgets to the page', async () => {
			await page.goto('/web/guest' + layout.friendlyURL);

			await searchPage.addPortlet('Category Facet', 'Search');

			await searchPage.addPortlet('Tag Facet', 'Search');
		});

		await test.step('Visit the page as a guest with both params and assert the canonical URL keeps both', async () => {
			const guestContext = await browser.newContext();

			try {
				const guestPage = await guestContext.newPage();

				await guestPage.goto(
					'/web/guest' +
						layout.friendlyURL +
						'?category=shoes&tag=running'
				);

				const canonicalHref = await guestPage
					.locator('link[rel="canonical"]')
					.getAttribute('href');

				expect(canonicalHref).toContain('category=shoes');
				expect(canonicalHref).toContain('tag=running');
			}
			finally {
				await guestContext.close();
			}
		});
	});

	test('Does not add noindex robots meta tag when indexing is enabled @LPD-86136', async ({
		layout,
		page,
		searchPage,
	}) => {
		await test.step('Add Category Facet widget to the page', async () => {
			await page.goto('/web/guest' + layout.friendlyURL);

			await searchPage.addPortlet('Category Facet', 'Search');
		});

		await test.step('Visit the page with category param and assert no noindex meta', async () => {
			await page.goto('/web/guest' + layout.friendlyURL + '?category=1');

			await expect(page.locator(ROBOTS_META_SELECTOR)).toHaveCount(0);
		});
	});

	test('Does not contribute disallow entries to robots.txt when indexing is enabled @LPD-86136', async ({
		layout,
		page,
		searchPage,
	}) => {
		await test.step('Add Category Facet widget to the page', async () => {
			await page.goto('/web/guest' + layout.friendlyURL);

			await searchPage.addPortlet('Category Facet', 'Search');
		});

		await test.step('Fetch robots.txt and assert no disallow entries for the page', async () => {
			const response = await page.request.get('/robots.txt');

			expect(response.ok()).toBe(true);

			const body = await response.text();

			expect(body).not.toContain(layout.friendlyURL);
		});
	});

	test('Strips unrecognized query parameters from the canonical URL @LPD-86136', async ({
		browser,
		layout,
		page,
		searchPage,
	}) => {
		await test.step('Add Category Facet widget to the page', async () => {
			await page.goto('/web/guest' + layout.friendlyURL);

			await searchPage.addPortlet('Category Facet', 'Search');
		});

		await test.step('Visit the page as a guest with a recognized and an unrecognized param and assert the canonical URL keeps only the recognized one', async () => {
			const guestContext = await browser.newContext();

			try {
				const guestPage = await guestContext.newPage();

				await guestPage.goto(
					'/web/guest' +
						layout.friendlyURL +
						'?category=shoes&utm_source=email'
				);

				const canonicalHref = await guestPage
					.locator('link[rel="canonical"]')
					.getAttribute('href');

				expect(canonicalHref).toContain('category=shoes');
				expect(canonicalHref).not.toContain('utm_source');
			}
			finally {
				await guestContext.close();
			}
		});
	});
});
