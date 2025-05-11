/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../fixtures/accountsPagesTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {AccountOrganizationSelectorPage} from '../../../pages/account-admin-web/AccountOrganizationSelectorPage';
import {AccountsPage} from '../../../pages/account-admin-web/AccountsPage';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {nextPage, setItemsPerPage} from '../../../utils/pagination';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {addAccountRole, initAccountAdministrator} from './utils/roles';

export const test = mergeTests(
	accountsPagesTest,
	applicationsMenuPageTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	usersAndOrganizationsPagesTest
);

async function postRoleWithAccountAdminPermissions(
	apiHelpers: any,
	companyId: string
) {
	return await apiHelpers.headlessAdminUser.postRole({
		name: getRandomString(),
		rolePermissions: [
			{
				actionIds: [
					'ASSIGN_USERS',
					'MANAGE_ADDRESSES',
					'MANAGE_CHANNEL_DEFAULTS',
					'MANAGE_ORGANIZATIONS',
					'MANAGE_USERS',
					'UPDATE',
					'VIEW',
					'VIEW_ACCOUNT_ROLES',
					'VIEW_ADDRESSES',
					'VIEW_CHANNEL_DEFAULTS',
					'VIEW_ORGANIZATIONS',
					'VIEW_USERS',
				],
				primaryKey: '0',
				resourceName: 'com.liferay.account.model.AccountEntry',
				scope: 3,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: '0',
				resourceName: 'com.liferay.account.model.AccountRole',
				scope: 3,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName: 'com.liferay.commerce.model.CommerceOrderType',
				scope: 1,
			},
			{
				actionIds: [
					'ADD_COMMERCE_ORDER',
					'APPROVE_OPEN_COMMERCE_ORDERS',
					'CHECKOUT_OPEN_COMMERCE_ORDERS',
					'DELETE_COMMERCE_ORDERS',
					'MANAGE_COMMERCE_ORDERS',
					'MANAGE_COMMERCE_ORDER_DELIVERY_TERMS',
					'MANAGE_COMMERCE_ORDER_PAYMENT_METHODS',
					'MANAGE_COMMERCE_ORDER_PAYMENT_STATUSES',
					'MANAGE_COMMERCE_ORDER_PAYMENT_TERMS',
					'MANAGE_COMMERCE_ORDER_SHIPPING_OPTIONS',
					'VIEW_BILLING_ADDRESS',
					'VIEW_COMMERCE_ORDERS',
					'VIEW_OPEN_COMMERCE_ORDERS',
				],
				primaryKey: '0',
				resourceName: 'com.liferay.commerce.order',
				scope: 3,
			},
		],
		roleType: 'account',
	});
}

