/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../utils/waitForAlert';
import {faroConfig} from '../main/faro.config';
import {ACPage, navigateToACSettingsViaURL} from '../main/utils/navigation';
import {signInToAnalyticsCloud} from '../main/utils/signInToAnalyticsCloud';
import {
	selectPaginationItemsPerPage,
	selectPaginationPageNumber,
	viewPaginationResults,
} from '../main/utils/utils';

export const test = mergeTests(
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedChannelTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

test(
	'Delete button to manage user permissions',
	{
		tag: ['@LRAC-9063', '@LRAC-9069', '@LRAC-9070', '@LRAC-9055'],
	},
	async ({page, project}) => {
		await test.step('go to AC Properties Page', async () => {
			await navigateToACSettingsViaURL({
				acPage: ACPage.userManagementPage,
				page,
				projectID: project.groupId,
			});
		});

		await test.step('add two users', async () => {
			await page.getByRole('button', {name: 'Invite Users'}).click();

			await page
				.getByPlaceholder('Enter Email Address')
				.fill('user1@liferay.com');

			await page.keyboard.press('Enter');

			await page
				.getByPlaceholder('Enter Email Address')
				.fill('user2@liferay.com');

			await page.keyboard.press('Enter');

			await page.getByRole('button', {name: 'Send'}).click();

			await expect(
				page.getByText('Success:Invitations have been sent.')
			).toBeVisible();
		});

		await test.step('check users', async () => {
			await expect(
				page.getByRole('cell', {name: 'user1@liferay.com'})
			).toBeVisible();
			await expect(
				page.getByRole('cell', {name: 'user2@liferay.com'})
			).toBeVisible();
		});

		await test.step('check users', async () => {
			await page
				.getByRole('row', {name: 'user1@liferay.com'})
				.getByLabel('Delete')
				.click();

			await page.getByRole('button', {name: 'Continue'}).click();

			await expect(
				page.getByText('Success:1 user has been deleted.').first()
			).toBeVisible();
			await expect(
				page.getByRole('cell', {name: 'user1@liferay.com'})
			).not.toBeVisible();
		});

		await test.step('check users', async () => {
			await page
				.getByRole('row', {name: 'user2@liferay.com'})
				.getByLabel('Delete')
				.click();

			await page.getByRole('button', {name: 'Continue'}).click();

			await expect(
				page.getByText('Success:1 user has been deleted.').nth(1)
			).toBeVisible();
			await expect(
				page.getByRole('cell', {name: 'user2@liferay.com'})
			).not.toBeVisible();
		});
	}
);

test(
	'User Management settings page surfaces the header and Invite Users action',
	{
		tag: '@LRAC-9048',
	},
	async ({page, project}) => {
		await navigateToACSettingsViaURL({
			acPage: ACPage.userManagementPage,
			page,
			projectID: project.groupId,
		});

		await expect(
			page.getByRole('heading', {name: 'User Management'})
		).toBeVisible();

		await expect(
			page.getByRole('button', {name: 'Invite Users'})
		).toBeVisible();
	}
);

test(
	'Owner row in User Management has its Edit, Delete and checkbox disabled',
	{
		tag: ['@LRAC-9049', '@LRAC-9050'],
	},
	async ({page, project}) => {
		await navigateToACSettingsViaURL({
			acPage: ACPage.userManagementPage,
			page,
			projectID: project.groupId,
		});

		const ownerEmail = 'bryan.cheung@faro.io';

		await page.getByPlaceholder('Search').first().fill(ownerEmail);

		await page.keyboard.press('Enter');

		const ownerRow = page.getByRole('row', {name: ownerEmail});

		await expect(ownerRow).toBeVisible();

		await expect(ownerRow.getByRole('button').first()).not.toBeVisible();

		await expect(ownerRow.getByLabel('Delete')).not.toBeVisible();

		await expect(ownerRow.getByRole('checkbox')).toBeDisabled();
	}
);

test(
	'Admin row in User Management can be selected via its checkbox',
	{
		tag: ['@LRAC-9052', '@LRAC-9054'],
	},
	async ({page, project}) => {
		await navigateToACSettingsViaURL({
			acPage: ACPage.userManagementPage,
			page,
			projectID: project.groupId,
		});

		const adminEmail = 'corbin.murakami@faro.io';

		await page.getByPlaceholder('Search').first().fill(adminEmail);

		await page.keyboard.press('Enter');

		const adminRow = page.getByRole('row', {name: adminEmail});

		const adminCheckbox = adminRow.getByRole('checkbox');

		await adminCheckbox.check();

		await expect(adminCheckbox).toBeChecked();
	}
);

test(
	'Admin user can promote an invited user to Administrator',
	{
		tag: '@LRAC-9095',
	},
	async ({page, project}) => {
		const invitedEmail = `admin-role-${Date.now()}@liferay.com`;

		try {
			await signInToAnalyticsCloud(page, 'michelle.hoshi@faro.io');

			await navigateToACSettingsViaURL({
				acPage: ACPage.userManagementPage,
				page,
				projectID: project.groupId,
			});

			await page.getByRole('button', {name: 'Invite Users'}).click();

			await page
				.getByPlaceholder('Enter Email Address')
				.fill(invitedEmail);

			await page.keyboard.press('Enter');

			await page.getByRole('button', {name: 'Send'}).click();

			await expect(
				page.getByText('Success:Invitations have been sent.')
			).toBeVisible();

			const invitedRow = page.getByRole('row', {name: invitedEmail});

			await invitedRow.getByRole('button').first().click();

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					exact: true,
					name: 'Administrator',
				}),
				trigger: page.getByRole('button', {
					exact: true,
					name: 'Member',
				}),
			});

			await page.getByRole('button', {name: 'Save'}).click();

			await expect(
				page.getByText(
					'Success:Permissions have been changed for 1 users.'
				)
			).toBeVisible();

			await expect(
				invitedRow.getByText('Administrator', {exact: true})
			).toBeVisible();

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('button', {name: 'Continue'}),
				trigger: invitedRow.getByLabel('Delete'),
			});

			await expect(
				page.getByText('Success:1 user has been deleted.')
			).toBeVisible();
		}
		finally {
			await signInToAnalyticsCloud(page, faroConfig.user.login);
		}
	}
);

