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

test(
	'A Structured Content entry is reachable at its DPT friendly URL for GUEST',
	{tag: ['@LPD-85683', '@LPD-85683/TC-1.c']},
	async ({
		apiHelpers,
		browser,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
		structureBuilderPage,
	}) => {
		test.setTimeout(240000);

		const spaceName = `Space ${getRandomString()}`;
		const structureLabel = `Event ${getRandomString()}`;
		const structureName = `Event${getRandomInt()}`;
		const dptName = `DPT ${getRandomString()}`;
		const bodyValue = `Body ${getRandomString()}`;
		const titleValue = `Title ${getRandomString()}`;
		const friendlyUrlPath = `tc1c-${getRandomString()}`.toLowerCase();

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const objectDefinitionId =
			await test.step('Build a custom Event structure (Body text + repeatable Numeric)', async () => {
				const id = await structureBuilderPage.createStructureFromData({
					label: structureLabel,
					name: structureName,
					page: structureBuilderPage,
					publish: false,
					spaces: [spaceName],
				});

				await structureBuilderPage.addField('Text');
				await structureBuilderPage.selectFields([{label: 'Text'}]);
				await structureBuilderPage.changeFieldSettings({
					label: 'Body',
					localizable: true,
				});

				await structureBuilderPage.addField('Numeric');
				await structureBuilderPage.createRepeatableGroup({
					fields: [{label: 'Numeric'}],
					label: 'Repeatable Group',
				});

				await structureBuilderPage.publishStructure();

				return id;
			});

		await test.step('Create and activate a DPT for the structure (Title + Body)', async () => {
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

			await pageEditorPage.addFragment('Basic Components', 'Paragraph');

			const paragraphId = await pageEditorPage.getFragmentId('Paragraph');

			await pageEditorPage.selectEditable(paragraphId, 'element-text');

			await pageEditorPage.changeConfiguration({
				fieldLabel: 'Field',
				tab: 'Mapping',
				value: 'Body',
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

		const separator = objectDefinition.friendlyURLSeparator;

		const bodyField = objectDefinition.objectFields.find(
			(objectField) =>
				objectField.label && objectField.label.en_US === 'Body'
		);

		if (!bodyField) {
			throw new Error('Body field not found in object definition');
		}

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				[bodyField.name]: bodyValue,
				[objectDefinition.titleObjectFieldName]: titleValue,
				friendlyUrlPath,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			},
			applicationName,
			spaceName
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

		const displayUrl = `/web${site.friendlyUrlPath}/${separator}/asset-library-${space.id}/${friendlyUrlPath}`;

		await test.step('GUEST reaches the structured content at its DPT friendly URL', async () => {
			const guestContext = await browser.newContext();

			const guestPage = await guestContext.newPage();

			try {
				await expect(async () => {
					await guestPage.goto(displayUrl);

					await expect(
						guestPage.getByText(titleValue, {exact: true})
					).toBeVisible({timeout: 2000});

					await expect(
						guestPage.getByText(bodyValue, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 5000});
			}
			finally {
				await guestContext.close();
			}
		});
	}
);
