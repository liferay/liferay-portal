/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {createCategories} from '../../../helpers/CreateCategories';
import {changeManagementToolbarView} from '../../../utils/changeManagementToolbarView';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import addSingleApproverWorkflow from '../../content-dashboard-web/main/utils/addSingleApproverWorkflow';
import addConnectedDepot from '../../depot-web/main/utils/addConnectedDepot';
import {journalPagesTest} from './fixtures/journalPagesTest';
import getDataStructureDefinition from './utils/getDataStructureDefinition';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest()
);

test(
	'Web content shows its status, version and icon in every view',
	{tag: ['@LPS-155805', '@LPS-197978', '@LPS-202596']},
	async ({apiHelpers, journalPage, page, site}) => {
		const ddmStructureId = await getBasicWebContentStructureId(apiHelpers);

		const folderName = getRandomString();

		await apiHelpers.jsonWebServicesJournal.addFolder({
			groupId: site.id,
			name: folderName,
		});

		const titles = [
			getRandomString(),
			getRandomString(),
			getRandomString(),
		];

		let webContent = await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId,
			groupId: site.id,
			titleMap: {en_US: titles[0]},
		});

		for (const title of titles.slice(1)) {
			webContent = await apiHelpers.jsonWebServicesJournal.editWebContent(
				{title},
				site.id,
				webContent
			);
		}

		const draftTitle = getRandomString();

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId,
			groupId: site.id,
			serviceContext: {workflowAction: 2},
			titleMap: {en_US: draftTitle},
		});

		await journalPage.goto(site.friendlyUrlPath);

		await changeManagementToolbarView(page, 'List');

		const rows = page.locator('[data-qa-id="row"]');

		await expect(rows.filter({hasText: folderName})).toBeVisible();
		await expect(rows.filter({hasText: titles[0]})).toBeHidden();

		await expect(journalPage.getArticleStatus(titles[2])).toHaveText(
			'Approved'
		);
		await expect(journalPage.getArticleStatus(draftTitle)).toHaveText(
			'Draft'
		);

		for (const view of ['Cards', 'Table', 'List']) {
			await changeManagementToolbarView(page, view);

			await expect(
				rows
					.filter({hasText: titles[2]})
					.locator('.lexicon-icon-web-content')
					.first()
			).toBeVisible();
		}
	}
);

test(
	'Info panel shows the status and the last ten versions of web content',
	{tag: '@LPS-191927'},
	async ({apiHelpers, journalPage, page, site}) => {
		const ddmStructureId = await getBasicWebContentStructureId(apiHelpers);

		const titles = Array.from({length: 11}, () => getRandomString());

		let webContent = await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId,
			groupId: site.id,
			titleMap: {en_US: titles[0]},
		});

		for (const title of titles.slice(1)) {
			webContent = await apiHelpers.jsonWebServicesJournal.editWebContent(
				{title},
				site.id,
				webContent
			);
		}

		await journalPage.goto(site.friendlyUrlPath);

		await changeManagementToolbarView(page, 'List');

		await page
			.getByRole('checkbox', {name: `Select ${titles[10]}`})
			.check();

		await page.getByRole('button', {name: 'Toggle Info Panel'}).click();

		const infoPanel = page.locator('.sidebar');

		await expect(
			infoPanel.getByRole('heading', {name: titles[10]})
		).toBeVisible();
		await expect(
			infoPanel.getByText('Version 2.0', {exact: true}).first()
		).toBeVisible();
		await expect(
			infoPanel.getByText('Approved', {exact: true}).first()
		).toBeVisible();

		// Check the versions tab

		await infoPanel.getByRole('tab', {name: 'Versions'}).click();

		const versionsPanel = infoPanel.getByRole('tabpanel');

		for (const version of [
			'1.1',
			'1.2',
			'1.3',
			'1.4',
			'1.5',
			'1.6',
			'1.7',
			'1.8',
			'1.9',
			'2.0',
		]) {
			await expect(
				versionsPanel.getByText(`Version ${version}`, {exact: true})
			).toBeVisible();
		}

		await expect(
			versionsPanel.getByText('Version 1.0', {exact: true})
		).toBeHidden();

		await versionsPanel.getByText('View More', {exact: true}).click();

		for (const title of titles) {
			await expect(
				page.getByRole('cell', {exact: true, name: title})
			).toBeVisible();
		}
	}
);

