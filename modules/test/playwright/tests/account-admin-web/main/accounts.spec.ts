/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {accountsPagesTest} from '../../../fixtures/accountsPagesTest';
import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {customFieldsPagesTest} from '../../../fixtures/customFieldsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {serverAdministrationPageTest} from '../../../fixtures/serverAdministrationPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {createCategories} from '../../../helpers/CreateCategories';
import {TCustomField} from '../../../helpers/CustomFieldTypesHelper';
import getGlobalSiteId from '../../../utils/getGlobalSiteId';
import getRandomString from '../../../utils/getRandomString';
import {nextPage, setItemsPerPage} from '../../../utils/pagination';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {tagsPagesTest} from '../../asset-tags-admin-web/main/fixtures/tagsAdminPagesTest';

export const test = mergeTests(
	accountsPagesTest,
	apiHelpersTest,
	applicationsMenuPageTest,
	customFieldsPagesTest,
	dataApiHelpersTest,
	loginTest(),
	serverAdministrationPageTest,
	tagsPagesTest,
	usersAndOrganizationsPagesTest
);

test('LPD-18485 Update account contact information fields', async ({
	accountsPage,
	apiHelpers,
	editAccountContactInformationPage,
	editAccountContactPage,
	editAccountPage,
	page,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: 'test',
		type: 'business',
	});

	await accountsPage.goto();

	await (await accountsPage.accountsTable.cellLink(account.name)).click();
	await editAccountPage.contactLink.click();
	await editAccountContactPage.contactInformationLink.click();
	await editAccountContactInformationPage.updateContactInformation(
		'facebookInput',
		'jabberInput',
		'skypeInput',
		'smsInput',
		'twitterInput'
	);

	await expect(
		page.getByText('Success:Your request completed successfully.')
	).toBeVisible();

	await page.reload();

	await expect(editAccountContactInformationPage.facebookInput).toHaveValue(
		'facebookInput'
	);
});

test('LPD-18484 Add account contact address', async ({
	accountContactAddressPage,
	accountsPage,
	apiHelpers,
	editAccountContactAddressPage,
	editAccountPage,
	page,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: 'test',
		type: 'business',
	});

	await accountsPage.goto();

	await (await accountsPage.accountsTable.cellLink(account.name)).click();
	await editAccountPage.contactLink.click();
	await accountContactAddressPage.addAddressesButton.click();
	await editAccountContactAddressPage.updateAddress(
		'city',
		'United States',
		getRandomString(),
		'California',
		'address1'
	);

	await expect(
		page.getByText('Success:Your request completed successfully.')
	).toBeVisible();

	await expect(
		await editAccountContactAddressPage.addressDisplay('address1city')
	).toBeVisible();
});

test('LPD-18482 Add account phone', async ({
	accountsPage,
	apiHelpers,
	editAccountContactInformationPage,
	editAccountContactPage,
	editAccountPage,
	editAccountPhonePage,
	page,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: 'test',
		type: 'business',
	});

	await accountsPage.goto();

	await (await accountsPage.accountsTable.cellLink(account.name)).click();
	await editAccountPage.contactLink.click();
	await editAccountContactPage.contactInformationLink.click();
	await editAccountContactInformationPage.addPhoneNumbersButton.click();
	await editAccountPhonePage.updatePhoneNumber('111-111-1111');

	await expect(
		page.getByText('Success:Your request completed successfully.')
	).toBeVisible();

	await expect(page.getByRole('cell', {name: '111-111-1111'})).toBeVisible();
});

test('LPD-18483 Add account email address', async ({
	accountsPage,
	apiHelpers,
	editAccountContactInformationPage,
	editAccountContactPage,
	editAccountEmailAddressPage,
	editAccountPage,
	page,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: 'test',
		type: 'business',
	});

	await accountsPage.goto();

	await (await accountsPage.accountsTable.cellLink(account.name)).click();
	await editAccountPage.contactLink.click();
	await editAccountContactPage.contactInformationLink.click();
	await editAccountContactInformationPage.addEmailAddressesButton.click();
	await editAccountEmailAddressPage.updateEmailAddress(
		'emailAddress@liferay.com'
	);

	await expect(
		page.getByText('Success:Your request completed successfully.')
	).toBeVisible();

	await expect(
		page.getByRole('cell', {name: 'emailAddress@liferay.com'})
	).toBeVisible();
});

test('LPD-18484 Add account website', async ({
	accountsPage,
	apiHelpers,
	editAccountContactInformationPage,
	editAccountContactPage,
	editAccountPage,
	editAccountWebsitePage,
	page,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: 'test',
		type: 'business',
	});

	await accountsPage.goto();

	await (await accountsPage.accountsTable.cellLink(account.name)).click();
	await editAccountPage.contactLink.click();
	await editAccountContactPage.contactInformationLink.click();
	await editAccountContactInformationPage.addWebsitesButton.click();
	await editAccountWebsitePage.updateWebsite('https://www.website.com');

	await expect(
		page.getByText('Success:Your request completed successfully.')
	).toBeVisible();

	await expect(
		page.getByRole('cell', {name: 'https://www.website.com'})
	).toBeVisible();
});

