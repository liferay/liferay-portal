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
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi, userData} from '../../../../utils/performLogin';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

const APPLICATION_NAME = 'cms/basic-web-contents';

const ENTITY = 'Basic Web Contents (CMS)';

test(
	'A Basic Web Content mapped to page fragments renders and live-updates for GUEST and USER',
	{tag: ['@LPD-95525', '@LPD-95525/TC-2.a']},
	async ({apiHelpers, browser, pageEditorPage, site}) => {
		test.setTimeout(240000);

		const spaceName = `Space ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;
		const bodyValue = `Body ${getRandomString()}`;
		const updatedTitle = `Title ${getRandomString()}`;
		const updatedBody = `Body ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				content: `<p>${bodyValue}</p>`,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			APPLICATION_NAME,
			spaceName
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			APPLICATION_NAME,
			entry.id,
			[
				{actionIds: ['VIEW'], roleName: 'Guest'},
				{actionIds: ['VIEW'], roleName: 'User'},
			]
		);

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><div><lfr-editable id="content" type="rich-text">Content</lfr-editable></div></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map the entry Title and Content into the page fragment and publish', async () => {

			// The entry is picked by title in the editor's mapping dialog, which
			// depends on the search index. Retry until the entry is selectable.

			await expect(async () => {
				await pageEditorPage.selectEditable(fragmentId, 'title');

				await pageEditorPage.setMappingConfiguration({
					mapping: {
						entity: ENTITY,
						entry: contentTitle,
						field: 'Title',
					},
				});

				await pageEditorPage.selectEditable(fragmentId, 'content');

				await pageEditorPage.setMappingConfiguration({
					mapping: {
						entity: ENTITY,
						entry: contentTitle,
						field: 'Content',
					},
				});
			}).toPass({timeout: 30000});

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();
		});

		await test.step('GUEST sees the mapped content, then the CMS edit without re-publishing', async () => {
			const guestContext = await browser.newContext();

			const guestPage = await guestContext.newPage();

			try {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(contentTitle, {exact: true})
					).toBeVisible({timeout: 2000});

					await expect(
						guestPage.getByText(bodyValue, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 30000});

				await apiHelpers.objectEntry.putObjectEntry(
					{
						content: `<p>${updatedBody}</p>`,
						title: updatedTitle,
					},
					APPLICATION_NAME,
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
