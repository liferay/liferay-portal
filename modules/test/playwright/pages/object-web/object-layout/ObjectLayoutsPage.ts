/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {ViewObjectDefinitionsPage} from '../ViewObjectDefinitionsPage';

export class ObjectLayoutsPage {
	readonly addBlock: Locator;
	readonly addField: Locator;
	readonly addObjectLayoutButton: Locator;
	readonly addTab: Locator;
	readonly fieldList: Locator;
	readonly fieldSelect: Locator;
	readonly iframeLocator: FrameLocator;
	readonly labelInput: Locator;
	readonly layoutNameInput: Locator;
	readonly layoutsTabItem: Locator;
	readonly layoutTab: Locator;
	readonly markAsDefaultButton: Locator;
	readonly page: Page;
	readonly relationshipSelect: Locator;
	readonly relationshipType: Locator;
	readonly saveAddFieldButton: Locator;
	readonly saveAddLayoutButton: Locator;
	readonly saveBlockButton: Locator;
	readonly saveTabButton: Locator;
	readonly saveUpdateLayoutButton: Locator;
	readonly viewObjectDefinitionsPage: ViewObjectDefinitionsPage;

	constructor(page: Page) {
		this.iframeLocator = page.frameLocator('iframe');
		this.addBlock = this.iframeLocator.getByRole('button', {
			name: 'Add Block',
		});
		this.addField = this.iframeLocator.getByRole('button', {
			name: 'Add Field',
		});
		this.addObjectLayoutButton = page.getByLabel('Add Object Layout');
		this.addTab = this.iframeLocator.getByRole('button', {name: 'Add Tab'});
		this.fieldList = this.iframeLocator.getByRole('combobox', {
			name: 'Relationship',
		});
		this.fieldSelect = this.iframeLocator.getByText('Select an Option');
		this.labelInput = this.iframeLocator.getByLabel('Label');
		this.layoutNameInput = page.getByLabel('Name');
		this.layoutsTabItem = page.getByRole('link', {name: 'Layouts'});
		this.layoutTab = this.iframeLocator.getByRole('tab', {name: 'Layout'});
		this.markAsDefaultButton =
			this.iframeLocator.getByLabel('Mark as Default');
		this.page = page;
		this.relationshipSelect = this.iframeLocator.getByLabel('Relationship');
		this.relationshipType = this.iframeLocator.getByText('Relationships', {
			exact: true,
		});
		this.saveAddLayoutButton = page.getByRole('button', {name: 'Save'});
		this.saveAddFieldButton = this.iframeLocator
			.getByLabel('Add Field')
			.getByRole('button', {name: 'Save'});
		this.saveBlockButton = this.iframeLocator
			.getByLabel('Add Block')
			.getByRole('button', {name: 'Save'});
		this.saveTabButton = this.iframeLocator
			.getByLabel('Add Tab')
			.getByRole('button', {name: 'Save'});
		this.saveUpdateLayoutButton = this.iframeLocator
			.locator(
				'.lfr-objects__side-panel-content-container.btn-group-spaced'
			)
			.getByRole('button', {name: 'Save'});
		this.viewObjectDefinitionsPage = new ViewObjectDefinitionsPage(page);
	}

	async createObjectLayout(objectLayoutName: string) {
		await this.addObjectLayoutButton.click();
		await this.layoutNameInput.fill(objectLayoutName);
		await this.saveAddLayoutButton.click();
	}

	async createObjectLayoutBlock(objectLayoutBlockName: string) {
		await this.addBlock.click();
		await this.labelInput.fill(objectLayoutBlockName);
		await this.saveBlockButton.click();
	}

	async createObjectLayoutTab(objectLayoutTabName: string) {
		await this.addTab.click();
		await this.labelInput.fill(objectLayoutTabName);
		await this.saveTabButton.click();
	}

	async createObjectRelationshipTab(
		objectLayoutName: string,
		objectLayoutTabName: string,
		relationshipField: string
	) {
		await this.openObjectLayoutConfiguration(objectLayoutName);
		await this.addTab.click();
		await this.labelInput.fill(objectLayoutTabName);
		await this.relationshipType.click();
		await this.fieldList.click();
		await this.iframeLocator
			.getByRole('option', {name: relationshipField})
			.click();
		await this.saveTabButton.click();
		await this.saveUpdateLayoutButton.click();
	}

	async createObjectLayoutContent(
		objectLayoutBlockName: string,
		objectLayoutName: string,
		objectLayoutTabName: string
	) {
		await this.openObjectLayoutConfiguration(objectLayoutName);
		await this.createObjectLayoutTab(objectLayoutTabName);
		await this.createObjectLayoutBlock(objectLayoutBlockName);
		await this.openObjectLayoutObjectField();
	}

	async goto(objectDefinitionLabel: string) {
		await this.viewObjectDefinitionsPage.goto();

		await this.viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			objectDefinitionLabel
		);

		await this.layoutsTabItem.click();
	}

	async openObjectLayoutConfiguration(objectLayoutName: string) {
		await this.page.getByRole('link', {name: objectLayoutName}).click();
		await this.layoutTab.click();
	}

	async openObjectLayoutObjectField() {
		await this.addField.click();
		await this.fieldSelect.click();
	}
}
