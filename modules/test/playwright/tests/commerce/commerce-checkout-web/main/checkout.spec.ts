/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../../fixtures/accountsPagesTest';
import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {notificationPagesTest} from '../../../../fixtures/notificationPagesTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../../../fixtures/pageViewModePagesTest';
import {systemSettingsPageTest} from '../../../../fixtures/systemSettingsPageTest';
import {liferayConfig} from '../../../../liferay.config';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import getFragmentDefinition from '../../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {miniumSetUp} from '../../utils/commerce';
import {getDateFormatted, setFutureDate} from '../../utils/date';

export const test = mergeTests(
	applicationsMenuPageTest,
	accountsPagesTest,
	commercePagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPD-20379': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	notificationPagesTest,
	pageEditorPagesTest,
	pageViewModePagesTest,
	systemSettingsPageTest
);

test(
	'Checkout widget configuration to display full addresses and phone number',
	{tag: ['@LPD-25860']},
	async ({apiHelpers, checkoutPage, page, site, widgetPagePage}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product'},
			});

		const productSkus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product.productId)
			.then((product) => {
				return product.skus;
			});

		const sku = productSkus[0];

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						options: '[]',
						quantity: 1,
						replacedSkuId: 0,
						skuId: sku.id,
					},
				],
			},
			channel.id
		);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Checkout');

		const phoneNumber = '1234567890';
		const region = 'Florida';
		const zipCode = '33101';

		await checkoutPage.addressInput.fill('123 Main St');
		await checkoutPage.cityInput.fill('Miami');
		await checkoutPage.countryInput.selectOption({label: 'United States'});
		await checkoutPage.nameInput.fill('John Doe');
		await checkoutPage.phoneNumberInput.fill(phoneNumber);
		await checkoutPage.regionInput.selectOption({label: region});
		await checkoutPage.zipInput.fill(zipCode);

		await checkoutPage.continueButton.click();

		await expect(checkoutPage.commerceBillingAddress).not.toContainText(
			phoneNumber
		);
		await expect(checkoutPage.commerceBillingAddress).not.toContainText(
			region
		);
		await expect(checkoutPage.commerceBillingAddress).not.toContainText(
			zipCode
		);
		await expect(checkoutPage.commerceShippingAddress).not.toContainText(
			phoneNumber
		);
		await expect(checkoutPage.commerceShippingAddress).not.toContainText(
			region
		);
		await expect(checkoutPage.commerceShippingAddress).not.toContainText(
			zipCode
		);

		await checkoutPage.optionsButton.click();
		await checkoutPage.configurationMenuItem.click();

		await checkoutPage.configurationIFrameShowFullAddressToggle.check();
		await checkoutPage.configurationIFrameShowPhoneNumberToggle.check();
		await checkoutPage.configurationIFrameSaveButton.click();
		await waitForAlert(checkoutPage.configurationIFrame);

		await page.reload();

		await expect(checkoutPage.commerceBillingAddress).toContainText(
			phoneNumber
		);
		await expect(checkoutPage.commerceBillingAddress).toContainText(region);
		await expect(checkoutPage.commerceBillingAddress).toContainText(
			zipCode
		);
		await expect(checkoutPage.commerceShippingAddress).toContainText(
			phoneNumber
		);
		await expect(checkoutPage.commerceShippingAddress).toContainText(
			region
		);
		await expect(checkoutPage.commerceShippingAddress).toContainText(
			zipCode
		);
	}
);

test(
	'Payment Term is reset correctly',
	{tag: ['@LPP-55128']},
	async ({
		accountsPage,
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		editAccountChannelDefaultsPage,
		editAccountPage,
		page,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		const paymentTerm1 =
			await apiHelpers.headlessCommerceAdminOrder.postTerm({
				label: {
					en_US: 'MoneyA',
				},
				name: 'moneya',
				priority: 0,
				type: 'payment-terms',
			});
		const paymentTerm2 =
			await apiHelpers.headlessCommerceAdminOrder.postTerm({
				label: {
					en_US: 'MoneyB',
				},
				name: 'moneyb',
				priority: 1,
				type: 'payment-terms',
			});
		const paymentTerm3 =
			await apiHelpers.headlessCommerceAdminOrder.postTerm({
				label: {
					en_US: 'PayPalA',
				},
				name: 'paypala',
				priority: 2,
				type: 'payment-terms',
			});
		const paymentTerm4 =
			await apiHelpers.headlessCommerceAdminOrder.postTerm({
				label: {
					en_US: 'MoneyC',
				},
				name: 'moneyc',
				priority: 3,
				type: 'payment-terms',
			});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product'},
			});

		const productSkus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product.productId)
			.then((product) => {
				return product.skus;
			});

		const sku = productSkus[0];

		const basePriceListId =
			await apiHelpers.headlessCommerceAdminPricing.getBasePriceListId(
				catalog.id
			);

		await apiHelpers.headlessCommerceAdminPricing.postPriceEntry({
			price: 100,
			priceListId: basePriceListId.items[0].id,
			skuId: sku.id,
		});

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['test@liferay.com']
		);

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						options: '[]',
						quantity: 1,
						replacedSkuId: 0,
						skuId: sku.id,
					},
				],
			},
			channel.id
		);

		await accountsPage.goto();
		await (await accountsPage.accountsTable.cellLink(account.name)).click();
		await editAccountPage.channelDefaultsLink.click();
		await editAccountChannelDefaultsPage.addDefaultPaymentTerm(
			paymentTerm2.id
		);

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'Money Order',
			'Payment Methods'
		);
		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				'Money Order'
			)
		).click();
		await commerceAdminChannelDetailsPage.setEntryEligibility(
			'Specific Payment Terms',
			paymentTerm1.name,
			'Payment Methods'
		);
		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				'Money Order'
			)
		).click();
		await commerceAdminChannelDetailsPage.setEntryEligibility(
			'Specific Payment Terms',
			paymentTerm2.name,
			'Payment Methods'
		);
		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				'Money Order'
			)
		).click();
		await commerceAdminChannelDetailsPage.setEntryEligibility(
			'Specific Payment Terms',
			paymentTerm4.name,
			'Payment Methods'
		);

		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'PayPal',
			'Payment Methods'
		);
		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				'PayPal'
			)
		).click();
		await commerceAdminChannelDetailsPage.setEntryEligibility(
			'Specific Payment Terms',
			paymentTerm3.name,
			'Payment Methods'
		);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Checkout');

		await checkoutPage.addressInput.fill('123 Main St');
		await checkoutPage.cityInput.fill('Miami');
		await checkoutPage.countryInput.selectOption({label: 'United States'});
		await checkoutPage.nameInput.fill('John Doe');
		await checkoutPage.phoneNumberInput.fill('1234567890');
		await checkoutPage.regionInput.selectOption({label: 'Florida'});
		await checkoutPage.zipInput.fill('33101');

		await checkoutPage.continueButton.click();

		expect(page.getByLabel('Money Order')).toBeChecked();
		await checkoutPage.continueButton.click();
		await page.waitForURL((url) => url.href.includes('payment-terms'));
		expect(page.getByLabel('MoneyB')).toBeChecked();
		await checkoutPage.continueButton.click();
		await page.waitForURL((url) => url.href.includes('order-summary'));
		await checkoutPage.previousButton.click();
		await page.waitForURL((url) => url.href.includes('payment-terms'));
		await checkoutPage.previousButton.click();
		await page.waitForURL((url) => url.href.includes('payment-method'));
		await page.getByLabel('PayPal').check();
		await checkoutPage.continueButton.click();
		await page.waitForURL((url) => url.href.includes('order-summary'));
		await checkoutPage.previousButton.click();
		await page.waitForURL((url) => url.href.includes('payment-method'));
		await page.getByLabel('Money Order').check();
		await checkoutPage.continueButton.click();
		await page.waitForURL((url) => url.href.includes('payment-terms'));
		expect(page.getByLabel('MoneyB')).toBeChecked();
	}
);

