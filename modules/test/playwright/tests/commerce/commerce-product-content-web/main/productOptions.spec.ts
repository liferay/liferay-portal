/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {classicCommerceSetUp} from '../../utils/commerce';

export const test = mergeTests(
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-20379': {enabled: true},
	}),
	loginTest()
);

test(
	'User can add to cart and checkout a product with upload option',
	{tag: ['@LPD-50964']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceMiniCartPage,
		commerceThemeClassicOrdersPage,
		page,
		productDetailsPage,
	}) => {
		test.setTimeout(90000);

		const {catalog, channel, site} = await classicCommerceSetUp(
			apiHelpers,
			`UploadOption${getRandomString()}`
		);

		await apiHelpers.headlessAdminUser.postAccount({
			name: 'admin',
			type: 'business',
		});

		const uploadOption =
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'document_library',
				'Upload',
				'Upload',
				1
			);

		const productWithUploadOption =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'ProductWithUploadOption'},
				productOptions: [
					{
						fieldType: 'document_library',
						key: 'upload',
						name: {
							en_US: 'Upload',
						},
						optionId: uploadOption.id,
						priceType: '',
						priority: 1,
					},
				],
				skus: [
					{
						cost: 0,
						price: 10,
						published: true,
						purchasable: true,
						sku: 'Sku' + getRandomInt(),
					},
				],
			});

		const productSku = (
			await apiHelpers.headlessCommerceDeliveryCatalog.getChannelProductSkusPage(
				channel.id,
				productWithUploadOption.productId,
				new URLSearchParams({
					nestedFields: `price`,
				})
			)
		).items[0];

		await page.goto(
			`/web/${site.name}/p/${productWithUploadOption.name['en_US']}`
		);

		await expect(page.getByText('UploadSelect')).toBeVisible();

		await productDetailsPage.optionSelector('Upload').click();

		await expect(
			page.getByRole('heading', {name: 'Select Document'})
		).toBeVisible();

		await productDetailsPage.selectDocumentFrame
			.getByRole('link', {name: 'Commerce'})
			.click();

		const fileName = '400_color.svg';

		await productDetailsPage.selectDocumentFrame
			.getByText(fileName)
			.click();

		await expect(productDetailsPage.selectedDocumentLabel).toHaveValue(
			fileName
		);

		try {
			await productDetailsPage.addToCartButton.click();

			await commerceMiniCartPage.miniCartButton.click();

			await expect(
				commerceMiniCartPage.miniCartItem(
					productWithUploadOption.name['en_US']
				)
			).toBeVisible();
			await expect(commerceMiniCartPage.miniCartTotalPrice).toHaveText(
				productSku.price.priceFormatted
			);

			await commerceMiniCartPage.submitButton.click();

			await checkoutPage.performCheckout({
				shippingAddress: {
					city: 'testCity',
					countryLabel: 'United States',
					name: 'John Doe',
					regionLabel: 'Florida',
					street: 'testStreet',
					zip: '12345',
				},
			});

			await checkoutPage.goToOrderDetailsButton.click();

			await expect(
				(
					await commerceThemeClassicOrdersPage.orderItemsTableRow(
						2,
						productWithUploadOption.productOptions[0].name['en_US']
					)
				).column.getByText(
					productWithUploadOption.productOptions[0].name['en_US']
				)
			).toBeVisible();
			await expect(
				(
					await commerceThemeClassicOrdersPage.orderItemsTableRow(
						2,
						fileName
					)
				).column.getByText(fileName)
			).toBeVisible();
			await expect(
				(
					await commerceThemeClassicOrdersPage.orderItemsTableRow(
						9,
						productSku.price.priceFormatted,
						true
					)
				).column.getByText(productSku.price.priceFormatted)
			).toBeVisible();
		}
		finally {
			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			apiHelpers.data.push({id: orders.items[0].id, type: 'order'});
		}
	}
);
