/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {createCategories} from '../../../helpers/CreateCategories';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import {DisplayPageTemplatesPage} from '../../../pages/layout-page-template-admin-web/DisplayPageTemplatesPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {enableLocalStaging} from '../../../utils/staging';
import addApprovedStructuredContent from '../../../utils/structured-content/addApprovedStructuredContent';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {stagingPageTest} from '../../export-import-web/main/fixtures/stagingPageTest';
import {assetCategoriesPagesTest} from './fixtures/assetCategoriesAdminPagesTest';
import {AssetCategoriesAdminPage} from './pages/AssetCategoriesAdminPage';
import {AssetCategoriesEditPage} from './pages/AssetCategoriesEditPage';

const _ASSET_CATEGORY_CLASS_NAME =
	'com.liferay.asset.kernel.model.AssetCategory';

const test = mergeTests(
	assetCategoriesPagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	stagingPageTest
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

test(
	'Select default, specific and inherited display page templates in the category and subcategory editors',
	{tag: '@LPD-109100'},
	async ({
		apiHelpers,
		assetCategoriesAdminPage,
		assetCategoriesEditPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Each template maps a different field, so the rendered value tells
		// which template a category uses

		await displayPageTemplatesPage.createDefaultTemplate({
			className: _ASSET_CATEGORY_CLASS_NAME,
			mappedField: 'Name',
			pageEditorPage,
			site,
		});

		const {name: descriptionTemplateName} =
			await displayPageTemplatesPage.createMappedTemplate({
				className: _ASSET_CATEGORY_CLASS_NAME,
				mappedField: 'Description',
				pageEditorPage,
				site,
			});

		const {name: vocabularyTemplateName} =
			await displayPageTemplatesPage.createMappedTemplate({
				className: _ASSET_CATEGORY_CLASS_NAME,
				mappedField: 'Vocabulary',
				pageEditorPage,
				site,
			});

		const vocabularyName = getRandomString();

		await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
			name: vocabularyName,
			siteId: site.id,
		});

		const saveCategory = async ({
			description,
			displayPageName,
			name,
			type,
		}: {
			description?: string;
			displayPageName?: string;
			name: string;
			type: 'Default' | 'Inherited' | 'Specific';
		}) => {
			await assetCategoriesEditPage.fillName(name);

			if (description) {
				await assetCategoriesEditPage.descriptionField.fill(
					description
				);
			}

			await assetCategoriesEditPage.selectDisplayPage({
				displayPageName,
				type,
			});

			await assetCategoriesEditPage.save(`Success:${name}`);
		};

		const viewDisplayPage = async (categoryName: string, text: string) => {
			await assetCategoriesAdminPage.gotoAction(
				'View Display Page',
				categoryName
			);

			await expect(page).toHaveURL(/\/v\//);

			await expect(
				page.locator('[data-lfr-editable-id="element-text"]')
			).toHaveText(text);
		};

		const categoryName = getRandomString();
		const description = getRandomString();

		await test.step('A category uses the default display page template', async () => {
			await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

			await assetCategoriesAdminPage.gotoVocabulary(vocabularyName);

			await page.getByRole('link', {name: 'Add Category'}).click();

			await saveCategory({
				description,
				name: categoryName,
				type: 'Default',
			});

			await viewDisplayPage(categoryName, categoryName);
		});

		await test.step('A category uses a specific display page template', async () => {
			await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

			await assetCategoriesAdminPage.gotoVocabulary(vocabularyName);

			await assetCategoriesEditPage.goto(categoryName);

			await assetCategoriesEditPage.selectDisplayPage({
				displayPageName: descriptionTemplateName,
				type: 'Specific',
			});

			await assetCategoriesEditPage.save(`Success:${categoryName}`);

			await viewDisplayPage(categoryName, description);
		});

		const subcategoryName = getRandomString();

		await test.step('A subcategory uses a specific display page template and its subcategory inherits it', async () => {
			await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

			await assetCategoriesAdminPage.gotoVocabulary(vocabularyName);

			await assetCategoriesAdminPage.gotoAction(
				'Add Subcategory',
				categoryName
			);

			await saveCategory({
				displayPageName: vocabularyTemplateName,
				name: subcategoryName,
				type: 'Specific',
			});

			await assetCategoriesAdminPage.gotoAction(
				'Add Subcategory',
				subcategoryName
			);

			const grandSubcategoryName = getRandomString();

			await saveCategory({name: grandSubcategoryName, type: 'Inherited'});

			await viewDisplayPage(grandSubcategoryName, vocabularyName);
		});

		await test.step('A subcategory inherits the display page template of its parent', async () => {
			await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

			await assetCategoriesAdminPage.gotoVocabulary(vocabularyName);

			await assetCategoriesAdminPage.gotoAction(
				'Add Subcategory',
				categoryName
			);

			const childCategoryName = getRandomString();
			const childDescription = getRandomString();

			await saveCategory({
				description: childDescription,
				name: childCategoryName,
				type: 'Inherited',
			});

			await viewDisplayPage(childCategoryName, childDescription);
		});
	}
);

test(
	'View the items with the category in a collection display of its display page template',
	{tag: '@LPD-109100'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		await test.step('Add a collection display of the items with this category', async () => {
			const name = getRandomString();

			const {classNameId} =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					_ASSET_CATEGORY_CLASS_NAME
				);

			const {layoutPageTemplateEntryId} =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
					{
						classNameId,
						groupId: String(site.id),
						name,
					}
				);

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
				{layoutPageTemplateEntryId}
			);

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.editTemplate(name);

			await pageEditorPage.addFragment(
				'Content Display',
				'Collection Display'
			);

			await page.locator('.lfr-layout-structure-item-collection').click();

			await pageEditorPage.chooseCollectionDisplayCollection(
				'Related Items Collection Providers',
				'Items with This Category'
			);

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.addFragment(
				'Basic Components',
				'Heading',
				page.locator('.page-editor__collection-item.empty').first()
			);

			const headingId = await pageEditorPage.getFragmentId('Heading');

			await pageEditorPage.selectEditable(headingId, 'element-text');

			await page.getByLabel('Field').selectOption('Title');

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();
		});

		const taxonomy = await _createTaxonomy(apiHelpers, site);

		const blogPosting = await apiHelpers.headlessDelivery.postBlog(
			site.id,
			{taxonomyCategoryIds: [taxonomy.categoryId]}
		);

		const uncategorizedBlogPosting =
			await apiHelpers.headlessDelivery.postBlog(site.id);

		const document = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(
					__dirname,
					'../../document-library-web/main/dependencies/image1.jpeg'
				)
			),
			{
				fileName: getRandomString(),
				taxonomyCategoryIds: [taxonomy.categoryId],
				title: getRandomString(),
			}
		);

		const webContentTitle = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			categoryIds: [taxonomy.categoryId],
			contentStructureId: await getBasicWebContentStructureId(apiHelpers),
			siteId: site.id,
			title: webContentTitle,
		});

		await page.goto(
			`/web${site.friendlyUrlPath}/v/${taxonomy.vocabularyName}/${taxonomy.categoryName}`
		);

		const collection = page.locator(
			'.lfr-layout-structure-item-collection'
		);

		for (const title of [
			blogPosting.headline,
			document.title,
			webContentTitle,
		]) {
			await expect(
				collection.getByText(title, {exact: true})
			).toBeVisible();
		}

		await expect(
			collection.getByText(uncategorizedBlogPosting.headline, {
				exact: true,
			})
		).toHaveCount(0);
	}
);

