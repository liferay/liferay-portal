/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {loginTest} from '../../fixtures/loginTest';
import {systemSettingsPageTest} from '../../fixtures/systemSettingsPageTest';
import {liferayConfig} from '../../liferay.config';
import {getRandomInt} from '../../utils/getRandomInt';
import getRandomString from '../../utils/getRandomString';
import performLogin, {performLogout} from '../../utils/performLogin';
import {waitForAlert} from '../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-203351': true,
	}),
	loginTest(),
	systemSettingsPageTest
);

test('LPD-25926 Display page template edit mode works with Speedwell theme', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceLayoutsPage,
	page,
}) => {
	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const displayPageTemplateName = getRandomString();

	await applicationsMenuPage.goToSite(site.name);

	await commerceLayoutsPage.goToDisplayPageTemplates();
	await commerceLayoutsPage.createDisplayPageTemplate(
		displayPageTemplateName,
		'Blogs Entry',
		site.name
	);
	await commerceLayoutsPage.addFragment('Heading');

	await expect(page.getByText('Heading Example')).toBeVisible();

	await commerceLayoutsPage.publishButton.click();
	await commerceLayoutsPage.configureDisplayPageTemplateTheme(
		'Select Speedwell By Liferay, Inc.'
	);

	await expect(
		page.getByText('Success:The page was updated successfully.')
	).toBeVisible();

	await commerceLayoutsPage.backLink.click();

	await commerceLayoutsPage
		.displayPageTemplateLink(displayPageTemplateName)
		.click();

	await expect(page.getByText('Heading Example')).toBeVisible();
});

test('LPD-33439 Default order display page template is accessible via friendly URL', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceLayoutsPage,
	page,
	systemSettingsPage,
}) => {
	try {
		await systemSettingsPage.goToSystemSetting(
			'Feature Flags',
			'Developer'
		);

		if (!(await page.getByLabel('COMMERCE-9410').isChecked())) {
			await page.getByLabel('COMMERCE-9410').click();
		}

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		apiHelpers.data.push({id: account.id, type: 'account'});

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		await applicationsMenuPage.goToSite(site.name);

		await commerceLayoutsPage.goToDisplayPageTemplates();
		await commerceLayoutsPage.createDisplayPageTemplate(
			getRandomString(),
			'Order',
			site.name
		);
		await commerceLayoutsPage.addFragment('Heading');

		await expect(page.getByText('Heading Example')).toBeVisible();

		await commerceLayoutsPage.publishButton.click();
		await commerceLayoutsPage.moreActionsButton.click();
		await commerceLayoutsPage.markAsDefaultMenuItem.click();

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

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
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

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(page.getByText('Heading Example')).toBeVisible();
	}
	finally {
		await systemSettingsPage.goToSystemSetting(
			'Feature Flags',
			'Developer'
		);

		if (await page.getByLabel('COMMERCE-9410').isChecked()) {
			await page.getByLabel('COMMERCE-9410').click();
		}
	}
});

