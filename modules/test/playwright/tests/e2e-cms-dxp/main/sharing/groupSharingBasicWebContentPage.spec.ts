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
import {performLoginViaApi} from '../../../../utils/performLogin';
import {createRecipient} from '../../../../utils/sharingRecipient';

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
	'A group-shared Basic Web Content mapped to a page is visible to group members but not to guests',
	{tag: ['@LPD-95527', '@LPD-95527/TC-4.d']},
	async ({apiHelpers, browser, pageEditorPage, site}) => {
		test.setTimeout(300000);

		const spaceName = `Space ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;

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

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><div><lfr-editable id="content" type="rich-text">Content</lfr-editable></div></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map the shared content into the page fragment and publish', async () => {
			await pageEditorPage.selectEditable(fragmentId, 'title');

			await pageEditorPage.setMappingConfiguration({
				mapping: {entity: ENTITY, entry: contentTitle, field: 'Title'},
			});

			await pageEditorPage.selectEditable(fragmentId, 'content');

			await pageEditorPage.setMappingConfiguration({
				mapping: {
					entity: ENTITY,
					entry: contentTitle,
					field: 'Content',
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
						memberPage.getByText(contentTitle, {exact: true})
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
					guestPage.getByText(contentTitle, {exact: true})
				).toHaveCount(0);
			}
			finally {
				await guestContext.close();
			}
		});
	}
);
