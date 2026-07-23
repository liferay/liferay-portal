/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Browser, Page, expect, mergeTests} from '@playwright/test';

import {collectionsPagesTest} from '../../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import getRandomString from '../../../../utils/getRandomString';
import {
	CMS_FILE_APPLICATION,
	CMS_WEB_CONTENT_APPLICATION,
	addMappedCollectionDisplay,
	createCMSCategory,
	createDynamicCollection,
	createDynamicCollectionWithFilterViaUI,
	createEventStructure,
	createManualCollection,
	getAssetEntryId,
	getBasicDocumentClassName,
	getBasicWebContentClassName,
	getClassNameId,
} from './helpers/collectionDisplay';

const test = mergeTests(
	collectionsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

const TINY_FILE = 'R0lGODlhAQABAAAAACw=';

async function createSpace(apiHelpers: DataApiHelpers) {
	return apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: `Space ${getRandomString()}`,
		settings: {},
		type: 'Space',
	});
}

async function connectSpace(
	apiHelpers: DataApiHelpers,
	space: {externalReferenceCode: string},
	site: {externalReferenceCode: string}
) {
	await apiHelpers.headlessAssetLibrary.connectSite(
		space.externalReferenceCode,
		site.externalReferenceCode,
		{searchable: true}
	);
}

async function createWebContent(
	apiHelpers: DataApiHelpers,
	spaceName: string,
	title: string
) {
	return apiHelpers.objectEntry.postObjectEntry(
		{
			content: `<p>${title} body</p>`,
			objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			title,
		},
		CMS_WEB_CONTENT_APPLICATION,
		spaceName
	);
}

async function createDocument(
	apiHelpers: DataApiHelpers,
	spaceName: string,
	title: string
) {
	return apiHelpers.objectEntry.postObjectEntry(
		{
			file: {fileBase64: TINY_FILE, name: `${title}.png`},
			objectEntryFolderExternalReferenceCode: 'L_FILES',
			title,
		},
		CMS_FILE_APPLICATION,
		spaceName
	);
}

async function createEventEntry(
	apiHelpers: DataApiHelpers,
	applicationName: string,
	spaceName: string,
	title: string
) {
	return apiHelpers.objectEntry.postObjectEntry(
		{
			body: `${title} body`,
			objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			title,
		},
		applicationName,
		spaceName
	);
}

async function withGuestPage(
	browser: Browser,
	viewURL: string,
	assertions: (guestPage: Page) => Promise<void>
) {
	const context = await browser.newContext();

	try {
		const guestPage = await context.newPage();

		await guestPage.goto(viewURL);

		await assertions(guestPage);
	}
	finally {
		await context.close();
	}
}

test(
	'TC-7.a Manual collection of Basic Web Content renders the items in order for GUEST',
	{tag: '@LPD-95530'},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		const space = await createSpace(apiHelpers);

		const className = await getBasicWebContentClassName(apiHelpers);

		const titles = [
			`A ${getRandomString()}`,
			`B ${getRandomString()}`,
			`C ${getRandomString()}`,
		];

		const assetEntryIds = [];

		for (const title of titles) {
			const entry = await createWebContent(apiHelpers, space.name, title);

			await apiHelpers.objectEntry.putObjectEntryPermissions(
				CMS_WEB_CONTENT_APPLICATION,
				entry.id,
				[{actionIds: ['VIEW'], roleName: 'Guest'}]
			);

			assetEntryIds.push(
				await getAssetEntryId(apiHelpers, className, entry.id)
			);
		}

		await connectSpace(apiHelpers, space, site);

		const orderedTitles = [titles[2], titles[0], titles[1]];

		const collection = await createManualCollection(
			apiHelpers,
			String(site.id),
			[assetEntryIds[2], assetEntryIds[0], assetEntryIds[1]]
		);

		const {viewURL} =
			await test.step('Render the collection in a Collection Display mapped to Title', () =>
				addMappedCollectionDisplay(
					apiHelpers,
					page,
					pageEditorPage,
					site,
					collection.title
				));

		await test.step('GUEST sees the items in the configured order', async () => {
			await withGuestPage(browser, viewURL, async (guestPage) => {
				for (const title of orderedTitles) {
					await expect(guestPage.getByText(title)).toBeVisible();
				}

				const content = await guestPage
					.locator('#main-content')
					.innerText();

				const positions = orderedTitles.map((title) =>
					content.indexOf(title)
				);

				for (const position of positions) {
					expect(position).toBeGreaterThanOrEqual(0);
				}

				expect(positions[0]).toBeLessThan(positions[1]);
				expect(positions[1]).toBeLessThan(positions[2]);
			});
		});
	}
);

