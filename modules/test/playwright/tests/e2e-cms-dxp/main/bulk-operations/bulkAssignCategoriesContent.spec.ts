/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi, userData} from '../../../../utils/performLogin';
import {waitForModal} from '../../../../utils/waitFor';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {AssetsPage} from '../../../site-cms-site-initializer/main/pages/AssetsPage';
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';
import {
	addCMSAdministratorUser,
	clearFirstSignInWalls,
	disconnectSite,
	execBulkAction,
} from './utils/bulkOperations';

const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	structureBuilderPagesTest
);

const BASIC_WEB_CONTENT_APPLICATION_NAME = 'cms/basic-web-contents';

test(
	'Bulk assigned categories and tags make content entries discoverable in site search for USER',
	{tag: ['@LPD-95537', '@LPD-95537/TC-14.e']},
	async ({apiHelpers, browser, structureBuilderPage}) => {
		test.setTimeout(600000);

		const spaceName = `Space ${getRandomString()}`;
		const structureLabel = `Event ${getRandomString()}`;
		const structureName = `Event${getRandomInt()}`;
		const categoryName = `category${getRandomInt()}`;
		const tagName = `tag${getRandomInt()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const guestSite = await apiHelpers.headlessAdminSite.getSite('L_GUEST');

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			guestSite.externalReferenceCode,
			{searchable: true}
		);

		try {
			const cmsAdministrator = await addCMSAdministratorUser(apiHelpers);

			const cmsSite =
				await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
					'cms'
				);

			const vocabulary =
				await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary(
					{
						assetLibraries: [{id: -1}],
						assetTypes: [
							{
								required: false,
								subtype: 'AllAssetSubtypes',
								type: 'AllAssetTypes',
							},
						],
						name: `Vocabulary ${getRandomString()}`,
						siteId: cmsSite.id,
						visibilityType: 'PUBLIC',
					}
				);

			await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
				{
					name: categoryName,
					vocabularyId: vocabulary.id,
				}
			);

			const tag = await apiHelpers.headlessAdminTaxonomy.postSiteKeyword({
				name: tagName,
				siteId: cmsSite.id,
			});

			apiHelpers.data.push({
				id: tag.id,
				type: 'keyword',
			});

			const objectDefinitionId =
				await test.step('Build a custom Event structure', async () => {
					return await structureBuilderPage.createStructureFromData({
						label: structureLabel,
						name: structureName,
						page: structureBuilderPage,
						publish: true,
						spaces: [spaceName],
					});
				});

			const objectDefinition = await apiHelpers.get(
				`${apiHelpers.baseUrl}object-admin/v1.0/object-definitions/${objectDefinitionId}`
			);

			const structuredContentApplicationName =
				objectDefinition.restContextPath.replace('/o/', '');

			const entries = [];

			for (const applicationName of [
				BASIC_WEB_CONTENT_APPLICATION_NAME,
				structuredContentApplicationName,
			]) {
				const titleField =
					applicationName === BASIC_WEB_CONTENT_APPLICATION_NAME
						? 'title'
						: objectDefinition.titleObjectFieldName;

				const entry = await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						[titleField]: `Title ${getRandomString()}`,
					},
					applicationName,
					spaceName
				);

				await apiHelpers.objectEntry.putObjectEntryPermissions(
					applicationName,
					entry.id,
					[
						{
							actionIds: ['DELETE', 'UPDATE', 'VIEW'],
							roleName: 'CMS Administrator',
						},
						{actionIds: ['VIEW'], roleName: 'Guest'},
						{actionIds: ['VIEW'], roleName: 'User'},
					]
				);

				entries.push({applicationName, entry});
			}

			const entryTitles = entries.map(({entry}) => entry.title);

			const caContext = await browser.newContext();

			const caPage = await caContext.newPage();

			try {
				await performLoginViaApi({
					page: caPage,
					screenName: cmsAdministrator.alternateName,
				});

				const caAssetsPage = new AssetsPage(caPage);

				await test.step('The CMS Administrator bulk assigns a category to both entries', async () => {
					await caAssetsPage.gotoContents();

					await caAssetsPage.selectItems(entryTitles);

					await execBulkAction(caPage, 'Edit Categories');

					await waitForModal({page: caPage});

					await caPage
						.getByPlaceholder('Add category')
						.fill(categoryName);

					await caPage
						.getByRole('option', {name: categoryName})
						.click();

					await expect(
						caPage.locator('.label-item', {hasText: categoryName})
					).toBeAttached({timeout: 5000});

					await caPage.getByRole('button', {name: 'Save'}).click();

					await waitForAlert(
						caPage,
						'Info:Categories update action started for 2 assets.',
						{type: 'info'}
					);
				});

				await test.step('The CMS Administrator bulk assigns a tag to both entries', async () => {
					await caAssetsPage.gotoContents();

					await caAssetsPage.selectItems(entryTitles);

					await execBulkAction(caPage, 'Edit Tags');

					await waitForModal({page: caPage});

					await caPage.getByPlaceholder('Add tag').fill(tagName);

					await caPage
						.getByRole('option', {exact: true, name: tagName})
						.click();

					await expect(
						caPage.locator('.label-item', {hasText: tagName})
					).toBeAttached({timeout: 5000});

					await caPage.getByRole('button', {name: 'Save'}).click();

					await waitForAlert(
						caPage,
						'Info:Tags update action started for 2 assets.',
						{type: 'info'}
					);
				});
			}
			finally {
				await caContext.close();
			}

			await test.step('Both entries carry the category and the tag', async () => {
				for (const {applicationName, entry} of entries) {
					await expect(async () => {
						const updatedEntry =
							await apiHelpers.objectEntry.getObjectEntryById(
								applicationName,
								String(entry.id)
							);

						const categoryNames = (
							updatedEntry.taxonomyCategoryBriefs || []
						).map((brief) => brief.taxonomyCategoryName);

						expect(categoryNames).toContain(categoryName);

						expect(updatedEntry.keywords || []).toContain(tagName);
					}).toPass({timeout: 60000});
				}
			});

			await test.step('USER finds both entries in site search by category and by tag', async () => {
				const user =
					await apiHelpers.headlessAdminUser.postUserAccount();

				userData[user.alternateName] = {
					name: user.givenName,
					password: 'test',
					surname: user.familyName,
				};

				await clearFirstSignInWalls(apiHelpers, user);

				await apiHelpers.jsonWebServicesUser.addGroupUsers(
					String(guestSite.id),
					[user.id]
				);

				const userContext = await browser.newContext();

				const userPage = await userContext.newPage();

				try {
					await performLoginViaApi({
						page: userPage,
						screenName: user.alternateName,
					});

					for (const searchTerm of [categoryName, tagName]) {
						await expect(async () => {
							await userPage.goto(
								`/web/guest/search?q=${searchTerm}`,
								{waitUntil: 'domcontentloaded'}
							);

							for (const title of entryTitles) {
								await expect(
									userPage
										.getByText(title, {exact: true})
										.first()
								).toBeVisible({timeout: 3000});
							}
						}).toPass({timeout: 90000});
					}
				}
				finally {
					await userContext.close();
				}
			});
		}
		finally {
			await disconnectSite(
				apiHelpers,
				space.externalReferenceCode,
				guestSite.externalReferenceCode
			);
		}
	}
);
