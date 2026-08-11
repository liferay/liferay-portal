/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {liferayConfig} from '../../../../liferay.config';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {ORDER_WORKFLOW_STATUS_CODE} from '../../../workspaces/liferay-workspace-marketplace/main/utils/constants';
import {classicCommerceSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPD-6252': {enabled: true},
	}),
	globalMenuPagesTest,
	pageEditorPagesTest,
	isolatedSiteTest,
	loginTest()
);

async function setUpOrder(apiHelpers, site) {
	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: getRandomString()},
	});

	const productSkus = await apiHelpers.headlessCommerceAdminCatalog
		.getProduct(product.productId)
		.then((product) => {
			return product.skus;
		});

	const sku = productSkus[0];

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
		account.id,
		['test@liferay.com']
	);

	const address = await apiHelpers.headlessCommerceAdminAccount.postAddress(
		account.id,
		{regionISOCode: 'AL'}
	);

	const warehouse =
		await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehouses(
			{
				active: true,
				latitude: getRandomInt(),
				longitude: getRandomInt(),
				warehouseItems: [{quantity: 1, sku: sku.sku}],
			}
		);

	await apiHelpers.headlessCommerceAdminInventoryApiHelper.postWarehousesChannels(
		warehouse.id,
		channel.id
	);

	const order = await apiHelpers.headlessCommerceAdminOrder.postOrder({
		accountId: account.id,
		billingAddressId: address.id,
		channelId: channel.id,
		orderItems: [{quantity: 1, skuId: sku.id}],
		orderStatus: '1',
		paymentMethod: 'money-order',
		paymentStatus: '0',
		shippingAddressId: address.id,
		shippingMethod: 'by-weight',
		shippingOption: 'standard-option',
	});

	return {account, order};
}

test(
	'Verify the order Attachments admin tab lists, adds, edits, and deletes attachments',
	{tag: ['@LPD-83042']},
	async ({
		apiHelpers,
		commerceAdminOrderAttachmentsPage,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		page,
		site,
	}) => {
		test.setTimeout(80000);

		const {order} = await setUpOrder(apiHelpers, site);

		const seededTitle = `seeded-${getRandomString()}.png`;

		await apiHelpers.headlessCommerceAdminOrderAttachment.postOrderAttachment(
			order.id,
			{
				attachment:
					'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=',
				priority: 1,
				title: seededTitle,
				type: 'invoice',
			}
		);

		await test.step('Open the order details', async () => {
			await commerceAdminOrdersPage.goto();

			await (
				await commerceAdminOrdersPage.tableRowLink({
					colIndex: 1,
					rowValue: order.id,
				})
			).click();

			await expect(
				commerceAdminOrderDetailsPage.headerDetailsTitle
			).toBeVisible();
		});

		await test.step('Open the Attachments tab and verify the seeded row', async () => {
			await commerceAdminOrderAttachmentsPage.attachmentsTab.click();

			await expect(
				commerceAdminOrderAttachmentsPage.rowByTitle(seededTitle)
			).toBeVisible();
		});

		const addedTitle = `added-${getRandomString()}.png`;

		await test.step('Add a new attachment via the creation menu', async () => {
			await commerceAdminOrderAttachmentsPage.addAttachmentMenuItem.click();

			await expect(
				commerceAdminOrderAttachmentsPage.sidePanelTitleInput
			).toBeVisible();

			await commerceAdminOrderAttachmentsPage.sidePanelTitleInput.fill(
				addedTitle
			);
			await commerceAdminOrderAttachmentsPage.sidePanelTypeSelect.selectOption(
				'invoice'
			);
			await commerceAdminOrderAttachmentsPage.sidePanelPriorityInput.fill(
				'5'
			);

			const fileChooserPromise = page.waitForEvent('filechooser');

			await commerceAdminOrderAttachmentsPage.sidePanelSelectFileButton.click();

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles({
				buffer: Buffer.from(
					'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=',
					'base64'
				),
				mimeType: 'image/png',
				name: addedTitle,
			});

			await commerceAdminOrderAttachmentsPage.sidePanelSaveButton.click();

			await waitForAlert(page);

			await expect(
				commerceAdminOrderAttachmentsPage.sidePanelTitleInput
			).toHaveCount(0);
			await expect(
				commerceAdminOrderAttachmentsPage.rowByTitle(addedTitle)
			).toBeVisible();
		});

		let editedTitle = `edited-${getRandomString()}.png`;

		await test.step('Edit the added attachment from the row dropdown', async () => {
			await commerceAdminOrderAttachmentsPage.openEditSidePanel(
				addedTitle
			);

			await expect(
				commerceAdminOrderAttachmentsPage.sidePanelTitleInput
			).toHaveValue(addedTitle);

			await commerceAdminOrderAttachmentsPage.sidePanelTitleInput.fill(
				editedTitle
			);

			await expect(async () => {
				if (
					await commerceAdminOrderAttachmentsPage.sidePanelTitleInput.isVisible(
						{timeout: 100}
					)
				) {
					await commerceAdminOrderAttachmentsPage.sidePanelSaveButton.click(
						{timeout: 1000}
					);
				}

				await waitForAlert(page, undefined, {timeout: 1000});
			}).toPass({timeout: 10000});

			await expect(
				commerceAdminOrderAttachmentsPage.sidePanelTitleInput
			).toHaveCount(0);
			await expect(
				commerceAdminOrderAttachmentsPage.rowByTitle(editedTitle)
			).toBeVisible();
			await expect(
				commerceAdminOrderAttachmentsPage.rowByTitle(addedTitle)
			).toHaveCount(0);
		});

		await test.step('Edit the attachment again via the title link', async () => {
			await commerceAdminOrderAttachmentsPage
				.rowTitleLink(editedTitle)
				.click();

			await expect(
				commerceAdminOrderAttachmentsPage.sidePanelTitleInput
			).toHaveValue(editedTitle);

			const previousTitle = editedTitle;

			editedTitle = `edited-twice-${getRandomString()}.png`;

			await commerceAdminOrderAttachmentsPage.sidePanelTitleInput.fill(
				editedTitle
			);

			await expect(async () => {
				if (
					await commerceAdminOrderAttachmentsPage.sidePanelTitleInput.isVisible(
						{timeout: 100}
					)
				) {
					await commerceAdminOrderAttachmentsPage.sidePanelSaveButton.click(
						{timeout: 1000}
					);
				}

				await waitForAlert(page, undefined, {timeout: 1000});
			}).toPass({timeout: 10000});

			await expect(
				commerceAdminOrderAttachmentsPage.sidePanelTitleInput
			).toHaveCount(0);
			await expect(
				commerceAdminOrderAttachmentsPage.rowByTitle(editedTitle)
			).toBeVisible();
			await expect(
				commerceAdminOrderAttachmentsPage.rowByTitle(previousTitle)
			).toHaveCount(0);
		});

		await test.step('Delete the edited attachment from the row dropdown', async () => {
			await expect(async () => {
				if (
					!(await commerceAdminOrderAttachmentsPage.deleteConfirmButton.isVisible())
				) {
					await commerceAdminOrderAttachmentsPage
						.rowActionsButton(editedTitle)
						.click();

					await expect(
						commerceAdminOrderAttachmentsPage.deleteRowAction
					).toBeVisible({timeout: 500});

					await commerceAdminOrderAttachmentsPage.deleteRowAction.click(
						{timeout: 500}
					);
				}

				await expect(
					commerceAdminOrderAttachmentsPage.deleteConfirmButton
				).toBeVisible({timeout: 500});
			}).toPass({timeout: 5000});

			await commerceAdminOrderAttachmentsPage.deleteConfirmButton.click();

			await expect(
				commerceAdminOrderAttachmentsPage.rowByTitle(editedTitle)
			).toHaveCount(0);
			await expect(
				commerceAdminOrderAttachmentsPage.rowByTitle(seededTitle)
			).toBeVisible();
		});
	}
);

