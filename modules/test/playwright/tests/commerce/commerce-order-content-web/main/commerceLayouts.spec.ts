/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {liferayConfig} from '../../../../liferay.config';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import performLogin, {
	performLoginViaApi,
	performLogout,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import getFragmentDefinition from '../../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {ORDER_WORKFLOW_STATUS_CODE} from '../../../workspaces/liferay-workspace-marketplace/main/utils/constants';
import {classicCommerceSetUp, commerceReturnSetUp} from '../../utils/commerce';
import {checkLocalizedDate} from '../../utils/date';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPD-20379': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	pageEditorPagesTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Display page template edit mode works with Speedwell theme',
	{tag: '@LPD-25926'},
	async ({
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: 'Blogs Entry',
			name: displayPageTemplateName,
		});
		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment('Order', 'Heading');

		await expect(page.getByText('Heading Example')).toBeVisible();

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

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
	}
);

test(
	'Default order display page template is accessible via friendly URL',
	{tag: '@LPD-33439'},
	async ({
		apiHelpers,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		site,
	}) => {
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
);

test(
	'Order info box fragment configuration',
	{tag: '@LPD-32227'},
	async ({
		apiHelpers,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
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

		await pageEditorPage.addFragment('Order', 'Info Box');

		const infoBoxFragmentId =
			await pageEditorPage.getFragmentId('Info Box');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Field',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'purchaseOrderNumber',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'PON',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Read Only',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: false,
		});

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

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

		await page.waitForLoadState('networkidle');

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

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		await commerceLayoutsPage.moreActionsButton.click();
		await commerceLayoutsPage.editMenuItem.click();

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Field',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'accountInfo',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'Account Info',
		});

		await expect(
			page.getByText(
				'The info box component is not correctly configured.'
			)
		).toBeVisible();

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Read Only',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: true,
		});

		await expect(
			page.getByText(
				'The info box component is not correctly configured.'
			)
		).toBeHidden();

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(page.getByText('Account Info')).toBeVisible();
		await expect(page.getByText(account.name)).toBeVisible();
		await expect(page.getByText(String(account.id))).toBeVisible();
		await expect(commerceLayoutsPage.infoBoxButton('PON')).toBeHidden();
	}
);

test(
	'Order Step Tracker fragment configuration',
	{tag: ['@LPD-32236', '@LPD-39679']},
	async ({
		apiHelpers,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
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

		await pageEditorPage.addFragment('Order', 'Step Tracker');

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

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

		await apiHelpers.headlessCommerceAdminOrder.patchOrder(cart.id, {
			orderStatus: '20',
		});

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(
			commerceLayoutsPage.stepTrackerItem('Pending')
		).toHaveCount(0);
		await expect(
			commerceLayoutsPage.stepTrackerItem('Processing')
		).toHaveCount(0);
		await expect(
			commerceLayoutsPage.stepTrackerItem('Shipped')
		).toHaveCount(0);
		await expect(
			commerceLayoutsPage.stepTrackerItem('Completed')
		).toHaveCount(0);
		await expect(
			commerceLayoutsPage.stepTrackerItem('On Hold')
		).toHaveCount(0);

		await apiHelpers.headlessCommerceAdminOrder.patchOrder(cart.id, {
			orderStatus: '8',
		});

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(
			commerceLayoutsPage.stepTrackerItem('Pending')
		).toHaveCount(0);
		await expect(
			commerceLayoutsPage.stepTrackerItem('Processing')
		).toHaveCount(0);
		await expect(
			commerceLayoutsPage.stepTrackerItem('Shipped')
		).toHaveCount(0);
		await expect(
			commerceLayoutsPage.stepTrackerItem('Completed')
		).toHaveCount(0);
		await expect(
			commerceLayoutsPage.stepTrackerItem('Cancelled')
		).toHaveCount(0);
	}
);

test(
	'Edit Requested Delivery Date in Open Order Details',
	{tag: '@LPD-32232'},
	async ({
		apiHelpers,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
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

		await pageEditorPage.addFragment('Order', 'Info Box');

		const infoBoxFragmentId =
			await pageEditorPage.getFragmentId('Info Box');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Field',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'requestedDeliveryDate',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'Requested Delivery Date',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Read Only',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: false,
		});

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

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

		await page.waitForLoadState('networkidle');

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
);

test(
	'Edit Shipping Method in Open Order Details',
	{tag: '@LPD-33808'},
	async ({
		apiHelpers,
		commerceAdminChannelsPage,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		test.setTimeout(180000);

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: 'Order',
			name: displayPageTemplateName,
		});
		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment('Order', 'Info Box');

		const infoBoxFragmentId =
			await pageEditorPage.getFragmentId('Info Box');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Field',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'shippingMethod',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'Shipping Method',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Read Only',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: false,
		});

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

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

		await page.waitForLoadState('networkidle');

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
);

