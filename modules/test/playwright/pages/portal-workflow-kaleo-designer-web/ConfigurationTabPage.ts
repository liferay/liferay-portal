/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../utils/waitForAlert';
import {ProcessBuilderPage} from './ProcessBuilderPage';

export class ConfigurationTabPage {
	readonly configurationTabLink: Locator;
	readonly page: Page;
	readonly processBuilderPage: ProcessBuilderPage;

	constructor(page: Page) {
		this.configurationTabLink = page.getByRole('link', {
			name: 'Configuration',
		});
		this.page = page;
		this.processBuilderPage = new ProcessBuilderPage(page);
	}

	async goTo() {
		await this.processBuilderPage.goto();
		await this.configurationTabLink.waitFor({state: 'visible'});
		await this.configurationTabLink.click({force: true});
		await this.page.waitForURL((url) =>
			url.href.includes('=configuration')
		);
	}

	private async clickAssetTypeEditButton(assetType: string) {

		// Callers reach this tab either through goTo, which waits for the tab's
		// own address, or by clicking the tab link themselves and continuing
		// straight into an assignment. Until that navigation lands, the tab the
		// caller came from is still on screen, and its management bar carries
		// its own enabled search form, so a submit sent in that window filters
		// that other table and leaves this one empty for good. Wait for the
		// address before touching the search form.

		await this.page.waitForURL((url) =>
			url.href.includes('=configuration')
		);

		// The table lists every workflow enabled asset type in the instance and
		// pages at twenty rows, which leaves a generated object definition on the
		// boundary of the first page. Filter by name so the lookup does not
		// depend on how many asset types the instance happens to hold.

		const editButton = this.page
			.getByRole('row')
			.filter({
				has: this.page.getByRole('cell', {
					exact: true,
					name: assetType,
				}),
			})
			.getByRole('button', {name: 'Edit'});

		// The search submit button is served disabled and the toolbar's script
		// enables it only once it runs. It is the form's default button, and a
		// form whose default button is disabled ignores Enter, so wait for the
		// button itself before submitting. No load state can stand in for it:
		// the tab arrives through a single page application navigation, after
		// which load and networkidle resolve while the button is still
		// disabled.

		await expect(
			this.page.locator('.management-bar button[type="submit"]')
		).toBeEnabled();

		const searchInput = this.page.getByPlaceholder('Search for');

		await searchInput.fill(assetType);

		await searchInput.press('Enter');

		await expect(editButton).toBeVisible();

		// The edit button swaps the row into its inline edit form. A click
		// fired before the row's script is wired gets swallowed, leaving the
		// workflow definition select present but hidden, so retry the click
		// until the select is visible.

		await clickAndExpectToBeVisible({
			target: this.getAssignWorkflowDropdown(assetType),
			trigger: editButton,
		});
	}

	private async clickAssetTypeSaveButton(
		actionResult: 'assigned' | 'unassigned',
		assetType: string
	) {
		const saveButton = this.page
			.getByRole('row', {name: assetType})
			.getByRole('button', {name: 'Save'});

		await saveButton.waitFor({state: 'visible'});
		await saveButton.click();

		if (actionResult === 'assigned') {
			await waitForAlert(
				this.page,
				`Success:Workflow ${actionResult} to ${assetType}.`
			);
		}
	}

	async assignWorkflowToAssetType(workflowName: string, assetType: string) {
		await this.clickAssetTypeEditButton(assetType);

		await this.getAssignWorkflowDropdown(assetType).selectOption(
			workflowName
		);

		await this.clickAssetTypeSaveButton('assigned', assetType);
	}

	async unassignWorkflowFromAssetType(assetType: string) {
		await this.clickAssetTypeEditButton(assetType);

		await this.getAssignWorkflowDropdown(assetType).selectOption(
			'No Workflow'
		);

		await this.clickAssetTypeSaveButton('unassigned', assetType);
	}

	getAssignWorkflowDropdown(assetType: string) {
		return this.page
			.getByRole('row')
			.filter({
				has: this.page.getByRole('cell', {
					exact: true,
					name: assetType,
				}),
			})
			.getByTitle('Workflow Definition');
	}
}
