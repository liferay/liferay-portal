/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ViewObjectDefinitionsPage} from '../ViewObjectDefinitionsPage';

export class ObjectRelationshipsPage {
	readonly actionsButton: Locator;
	readonly addObjectRelationshipButton: Locator;
	readonly cancelButton: Locator;
	readonly deleteObjectRelationshipOption: Locator;
	readonly editObjectRelationshipOption: Locator;
	readonly inheritanceCheckbox: Locator;
	readonly inheritanceModalConfirmationMessage: Locator;
	readonly inheritanceModalDisableButton: Locator;
	readonly inheritanceModalHeader: Locator;
	readonly inheritanceWarningMessage: Locator;
	readonly multipleParentInheritanceErrorMessage: Locator;
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
		this.multipleParentInheritanceErrorMessage = page
			.frameLocator('iframe')
			.getByText(
				'Error:You cannot enable inheritance because there are already child entries in the regular relationship.'
			);
		this.relationshipTabItem = page.getByRole('link', {
			name: 'Relationships',
		});
		this.saveObjectRelationshipButton = page
			.frameLocator('iframe')
			.getByRole('button', {name: 'Save'});
		this.viewObjectDefinitionsPage = new ViewObjectDefinitionsPage(page);
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
}