test(
	'Edit Payment Method in Open Order Details',
	{tag: '@LPD-33809'},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: 'Order',
			name: displayPageTemplateName,
		});
		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment('Order', 'Info Box');

		const infoBoxFragmentId =
			await pageEditorPage.getFragmentId('Info Box');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Field',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'paymentMethod',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'Payment Method',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Read Only',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: false,
		});

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

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
				`/web/${site.name}/order/${cart.id}`,
			{waitUntil: 'networkidle'}
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
		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			paymentMethod2,
			'Payment Methods'
		);

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`,
			{waitUntil: 'networkidle'}
		);

		await expect(
			commerceLayoutsPage.infoBoxButton('Payment Method')
		).toBeVisible();

		await commerceLayoutsPage.infoBoxButton('Payment Method').click();
		await commerceLayoutsPage.infoBoxValue(paymentMethod1).check();

		await expect(
			page.getByLabel(paymentMethod1, {exact: true})
		).toBeChecked();

		await commerceLayoutsPage.saveButton.click();

		await expect(
			commerceLayoutsPage.infoBoxValue(paymentMethod1)
		).toBeVisible();

		await commerceLayoutsPage.infoBoxButton('Payment Method').click();
		await commerceLayoutsPage.infoBoxValue(paymentMethod2).check();

		await expect(
			page.getByLabel(paymentMethod2, {exact: true})
		).toBeChecked();

		await commerceLayoutsPage.saveButton.click();
		await page.waitForLoadState('load');

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
);

test(
	'Order Details - Order Summary',
	{tag: ['@LPD-35558', '@LPD-35252']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceAdminDiscountsPage,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
	}) => {
		const {channel, site} = await classicCommerceSetUp(apiHelpers);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			['demo.unprivileged@liferay.com']
		);
		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'demo.unprivileged@liferay.com'
			);

		const rolesResponse =
			await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

		const accountRoleBuyer = rolesResponse?.items?.filter((role) => {
			return role.name === 'Buyer';
		});

		const siteRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

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

		const shippingOption = getRandomString();

		await commerceAdminChannelsPage.setupCommerceChannelShippingMethod(
			channel.name,
			'Flat Rate',
			[shippingOption],
			true
		);

		const taxCategory = (
			await apiHelpers.headlessCommerceAdminChannel.getTaxCategories()
		).items[0];

		await commerceAdminChannelDetailsPage.addFixedTaxRate(
			'7.5',
			taxCategory.name.en_US
		);

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.getProducts(
				new URLSearchParams({
					filter: `name eq 'U-Joint'`,
				})
			);

		const productId = product.items[0].productId;

		const productSkus = await apiHelpers.headlessCommerceAdminCatalog
			.getProduct(productId)
			.then((product) => {
				return product.skus;
			});

		const sku = productSkus[0];

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{phoneNumber: '323262', regionISOCode: 'AL'}
			);

		const postCart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				billingAddressId: address.id,
				cartItems: [
					{
						options: '[]',
						quantity: 1,
						replacedSkuId: 0,
						skuId: sku.id,
					},
				],
				shippingAddressId: address.id,
				shippingMethod: 'fixed',
				shippingOption,
			},
			channel.id
		);

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);
		await displayPageTemplatesPage.editTemplate('Order');

		const orderSummaryId =
			await pageEditorPage.getFragmentId('Order Summary');

		await expect(page.getByLabel('Details', {exact: true})).toContainText(
			'Subtotal'
		);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Field',
			fragmentId: orderSummaryId,
			tab: 'General',
			value: 'Total',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId: orderSummaryId,
			tab: 'General',
			value: 'TotalTest',
		});

		await expect(page.getByLabel('Details', {exact: true})).toContainText(
			'TotalTest'
		);

		await pageEditorPage.addWidget('Commerce', 'Coupon Code Entry');
		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

		await expect(
			commerceLayoutsPage.defaultDisplayPageTemplateIcon
		).toBeVisible();

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${postCart.id}`
		);

		let cart = await apiHelpers.headlessCommerceDeliveryCart.getCart(
			postCart.id
		);

		await expect(page.getByLabel('Details', {exact: true})).toContainText(
			`TotalTest ${cart.summary.totalFormatted}`
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
			target: 'shipping',
			usePercentage: true,
		});
		await apiHelpers.headlessCommerceAdminPricing.postDiscount({
			percentageLevel1: 10,
			target: 'total',
			usePercentage: true,
		});

		await commerceAdminDiscountsPage.enterPromoCodeToWidget(
			discount.couponCode
		);

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);
		await displayPageTemplatesPage.editTemplate('Order');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Field',
			fragmentId: orderSummaryId,
			tab: 'General',
			value: 'Subtotal',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId: orderSummaryId,
			tab: 'General',
			value: 'Subtotal',
		});

		await displayPageTemplatesPage.publishTemplate();

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${postCart.id}`
		);

		await expect(page.getByText('Order Summary')).toBeVisible();
		await expect(
			page.getByText(`Promotion Code ${discount.couponCode}`)
		).toBeVisible();

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${postCart.id}`
		);

		cart = await apiHelpers.headlessCommerceDeliveryCart.getCart(
			postCart.id
		);

		await expect(page.getByLabel('Details', {exact: true})).toContainText(
			`Subtotal ${cart.summary.subtotalFormatted}`
		);
		await expect(page.getByLabel('Details', {exact: true})).toContainText(
			`Subtotal Discount ${cart.summary.subtotalDiscountValueFormatted}`
		);
		await expect(page.getByLabel('Details', {exact: true})).toContainText(
			`Total Discount ${cart.summary.totalDiscountValueFormatted}`
		);
		await expect(
			page.getByText(`Promotion Code ${discount.couponCode}`)
		).toBeVisible();
		await expect(page.getByLabel('Details', {exact: true})).toContainText(
			`Tax ${cart.summary.taxValueFormatted}`
		);
		await expect(page.getByLabel('Details', {exact: true})).toContainText(
			`Delivery ${cart.summary.shippingValueFormatted}`
		);
		await expect(page.getByLabel('Details', {exact: true})).toContainText(
			`Delivery Discount ${cart.summary.shippingDiscountValueFormatted}`
		);
		await expect(page.getByLabel('Details', {exact: true})).toContainText(
			`Total ${cart.summary.totalFormatted}`
		);
	}
);