test(
	'Verify that adding a restricted attachment actually gives it the correct flag',
	{tag: ['@LPD-83042']},
	async ({
		apiHelpers,
		commerceAdminOrderAttachmentsPage,
		commerceAdminOrderDetailsPage,
		commerceAdminOrdersPage,
		page,
		site,
	}) => {
		test.setTimeout(80000);

		const {order} = await setUpOrder(apiHelpers, site);

		const seededTitle = `seeded-${getRandomString()}.png`;

		await apiHelpers.headlessCommerceAdminOrderAttachment.postOrderAttachment(
			order.id,
			{
				attachment:
					'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=',
				priority: 1,
				title: seededTitle,
				type: 'invoice',
			}
		);

		await test.step('Open the order details', async () => {
			await commerceAdminOrdersPage.goto();

			await (
				await commerceAdminOrdersPage.tableRowLink({
					colIndex: 1,
					rowValue: order.id,
				})
			).click();

			await expect(
				commerceAdminOrderDetailsPage.headerDetailsTitle
			).toBeVisible();
		});

		await test.step('Open the Attachments tab and verify the seeded row', async () => {
			await commerceAdminOrderAttachmentsPage.attachmentsTab.click();

			await expect(
				commerceAdminOrderAttachmentsPage.rowByTitle(seededTitle)
			).toBeVisible();
			await expect(
				commerceAdminOrderAttachmentsPage.rowRestrictedIcon(seededTitle)
			).toHaveCount(0);
		});

		const addedTitle = `added-${getRandomString()}.png`;

		await test.step('Add a new attachment via the creation menu', async () => {
			await commerceAdminOrderAttachmentsPage.addAttachmentMenuItem.click();

			await expect(
				commerceAdminOrderAttachmentsPage.sidePanelTitleInput
			).toBeVisible();

			await commerceAdminOrderAttachmentsPage.sidePanelTitleInput.fill(
				addedTitle
			);
			await commerceAdminOrderAttachmentsPage.sidePanelTypeSelect.selectOption(
				'invoice'
			);
			await commerceAdminOrderAttachmentsPage.sidePanelPriorityInput.fill(
				'5'
			);

			const fileChooserPromise = page.waitForEvent('filechooser');

			await commerceAdminOrderAttachmentsPage.sidePanelSelectFileButton.click();

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles({
				buffer: Buffer.from(
					'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=',
					'base64'
				),
				mimeType: 'image/png',
				name: addedTitle,
			});

			await commerceAdminOrderAttachmentsPage.sidePanelRestrictedCheckbox.setChecked(
				true
			);

			await commerceAdminOrderAttachmentsPage.sidePanelSaveButton.click();

			await waitForAlert(page);

			await expect(
				commerceAdminOrderAttachmentsPage.sidePanelTitleInput
			).toHaveCount(0);
			await expect(
				commerceAdminOrderAttachmentsPage.rowByTitle(addedTitle)
			).toBeVisible();
			await expect(
				commerceAdminOrderAttachmentsPage.rowRestrictedIcon(addedTitle)
			).toBeVisible();
		});

		await test.step('Verify the file has the checkbox set', async () => {
			await commerceAdminOrderAttachmentsPage.openEditSidePanel(
				addedTitle
			);

			await expect(
				commerceAdminOrderAttachmentsPage.sidePanelRestrictedCheckbox
			).toBeChecked();

			await expect(async () => {
				if (
					await commerceAdminOrderAttachmentsPage.sidePanelRestrictedCheckbox.isVisible(
						{timeout: 100}
					)
				) {
					await commerceAdminOrderAttachmentsPage.sidePanelCancelButton.click(
						{timeout: 1000}
					);
				}

				await expect(
					commerceAdminOrderAttachmentsPage.sidePanelRestrictedCheckbox
				).toHaveCount(0, {timeout: 500});
			}).toPass({timeout: 5000});
		});
	}
);