test(
	'View the name, display page URL and main image of a category in its display page template',
	{tag: '@LPD-109100'},
	async ({
		apiHelpers,
		assetCategoriesAdminPage,
		assetCategoriesEditPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		await test.step('Map the name, display page URL and main image', async () => {
			const name = getRandomString();

			const {classNameId} =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					_ASSET_CATEGORY_CLASS_NAME
				);

			const {layoutPageTemplateEntryId} =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
					{
						classNameId,
						groupId: String(site.id),
						name,
					}
				);

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
				{layoutPageTemplateEntryId}
			);

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.editTemplate(name);

			for (const [fragmentName, editableId, field] of [
				['Heading', 'element-text', 'Name'],
				['Button', 'link', 'Default'],
			]) {
				await pageEditorPage.addFragment(
					'Basic Components',
					fragmentName
				);

				await pageEditorPage.selectEditable(
					await pageEditorPage.getFragmentId(fragmentName),
					editableId
				);

				await pageEditorPage.changeConfiguration({
					fieldLabel: 'Field',
					tab: 'Mapping',
					value: field,
				});
			}

			await pageEditorPage.addFragment('Basic Components', 'Image');

			await pageEditorPage.selectEditable(
				await pageEditorPage.getFragmentId('Image'),
				'image-square'
			);

			await pageEditorPage.changeConfiguration({
				fieldLabel: 'Source Selection',
				tab: 'Image Source',
				value: 'Mapping',
			});

			await pageEditorPage.changeConfiguration({
				fieldLabel: 'Field',
				tab: 'Image Source',
				value: 'Main Image',
			});

			await pageEditorPage.publishPage();
		});

		const taxonomy = await _createTaxonomy(apiHelpers, site);

		await test.step('Add an image to the category', async () => {
			await _addCategoryImage({
				assetCategoriesAdminPage,
				assetCategoriesEditPage,
				page,
				site,
				taxonomy,
			});
		});

		await test.step('View the mapped values in the display page', async () => {
			await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

			await assetCategoriesAdminPage.gotoVocabulary(
				taxonomy.vocabularyName
			);

			await assetCategoriesAdminPage.gotoAction(
				'View Display Page',
				taxonomy.categoryName
			);

			await expect(page).toHaveURL(/\/v\//);

			await expect(
				page.locator('[data-lfr-editable-id="element-text"]')
			).toHaveText(taxonomy.categoryName);

			await expect(
				page.locator('[data-lfr-editable-id="link"]')
			).toHaveText(
				`${new URL(page.url()).origin}/web${site.friendlyUrlPath}/v/` +
					`${taxonomy.vocabularyName}/${taxonomy.categoryName}`
			);

			const image = page.locator('[data-lfr-editable-id="image-square"]');

			await expect(image).toHaveAttribute('src', /\/images\/\d+/);

			await expect
				.poll(() =>
					image.evaluate(
						(element: HTMLImageElement) => element.naturalWidth
					)
				)
				.toBeGreaterThan(0);
		});
	}
);