test(
	'Order actions fragment',
	{tag: '@LPD-32237'},
	async ({
		apiHelpers,
		checkoutPage,
		commerceAdminChannelsPage,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
	}) => {
		test.setTimeout(180000);

		let account;
		let cart1;
		let cart2;
		let catalog;
		let channel;
		let orderActionsFragmentId;
		let product;
		let site;
		let sku;
		let user;

		await test.step('Initialize Commerce Classic Site', async () => {
			const {
				catalog: catalogSetup,
				channel: channelSetup,
				site: siteSetup,
			} = await classicCommerceSetUp(apiHelpers, getRandomString());

			catalog = catalogSetup;
			channel = channelSetup;
			site = siteSetup;
		});

		await test.step('Create an Account and a Buyer user', async () => {
			account = await apiHelpers.headlessAdminUser.postAccount({
				name: getRandomString(),
				type: 'business',
			});

			user =
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
			await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
				account.id,
				['demo.unprivileged@liferay.com']
			);

			const siteRole =
				await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

			await apiHelpers.headlessAdminUser.assignUserToSite(
				siteRole.id,
				site.id,
				user.id
			);
		});

		await test.step('Create a Product', async () => {
			product = await apiHelpers.headlessCommerceAdminCatalog.postProduct(
				{
					catalogId: catalog.id,
				}
			);

			sku = product.skus[0];
		});

		await test.step('Change fragment configuration of order actions fragment disabling import from CSV', async () => {
			await displayPageTemplatesPage.goto(site.friendlyUrlPath);
			await displayPageTemplatesPage.editTemplate('Order');

			orderActionsFragmentId =
				await pageEditorPage.getFragmentId('Order Actions');

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Enable Import from CSV',
				fragmentId: orderActionsFragmentId,
				tab: 'General',
				value: false,
			});

			await pageEditorPage.waitForChangesSaved();

			await displayPageTemplatesPage.publishTemplate();
		});

		await test.step('As Buyer create a cart and navigate to order and assert that order actions buttons are correctly displayed', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'demo.unprivileged'});

			cart1 = await apiHelpers.headlessCommerceDeliveryCart.postCart(
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

			await commerceLayoutsPage.orderActionDropDownButton.click();

			await expect(
				page.getByRole('menuitem', {name: 'Import from CSV'})
			).toBeHidden();

			await expect(
				page.getByRole('menuitem', {name: 'Import from Orders'})
			).toBeVisible();

			await expect(
				page.getByRole('menuitem', {name: 'Import from Wish List'})
			).toBeVisible();
		});

		await test.step('Change fragment configuration of order actions fragment enabling import from CSV', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);
			await displayPageTemplatesPage.editTemplate('Order');

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Enable Import from CSV',
				fragmentId: orderActionsFragmentId,
				tab: 'General',
				value: true,
			});

			await pageEditorPage.waitForChangesSaved();

			await displayPageTemplatesPage.publishTemplate();
		});

		await test.step('As Buyer navigate to order, click import from CSV and download CSV template', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'demo.unprivileged'});

			await page.goto(
				liferayConfig.environment.baseUrl +
					`/web/${site.name}/order/${cart1.id}`
			);

			await expect(
				page.getByRole('heading', {name: String(cart1.id)}).first()
			).toBeVisible();

			await commerceLayoutsPage.orderActionDropDownButton.click();

			await page.getByRole('menuitem', {name: 'Import from CSV'}).click();

			const downloadPromise = page.waitForEvent('download');

			await commerceLayoutsPage.downloadCsvTemplateButton.click();

			const download = await downloadPromise;
			expect(download.suggestedFilename()).toEqual('csv_template.csv');

			await commerceLayoutsPage.closeFrameButton.click();
		});

		await test.step('As Buyer perform checkout and assert that order actions buttons are correctly displayed', async () => {
			await commerceLayoutsPage.expectOrderActionButtons({
				checkoutCount: 1,
				submitCount: 1,
			});
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

			await commerceLayoutsPage.expectOrderActionButtons({
				reorderCount: 1,
				submitCount: 1,
			});
		});

		await test.step('Change Buyer and Seller order approval workflows', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			await commerceAdminChannelsPage.changeCommerceChannelBuyerOrderApprovalWorkflow(
				'Single Approver (Version 1)',
				channel.name
			);
			await commerceAdminChannelsPage.changeCommerceChannelSellerOrderAcceptanceWorkflow(
				'Single Approver (Version 1)',
				channel.name,
				true
			);
		});

		await test.step('As Buyer create a cart, navigate to order and submit it', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'demo.unprivileged'});

			cart2 = await apiHelpers.headlessCommerceDeliveryCart.postCart(
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

			await commerceLayoutsPage.expectOrderActionButtons({
				submitCount: 1,
			});
			await commerceLayoutsPage.orderActionsButton('Submit').click();

			await expect(
				page.getByRole('heading', {name: String(cart2.id)}).first()
			).toBeVisible();

			await commerceLayoutsPage.expectOrderActionButtons({});
		});

		await test.step('As Admin approve the order and assert that order actions buttons are correctly displayed', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

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

			await commerceLayoutsPage.expectOrderActionButtons({
				checkoutCount: 1,
			});
		});

		await test.step('As Buyer create a cart, navigate to order and perform checkout', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'demo.unprivileged'});

			await page.goto(
				liferayConfig.environment.baseUrl +
					`/web/${site.name}/order/${cart2.id}`
			);

			await expect(
				page.getByRole('heading', {name: String(cart2.id)}).first()
			).toBeVisible();

			await commerceLayoutsPage.expectOrderActionButtons({
				checkoutCount: 1,
				submitCount: 1,
			});
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
		});

		await test.step('As Admin approve the order and assert that order actions buttons are correctly displayed', async () => {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

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
				submitCount: 1,
			});
			await commerceLayoutsPage.orderActionsButton('Approve').click();

			await expect(
				page.getByRole('heading', {name: String(cart2.id)}).first()
			).toBeVisible();

			await commerceLayoutsPage.expectOrderActionButtons({
				reorderCount: 1,
				submitCount: 1,
			});
		});

		await test.step('As Buyer navigate to order and assert that order actions buttons are correctly displayed', async () => {
			await performLogout(page);
			await performLogin(page, 'demo.unprivileged');

			await page.goto(
				liferayConfig.environment.baseUrl +
					`/web/${site.name}/order/${cart2.id}`
			);

			await expect(
				page.getByRole('heading', {name: String(cart2.id)}).first()
			).toBeVisible();

			await commerceLayoutsPage.expectOrderActionButtons({
				reorderCount: 1,
				submitCount: 1,
			});
			try {
				await commerceLayoutsPage.orderActionsButton('Reorder').click();

				await expect(
					page.getByRole('heading', {name: String(cart2.id)})
				).toHaveCount(0);

				await commerceLayoutsPage.expectOrderActionButtons({
					submitCount: 1,
				});
			}
			finally {
				await performLogout(page);
				await performLoginViaApi({page, screenName: 'test'});

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
		});
	}
);