test(
	'TC-7.b Manual collection of custom structure content renders in order for GUEST',
	{tag: '@LPD-95530'},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		const space = await createSpace(apiHelpers);

		const {applicationName, definition} = await createEventStructure(
			apiHelpers,
			space.externalReferenceCode
		);

		const titles = [
			`A ${getRandomString()}`,
			`B ${getRandomString()}`,
			`C ${getRandomString()}`,
		];

		const assetEntryIds = [];

		for (const title of titles) {
			const entry = await createEventEntry(
				apiHelpers,
				applicationName,
				space.name,
				title
			);

			await apiHelpers.objectEntry.putObjectEntryPermissions(
				applicationName,
				entry.id,
				[{actionIds: ['VIEW'], roleName: 'Guest'}]
			);

			assetEntryIds.push(
				await getAssetEntryId(
					apiHelpers,
					definition.className as string,
					entry.id
				)
			);
		}

		await connectSpace(apiHelpers, space, site);

		const orderedTitles = [titles[2], titles[0], titles[1]];

		const collection = await createManualCollection(
			apiHelpers,
			String(site.id),
			[assetEntryIds[2], assetEntryIds[0], assetEntryIds[1]]
		);

		const {viewURL} =
			await test.step('Render the collection in a Collection Display mapped to Title', () =>
				addMappedCollectionDisplay(
					apiHelpers,
					page,
					pageEditorPage,
					site,
					collection.title
				));

		await test.step('GUEST sees the custom structure items in the configured order', async () => {
			await withGuestPage(browser, viewURL, async (guestPage) => {
				for (const title of orderedTitles) {
					await expect(guestPage.getByText(title)).toBeVisible();
				}

				const content = await guestPage
					.locator('#main-content')
					.innerText();

				const positions = orderedTitles.map((title) =>
					content.indexOf(title)
				);

				for (const position of positions) {
					expect(position).toBeGreaterThanOrEqual(0);
				}

				expect(positions[0]).toBeLessThan(positions[1]);
				expect(positions[1]).toBeLessThan(positions[2]);
			});
		});
	}
);

