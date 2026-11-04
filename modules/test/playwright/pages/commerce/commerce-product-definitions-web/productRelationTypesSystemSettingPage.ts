/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {waitForAlert} from '../../../utils/waitForAlert';
import {waitForPageToBeLoaded} from '../../../utils/waitForPageToBeLoaded';

export class ProductRelationTypesSystemSettingPage {
	readonly actionsButton: (type: string) => Locator;
	readonly addLink: Locator;
	readonly configurationLink: (type: string) => Locator;
	readonly deleteMenuItem: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly typeInput: Locator;

	constructor(page: Page) {
		this.actionsButton = (type: string) =>
			page
				.getByRole('row')
				.filter({
					has: page.getByRole('link', {exact: true, name: type}),
				})
				.getByRole('button', {name: 'Actions'});
		this.addLink = page.getByRole('link', {exact: true, name: 'Add'});
		this.configurationLink = (type: string) =>
			page.getByRole('link', {exact: true, name: type});
		this.deleteMenuItem = page
			.getByRole('menu')
			.getByRole('link', {exact: true, name: 'Delete'});
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.typeInput = page.getByRole('textbox', {exact: true, name: 'Type'});
	}

	async addType(type: string) {
		await this.goto();

		await this.addLink.click();
		await this.typeInput.fill(type);
		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async deleteType(type: string) {
		await this.goto();

		if (!(await this.configurationLink(type).isVisible())) {
			return;
		}

		this.page.once('dialog', (dialog) => dialog.accept());

		await expect(async () => {
			await this.actionsButton(type).click();

			await expect(this.deleteMenuItem).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await this.deleteMenuItem.click();

		await waitForAlert(this.page);
	}

	async goto() {
		const portletId =
			'com_liferay_configuration_admin_web_portlet_SystemSettingsPortlet';

		await this.page.goto(
			`/group/control_panel/manage?p_p_id=${portletId}` +
				`&_${portletId}_mvcRenderCommandName=` +
				'%2Fconfiguration_admin%2Fview_factory_instances' +
				`&_${portletId}_factoryPid=com.liferay.commerce.product` +
				'.configuration.CPDefinitionLinkTypeConfiguration'
		);

		await waitForPageToBeLoaded(this.page);
	}
}
