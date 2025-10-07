/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(dataApiHelpersTest, loginTest());

test(
	'Multiple selection with pagination in SearchContainer modal',
	{tag: '@LPD-50672'},
	async ({apiHelpers, page}) => {
		const maxNumberOfRoles = 15;
		let iframeSelectRole;
		const roleIds = [];

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		await test.step('Create extra roles', async () => {
			for (let i = 0; i < maxNumberOfRoles; i++) {
				const role = await apiHelpers.headlessAdminUser.postRole({
					name: 'role' + i,
					rolePermissions: [
						{
							actionIds: ['VIEW'],
							primaryKey: companyId,
							resourceName:
								'com.liferay.account.model.AccountEntry',
							scope: 1,
						},
					],
				});

				roleIds.push(role.id);
			}
		});

		await test.step('Navigate to Configuration > Site Settings > Menu Access', async () => {
			await page.goto(`/group/guest${PORTLET_URLS.siteSettings}`);

			await page
				.getByRole('link', {exact: true, name: 'Site Configuration'})
				.click();

			await page
				.getByRole('menuitem', {exact: true, name: 'Menu Access'})
				.click();

			await expect(
				page.getByRole('heading', {name: 'Menu Access'})
			).toBeVisible();
		});

		await test.step('Open Role Selection modal', async () => {
			await page.getByText('Show Control Menu by Role').setChecked(true);

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(
				page,
				'Success:Your request completed successfully.'
			);

			await page.getByRole('button', {name: 'Select'}).click();
			await page.getByRole('heading', {name: 'Select Role'}).waitFor();
		});

		await test.step('Select a role in page 1', async () => {
			iframeSelectRole = page.frameLocator('iframe[title="Select Role"]');

			const itemRow = iframeSelectRole.locator(
				'tr[data-name="Analytics Administrator"]'
			);

			await itemRow.waitFor();
			await itemRow.locator('input[type="checkbox"]').setChecked(true);
			await expect(
				itemRow.locator('input[type="checkbox"]')
			).toBeChecked();
		});

		await test.step('Navigate to page 2', async () => {
			await iframeSelectRole
				.getByRole('link', {exact: true, name: 'Page 2'})
				.click();
		});

		await test.step('Check that selected role info is still in the DOM', async () => {
			const selectedRole = iframeSelectRole.locator(
				'input[data-name="Analytics Administrator"]'
			);
			const selectedRoleValue = await selectedRole.getAttribute('value');

			await expect(selectedRole).not.toBeVisible();
			await expect(selectedRole).toHaveAttribute(
				'data-name',
				'Analytics Administrator'
			);

			expect(Number(selectedRoleValue)).toBeGreaterThanOrEqual(0);
		});
	}
);
