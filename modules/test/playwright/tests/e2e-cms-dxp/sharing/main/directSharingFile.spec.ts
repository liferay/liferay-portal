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
	'A CMS file shared directly with a user notifies that user and not others',
	{tag: ['@LPD-95527', '@LPD-95527/TC-4.h']},
	async ({apiHelpers, browser}) => {
		test.setTimeout(240000);

		const spaceName = `Space ${getRandomString()}`;
		const fileTitle = `File ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const recipient = await createRecipient(apiHelpers);
		const other = await createRecipient(apiHelpers);

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
					id: recipient.id,
					share: false,
					type: 'User',
				},
			],
			APPLICATION_NAME,
			entry.id
		);

		await test.step('The recipient is notified of the shared file', async () => {
			await expectShareNotification(
				browser,
				recipient.alternateName,
				fileTitle,
				{present: true}
			);
		});

		await test.step('A user the file was not shared with is not notified', async () => {
			await expectShareNotification(
				browser,
				other.alternateName,
				fileTitle,
				{present: false}
			);
		});
	}
);
