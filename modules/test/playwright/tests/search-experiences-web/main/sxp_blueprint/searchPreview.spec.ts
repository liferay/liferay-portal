/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {searchExperiencesPagesTest} from '../../../../fixtures/searchExperiencesPageTest';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getBasicWebContentStructureId from '../../../../utils/structured-content/getBasicWebContentStructureId';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	searchExperiencesPagesTest
);

test.describe('SXP Elements', () => {
	test('Searching on preview with attributes is accurate', async ({
		apiHelpers,
		editSXPBlueprintPage,
		page,
		site,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		let categoryId: number;
		let sxpBlueprint: SXPBlueprint;

		const categoryName = `Category ${getRandomInt()}`;
		const vocabularyName = `Vocabulary ${getRandomInt()}`;

		await test.step('Create a vocabulary + category with API', async () => {
			const {id: vocabularyId} =
				await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary(
					{
						name: vocabularyName,
						siteId: site.id,
					}
				);

			const {id} =
				await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
					{
						name: categoryName,
						vocabularyId,
					}
				);

			categoryId = id;

			apiHelpers.data.push({
				id: vocabularyId,
				type: 'taxonomyVocabulary',
			});
		});

		await test.step('Create web contents, one connected to the category', async () => {
			const basicWebContentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			await apiHelpers.jsonWebServicesJournal.addWebContent({
				content: 'apple',
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				serviceContext: {
					assetCategoryIds: [categoryId],
				},
				titleMap: {en_US: 'gala'},
			});

			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: 'apple apple apple'},
			});
		});

		await test.step('Create blueprint with API', async () => {
			sxpBlueprint =
				await apiHelpers.searchExperiences.createSXPBlueprint();
		});

		await test.step('Navigate to created blueprint', async () => {
			await sxpBlueprintsAndElementsViewPage.goto();

			await sxpBlueprintsAndElementsViewPage.selectTableLink(
				sxpBlueprint.title
			);
		});

		await test.step('Add element to blueprint', async () => {
			await editSXPBlueprintPage.addQueryElement(
				'Boost Contents in a Category'
			);
		});

		await test.step('Configure element with new category and high boost', async () => {
			await editSXPBlueprintPage.querySXPElements
				.getByRole('combobox', {name: 'Asset Category External'})
				.fill(categoryName);

			await clickAndExpectToBeVisible({
				target: page.getByRole('gridcell', {
					name: new RegExp(`^${categoryName} \\(ERC:`),
				}),
				trigger: page.getByRole('option', {name: categoryName}),
			});

			await editSXPBlueprintPage.querySXPElements
				.getByLabel('Boost')
				.fill('2000');
		});

		await test.step('Search for "apple" in preview sidebar with attributes', async () => {
			await editSXPBlueprintPage.openPreviewSidebar();

			await editSXPBlueprintPage.addPreviewAttributes([
				{
					key: 'search.experiences.scope.group.id',
					value: String(site.id),
				},
			]);

			await editSXPBlueprintPage.searchInPreviewSidebar('apple');
		});

		await test.step('Assert the two web contents in preview results', async () => {
			await editSXPBlueprintPage.assertPreviewSidebarSearchResult(
				'gala',
				[
					{
						label: 'entryClassName',
						value: 'com.liferay.journal.model.JournalArticle',
					},
				]
			);

			await editSXPBlueprintPage.assertPreviewSidebarSearchResult(
				'apple apple apple',
				[
					{
						label: 'entryClassName',
						value: 'com.liferay.journal.model.JournalArticle',
					},
				]
			);
		});

		await test.step('Assert "gala" is the first result', async () => {
			await expect(
				page
					.getByTestId('previewSidebarResultListItem')
					.nth(0)
					.filter({has: page.getByText('gala')})
			).toBeVisible();
		});
	});
});
