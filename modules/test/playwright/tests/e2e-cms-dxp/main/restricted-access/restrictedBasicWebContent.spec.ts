/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../../../fixtures/fragmentPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi, userData} from '../../../../utils/performLogin';

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

const APPLICATION_NAME = 'cms/basic-web-contents';

test(
	'A restricted Basic Web Content is denied to GUEST and to a non-member, and renders for a member at its DPT URL',
	{tag: ['@LPD-95542', '@LPD-95542/TC-19.g', '@LPD-95542/TC-19.j']},
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
		const contentTitle = `Title ${getRandomString()}`;
		const friendlyUrlPath = `restricted-${getRandomString()}`.toLowerCase();

		await test.step('Create and activate a Basic Web Content DPT', async () => {
			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.createTemplate({
				contentType: 'Basic Web Content',
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
				content: `<p>Body ${getRandomString()}</p>`,
				friendlyUrlPath,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			APPLICATION_NAME,
			spaceName
		);

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: `cms_viewer_${getRandomString()}`,
			roleType: 'regular',
		});

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			APPLICATION_NAME,
			entry.id,
			[{actionIds: ['VIEW'], roleName: role.name}]
		);

		const displayUrl = `/web${site.friendlyUrlPath}/cmsbasicwebcontent/asset-library-${space.id}/${friendlyUrlPath}`;

		await test.step('GUEST is denied and the content body is not exposed', async () => {
			const guestContext = await browser.newContext({
				storageState: {cookies: [], origins: []},
			});

			const guestPage = await guestContext.newPage();

			try {
				await guestPage.goto(displayUrl);

				await guestPage.waitForLoadState('networkidle');

				await expect(
					guestPage.getByText(contentTitle, {exact: true})
				).toBeHidden();
			}
			finally {
				await guestContext.close();
			}
		});

		await test.step('A member USER sees the restricted content rendered', async () => {
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
						memberPage.getByText(contentTitle, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 10000});
			}
			finally {
				await memberContext.close();
			}
		});
	}
);
