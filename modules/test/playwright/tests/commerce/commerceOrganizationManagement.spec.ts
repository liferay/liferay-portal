/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';

import {commercePagesTest} from '../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../fixtures/usersAndOrganizationsPagesTest';
import {getRandomInt} from '../../utils/getRandomInt';
import getRandomString from '../../utils/getRandomString';
import {waitForAlert} from '../../utils/waitForAlert';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest(),
	usersAndOrganizationsPagesTest
);

async function waitForAnimationEnd(locator: Locator) {
	const handle = await locator.elementHandle();
	await handle?.waitForElementState('stable');

	return handle?.dispose();
}

test('LPD-30190 Can move accounts and organizations in the widget', async ({
	apiHelpers,
	organizationManagementPage,
	page,
	usersAndOrganizationsPage,
}) => {
	page.on('dialog', (dialog) => dialog.accept());

	const site = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});

	apiHelpers.data.push({id: site.id, type: 'site'});

	const organization1 = await apiHelpers.headlessAdminUser.postOrganization({
		name: `Org${getRandomInt()}`,
	});
	const organization2 = await apiHelpers.headlessAdminUser.postOrganization({
		name: `Org${getRandomInt()}`,
	});
	const organization3 = await apiHelpers.headlessAdminUser.postOrganization({
		name: `Org${getRandomInt()}`,
		parentOrganization: {
			externalReferenceCode: organization2.externalReferenceCode,
		},
	});

	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: `Acc${getRandomInt()}`,
	});

	apiHelpers.data.push({id: account.id, type: 'account'});

	await apiHelpers.headlessAdminUser.postAccountOrganization(
		account.id,
		organization2.id
	);

	await usersAndOrganizationsPage.goToOrganizationChart();

	await expect(organizationManagementPage.chart).toBeVisible();

	await waitForAnimationEnd(
		organizationManagementPage.organizationNode(organization2.name)
	);
	await organizationManagementPage
		.organizationNode(organization2.name)
		.click();
	await waitForAnimationEnd(
		organizationManagementPage.organizationNode(organization3.name)
	);

	await expect(
		organizationManagementPage.accountNode(account.name)
	).toHaveCount(1);
	await expect(
		organizationManagementPage.organizationNode(organization3.name)
	).toHaveCount(1);

	await organizationManagementPage
		.accountNode(account.name)
		.dragTo(
			organizationManagementPage.organizationNode(organization1.name)
		);
	await organizationManagementPage
		.organizationNode(organization3.name)
		.dragTo(
			organizationManagementPage.organizationNode(organization1.name)
		);

	await page.waitForTimeout(200);
	await page.reload();

	await expect(organizationManagementPage.chart).toBeVisible();

	await waitForAnimationEnd(
		organizationManagementPage.organizationNode(organization2.name)
	);
	await organizationManagementPage
		.organizationNode(organization2.name)
		.click();

	await expect(
		organizationManagementPage.accountNode(account.name)
	).toHaveCount(0);
	await expect(
		organizationManagementPage.organizationNode(organization3.name)
	).toHaveCount(0);

	await organizationManagementPage
		.organizationNode(organization1.name)
		.click();
	await waitForAnimationEnd(
		organizationManagementPage.organizationNode(organization3.name)
	);

	await expect(
		organizationManagementPage.accountNode(account.name)
	).toHaveCount(1);
	await expect(
		organizationManagementPage.organizationNode(organization3.name)
	).toHaveCount(1);

	await organizationManagementPage
		.menuButton(
			organizationManagementPage.organizationNode(organization3.name)
		)
		.click();
	await organizationManagementPage.removeItem.click();

	await expect(
		organizationManagementPage.organizationNode(organization3.name)
	).toHaveCount(0);

	await page.waitForTimeout(200);
	await page.reload();

	await expect(organizationManagementPage.chart).toBeVisible();
	await expect(
		organizationManagementPage.organizationNode(organization3.name)
	).toHaveCount(1);

	await waitForAnimationEnd(
		organizationManagementPage.organizationNode(organization1.name)
	);
	await organizationManagementPage
		.organizationNode(organization1.name)
		.click();

	await expect(
		organizationManagementPage.accountNode(account.name)
	).toHaveCount(1);
	await expect(
		organizationManagementPage.organizationNode(organization3.name)
	).toHaveCount(1);
});

test('LPD-31011 Can associate existing user using the widget', async ({
	apiHelpers,
	organizationManagementPage,
	page,
	usersAndOrganizationsPage,
}) => {
	page.on('dialog', (dialog) => dialog.accept());

	const organization = await apiHelpers.headlessAdminUser.postOrganization({
		name: `Org${getRandomInt()}`,
	});

	await usersAndOrganizationsPage.goToOrganizationChart();

	await expect(organizationManagementPage.chart).toBeVisible();

	await waitForAnimationEnd(
		organizationManagementPage.organizationNode(organization.name)
	);
	await organizationManagementPage
		.organizationNode(organization.name)
		.click();
	await waitForAnimationEnd(organizationManagementPage.addNode);

	await organizationManagementPage.addUserToOrganization();
	await waitForAlert(page, `1 user was added to ${organization.name}`);

	apiHelpers.data.push({
		id: `${organization.id}_test@liferay.com`,
		type: 'organizationUserAccountAssociation',
	});

	await page.reload();

	await waitForAnimationEnd(
		organizationManagementPage.organizationNode(organization.name)
	);
	await organizationManagementPage
		.organizationNode(organization.name)
		.click();
	await waitForAnimationEnd(organizationManagementPage.addNode);

	await expect(organizationManagementPage.userNode('Test Test')).toHaveCount(
		1
	);
});