test(
	'TC-7.d Dynamic collection filtered by tag shows only tagged content for GUEST',
	{tag: ['@LPD-95530', '@LPD-96296']},
	async ({
		apiHelpers,
		browser,
		collectionsPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const space = await createSpace(apiHelpers);

		const tag = `tag${getRandomString()}`.toLowerCase();
		const taggedTitle = `Tagged ${getRandomString()}`;
		const untaggedTitle = `Untagged ${getRandomString()}`;

		const tagged = await apiHelpers.objectEntry.postObjectEntry(
			{
				keywords: [tag],
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: taggedTitle,
			},
			CMS_WEB_CONTENT_APPLICATION,
			space.name
		);
		const untagged = await createWebContent(
			apiHelpers,
			space.name,
			untaggedTitle
		);

		for (const entry of [tagged, untagged]) {
			await apiHelpers.objectEntry.putObjectEntryPermissions(
				CMS_WEB_CONTENT_APPLICATION,
				entry.id,
				[{actionIds: ['VIEW'], roleName: 'Guest'}]
			);
		}

		await connectSpace(apiHelpers, space, site);

		const collection = await createDynamicCollectionWithFilterViaUI(
			collectionsPage,
			page,
			site,
			{
				filterProperty: 'assetTags',
				filterValueName: tag,
				itemTypeLabel: 'Basic Web Content (CMS)',
				spaceName: space.name,
			}
		);

		const {viewURL} =
			await test.step('Render the collection in a Collection Display mapped to Title', () =>
				addMappedCollectionDisplay(
					apiHelpers,
					page,
					pageEditorPage,
					site,
					collection.title
				));

		await test.step('GUEST sees only the tagged content', async () => {
			await withGuestPage(browser, viewURL, async (guestPage) => {
				await expect(guestPage.getByText(taggedTitle)).toBeVisible();
				await expect(guestPage.getByText(untaggedTitle)).toHaveCount(0);
			});
		});
	}
);

test(
	'TC-7.c Dynamic collection filtered by category shows only categorized content for GUEST',
	{tag: '@LPD-95530'},
	async ({
		apiHelpers,
		browser,
		collectionsPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const space = await createSpace(apiHelpers);

		const category = await createCMSCategory(apiHelpers);

		const categorizedTitle = `Categorized ${getRandomString()}`;
		const uncategorizedTitle = `Uncategorized ${getRandomString()}`;

		const categorized = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				taxonomyCategoryIds: [category.id],
				title: categorizedTitle,
			},
			CMS_WEB_CONTENT_APPLICATION,
			space.name
		);
		const uncategorized = await createWebContent(
			apiHelpers,
			space.name,
			uncategorizedTitle
		);

		for (const entry of [categorized, uncategorized]) {
			await apiHelpers.objectEntry.putObjectEntryPermissions(
				CMS_WEB_CONTENT_APPLICATION,
				entry.id,
				[{actionIds: ['VIEW'], roleName: 'Guest'}]
			);
		}

		await connectSpace(apiHelpers, space, site);

		const collection = await createDynamicCollectionWithFilterViaUI(
			collectionsPage,
			page,
			site,
			{
				filterProperty: 'assetCategories',
				filterValueName: category.name,
				itemTypeLabel: 'Basic Web Content (CMS)',
				spaceName: space.name,
			}
		);

		const {viewURL} =
			await test.step('Render the collection in a Collection Display mapped to Title', () =>
				addMappedCollectionDisplay(
					apiHelpers,
					page,
					pageEditorPage,
					site,
					collection.title
				));

		await test.step('GUEST sees only the categorized content', async () => {
			await withGuestPage(browser, viewURL, async (guestPage) => {
				await expect(
					guestPage.getByText(categorizedTitle)
				).toBeVisible();
				await expect(
					guestPage.getByText(uncategorizedTitle)
				).toHaveCount(0);
			});
		});
	}
);

test(
	'TC-7.e Dynamic collection filtered by Basic Web Content (CMS) shows only web content for GUEST',
	{tag: '@LPD-95530'},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		const space = await createSpace(apiHelpers);

		const className = await getBasicWebContentClassName(apiHelpers);
		const classNameId = await getClassNameId(apiHelpers, className);

		const webContentTitle = `Content ${getRandomString()}`;
		const documentTitle = `Document ${getRandomString()}`;

		const webContent = await createWebContent(
			apiHelpers,
			space.name,
			webContentTitle
		);
		await createDocument(apiHelpers, space.name, documentTitle);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			CMS_WEB_CONTENT_APPLICATION,
			webContent.id,
			[{actionIds: ['VIEW'], roleName: 'Guest'}]
		);

		await connectSpace(apiHelpers, space, site);

		const collection = await createDynamicCollection(
			apiHelpers,
			String(site.id),
			`anyAssetType=${classNameId}\nclassNameIds=${classNameId}\ngroupIds=${space.siteId}\n`
		);

		const {viewURL} =
			await test.step('Render the collection in a Collection Display mapped to Title', () =>
				addMappedCollectionDisplay(
					apiHelpers,
					page,
					pageEditorPage,
					site,
					collection.title
				));

		await test.step('GUEST sees only the Basic Web Content', async () => {
			await withGuestPage(browser, viewURL, async (guestPage) => {
				await expect(
					guestPage.getByText(webContentTitle)
				).toBeVisible();
				await expect(guestPage.getByText(documentTitle)).toHaveCount(0);
			});
		});
	}
);

