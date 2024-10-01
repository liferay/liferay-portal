/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';

export class WorkflowTasksPage {
	readonly assignedToMyRolesLink: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.assignedToMyRolesLink = page.getByRole('link', {
			name: 'Assigned to my roles',
		});
		this.page = page;
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.myWorkflowTasks}`
		);

		await this.page.waitForLoadState();
	}

	async goToAssignedToMyRoles(siteUrl?: Site['friendlyUrlPath']) {
		await this.goto(siteUrl);

		await this.assignedToMyRolesLink.click();
	}

	async approve(articleTitle: string) {
		const row = await this.page
			.getByRole('row')
			.filter({hasText: articleTitle});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page
				.locator('.dropdown-menu:visible')
				.getByText('Approve', {exact: true}),
			trigger: row.locator('.dropdown-toggle'),
		});

		await this.page.getByRole('button', {name: 'Done'}).click();

		await waitForAlert(this.page);
	}

	async assignToMe(articleTitle: string) {
		const row = await this.page
			.getByRole('row')
			.filter({hasText: articleTitle});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page
				.locator('.dropdown-menu:visible')
				.getByText('Assign to Me', {exact: true}),
			trigger: row.locator('.dropdown-toggle'),
		});

		await this.page
			.frameLocator(`iframe[title="Assign to Me"]`)
			.getByRole('button', {name: 'Done'})
			.waitFor();

		await this.page
			.frameLocator(`iframe[title="Assign to Me"]`)
			.getByRole('button', {name: 'Done'})
			.click();

		await waitForAlert(this.page);
	}

	async reject(articleTitle: string) {
		const row = await this.page
			.getByRole('row')
			.filter({hasText: articleTitle});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page
				.locator('.dropdown-menu:visible')
				.getByText('Reject', {exact: true}),
			trigger: row.locator('.dropdown-toggle'),
		});

		await this.page.getByRole('button', {name: 'Done'}).click();

		await waitForAlert(this.page);
	}

	async resubmit(articleTitle: string) {
		await this.goto();

		await this.page.reload();

		const row = await this.page
			.getByRole('row')
			.filter({hasText: articleTitle});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page
				.locator('.dropdown-menu:visible')
				.getByText('Resubmit', {exact: true}),
			trigger: row.locator('.dropdown-toggle'),
		});

		await this.page.getByRole('button', {name: 'Done'}).click();

		await waitForAlert(this.page);
	}
}
