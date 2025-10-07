/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

export class HomePage {
	readonly page: Page;

	readonly assignedToMeMenuItem: Locator;
	readonly assignedToMyRolesMenuItem: Locator;
	readonly basicDocumentButton: Locator;
	readonly basicWebContentButton: Locator;
	readonly blogButton: Locator;
	readonly knowledgeBaseButton: Locator;
	readonly vocabularyButton: Locator;
	readonly workflowTaskFilterButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.assignedToMeMenuItem = page.getByRole('menuitem', {
			name: 'Assigned to Me',
		});
		this.assignedToMyRolesMenuItem = page.getByRole('menuitem', {
			name: 'Assigned to My Roles',
		});
		this.basicDocumentButton = page.getByRole('button', {
			name: 'Basic Document',
		});
		this.basicWebContentButton = page.getByRole('button', {
			name: 'Basic Web Content',
		});
		this.blogButton = page.getByRole('button', {
			name: 'Blog',
		});
		this.knowledgeBaseButton = page.getByRole('button', {
			name: 'Knowledge Base',
		});
		this.vocabularyButton = page.getByRole('button', {
			name: 'Vocabulary',
		});
		this.workflowTaskFilterButton = page.getByRole('button', {
			name: /^Assigned to/,
		});
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.cmsHome);
		await this.page.getByRole('heading', {name: 'Home'}).waitFor();
	}

	async assignToMe(name: string) {
		const workflowTaskRow = this.page.getByRole('row', {name});

		await workflowTaskRow.getByRole('button').click();

		await this.page.getByRole('menuitem', {name: 'Assign to Me'}).click();

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}

	async assignTo(name: string) {
		const workflowTaskRow = this.page.getByRole('row', {name});

		await workflowTaskRow.getByRole('button').click();

		await this.page.getByRole('menuitem', {name: 'Assign to...'}).click();

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}

	async approveWorkflowTask(name: string) {
		const workflowTaskRow = this.page.getByRole('row', {name});

		await workflowTaskRow.getByRole('button').click();

		await this.page.getByRole('menuitem', {name: 'Approve'}).click();

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}

	async rejectWorkflowTask(name: string) {
		const workflowTaskRow = this.page.getByRole('row', {name});

		await workflowTaskRow.getByRole('button').click();

		await this.page.getByRole('menuitem', {name: 'Reject'}).click();

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}

	async selectSpace(name: string) {
		const selectButton: Locator = this.page.getByText('Select a Space');

		await selectButton.waitFor({state: 'visible'});

		await selectButton.click();

		const dropdown = this.page.getByRole('listbox');

		await dropdown.waitFor({state: 'visible'});

		const spaceButton: Locator = this.page.getByRole('option', {
			name,
		});

		await spaceButton.click();

		const saveButton: Locator = this.page.getByRole('button', {
			name: 'Save',
		});

		await saveButton.click();
	}

	async updateDueDate(date: string, name: string) {
		const workflowTaskRow = this.page.getByRole('row', {name});

		await workflowTaskRow.getByRole('button').click();

		await this.page
			.getByRole('menuitem', {name: 'Update Due Date'})
			.click();

		await this.page.locator('input[type="date"]').fill(date);

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}
}
