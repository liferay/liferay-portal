/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../fixtures/accountsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	accountsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35914': {enabled: true},
	}),
	loginTest()
);

test(
	'Can search for account group',
	{tag: ['@LPD-25948']},
	async ({accountAccountGroupsPage, accountsPage, apiHelpers, page}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const accountGroup1 =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: getRandomString(),
			});

		apiHelpers.data.push({id: accountGroup1.id, type: 'accountGroup'});

		const accountGroup2 =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: getRandomString(),
			});

		apiHelpers.data.push({id: accountGroup2.id, type: 'accountGroup'});

		await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
			account.externalReferenceCode,
			accountGroup1.externalReferenceCode
		);

		await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
			account.externalReferenceCode,
			accountGroup2.externalReferenceCode
		);

		await accountsPage.goto();
		await (await accountsPage.accountsTable.cellLink(account.name)).click();
		await accountsPage.accountGroupsTab.click();

		await accountAccountGroupsPage.searchInput.fill(accountGroup1.name);
		await accountAccountGroupsPage.searchButton.click();

		await expect(
			page.getByText(accountGroup1.name, {exact: true})
		).toBeVisible();

		await expect(
			page.getByText(accountGroup2.name, {exact: true})
		).toHaveCount(0);
	}
);

test(
	'Account groups are displayed in account details page',
	{tag: ['@LPD-28159', '@LPD-58550']},
	async ({accountAccountGroupsPage, accountsPage, apiHelpers}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const accountGroup1 =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: getRandomString(),
			});

		apiHelpers.data.push({id: accountGroup1.id, type: 'accountGroup'});

		await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
			account.externalReferenceCode,
			accountGroup1.externalReferenceCode
		);

		const accountGroup2 =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: getRandomString(),
			});

		apiHelpers.data.push({id: accountGroup2.id, type: 'accountGroup'});

		await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
			account.externalReferenceCode,
			accountGroup2.externalReferenceCode
		);

		const accountGroup3 =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: getRandomString(),
			});

		apiHelpers.data.push({id: accountGroup3.id, type: 'accountGroup'});

		await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
			account.externalReferenceCode,
			accountGroup3.externalReferenceCode
		);

		await accountsPage.goto();
		await (await accountsPage.accountsTable.cell(account.name)).click();
		await accountsPage.accountGroupsTab.click();

		await expect(
			await accountAccountGroupsPage.accountGroupName(accountGroup1.name)
		).toBeVisible();
		await expect(
			await accountAccountGroupsPage.accountGroupName(accountGroup2.name)
		).toBeVisible();
		await expect(
			await accountAccountGroupsPage.accountGroupName(accountGroup3.name)
		).toBeVisible();
		await expect(accountAccountGroupsPage.statusColumn).toBeVisible();
	}
);

test('Can search for account group in account groups page', async ({
	accountGroupsPage,
	apiHelpers,
}) => {
	const accountGroup1 = await apiHelpers.headlessAdminUser.postAccountGroup();

	apiHelpers.data.push({id: accountGroup1.id, type: 'accountGroup'});

	const accountGroup2 = await apiHelpers.headlessAdminUser.postAccountGroup();

	apiHelpers.data.push({id: accountGroup2.id, type: 'accountGroup'});

	await accountGroupsPage.goto();

	await accountGroupsPage.accountGroupsTable.search(getRandomString());

	await expect(
		accountGroupsPage.accountGroupsTable.cell(accountGroup1.name)
	).toHaveCount(0);
	await expect(
		accountGroupsPage.accountGroupsTable.cell(accountGroup2.name)
	).toHaveCount(0);

	await accountGroupsPage.accountGroupsTable.search(accountGroup1.name);

	await expect(
		accountGroupsPage.accountGroupsTable.cell(accountGroup1.name)
	).toBeVisible();
	await expect(
		accountGroupsPage.accountGroupsTable.cell(accountGroup2.name)
	).toHaveCount(0);

	await accountGroupsPage.accountGroupsTable.search(accountGroup2.name);

	await expect(
		accountGroupsPage.accountGroupsTable.cell(accountGroup1.name)
	).toHaveCount(0);
	await expect(
		accountGroupsPage.accountGroupsTable.cell(accountGroup2.name)
	).toBeVisible();

	await accountGroupsPage.accountGroupsTable.search('');

	await expect(
		accountGroupsPage.accountGroupsTable.cell(accountGroup1.name)
	).toBeVisible();
	await expect(
		accountGroupsPage.accountGroupsTable.cell(accountGroup2.name)
	).toBeVisible();
});

