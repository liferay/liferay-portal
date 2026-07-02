/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {DataApiHelpers} from '../../../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../../../liferay.config';
import {CollectionsPage} from '../../../../../pages/asset-list-web/CollectionsPage';
import {PageEditorPage} from '../../../../../pages/layout-content-page-editor-web/PageEditorPage';
import {getRandomInt} from '../../../../../utils/getRandomInt';
import getRandomString from '../../../../../utils/getRandomString';
import {waitForAlert} from '../../../../../utils/waitForAlert';
import getPageDefinition from '../../../../layout-content-page-editor-web/main/utils/getPageDefinition';

const BASIC_DOCUMENT_ERC = 'L_CMS_BASIC_DOCUMENT';
const BASIC_WEB_CONTENT_ERC = 'L_CMS_BASIC_WEB_CONTENT';

export const CMS_FILE_APPLICATION = 'cms/basic-documents';
export const CMS_WEB_CONTENT_APPLICATION = 'cms/basic-web-contents';

const jsonWebServices = async (
	apiHelpers: DataApiHelpers,
	path: string,
	params: Record<string, string>
) => {
	const urlSearchParams = new URLSearchParams();

	for (const [key, value] of Object.entries(params)) {
		urlSearchParams.append(key, value);
	}

	return apiHelpers.post(
		`${liferayConfig.environment.baseUrl}/api/jsonws/${path}`,
		{
			data: urlSearchParams.toString(),
			headers: await apiHelpers.getJSONWebServicesHeaders(),
		}
	);
};

export async function getObjectClassName(
	apiHelpers: DataApiHelpers,
	externalReferenceCode: string
): Promise<string> {
	const objectDefinition = await apiHelpers.get(
		`${apiHelpers.baseUrl}object-admin/v1.0/object-definitions/by-external-reference-code/${externalReferenceCode}`
	);

	return objectDefinition.className;
}

export async function getBasicWebContentClassName(apiHelpers: DataApiHelpers) {
	return getObjectClassName(apiHelpers, BASIC_WEB_CONTENT_ERC);
}

export async function getBasicDocumentClassName(apiHelpers: DataApiHelpers) {
	return getObjectClassName(apiHelpers, BASIC_DOCUMENT_ERC);
}

export async function getClassNameId(
	apiHelpers: DataApiHelpers,
	className: string
): Promise<string> {
	const {classNameId} =
		await apiHelpers.jsonWebServicesClassName.fetchClassName(className);

	return classNameId;
}

export async function getAssetEntryId(
	apiHelpers: DataApiHelpers,
	className: string,
	classPK: number | string
): Promise<string> {
	const assetEntry = await jsonWebServices(
		apiHelpers,
		'assetentry/get-entry',
		{
			className,
			classPK: String(classPK),
		}
	);

	return assetEntry.entryId;
}

/**
 * Creates a published custom CMS content structure (e.g. "Event") with a
 * localizable Title and Body, available in the given Space, and returns its
 * REST application name and class name id.
 */
