/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import * as path from 'path';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {customFieldsPagesTest} from '../../../../fixtures/customFieldsPagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import getGlobalSiteId from '../../../../utils/getGlobalSiteId';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	customFieldsPagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
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

async function addProductAttachment(apiHelpers: DataApiHelpers) {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
	});

	const title = getRandomString();

	const document = await apiHelpers.headlessDelivery.postDocument(
		await getGlobalSiteId(apiHelpers),
		createReadStream(path.join(__dirname, '/dependencies/attachment.txt')),
		{fileName: `${title}.txt`, title}
	);

	apiHelpers.data.push({id: document.id, type: 'document'});

	await apiHelpers.headlessCommerceAdminCatalog.postAttachment(
		product.productId,
		document.id,
		title
	);

	return {product, title};
}

test(
	'Add a product attachment',
	{tag: ['@COMMERCE-6030', '@LPD-108797', '@LPD-111085', '@LPD-113914']},
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

			await clickAndExpectToBeVisible({
				target: commerceAdminProductDetailsMediaPage.addAttachmentButton,
				trigger: commerceAdminProductDetailsPage.productMediaLink,
			});

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
		});

		await test.step('Publish', async () => {
			const title = getRandomString();

			await commerceAdminProductDetailsMediaPage.sidePanelFrame
				.getByLabel('Title')
				.fill(title);

			await commerceAdminProductDetailsMediaPage.publishAttachment();

			await commerceAdminProductDetailsMediaPage.page.reload();

			await expect(
				commerceAdminProductDetailsMediaPage.mediaForm.getByText(
					title,
					{exact: true}
				)
			).toBeVisible();
		});
	}
);

test(
	'Edit a product attachment',
	{tag: ['@COMMERCE-6031', '@LPD-108797', '@LPD-111085', '@LPD-113914']},
	async ({
		addCustomFieldPage,
		apiHelpers,
		commerceAdminProductDetailsMediaPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
		viewAttributesPage,
	}) => {
		const customFieldName = getRandomString();

		await addCustomFieldPage.addCustomField({
			fieldName: customFieldName,
			fieldType: 'inputField',
			resource: 'Product Attachment',
		});

		try {
			const {product, title} = await addProductAttachment(apiHelpers);

			await commerceAdminProductPage.gotoProduct(product.name['en_US']);

			await clickAndExpectToBeVisible({
				target: commerceAdminProductDetailsMediaPage.addAttachmentButton,
				trigger: commerceAdminProductDetailsPage.productMediaLink,
			});

			const editedTitle = getRandomString();

			await test.step('Edit the title and the custom field', async () => {
				await commerceAdminProductDetailsMediaPage.openAttachment(
					title
				);

				await commerceAdminProductDetailsMediaPage.sidePanelFrame
					.getByLabel('Title')
					.fill(editedTitle);

				await commerceAdminProductDetailsMediaPage
					.sidePanelCustomFieldInput(customFieldName)
					.fill('Test');

				await commerceAdminProductDetailsMediaPage.sidePanelFrame
					.getByRole('button', {exact: true, name: 'Save as Draft'})
					.click();

				await waitForAlert(
					commerceAdminProductDetailsMediaPage.sidePanelFrame
				);
			});

			await test.step('Check the edited attachment', async () => {
				await commerceAdminProductDetailsMediaPage.openAttachment(
					editedTitle
				);

				await expect(
					commerceAdminProductDetailsMediaPage.sidePanelCustomFieldInput(
						customFieldName
					)
				).toHaveValue('Test');
			});
		}
		finally {
			await viewAttributesPage.deleteCustomField(
				customFieldName,
				'Product Attachment'
			);
		}
	}
);

