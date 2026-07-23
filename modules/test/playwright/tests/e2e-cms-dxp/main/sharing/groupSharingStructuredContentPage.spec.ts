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
import {performLoginViaApi} from '../../../../utils/performLogin';
import {createRecipient} from '../../../../utils/sharingRecipient';
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
	'A group-shared Event structured content mapped to a page is visible to group members but not to guests',
	{tag: ['@LPD-95527', '@LPD-95527/TC-4.e']},
	async ({
		apiHelpers,
		browser,
		pageEditorPage,
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

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		const member = await createRecipient(apiHelpers);

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[String(member.id)]
		);

		const objectDefinitionId =
			await test.step('Build a custom Event structure (localizable Body text)', async () => {
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
				[bodyField.name]: bodyValue,
				[objectDefinition.titleObjectFieldName]: titleValue,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			},
			applicationName,
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntryCollaborators(
			[
				{
					actionIds: ['VIEW'],
					id: userGroup.id,
					share: false,
					type: 'UserGroup',
				},
			],
			applicationName,
			entry.id
		);

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><p><lfr-editable id="body" type="text">Body</lfr-editable></p></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map the shared Title and Body into the page fragment and publish', async () => {
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

			await pageEditorPage.publishPage();
		});

		await test.step('A group member sees the shared content on the page', async () => {
			const memberContext = await browser.newContext();

			const memberPage = await memberContext.newPage();

			try {
				await performLoginViaApi({
					page: memberPage,
					screenName: member.alternateName,
				});

				await expect(async () => {
					await memberPage.goto(viewUrl);

					await expect(
						memberPage.getByText(titleValue, {exact: true})
					).toBeVisible({timeout: 5000});
				}).toPass({timeout: 120000});
			}
			finally {
				await memberContext.close();
			}
		});

		await test.step('A guest does not see the shared content on the page', async () => {
			const guestContext = await browser.newContext();

			const guestPage = await guestContext.newPage();

			try {
				const response = await guestPage.goto(viewUrl);

				// Positive control: a guest can load the public page itself, so
				// the missing content below reflects the permission gate and not
				// a page the guest never reached.

				expect(response?.ok()).toBe(true);
				expect(guestPage.url()).toContain(viewUrl);

				await expect(
					guestPage.getByText(titleValue, {exact: true})
				).toHaveCount(0);
			}
			finally {
				await guestContext.close();
			}
		});
	}
);
