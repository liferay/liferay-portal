/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {createCategories} from '../../../helpers/CreateCategories';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {assetCategoriesPagesTest} from './fixtures/assetCategoriesAdminPagesTest';

const test = mergeTests(
	apiHelpersTest,
	assetCategoriesPagesTest,
	isolatedSiteTest,
	loginTest()
);

const assetTypes = [
	'All Asset Types',
	'Blogs Entry',
	'Document',
	'User',
	'Web Content Article',
];

test(
	'Edit external reference code of an existing vocabulary',
	{tag: '@LPD-90008'},
	async ({
		apiHelpers,
		assetCategoriesAdminPage,
		page,
		site,
		vocabulariesEditPage,
	}) => {
		const updatedExternalReferenceCode = getRandomString();
		const vocabularyName = getRandomString();

		const [vocabulary1, vocabulary2] = await Promise.all([
			apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
				name: vocabularyName,
				siteId: site.id,
			}),
			apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
				name: getRandomString(),
				siteId: site.id,
			}),
		]);

		await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

		await test.step('Edit the external reference code', async () => {
			await vocabulariesEditPage.goto(vocabularyName);

			await expect(
				vocabulariesEditPage.externalReferenceCodeInput
			).toBeEditable();

			await expect(
				vocabulariesEditPage.externalReferenceCodeInput
			).toHaveValue(vocabulary1.externalReferenceCode);

			await vocabulariesEditPage.fillExternalReferenceCode(
				updatedExternalReferenceCode
			);

			await vocabulariesEditPage.saveButton.click();

			await waitForAlert(page);

			await vocabulariesEditPage.goto(vocabularyName);

			await expect(
				vocabulariesEditPage.externalReferenceCodeInput
			).toHaveValue(updatedExternalReferenceCode);
		});

		await test.step('Reject duplicate external reference code on edit', async () => {
			await vocabulariesEditPage.fillExternalReferenceCode(
				vocabulary2.externalReferenceCode
			);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: vocabulariesEditPage.saveButton,
				trigger: page.getByText(
					'Please enter a unique external reference code.',
					{exact: true}
				),
			});
		});
	}
);

test(
	'Edit external reference code of an existing category',
	{tag: '@LPD-90008'},
	async ({
		apiHelpers,
		assetCategoriesAdminPage,
		assetCategoriesEditPage,
		page,
		site,
	}) => {
		const categoryName = getRandomString();
		const updatedExternalReferenceCode = getRandomString();
		const vocabularyName = getRandomString();

		const [category1, category2] = await createCategories({
			apiHelpers,
			categoryNames: [{name: categoryName}, {name: getRandomString()}],
			siteId: site.id,
			vocabularyName,
		});

		await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

		await test.step('Edit the external reference code', async () => {
			await assetCategoriesEditPage.goto(categoryName);

			await expect(
				assetCategoriesEditPage.externalReferenceCodeInput
			).toBeEditable();

			await expect(
				assetCategoriesEditPage.externalReferenceCodeInput
			).toHaveValue(category1.externalReferenceCode);

			await assetCategoriesEditPage.fillExternalReferenceCode(
				updatedExternalReferenceCode
			);

			await assetCategoriesEditPage.save(`Success:${categoryName}`);

			await assetCategoriesEditPage.goto(categoryName);

			await expect(
				assetCategoriesEditPage.externalReferenceCodeInput
			).toHaveValue(updatedExternalReferenceCode);
		});

		await test.step('Reject duplicate external reference code on edit', async () => {
			await assetCategoriesEditPage.fillExternalReferenceCode(
				category2.externalReferenceCode
			);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: assetCategoriesEditPage.saveButton,
				trigger: page.getByText(
					'Please enter a unique external reference code.',
					{exact: true}
				),
			});
		});
	}
);

