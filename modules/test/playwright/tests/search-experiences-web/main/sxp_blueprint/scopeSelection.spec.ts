/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../../fixtures/isolatedLayoutTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {searchExperiencesPagesTest} from '../../../../fixtures/searchExperiencesPageTest';
import {searchPageTest} from '../../../../fixtures/searchPageTest';
import {DEFAULT_SXP_BLUEPRINT_CONFIGURATION} from '../../../../helpers/SearchExperiencesApiHelper';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../../utils/structured-content/getBasicWebContentStructureId';

export const test = mergeTests(
	isolatedLayoutTest({type: 'portlet'}),
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true}, // Headless Site Page API
	}),
	isolatedSiteTest,
	pageEditorPagesTest,
	loginTest(),
	searchPageTest,
	searchExperiencesPagesTest
);

test.describe('Asset Library/Space Scope', () => {
	let assetLibrary = null;
	let space = null;

	test.beforeEach(async ({apiHelpers}) => {
		await test.step('Create new asset libraries', async () => {
			assetLibrary =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: `Asset Library ${getRandomString()}`,
					settings: {},
					type: 'AssetLibrary',
				});

			space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: `Space ${getRandomString()}`,
				settings: {},
				type: 'Space',
			});
		});
	});

	test('Scope selection persists after saving blueprint', async ({
		apiHelpers,
		editSXPBlueprintPage,
		page,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		let sxpBlueprint: SXPBlueprint;

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

		await test.step('Select asset libraries for the scope', async () => {
			await editSXPBlueprintPage.selectScope({
				label: assetLibrary.name,
				tab: 'Asset Libraries',
			});

			await editSXPBlueprintPage.selectScope({
				label: space.name,
				tab: 'Spaces',
			});
		});

		await test.step('Save blueprint and redirect back to it', async () => {
			await editSXPBlueprintPage.saveBlueprint();

			await sxpBlueprintsAndElementsViewPage.selectTableLink(
				sxpBlueprint.title
			);
		});

		await test.step('Assert the scope selections saved', async () => {
			await expect(
				page
					.locator('.scope-selector tr')
					.filter({
						has: page.getByRole('cell', {name: assetLibrary.name}),
					})
					.filter({hasText: 'Asset Library'})
					.filter({hasText: 'Active'})
			).toBeVisible();

			await expect(
				page
					.locator('.scope-selector tr')
					.filter({has: page.getByRole('cell', {name: space.name})})
					.filter({hasText: 'Space'})
					.filter({hasText: 'Active'})
			).toBeVisible();
		});
	});
});

test.describe('Site Scope', () => {
	let site1: any;
	let site2: any;

	test.beforeEach(async ({apiHelpers}) => {
		site1 = await apiHelpers.headlessAdminSite.postSite({
			name: `Site1 ${getRandomInt()}`,
		});

		site2 = await apiHelpers.headlessAdminSite.postSite({
			name: `Site2 ${getRandomInt()}`,
		});
	});

	test('Scope selection persists after saving blueprint', async ({
		apiHelpers,
		editSXPBlueprintPage,
		page,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		let sxpBlueprint: SXPBlueprint;

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

		await test.step('Select site1 for the scope', async () => {
			await editSXPBlueprintPage.selectScope({
				label: site1.name,
				tab: 'My Sites',
			});
		});

		await test.step('Save blueprint and redirect back to it', async () => {
			await editSXPBlueprintPage.saveBlueprint();

			await sxpBlueprintsAndElementsViewPage.selectTableLink(
				sxpBlueprint.title
			);
		});

		await test.step('Assert the scope selections saved', async () => {
			await expect(
				page
					.locator('.scope-selector tr')
					.filter({has: page.getByRole('cell', {name: site1.name})})
					.filter({hasText: 'Site'})
					.filter({hasText: 'Active'})
			).toBeVisible();
		});
	});

	test('Sites with content are shown in blueprint preview', async ({
		apiHelpers,
		editSXPBlueprintPage,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		let sxpBlueprint: SXPBlueprint;

		await test.step('Create content for two sites', async () => {
			const basicWebContentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site1.id,
				titleMap: {en_US: `Web Content for ${site1.name}`},
			});
		});

		await test.step('Create blueprint with API scoped to the first site', async () => {
			sxpBlueprint =
				await apiHelpers.searchExperiences.createSXPBlueprint({
					configuration: {
						...DEFAULT_SXP_BLUEPRINT_CONFIGURATION,
						generalConfiguration: {
							...DEFAULT_SXP_BLUEPRINT_CONFIGURATION.generalConfiguration,
							scope: [
								site1.externalReferenceCode,
								site2.externalReferenceCode,
							],
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

		await test.step('Verify web content is shown in blueprint preview', async () => {
			await editSXPBlueprintPage.openPreviewSidebar();

			await editSXPBlueprintPage.searchInPreviewSidebar(site1.name);

			await editSXPBlueprintPage.assertPreviewSidebarSearchResult(
				`Web Content for ${site1.name}`,
				[
					{
						label: 'entryClassName',
						value: 'com.liferay.journal.model.JournalArticle',
					},
				]
			);
		});

		await test.step('Remove first site from blueprint scope', async () => {
			await editSXPBlueprintPage.closeSidebars();

			await editSXPBlueprintPage.removeScope({label: site1.name});

			await editSXPBlueprintPage.openPreviewSidebar();
		});

		await test.step('Verify web content is no longer shown in blueprint preview', async () => {
			await editSXPBlueprintPage.previewSidebar
				.getByLabel('Refresh')
				.click();

			await expect(
				editSXPBlueprintPage.previewSidebar.getByText(
					'No Results Found'
				)
			).toBeVisible();
		});
	});
});
