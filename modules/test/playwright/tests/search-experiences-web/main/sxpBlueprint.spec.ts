/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {searchExperiencesPagesTest} from '../../../fixtures/searchExperiencesPageTest';
import {DEFAULT_SXP_BLUEPRINT_CONFIGURATION} from '../../../helpers/SearchExperiencesApiHelper';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-129412': {enabled: true}, // Collection Providers for Blueprint
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	pageEditorPagesTest,
	loginTest(),
	searchExperiencesPagesTest
);

test.describe('Blueprint table fields can toggle visibility', () => {
	const tableFieldsList = [
		'Description',
		'ID',
		'Author',
		'Created',
		'Modified',
	];

	test.beforeEach(async ({apiHelpers}) => {
		await test.step('Create blueprint with API', async () => {
			await apiHelpers.searchExperiences.createSXPBlueprint();
		});
	});

	test.afterEach(async ({page, sxpBlueprintsAndElementsViewPage}) => {
		await test.step('Select all blueprint table fields to view', async () => {
			for (const tableField of tableFieldsList) {
				const tableFieldMenuItem = page.getByRole('menuitem', {
					exact: true,
					name: tableField,
				});

				if (!(await tableFieldMenuItem.isVisible())) {
					await sxpBlueprintsAndElementsViewPage.blueprintElementTableOpenFieldsMenuButton.click();
				}

				if (
					!(await tableFieldMenuItem
						.locator('.lexicon-icon-check')
						.isVisible())
				) {
					await tableFieldMenuItem.click();
				}

				await expect(
					sxpBlueprintsAndElementsViewPage.blueprintElementTableHeading
				).toContainText(tableField);
			}
		});
	});

	test('Deselect blueprint table fields from view', async ({
		page,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		await test.step('Navigate to created blueprint', async () => {
			await sxpBlueprintsAndElementsViewPage.goto();
		});

		await test.step('Assert blueprint table fields are available', async () => {
			for (const tableField of tableFieldsList) {
				await expect(
					sxpBlueprintsAndElementsViewPage.blueprintElementTable.getByText(
						tableField,
						{exact: true}
					)
				).toBeVisible();
			}
		});

		await test.step('Toggle off blueprint table fields', async () => {
			for (const tableField of tableFieldsList) {
				const tableFieldMenuItem = page.getByRole('menuitem', {
					exact: true,
					name: tableField,
				});

				if (!(await tableFieldMenuItem.isVisible())) {
					await sxpBlueprintsAndElementsViewPage.blueprintElementTableOpenFieldsMenuButton.click();
				}

				await tableFieldMenuItem.click();

				await expect(
					tableFieldMenuItem.locator('.lexicon-icon-check')
				).not.toBeVisible();
			}
		});

		await test.step('Assert blueprint table fields are not available', async () => {
			for (const tableField of tableFieldsList) {
				await expect(
					sxpBlueprintsAndElementsViewPage.blueprintElementTableHeading
				).not.toContainText(tableField);
			}
		});
	});
});

test.describe('Created blueprint has accurate clause contributors', () => {
	let sxpBlueprintId: string;

	test.beforeEach(
		async ({editSXPBlueprintPage, sxpBlueprintsAndElementsViewPage}) => {
			const sxpBlueprintTitle = `Blueprint${getRandomInt()}`;

			await test.step('Create a blueprint via page', async () => {
				await sxpBlueprintsAndElementsViewPage.goto();

				await sxpBlueprintsAndElementsViewPage.createBlueprint(
					sxpBlueprintTitle
				);
			});

			await test.step('Save ID for created blueprint', async () => {
				await expect(editSXPBlueprintPage.editTitleButton).toHaveText(
					sxpBlueprintTitle
				);

				sxpBlueprintId = await editSXPBlueprintPage.getSXPBlueprintId();
			});
		}
	);

	test.afterEach(async ({apiHelpers}) => {
		if (sxpBlueprintId) {
			await apiHelpers.searchExperiences.deleteSXPBlueprint(
				sxpBlueprintId
			);
		}
	});

	test('Newly created blueprint starts with "Enable All" clause contributors @LPD-22974', async ({
		editSXPBlueprintPage,
	}) => {
		await test.step('Confirm clause contributors is set as Enable All', async () => {
			await editSXPBlueprintPage.goToQuerySettingsMenuItem();

			await editSXPBlueprintPage.assertQuerySettingsRadioPropertySelection(
				'Enable All'
			);
		});
	});
});

test.describe('Saved blueprint maintains accurate clause contributors', () => {
	let sxpBlueprint: SXPBlueprint;

	test.beforeEach(async ({apiHelpers, sxpBlueprintsAndElementsViewPage}) => {
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
	});

	test('Saving "Enable All" clause contributors persists @LPD-22974', async ({
		editSXPBlueprintPage,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		await test.step('Set clause contributors to "Enable All"', async () => {
			await editSXPBlueprintPage.goToQuerySettingsMenuItem();

			await editSXPBlueprintPage.selectQuerySettingsRadioProperty(
				'Enable All'
			);

			await editSXPBlueprintPage.saveBlueprint();
		});

		await test.step('Check that "Enable All" setting persists', async () => {
			await sxpBlueprintsAndElementsViewPage.selectTableLink(
				sxpBlueprint.title
			);

			await editSXPBlueprintPage.goToQuerySettingsMenuItem();

			await editSXPBlueprintPage.assertQuerySettingsRadioPropertySelection(
				'Enable All'
			);
		});
	});

	test('Saving "Disable All" clause contributors persists @LPD-22974', async ({
		editSXPBlueprintPage,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		await test.step('Set clause contributors to "Disable All"', async () => {
			await editSXPBlueprintPage.goToQuerySettingsMenuItem();

			await editSXPBlueprintPage.selectQuerySettingsRadioProperty(
				'Disable All'
			);

			await editSXPBlueprintPage.saveBlueprint();
		});

		await test.step('Check that "Disable All" setting persists', async () => {
			await sxpBlueprintsAndElementsViewPage.selectTableLink(
				sxpBlueprint.title
			);

			await editSXPBlueprintPage.goToQuerySettingsMenuItem();

			await editSXPBlueprintPage.assertQuerySettingsRadioPropertySelection(
				'Disable All'
			);
		});
	});

	test('Saving "Customize - All Enabled" clause contributors persists @LPD-22974', async ({
		editSXPBlueprintPage,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		await test.step('Set clause contributors to "Customize"', async () => {
			await editSXPBlueprintPage.goToQuerySettingsMenuItem();

			await editSXPBlueprintPage.selectQuerySettingsRadioProperty(
				'Customize'
			);
		});

		await test.step('Assert all clause contributors are enabled by default', async () => {
			await editSXPBlueprintPage.openClauseContributorsSidebar();

			await editSXPBlueprintPage.assertClauseContributorSelection({
				labels: ['*'],
				value: true,
			});

			await editSXPBlueprintPage.saveBlueprint();
		});

		await test.step('Check that all enabled customized clause contributors persists', async () => {
			await sxpBlueprintsAndElementsViewPage.selectTableLink(
				sxpBlueprint.title
			);

			await editSXPBlueprintPage.goToQuerySettingsMenuItem();

			await editSXPBlueprintPage.assertQuerySettingsRadioPropertySelection(
				'Customize'
			);

			await editSXPBlueprintPage.openClauseContributorsSidebar();

			await editSXPBlueprintPage.assertClauseContributorSelection({
				labels: ['*'],
				value: true,
			});
		});
	});

	test('Saving "Customize - All Disabled" clause contributors is set to "Disable All" @LPD-22974', async ({
		editSXPBlueprintPage,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		await test.step('Set clause contributors to "Customize"', async () => {
			await editSXPBlueprintPage.goToQuerySettingsMenuItem();

			await editSXPBlueprintPage.selectQuerySettingsRadioProperty(
				'Customize'
			);
		});

		await test.step('Disable all contributors', async () => {
			await editSXPBlueprintPage.openClauseContributorsSidebar();

			await editSXPBlueprintPage.selectClauseContributors({
				labels: ['*'],
				value: false,
			});

			await editSXPBlueprintPage.saveBlueprint();
		});

		await test.step('Check that all disabled customized clause contributors persists as "Disable All"', async () => {
			await sxpBlueprintsAndElementsViewPage.selectTableLink(
				sxpBlueprint.title
			);

			await editSXPBlueprintPage.goToQuerySettingsMenuItem();

			await editSXPBlueprintPage.assertQuerySettingsRadioPropertySelection(
				'Disable All'
			);
		});
	});

	test('Saving "Customize - Varied" clause contributors persists @LPD-22974', async ({
		editSXPBlueprintPage,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		await test.step('Set clause contributors to "Customize"', async () => {
			await editSXPBlueprintPage.goToQuerySettingsMenuItem();

			await editSXPBlueprintPage.selectQuerySettingsRadioProperty(
				'Customize'
			);
		});

		await test.step('Vary clause contributors selection', async () => {
			await editSXPBlueprintPage.openClauseContributorsSidebar();

			await editSXPBlueprintPage.selectClauseContributors({
				labels: [
					'Account Entry Keyword Query Contributor',
					'Address Model Pre Filter Contributor',
					'Group Id Query Pre Filter Contributor',
				],
				value: false,
			});

			await editSXPBlueprintPage.saveBlueprint();
		});

		await test.step('Check that customized clause contributors persists', async () => {
			await sxpBlueprintsAndElementsViewPage.selectTableLink(
				sxpBlueprint.title
			);

			await editSXPBlueprintPage.goToQuerySettingsMenuItem();

			await editSXPBlueprintPage.assertQuerySettingsRadioPropertySelection(
				'Customize'
			);

			await editSXPBlueprintPage.openClauseContributorsSidebar();

			await editSXPBlueprintPage.assertClauseContributorSelection({
				labels: [
					'Account Entry Keyword Query Contributor',
					'Address Model Pre Filter Contributor',
					'Group Id Query Pre Filter Contributor',
				],
				value: false,
			});
		});
	});
});

test.describe('Searching in preview with clause contributors is accurate', () => {
	let sxpBlueprint: SXPBlueprint;

	test.beforeEach(async ({apiHelpers, sxpBlueprintsAndElementsViewPage}) => {
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
	});

	test('Searching with "Enable All" clause contributors returns expected result @LPD-22974', async ({
		editSXPBlueprintPage,
	}) => {
		await test.step('Set clause contributors to "Enable All"', async () => {
			await editSXPBlueprintPage.goToQuerySettingsMenuItem();

			await editSXPBlueprintPage.selectQuerySettingsRadioProperty(
				'Enable All'
			);
		});

		await test.step('Search for "tree" in preview sidebar and find tree.png', async () => {
			await editSXPBlueprintPage.openPreviewSidebar();

			await editSXPBlueprintPage.searchInPreviewSidebar('tree');

			await editSXPBlueprintPage.assertPreviewSidebarSearchResult(
				'tree.png',
				[
					{
						label: 'entryClassName',
						value: 'com.liferay.document.library.kernel.model.DLFileEntry',
					},
					{label: 'userName', value: 'test test'},
				]
			);
		});
	});

	test('Searching with "Disable All" clause contributors returns no results @LPD-22974', async ({
		editSXPBlueprintPage,
	}) => {
		await test.step('Set clause contributors to "Disable All"', async () => {
			await editSXPBlueprintPage.goToQuerySettingsMenuItem();

			await editSXPBlueprintPage.selectQuerySettingsRadioProperty(
				'Disable All'
			);
		});

		await test.step('Search for "tree" in preview sidebar and find no results', async () => {
			await editSXPBlueprintPage.openPreviewSidebar();

			await editSXPBlueprintPage.searchInPreviewSidebar('tree');

			await editSXPBlueprintPage.assertPreviewSidebarNoResults();
		});
	});

	test('Searching with "Customize - All Disabled" returns no results @LPD-22974', async ({
		editSXPBlueprintPage,
	}) => {
		await test.step('Set clause contributors to "Customize"', async () => {
			await editSXPBlueprintPage.goToQuerySettingsMenuItem();

			await editSXPBlueprintPage.selectQuerySettingsRadioProperty(
				'Customize'
			);
		});

		await test.step('Disable all clause contributors', async () => {
			await editSXPBlueprintPage.openClauseContributorsSidebar();

			await editSXPBlueprintPage.selectClauseContributors({
				labels: ['*'],
				value: false,
			});
		});

		await test.step('Search for "tree" in preview sidebar and find no results', async () => {
			await editSXPBlueprintPage.openPreviewSidebar();

			await editSXPBlueprintPage.searchInPreviewSidebar('tree');

			await editSXPBlueprintPage.assertPreviewSidebarNoResults();
		});
	});

	test('Searching with only "DL File Entry Keyword Query Contributor" returns expected result @LPD-22974', async ({
		editSXPBlueprintPage,
	}) => {
		await test.step('Set clause contributors to "Customize"', async () => {
			await editSXPBlueprintPage.goToQuerySettingsMenuItem();

			await editSXPBlueprintPage.selectQuerySettingsRadioProperty(
				'Customize'
			);
		});

		await test.step('Enable only "DL File Entry" clause contributor', async () => {
			await editSXPBlueprintPage.openClauseContributorsSidebar();

			await editSXPBlueprintPage.selectClauseContributors({
				labels: ['*'],
				value: false,
			});

			await editSXPBlueprintPage.selectClauseContributors({
				labels: ['DL File Entry Keyword Query Contributor'],
				value: true,
			});
		});

		await test.step('Search for "tree" in preview sidebar and find file tree.png', async () => {
			await editSXPBlueprintPage.openPreviewSidebar();

			await editSXPBlueprintPage.searchInPreviewSidebar('tree');

			await editSXPBlueprintPage.assertPreviewSidebarSearchResult(
				'tree.png',
				[
					{
						label: 'entryClassName',
						value: 'com.liferay.document.library.kernel.model.DLFileEntry',
					},
					{label: 'userName', value: 'test test'},
				]
			);
		});
	});
});

test.describe('Blueprint can be registered as a collection provider', () => {
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
			await editSXPBlueprintPage.goToQuerySettingsMenuItem();

			await editSXPBlueprintPage.selectQuerySettingsRadioProperty(
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
