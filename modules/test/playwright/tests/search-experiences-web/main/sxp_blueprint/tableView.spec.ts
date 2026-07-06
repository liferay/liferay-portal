/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {searchExperiencesPagesTest} from '../../../../fixtures/searchExperiencesPageTest';

export const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	searchExperiencesPagesTest
);

test.describe('Table Fields Selection', () => {
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
