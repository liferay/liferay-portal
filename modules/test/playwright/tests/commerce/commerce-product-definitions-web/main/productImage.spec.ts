/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import * as path from 'path';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {TProduct} from '../../../../helpers/HeadlessCommerceAdminCatalogApiHelper';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import getGlobalSiteId from '../../../../utils/getGlobalSiteId';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

async function postImageDocument(apiHelpers: DataApiHelpers) {
	const title = getRandomString();

	const document = await apiHelpers.headlessDelivery.postDocument(
		await getGlobalSiteId(apiHelpers),
		createReadStream(path.join(__dirname, '/dependencies/liferay.png')),
		{fileName: `${title}.png`, title}
	);

	apiHelpers.data.push({id: document.id, type: 'document'});

	return document;
}

async function addProductImage(
	apiHelpers: DataApiHelpers,
	product?: Partial<TProduct>
) {
	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const postedProduct =
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			...(product || {}),
		});

	const document = await postImageDocument(apiHelpers);

	const title = getRandomString();

	await apiHelpers.headlessCommerceAdminCatalog.postImage(
		postedProduct.productId,
		document.id,
		title
	);

	return {product: postedProduct, title};
}

test(
	'Add a product image',
	{tag: ['@COMMERCE-6025', '@LPD-108797', '@LPD-113916']},
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

		const document = await postImageDocument(apiHelpers);

		await test.step('Open the add image side panel', async () => {
			await commerceAdminProductPage.gotoProduct(product.name['en_US']);

			await clickAndExpectToBeVisible({
				target: commerceAdminProductDetailsMediaPage.addImageButton,
				trigger: commerceAdminProductDetailsPage.productMediaLink,
			});

			await clickAndExpectToBeVisible({
				target: commerceAdminProductDetailsMediaPage.sidePanelTitleInput,
				trigger: commerceAdminProductDetailsMediaPage.addImageButton,
			});
		});

		await test.step('Select the image', async () => {
			await clickAndExpectToBeVisible({
				target: commerceAdminProductDetailsMediaPage.selectFileIframe,
				trigger: commerceAdminProductDetailsMediaPage.selectFileButton,
			});

			await commerceAdminProductDetailsMediaPage.selectFileSearchInput.fill(
				document.title
			);
			await commerceAdminProductDetailsMediaPage.selectFileSearchInput.press(
				'Enter'
			);

			await commerceAdminProductDetailsMediaPage
				.selectFileItem(document.title)
				.click();

			await expect(
				commerceAdminProductDetailsMediaPage.sidePanelCurrentImage
			).toBeVisible();
		});

		await test.step('Publish', async () => {
			const title = getRandomString();

			await commerceAdminProductDetailsMediaPage.sidePanelTitleInput.fill(
				title
			);

			await commerceAdminProductDetailsMediaPage.publishAttachment();

			await commerceAdminProductDetailsMediaPage.page.reload();

			await expect(
				commerceAdminProductDetailsMediaPage.mediaImagesTable.getByText(
					title,
					{exact: true}
				)
			).toBeVisible();
		});
	}
);

test(
	'Add a product image to an option',
	{tag: ['@COMMERCE-6028', '@LPD-108797', '@LPD-113916']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsMediaPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
	}) => {
		const option = await apiHelpers.headlessCommerceAdminCatalog.postOption(
			'select',
			getRandomString().toLowerCase(),
			'Color'
		);

		const {product, title} = await addProductImage(apiHelpers, {
			productOptions: [
				{
					fieldType: 'select',
					key: option.key,
					name: {en_US: 'Color'},
					optionId: option.id,
					priority: 1,
					productOptionValues: [
						{
							key: 'black',
							name: {en_US: 'Black'},
							priority: 1,
						},
					],
					required: true,
					skuContributor: true,
				},
			],
		});

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await clickAndExpectToBeVisible({
			target: commerceAdminProductDetailsMediaPage.addImageButton,
			trigger: commerceAdminProductDetailsPage.productMediaLink,
		});

		await test.step('Select the option value', async () => {
			await commerceAdminProductDetailsMediaPage.openAttachment(title);

			await commerceAdminProductDetailsMediaPage
				.sidePanelOptionSelect('Color')
				.selectOption({label: 'Black'});

			await commerceAdminProductDetailsMediaPage.publishAttachment();
		});

		await test.step('Check the option value', async () => {
			await commerceAdminProductDetailsMediaPage.openAttachment(title);

			await expect(
				commerceAdminProductDetailsMediaPage
					.sidePanelOptionSelect('Color')
					.locator('option:checked')
			).toHaveText('Black');
		});
	}
);

test(
	'Edit a product image',
	{tag: ['@COMMERCE-6027', '@LPD-108797', '@LPD-113916']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsMediaPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
	}) => {
		const {product, title} = await addProductImage(apiHelpers);

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await clickAndExpectToBeVisible({
			target: commerceAdminProductDetailsMediaPage.addImageButton,
			trigger: commerceAdminProductDetailsPage.productMediaLink,
		});

		const editedTitle = getRandomString();

		await commerceAdminProductDetailsMediaPage.openAttachment(title);

		await commerceAdminProductDetailsMediaPage.sidePanelTitleInput.fill(
			editedTitle
		);

		await commerceAdminProductDetailsMediaPage.publishAttachment();

		await commerceAdminProductDetailsMediaPage.page.reload();

		await expect(
			commerceAdminProductDetailsMediaPage.mediaImagesTable.getByText(
				editedTitle,
				{exact: true}
			)
		).toBeVisible();
		await expect(
			commerceAdminProductDetailsMediaPage.mediaImagesTable.getByText(
				title,
				{exact: true}
			)
		).toHaveCount(0);
	}
);

test(
	'Add and remove the tags of a product image',
	{tag: ['@COMMERCE-12852', '@LPD-108797', '@LPD-113916']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsMediaPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
	}) => {
		const {product, title} = await addProductImage(apiHelpers);

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
			target: commerceAdminProductDetailsMediaPage.addImageButton,
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
				.sidePanelTagRemoveButton(newTagName)
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

			await commerceAdminProductDetailsMediaPage
				.sidePanelTagsSelectCheckbox(newTagName)
				.check();

			await commerceAdminProductDetailsMediaPage.sidePanelDoneButton.click();

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
	'Remove a product image',
	{tag: ['@COMMERCE-6026', '@LPD-108797', '@LPD-113916']},
	async ({
		apiHelpers,
		commerceAdminProductDetailsMediaPage,
		commerceAdminProductDetailsPage,
		commerceAdminProductPage,
	}) => {
		const {product, title} = await addProductImage(apiHelpers);

		await commerceAdminProductPage.gotoProduct(product.name['en_US']);

		await clickAndExpectToBeVisible({
			target: commerceAdminProductDetailsMediaPage.addImageButton,
			trigger: commerceAdminProductDetailsPage.productMediaLink,
		});

		await commerceAdminProductDetailsMediaPage
			.attachmentActionsButton(title)
			.click();

		await commerceAdminProductDetailsMediaPage.deleteMenuItem.click();

		await waitForAlert(commerceAdminProductDetailsMediaPage.page);

		await expect(
			commerceAdminProductDetailsMediaPage.mediaImagesTable.getByText(
				title,
				{exact: true}
			)
		).toHaveCount(0);
	}
);
