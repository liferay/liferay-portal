/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';

export class CommerceAccountAddressEligibilityPage {
	readonly allChannelsRadio: Locator;
	readonly channelRemoveButton: (channelName: string) => Locator;
	readonly channelSelectButton: (channelName: string) => Locator;
	readonly findChannelInput: Locator;
	readonly page: Page;
	readonly specificChannelsRadio: Locator;

	constructor(page: Page) {
		this.allChannelsRadio = page.getByRole('radio', {
			exact: true,
			name: 'All Channels',
		});
		this.channelRemoveButton = (channelName: string) =>
			page
				.getByRole('row')
				.filter({hasText: channelName})
				.getByRole('button', {exact: true, name: 'Remove'});
		this.channelSelectButton = (channelName: string) =>
			page
				.getByRole('row')
				.filter({hasText: channelName})
				.getByRole('button', {exact: true, name: 'Select'});
		this.findChannelInput = page.getByRole('textbox', {
			exact: true,
			name: 'Find a Channel',
		});
		this.page = page;
		this.specificChannelsRadio = page.getByRole('radio', {
			exact: true,
			name: 'Specific Channels',
		});
	}

	async addChannel(channelName: string) {
		await this.specificChannelsRadio.check();

		await this.findChannelInput.fill(channelName);

		await this.channelSelectButton(channelName).click();

		await expect(this.channelRemoveButton(channelName)).toBeVisible();
	}

	async goto(accountId: number, addressId: number) {
		const portletId =
			'com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet';

		await this.page.goto(
			`/group/control_panel/manage?p_p_id=${portletId}` +
				`&_${portletId}_accountEntryAddressId=${addressId}` +
				`&_${portletId}_accountEntryId=${accountId}` +
				`&_${portletId}_mvcRenderCommandName=` +
				'%2Faccount_admin%2Fedit_account_entry_address' +
				`&_${portletId}_screenNavigationCategoryKey=qualifiers`
		);
	}

	async removeChannel(channelName: string) {
		await expect(this.channelRemoveButton(channelName)).toBeVisible();

		await clickAndExpectToBeHidden({
			target: this.channelRemoveButton(channelName),
			trigger: this.channelRemoveButton(channelName),
		});
	}
}