test('LPD-28161 Can view role and organization name escaped', async ({
	accountRolesPage,
	accountUsersPage,
	accountsPage,
	apiHelpers,
	editAccountPage,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	const roleName = 'My title<script>confirm("compromised")</script>';

	const accountRole =
		await apiHelpers.headlessAdminUser.postAccountAccountRoles(account.id, {
			name: roleName,
		});

	apiHelpers.data.push({id: accountRole.id, type: 'role'});

	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	await apiHelpers.headlessAdminUser.postAccountUserAccountByEmailAddress(
		account.id,
		[accountRole.id],
		[user.emailAddress]
	);

	const organizationName = 'My org1<script>confirm("compromised")</script>';

	const organization = await apiHelpers.headlessAdminUser.postOrganization({
		name: organizationName,
	});

	await apiHelpers.headlessAdminUser.postOrganizationAccounts(
		Number(organization.id),
		[account.id]
	);

	await accountsPage.goto();

	await expect(
		await accountsPage.organizationName(organizationName)
	).toBeVisible();

	await (await accountsPage.accountsTable.cellLink(account.name)).click();
	await editAccountPage.rolesLink.click();

	await expect(await accountRolesPage.roleName(roleName)).toBeVisible();

	await editAccountPage.usersLink.click();

	await expect(await accountUsersPage.roleName(roleName)).toBeVisible();
});

test('LPD-32045 All account entry can be seen by admin user', async ({
	accountsPage,
	apiHelpers,
	page,
}) => {
	const account1 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});
	const account2 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});
	const account3 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	const role =
		await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

	await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
		role.externalReferenceCode,
		userAccount.id
	);

	await performLogout(page);
	await performLogin(page, userAccount.alternateName);

	try {
		await accountsPage.goto();

		await expect(
			await accountsPage.accountsTable.cellLink(account1.name)
		).toHaveCount(1);
		await expect(
			await accountsPage.accountsTable.cellLink(account2.name)
		).toHaveCount(1);
		await expect(
			await accountsPage.accountsTable.cellLink(account3.name)
		).toHaveCount(1);
	}
	finally {
		await performLogout(page);
		await performLogin(page, 'test');
	}
});

test('LPD-33636 Email address is not deleted by saving in the UI', async ({
	accountsPage,
	apiHelpers,
	applicationsMenuPage,
	editAccountPage,
	page,
	serverAdministrationPage,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount();

	await applicationsMenuPage.goToServerAdministration();

	const emailAddress = getRandomString() + '@liferay.com';

	const script = `
		import com.liferay.account.model.*;
		import com.liferay.account.service.*;
		AccountEntry account = AccountEntryLocalServiceUtil.fetchAccountEntry(${account.id});
		account.setEmailAddress("${emailAddress}");
		AccountEntryLocalServiceUtil.updateAccountEntry(account);
    `;

	await serverAdministrationPage.executeScript(script);

	await accountsPage.goto();
	await (await accountsPage.accountsTable.cellLink(account.name)).click();
	await editAccountPage.saveButton.click();
	await waitForAlert(page);

	await applicationsMenuPage.goToServerAdministration();

	const fetchScript = `
		import com.liferay.account.model.*; 
		import com.liferay.account.service.*;
		AccountEntry account = AccountEntryLocalServiceUtil.fetchAccountEntry(${account.id});
		out.println(account);
	`;

	await serverAdministrationPage.executeScript(fetchScript);
	await expect(
		page.getByText('"emailAddress": "' + emailAddress)
	).toBeVisible();
});

test('LPD-44526 Can activate and deactivate an account', async ({
	accountsPage,
	apiHelpers,
	page,
}) => {
	page.on('dialog', (dialog) => {
		dialog.accept().catch(() => {});
	});

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await accountsPage.goto();

	await expect(async () => {
		await (
			await accountsPage.accountsTable.rowActions(account.name)
		).click();

		await expect(accountsPage.deactivateButton).toBeVisible({timeout: 300});
	}).toPass();

	await accountsPage.deactivateButton.click();

	await waitForAlert(page);

	await expect(accountsPage.accountsTable.cell(account.name)).toHaveCount(0);

	await accountsPage.accountsTable.filterButton.click();
	await accountsPage.accountsTable.filterMenuItem('Inactive').click();

	await expect(accountsPage.accountsTable.cell(account.name)).toBeVisible();

	await (await accountsPage.accountsTable.rowActions(account.name)).click();
	await accountsPage.activateButton.click();

	await expect(accountsPage.accountsTable.cell(account.name)).toHaveCount(0);

	await accountsPage.accountsTable.filterButton.click();
	await accountsPage.accountsTable.filterMenuItem('Active').click();

	await expect(accountsPage.accountsTable.cell(account.name)).toBeVisible();
});

test('LPD-44526 Can deactivate and activate accounts in bulk', async ({
	accountsPage,
	apiHelpers,
	page,
}) => {
	const accounts = [];

	for (let i = 1; i < 7; i++) {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: `Account ${i}`,
			type: 'business',
		});

		accounts.push(account);
	}

	await accountsPage.goto();

	const accountNames: string[] = [
		accounts[0].name,
		accounts[2].name,
		accounts[4].name,
	];

	for (const name of accountNames) {
		await (await accountsPage.accountsTable.rowCheckbox(name)).click();
	}

	page.on('dialog', async (dialog) => await dialog.accept());

	await accountsPage.deactivateButton.click();

	await waitForAlert(page);

	for (const name of accountNames) {
		try {
			await accountsPage.accountsTable.cellLink(name);
		}
		catch (error) {
			expect(error).toBeDefined();
		}
	}

	await accountsPage.changeFilter('Inactive');

	for (const name of accountNames) {
		await expect(
			(await accountsPage.accountsTable.row(1, name, true)).row
		).toBeVisible();
	}

	for (const name of accountNames) {
		await (await accountsPage.accountsTable.rowCheckbox(name)).click();
	}

	await accountsPage.activateButton.click();

	await waitForAlert(page);

	await expect(accountsPage.noAccountsMessage).toBeVisible();

	await accountsPage.changeFilter('Active');

	for (const account of accounts) {
		await expect(
			(await accountsPage.accountsTable.row(1, account.name, true)).row
		).toBeVisible();
	}
});

