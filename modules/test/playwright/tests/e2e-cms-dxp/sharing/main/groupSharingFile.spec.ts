/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {
	createRecipient,
	expectShareNotification,
} from '../../../../utils/sharingRecipient';

const test = mergeTests(dataApiHelpersTest, loginTest());

const APPLICATION_NAME = 'cms/basic-documents';

const fileBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

test(
	'A CMS file shared with a user group notifies group members and not non-members',
	{tag: ['@LPD-95527', '@LPD-95527/TC-4.c']},
	async ({apiHelpers, browser}) => {
		test.setTimeout(240000);

		const spaceName = `Space ${getRandomString()}`;
		const fileTitle = `File ${getRandomString()}`;

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
				file: {fileBase64, name: `${getRandomString()}.jpg`},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
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

		await test.step('A group member is notified of the shared file', async () => {
			await expectShareNotification(
				browser,
				member.alternateName,
				fileTitle,
				{present: true}
			);
		});

		await test.step('A user who is not a group member is not notified', async () => {
			await expectShareNotification(
				browser,
				nonMember.alternateName,
				fileTitle,
				{present: false}
			);
		});
	}
);
