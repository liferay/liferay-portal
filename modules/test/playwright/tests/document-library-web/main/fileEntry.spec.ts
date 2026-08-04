/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {siteSettingsPagesTest} from '../../../fixtures/siteSettingsPagesTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {createCategories} from '../../../helpers/CreateCategories';
import {DLFILE_STATUS} from '../../../helpers/json-web-services/JSONWebServicesDocumentLibraryApiHelper';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {performLogout} from '../../../utils/performLogin';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

const test = mergeTests(
	apiHelpersTest,
	documentLibraryPagesTest,
	workflowPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	globalMenuPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	siteSettingsPagesTest
);

test(
	'Check View Usage in Action Menu',
	{
		tag: '@LPD-43391',
	},
	async ({apiHelpers, documentLibraryPage, page, pageEditorPage, site}) => {
		const documentTest = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
			{
				description: getRandomString(),
				fileName: getRandomString(),
				title: getRandomString(),
			}
		);
		await documentLibraryPage.goto(site.friendlyUrlPath);
		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('menuitem', {name: 'View Usages'}),
			trigger: page.getByLabel('Actions', {exact: true}),
		});
		await expect(
			page.getByRole('menuitem', {name: 'View Usages'})
		).toHaveClass(/disabled/);

		const imageId = getRandomString();
		const imageFragment = getFragmentDefinition({
			id: imageId,
			key: 'BASIC_COMPONENT-image',
		});

		const layoutTitle = getRandomString();
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([imageFragment]),
			siteId: site.id,
			title: layoutTitle,
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.selectEditable(imageId, 'image-square');

		await page.getByTitle('Select Image').click();

		const imageCard = page
			.frameLocator('iframe[title="Select"]')
			.getByText(documentTest.title);

		await clickAndExpectToBeHidden({
			target: page.locator('.modal-dialog'),
			trigger: imageCard,
		});

		await pageEditorPage.waitForChangesSaved();

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'View Usages'}),
			trigger: page.getByLabel('Actions', {exact: true}),
		});
		await expect(page.getByRole('menubar')).toContainText('Pages (1)');
		await expect(
			page.getByRole('cell', {name: `${layoutTitle} (Draft)`})
		).toBeVisible();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'View in Page'}),
			trigger: page
				.getByRole('row', {name: `${layoutTitle} (Draft) Page Image`})
				.getByLabel('Show Actions'),
		});

		await expect(
			page.getByRole('heading', {name: layoutTitle})
		).toBeVisible();
	}
);

test(
	'Check order by Relevance in Search of DL',
	{
		tag: '@LPD-32481',
	},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const imagePath = path.join(__dirname, '/dependencies/image1.jpeg');

		for (const title of ['test', getRandomString(), getRandomString()]) {
			await apiHelpers.headlessDelivery.postDocument(
				site.id,
				createReadStream(imagePath),
				{title}
			);
		}

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await documentLibraryPage.orderMenu.click();
		await expect(
			page.getByRole('menuitem', {name: 'Relevance'})
		).not.toBeVisible();

		await page.reload();

		await documentLibraryPage.search('test');

		await documentLibraryPage.orderBy('relevance');

		await expect(page.locator('dd[data-title="test"]')).toHaveAttribute(
			'id',
			/_entries_1$/
		);
	}
);

test(
	'Check if Ordering by Modified Date working, after editing a document',
	{
		tag: '@LPD-32483',
	},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const imagePath = path.join(__dirname, '/dependencies/image1.jpeg');

		const title = getRandomString();
		const document = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(imagePath),
			{title}
		);

		const title2 = getRandomString();
		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(imagePath),
			{title: title2}
		);

		await apiHelpers.headlessDelivery.patchDocument({
			document: {description: getRandomString()},
			documentId: document.id,
		});

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await documentLibraryPage.orderBy('Ascending');
		await documentLibraryPage.orderBy('Descending');

		await expect(
			page.locator(`dd.card-page-item[data-title="${title}"]`)
		).toHaveAttribute('id', /_entries_1$/);
	}
);

