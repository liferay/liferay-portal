/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../../fixtures/pageViewModePagesTest';
import {workflowPagesTest} from '../../../../fixtures/workflowPagesTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {WidgetPagePage} from '../../../../pages/layout-admin-web/WidgetPagePage';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitch,
	userData,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {createAccountWithBuyerUser, miniumSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest(),
	pageViewModePagesTest,
	workflowPagesTest
);

let catalog: {id: number};
let channel: {id: number; name: string};
let setupData: Array<{id: number | string; type: string}>;
let site: Site;
let sku1: number;
let sku2: number;
let uJointProductId: number;

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const miniumResult = await miniumSetUp(apiHelpers);

	catalog = miniumResult.catalog;
	channel = miniumResult.channel;
	site = miniumResult.site;
	setupData = [...apiHelpers.data];

	const products =
		await apiHelpers.headlessCommerceAdminCatalog.getProductsPage(100, '');

	let product = products.items.find(
		(item: {name: {en_US: string}}) => item.name.en_US === 'ABS Sensor'
	);

	sku1 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProduct(
			product.productId
		)
	).skus[0].id;

	product = products.items.find(
		(item: {name: {en_US: string}}) => item.name.en_US === 'U-Joint'
	);

	uJointProductId = product.productId;

	sku2 = (
		await apiHelpers.headlessCommerceAdminCatalog.getProduct(
			product.productId
		)
	).skus[0].id;

	await page.goto(`/web${site.friendlyUrlPath}/pending-orders`);

	const widgetPagePage = new WidgetPagePage(page);

	await widgetPagePage.addPortlet('Coupon Code Entry');

	await page.close();
});

test.afterAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	apiHelpers.setData(setupData);

	await apiHelpers.clearData();

	await page.close();
});

test.afterEach(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	const baselineChannelIds = new Set(
		setupData
			.filter((entry) => entry.type === 'channel')
			.map((entry) => entry.id)
	);

	const channelsPage =
		await apiHelpers.headlessCommerceAdminChannel.getChannelsPage('');

	await Promise.all(
		(channelsPage.items ?? [])
			.filter((c: {id: number}) => !baselineChannelIds.has(c.id))
			.map((c: {id: number}) =>
				apiHelpers.headlessCommerceAdminChannel.deleteChannel(c.id)
			)
	);

	const orderTypesPage =
		await apiHelpers.headlessCommerceAdminOrder.getOrderTypesPage();

	await Promise.all(
		(orderTypesPage.items ?? []).map((orderType: {id: number}) =>
			apiHelpers.headlessCommerceAdminOrder.deleteOrderTypes(orderType.id)
		)
	);

	const discountsPage = await apiHelpers.get(
		`${apiHelpers.baseUrl}headless-commerce-admin-pricing/v2.0/discounts?pageSize=200`
	);

	await Promise.all(
		(discountsPage.items ?? []).map((discount: {id: number}) =>
			apiHelpers.headlessCommerceAdminPricing.deleteDiscount(discount.id)
		)
	);

	await page.close();
});

test(
	'Create a new discount via the admin UI',
	{tag: ['@LPD-85008']},
	async ({
		apiHelpers,
		commerceAdminDiscountDetailsPage,
		commerceAdminDiscountsPage,
		page,
	}) => {
		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: `Test Channel ${getRandomString()}`,
				siteGroupId: 0,
			});
		const discountName = `Test Discount ${getRandomString()}`;

		await test.step('Open Discounts admin and submit the Add Discount modal', async () => {
			await commerceAdminDiscountsPage.goto();

			await commerceAdminDiscountsPage.addDiscountButton.click();

			await expect(async () => {
				await commerceAdminDiscountsPage.addDiscountModalNameInput.fill(
					discountName
				);

				await expect(
					commerceAdminDiscountsPage.addDiscountModalNameInput
				).toHaveValue(discountName, {timeout: 1000});
			}).toPass({timeout: 10000});

			await commerceAdminDiscountsPage.addDiscountModalTypeSelect.selectOption(
				{label: 'Percentage'}
			);
			await commerceAdminDiscountsPage.addDiscountModalApplyToSelect.selectOption(
				{label: 'Products'}
			);
			await commerceAdminDiscountsPage.addDiscountModalSubmitButton.click();
		});

		await test.step('Fill amount and maximum, then publish', async () => {
			await commerceAdminDiscountDetailsPage.amountInput.fill('15');
			await commerceAdminDiscountDetailsPage.maximumDiscountAmountInput.fill(
				'10'
			);
			await commerceAdminDiscountDetailsPage.publishButton.click();

			await waitForAlert(page);
		});

		await test.step('Set channel eligibility', async () => {
			await commerceAdminDiscountDetailsPage.eligibilityTab.click();
			await commerceAdminDiscountDetailsPage.specificChannelsRadio.check();
			await commerceAdminDiscountDetailsPage.addEligibilityEntry(
				'Find a Channel',
				channel.name
			);

			await commerceAdminDiscountDetailsPage.publishButton.click();

			await waitForAlert(page);
		});

		await test.step('Re-open the discount and verify all fields', async () => {
			await commerceAdminDiscountsPage.goto();

			await commerceAdminDiscountsPage.discountLink(discountName).click();

			await expect(
				commerceAdminDiscountDetailsPage.amountInput
			).toHaveValue(/15/);
			await expect(
				commerceAdminDiscountDetailsPage.maximumDiscountAmountInput
			).toHaveValue(/10/);

			await commerceAdminDiscountDetailsPage.eligibilityTab.click();

			await expect(page.getByText(channel.name).first()).toBeVisible();
		});
	}
);

