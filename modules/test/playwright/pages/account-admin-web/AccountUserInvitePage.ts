/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

export class AccountUserInvitePage {
	readonly addEntryButton: Locator;
	readonly clearAllButton: (entry: Locator) => Locator;
	readonly emailAddressInput: (entry: Locator) => Locator;
	readonly entries: Locator;
	readonly firstEntry: Locator;
	readonly form: Locator;
	readonly formError: (entry: Locator, message: string) => Locator;
	readonly frame: FrameLocator;
	readonly inviteButton: Locator;
	readonly lastEntry: Locator;
	readonly page: Page;
	readonly removeEntryButton: (entry: Locator) => Locator;

	constructor(page: Page) {
		this.clearAllButton = (entry) => entry.getByLabel('Clear All');
		this.emailAddressInput = (entry) =>
			entry.getByPlaceholder(
				'Type a comma or press enter to input email addresses.'
			);
		this.formError = (entry, message) => entry.getByText(message);
		this.frame = page.frameLocator(
			'iframe[id*="Portlet_inviteUsersDialog_iframe_"]'
		);
		this.inviteButton = page.getByRole('button', {
			exact: true,
			name: 'Invite',
		});
		this.page = page;
		this.removeEntryButton = (entry) => entry.getByLabel('Remove Entry');

		this.addEntryButton = this.frame.getByRole('button', {
			name: 'Add Entry',
		});
		this.entries = this.frame.locator('.sheet');
		this.form = this.frame
			.locator(
				'#_com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet_inviteUserForm'
			)
			.or(
				this.frame.locator(
					'#_com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet_inviteUserForm'
				)
			);

		this.firstEntry = this.form.locator('.sheet').first();
		this.lastEntry = this.form.locator('.sheet').last();
	}
}
