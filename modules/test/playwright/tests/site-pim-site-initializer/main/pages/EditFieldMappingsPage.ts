/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class EditFieldMappingsPage {
	readonly addRowButton: Locator;
	readonly deleteRowButton: Locator;
	readonly destinationHeading: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly source: (index: number) => Locator;
	readonly sourceAttribute: (index: number) => Locator;
	readonly value: Locator;

	constructor(page: Page) {
		this.addRowButton = page.getByRole('button', {name: 'Add Row'}).last();
		this.deleteRowButton = page
			.getByRole('button', {name: 'Delete Row'})
			.last();
		this.destinationHeading = page.getByRole('heading', {
			name: 'Destination',
		});
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.source = (index) =>
			page.getByLabel('Source', {exact: true}).nth(index);
		this.sourceAttribute = (index) =>
			page.getByLabel('Source Attribute', {exact: true}).nth(index);
		this.value = page.getByLabel('Value', {exact: true});
	}

	async mapToSourceAttribute(sourceAttributeLabel: string, index = 0) {
		await this.sourceAttribute(index).selectOption({
			label: sourceAttributeLabel,
		});
	}

	async mapToValue(value: string, index = 0) {
		await this.sourceAttribute(index).selectOption({label: 'Fixed Value'});

		await this.value.fill(value);
	}

	async unmap(index = 0) {
		await this.sourceAttribute(index).selectOption({label: 'Not Mapped'});
	}
}
