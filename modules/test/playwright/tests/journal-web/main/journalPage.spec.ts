/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {createCategories} from '../../../helpers/CreateCategories';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {journalPagesTest} from './fixtures/journalPagesTest';
import getDataStructureDefinition from './utils/getDataStructureDefinition';

export const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest()
);

test(
	'List view displays folders and articles correctly',
	{
		tag: '@LPD-53481',
	},
	async ({apiHelpers, journalPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: 'First Web content'},
		});

		await apiHelpers.jsonWebServicesJournal.addFolder({
			groupId: site.id,
		});

		await journalPage.goto(site.friendlyUrlPath);

		await journalPage.changeView('list');

		await expect(
			page
				.locator(
					'[id="_com_liferay_journal_web_portlet_JournalPortlet_articlesSearchContainer"]'
				)
				.getByText('Web Content', {exact: true})
		).toBeVisible();

		await expect(page.getByText('Folders')).toBeVisible();
	}
);

test(
	'Table view displays folders and articles correctly',
	{
		tag: '@LPD-42429',
	},
	async ({apiHelpers, journalPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: 'First Web content'},
		});

		await apiHelpers.jsonWebServicesJournal.addFolder({
			groupId: site.id,
		});

		await journalPage.goto(site.friendlyUrlPath);

		await journalPage.changeView('table');

		await expect(page.getByRole('cell', {name: 'Title'})).toBeVisible();

		await expect(
			page.getByRole('cell', {name: 'Description'})
		).toBeVisible();

		await expect(page.getByRole('cell', {name: 'Author'})).toBeVisible();

		await expect(
			page.getByRole('cell', {exact: true, name: 'Web Content'})
		).toBeVisible();

		await expect(
			page.getByRole('cell', {exact: true, name: 'Folders'})
		).toBeVisible();

		await expect(page.getByRole('cell', {name: 'Status'})).toBeVisible();

		await expect(page.getByRole('cell', {name: 'Type'})).toBeVisible();

		await expect(
			page.getByRole('cell', {name: 'Modified Date'})
		).toBeVisible();

		await expect(
			page.getByRole('cell', {name: 'Display Date'})
		).toBeVisible();

		await expect(
			page.getByRole('cell', {name: 'Create Date'})
		).toBeVisible();
	}
);

test(
	'After clicking on Clear (filter by structure) you can see all the web contents',
	{
		tag: '@LPS-191026',
	},
	async ({apiHelpers, journalEditArticlePage, journalPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: 'First Web content'},
		});

		const structureName = 'Structure Test';

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [{name: 'Text', repeatable: false}],
			name: structureName,
		});

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		await journalEditArticlePage.createArticleForStructure({
			structureName,
			title: 'Second Web Content',
		});

		await journalPage.goto(site.friendlyUrlPath);

		await expect(
			page.getByRole('link', {name: 'First Web content'})
		).toBeVisible();

		await expect(
			page.getByRole('link', {name: 'Second Web content'})
		).toBeVisible();

		await page.getByLabel('Filter', {exact: true}).click();

		await page.getByRole('menuitem', {name: 'Structures'}).click();

		const structuresFrame = await page.frameLocator(
			'iframe[title="Structures"]'
		);

		await structuresFrame
			.getByLabel('Reverse Order Direction: Currently Descending')
			.waitFor();

		await structuresFrame
			.getByRole('cell', {name: 'Basic Web Content'})
			.click();

		await expect(
			page.getByRole('link', {name: 'Second Web content'})
		).toBeHidden();

		await page
			.getByLabel('Clear 1 Result for Structures: Basic Web Content')
			.click();

		await expect(
			page.getByRole('link', {name: 'Second Web content'})
		).toBeVisible();
	}
);

