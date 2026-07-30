/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../fixtures/accountsPagesTest';
import {commercePagesTest} from '../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {PagesAdminPage} from '../../../pages/layout-admin-web/PagesAdminPage';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

export const test = mergeTests(
	accountsPagesTest,
	commercePagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	usersAndOrganizationsPagesTest
);

async function createWidgetPage(apiHelpers: any, siteId: number | string) {
	return await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getWidgetDefinition({
				id: getRandomString(),
				widgetName:
					'com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet',
			}),
		]),
		siteId,
		title: getRandomString(),
	});
}

async function reloadUntil(
	page: Page,
	url: string,
	assertion: () => Promise<void>
) {
	await expect(async () => {
		await page.goto(url);

		await assertion();
	}).toPass({timeout: 30000});
}

test(
	'User can change selected accounts',
	{tag: ['@LPD-28846']},
	async ({
		accountEntriesManagementPortletPage,
		apiHelpers,
		commerceAdminChannelsPage,
		page,
		site,
	}) => {
		test.setTimeout(120000);

		const layout = await createWidgetPage(apiHelpers, site.id);

		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({
				siteGroupId: site.id,
			});

		await commerceAdminChannelsPage.changeCommerceChannelSiteType(
			channel.name,
			'B2B'
		);

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const account1 = await apiHelpers.headlessAdminUser.postAccount({
			name: 'account' + getRandomInt(),
			type: 'business',
		});
		await apiHelpers.headlessAdminUser.postAccount({
			name: 'account' + getRandomInt(),
			type: 'business',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'role' + getRandomInt(),
			rolePermissions: [
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.account.model.AccountEntry',
					scope: 1,
				},
			],
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		await expect(async () => {
			await page.goto(
				`/web/${site.name}/${layout.friendlyUrlPath}?doAsUserId=${user.id}`
			);

			await expect(
				accountEntriesManagementPortletPage.searchInput
			).toBeEnabled({timeout: 2000});
		}).toPass();

		await accountEntriesManagementPortletPage.selectAccount(account1.name);

		await page.reload();

		await expect(
			await accountEntriesManagementPortletPage.accountEntriesTableRowSelectedCheck(
				account1.name
			)
		).toBeVisible();
	}
);

test(
	'User can search for account',
	{tag: ['@LPD-81993']},
	async ({accountManagementWidgetPage, apiHelpers, page, site}) => {
		const layout = await createWidgetPage(apiHelpers, site.id);

		const acmeIncName = `Acme Inc ${getRandomInt()}`;
		const liferayName = `Liferay ${getRandomInt()}`;
		const southBayName = `South Bay Auto Parts ${getRandomInt()}`;

		for (const accountName of [acmeIncName, liferayName, southBayName]) {
			await apiHelpers.headlessAdminUser.postAccount({
				name: accountName,
				type: 'business',
			});
		}

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await accountManagementWidgetPage.accountsTable.search(southBayName);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(acmeIncName)
		).not.toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(liferayName)
		).not.toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(southBayName)
		).toBeVisible();

		await accountManagementWidgetPage.accountsTable.search(acmeIncName);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(acmeIncName)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(liferayName)
		).not.toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(southBayName)
		).not.toBeVisible();

		await accountManagementWidgetPage.accountsTable.search('');

		await expect(
			accountManagementWidgetPage.accountsTable.cell(acmeIncName)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(liferayName)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(southBayName)
		).toBeVisible();
	}
);

