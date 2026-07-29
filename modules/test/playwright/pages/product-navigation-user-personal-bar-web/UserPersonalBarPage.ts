/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {InstanceSettingsPage} from '../configuration-admin-web/InstanceSettingsPage';
import {GlobalMenuPage} from '../product-navigation-applications-menu/GlobalMenuPage';

export class UserPersonalBarPage {
	readonly backArrow: Locator;
	readonly editConfigurationSubmitButton: Locator;
	readonly globalMenuPage: GlobalMenuPage;
	readonly instanceSettingsPage: InstanceSettingsPage;
	readonly notificationBadge: Locator;
	readonly notificationsMenuItem: Locator;
	readonly page: Page;
	readonly processBuilderConfigurationTab: Locator;
	readonly productsMenuItem: Locator;
	readonly searchInput: Locator;
	readonly searchResultText: Locator;
	readonly searchSubmit: Locator;
	readonly showNotificationBadgeInPersonalMenuLabel: Locator;
	readonly submitForWorkflowButton: Locator;
	readonly userMenuButton: Locator;
	readonly workflowDefinitionLinkCancelButton: Locator;
	readonly workflowDefinitionLinkEditButton: Locator;
	readonly workflowDefinitionLinkSaveButton: Locator;
	readonly workflowDefinitionLinkSelectButton: Locator;

	constructor(page: Page) {
		this.backArrow = page.getByRole('link', {exact: true, name: 'Back'});
		this.editConfigurationSubmitButton = page.getByTestId(
			'submitConfiguration'
		);
		this.globalMenuPage = new GlobalMenuPage(page);
		this.instanceSettingsPage = new InstanceSettingsPage(page);
		this.notificationBadge = page.getByLabel('New Notification');
		this.notificationsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Notifications',
		});
		this.page = page;
		this.processBuilderConfigurationTab = page.getByRole('link', {
			name: 'Configuration',
		});
		this.productsMenuItem = page.getByRole('menuitem', {
			name: 'Products',
		});
		this.searchInput = page
			.locator(
				'#_com_liferay_portal_workflow_web_portlet_ControlPanelWorkflowPortlet_fm_search'
			)
			.getByPlaceholder(/^Search/);
		this.searchResultText = page.getByTestId('searchResultText');
		this.searchSubmit = page
			.locator(
				'#_com_liferay_portal_workflow_web_portlet_ControlPanelWorkflowPortlet_fm_search'
			)
			.getByLabel('Search for', {exact: true});
		this.showNotificationBadgeInPersonalMenuLabel = page
			.getByTestId('showNotificationBadgeInPersonalMenu')
			.getByLabel('Show Notification Badge in Personal Menu', {
				exact: true,
			});
		this.submitForWorkflowButton = page.getByRole('link', {
			name: 'Submit for Workflow',
		});
		this.userMenuButton = page.getByTestId('userPersonalMenu');
		this.workflowDefinitionLinkCancelButton = page
			.getByTestId('actionProduct')
			.getByRole('button', {name: 'Cancel'});
		this.workflowDefinitionLinkEditButton = page
			.getByTestId('actionProduct')
			.getByRole('button', {name: 'Edit'});
		this.workflowDefinitionLinkSaveButton = page
			.getByTestId('actionProduct')
			.getByRole('button', {name: 'Save'});
		this.workflowDefinitionLinkSelectButton =
			page.getByTestId('selectProduct');
	}

	async disableNotificationBadgeInPersonalMenu() {
		await this.instanceSettingsPage.goToInstanceSetting(
			'Users',
			'Personal Menu'
		);
		await this.showNotificationBadgeInPersonalMenuLabel.uncheck();
		await this.editConfigurationSubmitButton.click();

		await waitForAlert(this.page);
	}

	async disableSingleApproverWorkflowProduct() {
		await this.goToProcessBuilderConfigurationTab();
		await this.searchInput.fill('Product');
		await this.searchSubmit.click();
		await this.searchResultText.waitFor({
			state: 'attached',
		});
		await this.workflowDefinitionLinkEditButton.click();
		await this.workflowDefinitionLinkSelectButton.selectOption(
			'No Workflow'
		);
		await this.workflowDefinitionLinkSaveButton.click();
	}

	async enableNotificationBadgeInPersonalMenu() {
		await this.instanceSettingsPage.goToInstanceSetting(
			'Users',
			'Personal Menu'
		);
		await this.showNotificationBadgeInPersonalMenuLabel.check();
		await this.editConfigurationSubmitButton.click();

		await waitForAlert(this.page);
	}

	async enableSingleApproverWorkflowProduct() {
		await this.goToProcessBuilderConfigurationTab();
		await this.searchInput.fill('Product');
		await this.searchSubmit.click();
		await this.searchResultText.waitFor({
			state: 'attached',
		});
		await this.workflowDefinitionLinkEditButton.click();
		await this.workflowDefinitionLinkSelectButton.selectOption(
			'Single Approver'
		);
		await this.workflowDefinitionLinkSaveButton.click();
	}

	async goToProcessBuilderConfigurationTab() {
		await this.globalMenuPage.goToApplications('Process Builder');
		await Promise.all([
			this.processBuilderConfigurationTab.click(),
			this.page.waitForResponse(
				(resp) =>
					resp.status() === 200 &&
					resp
						.url()
						.includes(
							'_com_liferay_portal_workflow_web_portlet_ControlPanelWorkflowPortlet_tab=configuration'
						)
			),
		]);
	}
}
