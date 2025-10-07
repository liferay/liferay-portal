/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

export class CommerceSpecificationsPage {
	readonly addNewProductSpecifications: Locator;
	readonly addNewProductSpecificationsGroup: Locator;
	readonly addDescriptionSpecifications: Locator;
	readonly addDescriptionSpecificationsGroup: Locator;
	readonly createNewSpecificationsProduct: Locator;
	readonly createNewSpecificationsProductGroup: Locator;
	readonly deleteModalButtonAction: (action: string) => Locator;
	readonly goBack: Locator;
	readonly goToSpecificationGroup: Locator;
	readonly keyContent: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly sidePanelSpecificationPicklistItemsFrame: FrameLocator;
	readonly specificationNameLink: (specificationName: string) => Locator;
	readonly specificationPicklistActionButton: Locator;
	readonly specificationPicklistItemsActionButton: Locator;
	readonly specificationPicklistDropdownMenuItems: (
		action: string
	) => Locator;
	readonly specificationPicklistDropdownMenu: (action: string) => Locator;
	readonly successMessage: Locator;
	readonly visibleToggle: Locator;

	constructor(page: Page) {
		this.page = page;

		this.addNewProductSpecifications = page.getByLabel(
			'Label\n\n\t\t\t\n\t\t\t\t\n\n\t\t\t\tRequired'
		);
		this.addNewProductSpecificationsGroup =
			page.getByText('Title required');
		this.addDescriptionSpecifications = page.getByLabel(
			'Characters Maximum: 4000'
		);
		this.addDescriptionSpecificationsGroup = page.getByLabel('Description');
		this.createNewSpecificationsProduct = page.getByRole('link', {
			name: 'Add Specification Label',
		});
		this.createNewSpecificationsProductGroup = page.getByRole('link', {
			name: 'Add Specification Group',
		});
		this.deleteModalButtonAction = (action: string) =>
			page.getByRole('button', {exact: true, name: action});
		this.goBack = page.locator('span[title="Back"]');
		this.goToSpecificationGroup = page.getByRole('link', {
			name: 'Specification Groups',
		});
		this.keyContent = page.getByLabel('Key Required');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.sidePanelSpecificationPicklistItemsFrame =
			page.frameLocator('iframe');
		this.specificationNameLink = (specificationName) =>
			page.getByRole('link', {exact: true, name: specificationName});
		this.specificationPicklistActionButton = page.getByRole('button', {
			exact: true,
			name: 'Actions',
		});
		this.specificationPicklistItemsActionButton =
			this.sidePanelSpecificationPicklistItemsFrame.getByRole('button', {
				exact: true,
				name: 'Actions',
			});
		this.specificationPicklistDropdownMenu = (action) =>
			page.getByRole('menuitem', {exact: true, name: action});
		this.specificationPicklistDropdownMenuItems = (action: string) =>
			this.sidePanelSpecificationPicklistItemsFrame.getByRole(
				'menuitem',
				{exact: true, name: action}
			);
		this.successMessage = page.getByText(
			'Success:Your request completed successfully.'
		);
		this.visibleToggle = page.getByLabel('Visible', {exact: true});
	}

	async waitForKey(specificationName) {
		await this.addNewProductSpecifications.fill(specificationName);
		await this.addNewProductSpecifications.waitFor();
	}
}