test('Can add and edit an account group', async ({
	accountGroupsPage,
	apiHelpers,
	editAccountGroupPage,
	page,
}) => {
	let accountGroup = {
		description: getRandomString(),
		externalReferenceCode: getRandomString(),
		name: getRandomString(),
	};

	await accountGroupsPage.goto();

	await accountGroupsPage.accountGroupsTable.newButton.click();
	await editAccountGroupPage.addAccountGroup(apiHelpers, accountGroup);
	await editAccountGroupPage.backButton.click();

	await expect(
		accountGroupsPage.accountGroupsTable.cell(accountGroup.name)
	).toBeVisible();

	await accountGroupsPage.accountGroupLink(accountGroup.name).click();
	await editAccountGroupPage.detailsLink.click();

	await expect(editAccountGroupPage.accountGroupNameInput).toHaveValue(
		accountGroup.name
	);
	await expect(editAccountGroupPage.descriptionInput).toHaveValue(
		accountGroup.description
	);
	await expect(editAccountGroupPage.externalReferenceCodeInput).toHaveValue(
		accountGroup.externalReferenceCode
	);

	accountGroup = {
		description: getRandomString(),
		externalReferenceCode: getRandomString(),
		name: getRandomString(),
	};

	await editAccountGroupPage.accountGroupNameInput.fill(accountGroup.name);
	await editAccountGroupPage.descriptionInput.fill(accountGroup.description);
	await editAccountGroupPage.externalReferenceCodeInput.fill(
		accountGroup.externalReferenceCode
	);

	await editAccountGroupPage.saveButton.click();

	await waitForAlert(page);

	await editAccountGroupPage.backButton.click();

	await expect(
		accountGroupsPage.accountGroupsTable.cell(accountGroup.name)
	).toBeVisible();

	await accountGroupsPage.accountGroupLink(accountGroup.name).click();
	await editAccountGroupPage.detailsLink.click();

	await expect(editAccountGroupPage.accountGroupNameInput).toHaveValue(
		accountGroup.name
	);
	await expect(editAccountGroupPage.descriptionInput).toHaveValue(
		accountGroup.description
	);
	await expect(editAccountGroupPage.externalReferenceCodeInput).toHaveValue(
		accountGroup.externalReferenceCode
	);
});

test('Can delete an account group', async ({
	accountGroupsPage,
	apiHelpers,
	page,
}) => {
	page.on('dialog', (dialog) => dialog.accept());

	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup();

	try {
		await accountGroupsPage.goto();

		await (
			await accountGroupsPage.accountGroupsTable.rowActions(
				accountGroup.name
			)
		).click();
		await accountGroupsPage.deleteButton.click();

		await expect(
			accountGroupsPage.accountGroupLink(accountGroup.name)
		).toHaveCount(0);
	}
	catch (exception) {
		apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});
	}
});

test('Can bulk delete account groups', async ({
	accountGroupsPage,
	apiHelpers,
	page,
}) => {
	page.on('dialog', (dialog) => dialog.accept());

	const accountGroup1 = await apiHelpers.headlessAdminUser.postAccountGroup();
	const accountGroup2 = await apiHelpers.headlessAdminUser.postAccountGroup();
	const accountGroup3 = await apiHelpers.headlessAdminUser.postAccountGroup();

	apiHelpers.data.push({id: accountGroup3.id, type: 'accountGroup'});

	try {
		await accountGroupsPage.goto();

		await (
			await accountGroupsPage.accountGroupsTable.rowCheckbox(
				accountGroup1.name
			)
		).check();
		await (
			await accountGroupsPage.accountGroupsTable.rowCheckbox(
				accountGroup2.name
			)
		).check();
		await accountGroupsPage.deleteButton.click();

		await expect(
			accountGroupsPage.accountGroupLink(accountGroup1.name)
		).toHaveCount(0);
		await expect(
			accountGroupsPage.accountGroupLink(accountGroup2.name)
		).toHaveCount(0);
		await expect(
			accountGroupsPage.accountGroupLink(accountGroup3.name)
		).toBeVisible();
	}
	catch (exception) {
		apiHelpers.data.push({id: accountGroup1.id, type: 'accountGroup'});
		apiHelpers.data.push({id: accountGroup2.id, type: 'accountGroup'});
	}
});

