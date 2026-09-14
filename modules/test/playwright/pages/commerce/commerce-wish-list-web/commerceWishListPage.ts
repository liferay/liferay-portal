/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class CommerceWishListPage {
	readonly addToCartButton: Locator;
	readonly addWishListButton: Locator;
	readonly deleteWishListButton: Locator;
	readonly editWishListButton: Locator;
	readonly nameInput: Locator;
	readonly saveButton: Locator;
	readonly wishListContentPortlet: Locator;
	readonly wishListHeading: (wishListName: string) => Locator;
	readonly wishListLink: (wishListName: string) => Locator;

	constructor(page: Page) {
		this.addToCartButton = page.getByRole('button', {name: 'Add to Cart'});
		this.addWishListButton = page.getByLabel('Add Wish List');
		this.deleteWishListButton = page.getByLabel('Delete');
		this.wishListContentPortlet = page.locator(
			'.portlet-commerce-wish-list-content'
		);
		this.editWishListButton = this.wishListContentPortlet.getByRole(
			'button',
			{exact: true, name: 'Edit'}
		);
		this.nameInput = this.wishListContentPortlet.getByLabel('Name');
		this.saveButton = this.wishListContentPortlet.getByRole('button', {
			exact: true,
			name: 'Save',
		});
		this.wishListHeading = (wishListName: string) =>
			this.wishListContentPortlet.getByRole('heading', {
				exact: true,
				name: wishListName,
			});
		this.wishListLink = (wishListName: string) =>
			page.getByRole('link', {exact: true, name: wishListName});
	}
}
