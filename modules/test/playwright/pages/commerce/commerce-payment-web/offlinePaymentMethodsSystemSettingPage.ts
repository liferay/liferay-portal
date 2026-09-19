/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../../utils/waitForAlert';
import {waitForPageToBeLoaded} from '../../../utils/waitForPageToBeLoaded';

export class OfflinePaymentMethodsSystemSettingPage {
	readonly actionsButton: (key: string) => Locator;
	readonly addLink: Locator;
	readonly configurationLink: (key: string) => Locator;
	readonly deleteMenuItem: Locator;
	readonly keyInput: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly updateButton: Locator;

	constructor(page: Page) {
		this.actionsButton = (key: string) =>
			page
				.getByRole('row')
				.filter({
					has: page.getByRole('link', {exact: true, name: key}),
				})
				.getByRole('button', {name: 'Actions'});
		this.addLink = page.getByRole('link', {exact: true, name: 'Add'});
		this.configurationLink = (key: string) =>
			page.getByRole('link', {exact: true, name: key});
		this.deleteMenuItem = page
			.getByRole('menu')
			.getByRole('link', {exact: true, name: 'Delete'});
		this.keyInput = page.getByRole('textbox', {exact: true, name: 'Key'});
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.updateButton = page.getByRole('button', {name: 'Update'});
	}

	async goto() {
		const portletId =
			'com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet';

		await this.page.goto(
			`/group/control_panel/manage?p_p_id=${portletId}` +
				`&_${portletId}_mvcRenderCommandName=` +
				'%2Fconfiguration_admin%2Fview_factory_instances' +
				`&_${portletId}_factoryPid=com.liferay.commerce.payment` +
				'.internal.configuration.OfflineCommercePaymentMethodConfiguration'
		);

		await waitForPageToBeLoaded(this.page);
	}

	async addKey(key: string) {
		await this.addLink.click();
		await this.keyInput.fill(key);
		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async deleteKey(key: string) {
		if (!(await this.configurationLink(key).isVisible())) {
			return;
		}

		this.page.on('dialog', (dialog) => dialog.accept());

		await this.actionsButton(key).click();
		await this.deleteMenuItem.click();

		await waitForAlert(this.page);
	}

	async editKey(oldKey: string, newKey: string) {
		await this.configurationLink(oldKey).click();
		await this.keyInput.fill(newKey);
		await this.updateButton.click();

		await waitForAlert(this.page);
	}
}