test(
	'Validate Modified Date format in Table View',
	{
		tag: '@LPD-48258',
	},
	async ({apiHelpers, journalPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: 'First Web content'},
		});

		await journalPage.goto(site.friendlyUrlPath);

		expect(
			page.getByRole('row', {name: /\d+ .* ago by .*/i})
		).toBeVisible();
	}
);

test(
	'Delete option should not be available when there is only one version available',
	{tag: '@LPD-65083'},
	async ({apiHelpers, journalPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const article = await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: 'Basic Web content'},
		});

		await journalPage.goto(site.friendlyUrlPath);

		await page.getByRole('button', {name: 'Actions'}).click();

		await page.getByRole('menuitem', {name: 'View History'}).click();

		await page.getByRole('button', {name: 'Actions'}).first().click();

		await expect(
			page.getByRole('menuitem', {name: 'Delete'})
		).not.toBeVisible();

		await apiHelpers.jsonWebServicesJournal.editWebContent(
			{title: 'Updated Basic Web content'},
			site.id,
			article
		);

		await journalPage.goto(site.friendlyUrlPath);

		await page.getByRole('button', {name: 'Actions'}).click();

		await page.getByRole('menuitem', {name: 'View History'}).click();

		await page.getByRole('button', {name: 'Actions'}).first().click();

		await expect(
			page.getByRole('menuitem', {name: 'Delete'})
		).toBeVisible();
	}
);

test(
	'Current item checkbox is disabled in Related Assets',
	{
		tag: '@LPD-54293',
	},
	async ({apiHelpers, journalEditArticlePage, journalPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const webContentTitles = [
			'First Web Content',
			'Second Web Content',
			'Third Web Content',
		];

		for (const title of webContentTitles) {
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: title},
			});
		}

		await journalPage.goto(site.friendlyUrlPath);

		await page.getByRole('link', {name: 'First Web Content'}).click();

		await journalEditArticlePage.openRelatedAsset('Basic Web Content');

		await journalEditArticlePage.changeViewInRelatedAssetPopUp(
			'Basic Web Content',
			'list'
		);

		const relatedAssetsFrame = page.frameLocator(
			`iframe[title="Select Basic Web Content"]`
		);

		const relatedAssetItems = relatedAssetsFrame.locator(
			'dd:has(input[type="checkbox"]:not([disabled]))'
		);

		await expect(relatedAssetItems).toHaveCount(2);
	}
);

test(
	'Check Search Works in View History Page',
	{
		tag: '@LPD-54659',
	},
	async ({apiHelpers, journalPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const webContent =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: 'test'},
			});

		await apiHelpers.jsonWebServicesJournal.editWebContent(
			{title: 'Basic Web Content'},
			site.id,
			webContent
		);

		await journalPage.goto(site.friendlyUrlPath);

		await page.getByRole('button', {name: 'Actions'}).click();
		await page.getByRole('menuitem', {name: 'View History'}).click();

		await page.getByRole('button', {name: 'Versions'}).waitFor();

		await page.getByLabel('Select View, Currently').click();
		await page.getByRole('menuitem', {name: 'Table'}).click();

		const searchInput = page.locator('input[type="search"]');
		await searchInput.waitFor({state: 'visible'});
		await searchInput.fill('Basic');
		await searchInput.press('Enter');

		await page
			.locator('div')
			.filter({hasText: /^Clear$/})
			.waitFor();

		const resultRows = page.locator('tbody tr[data-selectable="true"]');
		await resultRows.first().waitFor({state: 'visible'});
		const count = await resultRows.count();
		expect(count).toBe(1);
	}
);