test(
	'Delivery group multishipping checkout summary',
	{tag: ['@LPD-35329']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		page,
	}) => {
		test.setTimeout(180000);

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		await waitForAlert(page);

		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				'Flat Rate'
			)
		).click();
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'Flat Rate',
			'Shipping Methods'
		);
		await commerceAdminChannelDetailsPage.addFlatRateShippingOption(
			getRandomString()
		);
		await commerceAdminChannelDetailsPage.addFlatRateShippingOption(
			getRandomString()
		);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
				shippingConfiguration: {
					freeShipping: false,
					shippable: true,
					shippingSeparately: false,
				},
			});

		const productSkus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(product.productId)
			.then((product) => {
				return product.skus;
			});

		const sku = productSkus[0];

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{phoneNumber: '1234567890', regionISOCode: 'AL'}
			);

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						deliveryGroupName: getRandomString(),
						quantity: 1,
						requestedDeliveryDate: setFutureDate(7),
						shippingAddressId: address.id,
						skuId: sku.id,
					},
				],
			},
			channel.id
		);

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_cart_content_web_internal_portlet_CommerceCartContentTotalPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_checkout_web_internal_portlet_CommerceCheckoutPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const cartItems = await apiHelpers.headlessCommerceDeliveryCart
			.getCartItems(cart.id)
			.then((response) => {
				return response.items;
			});

		const cartItem = cartItems[0];

		await page.goto(
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		const locale = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getBCP47LanguageId();
		});

		await expect(
			(await checkoutPage.tableRow(0, cartItem.deliveryGroupName, true))
				.row
		).toBeVisible();
		await expect(
			(
				await checkoutPage.tableRow(
					1,
					address.street1 +
						', ' +
						address.city +
						', ' +
						'United States',
					true
				)
			).row
		).toBeVisible();
		await expect(
			(
				await checkoutPage.tableRow(
					2,
					getDateFormatted(cartItem.requestedDeliveryDate, locale),
					true
				)
			).row
		).toBeVisible();

		await checkoutPage.viewDeliveryGroupTableButton.click();

		await expect(
			checkoutPage.headingDeliveryGroupModal(cartItem.deliveryGroup)
		).toBeVisible();
		await expect(
			checkoutPage.assertDataDeliveryGroupModal(address.street1)
		).toBeVisible();
		await expect(
			checkoutPage.assertDataDeliveryGroupModal(address.street2)
		).toBeVisible();
		await expect(
			checkoutPage.assertDataDeliveryGroupModal(address.street3)
		).toBeVisible();
		await expect(
			checkoutPage.assertDataDeliveryGroupModal(
				address.city + ' , ' + 'Alabama'
			)
		).toBeVisible();
		await expect(
			checkoutPage.assertDataDeliveryGroupModal(
				address.zip + ' , ' + 'United States'
			)
		).toBeVisible();
		await expect(
			checkoutPage.configurationIFrame.getByText(
				getDateFormatted(cartItem.requestedDeliveryDate, locale)
			)
		).toBeVisible();

		await checkoutPage.iframeOkButton.click();
		await checkoutPage.continueButton.click();
		await checkoutPage.continueButton.click();

		await expect(checkoutPage.orderItemsTabLink).toBeVisible();
		await expect(checkoutPage.multishippingTabLink).toBeVisible();
		await checkoutPage.orderItemsTabLink.click();
		await expect(checkoutPage.orderItemsTableLocator).toBeVisible();
		await checkoutPage.multishippingTabLink.click();
		await expect(checkoutPage.multishippingTableLocator).toBeVisible();
		await expect(page.getByText('Shipping Address & Date')).toBeVisible();
		await expect(page.getByText('Billing Address')).toBeVisible();
		await expect(checkoutPage.orderSummaryShippingMethod).toBeVisible();
	}
);

test(
	'Checkout order detail redirect works correctly when order DPT is enabled',
	{tag: ['@LPD-40425']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
		widgetPagePage,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: 'Order',
			name: displayPageTemplateName,
		});
		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		await expect(page.getByText('Heading Example')).toBeVisible();

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();
		await displayPageTemplatesPage.clickMoreActions(
			displayPageTemplateName,
			'Mark as Default'
		);

		await waitForAlert(page);

		await expect(
			commerceLayoutsPage.defaultDisplayPageTemplateIcon
		).toBeVisible();

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const sku = product.skus[0];

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						quantity: 1,
						skuId: sku.id,
					},
				],
			},
			channel.id
		);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Checkout');

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

		await expect(page.getByText('Heading Example')).toBeVisible();
	}
);

