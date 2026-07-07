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
	'A published Basic Web Content is reachable at its DPT friendly URL for GUEST and USER',
	{tag: ['@LPD-85683', '@LPD-85683/TC-1.a', '@LPD-85683/TC-1.b']},
	async ({
		apiHelpers,
		browser,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const dptName = `DPT ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;
		const contentTitle = `Content ${getRandomString()}`;
		const friendlyUrlPath = `tc1a-${getRandomString()}`.toLowerCase();

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

		const objectDefinition =
			await apiHelpers.objectAdmin.getObjectDefinitionByName(
				'CMSBasicWebContent'
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
				['VIEW'],
				companyId,
				String(space.siteId),
				objectDefinition.className,
				String(entry.id),
				String(role.roleId)
			);
		}

		const displayUrl = `/web${site.friendlyUrlPath}/cmsbasicwebcontent/asset-library-${space.id}/${friendlyUrlPath}`;

		await test.step('GUEST reaches the entry at its DPT friendly URL', async () => {
			const guestContext = await browser.newContext();

			const guestPage = await guestContext.newPage();

			try {
				await expect(async () => {
					await guestPage.goto(displayUrl);

					await expect(
						guestPage.getByText(contentTitle, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 5000});
			}
			finally {
				await guestContext.close();
			}
		});

		await test.step('USER reaches the entry at its DPT friendly URL', async () => {
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
					await userPage.goto(displayUrl);

					await expect(
						userPage.getByText(contentTitle, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 5000});
			}
			finally {
				await userContext.close();
			}
		});
	}
);
