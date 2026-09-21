/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {
	FacetWidget,
	SpecificationFacetsPage,
} from '../../../../pages/commerce/commerce-product-content-search-web/specificationFacetsPage';
import getRandomString from '../../../../utils/getRandomString';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest()
);

type FacetPlan = {
	name: string;
	values: string[][];
};

type SeedFacets = (
	apiHelpers: DataApiHelpers,
	catalogId: number,
	facetPlans: FacetPlan[]
) => Promise<void>;

async function seedOptionFacets(
	apiHelpers: DataApiHelpers,
	catalogId: number,
	facetPlans: FacetPlan[]
) {
	const options = [];

	for (const [index, facetPlan] of facetPlans.entries()) {
		options.push(
			await apiHelpers.headlessCommerceAdminCatalog.postOption(
				'select',
				facetPlan.name.toLowerCase(),
				facetPlan.name,
				index,
				true
			)
		);
	}

	for (const productValues of toProductValues(facetPlans)) {
		const productOptions = [];

		productValues.forEach((values, facetIndex) => {
			if (!values.length) {
				return;
			}

			productOptions.push({
				facetable: true,
				fieldType: 'select',
				key: options[facetIndex].key,
				name: {en_US: facetPlans[facetIndex].name},
				optionId: options[facetIndex].id,
				priceType: 'static',
				priority: facetIndex,
				productOptionValues: values.map((value, valueIndex) => ({
					key: `value-${valueIndex}`,
					name: {en_US: value},
					priority: valueIndex,
				})),
			});
		});

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId,
			name: {en_US: getRandomString()},
			productOptions,
		});
	}
}

async function seedSpecificationFacets(
	apiHelpers: DataApiHelpers,
	catalogId: number,
	facetPlans: FacetPlan[]
) {
	const specifications = [];

	for (const [index, facetPlan] of facetPlans.entries()) {
		specifications.push(
			await apiHelpers.headlessCommerceAdminCatalog.postSpecification(
				true,
				index,
				facetPlan.name
			)
		);
	}

	for (const productValues of toProductValues(facetPlans)) {
		const productSpecifications = [];

		productValues.forEach((values, facetIndex) => {
			for (const value of values) {
				productSpecifications.push({
					specificationKey: specifications[facetIndex].key,
					value: {en_US: value},
				});
			}
		});

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId,
			name: {en_US: getRandomString()},
			productSpecifications,
		});
	}
}

function toProductValues(facetPlans: FacetPlan[]): string[][][] {
	const productCount = Math.max(
		...facetPlans.map((facetPlan) => facetPlan.values.length)
	);

	return Array.from({length: productCount}, (_, productIndex) =>
		facetPlans.map((facetPlan) => facetPlan.values[productIndex] ?? [])
	);
}

async function setUpFacetPage(
	apiHelpers: DataApiHelpers,
	page: Page,
	specificationFacetsPage: SpecificationFacetsPage,
	site: Site
) {
	await apiHelpers.headlessCommerceAdminChannel.postChannel({
		siteGroupId: site.id,
	});

	const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog({
		name: getRandomString(),
	});

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		title: getRandomString(),
	});

	const url = `/web${site.friendlyUrlPath}${layout.friendlyURL}`;

	await page.goto(url);

	await specificationFacetsPage.addRequiredFacetWidgets();

	await specificationFacetsPage.configureSearchBar();

	await specificationFacetsPage.configureSearchOptions();

	return {catalogId: catalog.id, url};
}

async function goToIndexedFacetPage(
	page: Page,
	specificationFacetsPage: SpecificationFacetsPage,
	url: string,
	widget: FacetWidget,
	facetName: string
) {
	await page.goto(url);

	await expect(async () => {
		await page.reload();

		await expect(
			specificationFacetsPage.facetPanel(widget, facetName)
		).toBeVisible({timeout: 5000});
	}).toPass({timeout: 60000});
}

