/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import getRandomString from '../../../utils/getRandomString';

const _ASSET_CATEGORY_CLASS_NAME =
	'com.liferay.asset.kernel.model.AssetCategory';

const test = mergeTests(
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

test(
	'A category display page resolves at its name-based URL',
	{tag: '@LPD-103529'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const taxonomy = await _createTaxonomy(apiHelpers, site);

		await displayPageTemplatesPage.createDefaultTemplate({
			className: _ASSET_CATEGORY_CLASS_NAME,
			mappedField: 'Name',
			pageEditorPage,
			site,
		});

		await test.step('The vocabulary and the category name the URL', async () => {
			await _expectCategoryAt(
				page,
				`/web${site.friendlyUrlPath}/v/${taxonomy.vocabularyName}/${taxonomy.categoryName}`,
				taxonomy.categoryName
			);
		});

		await test.step('A nested category appends its own segment', async () => {
			await _expectCategoryAt(
				page,
				`/web${site.friendlyUrlPath}/v/${taxonomy.vocabularyName}/` +
					`${taxonomy.categoryName}/${taxonomy.childCategoryName}`,
				taxonomy.childCategoryName
			);
		});

		await test.step('An unknown category name does not resolve', async () => {
			const response = await page.goto(
				`/web${site.friendlyUrlPath}/v/${taxonomy.vocabularyName}/` +
					getRandomString()
			);

			expect(response?.status()).toBe(404);
		});
	}
);

test(
	'A legacy ID-based category URL redirects to the name-based URL',
	{tag: '@LPD-103529'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const taxonomy = await _createTaxonomy(apiHelpers, site);

		await displayPageTemplatesPage.createDefaultTemplate({
			className: _ASSET_CATEGORY_CLASS_NAME,
			mappedField: 'Name',
			pageEditorPage,
			site,
		});

		await test.step('A root category redirects to its name-based URL', async () => {
			await _expectRedirect(
				page,
				`/web${site.friendlyUrlPath}/v/${taxonomy.categoryId}`,
				`/web${site.friendlyUrlPath}/v/${taxonomy.vocabularyName}/` +
					taxonomy.categoryName,
				taxonomy.categoryName
			);
		});

		await test.step('A nested category redirects to its full path', async () => {
			await _expectRedirect(
				page,
				`/web${site.friendlyUrlPath}/v/${taxonomy.childCategoryId}`,
				`/web${site.friendlyUrlPath}/v/${taxonomy.vocabularyName}/` +
					`${taxonomy.categoryName}/${taxonomy.childCategoryName}`,
				taxonomy.childCategoryName
			);
		});
	}
);

test(
	'A vocabulary holding spaces and accents resolves its categories',
	{tag: '@LPD-103529'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const categoryName = `categoría ${getRandomString()}`.toLowerCase();
		const vocabularyName =
			`vocabulario ñ ${getRandomString()}`.toLowerCase();

		await test.step('Create the taxonomy and grant Guest VIEW', async () => {
			const vocabulary =
				await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary(
					{
						name: vocabularyName,
						siteId: String(site.id),
					}
				);

			const category =
				await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
					{
						name: categoryName,
						vocabularyId: vocabulary.id,
					}
				);

			await apiHelpers.headlessAdminTaxonomy.putTaxonomyVocabulariesTaxonomyVocabularyPermissions(
				vocabulary.id,
				{actionIds: ['VIEW'], roleName: 'Guest'}
			);

			await apiHelpers.headlessAdminTaxonomy.putTaxonomyCategoriesTaxonomyCategoryPermissions(
				category.id,
				{actionIds: ['VIEW'], roleName: 'Guest'}
			);
		});

		await displayPageTemplatesPage.createDefaultTemplate({
			className: _ASSET_CATEGORY_CLASS_NAME,
			mappedField: 'Name',
			pageEditorPage,
			site,
		});

		await _expectCategoryAt(
			page,
			`/web${site.friendlyUrlPath}/v/${encodeURIComponent(
				vocabularyName
			)}/${encodeURIComponent(categoryName)}`,
			categoryName
		);
	}
);

test(
	'A category whose name-based URL exceeds the maximum length resolves by ID',
	{tag: '@LPD-103529'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// A friendly URL holds up to 255 characters, so only a deep chain of
		// categories takes the composed URL over its maximum length

		const namePadding = 'x'.repeat(240);
		const vocabularyName = `vocabulary-${getRandomString()}`.toLowerCase();

		let categoryId: number;

		await test.step('Create a chain deep enough to exceed the length', async () => {
			const vocabulary =
				await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary(
					{
						name: vocabularyName,
						siteId: String(site.id),
					}
				);

			await apiHelpers.headlessAdminTaxonomy.putTaxonomyVocabulariesTaxonomyVocabularyPermissions(
				vocabulary.id,
				{actionIds: ['VIEW'], roleName: 'Guest'}
			);

			const category =
				await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
					{
						name: `1-${namePadding}`,
						vocabularyId: vocabulary.id,
					}
				);

			categoryId = category.id;

			for (let i = 2; i <= 9; i++) {
				const childCategory =
					await apiHelpers.headlessAdminTaxonomy.postTaxonomyCategoryTaxonomyCategory(
						{
							name: `${i}-${namePadding}`,
							parentTaxonomyCategoryId: categoryId,
						}
					);

				categoryId = childCategory.id;
			}

			await apiHelpers.headlessAdminTaxonomy.putTaxonomyCategoriesTaxonomyCategoryPermissions(
				categoryId,
				{actionIds: ['VIEW'], roleName: 'Guest'}
			);
		});

		await displayPageTemplatesPage.createDefaultTemplate({
			className: _ASSET_CATEGORY_CLASS_NAME,
			mappedField: 'Name',
			pageEditorPage,
			site,
		});

		// The ID keeps serving the page instead of redirecting, because the
		// name-based URL it would redirect to does not fit

		await _expectCategoryAt(
			page,
			`/web${site.friendlyUrlPath}/v/${categoryId}`,
			`9-${namePadding}`
		);

		await expect(page).toHaveURL(new RegExp(`/v/${categoryId}$`), {
			timeout: 2000,
		});
	}
);

