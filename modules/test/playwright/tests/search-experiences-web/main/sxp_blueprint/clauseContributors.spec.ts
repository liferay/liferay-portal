/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {searchExperiencesPagesTest} from '../../../../fixtures/searchExperiencesPageTest';
import {getRandomInt} from '../../../../utils/getRandomInt';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	searchExperiencesPagesTest
);

test.describe('Data Persistence', () => {
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
			await editSXPBlueprintPage.expandPanel(
				'Search Framework Query Contributors'
			);

			await editSXPBlueprintPage.selectSourceRadioProperty('Enable All');

			await editSXPBlueprintPage.saveBlueprint();
		});

		await test.step('Check that "Enable All" setting persists', async () => {
			await sxpBlueprintsAndElementsViewPage.selectTableLink(
				sxpBlueprint.title
			);

			await editSXPBlueprintPage.expandPanel(
				'Search Framework Query Contributors'
			);

			await editSXPBlueprintPage.assertSourceRadioPropertySelection(
				'Enable All'
			);
		});
	});

	test('Saving "Disable All" clause contributors persists @LPD-22974', async ({
		editSXPBlueprintPage,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		await test.step('Set clause contributors to "Disable All"', async () => {
			await editSXPBlueprintPage.expandPanel(
				'Search Framework Query Contributors'
			);

			await editSXPBlueprintPage.selectSourceRadioProperty('Disable All');

			await editSXPBlueprintPage.saveBlueprint();
		});

		await test.step('Check that "Disable All" setting persists', async () => {
			await sxpBlueprintsAndElementsViewPage.selectTableLink(
				sxpBlueprint.title
			);

			await editSXPBlueprintPage.expandPanel(
				'Search Framework Query Contributors'
			);

			await editSXPBlueprintPage.assertSourceRadioPropertySelection(
				'Disable All'
			);
		});
	});

	test('Saving "Customize - All Enabled" clause contributors persists @LPD-22974', async ({
		editSXPBlueprintPage,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		await test.step('Set clause contributors to "Customize"', async () => {
			await editSXPBlueprintPage.expandPanel(
				'Search Framework Query Contributors'
			);

			await editSXPBlueprintPage.selectSourceRadioProperty('Customize');
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

			await editSXPBlueprintPage.expandPanel(
				'Search Framework Query Contributors'
			);

			await editSXPBlueprintPage.assertSourceRadioPropertySelection(
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
			await editSXPBlueprintPage.expandPanel(
				'Search Framework Query Contributors'
			);

			await editSXPBlueprintPage.selectSourceRadioProperty('Customize');
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

			await editSXPBlueprintPage.expandPanel(
				'Search Framework Query Contributors'
			);

			await editSXPBlueprintPage.assertSourceRadioPropertySelection(
				'Disable All'
			);
		});
	});

	test('Saving "Customize - Varied" clause contributors persists @LPD-22974', async ({
		editSXPBlueprintPage,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		await test.step('Set clause contributors to "Customize"', async () => {
			await editSXPBlueprintPage.expandPanel(
				'Search Framework Query Contributors'
			);

			await editSXPBlueprintPage.selectSourceRadioProperty('Customize');
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

			await editSXPBlueprintPage.expandPanel(
				'Search Framework Query Contributors'
			);

			await editSXPBlueprintPage.assertSourceRadioPropertySelection(
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

test.describe('Search Preview', () => {
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
			await editSXPBlueprintPage.expandPanel(
				'Search Framework Query Contributors'
			);

			await editSXPBlueprintPage.selectSourceRadioProperty('Enable All');
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
			await editSXPBlueprintPage.expandPanel(
				'Search Framework Query Contributors'
			);

			await editSXPBlueprintPage.selectSourceRadioProperty('Disable All');
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
			await editSXPBlueprintPage.expandPanel(
				'Search Framework Query Contributors'
			);

			await editSXPBlueprintPage.selectSourceRadioProperty('Customize');
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
			await editSXPBlueprintPage.expandPanel(
				'Search Framework Query Contributors'
			);

			await editSXPBlueprintPage.selectSourceRadioProperty('Customize');
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

test.describe('Manual Creation', () => {
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
			await editSXPBlueprintPage.expandPanel(
				'Search Framework Query Contributors'
			);

			await editSXPBlueprintPage.assertSourceRadioPropertySelection(
				'Enable All'
			);
		});
	});
});
