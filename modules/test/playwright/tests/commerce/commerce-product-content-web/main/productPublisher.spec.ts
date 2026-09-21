/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../../../fixtures/pageViewModePagesTest';
import getRandomString from '../../../../utils/getRandomString';
import {templatesPageTest} from '../../../template-web/main/fixtures/templatesPageTest';
import {classicCommerceSetUp} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pageViewModePagesTest,
	templatesPageTest
);

test('LPD-30188 Product publisher tag filters can be added and removed', async ({
	apiHelpers,
	page,
	productPublisherPage,
	site,
	widgetPagePage,
}) => {
	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

	const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: getRandomString()},
		tags: ['tag1'],
	});
	const product2 = await apiHelpers.headlessCommerceAdminCatalog.postProduct({
		catalogId: catalog.id,
		name: {en_US: getRandomString()},
		tags: ['tag2'],
	});

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await widgetPagePage.addPortlet('Product Publisher');

	await expect(
		await productPublisherPage.productLink(product1.name.en_US)
	).toBeVisible();
	await expect(
		await productPublisherPage.productLink(product2.name.en_US)
	).toBeVisible();

	await productPublisherPage.addProductPublisherTagFilter('tag1');

	await page.goto(`/web/${site.name}`);

	await expect(
		await productPublisherPage.productLink(product1.name.en_US)
	).toBeVisible();
	await expect(
		await productPublisherPage.productLink(product2.name.en_US)
	).toHaveCount(0);

	await productPublisherPage.removeProductPublisherTagFilter('tag1');

	await page.goto(`/web/${site.name}`);

	await expect(
		await productPublisherPage.productLink(product1.name.en_US)
	).toBeVisible();
	await expect(
		await productPublisherPage.productLink(product2.name.en_US)
	).toBeVisible();
});

test(
	'Preview data source for product relations in page templates',
	{tag: ['@LPD-70139']},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		productPublisherPage,
		site,
		templatesPage,
	}) => {
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

		apiHelpers.headlessCommerceAdminCatalog.postProductRelatedProduct(
			product1.productId,
			{productId: product2.productId}
		);

		await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);

		const productPublisherWidgetTemplateName = getRandomString();

		await templatesPage.createWidgetTemplate(
			productPublisherWidgetTemplateName,
			'Product Publisher Template'
		);
		await templatesPage.editTemplate(productPublisherWidgetTemplateName);
		await templatesPage.importInformationTemplate(
			__dirname,
			'product_publisher_data_source_product_relations_template.ftl'
		);

		await templatesPage.saveTemplate(productPublisherWidgetTemplateName);

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: 'Product',
			name: displayPageTemplateName,
		});
		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addWidget('Commerce', 'Product Publisher');

		await pageEditorPage.getFragmentId('Product Publisher');

		await productPublisherPage.addProductPublisherProductSelectionDataSource(
			'Product Relations cross-sell',
			productPublisherWidgetTemplateName
		);

		await displayPageTemplatesPage.changePreviewItem(product1.name.en_US);

		await expect(page.getByText(product2.name.en_US)).toBeVisible();
	}
);

test(
	'Product Publisher CSS should persist through removal of widgets and fragments',
	{tag: ['@LPD-83580']},
	async ({apiHelpers, commerceLayoutsPage, page, pageEditorPage}) => {
		const {site} = await classicCommerceSetUp(apiHelpers);

		await page.goto(`/web/${site.name}`);

		await expect(
			commerceLayoutsPage.accountSelectorButton('Select Account & Order')
		).toBeVisible();

		await commerceLayoutsPage.editButton.click();

		await page.waitForLoadState('networkidle');

		const fragmentEntryLinkId =
			await pageEditorPage.getFragmentId('Price Range Facet');

		await pageEditorPage.removeFragment(fragmentEntryLinkId);

		await pageEditorPage.publishPage();

		await page.waitForLoadState('networkidle');

		const productTiles = page.locator('.product-card-tiles');

		await expect(productTiles).toHaveCSS('display', 'grid');
	}
);

test(
	'The Product Publisher configuration reveals the Product Entries panel as soon as the product source is manual',
	{tag: ['@COMMERCE-10454', '@LPD-106723']},
	async ({apiHelpers, page, productPublisherPage, site, widgetPagePage}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Product Publisher');

		await productPublisherPage.goToProductSelection();

		await expect(
			productPublisherPage.productSelectionRadio('Dynamic')
		).toBeChecked();
		await expect(productPublisherPage.productEntriesButton).toHaveCount(0);

		await productPublisherPage.productSelectionRadio('Manual').click();

		await expect(productPublisherPage.productEntriesButton).toBeVisible();
	}
);
