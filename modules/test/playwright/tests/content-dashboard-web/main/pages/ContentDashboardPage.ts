/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {changeManagementToolbarView} from '../../../../utils/changeManagementToolbarView';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {hoverAndExpectToBeVisible} from '../../../../utils/hoverAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForPageToBeLoaded} from '../../../../utils/waitForPageToBeLoaded';

export class ContentDashboardPage {
	readonly exportXLSButton: Locator;
	readonly filterButton: Locator;
	readonly infoPanel: Locator;
	readonly infoPanelAssetType: Locator;
	readonly infoPanelTitle: Locator;
	readonly orderButton: Locator;
	readonly page: Page;
	readonly searchInput: Locator;
	readonly versionTitles: Locator;
	readonly viewMoreVersionsLink: Locator;

	constructor(page: Page) {
		this.exportXLSButton = page.getByRole('button', {
			name: 'Download your data as an XLS file.',
		});
		this.filterButton = page.getByLabel('Filter', {exact: true});
		this.infoPanel = page.locator(
			'body.sidebar-open .content-dashboard.sidebar'
		);
		this.infoPanelAssetType = this.infoPanel.getByTestId('assetTypeInfo');
		this.infoPanelTitle = this.infoPanel.locator(
			'.sidebar-header .font-weight-bold'
		);
		this.orderButton = page.getByLabel('Order', {exact: true});
		this.page = page;
		this.searchInput = page.getByPlaceholder('Search for');
		this.versionTitles = this.infoPanel.locator('.list-group-title');
		this.viewMoreVersionsLink = this.infoPanel.getByRole('link', {
			name: 'View More',
		});
	}

	async clickQuickAction(title: string, actionTitle: string) {
		await hoverAndExpectToBeVisible({
			autoClick: true,
			target: this.getQuickAction(title, actionTitle),
			trigger: this.getRow(title),
		});
	}

