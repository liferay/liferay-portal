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
		await this.configurationTabLink.click();
		await this.page.waitForURL((url) =>
			url.href.includes('=configuration')
		);
	}

	async searchAssetType(assetType: string) {
		await this.page.goto(
			'/group/control_panel/manage' +
				'?p_p_id=com_liferay_portal_workflow_web_portlet_ControlPanelWorkflowPortlet' +
				`&${WORKFLOW_NAMESPACE}tab=configuration` +
				`&${WORKFLOW_NAMESPACE}keywords=${encodeURIComponent(assetType)}`
		);

		await this.page.waitForLoadState('networkidle');
	}

	private async clickAssetTypeEditButton(assetType: string) {
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
