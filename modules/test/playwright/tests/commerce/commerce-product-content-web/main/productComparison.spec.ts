/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../../fixtures/pageViewModePagesTest';
import getRandomString from '../../../../utils/getRandomString';
import performLogin, {performLogout} from '../../../../utils/performLogin';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {templatesPageTest} from '../../../template-web/main/fixtures/templatesPageTest';

export const test = mergeTests(
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	templatesPageTest,
	pageViewModePagesTest
);

test(
	'CPContentHelper is available in product comparison widget templates',
	{tag: '@LPD-46669'},
	async ({apiHelpers, page, site, templatesPage}) => {
		await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);

		const productComparisonBarWidgetTemplateName = getRandomString();

		await templatesPage.createWidgetTemplate(
			productComparisonBarWidgetTemplateName,
			'Product Comparison Bar Template'
		);
		await templatesPage.editTemplate(
			productComparisonBarWidgetTemplateName
		);
		await templatesPage.importInformationTemplate(
			__dirname,
			'product_comparison_template.ftl'
		);

		const productComparisonBarWidgetTemplateKey =
			await templatesPage.getTemplateKey();

		await templatesPage.saveTemplate(
			productComparisonBarWidgetTemplateName
		);
		await templatesPage.gotoWidgetTemplates(site.friendlyUrlPath);

		const productComparisonTableWidgetTemplateName = getRandomString();

		await templatesPage.createWidgetTemplate(
			productComparisonTableWidgetTemplateName,
			'Product Comparison Table Template'
		);
		await templatesPage.editTemplate(
			productComparisonTableWidgetTemplateName
		);
		await templatesPage.importInformationTemplate(
			__dirname,
			'product_comparison_template.ftl'
		);

		const productComparisonTableWidgetTemplateKey =
			await templatesPage.getTemplateKey();

		await templatesPage.saveTemplate(
			productComparisonTableWidgetTemplateName
		);

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {
						displayStyle:
							'ddmTemplate_' +
							productComparisonBarWidgetTemplateKey,
						displayStyleGroupId: String(site.id),
						selectionStyle: 'adt',
					},
					widgetName:
						'com_liferay_commerce_product_content_web_internal_portlet_CPCompareContentMiniPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetConfig: {
						displayStyle:
							'ddmTemplate_' +
							productComparisonTableWidgetTemplateKey,
						displayStyleGroupId: String(site.id),
						selectionStyle: 'adt',
					},
					widgetName:
						'com_liferay_commerce_product_content_web_internal_portlet_CPCompareContentPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(page.getByText('TEST', {exact: true})).toHaveCount(2);
	}
);

test(
	'Product Compare is removed upon logout and login',
	{tag: ['@LPD-37427', '@LPD-60912']},
	async ({apiHelpers, applicationsMenuPage, page, productComparisonPage}) => {
		const site = await apiHelpers.headlessSite.createSite({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: site.id, type: 'site'});

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {en_US: 'Product'},
		});

		await applicationsMenuPage.goToSite(site.name);

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_web_internal_portlet_CPCompareContentMiniPortlet',
				}),
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_commerce_product_content_web_internal_portlet_CPPublisherPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await applicationsMenuPage.goToSite(site.name);

		await page.getByRole('checkbox', {disabled: false}).first().click();

		await expect(productComparisonPage.compareBar).toHaveCount(1);

		await page.reload();

		await expect(productComparisonPage.compareBar).toHaveCount(1);

		await performLogout(page);
		await performLogin(page, 'test');

		await applicationsMenuPage.goToSite(site.name);

		await expect(productComparisonPage.compareBar).toHaveCount(0);
	}
);