test(
	'Billing and shipping address order info box fragment configuration',
	{tag: '@LPD-32230'},
	async ({
		apiHelpers,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
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

		await pageEditorPage.addFragment('Order', 'Info Box');

		const infoBoxFragmentId =
			await pageEditorPage.getFragmentId('Info Box');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Field',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'billingAddress',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'Billing Address',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Read Only',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: false,
		});

		await expect(
			page.getByText('The info box component will be shown here.')
		).toBeVisible();

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

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

		await page.waitForLoadState('networkidle');

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(page.getByText('Billing Address')).toBeVisible();

		await commerceLayoutsPage.infoBoxButton('Billing Address').click();

		await commerceLayoutsPage.cancelButton.click();

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		await commerceLayoutsPage.moreActionsButton.click();
		await commerceLayoutsPage.editMenuItem.click();

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Field',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'shippingAddress',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'Shipping Address',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Read Only',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: true,
		});

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(page.getByText('Shipping Address')).toBeVisible();
		await expect(
			commerceLayoutsPage.infoBoxButton('Shipping Address')
		).toBeHidden();
	}
);

test(
	'Order Details - Questions & Answers',
	{tag: '@LPD-33503'},
	async ({
		apiHelpers,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

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

		const className =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				'com.liferay.commerce.model.CommerceOrder'
			);

		const displayPageTemplateName = getRandomString();

		const displayPage =
			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
				{
					classNameId: className.classNameId,
					groupId: site.id,
					name: displayPageTemplateName,
				}
			);

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
			{
				layoutPageTemplateEntryId:
					displayPage.layoutPageTemplateEntryId,
			}
		);

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);
		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment('Order', 'Info Box');

		const infoBoxFragmentId =
			await pageEditorPage.getFragmentId('Info Box');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Field',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'notes',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'Order notes',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Read Only',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: false,
		});

		await expect(
			page.getByText('The info box component will be shown here.')
		).toBeVisible();

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

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

		await page.waitForLoadState('networkidle');

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(page.getByText('Order notes')).toBeVisible();

		await commerceLayoutsPage.infoBoxButton('Order notes').click();

		const randomComment = getRandomString();

		await commerceLayoutsPage.inputTextArea.fill(randomComment);

		await commerceLayoutsPage.submitButton.click();

		let comment = await apiHelpers.headlessCommerceDeliveryCart.getComments(
			cart.id
		);

		let commentModifiedDate = await page.getByRole('paragraph').innerText();

		expect(
			checkLocalizedDate(
				comment.items[0].modifiedDate,
				commentModifiedDate
			)
		).toBe(true);

		await commerceLayoutsPage.infoBoxButton('Order notes').click();

		await expect(page.getByText(comment.items[0].author)).toBeVisible();
		await expect(page.getByText(randomComment)).toBeVisible();

		const randomComment2 = getRandomString();

		await commerceLayoutsPage.inputTextArea.fill(randomComment2);

		await page.getByLabel('Private').check();

		await commerceLayoutsPage.submitButton.click();

		await commerceLayoutsPage.infoBoxButton('Order notes').click();

		await expect(page.getByText(randomComment2)).toBeVisible();
		await expect(commerceLayoutsPage.iconLock).toBeVisible();

		await commerceLayoutsPage.moreActionsButton.first().click();

		page.once('dialog', async (dialog) => {
			expect(dialog.message()).toContain(
				'Are you sure you want to delete this? It will be deleted immediately.'
			);
			await dialog.accept();
		});

		await commerceLayoutsPage.deleteMenuItemModal.click();

		const randomComment3 = getRandomString();

		await commerceLayoutsPage.infoBoxButton('Order notes').click();

		await commerceLayoutsPage.inputTextArea.fill(randomComment3);

		await commerceLayoutsPage.submitButton.click();

		await performLogout(page);

		await performLogin(page, 'demo.unprivileged');

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		comment = await apiHelpers.headlessCommerceDeliveryCart.getComments(
			cart.id
		);

		commentModifiedDate = await page.getByRole('paragraph').innerText();

		expect(
			checkLocalizedDate(
				comment.items[0].modifiedDate,
				commentModifiedDate
			)
		).toBe(true);

		await commerceLayoutsPage.infoBoxButton('Order notes').click();

		await expect(page.getByText(comment.items[0].author)).toBeVisible();
		await expect(page.getByText(randomComment3)).toBeVisible();
		await expect(commerceLayoutsPage.iconLock).toBeHidden();

		await performLogout(page);
	}
);

