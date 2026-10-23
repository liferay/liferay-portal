/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {DataSetPage} from '../../../site-cms-site-initializer/main/pages/DataSetPage';
import {EditConnectorPage} from './EditConnectorPage';

export class ConnectorsPage {
	readonly dataSetFragmentPage: DataSetPage;
	readonly deleteModal: Locator;
	readonly deleteModalCancelButton: Locator;
	readonly deleteModalConfirmButton: Locator;
	readonly deleteModalTitle: Locator;
	readonly emptyStateTitle: Locator;
	readonly filterButton: Locator;
	readonly filterMenuItem: (name: string) => Locator;
	readonly newConnectorButton: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.dataSetFragmentPage = new DataSetPage(page);
		this.deleteModal = page.locator('.modal-content');
		this.deleteModalCancelButton = this.deleteModal.getByRole('button', {
			exact: true,
			name: 'Cancel',
		});
		this.deleteModalConfirmButton = this.deleteModal.getByRole('button', {
			exact: true,
			name: 'Delete',
		});
		this.deleteModalTitle = this.deleteModal.locator('.modal-title');
		this.emptyStateTitle = page.getByText('No Connectors Yet', {
			exact: true,
		});
		this.filterButton = page.getByRole('button', {
			exact: true,
			name: 'Filter',
		});
		this.filterMenuItem = (name) =>
			page.getByRole('menuitem', {exact: true, name});
		this.newConnectorButton = page.getByTestId('fdsCreationActionButton');
		this.page = page;
	}

	async createConnector({
		active = false,
		connector,
		name,
	}: {
		active?: boolean;
		connector: string;
		name: string;
	}) {
		await this.goto();

		await this.newConnectorButton.click();

		const editConnectorPage = new EditConnectorPage(this.page);

		await editConnectorPage.createConnector({active, connector, name});

		await expect(this.getConnector(name)).toBeVisible();
	}

	async deleteConnector(name: string) {
		await this.openDeleteConfirmation(name);

		await this.deleteModalConfirmButton.click();

		await this.getConnector(name).waitFor({state: 'hidden'});
	}

	async deleteConnectorIfPresent(name: string) {
		if (await this.getConnector(name).isVisible()) {
			await this.deleteConnector(name);
		}
	}

	async executeConnector(name: string) {
		await this.dataSetFragmentPage.execItemAction({
			action: 'Execute',
			filter: name,
		});

		await waitForAlert(
			this.page,
			'Execution has started successfully and will continue in the background.'
		);
	}

	getConnector(name: string) {
		return this.dataSetFragmentPage.getRow(name).getByRole('link', {name});
	}

	getConnectorLastExecutionStatus(name: string) {
		return this.dataSetFragmentPage
			.getRow(name)
			.locator('.cell-lastExecutionStatus .label');
	}

	getConnectorLastExecutionDate(name: string) {
		return this.dataSetFragmentPage
			.getRow(name)
			.locator('.cell-lastExecutionDate');
	}

	getConnectorMappingStatus(name: string) {
		return this.dataSetFragmentPage
			.getRow(name)
			.locator('.cell-mappingStatus .label');
	}

	getConnectorStatus(name: string) {
		return this.dataSetFragmentPage
			.getRow(name)
			.locator('.cell-active .label');
	}

	async openConnectorSchedule(name: string) {
		await this.dataSetFragmentPage.execItemAction({
			action: 'Schedule',
			filter: name,
		});

		await this.page.waitForURL(/edit_dispatch_trigger/);
	}

	async openDeleteConfirmation(name: string) {
		await this.dataSetFragmentPage.execItemAction({
			action: 'Delete',
			filter: name,
		});

		await this.deleteModal.waitFor({state: 'visible'});
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.pimConnectors);

		await this.newConnectorButton.waitFor({state: 'visible'});
	}
}
