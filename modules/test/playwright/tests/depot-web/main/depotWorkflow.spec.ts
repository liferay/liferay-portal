/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {WorkflowTaskDetailsPage} from '../../../pages/portal-workflow-task-web/WorkflowTaskDetailsPage';
import {WorkflowTasksPage} from '../../../pages/portal-workflow-task-web/WorkflowTasksPage';
import {addSpaceUser} from '../../../utils/addSpaceUser';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch} from '../../../utils/performLogin';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {JournalEditArticlePage} from '../../journal-web/main/pages/JournalEditArticlePage';
import {JournalPage} from '../../journal-web/main/pages/JournalPage';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';
import {depotsPagesTest} from './fixtures/depotsPagesTest';
import addEditableDepot from './utils/addEditableDepot';

const test = mergeTests(
	blogsPagesTest,
	dataApiHelpersTest,
	depotsPagesTest,
	documentLibraryPagesTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	workflowPagesTest
);

const DL_FOLDER_CLASS_NAME =
	'com.liferay.document.library.kernel.model.DLFolder';

const FIELD_NAMES = {
	image: 'Image',
	journal_article: 'WebContent',
};

const JOURNAL_ARTICLE_CLASS_NAME = 'com.liferay.journal.model.JournalArticle';

async function addDepot(apiHelpers: DataApiHelpers, site?: Site) {
	const depot = await addEditableDepot(apiHelpers);

	if (site) {
		await apiHelpers.headlessAssetLibrary.connectSite(
			depot.externalReferenceCode,
			site.externalReferenceCode
		);
	}

	return {...depot, url: `/asset-library-${depot.depotEntryId}`};
}

async function addWebContentAssignedToContentReviewer(
	apiHelpers: DataApiHelpers,
	journalPage: JournalPage,
	page: Page,
	workflowTasksPage: WorkflowTasksPage
) {
	const depot = await addDepot(apiHelpers);

	const user = await addSpaceUser(
		apiHelpers,
		depot.externalReferenceCode,
		'Asset Library Content Reviewer'
	);

	await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
	await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

	await enableWorkflow(apiHelpers, JOURNAL_ARTICLE_CLASS_NAME, depot.groupId);

	const title = getRandomString();

	await apiHelpers.jsonWebServicesJournal.addWebContent({
		ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
		groupId: depot.groupId,
		titleMap: {en_US: title},
	});

	await journalPage.gotoFolder({folderId: 0, siteUrl: depot.url});

	await expect(journalPage.getArticleStatus(title)).toHaveText('Pending');

	await performUserSwitch(page, user.alternateName);

	await workflowTasksPage.goToAssignedToMyRoles();

	await workflowTasksPage.waitForTask(title);

	await performUserSwitch(page, 'test');

	await workflowTasksPage.goToAssignedToMyRoles();

	await workflowTasksPage.waitForTask(title);

	await workflowTasksPage.assignToUser(title, user);

	await waitForAlert(page);

	await performUserSwitch(page, user.alternateName);

	await workflowTasksPage.goto();

	await workflowTasksPage.waitForTask(title);

	return {depot, title};
}

async function addWebContentStructure(
	apiHelpers: DataApiHelpers,
	fieldType: 'image' | 'journal_article',
	site: Site
) {
	const structureName = getRandomString();

	await apiHelpers.dataEngine.createStructure(
		site.id,
		getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [{fieldType, name: FIELD_NAMES[fieldType]}],
			name: structureName,
		})
	);

	return structureName;
}

async function editFromUpdateTask(
	editorLocator: Locator,
	title: string,
	workflowTaskDetailsPage: WorkflowTaskDetailsPage
) {
	await workflowTaskDetailsPage.goTo(title, 'Update');

	await workflowTaskDetailsPage.editAssetButton.click();

	const page = workflowTaskDetailsPage.page;

	await page.waitForURL((url) => !url.pathname.includes('my_workflow_tasks'));

	await expect(async () => {
		await page.reload({timeout: 10000});

		await expect(editorLocator).toBeAttached({timeout: 10000});
	}).toPass({timeout: 60000});
}

async function enableWorkflow(
	apiHelpers: DataApiHelpers,
	className: string,
	groupId: number
) {
	const workflowDefinition =
		await apiHelpers.headlessAdminWorkflow.getWorkflowDefinitionByName(
			'Single Approver'
		);

	await apiHelpers.headlessAdminWorkflow.postWorkflowDefinitionLink(
		className,
		groupId,
		workflowDefinition.id,
		workflowDefinition.name,
		Number(workflowDefinition.version)
	);
}

