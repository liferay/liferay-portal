/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {HomePage} from '../portal-web/HomePage';

export class ApplicationsMenuPage {
	private readonly accountGroupsItem: Locator;
	private readonly accountsItem: Locator;
	private readonly accountUsersItem: Locator;
	private readonly aiCreatorLink: Locator;
	private readonly announcementsItem: Locator;
	private readonly apiBuilderMenuItem: Locator;
	readonly applicationsMenuTabButton: Locator;
	private readonly auditItem: Locator;
	private readonly blueprintsItem: Locator;
	private readonly clientExtensionsLink: Locator;
	private readonly commerceCatalogsMenuItem: Locator;
	private readonly commerceChannelsMenuItem: Locator;
	private readonly commerceCurrenciesMenuItem: Locator;
	private readonly commerceDiscountsMenuItem: Locator;
	private readonly commerceHealthCheckMenuItem: Locator;
	private readonly commerceOrdersMenuItem: Locator;
	private readonly commerceOrderTypesMenuItem: Locator;
	private readonly commercePanelButton: Locator;
	private readonly commerceProductConfigurationListsMenuItem: Locator;
	private readonly commerceReturnsMenuItem: Locator;
	private readonly commerceShipmentsMenuItem: Locator;
	private readonly commerceSpecificationsMenuItem: Locator;
	private readonly commerceTaxCategoriesMenuItem: Locator;
	private readonly commerceWarehousesMenuItem: Locator;
	private readonly componentsMenuItem: Locator;
	private readonly controlPanelButton: Locator;
	private readonly countriesManagementItem: Locator;
	private readonly customFieldsMenuItem: Locator;
	private readonly dataMigrationCenterMenuItem: Locator;
	private readonly dataSetManagerMenuItem: Locator;
	private readonly defaultPermissionsLink: Locator;
	readonly exportMenuItem: Locator;
	private readonly gogoShellItem: Locator;
	private readonly homePage: HomePage;
	readonly importMenuItem: Locator;
	private readonly instanceSettingsMenuItem: Locator;
	private readonly jobSchedulerMenuItem: Locator;
	private readonly metricsItem: Locator;
	private readonly oAuth2Administration: Locator;
	private readonly objectsMenuItem: Locator;
	private readonly page: Page;
	private readonly passwordPoliciesAdminItem: Locator;
	private readonly paymentsMenuItem: Locator;
	private readonly picklistsMenuItem: Locator;
	private readonly processBuilderItem: Locator;
	private readonly productsMenuItem: Locator;
	private readonly queueMenuItem: Locator;
	private readonly resultRankingsItem: Locator;
	private readonly rolesItem: Locator;
	private readonly samlAdminItem: Locator;
	private readonly searchItem: Locator;
	private readonly serverAdministrationItem: Locator;
	private readonly serviceAccountsItem: Locator;
	private readonly sitesItem: Locator;
	private readonly siteTemplatesButton: Locator;
	private readonly systemSettingsItem: Locator;
	private readonly userGroupsItem: Locator;
	private readonly usersAndOrganizationsItem: Locator;
	private readonly virtualInstancesItem: Locator;

