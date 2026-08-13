/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../utils/waitForAlert';
import {ProcessBuilderPage} from './ProcessBuilderPage';

const WORKFLOW_NAMESPACE =
	'_com_liferay_portal_workflow_web_portlet_ControlPanelWorkflowPortlet_';

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

	async searchAssetType(assetType: string) {

		// The tab a caller arrives from stays on screen until its navigation
		// lands, and it carries its own search form, so a submit sent in that
		// window filters the other table and navigates away from this tab for
		// good. Ask for the tab and the keywords in one address instead: the
		// table is rendered already filtered, with no form to race.

		// Object definitions are registered as workflow asset types for the
		// instance, so they are listed by the instance wide control panel and
		// not by a site scoped one.

		await this.page.goto(
			'/group/control_panel/manage' +
				'?p_p_id=com_liferay_portal_workflow_web_portlet_ControlPanelWorkflowPortlet' +
				`&${WORKFLOW_NAMESPACE}tab=configuration` +
				`&${WORKFLOW_NAMESPACE}keywords=${encodeURIComponent(assetType)}`
		);

		await this.page.waitForLoadState('networkidle');
	}

	private async clickAssetTypeEditButton(assetType: string) {

		// The table lists every workflow enabled asset type in the instance and
		// pages at twenty rows, which leaves a generated object definition on the
		// boundary of the first page. Filter by name so the lookup does not
		// depend on how many asset types the instance happens to hold.

		await this.searchAssetType(assetType);

		const editButton = this.page
			.getByRole('row')
			.filter({
				has: this.page.getByRole('cell', {
					exact: true,
					name: assetType,
				}),
			})
			.getByRole('button', {name: 'Edit'});

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