test(
	'Set address subtype during checkout',
	{tag: ['@LPD-51453']},
	async ({
		accountInstanceSettingsAccountAddressSubtypePage,
		apiHelpers,
		checkoutPage,
		commerceAdminChannelsPage,
		page,
		site,
	}) => {
		test.setTimeout(180000);

		const fillAddressForm = async (address: any) => {
			await checkoutPage.addressInput.fill(address.street1);
			await checkoutPage.cityInput.fill(address.city);
			await checkoutPage.countryInput.selectOption({
				label: address.country,
			});
			await checkoutPage.nameInput.fill(address.name);
			await checkoutPage.regionInput.selectOption({
				label: address.region,
			});
			await checkoutPage.subtypeInput.fill(address.subtype);
			await checkoutPage.subtypeMenuItem(address.subtype).click();
			await checkoutPage.zipInput.fill(address.postalCode);
		};

		await accountInstanceSettingsAccountAddressSubtypePage.setAddressSubtypeExternalReferenceCodes();

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		await waitForAlert(page);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

		const sku = product.skus[0];

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_checkout_web_internal_portlet_CommerceCheckoutPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await page.goto(
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(async () => {
			await apiHelpers.headlessCommerceDeliveryCart.postCart(
				{
					accountId: account.id,
					cartItems: [
						{
							quantity: 1,
							skuId: sku.id,
						},
					],
				},
				channel.id
			);

			await page.reload();

			await expect(checkoutPage.useAsBillingCheckbox).toBeVisible();
		}).toPass();

		await expect(checkoutPage.subtypeInput).toHaveCount(0);

		const {
			billingAndShippingListTypeDefinition,
			billingAndShippingListTypeEntry,
			billingListTypeDefinition,
			billingListTypeEntry,
			shippingListTypeDefinition,
			shippingListTypeEntry,
		} =
			await accountInstanceSettingsAccountAddressSubtypePage.initAddressSubtypePicklists(
				apiHelpers
			);

		await accountInstanceSettingsAccountAddressSubtypePage.setAddressSubtypeExternalReferenceCodes(
			billingListTypeDefinition.name,
			billingAndShippingListTypeDefinition.name,
			shippingListTypeDefinition.name
		);

		try {
			await page.goto(
				`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(checkoutPage.subtypeErrorMessage).toHaveCount(0);
			await expect(checkoutPage.subtypeInput).toBeVisible();
			await expect(checkoutPage.subtypeInput).toBeEnabled();
			await expect(checkoutPage.useAsBillingCheckbox).toBeChecked();

			const shippingAddress = {
				city: getRandomString(),
				country: 'United States',
				countryId: '0',
				name: getRandomString(),
				postalCode: String(getRandomInt()),
				region: 'Alabama',
				regionId: '0',
				street1: getRandomString(),
				subtype: billingAndShippingListTypeEntry.key,
				type: 'Billing',
			};

			await fillAddressForm(shippingAddress);
			await checkoutPage.useAsBillingCheckbox.setChecked(false);

			await expect(checkoutPage.subtypeErrorMessage).toBeVisible();
			await expect(checkoutPage.subtypeInput).toHaveValue('');

			await checkoutPage.subtypeInput.fill(shippingListTypeEntry.key);
			await checkoutPage
				.subtypeMenuItem(shippingListTypeEntry.key)
				.click();
			await checkoutPage.continueButton.click();

			await expect(checkoutPage.useAsBillingCheckbox).toHaveCount(0);

			await checkoutPage.previousButton.click();

			await expect(checkoutPage.useAsBillingCheckbox).toBeVisible();
			await expect(checkoutPage.useAsBillingCheckbox).not.toBeChecked();
			await expect(checkoutPage.nameInput).toBeDisabled();
			await expect(checkoutPage.nameInput).toHaveValue(
				shippingAddress.name
			);
			await expect(checkoutPage.subtypeInput).toBeDisabled();
			await expect(checkoutPage.subtypeInput).toHaveValue(
				new RegExp(shippingListTypeEntry.key, 'i')
			);

			await checkoutPage.continueButton.click();

			await expect(checkoutPage.useAsBillingCheckbox).toHaveCount(0);

			const billingAddress = {
				city: getRandomString(),
				country: 'United States',
				countryId: '0',
				name: getRandomString(),
				postalCode: String(getRandomInt()),
				region: 'Alabama',
				regionId: '0',
				street1: getRandomString(),
				subtype: billingListTypeEntry.key,
				type: 'Billing',
			};

			await fillAddressForm(billingAddress);

			await checkoutPage.continueButton.click();

			await expect(checkoutPage.commerceBillingAddress).toContainText(
				billingListTypeEntry.key,
				{ignoreCase: true}
			);
			await expect(checkoutPage.commerceShippingAddress).toContainText(
				shippingListTypeEntry.key,
				{ignoreCase: true}
			);

			await checkoutPage.previousButton.click();

			await expect(checkoutPage.nameInput).toBeDisabled();
			await expect(checkoutPage.nameInput).toHaveValue(
				billingAddress.name
			);
			await expect(checkoutPage.subtypeInput).toBeDisabled();
			await expect(checkoutPage.subtypeInput).toHaveValue(
				new RegExp(billingListTypeEntry.key, 'i')
			);

			await checkoutPage.previousButton.click();

			await expect(checkoutPage.useAsBillingCheckbox).toBeVisible();
			await expect(checkoutPage.useAsBillingCheckbox).not.toBeChecked();

			await checkoutPage.useAsBillingCheckbox.setChecked(true);

			await expect(checkoutPage.subtypeResetModalTitle).toBeVisible();
			await expect(checkoutPage.subtypeResetSubtypeInput).toBeVisible();

			await checkoutPage.subtypeResetSubtypeInput.fill(
				billingAndShippingListTypeEntry.key
			);
			await checkoutPage
				.subtypeMenuItem(billingAndShippingListTypeEntry.key)
				.click();
			await checkoutPage.saveButton.click();

			await expect(checkoutPage.commerceBillingAddress).toContainText(
				billingAndShippingListTypeEntry.key,
				{ignoreCase: true}
			);
			await expect(checkoutPage.commerceShippingAddress).toContainText(
				billingAndShippingListTypeEntry.key,
				{ignoreCase: true}
			);
		}
		finally {
			await accountInstanceSettingsAccountAddressSubtypePage.setAddressSubtypeExternalReferenceCodes();
		}
	}
);

test(
	'Create a full Checkout flow functional test for stable run 1',
	{tag: ['@LPD-56215']},
	async ({
		apiHelpers,
		commerceAdminCatalogsPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductDetailsSkusPage,
		commerceAdminProductPage,
		commerceAdminWarehouseDetailsPage,
		commerceAdminWarehouseEligibilityPage,
		commerceAdminWarehousesPage,
		page,
	}) => {
		const catalog = {id: 0, name: getRandomString()};
		const channel = {id: 0, name: getRandomString()};
		const product1 = {id: 0, name: getRandomString()};
		let sku1: string;
		const warehouse = {id: 0, name: getRandomString()};

		await test.step('Create a Catalog', async () => {
			await commerceAdminCatalogsPage.goto();
			await commerceAdminCatalogsPage.addCatalogsButton.click();

			await expect(
				commerceAdminCatalogsPage.modalFrameLocator.getByText(
					'Add Catalog'
				)
			).toBeVisible();

			await commerceAdminCatalogsPage.modalFieldName.fill(catalog.name);
			await commerceAdminCatalogsPage.modalSubmitButton.click();

			catalog.id = parseInt(
				await commerceAdminCatalogsPage.catalogId.textContent(),
				10
			);

			apiHelpers.data.push({id: catalog.id, type: 'catalog'});

			await commerceAdminCatalogsPage.catalogSaveButton.click();

			await waitForAlert(page);
		});

		await test.step('Create a Channel', async () => {
			await commerceAdminChannelsPage.goto();
			await commerceAdminChannelsPage.addButton.click();

			await expect(
				commerceAdminChannelsPage.modalFrameLocator.getByText(
					'Add Channel'
				)
			).toBeVisible();

			await commerceAdminChannelsPage.modalFieldName.fill(channel.name);
			await commerceAdminChannelsPage.modalSelectType.selectOption(
				'Site'
			);
			await commerceAdminChannelsPage.modalAddButton.click();

			channel.id = parseInt(
				await commerceAdminChannelDetailsPage.channelId.textContent(),
				10
			);

			apiHelpers.data.push({id: channel.id, type: 'channel'});
		});

		await test.step('Create a Warehouse', async () => {
			await commerceAdminWarehousesPage.goto();
			await commerceAdminWarehousesPage.addButton.click();

			await expect(
				commerceAdminWarehousesPage.modalFrameLocator.getByText(
					'Add Warehouse'
				)
			).toBeVisible();

			await commerceAdminWarehousesPage.modalFieldName.fill(
				warehouse.name
			);

			await expect(
				commerceAdminWarehousesPage.modalFieldName
			).toHaveValue(warehouse.name);

			await commerceAdminWarehousesPage.modalSubmitButton.click();

			await expect(page.getByLabel('Name Required')).toHaveValue(
				warehouse.name
			);

			await commerceAdminWarehouseDetailsPage
				.geolocationField('Latitude')
				.fill(getRandomInt().toString());
			await commerceAdminWarehouseDetailsPage
				.geolocationField('Longitude')
				.fill(getRandomInt().toString());
			await commerceAdminWarehouseDetailsPage.detailsActiveToggle.check();

			warehouse.id = parseInt(
				await commerceAdminWarehouseDetailsPage.warehouseId.textContent(),
				10
			);

			apiHelpers.data.push({id: warehouse.id, type: 'warehouse'});

			await commerceAdminWarehouseDetailsPage.saveButton.click();

			await waitForAlert(page);

			await commerceAdminWarehouseEligibilityPage.linkTab.click();
			await commerceAdminWarehouseEligibilityPage.specificChannelRadio.click();
			await commerceAdminWarehouseEligibilityPage.addChannels.fill(
				channel.name
			);
			await commerceAdminWarehouseEligibilityPage.selectButton.click();

			await page.keyboard.press('Escape');

			await expect(
				page.getByRole('link', {name: channel.name})
			).toBeVisible();

			await commerceAdminWarehouseDetailsPage.saveButton.click();

			await waitForAlert(page);
		});

		await test.step('Create a Simple Product', async () => {
			await commerceAdminProductPage.goto();
			await commerceAdminProductPage.addButton.click();
			await commerceAdminProductPage
				.menuItemProductType('Simple')
				.click();

			await expect(
				commerceAdminProductPage.modalFrameLocator.getByText(
					'Create New Product'
				)
			).toBeVisible();

			await commerceAdminProductPage.modalFieldName.fill(product1.name);
			await commerceAdminProductPage.modalPlaceHolder.fill(catalog.name);
			await commerceAdminProductPage.modalMenuItem(catalog.name).click();
			await commerceAdminProductPage.modalSubmitButton.click();

			await expect(page.getByText(product1.name)).toBeVisible();

			product1.id = parseInt(
				await commerceAdminProductDetailsPage.productId.textContent(),
				10
			);

			apiHelpers.data.push({id: product1.id, type: 'product'});

			await commerceAdminProductDetailsPage.publishLink.click();

			await expect(page.getByText('Approved')).toBeVisible();

			await commerceAdminProductDetailsSkusPage.skusLink.click();
			await commerceAdminProductDetailsSkusPage
				.skusTableRowLink('default')
				.click();

			sku1 = getRandomString();

			await commerceAdminProductDetailsSkusPage.sidePanelDetailsSkuFieldName.fill(
				sku1
			);
			await commerceAdminProductDetailsSkusPage.sidePanelDetailsSkuPublishButton.click();

			await waitForAlert(
				commerceAdminProductDetailsSkusPage.sidePanelFrame
			);

			await commerceAdminProductDetailsSkusPage.goToSkuTab('Inventory');
			await commerceAdminProductDetailsSkusPage.addWarehouseQuantity(
				'10',
				warehouse.name
			);

			await waitForAlert(
				commerceAdminProductDetailsSkusPage.sidePanelFrame
			);

			await (
				await commerceAdminProductDetailsSkusPage.closeSidePanelFrame(
					false
				)
			).click();

			await expect(
				await commerceAdminProductDetailsSkusPage.closeSidePanelFrame(
					false
				)
			).toBeHidden();

			await commerceAdminProductDetailsPage.publishLink.click();

			await page.waitForLoadState('domcontentloaded');

			await waitForAlert(
				page,
				'Success:Your request completed successfully.',
				{autoClose: false}
			);

			await commerceAdminProductDetailsSkusPage
				.skusTableRowLink(sku1)
				.click();
			await commerceAdminProductDetailsSkusPage.goToSkuTab('Price');
			await commerceAdminProductDetailsSkusPage
				.sidePanelSkuPriceTableRowLink(
					`${catalog.name} Base Price List`
				)
				.click();

			const skuBasePriceList = 10.0;

			await commerceAdminProductDetailsSkusPage.sidePanelNestedPriceListPrice.fill(
				String(skuBasePriceList)
			);
			await commerceAdminProductDetailsSkusPage.sidePanelNestedSaveButton.click();

			await waitForAlert(
				commerceAdminProductDetailsSkusPage.sidePanelNestedFrame
			);
		});
	}
);

test(
	'Create a full Checkout flow functional test for stable run 2',
	{tag: ['@LPD-56215']},
	async ({
		apiHelpers,
		checkoutPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelDetailsTypePage,
		commerceAdminChannelsPage,
		commerceMiniCartPage,
		page,
		productDetailsPage,
		productPublisherPage,
		site,
	}) => {
		let account: any;
		let catalog;
		let layout: any;
		let product1;
		let product2;
		let sku1;
		let sku2;

		await test.step('Create a Catalog', async () => {
			catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog(
				{
					name: getRandomString(),
				}
			);
		});

		await test.step('Create a Channel via API, link the channel to the site, add two shipping options and two payment methods', async () => {
			const channel =
				await apiHelpers.headlessCommerceAdminChannel.postChannel({});

			await commerceAdminChannelsPage.goto();
			await (
				await commerceAdminChannelsPage.channelsTableRowLink(
					channel.name
				)
			).click();

			await commerceAdminChannelDetailsTypePage.typeLinkTab.click();

			await expect(
				commerceAdminChannelDetailsTypePage.selectSiteButton
			).toBeVisible({visible: true});

			await commerceAdminChannelDetailsTypePage.selectSiteButton.click();
			await (
				await commerceAdminChannelDetailsTypePage.typeTableRowAction(
					site.name
				)
			).click();

			await expect(
				page.getByRole('cell', {exact: true, name: `${site.name}`})
			).toBeVisible();

			await commerceAdminChannelDetailsPage.saveButton.click();

			await waitForAlert(page);

			await commerceAdminChannelDetailsPage.goToTab('General');

			await commerceAdminChannelsPage.changeCommerceChannelSiteType(
				channel.name,
				'B2B',
				true
			);

			channel.id = parseInt(
				await commerceAdminChannelDetailsPage.channelId.textContent(),
				10
			);

			apiHelpers.data.push({id: channel.id, type: 'channel'});

			await (
				await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
					'Money Order'
				)
			).click();
			await commerceAdminChannelDetailsPage.isActive.click();
			await commerceAdminChannelDetailsPage.sidePanelSaveButton.click();

			await waitForAlert(commerceAdminChannelsPage.sidePanelFrameLocator);

			await (
				await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
					'PayPal'
				)
			).click();
			await commerceAdminChannelDetailsPage.isActive.click();
			await commerceAdminChannelDetailsPage.sidePanelSaveButton.click();

			await waitForAlert(commerceAdminChannelsPage.sidePanelFrameLocator);

			await commerceAdminChannelsPage.setupCommerceChannelShippingMethod(
				channel.name,
				'Flat Rate',
				[getRandomString(), getRandomString()],
				true,
				true
			);

			await page.waitForLoadState('domcontentloaded');
		});

		await test.step('Create an Account and a buyer', async () => {
			account = await apiHelpers.headlessAdminUser.postAccount({
				name: getRandomString(),
				type: 'business',
			});

			const user =
				await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
					'demo.unprivileged@liferay.com'
				);

			const rolesResponse =
				await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

			const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
				return role.name === 'Buyer';
			});

			await apiHelpers.headlessAdminUser.assignAccountRoles(
				account.externalReferenceCode,
				accountRoleBuyer[0].id,
				user.emailAddress
			);

			const siteRole =
				await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

			await apiHelpers.headlessAdminUser.assignUserToSite(
				siteRole.id,
				site.id,
				user.id
			);
			await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
				account.id,
				[user.emailAddress]
			);
		});

		await test.step('Create two products via API', async () => {
			product1 =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
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

			const productSkus1 = await apiHelpers.headlessCommerceAdminCatalog
				.getProduct(product1.productId)
				.then((product) => {
					return product.skus;
				});

			sku1 = productSkus1[0];

			product2 =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					skus: [
						{
							cost: 0,
							price: 20,
							published: true,
							purchasable: true,
							sku: 'Sku' + getRandomInt(),
						},
					],
				});

			const productSkus2 = await apiHelpers.headlessCommerceAdminCatalog
				.getProduct(product2.productId)
				.then((product) => {
					return product.skus;
				});

			sku2 = productSkus2[0];
		});

		await test.step('Create three different page with Commerce Product Publisher, Commerce Product Details, Mini Cart Fragment and Commerce Checkout', async () => {
			layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getFragmentDefinition({
						id: getRandomString(),
						key: 'COMMERCE_ACCOUNT_FRAGMENTS-account-selector',
					}),
					getWidgetDefinition({
						id: getRandomString(),
						widgetName:
							'com_liferay_commerce_product_content_web_internal_portlet_CPPublisherPortlet',
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});
			await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getWidgetDefinition({
						id: getRandomString(),
						widgetName:
							'com_liferay_commerce_product_content_web_internal_portlet_CPContentPortlet',
					}),
					getFragmentDefinition({
						id: getRandomString(),
						key: 'COMMERCE_CART_FRAGMENTS-mini-cart',
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});
			await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getWidgetDefinition({
						id: getRandomString(),
						widgetName:
							'com_liferay_commerce_checkout_web_internal_portlet_CommerceCheckoutPortlet',
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});
		});

		await test.step('Login as a buyer, go to the Product Publisher page and assert that two product are displayed', async () => {
			await performLogout(page);
			await performLoginViaApi({
				page,
				screenName: 'demo.unprivileged',
			});

			await page.goto(
				`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(
				await productPublisherPage.productLink(product1.name.en_US)
			).toBeVisible();
			await expect(
				await productPublisherPage.productLink(product2.name.en_US)
			).toBeVisible();
			await expect(
				productPublisherPage.productCardPrice(
					product1.name.en_US,
					'$ 10.00'
				)
			).toBeVisible();
			await expect(
				productPublisherPage.productCardPrice(
					product2.name.en_US,
					'$ 20.00'
				)
			).toBeVisible();
			await expect(
				await productPublisherPage.productSku(sku1.sku)
			).toBeVisible();
			await expect(
				await productPublisherPage.productSku(sku2.sku)
			).toBeVisible();
		});

		await test.step('Add to cart one product from the product publisher and one product from the product details', async () => {
			await productPublisherPage
				.productCardAddToCartButton(product2.name.en_US)
				.click();
			await (
				await productPublisherPage.productLink(product1.name.en_US)
			).click();

			await expect(
				await productDetailsPage.skuField(sku1.sku)
			).toBeVisible();
			await expect(
				await productDetailsPage.nameField(product1.name.en_US)
			).toBeVisible();
			await expect(
				await productDetailsPage.priceField('$ 10.00')
			).toBeVisible();

			await productDetailsPage.addToCartButton.click();

			await page.waitForLoadState('domcontentloaded');
		});

		await test.step('Open the Mini cart and assert that two product are visible and submit', async () => {
			await commerceMiniCartPage.miniCartButton.click();

			await expect(
				commerceMiniCartPage.miniCartItem(product2.name.en_US)
			).toHaveCount(1);
			await expect(
				commerceMiniCartPage.miniCartItem(product2.name.en_US)
			).toBeVisible();
			await expect(
				commerceMiniCartPage.miniCartItem(product1.name.en_US)
			).toHaveCount(1);
			await expect(
				commerceMiniCartPage.miniCartItem(product1.name.en_US)
			).toBeVisible();
			await expect(commerceMiniCartPage.miniCartTotalPrice).toHaveText(
				'$ 30.00'
			);

			await commerceMiniCartPage.submitButton.click();

			await expect(commerceMiniCartPage.submitButton).toBeHidden();
		});

		await test.step('Checkout the order', async () => {
			await checkoutPage.addAddress({
				city: 'testCity',
				countryLabel: 'United States',
				name: 'John Doe',
				regionLabel: 'Florida',
				street: 'testStreet',
				zip: '12345',
			});

			await checkoutPage.continueButton.click();

			await expect(page.getByText('Money Order')).toBeVisible();
			await expect(
				page.locator(
					'[id="_com_liferay_commerce_checkout_web_internal_portlet_CommerceCheckoutPortlet_commercePaymentMethodKey_1"]'
				)
			).toBeChecked();
			await expect(page.getByText('PayPal')).toBeVisible();

			await checkoutPage.continueButton.click();

			await expect(
				(
					await checkoutPage.orderSummaryTableRow(
						1,
						product2.name.en_US,
						true
					)
				).row
			).toBeVisible();
			await expect(
				(await checkoutPage.orderSummaryTableRow(4, '$ 20.00')).row
			).toBeVisible();
			await expect(
				(await checkoutPage.orderSummaryTableRow(6, '$ 20.00')).row
			).toBeVisible();
			await expect(
				(
					await checkoutPage.orderSummaryTableRow(
						1,
						product1.name.en_US,
						true
					)
				).row
			).toBeVisible();
			await expect(
				(await checkoutPage.orderSummaryTableRow(4, '$ 10.00')).row
			).toBeVisible();
			await expect(
				(await checkoutPage.orderSummaryTableRow(6, '$ 10.00')).row
			).toBeVisible();
			await expect(checkoutPage.commerceShippingAddress).toContainText(
				'John Doe'
			);
			await expect(checkoutPage.commerceShippingAddress).toContainText(
				'testStreet'
			);
			await expect(checkoutPage.commerceShippingAddress).toContainText(
				'testCity'
			);
			await expect(checkoutPage.commerceShippingAddress).toContainText(
				'United States'
			);
			await expect(checkoutPage.commerceBillingAddress).toContainText(
				'John Doe'
			);
			await expect(checkoutPage.commerceBillingAddress).toContainText(
				'testStreet'
			);
			await expect(checkoutPage.commerceBillingAddress).toContainText(
				'testCity'
			);
			await expect(checkoutPage.commerceBillingAddress).toContainText(
				'United States'
			);
			await expect(
				page
					.locator('div .payment-method')
					.locator('.shipping-description')
			).toContainText('Money Order');
			await expect(page.locator('.commerce-subtotal')).toContainText(
				'$ 30.00'
			);
			await expect(page.locator('.commerce-total')).toContainText(
				'$ 30.00'
			);

			await checkoutPage.continueButton.click();

			await expect(page.getByLabel('Checkout')).toContainText(
				'Your order has been processed but not completed yet.'
			);
		});
	}
);

test(
	'Create a full Checkout flow functional test for stable run 3',
	{tag: ['@LPD-56215']},
	async ({
		apiHelpers,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		commerceAdminShipmentsPage,
		page,
		site,
	}) => {
		test.setTimeout(120000);

		let account;
		let catalog;
		let channel;
		let product1;
		let product2;
		let sku1;
		let sku2;

		await test.step('Create a Catalog', async () => {
			catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog(
				{
					name: getRandomString(),
				}
			);
		});

		await test.step('Create two products via API', async () => {
			product1 =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
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

			const productSkus1 = await apiHelpers.headlessCommerceAdminCatalog
				.getProduct(product1.productId)
				.then((product) => {
					return product.skus;
				});

			sku1 = productSkus1[0];

			product2 =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					skus: [
						{
							cost: 0,
							price: 20,
							published: true,
							purchasable: true,
							sku: 'Sku' + getRandomInt(),
						},
					],
				});

			const productSkus2 = await apiHelpers.headlessCommerceAdminCatalog
				.getProduct(product2.productId)
				.then((product) => {
					return product.skus;
				});

			sku2 = productSkus2[0];
		});

		await test.step('Create a Channel and Warehouse', async () => {
			channel = await apiHelpers.headlessCommerceAdminChannel.postChannel(
				{
					siteGroupId: site.id,
				}
			);

			const warehouse =
				await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
					{
						active: true,
						latitude: getRandomInt(),
						longitude: getRandomInt(),
						warehouseItems: [
							{
								quantity: 10,
								sku: sku1.sku,
							},
							{
								quantity: 10,
								sku: sku2.sku,
							},
						],
					}
				);

			await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
				warehouse.id,
				channel.id
			);
		});

		await test.step('Create an account an address and checkout the order', async () => {
			account = await apiHelpers.headlessAdminUser.postAccount({
				name: getRandomString(),
				type: 'business',
			});

			const address =
				await apiHelpers.headlessCommerceAdminAccount.postAddress(
					account.id,
					{phoneNumber: '1234567890', regionISOCode: 'AL'}
				);

			const order = await apiHelpers.headlessCommerceAdminOrder.postOrder(
				{
					accountId: account.id,
					billingAddressId: address.id,
					channelId: channel.id,
					orderItems: [
						{
							quantity: 1,
							skuId: sku1.id,
						},
						{
							quantity: 1,
							skuId: sku2.id,
						},
					],
					paymentMethod: 'paypal',
					shippingAddressId: address.id,
					total: 30,
				}
			);

			const payment =
				await apiHelpers.headlessCommerceAdminPaymentApiHelper.postPayment(
					{
						amount: order.total,
						channelId: channel.id,
						currencyCode: 'USD',
						paymentIntegrationType: 1,
						relatedItemId: order.id,
						relatedItemName:
							'com.liferay.commerce.model.CommerceOrder',
					}
				);

			await apiHelpers.headlessCommerceAdminPaymentApiHelper.patchPayment(
				{
					paymentStatus: 0,
					relatedItemId: payment.relatedItemId,
				},
				payment.id
			);

			await apiHelpers.headlessCommerceAdminOrder.patchOrder(order.id, {
				orderStatus: '1',
			});
		});

		await test.step('Go to Orders and accept the order', async () => {
			await commerceAdminOrdersPage.goto();

			await expect(
				(await commerceAdminOrdersPage.tableRow(5, '$ 30.00')).row
			).toBeVisible();
			await expect(
				(await commerceAdminOrdersPage.tableRow(7, 'Pending')).row
			).toBeVisible();

			await commerceAdminOrdersPage
				.menuActionButton(account.name)
				.click();
			await commerceAdminOrdersPage.menuItemAction('View').click();

			const orderId = parseInt(
				await commerceAdminOrderDetailsPage.orderId.textContent(),
				10
			);

			apiHelpers.data.push({id: orderId, type: 'order'});

			await commerceAdminOrdersPage
				.orderStatusLink('Accept Order')
				.click();

			await waitForAlert(page);
		});

		await test.step('Create a shipment and assert that the order is completed', async () => {
			await commerceAdminOrdersPage
				.orderStatusLink('Create Shipment')
				.click();

			await expect(
				page.getByText('Processing', {exact: true})
			).toBeVisible();

			await commerceAdminShipmentsPage.addProductsToShipment.click();
			await (
				await commerceAdminShipmentsPage.shipmentItemsTableRowAction(
					1,
					sku1.sku
				)
			).check();
			await (
				await commerceAdminShipmentsPage.shipmentItemsTableRowAction(
					1,
					sku2.sku
				)
			).check();
			await commerceAdminShipmentsPage.shipmentsItemSubmitButton.click();
			await commerceAdminShipmentsPage.productsSkuLink(sku1.sku).click();
			await commerceAdminShipmentsPage.addQuantityInShipment.fill('1');
			await commerceAdminShipmentsPage.editProductSaveButton.click();

			await waitForAlert(page.frameLocator('iframe'));

			await commerceAdminShipmentsPage.editProductCloseButton.click();

			await expect(
				commerceAdminShipmentsPage.addQuantityInShipment
			).not.toBeVisible();

			await commerceAdminShipmentsPage.productsSkuLink(sku2.sku).click();

			await expect(
				commerceAdminShipmentsPage.addQuantityInShipment
			).toBeVisible();

			await commerceAdminShipmentsPage.addQuantityInShipment.fill('1');
			await commerceAdminShipmentsPage.editProductSaveButton.click();

			await waitForAlert(page.frameLocator('iframe'));

			await commerceAdminShipmentsPage.editProductCloseButton.click();
			await commerceAdminShipmentsPage
				.shipmentStatusLink('Finish Processing')
				.click();

			await waitForAlert(page);

			await commerceAdminShipmentsPage.shipmentStatusLink('Ship').click();

			await waitForAlert(page);

			await commerceAdminShipmentsPage
				.shipmentStatusLink('Deliver')
				.click();

			await waitForAlert(page);

			await commerceAdminOrdersPage.goto();

			await expect(
				(await commerceAdminOrdersPage.tableRow(7, 'Completed')).row
			).toBeVisible();
		});
	}
);