async function openFieldItemSelector(
	fieldType: 'image' | 'journal_article',
	journalEditArticlePage: JournalEditArticlePage
) {
	await journalEditArticlePage.page
		.getByTestId(FIELD_NAMES[fieldType])
		.getByRole('button', {name: 'Select'})
		.click();
}

async function publishSiteWebContent(
	journalEditArticlePage: JournalEditArticlePage,
	site: Site
) {
	const title = getRandomString();

	await journalEditArticlePage.fillTitle(title);

	await journalEditArticlePage.publishArticle(true);

	await expect(journalEditArticlePage.titleInput).toBeHidden();

	await journalEditArticlePage.journalPage.gotoFolder({
		folderId: 0,
		siteUrl: site.friendlyUrlPath,
	});

	await expect(
		journalEditArticlePage.journalPage.getArticleStatus(title)
	).toHaveText('Approved');
}

test(
	'A pending depot image is selectable in a connected site only after approval',
	{tag: '@LPD-110541'},
	async ({
		apiHelpers,
		blogsEditBlogEntryPage,
		depotItemSelectorPage,
		documentLibraryPage,
		page,
		site,
		workflowTasksPage,
	}) => {
		const depot = await addDepot(apiHelpers, site);

		await enableWorkflow(apiHelpers, DL_FOLDER_CLASS_NAME, depot.groupId);

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			depot.groupId
		);

		const title = getRandomString();

		await apiHelpers.headlessDelivery.postDocumentFolderDocument(
			folder.id,
			createReadStream(path.join(__dirname, 'dependencies/Document.jpg')),
			{fileName: `${title}.jpg`, title}
		);

		await documentLibraryPage.gotoFolder({
			folderId: folder.id,
			siteUrl: depot.url,
		});

		await expect(documentLibraryPage.getFileEntryStatus(title)).toHaveText(
			'Pending'
		);

		const openImageSelector = async () => {
			await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

			await page
				.getByRole('button', {name: 'Select File'})
				.first()
				.click();

			const itemSelector =
				depotItemSelectorPage.getItemSelector('Select File');

			await itemSelector
				.getByRole('link', {name: 'Documents and Media'})
				.click();

			await depotItemSelectorPage.openDepotFolder({
				depotName: depot.name,
				folderName: folder.name,
				itemSelector,
			});

			return itemSelector;
		};

		// The pending image is not offered in the connected site

		let itemSelector = await openImageSelector();

		await expect(
			itemSelector.getByText(
				'There are no documents or media files in this folder.'
			)
		).toBeVisible();

		await expect(itemSelector.getByText(title)).toHaveCount(0);

		// Approve the image

		await workflowTasksPage.goToAssignedToMyRoles();

		await workflowTasksPage.waitForTask(title);

		await workflowTasksPage.assignToMe(title);

		await workflowTasksPage.approve(title);

		await documentLibraryPage.gotoFolder({
			folderId: folder.id,
			siteUrl: depot.url,
		});

		await expect(documentLibraryPage.getFileEntryStatus(title)).toHaveText(
			'Approved'
		);

		// The approved image can be selected in the connected site

		itemSelector = await openImageSelector();

		await itemSelector.getByText(title, {exact: true}).click();

		await expect(page.locator('.image-wrapper img')).toHaveAttribute(
			'src',
			new RegExp(title)
		);
	}
);