test(
	'Preview a category, a subcategory and no item in a display page template',
	{tag: '@LPD-109100'},
	async ({
		apiHelpers,
		assetCategoriesAdminPage,
		assetCategoriesEditPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const taxonomy = await _createTaxonomy(apiHelpers, site);

		await _addCategoryImage({
			assetCategoriesAdminPage,
			assetCategoriesEditPage,
			page,
			site,
			taxonomy,
		});

		await test.step('Map the name and main image to a card', async () => {
			const name = getRandomString();

			const {classNameId} =
				await apiHelpers.jsonWebServicesClassName.fetchClassName(
					_ASSET_CATEGORY_CLASS_NAME
				);

			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
				{
					classNameId,
					groupId: String(site.id),
					name,
				}
			);

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.editTemplate(name);

			await pageEditorPage.addFragment('Basic Components', 'Card');

			const cardId = await pageEditorPage.getFragmentId('Card');

			await pageEditorPage.selectEditable(cardId, '04-link');

			await pageEditorPage.changeConfiguration({
				fieldLabel: 'Field',
				tab: 'Mapping',
				value: 'Name',
			});

			await pageEditorPage.selectEditable(cardId, '01-img');

			await pageEditorPage.changeConfiguration({
				fieldLabel: 'Source Selection',
				tab: 'Image Source',
				value: 'Mapping',
			});

			await pageEditorPage.changeConfiguration({
				fieldLabel: 'Field',
				tab: 'Image Source',
				value: 'Main Image',
			});

			await pageEditorPage.waitForChangesSaved();
		});

		const expectCard = async (
			targetPage: Page,
			linkText: string,
			imageSrc: RegExp
		) => {
			await expect(
				targetPage.locator('[data-lfr-editable-id="04-link"]')
			).toHaveText(linkText);

			await expect(
				targetPage.locator('[data-lfr-editable-id="01-img"]')
			).toHaveAttribute('src', imageSrc);
		};

		const expectPreviewInNewTab = async (
			linkText: string,
			imageSrc: RegExp
		) => {
			const [previewPage] = await Promise.all([
				page.context().waitForEvent('page'),
				clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('menuitem', {
						name: 'Preview in a New Tab',
					}),
					trigger: page
						.locator('.control-menu-nav-item')
						.getByLabel('Options', {exact: true}),
				}),
			]);

			await expectCard(previewPage, linkText, imageSrc);

			await previewPage.close();
		};

		const selectNone = async () => {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: 'None'}),
				trigger: pageEditorPage.previewItemSelectorButton,
			});

			await expect(pageEditorPage.previewItemSelectorButton).toHaveText(
				'None'
			);
		};

		const selectCategory = async (categoryName: string) => {
			await pageEditorPage.previewItemSelectorButton.click();

			await pageEditorPage.selectOtherPreviewItemMenuItem.click();

			const selectFrame = page.frameLocator('iframe[title="Select"]');

			await selectFrame
				.getByRole('searchbox', {name: 'Search Categories'})
				.fill(categoryName);

			await selectFrame
				.getByRole('treeitem', {exact: true, name: categoryName})
				.click();

			await expect(pageEditorPage.previewItemSelectorButton).toHaveText(
				categoryName
			);
		};

		const defaultLinkText = 'Go Somewhere';
		const imageSrc = /\/images\/\d+/;
		const placeholderImageSrc = /^data:image\/png/;

		await test.step('Preview the category', async () => {
			await selectCategory(taxonomy.categoryName);

			await expectCard(page, taxonomy.categoryName, imageSrc);

			await expectPreviewInNewTab(taxonomy.categoryName, imageSrc);
		});

		await test.step('Preview no item after the category', async () => {
			await selectNone();

			await expectCard(page, defaultLinkText, placeholderImageSrc);

			await expectPreviewInNewTab(defaultLinkText, placeholderImageSrc);
		});

		await test.step('Preview the subcategory without an image', async () => {
			await selectCategory(taxonomy.childCategoryName);

			await expectCard(
				page,
				taxonomy.childCategoryName,
				placeholderImageSrc
			);

			await expectPreviewInNewTab(
				taxonomy.childCategoryName,
				placeholderImageSrc
			);
		});

		await test.step('Preview no item after the subcategory', async () => {
			await selectNone();

			await expectCard(page, defaultLinkText, placeholderImageSrc);

			await expectPreviewInNewTab(defaultLinkText, placeholderImageSrc);
		});
	}
);

