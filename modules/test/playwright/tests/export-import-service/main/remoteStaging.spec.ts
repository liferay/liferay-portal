/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {dataRemoteApiHelpersTest} from '../../../fixtures/dataRemoteApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {remotePageTest} from '../../../fixtures/remotePageTest';
import {uiElementsPageTest} from '../../../fixtures/uiElementsTest';
import {webContentDisplayPageTest} from '../../../fixtures/webContentDisplayPageTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {
	JournalContentPage,
	PageNode,
	createLayoutHierarchy,
	flattenPageHierarchy,
} from '../../../utils/createLayoutHierarchy';
import getGlobalSiteId from '../../../utils/getGlobalSiteId';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {reloadUntilVisible} from '../../../utils/reloadUntilVisible';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';
import {pagesPagesTest} from '../../layout-admin-web/main/fixtures/pagesPagesTest';
import {remoteStagingPagesTest} from './fixtures/remoteStagingPagesTest';
import {safeTeardown} from './utils/safeTeardown';

const remotePort = '9080';
const remotePage = remotePageTest(remotePort);

const test = mergeTests(
	dataApiHelpersTest,
	dataRemoteApiHelpersTest(remotePage, remotePort),
	loginTest(),
	featureFlagsTest({
		'LPD-39304': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	journalPagesTest,
	pageEditorPagesTest,
	pagesAdminPagesTest,
	pagesPagesTest,
	pageViewModePagesTest,
	productMenuPageTest,
	remoteStagingPagesTest,
	uiElementsPageTest,
	webContentDisplayPageTest
);

test(
	'Can publish web content with URL references to live via remote staging',
	{tag: '@LPS-159626'},
	async ({
		apiHelpers,
		journalEditTemplatePage,
		pageEditorPage,
		remoteApiHelpers,
		remotePage,
		remoteStagingPage,
	}) => {
		test.slow();

		const PAGE_HIERARCHY: Array<PageNode<{verify?: boolean}>> = [
			{
				children: [
					{
						children: [
							{
								pageNumber: '111',
								title: 'Page 111',
								verify: true,
							},
						],
						pageNumber: '11',
						title: 'Page 11',
					},
					{pageNumber: '12', title: 'Page 12'},
				],
				pageNumber: '1',
				title: 'Page 1',
			},
			{
				children: [
					{pageNumber: '21', title: 'Page 21', verify: true},
					{pageNumber: '22', title: 'Page 22'},
				],
				pageNumber: '2',
				title: 'Page 2',
			},
			{
				children: [
					{pageNumber: '31', title: 'Page 31'},
					{pageNumber: '32', title: 'Page 32'},
				],
				pageNumber: '3',
				title: 'Page 3',
				verify: true,
			},
		];

		const flatPages = flattenPageHierarchy<{verify?: boolean}>(
			PAGE_HIERARCHY
		);

		let remoteSite: Site;
		let site: Site;

		await test.step('Setup remote staging and sites', async () => {
			site = await apiHelpers.headlessAdminSite.postSite({
				name: `site-${getRandomString()}`,
			});

			remoteSite = await remoteApiHelpers.headlessAdminSite.postSite({
				name: site.name,
			});

			await apiHelpers.jsonWebServicesStaging.enableRemoteStaging({
				groupId: site.id,
				remoteGroupId: remoteSite.id,
				remotePort,
			});
		});

		const webContentTitle = getRandomString();

		let structure;
		let templateKey;

		await test.step('Create a data structure and template for page links', async () => {
			const structureName = getRandomString();
			const fields = flatPages.flatMap(({pageNumber}) => [
				{name: `Openpage${pageNumber}`, repeatable: false},
				{name: `URL${pageNumber}`, repeatable: false},
			]);

			const dataDefinition = getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields,
				name: structureName,
			});

			structure = await apiHelpers.dataEngine.createStructure(
				site.id,
				dataDefinition
			);

			const templateScript = flatPages
				.map(({pageNumber}) => {
					return `<p><a href="\${URL${pageNumber}.getData()}">\${Openpage${pageNumber}.getData()}</a></p>`;
				})
				.join('\n');
			const templateName = 'template1';

			await journalEditTemplatePage.goto(site.friendlyUrlPath);
			await journalEditTemplatePage.selectStructure(structureName);
			await journalEditTemplatePage.editTemplate(
				templateName,
				templateScript
			);
			await journalEditTemplatePage.saveTemplate();
			await journalEditTemplatePage.selectTemplateToEdit(templateName);

			templateKey = await journalEditTemplatePage.getDDMTemplateKey();
		});

		let structure2;
		let templateKey2;

		await test.step('Create a data structure and template for individual page content', async () => {
			const structureName2 = getRandomString();
			const dataDefinition2 = getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [
					{name: 'Content1', repeatable: false},
					{name: 'Content2', repeatable: false},
				],
				name: structureName2,
			});

			structure2 = await apiHelpers.dataEngine.createStructure(
				site.id,
				dataDefinition2
			);

			await journalEditTemplatePage.goto(site.friendlyUrlPath);
			await journalEditTemplatePage.selectStructure(structureName2);

			const templateScript2 =
				'<h1>${Content1.getData()}</h1>\n' +
				'<p>${Content2.getData()}</p>';
			const templateName2 = 'template2';
			await journalEditTemplatePage.editTemplate(
				templateName2,
				templateScript2
			);
			await journalEditTemplatePage.saveTemplate();
			await journalEditTemplatePage.selectTemplateToEdit(templateName2);

			templateKey2 = await journalEditTemplatePage.getDDMTemplateKey();
		});

		let allLinksArticleId: string;
		const perPageArticleIds = new Map<string, string>();

		await test.step('Create the "all links" web content article', async () => {
			const {articleId} =
				await apiHelpers.jsonWebServicesJournal.addWebContent({
					contentFields: flatPages.flatMap(
						({friendlyUrlPath, pageNumber, title}) => [
							{name: `Openpage${pageNumber}`, value: title},
							{
								name: `URL${pageNumber}`,
								value: `/web${site.friendlyUrlPath}/${friendlyUrlPath}`,
							},
						]
					),
					ddmStructureId: structure.id,
					ddmTemplateKey: templateKey,
					groupId: site.id,
					titleMap: {en_US: webContentTitle},
				});

			allLinksArticleId = articleId!;
		});

		await test.step('Create per-page web content articles', async () => {
			for (const {contentTitle, pageNumber} of flatPages) {
				const {articleId} =
					await apiHelpers.jsonWebServicesJournal.addWebContent({
						contentFields: [
							{name: 'Content1', value: contentTitle},
							{
								name: 'Content2',
								value: `Text Content for ${contentTitle}`,
							},
						],
						ddmStructureId: structure2.id,
						ddmTemplateKey: templateKey2,
						groupId: site.id,
						titleMap: {en_US: contentTitle},
					});

				perPageArticleIds.set(pageNumber, articleId!);
			}
		});

		const WC_DISPLAY =
			'com_liferay_journal_content_web_portlet_JournalContentPortlet';

		const attachWidgetsToNodes = (
			nodes: Array<PageNode<{verify?: boolean}>>
		): Array<PageNode<{verify?: boolean}>> =>
			nodes.map((node) => ({
				...node,
				children: node.children
					? attachWidgetsToNodes(node.children)
					: undefined,
				widgets: [
					{
						config: {
							articleId: allLinksArticleId,
							ddmTemplateKey: templateKey,
						},
						name: WC_DISPLAY,
					},
					{
						config: {
							articleId: perPageArticleIds.get(node.pageNumber)!,
							ddmTemplateKey: templateKey2,
						},
						name: WC_DISPLAY,
					},
				],
			}));

		let layouts: Array<JournalContentPage<{verify?: boolean}>> = [];

		await test.step('Create page hierarchy with pre-configured Web Content Display widgets', async () => {
			layouts = await createLayoutHierarchy<{verify?: boolean}>({
				apiHelpers,
				pageNodes: attachWidgetsToNodes(PAGE_HIERARCHY),
				siteId: site.id,
			});
		});

		await test.step('Publish each page so staging picks up the drafts', async () => {
			for (const layout of layouts) {
				await pageEditorPage.goto(layout, site.friendlyUrlPath);

				await pageEditorPage.publishPage();
			}
		});

		await test.step('Publish to live and verify content and links on the remote site', async () => {
			await remoteStagingPage.publishToLive({
				layoutFriendlyURL: layouts[0].friendlyUrlPath,
				siteFriendlyUrl: site.friendlyUrlPath,
			});

			await remotePage.goto(
				`/web${remoteSite.friendlyUrlPath}${layouts[0].friendlyUrlPath}`
			);

			for (const layout of layouts.filter(({verify}) => verify)) {
				await clickAndExpectToBeVisible({
					target: remotePage.locator('h1', {
						hasText: layout.contentTitle,
					}),
					trigger: remotePage.getByRole('link', {
						exact: true,
						name: layout.title,
					}),
				});

				await expect(remotePage).toHaveURL(
					`/web/${site.name}${layout.friendlyUrlPath}`
				);
			}
		});
	}
);

