/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {DiagramViewPage} from '../../pages/portal-workflow-kaleo-designer-web/DiagramViewPage';

export class ActionPage {
	readonly backButton: Locator;
	readonly diagramViewPage: DiagramViewPage;
	readonly nameInput: Locator;
	readonly page: Page;
	readonly scriptInput: Locator;
	readonly selectActionType: Locator;
	readonly selectActionTypeStatus: Locator;

	constructor(page: Page) {
		this.backButton = page.getByRole('button', {
			name: 'Back',
		});
		this.diagramViewPage = new DiagramViewPage(page);
		this.nameInput = page.locator('#name');
		this.page = page;
		this.scriptInput = page.locator('#script');
		this.selectActionType = page.locator('#type');
		this.selectActionTypeStatus = page.locator('#update-status');
	}

	async fillWorkflowAction({
		name,
		script,
		statusOption,
		typeOption,
	}: {
		name: string;
		script?: string;
		statusOption?: string;
		typeOption: string;
	}) {
		await this.selectActionType.selectOption(typeOption);

		if (typeOption === 'Update Status') {
			await this.selectActionTypeStatus.selectOption(statusOption);
		}
		else if (typeOption === 'Groovy' || typeOption === 'Java') {
			await this.scriptInput.fill(script);
			await this.scriptInput.blur();
		}

		await this.nameInput.fill(name);
		await this.nameInput.blur();
	}

	async getTypeSelectOption(optionValue: string) {
		return await this.page.$(`#type option[value="${optionValue}"]`);
	}
}
