/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {searchExperiencesPagesTest} from '../../../../fixtures/searchExperiencesPageTest';
import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {getRandomInt} from '../../../../utils/getRandomInt';
import {getTempDir} from '../../../../utils/temp';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	searchExperiencesPagesTest
);

const CONFIGURATION = {
	advancedConfiguration: {},
	aggregationConfiguration: {aggs: {}},
	generalConfiguration: {
		clauseContributorsExcludes: [],
		clauseContributorsIncludes: ['*'],
		searchableAssetTypes: ['com.liferay.blogs.model.BlogsEntry'],
	},
	highlightConfiguration: {},
	parameterConfiguration: {},
	queryConfiguration: {applyIndexerClauses: false},
	sortConfiguration: {},
};

async function assertConfigurationPreserved(
	apiHelpers: ApiHelpers,
	id: string
) {
	const {configuration} =
		await apiHelpers.searchExperiences.getSXPBlueprint(id);

	const {aggregationConfiguration, generalConfiguration, queryConfiguration} =
		configuration as {
			aggregationConfiguration: {aggs?: object};
			generalConfiguration: {searchableAssetTypes: string[]};
			queryConfiguration: {applyIndexerClauses: boolean};
		};

	expect(generalConfiguration.searchableAssetTypes).toContain(
		CONFIGURATION.generalConfiguration.searchableAssetTypes[0]
	);

	expect(queryConfiguration.applyIndexerClauses).toBe(
		CONFIGURATION.queryConfiguration.applyIndexerClauses
	);

	expect(aggregationConfiguration).toHaveProperty('aggs');
}

test.describe('Blueprint Duplication', () => {
	let title: string;

	test.beforeEach(async ({apiHelpers, sxpBlueprintsAndElementsViewPage}) => {
		await test.step('Create a configured blueprint with API', async () => {
			title = `Blueprint${getRandomInt()}`;

			await apiHelpers.searchExperiences.createSXPBlueprint({
				configuration: CONFIGURATION,
				title,
			});
		});

		await test.step('Navigate to the blueprints admin', async () => {
			await sxpBlueprintsAndElementsViewPage.goto();
		});
	});

	test('Copying a blueprint preserves its configuration', async ({
		apiHelpers,
		editSXPBlueprintPage,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		await test.step('Copy the blueprint', async () => {
			await sxpBlueprintsAndElementsViewPage.selectTableMenuOption(
				title,
				'Copy'
			);
		});

		await test.step('Open the copy and assert its configuration', async () => {
			await sxpBlueprintsAndElementsViewPage.selectTableLink(
				`Copy of ${title}`
			);

			const copyId = await editSXPBlueprintPage.getSXPBlueprintId();

			expect(copyId).toBeTruthy();

			apiHelpers.data.push({id: copyId, type: 'sxpBlueprint'});

			await assertConfigurationPreserved(apiHelpers, copyId!);
		});
	});

	test('Exporting and importing a blueprint preserves its configuration', async ({
		apiHelpers,
		editSXPBlueprintPage,
		page,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		let exportPath: string;

		await test.step('Export the blueprint to a file', async () => {
			const downloadPromise = page.waitForEvent('download');

			await sxpBlueprintsAndElementsViewPage.selectTableMenuOption(
				title,
				'Export'
			);

			const download = await downloadPromise;

			exportPath = getTempDir() + download.suggestedFilename();

			await download.saveAs(exportPath);
		});

		await test.step('Delete the original blueprint', async () => {
			page.once('dialog', (dialog) => dialog.accept());

			await sxpBlueprintsAndElementsViewPage.selectTableMenuOption(
				title,
				'Delete'
			);

			await expect(
				sxpBlueprintsAndElementsViewPage.blueprintElementTable.getByRole(
					'link',
					{name: title}
				)
			).toBeHidden();
		});

		await test.step('Import the exported file', async () => {
			await page.getByLabel('Options').click();

			await page.getByRole('menuitem', {name: 'Import'}).click();

			const importModal = page
				.getByRole('dialog')
				.filter({hasText: 'Import'});

			await importModal
				.locator('input[type="file"]')
				.setInputFiles(exportPath);

			await importModal.getByRole('button', {name: 'Import'}).click();

			await expect(importModal).toBeHidden();
		});

		await test.step('Open the imported blueprint and assert its configuration', async () => {
			await sxpBlueprintsAndElementsViewPage.selectTableLink(title);

			const importedId = await editSXPBlueprintPage.getSXPBlueprintId();

			expect(importedId).toBeTruthy();

			apiHelpers.data.push({id: importedId, type: 'sxpBlueprint'});

			await assertConfigurationPreserved(apiHelpers, importedId!);
		});
	});
});
