/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../../../fixtures/fragmentPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../../utils/getRandomString';

const test = mergeTests(
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	fragmentsPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

const APPLICATION_NAME = 'cms/basic-documents';

const SEPARATOR = 'cmsbasicdocument';

const pdfBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/test.pdf')
).toString('base64');

test(
	'A CMS PDF file is reachable at its DPT friendly URL for GUEST and is downloadable',
	{tag: ['@LPD-85683', '@LPD-85683/TC-1.e']},
	async ({
		apiHelpers,
		browser,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		test.setTimeout(240000);

		const dptName = `DPT ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;
		const fileTitle = `PDF ${getRandomString()}`;
		const friendlyUrlPath = `tc1e-${getRandomString()}`.toLowerCase();

		await test.step('Create and activate a Basic Document DPT (Title, Preview URL, Download button)', async () => {
			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.createTemplate({
				contentType: 'Basic Document',
				name: dptName,
			});

			await displayPageTemplatesPage.editTemplate(dptName);

			await pageEditorPage.addFragment('Basic Components', 'Heading');

			const headingId = await pageEditorPage.getFragmentId('Heading');

			await pageEditorPage.selectEditable(headingId, 'element-text');

			await pageEditorPage.changeConfiguration({
				fieldLabel: 'Field',
				tab: 'Mapping',
				value: 'Title',
			});

			await pageEditorPage.addFragment('Basic Components', 'Image');

			const imageId = await pageEditorPage.getFragmentId('Image');

			await pageEditorPage.selectEditable(imageId, 'image-square');

			await pageEditorPage.changeConfiguration({
				fieldLabel: 'Source Selection',
				tab: 'Image Source',
				value: 'Mapping',
			});

			await pageEditorPage.changeConfiguration({
				fieldLabel: 'Field',
				tab: 'Image Source',
				value: 'Preview URL',
			});

			await pageEditorPage.addFragment('Basic Components', 'Button');

			await pageEditorPage.mapEditableLink({
				editableId: 'link',
				fragmentName: 'Button',
				linkConfiguration: {
					mappingConfiguration: {
						mapping: {field: 'Download URL'},
						source: 'structure',
					},
					type: 'Mapped URL',
				},
			});

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.markAsDefault(dptName);
		});

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
				file: {
					fileBase64: pdfBase64,
					name: `${friendlyUrlPath}.pdf`,
				},
				friendlyUrlPath,
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
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

		for (const roleName of ['Guest', 'User']) {
			const role = await apiHelpers.jsonWebServicesRole.getRole(
				companyId,
				roleName
			);

			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
				['DOWNLOAD_FILE', 'VIEW'],
				companyId,
				String(space.siteId),
				objectDefinition.className,
				String(entry.id),
				String(role.roleId)
			);
		}

		const displayUrl = `/web${site.friendlyUrlPath}/${SEPARATOR}/asset-library-${space.id}/${friendlyUrlPath}`;

		await test.step('GUEST reaches the PDF and downloads it from the mapped Button', async () => {
			const guestContext = await browser.newContext();

			const guestPage = await guestContext.newPage();

			try {
				await expect(async () => {
					await guestPage.goto(displayUrl);

					await expect(
						guestPage.getByText(fileTitle, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 5000});

				const downloadPromise = guestPage.waitForEvent('download', {
					timeout: 5000,
				});

				await guestPage
					.getByRole('link', {name: 'Go Somewhere'})
					.click();

				const download = await downloadPromise;

				expect(download.suggestedFilename()).toContain('.pdf');
			}
			finally {
				await guestContext.close();
			}
		});
	}
);