test.describe('Test for Organization Account visibility depending on Permissions', () => {
	test(
		'Update Organizations permission visibility',
		{tag: ['@LPD-28116']},
		async ({
			accountOrganizationSelectorPage,
			accountsPage,
			apiHelpers,
			context,
			page,
			usersAndOrganizationsPage,
		}) => {
			const organization1 =
				await apiHelpers.headlessAdminUser.postOrganization();
			const organization2 =
				await apiHelpers.headlessAdminUser.postOrganization();

			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
				organization1.id,
				user.emailAddress
			);

			const companyId = await page.evaluate(() => {
				return Liferay.ThemeDisplay.getCompanyId();
			});

			const role = await apiHelpers.headlessAdminUser.postRole({
				name: getRandomString(),
				rolePermissions: [
					{
						actionIds: [
							'MANAGE_USERS',
							'UPDATE',
							'UPDATE_ORGANIZATIONS',
							'VIEW',
							'VIEW_ORGANIZATIONS',
						],
						primaryKey: companyId,
						resourceName: 'com.liferay.account.model.AccountEntry',
						scope: 1,
					},
					{
						actionIds: ['MANAGE_AVAILABLE_ACCOUNTS'],
						primaryKey: companyId,
						resourceName:
							'com.liferay.portal.kernel.model.Organization',
						scope: 1,
					},
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL'],
						primaryKey: companyId,
						resourceName:
							'com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet',
						scope: 1,
					},
				],
				roleType: 'organization',
			});

			await apiHelpers.headlessAdminUser.assignUserToOrganizationRole(
				role.id,
				user.id,
				organization1.id
			);

			const account = await apiHelpers.headlessAdminUser.postAccount();

			await apiHelpers.headlessAdminUser.postAccountOrganization(
				account.id,
				organization1.id
			);

			await usersAndOrganizationsPage.goToUsers();

			await (
				await usersAndOrganizationsPage.usersTableRowActions(
					`${user.alternateName}`
				)
			).click();

			const pagePromise = context.waitForEvent('page');

			await usersAndOrganizationsPage.impersonateUserMenuItem.click();

			const newPage = await pagePromise;
			accountsPage = new AccountsPage(newPage);

			await accountsPage.goto();
			await (
				await accountsPage.accountsTable.cellLink(account.name)
			).click();
			await accountsPage.organizationsTab.click();
			await accountsPage.accountsTable.newButton.click();

			await expect(
				accountOrganizationSelectorPage.frame.getByText(
					organization2.name,
					{exact: true}
				)
			).toHaveCount(0);
		}
	);

	test(
		'Manage Organizations Permission visibility',
		{tag: ['@LPD-28116']},
		async ({
			accountOrganizationSelectorPage,
			accountsPage,
			apiHelpers,
			context,
			page,
			usersAndOrganizationsPage,
		}) => {
			test.setTimeout(120000);

			const organization1 =
				await apiHelpers.headlessAdminUser.postOrganization();
			const organization2 =
				await apiHelpers.headlessAdminUser.postOrganization();

			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
				organization1.id,
				user.emailAddress
			);

			const companyId = await page.evaluate(() => {
				return Liferay.ThemeDisplay.getCompanyId();
			});

			const role = await apiHelpers.headlessAdminUser.postRole({
				name: getRandomString(),
				rolePermissions: [
					{
						actionIds: [
							'MANAGE_ORGANIZATIONS',
							'MANAGE_USERS',
							'UPDATE',
							'VIEW',
							'VIEW_ORGANIZATIONS',
						],
						primaryKey: companyId,
						resourceName: 'com.liferay.account.model.AccountEntry',
						scope: 1,
					},
					{
						actionIds: ['MANAGE_AVAILABLE_ACCOUNTS'],
						primaryKey: companyId,
						resourceName:
							'com.liferay.portal.kernel.model.Organization',
						scope: 1,
					},
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL'],
						primaryKey: companyId,
						resourceName:
							'com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet',
						scope: 1,
					},
				],
				roleType: 'organization',
			});

			await apiHelpers.headlessAdminUser.assignUserToOrganizationRole(
				role.id,
				user.id,
				organization1.id
			);

			const account = await apiHelpers.headlessAdminUser.postAccount();

			await apiHelpers.headlessAdminUser.postAccountOrganization(
				account.id,
				organization1.id
			);

			await usersAndOrganizationsPage.goToUsers();

			await (
				await usersAndOrganizationsPage.usersTableRowActions(
					`${user.alternateName}`
				)
			).click();

			const pagePromise = context.waitForEvent('page');

			await usersAndOrganizationsPage.impersonateUserMenuItem.click();

			const newPage = await pagePromise;
			accountsPage = new AccountsPage(newPage);
			accountOrganizationSelectorPage =
				new AccountOrganizationSelectorPage(newPage);

			await accountsPage.goto();
			await (
				await accountsPage.accountsTable.cellLink(account.name)
			).click();
			await accountsPage.organizationsTab.click();
			await accountsPage.accountsTable.newButton.click();

			await expect(
				accountOrganizationSelectorPage.organizationsTable.cell(
					organization1.name
				)
			).toBeVisible();
			await expect(
				accountOrganizationSelectorPage.organizationsTable.cell(
					organization2.name
				)
			).toBeVisible();
		}
	);

	test(
		'No Update or Manage Organizations permission',
		{tag: ['@LPD-28116']},
		async ({
			accountsPage,
			apiHelpers,
			context,
			page,
			usersAndOrganizationsPage,
		}) => {
			const organization1 =
				await apiHelpers.headlessAdminUser.postOrganization();

			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
				organization1.id,
				user.emailAddress
			);

			const companyId = await page.evaluate(() => {
				return Liferay.ThemeDisplay.getCompanyId();
			});

			const role = await apiHelpers.headlessAdminUser.postRole({
				name: getRandomString(),
				rolePermissions: [
					{
						actionIds: [
							'MANAGE_USERS',
							'UPDATE',
							'VIEW',
							'VIEW_ORGANIZATIONS',
						],
						primaryKey: companyId,
						resourceName: 'com.liferay.account.model.AccountEntry',
						scope: 1,
					},
					{
						actionIds: ['MANAGE_AVAILABLE_ACCOUNTS'],
						primaryKey: companyId,
						resourceName:
							'com.liferay.portal.kernel.model.Organization',
						scope: 1,
					},
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL'],
						primaryKey: companyId,
						resourceName:
							'com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet',
						scope: 1,
					},
				],
				roleType: 'organization',
			});

			await apiHelpers.headlessAdminUser.assignUserToOrganizationRole(
				role.id,
				user.id,
				organization1.id
			);

			const account = await apiHelpers.headlessAdminUser.postAccount();

			await apiHelpers.headlessAdminUser.postAccountOrganization(
				account.id,
				organization1.id
			);

			await usersAndOrganizationsPage.goToUsers();

			await (
				await usersAndOrganizationsPage.usersTableRowActions(
					`${user.alternateName}`
				)
			).click();

			const pagePromise = context.waitForEvent('page');

			await usersAndOrganizationsPage.impersonateUserMenuItem.click();

			const newPage = await pagePromise;
			accountsPage = new AccountsPage(newPage);

			await accountsPage.goto();
			await (
				await accountsPage.accountsTable.cellLink(account.name)
			).click();
			await accountsPage.organizationsTab.click();

			await expect(accountsPage.accountsTable.newButton).toHaveCount(0);
		}
	);
});

