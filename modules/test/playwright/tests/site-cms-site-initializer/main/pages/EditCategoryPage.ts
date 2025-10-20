/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';

export class EditCategoryPage {
	readonly page: Page;
	readonly saveButton: Locator;

	private readonly descriptionInput: Locator;
	private readonly editConfirmationModal: Locator;
	private readonly nameInput: Locator;
	private readonly permissionsFormGroup: Locator;
	private readonly permissionsTable: Locator;
	private readonly permissionsTableViewableByDropdown: Locator;
	private readonly propertiesTable: Locator;
	private readonly propertiesTableAddRowButton: (index: number) => Locator;
	private readonly propertiesTableDeleteRowButton: (index: number) => Locator;
	private readonly propertiesTableKeyInput: (index: number) => Locator;
	private readonly propertiesTableValueInput: (index: number) => Locator;
	private readonly saveAndAddAnotherButton: Locator;
	private readonly sidebar: Locator;

	constructor(page: Page) {
		this.page = page;

		this.descriptionInput = page.getByTestId('description-input');
		this.editConfirmationModal = page.locator('.modal-content');
		this.nameInput = page.getByTestId('name-input');

		this.permissionsFormGroup = page.getByTestId(
			'categorization-permissions-form-group'
		);
		this.permissionsTable = this.permissionsFormGroup.getByTestId(
			'categorization-permissions-table'
		);
		this.permissionsTableViewableByDropdown =
			this.permissionsTable.locator('#viewableBy');
		this.propertiesTable = page.getByTestId(
			'edit-category-properties-table'
		);
		this.propertiesTableAddRowButton = (index: number) =>
			page.getByTestId(`add-property-row-button-${index}`);
		this.propertiesTableDeleteRowButton = (index: number) =>
			page.getByTestId(`delete-property-row-button-${index}`);
		this.propertiesTableKeyInput = (index: number) =>
			page.getByTestId(`property-key-input-${index}`);
		this.propertiesTableValueInput = (index: number) =>
			page.getByTestId(`property-value-input-${index}`);
		this.saveAndAddAnotherButton = page.getByTestId(
			'save-and-add-another-button'
		);
		this.saveButton = page.getByTestId('save-button');
		this.sidebar = page.getByTestId('cms-vertical-nav');
	}

	async addPropertyRow(key?: string, value?: string) {
		await this.propertiesTable.waitFor();
		await this.propertiesTableAddRowButton(0).waitFor();

		await this.propertiesTableAddRowButton(0).click();

		await this.page.waitForLoadState();

		if (key !== undefined && value !== undefined) {
			const lastKeyInput = this.page.locator(
				"(//input[contains(@data-testid, 'key')])[last()]"
			);
			await lastKeyInput.fill(key);

			const lastValueInput = this.page.locator(
				"(//input[contains(@data-testid, 'value')])[last()]"
			);
			await lastValueInput.fill(value);
		}
	}

	async assertDefaultViewableByPermissions(roleName: string) {
		await this.permissionsTable.waitFor();
		await this.permissionsTableViewableByDropdown.waitFor();

		await expect(this.permissionsTableViewableByDropdown).toHaveValue(
			roleName
		);

		const guestUpdateCheckbox = this.permissionsTable.locator(
			'//td[@data-id="string,Guest-UPDATE"]//input[@type="checkbox"]'
		);
		const guestViewCheckbox = this.permissionsTable.locator(
			'//td[@data-id="string,Guest-VIEW"]//input[@type="checkbox"]'
		);
		const siteMemberViewCheckbox = this.permissionsTable.locator(
			'//td[@data-id="string,SiteMember-VIEW"]//input[@type="checkbox"]'
		);

		if (roleName === 'Guest') {
			await expect(guestUpdateCheckbox).toBeChecked({checked: false});
			await expect(guestViewCheckbox).toBeChecked();
			await expect(siteMemberViewCheckbox).toBeChecked();
		}
		else if (roleName === 'Site Member') {
			await expect(guestUpdateCheckbox).toBeChecked({checked: false});
			await expect(guestViewCheckbox).toBeChecked({checked: false});
			await expect(siteMemberViewCheckbox).toBeChecked();
		}
		else if (roleName === 'Owner') {
			await expect(guestUpdateCheckbox).toBeChecked({checked: false});
			await expect(guestViewCheckbox).toBeChecked({checked: false});
			await expect(siteMemberViewCheckbox).toBeChecked({checked: false});
		}

		await expect(guestUpdateCheckbox).toBeDisabled();
		await expect(guestViewCheckbox).toBeDisabled();
		await expect(siteMemberViewCheckbox).toBeDisabled();
	}
	async assertProperties(propertyRows: {key: string; value: string}[]) {
		await this.propertiesTable.waitFor();
		await this.propertiesTableKeyInput(0).waitFor();
		await this.page.waitForLoadState();

		for (let i = 0; i < propertyRows.length; i++) {
			await expect(this.propertiesTableKeyInput(i)).toHaveValue(
				propertyRows[i].key
			);
			await expect(this.propertiesTableValueInput(i)).toHaveValue(
				propertyRows[i].value
			);
		}
	}
	async clickSave() {
		await this.saveButton.waitFor();
		await this.saveButton.click();

		await this.page.waitForLoadState();
	}

