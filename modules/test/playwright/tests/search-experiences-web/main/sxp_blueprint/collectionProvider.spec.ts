/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {searchExperiencesPagesTest} from '../../../../fixtures/searchExperiencesPageTest';
import {DEFAULT_SXP_BLUEPRINT_CONFIGURATION} from '../../../../helpers/SearchExperiencesApiHelper';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import getDataStructureDefinition from '../../../journal-web/main/utils/getDataStructureDefinition';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	searchExperiencesPagesTest
);

test.describe('Blueprint as a Collection Provider', () => {
	test('Setup a blueprint with web content subtype as a collection provider', async ({
		apiHelpers,
		editSXPBlueprintPage,
		page,
		pageEditorPage,
		site,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		let layout;
		let sxpBlueprint: SXPBlueprint;

		const fieldName = `CustomTextField${getRandomInt()}`;
		const structureName = `WebContentStructure${getRandomInt()}`;

		await test.step('Create a web content structure with a custom text field', async () => {
			await apiHelpers.dataEngine.createStructure(
				site.id,
				getDataStructureDefinition({
					defaultLanguageId: 'en_US',
					fields: [
						{fieldType: 'text', name: fieldName, repeatable: true},
					],
					name: structureName,
				})
			);
		});

		await test.step('Create a blueprint with collection provider enabled', async () => {
			sxpBlueprint =
				await apiHelpers.searchExperiences.createSXPBlueprint({
					configuration: {
						...DEFAULT_SXP_BLUEPRINT_CONFIGURATION,
						generalConfiguration: {
							...DEFAULT_SXP_BLUEPRINT_CONFIGURATION.generalConfiguration,
							collectionProvider: true,
						},
					},
				});
		});

		await test.step('Navigate to created blueprint', async () => {
			await sxpBlueprintsAndElementsViewPage.goto();

			await sxpBlueprintsAndElementsViewPage.selectTableLink(
				sxpBlueprint.title
			);
		});

		await test.step('Add the custom web content structure to blueprint subtypes', async () => {
			await editSXPBlueprintPage.expandPanel(
				'Searchable Types and Subtypes'
			);

			await editSXPBlueprintPage.selectSourceRadioProperty(
				'Selected Types'
			);

			await editSXPBlueprintPage.selectAssetTypes([
				'Web Content Article',
			]);

			await editSXPBlueprintPage.selectAssetSubtypes(
				[structureName],
				'Web Content Article'
			);

			await editSXPBlueprintPage.saveBlueprint();
		});

		await test.step('Create a content page with the collection display fragment', async () => {
			layout = await apiHelpers.headlessDelivery.createSitePage({
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await pageEditorPage.addFragment(
				'Content Display',
				'Collection Display'
			);
		});

		await test.step('Select the blueprint as the collection provider', async () => {
			await pageEditorPage.chooseCollectionDisplayCollection(
				'Collection Providers',
				sxpBlueprint.title
			);

			await pageEditorPage.waitForChangesSaved();
		});

		await test.step('Add an HTML fragment to the collection display fragment', async () => {
			await pageEditorPage.addFragment(
				'Basic Components',
				'HTML',
				page.locator('.page-editor__collection-item.empty').first()
			);
		});

		await test.step('Open the sidebar to edit HTML fragment', async () => {
			const htmlFragmentId = await pageEditorPage.getFragmentId('HTML');

			await pageEditorPage.selectEditable(htmlFragmentId, 'element-html');
		});

		await test.step('Assert the type and subtype are correct (as established from blueprint)', async () => {
			await expect(
				page.locator('.page-editor__mapping-panel__type-label', {
					hasText: /Content Type/,
				})
			).toHaveText(/Web Content Article/);

			await expect(
				page.locator('.page-editor__mapping-panel__type-label', {
					hasText: /Subtype/,
				})
			).toHaveText(new RegExp(structureName));
		});

		await test.step('Map the custom text field to the HTML fragment', async () => {
			await page
				.getByLabel('Field', {exact: true})
				.selectOption(`DDMStructure_${fieldName}`);

			await pageEditorPage.waitForChangesSaved();
		});

		await test.step('Check that several basic fields are also available to map', async () => {
			['Title', 'Description', 'Publish Date', 'Author Name'].forEach(
				async (field) => {
					expect(
						page
							.getByLabel('Field', {exact: true})
							.locator('option', {hasText: field})
					).toBeDefined();
				}
			);
		});
	});
});