test(
	'Account admin can unassign organization from account',
	{tag: ['@LPD-30009']},
	async ({
		accountManagementWidgetPage,
		accountOrganizationsPage,
		apiHelpers,
		page,
		site,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const roleWithAccountAdminPermissions =
			await postRoleWithAccountAdminPermissions(apiHelpers, companyId);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: 'Account' + getRandomInt(),
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[userAccount.emailAddress]
		);

		const rolesResponse =
			await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

		const role = rolesResponse?.items?.filter(
			(role) => role.name === roleWithAccountAdminPermissions.name
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountRole(
			account.id,
			role[0].id,
			userAccount.id
		);

		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();

		await apiHelpers.headlessAdminUser.assignAccountToOrganization(
			account.id,
			organization.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: userAccount.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await accountManagementWidgetPage.accountNameLink(account.name).click();
		await accountManagementWidgetPage.organizationsTab.click();
		await accountOrganizationsPage.organizationsTable.selectAllItemsCheckbox.click();
		await accountOrganizationsPage.removeButton.click();

		await accountManagementWidgetPage.searchInput.waitFor();

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);
		await expect(
			accountOrganizationsPage.organizationsTable.cell(organization.name)
		).toHaveCount(0);
	}
);

test(
	'Account admin can unassign organizations in bulk',
	{tag: ['@LPD-30004']},
	async ({
		accountManagementWidgetPage,
		accountOrganizationsPage,
		apiHelpers,
		page,
		site,
	}) => {
		test.setTimeout(120000);

		page.on('dialog', (dialog) => dialog.accept());

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const roleWithAccountAdminPermissions =
			await postRoleWithAccountAdminPermissions(apiHelpers, companyId);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: 'Account' + getRandomInt(),
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[userAccount.emailAddress]
		);

		const rolesResponse =
			await apiHelpers.headlessAdminUser.getAccountRoles(account.id);

		const role = rolesResponse?.items?.filter(
			(role) => role.name === roleWithAccountAdminPermissions.name
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountRole(
			account.id,
			role[0].id,
			userAccount.id
		);

		const organization1 =
			await apiHelpers.headlessAdminUser.postOrganization();
		const organization2 =
			await apiHelpers.headlessAdminUser.postOrganization();
		const organization3 =
			await apiHelpers.headlessAdminUser.postOrganization();
		const organization4 =
			await apiHelpers.headlessAdminUser.postOrganization();

		await apiHelpers.headlessAdminUser.assignAccountToOrganization(
			account.id,
			organization1.id
		);
		await apiHelpers.headlessAdminUser.assignAccountToOrganization(
			account.id,
			organization2.id
		);
		await apiHelpers.headlessAdminUser.assignAccountToOrganization(
			account.id,
			organization3.id
		);
		await apiHelpers.headlessAdminUser.assignAccountToOrganization(
			account.id,
			organization4.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: userAccount.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await accountManagementWidgetPage.accountNameLink(account.name).click();
		await accountManagementWidgetPage.organizationsTab.click();
		await accountOrganizationsPage.organizationsTable.selectAllItemsCheckbox.click();
		await accountOrganizationsPage.removeButton.click();

		await accountManagementWidgetPage.searchInput.waitFor();

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountOrganizationsPage.organizationsTable.cell(organization1.name)
		).toHaveCount(0);
		await expect(
			accountOrganizationsPage.organizationsTable.cell(organization2.name)
		).toHaveCount(0);
		await expect(
			accountOrganizationsPage.organizationsTable.cell(organization3.name)
		).toHaveCount(0);
		await expect(
			accountOrganizationsPage.organizationsTable.cell(organization4.name)
		).toHaveCount(0);
	}
);

test(
	'Can change pagination in accounts',
	{tag: ['@LPD-45328']},
	async ({accountManagementWidgetPage, apiHelpers, page, site}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		const accounts = [];

		for (let i = 1; i <= 21; i++) {
			const account = await apiHelpers.headlessAdminUser.postAccount({
				name: `Account ${String(i).padStart(2, '0')}`,
				type: 'business',
			});

			accounts.push(account);

			await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
				account.id,
				[userAccount.emailAddress]
			);

			if (i === 1) {
				const rolesResponse =
					await apiHelpers.headlessAdminUser.getAccountRoles(
						account.id
					);

				const accountAdminRole = rolesResponse?.items?.filter(
					(role) => role.name === 'Account Administrator'
				);

				await apiHelpers.headlessAdminUser.assignUserToAccountRole(
					account.id,
					accountAdminRole[0].id,
					userAccount.id
				);
			}
		}

		await performLogout(page);
		await performLoginViaApi({page, screenName: userAccount.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await page.waitForLoadState('domcontentloaded');

		await setItemsPerPage(page, 20);

		for (const [index, account] of accounts.entries()) {
			if (index < 20) {
				await expect(
					accountManagementWidgetPage.accountCell(account.name)
				).toBeVisible();
			}
			else {
				await expect(
					accountManagementWidgetPage.accountCell(account.name)
				).toHaveCount(0);
			}
		}

		await nextPage(page);

		for (const [index, account] of accounts.entries()) {
			if (index < 20) {
				await expect(
					accountManagementWidgetPage.accountCell(account.name)
				).toHaveCount(0);
			}
			else {
				await expect(
					accountManagementWidgetPage.accountCell(account.name)
				).toBeVisible();
			}
		}

		await setItemsPerPage(page, 40);

		for (const account of accounts) {
			await expect(
				accountManagementWidgetPage.accountCell(account.name)
			).toBeVisible();
		}
	}
);

test(
	'Account Admin can view only accounts he is assigned to',
	{
		tag: ['@LPD-49715'],
	},
	async ({accountManagementWidgetPage, apiHelpers, page, site}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account: account1, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		const account2 = await apiHelpers.headlessAdminUser.postAccount();

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountNameLink(account1.name)
		).toBeVisible();
		await expect(
			await accountManagementWidgetPage.accountsTable.rowActions(
				account1.name
			)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toHaveCount(0);
	}
);

test(
	'Account Admin can edit an account he is assigned to',
	{
		tag: ['@LPD-49715'],
	},
	async ({
		accountManagementWidgetPage,
		apiHelpers,
		editAccountPage,
		page,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountNameLink(account.name)
		).toBeVisible();
		await expect(
			await accountManagementWidgetPage.accountsTable.rowActions(
				account.name
			)
		).toBeVisible();

		await accountManagementWidgetPage.accountNameLink(account.name).click();

		const name = getRandomString();

		await editAccountPage.accountNameInput.fill(name);

		await editAccountPage.saveButton.click();

		await waitForAlert(page);

		await editAccountPage.backButton.click();

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountNameLink(name)
		).toBeVisible();
		await expect(
			await accountManagementWidgetPage.accountsTable.rowActions(name)
		).toBeVisible();
	}
);

test(
	'Account Admin can add addresses to an account he is assigned to',
	{
		tag: ['@LPD-49715'],
	},
	async ({
		accountAddressesPage,
		accountManagementWidgetPage,
		apiHelpers,
		editAccountAddressPage,
		editAccountPage,
		page,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		const addresses = [
			{
				name: getRandomString(),
				type: 'Billing',
			},
			{
				name: getRandomString(),
				type: 'Shipping',
			},
			{
				name: getRandomString(),
				type: 'Billing and Shipping',
			},
		];

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await (
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).click();
		await editAccountPage.addressesTab.click();

		for (const address of addresses) {
			await accountAddressesPage.addressesTable.newButton.click();

			await editAccountAddressPage.addAddress(address);
		}
	}
);

test(
	'Account Admin can set default addresses to an account he is assigned to',
	{
		tag: ['@LPD-49715'],
	},
	async ({
		accountAddressesPage,
		accountDefaultAddressSelectorPage,
		accountManagementWidgetPage,
		apiHelpers,
		editAccountAddressPage,
		editAccountPage,
		page,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		const addresses = [
			{
				name: getRandomString(),
				type: 'Billing',
			},
			{
				name: getRandomString(),
				type: 'Shipping',
			},
			{
				name: getRandomString(),
				type: 'Billing and Shipping',
			},
		];

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await (
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).click();
		await editAccountPage.addressesTab.click();

		for (const address of addresses) {
			await accountAddressesPage.addressesTable.newButton.click();

			await editAccountAddressPage.addAddress(address);
		}

		await editAccountPage.detailsTab.click();
		await editAccountPage.setBillingDefaultAddressButton.click();
		await accountDefaultAddressSelectorPage.setDefaultAddress(
			addresses[0].name,
			'Billing'
		);

		await expect(
			editAccountPage.defaultBillingAddress(addresses[0].name)
		).toBeVisible();

		await editAccountPage.setShippingDefaultAddressButton.click();
		await accountDefaultAddressSelectorPage.setDefaultAddress(
			addresses[1].name,
			'Shipping'
		);

		await expect(
			editAccountPage.defaultShippingAddress(addresses[1].name)
		).toBeVisible();

		await editAccountPage.setBillingDefaultAddressButton.click();
		await accountDefaultAddressSelectorPage.setDefaultAddress(
			addresses[2].name,
			'Billing'
		);

		await expect(
			editAccountPage.defaultBillingAddress(addresses[2].name)
		).toBeVisible();

		await editAccountPage.setShippingDefaultAddressButton.click();
		await accountDefaultAddressSelectorPage.setDefaultAddress(
			addresses[2].name,
			'Shipping'
		);

		await expect(
			editAccountPage.defaultShippingAddress(addresses[2].name)
		).toBeVisible();
	}
);

test(
	'Account Admin can add users to an account he is assigned to',
	{
		tag: ['@LPD-49715'],
	},
	async ({
		accountManagementWidgetPage,
		accountUserSelectorPage,
		accountUsersPage,
		apiHelpers,
		editAccountPage,
		editUserPage,
		page,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await (
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).click();
		await editAccountPage.usersLink.click();
		await accountUsersPage.usersTable.newButton.click();
		await accountUsersPage.assignUserMenuItem.click();
		await accountUserSelectorPage.usersTable.newButton.click();

		const randomString = getRandomString();

		await editUserPage.emailAddressInput.fill(
			`${randomString}@liferay.com`
		);
		await editUserPage.firstNameInput.fill(randomString);
		await editUserPage.lastNameInput.fill(randomString);
		await editUserPage.screenNameInput.fill(randomString);

		await editUserPage.saveButton.click();

		await waitForAlert(page);

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await (
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).click();
		await editAccountPage.usersLink.click();

		await expect(
			accountUsersPage.usersTable.cell(randomString, false)
		).toBeVisible();
	}
);

test(
	'Account Admin can manage and invite users to an account he is assigned to',
	{
		tag: ['@LPD-49715', '@LPS-189070'],
	},
	async ({
		accountManagementWidgetPage,
		accountUserInvitePage,
		accountUserSelectorPage,
		accountUsersPage,
		apiHelpers,
		editAccountPage,
		page,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await (
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).click();
		await editAccountPage.usersLink.click();
		await accountUsersPage.usersTable.newButton.click();
		await accountUsersPage.inviteUserMenuItem.click();
		await accountUserInvitePage
			.emailAddressInput(accountUserInvitePage.firstEntry)
			.fill(`${getRandomString()}@liferay.com`);
		await accountUserInvitePage
			.emailAddressInput(accountUserInvitePage.firstEntry)
			.press('Enter');
		await accountUserInvitePage.inviteButton.click();

		await waitForAlert(page);

		await accountUsersPage.usersTable.newButton.click();
		await accountUsersPage.assignUserMenuItem.click();

		await expect(
			accountUserSelectorPage.usersTable.cell(userAccountAdmin.name)
		).toBeVisible();
		await expect(
			accountUserSelectorPage.usersTable.cell(user.name)
		).toBeVisible();
	}
);

test(
	'Account Admin can not add user with blocked domain to an account he is assigned to',
	{
		tag: ['@LPD-49715'],
	},
	async ({
		accountManagementWidgetPage,
		accountUserSelectorPage,
		accountUsersPage,
		apiHelpers,
		editAccountPage,
		editUserPage,
		emailDomainsInstanceSettingsPage,
		page,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		await emailDomainsInstanceSettingsPage.enableEmailDomainValidation(
			true,
			'yahoo.com,blocked.com'
		);

		try {
			await performLogout(page);
			await performLoginViaApi({
				page,
				screenName: userAccountAdmin.alternateName,
			});

			await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

			await (
				await accountManagementWidgetPage.accountsTable.cellLink(
					account.name
				)
			).click();
			await editAccountPage.usersLink.click();
			await accountUsersPage.usersTable.newButton.click();
			await accountUsersPage.assignUserMenuItem.click();
			await accountUserSelectorPage.usersTable.newButton.click();

			const randomString = getRandomString();

			await editUserPage.emailAddressInput.fill(
				`${getRandomString()}@blocked.com`
			);
			await editUserPage.firstNameInput.fill(randomString);
			await editUserPage.lastNameInput.fill(randomString);
			await editUserPage.screenNameInput.fill(randomString);

			await expect(editUserPage.emailAddressError).toContainText(
				'is a blocked domain.'
			);

			await editUserPage.saveButton.click();

			await waitForAlert(page, 'Your request failed to complete', {
				type: 'danger',
			});

			await editUserPage.cancelButton.click();

			await expect(
				accountUsersPage.usersTable.cell(randomString, false)
			).toHaveCount(0);
		}
		finally {
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			await emailDomainsInstanceSettingsPage.enableEmailDomainValidation(
				false,
				''
			);
		}
	}
);

test(
	'Account Admin can view all the users assigned to an account he is assigned to',
	{
		tag: ['@LPD-49715'],
	},
	async ({
		accountManagementWidgetPage,
		accountUsersPage,
		apiHelpers,
		editAccountPage,
		page,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user1.emailAddress]
		);

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user2.emailAddress]
		);

		const user3 = await apiHelpers.headlessAdminUser.postUserAccount();

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await (
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).click();
		await editAccountPage.usersLink.click();

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();
		await expect(accountUsersPage.usersTable.cell(user3.name)).toHaveCount(
			0
		);
	}
);

test(
	'Account Admin can search for users assigned to an account he is assigned to',
	{
		tag: ['@LPD-49715'],
	},
	async ({
		accountManagementWidgetPage,
		accountUsersPage,
		apiHelpers,
		editAccountPage,
		page,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user1.emailAddress]
		);

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user2.emailAddress]
		);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await (
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).click();
		await editAccountPage.usersLink.click();

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();

		await accountUsersPage.usersTable.search(getRandomString());

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toHaveCount(0);
		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await accountUsersPage.usersTable.search(userAccountAdmin.familyName);

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toBeVisible();
		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await accountUsersPage.usersTable.search(user1.familyName);

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toHaveCount(0);
		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await accountUsersPage.usersTable.search(user2.familyName);

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toHaveCount(0);
		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();

		await accountUsersPage.usersTable.search(userAccountAdmin.emailAddress);

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toBeVisible();
		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await accountUsersPage.usersTable.search(user1.emailAddress);

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toHaveCount(0);
		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await accountUsersPage.usersTable.search(user2.emailAddress);

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toHaveCount(0);
		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();

		await accountUsersPage.usersTable.search('');

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();
	}
);