test(
	'Can assign an account to an account group',
	{tag: ['@LPD-48520', '@LPD-58550']},
	async ({
		accountGroupAccountSelectorPage,
		accountGroupAccountsPage,
		accountGroupsPage,
		apiHelpers,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		const accountGroup =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: getRandomString(),
			});

		apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

		await accountGroupsPage.goto();

		await accountGroupsPage.accountGroupLink(accountGroup.name).click();

		await expect(accountGroupAccountsPage.noAccountsMessage).toBeVisible();

		await expect(async () => {
			await expect(
				accountGroupAccountsPage.accountsTable.searchInput
			).toBeEditable();

			await accountGroupAccountsPage.accountsTable.newButton.click();

			await expect(
				accountGroupAccountSelectorPage.accountsTable.cell(account.name)
			).toBeVisible();
			await expect(
				accountGroupAccountSelectorPage.accountsTable.cell('Active')
			).toBeVisible();
		}).toPass();

		await accountGroupAccountSelectorPage.selectAccounts([account.name]);

		await expect(
			accountGroupAccountsPage.accountsTable.cell(account.name)
		).toBeVisible();
	}
);

test(
	'Can bulk assign an account to an account group',
	{tag: ['@LPS-122414']},
	async ({
		accountGroupAccountSelectorPage,
		accountGroupAccountsPage,
		accountGroupsPage,
		apiHelpers,
	}) => {
		const accounts = [];

		for (let i = 1; i < 6; i++) {
			const account = await apiHelpers.headlessAdminUser.postAccount({
				name: `${i} ${getRandomString()}`,
				type: 'business',
			});

			accounts.push(account);
		}

		const accountGroup =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: getRandomString(),
			});

		apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

		await accountGroupsPage.goto();

		await accountGroupsPage.accountGroupLink(accountGroup.name).click();

		await expect(accountGroupAccountsPage.noAccountsMessage).toBeVisible();

		await expect(async () => {
			await expect(
				accountGroupAccountsPage.accountsTable.searchInput
			).toBeEditable();

			await accountGroupAccountsPage.accountsTable.newButton.click();

			for (const account of accounts) {
				await expect(
					accountGroupAccountSelectorPage.accountsTable.cell(
						account.name
					)
				).toBeVisible();
			}
		}).toPass();

		await accountGroupAccountSelectorPage.selectAccounts(
			accounts.slice(0, 3).map((account) => account.name)
		);

		for (const [index, account] of accounts.entries()) {
			if (index < 3) {
				await expect(
					accountGroupAccountsPage.accountsTable.cell(account.name)
				).toBeVisible();
			}
			else {
				await expect(
					accountGroupAccountsPage.accountsTable.cell(account.name)
				).toHaveCount(0);
			}
		}
	}
);