test(
	'TC-7.f Dynamic collection filtered by the custom structure type shows only those entries for GUEST',
	{tag: '@LPD-95530'},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		const space = await createSpace(apiHelpers);

		const {applicationName, classNameId} = await createEventStructure(
			apiHelpers,
			space.externalReferenceCode
		);

		const eventTitle = `Event ${getRandomString()}`;
		const webContentTitle = `Content ${getRandomString()}`;

		const event = await createEventEntry(
			apiHelpers,
			applicationName,
			space.name,
			eventTitle
		);
		await createWebContent(apiHelpers, space.name, webContentTitle);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			applicationName,
			event.id,
			[{actionIds: ['VIEW'], roleName: 'Guest'}]
		);

		await connectSpace(apiHelpers, space, site);

		const collection = await createDynamicCollection(
			apiHelpers,
			String(site.id),
			`anyAssetType=${classNameId}\nclassNameIds=${classNameId}\ngroupIds=${space.siteId}\n`
		);

		const {viewURL} =
			await test.step('Render the collection in a Collection Display mapped to Title', () =>
				addMappedCollectionDisplay(
					apiHelpers,
					page,
					pageEditorPage,
					site,
					collection.title
				));

		await test.step('GUEST sees only the custom structure entries', async () => {
			await withGuestPage(browser, viewURL, async (guestPage) => {
				await expect(guestPage.getByText(eventTitle)).toBeVisible();
				await expect(guestPage.getByText(webContentTitle)).toHaveCount(
					0
				);
			});
		});
	}
);

test(
	'TC-7.g Dynamic collection filtered by Basic Document (CMS) shows only files for GUEST',
	{tag: '@LPD-95530'},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		const space = await createSpace(apiHelpers);

		const className = await getBasicDocumentClassName(apiHelpers);
		const classNameId = await getClassNameId(apiHelpers, className);

		const documentTitle = `Document ${getRandomString()}`;
		const webContentTitle = `Content ${getRandomString()}`;

		const document = await createDocument(
			apiHelpers,
			space.name,
			documentTitle
		);
		await createWebContent(apiHelpers, space.name, webContentTitle);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			CMS_FILE_APPLICATION,
			document.id,
			[{actionIds: ['DOWNLOAD_FILE', 'VIEW'], roleName: 'Guest'}]
		);

		await connectSpace(apiHelpers, space, site);

		const collection = await createDynamicCollection(
			apiHelpers,
			String(site.id),
			`anyAssetType=${classNameId}\nclassNameIds=${classNameId}\ngroupIds=${space.siteId}\n`
		);

		const {viewURL} =
			await test.step('Render the collection in a Collection Display mapped to Title', () =>
				addMappedCollectionDisplay(
					apiHelpers,
					page,
					pageEditorPage,
					site,
					collection.title
				));

		await test.step('GUEST sees only the files', async () => {
			await withGuestPage(browser, viewURL, async (guestPage) => {
				await expect(guestPage.getByText(documentTitle)).toBeVisible();
				await expect(guestPage.getByText(webContentTitle)).toHaveCount(
					0
				);
			});
		});
	}
);

test(
	'TC-7.i Editing a Basic Web Content title updates the published page live for GUEST',
	{tag: '@LPD-95530'},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		const space = await createSpace(apiHelpers);

		const className = await getBasicWebContentClassName(apiHelpers);

		const originalTitle = `Content ${getRandomString()}`;
		const updatedTitle = `Updated ${getRandomString()}`;

		const entry = await createWebContent(
			apiHelpers,
			space.name,
			originalTitle
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			CMS_WEB_CONTENT_APPLICATION,
			entry.id,
			[{actionIds: ['VIEW'], roleName: 'Guest'}]
		);

		await connectSpace(apiHelpers, space, site);

		const collection = await createManualCollection(
			apiHelpers,
			String(site.id),
			[await getAssetEntryId(apiHelpers, className, entry.id)]
		);

		const {viewURL} =
			await test.step('Render the collection in a Collection Display mapped to Title', () =>
				addMappedCollectionDisplay(
					apiHelpers,
					page,
					pageEditorPage,
					site,
					collection.title
				));

		await test.step('GUEST sees the original title', async () => {
			await withGuestPage(browser, viewURL, async (guestPage) => {
				await expect(guestPage.getByText(originalTitle)).toBeVisible();
			});
		});

		await test.step('Edit the title in the CMS', async () => {
			await apiHelpers.objectEntry.putObjectEntry(
				{title: updatedTitle},
				CMS_WEB_CONTENT_APPLICATION,
				entry.id
			);
		});

		await test.step('GUEST sees the updated title without the page being republished', async () => {
			await withGuestPage(browser, viewURL, async (guestPage) => {
				await expect(guestPage.getByText(updatedTitle)).toBeVisible();
				await expect(guestPage.getByText(originalTitle)).toHaveCount(0);
			});
		});
	}
);

