/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Browser, Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {SearchPage} from '../../../pages/portal-search-web/SearchPage';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitchViaApi, userData} from '../../../utils/performLogin';

const test = mergeTests(
	isolatedLayoutTest({type: 'portlet'}),
	loginTest(),
	dataApiHelpersTest
);

test.describe.configure({timeout: 180000});

const GIF_BASE64 = 'R0lGODlhAQABAAAAACw=';

function uniqueId() {
	return `cms${getRandomString()}`.replace(/-/g, '');
}

async function createStructuredContentDefinition(
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
	};
}

async function createCmsCategory(
	apiHelpers: DataApiHelpers,
	cmsSiteId: number,
	name: string
) {
	const vocabulary =
		await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
			assetLibraries: [{id: -1}],
			assetTypes: [
				{
					required: false,
					subtype: 'AllAssetSubtypes',
					type: 'AllAssetTypes',
				},
			],
			name: getRandomString(),
			siteId: String(cmsSiteId),
			visibilityType: 'PUBLIC',
		});

	const category =
		await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
			{name, vocabularyId: vocabulary.id}
		);

	return {category, vocabulary};
}

async function grantEntryView(
	apiHelpers: DataApiHelpers,
	applicationName: string,
	entryId: number,
	roleNames: string[],
	actionIds: string[] = ['VIEW']
) {
	await apiHelpers.objectEntry.putObjectEntryPermissions(
		applicationName,
		entryId,
		roleNames.map((roleName) => ({actionIds, roleName}))
	);
}

async function grantCategoryView(
	apiHelpers: DataApiHelpers,
	vocabularyId: number,
	categoryId: number,
	roleNames: string[]
) {
	for (const roleName of roleNames) {
		await apiHelpers.headlessAdminTaxonomy.putTaxonomyVocabulariesTaxonomyVocabularyPermissions(
			vocabularyId,
			{actionIds: ['VIEW'], roleName}
		);

		await apiHelpers.headlessAdminTaxonomy.putTaxonomyCategoriesTaxonomyCategoryPermissions(
			categoryId,
			{actionIds: ['VIEW'], roleName}
		);
	}
}

async function connectSpaceToGuest(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string
) {
	await apiHelpers.headlessAssetLibrary.connectSite(
		spaceExternalReferenceCode,
		'L_GUEST',
		{searchable: true}
	);
}

async function disconnectSpaceFromGuest(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string
) {
	await apiHelpers.headlessAssetLibrary.disconnectSite(
		spaceExternalReferenceCode,
		'L_GUEST'
	);
}

async function createSiteUser(
	apiHelpers: DataApiHelpers,
	guestGroupId: string
) {
	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user.alternateName] = {
		login: user.emailAddress,
		password: 'test',
	};

	await apiHelpers.jsonWebServicesUser.addGroupUsers(guestGroupId, [
		String(user.id),
	]);

	return user;
}

async function buildSearchPage(searchPage: SearchPage, layoutUrl: string) {
	await searchPage.page.goto(layoutUrl, {waitUntil: 'domcontentloaded'});

	await searchPage.addPortlet('Search Bar', 'Search');
	await searchPage.addPortlet('Search Results', 'Search');
	await searchPage.addPortlet('Category Facet', 'Search');
	await searchPage.addPortlet('Tag Facet', 'Search');
}

interface VerifyOptions {
	applyCategory?: string;
	categoryKeepsVisible?: boolean;
	expectFound: boolean;
	layoutUrl: string;
	searchPage: SearchPage;
	searchText: string;
	title: string;
}

