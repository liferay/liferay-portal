/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../liferay.config';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import getRandomString from '../../../utils/getRandomString';
import {structureBuilderPagesTest} from './fixtures/structureBuilderPagesTest';

// The DPT editor renders two "Mapping" tabs in the DOM on this build, so the
// shared changeConfiguration helper's unscoped tab click is ambiguous; target
// the visible configuration tab and field select directly.

async function mapEditableToField(
	page: Page,
	pageEditorPage: PageEditorPage,
	fragmentId: string,
	field: string
) {
	await pageEditorPage.selectEditable(fragmentId, 'element-text');

	await page.getByRole('tab', {exact: true, name: 'Mapping'}).last().click();

	await page
		.getByRole('tabpanel', {name: 'Mapping'})
		.getByLabel('Field', {exact: true})
		.last()
		.selectOption(field);

	await pageEditorPage.waitForChangesSaved();
}

async function getRoleId(
	apiHelpers: DataApiHelpers,
	companyId: string,
	name: string
) {
	const urlSearchParams = new URLSearchParams();

	urlSearchParams.append('companyId', companyId);
	urlSearchParams.append('name', name);

	const role = await apiHelpers.post(
		`${liferayConfig.environment.baseUrl}/api/jsonws/role/get-role`,
		{
			data: urlSearchParams.toString(),
			headers: await apiHelpers.getJSONWebServicesHeaders(),
		}
	);

	return String(role.roleId);
}

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

async function createRichTextStructure(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string
) {
	const definition = await apiHelpers.objectAdmin.postRandomObjectDefinition({
		enableFriendlyURLCustomization: true,
		objectDefinitionSettings: [
			{
				name: 'acceptedGroupExternalReferenceCodes',
				value: spaceExternalReferenceCode as unknown as object,
			},
		],
		objectFields: [
			{
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
				label: {en_US: 'Title'},
				localized: true,
				name: 'title',
				required: true,
			},
			{
				DBType: 'Clob',
				businessType: 'RichText',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				label: {en_US: 'Body'},
				localized: true,
				name: 'body',
			},
		],
		objectFolderExternalReferenceCode: 'L_CMS_CONTENT_STRUCTURES',
		scope: 'depot',
		status: {code: 0},
		titleObjectFieldName: 'title',
	});

	apiHelpers.data.push({
		id: definition.id as number,
		type: 'objectDefinition',
	});

	return {
		applicationName: (definition.restContextPath as string).replace(
			/^\/o\//,
			''
		),
		className: definition.className as string,
		friendlyURLSeparator: definition.friendlyURLSeparator as string,
		label: (definition.label?.en_US ?? definition.name) as string,
	};
}

test(
	'A Structured Content entry from a Space renders at its DPT friendly URL for GUEST',
	{tag: ['@LPD-95535', '@LPD-95535/TC-12.d']},
	async ({
		apiHelpers,
		browser,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		test.setTimeout(240000);

		const id = getRandomString();
		const title = `Title ${id}`;
		const body = `Body ${id}`;
		const friendlyUrlPath = `slug-${id.toLowerCase()}`;
		const dptName = `DPT ${id}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const {applicationName, className, friendlyURLSeparator, label} =
			await createRichTextStructure(apiHelpers, space.externalReferenceCode);

		await test.step('Create and activate a DPT for the structure on the connected site', async () => {
			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.createTemplate({
				contentType: label,
				name: dptName,
			});

			await displayPageTemplatesPage.editTemplate(dptName);

			await pageEditorPage.addFragment('Basic Components', 'Heading');

			const headingId = await pageEditorPage.getFragmentId('Heading');

			await mapEditableToField(page, pageEditorPage, headingId, 'Title');

			await pageEditorPage.addFragment('Basic Components', 'Paragraph');

			const paragraphId = await pageEditorPage.getFragmentId('Paragraph');

			await mapEditableToField(page, pageEditorPage, paragraphId, 'Body');

			await pageEditorPage.publishPage();

			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.markAsDefault(dptName);
		});

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				body,
				friendlyUrlPath,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title,
			},
			applicationName,
			space.name
		);

		const companyId = String(
			await page.evaluate(() => Liferay.ThemeDisplay.getCompanyId())
		);

		for (const roleName of ['Guest', 'User']) {
			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
				['VIEW'],
				companyId,
				String(space.siteId),
				className,
				String(entry.id),
				await getRoleId(apiHelpers, companyId, roleName)
			);
		}

		const displayUrl = `/web${site.friendlyUrlPath}/${friendlyURLSeparator}/asset-library-${space.id}/${friendlyUrlPath}`;

		await test.step('GUEST reaches the structured content at its DPT friendly URL', async () => {
			const guestContext = await browser.newContext();

			const guestPage = await guestContext.newPage();

			try {
				await expect(async () => {
					await guestPage.goto(displayUrl);

					await expect(
						guestPage.getByText(title, {exact: true})
					).toBeVisible({timeout: 2000});

					await expect(
						guestPage.getByText(body, {exact: true})
					).toBeVisible({timeout: 2000});
				}).toPass({timeout: 5000});
			}
			finally {
				await guestContext.close();
			}
		});
	}
);