test('Add, edit and delete a vocabulary', async ({
	assetCategoriesAdminPage,
	page,
	site,
	vocabulariesEditPage,
}) => {
	await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

	const vocabularyDescription = getRandomString();
	const vocabularyExternalReferenceCode = getRandomString();
	const vocabularyName = getRandomString();

	await test.step('Add a vocabulary with description', async () => {
		await assetCategoriesAdminPage.newVocabularyButton.click();

		await vocabulariesEditPage.add({
			description: vocabularyDescription,
			externalReferenceCode: vocabularyExternalReferenceCode,
			name: vocabularyName,
		});

		await expect(
			page.getByRole('heading', {name: vocabularyName})
		).toBeVisible();

		await expect(page.getByText(vocabularyDescription)).toBeVisible();
	});

	const newVocabularyName = getRandomString();

	await test.step('Edit the vocabulary', async () => {
		await vocabulariesEditPage.goto(vocabularyName);

		await vocabulariesEditPage.fillName(newVocabularyName);

		await expect(
			vocabulariesEditPage.externalReferenceCodeInput
		).toHaveValue(vocabularyExternalReferenceCode);

		await vocabulariesEditPage.saveButton.click();

		await waitForAlert(page);

		await expect(
			page.getByRole('heading', {name: newVocabularyName})
		).toBeVisible();
	});

	await test.step('Add a vocabulary with existing external reference code', async () => {
		await assetCategoriesAdminPage.newVocabularyButton.click();

		await vocabulariesEditPage.fillName(getRandomString());
		await vocabulariesEditPage.fillExternalReferenceCode(
			vocabularyExternalReferenceCode
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: vocabulariesEditPage.saveButton,
			trigger: page.getByText(
				'Please enter a unique external reference code.',
				{
					exact: true,
				}
			),
		});

		await assetCategoriesAdminPage.goto(site.friendlyUrlPath);
	});

	await test.step('Delete the vocabulary', async () => {
		await vocabulariesEditPage.delete(newVocabularyName);

		await waitForAlert(page);

		await expect(
			page.getByRole('heading', {name: newVocabularyName})
		).not.toBeVisible();
	});
});

assetTypes.forEach(async (assetType) => {
	test(`Add a vocabulary for ${assetType}`, async ({
		assetCategoriesAdminPage,
		page,
		site,
		vocabulariesEditPage,
	}) => {
		await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

		const vocabularyName = getRandomString();

		await assetCategoriesAdminPage.newVocabularyButton.click();

		await vocabulariesEditPage.add({
			assetTypes: [assetType],
			name: vocabularyName,
		});

		await expect(
			page.getByRole('heading', {name: vocabularyName})
		).toBeVisible();

		await expect(page.getByText(assetType, {exact: true})).toBeVisible();
	});
});

test('Delete an associated asset type of vocabulary', async ({
	assetCategoriesAdminPage,
	page,
	site,
	vocabulariesEditPage,
}) => {
	await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

	const vocabularyName = getRandomString();
	const assetTypes = ['Blogs Entry', 'Object Entry Folder'];

	await test.step('Add a vocabulary with 2 asset types', async () => {
		await assetCategoriesAdminPage.newVocabularyButton.click();

		await vocabulariesEditPage.add({
			assetTypes,
			name: vocabularyName,
		});

		await expect(
			page.getByRole('heading', {name: vocabularyName})
		).toBeVisible();

		await expect(
			page.getByText(assetTypes.join(', '), {exact: true})
		).toBeVisible();
	});

	await test.step('Remove last associated asset type', async () => {
		await vocabulariesEditPage.goto(vocabularyName);

		await vocabulariesEditPage.removeLastAssociatedAssetType();

		await vocabulariesEditPage.saveButton.click();

		await waitForAlert(page);

		await expect(
			page.getByText('Object Entry Folder', {exact: true})
		).not.toBeVisible();
	});
});

test('Delete all categories of a vocabulary', async ({
	apiHelpers,
	assetCategoriesAdminPage,
	page,
	site,
}) => {
	await test.step('Add a vocabulary with 2 categories', async () => {
		await createCategories({
			apiHelpers,
			categoryNames: [
				{name: getRandomString()},
				{name: getRandomString()},
			],
			siteId: site.id,
			vocabularyName: getRandomString(),
		});
	});

	await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

	await test.step('Delete all categories', async () => {
		await assetCategoriesAdminPage.deleteAllCategories();

		await waitForAlert(page);

		await expect(page.getByText('There are no categories')).toBeVisible();
	});
});