test('LPD-45897 Can delete an account', async ({
	accountsPage,
	apiHelpers,
	page,
}) => {
	page.on('dialog', (dialog) => {
		dialog.accept().catch(() => {});
	});

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await accountsPage.goto();

	await expect(async () => {
		await (
			await accountsPage.accountsTable.rowActions(account.name)
		).click();

		await expect(accountsPage.deleteButton).toBeVisible({timeout: 300});
	}).toPass();

	await accountsPage.deleteButton.click();

	await waitForAlert(page);

	await expect(accountsPage.accountsTable.cell(account.name)).toHaveCount(0);

	await accountsPage.accountsTable.filterButton.click();
	await accountsPage.accountsTable.filterMenuItem('Inactive').click();

	await expect(accountsPage.accountsTable.cell(account.name)).toHaveCount(0);
});

test('LPD-45897 Can delete an inactive account', async ({
	accountsPage,
	apiHelpers,
	page,
}) => {
	page.on('dialog', (dialog) => {
		dialog.accept().catch(() => {});
	});

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await accountsPage.goto();

	await expect(async () => {
		await (
			await accountsPage.accountsTable.rowActions(account.name)
		).click();

		await expect(accountsPage.deactivateButton).toBeVisible({timeout: 500});
	}).toPass();

	await accountsPage.deactivateButton.click();

	await waitForAlert(page);

	await expect(accountsPage.accountsTable.cell(account.name)).toHaveCount(0);

	await accountsPage.accountsTable.filterButton.click();
	await accountsPage.accountsTable.filterMenuItem('Inactive').click();

	await expect(accountsPage.accountsTable.searchInput).toBeEnabled();
	await expect(accountsPage.accountsTable.cell(account.name)).toHaveCount(1);

	await (await accountsPage.accountsTable.rowActions(account.name)).click();

	await expect(async () => {
		await (
			await accountsPage.accountsTable.rowActions(account.name)
		).click();

		await expect(accountsPage.deleteButton).toBeVisible({timeout: 500});
	}).toPass();

	await accountsPage.deleteButton.click();

	await waitForAlert(page);

	await expect(accountsPage.accountsTable.cell(account.name)).toHaveCount(0);
});

test('LPD-45897 Can delete accounts in bulk', async ({
	accountsPage,
	apiHelpers,
	page,
}) => {
	page.on('dialog', async (dialog) => await dialog.accept());

	for (let i = 1; i < 7; i++) {
		if (i < 4) {
			await apiHelpers.headlessAdminUser.postAccount({
				name: `Account ${i}`,
				type: 'business',
			});
		}
		else {
			await apiHelpers.headlessAdminUser.postAccount({
				name: `Account ${i}`,
				type: 'person',
			});
		}
	}

	await accountsPage.goto();

	for (const i of [1, 2, 4, 6]) {
		await (
			await accountsPage.accountsTable.rowCheckbox(`Account ${i}`)
		).click();
	}

	await accountsPage.deleteButton.click();

	await waitForAlert(page);

	for (const i of [1, 2, 4, 6]) {
		await expect(
			accountsPage.accountsTable.cell(`Account ${i}`)
		).not.toBeVisible();
	}

	for (const i of [3, 5]) {
		await expect(
			accountsPage.accountsTable.cell(`Account ${i}`)
		).toBeVisible();
	}

	await accountsPage.changeFilter('Inactive');

	await expect(accountsPage.noAccountsMessage).toBeVisible();
});

test('LPS-195988 An account name should be limited to 250 characters', async ({
	accountsPage,
	apiHelpers,
	editAccountPage,
}) => {
	const name =
		'Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet';

	await accountsPage.goto();

	await expect(accountsPage.accountsTable.searchInput).toBeEditable();

	await accountsPage.accountsTable.newButton.click();
	await editAccountPage.createAccount(apiHelpers, {name});
	await editAccountPage.backButton.click();

	await expect(accountsPage.accountsTable.cell(name)).toHaveCount(0);
	await expect(
		accountsPage.accountsTable.cell(name.substring(0, 250))
	).toBeVisible();

	await (
		await accountsPage.accountsTable.cellLink(name.substring(0, 250))
	).click();

	await expect(editAccountPage.accountNameInput).toHaveValue(
		name.substring(0, 250)
	);
});

test('LPS-195988 The account external reference code should be unique', async ({
	accountsPage,
	apiHelpers,
	editAccountPage,
	page,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		externalReferenceCode: getRandomString(),
		name: getRandomString(),
		type: 'business',
	});

	await accountsPage.goto();

	await expect(accountsPage.accountsTable.searchInput).toBeEditable();

	await accountsPage.accountsTable.newButton.click();
	await editAccountPage.accountNameInput.fill(getRandomString());
	await editAccountPage.externalReferenceCodeInput.fill(
		account.externalReferenceCode
	);

	await editAccountPage.saveButton.click();

	await waitForAlert(
		page,
		'The given external reference code belongs to another account',
		{type: 'danger'}
	);
});

