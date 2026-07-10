/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {searchExperiencesPagesTest} from '../../../../fixtures/searchExperiencesPageTest';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	searchExperiencesPagesTest
);

test.use({permissions: ['clipboard-read', 'clipboard-write']});

async function assertCopiedFromModal(
	page: Page,
	modalTitle: string,
	expectedText: string
) {
	const modal = page.getByRole('dialog').filter({hasText: modalTitle});

	await expect(modal).toBeVisible();

	await modal.getByRole('button', {name: 'Copy to Clipboard'}).click();

	await expect(async () => {
		expect(
			await page.evaluate(() => navigator.clipboard.readText())
		).toContain(expectedText);
	}).toPass({timeout: 10000});
}

test.describe('Elements JSON', () => {
	test('Copies element JSON from the query builder', async ({
		apiHelpers,
		editSXPBlueprintPage,
		page,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		await test.step('Create a blueprint and open its editor', async () => {
			const {title} =
				await apiHelpers.searchExperiences.createSXPBlueprint();

			await sxpBlueprintsAndElementsViewPage.goto();

			await sxpBlueprintsAndElementsViewPage.selectTableLink(title);
		});

		await test.step('Add a query element', async () => {
			await editSXPBlueprintPage.addQueryElement(
				'Boost All Keywords Match'
			);
		});

		await test.step('Open the element JSON modal and copy it', async () => {
			const elementCard = editSXPBlueprintPage.querySXPElements
				.locator('[id*="querySXPElement"]')
				.filter({hasText: 'Boost All Keywords Match'});

			await editSXPBlueprintPage.closeSidebars();

			await elementCard.getByRole('button', {name: /dropdown/i}).click();

			await page
				.getByRole('menuitem', {name: 'View Element JSON'})
				.click();

			await assertCopiedFromModal(page, 'Element JSON', 'clauses');
		});
	});

	test('Copies element JSON from the edit element preview', async ({
		page,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		await test.step('Open a built-in element in the Elements tab', async () => {
			await sxpBlueprintsAndElementsViewPage.goto();

			await sxpBlueprintsAndElementsViewPage.goToElementsTab();

			await sxpBlueprintsAndElementsViewPage.selectTableLink(
				'Text Match Over Multiple Fields'
			);
		});

		await test.step('Open the element JSON modal from the preview and copy it', async () => {
			await page.getByRole('button', {name: 'Preview'}).click();

			await page.getByRole('button', {name: /dropdown/i}).click();

			await page
				.getByRole('menuitem', {name: 'View Element JSON'})
				.click();

			await assertCopiedFromModal(
				page,
				'Element JSON',
				'queryConfiguration'
			);
		});
	});
});

test.describe('Blueprints Preview', () => {
	test.beforeEach(
		async ({
			apiHelpers,
			editSXPBlueprintPage,
			sxpBlueprintsAndElementsViewPage,
		}) => {
			await test.step('Create a blueprint and open its preview', async () => {
				const {title} =
					await apiHelpers.searchExperiences.createSXPBlueprint();

				await sxpBlueprintsAndElementsViewPage.goto();

				await sxpBlueprintsAndElementsViewPage.selectTableLink(title);

				await editSXPBlueprintPage.openPreviewSidebar();

				await editSXPBlueprintPage.searchInPreviewSidebar('test');
			});
		}
	);

	test('Copies the raw request @LPS-165923', async ({
		editSXPBlueprintPage,
		page,
	}) => {
		await editSXPBlueprintPage.previewSidebar
			.getByRole('button', {name: 'View Raw Request'})
			.click();

		await assertCopiedFromModal(page, 'Raw Request', 'hits');
	});

	test('Copies the raw response', async ({editSXPBlueprintPage, page}) => {
		await editSXPBlueprintPage.previewSidebar
			.getByRole('button', {name: 'View Raw Response'})
			.click();

		await assertCopiedFromModal(page, 'Raw Response', 'hits');
	});

	test('Copies the score explanation', async ({page}) => {
		await page
			.getByTestId('previewSidebarResultListItem')
			.first()
			.locator('button.score')
			.click();

		await assertCopiedFromModal(
			page,
			'Score Explanation',
			'PerFieldSimilarity'
		);
	});
});
