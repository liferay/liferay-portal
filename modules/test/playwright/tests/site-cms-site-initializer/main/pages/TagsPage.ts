/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../../utils/getRandomInt';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {DataSetPage} from './DataSetPage';

export class TagsPage {
	readonly page: Page;
	readonly dataSetFragmentPage: DataSetPage;
	readonly newTagButton: Locator;
	readonly projectCheckbox: Locator;
	readonly projectSelector: Locator;
	readonly saveAndAddAnotherButton: Locator;
	readonly saveButton: Locator;
	readonly spaceCheckbox: Locator;

	constructor(page: Page) {
		this.page = page;
		this.dataSetFragmentPage = new DataSetPage(page);
		this.newTagButton = this.page.getByRole('button', {name: 'New'});
		this.projectCheckbox = this.page.getByRole('checkbox', {
			name: 'Make this tag available in all projects',
		});
		this.projectSelector = this.page.getByLabel('Project Selector');
		this.saveAndAddAnotherButton = this.page.getByText(
			'Save and Add Another'
		);
		this.saveButton = this.page.getByText('Save').last();
		this.spaceCheckbox = page.getByLabel(
			'Make this tag available in all spaces, including those yet to be created.'
		);
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.cmsTags);
		await this.page
			.getByRole('heading', {name: 'Categorization'})
			.waitFor();
	}

	async createTag(spaces?: string[]) {
		await this.goto();

		const tagName = `Tag${getRandomInt()}`;

		await this.newTagButton.click();

		await this.page.getByLabel('NameRequired').fill(tagName);

		if (spaces) {
			await this.spaceCheckbox.uncheck();

			for (const space of spaces) {
				await this.page.getByLabel('Space Selector').click();
				await this.page
					.locator(
						`//button[@role='option']//span[text()='${space}']`
					)
					.click();
			}
		}

		await clickAndExpectToBeVisible({
			target: this.page.getByText(
				`Success:${tagName} was created successfully.`
			),
			trigger: this.saveButton,
		});

		return tagName;
	}

	async clearProjects() {
		if (!(await this.projectCheckbox.isChecked())) {
			await this.projectCheckbox.click();
		}

		await expect(this.projectCheckbox).toBeChecked();
	}

	async deleteTag(name: string) {
		await this.execItemAction({
			action: 'Delete',
			filter: name,
		});

		await expect(
			this.page.getByRole('heading', {name: `Delete Tag`})
		).toBeVisible();

		await expect(
			this.page.locator('.liferay-modal .modal-dialog')
		).toHaveClass(/modal-dialog-centered/);

		await clickAndExpectToBeVisible({
			target: this.page.getByText(
				`Success:${name} was deleted successfully.`
			),
			trigger: this.page.getByRole('button', {name: 'Delete'}),
		});

		await expect(this.getItem(name)).not.toBeVisible();
	}

	async selectProject(projectName: string) {
		const option = this.page
			.getByRole('option')
			.filter({hasText: projectName});

		// Loading the projects resets the checkbox, so unchecking before the
		// list arrives is undone. Retry the whole sequence until an option
		// survives the load.

		await expect(async () => {
			if (await this.projectCheckbox.isChecked({timeout: 1000})) {
				await this.projectCheckbox.click({timeout: 1000});
			}

			await expect(this.projectSelector).toBeEnabled({timeout: 1000});

			await this.projectSelector.click({timeout: 1000});

			await expect(option).toBeVisible({timeout: 2000});
		}).toPass();

		await option.click();
	}

	getItem(filter: string) {
		return this.dataSetFragmentPage.getRow(filter);
	}

	async execItemAction({action, filter}: {action: string; filter: string}) {
		await this.dataSetFragmentPage.execItemAction({
			action,
			filter,
		});
	}
}
