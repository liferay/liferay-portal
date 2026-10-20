/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import * as path from 'path';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import getGlobalSiteId from '../../../../utils/getGlobalSiteId';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test('LPD-3209 The download URL is present when the file entry is a file upload', async ({
	apiHelpers,
	attachmentsPage,
}) => {
	await attachmentsPage.goToDocumentsAndMedia();
	await attachmentsPage.createFileEntry(
		path.join(__dirname, '/dependencies/attachment.txt'),
		'Attachment File Upload'
	);

	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: 'Catalog',
	});

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {
			en_US: 'Product',
		},
	});

	const siteDocumentsPage =
		await apiHelpers.headlessDelivery.getSiteDocumentsPage(
			site.id,
			'id:desc'
		);

	const siteDocument = siteDocumentsPage.items[0];

	const attachment =
		await apiHelpers.headlessCommerceAdminCatalog.postAttachment(
			product.productId,
			siteDocument.id,
			siteDocument.title
		);

	expect(attachment.fileEntryId).not.toBeNull();
	expect(attachment.src).not.toBe('');

	const channelProductAttachmentsPage =
		await apiHelpers.headlessCommerceDeliveryCatalog.getChannelProductAttachmentsPage(
			channel.id,
			product.productId
		);

	const channelProductAttachment = channelProductAttachmentsPage.items[0];

	expect(channelProductAttachment.fileEntryId).not.toBeNull();
	expect(channelProductAttachment.src).not.toBe('');

	await apiHelpers.headlessDelivery.deleteDocument(siteDocument.id);
});

test('LPD-3209 The download URL is not present when the file entry is an external resource', async ({
	apiHelpers,
	attachmentsPage,
}) => {
	await attachmentsPage.goToDocumentsAndMedia();
	await attachmentsPage.createExternalVideoShortcut(
		'https://www.youtube.com/watch?v=lMprg3wqgbE',
		'External Video Shortcut'
	);

	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	const channel = await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: 'Catalog',
	});

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {
			en_US: 'Product',
		},
	});

	const siteDocumentsPage =
		await apiHelpers.headlessDelivery.getSiteDocumentsPage(
			site.id,
			'id:desc'
		);

	const siteDocument = siteDocumentsPage.items[0];

	const attachment =
		await apiHelpers.headlessCommerceAdminCatalog.postAttachment(
			product.productId,
			siteDocument.id,
			siteDocument.title
		);

	expect(attachment.fileEntryId).not.toBeNull();
	expect(attachment.src).toBe('');

	const channelProductAttachmentsPage =
		await apiHelpers.headlessCommerceDeliveryCatalog.getChannelProductAttachmentsPage(
			channel.id,
			product.productId
		);

	const channelProductAttachment = channelProductAttachmentsPage.items[0];

	expect(channelProductAttachment.fileEntryId).not.toBeNull();
	expect(channelProductAttachment.src).toBe('');

	await apiHelpers.headlessDelivery.deleteDocument(siteDocument.id);
});

test(
	'Select the file of a product attachment',
	{tag: '@LPD-110416'},
	async ({
		apiHelpers,
		commerceAdminProductDetailsMediaPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
	}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
			});

		const documentTitle = getRandomString();

		const document = await apiHelpers.headlessDelivery.postDocument(
			await getGlobalSiteId(apiHelpers),
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.txt')
			),
			{fileName: `${documentTitle}.txt`, title: documentTitle}
		);

		apiHelpers.data.push({id: document.id, type: 'document'});

		await test.step('Open the add attachment side panel', async () => {
			await commerceAdminProductPage.gotoProduct(product.name['en_US']);

			await commerceAdminProductDetailsPage.productMediaLink.click();

			await commerceAdminProductDetailsMediaPage.addAttachmentButton.click();
		});

		await test.step('Select the file', async () => {
			await clickAndExpectToBeVisible({
				target: commerceAdminProductDetailsMediaPage.selectFileIframe,
				trigger: commerceAdminProductDetailsMediaPage.selectFileButton,
			});

			const searchInput =
				commerceAdminProductDetailsMediaPage.selectFileFrame.getByPlaceholder(
					'Search for'
				);

			await searchInput.fill(documentTitle);
			await searchInput.press('Enter');

			await commerceAdminProductDetailsMediaPage.selectFileFrame
				.getByText(documentTitle, {exact: true})
				.click();

			await expect(
				commerceAdminProductDetailsMediaPage.sidePanelFrame.locator(
					'[id$="fileEntryTitle"]'
				)
			).toHaveText(documentTitle);
		});

		await test.step('Publish', async () => {
			const title = getRandomString();

			await commerceAdminProductDetailsMediaPage.sidePanelFrame
				.getByLabel('Title')
				.fill(title);

			await commerceAdminProductDetailsMediaPage.sidePanelFrame
				.getByRole('button', {exact: true, name: 'Publish'})
				.click();

			await waitForAlert(
				commerceAdminProductDetailsMediaPage.sidePanelFrame
			);

			await commerceAdminProductDetailsMediaPage.page.reload();

			await expect(
				commerceAdminProductDetailsMediaPage.mediaForm.getByText(
					title,
					{
						exact: true,
					}
				)
			).toBeVisible();
		});
	}
);