test('LPS-195988 Can create different type of accounts', async ({
	accountsPage,
	apiHelpers,
	editAccountPage,
}) => {
	await accountsPage.goto();

	await expect(accountsPage.accountsTable.searchInput).toBeEditable();

	const accounts = [
		{name: getRandomString(), type: 'business'},
		{name: getRandomString(), type: 'guest'},
		{name: getRandomString(), type: 'person'},
		{name: getRandomString(), type: 'supplier'},
	];

	for (const {name, type} of accounts) {
		await accountsPage.accountsTable.newButton.click();
		await editAccountPage.createAccount(apiHelpers, {name, type});
		await editAccountPage.backButton.click();

		await expect(
			await accountsPage.accountsTable.cellLink(name)
		).toBeVisible();

		await (await accountsPage.accountsTable.cellLink(name)).click();

		await expect(editAccountPage.accountNameInput).toBeVisible();
		await expect(editAccountPage.typeInput).toHaveValue(
			new RegExp(type, 'i')
		);

		await editAccountPage.backButton.click();
	}

	for (const account of accounts) {
		await expect(
			accountsPage.accountsTable.cell(account.name)
		).toBeVisible();
	}

	await accountsPage.accountsTable.filterButton.click();
	await accountsPage.accountsTable.filterMenuItem('Business').click();

	for (const account of accounts) {
		if (account.type === 'business') {
			await expect(
				accountsPage.accountsTable.cell(account.name)
			).toBeVisible();
		}
		else {
			await expect(
				accountsPage.accountsTable.cell(account.name)
			).toHaveCount(0);
		}
	}

	await accountsPage.accountsTable.filterButton.click();
	await accountsPage.accountsTable.filterMenuItem('Guest').click();

	for (const account of accounts) {
		if (account.type === 'guest') {
			await expect(
				accountsPage.accountsTable.cell(account.name)
			).toBeVisible();
		}
		else {
			await expect(
				accountsPage.accountsTable.cell(account.name)
			).toHaveCount(0);
		}
	}

	await accountsPage.accountsTable.filterButton.click();
	await accountsPage.accountsTable.filterMenuItem('Person').click();

	for (const account of accounts) {
		if (account.type === 'person') {
			await expect(
				accountsPage.accountsTable.cell(account.name)
			).toBeVisible();
		}
		else {
			await expect(
				accountsPage.accountsTable.cell(account.name)
			).toHaveCount(0);
		}
	}

	await accountsPage.accountsTable.filterButton.click();
	await accountsPage.accountsTable.filterMenuItem('Supplier').click();

	for (const account of accounts) {
		if (account.type === 'supplier') {
			await expect(
				accountsPage.accountsTable.cell(account.name)
			).toBeVisible();
		}
		else {
			await expect(
				accountsPage.accountsTable.cell(account.name)
			).toHaveCount(0);
		}
	}
});

test('LPS-195988 Multiple accounts can be added with the same domain', async ({
	accountsPage,
	apiHelpers,
	editAccountPage,
	emailDomainsInstanceSettingsPage,
}) => {
	const accounts = [
		{
			domains: ['liferay.com'],
			name: getRandomString(),
			type: 'business',
		},
		{
			domains: ['liferay.com'],
			name: getRandomString(),
			type: 'business',
		},
	];

	await emailDomainsInstanceSettingsPage.enableEmailDomainValidation();

	try {
		await accountsPage.goto();

		await expect(accountsPage.accountsTable.searchInput).toBeEditable();

		for (const {domains, name, type} of accounts) {
			await accountsPage.accountsTable.newButton.click();
			await editAccountPage.createAccount(apiHelpers, {
				domains,
				name,
				type,
			});

			for (const domain of domains) {
				await expect(editAccountPage.domainCell(domain)).toBeVisible();
			}

			await editAccountPage.backButton.click();

			await (await accountsPage.accountsTable.cellLink(name)).click();

			for (const domain of domains) {
				await expect(editAccountPage.domainCell(domain)).toBeVisible();
			}

			await editAccountPage.backButton.click();
		}
	}
	finally {
		await emailDomainsInstanceSettingsPage.enableEmailDomainValidation(
			false
		);
	}
});

test('LPS-195988 A business account can have more than one domain', async ({
	accountsPage,
	apiHelpers,
	editAccountPage,
	emailDomainsInstanceSettingsPage,
}) => {
	const account = {
		domains: ['liferay.com', 'google.com', 'si-na.com', '9teen.com'],
		name: getRandomString(),
		type: 'business',
	};

	const {domains, name, type} = account;

	await emailDomainsInstanceSettingsPage.enableEmailDomainValidation();

	try {
		await accountsPage.goto();

		await expect(accountsPage.accountsTable.searchInput).toBeEditable();

		await accountsPage.accountsTable.newButton.click();
		await editAccountPage.createAccount(apiHelpers, {domains, name, type});

		for (const domain of domains) {
			await expect(editAccountPage.domainCell(domain)).toBeVisible();
		}

		await editAccountPage.backButton.click();

		await (await accountsPage.accountsTable.cellLink(name)).click();

		await editAccountPage.domainRemoveButton(domains[0]).click();

		for (const [index, domain] of domains.entries()) {
			if (index === 0) {
				await expect(editAccountPage.domainCell(domain)).toHaveCount(0);
			}
			else {
				await expect(editAccountPage.domainCell(domain)).toBeVisible();
			}
		}
	}
	finally {
		await emailDomainsInstanceSettingsPage.enableEmailDomainValidation(
			false
		);
	}
});

test('LPS-195988 Domain validation is not present in Person Accounts', async ({
	accountsPage,
	editAccountPage,
	emailDomainsInstanceSettingsPage,
}) => {
	await emailDomainsInstanceSettingsPage.enableEmailDomainValidation();

	try {
		await accountsPage.goto();

		await expect(accountsPage.accountsTable.searchInput).toBeEditable();

		await accountsPage.accountsTable.newButton.click();

		await expect(editAccountPage.validDomainsHeading).toBeVisible();

		await editAccountPage.typeInput.selectOption('person');

		await expect(editAccountPage.validDomainsHeading).toHaveCount(0);

		await editAccountPage.typeInput.selectOption('business');

		await expect(editAccountPage.validDomainsHeading).toBeVisible();
	}
	finally {
		await emailDomainsInstanceSettingsPage.enableEmailDomainValidation(
			false
		);
	}
});

