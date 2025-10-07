/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {liferayConfig} from '../../../../liferay.config';
import POM from '../../../../utils/POM';
import {EditClientExtensionsPage} from './EditClientExtensionsPage';
import {ViewClientExtensionPage} from './ViewClientExtensionPage';

const PORTLET_NAME =
	'com_liferay_client_extension_web_internal_portlet_ClientExtensionAdminPortlet';

const PORTLET_URL =
	`${liferayConfig.environment.baseUrl}/group/guest/~/control_panel/manage` +
	`?p_p_id=${PORTLET_NAME}`;

export enum Column {
	NAME = 0,
	TYPE = 1,
	STATUS = 2,
	CONFIGURED_FROM = 3,
	MODIFIED_DATE = 4,
}

export class ClientExtensionsPage extends POM {
	readonly addNewClientExtensionButton: Locator;
	readonly configuredFromTableHeader: Locator;
	readonly deleteMenuItem: Locator;
	readonly editMenuItem: Locator;
	readonly nameTableHeader: Locator;
	readonly portletName = PORTLET_NAME;
	readonly viewMenuItem: Locator;

	constructor(page: Page) {
		super(page, PORTLET_URL);

		this.addNewClientExtensionButton = page.getByTitle('New');
		this.configuredFromTableHeader = page.getByLabel('Configured From', {
			exact: true,
		});
		this.deleteMenuItem = page.getByRole('menuitem', {
			name: 'Delete',
		});
		this.editMenuItem = page.getByRole('menuitem', {
			name: 'Edit',
		});
		this.nameTableHeader = page.getByLabel('Name', {exact: true});
		this.viewMenuItem = page.getByRole('menuitem', {
			name: 'View',
		});
	}

	async deleteClientExtension(clientExtensionName: string) {
		await this._openItemActionsDropdown(clientExtensionName);

		this.page.on('dialog', (dialog) => dialog.accept());

		await this.deleteMenuItem.click();
	}

	async editClientExtension<T extends EditClientExtensionsPage>(
		clientExtensionName: string,
		editClientExtensionPageType?: new (page: Page) => T
	): Promise<T> {
		await this._openItemActionsDropdown(clientExtensionName);

		await this.editMenuItem.click();

		if (editClientExtensionPageType) {
			const editClientExtensionPage = new editClientExtensionPageType(
				this.page
			);

			await editClientExtensionPage.waitFor();

			return editClientExtensionPage;
		}

		await new EditClientExtensionsPage(this.page, null).waitFor();
	}

	getCellByText(column: Column, text: string) {
		return this.getRowByText(text).locator('td').nth(column);
	}

	getRowByText(text: string) {
		return this.page.locator('.fds tbody tr').filter({
			has: this.page.getByText(text, {exact: true}).first(),
		});
	}

	async search(query: string) {
		await this.page.getByPlaceholder('Search').fill(query);
		await this.page.getByRole('button', {name: 'Search'}).click();
	}

	async viewClientExtension(
		clientExtensionName: string
	): Promise<ViewClientExtensionPage> {
		await this._openItemActionsDropdown(clientExtensionName);

		// The ERC is inside the data-id attribute of each dataset TD

		const dataId = await this.page
			.locator('.fds tr')
			.filter({has: this.page.getByText(clientExtensionName)})
			.locator('.cell-name')

			// eslint-disable-next-line @liferay/no-get-data-attribute
			.getAttribute('data-id');

		const erc = dataId.replace('string,', '').replace(':name', '');

		// Now go to the view client extension page

		await this.viewMenuItem.click();

		const viewClientExtensionPage = new ViewClientExtensionPage(
			this.page,
			erc
		);

		await viewClientExtensionPage.waitFor();

		return viewClientExtensionPage;
	}

	override async waitFor() {
		await this.page
			.locator('.data-set-content-wrapper')
			.waitFor({state: 'visible'});
	}

	private async _openItemActionsDropdown(clientExtensionName: string) {
		await this.page
			.locator('.fds tr')
			.filter({has: this.page.getByText(clientExtensionName)})
			.getByRole('button', {
				name: 'Actions',
			})
			.click();
	}
}