test(
	'Bulk-selected users can be promoted to Administrator and demoted to Member',
	{
		tag: ['@LRAC-9087', '@LRAC-9088'],
	},
	async ({page, project}) => {
		const runId = Date.now();

		const emails = [
			`bulk-1-${runId}@liferay.com`,
			`bulk-2-${runId}@liferay.com`,
		];

		await navigateToACSettingsViaURL({
			acPage: ACPage.userManagementPage,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('button', {name: 'Invite Users'}).click();

		for (const email of emails) {
			await page.getByPlaceholder('Enter Email Address').fill(email);

			await page.keyboard.press('Enter');
		}

		await page.getByRole('button', {name: 'Send'}).click();

		await expect(
			page.getByText('Success:Invitations have been sent.')
		).toBeVisible();

		const rows = emails.map((email) =>
			page.getByRole('row', {name: email})
		);

		try {

			// Bulk change both rows to Administrator, then back to Member.

			for (const newRole of ['Administrator', 'Member']) {
				for (const row of rows) {
					await row.getByRole('checkbox').check();
				}

				await clickAndExpectToBeVisible({
					target: page.getByText(
						'Edit Permissions for Selected Users'
					),
					trigger: page.getByRole('button', {
						name: 'Change Permissions',
					}),
				});

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('menuitem', {
						exact: true,
						name: newRole,
					}),
					trigger: page.getByRole('button', {
						name: 'Select Permission',
					}),
				});

				await page.getByRole('button', {name: 'Save'}).click();

				await expect(
					page.getByText(
						'Success:Permissions have been changed for 2 users.'
					)
				).toBeVisible();

				await expect(page.getByRole('dialog')).toBeHidden();

				for (const row of rows) {
					await expect(
						row.getByText(newRole, {exact: true})
					).toBeVisible();
				}
			}
		}
		finally {
			for (const row of rows) {
				await row.getByLabel('Delete').click();

				await page
					.getByRole('dialog')
					.getByRole('button', {name: 'Continue'})
					.click();

				await waitForAlert(page, 'Success:1 user has been deleted.');
			}
		}
	}
);

