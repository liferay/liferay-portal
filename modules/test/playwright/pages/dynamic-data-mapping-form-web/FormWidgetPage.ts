/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {PageEditorPage} from '../layout-content-page-editor-web/PageEditorPage';

export class FormWidgetPage {
	readonly closeConfigurationModalButton: Locator;
	readonly configurationDropdownButton: Locator;
	readonly configurationFrame: FrameLocator;
	readonly displayingFormName: Locator;
	readonly dropdownButton: Locator;
	readonly formLabelLink: Locator;
	readonly formWidgetContainer: Locator;
	readonly javascriptLinks: Locator;
	readonly nextButton: Locator;
	readonly page: Page;
	readonly pageEditorPage: PageEditorPage;
	readonly previousButton: Locator;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.closeConfigurationModalButton = page
			.locator('.modal')
			.getByLabel('Close', {exact: true});
		this.configurationDropdownButton = page.getByRole('menuitem', {
			name: 'Configuration',
		});
		this.configurationFrame = page.frameLocator('#modalIframe');
		this.displayingFormName = this.configurationFrame.locator(
			'.displaying-form-instance-id'
		);
		this.dropdownButton = page
			.locator('#wrapper')
			.getByRole('button', {name: 'Options'});
		this.formLabelLink = page.locator(
			'tbody.table-data tr td a:has-text("Form A")'
		);
		this.formWidgetContainer = page.locator(
			'.lfr-layout-structure-item-com-liferay-dynamic-data-mapping-form-web-portlet-ddmformportlet'
		);
		this.javascriptLinks = this.configurationFrame.locator(
			'a[href^="javascript:"]'
		);
		this.nextButton = page.getByRole('button', {
			exact: true,
			name: 'Next',
		});
		this.page = page;
		this.pageEditorPage = new PageEditorPage(page);
		this.previousButton = page.getByRole('button', {
			exact: true,
			name: 'Previous',
		});
		this.saveButton = this.configurationFrame.getByText('Save');
	}

	async closeConfiguration() {
		await this.closeConfigurationModalButton.click();
	}

	getFormLink(formName: string) {
		return this.configurationFrame.getByRole('link', {name: formName});
	}

	async openConfiguration() {
		const widgetId = await this.pageEditorPage.getFragmentId('Form');

		await this.pageEditorPage.goToWidgetConfiguration(widgetId);
	}

	async saveConfiguration() {
		await this.saveButton.click();
	}

	async selectForm(formName: string) {
		await this.getFormLink(formName).click();

		await expect(this.displayingFormName).toHaveText(
			`${formName} (Modified)`
		);
	}

	async setFormWidgetConfiguration(formName: string) {
		await this.openConfiguration();

		await this.selectForm(formName);

		await this.saveConfiguration();

		await this.closeConfiguration();
	}
}
