/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class CommerceAdminProductDetailsVisibilityPage {
	readonly addChannelButton: Locator;
	readonly channelsEmptyStateMessage: Locator;
	readonly deleteEntityLink: (entityName: string) => Locator;
	readonly modalSelectButton: Locator;
	readonly page: Page;
	readonly selectAccountGroupsButton: Locator;
	readonly selectAccountGroupsCheckbox: (accountGroupName: string) => Locator;
	readonly selectAccountGroupsModal: Locator;
	readonly selectAccountGroupsRow: (
		accountGroupName: string
	) => Promise<Locator>;
	readonly selectAccountGroupsTitle: Locator;
	readonly selectChannelCheckbox: (channelName: string) => Locator;
	readonly selectChannelModal: Locator;
	readonly selectChannelTitle: Locator;
	readonly sectionToggle: (sectionName: string) => Locator;
	readonly sectionToggleInput: (sectionName: string) => Locator;
	readonly visibilityEntityRow: (entityName: string) => Locator;

	constructor(page: Page) {
		this.addChannelButton = page.getByLabel('Add Channel Relation to');
		this.channelsEmptyStateMessage = page
			.locator('.card', {
				has: page.locator('.card-header.h4', {hasText: 'Channels'}),
			})
			.getByText('No Results Found');
		this.deleteEntityLink = (entityName: string) =>
			page
				.getByRole('row')
				.filter({hasText: entityName})
				.getByRole('link', {exact: true, name: 'Delete'});
		this.modalSelectButton = page
			.getByRole('dialog')
			.getByRole('button', {exact: true, name: 'Select'});
		this.page = page;

		this.sectionToggle = (sectionName: string) =>
			page
				.locator('.card-header.h4')
				.filter({hasText: sectionName})
				.locator('.toggle-switch');

		this.sectionToggleInput = (sectionName: string) =>
			page
				.locator('.card-header.h4')
				.filter({hasText: sectionName})
				.locator('input[type="checkbox"]');

		this.selectAccountGroupsButton = page.getByLabel(
			'Add Account Group Relation to'
		);
		this.selectAccountGroupsModal = page.getByRole('dialog', {
			name: 'Select Account Groups',
		});
		this.selectAccountGroupsCheckbox = (accountGroupName: string) =>
			this.selectAccountGroupsModal.getByRole('checkbox', {
				exact: true,
				name: `Select ${accountGroupName}`,
			});
		this.selectAccountGroupsRow = async (accountGroupName: string) => {
			return this.selectAccountGroupsModal.getByRole('cell', {
				exact: true,
				name: accountGroupName,
			});
		};
		this.selectAccountGroupsTitle = page.getByRole('heading', {
			name: 'Select Account Groups',
		});
		this.selectChannelModal = page.getByRole('dialog', {
			name: 'Select Channel',
		});
		this.selectChannelCheckbox = (channelName: string) =>
			this.selectChannelModal.getByRole('checkbox', {
				exact: true,
				name: `Select ${channelName}`,
			});
		this.selectChannelTitle = page.getByRole('heading', {
			exact: true,
			name: 'Select Channel',
		});
		this.visibilityEntityRow = (entityName: string) =>
			page.getByRole('row').filter({hasText: entityName});
	}
}