test(
	'Editing the slug of a category moves its display page URL',
	{tag: '@LPD-103529'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const slug = `slug-${getRandomString()}`.toLowerCase();

		const taxonomy = await _createTaxonomy(apiHelpers, site);

		await displayPageTemplatesPage.createDefaultTemplate({
			className: _ASSET_CATEGORY_CLASS_NAME,
			mappedField: 'Name',
			pageEditorPage,
			site,
		});

		await apiHelpers.headlessAdminTaxonomy.patchTaxonomyCategory({
			friendlyUrlPath: slug,
			id: taxonomy.categoryId,
		});

		await test.step('The category answers at the edited slug', async () => {
			await _expectCategoryAt(
				page,
				`/web${site.friendlyUrlPath}/v/${taxonomy.vocabularyName}/${slug}`,
				taxonomy.categoryName
			);
		});

		await test.step('A nested category follows its parent slug', async () => {
			await _expectCategoryAt(
				page,
				`/web${site.friendlyUrlPath}/v/${taxonomy.vocabularyName}/` +
					`${slug}/${taxonomy.childCategoryName}`,
				taxonomy.childCategoryName
			);
		});
	}
);

/**
 * Creates a vocabulary holding a category and a nested one. Categories created
 * through the API are born with Owner permissions only, so the display page
 * 404s until Guest is granted VIEW on the vocabulary and on both categories.
 */
test(
	'A category display page resolves behind a locale prefix',
	{tag: '@LPD-103529'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const spanishCategoryName = `patata-${getRandomString()}`.toLowerCase();

		const taxonomy = await _createTaxonomy(apiHelpers, site, {
			'es-ES': spanishCategoryName,
		});

		await displayPageTemplatesPage.createDefaultTemplate({
			className: _ASSET_CATEGORY_CLASS_NAME,
			mappedField: 'Name',
			pageEditorPage,
			site,
		});

		const base = `/web${site.friendlyUrlPath}/v/${taxonomy.vocabularyName}`;

		await test.step('The Spanish name resolves under the locale prefix', async () => {
			await _expectCategoryAt(
				page,
				`/es${base}/${spanishCategoryName}`,
				spanishCategoryName
			);
		});

		await test.step('The Spanish name resolves without the locale prefix', async () => {
			await _expectCategoryAt(
				page,
				`${base}/${spanishCategoryName}`,
				spanishCategoryName
			);
		});

		await test.step('The default name keeps resolving under the locale prefix', async () => {
			await _expectCategoryAt(
				page,
				`/es${base}/${taxonomy.categoryName}`,
				spanishCategoryName
			);
		});

		await test.step('The ID keeps resolving under the locale prefix', async () => {
			await _expectCategoryAt(
				page,
				`/es/web${site.friendlyUrlPath}/v/${taxonomy.categoryId}`,
				spanishCategoryName
			);
		});
	}
);

async function _createTaxonomy(
	apiHelpers: DataApiHelpers,
	site: Site,
	categoryNameI18n?: {[key: string]: string}
) {
	const categoryName = `category-${getRandomString()}`.toLowerCase();
	const childCategoryName = `child-${getRandomString()}`.toLowerCase();
	const vocabularyName = `vocabulary-${getRandomString()}`.toLowerCase();

	const vocabulary =
		await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
			name: vocabularyName,
			siteId: String(site.id),
		});

	const category =
		await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
			{
				name: categoryName,
				name_i18n: categoryNameI18n,
				vocabularyId: vocabulary.id,
			}
		);

	const childCategory =
		await apiHelpers.headlessAdminTaxonomy.postTaxonomyCategoryTaxonomyCategory(
			{
				name: childCategoryName,
				parentTaxonomyCategoryId: category.id,
			}
		);

	await apiHelpers.headlessAdminTaxonomy.putTaxonomyVocabulariesTaxonomyVocabularyPermissions(
		vocabulary.id,
		{actionIds: ['VIEW'], roleName: 'Guest'}
	);

	for (const id of [category.id, childCategory.id]) {
		await apiHelpers.headlessAdminTaxonomy.putTaxonomyCategoriesTaxonomyCategoryPermissions(
			id,
			{actionIds: ['VIEW'], roleName: 'Guest'}
		);
	}

	return {
		categoryId: category.id,
		categoryName,
		childCategoryId: childCategory.id,
		childCategoryName,
		vocabularyName,
	};
}

async function _expectCategoryAt(page: Page, url: string, name: string) {
	await expect(async () => {
		await page.goto(url);

		await expect(
			page.locator('#main-content').getByText(name, {exact: true})
		).toBeVisible({timeout: 2000});
	}).toPass({timeout: 10000});
}

async function _expectRedirect(
	page: Page,
	url: string,
	expectedURL: string,
	name: string
) {
	await expect(async () => {
		await page.goto(url);

		await expect(page).toHaveURL(new RegExp(`${expectedURL}$`), {
			timeout: 2000,
		});

		await expect(
			page.locator('#main-content').getByText(name, {exact: true})
		).toBeVisible({timeout: 2000});
	}).toPass({timeout: 10000});
}
