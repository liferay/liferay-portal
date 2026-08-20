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
import {getRandomInt} from '../../../utils/getRandomInt';
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

async function postDatedContent(
	apiHelpers: DataApiHelpers,
	{
		dateField,
		hour,
		spaceName,
		title,
	}: {
		dateField: 'expirationDate' | 'reviewDate';
		hour: number;
		spaceName: string;
		title: string;
	}
) {
	const content = await apiHelpers.objectEntry.postObjectEntry(
		{
			[dateField]: getUpcomingDate(hour),
			displayDate: PAST_DATE,
			objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			title,
		},
		APPLICATION_NAME,
		spaceName
	);

	apiHelpers.data.push({id: content.id, type: 'document'});

	expect(content.id).toBeTruthy();

	return content;
}

function getUpcomingDate(hour: number) {
	const date = new Date();

	date.setDate(date.getDate() + 1);
	date.setHours(hour, 0, 0, 0);

	return date.toISOString().replace(/\.\d{3}Z$/, 'Z');
}

function formatInTimeZone(date: string) {
	return new Date(date).toLocaleString('en-US', {
		day: '2-digit',
		hour: '2-digit',
		hour12: true,
		minute: '2-digit',
		month: '2-digit',
		timeZone: TIME_ZONE,
		year: 'numeric',
	});
}

function getDocumentHTML(externalReferenceCode: string) {
	return (
		`<img src="/documents/${getRandomInt()}/0/image.jpg/${getRandomString()}` +
		'?download=true&amp;objectDefinitionExternalReferenceCode=' +
		'L_CMS_BASIC_DOCUMENT&amp;objectEntryExternalReferenceCode=' +
		`${externalReferenceCode}&amp;objectFieldExternalReferenceCode=FILE">`
	);
}