test('LPD-32227 Order info box fragment configuration', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceLayoutsPage,
	page,
	systemSettingsPage,
}) => {
	try {
		await systemSettingsPage.goToSystemSetting(
			'Feature Flags',
			'Developer'
		);

		if (!(await page.getByLabel('COMMERCE-9410').isChecked())) {
			await page.getByLabel('COMMERCE-9410').click();
		}

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		apiHelpers.data.push({id: account.id, type: 'account'});

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		await applicationsMenuPage.goToSite(site.name);

		await commerceLayoutsPage.goToDisplayPageTemplates();
		await commerceLayoutsPage.createDisplayPageTemplate(
			getRandomString(),
			'Order',
			site.name
		);
		await commerceLayoutsPage.addFragment('Info Box', 'Order');
		await commerceLayoutsPage.infoBoxReadOnlyToggle.uncheck();

		await expect(
			page.getByText(
				'The info box component is not correctly configured.'
			)
		).toBeVisible();

		await commerceLayoutsPage.infoBoxFieldSelect.selectOption(
			'purchaseOrderNumber'
		);

		await expect(
			page.getByText(
				'The info box component is not correctly configured.'
			)
		).toBeHidden();

		await commerceLayoutsPage.infoBoxLabelInput.fill('PON');
		await commerceLayoutsPage.publishButton.click();

		await waitForAlert(
			page,
			'The display page template was published successfully.'
		);

		await commerceLayoutsPage.moreActionsButton.click();
		await commerceLayoutsPage.markAsDefaultMenuItem.click();

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

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
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

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(page.getByText('PON')).toBeVisible();

		await commerceLayoutsPage.infoBoxButton('PON').click();
		await commerceLayoutsPage.inputTextbox('PON').fill('testPON');
		await commerceLayoutsPage.saveButton.click();

		await expect(page.getByText('testPON')).toBeVisible();

		await commerceLayoutsPage.infoBoxButton('PON').click();
		await commerceLayoutsPage.inputTextbox('PON').fill('testPONEdited');
		await commerceLayoutsPage.saveButton.click();

		await expect(page.getByText('testPONEdited')).toBeVisible();

		await commerceLayoutsPage.goToDisplayPageTemplates();
		await commerceLayoutsPage.moreActionsButton.click();
		await commerceLayoutsPage.editMenuItem.click();
		await commerceLayoutsPage.firstFragment.click();
		await commerceLayoutsPage.infoBoxFieldSelect.selectOption(
			'accountInfo'
		);

		await expect(
			page.getByText(
				'The info box component is not correctly configured.'
			)
		).toBeVisible();

		await commerceLayoutsPage.infoBoxReadOnlyToggle.check();

		await expect(
			page.getByText(
				'The info box component is not correctly configured.'
			)
		).toBeHidden();

		await commerceLayoutsPage.infoBoxLabelInput.fill('Account Info');
		await commerceLayoutsPage.publishButton.click();

		await waitForAlert(
			page,
			'The display page template was published successfully.'
		);

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(page.getByText('Account Info')).toBeVisible();
		await expect(page.getByText(account.name)).toBeVisible();
		await expect(page.getByText(String(account.id))).toBeVisible();
		await expect(commerceLayoutsPage.infoBoxButton('PON')).toBeHidden();
	}
	finally {
		await systemSettingsPage.goToSystemSetting(
			'Feature Flags',
			'Developer'
		);

		if (await page.getByLabel('COMMERCE-9410').isChecked()) {
			await page.getByLabel('COMMERCE-9410').click();
		}
	}
});

test('LPD-32236 Order Step Tracker fragment configuration', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceLayoutsPage,
	page,
	systemSettingsPage,
}) => {
	try {
		await systemSettingsPage.goToSystemSetting(
			'Feature Flags',
			'Developer'
		);

		await page.getByLabel('COMMERCE-9410').click();

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		apiHelpers.data.push({id: account.id, type: 'account'});

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		await applicationsMenuPage.goToSite(site.name);

		await commerceLayoutsPage.goToDisplayPageTemplates();
		await commerceLayoutsPage.createDisplayPageTemplate(
			getRandomString(),
			'Order',
			site.name
		);
		await commerceLayoutsPage.addFragment('Step Tracker', 'Order');
		await commerceLayoutsPage.publishButton.click();

		await waitForAlert(
			page,
			'The display page template was published successfully.'
		);

		await commerceLayoutsPage.moreActionsButton.click();
		await commerceLayoutsPage.markAsDefaultMenuItem.click();

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

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
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

		await apiHelpers.headlessCommerceDeliveryCart.checkoutCart(cart.id);

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(
			commerceLayoutsPage.stepTrackerItem('Pending')
		).toHaveClass(/active/);
		await expect(
			commerceLayoutsPage.stepTrackerItem('Processing')
		).not.toHaveClass(/active/);
		await expect(
			commerceLayoutsPage.stepTrackerItem('Shipped')
		).not.toHaveClass(/active/);
		await expect(
			commerceLayoutsPage.stepTrackerItem('Completed')
		).not.toHaveClass(/active/);
	}
	finally {
		await systemSettingsPage.goToSystemSetting(
			'Feature Flags',
			'Developer'
		);

		if (await page.getByLabel('COMMERCE-9410').isChecked()) {
			await page.getByLabel('COMMERCE-9410').click();
		}
	}
});

