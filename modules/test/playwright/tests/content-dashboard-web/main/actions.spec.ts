/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import {DisplayPageTemplatesPage} from '../../../pages/layout-page-template-admin-web/DisplayPageTemplatesPage';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import createUserWithPermissions from '../../../utils/createUserWithPermissions';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';
import addApprovedStructuredContent from '../../../utils/structured-content/addApprovedStructuredContent';
import addDraftStructuredContent from '../../../utils/structured-content/addDraftStructuredContent';
import addScheduledStructuredContent from '../../../utils/structured-content/addScheduledStructuredContent';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForPageToBeLoaded} from '../../../utils/waitForPageToBeLoaded';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {contentDashboardPagesTest} from './fixtures/contentDashboardPagesTest';
import addSingleApproverWorkflow from './utils/addSingleApproverWorkflow';
import getContentDashboardRolePermissions from './utils/getContentDashboardRolePermissions';
import getDocumentFile from './utils/getDocumentFile';

export const test = mergeTests(
	contentDashboardPagesTest,
	dataApiHelpersTest,
	documentLibraryPagesTest,
	globalMenuPagesTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	pageEditorPagesTest
);

const BLOGS_ENTRY_CLASS_NAME = 'com.liferay.blogs.model.BlogsEntry';

const FILE_ENTRY_CLASS_NAME =
	'com.liferay.portal.kernel.repository.model.FileEntry';

const JOURNAL_ARTICLE_CLASS_NAME = 'com.liferay.journal.model.JournalArticle';

async function addAssets({
	apiHelpers,
	siteId,
}: {
	apiHelpers: ApiHelpers;
	siteId: string;
}) {
	const blogTitle = getRandomString();

	await apiHelpers.headlessDelivery.postBlog(siteId, {
		headline: blogTitle,
		viewableBy: 'Anyone',
	});

	const documentTitle = getRandomString();

	await apiHelpers.headlessDelivery.postDocument(
		siteId,
		getDocumentFile(`${documentTitle}.txt`),
		{fileName: `${documentTitle}.txt`, title: documentTitle}
	);

	const webContentTitle = getRandomString();

	await addApprovedStructuredContent({
		apiHelpers,
		contentStructureId: await getBasicWebContentStructureId(apiHelpers),
		siteId,
		title: webContentTitle,
		viewableBy: 'Anyone',
	});

	return {blogTitle, documentTitle, webContentTitle};
}

async function addDefaultDisplayPageTemplates({
	classNames,
	displayPageTemplatesPage,
	pageEditorPage,
	site,
}: {
	classNames: string[];
	displayPageTemplatesPage: DisplayPageTemplatesPage;
	pageEditorPage: PageEditorPage;
	site: Site;
}) {
	const classTypeKeys = {
		[BLOGS_ENTRY_CLASS_NAME]: undefined,
		[FILE_ENTRY_CLASS_NAME]: 'BASIC-DOCUMENT',
		[JOURNAL_ARTICLE_CLASS_NAME]: 'BASIC-WEB-CONTENT',
	};

	for (const className of classNames) {
		await displayPageTemplatesPage.createDefaultTemplate({
			className,
			classTypeKey: classTypeKeys[className],
			pageEditorPage,
			site,
		});
	}
}

async function expectDisplayPage(page: Page, site: Site, title: string) {
	await expect(
		page.getByLabel('Control Menu').getByRole('heading', {name: title})
	).toBeVisible();

	await expect(page.locator('a.logo h1')).toHaveText(site.name);
}

async function waitForContentDashboardRedirect(page: Page) {
	await page.waitForURL(
		(url) =>
			url.searchParams.get('p_p_id') ===
			'com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet'
	);
}

