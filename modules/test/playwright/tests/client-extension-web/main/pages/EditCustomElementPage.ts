/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {EditClientExtensionsPage} from '../../pages/EditClientExtensionsPage';

export class EditCustomElementPage extends EditClientExtensionsPage {
	readonly addCSSURLButton: Locator;
	readonly addJavaScriptURLButton: Locator;
	readonly cssURLInput: Locator;
	readonly deleteCSSURLButton: Locator;
	readonly deleteJavaScriptURLButton: Locator;
	readonly friendlyURLMappingInput: Locator;
	readonly htmlElementNameInput: Locator;
	readonly instanceableCheckbox: Locator;
	readonly javaScriptURLInput: Locator;
	readonly propertiesTextArea: Locator;
	readonly useESModulesCheckbox: Locator;

	constructor(page: Page) {
		super(page, 'customElement');

		this.addCSSURLButton = page.locator(
			`#_${this.portletName}__cssURLs_field button.add-row`
		);
		this.addJavaScriptURLButton = page.locator(
			`#_${this.portletName}__urls_field button.add-row`
		);
		this.cssURLInput = page.locator(`[name=_${this.portletName}_cssURLs]`);
		this.deleteCSSURLButton = page.locator(
			`#_${this.portletName}__cssURLs_field button.delete-row`
		);
		this.deleteJavaScriptURLButton = page.locator(
			`#_${this.portletName}__urls_field button.delete-row`
		);
		this.friendlyURLMappingInput = page.locator(
			`[name=_${this.portletName}_friendlyURLMapping]`
		);
		this.htmlElementNameInput = page.locator(
			`[name=_${this.portletName}_htmlElementName]`
		);
		this.instanceableCheckbox = page.locator(
			`[name=_${this.portletName}_instanceable]`
		);
		this.javaScriptURLInput = page.locator(
			`[name=_${this.portletName}_urls]`
		);
		this.propertiesTextArea = page.locator(
			`[name=_${this.portletName}_properties]`
		);
		this.useESModulesCheckbox = page.locator(
			`[name=_${this.portletName}_useESM]`
		);
	}

	async fillRequiredFields() {
		await this.nameInput.fill('Test Element');
		await this.htmlElementNameInput.fill('test-element');
		await this.javaScriptURLInput.fill('http://sample.com/test-element.js');
	}
}
