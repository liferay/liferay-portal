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
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {performLoginViaApi, userData} from '../../../../utils/performLogin';
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';

const test = mergeTests(
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	fragmentsPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	structureBuilderPagesTest
);

async function createSiteMember(apiHelpers, siteId: string) {
	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
	await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

	await apiHelpers.jsonWebServicesUser.addGroupUsers(siteId, [user.id]);

	return user;
}

test(
	'A restricted Structured Content is denied to GUEST and to a non-member USER, and renders for a member at its DPT URL',
	{tag: ['@LPD-95542', '@LPD-95542/TC-19.h', '@LPD-95542/TC-19.k']},
	async ({
		apiHelpers,
		browser,
		displayPageTemplatesPage,
		pageEditorPage,
		site,
		structureBuilderPage,
	}) => {
		test.setTimeout(300000);

		const spaceName = `Space ${getRandomString()}`;
		const structureLabel = `Event ${getRandomString()}`;
		const structureName = `Event${getRandomInt()}`;
		const dptName = `DPT ${getRandomString()}`;
		const titleValue = `Title ${getRandomString()}`;
		const friendlyUrlPath = `restricted-${getRandomString()}`.toLowerCase();

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const objectDefinitionId =
			await test.step('Build a custom structure', async () => {
				const id = await structureBuilderPage.createStructureFromData({
					label: structureLabel,
					name: structureName,
					page: structureBuilderPage,
					publish: false,
					spaces: [spaceName],
				});

				await structureBuilderPage.addField('Text');
				await structureBuilderPage.selectFields([{label: 'Text'}]);
				await structureBuilderPage.changeFieldSettings({label: 'Body'});

				await structureBuilderPage.publishStructure();

				return id;
			});

		await test.step('Create and activate a DPT for the structure', async () => {
			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.createTemplate({
				contentType: structureLabel,
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

		const objectDefinition = await apiHelpers.get(
			`${apiHelpers.baseUrl}object-admin/v1.0/object-definitions/${objectDefinitionId}`
		);

		const applicationName = objectDefinition.restContextPath.replace(
			'/o/',
			''
		);

		const bodyField = objectDefinition.objectFields.find(
			(objectField) => objectField.label?.en_US === 'Body'
		);

		if (!bodyField) {
			throw new Error('Body field not found in object definition');
		}

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				[bodyField.name]: `Body ${getRandomString()}`,
				[objectDefinition.titleObjectFieldName]: titleValue,
				friendlyUrlPath,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			},
			applicationName,
			spaceName
		);

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: `cms_viewer_${getRandomString()}`,
			roleType: 'regular',
		});

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			applicationName,
			entry.id,
			[{actionIds: ['VIEW'], roleName: role.name}]
		);

		const displayUrl = `/web${site.friendlyUrlPath}/${objectDefinition.friendlyURLSeparator}/asset-library-${space.id}/${friendlyUrlPath}`;

		await test.step('A member USER sees the restricted structured content', async () => {
			const member = await createSiteMember(apiHelpers, String(site.id));

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
						memberPage.getByText(titleValue, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 10000});
			}
			finally {
				await memberContext.close();
			}
		});

		await test.step('GUEST is denied and the content is not exposed', async () => {
			const guestContext = await browser.newContext({
				storageState: {cookies: [], origins: []},
			});

			const guestPage = await guestContext.newPage();

			try {
				await guestPage.goto(displayUrl);

				await guestPage.waitForLoadState('networkidle');

				await expect(
					guestPage.getByText(titleValue, {exact: true})
				).toBeHidden();
			}
			finally {
				await guestContext.close();
			}
		});

		await test.step('A non-member USER is denied even when logged in', async () => {
			const nonMember = await createSiteMember(
				apiHelpers,
				String(site.id)
			);

			const nonMemberContext = await browser.newContext();

			const nonMemberPage = await nonMemberContext.newPage();

			try {
				await performLoginViaApi({
					page: nonMemberPage,
					screenName: nonMember.alternateName,
				});

				await nonMemberPage.goto(displayUrl);

				await nonMemberPage.waitForLoadState('networkidle');

				await expect(
					nonMemberPage.getByText(titleValue, {exact: true})
				).toBeHidden();
			}
			finally {
				await nonMemberContext.close();
			}
		});
	}
);