test(
	'Cannot link the same eligibility entry twice to a discount',
	{tag: ['@COMMERCE-10053', '@LPD-85008']},
	async ({
		apiHelpers,
		commerceAdminDiscountDetailsPage,
		commerceAdminDiscountsPage,
		page,
	}) => {
		const randomString = getRandomString().slice(0, 8);
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: `Test Account Run${randomString}`,
			type: 'business',
		});
		const discount =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				active: true,
				level: 'L1',
				percentageLevel1: 20,
				target: 'products',
				title: `Test Discount ${getRandomString()}`,
				usePercentage: true,
			});
		const orderType =
			await apiHelpers.headlessCommerceAdminOrder.postOrderType({
				active: true,
				name: {en_US: `Test Order Type Run${randomString}`},
			});

		await commerceAdminDiscountsPage.goto();

		await commerceAdminDiscountsPage.discountLink(discount.title).click();
		await commerceAdminDiscountDetailsPage.eligibilityTab.click();

		await test.step('Channel — first add succeeds, then the Select button is disabled for the same entry', async () => {
			await commerceAdminDiscountDetailsPage.specificChannelsRadio.check();
			await commerceAdminDiscountDetailsPage.addEligibilityEntry(
				'Find a Channel',
				channel.name
			);

			await commerceAdminDiscountDetailsPage
				.relationFindInput('Find a Channel')
				.fill(channel.name);

			await expect(
				commerceAdminDiscountDetailsPage.relationRowSelectButton(
					channel.name
				)
			).toBeDisabled();

			await page.keyboard.press('Escape');
		});

		await test.step('Account — Select is disabled after the entry is already added', async () => {
			await commerceAdminDiscountDetailsPage.specificAccountsRadio.check();
			await commerceAdminDiscountDetailsPage.addEligibilityEntry(
				'Find an Account',
				`Run${randomString}`
			);

			await commerceAdminDiscountDetailsPage
				.relationFindInput('Find an Account')
				.fill(`Run${randomString}`);

			await expect(
				commerceAdminDiscountDetailsPage.relationRowSelectButton(
					account.name
				)
			).toBeDisabled();

			await page.keyboard.press('Escape');
		});

		await test.step('Order Type — Select is disabled after the entry is already added', async () => {
			await commerceAdminDiscountDetailsPage.specificOrderTypesRadio.check();
			await commerceAdminDiscountDetailsPage.addEligibilityEntry(
				'Find an Order Type',
				`Run${randomString}`
			);

			await commerceAdminDiscountDetailsPage
				.relationFindInput('Find an Order Type')
				.fill(`Run${randomString}`);

			await expect(
				commerceAdminDiscountDetailsPage.relationRowSelectButton(
					orderType.name.en_US
				)
			).toBeDisabled();
		});
	}
);

