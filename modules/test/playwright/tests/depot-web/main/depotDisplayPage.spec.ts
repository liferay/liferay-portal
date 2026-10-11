/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import {DisplayPageTemplatesPage} from '../../../pages/layout-page-template-admin-web/DisplayPageTemplatesPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';

const test = mergeTests(
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

const DOCUMENT_PATH = path.join(__dirname, 'dependencies/Document.jpg');

async function addConnectedDepot(apiHelpers: DataApiHelpers, site: Site) {
	const assetLibrary =
		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: getRandomString(),
			type: 'AssetLibrary',
		});

	await apiHelpers.headlessAssetLibrary.connectSite(
		assetLibrary.externalReferenceCode,
		site.externalReferenceCode
	);

	const depotEntryGroupRel =
		await apiHelpers.jsonWebServicesDepotGroupRel.getDepotEntryGroupRelByDepotEntryIdToGroupId(
			String(assetLibrary.id),
			String(site.id)
		);

	await apiHelpers.jsonWebServicesDepotGroupRel.updateDDMStructuresAvailable(
		String(depotEntryGroupRel.depotEntryGroupRelId),
		true
	);

	return assetLibrary;
}

async function addDefaultDisplayPageTemplate({
	apiHelpers,
	contentSubtype,
	contentType,
	displayPageTemplatesPage,
	mappings,
	pageEditorPage,
	site,
}: {
	apiHelpers: DataApiHelpers;
	contentSubtype: string;
	contentType: string;
	displayPageTemplatesPage: DisplayPageTemplatesPage;
	mappings: {field: string; fragmentName: string}[];
	pageEditorPage: PageEditorPage;
	site: Site;
}) {
	const name = getRandomString();

	await displayPageTemplatesPage.goto(site.friendlyUrlPath);

	await displayPageTemplatesPage.createTemplate({
		contentSubtype,
		contentType,
		name,
	});

	if (mappings.length) {
		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		await displayPageTemplatesPage.editTemplate(name);

		for (const {field, fragmentName} of mappings) {
			await pageEditorPage.addFragment('Basic Components', fragmentName);

			await pageEditorPage.selectEditable(
				await pageEditorPage.getFragmentId(fragmentName),
				'element-text'
			);

			await pageEditorPage.setMappingConfiguration({
				mapping: {field},
				source: 'structure',
			});
		}

		await displayPageTemplatesPage.publishTemplate();
	}

	const layoutPageTemplateEntry =
		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.fetchLayoutPageTemplateEntry(
			{groupId: site.id, name, type: 'display-page'}
		);

	// Delete the display page template before the depot type it links to

	apiHelpers.data.push({
		id: layoutPageTemplateEntry.layoutPageTemplateEntryId,
		type: 'layoutPageTemplateEntry',
	});

	await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
		{
			layoutPageTemplateEntryId:
				layoutPageTemplateEntry.layoutPageTemplateEntryId,
		}
	);

	return name;
}

async function openConfiguration(page: Page, tab: 'Open Graph' | 'SEO') {
	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page
			.locator('.dropdown-menu')
			.getByRole('menuitem', {name: 'Configure'}),
		trigger: page
			.locator('.control-menu-nav-item')
			.getByLabel('Options', {exact: true}),
	});

	await openConfigurationTab(page, tab);
}

async function openConfigurationTab(page: Page, tab: 'Open Graph' | 'SEO') {
	await page.locator('nav.menubar').getByText(tab, {exact: true}).click();
}