test(
	'View the vocabulary info and reach the limit of categories',
	{tag: '@LPD-109100'},
	async ({
		apiHelpers,
		assetCategoriesAdminPage,
		assetCategoriesEditPage,
		page,
		site,
		vocabulariesEditPage,
	}) => {
		const vocabularyDescription =
			'The powerful, stylish 2021 Camry Hybrid gives up nothing with its optimized fuel economy, advanced tech and stirring drive. We have expanded our hybrid battery warranty to reflect our confidence in the quality of our products.';
		const vocabularyName = getRandomString();

		await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

		await test.step('View the info of a vocabulary with a long description', async () => {
			await assetCategoriesAdminPage.newVocabularyButton.click();

			await vocabulariesEditPage.add({
				description: vocabularyDescription,
				name: vocabularyName,
			});

			await expect(
				page.getByRole('heading', {name: vocabularyName})
			).toBeVisible();

			await expect(
				page.getByText('Asset Types: All Asset Types')
			).toBeVisible();

			await expect(
				page.getByText(`Description: ${vocabularyDescription}`)
			).toBeVisible();

			await expect(
				page.getByText(
					'The maximum number of categories per vocabulary is 150.'
				)
			).toBeVisible();
		});

		await test.step('Add the last category allowed by the default limit', async () => {
			const [vocabulary] = (
				await apiHelpers.headlessAdminTaxonomy.getTaxonomyVocabularyBySiteId(
					site.id
				)
			).items.filter(({name}: {name: string}) => name === vocabularyName);

			await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategoriesBatch(
				{
					names: Array.from(
						{length: 149},
						(_, index) => `${vocabularyName} ${index}`
					),
					vocabularyId: vocabulary.id,
				}
			);

			await page.reload();

			await page.getByRole('link', {name: 'Add Category'}).click();

			await expect(
				page.getByRole('button', {name: 'Save and Add a New One'})
			).toBeDisabled();

			const categoryName = getRandomString();

			await assetCategoriesEditPage.fillName(categoryName);

			await assetCategoriesEditPage.save(`Success:${categoryName}`);
		});

		await test.step('View the vocabulary at the limit of categories', async () => {
			await expect(
				page.getByText(
					'You have reached the limit of 150 categories for this vocabulary.'
				)
			).toBeVisible();

			await expect(
				page.getByRole('link', {name: 'Add Category'})
			).toBeHidden();

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: 'Edit'}),
				trigger: page
					.getByRole('row')
					.nth(1)
					.getByLabel('Show Actions'),
			});

			await expect(
				page.getByRole('menuitem', {name: 'Add Subcategory'})
			).toBeHidden();
		});
	}
);

test(
	'View the back button tooltip and move between vocabularies',
	{tag: '@LPD-109100'},
	async ({
		apiHelpers,
		assetCategoriesAdminPage,
		assetCategoriesEditPage,
		page,
		site,
		vocabulariesEditPage,
	}) => {
		const backLink = page.getByRole('link', {name: 'Go to Categories'});
		const categoryName = getRandomString();
		const vocabularyName = getRandomString();

		const assertBackTooltip = async () => {
			await expect(async () => {

				// Focusing an already focused link does not show the tooltip
				// again, so retries blur it first

				await backLink.blur({timeout: 1000});
				await backLink.focus({timeout: 1000});

				await expect(
					page.getByRole('tooltip', {name: 'Go to Categories'})
				).toBeVisible({timeout: 1000});
			}).toPass();
		};

		await test.step('View the tooltip when adding a vocabulary', async () => {
			await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

			await assetCategoriesAdminPage.newVocabularyButton.click();

			await expect(vocabulariesEditPage.nameInput).toBeVisible();

			await assertBackTooltip();
		});

		await createCategories({
			apiHelpers,
			categoryNames: [{name: categoryName}],
			siteId: site.id,
			vocabularyName,
		});

		await test.step('Move between vocabularies', async () => {
			await backLink.click();

			await assetCategoriesAdminPage.gotoVocabulary('Audience');

			await expect(
				page.getByRole('heading', {name: 'Audience'})
			).toBeVisible();

			await assetCategoriesAdminPage.gotoVocabulary(vocabularyName);

			await expect(
				page.getByRole('heading', {name: vocabularyName})
			).toBeVisible();
		});

		await test.step('View the tooltip when adding a category', async () => {
			await page.getByRole('link', {name: 'Add Category'}).click();

			await expect(assetCategoriesEditPage.nameInput).toBeVisible();

			await assertBackTooltip();
		});

		await test.step('View the tooltip when assigning a display page template', async () => {
			await backLink.click();

			await page.getByLabel('Select All Items on the Page').check();

			await page
				.getByRole('button', {name: 'Assign Display Page Template'})
				.click();

			await expect(
				page.getByLabel('Display Page Template')
			).toBeVisible();

			await assertBackTooltip();
		});

		await test.step('View the tooltip when adding a subcategory', async () => {
			await backLink.click();

			await assetCategoriesAdminPage.gotoAction(
				'Add Subcategory',
				categoryName
			);

			await expect(assetCategoriesEditPage.nameInput).toBeVisible();

			await assertBackTooltip();
		});
	}
);

