/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {DataSetPage} from '../DataSetPage';

export class PaginationPage {
	readonly cancelButton: Locator;
	readonly dataSetPage: DataSetPage;

	readonly listOfItemsPerPageTextarea: Locator;
	readonly defaultItemsPerPageInput: Locator;
	readonly defaultValueError: Locator;
	readonly fieldItemsLimitError: Locator;
	readonly fieldItemsNumberError: Locator;
	readonly fieldRequiredError: Locator;
	readonly header: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly showPaginationToggle: Locator;
	readonly toastContainer: Locator;

	constructor(page: Page) {
		this.cancelButton = page.getByRole('button', {name: 'Cancel'});
		this.dataSetPage = new DataSetPage(page);
		this.defaultItemsPerPageInput = page.getByLabel(
			'Default Items per PageRequired'
		);
		this.defaultValueError = page.getByText(
			'The default value must exist in the list of items per page.'
		);
		this.fieldItemsNumberError = page.getByText(
			'This field contains more than 25 elements.'
		);
		this.fieldItemsLimitError = page.getByText(
			'This field contains an invalid number. Only positive numbers between 1 and 1000 are allowed.'
		);
		this.fieldRequiredError = page.getByText('This field is required.');
		this.listOfItemsPerPageTextarea = page.getByLabel(
			'List Of Items per Page'
		);
		this.page = page;
		this.header = page.getByRole('heading', {name: 'Pagination'});
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.showPaginationToggle = page.getByLabel('Show Pagination');
		this.toastContainer = page.locator('.alert-container');
	}

	async goto({dataSetLabel}: {dataSetLabel: string}) {
		await this.dataSetPage.goto({
			dataSetLabel,
		});

		await this.dataSetPage.selectTab('Pagination');
	}
}