test('LPD-32232 Edit Requested Delivery Date in Open Order Details', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceLayoutsPage,
	page,
	systemSettingsPage,
}) => {
	try {
		await systemSettingsPage.goToSystemSetting(
			'Feature Flags',
			'Developer'
		);

		if (!(await page.getByLabel('COMMERCE-9410').isChecked())) {
			await page.getByLabel('COMMERCE-9410').click();
		}

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		apiHelpers.data.push({id: account.id, type: 'account'});

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		await applicationsMenuPage.goToSite(site.name);

		await commerceLayoutsPage.goToDisplayPageTemplates();
		await commerceLayoutsPage.createDisplayPageTemplate(
			getRandomString(),
			'Order',
			site.name
		);
		await commerceLayoutsPage.addFragment('Info Box', 'Order');
		await commerceLayoutsPage.infoBoxReadOnlyToggle.uncheck();

		await expect(
			page.getByText(
				'The info box component is not correctly configured.'
			)
		).toBeVisible();

		await commerceLayoutsPage.infoBoxFieldSelect.selectOption(
			'requestedDeliveryDate'
		);

		await expect(
			page.getByText(
				'The info box component is not correctly configured.'
			)
		).toBeHidden();

		await commerceLayoutsPage.infoBoxLabelInput.fill(
			'Requested Delivery Date'
		);
		await commerceLayoutsPage.publishButton.click();

		await waitForAlert(
			page,
			'The display page template was published successfully.'
		);

		await commerceLayoutsPage.moreActionsButton.click();
		await commerceLayoutsPage.markAsDefaultMenuItem.click();

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

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
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

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(page.getByText('Requested Delivery Date')).toBeVisible();

		await commerceLayoutsPage
			.infoBoxButton('Requested Delivery Date')
			.click();
		await commerceLayoutsPage
			.inputTextbox('Requested Delivery Date')
			.fill('2024-09-11');
		await commerceLayoutsPage.saveButton.click();

		await expect(page.getByText('9/11/24', {exact: true})).toBeVisible();

		await commerceLayoutsPage
			.infoBoxButton('Requested Delivery Date')
			.click();
		await commerceLayoutsPage
			.inputTextbox('Requested Delivery Date')
			.fill('2024-09-13');
		await commerceLayoutsPage.saveButton.click();

		await expect(page.getByText('9/13/24', {exact: true})).toBeVisible();

		await apiHelpers.headlessCommerceDeliveryCart.checkoutCart(cart.id);

		await page.reload();

		await expect(
			commerceLayoutsPage.infoBoxButton('Requested Delivery Date')
		).toBeHidden();
	}
	finally {
		await systemSettingsPage.goToSystemSetting(
			'Feature Flags',
			'Developer'
		);

		if (await page.getByLabel('COMMERCE-9410').isChecked()) {
			await page.getByLabel('COMMERCE-9410').click();
		}
	}
});