test('LPS-101893 Account list should be paginated', async ({
	accountsPage,
	apiHelpers,
	page,
}) => {
	const accounts = [];

	for (let i = 1; i <= 21; i++) {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: `Account ${String(i).padStart(2, '0')}`,
			type: 'business',
		});

		accounts.push(account);
	}

	await accountsPage.goto();

	await setItemsPerPage(page, 20);

	for (const [index, account] of accounts.entries()) {
		if (index < 20) {
			await expect(
				accountsPage.accountsTable.cell(account.name)
			).toBeVisible();
		}
		else {
			await expect(
				accountsPage.accountsTable.cell(account.name)
			).toHaveCount(0);
		}
	}

	await nextPage(page);

	for (const [index, account] of accounts.entries()) {
		if (index < 20) {
			await expect(
				accountsPage.accountsTable.cell(account.name)
			).toHaveCount(0);
		}
		else {
			await expect(
				accountsPage.accountsTable.cell(account.name)
			).toBeVisible();
		}
	}
});

test('LPS-157661 An account avatar can be added in creation', async ({
	accountsPage,
	apiHelpers,
	editAccountPage,
}) => {
	const name = getRandomString();

	await accountsPage.goto();

	await expect(accountsPage.accountsTable.searchInput).toBeEditable();

	await accountsPage.accountsTable.newButton.click();
	await editAccountPage.createAccount(apiHelpers, {
		avatar: path.join(__dirname, '/dependencies/liferay.png'),
		name,
	});
	await editAccountPage.backButton.click();

	await (await accountsPage.accountsTable.cellLink(name)).click();

	await expect(editAccountPage.imageInput).toHaveValue('Custom Image');
});

test('LPS-195988 An account can be updated', async ({
	accountsPage,
	apiHelpers,
	editAccountPage,
	page,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		description: getRandomString(),
		type: 'business',
	});

	await accountsPage.goto();

	await accountsPage.accountNameLink(account.name).click();

	await expect(editAccountPage.accountNameInput).toHaveValue(account.name);
	await expect(editAccountPage.descriptionInput).toHaveValue(
		account.description
	);
	await expect(editAccountPage.externalReferenceCodeInput).toHaveValue(
		account.externalReferenceCode
	);

	const updatedAccount = {
		description: getRandomString(),
		externalReferenceCode: getRandomString(),
		name: getRandomString(),
	};

	await editAccountPage.accountNameInput.fill(updatedAccount.name);
	await editAccountPage.descriptionInput.fill(updatedAccount.description);
	await editAccountPage.externalReferenceCodeInput.fill(
		updatedAccount.externalReferenceCode
	);
	await editAccountPage.saveButton.click();

	await waitForAlert(page);

	await editAccountPage.backButton.click();

	await expect(accountsPage.accountsTable.cell(account.name)).toHaveCount(0);

	await accountsPage.accountNameLink(updatedAccount.name).click();

	await expect(editAccountPage.accountNameInput).toHaveValue(
		updatedAccount.name
	);
	await expect(editAccountPage.descriptionInput).toHaveValue(
		updatedAccount.description
	);
	await expect(editAccountPage.externalReferenceCodeInput).toHaveValue(
		updatedAccount.externalReferenceCode
	);
});

test('LPS-101221 An inactive account can be updated', async ({
	accountsPage,
	apiHelpers,
	editAccountPage,
	page,
}) => {
	page.on('dialog', (dialog) => {
		dialog.accept().catch(() => {});
	});

	const account = await apiHelpers.headlessAdminUser.postAccount({
		description: getRandomString(),
		type: 'business',
	});

	await accountsPage.goto();

	await expect(async () => {
		await (
			await accountsPage.accountsTable.rowActions(account.name)
		).click();

		await expect(accountsPage.deactivateButton).toBeVisible({timeout: 300});
	}).toPass();

	await accountsPage.deactivateButton.click();

	await waitForAlert(page);

	await accountsPage.accountsTable.filterButton.click();
	await accountsPage.accountsTable.filterMenuItem('Inactive').click();

	await accountsPage.accountNameLink(account.name).click();

	await expect(editAccountPage.accountNameInput).toHaveValue(account.name);
	await expect(editAccountPage.descriptionInput).toHaveValue(
		account.description
	);
	await expect(editAccountPage.externalReferenceCodeInput).toHaveValue(
		account.externalReferenceCode
	);

	const updatedAccount = {
		description: getRandomString(),
		externalReferenceCode: getRandomString(),
		name: getRandomString(),
	};

	await editAccountPage.accountNameInput.fill(updatedAccount.name);
	await editAccountPage.descriptionInput.fill(updatedAccount.description);
	await editAccountPage.externalReferenceCodeInput.fill(
		updatedAccount.externalReferenceCode
	);
	await editAccountPage.saveButton.click();

	await waitForAlert(page);

	await editAccountPage.backButton.click();

	await expect(accountsPage.accountsTable.cell(account.name)).toHaveCount(0);
	await expect(
		accountsPage.accountsTable.cell(updatedAccount.name)
	).toBeVisible();

	await accountsPage.accountsTable.filterButton.click();
	await accountsPage.accountsTable.filterMenuItem('Active').click();

	await expect(accountsPage.accountsTable.cell(account.name)).toHaveCount(0);
	await expect(
		accountsPage.accountsTable.cell(updatedAccount.name)
	).toHaveCount(0);

	await accountsPage.accountsTable.filterButton.click();
	await accountsPage.accountsTable.filterMenuItem('Inactive').click();

	await expect(accountsPage.accountsTable.cell(account.name)).toHaveCount(0);
	await expect(
		accountsPage.accountsTable.cell(updatedAccount.name)
	).toBeVisible();
});

