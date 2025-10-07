/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../../utils/getRandomString';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	isolatedSiteTest,
	pageEditorPagesTest,
	loginTest()
);

test(
	'Product specification fragment only shows correct specifications',
	{tag: '@LPD-13652'},
	async ({
		apiHelpers,
		commerceLayoutsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: 'Specification Fragment Catalog',
			});

		const specification =
			await apiHelpers.headlessCommerceAdminCatalog.postSpecification(
				true,
				0,
				'Test Specification'
			);

		const product1 =
			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: 'Product1'},
				productSpecifications: [
					{
						specificationKey: specification.key,
						value: {
							en_US: 'Product1',
						},
					},
				],
			});
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: 'Product2'},
			productSpecifications: [
				{
					specificationKey: specification.key,
					value: {
						en_US: 'Product2',
					},
				},
				{
					label: {
						en_US: 'Product2 hidden label',
					},
					specificationKey: 'hidden-spec',
					value: {
						en_US: 'Product2 hidden',
					},
				},
			],
		});
		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: 'Product3'},
			productSpecifications: [
				{
					label: {
						en_US: 'Product2 hidden label',
					},
					specificationKey: 'invisible-spec',
					value: {
						en_US: 'Product3 invisible',
					},
					visible: false,
				},
			],
		});

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: 'Product',
			name: displayPageTemplateName,
		});
		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment('Product', 'Price');
		await pageEditorPage.addFragment('Product', 'Product Specification');

		await pageEditorPage.waitForChangesSaved();

		await page
			.getByText(
				'The Product Specification component will be shown here.'
			)
			.click();
		await page
			.getByLabel('Key', {exact: true})
			.fill(product1.productSpecifications[0].key);

		await commerceLayoutsPage.selectDisplayPageTemplatePreviewItem(
			'Product1'
		);

		await expect(
			page.getByText('Test Specification Product1')
		).toBeVisible();

		await commerceLayoutsPage.showLabelInput.uncheck();
		await commerceLayoutsPage.selectDisplayPageTemplatePreviewItem(
			'Product2'
		);

		await expect(page.getByText('Test Specification')).toBeHidden();

		await page.getByLabel('Key', {exact: true}).fill('hidden-spec');

		await expect(page.getByText('Test Specification')).toBeHidden();

		await commerceLayoutsPage.selectDisplayPageTemplatePreviewItem(
			'Product3',
			false
		);
	}
);
