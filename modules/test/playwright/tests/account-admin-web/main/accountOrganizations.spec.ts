/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountsPagesTest} from '../../../fixtures/accountsPagesTest';
import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {serverAdministrationPageTest} from '../../../fixtures/serverAdministrationPageTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {nextPage, setItemsPerPage} from '../../../utils/pagination';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	accountsPagesTest,
	apiHelpersTest,
	applicationsMenuPageTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35914': {enabled: true},
	}),
	loginTest(),
	usersAndOrganizationsPagesTest,
	serverAdministrationPageTest
);

test('LPD-47225 Can add and remove an organizations to an account', async ({
	accountOrganizationSelectorPage,
	accountOrganizationsPage,
	accountsPage,
	apiHelpers,
	page,
}) => {
	page.on('dialog', (dialog) => dialog.accept());

	const account = await apiHelpers.headlessAdminUser.postAccount();

	const organization1 = await apiHelpers.headlessAdminUser.postOrganization();
	const organization2 = await apiHelpers.headlessAdminUser.postOrganization();

	await accountsPage.goto();

	await (await accountsPage.accountsTable.valueLink(account.name)).click();

	await accountsPage.organizationsTab.click();
	await accountOrganizationsPage.organizationsTable.newButton.click();

	await expect(
		accountOrganizationSelectorPage.organizationsTable.cell('Approved')
	).toBeVisible();

	await accountOrganizationSelectorPage.assignOrganizations([
		organization1.name,
	]);

	await accountOrganizationsPage.organizationsTable.newButton.click();

	await accountOrganizationSelectorPage.assignOrganizations([
		organization2.name,
	]);

	await expect(
		accountOrganizationsPage.organizationsTable.cell(organization1.name)
	).toBeVisible();
	await expect(
		accountOrganizationsPage.organizationsTable.cell(organization2.name)
	).toBeVisible();

	await expect(
		accountOrganizationsPage.organizationsTable.cell('Approved')
	).toBeVisible();

	await (
		await accountOrganizationsPage.organizationRemoveButton(
			organization1.name
		)
	).click();

	await expect(
		accountOrganizationsPage.organizationsTable.cell(organization1.name)
	).toHaveCount(0);
	await expect(
		accountOrganizationsPage.organizationsTable.cell(organization2.name)
	).toBeVisible();
});

test('LPD-47225 Can add and remove organizations to an account in bulk', async ({
	accountOrganizationSelectorPage,
	accountOrganizationsPage,
	accountsPage,
	apiHelpers,
	page,
}) => {
	page.on('dialog', (dialog) => dialog.accept());

	const account = await apiHelpers.headlessAdminUser.postAccount();

	const organizations = [];

	for (let i = 1; i < 5; i++) {
		organizations.push(
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `Organization ${i}`,
			})
		);
	}

	await accountsPage.goto();

	await (await accountsPage.accountsTable.cellLink(account.name)).click();

	await accountsPage.organizationsTab.click();
	await accountOrganizationsPage.organizationsTable.newButton.click();

	await accountOrganizationSelectorPage.assignOrganizations(
		organizations.map((organization) => organization.name)
	);

	for (const organization of organizations) {
		await expect(
			accountOrganizationsPage.organizationsTable.cell(organization.name)
		).toBeVisible();
	}

	for (const index of [1, 3]) {
		await (
			await accountOrganizationsPage.organizationsTable.rowCheckbox(
				`Organization ${index}`
			)
		).check();
	}

	await accountOrganizationsPage.removeButton.click();

	await waitForAlert(page);

	for (let i = 1; i < 5; i++) {
		if (i % 2 === 0) {
			await expect(
				accountOrganizationsPage.organizationsTable.cell(
					`Organization ${i}`
				)
			).toBeVisible();
		}
		else {
			await expect(
				accountOrganizationsPage.organizationsTable.cell(
					`Organization ${i}`
				)
			).not.toBeVisible();
		}
	}
});