test('LPS-101221 Can search an account', async ({
	accountsPage,
	apiHelpers,
	page,
}) => {
	page.on('dialog', (dialog) => {
		dialog.accept().catch(() => {});
	});

	const account1 = await apiHelpers.headlessAdminUser.postAccount({
		description: getRandomString(),
		type: 'business',
	});
	const account2 = await apiHelpers.headlessAdminUser.postAccount({
		description: getRandomString(),
		type: 'business',
	});

	await accountsPage.goto();

	await expect(async () => {
		await (
			await accountsPage.accountsTable.rowActions(account1.name)
		).click();

		await expect(accountsPage.deactivateButton).toBeVisible({timeout: 300});
	}).toPass();

	await accountsPage.deactivateButton.click();

	await waitForAlert(page);

	await accountsPage.accountsTable.search(account1.name);

	await expect(accountsPage.accountsTable.cell(account1.name)).toHaveCount(0);
	await expect(accountsPage.accountsTable.cell(account2.name)).toHaveCount(0);

	await accountsPage.accountsTable.search(account2.name);

	await expect(accountsPage.accountsTable.cell(account1.name)).toHaveCount(0);
	await expect(accountsPage.accountsTable.cell(account2.name)).toBeVisible();

	await expect(accountsPage.accountsTable.searchInput).toBeEnabled();

	await accountsPage.accountsTable.filterButton.click();
	await accountsPage.accountsTable.filterMenuItem('Inactive').click();

	await expect(async () => {
		await accountsPage.accountsTable.search(account1.name);

		await expect(
			accountsPage.accountsTable.cell(account1.name)
		).toBeVisible({timeout: 500});
		await expect(
			accountsPage.accountsTable.cell(account2.name)
		).toHaveCount(0);
	}).toPass();
});

test('LPD-47225 Can edit account custom fields', async ({
	accountsPage,
	addCustomFieldPage,
	apiHelpers,
	editAccountPage,
	page,
}) => {
	const customField: TCustomField = {
		fieldName: getRandomString(),
		fieldType: 'inputField',
		resource: 'Account Entry',
	};

	await addCustomFieldPage.addCustomField(customField);

	const account = await apiHelpers.headlessAdminUser.postAccount();

	await accountsPage.goto();

	await accountsPage.accountNameLink(account.name).click();

	const randomString = getRandomString();

	await editAccountPage
		.customFieldInput(customField.fieldName)
		.fill(randomString);

	await editAccountPage.saveButton.click();

	await waitForAlert(page);

	await accountsPage.goto();

	await accountsPage.accountNameLink(account.name).click();

	await expect(
		editAccountPage.customFieldInput(customField.fieldName)
	).toHaveValue(randomString);
});

test('LPD-47225 Can add and remove categories to an account', async ({
	accountCategorySelectorPage,
	accountsPage,
	apiHelpers,
	editAccountPage,
	page,
}) => {
	const categoryNames = [
		{name: getRandomString()},
		{name: getRandomString()},
	];
	const vocabularyName = getRandomString();

	const categories: Array<any> = await createCategories({
		apiHelpers,
		categoryNames,
		siteId: await getGlobalSiteId(apiHelpers),
		vocabularyName,
	});

	apiHelpers.data.push({
		id: categories[0].vocabularyId,
		type: 'taxonomyVocabulary',
	});

	const account = await apiHelpers.headlessAdminUser.postAccount();

	await accountsPage.goto();

	await accountsPage.accountNameLink(account.name).click();
	await editAccountPage.selectCategoriesButton(vocabularyName).click();
	await accountCategorySelectorPage.selectCategories(
		[categoryNames[0].name],
		vocabularyName
	);

	await expect(editAccountPage.vocabularyLabel(vocabularyName)).toBeVisible();
	await expect(
		editAccountPage.categoryLabel(categoryNames[0].name)
	).toBeVisible();
	await expect(
		editAccountPage.categoryLabel(categoryNames[1].name)
	).toHaveCount(0);

	await editAccountPage.selectCategoriesButton(vocabularyName).click();
	await accountCategorySelectorPage.selectCategories(
		[categoryNames[1].name],
		vocabularyName
	);

	await expect(editAccountPage.vocabularyLabel(vocabularyName)).toBeVisible();
	await expect(
		editAccountPage.categoryLabel(categoryNames[0].name)
	).toBeVisible();
	await expect(
		editAccountPage.categoryLabel(categoryNames[1].name)
	).toBeVisible();

	await editAccountPage.saveButton.click();

	await waitForAlert(page);

	await accountsPage.goto();

	await accountsPage.accountNameLink(account.name).click();

	await expect(editAccountPage.vocabularyLabel(vocabularyName)).toBeVisible();
	await expect(
		editAccountPage.categoryLabel(categoryNames[0].name)
	).toBeVisible();
	await expect(
		editAccountPage.categoryLabel(categoryNames[1].name)
	).toBeVisible();

	await editAccountPage.categoryClearAllButton.click();

	await expect(async () => {
		await editAccountPage.categoryLabel(vocabularyName).press('Tab');

		await expect(editAccountPage.saveButton).toBeVisible();
	}).toPass();

	await editAccountPage.saveButton.click();

	await waitForAlert(page);

	await accountsPage.goto();

	await accountsPage.accountNameLink(account.name).click();

	await expect(editAccountPage.vocabularyLabel(vocabularyName)).toBeVisible();
	await expect(
		editAccountPage.categoryLabel(categoryNames[0].name)
	).toHaveCount(0);
	await expect(
		editAccountPage.categoryLabel(categoryNames[1].name)
	).toHaveCount(0);
});

