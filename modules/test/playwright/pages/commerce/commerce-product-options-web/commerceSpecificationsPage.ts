/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

export class CommerceSpecificationsPage {
	readonly addDescriptionSpecifications: Locator;
	readonly addDescriptionSpecificationsGroup: Locator;
	readonly createNewSpecificationsProduct: Locator;
	readonly createNewSpecificationsProductGroup: Locator;
	readonly defaultGroupCell: (entryName: string) => Locator;
	readonly deleteModalButtonAction: (action: string) => Locator;
	readonly entryRow: (entryName: string) => Locator;
	readonly entryRowActionButton: (entryName: string) => Locator;
	readonly entryRowActionMenuItem: (action: string) => Locator;
	readonly goBack: Locator;
	readonly goToSpecificationGroup: Locator;
	readonly goToSpecificationLabel: Locator;
	readonly groupTitle: Locator;
	readonly keyContent: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly sidePanelSpecificationPicklistItemsFrame: FrameLocator;
	readonly specificationLabel: Locator;
	readonly specificationNameLink: (specificationName: string) => Locator;
	readonly specificationPicklistActionButton: (
		specificationName: string
	) => Locator;
	readonly specificationPicklistItemsActionButton: (
		specificationPickListName: string
	) => Locator;
	readonly specificationPicklistDropdownMenuItems: (
		action: string
	) => Locator;
	readonly specificationPicklistDropdownMenu: (action: string) => Locator;
	readonly successMessage: Locator;
	readonly visibleToggle: Locator;

	constructor(page: Page) {
		this.page = page;

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
		this.defaultGroupCell = (entryName: string) =>
			this.entryRow(entryName).locator('.lfr-default-group-column');
		this.deleteModalButtonAction = (action: string) =>
			page.getByRole('button', {exact: true, name: action});
		this.entryRow = (entryName: string) =>
			page.getByRole('row').filter({hasText: entryName});
		this.entryRowActionButton = (entryName: string) =>
			this.entryRow(entryName).locator(
				'a.component-action.dropdown-toggle'
			);
		this.entryRowActionMenuItem = (action: string) =>
			page
				.locator('.dropdown-menu:visible')
				.getByText(action, {exact: true});
		this.goBack = page.locator('span[title="Back"]');
		this.goToSpecificationGroup = page.getByRole('link', {
			name: 'Specification Groups',
		});
		this.goToSpecificationLabel = page.getByRole('link', {
			name: 'Specification Labels',
		});
		this.groupTitle = page.getByLabel('Title Required');
		this.keyContent = page.getByLabel('Key Required');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.sidePanelSpecificationPicklistItemsFrame =
			page.frameLocator('iframe');
		this.specificationLabel = page.getByLabel('Label Required');
		this.specificationNameLink = (specificationName) =>
			page.getByRole('link', {exact: true, name: specificationName});
		this.specificationPicklistActionButton = (specificationName: string) =>
			page.getByRole('button', {
				exact: true,
				name: `${specificationName} Actions`,
			});
		this.specificationPicklistItemsActionButton = (
			specificationPickListName: string
		) =>
			this.sidePanelSpecificationPicklistItemsFrame.getByRole('button', {
				exact: true,
				name: `${specificationPickListName} Actions`,
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
		await this.specificationLabel.fill(specificationName);
		await this.specificationLabel.waitFor();
	}
}
