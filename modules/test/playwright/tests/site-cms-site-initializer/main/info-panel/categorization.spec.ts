/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {
	performUserSwitchViaApi,
	userData,
} from '../../../../utils/performLogin';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPD-58677': {enabled: true},
	}),
	loginTest()
);

test(
	'Info Panel Categories tab',
	{tag: '@LPD-68491'},
	async ({apiHelpers, assetsPage, infoPanelPage, page, spaceSummaryPage}) => {
		const applicationName = 'cms/basic-web-contents';
		let categoryLabel;
		const categoryName = getRandomString();
		const file1Title = `title ${getRandomString()}`;
		let objectEntry;
		const spaceName = 'Default';
		const tagName = getRandomString();
		let tagLabel;
		let user;
		const vocabularyName = getRandomString();

		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const vocabularyId = await apiHelpers.headlessAdminTaxonomy
			.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1}],
				assetTypes: [
					{
						required: false,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name: vocabularyName,
				siteId,
				visibilityType: 'PUBLIC',
			})
			.then((response) => response.id);

		const categoryId = await apiHelpers.headlessAdminTaxonomy
			.postTaxonomyVocabularyTaxonomyCategory({
				name: categoryName,
				vocabularyId,
			})
			.then((response) => response.id);

		await apiHelpers.headlessAdminTaxonomy.putTaxonomyVocabulariesTaxonomyVocabularyPermissions(
			vocabularyId,
			{actionIds: ['VIEW'], roleName: 'Site Member'}
		);

		await apiHelpers.headlessAdminTaxonomy.putTaxonomyCategoriesTaxonomyCategoryPermissions(
			categoryId,
			{actionIds: ['VIEW'], roleName: 'Site Member'}
		);

		try {
			objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: file1Title,
				},
				applicationName,
				spaceName
			);

			await test.step('Go to All Assets and open the Info Panel Categorization Tab', async () => {
				await assetsPage.gotoAll();

				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: file1Title,
				});

				await expect(
					page.getByRole('heading', {name: file1Title})
				).toBeVisible();

				await infoPanelPage.selectTab('Categorization').click();
			});

			await test.step('Add a new tag to the content', async () => {
				const tagsAutocomplete = page.getByPlaceholder('Add tag');

				await tagsAutocomplete.fill(tagName);

				const newTagOption = page.getByRole('option', {
					name: 'Create New Tag:',
				});

				await newTagOption.waitFor();
				await newTagOption.click();

				tagLabel = page.locator('.label-item', {hasText: tagName});

				await expect(tagLabel).toBeAttached();
			});

			await test.step('Add a new category to the content', async () => {
				const categoriesAutocomplete =
					page.getByPlaceholder('Add category');

				await categoriesAutocomplete.fill(categoryName);

				const option = page.getByRole('option', {name: categoryName});

				await option.waitFor();
				await option.click();

				categoryLabel = page.locator('.label-item', {
					hasText: categoryName,
				});

				await expect(categoryLabel).toBeAttached();
			});

			let personaLabel;
			let personaName;

			await test.step('Add a new persona to the content', async () => {
				const personasAutocomplete =
					page.getByPlaceholder('Add personas');

				personaName = 'Decision Maker';

				await personasAutocomplete.fill(personaName);

				const option = page.getByRole('option', {name: personaName});

				await option.waitFor();
				await option.click();

				personaLabel = page.locator('.label-item', {
					hasText: personaName,
				});

				await expect(personaLabel).toBeAttached();
			});

			let stageLabel;
			let stageName;

			await test.step('Add a new funnel stage to the content', async () => {
				const stagesAutocomplete = page.getByPlaceholder('Add stages');

				stageName = 'Awareness';

				await stagesAutocomplete.fill(stageName);

				const option = page.getByRole('option', {name: stageName});

				await option.waitFor();
				await option.click();

				stageLabel = page.locator('.label-item', {hasText: stageName});

				await expect(stageLabel).toBeAttached();
			});

			await test.step('Create an user and add to the Space', async () => {
				user = await apiHelpers.headlessAdminUser.postUserAccount();

				userData[user.alternateName] = {
					name: user.givenName,
					password: 'test',
					surname: user.familyName,
				};

				await spaceSummaryPage.goto(spaceName);

				await spaceSummaryPage.addUserOrUserGroup(user.name, 'users');
			});

			await test.step('Login as a space member and go to Info Panel Categorization tab', async () => {
				await performUserSwitchViaApi(page, user.alternateName);

				await assetsPage.gotoAll();

				await expect(assetsPage.getItem(file1Title)).toBeVisible();

				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: file1Title,
				});

				await expect(
					page.getByRole('heading', {name: file1Title})
				).toBeVisible();

				await infoPanelPage.selectTab('Categorization').click();
			});

			await test.step('Check that space member can see tags and vocabulary but cannot edit them', async () => {
				await expect(tagLabel).toBeAttached();
				await expect(categoryLabel).toBeAttached();
				await expect(personaLabel).toBeAttached();
				await expect(stageLabel).toBeAttached();
				await expect(
					page.getByLabel(tagName).getByLabel('Close')
				).toBeDisabled();
				await expect(
					page.getByLabel(categoryName).getByLabel('Close')
				).toBeDisabled();
				await expect(
					page.getByLabel(personaName).getByLabel('Close')
				).toBeDisabled();
				await expect(
					page.getByLabel(stageName).getByLabel('Close')
				).toBeDisabled();
			});
		}
		finally {
			await performUserSwitchViaApi(page, 'test');

			if (objectEntry?.id) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					applicationName,
					String(objectEntry.id)
				);
			}

			await apiHelpers.headlessAdminTaxonomy.deleteTaxonomyVocabulary(
				vocabularyId
			);
		}
	}
);

