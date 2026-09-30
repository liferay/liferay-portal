/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {globalMenuPagesTest} from '../../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import {ORDER_WORKFLOW_STATUS_CODE} from '../../../workspaces/liferay-workspace-marketplace/main/utils/constants';
import {classicCommerceSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	globalMenuPagesTest,
	pageEditorPagesTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Order Attachments Data Set fragment - placed order',
	{tag: '@LPD-83041'},
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
					quantity: 1,
					skuId: sku.id,
				},
			],
			shippingAddressId: address.id,
		});

		await apiHelpers.headlessCommerceAdminOrder.patchOrder(order.id, {
			orderStatus: ORDER_WORKFLOW_STATUS_CODE.PROCESSING,
		});

		const attachmentTitle = `${getRandomString()}.png`;

		await apiHelpers.headlessCommerceAdminOrderAttachment.postOrderAttachment(
			order.id,
			{
				attachment:
					'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=',
				priority: 1,
				title: attachmentTitle,
				type: 'invoice',
			}
		);

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: 'Order',
			name: displayPageTemplateName,
		});
		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment('Order', 'Order Attachments Data Set');
		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();
		await displayPageTemplatesPage.markAsDefault(displayPageTemplateName);

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${order.id}`
		);

		await expect(
			page.getByText(attachmentTitle, {exact: true})
		).toBeVisible();
		await expect(page.getByText('png', {exact: true})).toBeVisible();
		await expect(page.getByText('Invoice', {exact: true})).toBeVisible();
	}
);

test(
	'Order Attachments Data Set fragment - open cart',
	{tag: '@LPD-83041'},
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
					quantity: 1,
					skuId: sku.id,
				},
			],
			shippingAddressId: address.id,
		});

		const attachmentTitle = `${getRandomString()}.png`;

		await apiHelpers.headlessCommerceAdminOrderAttachment.postOrderAttachment(
			order.id,
			{
				attachment:
					'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=',
				priority: 1,
				title: attachmentTitle,
				type: 'invoice',
			}
		);

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: 'Order',
			name: displayPageTemplateName,
		});
		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment('Order', 'Order Attachments Data Set');
		await pageEditorPage.waitForChangesSaved();

		await displayPageTemplatesPage.publishTemplate();
		await displayPageTemplatesPage.markAsDefault(displayPageTemplateName);

		await page.goto(
			liferayConfig.environment.baseUrl +
				`/web/${site.name}/order/${order.id}`
		);

		await expect(
			page.getByText(attachmentTitle, {exact: true})
		).toBeVisible();
		await expect(page.getByText('png', {exact: true})).toBeVisible();
		await expect(page.getByText('Invoice', {exact: true})).toBeVisible();
	}
);

test(
	'Verify that the purchase order document is visible in the attachments tab',
	{tag: '@LPD-83041'},
	async ({
		apiHelpers,
		commerceAdminOrderAttachmentsPage,
		commerceAdminOrdersPage,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
	}) => {
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
					quantity: 1,
					skuId: sku.id,
				},
			],
			orderStatus: '2',
			shippingAddressId: address.id,
		});

		await test.step('Open the order details', async () => {
			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentType: 'Order',
				name: displayPageTemplateName,
			});
			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

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
			await displayPageTemplatesPage.markAsDefault(
				displayPageTemplateName
			);

			await page.goto(
				liferayConfig.environment.baseUrl +
					`/web/${site.name}/order/${order.id}`
			);

			await expect(
				commerceLayoutsPage.infoBoxButton('purchaseOrderDocument')
			).toBeVisible();
		});

		await test.step('Add the Purchase Order Document', async () => {
			const fileChooserPromise = page.waitForEvent('filechooser');

			await commerceLayoutsPage
				.infoBoxButton('purchaseOrderDocument')
				.click();

			const fileChooser = await fileChooserPromise;
			await fileChooser.setFiles(
				path.join(__dirname, '/dependencies/image1.jpg')
			);

			await expect(
				commerceLayoutsPage.infoBoxValue('image1.jpg')
			).toBeVisible();
		});

		await test.step('Check the presence of the purchaseOrderDocument in the order admin attachment page', async () => {
			await commerceAdminOrdersPage.goto();

			await (
				await commerceAdminOrdersPage.tableRowLink({
					colIndex: 1,
					rowValue: order.id,
				})
			).click();

			await commerceAdminOrderAttachmentsPage.attachmentsTab.click();

			await expect(
				page.getByText('image1.jpg', {exact: true})
			).toBeVisible();
			await expect(page.getByText('jpg', {exact: true})).toBeVisible();
			await expect(
				page.getByText('Purchase Order Document', {exact: true})
			).toBeVisible();
		});
	}
);