test(
	'Show a success message after scheduling a new file',
	{tag: '@LPD-16658'},
	async ({documentLibraryEditFilePage, page, site}) => {
		const scheduleDate = `01/01/${new Date().getFullYear() + 1}`;
		const title = getRandomString();

		await documentLibraryEditFilePage.goToPublishNewFileWithScheduleDate(
			scheduleDate,
			title,
			site.friendlyUrlPath
		);

		await expect(page.getByRole('link', {name: title})).toBeVisible();

		const toastAlertContainer = page.locator('[id="ToastAlertContainer"]');

		await expect(toastAlertContainer).toBeVisible();

		await expect(toastAlertContainer).toContainText(`Success:${title}`);

		await expect(toastAlertContainer).toContainText(
			new Intl.DateTimeFormat('en-US', {
				day: 'numeric',
				month: 'numeric',
				year: '2-digit',
			}).format(new Date(scheduleDate))
		);
	}
);

test(
	'Identify at a glance if a Document is visible for guests',
	{tag: '@LPD-16313'},
	async ({apiHelpers, documentLibraryPage, site}) => {
		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
			{
				title: getRandomString(),
				viewableBy: 'Members',
			}
		);

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await documentLibraryPage.changeView('cards');
		await documentLibraryPage.assertPrivateFileIcon();

		await documentLibraryPage.changeView('table');
		await documentLibraryPage.assertPrivateFileIcon();

		await documentLibraryPage.changeView('list');
		await documentLibraryPage.assertPrivateFileIcon();
	}
);

test(
	'Show icon in the content admin and content editor',
	{tag: '@LPD-16313'},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const title = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
			{
				title,
				viewableBy: 'Members',
			}
		);

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await documentLibraryPage.changeView('cards');

		await documentLibraryPage.goToEditFileEntry(title);

		await documentLibraryPage.assertPrivateFileIcon();

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await page.getByRole('link', {name: title}).click();

		await documentLibraryPage.assertPrivateFileIcon();
	}
);

test(
	'Show icon in the DL item selector',
	{tag: '@LPD-16313'},
	async ({
		apiHelpers,
		documentLibraryEditDocumentTypesPage,
		documentLibraryEditFilePage,
		documentLibraryPage,
		site,
	}) => {
		const dTypeTitle = getRandomString();
		const title = getRandomString();

		await documentLibraryEditDocumentTypesPage.createNewDLTypeWithUploadField(
			dTypeTitle,
			site.friendlyUrlPath
		);

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
			{
				title,
				viewableBy: 'Members',
			}
		);

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await documentLibraryEditFilePage.goToNewFileDifferentType(
			dTypeTitle,
			site.friendlyUrlPath
		);

		await documentLibraryEditFilePage.selectForUpdateButton.click();

		await documentLibraryEditFilePage.assertPrivateFileIconInSelectPopUp(
			'Document'
		);

		await documentLibraryEditFilePage.changeViewInItemSelector(
			'Document',
			'List'
		);

		await documentLibraryEditFilePage.assertPrivateFileIconInSelectPopUp(
			'Document'
		);

		await documentLibraryEditFilePage.changeViewInItemSelector(
			'Document',
			'Table'
		);

		await documentLibraryEditFilePage.assertPrivateFileIconInSelectPopUp(
			'Document'
		);
	}
);

test(
	'Error uploading multiples files with custom document type',
	{
		tag: '@LPD-29609',
	},
	async ({
		documentLibraryEditDocumentTypesPage,
		documentLibraryEditFilePage,
		page,
	}) => {
		const dTypeTitle = getRandomString();

		await documentLibraryEditDocumentTypesPage.createNewDLTypeWithNumericField(
			dTypeTitle
		);
		await documentLibraryEditFilePage.goToNewFileDifferentType(
			'Multiple Files Upload'
		);

		await documentLibraryEditFilePage.publishMultipleFiles(dTypeTitle, [
			path.join(__dirname, '/dependencies/image1.jpeg'),
		]);

		await expect(
			page.getByRole('link', {exact: true, name: 'image1'})
		).toBeVisible();
	}
);