test(
	'Clicking the title of an asset opens its display page or its editor',
	{
		tag: ['@LPS-119966', '@LPS-121658', '@LPS-132509', '@LPS-177756'],
	},
	async ({
		apiHelpers,
		contentDashboardPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		await addDefaultDisplayPageTemplates({
			classNames: [
				BLOGS_ENTRY_CLASS_NAME,
				FILE_ENTRY_CLASS_NAME,
				JOURNAL_ARTICLE_CLASS_NAME,
			],
			displayPageTemplatesPage,
			pageEditorPage,
			site,
		});

		const displayPageAssets = await addAssets({
			apiHelpers,
			siteId: site.id,
		});

		const noDisplayPageSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const noDisplayPageAssets = await addAssets({
			apiHelpers,
			siteId: noDisplayPageSite.id,
		});

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const draftWebContentTitle = getRandomString();

		await addDraftStructuredContent({
			apiHelpers,
			contentStructureId: basicWebContentStructureId,
			siteId: noDisplayPageSite.id,
			title: draftWebContentTitle,
		});

		const scheduledWebContentTitle = getRandomString();

		await addScheduledStructuredContent(
			apiHelpers,
			noDisplayPageSite.id,
			basicWebContentStructureId,
			scheduledWebContentTitle
		);

		const clickTitle = async (siteId: string, title: string) => {
			await contentDashboardPage.goto(undefined, {scopeId: siteId});

			await contentDashboardPage
				.getRow(title)
				.getByRole('link', {exact: true, name: title})
				.click();
		};

		await test.step('ClickTitleBlogDPTWithPermissions, ClickTitleDocumentDPTWithPermissions', async () => {
			for (const title of [
				displayPageAssets.blogTitle,
				displayPageAssets.documentTitle,
			]) {
				await clickTitle(site.id, title);

				await expectDisplayPage(page, site, title);
			}
		});

		await test.step('ViewBackButtonTooltipInAssociatedDisplayPageTemplate', async () => {
			await clickTitle(site.id, displayPageAssets.webContentTitle);

			await page
				.getByLabel('Control Menu')
				.locator('a:has(.lexicon-icon-angle-left)')
				.hover();

			await expect(
				page
					.getByRole('tooltip')
					.filter({hasText: 'Go to Content Dashboard'})
			).toBeVisible();
		});

		await test.step('ClickTitleApprovedNoDPT, ClickTitleBlogNoDPTWithPermissions, ClickTitleDraftNoDPT, ClickTitleScheduledNoDPT', async () => {
			for (const title of [
				draftWebContentTitle,
				noDisplayPageAssets.blogTitle,
				noDisplayPageAssets.webContentTitle,
				scheduledWebContentTitle,
			]) {
				await clickTitle(noDisplayPageSite.id, title);

				await expect(
					page.getByRole('button', {exact: true, name: 'Publish'})
				).toBeVisible();
			}
		});

		await test.step('ClickTitlePendingNoDPT', async () => {
			await addSingleApproverWorkflow({
				apiHelpers,
				className: JOURNAL_ARTICLE_CLASS_NAME,
				siteId: noDisplayPageSite.id,
			});

			const pendingWebContentTitle = getRandomString();

			await addApprovedStructuredContent({
				apiHelpers,
				contentStructureId: basicWebContentStructureId,
				siteId: noDisplayPageSite.id,
				title: pendingWebContentTitle,
			});

			await clickTitle(noDisplayPageSite.id, pendingWebContentTitle);

			await expect(
				page.getByRole('button', {
					exact: true,
					name: 'Submit for Workflow',
				})
			).toBeVisible();
		});
	}
);

test(
	'Users with limited permissions reach Content Dashboard and the display page of an asset from its title',
	{
		tag: ['@LPS-113832', '@LPS-119966', '@LPS-121658', '@LPS-132509'],
	},
	async ({
		apiHelpers,
		contentDashboardPage,
		displayPageTemplatesPage,
		globalMenuPage,
		page,
		pageEditorPage,
		site,
	}) => {
		await addDefaultDisplayPageTemplates({
			classNames: [
				BLOGS_ENTRY_CLASS_NAME,
				FILE_ENTRY_CLASS_NAME,
				JOURNAL_ARTICLE_CLASS_NAME,
			],
			displayPageTemplatesPage,
			pageEditorPage,
			site,
		});

		const displayPageAssets = await addAssets({
			apiHelpers,
			siteId: site.id,
		});

		const noDisplayPageSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const noDisplayPageBlogTitle = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(noDisplayPageSite.id, {
			headline: noDisplayPageBlogTitle,
			viewableBy: 'Anyone',
		});

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const restrictedUser = await createUserWithPermissions({
			apiHelpers,
			rolePermissions: getContentDashboardRolePermissions(
				company.companyId
			),
		});

		const webContentUser = await createUserWithPermissions({
			apiHelpers,
			rolePermissions: [
				...getContentDashboardRolePermissions(company.companyId),
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: company.companyId,
					resourceName:
						'com_liferay_journal_web_portlet_JournalPortlet',
					scope: 1,
				},
				{
					actionIds: ['ADD_ARTICLE', 'VIEW'],
					primaryKey: company.companyId,
					resourceName: 'com.liferay.journal',
					scope: 1,
				},
			],
		});

		await performLogout(page);

		await performLoginViaApi({
			page,
			screenName: restrictedUser.alternateName,
		});

		await test.step('PermissionsForContentDashboard', async () => {
			await globalMenuPage.goToApplications('Content Dashboard');

			await expect(
				page
					.getByLabel('Control Menu')
					.getByRole('heading', {name: 'Content Dashboard'})
			).toBeVisible();

			await expect(contentDashboardPage.filterButton).toBeVisible();
		});

		await test.step('ClickTitleApprovedDPTWithNoPermissions, ClickTitleBlogDPTWithNoPermissions, ClickTitleDocumentDPTWithNoPermissions', async () => {
			for (const title of [
				displayPageAssets.blogTitle,
				displayPageAssets.documentTitle,
				displayPageAssets.webContentTitle,
			]) {
				await contentDashboardPage.goto(undefined, {scopeId: site.id});

				await contentDashboardPage
					.getRow(title)
					.getByRole('link', {exact: true, name: title})
					.click();

				await expectDisplayPage(page, site, title);
			}
		});

		await test.step('ClickTitleBlogNoDPTWithNoPermissions', async () => {
			await contentDashboardPage.goto(undefined, {
				scopeId: noDisplayPageSite.id,
			});

			await contentDashboardPage.openRowActions(noDisplayPageBlogTitle);

			for (const actionName of ['Edit', 'View']) {
				await expect(
					page.getByRole('menuitem', {exact: true, name: actionName})
				).toHaveCount(0);
			}
		});

		await test.step('ClickTitleApprovedDPTWithPermissions', async () => {
			await performLogout(page);

			await performLoginViaApi({
				page,
				screenName: webContentUser.alternateName,
			});

			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage
				.getRow(displayPageAssets.webContentTitle)
				.getByRole('link', {
					exact: true,
					name: displayPageAssets.webContentTitle,
				})
				.click();

			await expectDisplayPage(
				page,
				site,
				displayPageAssets.webContentTitle
			);
		});
	}
);

