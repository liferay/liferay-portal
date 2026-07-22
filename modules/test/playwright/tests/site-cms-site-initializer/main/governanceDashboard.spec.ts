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
const DATE_DISPLAYED = '12/31/2099, 10:00 AM';
const DATE_INPUT = '12/31/2099 10:00 AM';
const DUE_DATE_INPUT = '2099-12-31';
const DUE_TIME_INPUT = '10:00';
const PAST_DATE = '2020-01-01T00:00:00Z';
const TIME_ZONE = 'America/Los_Angeles';

test.use({timezoneId: TIME_ZONE});

async function fillScheduleDateModal(page: Page, date: string) {
	const dateInput = page.locator('.modal input.form-control').first();

	await dateInput.fill(date);

	await dateInput.blur();

	await page.locator('.modal').getByRole('button', {name: 'Save'}).click();
}

async function pollDate(
	apiHelpers: DataApiHelpers,
	objectEntryId: number,
	fieldName: 'expirationDate' | 'reviewDate'
) {
	await expect
		.poll(
			async () => {
				const objectEntry =
					await apiHelpers.objectEntry.getObjectEntryById(
						APPLICATION_NAME,
						String(objectEntryId)
					);

				return new Date(objectEntry[fieldName]).toLocaleString(
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
		.toBe(DATE_DISPLAYED);
}

async function updateReviewDate(page: Page, title: string, reviewDate: string) {
	await page.getByRole('button', {name: `${title} Actions`}).click();

	await page.getByRole('menuitem', {name: 'Update Review Date'}).click();

	await fillScheduleDateModal(page, reviewDate);
}

async function updateExpirationDate(
	page: Page,
	title: string,
	expirationDate: string
) {
	await page.getByRole('button', {name: `${title} Actions`}).click();

	await page.getByRole('menuitem', {name: 'Update Expiration Date'}).click();

	await fillScheduleDateModal(page, expirationDate);
}

async function selectSpace(page: Page, spaceName: string) {
	const searchInput = page.getByRole('textbox', {name: 'Search'});

	await expect(async () => {
		await page.getByRole('combobox', {name: 'Filter by Spaces'}).click();

		await expect(searchInput).toBeVisible();
	}).toPass();

	await searchInput.fill(spaceName);

	await page.getByRole('option', {name: spaceName}).click();
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

		await updateReviewDate(page, title, DATE_INPUT);

		await pollDate(apiHelpers, overdueContent.id, 'reviewDate');
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

			await selectSpace(page, firstSpaceName);

			await expect(overdueReviewsCount).toHaveText('1');

			await selectSpace(page, secondSpaceName);

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
			await updateReviewDate(page, secondSpaceTitle, DATE_INPUT);

			await pollDate(apiHelpers, overdueContents[1].id, 'reviewDate');

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

			await fillScheduleDateModal(page, DATE_INPUT);

			for (const overdueContent of overdueContents.slice(2)) {
				await pollDate(apiHelpers, overdueContent.id, 'reviewDate');
			}

			await page.reload();

			for (const title of remainingTitles) {
				await expect(page.getByText(title, {exact: true})).toBeHidden();
			}
		});
	}
);

test(
	'Updates the expiration date of expired contents from the dashboard',
	{tag: '@LPD-98348'},
	async ({apiHelpers, page}) => {
		const firstSpaceName = `space ${getRandomString()}`;
		const secondSpaceName = `space ${getRandomString()}`;
		const secondSpaceTitle = `expired ${getRandomString()}`;
		const remainingTitles = [
			`expired ${getRandomString()}`,
			`expired ${getRandomString()}`,
		];

		let expiredContents: Awaited<
			ReturnType<typeof apiHelpers.objectEntry.postObjectEntry>
		>[];

		await test.step('Create and expire contents in two spaces', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: firstSpaceName,
				type: 'Space',
			});

			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: secondSpaceName,
				type: 'Space',
			});

			const postExpiredContent = async (
				spaceName: string,
				title: string
			) => {
				const objectEntry =
					await apiHelpers.objectEntry.postObjectEntry(
						{
							objectEntryFolderExternalReferenceCode:
								'L_CONTENTS',
							title,
						},
						APPLICATION_NAME,
						spaceName
					);

				await apiHelpers.objectEntry.expireObjectEntryByExternalReferenceCode(
					APPLICATION_NAME,
					spaceName,
					objectEntry.externalReferenceCode
				);

				return objectEntry;
			};

			expiredContents = [
				await postExpiredContent(
					firstSpaceName,
					`expired ${getRandomString()}`
				),
				await postExpiredContent(secondSpaceName, secondSpaceTitle),
				await postExpiredContent(secondSpaceName, remainingTitles[0]),
				await postExpiredContent(secondSpaceName, remainingTitles[1]),
			];

			for (const expiredContent of expiredContents) {
				apiHelpers.data.push({id: expiredContent.id, type: 'document'});

				expect(expiredContent.id).toBeTruthy();
			}
		});

		const expiredAssetsCard = page.getByRole('button', {
			name: /expired assets/i,
		});

		const expiredAssetsCount = expiredAssetsCard
			.locator('.cms-dashboard__interactive-card__metric > div')
			.first();

		await test.step('Check the expired assets count per space', async () => {
			await page.goto('/web/cms/dashboard');

			await selectSpace(page, firstSpaceName);

			await expect(expiredAssetsCount).toHaveText('1');

			await selectSpace(page, secondSpaceName);

			await expect(expiredAssetsCount).toHaveText('3');
		});

		await test.step('Open the expired assets list from its card', async () => {
			await expiredAssetsCard.click();

			await expect(page).toHaveURL(/cms\/expired-assets/);

			await expect(async () => {
				await page.reload();

				await expect(
					page.getByText(secondSpaceTitle, {exact: true})
				).toBeVisible({timeout: 3000});
			}).toPass();
		});

		await test.step('Update an expiration date from its row action', async () => {
			await updateExpirationDate(page, secondSpaceTitle, DATE_INPUT);

			await pollDate(apiHelpers, expiredContents[1].id, 'expirationDate');

			await expect(async () => {
				await page.reload();

				await expect(
					page.getByText(secondSpaceTitle, {exact: true})
				).toBeHidden({timeout: 3000});
			}).toPass();
		});

		await test.step('Update the remaining expiration dates in bulk', async () => {
			for (const title of remainingTitles) {
				await page.getByLabel(`Select ${title}`, {exact: true}).check();
			}

			await page
				.locator('[data-qa-id="selectionToolbar"]')
				.getByRole('button', {name: 'Actions'})
				.click();

			await page
				.getByRole('menuitem', {name: 'Update Expiration Date'})
				.click();

			await fillScheduleDateModal(page, DATE_INPUT);

			for (const expiredContent of expiredContents.slice(2)) {
				await pollDate(apiHelpers, expiredContent.id, 'expirationDate');
			}

			await expect(async () => {
				await page.reload();

				for (const title of remainingTitles) {
					await expect(
						page.getByText(title, {exact: true})
					).toBeHidden({timeout: 3000});
				}
			}).toPass();
		});
	}
);