test('Can search assigneed accounts of an account group', async ({
	accountGroupAccountsPage,
	accountGroupsPage,
	apiHelpers,
	editAccountGroupPage,
}) => {
	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

	const account1 = await apiHelpers.headlessAdminUser.postAccount({
		name: `Z ${getRandomString()}`,
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
		account1.externalReferenceCode,
		accountGroup.externalReferenceCode
	);

	const account2 = await apiHelpers.headlessAdminUser.postAccount({
		name: `A ${getRandomString()}`,
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
		account2.externalReferenceCode,
		accountGroup.externalReferenceCode
	);

	await accountGroupsPage.goto();

	await accountGroupsPage.accountGroupLink(accountGroup.name).click();

	await expect(editAccountGroupPage.detailsLink).toBeVisible();

	await accountGroupAccountsPage.accountsTable.search(getRandomString());

	await expect(
		accountGroupAccountsPage.accountsTable.cell(account1.name)
	).toHaveCount(0);
	await expect(
		accountGroupAccountsPage.accountsTable.cell(account2.name)
	).toHaveCount(0);

	await accountGroupAccountsPage.accountsTable.search(account1.name);

	await expect(
		accountGroupAccountsPage.accountsTable.cell(account1.name)
	).toBeVisible();
	await expect(
		accountGroupAccountsPage.accountsTable.cell(account2.name)
	).toHaveCount(0);

	await accountGroupAccountsPage.accountsTable.search(account2.name);

	await expect(
		accountGroupAccountsPage.accountsTable.cell(account1.name)
	).toHaveCount(0);
	await expect(
		accountGroupAccountsPage.accountsTable.cell(account2.name)
	).toBeVisible();

	await accountGroupAccountsPage.accountsTable.search('');

	await expect(
		accountGroupAccountsPage.accountsTable.cell(account1.name)
	).toBeVisible();
	await expect(
		accountGroupAccountsPage.accountsTable.cell(account2.name)
	).toBeVisible();
	await expect(
		await accountGroupAccountsPage.accountsTable.firstRow()
	).toContainText(account2.name);
	await expect(
		await accountGroupAccountsPage.accountsTable.lastRow()
	).toContainText(account1.name);
});

test('Can search assigning accounts in an account group', async ({
	accountGroupAccountSelectorPage,
	accountGroupAccountsPage,
	accountGroupsPage,
	apiHelpers,
	editAccountGroupPage,
}) => {
	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

	const account1 = await apiHelpers.headlessAdminUser.postAccount({
		name: `Z ${getRandomString()}`,
		type: 'business',
	});
	const account2 = await apiHelpers.headlessAdminUser.postAccount({
		name: `A ${getRandomString()}`,
		type: 'business',
	});

	await accountGroupsPage.goto();

	await accountGroupsPage.accountGroupLink(accountGroup.name).click();

	await expect(editAccountGroupPage.detailsLink).toBeVisible();

	await accountGroupAccountsPage.accountsTable.newButton.click();
	await accountGroupAccountSelectorPage.accountsTable.search(
		getRandomString()
	);

	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account1.name)
	).toHaveCount(0);
	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account2.name)
	).toHaveCount(0);

	await accountGroupAccountSelectorPage.accountsTable.search(account1.name);

	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account1.name)
	).toBeVisible();
	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account2.name)
	).toHaveCount(0);

	await accountGroupAccountSelectorPage.accountsTable.search(account2.name);

	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account1.name)
	).toHaveCount(0);
	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account2.name)
	).toBeVisible();

	await accountGroupAccountSelectorPage.accountsTable.search('');

	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account1.name)
	).toBeVisible();
	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account2.name)
	).toBeVisible();
});

test('Can filter assigneed accounts of an account group by status', async ({
	accountGroupAccountSelectorPage,
	accountGroupAccountsPage,
	accountGroupsPage,
	apiHelpers,
	editAccountGroupPage,
}) => {
	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

	const account1 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});
	const account2 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		status: 5,
		type: 'business',
	});

	await accountGroupsPage.goto();

	await accountGroupsPage.accountGroupLink(accountGroup.name).click();

	await expect(editAccountGroupPage.detailsLink).toBeVisible();

	await accountGroupAccountsPage.accountsTable.newButton.click();

	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account1.name)
	).toBeVisible();
	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account2.name)
	).toHaveCount(0);

	await accountGroupAccountSelectorPage.accountsTable.filterButton.click();
	await accountGroupAccountSelectorPage.accountsTable
		.filterMenuItem('Inactive')
		.click();

	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account1.name)
	).toHaveCount(0);
	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account2.name)
	).toBeVisible();

	await accountGroupAccountSelectorPage.accountsTable.filterButton.click();
	await accountGroupAccountSelectorPage.accountsTable
		.filterMenuItem('All')
		.click();

	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account1.name)
	).toBeVisible();
	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account2.name)
	).toBeVisible();

	await accountGroupAccountSelectorPage.selectAccounts([
		account1.name,
		account2.name,
	]);

	await expect(
		accountGroupAccountsPage.accountsTable.cell(account1.name)
	).toBeVisible();
	await expect(
		accountGroupAccountsPage.accountsTable.cell(account2.name)
	).toHaveCount(0);

	await accountGroupAccountsPage.accountsTable.filterButton.click();
	await accountGroupAccountsPage.accountsTable
		.filterMenuItem('Inactive')
		.click();

	await expect(
		accountGroupAccountsPage.accountsTable.cell(account1.name)
	).toHaveCount(0);
	await expect(
		accountGroupAccountsPage.accountsTable.cell(account2.name)
	).toBeVisible();

	await accountGroupAccountsPage.accountsTable.filterButton.click();
	await accountGroupAccountsPage.accountsTable.filterMenuItem('All').click();

	await expect(
		accountGroupAccountsPage.accountsTable.cell(account1.name)
	).toBeVisible();
	await expect(
		accountGroupAccountsPage.accountsTable.cell(account2.name)
	).toBeVisible();
});