async function verifySearch({
	applyCategory,
	categoryKeepsVisible = true,
	expectFound,
	layoutUrl,
	searchPage,
	searchText,
	title,
}: VerifyOptions) {
	const resultLink = searchPage.searchResults
		.getByRole('link', {name: title})
		.first();

	await expect(async () => {
		await searchPage.page.goto(layoutUrl, {waitUntil: 'domcontentloaded'});

		await searchPage.searchKeywordInMainContent(searchText);

		if (expectFound) {
			await expect(resultLink).toBeVisible({timeout: 2000});
		}
		else {

			// Anchor on the rendered Search Results portlet before asserting
			// zero hits, so a portlet that failed to render cannot pass the
			// empty-count assertion trivially.

			await expect(searchPage.searchResults).toBeVisible({timeout: 2000});

			await expect(searchPage.searchResultsItems).toHaveCount(0, {
				timeout: 2000,
			});
		}
	}).toPass({timeout: 30000});

	if (applyCategory) {
		const categoryCheckbox = await searchPage.getSearchFacetCheckbox(
			applyCategory,
			'Category'
		);

		await searchPage.selectSearchFacetCheckbox(categoryCheckbox);

		if (categoryKeepsVisible) {
			await expect(resultLink).toBeVisible({timeout: 5000});
		}
		else {
			await expect(resultLink).toBeHidden({timeout: 5000});
		}
	}
}

async function verifyAsGuest(
	browser: Browser,
	options: Omit<VerifyOptions, 'searchPage'>
) {
	const guestContext = await browser.newContext();

	try {
		await verifySearch({
			...options,
			searchPage: new SearchPage(await guestContext.newPage()),
		});
	}
	finally {
		await guestContext.close();
	}
}

async function verifyAsUser(
	page: Page,
	alternateName: string,
	options: Omit<VerifyOptions, 'searchPage'>
) {
	await performUserSwitchViaApi(page, alternateName);

	await verifySearch({...options, searchPage: new SearchPage(page)});
}

interface Baseline {
	cmsSiteId: number;
	guestGroupId: string;
	id: string;
	layoutUrl: string;
	roleNames: string[];
	space: {externalReferenceCode: string; name: string; siteId: number};
	user: Awaited<ReturnType<typeof createSiteUser>> | null;
}

async function provisionBaseline(
	apiHelpers: DataApiHelpers,
	page: Page,
	layout: Layout,
	withUser: boolean
): Promise<Baseline> {
	const id = uniqueId();

	const guestGroupId = String(
		await apiHelpers.headlessAdminSite.getSite('L_GUEST').then((r) => r.id)
	);
	const cmsSiteId = await apiHelpers.headlessAdminUser
		.getSiteByFriendlyUrlPath('cms')
		.then((r) => r.id);

	const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: `Space ${id}`,
		settings: {},
		type: 'Space',
	});

	const user = withUser
		? await createSiteUser(apiHelpers, guestGroupId)
		: null;

	return {
		cmsSiteId,
		guestGroupId,
		id,
		layoutUrl: `/web/guest${layout.friendlyURL}`,
		roleNames: withUser ? ['Guest', 'User'] : ['Guest'],
		space,
		user,
	};
}

test(
	'Tagged and categorized Basic Web Content is discoverable by keyword and category filter for GUEST and USER',
	{tag: ['@LPD-95529', '@LPD-95529/TC-6.a']},
	async ({apiHelpers, browser, layout, page}) => {
		const base = await provisionBaseline(apiHelpers, page, layout, true);
		const title = `Content ${base.id}`;
		const catName = `cat${base.id}`;

		const {category, vocabulary} = await createCmsCategory(
			apiHelpers,
			base.cmsSiteId,
			catName
		);

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				keywords: [`tag${base.id}`],
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				taxonomyCategoryIds: [category.id],
				title,
			},
			'cms/basic-web-contents',
			base.space.name
		);

		await grantEntryView(
			apiHelpers,
			'cms/basic-web-contents',
			entry.id,
			base.roleNames
		);

		await grantCategoryView(
			apiHelpers,
			vocabulary.id,
			category.id,
			base.roleNames
		);

		await connectSpaceToGuest(apiHelpers, base.space.externalReferenceCode);

		await test.step('SiteA builds the search page', async () => {
			await buildSearchPage(new SearchPage(page), base.layoutUrl);
		});

		await test.step('GUEST finds the content by keyword and category filter', async () => {
			await verifyAsGuest(browser, {
				applyCategory: catName,
				expectFound: true,
				layoutUrl: base.layoutUrl,
				searchText: base.id,
				title,
			});
		});

		await test.step('USER finds the content by keyword and category filter', async () => {
			await verifyAsUser(page, base.user.alternateName, {
				applyCategory: catName,
				expectFound: true,
				layoutUrl: base.layoutUrl,
				searchText: base.id,
				title,
			});
		});
	}
);

