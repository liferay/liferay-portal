/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';

export type TVocabularyCategory = {
	categoryNames: string[];
	vocabularyName: string;
};

export class DocumentLibraryPage {
	readonly exportImportOptionsMenuItem: Locator;
	readonly infoPanel: Locator;
	readonly infoPanelButton: Locator;
	readonly infoPanelTab: Locator;
	readonly optionsMenu: Locator;
	readonly orderMenu: Locator;
	readonly page: Page;
	readonly permissionsFrameLocator: FrameLocator;
	readonly searchButton: Locator;
	readonly searchInput: Locator;

	constructor(page: Page) {
		this.exportImportOptionsMenuItem = page.getByRole('menuitem', {
			name: 'Export / Import',
		});
		this.infoPanel = page.getByLabel('Info Panel');
		this.infoPanelButton = page.locator(
			'[id="_com_liferay_document_library_web_portlet_DLAdminPortlet_infoPanelId_trigger"]'
		);
		this.infoPanelTab = page.locator(
			'[id^=_com_liferay_document_library_web_portlet_DLAdminPortlet_tabs_]'
		);
		this.optionsMenu = page
			.getByTestId('headerOptions')
			.getByLabel('Options');
		this.orderMenu = page.getByLabel('Order');
		this.page = page;
		this.permissionsFrameLocator = page.frameLocator(
			'iframe[title="Permissions"]'
		);
		this.searchButton = page.getByRole('button', {
			name: 'Search for',
		});
		this.searchInput = page.getByRole('searchbox', {
			name: 'Search for:',
		});
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.documentLibrary}`
		);
	}

	async waitForSuccessAlert() {
		await waitForAlert(
			this.page,
			'Success:Your request completed successfully.'
		);
	}

	async assertPrivateFileIcon(parent: Page | FrameLocator = this.page) {
		await expect(async () => {
			await expect(
				await parent.getByLabel('Not Visible to Guest Users').last()
			).toBeVisible();
		}).toPass();
	}

	async openInfoPanel(entryTitle: string, tabName: 'Details' | 'Versions') {
		const infoPanelHeading = this.infoPanel.getByRole('heading', {
			name: entryTitle,
		});

		if (await infoPanelHeading.isHidden()) {
			this.infoPanelButton.click();
		}

		await infoPanelHeading.waitFor();

		const infoPanelTab = this.page.getByRole('tab', {name: tabName});

		if (
			await infoPanelTab.evaluate(
				(element) => !element.classList.contains('active')
			)
		) {
			await infoPanelTab.click();
		}
	}

	async changeTab(tabName: string) {
		await this.page.getByRole('link', {name: tabName}).click();
	}

	async changeView(viewName: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: viewName}),
			trigger: this.page.getByLabel('Select View, Currently Selected: '),
		});

		await expect(
			this.page.getByLabel(`Select View, Currently Selected: ${viewName}`)
		).toBeVisible();
	}

	async deleteAllFileEntries() {
		await this.goto();
		for (const checkbox of await this.page
			.locator('input[data-modelclassname="FileEntry"]')
			.all()) {
			await checkbox.check();
		}
		await this.page.getByRole('button', {name: 'Delete'}).click();
	}

	async clickFileEntryAction(actionName: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: actionName,
			}),
			trigger: this.page.getByRole('button', {
				name: 'Show Actions',
			}),
		});
	}

	async deleteDocumentType(name: string) {
		await this.goto();
		await this.changeTab('Document Types');

		await this.page.getByRole('row', {name}).getByTitle('Actions').click();
		this.page.once('dialog', (dialog) => {
			dialog.accept();
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('link', {name: 'Delete'}),
			trigger: this.page.getByRole('row', {name}).getByTitle('Actions'),
		});
	}

	async downloadSelectedFileEntries() {
		await this.page
			.locator('.management-bar')
			.getByRole('button', {name: 'Download'})
			.click();
	}

	async goToViewFileEntry(entryTitle: string) {
		await this.page
			.getByRole('link', {exact: true, name: entryTitle})
			.click();

		await this.page
			.getByLabel('Control Menu')
			.getByRole('heading', {name: entryTitle})
			.waitFor();
	}

	async goToViewHistoryFileEntry(entryTitle: string) {
		await this.page
			.getByRole('link', {exact: true, name: entryTitle})
			.click();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'View History',
			}),
			trigger: this.page.getByRole('button', {name: 'Show Actions'}),
		});
	}

	async goToShareFileEntry(entryTitle: string) {
		await this.page
			.getByRole('link', {exact: true, name: entryTitle})
			.click();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Share',
			}),
			trigger: this.page.getByRole('button', {name: 'Show Actions'}),
		});
	}

	async goToEditFileEntry(entryTitle: string) {
		await this.page
			.getByRole('link', {exact: true, name: entryTitle})
			.click();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Edit',
			}),
			trigger: this.page.getByRole('button', {name: 'Show Actions'}),
		});
	}

	async goToEditFolder(entryTitle: string) {
		await this.page
			.locator(`.card-body:has-text('${entryTitle}')`)
			.getByLabel('More actions')
			.click();
		await this.page.getByRole('menuitem', {name: 'Edit'}).click();
	}

	async goToCreateNewFile() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'File Upload'}),
			trigger: this.page.getByRole('button', {exact: true, name: 'New'}),
		});
	}

	async goToCreateNewFileWithDifferentType(type: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: type}),
			trigger: this.page.getByRole('button', {exact: true, name: 'New'}),
		});
	}

	async goToCreateNewFolder() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Folder'}),
			trigger: this.page.getByRole('button', {exact: true, name: 'New'}),
		});
	}

	async goToFileEntryAction(action: string, entryTitle: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: action,
			}),
			trigger: this.page
				.locator(`.card-body:has-text('${entryTitle}')`)
				.getByLabel('Actions'),
		});
	}

	async goToFolderAction(action: string, entryTitle: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: action,
			}),
			trigger: this.page
				.locator(`.card-body:has-text('${entryTitle}')`)
				.getByLabel('More actions'),
		});
	}

	async assertFileEntryAction(action: string, entryTitle: string) {
		await clickAndExpectToBeVisible({
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: action,
			}),
			trigger: this.page
				.locator(`.card-body:has-text('${entryTitle}')`)
				.getByLabel('Actions'),
		});
	}

	async openBulkEditCategoriesModal(titles: string[]) {
		await this.selectFileEntries(titles);
		await this.page.getByRole('button', {name: 'Edit Categories'}).click();
		await this.page
			.getByRole('heading', {name: 'Edit Categories'})
			.waitFor();
	}

	async openCreateAIImage() {
		await this.openNewButton();

		await this.page
			.getByRole('menuitem', {
				name: 'Create AI Image',
			})
			.click();
	}

	async openNewButton() {
		await this.page.getByRole('button', {exact: true, name: 'New'}).click();
	}

	async openNewDLTypeButton() {
		await this.page.getByRole('link', {name: 'New Document Type'}).click();
	}

	async openOptionsMenu() {
		await this.optionsMenu
			.and(this.page.locator('[aria-haspopup]'))
			.click();
	}

	async orderBy(name: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name}),
			trigger: this.orderMenu,
		});
	}

	async replaceCategoriesUsingBulkEditCategoriesModal(
		fileNames: string[],
		vocabularyCategories: TVocabularyCategory[]
	) {
		await this.openBulkEditCategoriesModal(fileNames);
		await this.page.getByLabel('ReplaceThese categories').check();
		for (const vocabularyCategory of vocabularyCategories) {
			for (const categoryName of vocabularyCategory.categoryNames) {
				await this.page
					.getByLabel(vocabularyCategory.vocabularyName, {
						exact: true,
					})
					.fill(categoryName);
				await this.page
					.getByRole('option', {name: categoryName})
					.click();
			}
		}
		await this.page.getByRole('button', {name: 'Save'}).click();
	}

	async search(entryTitle: string) {
		const dlPortlet = this.page.locator('.portlet-document-library');

		await dlPortlet.getByPlaceholder('Search for').first().fill(entryTitle);
		await dlPortlet.getByPlaceholder('Search for').first().press('Enter');
	}

	async searchInDL(query: string) {
		await this.searchInput.fill(query);
		await this.searchButton.click();
	}

	async selectFileEntries(entryTitles: string[]) {
		for (const entryTitle of entryTitles) {
			await this.selectFileEntry(entryTitle);
		}
	}

	async selectFileEntry(entryTitle: string) {
		const fileEntryCheckbox = this.page
			.locator(`.card:has-text('${entryTitle}')`)
			.getByRole('checkbox');

		if (await fileEntryCheckbox.isHidden()) {
			await this.search(entryTitle);

			await expect(fileEntryCheckbox).toBeVisible();
		}

		await fileEntryCheckbox.check();
	}

	async selectFolder(folderName: string) {
		const folderCheckbox = this.page
			.locator(`label:has-text('${folderName}')`)
			.getByRole('checkbox');

		if (await folderCheckbox.isHidden()) {
			await this.search(folderName);

			await expect(folderCheckbox).toBeVisible();
		}

		await folderCheckbox.check();
	}

	async assertFileEntryPermissions(
		permissions: {enabled: boolean; locator: string}[],
		title: string
	) {
		await this.goToFileEntryAction('Permissions', title);

		await this.permissionsFrameLocator
			.locator(permissions[0].locator)
			.waitFor();

		for (const permission of permissions) {
			const permissionCheckbox = this.permissionsFrameLocator.locator(
				permission.locator
			);

			if (permission.enabled) {
				await expect(permissionCheckbox).toBeChecked();
			}
			else {
				await expect(permissionCheckbox).not.toBeChecked();
			}
		}

		await this.permissionsFrameLocator
			.getByRole('button', {name: 'Cancel'})
			.click();
	}
}