async function gotoBrokenLinks(page: Page, spaceName: string, title: string) {
	await page.goto('/web/cms/dashboard');

	await selectSpace(page, spaceName);

	await page.getByRole('button', {name: /broken links/i}).click();

	await expect(page).toHaveURL(/broken-links\?groupId=/);

	await expect(async () => {
		await page.reload();

		await expect(page.getByText(title, {exact: true})).toBeVisible({
			timeout: 5000,
		});
	}).toPass();
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

				return formatInTimeZone(objectEntry[fieldName]);
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

test.describe('Attention Required section', () => {
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
							objectEntryFolderExternalReferenceCode:
								'L_CONTENTS',
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
					await postOverdueContent(
						secondSpaceName,
						remainingTitles[0]
					),
					await postOverdueContent(
						secondSpaceName,
						remainingTitles[1]
					),
				];

				for (const overdueContent of overdueContents) {
					apiHelpers.data.push({
						id: overdueContent.id,
						type: 'document',
					});

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
					await page
						.getByLabel(`Select ${title}`, {exact: true})
						.check();
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
					await expect(
						page.getByText(title, {exact: true})
					).toBeHidden();
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
					await postExpiredContent(
						secondSpaceName,
						remainingTitles[0]
					),
					await postExpiredContent(
						secondSpaceName,
						remainingTitles[1]
					),
				];

				for (const expiredContent of expiredContents) {
					apiHelpers.data.push({
						id: expiredContent.id,
						type: 'document',
					});

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

				await pollDate(
					apiHelpers,
					expiredContents[1].id,
					'expirationDate'
				);

				await expect(async () => {
					await page.reload();

					await expect(
						page.getByText(secondSpaceTitle, {exact: true})
					).toBeHidden({timeout: 3000});
				}).toPass();
			});

			await test.step('Update the remaining expiration dates in bulk', async () => {
				for (const title of remainingTitles) {
					await page
						.getByLabel(`Select ${title}`, {exact: true})
						.check();
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
					await pollDate(
						apiHelpers,
						expiredContent.id,
						'expirationDate'
					);
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
							objectEntryFolderExternalReferenceCode:
								'L_CONTENTS',
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
					await postPendingContent(
						secondSpaceName,
						remainingTitles[0]
					),
					await postPendingContent(
						secondSpaceName,
						remainingTitles[1]
					),
				];

				for (const pendingContent of pendingContents) {
					apiHelpers.data.push({
						id: pendingContent.id,
						type: 'document',
					});

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

				await expect(
					page.getByRole('link', {
						exact: true,
						name: secondSpaceTitle,
					})
				).toHaveAttribute(
					'href',
					new RegExp(
						`edit_content_item\\?objectEntryId=${pendingContents[1].id}&redirect=%2F`
					)
				);
			});

			await test.step('Update the due date from its row action', async () => {
				await page
					.getByRole('button', {name: `${secondSpaceTitle} Actions`})
					.click();

				await page
					.getByRole('menuitem', {name: 'Update Due Date'})
					.click();

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
							? formatInTimeZone(workflowTask.dateDue)
							: null;
					})
					.toBe(DATE_DISPLAYED);
			});
		}
	);

	test(
		'Lists the contents that point at an expired asset of the selected space',
		{tag: '@LPD-103027'},
		async ({apiHelpers, page}) => {
			const firstSpaceName = `space ${getRandomString()}`;
			const secondSpaceName = `space ${getRandomString()}`;
			const expiredTitle = `expired ${getRandomString()}`;
			const referringTitle = `referring ${getRandomString()}`;
			const secondSpaceReferringTitle = `referring ${getRandomString()}`;

			await test.step('Create a broken link in two spaces', async () => {
				const postBrokenLink = async (
					spaceName: string,
					expiredTitle: string,
					referringTitle: string
				) => {
					await apiHelpers.headlessAssetLibrary.createAssetLibrary({
						name: spaceName,
						type: 'Space',
					});

					const expiredContent =
						await apiHelpers.objectEntry.postObjectEntry(
							{
								objectEntryFolderExternalReferenceCode:
									'L_CONTENTS',
								title: expiredTitle,
							},
							APPLICATION_NAME,
							spaceName
						);

					await apiHelpers.objectEntry.expireObjectEntryByExternalReferenceCode(
						APPLICATION_NAME,
						spaceName,
						expiredContent.externalReferenceCode
					);

					const referringContent =
						await apiHelpers.objectEntry.postObjectEntry(
							{
								content: getDocumentHTML(
									expiredContent.externalReferenceCode
								),
								objectEntryFolderExternalReferenceCode:
									'L_CONTENTS',
								title: referringTitle,
							},
							APPLICATION_NAME,
							spaceName
						);

					for (const objectEntry of [
						expiredContent,
						referringContent,
					]) {
						apiHelpers.data.push({
							id: objectEntry.id,
							type: 'document',
						});

						expect(objectEntry.id).toBeTruthy();
					}
				};

				await postBrokenLink(
					firstSpaceName,
					expiredTitle,
					referringTitle
				);
				await postBrokenLink(
					secondSpaceName,
					`expired ${getRandomString()}`,
					secondSpaceReferringTitle
				);
			});

			await test.step('Confirm the second space lists its own broken link', async () => {
				await gotoBrokenLinks(
					page,
					secondSpaceName,
					secondSpaceReferringTitle
				);
			});

			await test.step('Open the broken links page of the first space', async () => {
				await gotoBrokenLinks(page, firstSpaceName, referringTitle);
			});

			await test.step('Check that only the first space is listed', async () => {
				await expect(
					page.getByText(`${expiredTitle} - Expired Asset`, {
						exact: true,
					})
				).toBeVisible();

				await expect(
					page.getByText(secondSpaceReferringTitle, {exact: true})
				).toBeHidden();
			});

			await test.step('Edit the content from the list', async () => {
				await page
					.getByRole('button', {name: `${referringTitle} Actions`})
					.click();

				await page.getByRole('menuitem', {name: 'Edit'}).click();

				await expect(page.getByLabel('Title')).toHaveValue(
					referringTitle
				);
			});
		}
	);
});

