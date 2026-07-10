/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {addMappingFragment} from '../../../../utils/addMappingFragment';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi, userData} from '../../../../utils/performLogin';
import {cmsPagesTest} from '../../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';
import {deleteEntryToRecycleBin, restoreEntry} from './utils/recycleBin';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	structureBuilderPagesTest
);

test(
	'A CMS Administrator restores a deleted Structured Content from the Recycle Bin and it renders again for USER',
	{tag: ['@LPD-95539', '@LPD-95539/TC-16.b']},
	async ({
		apiHelpers,
		browser,
		contentsPage,
		page,
		pageEditorPage,
		recycleBinPage,
		site,
		structureBuilderPage,
	}) => {
		test.setTimeout(360000);

		const spaceName = `Space ${getRandomString()}`;
		const structureLabel = `Event ${getRandomString()}`;
		const structureName = `Event${getRandomInt()}`;
		const titleValue = `Title ${getRandomString()}`;
		const bodyValue = `Body ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const objectDefinitionId =
			await test.step('Build a custom structure with a Body field', async () => {
				const id = await structureBuilderPage.createStructureFromData({
					label: structureLabel,
					name: structureName,
					page: structureBuilderPage,
					publish: false,
					spaces: [spaceName],
				});

				await structureBuilderPage.addField('Text');
				await structureBuilderPage.selectFields([{label: 'Text'}]);
				await structureBuilderPage.changeFieldSettings({
					label: 'Body',
				});

				await structureBuilderPage.publishStructure();

				return id;
			});

		const objectDefinition = await apiHelpers.get(
			`${apiHelpers.baseUrl}object-admin/v1.0/object-definitions/${objectDefinitionId}`
		);

		const applicationName = objectDefinition.restContextPath.replace(
			'/o/',
			''
		);

		const entityLabel = `${objectDefinition.pluralLabel.en_US} (CMS)`;

		const bodyField = objectDefinition.objectFields.find(
			(objectField) =>
				objectField.label && objectField.label.en_US === 'Body'
		);

		if (!bodyField) {
			throw new Error('Body field not found in object definition');
		}

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				[bodyField.name]: bodyValue,
				[objectDefinition.titleObjectFieldName]: titleValue,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			},
			applicationName,
			spaceName
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			applicationName,
			entry.id,
			[
				{actionIds: ['VIEW'], roleName: 'Guest'},
				{actionIds: ['VIEW'], roleName: 'User'},
			]
		);

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><p><lfr-editable id="body" type="text">Body</lfr-editable></p></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map the Title and Body into the page fragment and publish', async () => {
			await expect(async () => {
				await pageEditorPage.selectEditable(fragmentId, 'title');

				await pageEditorPage.setMappingConfiguration({
					mapping: {
						entity: entityLabel,
						entry: titleValue,
						field: 'Title',
					},
				});

				await pageEditorPage.selectEditable(fragmentId, 'body');

				await pageEditorPage.setMappingConfiguration({
					mapping: {
						entity: entityLabel,
						entry: titleValue,
						field: 'Body',
					},
				});
			}).toPass({timeout: 30000});

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
		await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);
		await apiHelpers.jsonWebServicesUser.addGroupUsers(String(site.id), [
			user.id,
		]);

		const userContext = await browser.newContext();

		const userPage = await userContext.newPage();

		try {
			await performLoginViaApi({
				page: userPage,
				screenName: user.alternateName,
			});

			await test.step('USER sees the published content', async () => {
				await expect(async () => {
					await userPage.goto(viewUrl);

					await expect(
						userPage.getByText(bodyValue, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});
			});

			await test.step('The CMS Administrator deletes the content into the Recycle Bin', async () => {
				await contentsPage.goto();

				await deleteEntryToRecycleBin(page, titleValue);
			});

			await test.step('The CMS Administrator restores the content from the Recycle Bin', async () => {
				await recycleBinPage.goto();

				await restoreEntry(recycleBinPage, titleValue);
			});

			await test.step('USER sees the restored content again on the DXP page', async () => {
				await expect(async () => {
					await userPage.goto(viewUrl);

					await expect(
						userPage.getByText(bodyValue, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});
			});
		}
		finally {
			await userContext.close();
		}
	}
);