test(
	'A version of web content can be previewed and expired from the info panel',
	{tag: '@LPS-191927'},
	async ({apiHelpers, journalPage, page, site}) => {
		const ddmStructureId = await getBasicWebContentStructureId(apiHelpers);

		const content = getRandomString();
		const titles = [
			getRandomString(),
			getRandomString(),
			getRandomString(),
		];

		let webContent = await apiHelpers.jsonWebServicesJournal.addWebContent({
			content,
			ddmStructureId,
			groupId: site.id,
			titleMap: {en_US: titles[0]},
		});

		for (const title of titles.slice(1)) {
			webContent = await apiHelpers.jsonWebServicesJournal.editWebContent(
				{content: getRandomString(), title},
				site.id,
				webContent
			);
		}

		await journalPage.goto(site.friendlyUrlPath);

		await changeManagementToolbarView(page, 'List');

		await page.getByRole('checkbox', {name: `Select ${titles[2]}`}).check();

		await page.getByRole('button', {name: 'Toggle Info Panel'}).click();

		const infoPanel = page.locator('.sidebar');

		await infoPanel.getByRole('tab', {name: 'Versions'}).click();

		// Preview version 1.0

		const versionRow = (version: string) =>
			infoPanel.locator('li').filter({
				has: page.getByText(`Version ${version}`, {exact: true}),
				visible: true,
			});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Preview'}),
			trigger: versionRow('1.0').getByRole('button'),
		});

		const modal = page.locator('.modal-dialog');

		await expect(modal.getByText(titles[0])).toBeVisible();
		await expect(
			modal.frameLocator('iframe').getByText(content)
		).toBeVisible();

		await modal.getByLabel('Close').click();

		// Expire version 1.1

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Expire'}),
			trigger: versionRow('1.1').getByRole('button'),
		});

		await waitForAlert(page);

		await page.getByRole('checkbox', {name: `Select ${titles[2]}`}).check();

		await page.getByRole('button', {name: 'Toggle Info Panel'}).click();

		await infoPanel.getByRole('tab', {name: 'Versions'}).click();

		await expect(versionRow('1.1')).toContainText('Expired');
	}
);

test(
	'Search results can be sorted and navigated by their breadcrumb',
	{tag: ['@LPS-196842', '@LPS-197163']},
	async ({apiHelpers, journalPage, page, site}) => {
		const ddmStructureId = await getBasicWebContentStructureId(apiHelpers);

		for (const title of ['EMEA Guild', 'APAC Guild']) {
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId,
				groupId: site.id,
				titleMap: {en_US: title},
			});
		}

		const folderName = 'Charters';

		const folder = await apiHelpers.jsonWebServicesJournal.addFolder({
			groupId: site.id,
			name: folderName,
		});

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId,
			folderId: folder.folderId,
			groupId: site.id,
			titleMap: {en_US: 'Guild Charter'},
		});

		await journalPage.goto(site.friendlyUrlPath);

		await changeManagementToolbarView(page, 'List');

		await journalPage.search('Guild');

		await expect(
			page.getByText('3 Results Found for "Guild"')
		).toBeVisible();

		// Sort by title

		const setOrder = async (menuItem: string) => {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: menuItem}),
				trigger: page.getByRole('button', {exact: true, name: 'Order'}),
			});
		};

		await setOrder('Title');
		await setOrder('Ascending');

		const rowTitles = page
			.locator('[data-qa-id="row"]')
			.getByRole('link', {name: /Guild/});

		await expect(rowTitles).toHaveText([
			'APAC Guild',
			'EMEA Guild',
			'Guild Charter',
		]);

		await setOrder('Descending');

		await expect(rowTitles).toHaveText([
			'Guild Charter',
			'EMEA Guild',
			'APAC Guild',
		]);

		// Navigate by breadcrumb

		await page
			.locator('[data-qa-id="row"]')
			.filter({hasText: 'Guild Charter'})
			.getByRole('link', {exact: true, name: folderName})
			.click();

		await expect(
			page.getByRole('link', {exact: true, name: 'Guild Charter'})
		).toBeVisible();
		await expect(
			page.getByRole('link', {exact: true, name: 'APAC Guild'})
		).toBeHidden();

		await journalPage.goto(site.friendlyUrlPath);

		await journalPage.search('Guild');

		await page
			.locator('[data-qa-id="row"]')
			.filter({hasText: 'APAC Guild'})
			.getByRole('link', {exact: true, name: 'Home'})
			.click();

		await expect(
			page.getByRole('link', {exact: true, name: folderName})
		).toBeVisible();

		await setOrder('Modified Date');
	}
);