test(
	'Validate PrimaryKey for Folder in Related Assets',
	{
		tag: '@LPD-55314',
	},
	async ({apiHelpers, journalEditArticlePage, journalPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: 'Basic Web content'},
		});

		await apiHelpers.jsonWebServicesJournal.addFolder({
			groupId: site.id,
		});

		await journalPage.goto(site.friendlyUrlPath);

		await page.getByRole('link', {name: 'Basic Web content'}).click();

		await journalEditArticlePage.openRelatedAsset('Basic Web Content');

		await journalEditArticlePage.changeViewInRelatedAssetPopUp(
			'Basic Web Content',
			'list'
		);

		const relatedAssetsFrame = page.frameLocator(
			`iframe[title="Select Basic Web Content"]`
		);

		const inputValue = await relatedAssetsFrame
			.locator(
				'#_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_articlesPrimaryKeys'
			)
			.getAttribute('value');

		expect(/[[{]/.test(inputValue || '')).toBeFalsy();
	}
);

test(
	'Folders come first when having multiple pages and filtering by Approved',
	{
		tag: '@LPD-55865',
	},
	async ({apiHelpers, journalPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		for (let i = 1; i <= 6; i++) {
			await apiHelpers.jsonWebServicesJournal.addFolder({
				groupId: site.id,
				name: `Folder ${i}`,
			});
		}

		for (let i = 1; i <= 6; i++) {
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: `Web Content ${i}`},
			});
		}

		for (let i = 7; i <= 12; i++) {
			await apiHelpers.jsonWebServicesJournal.addFolder({
				groupId: site.id,
				name: `Folder ${i}`,
			});
		}

		for (let i = 7; i <= 12; i++) {
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: `Web Content ${i}`},
			});
		}

		await journalPage.goto(site.friendlyUrlPath);

		await page.getByLabel('Filter', {exact: true}).click();

		await page.getByRole('menuitem', {name: 'Approved'}).click();

		await page.getByLabel('Filter', {exact: true}).click();

		await page.getByRole('menuitem', {name: 'Web Content'}).click();

		const foldersList = await page
			.getByRole('link')
			.filter({hasText: 'Folder'})
			.all();
		expect(foldersList.length).toBe(12);
	}
);

test(
	'Permissions dialog is launched with the roles list visible',
	{
		tag: '@LPD-63441',
	},
	async ({apiHelpers, journalPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		for (let i = 1; i <= 2; i++) {
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: `Web Content ${i}`},
			});
		}

		await journalPage.goto(site.friendlyUrlPath);

		const checkboxes = page.locator(
			'input[type="checkbox"][name="_com_liferay_journal_web_portlet_JournalPortlet_rowIdsJournalArticle"]'
		);

		const count = await checkboxes.count();

		for (let i = 0; i < count; i++) {
			await checkboxes.nth(i).check();
		}

		await page.getByTitle('Actions', {exact: true}).click();

		const permissionsButton = page.locator(
			'button[data-action="changePermissions"]',
			{hasText: 'Permissions'}
		);

		await permissionsButton.click();

		const permissionsFrame = page.frameLocator(
			'iframe[title*="Permissions"]'
		);

		const guestTd = permissionsFrame.locator('td.lfr-role-column', {
			hasText: 'Guest',
		});

		await guestTd.waitFor({state: 'attached', timeout: 10000});

		await expect(guestTd).toBeVisible();
	}
);

test(
	'Web Content Category Filter shows public categories only',
	{
		tag: '@LPP-60943',
	},
	async ({apiHelpers, journalPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		for (let i = 1; i <= 2; i++) {
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: `Web Content ${i}`},
			});
		}

		const vocabularyName = 'Private Vocabulary 1';
		const internalCategoryName = 'Internal Category 1';

		await createCategories({
			apiHelpers,
			categoryNames: [{name: internalCategoryName}],
			siteId: site.id,
			vocabularyName,
			vocabularyVisibility: true,
		});

		await journalPage.goto(site.friendlyUrlPath);

		await page.getByLabel('Filter', {exact: true}).click();

		await page.getByRole('menuitem', {name: 'Categories'}).click();

		const categoriesFrame = page.frameLocator(
			'iframe[title*="Filter by Categories"]'
		);

		await expect(
			categoriesFrame.locator('text=' + vocabularyName)
		).toHaveCount(0);
	}
);