test(
	'Account group search filters results by keyword',
	{tag: ['@COMMERCE-11893', '@LPD-85008']},
	async ({
		apiHelpers,
		commerceAdminDiscountDetailsPage,
		commerceAdminDiscountsPage,
	}) => {
		const randomString = `Run${getRandomString().replace(/-/g, '').slice(0, 8)}`;
		const accountGroups: Array<{id: number; name: string}> = [];
		const uniqueTokens = ['Alpha', 'Bravo', 'Charlie'];

		for (const token of uniqueTokens) {
			const group = await apiHelpers.headlessAdminUser.postAccountGroup({
				name: `${token} ${randomString} Group`,
			});

			accountGroups.push({id: group.id, name: group.name});
		}

		const discount =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				active: true,
				level: 'L1',
				percentageLevel1: 20,
				target: 'products',
				title: `Test Discount ${getRandomString()}`,
				usePercentage: true,
			});

		try {
			await commerceAdminDiscountsPage.goto();

			await commerceAdminDiscountsPage
				.discountLink(discount.title)
				.click();

			await commerceAdminDiscountDetailsPage.eligibilityTab.click();

			await commerceAdminDiscountDetailsPage.specificAccountGroupsRadio.check();

			const findInput =
				commerceAdminDiscountDetailsPage.relationFindInput(
					'Find an Account Group'
				);

			await test.step('Type the shared run id — all 3 account groups appear', async () => {
				await findInput.click();
				await findInput.fill(randomString);

				for (const group of accountGroups) {
					await expect(
						commerceAdminDiscountDetailsPage.relationResultCell(
							group.name
						)
					).toBeVisible();
				}
			});

			await test.step('Type a token unique to group 1 — only group 1 appears', async () => {
				await findInput.fill('');
				await findInput.fill(uniqueTokens[0]);

				await expect(
					commerceAdminDiscountDetailsPage.relationResultCell(
						accountGroups[0].name
					)
				).toBeVisible();
				await expect(
					commerceAdminDiscountDetailsPage.relationResultCell(
						accountGroups[1].name
					)
				).toHaveCount(0);
				await expect(
					commerceAdminDiscountDetailsPage.relationResultCell(
						accountGroups[2].name
					)
				).toHaveCount(0);
			});

			await test.step('Type a keyword that matches none — no account groups appear', async () => {
				await findInput.fill('');
				await findInput.fill('NoMatch' + randomString);

				for (const group of accountGroups) {
					await expect(
						commerceAdminDiscountDetailsPage.relationResultCell(
							group.name
						)
					).toHaveCount(0);
				}
			});
		}
		finally {
			await Promise.all(
				accountGroups.map((g) =>
					apiHelpers.headlessAdminUser.deleteAccountGroup(g.id)
				)
			);
		}
	}
);

test(
	'Discounts table is sorted by Last Modified date/time',
	{tag: ['@COMMERCE-12099', '@LPD-85008']},
	async ({
		apiHelpers,
		commerceAdminDiscountDetailsPage,
		commerceAdminDiscountsPage,
		page,
	}) => {
		const suffix = getRandomString();
		const discountNames: string[] = [];

		for (let i = 1; i <= 3; i++) {
			const name = `TestDiscount${i}${suffix}`;
			discountNames.push(name);

			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				active: true,
				level: 'L1',
				percentageLevel1: 10,
				target: 'products',
				title: name,
				usePercentage: true,
			});
		}

		await test.step('Discounts list is in modified-desc order: 3, 2, 1', async () => {
			await commerceAdminDiscountsPage.goto();

			const expectedTopThree = [
				discountNames[2],
				discountNames[1],
				discountNames[0],
			];

			for (let i = 0; i < expectedTopThree.length; i++) {
				await expect(
					commerceAdminDiscountsPage.tableRowAt(i)
				).toContainText(expectedTopThree[i]);
			}
		});

		await test.step('Edit (re-publish) the second discount', async () => {
			await commerceAdminDiscountsPage
				.discountLink(discountNames[1])
				.click();

			await commerceAdminDiscountDetailsPage.publishButton.click();

			await waitForAlert(page);
		});

		await test.step('After re-publish, list order is 2, 3, 1', async () => {
			await commerceAdminDiscountsPage.goto();

			const expectedTopThree = [
				discountNames[1],
				discountNames[2],
				discountNames[0],
			];

			for (let i = 0; i < expectedTopThree.length; i++) {
				await expect(
					commerceAdminDiscountsPage.tableRowAt(i)
				).toContainText(expectedTopThree[i]);
			}
		});
	}
);

test(
	'Discount applies to a product on its eligible channel',
	{tag: ['@LPD-85008']},
	async ({apiHelpers, page, productDetailsPage}) => {
		const productName = `U-Joint Discount ${getRandomString()}`;

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				active: true,
				catalogId: catalog.id,
				name: {en_US: productName},
				skus: [
					{
						cost: 0,
						price: 24,
						published: true,
						purchasable: true,
						sku: `SKU${getRandomString()}`,
					},
				],
			});

		await apiHelpers.headlessCommerceAdminPricing.postDiscount({
			active: true,
			discountChannels: [{channelId: channel.id}],
			discountProducts: [{productId: product.productId}],
			level: 'L1',
			percentageLevel1: 25,
			target: 'products',
			title: `Test Discount ${getRandomString()}`,
			usePercentage: true,
		} as any);

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(
			`/web${site.friendlyUrlPath}/p/${encodeURIComponent(productName)}`
		);

		await expect(
			await productDetailsPage.promoPriceField(
				'$ 18.00',
				productDetailsPage.priceContainer
			)
		).toBeVisible();
		await expect(
			await productDetailsPage.priceField(
				'$ 24.00',
				productDetailsPage.priceContainer
			)
		).toBeVisible();
	}
);