for (const inFolder of [false, true]) {
	test(
		`A pending depot web content ${inFolder ? 'in a folder ' : ''}is selectable in a connected site only after approval`,
		{tag: '@LPD-110541'},
		async ({
			apiHelpers,
			depotItemSelectorPage,
			journalEditArticlePage,
			journalPage,
			site,
			workflowTasksPage,
		}) => {
			const depot = await addDepot(apiHelpers, site);

			await enableWorkflow(
				apiHelpers,
				JOURNAL_ARTICLE_CLASS_NAME,
				depot.groupId
			);

			const folder = inFolder
				? await apiHelpers.jsonWebServicesJournal.addFolder({
						groupId: depot.groupId,
					})
				: null;

			const folderId = folder ? folder.folderId : 0;

			const title = getRandomString();

			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
				folderId,
				groupId: depot.groupId,
				titleMap: {en_US: title},
			});

			await journalPage.gotoFolder({folderId, siteUrl: depot.url});

			await expect(journalPage.getArticleStatus(title)).toHaveText(
				'Pending'
			);

			const structureName = await addWebContentStructure(
				apiHelpers,
				'journal_article',
				site
			);

			const openWebContentSelector = async () => {
				await journalEditArticlePage.goto({
					siteUrl: site.friendlyUrlPath,
					structureName,
				});

				await openFieldItemSelector(
					'journal_article',
					journalEditArticlePage
				);

				const itemSelector =
					depotItemSelectorPage.getItemSelector('Web Content');

				await depotItemSelectorPage.openDepotFolder({
					depotName: depot.name,
					folderName: folder?.name,
					itemSelector,
				});

				return itemSelector;
			};

			// The pending web content is not offered in the connected site

			let itemSelector = await openWebContentSelector();

			await expect(
				itemSelector.getByText('No web content was found.')
			).toBeVisible();

			await expect(itemSelector.getByText(title)).toHaveCount(0);

			// Approve the web content

			await workflowTasksPage.goToAssignedToMyRoles();

			await workflowTasksPage.waitForTask(title);

			await workflowTasksPage.assignToMe(title);

			await workflowTasksPage.approve(title);

			await journalPage.gotoFolder({folderId, siteUrl: depot.url});

			await expect(journalPage.getArticleStatus(title)).toHaveText(
				'Approved'
			);

			// The approved web content can be referenced from the connected site

			itemSelector = await openWebContentSelector();

			await itemSelector.getByText(title, {exact: true}).click();

			await expect(
				journalEditArticlePage.page
					.getByTestId(FIELD_NAMES.journal_article)
					.getByRole('textbox')
			).toHaveValue(title);

			await publishSiteWebContent(journalEditArticlePage, site);
		}
	);
}

test(
	'A depot content reviewer can approve a depot web content',
	{tag: ['@LPD-110541', '@LPS-113392']},
	async ({apiHelpers, journalPage, page, workflowTasksPage}) => {
		const {depot, title} = await addWebContentAssignedToContentReviewer(
			apiHelpers,
			journalPage,
			page,
			workflowTasksPage
		);

		// The content reviewer approves the web content

		await workflowTasksPage.approve(title);

		await performUserSwitch(page, 'test');

		await journalPage.gotoFolder({folderId: 0, siteUrl: depot.url});

		await expect(journalPage.getArticleStatus(title)).toHaveText(
			'Approved'
		);
	}
);

test(
	'A depot content reviewer can reject a depot web content',
	{tag: '@LPD-110541'},
	async ({apiHelpers, journalPage, page, workflowTasksPage}) => {
		const {depot, title} = await addWebContentAssignedToContentReviewer(
			apiHelpers,
			journalPage,
			page,
			workflowTasksPage
		);

		// The content reviewer rejects the web content

		await workflowTasksPage.reject(title);

		await performUserSwitch(page, 'test');

		// The web content goes back to its author for an update

		await workflowTasksPage.goto();

		await workflowTasksPage.waitForTask(title, 'Update');

		await journalPage.gotoFolder({folderId: 0, siteUrl: depot.url});

		await expect(journalPage.getArticleStatus(title)).toHaveText('Pending');
	}
);

test(
	'A rejected depot image can be replaced, resubmitted and used in a connected site',
	{tag: '@LPD-110541'},
	async ({
		apiHelpers,
		depotItemSelectorPage,
		documentLibraryEditFilePage,
		documentLibraryPage,
		journalEditArticlePage,
		page,
		site,
		workflowTaskDetailsPage,
		workflowTasksPage,
	}) => {
		const depot = await addDepot(apiHelpers, site);

		await enableWorkflow(apiHelpers, DL_FOLDER_CLASS_NAME, depot.groupId);

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			depot.groupId
		);

		const title = getRandomString();

		await apiHelpers.headlessDelivery.postDocumentFolderDocument(
			folder.id,
			createReadStream(path.join(__dirname, 'dependencies/Document.jpg')),
			{fileName: `${title}.jpg`, title}
		);

		// Reject the image

		await workflowTasksPage.goToAssignedToMyRoles();

		await workflowTasksPage.waitForTask(title);

		await workflowTasksPage.assignToMe(title);

		await workflowTasksPage.reject(title);

		// Replace the image from the update task

		await editFromUpdateTask(
			page.locator('input[type="file"]'),
			title,
			workflowTaskDetailsPage
		);

		const newTitle = getRandomString();

		await page
			.locator('input[type="file"]')
			.setInputFiles(
				path.join(__dirname, 'dependencies/Document_Small.jpeg')
			);

		await documentLibraryEditFilePage.titleSelector.fill(newTitle);

		await documentLibraryEditFilePage.submitForWorkflowButton.click();

		await waitForAlert(page);

		// Resubmit and approve the replaced image

		await workflowTasksPage.resubmit(newTitle);

		await workflowTasksPage.goToAssignedToMyRoles();

		await workflowTasksPage.waitForTask(newTitle);

		await workflowTasksPage.assignToMe(newTitle);

		await workflowTasksPage.approve(newTitle);

		await documentLibraryPage.gotoFolder({
			folderId: folder.id,
			siteUrl: depot.url,
		});

		await expect(
			documentLibraryPage.getFileEntryStatus(newTitle)
		).toHaveText('Approved');

		// The replaced image can be used in the connected site

		const structureName = await addWebContentStructure(
			apiHelpers,
			'image',
			site
		);

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		await openFieldItemSelector('image', journalEditArticlePage);

		const itemSelector =
			depotItemSelectorPage.getItemSelector('Select Image');

		await depotItemSelectorPage.openDepotFolder({
			depotName: depot.name,
			folderName: folder.name,
			itemSelector,
		});

		await itemSelector.getByText(newTitle, {exact: true}).click();

		await expect(
			page.getByTestId(FIELD_NAMES.image).getByRole('textbox').first()
		).toHaveValue(newTitle);

		await publishSiteWebContent(journalEditArticlePage, site);
	}
);

