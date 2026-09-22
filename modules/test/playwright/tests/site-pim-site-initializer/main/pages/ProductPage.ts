/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

function getObjectFieldInput(objectFieldName: string, page: Page) {
	return page.locator(`[name="ObjectField_${objectFieldName}"]`);
}

export class ProductPage {
	readonly code: Locator;
	readonly depth: Locator;
	readonly descriptionField: Locator;
	readonly dimensions: Locator;
	readonly height: Locator;
	readonly name: Locator;
	readonly page: Page;
	readonly tabs: Locator;
	readonly unitOfMeasureAllowDecimalQuantities: Locator;
	readonly unitOfMeasureKey: Locator;
	readonly unitOfMeasureName: Locator;
	readonly unitOfMeasureSymbol: Locator;
	readonly virtual: Locator;
	readonly weight: Locator;
	readonly width: Locator;

	constructor(page: Page) {
		this.code = getObjectFieldInput('code', page);
		this.depth = getObjectFieldInput('depth', page);
		this.descriptionField = page
			.locator('.cms-object-layout-form')
			.getByText('Description', {exact: true});
		this.dimensions = page
			.locator('.cms-object-layout-form .panel-title')
			.filter({hasText: 'Dimensions'});
		this.height = getObjectFieldInput('height', page);
		this.name = getObjectFieldInput('name', page);
		this.page = page;
		this.tabs = page
			.locator('.cms-object-layout-form .component-tabs')
			.getByRole('tab');
		this.unitOfMeasureAllowDecimalQuantities = getObjectFieldInput(
			'unitOfMeasureAllowDecimalQuantities',
			page
		);
		this.unitOfMeasureKey = getObjectFieldInput('unitOfMeasureKey', page);
		this.unitOfMeasureName = getObjectFieldInput('unitOfMeasureName', page);
		this.unitOfMeasureSymbol = getObjectFieldInput(
			'unitOfMeasureSymbol',
			page
		);
		this.virtual = getObjectFieldInput('virtual', page);
		this.weight = getObjectFieldInput('weight', page);
		this.width = getObjectFieldInput('width', page);
	}

	getTab(name: string) {
		return this.tabs.filter({hasText: name});
	}
}