test(
	'Account Admin can filter users assigned to an account he is assigned to by status',
	{
		tag: ['@LPD-49715'],
	},
	async ({
		accountManagementWidgetPage,
		accountUsersPage,
		apiHelpers,
		editAccountPage,
		page,
		site,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user1.emailAddress]
		);

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user2.emailAddress]
		);

		await accountUsersPage.goto();

		await accountUsersPage.usersTable.search(user2.name);
		await (
			await accountUsersPage.usersTable.rowActions(user2.name)
		).click();
		await accountUsersPage.deactivateButton.click();

		await waitForAlert(page);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await (
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).click();
		await editAccountPage.usersLink.click();

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);

		await expect(async () => {
			await accountUsersPage.usersTable.filterButton.click();

			await expect(
				accountUsersPage.usersTable.filterMenuItem('Inactive')
			).toBeVisible();
		}).toPass();

		await accountUsersPage.usersTable.filterMenuItem('Inactive').click();

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toHaveCount(0);
		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();

		await expect(async () => {
			await accountUsersPage.usersTable.filterButton.click();

			await expect(
				accountUsersPage.usersTable.filterMenuItem('Active')
			).toBeVisible();
		}).toPass();

		await accountUsersPage.usersTable.filterMenuItem('Active').click();

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(accountUsersPage.usersTable.cell(user2.name)).toHaveCount(
			0
		);
	}
);