test(
	'Info Panel Categories tab for file type asset',
	{tag: '@LPD-68491'},
	async ({apiHelpers, assetsPage, infoPanelPage, page}) => {
		const categoryName = getRandomString();
		const fileTitle = `title ${getRandomString()}`;
		const tagName = getRandomString();

		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64:
						'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVR42mNkqAcAAIUAgUW0RjgAAAAASUVORK5CYII=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			'cms/basic-documents',
			'Default'
		);

		apiHelpers.data.push({
			id: objectEntry.id,
			type: 'document',
		});

		const vocabularyId = await apiHelpers.headlessAdminTaxonomy
			.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1, name: 'All Spaces'}],
				assetTypes: [
					{
						required: false,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name: getRandomString(),
				siteId,
				visibilityType: 'PUBLIC',
			})
			.then((response) => response.id);

		apiHelpers.data.push({
			id: vocabularyId,
			type: 'taxonomyVocabulary',
		});

		await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
			{
				name: categoryName,
				vocabularyId,
			}
		);

		// Go to All Assets and open the Info Panel Categorization Tab

		await assetsPage.gotoAll();

		await assetsPage.execItemAction({
			action: 'Show Details',
			filter: fileTitle,
		});

		await expect(
			page.getByRole('heading', {name: fileTitle})
		).toBeVisible();

		await infoPanelPage.selectTab('Categorization').click();

		// Add a new tag to the file

		const tagsAutocomplete = page.getByPlaceholder('Add tag');

		await tagsAutocomplete.fill(tagName);

		const newTagOption = page.getByRole('option', {
			name: 'Create New Tag:',
		});

		await newTagOption.waitFor();
		await newTagOption.click();

		const tagLabel = page.locator('.label-item', {hasText: tagName});

		await expect(tagLabel).toBeAttached();

		// Add a new category to the file

		const categoriesAutocomplete = page.getByPlaceholder('Add category');

		await categoriesAutocomplete.fill(categoryName);

		const option = page.getByRole('option', {name: categoryName});

		await option.waitFor();
		await option.click();

		const categoryLabel = page.locator('.label-item', {
			hasText: categoryName,
		});

		await expect(categoryLabel).toBeAttached();
	}
);

test(
	'Info Panel Categories tab generates a new Asset Version',
	{tag: '@LPD-83267'},
	async ({apiHelpers, assetsPage, infoPanelPage, page}) => {
		const applicationName = 'cms/basic-documents';
		let categoryLabel;
		const categoryName = `category ${getRandomString()}`;
		const file1Title = `title ${getRandomString()}`;
		let objectEntry;
		const spaceName = `Space ${getRandomString()}`;
		let spaceExternalReferenceCode: string;
		const vocabularyName = `vocabulary ${getRandomString()}`;

		await test.step('Create a new Space', async () => {
			const space =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: spaceName,
					settings: {},
					type: 'Space',
				});
			spaceExternalReferenceCode = space.externalReferenceCode;
		});

		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const vocabularyId = await apiHelpers.headlessAdminTaxonomy
			.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1}],
				assetTypes: [
					{
						required: false,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name: vocabularyName,
				siteId,
				visibilityType: 'PUBLIC',
			})
			.then((response) => response.id);

		await apiHelpers.headlessAdminTaxonomy
			.postTaxonomyVocabularyTaxonomyCategory({
				name: categoryName,
				vocabularyId,
			})
			.then((response) => response.id);

		try {
			objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64:
							'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVR42mNkqAcAAIUAgUW0RjgAAAAASUVORK5CYII=',
						name: file1Title,
					},
					title: file1Title,
				},
				applicationName,
				spaceName
			);

			await test.step('Go to All Assets and open the Info Panel Categorization Tab', async () => {
				await assetsPage.gotoAll();

				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: file1Title,
				});

				await expect(
					page.getByRole('heading', {name: file1Title})
				).toBeVisible();

				await infoPanelPage.selectTab('Categorization').click();
			});

			await test.step('Add a new category to the file', async () => {
				const categoriesAutocomplete =
					page.getByPlaceholder('Add category');

				await categoriesAutocomplete.fill(categoryName);

				const option = page.getByRole('option', {name: categoryName});

				await option.waitFor();
				await option.click();

				categoryLabel = page.locator('.label-item', {
					hasText: categoryName,
				});

				await expect(categoryLabel).toBeAttached();
			});

			await test.step('Validate new version is generated', async () => {
				await assetsPage.execItemAction({
					action: 'View History',
					filter: file1Title,
				});

				await expect(
					page.getByRole('heading', {name: `"${file1Title}" History`})
				).toBeVisible();

				await page
					.getByRole('button', {exact: true, name: file1Title})
					.first()
					.click();

				await expect(
					page.getByRole('heading', {
						name: `${file1Title} (Version 2)`,
					})
				).toBeVisible();
			});
		}
		finally {
			if (objectEntry?.id) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					applicationName,
					String(objectEntry.id)
				);
			}

			await apiHelpers.headlessAdminTaxonomy.deleteTaxonomyVocabulary(
				vocabularyId
			);

			if (spaceExternalReferenceCode) {
				await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
					spaceExternalReferenceCode
				);
			}
		}
	}
);