test(
	'Tagged and categorized Structured Content is discoverable by keyword and category filter for USER',
	{tag: ['@LPD-95529', '@LPD-95529/TC-6.b']},
	async ({apiHelpers, layout, page}) => {
		const base = await provisionBaseline(apiHelpers, page, layout, true);
		const title = `Content ${base.id}`;
		const catName = `cat${base.id}`;

		const {applicationName} = await createStructuredContentDefinition(
			apiHelpers,
			base.space.externalReferenceCode
		);

		const {category, vocabulary} = await createCmsCategory(
			apiHelpers,
			base.cmsSiteId,
			catName
		);

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				keywords: [`tag${base.id}`],
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				taxonomyCategoryIds: [category.id],
				title,
			},
			applicationName,
			base.space.name
		);

		await grantEntryView(
			apiHelpers,
			applicationName,
			entry.id,
			base.roleNames
		);

		await grantCategoryView(
			apiHelpers,
			vocabulary.id,
			category.id,
			base.roleNames
		);

		await connectSpaceToGuest(apiHelpers, base.space.externalReferenceCode);

		await test.step('SiteA builds the search page', async () => {
			await buildSearchPage(new SearchPage(page), base.layoutUrl);
		});

		await test.step('USER finds the structured content by keyword and category filter', async () => {
			await verifyAsUser(page, base.user.alternateName, {
				applyCategory: catName,
				expectFound: true,
				layoutUrl: base.layoutUrl,
				searchText: base.id,
				title,
			});
		});
	}
);

test(
	'Tagged and categorized CMS file is discoverable by keyword and category filter for GUEST',
	{tag: ['@LPD-95529', '@LPD-95529/TC-6.c']},
	async ({apiHelpers, browser, layout, page}) => {
		const base = await provisionBaseline(apiHelpers, page, layout, false);
		const title = `Content ${base.id}`;
		const catName = `cat${base.id}`;

		const {category, vocabulary} = await createCmsCategory(
			apiHelpers,
			base.cmsSiteId,
			catName
		);

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {fileBase64: GIF_BASE64, name: `${base.id}.gif`},
				keywords: [`tag${base.id}`],
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				taxonomyCategoryIds: [category.id],
				title,
			},
			'cms/basic-documents',
			base.space.name
		);

		await grantEntryView(
			apiHelpers,
			'cms/basic-documents',
			entry.id,
			base.roleNames,
			['DOWNLOAD_FILE', 'VIEW']
		);

		await grantCategoryView(
			apiHelpers,
			vocabulary.id,
			category.id,
			base.roleNames
		);

		await connectSpaceToGuest(apiHelpers, base.space.externalReferenceCode);

		await test.step('SiteA builds the search page', async () => {
			await buildSearchPage(new SearchPage(page), base.layoutUrl);
		});

		await test.step('GUEST finds the file by keyword and category filter', async () => {
			await verifyAsGuest(browser, {
				applyCategory: catName,
				expectFound: true,
				layoutUrl: base.layoutUrl,
				searchText: base.id,
				title,
			});
		});
	}
);