test(
	'Search categories and subcategories',
	{tag: '@LPD-109100'},
	async ({apiHelpers, assetCategoriesAdminPage, page, site}) => {
		const vocabularyName = getRandomString();

		const [category1, category2, parentCategory] = await createCategories({
			apiHelpers,
			categoryNames: [
				{description: 'black and white', name: 'panda'},
				{description: 'brown or white', name: 'llama'},
				{name: getRandomString()},
			],
			siteId: site.id,
			vocabularyName,
		});

		await Promise.all([
			apiHelpers.headlessAdminTaxonomy.postTaxonomyCategoryTaxonomyCategory(
				{
					description: 'friendly chops',
					name: 'beard hair',
					parentTaxonomyCategoryId: parentCategory.id,
				}
			),
			apiHelpers.headlessAdminTaxonomy.postTaxonomyCategoryTaxonomyCategory(
				{
					description: 'handlebar',
					name: 'mustache hair',
					parentTaxonomyCategoryId: parentCategory.id,
				}
			),
		]);

		const searchInput = page.getByRole('searchbox', {name: 'Search for:'});

		const assertSearchResults = async (
			keywords: string,
			expectedNames: string[]
		) => {

			// The first search can be submitted before the management toolbar
			// is ready, so retry until the search results show up

			await expect(async () => {
				await searchInput.fill(keywords, {timeout: 2000});
				await searchInput.press('Enter', {timeout: 2000});

				await expect(
					page.getByText(
						new RegExp(
							`^${expectedNames.length} Results? Found for "${keywords}"`
						)
					)
				).toBeVisible({timeout: 3000});
			}).toPass();

			for (const name of expectedNames) {
				await expect(
					page.getByRole('row').filter({hasText: name})
				).toBeVisible();
			}

			await expect(page.getByRole('row')).toHaveCount(
				expectedNames.length ? expectedNames.length + 1 : 0
			);
		};

		await test.step('Search categories by name and description', async () => {
			await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

			await assetCategoriesAdminPage.gotoVocabulary(vocabularyName);

			await expect(
				page.getByRole('row').filter({hasText: category1.name})
			).toBeVisible();

			await assertSearchResults('llama', [category2.name]);
			await assertSearchResults('panda', [category1.name]);
			await assertSearchResults('brown', [category2.name]);
			await assertSearchResults('black', [category1.name]);
			await assertSearchResults('white', [
				category1.name,
				category2.name,
			]);
			await assertSearchResults(getRandomString(), []);
		});

		await test.step('Search subcategories by name and description', async () => {
			await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

			await assetCategoriesAdminPage.gotoVocabulary(vocabularyName);

			await page.getByRole('link', {name: parentCategory.name}).click();

			await expect(
				page.getByRole('row').filter({hasText: 'beard hair'})
			).toBeVisible();

			await assertSearchResults('beard', ['beard hair']);
			await assertSearchResults('mustache', ['mustache hair']);
			await assertSearchResults('friendly chops', ['beard hair']);
			await assertSearchResults('handlebar', ['mustache hair']);
			await assertSearchResults('hair', ['beard hair', 'mustache hair']);
			await assertSearchResults(getRandomString(), []);
		});
	}
);