test('LPD-33808 Edit Shipping Method in Open Order Details', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminChannelsPage,
	commerceLayoutsPage,
	page,
	systemSettingsPage,
}) => {
	test.setTimeout(180000);

	try {
		await systemSettingsPage.goToSystemSetting(
			'Feature Flags',
			'Developer'
		);

		if (!(await page.getByLabel('COMMERCE-9410').isChecked())) {
			await page.getByLabel('COMMERCE-9410').click();
		}

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		await applicationsMenuPage.goToSite(site.name);

		await commerceLayoutsPage.goToDisplayPageTemplates();
		await commerceLayoutsPage.createDisplayPageTemplate(
			getRandomString(),
			'Order',
			site.name
		);
		await commerceLayoutsPage.addFragment('Info Box', 'Order');
		await commerceLayoutsPage.infoBoxReadOnlyToggle.uncheck();

		await expect(
			page.getByText(
				'The info box component is not correctly configured.'
			)
		).toBeVisible();

		await commerceLayoutsPage.infoBoxFieldSelect.selectOption(
			'shippingMethod'
		);
		await commerceLayoutsPage.infoBoxLabelInput.fill('Shipping Method');

		await expect(
			page.getByText(
				'The info box component is not correctly configured.'
			)
		).toBeHidden();

		await commerceLayoutsPage.publishButton.click();

		await waitForAlert(
			page,
			'The display page template was published successfully.'
		);

		await commerceLayoutsPage.moreActionsButton.click();
		await commerceLayoutsPage.markAsDefaultMenuItem.click();

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

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		apiHelpers.data.push({id: account.id, type: 'account'});

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
						quantity: 1,
						skuId: sku.id,
					},
				],
				shippingAddressId: address.id,
			},
			channel.id
		);

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(
			commerceLayoutsPage.infoBoxButton('Shipping Method')
		).toBeVisible();

		await commerceLayoutsPage.infoBoxButton('Shipping Method').click();

		await expect(
			commerceLayoutsPage.infoBoxShippingMethodAlert
		).toBeVisible();

		await commerceLayoutsPage.infoBoxCancelButton.click();

		const shippingOptions = [getRandomString(), getRandomString()];

		await commerceAdminChannelsPage.setupCommerceChannelShippingMethod(
			channel.name,
			'Flat Rate',
			shippingOptions
		);

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(
			commerceLayoutsPage.infoBoxButton('Shipping Method')
		).toBeVisible();

		await commerceLayoutsPage.infoBoxButton('Shipping Method').click();

		await expect(
			commerceLayoutsPage.infoBoxValue(shippingOptions[0])
		).toHaveCount(0);

		await commerceLayoutsPage.infoBoxShippingMethodSelect.selectOption(
			'Flat Rate'
		);

		await expect(
			commerceLayoutsPage.infoBoxValue(shippingOptions[0])
		).toBeVisible();

		await commerceLayoutsPage.infoBoxValue(shippingOptions[0]).click();
		await commerceLayoutsPage.saveButton.click();

		await expect(
			commerceLayoutsPage.infoBoxValue(
				'Flat Rate - ' + shippingOptions[0]
			)
		).toBeVisible();

		await commerceLayoutsPage.infoBoxButton('Shipping Method').click();
		await commerceLayoutsPage.infoBoxValue(shippingOptions[1]).click();
		await commerceLayoutsPage.saveButton.click();

		await expect(
			commerceLayoutsPage.infoBoxValue(
				'Flat Rate - ' + shippingOptions[1]
			)
		).toBeVisible();

		await page.reload();

		await expect(
			commerceLayoutsPage.infoBoxValue(
				'Flat Rate - ' + shippingOptions[1]
			)
		).toBeVisible();

		await apiHelpers.headlessCommerceDeliveryCart.checkoutCart(cart.id);

		await page.reload();

		await expect(
			commerceLayoutsPage.infoBoxButton('Shipping Method')
		).toBeHidden();
	}
	finally {
		await systemSettingsPage.goToSystemSetting(
			'Feature Flags',
			'Developer'
		);

		if (await page.getByLabel('COMMERCE-9410').isChecked()) {
			await page.getByLabel('COMMERCE-9410').click();
		}
	}
});