	async clickSaveAndAddAnother() {
		await this.saveAndAddAnotherButton.waitFor();
		await this.saveAndAddAnotherButton.click();

		await this.page.waitForLoadState();

		await expect(this.page.getByText('Basic Info')).toBeVisible();
		await expect(this.nameInput).toBeEmpty();
		await expect(this.descriptionInput).toBeEmpty();
	}

	async clickSidebarTab(tabName: string) {
		await this.sidebar.getByText(tabName).waitFor();

		await this.sidebar.getByText(tabName).click();
	}

	async deleteNthPropertyRow(rowIndex: number) {
		if (
			rowIndex === 0 &&
			(await this.propertiesTableDeleteRowButton(0).isDisabled())
		) {
			throw new Error(
				"Can't delete the first property row if it is the only row in the table"
			);
		}
		await this.propertiesTable.waitFor();
		await this.propertiesTableDeleteRowButton(rowIndex).waitFor();

		await this.propertiesTableDeleteRowButton(rowIndex).click();
	}

	async gotoCreateCategory(vocabularyId: number | string) {
		await this.page.goto(
			PORTLET_URLS.cmsNewCategory + '?vocabularyId=' + vocabularyId
		);

		await expect(this.page.getByText('Basic Info')).toBeVisible();
	}

	async gotoEditCategory(categoryId: number | string) {
		await this.page.goto(
			PORTLET_URLS.cmsEditCategory + '?categoryId=' + categoryId
		);

		await expect(this.page.getByText('Basic Info')).toBeVisible();
	}

	async fillDescription(description: string) {
		await this.descriptionInput.waitFor();
		await this.descriptionInput.fill(description);
	}

	async fillName(name: string) {
		await this.nameInput.waitFor();
		await this.nameInput.fill(name);
	}

	async fillProperties(propertyRows: {key: string; value: string}[]) {
		await this.page.waitForLoadState();
		await this.propertiesTable.waitFor();

		for (let i = 0; i < propertyRows.length; i++) {
			if (i !== 0) {
				await this.addPropertyRow();

				try {
					await expect(this.propertiesTableKeyInput(i)).toBeVisible();
				}
				catch (error) {
					await this.addPropertyRow();
				}
			}

			await this.propertiesTableKeyInput(i).click();
			await this.propertiesTableKeyInput(i).fill(propertyRows[i].key);
			await this.propertiesTableValueInput(i).click();
			await this.propertiesTableValueInput(i).fill(propertyRows[i].value);
		}
	}

	async handleEditConfirmationModal(clickSave: boolean) {
		await expect(this.editConfirmationModal).toBeVisible();

		clickSave
			? await this.editConfirmationModal
					.getByRole('button', {name: 'Save'})
					.click()
			: await this.editConfirmationModal
					.getByRole('button', {name: 'Cancel'})
					.click();
	}

	async setViewableByPermissions(roleName: string) {
		await this.permissionsTable.waitFor();
		await this.permissionsTableViewableByDropdown.waitFor();

		await this.permissionsTableViewableByDropdown.selectOption(roleName);

		await expect(this.permissionsTableViewableByDropdown).toHaveValue(
			roleName
		);
	}

	async tickPermissionCheckbox(
		roleName: string,
		permissionName: string,
		checked: boolean = true
	) {
		await this.permissionsTable.waitFor();

		const checkbox = this.permissionsTable.locator(
			`//td[@data-id="string,${roleName.replace(' ', '')}-${permissionName.toUpperCase()}"]//input[@type="checkbox"]`
		);

		if (checked) {
			await checkbox.check();

			await expect(checkbox).toBeChecked();
		}
		else {
			await checkbox.uncheck();

			await expect(checkbox).toBeChecked({checked: false});
		}
	}
}