test(
	'Search location and fields can be picked with the keyboard',
	{tag: '@LPS-196839'},
	async ({apiHelpers, journalPage, page, site}) => {
		const ddmStructureId = await getBasicWebContentStructureId(apiHelpers);

		for (const title of ['Over The Years', 'Over The Earth']) {
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				content: 'Liferay DXP',
				ddmStructureId,
				groupId: site.id,
				titleMap: {en_US: title},
			});
		}

		const folder = await apiHelpers.jsonWebServicesJournal.addFolder({
			groupId: site.id,
			name: getRandomString(),
		});

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			content: 'After many years I understood',
			ddmStructureId,
			folderId: folder.folderId,
			groupId: site.id,
			titleMap: {en_US: 'Folder Article'},
		});

		await journalPage.gotoFolder({
			folderId: folder.folderId,
			siteUrl: site.friendlyUrlPath,
		});

		await changeManagementToolbarView(page, 'List');

		await journalPage.search('years');

		const rowTitles = page
			.locator('[data-qa-id="row"]')
			.getByRole('link', {name: /Years|Folder Article/});

		const selectOption = async (buttonSelector: string, key: string) => {
			const button = page.locator(buttonSelector);

			await button.focus();
			await page.keyboard.press('Enter');
			await page.keyboard.press(key);
			await page.keyboard.press('Enter');

			return button;
		};

		// Search everywhere

		await expect(
			await selectOption('button[id$="_searchLocation"]', 'ArrowDown')
		).toHaveText('Everywhere');

		await expect(rowTitles).toHaveCount(2);
		await expect(
			rowTitles.filter({hasText: 'Folder Article'})
		).toBeVisible();
		await expect(
			rowTitles.filter({hasText: 'Over The Years'})
		).toBeVisible();

		await checkAccessibility({page, selectors: ['#main-content']});

		// Search in titles only

		await expect(
			await selectOption('button[id$="_searchIn"]', 'ArrowDown')
		).toHaveText('Title Only');

		await expect(rowTitles).toHaveText(['Over The Years']);

		await checkAccessibility({page, selectors: ['#main-content']});

		// Search the current folder

		await selectOption('button[id$="_searchLocation"]', 'ArrowUp');

		await expect(page.getByText('No web content was found.')).toBeVisible();
	}
);

test(
	'Status filter lists the workflow statuses when a workflow is enabled',
	{tag: ['@LPS-146458', '@LPS-202596']},
	async ({apiHelpers, journalPage, page, site}) => {
		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
			groupId: site.id,
		});

		const statusMenuItems = page
			.getByRole('group', {name: 'Filter by Status'})
			.getByRole('menuitem');

		await journalPage.goto(site.friendlyUrlPath);

		await clickAndExpectToBeVisible({
			target: statusMenuItems.first(),
			trigger: page.getByRole('button', {exact: true, name: 'Filter'}),
		});

		await expect(statusMenuItems).toHaveText([
			'Any',
			'Draft',
			'Approved',
			'Expired',
			'Scheduled',
		]);

		// Enable a workflow

		await addSingleApproverWorkflow({
			apiHelpers,
			className: 'com.liferay.journal.model.JournalArticle',
			siteId: site.id,
		});

		await journalPage.goto(site.friendlyUrlPath);

		await clickAndExpectToBeVisible({
			target: statusMenuItems.first(),
			trigger: page.getByRole('button', {exact: true, name: 'Filter'}),
		});

		await expect(statusMenuItems).toHaveText([
			'Any',
			'Draft',
			'Pending',
			'Denied',
			'Approved',
			'Expired',
			'Scheduled',
		]);
	}
);

