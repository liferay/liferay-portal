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
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	structureBuilderPagesTest
);

test(
	'A translated Event structured content shows localized fields per language to a USER changing the URL locale',
	{tag: ['@LPD-95528', '@LPD-95528/TC-5.b']},
	async ({
		apiHelpers,
		browser,
		page,
		pageEditorPage,
		site,
		structureBuilderPage,
	}) => {
		test.setTimeout(240000);

		const spaceName = `Space ${getRandomString()}`;
		const structureLabel = `Event ${getRandomString()}`;
		const structureName = `Event${getRandomInt()}`;
		const titleEn = `TitleEN ${getRandomString()}`;
		const titleEs = `TitleES ${getRandomString()}`;
		const bodyEn = `BodyEN ${getRandomString()}`;
		const bodyEs = `BodyES ${getRandomString()}`;
		const numericValue = 100000 + (getRandomInt() % 800000);

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const objectDefinitionId =
			await test.step('Build a custom Event structure (localizable Body text + Numeric)', async () => {
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

				await structureBuilderPage.addField('Numeric');

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

		const numericField = objectDefinition.objectFields.find(
			(objectField) =>
				objectField.label && objectField.label.en_US === 'Numeric'
		);

		if (!bodyField || !numericField) {
			throw new Error(
				'The "Body" or "Numeric" field was not found in the object definition'
			);
		}

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				[`${bodyField.name}_i18n`]: {en_US: bodyEn, es_ES: bodyEs},
				[numericField.name]: numericValue,
				[`${objectDefinition.titleObjectFieldName}_i18n`]: {
					en_US: titleEn,
					es_ES: titleEs,
				},
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			},
			applicationName,
			spaceName
		);

		const companyId = String(
			await page.evaluate(() => Liferay.ThemeDisplay.getCompanyId())
		);

		const guestRole = await apiHelpers.jsonWebServicesRole.getRole(
			companyId,
			'Guest'
		);

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			['VIEW'],
			companyId,
			String(space.siteId),
			objectDefinition.className,
			String(entry.id),
			String(guestRole.roleId)
		);

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.jsonWebServicesUser.addGroupUsers(String(site.id), [
			user.id,
		]);

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><p><lfr-editable id="body" type="text">Body</lfr-editable></p><h2><lfr-editable id="numeric" type="text">Numeric</lfr-editable></h2></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map the Title, Body and Numeric fields into the page fragment and publish', async () => {
			await pageEditorPage.selectEditable(fragmentId, 'title');

			await pageEditorPage.setMappingConfiguration({
				mapping: {entity: entityLabel, entry: titleEn, field: 'Title'},
			});

			await pageEditorPage.selectEditable(fragmentId, 'body');

			await pageEditorPage.setMappingConfiguration({
				mapping: {entity: entityLabel, entry: titleEn, field: 'Body'},
			});

			await pageEditorPage.selectEditable(fragmentId, 'numeric');

			await pageEditorPage.setMappingConfiguration({
				mapping: {
					entity: entityLabel,
					entry: titleEn,
					field: 'Numeric',
				},
			});

			await pageEditorPage.publishPage();
		});

		const userContext = await browser.newContext();

		const userPage = await userContext.newPage();

		try {
			await performLoginViaApi({
				page: userPage,
				screenName: user.alternateName,
			});

			await test.step('The USER sees the Spanish translation at /es/', async () => {
				await expect(async () => {
					await userPage.goto(`/es${viewUrl}`);

					await expect(
						userPage.getByText(titleEs, {exact: true})
					).toBeVisible({timeout: 5000});

					await expect(
						userPage.getByText(bodyEs, {exact: true})
					).toBeVisible({timeout: 5000});
				}).toPass({timeout: 30000});
			});

			await test.step('The USER sees the English translation at /en/', async () => {
				await expect(async () => {
					await userPage.goto(`/en${viewUrl}`);

					await expect(
						userPage.getByText(titleEn, {exact: true})
					).toBeVisible({timeout: 5000});

					await expect(
						userPage.getByText(bodyEn, {exact: true})
					).toBeVisible({timeout: 5000});
				}).toPass({timeout: 30000});
			});
		}
		finally {
			await userContext.close();
		}
	}
);