test(
	'Buyers cannot see product discounts when the discount is saved as draft',
	{tag: ['@COMMERCE-8912', '@LPD-85008']},
	async ({
		apiHelpers,
		commerceAdminDiscountDetailsPage,
		commerceAdminDiscountsPage,
		page,
		productDetailsPage,
	}) => {
		const productName = `U-Joint Draft ${getRandomString()}`;

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				active: true,
				catalogId: catalog.id,
				name: {en_US: productName},
				skus: [
					{
						cost: 0,
						price: 24,
						published: true,
						purchasable: true,
						sku: `SKU${getRandomString()}`,
					},
				],
			});

		const discount =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				active: true,
				discountProducts: [{productId: product.productId}],
				level: 'L1',
				percentageLevel1: 10,
				target: 'products',
				title: `Test Discount ${getRandomString()}`,
				usePercentage: true,
			});

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await test.step('Published 10% discount — buyer sees discounted price', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}/p/${encodeURIComponent(productName)}`
			);

			await expect(
				await productDetailsPage.promoPriceField(
					'$ 21.60',
					productDetailsPage.priceContainer
				)
			).toBeVisible();
			await expect(
				await productDetailsPage.priceField(
					'$ 24.00',
					productDetailsPage.priceContainer
				)
			).toBeVisible();
		});

		await test.step('Admin edits the discount and saves as draft', async () => {
			await performUserSwitch(page, 'test');

			await commerceAdminDiscountsPage.goto();

			await commerceAdminDiscountsPage
				.discountLink(discount.title)
				.click();

			await commerceAdminDiscountDetailsPage.amountInput.fill('20');
			await commerceAdminDiscountDetailsPage.saveAsDraftButton.click();

			await waitForAlert(page);
		});

		await test.step('Buyer sees only the list price (no discount applied)', async () => {
			await performUserSwitch(page, buyerUser.alternateName);

			await page.goto(
				`/web${site.friendlyUrlPath}/p/${encodeURIComponent(productName)}`
			);

			await expect(
				await productDetailsPage.priceField(
					'$ 24.00',
					productDetailsPage.priceContainer
				)
			).toBeVisible();
			await expect(
				await productDetailsPage.promoPriceField(
					'$ 21.60',
					productDetailsPage.priceContainer
				)
			).toHaveCount(0);
			await expect(
				await productDetailsPage.promoPriceField(
					'$ 19.20',
					productDetailsPage.priceContainer
				)
			).toHaveCount(0);
		});
	}
);

test(
	'Discount lifecycle — add, edit (amount change), and delete a discount with storefront verification',
	{tag: ['@COMMERCE-6231', '@LPD-85008']},
	async ({
		apiHelpers,
		commerceAdminDiscountDetailsPage,
		commerceAdminDiscountsPage,
		page,
		productDetailsPage,
	}) => {
		const productName = `U-Joint Lifecycle ${getRandomString()}`;

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				active: true,
				catalogId: catalog.id,
				name: {en_US: productName},
				skus: [
					{
						cost: 0,
						price: 24,
						published: true,
						purchasable: true,
						sku: `SKU${getRandomString()}`,
					},
				],
			});

		const discountTitle = `Test Discount ${getRandomString()}`;

		const discount =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				active: true,
				discountChannels: [{channelId: channel.id}],
				discountProducts: [{productId: product.productId}],
				level: 'L1',
				percentageLevel1: 25,
				target: 'products',
				title: discountTitle,
				usePercentage: true,
			} as any);

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await test.step('Initial 25% discount — buyer sees $18.00 discounted, $24.00 list', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}/p/${encodeURIComponent(productName)}`
			);

			await expect(
				await productDetailsPage.promoPriceField(
					'$ 18.00',
					productDetailsPage.priceContainer
				)
			).toBeVisible();
			await expect(
				await productDetailsPage.priceField(
					'$ 24.00',
					productDetailsPage.priceContainer
				)
			).toBeVisible();
		});

		await test.step('Admin edits the discount amount to 10%', async () => {
			await performUserSwitch(page, 'test');

			await commerceAdminDiscountsPage.goto();

			await commerceAdminDiscountsPage
				.discountLink(discountTitle)
				.click();

			await commerceAdminDiscountDetailsPage.amountInput.fill('10');
			await commerceAdminDiscountDetailsPage.publishButton.click();

			await waitForAlert(page);
		});

		await test.step('After edit — buyer sees $21.60 discounted, $24.00 list', async () => {
			await performUserSwitch(page, buyerUser.alternateName);

			await page.goto(
				`/web${site.friendlyUrlPath}/p/${encodeURIComponent(productName)}`
			);

			await expect(
				await productDetailsPage.promoPriceField(
					'$ 21.60',
					productDetailsPage.priceContainer
				)
			).toBeVisible();
			await expect(
				await productDetailsPage.priceField(
					'$ 24.00',
					productDetailsPage.priceContainer
				)
			).toBeVisible();
		});

		await test.step('Admin deletes the discount', async () => {
			await performUserSwitch(page, 'test');

			await apiHelpers.headlessCommerceAdminPricing.deleteDiscount(
				discount.id
			);
		});

		await test.step('After delete — buyer sees only $24.00 list price', async () => {
			await performUserSwitch(page, buyerUser.alternateName);

			await page.goto(
				`/web${site.friendlyUrlPath}/p/${encodeURIComponent(productName)}`
			);

			await expect(
				await productDetailsPage.priceField(
					'$ 24.00',
					productDetailsPage.priceContainer
				)
			).toBeVisible();
			await expect(
				await productDetailsPage.promoPriceField(
					'$ 21.60',
					productDetailsPage.priceContainer
				)
			).toHaveCount(0);
		});
	}
);

