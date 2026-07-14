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
import {AssetsPage} from '../../../site-cms-site-initializer/main/pages/AssetsPage';
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';
import {openVersionHistory, restoreVersionByNumber} from './utils/versioning';

const test = mergeTests(
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
	'A CMS Administrator restores a previous Structured Content version and its localizable field renders for USER',
	{tag: ['@LPD-95538', '@LPD-95538/TC-15.b']},
	async ({
		apiHelpers,
		browser,
		page,
		pageEditorPage,
		site,
		structureBuilderPage,
	}) => {
		test.setTimeout(360000);

		const spaceName = `Space ${getRandomString()}`;
		const structureLabel = `Event ${getRandomString()}`;
		const structureName = `Event${getRandomInt()}`;
		const titleValue = `Title ${getRandomString()}`;
		const bodyV1 = `Body ${getRandomString()}`;
		const bodyV3 = `Body ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const objectDefinitionId =
			await test.step('Build a structure with a localizable Body field', async () => {
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
					localizable: true,
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
				[bodyField.name]: bodyV1,
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

		await test.step('Map the Title and localizable Body into the page fragment and publish', async () => {
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

		await test.step('Edit the entry twice to generate versions 2 and 3', async () => {
			await apiHelpers.objectEntry.patchObjectEntry(
				{
					[`${bodyField.name}_i18n`]: {
						en_US: `Body ${getRandomString()}`,
					},
				},
				applicationName,
				entry.id
			);

			await apiHelpers.objectEntry.patchObjectEntry(
				{[`${bodyField.name}_i18n`]: {en_US: bodyV3}},
				applicationName,
				entry.id
			);
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

			await test.step('USER sees the current localizable Body value', async () => {
				await expect(async () => {
					await userPage.goto(viewUrl);

					await expect(
						userPage.getByText(titleValue, {exact: true})
					).toBeVisible({timeout: 2000});

					await expect(
						userPage.getByText(bodyV3, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});
			});

			const assetsPage = new AssetsPage(page);

			await test.step('The CMS Administrator restores version 1', async () => {
				await assetsPage.gotoContents();

				await openVersionHistory(assetsPage, titleValue);

				await expect(page.locator('tbody tr')).toHaveCount(3, {
					timeout: 10000,
				});

				await restoreVersionByNumber(page, '1');
			});

			await test.step('USER sees the restored version 1 localizable Body value', async () => {
				await expect(async () => {
					await userPage.goto(viewUrl);

					await expect(
						userPage.getByText(bodyV1, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});

				await expect(
					userPage.getByText(bodyV3, {exact: true})
				).toBeHidden({timeout: 2000});
			});
		}
		finally {
			await userContext.close();
		}
	}
);