test(
	'Updates the due date of pending workflow contents from the dashboard',
	{tag: '@LPD-98986'},
	async ({apiHelpers, page}) => {
		const firstSpaceName = `space ${getRandomString()}`;
		const secondSpaceName = `space ${getRandomString()}`;
		const secondSpaceTitle = `pending ${getRandomString()}`;
		const remainingTitles = [
			`pending ${getRandomString()}`,
			`pending ${getRandomString()}`,
		];

		const pendingWorkflowsCard = page.getByRole('button', {
			name: /pending workflows/i,
		});

		const pendingWorkflowsCount = pendingWorkflowsCard
			.locator('.cms-dashboard__interactive-card__metric > div')
			.first();

		let objectDefinitionClassName: string;
		let pendingContents: Awaited<
			ReturnType<typeof apiHelpers.objectEntry.postObjectEntry>
		>[];

		await test.step('Enable workflow and create pending contents in two spaces', async () => {
			const firstSpace =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: firstSpaceName,
					type: 'Space',
				});

			const secondSpace =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: secondSpaceName,
					type: 'Space',
				});

			const objectDefinition =
				await apiHelpers.objectAdmin.getObjectDefinitionByName(
					'CMSBasicWebContent'
				);

			objectDefinitionClassName = objectDefinition.className;

			const workflowDefinition =
				await apiHelpers.headlessAdminWorkflow.getWorkflowDefinitionByName(
					'Single Approver'
				);

			for (const space of [firstSpace, secondSpace]) {
				await apiHelpers.headlessAdminWorkflow.postWorkflowDefinitionLink(
					objectDefinition.className,
					space.siteId,
					workflowDefinition.id,
					workflowDefinition.name,
					Number(workflowDefinition.version)
				);
			}

			const postPendingContent = (spaceName: string, title: string) =>
				apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						title,
					},
					APPLICATION_NAME,
					spaceName
				);

			pendingContents = [
				await postPendingContent(
					firstSpaceName,
					`pending ${getRandomString()}`
				),
				await postPendingContent(secondSpaceName, secondSpaceTitle),
				await postPendingContent(secondSpaceName, remainingTitles[0]),
				await postPendingContent(secondSpaceName, remainingTitles[1]),
			];

			for (const pendingContent of pendingContents) {
				apiHelpers.data.push({id: pendingContent.id, type: 'document'});

				expect(pendingContent.id).toBeTruthy();
			}
		});

		await test.step('Check the pending workflows count per space', async () => {
			await page.goto('/web/cms/dashboard');

			await selectSpace(page, firstSpaceName);

			await expect(pendingWorkflowsCount).toHaveText('1');

			await selectSpace(page, secondSpaceName);

			await expect(pendingWorkflowsCount).toHaveText('3');
		});

		await test.step('Open the pending workflows list', async () => {
			await pendingWorkflowsCard.click();

			await expect(page).toHaveURL(/cms\/pending-workflows/);

			await expect(async () => {
				await page.reload();

				await expect(
					page.getByText(secondSpaceTitle, {exact: true})
				).toBeVisible({timeout: 5000});
			}).toPass();
		});

		await test.step('Update the due date from its row action', async () => {
			await page
				.getByRole('button', {name: `${secondSpaceTitle} Actions`})
				.click();

			await page.getByRole('menuitem', {name: 'Update Due Date'}).click();

			const modal = page.locator('.modal-content');

			await expect(modal).toBeVisible();

			await modal.locator('input[type="date"]').fill(DUE_DATE_INPUT);
			await modal.locator('input[type="time"]').fill(DUE_TIME_INPUT);

			await modal.getByRole('button', {name: 'Save'}).click();

			await expect
				.poll(async () => {
					const workflowTask =
						await apiHelpers.headlessAdminWorkflow.getWorkflowTaskByAsset(
							objectDefinitionClassName,
							String(pendingContents[1].id)
						);

					return workflowTask?.dateDue
						? new Date(workflowTask.dateDue).toISOString()
						: null;
				})
				.toBe(`${DUE_DATE_INPUT}T${DUE_TIME_INPUT}:00.000Z`);
		});
	}
);