test(
	'An invited user can be promoted to Administrator and demoted back to Member',
	{
		tag: '@LRAC-9086',
	},
	async ({page, project}) => {
		const invitedEmail = `role-roundtrip-${Date.now()}@liferay.com`;

		await navigateToACSettingsViaURL({
			acPage: ACPage.userManagementPage,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('button', {name: 'Invite Users'}).click();

		await page.getByPlaceholder('Enter Email Address').fill(invitedEmail);

		await page.keyboard.press('Enter');

		await page.getByRole('button', {name: 'Send'}).click();

		await expect(
			page.getByText('Success:Invitations have been sent.')
		).toBeVisible();

		const invitedRow = page.getByRole('row', {name: invitedEmail});

		try {

			// Promote Member -> Administrator and back to Member, asserting the
			// displayed role after each change.

			for (const newRole of ['Administrator', 'Member']) {
				await invitedRow.getByRole('button').first().click();

				const currentRole =
					newRole === 'Administrator' ? 'Member' : 'Administrator';

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('menuitem', {
						exact: true,
						name: newRole,
					}),
					trigger: page.getByRole('button', {
						exact: true,
						name: currentRole,
					}),
				});

				await page.getByRole('button', {name: 'Save'}).click();

				await expect(
					page
						.getByText(
							'Success:Permissions have been changed for 1 users.'
						)
						.first()
				).toBeVisible();

				await expect(
					invitedRow.getByText(newRole, {exact: true})
				).toBeVisible();
			}
		}
		finally {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('button', {name: 'Continue'}),
				trigger: invitedRow.getByLabel('Delete'),
			});

			await expect(
				page.getByText('Success:1 user has been deleted.')
			).toBeVisible();
		}
	}
);

test(
	'Member user cannot add a data source or create a property',
	{
		tag: ['@LRAC-9096', '@LRAC-9083'],
	},
	async ({page, project}) => {
		try {
			await signInToAnalyticsCloud(page, 'corbin.murakami@faro.io');

			await navigateToACSettingsViaURL({
				acPage: ACPage.dataSourcePage,
				page,
				projectID: project.groupId,
			});

			await expect(
				page.getByRole('button', {name: 'Add Data Source'})
			).toHaveCount(0);

			await navigateToACSettingsViaURL({
				acPage: ACPage.propertiesPage,
				page,
				projectID: project.groupId,
			});

			await expect(
				page.getByRole('button', {name: 'New Property'})
			).toHaveCount(0);
		}
		finally {
			await signInToAnalyticsCloud(page, faroConfig.user.login);
		}
	}
);

test(
	'Member user cannot invite, delete, or change roles in User Management',
	{
		tag: ['@LRAC-9066', '@LRAC-9073', '@LRAC-9098'],
	},
	async ({page, project}) => {
		try {
			await signInToAnalyticsCloud(page, 'corbin.murakami@faro.io');

			await navigateToACSettingsViaURL({
				acPage: ACPage.userManagementPage,
				page,
				projectID: project.groupId,
			});

			await expect(
				page.getByRole('button', {name: 'Invite Users'})
			).toHaveCount(0);

			await expect(page.getByLabel('Delete')).toHaveCount(0);

			await expect(page.getByLabel('Edit')).toHaveCount(0);
		}
		finally {
			await signInToAnalyticsCloud(page, faroConfig.user.login);
		}
	}
);