test(
	'Order Data Sets and header fragments',
	{tag: ['@LPD-35558', '@LPD-48375']},
	async ({
		apiHelpers,
		commerceLayoutsPage,
		commerceThemeClassicOrdersPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);
		const displayPageTemplateName = getRandomString();
		await displayPageTemplatesPage.createTemplate({
			contentType: 'Order',
			name: displayPageTemplateName,
		});
		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment('Order', 'Orders Data Set');
		await pageEditorPage.addFragment('Order', 'Order Items Data Set');
		await pageEditorPage.addFragment('Order', 'Order Status Label');
		await pageEditorPage.addFragment(
			'Order',
			'Inline Editable Order Field'
		);

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

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

		await commerceLayoutsPage.moreActionsButton.click();
		await commerceLayoutsPage.markAsDefaultMenuItem.click();

		await waitForAlert(page);

		await expect(
			commerceLayoutsPage.defaultDisplayPageTemplateIcon
		).toBeVisible();

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`,
			{waitUntil: 'networkidle'}
		);

		await expect(
			(await commerceThemeClassicOrdersPage.tableRow(1, cart.id)).row
		).toBeVisible();
		await expect(
			(
				await commerceThemeClassicOrdersPage.orderItemsTableRow(
					2,
					sku.sku
				)
			).row
		).toBeVisible();

		const cart2 = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [],
			},
			channel.id
		);

		await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [],
			},
			channel.id
		);

		page.on('dialog', async (dialog) => {
			await dialog.accept();
		});

		await (await commerceThemeClassicOrdersPage.tableRow(11, 'Actions')).row
			.getByRole('button')
			.click();
		await commerceThemeClassicOrdersPage
			.orderTableMenuItem('Delete')
			.click();

		await waitForAlert(page);

		await expect(
			page.getByRole('link', {name: cart.id.toString()})
		).not.toBeVisible();

		await commerceThemeClassicOrdersPage.orderTableItemsSelector.click();
		await commerceThemeClassicOrdersPage.orderTableItemsSelectorDropdown.click();
		await commerceThemeClassicOrdersPage
			.orderTableMenuItem('Delete')
			.click();

		await waitForAlert(page);

		await expect(
			page.getByRole('link', {name: cart2.id.toString()})
		).not.toBeVisible();
	}
);

test(
	'Payment and Delivery Terms order info box fragment configuration',
	{tag: '@LPD-37698'},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		test.setTimeout(180000);

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

		await pageEditorPage.addFragment('Order', 'Info Box');

		const infoBoxFragmentId =
			await pageEditorPage.getFragmentId('Info Box');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Field',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'paymentTermId',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'Payment Term',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Read Only',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: false,
		});

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

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

		await commerceAdminChannelsPage.goto();
		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

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

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const sku = product.skus[0];

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
				paymentMethod: 'money-order',
				shippingAddressId: address.id,
			},
			channel.id
		);

		await page.waitForLoadState('networkidle');

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(page.getByText('Payment Term')).toBeVisible();

		await commerceLayoutsPage.infoBoxButton('Payment Term').click();

		await commerceLayoutsPage.paymentTermsSelect.selectOption(
			String(paymentTerm1.id)
		);

		await expect(
			page.getByText(paymentTerm1.description.en_US)
		).toBeVisible();

		await commerceLayoutsPage.saveButton.click();

		await expect(page.getByTestId('infoBoxValue')).toHaveText(
			String(paymentTerm1.label.en_US)
		);

		await commerceLayoutsPage.infoBoxButton('Payment Term').click();

		await commerceLayoutsPage.paymentTermsSelect.selectOption(
			String(paymentTerm2.id)
		);

		await expect(
			page.getByText(paymentTerm2.description.en_US)
		).toBeVisible();

		await commerceLayoutsPage.saveButton.click();

		await expect(page.getByTestId('infoBoxValue')).toHaveText(
			String(paymentTerm2.label.en_US)
		);

		await page.reload();

		await expect(page.getByTestId('infoBoxValue')).toHaveText(
			String(paymentTerm2.label.en_US)
		);

		await apiHelpers.headlessCommerceDeliveryCart.checkoutCart(cart.id);

		await page.reload();

		await commerceLayoutsPage.infoBoxButton('Payment Term').click();

		await expect(commerceLayoutsPage.saveButton).not.toBeVisible();
	}
);

test(
	'Purchase Document in Open Order Details',
	{tag: '@LPD-33490'},
	async ({
		apiHelpers,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		test.setTimeout(180000);

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: 'Order',
			name: displayPageTemplateName,
		});
		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment('Order', 'Info Box');

		const infoBoxFragmentId =
			await pageEditorPage.getFragmentId('Info Box');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Field',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'purchaseOrderDocument',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: 'Purchase Order Document',
		});
		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Read Only',
			fragmentId: infoBoxFragmentId,
			tab: 'General',
			value: false,
		});

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

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

		await page.waitForLoadState('networkidle');

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(
			commerceLayoutsPage.infoBoxButton('purchaseOrderDocument')
		).toBeVisible();

		let fileChooserPromise = page.waitForEvent('filechooser');

		await commerceLayoutsPage
			.infoBoxButton('purchaseOrderDocument')
			.click();

		let fileChooser = await fileChooserPromise;
		await fileChooser.setFiles(
			path.join(__dirname, '/dependencies/image1.jpg')
		);

		await expect(
			commerceLayoutsPage.infoBoxButton('purchaseOrderDocument')
		).toHaveCount(0);
		await expect(
			commerceLayoutsPage.infoBoxValue('image1.jpg')
		).toBeVisible();

		fileChooserPromise = page.waitForEvent('filechooser');

		await commerceLayoutsPage.infoBoxEditPurchaseOrderDocumentButton.click();

		fileChooser = await fileChooserPromise;
		await fileChooser.setFiles(
			path.join(__dirname, '/dependencies/image2.jpg')
		);

		await expect(
			commerceLayoutsPage.infoBoxButton('purchaseOrderDocument')
		).toHaveCount(0);
		await expect(
			commerceLayoutsPage.infoBoxValue('image2.jpg')
		).toBeVisible();

		await page.reload();

		await expect(
			commerceLayoutsPage.infoBoxButton('purchaseOrderDocument')
		).toHaveCount(0);
		await expect(
			commerceLayoutsPage.infoBoxValue('image2.jpg')
		).toBeVisible();

		await commerceLayoutsPage.infoBoxDeletePurchaseOrderDocumentButton.click();

		await expect(
			commerceLayoutsPage.infoBoxButton('purchaseOrderDocument')
		).toBeVisible();
		await expect(
			commerceLayoutsPage.infoBoxValue('image2.jpg')
		).toHaveCount(0);

		await page.reload();

		await expect(
			commerceLayoutsPage.infoBoxButton('purchaseOrderDocument')
		).toBeVisible();
		await expect(
			commerceLayoutsPage.infoBoxValue('image2.jpg')
		).toHaveCount(0);
	}
);

test(
	'Quick checkout from order actions fragment',
	{tag: '@LPD-34399'},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
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

		await commerceLayoutsPage.addFragment('Heading');

		await page.getByText('Heading Example', {exact: true}).dblclick();
		await page.getByLabel('Field').selectOption('CommerceOrder_orderId');

		await commerceLayoutsPage.addFragment('Order Actions', 'Order');

		await expect(
			page.getByText('The order actions component will be shown here.')
		).toBeVisible();

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

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

		await commerceAdminChannelsPage.goto();
		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();
		await commerceAdminChannelsPage
			.ordersTabToggle('Quick Checkout')
			.click();
		await commerceAdminChannelsPage.headerActionsSaveButton.click();

		await waitForAlert(page);

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(
			commerceLayoutsPage.orderActionsButton('Quick Checkout')
		).toBeDisabled();

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{phoneNumber: '1234567890', regionISOCode: 'AL'}
			);

		await commerceAdminChannelsPage.goto();
		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await commerceAdminChannelDetailsPage.activateChannelConfiguration(
			'Money Order',
			'Payment Methods'
		);
		await (
			await commerceAdminChannelDetailsPage.generalCommerceAdminChannelTableLink(
				'Money Order'
			)
		).click();

		const shippingOption = getRandomString();

		await commerceAdminChannelsPage.setupCommerceChannelShippingMethod(
			channel.name,
			'Flat Rate',
			[shippingOption]
		);

		await apiHelpers.headlessCommerceDeliveryCart.patchCart(
			{
				accountId: account.id,
				billingAddressId: address.id,
				paymentMethod: 'money-order',
				shippingAddressId: address.id,
				shippingMethod: 'fixed',
				shippingOption,
			},
			cart.id
		);

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(
			commerceLayoutsPage.orderActionsButton('Quick Checkout')
		).toBeEnabled();
	}
);

test(
	'Order Returns Data Set fragment',
	{tag: '@LPD-32243'},
	async ({
		apiHelpers,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
	}) => {
		const {commerceReturn, order, site} =
			await commerceReturnSetUp(apiHelpers);

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: 'Order',
			name: displayPageTemplateName,
		});
		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await commerceLayoutsPage.addFragment(
			'Order Returns Data Set',
			'Order'
		);

		await expect(
			page.getByText(
				'The order returns data set component will be shown here.'
			)
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

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${order.id}`
		);

		await expect(
			page
				.getByRole('columnheader', {name: 'Return ID'})
				.getByRole('button')
		).toBeVisible();
		await expect(page.getByText(commerceReturn.id)).toBeVisible();
	}
);