test('Can filter assigneed accounts of an account group by type', async ({
	accountGroupAccountSelectorPage,
	accountGroupAccountsPage,
	accountGroupsPage,
	apiHelpers,
	editAccountGroupPage,
}) => {
	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

	const account1 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});
	const account2 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'person',
	});

	await accountGroupsPage.goto();

	await accountGroupsPage.accountGroupLink(accountGroup.name).click();

	await expect(editAccountGroupPage.detailsLink).toBeVisible();

	await accountGroupAccountsPage.accountsTable.newButton.click();

	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account1.name)
	).toBeVisible();
	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account2.name)
	).toBeVisible();

	await accountGroupAccountSelectorPage.accountsTable.filterButton.click();
	await accountGroupAccountSelectorPage.accountsTable
		.filterMenuItem('Business')
		.click();

	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account1.name)
	).toBeVisible();
	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account2.name)
	).toHaveCount(0);

	await accountGroupAccountSelectorPage.accountsTable.filterButton.click();
	await accountGroupAccountSelectorPage.accountsTable
		.filterMenuItem('Person')
		.click();

	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account1.name)
	).toHaveCount(0);
	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account2.name)
	).toBeVisible();

	await accountGroupAccountSelectorPage.accountsTable.clearButton.click();

	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account1.name)
	).toBeVisible();
	await expect(
		accountGroupAccountSelectorPage.accountsTable.cell(account2.name)
	).toBeVisible();

	await accountGroupAccountSelectorPage.selectAccounts([
		account1.name,
		account2.name,
	]);

	await expect(
		accountGroupAccountsPage.accountsTable.cell(account1.name)
	).toBeVisible();
	await expect(
		accountGroupAccountsPage.accountsTable.cell(account2.name)
	).toBeVisible();

	await accountGroupAccountsPage.accountsTable.filterButton.click();
	await accountGroupAccountsPage.accountsTable
		.filterMenuItem('Business')
		.click();

	await expect(
		accountGroupAccountsPage.accountsTable.cell(account1.name)
	).toBeVisible();
	await expect(
		accountGroupAccountsPage.accountsTable.cell(account2.name)
	).toHaveCount(0);

	await accountGroupAccountsPage.accountsTable.filterButton.click();
	await accountGroupAccountsPage.accountsTable
		.filterMenuItem('Person')
		.click();

	await expect(
		accountGroupAccountsPage.accountsTable.cell(account1.name)
	).toHaveCount(0);
	await expect(
		accountGroupAccountsPage.accountsTable.cell(account2.name)
	).toBeVisible();

	await accountGroupAccountsPage.accountsTable.filterButton.click();
	await accountGroupAccountsPage.accountsTable
		.filterMenuItem('Guest')
		.click();

	await expect(accountGroupAccountsPage.noAccountsMessage).toBeVisible();
});

test('Can remove accounts from an account group', async ({
	accountGroupAccountsPage,
	accountGroupsPage,
	accountsPage,
	apiHelpers,
	editAccountGroupPage,
	page,
}) => {
	page.on('dialog', (dialog) => dialog.accept());

	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

	const account1 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
		account1.externalReferenceCode,
		accountGroup.externalReferenceCode
	);

	const account2 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'person',
	});

	await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
		account2.externalReferenceCode,
		accountGroup.externalReferenceCode
	);

	await accountGroupsPage.goto();

	await accountGroupsPage.accountGroupLink(accountGroup.name).click();

	await expect(editAccountGroupPage.detailsLink).toBeVisible();

	await expect(
		accountGroupAccountsPage.accountsTable.cell(account1.name)
	).toBeVisible();
	await expect(
		accountGroupAccountsPage.accountsTable.cell(account2.name)
	).toBeVisible();

	await (await accountGroupAccountsPage.removeLink(account1.name)).click();

	await expect(
		accountGroupAccountsPage.accountsTable.cell(account1.name)
	).toHaveCount(0);
	await expect(
		accountGroupAccountsPage.accountsTable.cell(account2.name)
	).toBeVisible();

	await page.reload();

	await expect(
		accountGroupAccountsPage.accountsTable.cell(account1.name)
	).toHaveCount(0);
	await expect(
		accountGroupAccountsPage.accountsTable.cell(account2.name)
	).toBeVisible();

	await accountGroupAccountsPage.accountsTable.filterButton.click();
	await accountGroupAccountsPage.accountsTable
		.filterMenuItem('Inactive')
		.click();

	await expect(accountGroupAccountsPage.noAccountsMessage).toBeVisible();

	await accountsPage.goto();

	await expect(accountsPage.accountsTable.cell(account1.name)).toBeVisible();
	await expect(accountsPage.accountsTable.cell(account2.name)).toBeVisible();
});

