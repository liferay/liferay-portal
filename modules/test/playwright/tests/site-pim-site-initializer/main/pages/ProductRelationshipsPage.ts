/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';

export class ProductRelationshipsPage {
	readonly addRelationshipButton: Locator;
	readonly container: Locator;
	readonly page: Page;
	readonly rows: Locator;
	readonly selectorConfirmButton: Locator;
	readonly selectorDialog: Locator;

	constructor(page: Page) {
		this.container = page.locator('.pim-product-relationships');

		this.addRelationshipButton = this.container
			.getByTestId('fdsCreationActionButton')
			.first();
		this.page = page;
		this.rows = this.container.locator('.fds table tbody tr');
		this.selectorDialog = page.getByRole('dialog');

		this.selectorConfirmButton = this.selectorDialog.getByRole('button', {
			exact: true,
			name: 'Save',
		});
	}

	async addRelationships(names: string[]) {
		await clickAndExpectToBeVisible({
			target: this.selectorConfirmButton,
			trigger: this.addRelationshipButton,
		});

		for (const name of names) {
			await this.selectorDialog
				.getByRole('checkbox', {name: `Select ${name}`})
				.check();
		}

		await this.selectorConfirmButton.click();

		await expect(this.selectorDialog).toBeHidden();

		for (const name of names) {
			await expect(this.getRelatedProduct(name)).toBeVisible();
		}
	}

	async clickItemAction(action: string, name: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: action,
			}),
			trigger: this.getRelatedProduct(name).getByRole('button', {
				name: `${name} Actions`,
			}),
		});
	}

	async removeRelationship(name: string) {
		await this.clickItemAction('Remove', name);

		await this.page
			.locator('.modal-content')
			.getByRole('button', {name: 'Remove'})
			.click();

		await expect(this.getRelatedProduct(name)).toBeHidden();
	}

	getRelatedProduct(name: string) {
		return this.rows.filter({hasText: name});
	}
}