test(
	'Coupon code is matched case-insensitively when applied to a cart',
	{tag: ['@COMMERCE-12768', '@LPD-85008']},
	async ({apiHelpers, orderDetailsPage, page, pendingOrdersPage}) => {
		await apiHelpers.headlessCommerceAdminPricing.postDiscount({
			active: true,
			couponCode: 'Coupon50',
			level: 'L1',
			percentageLevel1: 50,
			target: 'total',
			title: `Test Discount ${getRandomString()}`,
			useCouponCode: true,
			usePercentage: true,
		});

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [{options: '[]', quantity: 1, skuId: sku2}],
			},
			channel.id
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/pending-orders`);

		await pendingOrdersPage.viewButton.click();

		await orderDetailsPage.applyCouponCode('coupon50');

		await expect(
			orderDetailsPage.couponCodeHeading('Coupon50')
		).toBeVisible();
		await expect(orderDetailsPage.totalDiscountLabel).toBeVisible();
		await expect(orderDetailsPage.orderTotal('$ 12.00')).toBeVisible();
	}
);

test(
	'Coupon code does not apply to a buyer outside the eligible accounts',
	{tag: ['@COMMERCE-6229', '@LPD-85008']},
	async ({
		apiHelpers,
		commerceAdminDiscountDetailsPage,
		commerceAdminDiscountsPage,
		orderDetailsPage,
		page,
		pendingOrdersPage,
	}) => {
		const couponCode = `TEST${getRandomString()}`;

		const randomString = getRandomString();

		await apiHelpers.headlessAdminUser.postAccount({
			name: `Eligible Account Run${randomString}`,
			type: 'business',
		});

		const discount =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				active: true,
				couponCode,
				level: 'L1',
				percentageLevel1: 10,
				target: 'total',
				title: `Test Discount ${getRandomString()}`,
				useCouponCode: true,
				usePercentage: true,
			});

		await commerceAdminDiscountsPage.goto();

		await commerceAdminDiscountsPage.discountLink(discount.title).click();
		await commerceAdminDiscountDetailsPage.eligibilityTab.click();
		await commerceAdminDiscountDetailsPage.specificAccountsRadio.click();
		await commerceAdminDiscountDetailsPage.addEligibilityEntry(
			'Find an Account',
			`Run${randomString}`
		);
		await commerceAdminDiscountDetailsPage.publishButton.click();

		await waitForAlert(page);

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [{options: '[]', quantity: 1, skuId: sku2}],
			},
			channel.id
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/pending-orders`);

		await pendingOrdersPage.viewButton.click();

		await orderDetailsPage.applyCouponCode(couponCode);

		await expect(orderDetailsPage.accountNotQualifiedError).toBeVisible();
	}
);

test(
	'Coupon code does not apply to a buyer outside the eligible account groups',
	{tag: ['@COMMERCE-9789', '@LPD-85008']},
	async ({apiHelpers, orderDetailsPage, page, pendingOrdersPage}) => {
		const couponCode = `FREESHIPPING${getRandomString()}`;

		const eligibleAccountGroup =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: `Eligible AG ${getRandomString()}`,
			});

		const ineligibleAccountGroup =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: `Ineligible AG ${getRandomString()}`,
			});

		await apiHelpers.headlessCommerceAdminPricing.postDiscount({
			active: true,
			couponCode,
			discountAccountGroups: [{accountGroupId: eligibleAccountGroup.id}],
			level: 'L1',
			maximumDiscountAmount: 100,
			percentageLevel1: 100,
			target: 'shipping',
			title: `Free Shipping Discount ${getRandomString()}`,
			useCouponCode: true,
			usePercentage: true,
		} as any);

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
			account.externalReferenceCode,
			ineligibleAccountGroup.externalReferenceCode
		);

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [{options: '[]', quantity: 1, skuId: sku2}],
			},
			channel.id
		);

		await test.step('Buyer in the non-eligible account group cannot apply the coupon', async () => {
			await performUserSwitch(page, buyerUser.alternateName);

			await page.goto(`/web${site.friendlyUrlPath}/pending-orders`);

			await pendingOrdersPage.viewButton.click();

			await orderDetailsPage.applyCouponCode(couponCode);

			await expect(
				orderDetailsPage.accountNotQualifiedError
			).toBeVisible();
		});

		await test.step('After the account is added to the eligible group, the coupon applies', async () => {
			await performUserSwitch(page, 'test');

			await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
				account.externalReferenceCode,
				eligibleAccountGroup.externalReferenceCode
			);

			await performUserSwitch(page, buyerUser.alternateName);

			await page.goto(`/web${site.friendlyUrlPath}/pending-orders`);

			await pendingOrdersPage.viewButton.click();

			await orderDetailsPage.applyCouponCode(couponCode);

			await expect(
				orderDetailsPage.couponCodeHeading(couponCode)
			).toBeVisible();
		});

		await performUserSwitch(page, 'test');

		await apiHelpers.headlessAdminUser.deleteAccountGroup(
			eligibleAccountGroup.id
		);
		await apiHelpers.headlessAdminUser.deleteAccountGroup(
			ineligibleAccountGroup.id
		);
	}
);