const FACET_WIDGETS: Array<{
	displayTemplateTerms: string[];
	seedFacets: SeedFacets;
	tickets: Record<string, string>;
	widget: FacetWidget;
}> = [
	{
		displayTemplateTerms: ['6', '12', '112'],
		seedFacets: seedOptionFacets,
		tickets: {
			displayFrequencies: '@COMMERCE-8646',
			displayTemplate: '@COMMERCE-8646',
			frequencyThreshold: '@COMMERCE-8646',
			maxEntities: '@COMMERCE-12895',
			maxTerms: '@COMMERCE-12895',
			maxTermsValidation: '@COMMERCE-8646',
			setupTab: '@COMMERCE-8646',
		},
		widget: 'Option Facet',
	},
	{
		displayTemplateTerms: ['Cast Iron', 'Neoprene', 'Stainless Steel'],
		seedFacets: seedSpecificationFacets,
		tickets: {
			displayFrequencies: '@COMMERCE-8403',
			displayTemplate: '@COMMERCE-8401',
			frequencyThreshold: '@COMMERCE-8401',
			maxEntities: '@COMMERCE-12895',
			maxTerms: '@COMMERCE-12895',
			maxTermsValidation: '@COMMERCE-8399',
			setupTab: '@COMMERCE-8384',
		},
		widget: 'Specification Facet',
	},
];