test(
	'Only the public vocabularies are available in the preview item selector',
	{tag: '@LPD-109100'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const companyGroup =
			await apiHelpers.jsonWebServicesGroup.getCompanyGroup(
				company.companyId
			);

		const internalVocabularyName = getRandomString();
		const publicVocabularyName = getRandomString();

		for (const [vocabularyName, vocabularyVisibility] of [
			[internalVocabularyName, true],
			[publicVocabularyName, false],
		] as const) {
			await createCategories({
				apiHelpers,
				categoryNames: [{name: getRandomString()}],
				siteId: companyGroup.groupId,
				vocabularyName,
				vocabularyVisibility,
			});
		}

		const name = getRandomString();

		const {classNameId} =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				_ASSET_CATEGORY_CLASS_NAME
			);

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
			{
				classNameId,
				groupId: String(site.id),
				name,
			}
		);

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		await displayPageTemplatesPage.editTemplate(name);

		await pageEditorPage.previewItemSelectorButton.click();

		await pageEditorPage.selectOtherPreviewItemMenuItem.click();

		const selectFrame = page.frameLocator('iframe[title="Select"]');

		await expect(
			selectFrame.getByRole('treeitem', {
				name: `${publicVocabularyName} (Global)`,
			})
		).toBeVisible();

		await expect(
			selectFrame.getByRole('treeitem', {
				name: `${internalVocabularyName} (Global)`,
			})
		).toHaveCount(0);
	}
);

