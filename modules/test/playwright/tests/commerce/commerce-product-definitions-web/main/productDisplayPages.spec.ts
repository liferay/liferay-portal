/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest()
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
