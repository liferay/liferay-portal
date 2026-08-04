/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {type Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {addCMSAdministrator} from '../../../utils/addCMSAdministrator';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitchViaApi} from '../../../utils/performLogin';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-82226': {enabled: true},
	}),
	loginTest()
);

const APPLICATION_NAME = 'cms/basic-web-contents';
const PAST_DATE = '2020-01-01T00:00:00Z';
const REVIEW_DATE_DISPLAYED = '12/31/2099, 10:00 AM';
const REVIEW_DATE_INPUT = '12/31/2099 10:00 AM';
const TIME_ZONE = 'America/Los_Angeles';

test.use({timezoneId: TIME_ZONE});

async function fillReviewDateModal(page: Page, reviewDate: string) {
	const reviewDateInput = page.locator('.modal input.form-control').first();

	await reviewDateInput.fill(reviewDate);

	await reviewDateInput.blur();

	await page.locator('.modal').getByRole('button', {name: 'Save'}).click();
}

async function pollReviewDate(
	apiHelpers: DataApiHelpers,
	objectEntryId: number
) {
	await expect
		.poll(
			async () => {
				const objectEntry =
					await apiHelpers.objectEntry.getObjectEntryById(
						APPLICATION_NAME,
						String(objectEntryId)
					);

				return new Date(objectEntry.reviewDate).toLocaleString(
					'en-US',
					{
						day: '2-digit',
						hour: '2-digit',
						hour12: true,
						minute: '2-digit',
						month: '2-digit',
						timeZone: TIME_ZONE,
						year: 'numeric',
					}
				);
			},
			{timeout: 3000}
		)
		.toBe(REVIEW_DATE_DISPLAYED);
}

async function updateReviewDate(page: Page, title: string, reviewDate: string) {
	await page.getByRole('button', {name: `${title} Actions`}).click();

	await page.getByRole('menuitem', {name: 'Update Review Date'}).click();

	await fillReviewDateModal(page, reviewDate);
}

test.beforeEach(async ({apiHelpers, page}) => {
	const user = await addCMSAdministrator(apiHelpers);

	await performUserSwitchViaApi(page, user.alternateName);
});

test(
	'Stores the review date the user picked in their own time zone',
	{tag: '@LPD-98462'},
	async ({apiHelpers, page}) => {
		const spaceName = `space ${getRandomString()}`;
		const title = `overdue ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const overdueContent = await apiHelpers.objectEntry.postObjectEntry(
			{
				displayDate: PAST_DATE,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				reviewDate: PAST_DATE,
				title,
			},
			APPLICATION_NAME,
			spaceName
		);

		apiHelpers.data.push({id: overdueContent.id, type: 'document'});

		await page.goto('/web/cms/overdue-reviews');

		await updateReviewDate(page, title, REVIEW_DATE_INPUT);

		await pollReviewDate(apiHelpers, overdueContent.id);
	}
);

test(
	'Updates the review date of overdue contents from the dashboard',
	{tag: '@LPD-98462'},
	async ({apiHelpers, page}) => {
		const firstSpaceName = `space ${getRandomString()}`;
		const secondSpaceName = `space ${getRandomString()}`;
		const secondSpaceTitle = `overdue ${getRandomString()}`;
		const remainingTitles = [
			`overdue ${getRandomString()}`,
			`overdue ${getRandomString()}`,
		];

		const selectSpace = async (spaceName: string) => {
			const searchInput = page.getByRole('textbox', {name: 'Search'});

			await expect(async () => {
				await page
					.getByRole('combobox', {name: 'Filter by Spaces'})
					.click();

				await expect(searchInput).toBeVisible();
			}).toPass();

			await searchInput.fill(spaceName);

			await page.getByRole('option', {name: spaceName}).click();
		};

		const overdueReviewsCard = page.getByRole('button', {
			name: /overdue reviews/,
		});

		const overdueReviewsCount = overdueReviewsCard
			.locator('.cms-dashboard__interactive-card__metric > div')
			.first();

		let overdueContents: Awaited<
			ReturnType<typeof apiHelpers.objectEntry.postObjectEntry>
		>[];

		await test.step('Create overdue contents in two spaces', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: firstSpaceName,
				type: 'Space',
			});

			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: secondSpaceName,
				type: 'Space',
			});

			const postOverdueContent = (spaceName: string, title: string) =>
				apiHelpers.objectEntry.postObjectEntry(
					{
						displayDate: PAST_DATE,
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						reviewDate: PAST_DATE,
						title,
					},
					APPLICATION_NAME,
					spaceName
				);

			overdueContents = [
				await postOverdueContent(
					firstSpaceName,
					`overdue ${getRandomString()}`
				),
				await postOverdueContent(secondSpaceName, secondSpaceTitle),
				await postOverdueContent(secondSpaceName, remainingTitles[0]),
				await postOverdueContent(secondSpaceName, remainingTitles[1]),
			];

			for (const overdueContent of overdueContents) {
				apiHelpers.data.push({id: overdueContent.id, type: 'document'});

				expect(overdueContent.id).toBeTruthy();
			}
		});

		await test.step('Check the overdue reviews count per space', async () => {
			await page.goto('/web/cms/dashboard');

			await selectSpace(firstSpaceName);

			await expect(overdueReviewsCount).toHaveText('1');

			await selectSpace(secondSpaceName);

			await expect(overdueReviewsCount).toHaveText('3');
		});

		await test.step('Open the overdue reviews list', async () => {
			await overdueReviewsCard.click();

			await expect(page).toHaveURL(/cms\/overdue-reviews/);

			await expect(
				page.getByText(secondSpaceTitle, {exact: true})
			).toBeVisible();
		});

		await test.step('Update a review date from its row action', async () => {
			await updateReviewDate(page, secondSpaceTitle, REVIEW_DATE_INPUT);

			await pollReviewDate(apiHelpers, overdueContents[1].id);

			await page.reload();

			await expect(
				page.getByText(secondSpaceTitle, {exact: true})
			).toBeHidden();
		});

		await test.step('Update the remaining review dates in bulk', async () => {
			for (const title of remainingTitles) {
				await page.getByLabel(`Select ${title}`, {exact: true}).check();
			}

			await page
				.locator('[data-qa-id="selectionToolbar"]')
				.getByRole('button', {name: 'Actions'})
				.click();

			await page
				.getByRole('menuitem', {name: 'Update Review Date'})
				.click();

			await fillReviewDateModal(page, REVIEW_DATE_INPUT);

			for (const overdueContent of overdueContents.slice(2)) {
				await pollReviewDate(apiHelpers, overdueContent.id);
			}

			await page.reload();

			for (const title of remainingTitles) {
				await expect(page.getByText(title, {exact: true})).toBeHidden();
			}
		});
	}
);