test(
	'Assign display page templates to several categories in staging',
	{tag: '@LPD-109100'},
	async ({
		apiHelpers,
		assetCategoriesAdminPage,
		assetCategoriesEditPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
		stagingPage,
	}) => {
		const {categoryNames, displayPageName, stagingSite, vocabularyName} =
			await _addStagingCategories({
				apiHelpers,
				displayPageTemplatesPage,
				page,
				pageEditorPage,
				site,
			});

		const assignDisplayPageButton = page.getByRole('button', {
			name: 'Assign Display Page Template',
		});

		const viewDisplayPages = (
			withDisplayPage: string[],
			withoutDisplayPage: string[] = []
		) =>
			_viewDisplayPages({
				assetCategoriesAdminPage,
				page,
				siteUrl: stagingSite.friendlyUrlPath,
				vocabularyName,
				withDisplayPage,
				withoutDisplayPage,
			});

		await test.step('Assign a specific display page template to all the categories', async () => {
			await assetCategoriesAdminPage.goto(stagingSite.friendlyUrlPath);

			await assetCategoriesAdminPage.gotoVocabulary(vocabularyName);

			await page.getByLabel('Select All Items on the Page').check();

			await assignDisplayPageButton.click();

			await assetCategoriesEditPage.selectDisplayPage({
				displayPageName,
				type: 'Specific',
			});

			await assetCategoriesEditPage.save();

			await viewDisplayPages(categoryNames);
		});

		await test.step('The categories cannot be assigned in live', async () => {
			await stagingPage.goto(
				`${site.friendlyUrlPath.substring(1)}-staging`
			);

			await stagingPage.publish();

			await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

			await assetCategoriesAdminPage.gotoVocabulary(vocabularyName);

			await page.getByLabel('Select All Items on the Page').check();

			await expect(assignDisplayPageButton).toBeDisabled();
		});

		await test.step('Assign the default display page template to two categories', async () => {
			await assetCategoriesAdminPage.goto(stagingSite.friendlyUrlPath);

			await assetCategoriesAdminPage.gotoVocabulary(vocabularyName);

			for (const categoryName of categoryNames.slice(0, 2)) {
				await page
					.getByRole('row', {name: categoryName})
					.getByRole('checkbox')
					.check();
			}

			await assignDisplayPageButton.click();

			await assetCategoriesEditPage.selectDisplayPage({type: 'Default'});

			await assetCategoriesEditPage.save();

			await viewDisplayPages(
				categoryNames.slice(2),
				categoryNames.slice(0, 2)
			);
		});
	}
);

// Quarantined until LPD-108099: the display page assignments of the
// categories do not reach the live site, so their display pages return 404

test.skip(
	'View the display page templates assigned in staging in live',
	{tag: '@LPD-109100'},
	async ({
		apiHelpers,
		assetCategoriesAdminPage,
		assetCategoriesEditPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
		stagingPage,
	}) => {
		const {categoryNames, displayPageName, stagingSite, vocabularyName} =
			await _addStagingCategories({
				apiHelpers,
				displayPageTemplatesPage,
				page,
				pageEditorPage,
				site,
			});

		await assetCategoriesAdminPage.goto(stagingSite.friendlyUrlPath);

		await assetCategoriesAdminPage.gotoVocabulary(vocabularyName);

		await page.getByLabel('Select All Items on the Page').check();

		await page
			.getByRole('button', {name: 'Assign Display Page Template'})
			.click();

		await assetCategoriesEditPage.selectDisplayPage({
			displayPageName,
			type: 'Specific',
		});

		await assetCategoriesEditPage.save();

		await stagingPage.goto(`${site.friendlyUrlPath.substring(1)}-staging`);

		await stagingPage.publish();

		await _viewDisplayPages({
			assetCategoriesAdminPage,
			page,
			siteUrl: site.friendlyUrlPath,
			vocabularyName,
			withDisplayPage: categoryNames,
		});
	}
);

