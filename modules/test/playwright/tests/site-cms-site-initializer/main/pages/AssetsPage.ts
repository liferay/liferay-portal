/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {DataSetPage} from './DataSetPage';

// Page for All, Content and Files page

export class AssetsPage {
	readonly page: Page;

	readonly dataSetFragmentPage: DataSetPage;
	readonly modalDeleteButton: Locator;
	readonly newButton: Locator;
	readonly processingTasksButton: Locator;
	readonly table: {
		bodyRows: Locator;
		container: Locator;

		headRow: Locator;
	};
	readonly taskStatusButton: (buttonName: string) => Locator;
	readonly taskStatusDropdownItemButton: (taskName: string) => Locator;
	readonly taskStatusDropdownList: Locator;
	readonly taskStatusFormsButton: Locator;
	readonly viewAllTasksLink: Locator;

	readonly modalContainer: Locator;
	readonly modal: {
		body: Locator;
		container: Locator;
		footer: Locator;
		title: Locator;
	};

	constructor(page: Page) {
		this.page = page;

		this.dataSetFragmentPage = new DataSetPage(page);
		this.newButton = page.getByLabel('New');
		this.processingTasksButton = page.getByRole('button', {
			name: /Processing Tasks?/,
		}) as Locator;
		this.taskStatusButton = (buttonName: string) => {
			return page.getByRole('button', {exact: true, name: buttonName});
		};
		this.taskStatusDropdownItemButton = (taskName: string) => {
			return page.getByRole('button', {name: taskName});
		};
		this.taskStatusDropdownList = page.locator('ul.task-status');
		this.taskStatusFormsButton = page
			.locator('li.tbar-item')
			.locator('svg.lexicon-icon-forms');
		this.viewAllTasksLink = page.getByRole('link', {
			exact: true,
			name: 'View All Tasks',
		});

		this.table = this.dataSetFragmentPage.table;

		const modalContainer = page.locator('.modal-dialog');

		this.modal = {
			body: modalContainer.locator('.modal-body'),
			container: modalContainer,
			footer: modalContainer.locator('.modal-footer'),
			title: modalContainer.locator('.modal-title'),
		};

		this.modalDeleteButton = this.modal.footer.getByRole('button', {
			exact: true,
			name: 'Delete',
		});
	}

	async gotoAll() {
		await this.page.goto(PORTLET_URLS.cmsAll);
		await this.page
			.getByRole('heading', {name: 'All Restricted Page'})
			.waitFor();
	}

	async gotoFiles() {
		await this.page.goto(PORTLET_URLS.cmsFiles);
		await this.page
			.getByRole('heading', {name: 'Files Restricted Page'})
			.waitFor();
	}

	async createContent(type: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: type}),
			trigger: this.newButton,
		});
	}

	getItem(filter: string) {
		return this.dataSetFragmentPage.getRow(filter);
	}

	async execBulkItemAction(action: string) {
		await this.dataSetFragmentPage.execBulkItemAction({action});
	}

	async execItemAction({
		action,
		filter,
	}: {
		action: 'Download' | 'Share' | 'Show Details' | 'View';
		filter: string;
	}) {
		await this.dataSetFragmentPage.execItemAction({
			action,
			filter,
		});
	}

	async changeVisualizationMode(
		...args: Parameters<DataSetPage['changeVisualizationMode']>
	) {
		await this.dataSetFragmentPage.changeVisualizationMode(...args);
	}

	async selectItems(titles: string[]) {
		for (const title of titles) {
			const card = this.page
				.locator('tr', {hasText: title})
				.or(this.page.locator('.card-row', {hasText: title}));

			await card.getByRole('checkbox').check();
		}
	}
}
