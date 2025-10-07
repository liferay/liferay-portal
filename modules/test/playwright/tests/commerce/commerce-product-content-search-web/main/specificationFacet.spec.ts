/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../../fixtures/apiHelpersTest';
import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../../fixtures/pageViewModePagesTest';
import getRandomString from '../../../../utils/getRandomString';

export const test = mergeTests(
	apiHelpersTest,
	commercePagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	pageViewModePagesTest
);

test(
	'Can sort specifications by specification group and label priority',
	{tag: '@LPD-13560'},
	async ({apiHelpers, page, site, specificationFacetsPage}) => {
		test.setTimeout(180000);

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await specificationFacetsPage.addRequiredFacetWidgets();
		await specificationFacetsPage.configureSearchOptions();

		const optionCategory1 =
			await apiHelpers.headlessCommerceAdminCatalog.postOptionCategory(
				'Warranty',
				1
			);

		const optionCategory2 =
			await apiHelpers.headlessCommerceAdminCatalog.postOptionCategory(
				'Material',
				0
			);

		const specification1 =
			await apiHelpers.headlessCommerceAdminCatalog.postSpecification(
				true,
				optionCategory2.priority,
				'Warranty1',
				{
					id: optionCategory1.id,
					key: optionCategory1.key,
					priority: optionCategory1.priority,
					title: {
						en_US: optionCategory1.key,
					},
				}
			);

		const specification2 =
			await apiHelpers.headlessCommerceAdminCatalog.postSpecification(
				true,
				optionCategory1.priority,
				'Material1',
				{
					id: optionCategory2.id,
					key: optionCategory2.key,
					priority: optionCategory2.priority,
					title: {
						en_US: optionCategory2.key,
					},
				}
			);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {
				en_US: 'Product1',
			},
			productSpecifications: [
				{
					specificationKey: specification1.key,
					value: {
						en_US: 'Product1',
					},
				},
			],
		});

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {
				en_US: 'Product2',
			},
			productSpecifications: [
				{
					specificationKey: specification2.key,
					value: {
						en_US: 'Product2',
					},
				},
			],
		});

		await specificationFacetsPage.reloadPage();

		const panelList = await specificationFacetsPage.panelList.all();

		const specificationFacetsList = ['Material1', 'Warranty1'];

		for (let i = 0; i < specificationFacetsList.length; i++) {
			await expect(panelList[i]).toHaveText(specificationFacetsList[i]);
		}

		await specificationFacetsPage.configureSpecificationFacetOrdering(
			'label-priority:asc'
		);

		await specificationFacetsPage.reloadPage();

		const reverseSpecificationFacetsList =
			specificationFacetsList.reverse();

		for (let i = 0; i < reverseSpecificationFacetsList.length; i++) {
			await expect(panelList[i]).toHaveText(
				reverseSpecificationFacetsList[i]
			);
		}
	}
);

test(
	'Option and Specification facet portlets behave the same way',
	{tag: '@LPD-20340'},
	async ({apiHelpers, page, site, specificationFacetsPage}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		const optionCategory1 =
			await apiHelpers.headlessCommerceAdminCatalog.postOptionCategory(
				'Warranty',
				1
			);

		const optionCategory2 =
			await apiHelpers.headlessCommerceAdminCatalog.postOptionCategory(
				'Material',
				0
			);

		const specification1 =
			await apiHelpers.headlessCommerceAdminCatalog.postSpecification(
				true,
				optionCategory2.priority,
				'Warranty1',
				{
					id: optionCategory1.id,
					key: optionCategory1.key,
					priority: optionCategory1.priority,
					title: {
						en_US: optionCategory1.key,
					},
				}
			);

		const specification2 =
			await apiHelpers.headlessCommerceAdminCatalog.postSpecification(
				true,
				optionCategory1.priority,
				'Material1',
				{
					id: optionCategory2.id,
					key: optionCategory2.key,
					priority: optionCategory2.priority,
					title: {
						en_US: optionCategory2.key,
					},
				}
			);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {
				en_US: 'Product1',
			},
			productSpecifications: [
				{
					specificationKey: specification1.key,
					value: {
						en_US: 'Product1',
					},
				},
			],
		});

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {
				en_US: 'Product2',
			},
			productSpecifications: [
				{
					specificationKey: specification2.key,
					value: {
						en_US: 'Product2',
					},
				},
			],
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await specificationFacetsPage.addRequiredFacetWidgets();
		await specificationFacetsPage.configureSearchOptions();

		await expect(
			specificationFacetsPage.searchOptionsConfigurationSaveButton
		).toBeEnabled();

		await page.reload();

		const panelList = await specificationFacetsPage.panelList.all();

		const specificationFacetsList = ['Material1', 'Warranty1'];

		for (let i = 0; i < specificationFacetsList.length; i++) {
			await expect(panelList[i]).toHaveText(specificationFacetsList[i]);
		}

		await specificationFacetsPage.configureOptionFacetFrequencyThreshold(
			'60'
		);

		await expect(
			specificationFacetsPage.configurationSaveButton
		).toBeEnabled();

		await page.reload();

		await specificationFacetsPage.configureSpecificationFacetFrequencyThreshold(
			'60'
		);

		await expect(
			specificationFacetsPage.configurationSaveButton
		).toBeEnabled();

		await page.reload();

		await expect(page.getByText('No facets were found.')).toHaveCount(2);

		await specificationFacetsPage.configureSpecificationFacetFrequencyThreshold(
			'2'
		);

		await expect(
			specificationFacetsPage.configurationSaveButton
		).toBeEnabled();

		await page.reload();

		await expect(page.getByText('No facets were found.')).toHaveCount(2);
	}
);

test(
	'Only visible specifications are listed in facet widget',
	{tag: '@LPD-48103'},
	async ({apiHelpers, page, site, specificationFacetsPage}) => {
		test.setTimeout(180000);

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await apiHelpers.headlessCommerceAdminChannel.postChannel({
			siteGroupId: site.id,
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await specificationFacetsPage.addRequiredFacetWidgets();
		await specificationFacetsPage.configureSearchOptions();

		const optionCategory1 =
			await apiHelpers.headlessCommerceAdminCatalog.postOptionCategory(
				'Warranty',
				1
			);

		const optionCategory2 =
			await apiHelpers.headlessCommerceAdminCatalog.postOptionCategory(
				'Material',
				0
			);

		const specification1 =
			await apiHelpers.headlessCommerceAdminCatalog.postSpecification(
				true,
				optionCategory2.priority,
				'Warranty1',
				{
					id: optionCategory1.id,
					key: optionCategory1.key,
					priority: optionCategory1.priority,
					title: {
						en_US: optionCategory1.key,
					},
				}
			);

		const specification2 =
			await apiHelpers.headlessCommerceAdminCatalog.postSpecification(
				true,
				optionCategory1.priority,
				'Material1',
				{
					id: optionCategory2.id,
					key: optionCategory2.key,
					priority: optionCategory2.priority,
					title: {
						en_US: optionCategory2.key,
					},
				}
			);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
				name: getRandomString(),
			});

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {
				en_US: 'Product1',
			},
			productSpecifications: [
				{
					specificationKey: specification1.key,
					value: {
						en_US: 'Product1',
					},
				},
			],
		});

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			name: {
				en_US: 'Product2',
			},
			productSpecifications: [
				{
					specificationKey: specification2.key,
					value: {
						en_US: 'Product2',
					},
					visible: false,
				},
			],
		});

		await specificationFacetsPage.reloadPage();

		const panelList = await specificationFacetsPage.panelList.all();

		await expect(panelList[0]).toHaveText('Warranty1');
	}
);