test(
	'All commerce widgets work in a content page',
	{tag: '@LPD-38261'},
	async ({apiHelpers, page, site}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_cart_content_web_internal_portlet_CommerceCartContentPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_cart_content_web_internal_portlet_CommerceCartContentTotalPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_web_internal_portlet_CPCategoryContentPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_checkout_web_internal_portlet_CommerceCheckoutPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_address_content_web_internal_portlet_CommerceAddressContentPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_asset_categories_navigation_web_internal_portlet_CPAssetCategoriesNavigationPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_discount_content_web_internal_portlet_CommerceDiscountContentPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_cart_content_web_internal_portlet_CommerceCartContentMiniPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_order_content_web_internal_portlet_CommerceOpenOrderContentPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_search_web_internal_portlet_CPOptionFacetsPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_dashboard_web_internal_portlet_CommerceDashboardForecastsChartPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_organization_web_internal_portlet_CommerceOrganizationPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_order_content_web_internal_portlet_CommerceOrderContentPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_search_web_internal_portlet_CPPriceRangeFacetsPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_web_internal_portlet_CPCompareContentMiniPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_web_internal_portlet_CPCompareContentPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_web_internal_portlet_CPContentPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_type_virtual_order_content_web_internal_portlet_CommerceVirtualOrderItemContentPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_web_internal_portlet_CPPublisherPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_subscription_web_internal_portlet_CommerceSubscriptionContentPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_order_content_web_internal_portlet_CommerceReturnContentPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_search_web_internal_portlet_CPSearchResultsPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_shipment_content_web_internal_portlet_CommerceShipmentContentPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_search_web_internal_portlet_CPSortPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_search_web_internal_portlet_CPSpecificationOptionFacetsPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_wish_list_web_internal_portlet_CommerceWishListContentPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_wish_list_web_internal_portlet_MyCommerceWishListsPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await page.goto(
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(page.getByText('is temporarily unavailable.')).toHaveCount(
			0
		);
	}
);

test(
	'Order Multishipping fragment',
	{tag: '@LPD-36953'},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
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

		await commerceLayoutsPage.addFragment('Multishipping', 'Order');

		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();

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

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});
		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const sku1 = product1.skus[0];
		const sku2 = product2.skus[0];

		const cart = await apiHelpers.headlessCommerceDeliveryCart.postCart(
			{
				accountId: account.id,
				cartItems: [
					{
						quantity: 1,
						skuId: sku1.id,
					},
					{
						quantity: 1,
						skuId: sku2.id,
					},
				],
			},
			channel.id
		);

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(page.getByText(sku1.sku)).toHaveCount(0);
		await expect(page.getByText(sku2.sku)).toHaveCount(0);

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await commerceAdminChannelDetailsPage.allowMultishippingToggle.setChecked(
			true
		);

		await expect(
			commerceAdminChannelDetailsPage.allowMultishippingToggle
		).toBeChecked();

		await commerceAdminChannelDetailsPage.saveButton.click();

		await expect(
			commerceAdminChannelDetailsPage.allowMultishippingToggle
		).toBeChecked();

		await waitForAlert(page);

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${cart.id}`
		);

		await expect(page.getByText(sku1.sku)).toBeVisible();

		await expect(page.getByText(sku2.sku)).toBeVisible();
	}
);

test(
	'Account selector redirects to order details DPT if open order content portlet is not present',
	{tag: '@LPD-43496'},
	async ({
		apiHelpers,
		applicationsMenuPage,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		site,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'COMMERCE_ACCOUNT_FRAGMENTS-account-selector',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		await applicationsMenuPage.goToSite(site.name);

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		await commerceLayoutsPage.createDisplayPageTemplate(
			getRandomString(),
			'Order',
			site.name
		);
		await commerceLayoutsPage.addFragment('Heading');

		await expect(page.getByText('Heading Example')).toBeVisible();

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

		await applicationsMenuPage.goToSite(site.name);

		await commerceLayoutsPage.accountSelectorButton(account.name).click();
		await commerceLayoutsPage.createNewOrderButton.click();

		await expect(page.getByText('Heading Example')).toBeVisible();
	}
);

test(
	'Placed Order Shipments Data Set fragment',
	{tag: ['@LPD-32242', '@LPD-53485']},
	async ({apiHelpers, displayPageTemplatesPage, page, pageEditorPage}) => {
		test.setTimeout(180000);

		const {channel, site} = await classicCommerceSetUp(
			apiHelpers,
			`B2B_${getRandomString()}`
		);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const address =
			await apiHelpers.headlessCommerceAdminAccount.postAddress(
				account.id,
				{phoneNumber: '1234567890', regionISOCode: 'AL'}
			);

		const sku =
			await apiHelpers.headlessCommerceAdminCatalog.getSkuByName(
				'CLSC55861'
			);

		const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
			accountId: account.id,
			billingAddressId: address.id,
			channelId: channel.id,
			orderItems: [
				{
					quantity: 2,
					skuId: sku.id,
				},
			],
			shippingAddressId: address.id,
		});
		await apiHelpers.headlessCommerceAdminOrder.patchOrder(order.id, {
			orderStatus: ORDER_WORKFLOW_STATUS_CODE.PROCESSING,
		});

		const now = new Date();
		const expectedDate = new Date(
			now.getFullYear() + 1,
			now.getMonth(),
			now.getDate()
		);

		const shipment1 =
			await apiHelpers.headlessCommerceAdminShipment.postShipment({
				expectedDate: expectedDate.toISOString(),
				orderId: order.id,
				shipmentItems: [
					{
						orderItemId: order.orderItems[0].id,
						quantity: 1,
					},
				],
				shippingAddressId: address.id,
			});
		const shipment2 =
			await apiHelpers.headlessCommerceAdminShipment.postShipment({
				orderId: order.id,
				shipmentItems: [
					{
						orderItemId: order.orderItems[0].id,
						quantity: 1,
					},
				],
				shippingAddressId: address.id,
			});

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: 'Order',
			name: displayPageTemplateName,
		});
		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment(
			'Order',
			'Placed Order Shipments Data Set'
		);
		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();
		await displayPageTemplatesPage.markAsDefault(displayPageTemplateName);

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${order.id}`
		);

		await expect(
			page
				.getByRole('columnheader', {name: 'Shipment ID'})
				.getByRole('button')
		).toBeVisible();
		await expect(page.getByText(String(shipment1.id))).toBeVisible();
		await expect(page.getByText(String(shipment2.id))).toBeVisible();

		await page.getByRole('button', {exact: true, name: 'Filter'}).click();
		await page
			.getByRole('menuitem', {exact: true, name: 'Delivery Date Range'})
			.click();
		await page
			.getByLabel('To', {exact: true})
			.fill(expectedDate.toISOString().replace(/T.*/, ''));
		await page
			.getByRole('button', {exact: true, name: 'Add Filter'})
			.click();

		await expect(page.getByText(String(shipment1.id))).toBeVisible();
		await expect(page.getByText(String(shipment2.id))).toHaveCount(0);
	}
);