test(
	'Account Admin can sort users assigned to an account he is assigned to by status',
	{
		tag: ['@LPD-49715'],
	},
	async ({
		accountManagementWidgetPage,
		accountUsersPage,
		apiHelpers,
		editAccountPage,
		page,
		site,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount({
			emailAddress: `AA${getRandomString()}@liferay.com`,
			familyName: `ZZ${getRandomString()}`,
			givenName: `AA${getRandomString()}`,
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user1.emailAddress]
		);

		const user2 = await apiHelpers.headlessAdminUser.postUserAccount({
			emailAddress: `ZZ${getRandomString()}@liferay.com`,
			familyName: `AA${getRandomString()}`,
			givenName: `ZZ${getRandomString()}`,
		});

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user2.emailAddress]
		);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await (
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).click();
		await editAccountPage.usersLink.click();

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user2.name)
		).toBeVisible();

		await accountUsersPage.usersTable.orderButton.click();
		await accountUsersPage.usersTable.orderMenuItem('First Name').click();

		await expect(accountUsersPage.usersTable.searchInput).toBeEditable();
		await expect(
			await accountUsersPage.usersTable.firstRow()
		).toContainText(user1.name);
		await expect(await accountUsersPage.usersTable.lastRow()).toContainText(
			user2.name
		);

		await accountUsersPage.usersTable.orderButton.click();
		await accountUsersPage.usersTable.orderMenuItem('Last Name').click();

		await expect(accountUsersPage.usersTable.searchInput).toBeEditable();
		await expect(
			await accountUsersPage.usersTable.firstRow()
		).toContainText(user2.name);
		await expect(await accountUsersPage.usersTable.lastRow()).toContainText(
			user1.name
		);

		await accountUsersPage.usersTable.orderButton.click();
		await accountUsersPage.usersTable
			.orderMenuItem('Email Address')
			.click();

		await expect(accountUsersPage.usersTable.searchInput).toBeEditable();
		await expect(
			await accountUsersPage.usersTable.firstRow()
		).toContainText(user1.name);
		await expect(await accountUsersPage.usersTable.lastRow()).toContainText(
			user2.name
		);
	}
);

test(
	'Account Admin can remove users from an account he is assigned to',
	{
		tag: ['@LPD-49715'],
	},
	async ({
		accountManagementWidgetPage,
		accountUsersPage,
		apiHelpers,
		editAccountPage,
		page,
		site,
	}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user1.emailAddress]
		);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await (
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).click();
		await editAccountPage.usersLink.click();

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(user1.name)
		).toBeVisible();

		await expect(async () => {
			await (
				await accountUsersPage.usersTable.rowActions(user1.name)
			).click();

			await expect(accountUsersPage.removeButton).toBeVisible();
		}).toPass();

		await accountUsersPage.removeButton.click();

		await waitForAlert(page);

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toBeVisible();
		await expect(accountUsersPage.usersTable.cell(user1.name)).toHaveCount(
			0
		);
	}
);