for (const {
	displayTemplateTerms,
	seedFacets,
	tickets,
	widget,
} of FACET_WIDGETS) {
	const maxEntitiesField =
		widget === 'Option Facet' ? 'Max Options' : 'Max Specifications';

	const facetPrefix = widget === 'Option Facet' ? 'OptionFacet' : 'SpecFacet';

	const facetNameAt = (index: number) =>
		`${facetPrefix}${String(index).padStart(2, '0')}`;

	test(
		`${widget} - ${maxEntitiesField} limits how many facets are displayed`,
		{tag: [tickets.maxEntities, '@LPD-105602']},
		async ({apiHelpers, page, site, specificationFacetsPage}) => {
			const facetNames = Array.from({length: 12}, (_, index) =>
				facetNameAt(index + 1)
			);

			const facetPlans = facetNames.map((name, index) =>
				index === 0
					? {name, values: [['Alpha'], ['Beta'], ['Gamma']]}
					: {name, values: [[`Value${index}`]]}
			);

			const {catalogId, url} = await setUpFacetPage(
				apiHelpers,
				page,
				specificationFacetsPage,
				site
			);

			await seedFacets(apiHelpers, catalogId, facetPlans);

			await goToIndexedFacetPage(
				page,
				specificationFacetsPage,
				url,
				widget,
				facetNames[0]
			);

			await test.step('All twelve facets are displayed when the limit is above their count', async () => {
				await specificationFacetsPage.updateFacetConfiguration(widget, {
					maxEntities: 15,
				});

				await specificationFacetsPage.closeFacetConfiguration();

				for (const facetName of facetNames) {
					await expect(
						specificationFacetsPage.facetPanel(widget, facetName)
					).toBeVisible();
				}
			});

			await test.step('Lowering the limit to one leaves only the most frequent facet', async () => {
				await specificationFacetsPage.updateFacetConfiguration(widget, {
					maxEntities: 1,
				});

				await specificationFacetsPage.closeFacetConfiguration();

				await expect(
					specificationFacetsPage.facetPanel(widget, facetNames[0])
				).toBeVisible();

				for (const facetName of facetNames.slice(1)) {
					await expect(
						specificationFacetsPage.facetPanel(widget, facetName)
					).toBeHidden();
				}
			});

			await test.step('The terms of the remaining facet are untouched', async () => {
				await expect(
					specificationFacetsPage.facetTerms(widget, facetNames[0])
				).toHaveCount(3);
			});
		}
	);

	test(
		`${widget} - Max Terms limits how many terms are displayed for each facet`,
		{tag: [tickets.maxTerms, '@LPD-105602']},
		async ({apiHelpers, page, site, specificationFacetsPage}) => {
			const [facetA, facetB] = [facetNameAt(1), facetNameAt(2)];

			const facetPlans = [
				{
					name: facetA,
					values: [
						['Shared'],
						['Shared'],
						['Shared'],
						['Rare1'],
						['Rare2'],
					],
				},
				{
					name: facetB,
					values: Array.from({length: 7}, (_, index) => [
						`Single${index + 1}`,
					]),
				},
			];

			const {catalogId, url} = await setUpFacetPage(
				apiHelpers,
				page,
				specificationFacetsPage,
				site
			);

			await seedFacets(apiHelpers, catalogId, facetPlans);

			await goToIndexedFacetPage(
				page,
				specificationFacetsPage,
				url,
				widget,
				facetA
			);

			await test.step('Every term is displayed when the limit is above their count', async () => {
				await specificationFacetsPage.updateFacetConfiguration(widget, {
					maxTerms: 15,
				});

				await specificationFacetsPage.closeFacetConfiguration();

				await expect(
					specificationFacetsPage.facetTerms(widget, facetA)
				).toHaveCount(3);
				await expect(
					specificationFacetsPage.facetTerms(widget, facetB)
				).toHaveCount(7);
			});

			await test.step('Lowering the limit truncates the terms without dropping the facets', async () => {
				for (const maxTerms of [1, 2]) {
					await specificationFacetsPage.updateFacetConfiguration(
						widget,
						{maxTerms}
					);

					await specificationFacetsPage.closeFacetConfiguration();

					await expect(
						specificationFacetsPage.facetTerms(widget, facetA)
					).toHaveCount(maxTerms);
					await expect(
						specificationFacetsPage.facetTerms(widget, facetB)
					).toHaveCount(maxTerms);

					for (const facetName of [facetA, facetB]) {
						await expect(
							specificationFacetsPage.facetPanel(
								widget,
								facetName
							)
						).toBeVisible();
					}
				}
			});

			await test.step('The frequency threshold drops the facet whose terms are all below it', async () => {
				await specificationFacetsPage.updateFacetConfiguration(widget, {
					frequencyThreshold: 3,
				});

				await specificationFacetsPage.closeFacetConfiguration();

				await expect(
					specificationFacetsPage.facetPanel(widget, facetA)
				).toBeVisible();
				await expect(
					specificationFacetsPage.facetPanel(widget, facetB)
				).toBeHidden();
				await expect(
					specificationFacetsPage.facetTerms(widget, facetA)
				).toHaveCount(1);
				await expect(
					specificationFacetsPage.facetTerms(widget, facetA)
				).toContainText(['Shared']);
			});
		}
	);

	test(
		`${widget} - Display Frequencies hides the term frequencies`,
		{tag: [tickets.displayFrequencies, '@LPD-105602']},
		async ({apiHelpers, page, site, specificationFacetsPage}) => {
			const facetName = facetNameAt(1);

			const facetPlans = [
				{
					name: facetName,
					values: [['Shared'], ['Shared'], ['Single']],
				},
			];

			const {catalogId, url} = await setUpFacetPage(
				apiHelpers,
				page,
				specificationFacetsPage,
				site
			);

			await seedFacets(apiHelpers, catalogId, facetPlans);

			await goToIndexedFacetPage(
				page,
				specificationFacetsPage,
				url,
				widget,
				facetName
			);

			await test.step('Frequencies are displayed by default', async () => {
				await expect(
					specificationFacetsPage.facetTermFrequencies(widget)
				).toHaveCount(2);
				await expect(
					specificationFacetsPage.facetTermFrequencies(widget).first()
				).toHaveText('(2)');
			});

			await test.step('Disabling them keeps the terms but drops the counts', async () => {
				await specificationFacetsPage.updateFacetConfiguration(widget, {
					displayFrequencies: false,
				});

				await specificationFacetsPage.closeFacetConfiguration();

				await expect(
					specificationFacetsPage.facetTerms(widget, facetName)
				).toHaveCount(2);
				await expect(
					specificationFacetsPage.facetTermFrequencies(widget)
				).toHaveCount(0);
			});
		}
	);

	test(
		`${widget} - Display Template renders the terms in the selected layout`,
		{tag: [tickets.displayTemplate, '@LPD-105602']},
		async ({apiHelpers, page, site, specificationFacetsPage}) => {
			const facetName = facetNameAt(1);

			const facetPlans = [
				{
					name: facetName,
					values: displayTemplateTerms.map((term) => [term]),
				},
			];

			const {catalogId, url} = await setUpFacetPage(
				apiHelpers,
				page,
				specificationFacetsPage,
				site
			);

			await seedFacets(apiHelpers, catalogId, facetPlans);

			await goToIndexedFacetPage(
				page,
				specificationFacetsPage,
				url,
				widget,
				facetName
			);

			for (const displayTemplate of [
				'Cloud Layout',
				'Compact Layout',
				'Label Layout',
			]) {
				await specificationFacetsPage.updateFacetConfiguration(widget, {
					displayTemplate,
				});

				await specificationFacetsPage.closeFacetConfiguration();

				for (const term of displayTemplateTerms) {
					await expect(
						specificationFacetsPage.facetTermsByDisplayTemplate(
							widget,
							displayTemplate,
							term
						)
					).toBeVisible();
				}
			}
		}
	);

	test(
		`${widget} - Frequency Threshold hides the terms below the threshold`,
		{tag: [tickets.frequencyThreshold, '@LPD-105602']},
		async ({apiHelpers, page, site, specificationFacetsPage}) => {
			const facetName = facetNameAt(1);

			const facetPlans = [
				{
					name: facetName,
					values: [
						['Shared'],
						['Shared'],
						['Shared'],
						['Rare1'],
						['Rare2'],
					],
				},
			];

			const {catalogId, url} = await setUpFacetPage(
				apiHelpers,
				page,
				specificationFacetsPage,
				site
			);

			await seedFacets(apiHelpers, catalogId, facetPlans);

			await goToIndexedFacetPage(
				page,
				specificationFacetsPage,
				url,
				widget,
				facetName
			);

			await expect(
				specificationFacetsPage.facetTerms(widget, facetName)
			).toHaveCount(3);

			await specificationFacetsPage.updateFacetConfiguration(widget, {
				frequencyThreshold: 3,
			});

			await specificationFacetsPage.closeFacetConfiguration();

			await expect(
				specificationFacetsPage.facetTerms(widget, facetName)
			).toHaveCount(1);
			await expect(
				specificationFacetsPage.facetTerms(widget, facetName)
			).toContainText(['Shared']);
		}
	);

	test(
		`${widget} - Max Terms cannot exceed 100`,
		{tag: [tickets.maxTermsValidation, '@LPD-105602']},
		async ({apiHelpers, page, site, specificationFacetsPage}) => {
			const facetName = facetNameAt(1);

			const {catalogId, url} = await setUpFacetPage(
				apiHelpers,
				page,
				specificationFacetsPage,
				site
			);

			await seedFacets(apiHelpers, catalogId, [
				{name: facetName, values: [['Single']]},
			]);

			await goToIndexedFacetPage(
				page,
				specificationFacetsPage,
				url,
				widget,
				facetName
			);

			await specificationFacetsPage.updateFacetConfiguration(widget, {
				maxTerms: 101,
			});

			await expect(
				specificationFacetsPage.configurationErrorMessage(
					'Maximum terms cannot exceed 100.'
				)
			).toBeVisible();
		}
	);

	test(
		`${widget} - Configuration exposes the facet display settings`,
		{tag: [tickets.setupTab, '@LPD-105602']},
		async ({apiHelpers, page, site, specificationFacetsPage}) => {
			const facetName = facetNameAt(1);

			const {catalogId, url} = await setUpFacetPage(
				apiHelpers,
				page,
				specificationFacetsPage,
				site
			);

			await seedFacets(apiHelpers, catalogId, [
				{name: facetName, values: [['Single']]},
			]);

			await goToIndexedFacetPage(
				page,
				specificationFacetsPage,
				url,
				widget,
				facetName
			);

			await specificationFacetsPage.openFacetConfiguration(widget);

			await expect(
				specificationFacetsPage.displayTemplateSelect
			).toBeVisible();
			await expect(
				specificationFacetsPage.maxEntitiesInput(widget)
			).toBeVisible();
			await expect(specificationFacetsPage.maxTermsInput).toBeVisible();
			await expect(
				specificationFacetsPage.frequencyThresholdInput
			).toBeVisible();
			await expect(
				specificationFacetsPage.displayFrequenciesCheckbox
			).toBeVisible();
		}
	);

	test(
		`${widget} - Max Terms displays up to 100 terms`,
		{tag: [tickets.maxTerms, '@LPD-105602']},
		async ({apiHelpers, page, site, specificationFacetsPage}) => {
			const facetName = facetNameAt(1);

			const facetPlans = [
				{
					name: facetName,
					values: [
						Array.from(
							{length: 100},
							(_, index) =>
								`Term${String(index + 1).padStart(3, '0')}`
						),
					],
				},
			];

			const {catalogId, url} = await setUpFacetPage(
				apiHelpers,
				page,
				specificationFacetsPage,
				site
			);

			await seedFacets(apiHelpers, catalogId, facetPlans);

			await goToIndexedFacetPage(
				page,
				specificationFacetsPage,
				url,
				widget,
				facetName
			);

			await expect(
				specificationFacetsPage.facetTerms(widget, facetName)
			).toHaveCount(10);

			await specificationFacetsPage.updateFacetConfiguration(widget, {
				maxTerms: 100,
			});

			await specificationFacetsPage.closeFacetConfiguration();

			await expect(
				specificationFacetsPage.facetTerms(widget, facetName)
			).toHaveCount(100);
		}
	);
}
