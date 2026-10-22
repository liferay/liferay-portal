/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {miniumSetUp, speedwellSetUp} from '../../utils/commerce';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-10562': {enabled: true},
	}),
	loginTest()
);

test(
	'The Minium initializer creates a storefront with its pages and navigation menu',
	{tag: '@LPD-108862'},
	async ({apiHelpers, commerceThemeMiniumCatalogPage, page}) => {
		const {site} = await miniumSetUp(apiHelpers);

		const miniumFrame = page.locator('.minium-frame');

		await test.step('Verify the catalog renders with the Minium theme', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/catalog`);

			await expect(miniumFrame).toBeVisible();
			await expect(
				page.getByText('50 Products Available', {exact: true})
			).toBeVisible();
			await expect(
				commerceThemeMiniumCatalogPage.productCard('U-Joint')
			).toBeVisible();
		});

		const navigation = page.locator('.minium-main-navigation');

		await test.step('Verify the navigation menu links every storefront page', async () => {
			for (const name of [
				'Account Management',
				'Catalog',
				'Pending Orders',
				'Placed Orders',
				'Returns',
			]) {
				await expect(
					navigation.getByRole('link', {exact: true, name})
				).toBeVisible();
			}
		});

		await test.step('Verify the Returns page renders the returns list', async () => {
			await navigation
				.getByRole('link', {exact: true, name: 'Returns'})
				.click();

			await expect(miniumFrame).toBeVisible();
			await expect(
				page.getByText('No Results Found', {exact: true})
			).toBeVisible();
		});
	}
);

test(
	'The Speedwell initializer creates a storefront with its pages and navigation menu',
	{tag: '@LPD-108862'},
	async ({
		apiHelpers,
		commerceCartSummaryPage,
		commerceThemeMiniumCatalogPage,
		page,
		productComparisonPage,
		productDetailsPage,
	}) => {
		const {site} = await speedwellSetUp(apiHelpers);

		const speedwellFrame = page.locator('.speedwell-frame').first();

		await test.step('Verify the Home page renders with the Speedwell theme', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/home`);

			await expect(speedwellFrame).toBeVisible();
			await expect(
				page.getByRole('heading', {exact: true, name: 'Free Shipping'})
			).toBeVisible();
		});

		await test.step('Verify the catalog renders with the Speedwell theme', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/catalog`);

			await expect(speedwellFrame).toBeVisible();
			await expect(
				page.getByText('50 Products Available', {exact: true})
			).toBeVisible();
			await expect(
				commerceThemeMiniumCatalogPage.productCard('U-Joint')
			).toBeVisible();
		});

		await test.step('Verify the Compare page renders the compared products', async () => {
			for (const productName of [
				'Transmission Cooler Line Assembly',
				'U-Joint',
			]) {
				const compareCheckbox =
					commerceThemeMiniumCatalogPage.productCardCompareCheckbox(
						productName
					);

				await compareCheckbox.click();

				await expect(compareCheckbox).toBeChecked();
			}

			await productComparisonPage.compareBarLink.click();

			await expect(speedwellFrame).toBeVisible();
			await expect(
				productComparisonPage.comparisonTableProductLink('U-Joint')
			).toBeVisible();
			await expect(
				productComparisonPage.comparisonTableProductLink(
					'Transmission Cooler Line Assembly'
				)
			).toBeVisible();
		});

		await test.step('Verify the Product Detail page renders the product', async () => {
			await productComparisonPage
				.comparisonTableProductLink('U-Joint')
				.click();

			await expect(speedwellFrame).toBeVisible();
			await expect(
				await productDetailsPage.productNameHeading('U-Joint')
			).toBeVisible();
		});

		await test.step('Verify the navigation menu links every storefront page', async () => {
			const mainMenuLinks = page.locator('.speedwell-main-menu__links');

			await clickAndExpectToBeVisible({
				target: mainMenuLinks.getByRole('link', {
					exact: true,
					name: 'Catalog',
				}),
				trigger: page.locator('.speedwell-main-menu__open'),
			});

			for (const name of ['Catalog', 'Home', 'My Account', 'My Cart']) {
				await expect(
					mainMenuLinks.getByRole('link', {exact: true, name})
				).toBeVisible();
			}
		});

		await test.step('Verify the My Account page renders the account dashboard', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/my-account`);

			await expect(speedwellFrame).toBeVisible();
			await expect(
				page.getByRole('heading', {exact: true, name: 'My Orders'})
			).toBeVisible();
		});

		await test.step('Verify the My Cart page renders the cart summary', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/my-cart`);

			await expect(speedwellFrame).toBeVisible();
			await expect(commerceCartSummaryPage.checkoutButton).toBeVisible();
		});
	}
);
