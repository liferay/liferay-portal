/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {DataTablePage} from '../account-admin-web/DataTablePage';
import {GlobalMenuPage} from '../product-navigation-applications-menu/GlobalMenuPage';

export class DigitalSalesRoomsPage {
	readonly archiveButton: Locator;
	readonly archiveMenuItem: Locator;
	readonly archivedRoomWarning: Locator;
	readonly archivedStatusFilterRadio: Locator;
	readonly deleteButton: Locator;
	readonly deleteConfirmationModal: Locator;
	readonly deleteMenuItem: Locator;
	readonly digitalSalesRoomsTable: DataTablePage;
	readonly documentRow: (documentName: string) => Locator;
	readonly documentRowCheckbox: (documentName: string) => Locator;
	readonly duplicateButton: Locator;
	readonly duplicateMenuItem: Locator;
	readonly duplicateModal: Locator;
	readonly duplicateModalHeading: Locator;
	readonly editMenuItem: Locator;
	readonly globalMenuPage: GlobalMenuPage;
	readonly homeLink: Locator;
	readonly newDigitalSalesRoomButton: Locator;
	readonly noResultsFoundMessage: Locator;
	readonly page: Page;
	readonly restoreMenuItem: Locator;
	readonly roomsLink: Locator;
	readonly saveAsTemplateMenuItem: Locator;
	readonly shareMenuItem: Locator;
	readonly settingsMenuItem: Locator;
	readonly showResultsButton: Locator;
	readonly startFromScratchButton: Locator;
	readonly startFromTemplateButton: Locator;
	readonly statusFilterButton: Locator;
	readonly templatesLink: Locator;
	readonly viewMenuItem: Locator;

	constructor(page: Page) {
		this.archiveButton = page.getByRole('button', {name: 'Archive'});
		this.archiveMenuItem = page.getByRole('menuitem', {name: 'Archive'});
		this.archivedRoomWarning = page.locator('#dsr-archived-room-warning');
		this.archivedStatusFilterRadio = page.getByRole('radio', {
			name: 'Archived',
		});
		this.deleteButton = page.getByRole('button', {name: 'Delete'});
		this.deleteConfirmationModal = page.getByRole('heading', {
			name: 'Delete Digital Sales Room',
		});
		this.deleteMenuItem = page.getByRole('menuitem', {name: 'Delete'});
		this.digitalSalesRoomsTable = new DataTablePage(
			page,
			page.locator(
				'[class*="site-dsr-site-initializer-internal-fragment-renderer-viewrooms"]'
			)
		);
		this.documentRow = (documentName: string) =>
			this.duplicateModal.locator('tr', {hasText: documentName});
		this.documentRowCheckbox = (documentName: string) =>
			this.documentRow(documentName).getByRole('checkbox');
		this.duplicateButton = page
			.getByRole('dialog')
			.getByRole('button', {name: 'Duplicate'});
		this.duplicateMenuItem = page.getByRole('menuitem', {
			name: 'Duplicate',
		});
		this.duplicateModal = page.getByRole('dialog');
		this.duplicateModalHeading = page
			.getByRole('dialog')
			.getByRole('heading', {name: 'Duplicate Digital Sales Room'});
		this.editMenuItem = page.getByRole('menuitem', {name: 'Edit'});
		this.globalMenuPage = new GlobalMenuPage(page);
		this.homeLink = page.locator('css=.sidebar').getByRole('menuitem', {
			exact: true,
			name: 'Home',
		});
		this.newDigitalSalesRoomButton = page.getByText(
			'New Digital Sales Room'
		);
		this.noResultsFoundMessage = page.getByText('No Results Found');
		this.page = page;
		this.restoreMenuItem = page.getByRole('menuitem', {name: 'Restore'});
		this.roomsLink = page.getByRole('menuitem', {
			exact: true,
			name: 'Rooms',
		});
		this.saveAsTemplateMenuItem = page.getByRole('menuitem', {
			name: 'Save as Template',
		});
		this.shareMenuItem = page.getByRole('menuitem', {name: 'Share'});
		this.settingsMenuItem = page.getByRole('menuitem', {
			name: 'Room Settings',
		});
		this.showResultsButton = page.getByRole('button', {
			name: 'Show Results',
		});
		this.startFromScratchButton = page.getByRole('menuitem', {
			name: 'Start from Scratch',
		});
		this.startFromTemplateButton = page.getByRole('menuitem', {
			name: 'Start from Template',
		});
		this.statusFilterButton = page.getByRole('button', {name: 'Status:'});
		this.templatesLink = page.getByRole('link', {
			exact: true,
			name: 'Templates',
		});
		this.viewMenuItem = page.getByRole('menuitem', {name: 'View'});
	}

	async archiveRoom(roomName: string) {
		await this.clickRowActionsMenuItem(roomName, this.archiveMenuItem);

		await expect(this.archiveButton).toHaveClass(/btn-warning/);

		await this.archiveButton.click();

		await expect(this.archiveButton).toBeHidden();
	}

	async clickRowActionsMenuItem(roomName: string, menuItem: Locator) {
		await expect(async () => {
			await (
				await this.digitalSalesRoomsTable.rowActions(roomName, 0, false)
			).click();

			await menuItem.click({timeout: 1000});
		}).toPass({timeout: 10000});
	}

	async goto() {
		await this.globalMenuPage.goToHome();
		await this.globalMenuPage.goToCommerce('Digital Sales Room Management');
		await this.homeLink.click();
	}

	async goToRoomActionsMenu(roomName: string, actionsCollapsed = false) {
		await this.goToRoomsPageViaURL();

		await expect(
			this.digitalSalesRoomsTable.cell(roomName, false)
		).toBeVisible({timeout: 2000});

		if (actionsCollapsed) {
			await expect(
				await this.digitalSalesRoomsTable.rowActions(roomName, 0, false)
			).toHaveCount(0);

			await expect(
				this.digitalSalesRoomsTable.table.getByLabel('View', {
					exact: true,
				})
			).toBeVisible();

			return;
		}

		await (
			await this.digitalSalesRoomsTable.rowActions(roomName, 0, false)
		).click();
	}

	async goToRoomsPage() {
		await this.globalMenuPage.goToHome();
		await this.globalMenuPage.goToCommerce('Digital Sales Room Management');
		await this.roomsLink.click();
	}

	async goToRoomsPageAsSeller() {
		await this.page.goto('/web/dsr/home');
		await this.roomsLink.click();
	}

	async goToRoomsPageViaURL() {
		await this.page.goto('/web/dsr/rooms');
	}

	async restoreRoom(roomName: string) {
		await this.clickRowActionsMenuItem(roomName, this.restoreMenuItem);

		await expect(
			this.digitalSalesRoomsTable.cell(roomName, false)
		).toBeHidden();
	}

	roomLink(roomName: string): Locator {
		return this.digitalSalesRoomsTable
			.cell(roomName, false)
			.getByRole('link', {name: roomName});
	}

	async showArchivedRooms() {
		await expect(async () => {
			await this.statusFilterButton.click();

			await expect(this.archivedStatusFilterRadio).toBeVisible({
				timeout: 1000,
			});
		}).toPass({timeout: 10000});

		await this.archivedStatusFilterRadio.click();

		await this.showResultsButton.click();

		await expect(this.statusFilterButton).toContainText('Archived');
	}
}
