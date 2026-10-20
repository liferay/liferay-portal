/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../../utils/getRandomString';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

test(
	'Pick the product, the page and the display page template of a product display page',
	{tag: '@LPD-110416'},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsProductDisplayPagesPage,
		commerceAdminChannelsPage,
		site,
	}) => {
		const channel =
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

		const layoutName = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: layoutName,
		});

		const className =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				'com.liferay.commerce.product.model.CPDefinition'
			);

		const displayPageTemplateName = getRandomString();

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
			{
				classNameId: className.classNameId,
				groupId: site.id,
				name: displayPageTemplateName,
			}
		);

		await test.step('Open the side panel', async () => {
			await commerceAdminChannelsPage.goto();

			await (
				await commerceAdminChannelsPage.channelsTableRowLink(
					channel.name
				)
			).click();

			await commerceAdminChannelDetailsProductDisplayPagesPage.goToProductDisplayPages();

			await commerceAdminChannelDetailsProductDisplayPagesPage.addDisplayLayoutButton.click();
		});

		await test.step('Pick the display page template', async () => {
			await commerceAdminChannelDetailsProductDisplayPagesPage.chooseDisplayPage(
				'Display Page Template'
			);

			await commerceAdminChannelDetailsProductDisplayPagesPage.selectProductDisplayPageFrame
				.getByText(displayPageTemplateName, {exact: true})
				.click();

			await expect(
				commerceAdminChannelDetailsProductDisplayPagesPage.displayPageName
			).toHaveText(displayPageTemplateName);
		});

		await test.step('Pick the page', async () => {
			await commerceAdminChannelDetailsProductDisplayPagesPage.chooseDisplayPage(
				'Layout'
			);

			await commerceAdminChannelDetailsProductDisplayPagesPage.selectProductDisplayPageFrame
				.getByText(layoutName, {exact: true})
				.click();

			await commerceAdminChannelDetailsProductDisplayPagesPage.doneButton.click();

			await expect(
				commerceAdminChannelDetailsProductDisplayPagesPage.displayPageName
			).toContainText(layoutName);
		});

		await test.step('Pick the product', async () => {
			await commerceAdminChannelDetailsProductDisplayPagesPage.selectProduct(
				product.name['en_US']
			);

			await expect(
				commerceAdminChannelDetailsProductDisplayPagesPage.addDisplayLayoutFrame.getByText(
					product.name['en_US'],
					{exact: true}
				)
			).toBeVisible();
		});

		await test.step('Save', async () => {
			await commerceAdminChannelDetailsProductDisplayPagesPage.saveButton.click();

			await expect(
				commerceAdminChannelDetailsProductDisplayPagesPage.successMessage
			).toBeVisible();
		});
	}
);

