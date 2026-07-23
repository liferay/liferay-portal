/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../liferay.config';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitchViaApi} from '../../../utils/performLogin';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {registerUserCredentials} from '../main/spaces/helpers/roleMembership';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	pageEditorPagesTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPS-178052': {enabled: true},
	}),
	loginTest()
);

const CREATION_BUTTON = 'fdsCreationActionButton';
const IMAGE_BASE64 = readFileSync(
	path.join(__dirname, '../main/dependencies/sample_small_wide_400x300.jpg')
).toString('base64');

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

async function grantViewToGuestAndUser(
	apiHelpers: DataApiHelpers,
	companyId: string,
	space: any,
	classNameOrExternalReferenceCode: string,
	entryId: string,
	actions: string[] = ['VIEW']
) {
	const className = classNameOrExternalReferenceCode.includes('.')
		? classNameOrExternalReferenceCode
		: await apiHelpers
				.get(
					`${apiHelpers.baseUrl}object-admin/v1.0/object-definitions/by-external-reference-code/${classNameOrExternalReferenceCode}`
				)
				.then((objectDefinition) => objectDefinition.className);

	for (const roleName of ['Guest', 'User']) {
		const roleId = await getRoleId(apiHelpers, companyId, roleName);

		await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
			actions,
			companyId,
			String(space.siteId),
			className,
			entryId,
			roleId
		);
	}
}

async function createEventStructure(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string
) {
	const definition = await apiHelpers.objectAdmin.postRandomObjectDefinition({
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
				DBType: 'String',
				businessType: 'Text',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
				label: {en_US: 'Body'},
				localized: true,
				name: 'body',
				required: false,
			},
			{
				DBType: 'Integer',
				businessType: 'Integer',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				label: {en_US: 'Number'},
				name: 'number',
				required: false,
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
		entity: `${definition.label?.en_US ?? definition.name} (CMS)`,
	};
}

async function connectSpaceToSite(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string,
	siteExternalReferenceCode: string
) {
	await apiHelpers.headlessAssetLibrary.connectSite(
		spaceExternalReferenceCode,
		siteExternalReferenceCode,
		{searchable: true}
	);
}

async function mapEditable(
	page: Page,
	pageEditorPage: PageEditorPage,
	{
		editableId = 'element-text',
		entity,
		entryTitle,
		field,
		fragmentId,
		sourceSelection,
	}: {
		editableId?: string;
		entity: string;
		entryTitle: string;
		field: string;
		fragmentId: string;
		sourceSelection?: string;
	}
) {
	const iframe = page.frameLocator('iframe[title="Select"]');

	await pageEditorPage.selectEditable(fragmentId, editableId);

	if (sourceSelection) {
		await page.getByLabel('Source Selection').selectOption(sourceSelection);
	}

	await page.getByLabel('Select Item').click();

	const selectItemMenuItem = page.getByRole('menuitem', {
		name: 'Select Item...',
	});

	if (await selectItemMenuItem.isVisible().catch(() => false)) {
		await selectItemMenuItem.click();
	}

	await iframe.getByRole('main').waitFor();

	await iframe.getByText(entity, {exact: true}).click();

	await iframe.getByText(entryTitle, {exact: true}).first().click();

	await expect(
		page.locator('.page-editor__item-selector__content-input')
	).toHaveValue(entryTitle, {timeout: 10000});

	await page.getByLabel('Field', {exact: true}).selectOption(field);

	await pageEditorPage.waitForChangesSaved();
}

async function prepareUser(apiHelpers: DataApiHelpers) {
	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	registerUserCredentials(user);

	await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
	await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

	return user;
}

async function createSpace(apiHelpers: DataApiHelpers) {
	return apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: getRandomString(),
		settings: {},
		type: 'Space',
	});
}

async function addSpaceUser(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string,
	spaceRoleNames: string[] = []
) {
	const user = await prepareUser(apiHelpers);

	await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
		spaceExternalReferenceCode,
		user.externalReferenceCode
	);

	if (spaceRoleNames.length) {
		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			spaceExternalReferenceCode,
			user.externalReferenceCode,
			spaceRoleNames
		);
	}

	return user;
}

async function createBasicWebContent(
	apiHelpers: DataApiHelpers,
	spaceName: string,
	title: string
) {
	return apiHelpers.objectEntry.postObjectEntry(
		{objectEntryFolderExternalReferenceCode: 'L_CONTENTS', title},
		'cms/basic-web-contents',
		spaceName
	);
}

