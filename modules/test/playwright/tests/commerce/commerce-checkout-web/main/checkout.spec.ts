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
import {waitForAlert} from '../../../../utils/waitForAlert';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {getDateFormatted, setFutureDate} from '../../utils/date';

export const test = mergeTests(
	applicationsMenuPageTest,
	accountsPagesTest,
	commercePagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPD-20379': {enabled: true},
		'LPD-43000': {enabled: true},
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
				name: getRandomString(),
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
				name: getRandomString(),
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
				name: getRandomString(),
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
				name: getRandomString(),
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
