/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

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

const APPLICATION_NAME = 'cms/basic-documents';

const ENTITY = 'Basic Documents (CMS)';

const fileBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

test(
	'A group-shared CMS file mapped to a page is visible to group members but not to guests',
	{tag: ['@LPD-95527', '@LPD-95527/TC-4.f']},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		test.setTimeout(300000);

		const spaceName = `Space ${getRandomString()}`;
		const fileTitle = `File ${getRandomString()}`;

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

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><lfr-editable id="image" type="image"><img alt="" src=""/></lfr-editable></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map the shared file Title and Preview URL into the page fragment and publish', async () => {
			await pageEditorPage.selectEditable(fragmentId, 'title');

			await pageEditorPage.setMappingConfiguration({
				mapping: {entity: ENTITY, entry: fileTitle, field: 'Title'},
			});

			await pageEditorPage.selectEditable(fragmentId, 'image');

			await page.getByLabel('Source Selection').selectOption('Mapping');

			await pageEditorPage.setMappedItem({
				entity: ENTITY,
				entry: fileTitle,
				field: 'Preview URL',
			});

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();
		});

		await test.step('A group member sees the shared file on the page', async () => {
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
						memberPage.getByText(fileTitle, {exact: true})
					).toBeVisible({timeout: 5000});
				}).toPass({timeout: 120000});
			}
			finally {
				await memberContext.close();
			}
		});

		await test.step('A guest does not see the shared file on the page', async () => {
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
					guestPage.getByText(fileTitle, {exact: true})
				).toHaveCount(0);
			}
			finally {
				await guestContext.close();
			}
		});
	}
);