test(
	'Web content can be filtered by categories and tags',
	{tag: '@LPS-197159'},
	async ({apiHelpers, journalPage, page, site}) => {
		const vocabularyName = getRandomString();

		const [category] = await createCategories({
			apiHelpers,
			categoryNames: [{name: getRandomString()}],
			siteId: site.id,
			vocabularyName,
		});

		const [usedTag, unusedTag] = [getRandomString(), getRandomString()];

		await apiHelpers.headlessAdminTaxonomy.postSiteKeyword({
			name: unusedTag,
			siteId: site.id,
		});

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const [plainTitle, categorizedTitle, taggedTitle] = [
			getRandomString(),
			getRandomString(),
			getRandomString(),
		];

		for (const {categoryIds, tags, title} of [
			{title: plainTitle},
			{categoryIds: [category.id], title: categorizedTitle},
			{tags: [usedTag], title: taggedTitle},
		]) {
			await apiHelpers.headlessDelivery.postStructuredContent({
				categoryIds,
				contentStructureId,
				datePublished: new Date().toISOString(),
				siteId: site.id,
				tags,
				title,
			});
		}

		const titleLink = (title: string) =>
			page.getByRole('link', {exact: true, name: title});

		const openFilter = async (filterName: string) => {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: filterName}),
				trigger: page.getByRole('button', {
					exact: true,
					name: 'Filter',
				}),
			});
		};

		// Filter by category

		await journalPage.goto(site.friendlyUrlPath);

		await openFilter('Categories');

		const categoriesModal = page.frameLocator(
			'iframe[title="Filter by Categories"]'
		);

		await clickAndExpectToBeVisible({
			target: categoriesModal.getByText(category.name, {exact: true}),
			trigger: categoriesModal.getByText(vocabularyName),
		});

		await categoriesModal.getByText(category.name, {exact: true}).click();

		await page
			.getByRole('dialog')
			.getByRole('button', {exact: true, name: 'Select'})
			.click();

		await expect(
			page.getByText('1 Result Found With Filters')
		).toBeVisible();
		await expect(
			page.getByText(`Categories: ${category.name}`)
		).toBeVisible();
		await expect(titleLink(categorizedTitle)).toBeVisible();
		await expect(titleLink(plainTitle)).toBeHidden();
		await expect(titleLink(taggedTitle)).toBeHidden();

		// Filter by tag

		for (const [tag, results] of [
			[usedTag, 1],
			[unusedTag, 0],
		] as const) {
			await journalPage.goto(site.friendlyUrlPath);

			await openFilter('Tags');

			const tagsModal = page.frameLocator(
				'iframe[title="Filter by Tags"]'
			);

			await tagsModal
				.getByRole('row', {name: tag})
				.getByRole('checkbox')
				.check();

			await page
				.getByRole('dialog')
				.getByRole('button', {exact: true, name: 'Select'})
				.click();

			await expect(
				page.getByText(
					`${results} Result${results === 1 ? '' : 's'} Found With Filters`
				)
			).toBeVisible();
			await expect(page.getByText(`Tags: ${tag}`)).toBeVisible();
		}

		await expect(page.getByText('No web content was found.')).toBeVisible();

		await journalPage.goto(site.friendlyUrlPath);

		await openFilter('Tags');

		await page
			.frameLocator('iframe[title="Filter by Tags"]')
			.getByRole('row', {name: usedTag})
			.getByRole('checkbox')
			.check();

		await page
			.getByRole('dialog')
			.getByRole('button', {exact: true, name: 'Select'})
			.click();

		await expect(titleLink(taggedTitle)).toBeVisible();
		await expect(titleLink(categorizedTitle)).toBeHidden();
	}
);