test(
	'LPD-47225 Tabs are visile only after account creation',
	{tag: ['@LPS-169033']},
	async ({accountsPage, apiHelpers, editAccountPage}) => {
		let account = {
			name: getRandomString(),
			type: 'business',
		};

		await accountsPage.goto();

		await expect(accountsPage.accountsTable.searchInput).toBeEditable();

		await accountsPage.accountsTable.newButton.click();

		await expect(editAccountPage.detailsTab).not.toBeVisible();
		await expect(editAccountPage.addressesTab).not.toBeVisible();
		await expect(editAccountPage.contactLink).not.toBeVisible();
		await expect(editAccountPage.usersLink).not.toBeVisible();
		await expect(editAccountPage.organizationsLink).not.toBeVisible();
		await expect(editAccountPage.accountGroupsLink).not.toBeVisible();
		await expect(editAccountPage.rolesLink).not.toBeVisible();
		await expect(editAccountPage.channelDefaultsLink).not.toBeVisible();

		await editAccountPage.createAccount(apiHelpers, account);

		await expect(editAccountPage.detailsTab).toBeVisible();
		await expect(editAccountPage.addressesTab).toBeVisible();
		await expect(editAccountPage.contactLink).toBeVisible();
		await expect(editAccountPage.usersLink).toBeVisible();
		await expect(editAccountPage.organizationsLink).toBeVisible();
		await expect(editAccountPage.accountGroupsLink).toBeVisible();
		await expect(editAccountPage.rolesLink).toBeVisible();
		await expect(editAccountPage.channelDefaultsLink).toBeVisible();

		await editAccountPage.backButton.click();

		await accountsPage.accountNameLink(account.name).click();

		await expect(editAccountPage.detailsTab).toBeVisible();
		await expect(editAccountPage.addressesTab).toBeVisible();
		await expect(editAccountPage.contactLink).toBeVisible();
		await expect(editAccountPage.usersLink).toBeVisible();
		await expect(editAccountPage.organizationsLink).toBeVisible();
		await expect(editAccountPage.accountGroupsLink).toBeVisible();
		await expect(editAccountPage.rolesLink).toBeVisible();
		await expect(editAccountPage.channelDefaultsLink).toBeVisible();

		await editAccountPage.backButton.click();

		await expect(accountsPage.accountsTable.searchInput).toBeEditable();

		account = {
			name: getRandomString(),
			type: 'person',
		};

		await accountsPage.accountsTable.newButton.click();
		await editAccountPage.createAccount(apiHelpers, account);

		await expect(editAccountPage.detailsTab).toBeVisible();
		await expect(editAccountPage.addressesTab).toBeVisible();
		await expect(editAccountPage.contactLink).toBeVisible();
		await expect(editAccountPage.usersLink).not.toBeVisible();
		await expect(editAccountPage.organizationsLink).toBeVisible();
		await expect(editAccountPage.accountGroupsLink).toBeVisible();
		await expect(editAccountPage.rolesLink).toBeVisible();
		await expect(editAccountPage.channelDefaultsLink).not.toBeVisible();

		await editAccountPage.backButton.click();

		await accountsPage.accountNameLink(account.name).click();

		await expect(editAccountPage.detailsTab).toBeVisible();
		await expect(editAccountPage.addressesTab).toBeVisible();
		await expect(editAccountPage.contactLink).toBeVisible();
		await expect(editAccountPage.usersLink).not.toBeVisible();
		await expect(editAccountPage.organizationsLink).toBeVisible();
		await expect(editAccountPage.accountGroupsLink).toBeVisible();
		await expect(editAccountPage.rolesLink).toBeVisible();
		await expect(editAccountPage.channelDefaultsLink).not.toBeVisible();
	}
);

test('LPD-47225 Can add and remove tags to an account', async ({
	accountTagSelectorPage,
	accountsPage,
	apiHelpers,
	editAccountPage,
	page,
	tagsEditPage,
}) => {
	const tags = [
		{name: getRandomString(), siteUrl: '/global'},
		{name: getRandomString(), siteUrl: '/global'},
		{name: getRandomString(), siteUrl: '/guest'},
	];

	for (const {name, siteUrl} of tags) {
		await tagsEditPage.add(name, siteUrl);
	}

	const account = await apiHelpers.headlessAdminUser.postAccount();

	await accountsPage.goto();

	await accountsPage.accountNameLink(account.name).click();

	await editAccountPage.selectTagsButton.click();
	await accountTagSelectorPage.selectTag([tags[0].name, tags[2].name]);

	await expect(editAccountPage.tagInput(tags[0].name)).toBeVisible();
	await expect(editAccountPage.tagInput(tags[1].name)).toHaveCount(0);
	await expect(editAccountPage.tagInput(tags[2].name)).toBeVisible();

	await editAccountPage.saveButton.click();

	await waitForAlert(page);

	await editAccountPage.backButton.click();
	await accountsPage.accountNameLink(account.name).click();

	await expect(editAccountPage.tagInput(tags[0].name)).toBeVisible();
	await expect(editAccountPage.tagInput(tags[1].name)).toHaveCount(0);
	await expect(editAccountPage.tagInput(tags[2].name)).toBeVisible();

	await editAccountPage.categoryClearAllButton.click();

	await expect(async () => {
		await page.getByLabel('Tags', {exact: true}).press('Tab');

		await expect(editAccountPage.tagInput(tags[0].name)).toHaveCount(0);
		await expect(editAccountPage.tagInput(tags[1].name)).toHaveCount(0);
		await expect(editAccountPage.tagInput(tags[2].name)).toHaveCount(0);
	}).toPass();

	await editAccountPage.saveButton.click();

	await waitForAlert(page);

	await editAccountPage.backButton.click();
	await accountsPage.accountNameLink(account.name).click();

	await expect(editAccountPage.tagInput(tags[0].name)).toHaveCount(0);
	await expect(editAccountPage.tagInput(tags[1].name)).toHaveCount(0);
	await expect(editAccountPage.tagInput(tags[2].name)).toHaveCount(0);
});