export async function createEventStructure(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string
) {
	const definition = await apiHelpers.objectAdmin.postRandomObjectDefinition({
		objectDefinitionExternalReferenceCode: `Event${getRandomInt()}`,
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
				businessType: 'LongText',
				externalReferenceCode: getRandomString(),
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
				label: {en_US: 'Body'},
				localized: true,
				name: 'body',
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
		classNameId: await getClassNameId(
			apiHelpers,
			definition.className as string
		),
		definition,
	};
}

/**
 * Creates a dynamic collection through the Collections editor UI: sets the item
 * type, scopes it to the given Space, and adds a tag or category filter. Returns
 * the collection title (used to select it in the Collection Display picker).
 */
export async function createDynamicCollectionWithFilterViaUI(
	collectionsPage: CollectionsPage,
	page: Page,
	site: {friendlyUrlPath: string},
	{
		filterProperty,
		filterValueName,
		itemTypeLabel,
		spaceName,
	}: {
		filterProperty: 'assetCategories' | 'assetTags';
		filterValueName: string;
		itemTypeLabel: string;
		spaceName: string;
	}
) {
	const title = getRandomString();

	await collectionsPage.goto(site.friendlyUrlPath);
	await collectionsPage.addNewDynamicCollection(title);

	await page.getByLabel('Item Type').selectOption({label: itemTypeLabel});

	// Scope to the Space

	await page.getByRole('button', {name: 'Scope'}).click();
	await page.getByRole('button', {name: 'Select Site'}).click();
	await page
		.getByRole('menuitem', {name: 'Other Site, Asset Library, or'})
		.click();

	const scopeFrame = page.locator('iframe[title="Scope"]').contentFrame();

	await scopeFrame.getByRole('link', {name: 'Spaces'}).click();
	await scopeFrame.getByRole('link', {exact: true, name: spaceName}).click();

	// Add the tag / category filter. The condition row defaults to "Tags"; the
	// "of the following" select chooses the property. The filter value picker is
	// opened by the last "Select" button (the first one belongs to Scope).

	await page.getByRole('button', {name: 'Filter'}).click();

	await page.getByLabel('of the following').selectOption(filterProperty);

	const isTag = filterProperty === 'assetTags';

	await page
		.getByRole('button', {
			exact: !isTag,
			name: isTag ? 'Select Tags' : 'Select',
		})
		.last()
		.click();

	// Pick the value, then confirm with "Done". The Tags picker is a table inside
	// an iframe; the Categories picker is a checkbox tree directly on the page.

	if (isTag) {
		await page
			.frameLocator('iframe[title="Tags"]')
			.getByRole('row', {name: filterValueName})
			.getByRole('checkbox')
			.check();
	}
	else {

		// The Categories tree row is a Clay custom checkbox with no accessible
		// name; toggle it by clicking its label within the matching row.

		await page
			.locator('.autofit-row')
			.filter({hasText: filterValueName})
			.locator('input[type="checkbox"]')
			.check({force: true});
	}

	await page.getByRole('button', {name: 'Done'}).click();

	await page.getByRole('button', {name: 'Save'}).click();
	await waitForAlert(page);

	return {title};
}

/**
 * Creates a CMS vocabulary (available to all asset libraries / Spaces) with a
 * single category, and returns the category's id and name.
 */
export async function createCmsCategory(apiHelpers: DataApiHelpers) {
	const cmsSiteId = await apiHelpers.headlessAdminUser
		.getSiteByFriendlyUrlPath('cms')
		.then((response) => response.id);

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
			siteId: cmsSiteId,
			visibilityType: 'PUBLIC',
		});

	const name = getRandomString();

	const category =
		await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
			{name, vocabularyId: vocabulary.id}
		);

	return {id: category.id, name};
}

export async function createManualCollection(
	apiHelpers: DataApiHelpers,
	groupId: string,
	assetEntryIds: string[]
) {
	const title = getRandomString();

	const collection =
		await apiHelpers.jsonWebServicesAssetListEntry.addManualAssetListEntry({
			groupId,
			title,
		});

	for (const assetEntryId of assetEntryIds) {
		await jsonWebServices(
			apiHelpers,
			'assetlist.assetlistentry/add-asset-entry-selection',
			{
				assetEntryId,
				assetListEntryId: String(collection.assetListEntryId),
				segmentsEntryId: '0',
				serviceContext: JSON.stringify({scopeGroupId: groupId}),
			}
		);
	}

	return {title};
}

export async function createDynamicCollection(
	apiHelpers: DataApiHelpers,
	groupId: string,
	typeSettings: string
) {
	const title = getRandomString();

	await apiHelpers.jsonWebServicesAssetListEntry.addDynamicAssetListEntry({
		groupId,
		title,
		typeSettings,
	});

	return {title};
}

/**
 * Creates a content page with a Collection Display bound to the named
 * collection, drops a Heading into the collection item mapped to the given
 * field (default "Title"), publishes the page, and returns its view URL.
 */
export async function addMappedCollectionDisplay(
	apiHelpers: DataApiHelpers,
	page: Page,
	pageEditorPage: PageEditorPage,
	site: {friendlyUrlPath: string; id: string},
	collectionTitle: string,
	fieldLabel = 'Title'
) {
	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition(),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.addFragment('Content Display', 'Collection Display');

	await pageEditorPage.selectFragment(
		await pageEditorPage.getFragmentId('Collection Display')
	);

	await pageEditorPage.chooseCollectionDisplayCollection(
		'Collections',
		collectionTitle,
		{search: true}
	);

	await pageEditorPage.waitForChangesSaved();

	// Drop a Heading into the collection item and map it to the entry field

	await pageEditorPage.addFragment(
		'Basic Components',
		'Heading',
		page.locator('.page-editor__collection-item.empty').first()
	);

	await pageEditorPage.goToSidebarTab('Browser');

	await page.getByLabel('Select element-text').click();

	await page.getByLabel('Field').selectOption({label: fieldLabel});

	await pageEditorPage.waitForChangesSaved();

	await pageEditorPage.publishPage();

	return {
		layout,
		viewURL: `${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`,
	};
}