async function _addStagingCategories({
	apiHelpers,
	displayPageTemplatesPage,
	page,
	pageEditorPage,
	site,
}: {
	apiHelpers: DataApiHelpers;
	displayPageTemplatesPage: DisplayPageTemplatesPage;
	page: Page;
	pageEditorPage: PageEditorPage;
	site: Site;
}) {
	await enableLocalStaging(apiHelpers, page, site);

	const company =
		await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
			'liferay.com'
		);

	const stagingGroup = await apiHelpers.jsonWebServicesGroup.getGroupByKey(
		company.companyId,
		`${site.name}-staging`
	);

	const stagingSite: Site = {
		...site,
		friendlyUrlPath: `${site.friendlyUrlPath}-staging`,
		id: String(stagingGroup.groupId),
	};

	const vocabularyName = getRandomString();

	const categories = await createCategories({
		apiHelpers,
		categoryNames: [
			{name: getRandomString()},
			{name: getRandomString()},
			{name: getRandomString()},
		],
		siteId: stagingSite.id,
		vocabularyName,
	});

	const {name: displayPageName} =
		await displayPageTemplatesPage.createMappedTemplate({
			className: _ASSET_CATEGORY_CLASS_NAME,
			mappedField: 'Name',
			pageEditorPage,
			site: stagingSite,
		});

	return {
		categoryNames: categories.map(({name}) => name),
		displayPageName,
		stagingSite,
		vocabularyName,
	};
}

async function _addCategoryImage({
	assetCategoriesAdminPage,
	assetCategoriesEditPage,
	page,
	site,
	taxonomy,
}: {
	assetCategoriesAdminPage: AssetCategoriesAdminPage;
	assetCategoriesEditPage: AssetCategoriesEditPage;
	page: Page;
	site: Site;
	taxonomy: {categoryName: string; vocabularyName: string};
}) {

	// The Images tab only shows when editing an existing category

	await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

	await assetCategoriesAdminPage.gotoVocabulary(taxonomy.vocabularyName);

	await assetCategoriesEditPage.goto(taxonomy.categoryName);

	await page.getByRole('link', {exact: true, name: 'Images'}).click();

	await page.getByRole('link', {name: 'Add Image'}).click();

	await page.getByRole('button', {name: 'Select File'}).click();

	const selectFileFrame = page.frameLocator('iframe[title="Select File"]');

	await selectFileFrame
		.locator('input[type="file"]')
		.setInputFiles(
			path.join(
				__dirname,
				'../../document-library-web/main/dependencies/image1.jpeg'
			)
		);

	await selectFileFrame
		.getByRole('button', {exact: true, name: 'Add'})
		.click();

	await page.getByRole('button', {name: 'Publish'}).click();

	await waitForAlert(page);
}

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

async function _viewDisplayPages({
	assetCategoriesAdminPage,
	page,
	siteUrl,
	vocabularyName,
	withDisplayPage,
	withoutDisplayPage = [],
}: {
	assetCategoriesAdminPage: AssetCategoriesAdminPage;
	page: Page;
	siteUrl: string;
	vocabularyName: string;
	withDisplayPage: string[];
	withoutDisplayPage?: string[];
}) {
	for (const categoryName of withDisplayPage) {
		await assetCategoriesAdminPage.goto(siteUrl);

		await assetCategoriesAdminPage.gotoVocabulary(vocabularyName);

		await assetCategoriesAdminPage.gotoAction(
			'View Display Page',
			categoryName
		);

		await expect(page).toHaveURL(new RegExp(`/web${siteUrl}/v/`));

		await expect(
			page.locator('[data-lfr-editable-id="element-text"]')
		).toHaveText(categoryName);
	}

	for (const categoryName of withoutDisplayPage) {
		await assetCategoriesAdminPage.goto(siteUrl);

		await assetCategoriesAdminPage.gotoVocabulary(vocabularyName);

		await clickAndExpectToBeVisible({
			target: page.getByRole('menuitem', {name: 'Edit'}),
			trigger: page
				.getByRole('row', {name: categoryName})
				.getByLabel('Show Actions'),
		});

		await expect(
			page.getByRole('menuitem', {name: 'View Display Page'})
		).toHaveCount(0);
	}
}
