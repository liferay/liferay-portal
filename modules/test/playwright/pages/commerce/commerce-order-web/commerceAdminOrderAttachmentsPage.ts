/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {CommerceDNDTablePage} from '../commerceDNDTablePage';

export class CommerceAdminOrderAttachmentsPage extends CommerceDNDTablePage {
	readonly addAttachmentMenuItem: Locator;
	readonly attachmentsTab: Locator;
	readonly deleteConfirmButton: Locator;
	readonly deleteRowAction: Locator;
	readonly downloadRowAction: Locator;
	readonly editRowAction: Locator;
	readonly page: Page;
	readonly rowActionsButton: (rowValue: string) => Locator;
	readonly rowByTitle: (title: string) => Locator;
	readonly rowRestrictedIcon: (title: string) => Locator;
	readonly rowTitleLink: (title: string) => Locator;
	readonly sidePanelCancelButton: Locator;
	readonly sidePanelFrame: FrameLocator;
	readonly sidePanelPriorityInput: Locator;
	readonly sidePanelRestrictedCheckbox: Locator;
	readonly sidePanelSaveButton: Locator;
	readonly sidePanelSelectFileButton: Locator;
	readonly sidePanelTitleInput: Locator;
	readonly sidePanelTypeSelect: Locator;

	constructor(page: Page) {
		super(
			page,
			'#_com_liferay_commerce_order_web_internal_portlet_CommerceOrderPortlet_editOrderContainer .fds table'
		);

		this.attachmentsTab = page.getByRole('link', {
			exact: true,
			name: 'Attachments',
		});
		this.addAttachmentMenuItem = page
			.getByTestId('managementToolbar')
			.locator('[data-testid="fdsCreationActionButton"]');
		this.deleteConfirmButton = page
			.getByRole('dialog')
			.getByRole('button', {exact: true, name: 'Delete'});
		this.deleteRowAction = page.getByRole('menuitem', {
			exact: true,
			name: 'Delete',
		});
		this.downloadRowAction = page.getByRole('menuitem', {
			exact: true,
			name: 'Download',
		});
		this.editRowAction = page.getByRole('menuitem', {
			exact: true,
			name: 'Edit',
		});
		this.page = page;
		this.rowActionsButton = (rowValue: string) =>
			page
				.getByRole('row', {name: rowValue})
				.getByRole('button', {name: 'Actions'});
		this.rowByTitle = (title: string) =>
			page.getByRole('row', {name: title});
		this.rowRestrictedIcon = (title: string) =>
			this.rowByTitle(title).getByRole('img', {name: 'Restricted'});
		this.rowTitleLink = (title: string) =>
			this.rowByTitle(title).getByRole('link', {name: title});
		this.sidePanelFrame = page.frameLocator('.fds-side-panel iframe');
		this.sidePanelCancelButton = this.sidePanelFrame.getByRole('button', {
			name: 'Cancel',
		});
		this.sidePanelPriorityInput =
			this.sidePanelFrame.getByLabel('Priority');
		this.sidePanelRestrictedCheckbox =
			this.sidePanelFrame.getByLabel('Restricted');
		this.sidePanelSaveButton = this.sidePanelFrame.getByRole('button', {
			name: 'Save',
		});
		this.sidePanelSelectFileButton = this.sidePanelFrame.getByRole(
			'button',
			{name: 'Select File'}
		);
		this.sidePanelTitleInput = this.sidePanelFrame.getByLabel('Title');
		this.sidePanelTypeSelect = this.sidePanelFrame.getByLabel('Type');
	}

	async openEditSidePanel(title: string) {
		await expect(async () => {
			if (!(await this.sidePanelTitleInput.isVisible())) {
				await this.rowActionsButton(title).click();

				await expect(this.editRowAction).toBeVisible({
					timeout: 2000,
				});

				await this.editRowAction.click({timeout: 2000});
			}

			await expect(this.sidePanelTitleInput).toBeVisible({
				timeout: 5000,
			});
		}).toPass({timeout: 20000});
	}
}
