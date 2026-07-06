/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi} from '../../../../utils/performLogin';
import {AssetsPage} from '../../../site-cms-site-initializer/main/pages/AssetsPage';
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';
import {
	addSpaceUserWithSession,
	bulkMoveToFolder,
	openFolder,
} from './utils/bulkOperations';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest(),
	structureBuilderPagesTest
);

const BASIC_WEB_CONTENT_APPLICATION_NAME = 'cms/basic-web-contents';

test(
	'A Space Administrator bulk moves Basic Web Content and Structured Content entries to another folder',
	{tag: ['@LPD-95537', '@LPD-95537/TC-14.a']},
	async ({apiHelpers, browser, structureBuilderPage}) => {
		test.setTimeout(480000);

		const spaceName = `Space ${getRandomString()}`;
		const originFolderName = `Origin ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const structureLabel = `Event ${getRandomString()}`;
		const structureName = `Event${getRandomInt()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const spaceAdministrator = await addSpaceUserWithSession(
			apiHelpers,
			space.externalReferenceCode,
			'Asset Library Administrator'
		);

		const originFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: spaceName,
				title: originFolderName,
			});

		const destinationFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: spaceName,
				title: destinationFolderName,
			});

		const objectDefinitionId =
			await test.step('Build a custom Event structure', async () => {
				const id = await structureBuilderPage.createStructureFromData({
					label: structureLabel,
					name: structureName,
					page: structureBuilderPage,
					publish: true,
					spaces: [spaceName],
				});

				return id;
			});

		const objectDefinition = await apiHelpers.get(
			`${apiHelpers.baseUrl}object-admin/v1.0/object-definitions/${objectDefinitionId}`
		);

		const structuredContentApplicationName =
			objectDefinition.restContextPath.replace('/o/', '');

		const entries = [];

		for (const applicationName of [
			BASIC_WEB_CONTENT_APPLICATION_NAME,
			BASIC_WEB_CONTENT_APPLICATION_NAME,
			structuredContentApplicationName,
			structuredContentApplicationName,
		]) {
			const titleField =
				applicationName === BASIC_WEB_CONTENT_APPLICATION_NAME
					? 'title'
					: objectDefinition.titleObjectFieldName;

			entries.push({
				applicationName,
				entry: await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode:
							originFolder.externalReferenceCode,
						[titleField]: `Title ${getRandomString()}`,
					},
					applicationName,
					spaceName
				),
			});
		}

		const entryTitles = entries.map(({entry}) => entry.title);

		const spaContext = await browser.newContext();

		const spaPage = await spaContext.newPage();

		try {
			await performLoginViaApi({
				page: spaPage,
				screenName: spaceAdministrator.alternateName,
			});

			const spaAssetsPage = new AssetsPage(spaPage);

			await test.step('The Space Administrator bulk moves the four entries', async () => {
				await spaAssetsPage.gotoContents();

				await openFolder(spaPage, originFolderName);

				await spaAssetsPage.selectItems(entryTitles);

				await bulkMoveToFolder(spaPage, {
					destinationFolder: destinationFolderName,
					destinationSpace: spaceName,
				});
			});

			await test.step('All entries sit in the destination folder', async () => {
				await expect(async () => {
					const folderIdsByTitle = [];

					for (const {applicationName, entry} of entries) {
						const movedEntry =
							await apiHelpers.objectEntry.getObjectEntryById(
								applicationName,
								String(entry.id)
							);

						folderIdsByTitle.push({
							folderId: movedEntry.objectEntryFolderId,
							title: entry.title,
						});
					}

					expect(folderIdsByTitle).toEqual(
						entries.map(({entry}) => ({
							folderId: destinationFolder.id,
							title: entry.title,
						}))
					);
				}).toPass({timeout: 60000});
			});

			await test.step('The entries are gone from the origin folder and listed in the destination', async () => {
				await spaAssetsPage.gotoContents();

				await openFolder(spaPage, originFolderName);

				for (const title of entryTitles) {
					await expect(
						spaPage.getByRole('link', {exact: true, name: title})
					).toBeHidden({timeout: 5000});
				}

				await spaAssetsPage.gotoContents();

				await openFolder(spaPage, destinationFolderName);

				for (const title of entryTitles) {
					await expect(
						spaPage.getByRole('link', {exact: true, name: title})
					).toBeVisible({timeout: 5000});
				}
			});
		}
		finally {
			await spaContext.close();
		}
	}
);