async function createCmsFile(
	apiHelpers: DataApiHelpers,
	spaceName: string,
	title: string
) {
	return apiHelpers.objectEntry.postObjectEntry(
		{
			file: {fileBase64: IMAGE_BASE64, name: `${title}.jpg`},
			objectEntryFolderExternalReferenceCode: 'L_FILES',
			title,
		},
		'cms/basic-documents',
		spaceName
	);
}

async function resolvePasswordResetWall(page: Page) {
	const heading = page.getByRole('heading', {name: 'Change Password'});

	const onWall = await heading
		.waitFor({state: 'visible', timeout: 3000})
		.then(() => true)
		.catch(() => false);

	if (!onWall) {
		return;
	}

	await page.getByLabel('Password', {exact: true}).fill('newpassword1');
	await page.getByLabel('Reenter Password').fill('newpassword1');
	await page.getByRole('button', {name: 'Save'}).click();

	await expect(heading).toBeHidden({timeout: 10000});
}

async function startSessionAs(page: Page, alternateName: string) {
	await performUserSwitchViaApi(page, alternateName);

	await page.goto(PORTLET_URLS.cmsHome, {waitUntil: 'domcontentloaded'});

	await resolvePasswordResetWall(page);
}

async function expectNoWriteActions(page: Page, title: string) {
	await expect(page.getByText(title).first()).toBeVisible();

	await expect(page.getByTestId(CREATION_BUTTON)).toBeHidden();

	const actionButton = page
		.getByRole('row', {name: title})
		.first()
		.getByRole('button');

	if (await actionButton.isVisible().catch(() => false)) {
		await actionButton.click();

		await expect(page.getByRole('menuitem', {name: 'Edit'})).toBeHidden();
		await expect(page.getByRole('menuitem', {name: 'Delete'})).toBeHidden();

		await page.keyboard.press('Escape');
	}
}

test(
	'A Space Member cannot create, edit, or delete content',
	{tag: ['@LPD-95533', '@LPD-95533/TC-10.a']},
	async ({apiHelpers, page}) => {
		const space = await createSpace(apiHelpers);
		const title = `Content ${getRandomString()}`;

		await createBasicWebContent(apiHelpers, space.name, title);

		const member = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode
		);

		await startSessionAs(page, member.alternateName);

		for (const url of [PORTLET_URLS.cmsContents, PORTLET_URLS.cmsAll]) {
			await page.goto(url, {waitUntil: 'domcontentloaded'});

			await expectNoWriteActions(page, title);
		}
	}
);

