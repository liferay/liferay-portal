/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {addSpaceUser} from '../../../../utils/addSpaceUser';
import getRandomString from '../../../../utils/getRandomString';
import {performUserSwitchViaApi} from '../../../../utils/performLogin';
import {cmsPagesTest} from '../../../site-cms-site-initializer/main/fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

test(
	'A Space Member cannot move content or files to another Space',
	{tag: ['@LPD-95540', '@LPD-95540/TC-17.c']},
	async ({apiHelpers, assetsPage, page}) => {
		const spaceName = `Space ${getRandomString()}`;
		const contentTitle = `Content ${getRandomString()}`;
		const fileTitle = `File ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				content: `<p>${getRandomString()}</p>`,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			'cms/basic-web-contents',
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'R0lGODlhAQABAAAAACw=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			'cms/basic-documents',
			spaceName
		);

		const spaceMember = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode,
			'Asset Library Member'
		);

		await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(spaceMember.id);
		await apiHelpers.jsonWebServicesUser.answerReminderQuery(
			spaceMember.id
		);

		await performUserSwitchViaApi(page, spaceMember.alternateName);

		await test.step('The Move action is not available on the content', async () => {
			await assetsPage.gotoSpaceContents(spaceName);

			await assetsPage.expectItemActionHidden('Move', contentTitle);
		});

		await test.step('The Move action is not available on the file', async () => {
			await assetsPage.gotoSpaceFiles(spaceName);

			await assetsPage.expectItemActionHidden('Move', fileTitle);
		});
	}
);
