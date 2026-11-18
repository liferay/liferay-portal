/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';

export class TaskPage {
	readonly apiHelpers: ApiHelpers;
	readonly deleteMenuItem: Locator;
	readonly moreActionsButton: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.apiHelpers = new ApiHelpers(page);
		this.deleteMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Delete',
		});
		this.moreActionsButton = page.getByRole('button', {
			name: 'More Actions',
		});
		this.page = page;
	}

	async goto(taskId: number) {
		const cmpTaskObjectDefinition =
			await this.apiHelpers.objectAdmin.getObjectDefinitionByName(
				'CMPTask'
			);

		const {classNameId} =
			await this.apiHelpers.jsonWebServicesClassName.fetchClassName(
				cmpTaskObjectDefinition.className
			);

		await this.page.goto(`/web/cms/e/task/${classNameId}/${taskId}`);
	}

	async openMoreActions() {
		await clickAndExpectToBeVisible({
			target: this.deleteMenuItem,
			trigger: this.moreActionsButton,
		});
	}
}