test(
	'Check Web contents can be published via their portlet using remote staging',
	{tag: '@LPS-81950'},
	async ({
		apiHelpers,
		pageEditorPage,
		remoteApiHelpers,
		remotePage,
		remoteStagingPage,
		uiElementsPage,
		webContentDisplayPage,
		widgetPagePage,
	}) => {
		test.slow();

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: 'Site Name',
		});

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {
				type: 'portlet',
			},
			title: 'Staging Test Page',
		});

		const remoteSite = await remoteApiHelpers.headlessAdminSite.postSite({
			name: 'Remote Site Name',
		});

		await apiHelpers.jsonWebServicesStaging.enableRemoteStaging({
			groupId: site.id,
			remoteGroupId: remoteSite.id,
			remotePort,
		});

		await remoteStagingPage.publishToLive({
			layoutFriendlyURL: layout.friendlyURL,
			siteFriendlyUrl: site.friendlyUrlPath,
		});

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			content: 'WC WebContent Content',
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: 'WC WebContent Title'},
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);
		await uiElementsPage.addButton.click();
		await widgetPagePage.addPortlet(
			'Web Content Display',
			'Content Management'
		);
		await webContentDisplayPage.addWebContentWithDisplay({
			pageType: 'widget',
			webContentName: 'WC WebContent Title',
		});

		await remoteStagingPage.publishToLive({
			layoutFriendlyURL: layout.friendlyURL,
			siteFriendlyUrl: site.friendlyUrlPath,
		});

		const remoteUrl = remoteApiHelpers.baseUrl.substring(
			0,
			remoteApiHelpers.baseUrl.length - 3
		);

		await remotePage.goto(`${remoteUrl}/web${remoteSite.friendlyUrlPath}`);

		await reloadUntilVisible({
			myLocator: remotePage.getByRole('heading', {
				name: 'WC WebContent Title',
			}),
			page: remotePage,
		});

		await expect(
			remotePage.getByRole('heading', {name: 'WC WebContent Title'})
		).toBeVisible();
		await expect(
			remotePage.getByText('WC WebContent Content')
		).toBeVisible();
	}
);

