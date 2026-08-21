/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {EditClientExtensionsPage} from '../../pages/EditClientExtensionsPage';

export class EditJSClientExtensionsPage extends EditClientExtensionsPage {
	readonly javaScriptURLInput: Locator;

	private _currentAttributeIndex = 0;

	constructor(page: Page) {
		super(page, 'globalJS');

		this.javaScriptURLInput = page.getByRole('textbox', {
			name: 'JavaScript URL',
		});
	}

	async addScriptAttribute(name: string, type: string, value: string) {
		const {nameField, typeField, valueField} =
			await this.addAttributeGroup();

		await nameField.fill(name);

		await this.selectOption(type, typeField);

		if (type === 'boolean') {
			await this.selectOption(value, valueField);
		}
		else {
			await valueField.fill(value);
		}
	}

	private async addAttributeGroup() {
		await this.page
			.getByRole('button', {
				name: 'Add Attribute Group',
			})
			.click();

		const index = this._currentAttributeIndex++;

		const nameField = this.page.locator(
			`#_${this.portletName}_name_${index}`
		);
		const typeField = this.page.locator(
			`#_${this.portletName}_type_${index}`
		);
		const valueField = this.page.locator(
			`#_${this.portletName}_value_${index}`
		);

		return {nameField, typeField, valueField};
	}

	private async selectOption(name: string, trigger: Locator) {
		const target = this.page.getByRole('option', {name});

		await clickAndExpectToBeVisible({target, trigger});

		if ((await target.getAttribute('aria-selected')) === 'true') {
			await clickAndExpectToBeHidden({target, trigger});
		}
		else {
			await clickAndExpectToBeHidden({target, trigger: target});
		}
	}
}
