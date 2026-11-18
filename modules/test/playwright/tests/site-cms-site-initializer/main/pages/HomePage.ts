/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

export class HomePage {
	readonly page: Page;

	readonly basicDocumentButton: Locator;
	readonly basicWebContentButton: Locator;
	readonly blogButton: Locator;
	readonly knowledgeBaseButton: Locator;
	readonly viewAllButton: Locator;
	readonly vocabularyButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.basicDocumentButton = page.getByRole('button', {
			exact: true,
			name: 'Basic Document',
		});
		this.basicWebContentButton = page.getByRole('button', {
			exact: true,
			name: 'Basic Web Content',
		});
		this.blogButton = page.getByRole('button', {
			exact: true,
			name: 'Blog',
		});
		this.knowledgeBaseButton = page.getByRole('button', {
			exact: true,
			name: 'Knowledge Base',
		});
		this.viewAllButton = page.getByRole('link', {
			exact: true,
			name: 'View All',
		});
		this.vocabularyButton = page.getByRole('button', {
			exact: true,
			name: 'Vocabulary',
		});
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.cmsHome);
		await this.page.getByRole('menuitem', {name: 'Home'}).waitFor();
	}

	async assignTo(name: string) {
		const workflowTaskRow = this.page
			.getByRole('row')
			.filter({hasText: /sent you/i})
			.filter({hasText: name});

		await workflowTaskRow.getByRole('button').click();

		await this.page.getByRole('menuitem', {name: 'Assign to...'}).click();

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}

	async approveWorkflowTask(name: string) {
		const workflowTaskRow = this.page
			.getByRole('row')
			.filter({hasText: /sent you/i})
			.filter({hasText: name});

		await workflowTaskRow.getByRole('button').click();

		await this.page.getByRole('menuitem', {name: 'Approve'}).click();

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}

	async rejectWorkflowTask(name: string) {
		const workflowTaskRow = this.page
			.getByRole('row')
			.filter({hasText: /sent you/i})
			.filter({hasText: name});

		await workflowTaskRow.getByRole('button').click();

		await this.page.getByRole('menuitem', {name: 'Reject'}).click();

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			this.page,
			`Success:Your request completed successfully.`
		);
	}

	async selectSpace(name: string) {
		const selectButton: Locator = this.page.getByLabel('SpaceMandatory');

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
		const workflowTaskRow = this.page
			.getByRole('row')
			.filter({hasText: /sent you/i})
			.filter({hasText: name});

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