test(
	'User can set active account',
	{tag: ['@LPD-81993']},
	async ({accountEntriesManagementPortletPage, apiHelpers, page, site}) => {
		const layout = await createWidgetPage(apiHelpers, site.id);

		const account1Name = `Account Name 1 ${getRandomInt()}`;
		const account2Name = `Account Name 2 ${getRandomInt()}`;

		const account1 = await apiHelpers.headlessAdminUser.postAccount({
			name: account1Name,
			type: 'business',
		});

		const account2 = await apiHelpers.headlessAdminUser.postAccount({
			name: account2Name,
			type: 'business',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account1.id,
			[user.emailAddress]
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account2.id,
			[user.emailAddress]
		);

		await expect(async () => {
			await page.goto(
				`/web/${site.name}/${layout.friendlyUrlPath}?doAsUserId=${user.id}`
			);

			await expect(
				accountEntriesManagementPortletPage.searchInput
			).toBeEnabled({timeout: 2000});
		}).toPass();

		await expect(
			await accountEntriesManagementPortletPage.accountEntriesTableRowSelectedCheck(
				account1Name
			)
		).toBeVisible();

		await accountEntriesManagementPortletPage.selectAccount(account2Name);

		await page.reload();

		await expect(
			await accountEntriesManagementPortletPage.accountEntriesTableRowSelectedCheck(
				account2Name
			)
		).toBeVisible();
	}
);

test(
	'Allowed account type setting is site-wide',
	{tag: ['@LPD-81993']},
	async ({
		accountInstanceSettingsPage,
		accountManagementWidgetPage,
		apiHelpers,
		editAccountPage,
		page,
		site,
	}) => {
		test.setTimeout(120000);

		const layout1 = await createWidgetPage(apiHelpers, site.id);

		const site2 = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const layout2 = await createWidgetPage(apiHelpers, site2.id);

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'role' + getRandomInt(),
			rolePermissions: [
				{
					actionIds: ['ADD_ACCOUNT_ENTRY'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
			],
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		const businessAccount = await apiHelpers.headlessAdminUser.postAccount({
			name: 'Business Account ' + getRandomInt(),
			type: 'business',
		});
		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			businessAccount.id,
			[user.emailAddress]
		);

		const personAccount = await apiHelpers.headlessAdminUser.postAccount({
			name: 'Person Account ' + getRandomInt(),
			type: 'person',
		});
		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			personAccount.id,
			[user.emailAddress]
		);

		await accountInstanceSettingsPage.setAllowedAccountType(
			site.friendlyUrlPath,
			'Person'
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await reloadUntil(
			page,
			`/web/${site.name}/${layout1.friendlyUrlPath}`,
			async () => {
				await expect(
					accountManagementWidgetPage.accountsTable.cell(
						businessAccount.name
					)
				).not.toBeVisible({timeout: 5000});
			}
		);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(personAccount.name)
		).toBeVisible();

		await page.goto(`/web/${site2.name}/${layout2.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(businessAccount.name)
		).toBeVisible();
		await expect(
			accountManagementWidgetPage.accountsTable.cell(personAccount.name)
		).toBeVisible();

		await accountManagementWidgetPage.accountsTable.newButton.click();

		await editAccountPage.createAccount(apiHelpers, {
			name: 'Business Account 2 ' + getRandomInt(),
		});

		await editAccountPage.backButton.click();

		await page.goto(`/web/${site2.name}/${layout2.friendlyUrlPath}`);

		await accountManagementWidgetPage.accountsTable.newButton.click();

		await editAccountPage.createAccount(apiHelpers, {
			name: 'Person Account 2 ' + getRandomInt(),
			type: 'person',
		});
	}
);

test(
	'Account modals can be custom styled',
	{tag: ['@LPD-81993']},
	async ({apiHelpers, page, site}) => {
		test.setTimeout(120000);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: 'Account Name ' + getRandomInt(),
			type: 'business',
		});

		const layout = await createWidgetPage(apiHelpers, site.id);

		const pagesAdminPage = new PagesAdminPage(page);

		await pagesAdminPage.gotoPagesConfiguration(site.friendlyUrlPath);

		await page.getByRole('button', {name: 'Change Current Theme'}).click();

		await page
			.frameLocator('iframe[title="Available Themes"]')
			.getByRole('button', {name: /Select CMS/})
			.click();

		await page.getByRole('button', {exact: true, name: 'Save'}).click();

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await page.getByRole('link', {exact: true, name: account.name}).click();

		await page.getByRole('link', {exact: true, name: 'Users'}).click();

		await expect(page.getByRole('button', {name: 'New'})).toBeVisible();

		await expect(
			page.locator('link.lfr-css-file[href*="/o/cms-theme/"]').first()
		).toBeAttached();
	}
);

test(
	'User can only add business accounts when allowed type is business',
	{tag: ['@LPD-81993']},
	async ({
		accountInstanceSettingsPage,
		accountManagementWidgetPage,
		apiHelpers,
		editAccountPage,
		page,
		site,
	}) => {
		test.setTimeout(120000);

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'role' + getRandomInt(),
			rolePermissions: [
				{
					actionIds: ['ADD_ACCOUNT_ENTRY'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
			],
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		await accountInstanceSettingsPage.setAllowedAccountType(
			site.friendlyUrlPath,
			'Business'
		);

		const layout = await createWidgetPage(apiHelpers, site.id);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await reloadUntil(
			page,
			`/web/${site.name}/${layout.friendlyUrlPath}`,
			async () => {
				await accountManagementWidgetPage.accountsTable.newButton.click();

				await expect(
					editAccountPage.typeInput.locator('option[value="person"]')
				).not.toBeAttached({timeout: 5000});
			}
		);

		const accountName = 'Business Account Only ' + getRandomInt();

		await editAccountPage.accountNameInput.fill(accountName);

		await editAccountPage.typeInput.selectOption('business');
		await editAccountPage.saveButton.click();

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(accountName)
		).toBeVisible();
	}
);

test(
	'User can only add person accounts when allowed type is person',
	{tag: ['@LPD-81993']},
	async ({
		accountInstanceSettingsPage,
		accountManagementWidgetPage,
		apiHelpers,
		editAccountPage,
		page,
		site,
	}) => {
		test.setTimeout(120000);

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'role' + getRandomInt(),
			rolePermissions: [
				{
					actionIds: ['ADD_ACCOUNT_ENTRY'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
			],
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		await accountInstanceSettingsPage.setAllowedAccountType(
			site.friendlyUrlPath,
			'Person'
		);

		const layout = await createWidgetPage(apiHelpers, site.id);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await reloadUntil(
			page,
			`/web/${site.name}/${layout.friendlyUrlPath}`,
			async () => {
				await accountManagementWidgetPage.accountsTable.newButton.click();

				await expect(
					editAccountPage.typeInput.locator(
						'option[value="business"]'
					)
				).not.toBeAttached({timeout: 5000});
			}
		);

		const accountName = 'Person Account Only ' + getRandomInt();

		await editAccountPage.accountNameInput.fill(accountName);

		await editAccountPage.typeInput.selectOption('person');
		await editAccountPage.saveButton.click();

		await performLogout(page);
		await performLoginViaApi({page, screenName: 'test'});

		await page.goto(`/web/${site.name}/${layout.friendlyUrlPath}`);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(accountName)
		).toBeVisible();
	}
);

test(
	'User can only view business accounts when allowed type is business',
	{tag: ['@LPD-81993']},
	async ({
		accountInstanceSettingsPage,
		accountManagementWidgetPage,
		apiHelpers,
		page,
		site,
	}) => {
		test.setTimeout(120000);

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const businessAccount = await apiHelpers.headlessAdminUser.postAccount({
			name: 'Business Account ' + getRandomInt(),
			type: 'business',
		});
		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			businessAccount.id,
			[user.emailAddress]
		);

		const personAccount = await apiHelpers.headlessAdminUser.postAccount({
			name: 'Person Account ' + getRandomInt(),
			type: 'person',
		});
		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			personAccount.id,
			[user.emailAddress]
		);

		await accountInstanceSettingsPage.setAllowedAccountType(
			site.friendlyUrlPath,
			'Business'
		);

		const layout = await createWidgetPage(apiHelpers, site.id);

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await reloadUntil(
			page,
			`/web/${site.name}/${layout.friendlyUrlPath}`,
			async () => {
				await expect(
					accountManagementWidgetPage.accountsTable.cell(
						personAccount.name
					)
				).not.toBeVisible({timeout: 5000});
			}
		);

		await expect(
			accountManagementWidgetPage.accountsTable.cell(businessAccount.name)
		).toBeVisible();
	}
);