test(
	'View actions open the display page of an asset only when it is approved and has one',
	{
		tag: [
			'@LPS-112357',
			'@LPS-116840',
			'@LPS-116987',
			'@LPS-119966',
			'@LPS-132597',
			'@LPS-151808',
		],
	},
	async ({
		apiHelpers,
		contentDashboardPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		await addDefaultDisplayPageTemplates({
			classNames: [FILE_ENTRY_CLASS_NAME, JOURNAL_ARTICLE_CLASS_NAME],
			displayPageTemplatesPage,
			pageEditorPage,
			site,
		});

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const webContentTitle = getRandomString();
		const webContentSpanishTitle = getRandomString();

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: webContentTitle, es_ES: webContentSpanishTitle},
		});

		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			getDocumentFile(`${documentTitle}.txt`),
			{fileName: `${documentTitle}.txt`, title: documentTitle}
		);

		const noDisplayPageSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const noDisplayPageWebContentTitle = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId: basicWebContentStructureId,
			siteId: noDisplayPageSite.id,
			title: noDisplayPageWebContentTitle,
		});

		await addSingleApproverWorkflow({
			apiHelpers,
			className: JOURNAL_ARTICLE_CLASS_NAME,
			siteId: site.id,
		});

		const pendingWebContentTitle = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId: basicWebContentStructureId,
			siteId: site.id,
			title: pendingWebContentTitle,
		});

		const mappedHeading = page.locator(
			'[data-lfr-editable-id="element-text"]'
		);

		await test.step('DocumentIconView, WebContentIconView', async () => {
			for (const title of [documentTitle, webContentTitle]) {
				await contentDashboardPage.goto(undefined, {scopeId: site.id});

				await contentDashboardPage.clickQuickAction(title, 'View');

				await expect(mappedHeading).toHaveText(title);
			}
		});

		await test.step('CheckMetricsIconofPendingWebContent, CheckViewOptionNotDisplayed, DisplayPageIconVisibility', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await expect(
				contentDashboardPage
					.getRow(webContentTitle)
					.locator('.lexicon-icon-page')
			).toBeVisible();

			await expect(
				contentDashboardPage
					.getRow(pendingWebContentTitle)
					.locator('.lexicon-icon-page')
			).toHaveCount(0);

			await contentDashboardPage.getRow(pendingWebContentTitle).hover();

			await expect(
				contentDashboardPage.getQuickAction(
					pendingWebContentTitle,
					'Info'
				)
			).toBeVisible();

			await expect(
				contentDashboardPage.getQuickAction(
					pendingWebContentTitle,
					'View Metrics'
				)
			).toHaveCount(0);

			await contentDashboardPage.openRowActions(pendingWebContentTitle);

			await expect(
				page.getByRole('menuitem', {exact: true, name: 'View'})
			).toHaveCount(0);

			await contentDashboardPage.goto(undefined, {
				scopeId: noDisplayPageSite.id,
			});

			await expect(
				contentDashboardPage
					.getRow(noDisplayPageWebContentTitle)
					.locator('.lexicon-icon-page')
			).toHaveCount(0);

			await contentDashboardPage.openRowActions(
				noDisplayPageWebContentTitle
			);

			await expect(
				page.getByRole('menuitem', {exact: true, name: 'View'})
			).toHaveCount(0);
		});

		await test.step('LanguageVersion', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.openInfoPanel(webContentTitle);

			await contentDashboardPage.infoPanel
				.locator('.autofit-row', {hasText: 'es-ES'})
				.getByTitle('View', {exact: true})
				.click();

			await expect(mappedHeading).toHaveText(webContentSpanishTitle);
		});
	}
);

