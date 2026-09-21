/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {
	performLoginViaApi,
	performUserSwitch,
} from '../../../../utils/performLogin';
import {
	createAccountWithBuyerUser,
	deployProductFragmentsOnDefaultDPT,
	miniumSetUp,
} from '../../utils/commerce';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	loginTest(),
	pageEditorPagesTest
);

let buyerUser;
let catalog: {id: number};
let setupData: Array<{id: number | string; type: string}>;
let site;

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	({catalog, site} = await miniumSetUp(apiHelpers));

	({buyerUser} = await createAccountWithBuyerUser(apiHelpers, site.id, {
		accountName: `Commerce Account ${site.name}`,
	}));

	for (const [index, skuName] of [
		'MIN55861',
		'MIN93022A',
		'MIN93022C',
	].entries()) {
		const product =
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				skuName === 'MIN55861' ? 'U-Joint' : 'Hoses',
				{catalogId: catalog.id, nestedFields: 'skus'}
			);

		await apiHelpers.headlessCommerceAdminCatalog.patchSku(
			String(
				product.skus.find((sku: {sku: string}) => sku.sku === skuName)
					.id
			),
			{
				gtin: `GTIN000${index + 1}`,
				manufacturerPartNumber: `MPN000${index + 1}`,
				sku: skuName,
			}
		);
	}

	setupData = [...apiHelpers.data];

	await page.close();
});

test.afterAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	apiHelpers.setData(setupData);

	await apiHelpers.clearData();

	await page.close();
});

async function deployDynamicFieldFragments(
	apiHelpers,
	displayPageTemplatesPage,
	pageEditorPage,
	elementTypes?: Array<[string, string]>
) {
	await deployProductFragmentsOnDefaultDPT(apiHelpers, {
		displayPageTemplatesPage,
		fragmentNames: [],
		onFragmentsAdded: async () => {
			for (const [index, field] of [
				'Inventory',
				'SKU',
				'GTIN',
				'Manufacturer Part Number',
			].entries()) {
				await pageEditorPage.addFragment('Product', 'Dynamic Field');

				for (const [fieldLabel, value] of [
					['Field', field],
					['Label', field],
					...(elementTypes
						? [
								['Label Element Type', elementTypes[index][0]],
								['Value Element Type', elementTypes[index][1]],
							]
						: []),
				] as Array<[string, string]>) {
					await pageEditorPage.changeConfiguration({
						fieldLabel,
						tab: 'General',
						value,
					});
				}
			}

			await pageEditorPage.addFragment('Product', 'Option Selector');
		},
		pageEditorPage,
		site,
	});
}

test(
	'The Dynamic Field fragments render the mapped product fields and follow the resolved SKU',
	{tag: ['@COMMERCE-11074', '@LPD-106767']},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		productDetailsPage,
	}) => {
		test.setTimeout(300000);

		await deployDynamicFieldFragments(
			apiHelpers,
			displayPageTemplatesPage,
			pageEditorPage
		);

		const productURLs = [];

		for (const [productName, values] of [
			['U-Joint', ['120', 'MIN55861', 'GTIN0001', 'MPN0001']],
			['Hoses', ['120', 'MIN93022A', 'GTIN0002', 'MPN0002']],
		] as Array<[string, string[]]>) {
			const product =
				await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
					productName,
					{catalogId: catalog.id}
				);

			productURLs.push([product.urls['en_US'], values]);
		}

		await performUserSwitch(page, buyerUser.alternateName);

		for (const [productURL, values] of productURLs as Array<
			[string, string[]]
		>) {
			await page.goto(`/web${site.friendlyUrlPath}/p/${productURL}`, {
				waitUntil: 'networkidle',
			});

			await expect(productDetailsPage.dynamicFieldLabels).toHaveText([
				'Inventory:',
				'SKU:',
				'GTIN:',
				'Manufacturer Part Number:',
			]);
			await expect(productDetailsPage.dynamicFieldValues).toHaveText(
				values
			);
		}

		await test.step('Changing the option resolves a different SKU and every field follows', async () => {
			await productDetailsPage.selectOption('48', 'Package Quantity');

			await expect(productDetailsPage.dynamicFieldValues).toHaveText([
				'240',
				'MIN93022C',
				'GTIN0003',
				'MPN0003',
			]);
		});
	}
);

test(
	'The Dynamic Field fragments render their label and value in the configured HTML elements',
	{tag: ['@COMMERCE-11075', '@LPD-106767']},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		productDetailsPage,
	}) => {
		test.setTimeout(300000);

		await deployDynamicFieldFragments(
			apiHelpers,
			displayPageTemplatesPage,
			pageEditorPage,
			[
				['div', 'H1'],
				['H2', 'H3'],
				['H4', 'H5'],
				['H6', 'p'],
			]
		);

		const uJoint =
			await apiHelpers.headlessCommerceAdminCatalog.getProductByName(
				'U-Joint',
				{catalogId: catalog.id}
			);

		await performUserSwitch(page, buyerUser.alternateName);

		await page.goto(
			`/web${site.friendlyUrlPath}/p/${uJoint.urls['en_US']}`,
			{waitUntil: 'networkidle'}
		);

		await expect(productDetailsPage.dynamicFieldValues).toHaveText([
			'120',
			'MIN55861',
			'GTIN0001',
			'MPN0001',
		]);

		expect(
			await productDetailsPage.dynamicFieldLabels.evaluateAll(
				(elements) =>
					elements.map((element) => element.tagName.toLowerCase())
			)
		).toEqual(['div', 'h2', 'h4', 'h6']);
		expect(
			await productDetailsPage.dynamicFieldValues.evaluateAll(
				(elements) =>
					elements.map((element) => element.tagName.toLowerCase())
			)
		).toEqual(['h1', 'h3', 'h5', 'p']);
	}
);