test(
	'Admin and Owner users can delete an invited user from the User Management page',
	{
		tag: ['@LRAC-9065', '@LRAC-8143'],
	},
	async ({page, project}) => {
		const deleters = [
			{email: 'michelle.hoshi@faro.io', role: 'admin'},
			{email: 'bryan.cheung@faro.io', role: 'owner'},
		];

		try {
			for (const deleter of deleters) {
				await signInToAnalyticsCloud(page, deleter.email);

				await navigateToACSettingsViaURL({
					acPage: ACPage.userManagementPage,
					page,
					projectID: project.groupId,
				});

				const invitedEmail = `${deleter.role}-delete-${Date.now()}@liferay.com`;

				await page.getByRole('button', {name: 'Invite Users'}).click();

				await page
					.getByPlaceholder('Enter Email Address')
					.fill(invitedEmail);

				await page.keyboard.press('Enter');

				await page.getByRole('button', {name: 'Send'}).click();

				await expect(
					page.getByText('Success:Invitations have been sent.')
				).toBeVisible();

				await page
					.getByRole('row', {name: invitedEmail})
					.getByLabel('Delete')
					.click();

				await page.getByRole('button', {name: 'Continue'}).click();

				await expect(
					page.getByRole('cell', {name: invitedEmail})
				).toHaveCount(0);
			}
		}
		finally {
			await signInToAnalyticsCloud(page, faroConfig.user.login);
		}
	}
);

test(
	'Admin and Owner users can invite a new user via the User Management page',
	{
		tag: ['@LRAC-9071', '@LRAC-9072'],
	},
	async ({page, project}) => {
		const inviters = [
			{email: 'michelle.hoshi@faro.io', role: 'Admin'},
			{email: 'bryan.cheung@faro.io', role: 'Owner'},
		];

		try {
			for (const inviter of inviters) {
				await signInToAnalyticsCloud(page, inviter.email);

				await navigateToACSettingsViaURL({
					acPage: ACPage.userManagementPage,
					page,
					projectID: project.groupId,
				});

				const invitedEmail = `invited-${inviter.role.toLowerCase()}-${Date.now()}@liferay.com`;

				await page.getByRole('button', {name: 'Invite Users'}).click();

				await page
					.getByPlaceholder('Enter Email Address')
					.fill(invitedEmail);

				await page.keyboard.press('Enter');

				await page.getByRole('button', {name: 'Send'}).click();

				await expect(
					page.getByText('Success:Invitations have been sent.')
				).toBeVisible();

				await expect(
					page.getByRole('cell', {name: invitedEmail})
				).toBeVisible();

				await page
					.getByRole('row', {name: invitedEmail})
					.getByLabel('Delete')
					.click();

				await page.getByRole('button', {name: 'Continue'}).click();
			}
		}
		finally {
			await signInToAnalyticsCloud(page, faroConfig.user.login);
		}
	}
);

test(
	'User Management settings page search filters the list to matching users',
	{
		tag: '@LRAC-8142',
	},
	async ({page, project}) => {
		await navigateToACSettingsViaURL({
			acPage: ACPage.userManagementPage,
			page,
			projectID: project.groupId,
		});

		for (const userName of ['michelle hoshi', 'corbin murakami']) {
			await page.getByPlaceholder('Search').first().fill(userName);

			await page.keyboard.press('Enter');

			await expect(
				page.getByRole('cell', {name: userName})
			).toBeVisible();
		}
	}
);

