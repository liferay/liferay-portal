/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi} from '../../../../utils/performLogin';
import {waitForModal} from '../../../../utils/waitFor';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {AssetsPage} from '../../../site-cms-site-initializer/main/pages/AssetsPage';
import {
	addCMSAdministratorUser,
	disconnectSite,
	execBulkAction,
} from './utils/bulkOperations';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

const APPLICATION_NAME = 'cms/basic-documents';

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

test(
	'Bulk assigned categories and tags make files discoverable in site search for GUEST',
	{tag: ['@LPD-95537', '@LPD-95537/TC-14.f']},
	async ({apiHelpers, browser}) => {
		test.setTimeout(600000);

		const spaceName = `Space ${getRandomString()}`;
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

			const entries = [];

			for (let i = 0; i < 2; i++) {
				const entry = await apiHelpers.objectEntry.postObjectEntry(
					{
						file: {
							fileBase64: imageBase64,
							name: `${getRandomString()}.jpg`,
						},
						objectEntryFolderExternalReferenceCode: 'L_FILES',
						title: `Image ${getRandomString()}`,
					},
					APPLICATION_NAME,
					spaceName
				);

				await apiHelpers.objectEntry.putObjectEntryPermissions(
					APPLICATION_NAME,
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

				entries.push(entry);
			}

			const fileTitles = entries.map((entry) => entry.title);

			const caContext = await browser.newContext();

			const caPage = await caContext.newPage();

			try {
				await performLoginViaApi({
					page: caPage,
					screenName: cmsAdministrator.alternateName,
				});

				const caAssetsPage = new AssetsPage(caPage);

				await test.step('The CMS Administrator bulk assigns a category to both files', async () => {
					await caAssetsPage.gotoFiles();

					await caAssetsPage.selectItems(fileTitles);

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

				await test.step('The CMS Administrator bulk assigns a tag to both files', async () => {
					await caAssetsPage.gotoFiles();

					await caAssetsPage.selectItems(fileTitles);

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

			await test.step('Both files carry the category and the tag', async () => {
				for (const entry of entries) {
					await expect(async () => {
						const updatedEntry =
							await apiHelpers.objectEntry.getObjectEntryById(
								APPLICATION_NAME,
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

			await test.step('GUEST finds both files in site search by category and by tag', async () => {
				const guestContext = await browser.newContext({
					storageState: {cookies: [], origins: []},
				});

				const guestPage = await guestContext.newPage();

				try {
					for (const searchTerm of [categoryName, tagName]) {
						await expect(async () => {
							await guestPage.goto(
								`/web/guest/search?q=${searchTerm}`,
								{waitUntil: 'domcontentloaded'}
							);

							for (const title of fileTitles) {
								await expect(
									guestPage
										.getByText(title, {exact: true})
										.first()
								).toBeVisible({timeout: 3000});
							}
						}).toPass({timeout: 90000});
					}
				}
				finally {
					await guestContext.close();
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