test(
	'Coupon code is rejected when applied to a non-qualifying product',
	{tag: ['@COMMERCE-9592', '@LPD-85008']},
	async ({apiHelpers, orderDetailsPage, page, pendingOrdersPage}) => {
		const couponCode = `TEST${getRandomString()}`;

		const productsPage =
			await apiHelpers.headlessCommerceAdminCatalog.getProductsPage(
				100,
				`name eq 'ABS Sensor'`
			);

		const absSensorProduct = productsPage.items[0];

		await apiHelpers.headlessCommerceAdminPricing.postDiscount({
			active: true,
			couponCode,
			discountProducts: [{productId: absSensorProduct.productId}],
			level: 'L1',
			percentageLevel1: 10,
			target: 'products',
			title: `Test Discount ${getRandomString()}`,
			useCouponCode: true,
			usePercentage: true,
		});

		await test.step('Eligible product — coupon applies and discount is shown', async () => {
			const {account, buyerUser} = await createAccountWithBuyerUser(
				apiHelpers,
				site.id
			);

			await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					cartItems: [{options: '[]', quantity: 1, skuId: sku1}],
				},
				channel.id
			);

			await performUserSwitch(page, buyerUser.alternateName);

			await page.goto(`/web${site.friendlyUrlPath}/pending-orders`);

			await pendingOrdersPage.viewButton.click();

			await orderDetailsPage.applyCouponCode(couponCode);

			await expect(
				orderDetailsPage.couponCodeHeading(couponCode)
			).toBeVisible();
		});

		await test.step('Non-qualifying product — coupon is rejected with an error', async () => {
			await performUserSwitch(page, 'test');

			const {account: secondAccount, buyerUser: secondBuyer} =
				await createAccountWithBuyerUser(apiHelpers, site.id);

			await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: secondAccount.id,
					cartItems: [{options: '[]', quantity: 1, skuId: sku2}],
				},
				channel.id
			);

			await performUserSwitch(page, secondBuyer.alternateName);

			await page.goto(`/web${site.friendlyUrlPath}/pending-orders`);

			await pendingOrdersPage.viewButton.click();

			await orderDetailsPage.applyCouponCode(couponCode);

			await expect(
				orderDetailsPage.discountNotApplicableError
			).toBeVisible();
		});
	}
);

