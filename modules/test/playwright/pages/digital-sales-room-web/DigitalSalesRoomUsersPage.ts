/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class DigitalSalesRoomUsersPage {
	readonly confirmExpirationButton: (nameOrEmail: string) => Locator;
	readonly doneButton: Locator;
	readonly editExpirationButton: (nameOrEmail: string) => Locator;
	readonly expirationLabel: (nameOrEmail: string) => Locator;
	readonly inviteButton: Locator;
	readonly inviteExpirationDateInput: Locator;
	readonly page: Page;
	readonly rowExpirationDateInput: (nameOrEmail: string) => Locator;
	readonly shareButton: Locator;
	readonly shareModal: Locator;
	readonly shareModalEmailInput: Locator;
	readonly shareModalHeading: Locator;
	readonly shareModalInviteButton: Locator;
	readonly userEmailAddressesInput: Locator;

	constructor(page: Page) {
		this.confirmExpirationButton = (nameOrEmail) =>
			this.userRow(nameOrEmail)
				.getByRole('button')
				.filter({has: page.locator('.lexicon-icon-check')});
		this.doneButton = page.getByRole('button', {name: 'Done'});
		this.editExpirationButton = (nameOrEmail) =>
			this.userRow(nameOrEmail)
				.getByRole('button')
				.filter({has: page.locator('.lexicon-icon-pencil')});
		this.expirationLabel = (nameOrEmail) =>
			this.userRow(nameOrEmail).locator('.dsr-expiration-label');
		this.inviteButton = page.locator('[data-testid="inviteButton"]');
		this.inviteExpirationDateInput = page.locator(
			'.dsr-expiration-date-picker input[type="text"]'
		);
		this.page = page;
		this.rowExpirationDateInput = (nameOrEmail) =>
			this.userRow(nameOrEmail).locator(
				'.dsr-expiration-date-picker input[type="text"]'
			);
		this.shareButton = page.getByRole('button', {name: 'share'});
		this.shareModal = page.locator('.modal-dialog');
		this.shareModalEmailInput = this.shareModal.locator(
			'[data-testid="emailAddressesInput"]'
		);
		this.shareModalHeading = this.shareModal.getByRole('heading', {
			name: 'Share',
		});
		this.shareModalInviteButton = this.shareModal.locator(
			'[data-testid="inviteButton"]'
		);
		this.userEmailAddressesInput = page.locator(
			'[data-testid="emailAddressesInput"]'
		);
	}

	removeUserButton(nameOrEmail: string): Locator {
		return this.userRow(nameOrEmail)
			.getByRole('button')
			.filter({has: this.page.locator('.lexicon-icon-trash')});
	}

	roleDropdown(nameOrEmail: string): Locator {
		return this.userRow(nameOrEmail).locator('.dropdown-toggle');
	}

	roleText(nameOrEmail: string, role: string): Locator {
		return this.userRow(nameOrEmail).getByText(role, {exact: true});
	}

	userRow(nameOrEmail: string): Locator {
		return this.page.locator('.user-row').filter({hasText: nameOrEmail});
	}
}