	constructor(page: Page) {
		this.accountGroupsItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Account Groups',
		});
		this.accountsItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Accounts',
		});
		this.accountUsersItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Account Users',
		});
		this.aiCreatorLink = page.getByRole('link', {
			exact: true,
			name: 'AI Creator',
		});
		this.announcementsItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Announcements and Alerts',
		});
		this.apiBuilderMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'API Builder',
		});
		this.applicationsMenuTabButton = page.getByRole('tab', {
			name: 'Applications',
		});
		this.auditItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Audit',
		});
		this.blueprintsItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Blueprints',
		});
		this.clientExtensionsLink = page.getByRole('menuitem', {
			name: 'Client Extensions',
		});
		this.commerceCatalogsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Catalogs',
		});
		this.commerceChannelsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Channels',
		});
		this.commerceCurrenciesMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Currencies',
		});
		this.commerceDiscountsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Discounts',
		});
		this.commerceHealthCheckMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Health Check',
		});
		this.commerceOrdersMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Orders',
		});
		this.commerceOrderTypesMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Order Types',
		});
		this.commercePanelButton = page.getByRole('tab', {
			name: 'Commerce',
		});
		this.commerceProductConfigurationListsMenuItem = page.getByRole(
			'menuitem',
			{
				exact: true,
				name: 'Product Configurations',
			}
		);
		this.commerceReturnsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Returns',
		});
		this.commerceShipmentsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Shipments',
		});
		this.commerceSpecificationsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Specifications',
		});
		this.commerceTaxCategoriesMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Tax Categories',
		});
		this.commerceWarehousesMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Warehouses',
		});
		this.componentsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Components',
		});
		this.controlPanelButton = page.getByRole('tab', {
			name: 'Control Panel',
		});
		this.countriesManagementItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Countries Management',
		});
		this.customFieldsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Custom Fields',
		});
		this.dataMigrationCenterMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Data Migration Center',
		});
		this.dataSetManagerMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Data Sets',
		});
		this.defaultPermissionsLink = page.getByRole('link', {
			exact: true,
			name: 'Default Permissions',
		});
		this.exportMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Export',
		});
		this.gogoShellItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Gogo Shell',
		});
		this.homePage = new HomePage(page);
		this.importMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Import',
		});
		this.instanceSettingsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Instance Settings',
		});
		this.jobSchedulerMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Job Scheduler',
		});
		this.metricsItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Metrics',
		});
		this.oAuth2Administration = page.getByRole('menuitem', {
			exact: true,
			name: 'OAuth 2 Administration',
		});
		this.objectsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Objects',
		});
		this.page = page;
		this.passwordPoliciesAdminItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Password Policies',
		});
		this.paymentsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Payments',
		});
		this.picklistsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Picklists',
		});
		this.processBuilderItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Process Builder',
		});
		this.productsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Products',
		});
		this.queueMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Queue',
		});
		this.resultRankingsItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Result Rankings',
		});
		this.rolesItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Roles',
		});
		this.samlAdminItem = page.getByRole('menuitem', {
			exact: true,
			name: 'SAML Admin',
		});
		this.searchItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Search',
		});
		this.serverAdministrationItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Server Administration',
		});
		this.serviceAccountsItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Service Accounts',
		});
		this.sitesItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Sites',
		});
		this.siteTemplatesButton = page.getByRole('menuitem', {
			exact: true,
			name: 'Site Templates',
		});
		this.systemSettingsItem = page.getByRole('menuitem', {
			exact: true,
			name: 'System Settings',
		});
		this.userGroupsItem = page.getByRole('menuitem', {
			exact: true,
			name: 'User Groups',
		});
		this.usersAndOrganizationsItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Users and Organizations',
		});
		this.virtualInstancesItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Virtual Instances',
		});
	}

	async goto(checkTabVisibility = true) {
		await this.homePage.goto();
		await this.homePage.openApplicationMenu();

		if (checkTabVisibility) {
			await expect(this.applicationsMenuTabButton).toBeVisible();
		}
	}

	async goToAccountGroups(forceReload = true) {
		if (forceReload) {
			await this.goto();
		}
		else {
			await this.homePage.openApplicationMenu();
		}

		await this.controlPanelButton.click();
		await this.accountGroupsItem.click();
	}

	async goToAccounts(forceReload = true) {
		if (forceReload) {
			await this.goto();
		}
		else {
			await this.homePage.openApplicationMenu();
		}

		await this.controlPanelButton.click();
		await this.accountsItem.click();
	}

	async goToAccountUsers(forceReload = true) {
		if (forceReload) {
			await this.goto();
		}
		else {
			await this.homePage.openApplicationMenu();
		}

		await this.controlPanelButton.click();
		await this.accountUsersItem.click();
	}

	async goToAICreator() {
		await this.goToInstanceSettings();
		await this.aiCreatorLink.click();
	}

	async goToAnnouncements() {
		await this.goToApplicationsMenu();
		await this.announcementsItem.click();
	}

	async goToAPIBuilder() {
		await this.goToControlPanel();
		await this.apiBuilderMenuItem.click();
	}

	async goToApplicationsMenu() {
		await this.goto();
		await this.applicationsMenuTabButton.click();
	}

	async goToAudit() {
		await this.goto();
		await this.controlPanelButton.click();
		await this.auditItem.click();
	}

	async goToBlueprints() {
		await this.goToApplicationsMenu();
		await this.blueprintsItem.click();
	}

	async goToClientExtensions() {
		await this.goto();
		await this.clientExtensionsLink.click();
	}

	async goToCommerceCatalogs(checkTabVisibility = true) {
		await this.goToCommercePanel(checkTabVisibility);
		await this.commerceCatalogsMenuItem.click();
	}

	async goToCommerceChannels(checkTabVisibility = true) {
		await this.goToCommercePanel(checkTabVisibility);
		await this.commerceChannelsMenuItem.click();
	}

	async goToCommerceCurrencies(checkTabVisibility = true) {
		await this.goToCommercePanel(checkTabVisibility);
		await this.commerceCurrenciesMenuItem.click();
	}

	async goToCommerceDiscounts() {
		await this.goToCommercePanel();
		await this.commerceDiscountsMenuItem.click();
	}

	async goToCommerceHealthCheck() {
		await this.goToCommercePanel();
		await this.commerceHealthCheckMenuItem.click();
	}

	async goToCommerceOrders(checkTabVisibility = true) {
		await this.goToCommercePanel(checkTabVisibility);
		await this.commerceOrdersMenuItem.click();
	}

	async goToCommerceOrderTypes(checkTabVisibility = true) {
		await this.goToCommercePanel(checkTabVisibility);
		await this.commerceOrderTypesMenuItem.click();
	}

	async goToCommercePanel(checkTabVisibility = true) {
		await this.goto(checkTabVisibility);
		await this.commercePanelButton.click();
	}

	async goToCommerceProductConfigurationLists(checkTabVisibility = true) {
		await this.goToCommercePanel(checkTabVisibility);
		await this.commerceProductConfigurationListsMenuItem.click();
	}

	async goToCommerceReturns(checkTabVisibility = true) {
		await this.goToCommercePanel(checkTabVisibility);
		await this.commerceReturnsMenuItem.click();
	}

	async goToCommerceShipments(checkTabVisibility = true) {
		await this.goToCommercePanel(checkTabVisibility);
		await this.commerceShipmentsMenuItem.click();
	}

	async goToCommerceSpecifications() {
		await this.goToCommercePanel();
		await this.commerceSpecificationsMenuItem.click();
	}

	async goToCommerceTaxCategories(checkTabVisibility = true) {
		await this.goToCommercePanel(checkTabVisibility);
		await this.commerceTaxCategoriesMenuItem.click();
	}

	async goToCommerceWarehouses(checkTabVisibility = true) {
		await this.goToCommercePanel(checkTabVisibility);
		await this.commerceWarehousesMenuItem.click();
	}

	async goToComponents() {
		await this.goto();
		await this.controlPanelButton.click();
		await this.componentsMenuItem.click();
	}

	async goToControlPanel(checkTabVisibility = true) {
		await this.goto(checkTabVisibility);
		await this.controlPanelButton.click();
	}

	async goToCountriesManagement() {
		await this.goto();
		await this.controlPanelButton.click();
		await this.countriesManagementItem.click();
	}

	async goToCustomFields(forceReload = true) {
		if (forceReload) {
			await this.goto();
		}
		else {
			await this.homePage.openApplicationMenu();
			await expect(this.applicationsMenuTabButton).toBeVisible();
		}

		await this.controlPanelButton.click();
		await this.customFieldsMenuItem.click();
	}

	async goToDataMigrationCenter() {
		await this.goToApplicationsMenu();
		await this.dataMigrationCenterMenuItem.click();
	}

	async goToDataSetManager(checkTabVisibility = true) {
		await this.goToControlPanel(checkTabVisibility);
		await this.dataSetManagerMenuItem.click();
	}

	async goToDefaultPermissions() {
		await this.goToInstanceSettings();
		await this.defaultPermissionsLink.click();
	}

	async goToExport() {
		await this.goToApplicationsMenu();
		await this.exportMenuItem.click();
	}

	async goToGlobalSite() {
		await this.goToSite('Global');
	}

	async goToGogoShell() {
		await this.goToControlPanel();
		await this.gogoShellItem.click();
	}

	async goToImport() {
		await this.goToApplicationsMenu();
		await this.importMenuItem.click();
	}

	async goToInstanceSettings(forceReload = true) {
		if (forceReload) {
			await this.goto();
		}
		else {
			await this.homePage.openApplicationMenu();

			await expect(this.applicationsMenuTabButton).toBeVisible();
		}

		await this.controlPanelButton.click();
		await this.instanceSettingsMenuItem.click();
	}

	async goToJobScheduler() {
		await this.goToControlPanel();
		await this.jobSchedulerMenuItem.click();
	}

	async goToMetrics() {
		await this.goToApplicationsMenu();
		await this.metricsItem.click();
	}

	async goToOauth2Administration() {
		await this.goToControlPanel();
		await this.oAuth2Administration.click();
	}

	async goToObjectDefinition(objectDefinitionName: string) {
		await this.goToControlPanel();
		await this.page
			.getByRole('menuitem', {name: objectDefinitionName})
			.click();
	}

	async goToObjects() {
		await this.goToControlPanel();
		await this.objectsMenuItem.click();
	}

	async goToPasswordPolicies() {
		await this.goToControlPanel();
		await this.passwordPoliciesAdminItem.click();
	}

	async goToPayments(checkTabVisibility = true) {
		await this.goToCommercePanel(checkTabVisibility);
		await this.paymentsMenuItem.click();
	}

	async goToPicklists() {
		await this.goToControlPanel();
		await this.picklistsMenuItem.click();
	}

	async goToProcessBuilder() {
		await this.goToApplicationsMenu();
		await this.processBuilderItem.click();
	}

	async goToProducts(checkTabVisibility = true) {
		await this.goToCommercePanel(checkTabVisibility);
		await this.productsMenuItem.click();
	}

	async goToQueue() {
		await this.goToControlPanel();
		await this.queueMenuItem.click();
	}

	async goToResultRankings() {
		await this.goToApplicationsMenu();
		await this.resultRankingsItem.click();
	}

	async goToRoles(checkTabVisibility = true) {
		await this.goto(checkTabVisibility);
		await this.controlPanelButton.click();
		await this.rolesItem.click();
	}

	async goToSamlAdmin(forceReload = true) {
		if (forceReload) {
			await this.goto();
		}
		else {
			await this.homePage.openApplicationMenu();

			await expect(this.applicationsMenuTabButton).toBeVisible();
		}

		await this.controlPanelButton.click();
		await this.samlAdminItem.click();
	}

	async goToSearch() {
		await this.goToControlPanel();
		await this.searchItem.click();
	}

	async goToServerAdministration() {
		await this.goToControlPanel();
		await this.serverAdministrationItem.click();
	}

	async goToServiceAccounts() {
		await this.goto();
		await this.controlPanelButton.click();
		await this.serviceAccountsItem.click();
	}

	async goToSite(name: string = 'Liferay DXP', checkTabVisibility = true) {
		await this.goto(checkTabVisibility);
		await this.page.getByRole('link', {exact: true, name}).click();
	}

	async goToSites(forceReload = true) {
		if (forceReload) {
			await this.goto();
		}
		else {
			await this.homePage.openApplicationMenu();

			await expect(this.applicationsMenuTabButton).toBeVisible();
		}

		await this.controlPanelButton.click();
		await this.sitesItem.click();
	}

	async goToSiteTemplates() {
		await this.goToControlPanel();
		await this.siteTemplatesButton.click();
	}

	async goToSystemSettings() {
		await this.goToControlPanel();
		await this.systemSettingsItem.click();
	}

	async goToUserGroups(forceReload = true) {
		if (forceReload) {
			await this.goto();
		}
		else {
			await this.homePage.openApplicationMenu();

			await expect(this.applicationsMenuTabButton).toBeVisible();
		}

		await this.controlPanelButton.click();
		await this.userGroupsItem.click();
	}

	async goToUserGroupsWithLimitedAccess() {
		await this.homePage.openApplicationMenu();
		await this.controlPanelButton.click();
		await this.userGroupsItem.click();
	}

	async goToUsersAndOrganizations(forceReload = true) {
		if (forceReload) {
			await this.goto();
		}
		else {
			await this.homePage.openApplicationMenu();

			await expect(this.applicationsMenuTabButton).toBeVisible();
		}

		await this.controlPanelButton.click();
		await this.usersAndOrganizationsItem.click();
	}

	async goToUsersAndOrganizationsWithLimitedAccess() {
		await this.homePage.openApplicationMenu();
		await this.controlPanelButton.click();
		await this.usersAndOrganizationsItem.click();
	}

	async goToVirtualInstances() {
		await this.goto();
		await this.controlPanelButton.click();
		await this.virtualInstancesItem.click();
	}
}
