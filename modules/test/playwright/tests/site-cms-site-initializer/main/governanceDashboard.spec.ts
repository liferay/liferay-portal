/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js';
import {type Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {ServerAdministrationPage} from '../../../pages/server-admin-web/ServerAdministrationPage';
import {addCMSAdministrator} from '../../../utils/addCMSAdministrator';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitchViaApi,
} from '../../../utils/performLogin';
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
const LONG_STANDING_DRAFT_AGE_DAYS = 45;
const PAST_DATE = '2020-01-01T00:00:00Z';
const TIME_ZONE_BROWSER = 'America/Los_Angeles';
const TIME_ZONE_USER = 'UTC';

test.use({timezoneId: TIME_ZONE_BROWSER});

async function fillScheduleDateModal(page: Page, date: string) {
	const dateInput = page.locator('.modal input.form-control').first();

	await dateInput.fill(date);

	await dateInput.blur();

	await page.locator('.modal').getByRole('button', {name: 'Save'}).click();
}

function getLongStandingDraftScript(objectEntryId: number) {
	return `
		import com.liferay.object.service.ObjectDefinitionLocalServiceUtil
		import com.liferay.object.service.ObjectEntryLocalServiceUtil
		import com.liferay.portal.kernel.search.IndexerRegistryUtil

		def objectEntry = ObjectEntryLocalServiceUtil.getObjectEntry(${objectEntryId})

		objectEntry.setModifiedDate(new Date(System.currentTimeMillis() - ${LONG_STANDING_DRAFT_AGE_DAYS} * 86400000L))

		objectEntry = ObjectEntryLocalServiceUtil.updateObjectEntry(objectEntry)

		def objectDefinition = ObjectDefinitionLocalServiceUtil.getObjectDefinition(objectEntry.getObjectDefinitionId())

		IndexerRegistryUtil.nullSafeGetIndexer(objectDefinition.getClassName()).reindex(objectEntry)
	`;
}

function getUpcomingDate(hour: number) {
	const date = new Date();

	date.setDate(date.getDate() + 1);
	date.setHours(hour, 0, 0, 0);

	return date.toISOString().replace(/\.\d{3}Z$/, 'Z');
}

