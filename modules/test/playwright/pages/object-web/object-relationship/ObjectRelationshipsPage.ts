/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {waitForPageToBeLoaded} from '../../../utils/waitForPageToBeLoaded';
import {ViewObjectDefinitionsPage} from '../ViewObjectDefinitionsPage';

export class ObjectRelationshipsPage {
	readonly actionsButton: Locator;
	readonly addObjectRelationshipButton: Locator;
	readonly cancelButton: Locator;
	readonly deleteObjectRelationshipOption: Locator;
	readonly disableInheritanceNotAllowedModalBody: Locator;
	readonly disableInheritanceNotAllowedModalDoneButton: Locator;
	readonly disableInheritanceNotAllowedModalHeader: Locator;
	readonly editObjectRelationshipOption: Locator;
	readonly inheritanceCheckbox: Locator;
	readonly inheritanceModalConfirmationMessage: Locator;
	readonly inheritanceModalDisableButton: Locator;
	readonly inheritanceModalHeader: Locator;
	readonly inheritanceWarningMessage: Locator;
	readonly labelInput: Locator;
	readonly multipleParentInheritanceErrorMessage: Locator;
	readonly page: Page;
	readonly relationshipTabItem: Locator;
	readonly saveObjectRelationshipButton: Locator;
	readonly viewObjectDefinitionsPage: ViewObjectDefinitionsPage;

	constructor(page: Page) {
		this.actionsButton = page.getByRole('button', {name: 'Actions'});
		this.addObjectRelationshipButton = page.getByLabel(
			'Add Object Relationship'
		);
		this.cancelButton = page.frameLocator('iframe').getByText('Cancel');
		this.deleteObjectRelationshipOption = page.getByRole('menuitem', {
			name: 'Delete',
		});
		this.disableInheritanceNotAllowedModalBody = page.getByText(
			'This object requires all entries to have a parent. To disable inheritance, you must first delete linked entries or enable standalone entries for this object.'
		);
		this.disableInheritanceNotAllowedModalDoneButton = page.getByRole(
			'button',
			{
				name: 'Done',
			}
		);
		this.disableInheritanceNotAllowedModalHeader = page.getByRole(
			'heading',
			{
				name: 'Disabling Inheritance Not Allowed',
			}
		);
		this.editObjectRelationshipOption = page.getByRole('menuitem', {
			name: 'Edit',
		});
		this.inheritanceCheckbox = page
			.frameLocator('iframe')
			.getByRole('checkbox');
		this.inheritanceModalConfirmationMessage = page.getByText(
			`When you disable inheritance, the regular relationship is restored. New child object entries' permissions, workflow, API structure, and application UI are defined by the child object definition.`
		);
		this.inheritanceModalDisableButton = page.getByRole('button', {
			name: 'Disable',
		});
		this.inheritanceModalHeader = page.getByRole('heading', {
			name: 'Disable Inheritance Confirmation',
		});
		this.inheritanceWarningMessage = page
			.frameLocator('iframe')
			.getByText(
				'Error:Unable to bind the object definitions when the child object definition is bound to another object definition'
			);
		this.labelInput = page
			.frameLocator('iframe')
			.getByLabel('LabelMandatory');
		this.multipleParentInheritanceErrorMessage = page
			.frameLocator('iframe')
			.getByText(
				'Error:You cannot enable inheritance because there are already child entries in the regular relationship.'
			);
		this.page = page;
		this.relationshipTabItem = page.getByRole('link', {
			name: 'Relationships',
		});
		this.saveObjectRelationshipButton = page
			.frameLocator('iframe')
			.getByRole('button', {name: 'Save'});
		this.viewObjectDefinitionsPage = new ViewObjectDefinitionsPage(page);
	}

	async deleteObjectRelationship(label: string, name: string) {
		await this.page
			.getByRole('row', {name: label})
			.getByRole('button', {name: 'Actions'})
			.click();

		await this.deleteObjectRelationshipOption.click();

		const modal = this.page.getByRole('dialog');

		await modal.getByRole('textbox').fill(name);

		const reload = this.page.waitForEvent('load', {timeout: 10000});

		await modal.getByRole('button', {exact: true, name: 'Delete'}).click();

		await reload;
	}

	async goto(objectDefinitionLabel: string, objectFolderLabel?: string) {
		await this.viewObjectDefinitionsPage.goto();

		if (objectFolderLabel) {
			await this.viewObjectDefinitionsPage.openObjectFolder(
				objectFolderLabel
			);
		}

		await this.viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			objectDefinitionLabel
		);

		await this.relationshipTabItem.click();
	}

	async saveObjectRelationship() {
		await this.saveObjectRelationshipButton.click();

		await this.saveObjectRelationshipButton.waitFor({state: 'hidden'});

		await waitForPageToBeLoaded(this.page);
	}
}