test(
	'Account Admin can assign roles to users of an account he is assigned to',
	{
		tag: ['@LPD-49715'],
	},
	async ({
		accountManagementWidgetPage,
		accountRoleSelectorPage,
		accountUsersPage,
		apiHelpers,
		editAccountPage,
		page,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		const {accountRole} = await addAccountRole(apiHelpers, account.id, []);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await (
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).click();
		await editAccountPage.usersLink.click();

		await expect(accountUsersPage.usersTable.cell(user.name)).toBeVisible();

		await expect(async () => {
			await (
				await accountUsersPage.usersTable.rowActions(user.name)
			).click();

			await expect(accountUsersPage.assignRolesMenuItem).toBeVisible();
		}).toPass();

		await accountUsersPage.assignRolesMenuItem.click();
		await accountRoleSelectorPage.selectRoles([
			'Account Administrator',
			accountRole.name,
		]);

		await expect(
			(await accountUsersPage.usersTable.row(1, user.name)).row.getByText(
				'Account Administrator'
			)
		).toBeVisible();
		await expect(
			(await accountUsersPage.usersTable.row(1, user.name)).row.getByText(
				accountRole.name
			)
		).toBeVisible();
	}
);

test(
	'Account Admin can unassign roles to users of an account he is assigned to',
	{
		tag: ['@LPD-49715'],
	},
	async ({
		accountManagementWidgetPage,
		accountRoleSelectorPage,
		accountUsersPage,
		apiHelpers,
		editAccountPage,
		page,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		const {accountRole} = await addAccountRole(apiHelpers, account.id, []);

		await apiHelpers.headlessAdminUser.assignUserToAccountRole(
			account.id,
			accountRole.id,
			user.id
		);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await (
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).click();
		await editAccountPage.usersLink.click();

		await expect(accountUsersPage.usersTable.cell(user.name)).toBeVisible();

		await expect(async () => {
			await (
				await accountUsersPage.usersTable.rowActions(user.name)
			).click();

			await expect(accountUsersPage.assignRolesMenuItem).toBeVisible();
		}).toPass();

		await accountUsersPage.assignRolesMenuItem.click();
		await accountRoleSelectorPage.selectRoles([accountRole.name], false);

		await expect(
			(await accountUsersPage.usersTable.row(1, user.name)).row.getByText(
				accountRole.name
			)
		).toHaveCount(0);
	}
);

test(
	'Account Admin with Add Account Entry permission can create a new account',
	{
		tag: ['@LPD-49715'],
	},
	async ({
		accountManagementWidgetPage,
		apiHelpers,
		editAccountPage,
		page,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountNameLink(account.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.newButton
		).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		const regularRole = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['ADD_ACCOUNT_ENTRY'],
					primaryKey: await page.evaluate(() => {
						return Liferay.ThemeDisplay.getCompanyId();
					}),
					resourceName: '90',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			regularRole.externalReferenceCode,
			userAccountAdmin.id
		);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountNameLink(account.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.newButton
		).toBeVisible();

		await accountManagementWidgetPage.accountsTable.newButton.click();

		const name = getRandomString();

		await editAccountPage.createAccount(apiHelpers, {name});
		await editAccountPage.backButton.click();

		await expect(
			accountManagementWidgetPage.accountsTable.cell(name)
		).toBeVisible();
		await expect(
			await accountManagementWidgetPage.accountsTable.cellLink(name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountNameLink(name)
		).toBeVisible();
		await expect(
			await accountManagementWidgetPage.accountsTable.rowActions(name)
		).toBeVisible();
	}
);

test(
	'Account Admin with Delete permission can delete an account he is assigned to',
	{
		tag: ['@LPD-49715'],
	},
	async ({accountManagementWidgetPage, apiHelpers, page, site}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountNameLink(account.name)
		).toBeVisible();
		await expect(
			await accountManagementWidgetPage.accountsTable.rowActions(
				account.name
			)
		).toBeVisible();
		await expect(async () => {
			await (
				await accountManagementWidgetPage.accountsTable.rowActions(
					account.name
				)
			).click();

			await expect(accountManagementWidgetPage.deleteButton).toHaveCount(
				0
			);
			await expect(
				accountManagementWidgetPage.deactivateButton
			).toHaveCount(0);
		}).toPass();

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		const regularRole = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['DELETE'],
					primaryKey: await page.evaluate(() => {
						return Liferay.ThemeDisplay.getCompanyId();
					}),
					resourceName: 'com.liferay.account.model.AccountEntry',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			regularRole.externalReferenceCode,
			userAccountAdmin.id
		);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountNameLink(account.name)
		).toBeVisible();
		await expect(
			await accountManagementWidgetPage.accountsTable.rowActions(
				account.name
			)
		).toBeVisible();
		await expect(async () => {
			await (
				await accountManagementWidgetPage.accountsTable.rowActions(
					account.name
				)
			).click();

			await expect(
				accountManagementWidgetPage.deleteButton
			).toBeVisible();

			await expect(
				accountManagementWidgetPage.deactivateButton
			).toHaveCount(0);
		}).toPass();

		await accountManagementWidgetPage.deleteButton.click();

		await waitForAlert(page);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account.name)
		).toHaveCount(0);
	}
);

test(
	'Account Admin with Deactivate permission can deactivate an account he is assigned to',
	{
		tag: ['@LPD-49715'],
	},
	async ({accountManagementWidgetPage, apiHelpers, page, site}) => {
		page.on('dialog', (dialog) => dialog.accept());

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountNameLink(account.name)
		).toBeVisible();
		await expect(
			await accountManagementWidgetPage.accountsTable.rowActions(
				account.name
			)
		).toBeVisible();
		await expect(async () => {
			await (
				await accountManagementWidgetPage.accountsTable.rowActions(
					account.name
				)
			).click();

			await expect(accountManagementWidgetPage.deleteButton).toHaveCount(
				0
			);
			await expect(
				accountManagementWidgetPage.deactivateButton
			).toHaveCount(0);
		}).toPass();

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		const regularRole = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['DEACTIVATE'],
					primaryKey: await page.evaluate(() => {
						return Liferay.ThemeDisplay.getCompanyId();
					}),
					resourceName: 'com.liferay.account.model.AccountEntry',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			regularRole.externalReferenceCode,
			userAccountAdmin.id
		);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountNameLink(account.name)
		).toBeVisible();
		await expect(
			await accountManagementWidgetPage.accountsTable.rowActions(
				account.name
			)
		).toBeVisible();
		await expect(async () => {
			await (
				await accountManagementWidgetPage.accountsTable.rowActions(
					account.name
				)
			).click();

			await expect(accountManagementWidgetPage.deleteButton).toHaveCount(
				0
			);
			await expect(
				accountManagementWidgetPage.deactivateButton
			).toBeVisible();
		}).toPass();

		await accountManagementWidgetPage.deactivateButton.click();

		await waitForAlert(page);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account.name)
		).toHaveCount(0);

		await expect(async () => {
			await accountManagementWidgetPage.accountsTable.filterButton.click();

			await accountManagementWidgetPage.accountsTable
				.filterMenuItem('Inactive')
				.click();

			await expect(
				accountManagementWidgetPage.accountsTable.cell(account.name)
			).toBeVisible();
		}).toPass();
	}
);