test('LPD-47225 Can search assigned organizations', async ({
	accountOrganizationSelectorPage,
	accountOrganizationsPage,
	accountsPage,
	apiHelpers,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount();

	const organization1 = await apiHelpers.headlessAdminUser.postOrganization();
	const organization2 = await apiHelpers.headlessAdminUser.postOrganization();

	await accountsPage.goto();

	await (await accountsPage.accountsTable.cellLink(account.name)).click();

	await accountsPage.organizationsTab.click();
	await accountOrganizationsPage.organizationsTable.newButton.click();

	await accountOrganizationSelectorPage.assignOrganizations([
		organization1.name,
		organization2.name,
	]);

	await expect(
		accountOrganizationsPage.organizationsTable.cell(organization1.name)
	).toBeVisible();
	await expect(
		accountOrganizationsPage.organizationsTable.cell(organization2.name)
	).toBeVisible();

	await accountOrganizationsPage.organizationsTable.search(getRandomString());

	await expect(
		accountOrganizationsPage.organizationsTable.cell(organization1.name)
	).toHaveCount(0);
	await expect(
		accountOrganizationsPage.organizationsTable.cell(organization2.name)
	).toHaveCount(0);

	await accountOrganizationsPage.organizationsTable.search(
		organization1.name
	);

	await expect(
		accountOrganizationsPage.organizationsTable.cell(organization1.name)
	).toBeVisible();
	await expect(
		accountOrganizationsPage.organizationsTable.cell(organization2.name)
	).toHaveCount(0);

	await accountOrganizationsPage.organizationsTable.search(
		organization2.name
	);

	await expect(
		accountOrganizationsPage.organizationsTable.cell(organization1.name)
	).toHaveCount(0);
	await expect(
		accountOrganizationsPage.organizationsTable.cell(organization2.name)
	).toBeVisible();

	await accountOrganizationsPage.organizationsTable.search('');

	await expect(
		accountOrganizationsPage.organizationsTable.cell(organization1.name)
	).toBeVisible();
	await expect(
		accountOrganizationsPage.organizationsTable.cell(organization2.name)
	).toBeVisible();
});

test('LPD-47225 Can search organizations during assignment', async ({
	accountOrganizationSelectorPage,
	accountOrganizationsPage,
	accountsPage,
	apiHelpers,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount();

	const organization1 = await apiHelpers.headlessAdminUser.postOrganization();
	const organization2 = await apiHelpers.headlessAdminUser.postOrganization();

	await accountsPage.goto();

	await (await accountsPage.accountsTable.cellLink(account.name)).click();

	await accountsPage.organizationsTab.click();
	await accountOrganizationsPage.organizationsTable.newButton.click();

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

	await accountOrganizationSelectorPage.organizationsTable.search(
		getRandomString()
	);

	await expect(
		accountOrganizationSelectorPage.organizationsTable.cell(
			organization1.name
		)
	).toHaveCount(0);
	await expect(
		accountOrganizationSelectorPage.organizationsTable.cell(
			organization2.name
		)
	).toHaveCount(0);

	await accountOrganizationSelectorPage.organizationsTable.search(
		organization1.name
	);

	await expect(
		accountOrganizationSelectorPage.organizationsTable.cell(
			organization1.name
		)
	).toBeVisible();
	await expect(
		accountOrganizationSelectorPage.organizationsTable.cell(
			organization2.name
		)
	).toHaveCount(0);

	await accountOrganizationSelectorPage.organizationsTable.search(
		organization2.name
	);

	await expect(
		accountOrganizationSelectorPage.organizationsTable.cell(
			organization1.name
		)
	).toHaveCount(0);
	await expect(
		accountOrganizationSelectorPage.organizationsTable.cell(
			organization2.name
		)
	).toBeVisible();

	await accountOrganizationSelectorPage.organizationsTable.search('');

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
});

test('LPD-47225 Can paginate organizations during assignment', async ({
	accountOrganizationSelectorPage,
	accountOrganizationsPage,
	accountsPage,
	apiHelpers,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount();

	const organizations = [];

	for (let i = 1; i <= 21; i++) {
		organizations.push(
			await apiHelpers.headlessAdminUser.postOrganization({
				name: `${String(i).padStart(2, '0')}_Organization `,
			})
		);
	}

	await accountsPage.goto();

	await (await accountsPage.accountsTable.cellLink(account.name)).click();
	await accountsPage.organizationsTab.click();
	await accountOrganizationsPage.organizationsTable.newButton.click();

	await setItemsPerPage(accountOrganizationSelectorPage.frame, 20);

	for (const [index, organization] of organizations.entries()) {
		if (index < 20) {
			await expect(
				accountOrganizationSelectorPage.organizationsTable.cell(
					organization.name
				)
			).toBeVisible();
		}
		else {
			await expect(
				accountOrganizationSelectorPage.organizationsTable.cell(
					organization.name
				)
			).toHaveCount(0);
		}
	}

	await nextPage(accountOrganizationSelectorPage.frame);

	for (const [index, organization] of organizations.entries()) {
		if (index < 20) {
			await expect(
				accountOrganizationSelectorPage.organizationsTable.cell(
					organization.name
				)
			).toHaveCount(0);
		}
		else {
			await expect(
				accountOrganizationSelectorPage.organizationsTable.cell(
					organization.name
				)
			).toBeVisible();
		}
	}
});
