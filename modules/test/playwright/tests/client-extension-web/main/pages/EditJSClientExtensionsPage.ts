/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
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
		const addAttributeGroupButton = this.page.getByRole('button', {
			name: 'Add Attribute Group',
		});

		await addAttributeGroupButton.click();

		await this.page
			.locator(
				`#_${this.portletName}_name_${this._currentAttributeIndex}`
			)
			.fill(name);

		await this.page
			.locator(
				`#_${this.portletName}_type_${this._currentAttributeIndex}`
			)
			.click();

		const option = this.page.getByRole('option', {name: type});

		await clickAndExpectToBeHidden({target: option, trigger: option});

		const attributeValueField = this.page.locator(
			`#_${this.portletName}_value_${this._currentAttributeIndex}`
		);

		if (type === 'boolean') {
			await attributeValueField.click();

			await this.page.getByRole('option', {name: value}).click();
		}
		else {
			await attributeValueField.fill(value);
		}

		this._currentAttributeIndex++;
	}
}
