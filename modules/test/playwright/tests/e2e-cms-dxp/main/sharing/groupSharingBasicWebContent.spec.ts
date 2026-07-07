/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {
	createRecipient,
	expectShareNotification,
} from '../../../../utils/sharingRecipient';

const test = mergeTests(dataApiHelpersTest, loginTest());

const APPLICATION_NAME = 'cms/basic-web-contents';

test(
	'A Basic Web Content shared with a user group notifies group members and not non-members',
	{tag: ['@LPD-95527', '@LPD-95527/TC-4.a']},
	async ({apiHelpers, browser}) => {
		test.setTimeout(240000);

		const spaceName = `Space ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;

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

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				content: `<p>${getRandomString()}</p>`,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			APPLICATION_NAME,
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
			APPLICATION_NAME,
			entry.id
		);

		await test.step('A group member is notified of the shared content', async () => {
			await expectShareNotification(
				browser,
				member.alternateName,
				contentTitle,
				{present: true}
			);
		});

		await test.step('A user who is not a group member is not notified', async () => {
			await expectShareNotification(
				browser,
				nonMember.alternateName,
				contentTitle,
				{present: false}
			);
		});
	}
);