test('LPD-33809 Edit Payment Method in Open Order Details', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminChannelDetailsPage,
	commerceAdminChannelsPage,
	commerceLayoutsPage,
	page,
	systemSettingsPage,
}) => {
	test.setTimeout(180000);

	try {
		await systemSettingsPage.goToSystemSetting(
			'Feature Flags',
			'Developer'
		);

		if (!(await page.getByLabel('COMMERCE-9410').isChecked())) {
			await page.getByLabel('COMMERCE-9410').click();
		}

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		await applicationsMenuPage.goToSite(site.name);

		await commerceLayoutsPage.goToDisplayPageTemplates();
		await commerceLayoutsPage.createDisplayPageTemplate(
			getRandomString(),
			'Order',
			site.name
		);
		await commerceLayoutsPage.addFragment('Info Box', 'Order');
		await commerceLayoutsPage.infoBoxReadOnlyToggle.uncheck();

		await expect(
			page.getByText(
				'The info box component is not correctly configured.'
			)
		).toBeVisible();

		await commerceLayoutsPage.infoBoxFieldSelect.selectOption(
			'paymentMethod'
		);
		await commerceLayoutsPage.infoBoxLabelInput.fill('Payment Method');

		await expect(
			page.getByText(
				'The info box component is not correctly configured.'
			)
		).toBeHidden();

		await commerceLayoutsPage.publishButton.click();

		await waitForAlert(
			page,
			'The display page template was published successfully.'
		);

		await commerceLayoutsPage.moreActionsButton.click();
		await commerceLayoutsPage.markAsDefaultMenuItem.click();

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

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		apiHelpers.data.push({id: account.id, type: 'account'});

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
						quantity: 1,
						skuId: sku.id,
					},
				],
				shippingAddressId: address.id,
			},
			channel.id
		);

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(
			commerceLayoutsPage.infoBoxButton('Payment Method')
		).toBeVisible();

		await commerceLayoutsPage.infoBoxButton('Payment Method').click();

		await expect(
			commerceLayoutsPage.infoBoxShippingMethodAlert
		).toBeVisible();

		await commerceLayoutsPage.infoBoxCancelButton.click();

		const paymentMethod1 = 'Money Order';
		const paymentMethod2 = 'PayPal';

		await commerceAdminChannelsPage.goto();
		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			paymentMethod1,
			'Payment Methods'
		);
		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				paymentMethod1
			)
		).click();
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			paymentMethod2,
			'Payment Methods'
		);
		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				paymentMethod2
			)
		).click();

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(
			commerceLayoutsPage.infoBoxButton('Payment Method')
		).toBeVisible();

		await commerceLayoutsPage.infoBoxButton('Payment Method').click();
		await commerceLayoutsPage.infoBoxValue(paymentMethod1).click();
		await commerceLayoutsPage.saveButton.click();

		await expect(
			commerceLayoutsPage.infoBoxValue(paymentMethod1)
		).toBeVisible();

		await commerceLayoutsPage.infoBoxButton('Payment Method').click();
		await commerceLayoutsPage.infoBoxValue(paymentMethod2).click();
		await commerceLayoutsPage.saveButton.click();

		await expect(
			commerceLayoutsPage.infoBoxValue(paymentMethod2)
		).toBeVisible();

		await page.reload();

		await expect(
			commerceLayoutsPage.infoBoxValue(paymentMethod2)
		).toBeVisible();

		await apiHelpers.headlessCommerceDeliveryCart.checkoutCart(cart.id);

		await page.reload();

		await expect(
			commerceLayoutsPage.infoBoxButton('Payment Method')
		).toBeHidden();
	}
	finally {
		await systemSettingsPage.goToSystemSetting(
			'Feature Flags',
			'Developer'
		);

		if (await page.getByLabel('COMMERCE-9410').isChecked()) {
			await page.getByLabel('COMMERCE-9410').click();
		}
	}
});

test('LPD-35558 Order Details - Order Summary', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminDiscountsPage,
	commerceLayoutsPage,
	page,
	systemSettingsPage,
}) => {
	try {
		await systemSettingsPage.goToSystemSetting(
			'Feature Flags',
			'Developer'
		);

		await page.getByLabel('COMMERCE-9410').click();

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		apiHelpers.data.push({id: account.id, type: 'account'});

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		await applicationsMenuPage.goToSite(site.name);

		await commerceLayoutsPage.goToDisplayPageTemplates();
		await commerceLayoutsPage.createDisplayPageTemplate(
			getRandomString(),
			'Order',
			site.name
		);
		await commerceLayoutsPage.addFragment('Info Box', 'Order');

		await commerceLayoutsPage.infoBoxReadOnlyToggle.check();

		await commerceLayoutsPage.infoBoxFieldSelect.selectOption(
			'orderSummary'
		);

		await commerceLayoutsPage.infoBoxLabelInput.fill('Order Summary');

		const product =
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

		const sku = product.skus[0];

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
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

		const discount =
			await apiHelpers.headlessCommerceAdminPricing.postDiscount({
				couponCode: getRandomString(),
				percentageLevel1: 10,
				target: 'subtotal',
				useCouponCode: true,
				usePercentage: true,
			});

		await apiHelpers.headlessCommerceAdminPricing.postDiscount({
			percentageLevel1: 10,
			target: 'total',
			usePercentage: true,
		});

		await commerceLayoutsPage.addWidget('Coupon Code Entry', 'Commerce');

		await commerceLayoutsPage.publishButton.click();

		await waitForAlert(
			page,
			'The display page template was published successfully.'
		);

		await commerceLayoutsPage.moreActionsButton.click();
		await commerceLayoutsPage.markAsDefaultMenuItem.click();

		await waitForAlert(page);

		await expect(
			commerceLayoutsPage.defaultDisplayPageTemplateIcon
		).toBeVisible();

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(page.getByText('Order Summary')).toBeVisible();

		await commerceAdminDiscountsPage.enterPromoCodeToWidget(
			discount.couponCode
		);

		await commerceLayoutsPage.checkValueOrderSummary('Subtotal', '$ 20.00');
		await commerceLayoutsPage.checkValueOrderSummary(
			'Subtotal Discount',
			'$ 2.00'
		);
		await commerceLayoutsPage.checkValueOrderSummary(
			'Total Discount',
			discount.couponCode
		);

		await expect(page.getByText('Total', {exact: true})).toBeVisible();
		await expect(page.getByText('$ 16.20')).toBeVisible();
	}
	finally {
		await systemSettingsPage.goToSystemSetting(
			'Feature Flags',
			'Developer'
		);

		if (await page.getByLabel('COMMERCE-9410').isChecked()) {
			await page.getByLabel('COMMERCE-9410').click();
		}
	}
});

