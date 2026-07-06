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
import {assertNewPageTitle} from '../utils/assertNewPageTitle';

export const test = mergeTests(
	isolatedLayoutTest({type: 'portlet'}),
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	pageEditorPagesTest,
	loginTest(),
	searchPageTest,
	searchExperiencesPagesTest
);

test.describe('Working Links', () => {
	test('Blueprint and element editor have working links @LPS-147066', async ({
		apiHelpers,
		context,
		editSXPBlueprintPage,
		page,
		sxpBlueprintsAndElementsViewPage,
	}) => {
		const sxpBlueprint =
			await apiHelpers.searchExperiences.createSXPBlueprint();

		await test.step('Navigate to created blueprint', async () => {
			await sxpBlueprintsAndElementsViewPage.goto();

			await sxpBlueprintsAndElementsViewPage.selectTableLink(
				sxpBlueprint.title
			);
		});

		await test.step('Check sidebar link', async () => {
			await editSXPBlueprintPage.addSXPElementSidebar
				.getByRole('link', {name: 'Learn more. (Opens a new window)'})
				.click();

			await assertNewPageTitle(
				context,
				'Search Blueprints Elements Reference'
			);
		});

		await test.step('Check source - indexer clauses', async () => {
			await page
				.getByRole('button', {name: 'Search Framework Indexer'})
				.locator('.lexicon-icon-question-circle')
				.click();

			await expect(editSXPBlueprintPage.infoSidebar).toBeVisible();

			await editSXPBlueprintPage.infoSidebar
				.getByRole('link', {
					name: 'Learn more. (Opens a new window)',
				})
				.click();

			await assertNewPageTitle(
				context,
				'Creating and Managing Search Blueprints'
			);
		});

		await test.step('Check source - query contributors', async () => {
			await page
				.getByRole('button', {
					name: 'Search Framework Query Contributors',
				})
				.locator('.lexicon-icon-question-circle')
				.click();

			await expect(editSXPBlueprintPage.infoSidebar).toBeVisible();

			await editSXPBlueprintPage.infoSidebar
				.getByRole('link', {
					name: 'Learn more. (Opens a new window)',
				})
				.click();

			await assertNewPageTitle(
				context,
				'Creating and Managing Search Blueprints'
			);
		});

		await test.step('Check Configuration tab', async () => {
			await editSXPBlueprintPage.goToConfigurationTab();

			const configurationTexts = [
				'Enter additional blueprints',
				'Add aggregations to those',
				'Override the default search',
				'Add sorts to those already',
				'Declare new template',
				'Add source includes and',
			];

			for (const text of configurationTexts) {
				await page
					.locator('.sheet-text', {
						has: page.getByText(text),
					})
					.getByRole('link')
					.click();

				await assertNewPageTitle(
					context,
					'Search Blueprints Configuration Reference'
				);
			}
		});

		await test.step('Go to elements', async () => {
			await editSXPBlueprintPage.cancelBlueprint();

			await sxpBlueprintsAndElementsViewPage.goToElementsTab();

			await sxpBlueprintsAndElementsViewPage.selectTableLink(
				'Text Match Over Multiple Fields'
			);
		});

		await test.step('Check info sidebar', async () => {
			await page.getByLabel('Info').click();

			await editSXPBlueprintPage.infoSidebar
				.getByRole('link', {
					name: 'Learn more. (Opens a new window)',
				})
				.click();

			await assertNewPageTitle(context, 'Creating Elements');
		});
	});

	test('Blueprint options portlet has working link @LPS-147066', async ({
		context,
		layout,
		page,
		searchPage,
	}) => {
		await test.step('Add search blueprint options to new page', async () => {
			await page.goto('/web/guest' + layout.friendlyURL);

			await searchPage.addPortlet('Blueprints Options', 'Search');
		});

		await test.step('Open blueprint options configuration modal', async () => {
			await page
				.getByRole('button', {
					name: 'Select a blueprint to make it visible.',
				})
				.click();
		});

		await test.step('Check blueprint link for search page', async () => {
			await page
				.frameLocator('iframe[id="modalIframe"]')
				.getByRole('link', {
					name: 'Learn how to use blueprints',
				})
				.click();

			await assertNewPageTitle(
				context,
				'Using a Search Blueprint on a Search Page'
			);
		});
	});
});