test(
	'Editing an asset from Content Dashboard updates it and keeps the applied filters',
	{
		tag: [
			'@LPS-112356',
			'@LPS-116840',
			'@LPS-116987',
			'@LPS-119966',
			'@LPS-132509',
			'@LPS-162826',
		],
	},
	async ({
		apiHelpers,
		contentDashboardPage,
		documentLibraryEditFilePage,
		journalEditArticlePage,
		page,
		site,
	}) => {
		const {blogTitle, documentTitle, webContentTitle} = await addAssets({
			apiHelpers,
			siteId: site.id,
		});

		const draftWebContentTitle = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId: await getBasicWebContentStructureId(apiHelpers),
			siteId: site.id,
			title: draftWebContentTitle,
		});

		await test.step('IconEdit', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.clickQuickAction(
				webContentTitle,
				'Edit'
			);

			await expect(page).toHaveURL(
				/com_liferay_journal_web_portlet_JournalPortlet.*edit_article/
			);

			await expect(
				page.locator('.contextual-sidebar-visible')
			).toBeVisible();
		});

		await test.step('EditWebContent, SaveFiltersAfterEditing', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.filterByAuthors({
				names: ['Test Test'],
				search: 'Test',
				view: 'Table',
			});

			await contentDashboardPage.clickRowAction(webContentTitle, 'Edit');

			const editedWebContentTitle = getRandomString();

			await journalEditArticlePage.fillTitle(editedWebContentTitle);

			await journalEditArticlePage.publishArticle(true);

			await expect(
				contentDashboardPage.getAppliedFilter('Author: Test Test')
			).toBeVisible();

			await expect(
				contentDashboardPage.getCell(
					editedWebContentTitle,
					'modified-date'
				)
			).toHaveText(/\d+ Seconds? Ago/);
		});

		await test.step('ApprovedAndDraftStatus', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.clickRowAction(
				draftWebContentTitle,
				'Edit'
			);

			await journalEditArticlePage.fillTitle(draftWebContentTitle);

			await page
				.getByRole('button', {exact: true, name: 'Save as Draft'})
				.click();

			await expect(page.getByText('1.1 Draft')).toBeVisible();

			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.openInfoPanel(draftWebContentTitle);

			await expect(
				contentDashboardPage.infoPanel.locator('.sidebar-header .label')
			).toHaveText(['Version 1.0', 'Approved', 'Version 1.1', 'Draft']);
		});

		await test.step('EditBlog', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.clickRowAction(blogTitle, 'Edit');

			await waitForPageToBeLoaded(page);

			const editedBlogTitle = getRandomString();

			const blogTitleInput = page.getByPlaceholder('Title *');

			await blogTitleInput.fill(editedBlogTitle);

			await expect(blogTitleInput).toHaveValue(editedBlogTitle);

			const publishButton = page.getByRole('button', {
				exact: true,
				name: 'Publish',
			});

			await clickAndExpectToBeHidden({
				target: publishButton,
				trigger: publishButton,
			});

			await waitForContentDashboardRedirect(page);

			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await expect(
				contentDashboardPage.getRow(editedBlogTitle)
			).toBeVisible();
		});

		await test.step('EditDocument', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.clickRowAction(documentTitle, 'Edit');

			const editedDocumentTitle = getRandomString();

			await documentLibraryEditFilePage.titleSelector.fill(
				editedDocumentTitle
			);

			await clickAndExpectToBeHidden({
				target: documentLibraryEditFilePage.publishButton,
				trigger: documentLibraryEditFilePage.publishButton,
			});

			await waitForContentDashboardRedirect(page);

			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await expect(
				contentDashboardPage.getRow(editedDocumentTitle)
			).toBeVisible();
		});
	}
);
