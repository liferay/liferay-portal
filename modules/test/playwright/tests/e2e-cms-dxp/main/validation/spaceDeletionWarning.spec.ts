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
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {cmsPagesTest} from '../../../site-cms-site-initializer/main/fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

const APPLICATION_NAME = 'cms/basic-web-contents';

const ENTITY = 'Basic Web Contents (CMS)';

test(
	'Deleting a Space whose content is mapped to a live page warns that connected sites are affected',
	{tag: ['@LPD-95548', '@LPD-95548/TC-23.h']},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		test.setTimeout(300000);

		const entryTitle = getRandomString();

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				content: `<p>${getRandomString()}</p>`,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: entryTitle,
			},
			APPLICATION_NAME,
			space.name
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			APPLICATION_NAME,
			entry.id,
			[{actionIds: ['VIEW'], roleName: 'Guest'}]
		);

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map the Space content onto a page and publish it', async () => {
			await expect(async () => {
				await pageEditorPage.selectEditable(fragmentId, 'title');

				await pageEditorPage.setMappingConfiguration({
					mapping: {
						entity: ENTITY,
						entry: entryTitle,
						field: 'Title',
					},
				});
			}).toPass({timeout: 60000});

			await pageEditorPage.publishPage();
		});

		await test.step('The mapped content is live on the page', async () => {
			const guestContext = await browser.newContext({
				storageState: {cookies: [], origins: []},
			});

			const guestPage = await guestContext.newPage();

			try {
				await expect(async () => {
					await guestPage.goto(viewUrl);

					await expect(
						guestPage.getByText(entryTitle, {exact: true})
					).toBeVisible({timeout: 5000});
				}).toPass({timeout: 120000});
			}
			finally {
				await guestContext.close();
			}
		});

		const spaceAdministrator = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode,
			'Asset Library Administrator'
		);

		await test.step('The Space Administrator triggers the Space deletion', async () => {
			await performUserSwitchViaApi(
				page,
				spaceAdministrator.alternateName
			);

			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await page
				.getByRole('button', {name: `${space.name} Actions`})
				.click();

			await page.getByRole('menuitem', {name: 'Delete'}).click();
		});

		await test.step('The confirmation warns that connected sites are impacted', async () => {
			await expect(
				page.getByText(
					'Deleting this space will permanently remove all its content and impact any connected sites or channels.'
				)
			).toBeVisible();
		});

		await test.step('The Space is deleted only after confirming the warning', async () => {
			await page
				.getByRole('button', {exact: true, name: 'Delete'})
				.click();

			await waitForAlert(
				page,
				`Success:${space.name} was successfully deleted.`
			);
		});
	}
);
