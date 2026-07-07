/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {
	createRecipient,
	expectShareNotification,
} from '../../../../utils/sharingRecipient';
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';

const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	structureBuilderPagesTest
);

test(
	'A custom Event structured content shared with a user group notifies group members and not non-members',
	{tag: ['@LPD-95527', '@LPD-95527/TC-4.b']},
	async ({apiHelpers, browser, structureBuilderPage}) => {
		test.setTimeout(300000);

		const spaceName = `Space ${getRandomString()}`;
		const structureLabel = `Event ${getRandomString()}`;
		const structureName = `Event${getRandomInt()}`;
		const titleValue = `Title ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		const member = await createRecipient(apiHelpers);
		const nonMember = await createRecipient(apiHelpers);

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

		const bodyField = objectDefinition.objectFields.find(
			(objectField) =>
				objectField.label && objectField.label.en_US === 'Body'
		);

		if (!bodyField) {
			throw new Error('Body field not found in object definition');
		}

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				[bodyField.name]: `Body ${getRandomString()}`,
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

		await test.step('A group member is notified of the shared content', async () => {
			await expectShareNotification(
				browser,
				member.alternateName,
				titleValue,
				{present: true}
			);
		});

		await test.step('A user who is not a group member is not notified', async () => {
			await expectShareNotification(
				browser,
				nonMember.alternateName,
				titleValue,
				{present: false}
			);
		});
	}
);