test(
	'Uncategorized Basic Web Content is excluded by the category filter for GUEST and USER',
	{tag: ['@LPD-95529', '@LPD-95529/TC-6.d']},
	async ({apiHelpers, browser, layout, page}) => {
		const base = await provisionBaseline(apiHelpers, page, layout, true);
		const catName = `cat${base.id}`;
		const categorizedTitle = `Content ${base.id} categorized`;
		const uncategorizedTitle = `Content ${base.id} plain`;

		const {category, vocabulary} = await createCmsCategory(
			apiHelpers,
			base.cmsSiteId,
			catName
		);

		const categorizedEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				taxonomyCategoryIds: [category.id],
				title: categorizedTitle,
			},
			'cms/basic-web-contents',
			base.space.name
		);

		const uncategorizedEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: uncategorizedTitle,
			},
			'cms/basic-web-contents',
			base.space.name
		);

		await grantEntryView(
			apiHelpers,
			'cms/basic-web-contents',
			categorizedEntry.id,
			base.roleNames
		);

		await grantEntryView(
			apiHelpers,
			'cms/basic-web-contents',
			uncategorizedEntry.id,
			base.roleNames
		);

		await grantCategoryView(
			apiHelpers,
			vocabulary.id,
			category.id,
			base.roleNames
		);

		await connectSpaceToGuest(apiHelpers, base.space.externalReferenceCode);

		await test.step('SiteA builds the search page', async () => {
			await buildSearchPage(new SearchPage(page), base.layoutUrl);
		});

		await test.step('GUEST sees the uncategorized content excluded by the category filter', async () => {
			await verifyAsGuest(browser, {
				applyCategory: catName,
				categoryKeepsVisible: false,
				expectFound: true,
				layoutUrl: base.layoutUrl,
				searchText: base.id,
				title: uncategorizedTitle,
			});
		});

		await test.step('USER sees the uncategorized content excluded by the category filter', async () => {
			await verifyAsUser(page, base.user.alternateName, {
				applyCategory: catName,
				categoryKeepsVisible: false,
				expectFound: true,
				layoutUrl: base.layoutUrl,
				searchText: base.id,
				title: uncategorizedTitle,
			});
		});
	}
);

test(
	'Structured Content becomes discoverable for USER after a tag is added and republished',
	{tag: ['@LPD-95529', '@LPD-95529/TC-6.e']},
	async ({apiHelpers, layout, page}) => {
		const base = await provisionBaseline(apiHelpers, page, layout, true);
		const title = `Content ${base.id}`;
		const tag = `tag${base.id}`;

		const {applicationName} = await createStructuredContentDefinition(
			apiHelpers,
			base.space.externalReferenceCode
		);

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title,
			},
			applicationName,
			base.space.name
		);

		await grantEntryView(
			apiHelpers,
			applicationName,
			entry.id,
			base.roleNames
		);

		await connectSpaceToGuest(apiHelpers, base.space.externalReferenceCode);

		await test.step('SiteA builds the search page', async () => {
			await buildSearchPage(new SearchPage(page), base.layoutUrl);
		});

		await test.step('USER does not find the content by the tag before tagging', async () => {
			await verifyAsUser(page, base.user.alternateName, {
				expectFound: false,
				layoutUrl: base.layoutUrl,
				searchText: tag,
				title,
			});
		});

		await test.step('CA adds the tag and republishes', async () => {

			// The previous step signed the page session in as the regular
			// user, and apiHelpers rides that session. The patch must run as
			// the administrator, or the portal rejects it with a 403 that
			// ApiHelpers.patch does not surface.

			await performUserSwitchViaApi(page, 'test');

			await apiHelpers.objectEntry.patchObjectEntry(
				{keywords: [tag]},
				applicationName,
				entry.id
			);
		});

		await test.step('USER finds the content by the tag after tagging', async () => {
			await verifyAsUser(page, base.user.alternateName, {
				expectFound: true,
				layoutUrl: base.layoutUrl,
				searchText: tag,
				title,
			});
		});
	}
);

test(
	'CMS file becomes discoverable for GUEST after a tag is added',
	{tag: ['@LPD-95529', '@LPD-95529/TC-6.f']},
	async ({apiHelpers, browser, layout, page}) => {
		const base = await provisionBaseline(apiHelpers, page, layout, false);
		const title = `Content ${base.id}`;
		const tag = `tag${base.id}`;

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {fileBase64: GIF_BASE64, name: `${base.id}.gif`},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title,
			},
			'cms/basic-documents',
			base.space.name
		);

		await grantEntryView(
			apiHelpers,
			'cms/basic-documents',
			entry.id,
			base.roleNames,
			['DOWNLOAD_FILE', 'VIEW']
		);

		await connectSpaceToGuest(apiHelpers, base.space.externalReferenceCode);

		await test.step('SiteA builds the search page', async () => {
			await buildSearchPage(new SearchPage(page), base.layoutUrl);
		});

		await test.step('GUEST does not find the file by the tag before tagging', async () => {
			await verifyAsGuest(browser, {
				expectFound: false,
				layoutUrl: base.layoutUrl,
				searchText: tag,
				title,
			});
		});

		await test.step('CA adds the tag to the file', async () => {
			await apiHelpers.objectEntry.patchObjectEntry(
				{keywords: [tag]},
				'cms/basic-documents',
				entry.id
			);
		});

		await test.step('GUEST finds the file by the tag after tagging', async () => {
			await verifyAsGuest(browser, {
				expectFound: true,
				layoutUrl: base.layoutUrl,
				searchText: tag,
				title,
			});
		});
	}
);