test('LPD-47225 Smoke test', async ({
	accountsPage,
	apiHelpers,
	editAccountPage,
}) => {
	const account = {
		name: getRandomString(),
		taxID: getRandomString(),
	};

	await accountsPage.goto();

	await expect(accountsPage.accountsTable.searchInput).toBeEditable();

	await accountsPage.accountsTable.newButton.click();
	await editAccountPage.createAccount(apiHelpers, account);
	await editAccountPage.backButton.click();
	await accountsPage.accountsTable.search(account.name);

	await expect(accountsPage.accountsTable.cell(account.name)).toBeVisible();
	await expect(
		(await accountsPage.accountsTable.row(1, account.name, true)).row
	).toContainText('Active');

	await accountsPage.accountNameLink(account.name).click();

	await expect(editAccountPage.accountNameInput).toHaveValue(account.name);
	await expect(editAccountPage.taxIDInput).toHaveValue(account.taxID);
});

test('LPD-47225 Smoke test for service accounts', async ({
	editUserPage,
	page,
	serviceAccountsPage,
}) => {
	const roleName = 'Portal Content Reviewer';

	await serviceAccountsPage.goto();

	await expect(
		serviceAccountsPage.usersTable.cell('default-service-account')
	).toBeVisible();

	await (
		await serviceAccountsPage.usersTable.cellLink(
			'default-service-account',
			1,
			false
		)
	).click();

	await expect(editUserPage.appsLink).toBeVisible();
	await expect(editUserPage.informationLink).toBeVisible();
	await expect(editUserPage.membershipsLink).toBeVisible();
	await expect(editUserPage.organizationsLink).toBeVisible();
	await expect(editUserPage.passwordLink).toHaveCount(0);
	await expect(editUserPage.profileAndDashboardLink).toBeVisible();
	await expect(editUserPage.rolesLink).toBeVisible();
	await expect(editUserPage.screenNameInput).toBeDisabled();

	await editUserPage.rolesLink.click();
	await editUserPage.selectRegularRolesButton.click();

	await expect(editUserPage.selectRegularRolesSearchInput).toBeEnabled();

	await editUserPage.selectRegularRolesChooseButton(roleName).click();

	await expect(editUserPage.regularRoleCell(roleName)).toBeVisible();

	await editUserPage.saveButton.click();

	await waitForAlert(page);

	await page.reload();

	await expect(editUserPage.regularRoleCell(roleName)).toBeVisible();

	await editUserPage.regularRoleCellButton(roleName).click();

	await expect(editUserPage.regularRoleCell(roleName)).toHaveCount(0);

	await editUserPage.saveButton.click();

	await waitForAlert(page);

	await page.reload();

	await expect(editUserPage.regularRoleCell(roleName)).toHaveCount(0);
});

test('LPD-47589 Check delete and deactivate permissions work independently', async ({
	accountsPage,
	apiHelpers,
	page,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: 'Test Role ' + getRandomString(),
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet',
				scope: 1,
			},
			{
				actionIds: ['DEACTIVATE', 'UPDATE', 'VIEW'],
				primaryKey: companyId,
				resourceName: 'com.liferay.account.model.AccountEntry',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: 'com_liferay_depot_web_portlet_DepotAdminPortlet',
				scope: 1,
			},
		],
	});

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
		role.externalReferenceCode,
		userAccount.id
	);

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	await performLogout(page);
	await performLogin(page, userAccount.alternateName);

	await accountsPage.goto();

	await (await accountsPage.accountsTable.rowActions(account.name)).click();

	await expect(await accountsPage.deactivateButton).toBeVisible();
	await expect(await accountsPage.deleteButton).not.toBeVisible();

	page.on('dialog', async (dialog) => await dialog.accept());

	await accountsPage.deactivateButton.click();

	await waitForAlert(page);

	await expect(accountsPage.accountsTable.cell(account.name)).toHaveCount(0);
});

test(
	'Phone number and phone extension should display as phone number and phone extension',
	{tag: ['@LPD-61973']},
	async ({
		accountsPage,
		apiHelpers,
		editAccountContactInformationPage,
		editAccountContactPage,
		editAccountPage,
		editAccountPhonePage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: 'test',
			type: 'business',
		});

		await accountsPage.goto();

		await (await accountsPage.accountsTable.cellLink(account.name)).click();
		await editAccountPage.contactLink.click();
		await editAccountContactPage.contactInformationLink.click();
		await editAccountContactInformationPage.addPhoneNumbersButton.click();

		await expect(editAccountPhonePage.extensionLabel).toBeVisible();

		await expect(editAccountPhonePage.numberLabel).toBeVisible();

		await editAccountPhonePage.updatePhoneNumber('111-111-1111');

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		await expect(
			editAccountContactInformationPage.phoneExtensionHeader
		).toBeVisible();
		await expect(
			editAccountContactInformationPage.phoneNumberHeader
		).toBeVisible();
	}
);

test(
	'Escape account name to avoid XSS injections',
	{tag: '@LPD-65007'},
	async ({accountsPage, apiHelpers, page}) => {
		const name = '<img src=x onerror=alert(origin)>';

		await apiHelpers.headlessAdminUser.postAccount({
			name,
			type: 'business',
		});

		page.on('dialog', async (dialog) => {
			if (dialog.type() === 'alert') {
				throw new Error('XSS detected');
			}
		});

		await accountsPage.goto();

		await expect(
			await accountsPage.accountsTable.cellLink(name)
		).toBeVisible();
	}
);
