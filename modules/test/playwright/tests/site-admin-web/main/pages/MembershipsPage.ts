/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {ProductMenuPage} from '../../../../pages/product-navigation-control-menu-web/ProductMenuPage';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../../utils/waitForAlert';

export class MembershipsPage {
	readonly inheritanceSourceLabel: Locator;
	readonly page: Page;
	readonly productMenuPage: ProductMenuPage;

	constructor(page: Page) {
		this.inheritanceSourceLabel = page.getByText('(Inherited)');
		this.page = page;
		this.productMenuPage = new ProductMenuPage(page);
	}

	async assignAllRolesToUser(userName: String) {
		await this.openAssignRoles(userName);

		await this.page.waitForTimeout(500);

		await this.page
			.frameLocator('iframe[title="Assign Roles"]')
			.getByLabel('Select All Items on the Page')
			.check();

		await this.page.getByRole('button', {name: 'Done'}).click();

		await waitForAlert(this.page);
	}

	async assignAllUsersSiteMembership() {
		await this.page.getByRole('button', {name: 'Add'}).click();

		await this.page.waitForTimeout(500);

		await this.page
			.frameLocator('iframe[title="Assign Users to This Site"]')
			.getByLabel('Select All Items on the Page')
			.check();

		await this.page.getByRole('button', {name: 'Done'}).click();

		await waitForAlert(this.page);
	}

	async assignSiteAdministratorRole() {
		await this.page.getByLabel('Select All Items on the Page').check();

		await this.page.getByRole('button', {name: 'Assign Roles'}).click();

		await this.page
			.frameLocator('iframe[title="Assign Roles"]')
			.locator('.file-card', {hasText: 'Site Administrator'})
			.getByRole('checkbox')
			.check();

		await this.page.getByRole('button', {name: 'Done'}).click();

		await waitForAlert(this.page);
	}

	async filterBySiteAdministratorRole() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Roles'}),
			timeout: 500,
			trigger: this.page.getByLabel('Filter'),
		});

		await expect(async () => {
			await this.page
				.frameLocator('iframe[title="Select Role"]')
				.getByText('Site Administrator')
				.click();

			await expect(
				this.page.getByRole('heading', {name: 'Search Results'})
			).toBeVisible({timeout: 1000});
		}).toPass();

		await expect(
			this.page.getByLabel('Select All Items on the Page')
		).toBeVisible();
	}

	async goto() {
		await this.productMenuPage.openProductMenuIfClosed();
		await this.productMenuPage.goToMemberships();
	}

	async openAssignRoles(userName: String) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Assign Roles',
			}),
			timeout: 500,
			trigger: this.page
				.locator(
					'[id="_com_liferay_site_memberships_web_portlet_SiteMembershipsPortlet_users_' +
						userName +
						'"]'
				)
				.getByLabel('More actions'),
		});
	}

	async removeSiteMembershipFromUser(userName: String) {
		this.page.once('dialog', (dialog) => {
			dialog.accept();
		});

		await this.triggerRemoveMembership(userName);

		await waitForAlert(this.page);
	}

	async removeSiteAdministratorRole() {
		this.page.once('dialog', (dialog) => {
			dialog.accept();
		});

		await this.page.getByLabel('Select All Items on the Page').check();

		await this.page
			.getByRole('button', {name: 'Remove Role: Site Administrator'})
			.click();

		await waitForAlert(this.page);
	}

	async triggerRemoveMembership(userName: String) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Remove Membership',
			}),
			timeout: 500,
			trigger: this.page
				.locator(
					'[id="_com_liferay_site_memberships_web_portlet_SiteMembershipsPortlet_users_' +
						userName +
						'"]'
				)
				.getByLabel('More actions'),
		});
	}

	async unassignAllRolesFromUser(userName: String) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Unassign Roles',
			}),
			timeout: 500,
			trigger: this.page
				.locator(
					'[id="_com_liferay_site_memberships_web_portlet_SiteMembershipsPortlet_users_' +
						userName +
						'"]'
				)
				.getByLabel('More actions'),
		});

		await this.page.waitForTimeout(500);

		await this.page
			.frameLocator('iframe[title="Unassign Roles"]')
			.getByLabel('Select All Items on the Page')
			.check();

		await this.page.getByRole('button', {name: 'Done'}).click();

		await waitForAlert(this.page);
	}
}