test(
	'Comments of web content can be searched by keyword',
	{tag: '@LPS-202596'},
	async ({apiHelpers, journalPage, page, site}) => {
		await apiHelpers.jsonWebServicesJournal.addFolder({
			groupId: site.id,
			name: 'Liferay',
		});

		const structuredContent =
			await apiHelpers.headlessDelivery.postStructuredContent({
				contentStructureId:
					await getBasicWebContentStructureId(apiHelpers),
				datePublished: new Date().toISOString(),
				siteId: site.id,
				title: 'Liferay DXP',
			});

		for (const comment of [
			'Welcome to Liferay DXP',
			'Enjoy using the best DXP on Earth!',
		]) {
			await apiHelpers.headlessDelivery.postStructuredContentComment(
				structuredContent.id,
				comment
			);
		}

		const searchInComments = async () => {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByText('Comments', {exact: true}),
				trigger: page.locator('button[id$="_searchIn"]'),
			});

			await page.waitForURL(/_searchIn=comments/);
		};

		await journalPage.goto(site.friendlyUrlPath);

		await changeManagementToolbarView(page, 'List');

		// Search for Liferay

		await journalPage.search('Liferay');

		await expect(
			page.getByText('2 Results Found for "Liferay"')
		).toBeVisible();

		await searchInComments();

		await expect(
			page.getByText('1 Result Found for "Liferay"')
		).toBeVisible();
		await expect(page.getByText('Welcome to Liferay DXP')).toBeVisible();

		// Search for DXP

		await journalPage.goto(site.friendlyUrlPath);

		await journalPage.search('DXP');

		await expect(page.getByText('1 Result Found for "DXP"')).toBeVisible();
		await expect(
			page.getByRole('link', {exact: true, name: 'Liferay DXP'})
		).toBeVisible();

		await searchInComments();

		await expect(page.getByText('2 Results Found for "DXP"')).toBeVisible();
		await expect(page.getByText('Welcome to Liferay DXP')).toBeVisible();
		await expect(
			page.getByText('Enjoy using the best DXP on Earth!')
		).toBeVisible();
	}
);

async function selectHighlightedStructures(page: Page, names: string[]) {
	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {name: 'Configuration'}),
		trigger: page.getByRole('button', {exact: true, name: 'Options'}),
	});

	await page.getByLabel('Select Highlighted Structures').click();

	const dialog = page.getByRole('dialog', {name: 'Select Structures'});

	for (const name of names) {
		await dialog
			.frameLocator('iframe')
			.getByRole('row', {name: `Select ${name}`})
			.getByRole('checkbox')
			.check();
	}

	await dialog.getByRole('button', {exact: true, name: 'Add'}).click();

	for (const name of names) {
		await expect(page.getByRole('cell', {exact: true, name})).toBeVisible();
	}

	await expect(async () => {
		await page
			.getByRole('button', {exact: true, name: 'Save'})
			.click({timeout: 2000});

		await expect(page.locator('.alert-success')).toBeVisible({
			timeout: 3000,
		});
	}).toPass({timeout: 20000});
}

