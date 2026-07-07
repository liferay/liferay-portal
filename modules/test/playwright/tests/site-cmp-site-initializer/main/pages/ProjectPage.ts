/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';

type BreadcrumbActions = 'Edit' | 'Watch Project' | 'Delete';

export class ProjectPage {
	readonly assetsTab: Locator;
	readonly deleteButton: Locator;
	readonly detailsTab: Locator;
	readonly matrixFilterButton: Locator;
	readonly moreActionsButton: Locator;
	readonly newButton: Locator;
	readonly newTaskButton: Locator;
	readonly page: Page;
	readonly tasksTab: Locator;

	constructor(page: Page) {
		this.assetsTab = page.getByRole('tab', {
			name: 'Assets',
		});
		this.deleteButton = page.getByRole('button', {
			name: 'Delete',
		});
		this.detailsTab = page.getByRole('tab', {
			name: 'Details',
		});
		this.matrixFilterButton = page
			.locator('.lfr-cmp__content-gap-cell-actions')
			.getByRole('button', {name: 'Filter'});
		this.moreActionsButton = page.getByRole('button', {
			name: 'More Actions',
		});
		this.newButton = page.getByRole('button', {
			name: 'New',
		});
		this.newTaskButton = page.getByRole('button', {
			name: 'New Task',
		});
		this.page = page;
		this.tasksTab = page.getByRole('tab', {
			name: 'Tasks',
		});
	}

	async editProject() {
		await clickAndExpectToBeVisible({
			target: this.getBreadcrumbAction('Edit'),
			trigger: this.moreActionsButton,
		});

		await this.getBreadcrumbAction('Edit').click();
	}

	async filterByMatrixCell(
		persona: string,
		funnelStage: string,
		count: number
	) {
		await clickAndExpectToBeVisible({
			target: this.matrixFilterButton,
			trigger: this.getMatrixCell(persona, funnelStage, count),
		});

		await this.matrixFilterButton.click();
	}

	getBreadcrumbAction(filter: BreadcrumbActions) {
		return this.page.getByRole('menuitem', {name: filter});
	}

	getMatrixCell(
		persona: string,
		funnelStage: string,
		count: number
	): Locator {
		return this.page.getByRole('gridcell', {
			exact: true,
			name: `${persona}, ${funnelStage}: ${count}`,
		});
	}
}