test(
	'Account Admin with Update permission can update users of an account he is assigned to',
	{
		tag: ['@LPD-49715'],
	},
	async ({
		accountManagementWidgetPage,
		accountUsersPage,
		apiHelpers,
		editAccountPage,
		editUserPage,
		page,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const {account, userAccountAdmin} =
			await initAccountAdministrator(apiHelpers);

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await (
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).click();
		await editAccountPage.usersLink.click();

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toBeVisible();
		await expect(
			await accountUsersPage.usersTable.cellLink(userAccountAdmin.name)
		).toBeVisible();
		await expect(accountUsersPage.usersTable.cell(user.name)).toBeVisible();
		await expect(
			await accountUsersPage.usersTable.cellLink(user.name)
		).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		const regularRole = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['UPDATE', 'VIEW'],
					primaryKey: await page.evaluate(() => {
						return Liferay.ThemeDisplay.getCompanyId();
					}),
					resourceName: 'com.liferay.portal.kernel.model.User',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			regularRole.externalReferenceCode,
			userAccountAdmin.id
		);

		await performLogout(page);
		await performLoginViaApi({
			page,
			screenName: userAccountAdmin.alternateName,
		});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await (
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).click();
		await editAccountPage.usersLink.click();

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toBeVisible();
		await expect(
			await accountUsersPage.usersTable.cellLink(userAccountAdmin.name)
		).toBeVisible();
		await expect(accountUsersPage.usersTable.cell(user.name)).toBeVisible();
		await expect(
			await accountUsersPage.usersTable.cellLink(user.name)
		).toBeVisible();

		await (await accountUsersPage.usersTable.cellLink(user.name)).click();

		const randomString = getRandomString();

		await editUserPage.firstNameInput.fill(randomString);
		await editUserPage.lastNameInput.fill(randomString);

		await editUserPage.saveButton.click();

		await waitForAlert(page);

		await editUserPage.backLink.click();

		await expect(
			accountUsersPage.usersTable.cell(userAccountAdmin.name)
		).toBeVisible();
		await expect(
			await accountUsersPage.usersTable.cellLink(userAccountAdmin.name)
		).toBeVisible();
		await expect(
			accountUsersPage.usersTable.cell(randomString, false)
		).toBeVisible();
		await expect(
			await accountUsersPage.usersTable.cellLink(randomString, 1, false)
		).toBeVisible();
		await expect(accountUsersPage.usersTable.cell(user.name)).toHaveCount(
			0
		);
	}
);

test(
	'Account Member can search accounts he is assigned to by name and ID',
	{
		tag: ['@LPD-49715', '@LRQA-73702'],
	},
	async ({accountManagementWidgetPage, apiHelpers, page, site}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const account1 = await apiHelpers.headlessAdminUser.postAccount();
		const account2 = await apiHelpers.headlessAdminUser.postAccount();
		const account3 = await apiHelpers.headlessAdminUser.postAccount();

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account1.id,
			[user.emailAddress]
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account2.id,
			[user.emailAddress]
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account3.name)
		).toHaveCount(0);

		await accountManagementWidgetPage.accountsTable.search(
			getRandomString()
		);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account3.name)
		).toHaveCount(0);

		await accountManagementWidgetPage.accountsTable.search(account1.name);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account3.name)
		).toHaveCount(0);

		await accountManagementWidgetPage.accountsTable.search(account2.name);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account3.name)
		).toHaveCount(0);

		await accountManagementWidgetPage.accountsTable.search(account3.name);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account3.name)
		).toHaveCount(0);

		await accountManagementWidgetPage.accountsTable.search('');

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account3.name)
		).toHaveCount(0);

		await accountManagementWidgetPage.accountsTable.search(
			String(getRandomInt())
		);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account3.name)
		).toHaveCount(0);

		await accountManagementWidgetPage.accountsTable.search(
			String(account1.id)
		);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account3.name)
		).toHaveCount(0);

		await accountManagementWidgetPage.accountsTable.search(
			String(account2.id)
		);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account3.name)
		).toHaveCount(0);

		await accountManagementWidgetPage.accountsTable.search(
			String(account3.id)
		);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account3.name)
		).toHaveCount(0);
	}
);

test(
	'Account Member can filter accounts he is assigned to by status',
	{
		tag: ['@LPD-49715'],
	},
	async ({accountManagementWidgetPage, apiHelpers, page, site}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const account1 = await apiHelpers.headlessAdminUser.postAccount();
		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			status: 5,
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account1.id,
			[user.emailAddress]
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account2.id,
			[user.emailAddress]
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toHaveCount(0);

		await expect(async () => {
			await accountManagementWidgetPage.accountsTable.filterButton.click();

			await expect(
				accountManagementWidgetPage.accountsTable.filterMenuItem(
					'Inactive'
				)
			).toBeVisible();
		}).toPass();

		await accountManagementWidgetPage.accountsTable
			.filterMenuItem('Inactive')
			.click();

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toBeVisible();

		await expect(async () => {
			await accountManagementWidgetPage.accountsTable.filterButton.click();

			await expect(
				accountManagementWidgetPage.accountsTable.filterMenuItem('All')
			).toBeVisible();
		}).toPass();

		await accountManagementWidgetPage.accountsTable
			.filterMenuItem('All')
			.click();

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toBeVisible();

		await expect(async () => {
			await accountManagementWidgetPage.accountsTable.filterButton.click();

			await expect(
				accountManagementWidgetPage.accountsTable.filterMenuItem(
					'Active'
				)
			).toBeVisible();
		}).toPass();

		await accountManagementWidgetPage.accountsTable
			.filterMenuItem('Active')
			.click();

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toHaveCount(0);
	}
);