test(
	'Selecting multiple invited users and clicking bulk Delete removes them at once',
	{
		tag: '@LRAC-9064',
	},
	async ({page, project}) => {
		await navigateToACSettingsViaURL({
			acPage: ACPage.userManagementPage,
			page,
			projectID: project.groupId,
		});

		const emails = [
			`bulk1-${Date.now()}@liferay.com`,
			`bulk2-${Date.now()}@liferay.com`,
		];

		await page.getByRole('button', {name: 'Invite Users'}).click();

		for (const email of emails) {
			await page.getByPlaceholder('Enter Email Address').fill(email);

			await page.keyboard.press('Enter');
		}

		await page.getByRole('button', {name: 'Send'}).click();

		await expect(
			page.getByText('Success:Invitations have been sent.')
		).toBeVisible();

		for (const email of emails) {
			await page
				.getByRole('row', {name: new RegExp(email)})
				.getByRole('checkbox')
				.check();
		}

		await page.getByRole('button', {exact: true, name: 'Delete'}).click();

		await page.getByRole('button', {name: 'Continue'}).click();

		for (const email of emails) {
			await expect(page.getByRole('cell', {name: email})).toHaveCount(0);
		}
	}
);

test(
	'User Management list can be paginated',
	{
		tag: '@LRAC-9045',
	},
	async ({page, project}) => {
		const emails = [
			`paginated-1-${Date.now()}@liferay.com`,
			`paginated-2-${Date.now()}@liferay.com`,
		];

		await navigateToACSettingsViaURL({
			acPage: ACPage.userManagementPage,
			page,
			projectID: project.groupId,
		});

		try {

			// Invite two users so the list spans more than one page of four

			await page.getByRole('button', {name: 'Invite Users'}).click();

			for (const email of emails) {
				await page.getByPlaceholder('Enter Email Address').fill(email);

				await page.keyboard.press('Enter');
			}

			await page.getByRole('button', {name: 'Send'}).click();

			await expect(
				page.getByText('Success:Invitations have been sent.')
			).toBeVisible();

			// Four users per page leaves the rest on page two

			await selectPaginationItemsPerPage({itemsPerPage: '4', page});

			await expect(page.locator('.table-title')).toHaveCount(4);

			await viewPaginationResults({
				page,
				paginationResults: 'Showing 1 to 4 of',
			});

			await selectPaginationPageNumber({page, paginationPageNumber: '2'});

			await viewPaginationResults({
				page,
				paginationResults: 'Showing 5 to',
			});
		}
		finally {
			await navigateToACSettingsViaURL({
				acPage: ACPage.userManagementPage,
				page,
				projectID: project.groupId,
			});

			for (const email of emails) {
				await page.getByPlaceholder('Search').first().fill(email);

				await page.keyboard.press('Enter');

				await page
					.getByRole('row', {name: email})
					.getByLabel('Delete')
					.click();

				await page.getByRole('button', {name: 'Continue'}).click();

				await expect(page.getByRole('cell', {name: email})).toHaveCount(
					0
				);
			}
		}
	}
);

test(
	'Admin and Owner users can add data sources',
	{
		tag: ['@LRAC-9090', '@LRAC-9093'],
	},
	async ({page, project}) => {
		const privilegedUsers = [
			'michelle.hoshi@faro.io',
			'bryan.cheung@faro.io',
		];

		try {
			for (const email of privilegedUsers) {
				await signInToAnalyticsCloud(page, email);

				await navigateToACSettingsViaURL({
					acPage: ACPage.dataSourcePage,
					page,
					projectID: project.groupId,
				});

				await expect(
					page.getByRole('button', {name: 'Add Data Source'})
				).toBeVisible();
			}
		}
		finally {
			await signInToAnalyticsCloud(page, faroConfig.user.login);
		}
	}
);

test(
	'User Management list order is reversed when toggling the name column sort',
	{
		tag: '@LRAC-9044',
	},
	async ({page, project}) => {
		await navigateToACSettingsViaURL({
			acPage: ACPage.userManagementPage,
			page,
			projectID: project.groupId,
		});

		const readNames = async () =>
			(await page.locator('.table-title').allInnerTexts()).map((name) =>
				name.trim()
			);

		const initialNames = await readNames();

		// Toggle the active name-column sort direction

		await page.getByRole('link', {name: 'Name Ascending'}).click();

		await expect.poll(readNames).toEqual([...initialNames].reverse());
	}
);
