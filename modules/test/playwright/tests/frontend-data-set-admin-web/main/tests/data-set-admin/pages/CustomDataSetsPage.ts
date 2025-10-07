/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {ApiHelpers} from '../../../../../../helpers/ApiHelpers';
import {ApplicationsMenuPage} from '../../../../../../pages/product-navigation-applications-menu/ApplicationsMenuPage';
import {waitForAlert} from '../../../../../../utils/waitForAlert';
import {API_ENDPOINT_PATH, DEFAULT_LABEL} from '../../../utils/constants';

export class CustomDataSetsPage {
	readonly apiHelpers: ApiHelpers;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly basePath: string;
	readonly customDataSetsTab: Locator;
	readonly dataSetDeleteButton: Locator;
	readonly dataSetDeleteMenuItem: Locator;
	readonly dataSetEditButton: Locator;
	readonly dataSetEditMenuItem: Locator;
	readonly dataSetPermissionsButton: Locator;
	readonly dataSetPermissionsMenuItem: Locator;
	readonly dataSetsEmptyState: Locator;
	readonly dataSetsTabs: Locator;
	readonly newDataSetButton: Locator;
	readonly newDataSetModal: {
		readonly cancel: Locator;
		readonly heading: Locator;
		readonly nameInput: Locator;
		readonly restApplicationField: Locator;
		readonly restApplicationOptions: Locator;
		readonly restEndpointField: Locator;
		readonly restEndpointOptions: Locator;
		readonly restSchemaField: Locator;
		readonly restSchemaOptions: Locator;
		readonly saveButton: Locator;
	};
	readonly page: Page;
	private readonly pageContainer: Locator;
	readonly permissionsModal: FrameLocator;
	readonly systemDataSetsTab: Locator;
	readonly table: {
		bodyRows: Locator;
		container: Locator;
		headRow: Locator;
	};

	constructor(page: Page) {
		this.apiHelpers = new ApiHelpers(page);
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.basePath = 'data-set-admin/entries';
		this.customDataSetsTab = page
			.locator('.nav-item')
			.filter({hasText: 'Custom Data Sets'});
		this.dataSetDeleteButton = page.getByRole('button', {
			exact: true,
			name: 'Delete',
		});
		this.dataSetDeleteMenuItem = page.getByRole('menuitem', {
			name: 'Delete',
		});
		this.dataSetEditButton = page.getByRole('button', {
			exact: true,
			name: 'Edit',
		});
		this.dataSetEditMenuItem = page.getByRole('menuitem', {name: 'Edit'});
		this.dataSetPermissionsButton = page.getByRole('button', {
			exact: true,
			name: 'Permissions',
		});
		this.dataSetPermissionsMenuItem = page.getByRole('menuitem', {
			name: 'Permissions',
		});
		this.dataSetsEmptyState = page.locator('.c-empty-state');
		this.dataSetsTabs = page
			.locator('.navbar-nav')
			.filter({hasText: 'Custom Data SetsSystem Data Sets'});
		this.newDataSetButton = page.getByLabel('New Data Set').first();
		this.newDataSetModal = {
			cancel: page.getByRole('button', {name: 'Cancel'}),
			heading: page.getByRole('heading', {name: 'New Data Set'}),
			nameInput: page.getByLabel('NameRequired'),
			restApplicationField: page.getByLabel('REST ApplicationRequired'),
			restApplicationOptions: page
				.locator('.fds-entries-dropdown-menu')
				.first(),
			restEndpointField: page.getByLabel('REST EndpointRequired'),
			restEndpointOptions: page
				.locator('.fds-entries-dropdown-menu')
				.locator('nth=2'),
			restSchemaField: page.getByLabel('REST SchemaRequired'),
			restSchemaOptions: page
				.locator('.fds-entries-dropdown-menu')
				.locator('nth=1'),
			saveButton: page.getByRole('button', {name: 'Save'}),
		};
		this.page = page;
		this.pageContainer = page.locator('.data-sets');
		this.permissionsModal = page.frameLocator(
			'iframe[title="Permissions"]'
		);
		this.systemDataSetsTab = page
			.locator('.nav-item')
			.filter({hasText: 'System Data Sets'});

		const tableContainer = page.locator('.fds table');

		this.table = {
			bodyRows: tableContainer.locator('tbody tr'),
			container: tableContainer,
			headRow: tableContainer.locator('thead tr'),
		};
	}

	async createDataSet({
		name = DEFAULT_LABEL.DATA_SET,
		restApplication = '/data-set-admin/table-sections',
		restEndpoint = '/',
		restSchema = 'DataSetTableSection',
	}: {
		name?: string;
		restApplication?: string;
		restEndpoint?: string;
		restSchema?: string;
	} = {}) {
		const modal = this.newDataSetModal;

		await this.newDataSetButton.click();

		await modal.nameInput.fill(name);

		await modal.restApplicationField.click();

		await modal.restApplicationOptions
			.getByRole('option', {name: restApplication})
			.click();

		await modal.restSchemaField.click();

		await this.page.getByRole('textbox', {name: 'Search'}).fill(restSchema);

		await modal.restSchemaOptions
			.getByRole('option', {exact: true, name: restSchema})
			.click();

		await this.page
			.locator('div')
			.filter({hasText: /^SaveCancel$/})
			.first()
			.click();

		await modal.restEndpointField.click();

		await this.page
			.getByRole('textbox', {name: 'Search'})
			.fill(restEndpoint);

		await modal.restEndpointOptions
			.getByRole('option', {name: restEndpoint})
			.click();

		await modal.restEndpointField.click();

		await modal.saveButton.click();

		await waitForAlert(this.page);
	}

	async goto({
		checkTabVisibility,
		dataSetsType = 'Custom Data Sets',
	}: {
		checkTabVisibility?: boolean;
		dataSetsType?: string;
	} = {}) {
		await this.applicationsMenuPage.goToDataSetManager(checkTabVisibility);

		if (dataSetsType === 'System Data Sets') {
			await this.systemDataSetsTab.click();
			await this.page.locator('.system-data-sets').waitFor();
		}
		else {
			await this.pageContainer.waitFor();
		}
	}

	async openDataSet(name = DEFAULT_LABEL.DATA_SET) {
		await this.pageContainer.waitFor();

		await this.pageContainer.getByRole('link', {name}).first().click();
	}

	async deleteDataSet(name = DEFAULT_LABEL.DATA_SET) {
		await this.goto();

		const datasetTestRow = await this.table.bodyRows.filter({
			hasText: name,
		});

		await datasetTestRow
			.first()
			.getByRole('button', {name: 'Actions'})
			.click();

		await this.page.getByRole('menuitem', {name: 'Delete'}).click();

		const deleteModal = await this.page.getByRole('dialog');

		await deleteModal.getByRole('button', {name: 'Delete'}).click();
	}

	async sortBy(columnName: string) {
		await Promise.all([
			this.table.headRow.locator('th', {hasText: columnName}).click(),

			this.page.waitForResponse(
				(response: any) =>
					response.status() === 200 &&
					response.url().includes(`${API_ENDPOINT_PATH}?`)
			),
		]);
	}
}
