/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';

export class EditTaskPage {
	readonly apiHelpers: ApiHelpers;
	readonly descriptionInput: Locator;
	readonly descriptionToolbar: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly titleInput: Locator;

	constructor(page: Page) {
		this.apiHelpers = new ApiHelpers(page);
		this.descriptionInput = page.getByRole('textbox', {
			name: /^Description/,
		});
		this.descriptionToolbar = page.getByRole('toolbar', {
			name: 'Editor toolbar',
		});
		this.page = page;
		this.saveButton = page.getByRole('button', {
			name: 'Save',
		});
		this.titleInput = page.getByPlaceholder('Untitled Task');
	}

	async goto(taskId: number) {
		const {classNameId} = await this.getCMPTaskObjectDefinition();

		await this.page.goto(
			`/web/cms/e/edit-task/${classNameId}/${taskId}?redirect=${encodeURIComponent(`/web/cms/e/task/${classNameId}/${taskId}`)}`
		);
	}

	async gotoAddSubtask(parentTaskId: number) {
		const {classNameId, id} = await this.getCMPTaskObjectDefinition();

		await this.page.goto(
			`/c/cms/add_task?objectDefinitionId=${id}&parentId=${parentTaskId}&redirect=${encodeURIComponent(`/web/cms/e/task/${classNameId}/${parentTaskId}`)}`
		);
	}

	private async getCMPTaskObjectDefinition() {
		const cmpTaskObjectDefinition =
			await this.apiHelpers.objectAdmin.getObjectDefinitionByName(
				'CMPTask'
			);

		const {classNameId} =
			await this.apiHelpers.jsonWebServicesClassName.fetchClassName(
				cmpTaskObjectDefinition.className
			);

		return {classNameId, id: cmpTaskObjectDefinition.id};
	}
}