function formatInTimeZone(date: string, timeZone: string) {
	return new Date(date).toLocaleString('en-US', {
		day: '2-digit',
		hour: '2-digit',
		hour12: true,
		minute: '2-digit',
		month: '2-digit',
		timeZone,
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

async function openQualityList(
	page: Page,
	{
		cardTitle,
		spaceName,
		text,
	}: {cardTitle: string; spaceName: string; text: string}
) {
	const qualityList = page.getByRole('region', {name: cardTitle});

	await expect(async () => {
		await page.goto('/web/cms/dashboard');

		await selectSpace(page, spaceName);

		await page
			.getByRole('button', {name: new RegExp(`^${cardTitle} `)})
			.click();

		await expect(qualityList.getByText(text, {exact: true})).toBeVisible({
			timeout: 5000,
		});
	}).toPass();

	return qualityList;
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

				return formatInTimeZone(objectEntry[fieldName], TIME_ZONE_USER);
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

test.describe('Freshness section', () => {
	test(
		'Stores the review date the user picked in their account time zone rather than the browser one',
		{tag: ['@LPD-98462', '@LPD-104263']},
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

			await page.goto('/web/cms/dashboard');

			await selectSpace(page, spaceName);

			await page.getByRole('button', {name: /^Overdue Reviews/}).click();

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
			const listTitle = `overdue ${getRandomString()}`;
			const remainingTitles = [
				`overdue ${getRandomString()}`,
				`overdue ${getRandomString()}`,
			];

			const overdueReviewsCard = page.getByRole('button', {
				name: /^Overdue Reviews/,
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
					await postOverdueContent(secondSpaceName, listTitle),
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

				await expect(overdueReviewsCount).toHaveText('4');
			});

			const overdueReviewsList = page.getByRole('region', {
				name: 'Overdue Reviews',
			});

			await test.step('Open the overdue reviews list', async () => {
				await overdueReviewsCard.click();

				await expect(
					overdueReviewsList.getByText(secondSpaceTitle, {
						exact: true,
					})
				).toBeVisible();
			});

			await test.step('Update a review date from the list', async () => {
				await updateReviewDate(page, listTitle, DATE_INPUT);

				await pollDate(apiHelpers, overdueContents[2].id, 'reviewDate');

				await expect(
					overdueReviewsList.getByText(listTitle, {exact: true})
				).toBeHidden();

				await expect(overdueReviewsCount).toHaveText('3');
			});

			await test.step('Open the all section from the list', async () => {
				await page
					.getByRole('link', {name: 'View All Overdue Reviews'})
					.click();

				await expect(page).toHaveURL(/cms\/all/);

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

				for (const overdueContent of overdueContents.slice(3)) {
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
			const listTitle = `expired ${getRandomString()}`;
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
					await postExpiredContent(secondSpaceName, listTitle),
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
				name: /^Expired/,
			});

			const expiredAssetsCount = expiredAssetsCard
				.locator('.cms-dashboard__interactive-card__metric > div')
				.first();

			await test.step('Check the expired assets count per space', async () => {
				await page.goto('/web/cms/dashboard');

				await selectSpace(page, firstSpaceName);

				await expect(expiredAssetsCount).toHaveText('1');

				await selectSpace(page, secondSpaceName);

				await expect(expiredAssetsCount).toHaveText('4');
			});

			const expiredAssetsList = page.getByRole('region', {
				name: 'Expired Assets',
			});

			await test.step('Open the expired assets list from its card', async () => {
				await expiredAssetsCard.click();

				await expect(
					expiredAssetsList.getByText(secondSpaceTitle, {exact: true})
				).toBeVisible();
			});

			await test.step('Update an expiration date from the list', async () => {
				await updateExpirationDate(page, listTitle, DATE_INPUT);

				await pollDate(
					apiHelpers,
					expiredContents[2].id,
					'expirationDate'
				);

				await expect(
					expiredAssetsList.getByText(listTitle, {exact: true})
				).toBeHidden();

				await expect(expiredAssetsCount).toHaveText('3');
			});

			await test.step('Open the all section from the list', async () => {
				await page
					.getByRole('link', {name: 'View All Expired Assets'})
					.click();

				await expect(page).toHaveURL(/cms\/all/);

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

				for (const expiredContent of expiredContents.slice(3)) {
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
});

test.describe('Quality section', () => {
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
				await openQualityList(page, {
					cardTitle: 'Broken Links',
					spaceName: secondSpaceName,
					text: secondSpaceReferringTitle,
				});
			});

			await test.step('Check that only the first space is listed', async () => {
				const brokenLinksList = await openQualityList(page, {
					cardTitle: 'Broken Links',
					spaceName: firstSpaceName,
					text: referringTitle,
				});

				await expect(
					brokenLinksList.getByText('1 Expired Asset', {exact: true})
				).toBeVisible();

				await expect(
					brokenLinksList.getByText(secondSpaceReferringTitle, {
						exact: true,
					})
				).toBeHidden();
			});

			await test.step('Open the broken links page of the first space', async () => {
				await page
					.getByRole('link', {name: 'View All Broken Links'})
					.click();

				await expect(page).toHaveURL(/broken-links\?groupId=/);

				await expect(
					page.getByText(referringTitle, {exact: true})
				).toBeVisible();
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

	test(
		'Lists the contents missing a required field added to their published content structure',
		{tag: '@LPD-110645'},
		async ({apiHelpers, page}) => {
			const spaceName = `space ${getRandomString()}`;
			const title = `content ${getRandomString()}`;

			let applicationName: string;
			let objectDefinitionId: number;
			let objectEntryId: number;

			await test.step('Publish a content of a versioned content structure', async () => {
				const space =
					await apiHelpers.headlessAssetLibrary.createAssetLibrary({
						name: spaceName,
						settings: {},
						type: 'Space',
					});

				const objectDefinitionAPIClient =
					await apiHelpers.buildRestClient(ObjectDefinitionAPI);

				const {body: objectDefinition} =
					await objectDefinitionAPIClient.postObjectDefinition({
						enableComments: true,
						enableFriendlyURLCustomization: true,
						enableIndexSearch: true,
						enableLocalization: true,
						enableObjectEntryDraft: true,
						enableObjectEntryHistory: true,
						enableObjectEntrySchedule: true,
						enableObjectEntryVersioning: true,
						externalReferenceCode: getRandomString(),
						label: {en_US: getRandomString()},
						name: `Structure${getRandomInt()}`,
						objectDefinitionSettings: [
							{
								name: 'acceptedGroupExternalReferenceCodes',
								value: space.externalReferenceCode,
							} as any,
						],
						objectFields: [
							{
								DBType: 'String',
								businessType: 'Text',
								externalReferenceCode: getRandomString(),
								indexed: true,
								indexedAsKeyword: false,
								indexedLanguageId: 'en_US',
								label: {en_US: 'Title'},
								localized: true,
								name: 'title',
								required: true,
							},
						],
						objectFolderExternalReferenceCode:
							'L_CMS_CONTENT_STRUCTURES',
						pluralLabel: {en_US: getRandomString()},
						scope: 'depot',
						status: {code: 0},
						titleObjectFieldName: 'title',
					});

				apiHelpers.data.push({
					id: objectDefinition.id as number,
					type: 'objectDefinition',
				});

				applicationName = (
					objectDefinition.restContextPath as string
				).replace(/^\/o\//, '');
				objectDefinitionId = objectDefinition.id as number;

				const objectEntry =
					await apiHelpers.objectEntry.postObjectEntry(
						{
							objectEntryFolderExternalReferenceCode:
								'L_CONTENTS',
							title,
						},
						applicationName,
						spaceName
					);

				objectEntryId = objectEntry.id;
			});

			await test.step('Add a required field to the published structure', async () => {
				await apiHelpers.objectAdmin.postObjectDefinitionObjectField(
					objectDefinitionId,
					{
						DBType: 'String',
						businessType: 'Text',
						externalReferenceCode: getRandomString(),
						indexed: true,
						indexedAsKeyword: false,
						indexedLanguageId: 'en_US',
						label: {en_US: 'Summary'},
						localized: true,
						name: 'summary',
						required: true,
					}
				);
			});

			await test.step('Check the dashboard lists the content with its missing field', async () => {
				const missingRequiredDataList = await openQualityList(page, {
					cardTitle: 'Missing Required Data',
					spaceName,
					text: title,
				});

				await expect(
					missingRequiredDataList.getByText(
						'1 Missing Required Field',
						{exact: true}
					)
				).toBeVisible();
			});

			await test.step('Open the missing required data page of the space', async () => {
				await page
					.getByRole('link', {name: 'View All Missing Required Data'})
					.click();

				await expect(page).toHaveURL(/missing-required-data\?groupId=/);

				await expect(
					page.getByText(title, {exact: true})
				).toBeVisible();
			});

			await test.step('Offer only the edit action in the page', async () => {
				await page
					.getByRole('button', {name: `${title} Actions`})
					.click();

				const editMenuItem = page.getByRole('menuitem', {name: 'Edit'});

				await expect(editMenuItem).toBeVisible();

				await expect(
					page.getByRole('menuitem', {name: 'View'})
				).toBeHidden();

				await editMenuItem.click();

				await expect(page.getByLabel('Title')).toHaveValue(title);
			});

			await test.step('Check the content leaves the list once the field is filled', async () => {
				const objectEntry =
					await apiHelpers.objectEntry.patchObjectEntry(
						{summary_i18n: {en_US: getRandomString()}},
						applicationName,
						objectEntryId
					);

				expect(objectEntry.id).toBe(objectEntryId);

				await openQualityList(page, {
					cardTitle: 'Missing Required Data',
					spaceName,
					text: 'No Missing Required Data',
				});
			});
		}
	);
});

test.describe('Duplication and Similarity section', () => {
	test(
		'Groups the duplicated topics of the selected space by the title they repeat',
		{tag: ['@LPD-102887', '@LPD-103246']},
		async ({apiHelpers, page}) => {
			const firstSpaceName = `space ${getRandomString()}`;
			const secondSpaceName = `space ${getRandomString()}`;

			const firstSpaceTitle = `duplicated ${getRandomString()}`;
			const secondSpaceTitles = [
				`Duplicated ${getRandomString()}`,
				`duplicated ${getRandomString()}`,
			];
			const uniqueTitle = `unique ${getRandomString()}`;

			const duplicateTopicsCard = page.getByRole('button', {
				name: /Duplicate Topics/,
			});

			const duplicateTopicsCount = duplicateTopicsCard
				.locator('.cms-dashboard__interactive-card__metric > div')
				.first();

			async function expectDuplicateTopicsCount(
				spaceName: string,
				count: string
			) {

				// The counter comes from a search aggregation, so the indexer may
				// still be behind. Reloading gives the picker a clean state and
				// refetches the counter for the space.

				await expect(async () => {
					await page.goto('/web/cms/dashboard');

					await selectSpace(page, spaceName);

					await expect(duplicateTopicsCount).toHaveText(count, {
						timeout: 10000,
					});
				}).toPass({timeout: 90000});
			}

			await test.step('Create contents that repeat a title in two spaces', async () => {
				for (const spaceName of [firstSpaceName, secondSpaceName]) {
					await apiHelpers.headlessAssetLibrary.createAssetLibrary({
						name: spaceName,
						type: 'Space',
					});
				}

				const postContent = (spaceName: string, title: string) =>
					apiHelpers.objectEntry.postObjectEntry(
						{
							objectEntryFolderExternalReferenceCode:
								'L_CONTENTS',
							title,
						},
						APPLICATION_NAME,
						spaceName
					);

				const contents = [
					await postContent(firstSpaceName, firstSpaceTitle),
					await postContent(firstSpaceName, firstSpaceTitle),
					await postContent(firstSpaceName, uniqueTitle),
					await postContent(secondSpaceName, secondSpaceTitles[0]),
					await postContent(secondSpaceName, secondSpaceTitles[0]),
					await postContent(secondSpaceName, secondSpaceTitles[0]),
					await postContent(secondSpaceName, secondSpaceTitles[1]),
					await postContent(secondSpaceName, secondSpaceTitles[1]),
				];

				for (const content of contents) {
					apiHelpers.data.push({id: content.id, type: 'document'});

					expect(content.id).toBeTruthy();
				}
			});

			await test.step('Count only the contents of the selected space', async () => {
				await expectDuplicateTopicsCount(firstSpaceName, '2');

				await expectDuplicateTopicsCount(secondSpaceName, '5');
			});

			await test.step('Review the duplicates grouped by their title', async () => {
				await duplicateTopicsCard.click();

				const modal = page.locator('.modal-full-screen .modal-content');

				await expect(
					modal.getByText(`${secondSpaceTitles[0]} (3)`)
				).toBeVisible();

				await expect(
					modal.getByText(`${secondSpaceTitles[1]} (2)`)
				).toBeVisible();

				await expect(
					modal.getByText(secondSpaceTitles[0], {exact: true})
				).toHaveCount(3);

				await expect(modal.getByText(uniqueTitle)).toHaveCount(0);

				await expect(
					modal.getByRole('button', {name: /^Edit /})
				).toHaveCount(5);

				// Editing is the only action a row offers.

				await expect(
					modal.locator('.list-group-item button')
				).toHaveCount(5);
			});
		}
	);
});

test.describe('Operations section', () => {
	test(
		'Shows the distribution of contents by status for the selected space',
		{tag: '@LPD-97421'},
		async ({apiHelpers, page}) => {
			const spaceName = `space ${getRandomString()}`;

			let approvedTitle: string;
			let draftTitle: string;

			await test.step('Create contents in every status', async () => {
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: spaceName,
					type: 'Space',
				});

				const postContent = async (body: object = {}) => {
					const content =
						await apiHelpers.objectEntry.postObjectEntry(
							{
								displayDate: PAST_DATE,
								objectEntryFolderExternalReferenceCode:
									'L_CONTENTS',
								title: getRandomString(),
								...body,
							},
							APPLICATION_NAME,
							spaceName
						);

					apiHelpers.data.push({id: content.id, type: 'document'});

					expect(content.id).toBeTruthy();

					return content;
				};

				const approvedContent = await postContent();

				approvedTitle = approvedContent.title;

				await postContent();

				const draftContent = await postContent({status: {code: 2}});

				draftTitle = draftContent.title;

				await postContent({displayDate: getUpcomingDate(10)});

				const expiredContent = await postContent();

				await apiHelpers.objectEntry.expireObjectEntryByExternalReferenceCode(
					APPLICATION_NAME,
					spaceName,
					expiredContent.externalReferenceCode
				);
			});

			const contentProgressCard = page
				.locator('.cms-dashboard__base-card')
				.filter({hasText: 'Content Progress'});

			await test.step('Check the distribution in the chart', async () => {

				// Retry with a reload to absorb the search index lag the
				// status facet reads from

				await expect(async () => {
					await page.goto('/web/cms/dashboard');

					await selectSpace(page, spaceName);

					await expect(
						contentProgressCard.getByRole('link', {
							name: 'Approved: 2',
						})
					).toBeVisible();
				}).toPass();

				await expect(
					contentProgressCard.getByRole('link', {name: 'Draft: 1'})
				).toBeVisible();

				await expect(
					contentProgressCard.getByRole('link', {
						name: 'Scheduled: 1',
					})
				).toBeVisible();

				await expect(
					contentProgressCard.getByRole('link', {name: 'Others: 1'})
				).toBeVisible();

				await expect(
					contentProgressCard.getByLabel(/Pending/)
				).toBeHidden();
			});

			await test.step('Open the All section filtered by draft', async () => {
				await contentProgressCard
					.getByRole('link', {name: 'Draft: 1'})
					.click();

				await expect(page).toHaveURL(
					/allSection_fdsConfig=.*filters.*status/
				);

				await expect(
					page.getByText(draftTitle, {exact: true})
				).toBeVisible();

				await expect(
					page.getByText(approvedTitle, {exact: true})
				).toBeHidden();
			});
		}
	);

	test(
		'Lists the drafts of the selected space not modified for more than 30 days',
		{tag: '@LPD-101600'},
		async ({apiHelpers, browser, page}) => {
			const spaceName = `space ${getRandomString()}`;
			const longStandingDraftTitle = `draft ${getRandomString()}`;
			const recentDraftTitle = `draft ${getRandomString()}`;

			await test.step('Create a long-standing draft and a recent one', async () => {
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: spaceName,
					type: 'Space',
				});

				const drafts = [];

				for (const title of [
					longStandingDraftTitle,
					recentDraftTitle,
				]) {
					const draft = await apiHelpers.objectEntry.postObjectEntry(
						{
							objectEntryFolderExternalReferenceCode:
								'L_CONTENTS',
							status: {code: 2},
							title,
						},
						APPLICATION_NAME,
						spaceName
					);

					apiHelpers.data.push({id: draft.id, type: 'document'});

					expect(draft.id).toBeTruthy();

					drafts.push(draft);
				}

				const adminContext = await browser.newContext();

				try {
					const adminPage = await adminContext.newPage();

					await performLoginViaApi({
						page: adminPage,
						screenName: 'test',
					});

					const serverAdministrationPage =
						new ServerAdministrationPage(adminPage);

					await serverAdministrationPage.goto();

					await serverAdministrationPage.executeScript(
						getLongStandingDraftScript(drafts[0].id)
					);
				}
				finally {
					await adminContext.close();
				}
			});

			const card = page.getByRole('button', {
				name: /^Long-Standing Drafts/,
			});

			await test.step('Count the long-standing draft in the card', async () => {
				await expect(async () => {
					await page.goto('/web/cms/dashboard');

					await selectSpace(page, spaceName);

					await expect(card).toContainText('50% of 2 Assets', {
						timeout: 5000,
					});
				}).toPass();
			});

			const list = page.getByRole('region', {
				name: 'Long-Standing Drafts',
			});

			await test.step('Expand the list of long-standing drafts', async () => {
				await card.click();

				await expect(card).toHaveAttribute('aria-expanded', 'true');

				await expect(
					list.getByRole('link', {name: longStandingDraftTitle})
				).toBeVisible();

				await expect(
					list.getByText(
						`${LONG_STANDING_DRAFT_AGE_DAYS} days in draft`
					)
				).toBeVisible();

				await expect(
					list.getByText(recentDraftTitle, {exact: true})
				).toBeHidden();
			});

			await test.step('Collapse the list', async () => {
				await card.click();

				await expect(card).toHaveAttribute('aria-expanded', 'false');

				await expect(list).toBeHidden();

				await card.click();
			});

			await test.step('Open the All section filtered by draft and modified date', async () => {
				await list
					.getByRole('link', {name: 'View All Long-Standing Drafts'})
					.click();

				await expect(page).toHaveURL(
					/allSection_fdsConfig=.*status.*dateModified/
				);

				await expect(
					page.getByText(longStandingDraftTitle, {exact: true})
				).toBeVisible();

				await expect(
					page.getByText(recentDraftTitle, {exact: true})
				).toBeHidden();
			});
		}
	);
});