test(
	'Unpublished Basic Web Content is no longer discoverable for GUEST and USER',
	{tag: ['@LPD-95529', '@LPD-95529/TC-6.g']},
	async ({apiHelpers, browser, layout, page}) => {
		const base = await provisionBaseline(apiHelpers, page, layout, true);
		const title = `Content ${base.id}`;

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				keywords: [`tag${base.id}`],
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title,
			},
			'cms/basic-web-contents',
			base.space.name
		);

		await grantEntryView(
			apiHelpers,
			'cms/basic-web-contents',
			entry.id,
			base.roleNames
		);

		await connectSpaceToGuest(apiHelpers, base.space.externalReferenceCode);

		await test.step('SiteA builds the search page', async () => {
			await buildSearchPage(new SearchPage(page), base.layoutUrl);
		});

		await test.step('GUEST finds the content while published', async () => {
			await verifyAsGuest(browser, {
				expectFound: true,
				layoutUrl: base.layoutUrl,
				searchText: base.id,
				title,
			});
		});

		await test.step('CA unpublishes the content', async () => {
			await apiHelpers.objectEntry.expireObjectEntryByExternalReferenceCode(
				'cms/basic-web-contents',
				base.space.name,
				entry.externalReferenceCode
			);
		});

		await test.step('GUEST no longer finds the content', async () => {
			await verifyAsGuest(browser, {
				expectFound: false,
				layoutUrl: base.layoutUrl,
				searchText: base.id,
				title,
			});
		});

		await test.step('USER no longer finds the content', async () => {
			await verifyAsUser(page, base.user.alternateName, {
				expectFound: false,
				layoutUrl: base.layoutUrl,
				searchText: base.id,
				title,
			});
		});
	}
);

test(
	'Content is no longer searchable after the Space is disconnected from the site',
	{tag: ['@LPD-95529', '@LPD-95529/TC-6.h']},
	async ({apiHelpers, browser, layout, page}) => {
		const base = await provisionBaseline(apiHelpers, page, layout, true);
		const title = `Content ${base.id}`;

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				keywords: [`tag${base.id}`],
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title,
			},
			'cms/basic-web-contents',
			base.space.name
		);

		await grantEntryView(
			apiHelpers,
			'cms/basic-web-contents',
			entry.id,
			base.roleNames
		);

		await connectSpaceToGuest(apiHelpers, base.space.externalReferenceCode);

		await test.step('SiteA builds the search page', async () => {
			await buildSearchPage(new SearchPage(page), base.layoutUrl);
		});

		await test.step('GUEST finds the content while connected', async () => {
			await verifyAsGuest(browser, {
				expectFound: true,
				layoutUrl: base.layoutUrl,
				searchText: base.id,
				title,
			});
		});

		await test.step('CA disconnects the Space from the site', async () => {
			await disconnectSpaceFromGuest(
				apiHelpers,
				base.space.externalReferenceCode
			);
		});

		await test.step('GUEST no longer finds the content', async () => {
			await verifyAsGuest(browser, {
				expectFound: false,
				layoutUrl: base.layoutUrl,
				searchText: base.id,
				title,
			});
		});

		await test.step('USER no longer finds the content', async () => {
			await verifyAsUser(page, base.user.alternateName, {
				expectFound: false,
				layoutUrl: base.layoutUrl,
				searchText: base.id,
				title,
			});
		});
	}
);