test(
	'Content in a connected Space is visible to a Space Member in the CMS and to a site user on a mapped DXP page',
	{tag: ['@LPD-95533', '@LPD-95533/TC-10.c']},
	async ({apiHelpers, assetsPage, page, pageEditorPage, site}) => {
		test.setTimeout(180000);

		const title = `Content ${getRandomString()}`;

		const space = await createSpace(apiHelpers);

		const entry = await createBasicWebContent(
			apiHelpers,
			space.name,
			title
		);

		const companyId = String(
			await page.evaluate(() => Liferay.ThemeDisplay.getCompanyId())
		);

		await grantViewToGuestAndUser(
			apiHelpers,
			companyId,
			space,
			'L_CMS_BASIC_WEB_CONTENT',
			String(entry.id)
		);

		await connectSpaceToSite(
			apiHelpers,
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const member = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode
		);

		const siteUser = await prepareUser(apiHelpers);
		await apiHelpers.jsonWebServicesUser.addGroupUsers(String(site.id), [
			String(siteUser.id),
		]);

		const headingId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: headingId,
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await test.step('SiteA maps the content title onto a DXP page', async () => {
			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await mapEditable(page, pageEditorPage, {
				entity: 'Basic Web Contents (CMS)',
				entryTitle: title,
				field: 'Title',
				fragmentId: headingId,
			});

			await pageEditorPage.publishPage();
		});

		await test.step('Space Member sees the content in the CMS', async () => {
			await startSessionAs(page, member.alternateName);

			await assetsPage.gotoAll();

			await expect(page.getByText(title).first()).toBeVisible();
		});

		await test.step('Site user sees the content on the DXP page', async () => {
			await startSessionAs(page, siteUser.alternateName);

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`,
				{waitUntil: 'domcontentloaded'}
			);

			await expect(page.getByText(title).first()).toBeVisible();
		});
	}
);

test(
	'Structured content from a custom structure renders all mapped fields on a DXP page for USER',
	{tag: ['@LPD-95533', '@LPD-95533/TC-10.d']},
	async ({apiHelpers, page, pageEditorPage, site}) => {
		test.setTimeout(180000);

		const id = getRandomString();
		const title = `Title ${id}`;
		const body = `Body ${id}`;
		const number = 1234567;

		const space = await createSpace(apiHelpers);

		const {applicationName, className, entity} = await createEventStructure(
			apiHelpers,
			space.externalReferenceCode
		);

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				body,
				number,
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title,
			},
			applicationName,
			space.name
		);

		const companyId = String(
			await page.evaluate(() => Liferay.ThemeDisplay.getCompanyId())
		);

		await grantViewToGuestAndUser(
			apiHelpers,
			companyId,
			space,
			className,
			String(entry.id)
		);

		await connectSpaceToSite(
			apiHelpers,
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const siteUser = await prepareUser(apiHelpers);
		await apiHelpers.jsonWebServicesUser.addGroupUsers(String(site.id), [
			String(siteUser.id),
		]);

		const titleId = getRandomString();
		const bodyId = getRandomString();
		const numberId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: titleId,
					key: 'BASIC_COMPONENT-heading',
				}),
				getFragmentDefinition({
					id: bodyId,
					key: 'BASIC_COMPONENT-paragraph',
				}),
				getFragmentDefinition({
					id: numberId,
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await test.step('SiteA maps the structured content fields onto a DXP page', async () => {
			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await mapEditable(page, pageEditorPage, {
				entity,
				entryTitle: title,
				field: 'Title',
				fragmentId: titleId,
			});

			await mapEditable(page, pageEditorPage, {
				entity,
				entryTitle: title,
				field: 'Body',
				fragmentId: bodyId,
			});

			await mapEditable(page, pageEditorPage, {
				entity,
				entryTitle: title,
				field: 'Number',
				fragmentId: numberId,
			});

			await pageEditorPage.publishPage();
		});

		await test.step('USER sees all mapped fields on the DXP page', async () => {
			await startSessionAs(page, siteUser.alternateName);

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`,
				{waitUntil: 'domcontentloaded'}
			);

			await expect(page.getByText(title).first()).toBeVisible();
			await expect(page.getByText(body).first()).toBeVisible();
			await expect(page.getByText(String(number)).first()).toBeVisible();
		});
	}
);

test(
	'A CMS file mapped onto a DXP page renders for GUEST',
	{tag: ['@LPD-95533', '@LPD-95533/TC-10.e']},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		test.setTimeout(180000);

		const title = `File ${getRandomString()}`;

		const space = await createSpace(apiHelpers);

		const entry = await createCmsFile(apiHelpers, space.name, title);

		const companyId = String(
			await page.evaluate(() => Liferay.ThemeDisplay.getCompanyId())
		);

		await grantViewToGuestAndUser(
			apiHelpers,
			companyId,
			space,
			'L_CMS_BASIC_DOCUMENT',
			String(entry.id),
			['DOWNLOAD_FILE', 'VIEW']
		);

		await connectSpaceToSite(
			apiHelpers,
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const imageId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: imageId,
					key: 'BASIC_COMPONENT-image',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await test.step('SiteA maps the file onto a DXP page', async () => {
			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await mapEditable(page, pageEditorPage, {
				editableId: 'image-square',
				entity: 'Basic Documents (CMS)',
				entryTitle: title,
				field: 'Preview URL',
				fragmentId: imageId,
				sourceSelection: 'Mapping',
			});

			await pageEditorPage.publishPage();
		});

		await test.step('GUEST sees the file render on the DXP page', async () => {
			const guestContext = await browser.newContext();

			try {
				const guestPage = await guestContext.newPage();

				await guestPage.goto(
					`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`,
					{waitUntil: 'domcontentloaded'}
				);

				await expect(
					guestPage.locator('.component-image img')
				).toBeVisible({timeout: 10000});
			}
			finally {
				await guestContext.close();
			}
		});
	}
);

test(
	'A Space Member cannot upload, edit, or delete files',
	{tag: ['@LPD-95533', '@LPD-95533/TC-10.b']},
	async ({apiHelpers, page}) => {
		const space = await createSpace(apiHelpers);
		const title = `File ${getRandomString()}`;

		await createCmsFile(apiHelpers, space.name, title);

		const member = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode
		);

		await startSessionAs(page, member.alternateName);

		await page.goto(PORTLET_URLS.cmsFiles, {waitUntil: 'domcontentloaded'});

		await expectNoWriteActions(page, title);
	}
);