test(
	'Verify that edits made in the Admin management page are reflected on the store front',
	{tag: ['@LPD-83042']},
	async ({
		apiHelpers,
		commerceAdminOrderAttachmentsPage,
		commerceAdminOrdersPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
	}) => {
		test.setTimeout(80000);

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

		const attachmentTitle = `added-${getRandomString()}.png`;

		await test.step('Add attachment from admin panel', async () => {
			await commerceAdminOrdersPage.goto();

			await (
				await commerceAdminOrdersPage.tableRowLink({
					colIndex: 1,
					rowValue: order.id,
				})
			).click();
			await commerceAdminOrderAttachmentsPage.attachmentsTab.click();
			await commerceAdminOrderAttachmentsPage.addAttachmentMenuItem.click();

			await expect(
				commerceAdminOrderAttachmentsPage.sidePanelTitleInput
			).toBeVisible();

			await commerceAdminOrderAttachmentsPage.sidePanelTitleInput.fill(
				attachmentTitle
			);
			await commerceAdminOrderAttachmentsPage.sidePanelTypeSelect.selectOption(
				'invoice'
			);
			await commerceAdminOrderAttachmentsPage.sidePanelPriorityInput.fill(
				'5'
			);

			const fileChooserPromise = page.waitForEvent('filechooser');

			await commerceAdminOrderAttachmentsPage.sidePanelSelectFileButton.click();

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles({
				buffer: Buffer.from(
					'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=',
					'base64'
				),
				mimeType: 'image/png',
				name: attachmentTitle,
			});

			await commerceAdminOrderAttachmentsPage.sidePanelSaveButton.click();

			await waitForAlert(page);

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			const displayPageTemplateName = getRandomString();

			await displayPageTemplatesPage.createTemplate({
				contentType: 'Order',
				name: displayPageTemplateName,
			});
			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);
			await pageEditorPage.addFragment(
				'Order',
				'Order Attachments Data Set'
			);

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
				page.getByText(attachmentTitle, {exact: true})
			).toBeVisible();
			await expect(page.getByText('png', {exact: true})).toBeVisible();
			await expect(
				page.getByText('Invoice', {exact: true})
			).toBeVisible();
		});

		await test.step('Delete the attachment from the admin panel', async () => {
			await expect(async () => {
				if (
					!(await commerceAdminOrderAttachmentsPage.deleteConfirmButton.isVisible())
				) {
					await commerceAdminOrderAttachmentsPage
						.rowActionsButton(attachmentTitle)
						.click();
					await expect(
						commerceAdminOrderAttachmentsPage.deleteRowAction
					).toBeVisible({timeout: 500});
					await commerceAdminOrderAttachmentsPage.deleteRowAction.click(
						{timeout: 500}
					);
				}

				await expect(
					commerceAdminOrderAttachmentsPage.deleteConfirmButton
				).toBeVisible({timeout: 500});
			}).toPass({timeout: 5000});

			await commerceAdminOrderAttachmentsPage.deleteConfirmButton.click();

			await page.goto(
				liferayConfig.environment.baseUrl +
					`/web/${site.name}/order/${order.id}`
			);

			await expect(page.getByText(attachmentTitle)).toHaveCount(0);
		});
	}
);