test(
	'Account Member can filter accounts he is assigned to by type',
	{
		tag: ['@LPD-49715'],
	},
	async ({accountManagementWidgetPage, apiHelpers, page, site}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const account1 = await apiHelpers.headlessAdminUser.postAccount();
		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			type: 'person',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account1.id,
			[user.emailAddress]
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account2.id,
			[user.emailAddress]
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toBeVisible();

		await expect(async () => {
			await accountManagementWidgetPage.accountsTable.filterButton.click();

			await expect(
				accountManagementWidgetPage.accountsTable.filterMenuItem(
					'Business'
				)
			).toBeVisible();
		}).toPass();

		await accountManagementWidgetPage.accountsTable
			.filterMenuItem('Business')
			.click();

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toHaveCount(0);

		await expect(async () => {
			await accountManagementWidgetPage.accountsTable.filterButton.click();

			await expect(
				accountManagementWidgetPage.accountsTable.filterMenuItem(
					'Person'
				)
			).toBeVisible();
		}).toPass();

		await accountManagementWidgetPage.accountsTable
			.filterMenuItem('Person')
			.click();

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toBeVisible();

		await expect(async () => {
			await accountManagementWidgetPage.accountsTable.filterButton.click();

			await expect(
				accountManagementWidgetPage.accountsTable.filterMenuItem(
					'Supplier'
				)
			).toBeVisible();
		}).toPass();

		await accountManagementWidgetPage.accountsTable
			.filterMenuItem('Supplier')
			.click();

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toHaveCount(0);
	}
);

test(
	'Account Member with Update permission can update an account he is assigned to',
	{
		tag: ['@LPD-49715'],
	},
	async ({
		accountManagementWidgetPage,
		apiHelpers,
		editAccountPage,
		page,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const account = await apiHelpers.headlessAdminUser.postAccount();

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account.name)
		).toBeVisible();
		await expect(
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		const {accountRole} = await addAccountRole(apiHelpers, account.id, [
			{
				actionIds: ['UPDATE'],
				primaryKey: '0',
				resourceName: 'com.liferay.account.model.AccountEntry',
				scope: 3,
			},
		]);

		await apiHelpers.headlessAdminUser.assignUserToAccountRole(
			account.id,
			accountRole.id,
			user.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account.name)
		).toBeVisible();
		await expect(
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).toBeVisible();

		await (
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).click();

		const randomString = getRandomString();

		await editAccountPage.accountNameInput.fill(randomString);

		await editAccountPage.saveButton.click();

		await waitForAlert(page);

		await editAccountPage.backButton.click();

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(randomString)
		).toBeVisible();
		await expect(
			await accountManagementWidgetPage.accountsTable.cellLink(
				randomString
			)
		).toBeVisible();
	}
);

test(
	'Account Member with Invite User permission can invite users to an account he is assigned to',
	{
		tag: ['@LPD-49715', '@LPS-189070'],
	},
	async ({
		accountManagementWidgetPage,
		accountUserInvitePage,
		accountUsersPage,
		apiHelpers,
		editAccountPage,
		page,
		site,
	}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const account = await apiHelpers.headlessAdminUser.postAccount();

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[user.emailAddress]
		);

		const {accountRole} = await addAccountRole(apiHelpers, account.id, [
			{
				actionIds: ['INVITE_USER', 'UPDATE', 'VIEW_USERS'],
				primaryKey: '0',
				resourceName: 'com.liferay.account.model.AccountEntry',
				scope: 3,
			},
		]);

		await apiHelpers.headlessAdminUser.assignUserToAccountRole(
			account.id,
			accountRole.id,
			user.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await (
			await accountManagementWidgetPage.accountsTable.cellLink(
				account.name
			)
		).click();
		await editAccountPage.usersLink.click();
		await accountUsersPage.usersTable.newButton.click();
		await accountUserInvitePage
			.emailAddressInput(accountUserInvitePage.firstEntry)
			.fill(`${getRandomString()}@liferay.com`);
		await accountUserInvitePage
			.emailAddressInput(accountUserInvitePage.firstEntry)
			.press('Enter');
		await accountUserInvitePage.inviteButton.click();

		await waitForAlert(page);
	}
);

test(
	'A user with View permission can view accounts but not be able to perform any other actions',
	{
		tag: ['@LPD-49715'],
	},
	async ({accountManagementWidgetPage, apiHelpers, page, site}) => {
		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName:
						'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const account1 = await apiHelpers.headlessAdminUser.postAccount();
		const account2 = await apiHelpers.headlessAdminUser.postAccount();

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toHaveCount(0);

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		const regularRole = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['VIEW'],
					primaryKey: await page.evaluate(() => {
						return Liferay.ThemeDisplay.getCompanyId();
					}),
					resourceName: 'com.liferay.account.model.AccountEntry',
					scope: 1,
				},
			],
			roleType: 'regular',
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			regularRole.externalReferenceCode,
			user.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account1.name)
		).toBeVisible();
		await expect(
			await accountManagementWidgetPage.accountsTable.cellLink(
				account1.name
			)
		).toHaveCount(0);

		await (
			await accountManagementWidgetPage.accountsTable.rowActions(
				account1.name
			)
		).click();

		await expect(accountManagementWidgetPage.deactivateButton).toHaveCount(
			0
		);
		await expect(accountManagementWidgetPage.deleteButton).toHaveCount(0);
		await expect(accountManagementWidgetPage.editButton).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.manageOrganizationsButton
		).toHaveCount(0);
		await expect(accountManagementWidgetPage.manageUsersButton).toHaveCount(
			0
		);
		await expect(
			accountManagementWidgetPage.selectAccountButton
		).toBeVisible();

		await expect(
			accountManagementWidgetPage.accountsTable.cell(account2.name)
		).toBeVisible();
		await expect(
			await accountManagementWidgetPage.accountsTable.cellLink(
				account2.name
			)
		).toHaveCount(0);

		await (
			await accountManagementWidgetPage.accountsTable.rowActions(
				account2.name
			)
		).click();

		await expect(accountManagementWidgetPage.deactivateButton).toHaveCount(
			0
		);
		await expect(accountManagementWidgetPage.deleteButton).toHaveCount(0);
		await expect(accountManagementWidgetPage.editButton).toHaveCount(0);
		await expect(
			accountManagementWidgetPage.manageOrganizationsButton
		).toHaveCount(0);
		await expect(accountManagementWidgetPage.manageUsersButton).toHaveCount(
			0
		);
		await expect(
			accountManagementWidgetPage.selectAccountButton
		).toBeVisible();
	}
);