test(
	'Add, edit and delete a product display page override',
	{tag: ['@COMMERCE-7842', '@COMMERCE-7951', '@COMMERCE-9433']},
	async ({
		apiHelpers,
		commerceAdminChannelDetailsProductDisplayPagesPage,
		commerceAdminChannelsPage,
		site,
	}) => {
		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});
		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

		const layout1 = await _addProductDetailsLayout(
			apiHelpers,
			site.id,
			false
		);
		const layout2 = await _addProductDetailsLayout(
			apiHelpers,
			site.id,
			false
		);

		await commerceAdminChannelsPage.goto();

		await (
			await commerceAdminChannelsPage.channelsTableRowLink(channel.name)
		).click();

		await commerceAdminChannelDetailsProductDisplayPagesPage.goToProductDisplayPages();

		await test.step('Save without a page', async () => {
			await commerceAdminChannelDetailsProductDisplayPagesPage.addDisplayLayoutButton.click();

			await commerceAdminChannelDetailsProductDisplayPagesPage.selectProduct(
				product1.name['en_US']
			);

			await commerceAdminChannelDetailsProductDisplayPagesPage.saveButton.click();

			await expect(
				commerceAdminChannelDetailsProductDisplayPagesPage.addDisplayLayoutFrame.getByText(
					'Please select a valid layout.'
				)
			).toBeVisible();

			await commerceAdminChannelDetailsProductDisplayPagesPage.closeButton.click();
		});

		await test.step('Save without a product', async () => {
			await commerceAdminChannelDetailsProductDisplayPagesPage.addDisplayLayoutButton.click();

			await commerceAdminChannelDetailsProductDisplayPagesPage.selectLayout(
				layout1.nameCurrentValue
			);

			await commerceAdminChannelDetailsProductDisplayPagesPage.saveButton.click();

			await expect(
				commerceAdminChannelDetailsProductDisplayPagesPage.addDisplayLayoutFrame.getByText(
					'Please select a valid product.'
				)
			).toBeVisible();

			await commerceAdminChannelDetailsProductDisplayPagesPage.closeButton.click();
		});

		await test.step('Add the override', async () => {
			await commerceAdminChannelDetailsProductDisplayPagesPage.addDisplayLayoutButton.click();

			await commerceAdminChannelDetailsProductDisplayPagesPage.selectLayout(
				layout1.nameCurrentValue
			);

			await commerceAdminChannelDetailsProductDisplayPagesPage.selectProduct(
				product1.name['en_US']
			);

			await commerceAdminChannelDetailsProductDisplayPagesPage.saveButton.click();

			await expect(
				commerceAdminChannelDetailsProductDisplayPagesPage.successMessage
			).toBeVisible();

			await commerceAdminChannelDetailsProductDisplayPagesPage.closeButton.click();

			await expect(
				commerceAdminChannelDetailsProductDisplayPagesPage.tableRow(
					product1.name['en_US']
				)
			).toContainText(layout1.nameCurrentValue);
		});

		await test.step('Edit the override', async () => {
			await commerceAdminChannelDetailsProductDisplayPagesPage.clickTableRowAction(
				product1.name['en_US'],
				'Edit'
			);

			await commerceAdminChannelDetailsProductDisplayPagesPage.selectLayout(
				layout2.nameCurrentValue
			);

			await commerceAdminChannelDetailsProductDisplayPagesPage.selectProduct(
				product2.name['en_US']
			);

			await commerceAdminChannelDetailsProductDisplayPagesPage.saveButton.click();

			await expect(
				commerceAdminChannelDetailsProductDisplayPagesPage.successMessage
			).toBeVisible();

			await commerceAdminChannelDetailsProductDisplayPagesPage.closeButton.click();

			await expect(
				commerceAdminChannelDetailsProductDisplayPagesPage.tableRow(
					product1.name['en_US']
				)
			).toHaveCount(0);
			await expect(
				commerceAdminChannelDetailsProductDisplayPagesPage.tableRow(
					product2.name['en_US']
				)
			).toContainText(layout2.nameCurrentValue);
		});

		await test.step('Delete the override', async () => {
			await commerceAdminChannelDetailsProductDisplayPagesPage.clickTableRowAction(
				product2.name['en_US'],
				'Delete'
			);

			await expect(
				commerceAdminChannelDetailsProductDisplayPagesPage.tableRow(
					product2.name['en_US']
				)
			).toHaveCount(0);
		});
	}
);

test(
	'Default and override product display pages route to public and private pages',
	{
		tag: [
			'@COMMERCE-7838',
			'@COMMERCE-7839',
			'@COMMERCE-7840',
			'@COMMERCE-7841',
			'@COMMERCE-7842',
		],
	},
	async ({apiHelpers, page, site}) => {
		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});
		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});
		const product3 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

		const publicLayout1 = await _addProductDetailsLayout(
			apiHelpers,
			site.id,
			false
		);
		const publicLayout2 = await _addProductDetailsLayout(
			apiHelpers,
			site.id,
			false
		);
		const privateLayout1 = await _addProductDetailsLayout(
			apiHelpers,
			site.id,
			true
		);
		const privateLayout2 = await _addProductDetailsLayout(
			apiHelpers,
			site.id,
			true
		);

		await test.step('Override a private default page with a public and a private page', async () => {
			await apiHelpers.headlessCommerceAdminChannel.postDefaultProductDisplayPage(
				channel.id,
				{pageUuid: privateLayout1.uuid}
			);

			await apiHelpers.headlessCommerceAdminChannel.postProductDisplayPage(
				channel.id,
				{pageUuid: publicLayout1.uuid, productId: product1.id}
			);
			await apiHelpers.headlessCommerceAdminChannel.postProductDisplayPage(
				channel.id,
				{pageUuid: privateLayout2.uuid, productId: product2.id}
			);

			await _assertProductDisplayPage(
				publicLayout1,
				page,
				product1,
				site
			);
			await _assertProductDisplayPage(
				privateLayout2,
				page,
				product2,
				site
			);
			await _assertProductDisplayPage(
				privateLayout1,
				page,
				product3,
				site
			);
		});

		await test.step('Override a public default page with a public and a private page', async () => {
			await apiHelpers.headlessCommerceAdminChannel.postDefaultProductDisplayPage(
				channel.id,
				{pageUuid: publicLayout2.uuid}
			);

			await _assertProductDisplayPage(
				publicLayout1,
				page,
				product1,
				site
			);
			await _assertProductDisplayPage(
				privateLayout2,
				page,
				product2,
				site
			);
			await _assertProductDisplayPage(
				publicLayout2,
				page,
				product3,
				site
			);
		});
	}
);