test('User without Assign Account permission can not assign/unassign account to an account group', async ({
	accountGroupAccountsPage,
	accountGroupsPage,
	apiHelpers,
	editAccountGroupPage,
	page,
}) => {
	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
		account.externalReferenceCode,
		accountGroup.externalReferenceCode
	);

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: getRandomString(),
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_account_admin_web_internal_portlet_AccountGroupsAdminPortlet',
				scope: 1,
			},
			{
				actionIds: ['VIEW', 'VIEW_ACCOUNTS'],
				primaryKey: companyId,
				resourceName: 'com.liferay.account.model.AccountGroup',
				scope: 1,
			},
		],
	});

	await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
		role.externalReferenceCode,
		userAccount.id
	);

	await performLogout(page);
	await performLoginViaApi({page, screenName: userAccount.alternateName});

	await accountGroupsPage.goto(false);

	await accountGroupsPage.accountGroupLink(accountGroup.name).click();

	await expect(editAccountGroupPage.detailsLink).toHaveCount(0);
	await expect(accountGroupAccountsPage.accountsTable.newButton).toHaveCount(
		0
	);
	await expect(
		accountGroupAccountsPage.accountsTable.cell(account.name)
	).toBeVisible();
	await expect(
		await accountGroupAccountsPage.accountsTable.cellLink(account.name)
	).toHaveCount(0);
	await expect(
		await accountGroupAccountsPage.removeLink(account.name)
	).toHaveCount(0);
});

test('User with Assign Account permission can assign/unassign account to an account group', async ({
	accountGroupAccountSelectorPage,
	accountGroupAccountsPage,
	accountGroupsPage,
	apiHelpers,
	editAccountGroupPage,
	page,
}) => {
	page.on('dialog', (dialog) => dialog.accept());

	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

	const account1 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
		account1.externalReferenceCode,
		accountGroup.externalReferenceCode
	);

	const account2 = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: getRandomString(),
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_account_admin_web_internal_portlet_AccountGroupsAdminPortlet',
				scope: 1,
			},
			{
				actionIds: ['ASSIGN_ACCOUNTS', 'VIEW', 'VIEW_ACCOUNTS'],
				primaryKey: companyId,
				resourceName: 'com.liferay.account.model.AccountGroup',
				scope: 1,
			},
		],
	});

	await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
		role.externalReferenceCode,
		userAccount.id
	);

	await performLogout(page);
	await performLoginViaApi({page, screenName: userAccount.alternateName});

	await accountGroupsPage.goto(false);

	await accountGroupsPage.accountGroupLink(accountGroup.name).click();

	await expect(editAccountGroupPage.detailsLink).toHaveCount(0);
	await expect(
		accountGroupAccountsPage.accountsTable.newButton
	).toBeVisible();
	await expect(
		accountGroupAccountsPage.accountsTable.cell(account1.name)
	).toBeVisible();
	await expect(
		await accountGroupAccountsPage.accountsTable.cellLink(account1.name)
	).toHaveCount(0);
	await expect(
		await accountGroupAccountsPage.removeLink(account1.name)
	).toBeVisible();

	await expect(async () => {
		await expect(
			accountGroupAccountsPage.accountsTable.searchInput
		).toBeEditable();

		await accountGroupAccountsPage.accountsTable.newButton.click();

		await expect(
			accountGroupAccountSelectorPage.accountsTable.cell(account2.name)
		).toBeVisible();
	}).toPass();

	await accountGroupAccountSelectorPage.selectAccounts([account2.name]);

	await expect(
		accountGroupAccountsPage.accountsTable.cell(account2.name)
	).toBeVisible();
	await expect(
		await accountGroupAccountsPage.removeLink(account2.name)
	).toBeVisible();

	await (await accountGroupAccountsPage.removeLink(account2.name)).click();

	await expect(
		accountGroupAccountsPage.accountsTable.cell(account2.name)
	).toHaveCount(0);
});

