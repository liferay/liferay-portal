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
import {addSpaceUser} from '../../../../utils/addSpaceUser';
import getRandomString from '../../../../utils/getRandomString';
import {performUserSwitchViaApi} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {cmsPagesTest} from '../../../site-cms-site-initializer/main/fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
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
	'A Basic Web Content moved from one Space to another can be published and mapped on the DXP site connected to the destination Space',
	{tag: ['@LPD-95540', '@LPD-95540/TC-17.a']},
	async ({apiHelpers, assetsPage, browser, page, pageEditorPage, site}) => {
		test.setTimeout(240000);

		const sourceSpaceName = `Space A ${getRandomString()}`;
		const destinationSpaceName = `Space B ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const contentTitle = `Title ${getRandomString()}`;
		const bodyValue = `Body ${getRandomString()}`;

		const destinationSpace =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: destinationSpaceName,
				type: 'Space',
			});

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: sourceSpaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			destinationSpace.externalReferenceCode,
			site.externalReferenceCode
		);

		const destinationFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: destinationSpaceName,
				title: destinationFolderName,
			});

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				content: `<p>${bodyValue}</p>`,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			APPLICATION_NAME,
			sourceSpaceName
		);

		const spaceAdmin = await addSpaceUser(
			apiHelpers,
			destinationSpace.externalReferenceCode,
			'Asset Library Administrator'
		);

		await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(spaceAdmin.id);
		await apiHelpers.jsonWebServicesUser.answerReminderQuery(spaceAdmin.id);

		await test.step('Move the content from the source Space to the destination Space', async () => {
			await assetsPage.gotoAll();

			await assetsPage.moveTo({
				destinationFolder: destinationFolderName,
				destinationSpace: destinationSpaceName,
				itemTitle: contentTitle,
			});

			await waitForAlert(
				page,
				`Success:${contentTitle} was successfully moved to ${destinationFolderName}.`,
				{first: true}
			);
		});

		await test.step('The content is no longer in the source Space and is present in the destination Space', async () => {
			const sourceResponse =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					APPLICATION_NAME,
					encodeURIComponent(sourceSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			expect(
				sourceResponse.items.filter(
					(item: {title: string}) => item.title === contentTitle
				)
			).toHaveLength(0);

			const destinationResponse =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					APPLICATION_NAME,
					encodeURIComponent(destinationSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			expect(
				destinationResponse.items.filter(
					(item: {title: string}) => item.title === contentTitle
				)
			).toHaveLength(1);
		});

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			APPLICATION_NAME,
			entry.id,
			[{actionIds: ['VIEW'], roleName: 'Guest'}]
		);

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><div><lfr-editable id="content" type="rich-text">Content</lfr-editable></div></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map the moved content into the page fragment and publish', async () => {
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

		await test.step('GUEST sees the moved content rendered on the connected DXP site', async () => {
			const guestContext = await browser.newContext({
				storageState: {cookies: [], origins: []},
			});

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
			}
			finally {
				await guestContext.close();
			}
		});

		await test.step('SPA of the destination Space sees the moved content in the destination Space', async () => {
			await performUserSwitchViaApi(page, spaceAdmin.alternateName);

			await assetsPage.gotoFolder(
				String(destinationFolder.id),
				destinationFolderName
			);

			await expect(assetsPage.getItem(contentTitle)).toBeVisible();
		});
	}
);
