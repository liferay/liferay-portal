/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {addSpaceUser} from '../../../../utils/addSpaceUser';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi} from '../../../../utils/performLogin';

const test = mergeTests(dataApiHelpersTest, loginTest());

const APPLICATION_NAME = 'cms/basic-web-contents';

test(
	'A Space Member cannot use the Share action on a content entry',
	{tag: ['@LPD-95527', '@LPD-95527/TC-4.i']},
	async ({apiHelpers, browser}) => {
		test.setTimeout(180000);

		const spaceName = `Space ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const spaceMember = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode,
			'Asset Library Member'
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				content: `<p>${getRandomString()}</p>`,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			APPLICATION_NAME,
			spaceName
		);

		await test.step('The Space Member has no Share action on the entry', async () => {
			const memberContext = await browser.newContext();

			const memberPage = await memberContext.newPage();

			try {
				await performLoginViaApi({
					page: memberPage,
					screenName: spaceMember.alternateName,
				});

				await memberPage.goto('/web/cms/contents');

				const row = memberPage
					.locator('tr', {hasText: contentTitle})
					.or(
						memberPage.locator('.card-row', {
							hasText: contentTitle,
						})
					)
					.first();

				await expect(row).toBeVisible({timeout: 5000});

				await row.hover();

				const actionsButton = row.locator('button').last();

				await actionsButton.click();

				// Positive control: the actions menu actually opened, so the
				// absence of Share below is a real result and not a menu that
				// never rendered.

				await expect(
					memberPage.getByRole('menuitem').first()
				).toBeVisible({timeout: 5000});

				await expect(
					memberPage.getByRole('menuitem', {
						exact: true,
						name: 'Share',
					})
				).toHaveCount(0);
			}
			finally {
				await memberContext.close();
			}
		});
	}
);