test(
	'TC-7.j Editing a custom structure entry title updates the published page live for GUEST',
	{tag: '@LPD-95530'},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		const space = await createSpace(apiHelpers);

		const {applicationName, definition} = await createEventStructure(
			apiHelpers,
			space.externalReferenceCode
		);

		const originalTitle = `Event ${getRandomString()}`;
		const updatedTitle = `Updated ${getRandomString()}`;

		const entry = await createEventEntry(
			apiHelpers,
			applicationName,
			space.name,
			originalTitle
		);

		await apiHelpers.objectEntry.putObjectEntryPermissions(
			applicationName,
			entry.id,
			[{actionIds: ['VIEW'], roleName: 'Guest'}]
		);

		await connectSpace(apiHelpers, space, site);

		const collection = await createManualCollection(
			apiHelpers,
			String(site.id),
			[
				await getAssetEntryId(
					apiHelpers,
					definition.className as string,
					entry.id
				),
			]
		);

		const {viewURL} =
			await test.step('Render the collection in a Collection Display mapped to Title', () =>
				addMappedCollectionDisplay(
					apiHelpers,
					page,
					pageEditorPage,
					site,
					collection.title
				));

		await test.step('GUEST sees the original title', async () => {
			await withGuestPage(browser, viewURL, async (guestPage) => {
				await expect(guestPage.getByText(originalTitle)).toBeVisible();
			});
		});

		await test.step('Edit the title in the CMS', async () => {
			await apiHelpers.objectEntry.putObjectEntry(
				{title: updatedTitle},
				applicationName,
				entry.id
			);
		});

		await test.step('GUEST sees the updated title without the page being republished', async () => {
			await withGuestPage(browser, viewURL, async (guestPage) => {
				await expect(guestPage.getByText(updatedTitle)).toBeVisible();
				await expect(guestPage.getByText(originalTitle)).toHaveCount(0);
			});
		});
	}
);

test(
	'TC-7.k Deleting a Basic Document removes it from the published page for GUEST',
	{tag: '@LPD-95530'},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		const space = await createSpace(apiHelpers);

		const className = await getBasicDocumentClassName(apiHelpers);

		const keptTitle = `Kept ${getRandomString()}`;
		const deletedTitle = `Deleted ${getRandomString()}`;

		const keptEntry = await createDocument(
			apiHelpers,
			space.name,
			keptTitle
		);
		const deletedEntry = await createDocument(
			apiHelpers,
			space.name,
			deletedTitle
		);

		const assetEntryIds = [];

		for (const entry of [keptEntry, deletedEntry]) {
			await apiHelpers.objectEntry.putObjectEntryPermissions(
				CMS_FILE_APPLICATION,
				entry.id,
				[{actionIds: ['DOWNLOAD_FILE', 'VIEW'], roleName: 'Guest'}]
			);

			assetEntryIds.push(
				await getAssetEntryId(apiHelpers, className, entry.id)
			);
		}

		await connectSpace(apiHelpers, space, site);

		const collection = await createManualCollection(
			apiHelpers,
			String(site.id),
			assetEntryIds
		);

		const {viewURL} =
			await test.step('Render the collection in a Collection Display mapped to Title', () =>
				addMappedCollectionDisplay(
					apiHelpers,
					page,
					pageEditorPage,
					site,
					collection.title
				));

		await test.step('GUEST sees both files', async () => {
			await withGuestPage(browser, viewURL, async (guestPage) => {
				await expect(guestPage.getByText(keptTitle)).toBeVisible();
				await expect(guestPage.getByText(deletedTitle)).toBeVisible();
			});
		});

		await test.step('Delete one document in the CMS', async () => {
			await apiHelpers.objectEntry.deleteObjectEntry(
				CMS_FILE_APPLICATION,
				String(deletedEntry.id)
			);
		});

		await test.step('GUEST no longer sees the deleted file', async () => {
			await withGuestPage(browser, viewURL, async (guestPage) => {
				await expect(guestPage.getByText(keptTitle)).toBeVisible();
				await expect(guestPage.getByText(deletedTitle)).toHaveCount(0);
			});
		});
	}
);
