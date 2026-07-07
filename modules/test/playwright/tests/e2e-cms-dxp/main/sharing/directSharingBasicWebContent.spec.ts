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
	'A Basic Web Content shared directly with a user notifies that user and not others',
	{tag: ['@LPD-95527', '@LPD-95527/TC-4.g']},
	async ({apiHelpers, browser}) => {
		test.setTimeout(240000);

		const spaceName = `Space ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const recipient = await createRecipient(apiHelpers);
		const other = await createRecipient(apiHelpers);

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
					id: recipient.id,
					share: false,
					type: 'User',
				},
			],
			APPLICATION_NAME,
			entry.id
		);

		await test.step('The recipient is notified of the shared content', async () => {
			await expectShareNotification(
				browser,
				recipient.alternateName,
				contentTitle,
				{present: true}
			);
		});

		await test.step('A user the content was not shared with is not notified', async () => {
			await expectShareNotification(
				browser,
				other.alternateName,
				contentTitle,
				{present: false}
			);
		});
	}
);