	async clickRowAction(title: string, actionName: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: actionName,
			}),
			trigger: this.getRow(title).getByRole('button', {
				name: 'Show Actions',
			}),
		});
	}

	async clickVersionAction(version: string, actionName: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: actionName}),
			trigger: this.getVersion(version).getByRole('button', {
				name: 'Actions',
			}),
		});
	}

	async filterBy(menuItemName: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page
				.getByRole('menuitem', {exact: true, name: menuItemName})
				.first(),
			trigger: this.filterButton,
		});

		await waitForPageToBeLoaded(this.page);
	}

	async filterByAuthors({
		names,
		search,
		view,
	}: {
		names: string[];
		search?: string;
		view?: 'Cards' | 'List' | 'Table';
	}) {
		const frame = await this.openFilterModal('Author', 'Select Author');

		if (view) {
			await changeManagementToolbarView(frame, view);
		}

		if (search) {
			const searchInput = frame.getByPlaceholder('Search for');

			await searchInput.fill(search);

			await searchInput.press('Enter');
		}

		for (const name of names) {
			await frame
				.locator('.card, .list-group-item, tr', {hasText: name})
				.getByRole('checkbox')
				.first()
				.check();
		}

		await this.selectModalItems();
	}

	async filterByCategories(
		categories: {categoryName: string; vocabularyLabel: string}[]
	) {
		const frame = await this.openFilterModal(
			'Categories',
			'Select Categories'
		);

		for (const {categoryName, vocabularyLabel} of categories) {
			const category = frame.getByText(categoryName, {exact: true});

			const vocabulary = frame.getByText(vocabularyLabel, {exact: true});

			await vocabulary.waitFor();

			await clickAndExpectToBeVisible({
				target: category,
				trigger: vocabulary,
			});

			await category.click();
		}

		await this.selectModalItems();
	}

	async filterBySite(siteName: string) {
		const frame = await this.openFilterModal(
			'Site or Asset Library',
			'Select Site or Asset Library'
		);

		await frame.getByRole('link', {exact: true, name: 'My Sites'}).click();

		const searchInput = frame.getByPlaceholder('Search for');

		await searchInput.fill(siteName);

		await searchInput.press('Enter');

		await frame.getByText(siteName, {exact: true}).click();

		await waitForPageToBeLoaded(this.page);
	}

	async filterByTag(tagName: string) {
		const frame = await this.openFilterModal('Tags', 'Select Tags');

		const searchInput = frame.getByPlaceholder('Search for');

		await searchInput.fill(tagName);

		await searchInput.press('Enter');

		await frame
			.locator('tr', {hasText: tagName})
			.getByRole('checkbox')
			.first()
			.check();

		await this.selectModalItems();
	}

	async filterByTreeNodes({
		dialogTitle,
		menuItemName,
		nodeNames,
	}: {
		dialogTitle: 'Filter by Extension' | 'Filter by Type';
		menuItemName: 'Extension' | 'Type';
		nodeNames: (RegExp | string)[];
	}) {
		const frame = await this.openFilterModal(menuItemName, dialogTitle);

		for (const nodeName of nodeNames) {
			await this.selectTreeNode({frame, nodeName});
		}

		await this.selectModalItems();
	}

	getAppliedFilter(label: RegExp | string) {
		return this.page.locator('.label-item-expand', {hasText: label});
	}

	getCell(title: string, columnName: string) {
		return this.getRow(title).locator(`.lfr-${columnName}-column`);
	}

	getQuickAction(title: string, actionTitle: string) {
		return this.getRow(title)
			.locator('.quick-action-menu')
			.getByTitle(actionTitle, {exact: true});
	}

	getRow(title: string) {
		return this.page
			.getByTestId('row')
			.filter({has: this.page.getByTitle(title, {exact: true})});
	}

	getRowTitles() {
		return this.page.getByTestId('row').locator('.table-title');
	}

	getTreeNode(frame: FrameLocator, nodeName: RegExp | string) {
		return frame
			.locator('.treeview-link')
			.filter({has: frame.getByText(nodeName, {exact: true})});
	}

	getVersion(version: string) {
		return this.infoPanel.locator('.list-group-item').filter({
			has: this.page.locator('.list-group-title', {
				hasText: new RegExp(`^Version ${version.replace('.', '\\.')}$`),
			}),
		});
	}

	async goto(
		siteUrl?: Site['friendlyUrlPath'],
		{scopeId}: {scopeId?: string} = {}
	) {
		let url = `/group${siteUrl || '/guest'}${PORTLET_URLS.contentDashboard}`;

		if (scopeId) {
			url += `&_com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet_scopeId=${scopeId}`;
		}

		await this.page.goto(url);
	}

	async goToCurrentTab({assetTitle, siteUrl, tabName}) {
		await this.goto(siteUrl);

		if (assetTitle) {
			const searchBar = this.page.getByPlaceholder('Search for');

			await searchBar.fill(assetTitle);

			await this.page.keyboard.press('Enter');
		}

		const dropDownButton = this.page
			.locator('.lfr-entry-action-column .dropdown-action button')
			.first();

		await dropDownButton.click();

		const showInfoButton = this.page
			.locator('[data-action=showInfo]')
			.first();

		await showInfoButton.click();

		await this.page.waitForTimeout(1000);

		await this.page.getByRole('tab', {name: tabName}).click();

		await this.page.waitForTimeout(1000);
	}

	async openFilterDropdown() {
		await this.filterButton.click();

		await this.page.getByText('Filter By', {exact: true}).waitFor();
	}

	async openFilterModal(menuItemName: string, dialogTitle: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: menuItemName,
			}),
			trigger: this.filterButton,
		});

		return this.page.frameLocator(`iframe[title="${dialogTitle}"]`);
	}

	async openInfoPanel(title: string) {
		await this.clickQuickAction(title, 'Info');

		await expect(this.infoPanelTitle).toHaveText(title);
	}

	async openInfoPanelTab(tabName: 'Categorization' | 'Details' | 'Versions') {
		await this.infoPanel
			.getByRole('tab', {exact: true, name: 'Details'})
			.waitFor();

		const tab = this.infoPanel.getByRole('tab', {
			exact: true,
			name: tabName,
		});

		if (tabName === 'Versions' && !(await tab.count())) {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.page.getByRole('menuitem', {name: 'Versions'}),
				trigger: this.infoPanel.getByRole('tab', {name: 'More'}),
			});
		}
		else {
			await tab.click();
		}

		if (tabName === 'Versions') {
			await this.versionTitles.first().waitFor();
		}
	}

	async openRowActions(title: string) {
		await clickAndExpectToBeVisible({
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Info',
			}),
			trigger: this.getRow(title).getByRole('button', {
				name: 'Show Actions',
			}),
		});
	}

	async orderBy(menuItemName: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: menuItemName,
			}),
			trigger: this.orderButton,
		});

		await waitForPageToBeLoaded(this.page);
	}

	async search(keywords: string) {
		await expect(async () => {
			await this.searchInput.fill(keywords, {timeout: 2000});

			await this.searchInput.press('Enter', {timeout: 2000});

			await expect(this.page).toHaveURL(
				(url) => [...url.searchParams.values()].includes(keywords),
				{timeout: 2000}
			);
		}).toPass();

		await waitForPageToBeLoaded(this.page);
	}

	async selectModalItems() {
		await this.page
			.getByRole('dialog')
			.getByRole('button', {exact: true, name: 'Select'})
			.click();

		await waitForPageToBeLoaded(this.page);
	}

	async selectTreeNode({
		frame,
		nodeName,
		search = typeof nodeName === 'string' ? nodeName : '',
	}: {
		frame: FrameLocator;
		nodeName: RegExp | string;
		search?: string;
	}) {
		await frame.getByPlaceholder('Search', {exact: true}).fill(search);

		await this.getTreeNode(frame, nodeName).getByRole('checkbox').check();
	}
}
