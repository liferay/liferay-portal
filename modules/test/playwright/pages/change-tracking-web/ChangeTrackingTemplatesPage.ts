/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {ChangeTrackingPage} from './ChangeTrackingPage';

export class ChangeTrackingTemplatesPage {
	readonly page: Page;

	private readonly changeTrackingPage: ChangeTrackingPage;

	private readonly addUsersButton: Locator;
	private readonly newTemplateButton: Locator;
	private readonly publicationCollaboratorsPanel: Locator;
	private readonly templateNameField: Locator;

	constructor(page: Page) {
		this.page = page;
		this.changeTrackingPage = new ChangeTrackingPage(page);

		this.addUsersButton = page.getByRole('button', {name: 'Add Users'});
		this.newTemplateButton = page
			.getByTestId('creationMenuNewButton')
			.getByText('New');
		this.publicationCollaboratorsPanel = page.locator(
			'//button[descendant::text()="Publication Collaborators"]'
		);
		this.templateNameField = page.getByRole('textbox', {name: 'Name'});
	}

	async goto() {
		await this.changeTrackingPage.goto();

		await this.page.getByLabel('Options').click();

		await this.page.getByRole('menuitem', {name: 'Templates'}).click();

		await expect(
			this.page.getByText('Publication Templates')
		).toBeVisible();
	}

	async gotoCreateTemplate() {
		await this.goto();

		await this.newTemplateButton.click();

		await expect(
			this.page.getByRole('heading', {
				name: 'Create a New Publication Template',
			})
		).toBeVisible();

		await expect(this.templateNameField).toBeVisible();
	}

	async openManageCollaboratorsModal() {
		if (
			(await this.publicationCollaboratorsPanel.getAttribute(
				'aria-expanded'
			)) === 'false'
		) {
			await this.publicationCollaboratorsPanel.click();

			await expect(this.addUsersButton).toBeVisible();
		}

		await this.addUsersButton.click();
	}
}