test(
	'A product display page template override wins over the default',
	{tag: '@COMMERCE-9433'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});
		const product2 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});
		const product3 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: getRandomString()},
			});

		const layout = await _addProductDetailsLayout(
			apiHelpers,
			site.id,
			false
		);

		const displayPageTemplate1 = await _addProductDisplayPageTemplate(
			apiHelpers,
			displayPageTemplatesPage,
			'Heading',
			pageEditorPage,
			site
		);
		const displayPageTemplate2 = await _addProductDisplayPageTemplate(
			apiHelpers,
			displayPageTemplatesPage,
			'Paragraph',
			pageEditorPage,
			site
		);

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
			{
				layoutPageTemplateEntryId:
					displayPageTemplate2.layoutPageTemplateEntryId,
			}
		);

		const displayPageTemplate1Text = page.getByText('Heading Example');

		await test.step('Override the default display page template', async () => {
			await apiHelpers.headlessCommerceAdminChannel.postProductDisplayPage(
				channel.id,
				{
					pageTemplateUuid: displayPageTemplate1.uuid,
					productId: product1.id,
				}
			);
			await apiHelpers.headlessCommerceAdminChannel.postProductDisplayPage(
				channel.id,
				{pageUuid: layout.uuid, productId: product3.id}
			);

			await page.goto(
				`/web${site.friendlyUrlPath}/p/${product1.urls['en_US']}`
			);

			await expect(displayPageTemplate1Text).toBeVisible();

			await page.goto(
				`/web${site.friendlyUrlPath}/p/${product2.urls['en_US']}`
			);

			const displayPageTemplate2Text = page.getByText(
				'A paragraph is a self-contained unit'
			);

			await expect(displayPageTemplate2Text).toBeVisible();

			await _assertProductDisplayPage(layout, page, product3, site);

			await expect(displayPageTemplate2Text).toHaveCount(0);
		});

		await test.step('Set a default product display page', async () => {
			await apiHelpers.headlessCommerceAdminChannel.postDefaultProductDisplayPage(
				channel.id,
				{pageUuid: layout.uuid}
			);

			await page.goto(
				`/web${site.friendlyUrlPath}/p/${product1.urls['en_US']}`
			);

			await expect(displayPageTemplate1Text).toBeVisible();
		});
	}
);

async function _addProductDetailsLayout(
	apiHelpers,
	groupId: string,
	privateLayout: boolean
) {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId,
		privateLayout: String(privateLayout),
		title: getRandomString(),
		typeSettings:
			'layout-template-id=1_column\ncolumn-1=com_liferay_commerce_product_content_web_internal_portlet_CPContentPortlet\n',
	});

	return {...layout, privateLayout};
}

async function _addProductDisplayPageTemplate(
	apiHelpers,
	displayPageTemplatesPage,
	fragmentName: string,
	pageEditorPage,
	site
) {
	const className = await apiHelpers.jsonWebServicesClassName.fetchClassName(
		'com.liferay.commerce.product.model.CPDefinition'
	);

	const name = getRandomString();

	const displayPageTemplate =
		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
			{
				classNameId: className.classNameId,
				groupId: site.id,
				name,
			}
		);

	apiHelpers.data.push({
		id: displayPageTemplate.layoutPageTemplateEntryId,
		type: 'layoutPageTemplateEntry',
	});

	await displayPageTemplatesPage.goto(site.friendlyUrlPath);

	await displayPageTemplatesPage.editTemplate(name);

	await pageEditorPage.addFragment('Basic Components', fragmentName);

	await displayPageTemplatesPage.publishTemplate();

	return displayPageTemplate;
}

async function _assertProductDisplayPage(layout, page: Page, product, site) {
	await page.goto(
		`/${layout.privateLayout ? 'group' : 'web'}${site.friendlyUrlPath}/p/${product.urls['en_US']}`
	);

	await expect(page).toHaveTitle(new RegExp(layout.nameCurrentValue));
	await expect(
		page.getByText(product.name['en_US'], {exact: true}).first()
	).toBeVisible();
}