test(
	'A depot document type maps Open Graph and SEO in a connected site display page',
	{tag: '@LPD-109572'},
	async ({
		apiHelpers,
		baseURL,
		browser,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const assetLibrary = await addConnectedDepot(apiHelpers, site);

		const documentTypeName = getRandomString();

		await apiHelpers.headlessDelivery.postSiteDocumentDataDefinitionType(
			assetLibrary.siteId,
			documentTypeName
		);

		const displayPageTemplateName = await addDefaultDisplayPageTemplate({
			apiHelpers,
			contentSubtype: documentTypeName,
			contentType: 'Document',
			displayPageTemplatesPage,
			mappings: [],
			pageEditorPage,
			site,
		});

		await test.step('Map the Open Graph and SEO fields to the depot document type', async () => {
			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			await openConfiguration(page, 'Open Graph');

			for (const [field, mappingField, value] of [
				['Title', 'File Name', '${fileName:File Name}'],
				['Description', 'MIME Type', '${mimeType:MIME Type}'],
				[
					'Image Alt Description',
					'Author Name',
					'${authorName:Author Name}',
				],
			]) {
				await page.getByLabel(field, {exact: true}).fill('');

				await displayPageTemplatesPage.mapConfiguration({
					field,
					mappingField,
				});

				await expect(page.getByLabel(field, {exact: true})).toHaveValue(
					value
				);
			}

			await displayPageTemplatesPage.mapConfiguration({
				field: 'Image',
				mappingField: 'Preview Image',
			});

			await expect(page.getByLabel('Image', {exact: true})).toHaveValue(
				/Preview Image$/
			);

			await openConfigurationTab(page, 'SEO');

			for (const [field, mappingField, value] of [
				['HTML Title', 'File Name', '${fileName:File Name}'],
				['Description', 'MIME Type', '${mimeType:MIME Type}'],
			]) {
				await page.getByLabel(field, {exact: true}).fill('');

				await displayPageTemplatesPage.mapConfiguration({
					field,
					mappingField,
				});

				await expect(page.getByLabel(field, {exact: true})).toHaveValue(
					value
				);
			}
		});

		const fileName = `${getRandomString()}.jpg`;

		const document = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(DOCUMENT_PATH),
			{
				documentType: {name: documentTypeName},
				fileName,
				title: getRandomString(),
			}
		);

		// Delete the document before its document type

		apiHelpers.data.push({id: document.id, type: 'document'});

		const userAccount =
			await apiHelpers.headlessAdminUser.getMyUserAccount();

		await test.step('A guest sees the mapped meta tags', async () => {
			const guestContext = await browser.newContext({baseURL});

			try {
				const guestPage = await guestContext.newPage();

				await guestPage.goto(
					`/web${site.friendlyUrlPath}/d/${document.id}`
				);

				for (const [property, content] of [
					['og:description', 'image/jpeg'],
					['og:image:alt', userAccount.name],
					['og:title', fileName],
				]) {
					await expect(
						guestPage.locator(
							`meta[property="${property}"][content="${content}"]`
						)
					).toBeAttached();
				}

				await expect(
					guestPage.locator(
						'meta[property="og:image"][content*="/documents/"]'
					)
				).toBeAttached();

				await expect(
					guestPage.locator(
						'meta[name="description"][content="image/jpeg"]'
					)
				).toBeAttached();

				await expect(guestPage).toHaveTitle(new RegExp(`^${fileName}`));
			}
			finally {
				await guestContext.close();
			}
		});
	}
);

test(
	'Depot types render with default display page templates on a connected site',
	{tag: '@LPD-109572'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const assetLibrary = await addConnectedDepot(apiHelpers, site);

		const structureName = getRandomString();

		const structure = await apiHelpers.dataEngine.createStructure(
			assetLibrary.siteId,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{name: 'Text'}],
				name: structureName,
			})
		);

		const documentTypeName = getRandomString();

		await apiHelpers.headlessDelivery.postSiteDocumentDataDefinitionType(
			assetLibrary.siteId,
			documentTypeName
		);

		await addDefaultDisplayPageTemplate({
			apiHelpers,
			contentSubtype: structureName,
			contentType: 'Web Content Article',
			displayPageTemplatesPage,
			mappings: [
				{field: 'Title', fragmentName: 'Heading'},
				{field: 'Text', fragmentName: 'Paragraph'},
			],
			pageEditorPage,
			site,
		});

		await addDefaultDisplayPageTemplate({
			apiHelpers,
			contentSubtype: documentTypeName,
			contentType: 'Document',
			displayPageTemplatesPage,
			mappings: [
				{field: 'Title', fragmentName: 'Heading'},
				{field: 'Description', fragmentName: 'Paragraph'},
			],
			pageEditorPage,
			site,
		});

		const webContentText = getRandomString();
		const webContentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postStructuredContent({
			contentFields: [
				{contentFieldValue: {data: webContentText}, name: 'Text'},
			],
			contentStructureId: Number(structure.id),
			datePublished: null,
			siteId: site.id,
			title: webContentTitle,
			viewableBy: 'Anyone',
		});

		const documentDescription = getRandomString();
		const documentTitle = getRandomString();

		const document =
			await apiHelpers.headlessDelivery.postAssetLibraryDocument(
				assetLibrary.id,
				createReadStream(DOCUMENT_PATH),
				{
					description: documentDescription,
					documentType: {name: documentTypeName},
					fileName: `${documentTitle}.jpg`,
					title: documentTitle,
				}
			);

		// Delete the document before its document type

		apiHelpers.data.push({id: document.id, type: 'document'});

		await test.step('The site web content renders with the depot structure display page', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/w/${webContentTitle}`);

			await expect(
				page
					.getByRole('main')
					.getByRole('heading', {name: webContentTitle})
			).toBeVisible();
			await expect(page.getByText(webContentText)).toBeVisible();
		});

		await test.step('The depot document renders with the depot document type display page', async () => {
			await page.goto(`/web${site.friendlyUrlPath}/d/${document.id}`);

			await expect(
				page
					.getByRole('main')
					.getByRole('heading', {name: documentTitle})
			).toBeVisible();
			await expect(page.getByText(documentDescription)).toBeVisible();
		});
	}
);