test.describe('Needs Review section', () => {
	test(
		'Scopes the upcoming reviews to the selected space and updates review dates from the All section',
		{tag: '@LPD-97420'},
		async ({apiHelpers, page}) => {
			const firstSpaceName = `space ${getRandomString()}`;
			const secondSpaceName = `space ${getRandomString()}`;
			const firstSpaceTitles = [
				`upcoming ${getRandomString()}`,
				`upcoming ${getRandomString()}`,
			];
			const secondSpaceTitles = [
				`upcoming ${getRandomString()}`,
				`upcoming ${getRandomString()}`,
				`upcoming ${getRandomString()}`,
			];

			let secondSpaceContents: Awaited<
				ReturnType<typeof apiHelpers.objectEntry.postObjectEntry>
			>[];

			await test.step('Create upcoming reviews in both spaces', async () => {
				for (const name of [firstSpaceName, secondSpaceName]) {
					await apiHelpers.headlessAssetLibrary.createAssetLibrary({
						name,
						type: 'Space',
					});
				}

				for (const [index, title] of firstSpaceTitles.entries()) {
					await postDatedContent(apiHelpers, {
						dateField: 'reviewDate',
						hour: index + 1,
						spaceName: firstSpaceName,
						title,
					});
				}

				secondSpaceContents = [];

				for (const [index, title] of secondSpaceTitles.entries()) {
					secondSpaceContents.push(
						await postDatedContent(apiHelpers, {
							dateField: 'reviewDate',
							hour: index + 3,
							spaceName: secondSpaceName,
							title,
						})
					);
				}
			});

			const upcomingReviews = page.getByRole('region', {
				name: 'Upcoming Reviews',
			});

			await test.step('Show the upcoming reviews card on the dashboard', async () => {
				await page.goto('/web/cms/dashboard');

				await expect(upcomingReviews).toBeVisible();
			});

			await test.step('List only the upcoming reviews of the selected space', async () => {
				await selectSpace(page, firstSpaceName);

				for (const title of firstSpaceTitles) {
					await expect(
						upcomingReviews.getByText(title, {exact: true})
					).toBeVisible();
				}

				for (const title of secondSpaceTitles) {
					await expect(
						upcomingReviews.getByText(title, {exact: true})
					).toBeHidden();
				}

				await selectSpace(page, secondSpaceName);

				for (const title of secondSpaceTitles) {
					await expect(
						upcomingReviews.getByText(title, {exact: true})
					).toBeVisible();
				}

				for (const title of firstSpaceTitles) {
					await expect(
						upcomingReviews.getByText(title, {exact: true})
					).toBeHidden();
				}
			});

			await test.step('Offer both date actions on a list row', async () => {
				await upcomingReviews
					.getByRole('button', {
						name: `${secondSpaceTitles[0]} Actions`,
					})
					.click();

				for (const name of [
					'Update Expiration Date',
					'Update Review Date',
				]) {
					await expect(
						page.getByRole('menuitem', {name})
					).toBeVisible();
				}

				await page.keyboard.press('Escape');
			});

			await test.step('Open the All section sorted by review date', async () => {
				await upcomingReviews
					.getByRole('link', {name: 'View Upcoming Reviews'})
					.click();

				await expect(page).toHaveURL(
					/allSection_fdsConfig=.*sorts.*dateReview/
				);

				for (const title of secondSpaceTitles) {
					await expect(
						page.getByText(title, {exact: true})
					).toBeVisible();
				}
			});

			await test.step('Update a review date from its row action', async () => {
				await updateReviewDate(page, secondSpaceTitles[0], DATE_INPUT);

				await pollDate(
					apiHelpers,
					secondSpaceContents[0].id,
					'reviewDate'
				);
			});

			await test.step('Update the remaining review dates in bulk', async () => {
				for (const title of secondSpaceTitles.slice(1)) {
					await page
						.getByLabel(`Select ${title}`, {exact: true})
						.check();
				}

				await page
					.locator('[data-qa-id="selectionToolbar"]')
					.getByRole('button', {name: 'Actions'})
					.click();

				await page
					.getByRole('menuitem', {name: 'Update Review Date'})
					.click();

				await fillScheduleDateModal(page, DATE_INPUT);

				for (const content of secondSpaceContents.slice(1)) {
					await pollDate(apiHelpers, content.id, 'reviewDate');
				}
			});
		}
	);

	test(
		'Scopes the expiring soon assets to the selected space and updates expiration dates from the All section',
		{tag: '@LPD-97420'},
		async ({apiHelpers, page}) => {
			const firstSpaceName = `space ${getRandomString()}`;
			const secondSpaceName = `space ${getRandomString()}`;
			const firstSpaceTitles = [
				`expiring ${getRandomString()}`,
				`expiring ${getRandomString()}`,
			];
			const secondSpaceTitles = [
				`expiring ${getRandomString()}`,
				`expiring ${getRandomString()}`,
				`expiring ${getRandomString()}`,
			];

			let secondSpaceContents: Awaited<
				ReturnType<typeof apiHelpers.objectEntry.postObjectEntry>
			>[];

			await test.step('Create expiring soon assets in both spaces', async () => {
				for (const name of [firstSpaceName, secondSpaceName]) {
					await apiHelpers.headlessAssetLibrary.createAssetLibrary({
						name,
						type: 'Space',
					});
				}

				for (const [index, title] of firstSpaceTitles.entries()) {
					await postDatedContent(apiHelpers, {
						dateField: 'expirationDate',
						hour: index + 1,
						spaceName: firstSpaceName,
						title,
					});
				}

				secondSpaceContents = [];

				for (const [index, title] of secondSpaceTitles.entries()) {
					secondSpaceContents.push(
						await postDatedContent(apiHelpers, {
							dateField: 'expirationDate',
							hour: index + 3,
							spaceName: secondSpaceName,
							title,
						})
					);
				}
			});

			const expiringSoon = page.getByRole('region', {
				name: 'Expiring Soon',
			});

			await test.step('Show the expiring soon card on the dashboard', async () => {
				await page.goto('/web/cms/dashboard');

				await expect(expiringSoon).toBeVisible();
			});

			await test.step('List only the expiring soon assets of the selected space', async () => {
				await selectSpace(page, firstSpaceName);

				for (const title of firstSpaceTitles) {
					await expect(
						expiringSoon.getByText(title, {exact: true})
					).toBeVisible();
				}

				for (const title of secondSpaceTitles) {
					await expect(
						expiringSoon.getByText(title, {exact: true})
					).toBeHidden();
				}

				await selectSpace(page, secondSpaceName);

				for (const title of secondSpaceTitles) {
					await expect(
						expiringSoon.getByText(title, {exact: true})
					).toBeVisible();
				}

				for (const title of firstSpaceTitles) {
					await expect(
						expiringSoon.getByText(title, {exact: true})
					).toBeHidden();
				}
			});

			await test.step('Open the All section filtered by expiration date', async () => {
				await expiringSoon
					.getByRole('link', {name: 'View Expiring Soon'})
					.click();

				await expect(page).toHaveURL(
					/allSection_fdsConfig=.*filters.*dateExpiration/
				);

				await expect(
					page.getByRole('button', {name: /Expiring Soon/})
				).toHaveAttribute('aria-pressed', 'true');

				for (const title of secondSpaceTitles) {
					await expect(
						page.getByText(title, {exact: true})
					).toBeVisible();
				}
			});

			await test.step('Update an expiration date from its row action', async () => {
				await updateExpirationDate(
					page,
					secondSpaceTitles[0],
					DATE_INPUT
				);

				await pollDate(
					apiHelpers,
					secondSpaceContents[0].id,
					'expirationDate'
				);
			});

			await test.step('Update the remaining expiration dates in bulk', async () => {
				for (const title of secondSpaceTitles.slice(1)) {
					await page
						.getByLabel(`Select ${title}`, {exact: true})
						.check();
				}

				await page
					.locator('[data-qa-id="selectionToolbar"]')
					.getByRole('button', {name: 'Actions'})
					.click();

				await page
					.getByRole('menuitem', {name: 'Update Expiration Date'})
					.click();

				await fillScheduleDateModal(page, DATE_INPUT);

				for (const content of secondSpaceContents.slice(1)) {
					await pollDate(apiHelpers, content.id, 'expirationDate');
				}
			});
		}
	);
});