test('User without View Account permission can not view accounts in an account group', async ({
	accountGroupsPage,
	apiHelpers,
	page,
}) => {
	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
		account.externalReferenceCode,
		accountGroup.externalReferenceCode
	);

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: getRandomString(),
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_account_admin_web_internal_portlet_AccountGroupsAdminPortlet',
				scope: 1,
			},
			{
				actionIds: ['VIEW'],
				primaryKey: companyId,
				resourceName: 'com.liferay.account.model.AccountGroup',
				scope: 1,
			},
		],
	});

	await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
		role.externalReferenceCode,
		userAccount.id
	);

	await performLogout(page);
	await performLoginViaApi({page, screenName: userAccount.alternateName});

	await accountGroupsPage.goto(false);

	await expect(
		accountGroupsPage.accountGroupsTable.cell(accountGroup.name)
	).toBeVisible();
	await expect(
		await accountGroupsPage.accountGroupsTable.cellLink(accountGroup.name)
	).toHaveCount(0);
	await expect(accountGroupsPage.accountGroupsTable.newButton).toHaveCount(0);
});

test('User with View Account permission can view accounts in an account group', async ({
	accountGroupAccountsPage,
	accountGroupsPage,
	apiHelpers,
	editAccountGroupPage,
	page,
}) => {
	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: getRandomString(),
		type: 'business',
	});

	await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
		account.externalReferenceCode,
		accountGroup.externalReferenceCode
	);

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: getRandomString(),
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_account_admin_web_internal_portlet_AccountGroupsAdminPortlet',
				scope: 1,
			},
			{
				actionIds: ['VIEW', 'VIEW_ACCOUNTS'],
				primaryKey: companyId,
				resourceName: 'com.liferay.account.model.AccountGroup',
				scope: 1,
			},
		],
	});

	await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
		role.externalReferenceCode,
		userAccount.id
	);

	await performLogout(page);
	await performLoginViaApi({page, screenName: userAccount.alternateName});

	await accountGroupsPage.goto(false);

	await expect(accountGroupsPage.accountGroupsTable.newButton).toHaveCount(0);

	await accountGroupsPage.accountGroupLink(accountGroup.name).click();

	await expect(editAccountGroupPage.detailsLink).toHaveCount(0);
	await expect(accountGroupAccountsPage.accountsTable.newButton).toHaveCount(
		0
	);
	await expect(
		accountGroupAccountsPage.accountsTable.cell(account.name)
	).toBeVisible();
	await expect(
		await accountGroupAccountsPage.accountsTable.cellLink(account.name)
	).toHaveCount(0);
});

test('User with Access in Control Panel permission can view account groups section', async ({
	accountGroupsPage,
	apiHelpers,
	page,
}) => {
	const accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const role = await apiHelpers.headlessAdminUser.postRole({
		name: getRandomString(),
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_account_admin_web_internal_portlet_AccountGroupsAdminPortlet',
				scope: 1,
			},
		],
	});

	await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
		role.externalReferenceCode,
		userAccount.id
	);

	await performLogout(page);
	await performLoginViaApi({page, screenName: userAccount.alternateName});

	await accountGroupsPage.goto(false);

	await expect(
		accountGroupsPage.accountGroupLink(accountGroup.name)
	).toHaveCount(0);
	await expect(
		accountGroupsPage.accountGroupsTable.cell(accountGroup.name)
	).toHaveCount(0);
	await expect(accountGroupsPage.accountGroupsTable.newButton).toHaveCount(0);
});

