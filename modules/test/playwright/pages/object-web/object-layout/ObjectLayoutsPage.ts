/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {ViewObjectDefinitionsPage} from '../ViewObjectDefinitionsPage';

export class ObjectLayoutsPage {
	readonly addCategorization: Locator;
	readonly addField: Locator;
	readonly addObjectLayoutButton: Locator;
	readonly addRegularBlock: Locator;
	readonly addSeo: Locator;
	readonly addTab: Locator;
	readonly fieldList: Locator;
	readonly fieldSelect: Locator;
	readonly headerDropdown: Locator;
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
		this.addCategorization = this.iframeLocator.getByRole('menuitem', {
			name: 'Add Categorization',
		});
		this.addField = this.iframeLocator.getByRole('button', {
			name: 'Add Field',
		});
		this.addObjectLayoutButton = page.getByLabel('Add Object Layout');
		this.addRegularBlock = this.iframeLocator.getByRole('button', {
			name: 'Add Block',
		});
		this.addSeo = this.iframeLocator.getByRole('menuitem', {
			name: 'Add SEO',
		});
		this.addTab = this.iframeLocator.getByRole('button', {name: 'Add Tab'});
		this.fieldList = this.iframeLocator.getByRole('combobox', {
			name: 'Relationship',
		});
		this.fieldSelect = this.iframeLocator.getByText('Select an Option');
		this.headerDropdown = this.iframeLocator
			.getByLabel('More Actions')
			.nth(0);
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

	async addBlock(option: 'categorization' | 'seo') {
		await this.headerDropdown.click();

		if (
			option === 'categorization' &&
			!(await this.addCategorization.isDisabled())
		) {
			await this.addCategorization.click();

			return;
		}
		else if (option === 'seo' && !(await this.addSeo.isDisabled())) {
			await this.addSeo.click();

			return;
		}

		await this.headerDropdown.click();
	}

	async addObjectLayoutObjectField(option: string) {
		await this.fieldSelect.waitFor({state: 'visible'});

		await this.iframeLocator
			.getByRole('option')
			.filter({hasText: option})
			.click();

		await this.saveAddFieldButton.click();
	}

	async createObjectLayout(objectLayoutName: string) {
		await this.addObjectLayoutButton.click();

		await this.layoutNameInput.fill(objectLayoutName);

		await this.saveAddLayoutButton.click();
	}

	async createObjectLayoutBlock({
		hasCategorizationBlock,
		hasSeoBlock,
		objectLayoutRegularBlockName,
	}: {
		hasCategorizationBlock?: boolean;
		hasSeoBlock?: boolean;
		objectLayoutRegularBlockName: string;
	}) {
		if (hasCategorizationBlock) {
			await this.addBlock('categorization');
		}

		if (hasSeoBlock) {
			await this.addBlock('seo');
		}

		await this.addRegularBlock.click();

		await this.labelInput.fill(objectLayoutRegularBlockName);

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

	async createObjectLayoutContent({
		hasCategorizationBlock,
		hasSeoBlock,
		objectFieldNames,
		objectLayoutName,
		objectLayoutRegularBlockName,
		objectLayoutTabName,
	}: {
		hasCategorizationBlock?: boolean;
		hasSeoBlock?: boolean;
		objectFieldNames: string[];
		objectLayoutName: string;
		objectLayoutRegularBlockName: string;
		objectLayoutTabName: string;
	}) {
		await this.openObjectLayoutConfiguration(objectLayoutName);

		await this.createObjectLayoutTab(objectLayoutTabName);

		await this.createObjectLayoutBlock({
			hasCategorizationBlock,
			hasSeoBlock,
			objectLayoutRegularBlockName,
		});

		for (const fieldName of objectFieldNames) {
			await this.openObjectLayoutObjectField();

			await this.addObjectLayoutObjectField(fieldName);
		}
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

	async setObjectLayoutAsDefault() {
		await this.iframeLocator.getByRole('tab', {name: 'Info'}).click();

		await this.iframeLocator.getByLabel('Mark as Default').click();
	}

	async toggleCollapsible(blockName: string) {
		const blockHeader = this.iframeLocator.locator(
			'.object-admin-panel__header',
			{hasText: blockName}
		);

		const blockToggle = blockHeader.getByRole('switch', {
			name: 'Collapsible',
		});

		await blockToggle.click();
	}
}