test('LPD-31026 Can add new user using the widget', async ({
	apiHelpers,
	organizationManagementPage,
	page,
	usersAndOrganizationsPage,
}) => {
	const organization = await apiHelpers.headlessAdminUser.postOrganization({
		name: `Org${getRandomInt()}`,
	});

	await usersAndOrganizationsPage.goToOrganizationChart();

	await expect(organizationManagementPage.chart).toBeVisible();

	await waitForAnimationEnd(
		organizationManagementPage.organizationNode(organization.name)
	);
	await organizationManagementPage
		.organizationNode(organization.name)
		.click();
	await waitForAnimationEnd(organizationManagementPage.addNode);

	const userEmailAddress = `${getRandomInt()}@liferay.com`;

	await organizationManagementPage.addUserToOrganization({
		email: userEmailAddress,
	});
	await waitForAlert(page, `1 user was added to ${organization.name}`);

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			userEmailAddress
		);

	apiHelpers.data.push({
		id: user.id,
		type: 'userAccount',
	});
	apiHelpers.data.push({
		id: `${organization.id}_${userEmailAddress}`,
		type: 'organizationUserAccountAssociation',
	});

	await page.reload();

	await waitForAnimationEnd(
		organizationManagementPage.organizationNode(organization.name)
	);
	await organizationManagementPage
		.organizationNode(organization.name)
		.click();
	await waitForAnimationEnd(organizationManagementPage.addNode);

	await expect(
		organizationManagementPage.userNode(
			userEmailAddress.substr(0, userEmailAddress.indexOf('@'))
		)
	).toHaveCount(1);
});

test('LPD-31052 Can associate existing account using the widget', async ({
	apiHelpers,
	organizationManagementPage,
	page,
	usersAndOrganizationsPage,
}) => {
	const account = await apiHelpers.headlessAdminUser.postAccount({
		name: `Acc${getRandomInt()}`,
	});

	apiHelpers.data.push({id: account.id, type: 'account'});

	const organization = await apiHelpers.headlessAdminUser.postOrganization({
		name: `Org${getRandomInt()}`,
	});

	await usersAndOrganizationsPage.goToOrganizationChart();

	await expect(organizationManagementPage.chart).toBeVisible();

	await waitForAnimationEnd(
		organizationManagementPage.organizationNode(organization.name)
	);
	await organizationManagementPage
		.organizationNode(organization.name)
		.click();
	await waitForAnimationEnd(organizationManagementPage.addNode);

	await organizationManagementPage.addAccountToOrganization({
		accountName: account.name,
		isNew: false,
	});
	await waitForAlert(page, `1 account was added to ${organization.name}`);

	await page.reload();

	await waitForAnimationEnd(
		organizationManagementPage.organizationNode(organization.name)
	);
	await organizationManagementPage
		.organizationNode(organization.name)
		.click();
	await waitForAnimationEnd(organizationManagementPage.addNode);

	await expect(
		organizationManagementPage.accountNode(account.name)
	).toHaveCount(1);
});

test('LPD-31052 Can add new account using the widget', async ({
	apiHelpers,
	organizationManagementPage,
	page,
	usersAndOrganizationsPage,
}) => {
	const organization = await apiHelpers.headlessAdminUser.postOrganization({
		name: `Org${getRandomInt()}`,
	});

	await usersAndOrganizationsPage.goToOrganizationChart();

	await expect(organizationManagementPage.chart).toBeVisible();

	await waitForAnimationEnd(
		organizationManagementPage.organizationNode(organization.name)
	);
	await organizationManagementPage
		.organizationNode(organization.name)
		.click();
	await waitForAnimationEnd(organizationManagementPage.addNode);

	const accountName = `Acc${getRandomInt()}`;

	await organizationManagementPage.addAccountToOrganization({
		accountName,
		isNew: true,
	});
	await waitForAlert(page, `1 account was added to ${organization.name}`);

	const account =
		await apiHelpers.headlessAdminUser.getAccountByName(accountName);

	apiHelpers.data.push({id: account.id, type: 'account'});

	await page.reload();

	await waitForAnimationEnd(
		organizationManagementPage.organizationNode(organization.name)
	);
	await organizationManagementPage
		.organizationNode(organization.name)
		.click();
	await waitForAnimationEnd(organizationManagementPage.addNode);

	await expect(
		organizationManagementPage.accountNode(accountName)
	).toHaveCount(1);
});

test('LPD-31403 Can add new organization using the widget', async ({
	apiHelpers,
	organizationManagementPage,
	page,
	usersAndOrganizationsPage,
}) => {
	const organization1 = await apiHelpers.headlessAdminUser.postOrganization({
		name: `Org${getRandomInt()}`,
	});

	await usersAndOrganizationsPage.goToOrganizationChart();

	await expect(organizationManagementPage.chart).toBeVisible();

	await waitForAnimationEnd(
		organizationManagementPage.organizationNode(organization1.name)
	);
	await organizationManagementPage
		.organizationNode(organization1.name)
		.click();
	await waitForAnimationEnd(organizationManagementPage.addNode);

	const organizationName = `Org${getRandomInt()}`;

	await organizationManagementPage.addOrganizationToOrganization({
		organizationName,
	});
	await waitForAlert(
		page,
		`1 organization was added to ${organization1.name}`
	);

	const organization2 =
		await apiHelpers.headlessAdminUser.getOrganizationByName(
			organizationName
		);

	apiHelpers.data.push({id: organization2.id, type: 'organization'});

	await page.reload();

	await waitForAnimationEnd(
		organizationManagementPage.organizationNode(organization1.name)
	);
	await organizationManagementPage
		.organizationNode(organization1.name)
		.click();
	await waitForAnimationEnd(organizationManagementPage.addNode);

	await expect(
		organizationManagementPage.organizationNode(organization2.name)
	).toHaveCount(1);
});