test('LPD-32237 Order actions and redirect fragments', async ({
	apiHelpers,
	applicationsMenuPage,
	checkoutPage,
	commerceAdminChannelsPage,
	commerceLayoutsPage,
	page,
	systemSettingsPage,
}) => {
	test.setTimeout(180000);

	try {
		await systemSettingsPage.goToSystemSetting(
			'Feature Flags',
			'Developer'
		);

		if (!(await page.getByLabel('COMMERCE-9410').isChecked())) {
			await page.getByLabel('COMMERCE-9410').click();
		}

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		apiHelpers.data.push({id: account.id, type: 'account'});

		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['demo.unprivileged@liferay.com']
		);
		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'demo.unprivileged@liferay.com'
			);

		const siteRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

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
		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteRole.id,
			site.id,
			user.id
		);

		await applicationsMenuPage.goToSite(site.name);

		await commerceLayoutsPage.goToDisplayPageTemplates();
		await commerceLayoutsPage.createDisplayPageTemplate(
			getRandomString(),
			'Order',
			site.name
		);
		await commerceLayoutsPage.addFragment('Heading');

		await page.getByText('Heading Example', {exact: true}).dblclick();
		await page.getByLabel('Field').selectOption('CommerceOrder_orderId');

		await commerceLayoutsPage.addFragment('Order Actions', 'Order');

		await expect(
			page.getByText('The order actions component will be shown here.')
		).toBeVisible();

		await commerceLayoutsPage.publishButton.click();

		await waitForAlert(
			page,
			'The display page template was published successfully.'
		);

		await commerceLayoutsPage.moreActionsButton.click();
		await commerceLayoutsPage.markAsDefaultMenuItem.click();

		await waitForAlert(page);

		await expect(
			commerceLayoutsPage.defaultDisplayPageTemplateIcon
		).toBeVisible();

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.fixCommerceChannelIssue(
			['Checkout'],
			channel.name
		);
		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B',
			true
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const sku = product.skus[0];

		await performLogout(page);

		await performLogin(page, 'demo.unprivileged');

		const cart1 = await apiHelpers.headlessCommerceDeliveryCart.postCart(
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

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart1.id}`
		);

		await expect(
			page.getByRole('heading', {name: String(cart1.id)}).first()
		).toBeVisible();

		await commerceLayoutsPage.expectOrderActionButtons({checkoutCount: 1});
		await commerceLayoutsPage.orderActionsButton('Checkout').click();

		await checkoutPage.performCheckout({
			shippingAddress: {
				city: 'testCity',
				countryLabel: 'United States',
				name: user.name,
				regionLabel: 'Florida',
				street: 'testStreet',
				zip: '12345',
			},
		});

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart1.id}`
		);

		await expect(
			page.getByRole('heading', {name: String(cart1.id)}).first()
		).toBeVisible();

		await commerceLayoutsPage.expectOrderActionButtons({reorderCount: 1});

		await performLogout(page);

		await performLogin(page, 'test');

		await commerceAdminChannelsPage.changeCommerceChannelBuyerOrderApprovalWorkflow(
			'Single Approver (Version 1)',
			channel.name
		);
		await commerceAdminChannelsPage.changeCommerceChannelSellerOrderAcceptanceWorkflow(
			'Single Approver (Version 1)',
			channel.name,
			true
		);

		await performLogout(page);

		await performLogin(page, 'demo.unprivileged');

		const cart2 = await apiHelpers.headlessCommerceDeliveryCart.postCart(
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

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart2.id}`
		);

		await expect(
			page.getByRole('heading', {name: String(cart2.id)}).first()
		).toBeVisible();

		await commerceLayoutsPage.expectOrderActionButtons({submitCount: 1});
		await commerceLayoutsPage.orderActionsButton('Submit').click();

		await expect(
			page.getByRole('heading', {name: String(cart2.id)}).first()
		).toBeVisible();

		await commerceLayoutsPage.expectOrderActionButtons({});

		await performLogout(page);

		await performLogin(page, 'test');

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart2.id}`
		);

		await expect(
			page.getByRole('heading', {name: String(cart2.id)}).first()
		).toBeVisible();

		await commerceLayoutsPage.expectOrderActionButtons({
			approveCount: 1,
			rejectCount: 1,
		});
		await commerceLayoutsPage.orderActionsButton('Approve').click();

		await expect(
			page.getByRole('heading', {name: String(cart2.id)}).first()
		).toBeVisible();

		await commerceLayoutsPage.expectOrderActionButtons({checkoutCount: 1});

		await performLogout(page);

		await performLogin(page, 'demo.unprivileged');

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart2.id}`
		);

		await expect(
			page.getByRole('heading', {name: String(cart2.id)}).first()
		).toBeVisible();

		await commerceLayoutsPage.expectOrderActionButtons({checkoutCount: 1});
		await commerceLayoutsPage.orderActionsButton('Checkout').click();

		await checkoutPage.performCheckout(
			{
				shippingAddress: {
					city: 'testCity',
					countryLabel: 'United States',
					name: user.name,
					regionLabel: 'California',
					street: 'testStreet',
					zip: '12345',
				},
			},
			async (activeStep: string) => {
				if (activeStep.includes('Order Confirmation')) {
					await page.waitForTimeout(1000);
				}
			}
		);

		await performLogout(page);

		await performLogin(page, 'test');

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart2.id}`
		);

		await expect(
			page.getByRole('heading', {name: String(cart2.id)}).first()
		).toBeVisible();

		await commerceLayoutsPage.expectOrderActionButtons({
			approveCount: 1,
			rejectCount: 1,
			reorderCount: 1,
		});
		await commerceLayoutsPage.orderActionsButton('Approve').click();

		await expect(
			page.getByRole('heading', {name: String(cart2.id)}).first()
		).toBeVisible();

		await commerceLayoutsPage.expectOrderActionButtons({reorderCount: 1});

		await performLogout(page);

		await performLogin(page, 'demo.unprivileged');

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart2.id}`
		);

		await expect(
			page.getByRole('heading', {name: String(cart2.id)}).first()
		).toBeVisible();

		await commerceLayoutsPage.expectOrderActionButtons({reorderCount: 1});
		await commerceLayoutsPage.orderActionsButton('Reorder').click();

		await expect(
			page.getByRole('heading', {name: String(cart2.id)})
		).toHaveCount(0);

		await commerceLayoutsPage.expectOrderActionButtons({submitCount: 1});
	}
	finally {
		await page.goto('/');

		if (await page.getByRole('button', {name: 'Sign In'}).isHidden()) {
			await performLogout(page);
		}

		await performLogin(page, 'test');

		await systemSettingsPage.goToSystemSetting(
			'Feature Flags',
			'Developer'
		);

		if (await page.getByLabel('COMMERCE-9410').isChecked()) {
			await page.getByLabel('COMMERCE-9410').click();
		}
	}
});