test(
	'Unable to filter by category if their vocabulary is related with a specific document type',
	{
		tag: '@LPD-50971',
	},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const vocabularyName = getRandomString();

		const categories = await createCategories({
			apiHelpers,
			assetTypes: [
				{
					required: false,
					subtype: 'Basic Document',
					type: 'Document',
				},
			],
			categoryNames: [{name: 'Books'}],
			siteId: site.id,
			vocabularyName,
		});

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
			{
				description: getRandomString(),
				fileName: getRandomString(),
				taxonomyCategoryIds: [categories[0].id],
				title: getRandomString(),
			}
		);

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await page.getByLabel('Filter', {exact: true}).click();

		await page.getByRole('menuitem', {name: 'Categories'}).click();

		await expect(
			page
				.frameLocator('iframe[title="Filter by Categories"]')
				.getByText(vocabularyName)
		).toBeVisible();
	}
);

test(
	'Only one well formatted success message on scheduling file from widget page',
	{
		tag: ['@LPD-45614', '@LPD-45658'],
	},
	async ({
		apiHelpers,
		documentLibraryEditFilePage,
		documentLibraryPage,
		page,
		site,
	}) => {
		const portletId = getRandomString();
		const widgetDefinition = getWidgetDefinition({
			id: portletId,
			widgetName: 'com_liferay_document_library_web_portlet_DLPortlet',
		});
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web/${site.name}${layout.friendlyUrlPath}`);

		await documentLibraryPage.goToCreateNewFile();

		const scheduleDate = `01/01/${new Date().getFullYear() + 1}`;
		const title = getRandomString();

		await documentLibraryEditFilePage.publishNewFileWithScheduleDate(
			scheduleDate,
			title
		);

		await expect(page.getByRole('link', {name: title})).toBeVisible();

		const toastAlertContainer = page.locator('[id="ToastAlertContainer"]');

		await expect(toastAlertContainer).not.toContainText(`<strong>`);
		await expect(toastAlertContainer).toContainText(`Success:${title}`);

		await expect(toastAlertContainer).toContainText(
			new Intl.DateTimeFormat('en-US', {
				day: 'numeric',
				month: 'numeric',
				year: '2-digit',
			}).format(new Date(scheduleDate))
		);

		let firstAlertAppear = false;
		let moreAlertsAppear = false;

		await expect(async () => {
			const toastAlertContainers = await page
				.locator('.alert-success', {
					hasText: `Success:${title}`,
				})
				.all();

			if (toastAlertContainers.length >= 1) {
				firstAlertAppear = true;
			}

			if (toastAlertContainers.length > 1) {
				moreAlertsAppear = true;
			}

			expect(firstAlertAppear).toBe(true);
			expect(moreAlertsAppear).toBe(false);
		}).toPass();
	}
);

test(
	'Search in DL portlet does not show results in card view',
	{tag: ['@LPD-31694', '@LPD-202909']},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const title = getRandomString();
		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
			{title}
		);

		const portletId = getRandomString();
		const widgetDefinition = getWidgetDefinition({
			id: portletId,
			widgetName: 'com_liferay_document_library_web_portlet_DLPortlet',
		});
		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await performLogout(page);

		await page.goto('/web' + site.friendlyUrlPath);

		await documentLibraryPage.search(title);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Cards'}),
			trigger: page.getByLabel('Select View, Currently Selected: '),
		});
		await expect(
			page
				.locator('.portlet-document-library')
				.getByRole('link', {name: title})
		).toBeVisible();
	}
);

async function addDocumentShortcut(
	apiHelpers,
	siteId,
	documentTitle: string,
	documentFolderId: number,
	shortcutFolderId: number
) {
	const document = await apiHelpers.headlessDelivery.postDocument(
		siteId,
		createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
		{
			documentFolderId,
			fileName: documentTitle + '.jpeg',
			title: documentTitle,
		}
	);

	return await apiHelpers.headlessDelivery.postDocumentShortcut(siteId, {
		folderId: shortcutFolderId,
		targetDocumentId: document.id,
	});
}

test(
	'Shortcuts in one folder linked to documents in another folder are correctly shown in DL widget as a List',
	{tag: ['@LPD-63743']},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const documentTitle = 'Title' + getRandomString();

		const folderDocuments =
			await apiHelpers.headlessDelivery.postDocumentFolder(site.id, {
				name: 'FolderDocuments' + getRandomString(),
			});

		const folderShortcuts =
			await apiHelpers.headlessDelivery.postDocumentFolder(site.id, {
				name: 'FolderShortcuts' + getRandomString(),
			});

		await addDocumentShortcut(
			apiHelpers,
			site.id,
			documentTitle + '1',
			folderDocuments.id,
			folderShortcuts.id
		);
		await addDocumentShortcut(
			apiHelpers,
			site.id,
			documentTitle + '2',
			folderDocuments.id,
			folderShortcuts.id
		);
		await addDocumentShortcut(
			apiHelpers,
			site.id,
			documentTitle + '3',
			folderDocuments.id,
			folderShortcuts.id
		);

		const widgetDefinition = getWidgetDefinition({
			id: getRandomString(),
			widgetConfig: {
				rootFolderExternalReferenceCode:
					folderShortcuts.externalReferenceCode,
				selectedGroupExternalReferenceCode: site.externalReferenceCode,
			},
			widgetName: 'com_liferay_document_library_web_portlet_DLPortlet',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			page
				.locator('.portlet-document-library')
				.getByRole('link', {name: documentTitle + '1'})
		).toBeVisible();

		await expect(
			page
				.locator('.portlet-document-library')
				.getByRole('link', {name: documentTitle + '2'})
		).toBeVisible();

		await expect(
			page
				.locator('.portlet-document-library')
				.getByRole('link', {name: documentTitle + '3'})
		).toBeVisible();

		await documentLibraryPage.changeView('list');

		await expect(
			page
				.locator('.portlet-document-library')
				.getByRole('link', {name: documentTitle + '1'})
		).toBeVisible();

		await expect(
			page
				.locator('.portlet-document-library')
				.getByRole('link', {name: documentTitle + '2'})
		).toBeVisible();

		await expect(
			page
				.locator('.portlet-document-library')
				.getByRole('link', {name: documentTitle + '3'})
		).toBeVisible();
	}
);

test(
	'Replace option does not work on Categories Selector',
	{
		tag: ['@LPD-27899', '@LPSA-74819'],
	},
	async ({
		apiHelpers,
		documentLibraryEditFilePage,
		documentLibraryPage,
		page,
		site,
	}) => {
		const vocabularyName = getRandomString();

		const categories = await createCategories({
			apiHelpers,
			categoryNames: [
				{name: 'Books'},
				{name: 'Plants'},
				{name: 'Pets'},
				{name: 'Furniture'},
			],
			siteId: site.id,
			vocabularyName,
		});

		const document1 = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
			{
				description: getRandomString(),
				fileName: getRandomString(),
				taxonomyCategoryIds: [categories[0].id, categories[1].id],
				title: getRandomString(),
			}
		);

		const document2 = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
			{
				description: getRandomString(),
				fileName: getRandomString(),
				taxonomyCategoryIds: [categories[0].id, categories[2].id],
				title: getRandomString(),
			}
		);

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await documentLibraryPage.openBulkEditCategoriesModal([
			document1.title,
			document2.title,
		]);

		await expect(
			page.locator('.modal .label-item-expand', {hasText: 'Books'})
		).toBeVisible();

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await documentLibraryPage.replaceCategoriesUsingBulkEditCategoriesModal(
			[document1.title, document2.title],
			[{categoryNames: ['Furniture'], vocabularyName}]
		);

		await waitForAlert(page, 'Success:Changes Saved');

		for (const document of [document1, document2]) {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.goToEditFileEntry(document.title);
			await documentLibraryEditFilePage.openFieldset('Categorization');
			await page.getByText(vocabularyName).waitFor();

			await expect(await page.getByText(document.title)).toBeVisible();
		}
	}
);

test(
	'Categories, related assets and tags of an expired versioned document should be visible',
	{
		tag: '@LPD-42737',
	},
	async ({
		apiHelpers,
		documentLibraryPage,
		documentLibraryViewFileEntryPage,
		site,
	}) => {
		const documentTitle = 'Title' + getRandomString();

		const documentId =
			await test.step('Create a new document', async () => {
				const document = await apiHelpers.headlessDelivery.postDocument(
					site.id,
					createReadStream(
						path.join(__dirname, '/dependencies/image1.jpeg')
					),
					{
						description: getRandomString(),
						fileName: getRandomString(),
						title: documentTitle,
					}
				);

				expect(document).toHaveProperty('title', documentTitle);

				return document.id;
			});

		const [categoryName, keyword, structuredContentTitle] =
			await test.step('Update the document with a new version: add category, related asset and tag', async () => {
				const categories = await createCategories({
					apiHelpers,
					categoryNames: [{name: 'Category' + getRandomString()}],
					siteId: site.id,
					vocabularyName: getRandomString(),
				});

				const keyword = 'Keyword' + getRandomString();

				const contentStructureId =
					await getBasicWebContentStructureId(apiHelpers);
				const structuredContentTitle =
					'StructuredContent' + getRandomString();

				await apiHelpers.headlessDelivery.postStructuredContent({
					contentStructureId,
					datePublished: null,
					description: getRandomString(),
					relatedContents: [
						{
							contentType: 'Document',
							id: documentId,
							title: documentTitle,
						},
					],
					siteId: site.id,
					title: structuredContentTitle,
					viewableBy: 'Anyone',
				});

				const updatedDocument =
					await apiHelpers.headlessDelivery.patchDocument({
						document: {
							keywords: [keyword],
							taxonomyCategoryIds: [categories[0].id],
						},
						documentId,
					});

				expect(updatedDocument.keywords).toContain(keyword);
				expect(
					updatedDocument.relatedContents.map((r) => r.title)
				).toContain(structuredContentTitle);
				expect(
					updatedDocument.taxonomyCategoryBriefs.map(
						(t) => t.taxonomyCategoryName
					)
				).toContain(categories[0].name);

				return [categories[0].name, keyword, structuredContentTitle];
			});

		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'test@liferay.com'
			);

		await test.step('Expire the document', async () => {
			const fileVersion =
				await apiHelpers.jsonWebServicesDocumentLibrary.getLastestFileVersion(
					documentId
				);

			const fileEntry =
				await apiHelpers.jsonWebServicesDocumentLibrary.updateStatus(
					user.id,
					fileVersion.fileVersionId,
					DLFILE_STATUS.EXPIRED
				);

			expect(fileEntry.fileEntryId).toBe(String(documentId));
		});

		await test.step('Check that category, related asset and tag are visible in document view page', async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.goToViewFileEntry(documentTitle);
			await documentLibraryViewFileEntryPage.openInfoPanel(
				documentTitle,
				'Details'
			);
			await documentLibraryViewFileEntryPage.assertInfoPanelCategories([
				categoryName,
			]);
			await documentLibraryViewFileEntryPage.assertInfoPanelRelatedAssets(
				[structuredContentTitle]
			);
			await documentLibraryViewFileEntryPage.assertInfoPanelTags([
				keyword,
			]);
		});
	}
);

test(
	'Back button works when viewing file entry history',
	{
		tag: '@LPD-44784',
	},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const title = getRandomString();
		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
			{title}
		);

		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.goToViewHistoryFileEntry(title);

		await page.locator('span[title="Back"]').click();

		await expect(
			page.getByRole('button', {name: 'Versions'})
		).not.toBeVisible();
	}
);

test(
	'A non-localizable field in a Document Type cannot be entered if accessed from a site that does not use the global site language',
	{
		tag: '@LPP-53324',
	},
	async ({
		apiHelpers,
		documentLibraryEditDocumentTypesPage,
		documentLibraryEditFilePage,
		documentLibraryPage,
		globalMenuPage,
		page,
		siteSettingsLocalizationPage,
	}) => {
		const dTypeTitle = getRandomString();

		await documentLibraryEditDocumentTypesPage.createNewDLTypeWithTextFieldRequiredNonLocalizable(
			dTypeTitle,
			'/global'
		);

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await siteSettingsLocalizationPage.setCustomDefaultLanguage(
			'Spanish (Spain)',
			site.friendlyUrlPath
		);

		await siteSettingsLocalizationPage.disableAllLanguagesExceptSp(
			site.friendlyUrlPath
		);

		await documentLibraryEditFilePage.goToNewFileDifferentType(
			dTypeTitle,
			site.friendlyUrlPath
		);

		await page.getByLabel('Title Required').fill(getRandomString());
		await page.getByLabel('Text').fill(getRandomString());

		await documentLibraryEditFilePage.publishButton.click();

		await waitForAlert(
			page,
			'Success:Your request completed successfully.'
		);

		await globalMenuPage.goToSite('Global');
		await apiHelpers.headlessAdminSite.deleteSite(
			site.externalReferenceCode
		);
		await documentLibraryPage.deleteDocumentType(dTypeTitle);

		await waitForAlert(
			page,
			'Success:Your request completed successfully.'
		);
	}
);

test(
	'File Entry Versions is ordered correctly',
	{tag: '@LPD-56610'},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const fileEntryTitle =
			await test.step('Create a new File Entry with multiple versions', async () => {
				const fileEntry =
					await apiHelpers.headlessDelivery.postDocument(
						site.id,
						createReadStream(
							path.join(__dirname, '/dependencies/image1.jpeg')
						)
					);

				for (let i = 0; i < 20; i++) {
					await apiHelpers.headlessDelivery.patchDocument({
						document: {
							description: '' + i,
						},
						documentId: fileEntry.id,
					});
				}

				return fileEntry.title;
			});

		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.goToViewFileEntry(fileEntryTitle);

		await page.click('button[data-qa-id="infoButton"]');

		await page.click('li[data-tab-name="versions"]');

		await expect(page.locator('div.list-group-title').nth(2)).toContainText(
			'1.18'
		);
	}
);

test(
	'Access from Desktop modal can be opened multiple times',
	{tag: '@LPD-47606'},
	async ({documentLibraryPage, page, site}) => {
		await documentLibraryPage.goto(site.friendlyUrlPath);

		await page.click('button[title="Options"]');

		await page.getByRole('menuitem', {name: 'Access from Desktop'}).click();

		await page.getByRole('dialog').waitFor({state: 'visible'});

		await page
			.getByRole('dialog')
			.getByRole('button', {name: 'Close'})
			.click();

		await page.click('button[title="Options"]');

		await page.getByRole('menuitem', {name: 'Access from Desktop'}).click();

		await expect(page.getByRole('dialog')).toBeVisible();
	}
);

test(
	'User search is working properly in share modal',
	{tag: '@LPD-40725'},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const title = getRandomString();
		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
			{title}
		);

		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.goToShareFileEntry(title);

		const iframeLocator = page.frameLocator('iframe[title^="Share"]');
		await iframeLocator.getByRole('combobox').click();

		await expect(iframeLocator.getByText('No results found')).toHaveCount(
			0
		);
	}
);

test(
	'Cannot revert to version with missing required fields',
	{
		tag: '@LPD-57911',
	},
	async ({
		documentLibraryEditDocumentTypesPage,
		documentLibraryEditFilePage,
		documentLibraryPage,
		page,
		site,
	}) => {
		const dlTypeTitle = getRandomString();
		const fileEntryTitle = getRandomString();

		await documentLibraryEditDocumentTypesPage.createNewDLTypeWithTextField(
			dlTypeTitle,
			false,
			site.friendlyUrlPath
		);

		await documentLibraryEditFilePage.goToNewFileDifferentType(
			dlTypeTitle,
			site.friendlyUrlPath
		);
		await page.getByLabel('Title Required').fill(fileEntryTitle);
		await documentLibraryEditFilePage.publishButton.click();
		await documentLibraryPage.waitForSuccessAlert();

		await documentLibraryPage.page
			.getByRole('link', {exact: true, name: fileEntryTitle})
			.click();
		await documentLibraryPage.clickFileEntryAction('Edit');
		await documentLibraryEditFilePage.descriptionInput.fill(fileEntryTitle);
		await documentLibraryEditFilePage.publishButton.click();
		await documentLibraryPage.waitForSuccessAlert();

		await documentLibraryEditDocumentTypesPage.updateDLTypeTextField(
			dlTypeTitle,
			true,
			site.friendlyUrlPath
		);

		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.page
			.getByRole('link', {exact: true, name: fileEntryTitle})
			.click();
		await documentLibraryPage.clickFileEntryAction('View History');
		await page
			.locator('tr', {hasText: '1.0'})
			.locator('[aria-label="Actions"], .dropdown-toggle')
			.click();

		await expect(
			page.getByRole('menuitem', {name: 'Revert'})
		).not.toBeVisible();
	}
);

test(
	'Select All should not be visible in case of search or filter',
	{
		tag: ['@LPD-71053'],
	},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const folderTitle = 'Folder' + getRandomString();

		for (let i = 0; i < 21; i++) {
			await apiHelpers.headlessDelivery.postDocumentFolder(site.id, {
				name: folderTitle + i,
			});
		}

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await documentLibraryPage.searchInDL('Folder');

		await page.getByLabel('Select All Items on the Page').click();

		await expect(
			page.getByRole('button', {name: 'Select All'})
		).not.toBeVisible();
	}
);

test(
	'Show the last modified date of the most recent version of a document, even if that version is still pending',
	{
		tag: ['@LPD-83517'],
	},
	async ({
		documentLibraryEditFilePage,
		documentLibraryEditFolderPage,
		documentLibraryPage,
		page,
		site,
		workflowTasksPage,
	}) => {
		const docTitle = getRandomString();
		const expectSuccessToast = async () => {
			await expect(
				page.getByText('Your request completed successfully.')
			).toBeVisible();
		};
		const folderTitle = getRandomString();
		const newDocTitle = docTitle + ' Edited';

		await test.step('Create a new Folder', async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.goToCreateNewFolder();
			await documentLibraryEditFolderPage.publishNewFolder(folderTitle);
		});

		await test.step('Add workflow to the Folder', async () => {
			await documentLibraryPage.goToEditFolder(folderTitle);
			await page.waitForURL(/edit_folder/);
			await documentLibraryEditFolderPage.setWorkflow('Single Approver');
			await documentLibraryPage.goto(site.friendlyUrlPath);
		});

		await test.step('Create a new File in the Folder', async () => {
			await page.getByRole('link', {name: folderTitle}).click();
			await page.waitForURL(/view_folder/);
			const folderUrl = page.url();
			await documentLibraryPage.goToCreateNewFile();
			await documentLibraryEditFilePage.submitWorkflowForBasicFileEntry(
				docTitle
			);
			await page.waitForURL(folderUrl);
		});

		await test.step('Approve the workflow task', async () => {
			await workflowTasksPage.goToAssignedToMyRoles();
			await workflowTasksPage.assignToMe(docTitle);
			await workflowTasksPage.approve(docTitle);
		});

		await test.step('Edit the new File', async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await page.getByRole('link', {name: folderTitle}).click();
			await page.waitForURL(/view_folder/);
			await documentLibraryPage.goToEditFileEntry(docTitle);
			await page.waitForURL(/edit_file_entry/);
			await expect(
				documentLibraryEditFilePage.titleSelector
			).toBeVisible();
			await documentLibraryEditFilePage.submitWorkflowForBasicFileEntry(
				newDocTitle
			);
			await expectSuccessToast();
		});

		await test.step('Verify modified date of the file', async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await page.getByRole('link', {name: folderTitle}).click();
			await page.waitForURL(/view_folder/);

			const editedCard = page.locator('.card', {hasText: newDocTitle});
			const recentlyModified =
				/Modified ((\d{1,2} Seconds? ago)|Just now)/;
			await expect(editedCard.locator('.card-subtitle')).toHaveText(
				recentlyModified
			);
		});
	}
);

test(
	'Check accessibility of the search container in DM',
	{
		tag: ['@LPD-83985'],
	},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const title = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
			{title}
		);

		await documentLibraryPage.goto(site.friendlyUrlPath);

		const views: string[] = ['table', 'list', 'cards'];

		for (const view of views) {
			await test.step(`Change visualization mode to ${view}`, async () => {
				await documentLibraryPage.changeView(view);

				await checkAccessibility({
					bestPractices: true,
					page,
					selectors: [
						'#_com_liferay_document_library_web_portlet_DLAdminPortlet_documentLibraryContainer',
					],
				});
			});
		}
	}
);

test(
	'Can filter documents by categories from the site and the global scope',
	{
		tag: '@LPS-154979',
	},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const companyId = await page.evaluate(() =>
			Liferay.ThemeDisplay.getCompanyId()
		);

		const globalGroup = await apiHelpers.jsonWebServicesGroup.getGroupByKey(
			companyId,
			companyId
		);

		const globalVocabularyName = 'Vocabulary' + getRandomString();
		const siteVocabularyName = 'Vocabulary' + getRandomString();

		const [siteCategory1, siteCategory2] = await createCategories({
			apiHelpers,
			categoryNames: [
				{name: 'Category' + getRandomString()},
				{name: 'Category' + getRandomString()},
			],
			siteId: site.id,
			vocabularyName: siteVocabularyName,
		});

		const [globalCategory] = await createCategories({
			apiHelpers,
			categoryNames: [{name: 'Category' + getRandomString()}],
			siteId: globalGroup.groupId,
			vocabularyName: globalVocabularyName,
		});

		const [documentTitle1, documentTitle2, documentTitle3] = [
			getRandomString(),
			getRandomString(),
			getRandomString(),
		];

		const taxonomyCategoryIdsByTitle = {
			[documentTitle1]: [siteCategory1.id, siteCategory2.id],
			[documentTitle2]: [siteCategory1.id, globalCategory.id],
			[documentTitle3]: [siteCategory2.id, globalCategory.id],
		};

		for (const [title, taxonomyCategoryIds] of Object.entries(
			taxonomyCategoryIdsByTitle
		)) {
			await apiHelpers.headlessDelivery.postDocument(
				site.id,
				createReadStream(
					path.join(__dirname, '/dependencies/image1.jpeg')
				),
				{
					fileName: getRandomString(),
					taxonomyCategoryIds,
					title,
				}
			);
		}

		const globalVocabularyLabel = `${globalVocabularyName} (Global)`;
		const siteVocabularyLabel = `${siteVocabularyName} (${site.name})`;

		const categoriesModal = page.frameLocator(
			'iframe[title="Filter by Categories"]'
		);

		const treeNode = (name: string) =>
			categoriesModal.getByText(name, {exact: true});

		const openCategoriesFilter = async () => {
			await page.getByLabel('Filter', {exact: true}).click();

			await page.getByRole('menuitem', {name: 'Categories'}).click();

			await expect(treeNode(siteVocabularyLabel)).toBeVisible();
		};

		const expandVocabulary = async (
			vocabularyLabel: string,
			categoryName: string
		) =>
			clickAndExpectToBeVisible({
				target: treeNode(categoryName),
				trigger: treeNode(vocabularyLabel),
			});

		const applyCategoriesFilter = () =>
			page
				.getByRole('dialog')
				.getByRole('button', {exact: true, name: 'Select'})
				.click();

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await test.step('Filter by a single category', async () => {
			await openCategoriesFilter();

			await expect(treeNode(globalVocabularyLabel)).toBeVisible();

			await expandVocabulary(siteVocabularyLabel, siteCategory1.name);

			await expect(treeNode(siteCategory2.name)).toBeVisible();

			await expandVocabulary(globalVocabularyLabel, globalCategory.name);

			await treeNode(siteCategory1.name).click();

			await expect(
				categoriesModal.getByText('1 Item Selected')
			).toBeVisible();

			await applyCategoriesFilter();

			await expect(
				page.getByText('2 Results Found With Filters')
			).toBeVisible();

			await expect(
				page.getByText(`Category: ${siteCategory1.name}`)
			).toBeVisible();

			await expect(
				page.getByRole('link', {name: documentTitle1})
			).toBeVisible();

			await expect(
				page.getByRole('link', {name: documentTitle2})
			).toBeVisible();

			await expect(
				page.getByRole('link', {name: documentTitle3})
			).toBeHidden();
		});

		await test.step('Filter by categories from both scopes', async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);

			await expect(
				page.getByText('2 Results Found With Filters')
			).toBeHidden();

			await openCategoriesFilter();

			await expandVocabulary(siteVocabularyLabel, siteCategory1.name);

			await treeNode(siteCategory1.name).click();

			await expandVocabulary(globalVocabularyLabel, globalCategory.name);

			await treeNode(globalCategory.name).click();

			await expect(
				categoriesModal.getByText('2 Items Selected')
			).toBeVisible();

			await applyCategoriesFilter();

			await expect(
				page.getByText('1 Result Found With Filters')
			).toBeVisible();

			await expect(
				page.getByText(`Category: ${siteCategory1.name}`)
			).toBeVisible();

			await expect(
				page.getByText(`Category: ${globalCategory.name}`)
			).toBeVisible();

			await expect(
				page.getByRole('link', {name: documentTitle2})
			).toBeVisible();

			await expect(
				page.getByRole('link', {name: documentTitle1})
			).toBeHidden();

			await expect(
				page.getByRole('link', {name: documentTitle3})
			).toBeHidden();
		});
	}
);
