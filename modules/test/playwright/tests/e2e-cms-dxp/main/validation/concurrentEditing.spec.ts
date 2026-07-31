/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {addCMSAdministrator} from '../../../../utils/addCMSAdministrator';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi} from '../../../../utils/performLogin';
import {cmsPagesTest} from '../../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {ContentsPage} from '../../../site-cms-site-initializer/main/pages/ContentsPage';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

test(
	'When two users concurrently edit and publish a Basic Web Content entry, the last publish wins',
	{tag: ['@LPD-95548', '@LPD-95548/TC-23.l']},
	async ({apiHelpers, browser, contentsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const editByAContent = `Edited by A ${getRandomString()}`;
		const editByBContent = `Edited by B ${getRandomString()}`;
		const friendlyUrl = getRandomString();
		const originalContent = `Original ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		const guestSite = await apiHelpers.headlessAdminSite.getSite('L_GUEST');

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			guestSite.externalReferenceCode
		);

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				content: `<p>${originalContent}</p>`,
				friendlyUrlPath: friendlyUrl,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: getRandomString(),
			},
			applicationName,
			space.name
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			applicationName,
			entry.id,
			[{actionIds: ['VIEW'], roleName: 'Guest'}]
		);

		const userB = await addCMSAdministrator(apiHelpers);

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			space.externalReferenceCode,
			userB.externalReferenceCode
		);

		const contextB = await browser.newContext();
		const pageB = await contextB.newPage();
		const contentsPageB = new ContentsPage(pageB);

		await performLoginViaApi({
			page: pageB,
			screenName: userB.alternateName,
		});

		await test.step('Both users open the same entry for editing', async () => {
			await contentsPage.goto();
			await contentsPage.editContent(entry.title as string);

			await contentsPageB.goto();
			await contentsPageB.editContent(entry.title as string);
		});

		const replaceContent = async (targetPage: Page, value: string) => {
			const contentEditor = targetPage.getByRole('textbox', {
				name: /^Content/,
			});

			await contentEditor.click();

			await targetPage.keyboard.press('ControlOrMeta+A');

			await targetPage.keyboard.type(value);
		};

		await test.step('User A edits the content and publishes first', async () => {
			await replaceContent(page, editByAContent);

			await contentsPage.saveContent();
		});

		await test.step('User B, unaware of A’s publish, edits and publishes', async () => {
			await replaceContent(pageB, editByBContent);

			await contentsPageB.saveContent();
		});

		await test.step('GUEST viewing the DXP page sees only User B’s version', async () => {
			const guestContext = await browser.newContext({
				storageState: {cookies: [], origins: []},
			});
			const guestPage = await guestContext.newPage();

			const viewUrl = `/web/cms/cmsbasicwebcontent/asset-library-${space.id}/${friendlyUrl}`;

			await expect(async () => {
				await guestPage.goto(viewUrl);

				await expect(guestPage.getByText(editByBContent)).toBeVisible({
					timeout: 5000,
				});
			}).toPass({timeout: 60000});

			await expect(guestPage.getByText(editByAContent)).toBeHidden();
			await expect(guestPage.getByText(originalContent)).toBeHidden();

			await guestContext.close();
		});

		await contextB.close();
	}
);
