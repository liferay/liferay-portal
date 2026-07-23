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
	'A custom Structured Content entry mapped to page fragments renders and live-updates for GUEST and USER',
	{tag: ['@LPD-95525', '@LPD-95525/TC-2.b']},
	async ({
		apiHelpers,
		browser,
		pageEditorPage,
		site,
		structureBuilderPage,
	}) => {
		test.setTimeout(240000);

		const spaceName = `Space ${getRandomString()}`;
		const structureLabel = `Event ${getRandomString()}`;
		const structureName = `Event${getRandomInt()}`;
		const titleValue = `Title ${getRandomString()}`;
		const bodyValue = `Body ${getRandomString()}`;

		// The Numeric field renders unformatted (no thousands separator), so the
		// rendered text matches String(numericValue) exactly. Compare against the
		// formatted value if the field is ever configured with a separator.

		const numericValue = 100000 + (getRandomInt() % 800000);
		const updatedTitle = `Title ${getRandomString()}`;
		const updatedBody = `Body ${getRandomString()}`;
		const updatedNumeric = 900000 + (getRandomInt() % 99999);

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const objectDefinitionId =
			await test.step('Build a custom Event structure (Body text + Numeric)', async () => {
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
				'Body or Numeric field not found in object definition'
			);
		}

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				[bodyField.name]: bodyValue,
				[numericField.name]: numericValue,
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
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><p><lfr-editable id="body" type="text">Body</lfr-editable></p><p><lfr-editable id="numeric" type="text">Numeric</lfr-editable></p></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map the Title, Body and Numeric fields into the page fragment and publish', async () => {

			// The entry is picked by title in the editor's mapping dialog, which
			// depends on the search index. Retry until the entry is selectable.

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

				await pageEditorPage.selectEditable(fragmentId, 'numeric');

				await pageEditorPage.setMappingConfiguration({
					mapping: {
						entity: entityLabel,
						entry: titleValue,
						field: 'Numeric',
					},
				});
			}).toPass({timeout: 30000});

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();
		});

		await test.step('GUEST sees the mapped fields, then the CMS edit without re-publishing', async () => {
			const guestContext = await browser.newContext();

			const guestPage = await guestContext.newPage();

			try {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(titleValue, {exact: true})
					).toBeVisible({timeout: 2000});

					await expect(
						guestPage.getByText(bodyValue, {exact: true})
					).toBeVisible({timeout: 2000});

					await expect(
						guestPage.getByText(String(numericValue), {
							exact: true,
						})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});

				await apiHelpers.objectEntry.putObjectEntry(
					{
						[bodyField.name]: updatedBody,
						[numericField.name]: updatedNumeric,
						[objectDefinition.titleObjectFieldName]: updatedTitle,
					},
					applicationName,
					entry.id
				);

				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(updatedTitle, {exact: true})
					).toBeVisible({timeout: 2000});

					await expect(
						guestPage.getByText(updatedBody, {exact: true})
					).toBeVisible({timeout: 2000});

					await expect(
						guestPage.getByText(String(updatedNumeric), {
							exact: true,
						})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});
			}
			finally {
				await guestContext.close();
			}
		});

		await test.step('USER sees the updated mapped content', async () => {
			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
			await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

			await apiHelpers.jsonWebServicesUser.addGroupUsers(
				String(site.id),
				[user.id]
			);

			const userContext = await browser.newContext();

			const userPage = await userContext.newPage();

			try {
				await performLoginViaApi({
					page: userPage,
					screenName: user.alternateName,
				});

				await expect(async () => {
					await userPage.goto(viewUrl);

					await expect(
						userPage.getByText(updatedTitle, {exact: true})
					).toBeVisible({timeout: 2000});

					await expect(
						userPage.getByText(updatedBody, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});
			}
			finally {
				await userContext.close();
			}
		});
	}
);