test(
	'User with only Update permission can edit an account group but not view its details',
	{tag: ['@LPS-156788']},
	async ({accountGroupsPage, apiHelpers, editAccountGroupPage, page}) => {
		let accountGroup = await apiHelpers.headlessAdminUser.postAccountGroup({
			name: getRandomString(),
		});

		apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

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

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: getRandomString(),
			rolePermissions: [
				{
					actionIds: ['VIEW_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_account_admin_web_internal_portlet_AccountGroupsAdminPortlet',
					scope: 1,
				},
				{
					actionIds: ['UPDATE', 'VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.account.model.AccountGroup',
					scope: 1,
				},
			],
		});

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			role.externalReferenceCode,
			userAccount.id
		);

		await performLogout(page);
		await performLoginViaApi({page, screenName: userAccount.alternateName});

		await accountGroupsPage.goto(false);

		await expect(
			accountGroupsPage.accountGroupLink(accountGroup.name)
		).toHaveCount(0);
		await expect(
			accountGroupsPage.accountGroupsTable.cell(accountGroup.name)
		).toBeVisible();
		await expect(
			accountGroupsPage.accountGroupsTable.newButton
		).toHaveCount(0);

		await (
			await accountGroupsPage.accountGroupsTable.rowActions(
				accountGroup.name
			)
		).click();
		await accountGroupsPage.editLink.click();

		accountGroup = {
			description: getRandomString(),
			externalReferenceCode: getRandomString(),
			name: getRandomString(),
		};

		await editAccountGroupPage.accountGroupNameInput.fill(
			accountGroup.name
		);
		await editAccountGroupPage.descriptionInput.fill(
			accountGroup.description
		);
		await editAccountGroupPage.externalReferenceCodeInput.fill(
			accountGroup.externalReferenceCode
		);

		await editAccountGroupPage.saveButton.click();

		await waitForAlert(page);

		await editAccountGroupPage.backButton.click();

		await expect(
			accountGroupsPage.accountGroupsTable.cell(accountGroup.name)
		).toBeVisible();

		await (
			await accountGroupsPage.accountGroupsTable.rowActions(
				accountGroup.name
			)
		).click();
		await accountGroupsPage.editLink.click();

		await expect(editAccountGroupPage.accountGroupNameInput).toHaveValue(
			accountGroup.name
		);
		await expect(editAccountGroupPage.descriptionInput).toHaveValue(
			accountGroup.description
		);
		await expect(
			editAccountGroupPage.externalReferenceCodeInput
		).toHaveValue(accountGroup.externalReferenceCode);
	}
);

test(
	'Unable to associate Personal Account with Account Group',
	{tag: ['@LPD-53669']},
	async ({
		accountGroupAccountSelectorPage,
		accountGroupAccountsPage,
		accountGroupsPage,
		apiHelpers,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'person',
		});

		apiHelpers.data.push({id: account.id, type: 'account'});

		const accountGroup =
			await apiHelpers.headlessAdminUser.postAccountGroup({
				name: getRandomString(),
			});

		apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[userAccount.emailAddress]
		);

		await accountGroupsPage.goto();

		await expect(
			accountGroupsPage.accountGroupLink(accountGroup.name)
		).toBeVisible();

		await accountGroupsPage.accountGroupLink(accountGroup.name).click();

		await expect(async () => {
			await expect(
				accountGroupAccountsPage.accountsTable.searchInput
			).toBeEditable();

			await accountGroupAccountsPage.accountsTable.newButton.click();

			await expect(
				accountGroupAccountSelectorPage.accountsTable.cell(account.name)
			).toBeVisible();
		}).toPass();
	}
);

test(
	'Can search an account group and view its status',
	{tag: '@LPD-55109'},
	async ({accountGroupsPage, apiHelpers}) => {
		const accountGroup =
			await apiHelpers.headlessAdminUser.postAccountGroup();

		await accountGroupsPage.goto();

		await accountGroupsPage.accountGroupsTable.search(accountGroup.name);

		await expect(
			accountGroupsPage.accountGroupsTable.cell(accountGroup.name)
		).toHaveCount(1);
		await expect(
			accountGroupsPage.accountGroupsTable.cell('Approved')
		).toBeVisible();
	}
);

test(
	'Account group display should show the correct number of accounts',
	{tag: ['@LPD-55664']},
	async ({accountGroupsPage, apiHelpers}) => {
		const accountGroup =
			await apiHelpers.headlessAdminUser.postAccountGroup();

		apiHelpers.data.push({id: accountGroup.id, type: 'accountGroup'});

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		await apiHelpers.headlessAdminUser.assignAccountToAccountGroup(
			account.externalReferenceCode,
			accountGroup.externalReferenceCode
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
			productAccountGroupFilter: true,
			productAccountGroups: [
				{
					accountGroupId: accountGroup.id,
					id: 0,
				},
			],
		});

		await accountGroupsPage.goto();

		await expect(
			accountGroupsPage.accountGroupsTable.cell('2', true)
		).toHaveCount(0);
		await expect(
			accountGroupsPage.accountGroupsTable.cell('1', true)
		).toBeVisible();
	}
);