test(
	'A rejected depot web content can be edited, resubmitted and used in a connected site',
	{tag: '@LPD-110541'},
	async ({
		apiHelpers,
		depotItemSelectorPage,
		journalEditArticlePage,
		journalPage,
		page,
		site,
		workflowTaskDetailsPage,
		workflowTasksPage,
	}) => {
		const depot = await addDepot(apiHelpers, site);

		await enableWorkflow(
			apiHelpers,
			JOURNAL_ARTICLE_CLASS_NAME,
			depot.groupId
		);

		const title = getRandomString();

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
			groupId: depot.groupId,
			titleMap: {en_US: title},
		});

		// Reject the web content

		await workflowTasksPage.goToAssignedToMyRoles();

		await workflowTasksPage.waitForTask(title);

		await workflowTasksPage.assignToMe(title);

		await workflowTasksPage.reject(title);

		// Edit the web content from the update task

		await editFromUpdateTask(
			journalEditArticlePage.propertiesTab,
			title,
			workflowTaskDetailsPage
		);

		const newTitle = getRandomString();

		await journalEditArticlePage.fillTitle(newTitle);

		await page.getByRole('button', {exact: true, name: 'Save'}).click();

		await waitForAlert(page, `Success:${newTitle} was successfully saved`);

		// Resubmit and approve the edited web content

		await workflowTasksPage.resubmit(newTitle);

		await workflowTasksPage.goToAssignedToMyRoles();

		await workflowTasksPage.waitForTask(newTitle);

		await workflowTasksPage.assignToMe(newTitle);

		await workflowTasksPage.approve(newTitle);

		await journalPage.gotoFolder({folderId: 0, siteUrl: depot.url});

		await expect(journalPage.getArticleStatus(newTitle)).toHaveText(
			'Approved'
		);

		// The edited web content can be referenced from the connected site

		const structureName = await addWebContentStructure(
			apiHelpers,
			'journal_article',
			site
		);

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		await openFieldItemSelector('journal_article', journalEditArticlePage);

		const itemSelector =
			depotItemSelectorPage.getItemSelector('Web Content');

		await depotItemSelectorPage.openDepotFolder({
			depotName: depot.name,
			itemSelector,
		});

		await itemSelector.getByText(newTitle, {exact: true}).click();

		await expect(
			page.getByTestId(FIELD_NAMES.journal_article).getByRole('textbox')
		).toHaveValue(newTitle);

		await publishSiteWebContent(journalEditArticlePage, site);
	}
);