test(
	'Add and remove the tags of a product attachment',
	{tag: ['@COMMERCE-12853', '@LPD-108797', '@LPD-111085', '@LPD-113914']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsMediaPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
	}) => {
		const {product, title} = await addProductAttachment(apiHelpers);

		const tagPrefix = getRandomString().toLowerCase();

		const featuredTagName = `${tagPrefix}featured`;
		const newTagName = `${tagPrefix}new`;

		const globalSiteId = await getGlobalSiteId(apiHelpers);

		for (const tagName of [featuredTagName, newTagName]) {
			await apiHelpers.headlessAdminTaxonomy.postSiteKeyword({
				name: tagName,
				siteId: globalSiteId,
			});
		}

		const expectTags = async (tagNames: string[]) => {
			await commerceAdminProductDetailsMediaPage.openAttachment(title);

			for (const tagName of [featuredTagName, newTagName]) {
				const tag =
					commerceAdminProductDetailsMediaPage.sidePanelTag(tagName);

				if (tagNames.includes(tagName)) {
					await expect(tag).toBeVisible();
				}
				else {
					await expect(tag).toHaveCount(0);
				}
			}
		};

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await clickAndExpectToBeVisible({
			target: commerceAdminProductDetailsMediaPage.addAttachmentButton,
			trigger: commerceAdminProductDetailsPage.productMediaLink,
		});

		await test.step('Add tags by typing them', async () => {
			await commerceAdminProductDetailsMediaPage.openAttachment(title);

			for (const tagName of [featuredTagName, newTagName]) {
				await commerceAdminProductDetailsMediaPage.sidePanelTagsInput.fill(
					tagName
				);

				await commerceAdminProductDetailsMediaPage
					.sidePanelTagOption(tagName)
					.click();
			}

			await commerceAdminProductDetailsMediaPage.publishAttachment();

			await expectTags([featuredTagName, newTagName]);
		});

		await test.step('Remove a tag', async () => {
			await commerceAdminProductDetailsMediaPage
				.sidePanelTag(newTagName)
				.getByRole('button')
				.click();

			await commerceAdminProductDetailsMediaPage.publishAttachment();

			await expectTags([featuredTagName]);
		});

		await test.step('Add a tag through the select modal', async () => {
			await clickAndExpectToBeVisible({
				target: commerceAdminProductDetailsMediaPage.sidePanelTagsSelectIframe,
				trigger:
					commerceAdminProductDetailsMediaPage.sidePanelTagsSelectButton,
			});

			await commerceAdminProductDetailsMediaPage.sidePanelTagsSelectFrame
				.getByRole('row')
				.filter({hasText: newTagName})
				.getByRole('checkbox')
				.check();

			await commerceAdminProductDetailsMediaPage.sidePanelFrame
				.getByRole('button', {exact: true, name: 'Done'})
				.click();

			await commerceAdminProductDetailsMediaPage.publishAttachment();

			await expectTags([featuredTagName, newTagName]);
		});

		await test.step('Clear all the tags', async () => {
			await commerceAdminProductDetailsMediaPage.sidePanelTagsClearAllButton.click();

			await commerceAdminProductDetailsMediaPage.publishAttachment();

			await expectTags([]);
		});

		await test.step('Add a tag from the autocomplete', async () => {
			await commerceAdminProductDetailsMediaPage.sidePanelTagsInput.fill(
				tagPrefix
			);

			await expect(
				commerceAdminProductDetailsMediaPage.sidePanelTagOption(
					featuredTagName
				)
			).toBeVisible();
			await expect(
				commerceAdminProductDetailsMediaPage.sidePanelTagOption(
					newTagName
				)
			).toBeVisible();

			await commerceAdminProductDetailsMediaPage.sidePanelTagsInput.fill(
				`${tagPrefix}n`
			);

			await expect(
				commerceAdminProductDetailsMediaPage.sidePanelTagOption(
					featuredTagName
				)
			).toHaveCount(0);

			await commerceAdminProductDetailsMediaPage
				.sidePanelTagOption(newTagName)
				.click();

			await commerceAdminProductDetailsMediaPage.publishAttachment();

			await expectTags([newTagName]);
		});
	}
);

test(
	'Remove a product attachment',
	{tag: ['@COMMERCE-6033', '@LPD-108797', '@LPD-111085', '@LPD-113914']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsMediaPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
	}) => {
		const {product, title} = await addProductAttachment(apiHelpers);

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await clickAndExpectToBeVisible({
			target: commerceAdminProductDetailsMediaPage.addAttachmentButton,
			trigger: commerceAdminProductDetailsPage.productMediaLink,
		});

		await commerceAdminProductDetailsMediaPage.mediaForm
			.getByRole('row')
			.filter({hasText: title})
			.getByRole('button', {name: 'Actions'})
			.click();

		await commerceAdminProductDetailsMediaPage.page
			.getByRole('menuitem', {name: 'Delete'})
			.click();

		await waitForAlert(commerceAdminProductDetailsMediaPage.page);

		await expect(
			commerceAdminProductDetailsMediaPage.mediaForm.getByText(title, {
				exact: true,
			})
		).toHaveCount(0);
	}
);

test(
	'Product attachments show in a collection display fragment',
	{tag: '@COMMERCE-9434'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

		const attachmentTitles = [getRandomString(), getRandomString()];

		for (const attachmentTitle of attachmentTitles) {
			const document = await apiHelpers.headlessDelivery.postDocument(
				site.id,
				createReadStream(
					path.join(__dirname, '/dependencies/attachment.txt')
				),
				{fileName: `${attachmentTitle}.txt`, title: attachmentTitle}
			);

			apiHelpers.data.push({id: document.id, type: 'document'});

			await apiHelpers.headlessCommerceAdminCatalog.postAttachment(
				product.productId,
				document.id,
				attachmentTitle
			);
		}

		await test.step('Add a collection display of the product attachments', async () => {
			const className =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					'com.liferay.commerce.product.model.CPDefinition'
				);

			const displayPageTemplateName = getRandomString();

			const displayPageTemplate =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
					{
						classNameId: className.classNameId,
						groupId: site.id,
						name: displayPageTemplateName,
					}
				);

			apiHelpers.data.push({
				id: displayPageTemplate.layoutPageTemplateEntryId,
				type: 'layoutPageTemplateEntry',
			});

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
				{
					layoutPageTemplateEntryId:
						displayPageTemplate.layoutPageTemplateEntryId,
				}
			);

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			await pageEditorPage.addFragment(
				'Content Display',
				'Collection Display'
			);
			await pageEditorPage.selectFragment(
				await pageEditorPage.getFragmentId('Collection Display')
			);

			await page.getByText('Select a collection to display.').click();

			await pageEditorPage.chooseCollectionDisplayCollection(
				'Related Items Collection Providers',
				'Product Attachments'
			);
			await pageEditorPage.waitForChangesSaved();

			await page
				.getByLabel('Style Display', {exact: true})
				.selectOption('Bordered List');

			await pageEditorPage.waitForChangesSaved();

			await displayPageTemplatesPage.publishTemplate();
		});

		await test.step('Check the attachments on the product page', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}/p/${product.urls['en_US']}`
			);

			for (const attachmentTitle of attachmentTitles) {
				await expect(
					page
						.getByRole('listitem')
						.filter({hasText: attachmentTitle})
						.getByRole('link', {name: 'Download'})
				).toBeVisible();
			}
		});
	}
);