test(
	'Coupon code is rejected when its usage limit is reached',
	{tag: ['@COMMERCE-6230', '@LPD-85008']},
	async ({
		apiHelpers,
		checkoutPage,
		orderDetailsPage,
		page,
		pendingOrdersPage,
	}) => {
		const couponCode = `LIMIT${getRandomString().slice(0, 6)}`;

		await apiHelpers.headlessCommerceAdminPricing.postDiscount({
			active: true,
			couponCode,
			level: 'L1',
			limitationTimes: 2,
			limitationType: 'limited',
			percentageLevel1: 10,
			target: 'total',
			title: `Test Discount ${getRandomString()}`,
			useCouponCode: true,
			usePercentage: true,
		});

		const buyers = [];

		for (let i = 0; i < 3; i++) {
			const {account, buyerUser} = await createAccountWithBuyerUser(
				apiHelpers,
				site.id
			);

			await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					cartItems: [{options: '[]', quantity: 1, skuId: sku2}],
				},
				channel.id
			);

			buyers.push(buyerUser);
		}

		for (let i = 0; i < 2; i++) {
			await performUserSwitch(page, buyers[i].alternateName);

			await page.goto(`/web${site.friendlyUrlPath}/pending-orders`);

			await pendingOrdersPage.viewButton.click();

			await orderDetailsPage.applyCouponCode(couponCode);

			await expect(
				orderDetailsPage.couponCodeHeading(couponCode)
			).toBeVisible();

			await orderDetailsPage.checkoutButton.click();

			await checkoutPage.addAddress({
				city: 'testCity',
				countryLabel: 'United States',
				name: buyers[i].alternateName,
				regionLabel: 'Florida',
				street: 'testStreet',
				zip: '12345',
			});

			await checkoutPage.continueButton.click();

			await checkoutPage.shippingMethodRadio('Standard').check();

			await checkoutPage.continueButton.click();
			await checkoutPage.continueButton.click();

			await expect(checkoutPage.orderSuccessMessage).toBeVisible();
		}

		await performUserSwitch(page, buyers[2].alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/pending-orders`);

		await pendingOrdersPage.viewButton.click();

		await orderDetailsPage.applyCouponCode(couponCode);

		await expect(orderDetailsPage.couponCodeUsageLimitError).toBeVisible();
	}
);

test(
	'Discount and tax category are applied correctly during checkout',
	{tag: ['@COMMERCE-11821', '@LPD-85008']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		orderDetailsPage,
		page,
		pendingOrdersPage,
	}) => {
		const taxCategory = (
			await apiHelpers.headlessCommerceAdminChannel.getTaxCategories()
		).items[0];

		const discount =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				active: true,
				level: 'L1',
				percentageLevel1: 15,
				target: 'shipping',
				title: `Discount 1 ${getRandomString()}`,
				usePercentage: false,
			});

		await apiHelpers.headlessCommerceAdminPricing.postDiscountRule(
			discount.id,
			{
				name: 'FREESHIPPING',
				type: 'cart-total',
				typeSettings: '200',
			}
		);

		await test.step('Configure a 10% fixed tax rate on the channel', async () => {
			await commerceAdminChannelsPage.goto();

			await (
				await commerceAdminChannelsPage.channelsTableRowLink(
					channel.name
				)
			).click();

			await commerceAdminChannelDetailsPage.addFixedTaxRate(
				'10',
				taxCategory.name.en_US,
				true
			);
		});

		await apiHelpers.headlessCommerceAdminCatalog.patchProductTaxConfiguration(
			uJointProductId,
			{id: taxCategory.id, taxable: true}
		);

		const {account, buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [{options: '[]', quantity: 10, skuId: sku2}],
			},
			channel.id
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}/pending-orders`);

		await pendingOrdersPage.viewButton.click();

		await orderDetailsPage.checkoutButton.click();

		await checkoutPage.addAddress({
			city: 'Test City',
			countryLabel: 'United States',
			name: buyerUser.alternateName,
			regionLabel: 'Florida',
			street: 'Test Address',
			zip: '12345',
		});

		await checkoutPage.continueButton.click();

		await checkoutPage.shippingMethodRadio('Standard').check();

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.summaryAmount('$ 240.00')).toBeVisible();
		await expect(checkoutPage.summaryAmount('$ 24.00')).toBeVisible();
		await expect(checkoutPage.summaryAmount('$ 264.00')).toBeVisible();

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.orderSuccessMessage).toBeVisible();
	}
);

test(
	'Single Approver workflow moves a draft discount to Pending on submit',
	{tag: ['@COMMERCE-10663', '@LPD-85008']},
	async ({
		commerceAdminDiscountDetailsPage,
		commerceAdminDiscountsPage,
		configurationTabPage,
		page,
	}) => {
		const discountName = `Test Discount ${getRandomString()}`;

		await test.step('Assign Single Approver workflow to Discount', async () => {
			await configurationTabPage.goTo();

			await configurationTabPage.assignWorkflowToAssetType(
				'Single Approver',
				'Discount'
			);
		});

		await test.step('Create a discount and save it as draft', async () => {
			await commerceAdminDiscountsPage.goto();

			await commerceAdminDiscountsPage.addDiscountButton.click();

			await expect(async () => {
				await commerceAdminDiscountsPage.addDiscountModalNameInput.fill(
					discountName
				);

				await expect(
					commerceAdminDiscountsPage.addDiscountModalNameInput
				).toHaveValue(discountName, {timeout: 1000});
			}).toPass({timeout: 10000});

			await commerceAdminDiscountsPage.addDiscountModalTypeSelect.selectOption(
				{label: 'Percentage'}
			);
			await commerceAdminDiscountsPage.addDiscountModalApplyToSelect.selectOption(
				{label: 'Products'}
			);
			await commerceAdminDiscountsPage.addDiscountModalSubmitButton.click();

			await commerceAdminDiscountDetailsPage.amountInput.fill('10');
			await commerceAdminDiscountDetailsPage.saveAsDraftButton.click();

			await waitForAlert(page);
		});

		await test.step('Discount status is Draft and Submit for Workflow is available', async () => {
			await expect(
				commerceAdminDiscountDetailsPage.draftStatus
			).toBeVisible();
		});

		await test.step('Submit for Workflow moves the discount to Pending', async () => {
			await commerceAdminDiscountDetailsPage.submitForWorkflowButton.click();

			await waitForAlert(page);

			await expect(
				commerceAdminDiscountDetailsPage.pendingStatus
			).toBeVisible();
		});

		await configurationTabPage.goTo();

		await configurationTabPage.unassignWorkflowFromAssetType('Discount');
	}
);