test(
	'A depot document folder workflow can be restored to the parent folder',
	{tag: '@LPD-110541'},
	async ({
		apiHelpers,
		documentLibraryEditFolderPage,
		documentLibraryPage,
	}) => {
		const depot = await addDepot(apiHelpers);

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			depot.groupId
		);

		const addDocument = async () => {
			const title = getRandomString();

			await apiHelpers.headlessDelivery.postDocumentFolderDocument(
				folder.id,
				createReadStream(
					path.join(__dirname, 'dependencies/Document.jpg')
				),
				{fileName: `${title}.jpg`, title}
			);

			return title;
		};

		const gotoEditFolder = () =>
			documentLibraryEditFolderPage.goto({
				folderId: folder.id,
				repositoryId: depot.groupId,
				siteUrl: depot.url,
			});

		// Use a workflow for the basic document type

		await gotoEditFolder();

		await documentLibraryEditFolderPage.setDocumentTypeWorkflow(
			'Basic Document',
			'Single Approver'
		);

		const documentTypeWorkflowTitle = await addDocument();

		// Use a default workflow for every document type

		await gotoEditFolder();

		await documentLibraryEditFolderPage.setWorkflow('Single Approver');

		const defaultWorkflowTitle = await addDocument();

		// Restore the workflow of the parent folder

		await gotoEditFolder();

		await documentLibraryEditFolderPage.useParentFolderWorkflow();

		const parentFolderWorkflowTitle = await addDocument();

		// Only the document added after the restore skips the review

		await documentLibraryPage.gotoFolder({
			folderId: folder.id,
			siteUrl: depot.url,
		});

		await expect(
			documentLibraryPage.getFileEntryStatus(documentTypeWorkflowTitle)
		).toHaveText('Pending');
		await expect(
			documentLibraryPage.getFileEntryStatus(defaultWorkflowTitle)
		).toHaveText('Pending');
		await expect(
			documentLibraryPage.getFileEntryStatus(parentFolderWorkflowTitle)
		).toHaveText('Approved');

		await gotoEditFolder();

		await expect(
			documentLibraryEditFolderPage.parentFolderWorkflowRadio
		).toBeChecked();
	}
);

test(
	'A depot web content folder workflow can be restored to the parent folder',
	{tag: '@LPD-110541'},
	async ({apiHelpers, journalEditFolderPage, journalPage}) => {
		const depot = await addDepot(apiHelpers);

		const folder = await apiHelpers.jsonWebServicesJournal.addFolder({
			groupId: depot.groupId,
		});

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const addWebContent = async () => {
			const title = getRandomString();

			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				folderId: folder.folderId,
				groupId: depot.groupId,
				titleMap: {en_US: title},
			});

			return title;
		};

		const gotoEditFolder = () =>
			journalEditFolderPage.goto({
				folderId: folder.folderId,
				groupId: depot.groupId,
				siteUrl: depot.url,
			});

		// Use a workflow for the basic web content structure

		await gotoEditFolder();

		await journalEditFolderPage.setStructureWorkflow(
			'Basic Web Content',
			'Single Approver'
		);

		const structureWorkflowTitle = await addWebContent();

		// Use a default workflow for every structure

		await gotoEditFolder();

		await journalEditFolderPage.setWorkflow('Single Approver');

		const defaultWorkflowTitle = await addWebContent();

		// Restore the workflow of the parent folder

		await gotoEditFolder();

		await journalEditFolderPage.useParentFolderWorkflow();

		const parentFolderWorkflowTitle = await addWebContent();

		// Only the web content added after the restore skips the review

		await journalPage.gotoFolder({
			folderId: folder.folderId,
			siteUrl: depot.url,
		});

		await expect(
			journalPage.getArticleStatus(structureWorkflowTitle)
		).toHaveText('Pending');
		await expect(
			journalPage.getArticleStatus(defaultWorkflowTitle)
		).toHaveText('Pending');
		await expect(
			journalPage.getArticleStatus(parentFolderWorkflowTitle)
		).toHaveText('Approved');

		await gotoEditFolder();

		await expect(
			journalEditFolderPage.parentFolderWorkflowRadio
		).toBeChecked();
	}
);

test(
	'Depot workflow covers web content only while the Web Content application is enabled',
	{tag: '@LPD-110541'},
	async ({
		apiHelpers,
		depotAdminPage,
		documentLibraryEditFolderPage,
		page,
		workflowPage,
	}) => {
		const depot = await addDepot(apiHelpers);

		// The depot workflow lists web content but not blogs

		await workflowPage.goto(depot.url);

		await expect(
			page
				.getByRole('row')
				.filter({hasText: 'Web Content Article'})
				.getByRole('button', {name: 'Edit'})
		).toBeVisible();

		await expect(
			page.getByRole('row').filter({hasText: 'Blogs Entry'})
		).toHaveCount(0);

		// Documents and Media folders own their workflow

		const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
			depot.groupId
		);

		await documentLibraryEditFolderPage.goto({
			folderId: folder.id,
			repositoryId: depot.groupId,
			siteUrl: depot.url,
		});

		for (const radio of [
			documentLibraryEditFolderPage.defaultWorkflowRadio,
			documentLibraryEditFolderPage.documentTypeWorkflowRadio,
			documentLibraryEditFolderPage.parentFolderWorkflowRadio,
		]) {
			await expect(radio).toBeVisible();
		}

		// Disabling Web Content empties the depot workflow

		await depotAdminPage.setApplications(
			depot.depotEntryId,
			['Web Content'],
			false
		);

		await workflowPage.goto(depot.url);

		await expect(page.getByText('No entries were found.')).toBeVisible();
	}
);