test(
	'Highlighted structures get their own sheet in the web content admin',
	{tag: '@LPS-196819'},
	async ({apiHelpers, journalPage, page, site}) => {
		const structureName = getRandomString();

		const structure = await apiHelpers.dataEngine.createStructure(
			site.id,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{name: 'Text'}],
				name: structureName,
			})
		);

		const folder = await apiHelpers.jsonWebServicesJournal.addFolder({
			groupId: site.id,
			name: 'Archive',
		});

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
			groupId: site.id,
			titleMap: {en_US: 'Liferay DXP'},
		});

		for (const [title, folderId] of [
			['Liferay LXC', 0],
			['Liferay Commerce', folder.folderId],
		]) {
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				contentFields: [{name: 'Text', value: getRandomString()}],
				ddmStructureId: structure.id,
				ddmTemplateKey: '',
				folderId,
				groupId: site.id,
				titleMap: {en_US: String(title)},
			});
		}

		const titleLink = (title: string) =>
			page.getByRole('link', {exact: true, name: title});

		const openStructureSheet = async () => {
			await journalPage.goto(site.friendlyUrlPath);

			await page
				.getByRole('menuitem', {exact: true, name: structureName})
				.click();

			await expect(
				page.getByRole('heading', {name: structureName})
			).toBeVisible();
		};

		// Highlight the structure

		await journalPage.goto(site.friendlyUrlPath);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Configuration'}),
			trigger: page.getByRole('button', {exact: true, name: 'Options'}),
		});

		await expect(page.getByText('No Structures Selected')).toBeVisible();

		await journalPage.goto(site.friendlyUrlPath);

		await selectHighlightedStructures(page, [structureName]);

		// Check the content library sheet

		await journalPage.goto(site.friendlyUrlPath);

		await changeManagementToolbarView(page, 'List');

		await expect(
			page.getByRole('heading', {name: 'Content Library'})
		).toBeVisible();

		for (const title of ['Archive', 'Liferay DXP', 'Liferay LXC']) {
			await expect(titleLink(title)).toBeVisible();
		}

		const newMenu = page.getByRole('menu');

		await clickAndExpectToBeVisible({
			target: newMenu,
			trigger: journalPage.newButton,
		});

		for (const type of ['Basic Web Content', 'Folder', structureName]) {
			await expect(
				newMenu.getByRole('menuitem', {exact: true, name: type})
			).toBeVisible();
		}

		await page.keyboard.press('Escape');

		await journalPage.search('Liferay');

		for (const title of [
			'Liferay Commerce',
			'Liferay DXP',
			'Liferay LXC',
		]) {
			await expect(titleLink(title)).toBeVisible();
		}

		// Check the structure sheet

		await openStructureSheet();

		for (const title of ['Liferay Commerce', 'Liferay LXC']) {
			await expect(titleLink(title)).toBeVisible();
		}

		for (const title of ['Archive', 'Liferay DXP']) {
			await expect(titleLink(title)).toBeHidden();
		}

		for (const view of ['Cards', 'Table', 'List']) {
			await openStructureSheet();

			await changeManagementToolbarView(page, view);

			await page
				.locator('[data-qa-id="row"]')
				.filter({hasText: 'Liferay LXC'})
				.getByRole('checkbox')
				.check();

			await page.getByRole('button', {name: 'Toggle Info Panel'}).click();

			const infoPanel = page.locator('.sidebar');

			await expect(
				infoPanel.getByRole('heading', {name: 'Liferay LXC'})
			).toBeVisible();
			await expect(
				infoPanel.getByText(structureName, {exact: true}).first()
			).toBeVisible();
		}

		await openStructureSheet();

		await journalPage.search('Liferay');

		await expect(titleLink('Liferay LXC')).toBeVisible();
		await expect(titleLink('Liferay DXP')).toBeHidden();
	}
);

test(
	'Highlighted structures can come from an asset library or the global site',
	{tag: '@LPS-196819'},
	async ({apiHelpers, journalPage, page, site}) => {
		const assetLibrary = await addConnectedDepot(apiHelpers, site, {
			ddmStructuresAvailable: true,
		});

		const structureName = getRandomString();

		const structure = await apiHelpers.dataEngine.createStructure(
			String(assetLibrary.siteId),
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{name: 'Text'}],
				name: structureName,
			})
		);

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
			groupId: assetLibrary.siteId,
			titleMap: {en_US: 'EMEA Guild'},
		});

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			contentFields: [{name: 'Text', value: getRandomString()}],
			ddmStructureId: structure.id,
			ddmTemplateKey: '',
			groupId: assetLibrary.siteId,
			titleMap: {en_US: 'LXC'},
		});

		const assetLibraryURL = `/asset-library-${assetLibrary.id}`;

		// Highlight the structure in the asset library

		await journalPage.goto(assetLibraryURL);

		await selectHighlightedStructures(page, [structureName]);

		await journalPage.goto(assetLibraryURL);

		await changeManagementToolbarView(page, 'List');

		for (const title of ['EMEA Guild', 'LXC']) {
			await expect(
				page.getByRole('link', {exact: true, name: title})
			).toBeVisible();
		}

		await page
			.getByRole('menuitem', {exact: true, name: structureName})
			.click();

		await expect(
			page.getByRole('link', {exact: true, name: 'LXC'})
		).toBeVisible();
		await expect(
			page.getByRole('link', {exact: true, name: 'EMEA Guild'})
		).toBeHidden();

		// Check the structures available in the connected site

		await journalPage.goto(site.friendlyUrlPath);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Configuration'}),
			trigger: page.getByRole('button', {exact: true, name: 'Options'}),
		});

		await page.getByLabel('Select Highlighted Structures').click();

		const structuresFrame = page
			.getByRole('dialog', {name: 'Select Structures'})
			.frameLocator('iframe');

		await expect(
			structuresFrame.getByRole('row', {name: 'Select Basic Web Content'})
		).toContainText('Global');
		await expect(
			structuresFrame.getByRole('row', {name: `Select ${structureName}`})
		).toContainText('Asset Library or Space');
	}
);