test(
	'Can set default address from account details tab',
	{tag: ['@COMMERCE-9873', '@LPD-32494']},
	async ({
		accountsPage,
		apiHelpers,
		applicationsMenuPage,
		checkoutPage,
		commerceAccountManagementPage,
		commerceThemeMiniumCatalogPage,
		commerceThemeMiniumPage,
		editAccountChannelDefaultsPage,
		editAccountPage,
		page,
	}) => {
		test.setTimeout(200000);

		const initiateCheckout = async (site: string) => {
			await expect(async () => {
				await applicationsMenuPage.goToSite(site);

				await page.waitForLoadState('networkidle');

				await commerceThemeMiniumCatalogPage.firstCardItemAddToCartButton.click();
				await commerceThemeMiniumPage.miniCartButton.click();

				await expect(
					commerceThemeMiniumPage.miniCartSubmitButton
				).toBeEnabled({timeout: 1000});
			}).toPass();

			await commerceThemeMiniumPage.miniCartSubmitButton.click();
			await checkoutPage.useAsBillingCheckbox.setChecked(false);
		};

		const verifyAddressAtOrderSummaryCheckoutStep = async (
			billingAddress,
			shippingAddress
		) => {
			await expect(checkoutPage.commerceBillingAddress).toBeVisible();
			await expect(checkoutPage.commerceBillingAddress).toContainText(
				billingAddress.city
			);
			await expect(checkoutPage.commerceBillingAddress).toContainText(
				billingAddress.name
			);
			await expect(checkoutPage.commerceBillingAddress).toContainText(
				billingAddress.street1
			);
			await expect(checkoutPage.commerceBillingAddress).toContainText(
				billingAddress.street2
			);
			await expect(checkoutPage.commerceShippingAddress).toContainText(
				shippingAddress.city
			);
			await expect(checkoutPage.commerceShippingAddress).toContainText(
				shippingAddress.name
			);
			await expect(checkoutPage.commerceShippingAddress).toContainText(
				shippingAddress.street1
			);
			await expect(checkoutPage.commerceShippingAddress).toContainText(
				shippingAddress.street2
			);
		};

		const setDefaultAddresses = async (
			account,
			billingAddress,
			shippingAddress
		) => {
			await accountsPage.goto();

			await commerceAccountManagementPage
				.accountsTableRowLink(account.name)
				.click();

			await expect(
				editAccountPage.setBillingDefaultAddressButton
			).toBeVisible();

			await editAccountPage.setBillingDefaultAddressButton.click();

			await expect(
				await editAccountPage.accountEntryAddressesTableRowRadioButton(
					billingAddress.name
				)
			).toBeVisible();

			await (
				await editAccountPage.accountEntryAddressesTableRowRadioButton(
					billingAddress.name
				)
			).click();
			await editAccountPage.setDefaultAddressFrameSaveButton.click();

			await waitForAlert(page);

			await editAccountPage.setShippingDefaultAddressButton.click();

			await expect(
				await editAccountPage.accountEntryAddressesTableRowRadioButton(
					shippingAddress.name
				)
			).toBeVisible();

			await (
				await editAccountPage.accountEntryAddressesTableRowRadioButton(
					shippingAddress.name
				)
			).click();
			await editAccountPage.setDefaultAddressFrameSaveButton.click();

			await waitForAlert(page);
		};

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const billingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name: 'Billing Address' + getRandomInt(),
					type: 1,
				}
			);

		apiHelpers.data.push({id: billingAddress.id, type: 'address'});

		const billingAndShippingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name: 'Billing and Shipping Address' + getRandomInt(),
					type: 2,
				}
			);

		apiHelpers.data.push({
			id: billingAndShippingAddress.id,
			type: 'address',
		});

		const shippingAddress =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{
					defaultBilling: false,
					defaultShipping: false,
					name: 'Shipping Address' + getRandomInt(),
					type: 3,
				}
			);

		apiHelpers.data.push({id: shippingAddress.id, type: 'address'});

		const siteName1 = 'Minium-' + getRandomInt();
		const siteName2 = 'Minium-' + getRandomInt();

		await miniumSetUp(apiHelpers, siteName1);
		await miniumSetUp(apiHelpers, siteName2);

		await setDefaultAddresses(account, billingAddress, shippingAddress);

		await editAccountPage.channelDefaultsLink.click();

		await expect(
			editAccountChannelDefaultsPage.billingAddressAllChannelsText
		).toBeVisible();
		await expect(
			editAccountChannelDefaultsPage.shippingAddressAllChannelsText
		).toBeVisible();
		await expect(
			await editAccountChannelDefaultsPage.addressTableRowColumn(
				1,
				'Billing',
				billingAddress.name
			)
		).toBeVisible();
		await expect(
			await editAccountChannelDefaultsPage.addressTableRowColumn(
				1,
				'Shipping',
				shippingAddress.name
			)
		).toBeVisible();

		await (
			await editAccountChannelDefaultsPage.addressTableRowColumn(
				3,
				'Billing',
				billingAddress.name
			)
		).click();
		await editAccountChannelDefaultsPage.deleteMenuItem.click();

		await waitForAlert(page);

		await (
			await editAccountChannelDefaultsPage.addressTableRowColumn(
				3,
				'Shipping',
				shippingAddress.name
			)
		).click();
		await editAccountChannelDefaultsPage.deleteMenuItem.click();

		await waitForAlert(page);

		await editAccountChannelDefaultsPage.addDefaultBillingAddressButton.click();

		await expect(
			editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu
		).toBeVisible();

		await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu.selectOption(
			siteName1 + ' Portal'
		);
		await editAccountChannelDefaultsPage.setDefaultBillingAddressFrameBillingAddressDropdownMenu.selectOption(
			billingAddress.name
		);
		await editAccountChannelDefaultsPage.setDefaultAddressFrameSaveButton.click();
		await editAccountChannelDefaultsPage.addDefaultShippingAddressButton.click();

		await expect(
			editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu
		).toBeVisible();

		await editAccountChannelDefaultsPage.setDefaultAddressFrameChannelDropdownMenu.selectOption(
			siteName1 + ' Portal'
		);
		await editAccountChannelDefaultsPage.setDefaultShippingAddressFrameBillingAddressDropdownMenu.selectOption(
			shippingAddress.name
		);
		await editAccountChannelDefaultsPage.setDefaultAddressFrameSaveButton.click();

		await expect(
			await editAccountChannelDefaultsPage.addressTableRowColumn(
				0,
				'Billing',
				siteName1
			)
		).toBeVisible();
		await expect(
			await editAccountChannelDefaultsPage.addressTableRowColumn(
				0,
				'Shipping',
				siteName1
			)
		).toBeVisible();

		try {
			await initiateCheckout(siteName1);

			await checkoutPage.performCheckoutUntilStep('Order Summary');

			await verifyAddressAtOrderSummaryCheckoutStep(
				billingAddress,
				shippingAddress
			);

			await setDefaultAddresses(
				account,
				billingAndShippingAddress,
				billingAndShippingAddress
			);

			await editAccountPage.channelDefaultsLink.click();

			await expect(
				editAccountChannelDefaultsPage.billingAddressAllOtherChannelsText
			).toBeVisible();
			await expect(
				editAccountChannelDefaultsPage.shippingAddressAllOtherChannelsText
			).toBeVisible();

			await initiateCheckout(siteName2);

			await checkoutPage.performCheckoutUntilStep('Order Summary');

			await verifyAddressAtOrderSummaryCheckoutStep(
				billingAndShippingAddress,
				billingAndShippingAddress
			);
		}
		finally {
			const orders =
				await apiHelpers.headlessCommerceAdminOrder.getOrdersPage();

			if (orders && orders.items) {
				for (const order of orders.items) {
					await apiHelpers.headlessCommerceAdminOrder.deleteOrder(
						order.id
					);
				}
			}
		}
	}
);
