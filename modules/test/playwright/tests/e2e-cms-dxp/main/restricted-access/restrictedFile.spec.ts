/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {fragmentsPagesTest} from '../../../../fixtures/fragmentPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi, userData} from '../../../../utils/performLogin';

const test = mergeTests(
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	fragmentsPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

const APPLICATION_NAME = 'cms/basic-documents';

const SEPARATOR = 'cmsbasicdocument';

const imageBase64 = readFileSync(
	path.join(__dirname, '../../dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

async function fetchStatus(page: Page, href: string) {
	return page.evaluate(
		async (url) => (await fetch(url, {redirect: 'manual'})).status,
		href
	);
}

test(
	'A restricted CMS file is denied to GUEST at its DPT URL and its binary is not served, while a member can download it',
	{tag: ['@LPD-95542', '@LPD-95542/TC-19.i']},
	async ({
		apiHelpers,
		browser,
		displayPageTemplatesPage,
		pageEditorPage,
		site,
	}) => {
		test.setTimeout(240000);

		const dptName = `DPT ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;
		const fileTitle = `File ${getRandomString()}`;
		const friendlyUrlPath = `restricted-${getRandomString()}`.toLowerCase();

		await test.step('Create and activate a Basic Document DPT (Title + Preview URL)', async () => {
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
				file: {fileBase64: imageBase64, name: `${friendlyUrlPath}.jpg`},
				friendlyUrlPath,
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			APPLICATION_NAME,
			spaceName
		);

		const downloadHref = entry.file.link.href;

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: `cms_viewer_${getRandomString()}`,
			roleType: 'regular',
		});

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			APPLICATION_NAME,
			entry.id,
			[{actionIds: ['DOWNLOAD_FILE', 'VIEW'], roleName: role.name}]
		);

		const displayUrl = `/web${site.friendlyUrlPath}/${SEPARATOR}/asset-library-${space.id}/${friendlyUrlPath}`;

		await test.step('A member USER sees the file page and can download the binary', async () => {
			const member = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[member.alternateName] = {
				name: member.givenName,
				password: 'test',
				surname: member.familyName,
			};

			await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(member.id);
			await apiHelpers.jsonWebServicesUser.answerReminderQuery(member.id);

			await apiHelpers.jsonWebServicesUser.addGroupUsers(
				String(site.id),
				[member.id]
			);

			await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
				role.id,
				Number(member.id)
			);

			const memberContext = await browser.newContext();

			const memberPage = await memberContext.newPage();

			try {
				await performLoginViaApi({
					page: memberPage,
					screenName: member.alternateName,
				});

				await expect(async () => {
					await memberPage.goto(displayUrl);

					await expect(
						memberPage.getByText(fileTitle, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 10000});

				expect(await fetchStatus(memberPage, downloadHref)).toBe(200);
			}
			finally {
				await memberContext.close();
			}
		});

		await test.step('GUEST is denied at the DPT URL and the binary is not served', async () => {
			const guestContext = await browser.newContext({
				storageState: {cookies: [], origins: []},
			});

			const guestPage = await guestContext.newPage();

			try {
				await guestPage.goto(displayUrl);

				await guestPage.waitForLoadState('networkidle');

				await expect(
					guestPage.getByText(fileTitle, {exact: true})
				).toBeHidden();

				expect(await fetchStatus(guestPage, downloadHref)).not.toBe(
					200
				);
			}
			finally {
				await guestContext.close();
			}
		});
	}
);