test(
	'A user with the Discount Manager role can access the Discounts panel',
	{tag: ['@COMMERCE-10819', '@LPD-85008']},
	async ({apiHelpers, commerceAdminDiscountsPage, page}) => {
		const screenName = `discount-manager-${getRandomString().slice(0, 8)}`;

		const discountManagerUser =
			await apiHelpers.headlessAdminUser.postUserAccount({
				alternateName: screenName,
				emailAddress: `${screenName}@liferay.com`,
				familyName: 'Manager',
				givenName: 'Discount',
			});

		const discountManagerRole =
			await apiHelpers.headlessAdminUser.getRoleByName(
				'Discount Manager'
			);

		await apiHelpers.headlessAdminUser.assignUserToRole(
			discountManagerRole.externalReferenceCode,
			discountManagerUser.id
		);

		userData[screenName] = {
			name: 'Discount',
			password: 'test',
			surname: 'Manager',
		};

		await performUserSwitch(page, screenName);

		await commerceAdminDiscountsPage.goto();

		await expect(commerceAdminDiscountsPage.discountsHeading).toBeVisible();
	}
);

test(
	'Discount targeting a different channel does not apply on the buyer-visible channel',
	{tag: ['@LPD-85008']},
	async ({apiHelpers, page, productDetailsPage}) => {
		const productName = `U-Joint Discount ${getRandomString()}`;

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				active: true,
				catalogId: catalog.id,
				name: {en_US: productName},
				skus: [
					{
						cost: 0,
						price: 24,
						published: true,
						purchasable: true,
						sku: `SKU${getRandomString()}`,
					},
				],
			});

		const otherSite = await apiHelpers.headlessAdminSite.postSite({
			name: 'OtherSite-' + getRandomString(),
		});

		const otherChannel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				name: `TestChannel-${getRandomString()}`,
				siteGroupId: otherSite.id,
			});

		await apiHelpers.headlessCommerceAdminPricing.postDiscount({
			active: true,
			discountChannels: [{channelId: otherChannel.id}],
			discountProducts: [{productId: product.productId}],
			level: 'L1',
			percentageLevel1: 25,
			target: 'products',
			title: `Test Discount ${getRandomString()}`,
			usePercentage: true,
		} as any);

		const {buyerUser} = await createAccountWithBuyerUser(
			apiHelpers,
			site.id
		);

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(
			`/web${site.friendlyUrlPath}/p/${encodeURIComponent(productName)}`
		);

		await expect(
			await productDetailsPage.priceField(
				'$ 24.00',
				productDetailsPage.priceContainer
			)
		).toBeVisible();
		await expect(
			await productDetailsPage.promoPriceField(
				'$ 18.00',
				productDetailsPage.priceContainer
			)
		).toHaveCount(0);
	}
);

test(
	'A discount eligibility entry can be added and then removed via the row actions menu',
	{tag: ['@COMMERCE-6234', '@LPD-85008']},
	async ({
		apiHelpers,
		commerceAdminDiscountDetailsPage,
		commerceAdminDiscountsPage,
		page,
	}) => {
		const randomString = getRandomString().slice(0, 8);
		const accountName = `Test Account Run${randomString}`;

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: accountName,
			type: 'business',
		});

		const discount =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				active: true,
				level: 'L1',
				percentageLevel1: 20,
				target: 'products',
				title: `Test Discount ${getRandomString()}`,
				usePercentage: true,
			});

		await apiHelpers.headlessCommerceAdminPricing.postDiscountAccount(
			discount.id,
			account.id
		);

		await commerceAdminDiscountsPage.goto();

		await commerceAdminDiscountsPage.discountLink(discount.title).click();
		await commerceAdminDiscountDetailsPage.eligibilityTab.click();
		await commerceAdminDiscountDetailsPage.specificAccountsRadio.check();

		await expect(
			commerceAdminDiscountDetailsPage.eligibilityEntryCell(accountName)
		).toBeVisible();

		await expect(async () => {
			await commerceAdminDiscountDetailsPage
				.eligibilityRowActions(accountName)
				.click();

			await expect(
				commerceAdminDiscountDetailsPage.eligibilityRowRemoveMenuItem
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await commerceAdminDiscountDetailsPage.eligibilityRowRemoveMenuItem.click();
		await commerceAdminDiscountDetailsPage.publishButton.click();

		await waitForAlert(page);

		await expect(
			commerceAdminDiscountDetailsPage.eligibilityEntryCell(accountName)
		).toHaveCount(0);
	}
);
