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
	'A translated CMS file renders its title in the requested language on a mapped page',
	{tag: ['@LPD-95528', '@LPD-95528/TC-5.c']},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		test.setTimeout(240000);

		const spaceName = `Space ${getRandomString()}`;
		const titleEn = `TitleEN ${getRandomString()}`;
		const titleEs = `TitleES ${getRandomString()}`;

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
				file: {fileBase64, name: `${getRandomString()}.jpg`},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title_i18n: {en_US: titleEn, es_ES: titleEs},
			},
			APPLICATION_NAME,
			spaceName
		);

		const objectDefinition =
			await apiHelpers.objectAdmin.getObjectDefinitionByName(
				'CMSBasicDocument'
			);

		const companyId = String(
			await page.evaluate(() => Liferay.ThemeDisplay.getCompanyId())
		);

		const guestRole = await apiHelpers.jsonWebServicesRole.getRole(
			companyId,
			'Guest'
		);

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			['DOWNLOAD_FILE', 'VIEW'],
			companyId,
			String(space.siteId),
			objectDefinition.className,
			String(entry.id),
			String(guestRole.roleId)
		);

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><lfr-editable id="image" type="image"><img alt="" src=""/></lfr-editable></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map the translated Title and Preview URL into the page fragment and publish', async () => {
			await pageEditorPage.selectEditable(fragmentId, 'title');

			await pageEditorPage.setMappingConfiguration({
				mapping: {entity: ENTITY, entry: titleEn, field: 'Title'},
			});

			await pageEditorPage.selectEditable(fragmentId, 'image');

			await page.getByLabel('Source Selection').selectOption('Mapping');

			await pageEditorPage.setMappedItem({
				entity: ENTITY,
				entry: titleEn,
				field: 'Preview URL',
			});

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();
		});

		await test.step('GUEST sees the Spanish file title at /es/', async () => {
			const guestContext = await browser.newContext();

			const guestPage = await guestContext.newPage();

			try {
				await expect(async () => {
					await guestPage.goto(`/es${viewUrl}`);

					await expect(
						guestPage.getByText(titleEs, {exact: true})
					).toBeVisible({timeout: 5000});
				}).toPass({timeout: 30000});
			}
			finally {
				await guestContext.close();
			}
		});

		await test.step('GUEST sees the English file title at /en/', async () => {
			const guestContext = await browser.newContext();

			const guestPage = await guestContext.newPage();

			try {
				await expect(async () => {
					await guestPage.goto(`/en${viewUrl}`);

					await expect(
						guestPage.getByText(titleEn, {exact: true})
					).toBeVisible({timeout: 5000});
				}).toPass({timeout: 30000});
			}
			finally {
				await guestContext.close();
			}
		});
	}
);
