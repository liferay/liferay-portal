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
	'A Basic Web Content with an untranslated field falls back to the default language for that field',
	{tag: ['@LPD-95528', '@LPD-95528/TC-5.d']},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		test.fail(
			true,
			'Fails due to LPD-96215: an untranslated field does not fall back to the default language, it renders empty'
		);

		test.setTimeout(240000);

		const spaceName = `Space ${getRandomString()}`;
		const titleEn = `TitleEN ${getRandomString()}`;
		const titleEs = `TitleES ${getRandomString()}`;
		const contentEn = `BodyEN ${getRandomString()}`;

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
				content_i18n: {en_US: `<p>${contentEn}</p>`},
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title_i18n: {en_US: titleEn, es_ES: titleEs},
			},
			APPLICATION_NAME,
			spaceName
		);

		const objectDefinition =
			await apiHelpers.objectAdmin.getObjectDefinitionByName(
				'CMSBasicWebContent'
			);

		const companyId = String(
			await page.evaluate(() => Liferay.ThemeDisplay.getCompanyId())
		);

		const guestRole = await apiHelpers.jsonWebServicesRole.getRole(
			companyId,
			'Guest'
		);

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			['VIEW'],
			companyId,
			String(space.siteId),
			objectDefinition.className,
			String(entry.id),
			String(guestRole.roleId)
		);

		const {fragmentId, viewUrl} = await addMappingFragment({
			apiHelpers,
			html: `<div><h1><lfr-editable id="title" type="text">Title</lfr-editable></h1><div><lfr-editable id="content" type="rich-text">Content</lfr-editable></div></div>`,
			pageEditorPage,
			site,
		});

		await test.step('Map the Title and Content into the page fragment and publish', async () => {
			await pageEditorPage.selectEditable(fragmentId, 'title');

			await pageEditorPage.setMappingConfiguration({
				mapping: {entity: ENTITY, entry: titleEn, field: 'Title'},
			});

			await pageEditorPage.selectEditable(fragmentId, 'content');

			await pageEditorPage.setMappingConfiguration({
				mapping: {entity: ENTITY, entry: titleEn, field: 'Content'},
			});

			await pageEditorPage.publishPage();
		});

		await test.step('At /es/ the translated Title shows and the untranslated Content falls back to the default language', async () => {
			const guestContext = await browser.newContext();

			const guestPage = await guestContext.newPage();

			try {
				await expect(async () => {
					await guestPage.goto(`/es${viewUrl}`);

					await expect(
						guestPage.getByText(titleEs, {exact: true})
					).toBeVisible({timeout: 5000});

					await expect(
						guestPage.getByText(contentEn, {exact: true})
					).toBeVisible({timeout: 5000});
				}).toPass({timeout: 30000});
			}
			finally {
				await guestContext.close();
			}
		});
	}
);