test(
	'Can publish vocabulary deletion from the Global Site using remote staging',
	{tag: ['@LPS-89981', '@LPS-88298']},
	async (
		{
			apiHelpers,
			configStagingPage,
			page,
			portletStagingPage,
			remoteApiHelpers,
			remotePage,
		},
		testInfo
	) => {
		const vocabularyName = `Vocabulary: ${getRandomString()}`;
		let globalSiteId;
		let vocabularyId;

		await test.step('Setup remote staging', async () => {
			globalSiteId = await getGlobalSiteId(apiHelpers);
			const remoteGlobalSiteId = await getGlobalSiteId(remoteApiHelpers);

			await apiHelpers.jsonWebServicesStaging.enableRemoteStaging({
				groupId: globalSiteId,
				remoteGroupId: remoteGlobalSiteId,
				remotePort,
			});
		});

		try {
			await test.step('Create vocabulary', async () => {
				const {id} =
					await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary(
						{
							name: vocabularyName,
							siteId: globalSiteId,
						}
					);
				apiHelpers.data.push({
					id,
					type: 'taxonomyVocabulary',
				});

				vocabularyId = id;
			});

			await test.step('Publish vocabulary and verify on remote site', async () => {
				await page.goto(`/group/global${PORTLET_URLS.categoriesAdmin}`);
				await portletStagingPage.openIframe();
				await portletStagingPage.publishToLive();

				await remotePage.goto(
					`/group/global${PORTLET_URLS.categoriesAdmin}`
				);
				await expect(
					remotePage.getByRole('menuitem', {name: vocabularyName})
				).toBeVisible();
			});

			await test.step('Delete vocabulary, publish deletion and verify removal on remote site', async () => {
				await apiHelpers.headlessAdminTaxonomy.deleteTaxonomyVocabulary(
					vocabularyId
				);

				await portletStagingPage.openIframe();

				const contentCheckbox =
					portletStagingPage.publishStagingIframe.getByLabel(
						/Content\s+\d+\s+Deletions/i
					);
				await expect(async () => {
					await expect(contentCheckbox).not.toBeChecked();
				}).toPass();

				await portletStagingPage.publishStagingIframe
					.getByLabel('Replicate Individual')
					.check();

				await contentCheckbox.check();

				await portletStagingPage.publishToLive();

				await remotePage.goto(
					`/group/global${PORTLET_URLS.categoriesAdmin}`
				);
				await expect(
					remotePage.getByRole('menuitem', {name: vocabularyName})
				).toBeHidden();
			});
		}
		finally {
			await test.step('Teardown: Disabling staging on global site', async () => {
				await safeTeardown(
					async () =>
						await configStagingPage.disableStaging('/global'),
					testInfo.timeout * 0.5
				);
			});
		}
	}
);