test(
	'When there is no site associated with the channel, inform the user',
	{tag: '@LPD-51595'},
	async ({apiHelpers, commerceLayoutsPage, page}) => {
		const {channel, site} = await classicCommerceSetUp(apiHelpers);

		await page.goto(`/web/${site.name}`);

		await expect(
			commerceLayoutsPage.accountSelectorButton('Select Account & Order')
		).toBeVisible();

		await apiHelpers.headlessCommerceAdminChannel.putChannel(channel.id, {
			accountId: channel.accountId,
			currencyCode: channel.currencyCode,
			name: channel.name,
			siteGroupId: 0,
			type: channel.type,
		});

		await page.goto(`/web/${site.name}`);

		await expect(
			commerceLayoutsPage.accountSelectorButton('Select Account & Order')
		).not.toBeVisible();
	}
);

test(
	'Add new order button disabled if max open account orders number is reached',
	{tag: '@LPD-52401'},
	async ({
		apiHelpers,
		applicationsMenuPage,
		commerceAdminChannelDetailsPage,
		commerceAdminChannelsPage,
		commerceLayoutsPage,
		page,
		site,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: getRandomString(),
					key: 'COMMERCE_ACCOUNT_FRAGMENTS-account-selector',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await commerceAdminChannelDetailsPage.maxOpenOrderAccountInput.fill(
			'1'
		);
		await commerceAdminChannelDetailsPage.saveButton.click();

		await waitForAlert(page);

		await applicationsMenuPage.goToSite(site.name);

		await commerceLayoutsPage.accountSelectorButton(account.name).click();
		await commerceLayoutsPage.createNewOrderButton.click();

		await applicationsMenuPage.goToSite(site.name);

		await commerceLayoutsPage.accountSelectorButton(account.name).click();

		await expect(commerceLayoutsPage.createNewOrderButton).toBeDisabled();
	}
);
