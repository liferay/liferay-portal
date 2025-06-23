/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

export class DiagramViewPage {
	readonly backButton: Locator;
	readonly definitionInfoButton: Locator;
	readonly deleteButton: Locator;
	readonly diagramArea: Locator;
	readonly diagramNodes: Locator;
	readonly page: Page;
	readonly publishWorkflowDefinitionButton: Locator;
	readonly saveWorkflowDefinitionButton: Locator;
	readonly sourceViewButton: Locator;
	readonly workflowDefinitionTitle: Locator;

	constructor(page: Page) {
		this.backButton = page.getByRole('link', {name: 'Back'});
		this.definitionInfoButton = page.getByRole('button', {
			name: 'Definition Info',
		});
		this.deleteButton = page.getByTitle('Delete').last();
		this.diagramArea = page.locator('.react-flow');
		this.diagramNodes = page.locator('.react-flow__node');
		this.page = page;
		this.publishWorkflowDefinitionButton = page.getByRole('button', {
			name: 'Publish',
		});
		this.saveWorkflowDefinitionButton = page.getByRole('button', {
			name: 'Save',
		});
		this.sourceViewButton = page.locator('button[title="Source View"]');
		this.workflowDefinitionTitle = page.locator('#definition-title');
	}

	async clickNode(nodeLabel: string) {
		await this.diagramNodes.getByText(nodeLabel, {exact: true}).click();
	}

	async clickSourceViewButton() {
		await this.sourceViewButton.click();
	}

	async clickTransition(name: string) {
		await this.page.getByText(name).click({force: true});
	}

	async deleteNode(nodeLabel: string) {
		await this.clickNode(nodeLabel);

		await this.page.keyboard.press('Delete');

		await expect(this.page.getByText('Delete Task Node')).toBeVisible();
		await this.deleteButton.click();
	}

	async publishWorkflowDefinition() {
		await this.publishWorkflowDefinitionButton.click();
	}

	async saveWorkflowDefinition() {
		await this.saveWorkflowDefinitionButton.click();
	}

	async goBack() {
		await this.backButton.click();
	}
}
