/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {openProductMenu} from '../../utils/productMenu';
import {waitForPageToBeLoaded} from '../../utils/waitForPageToBeLoaded';
import {HomePage} from '../portal-web/HomePage';

type Categories = 'Applications' | 'CMS' | 'Commerce' | 'Control Panel';

type ApplicationsMenuItem =
	| 'Announcements and Alerts'
	| 'Asset Libraries'
	| 'Blueprints'
	| 'Client Extensions'
	| 'Content Dashboard'
	| 'Data Migration Center'
	| 'Export'
	| 'Import'
	| 'Process Builder'
	| 'Publications'
	| 'Result Rankings'
	| 'Submissions'
	| 'Synonyms'
	| 'Workflow Metrics';

type ControlPanelMenuItem =
	| 'App Manager'
	| 'Accounts'
	| 'Account Groups'
	| 'Account Users'
	| 'Adaptive Media'
	| 'API Builder'
	| 'Audit'
	| 'Components'
	| 'Countries Management'
	| 'Custom Fields'
	| 'Data Sets'
	| 'Gogo Shell'
	| 'Instance Settings'
	| 'Job Scheduler'
	| 'Language Override'
	| 'Monitoring'
	| 'License Manager'
	| 'OAuth 2 Administration'
	| 'OAuth Client Administration'
	| 'Objects'
	| 'On-Demand Admin'
	| 'Password Policies'
	| 'Picklists'
	| 'Push Notifications'
	| 'Purchased'
	| 'Queue'
	| 'Roles'
	| 'SAML Admin'
	| 'Search'
	| 'Server Administration'
	| 'Service Access Policy'
	| 'Service Accounts'
	| 'Site Templates'
	| 'Sites'
	| 'Store'
	| 'System Settings'
	| 'Templates'
	| 'Users and Organizations'
	| 'User Groups'
	| 'Virtual Instances';

type CommerceMenuItem =
	| 'Avalara Connector'
	| 'Availability Estimates'
	| 'Channels'
	| 'Catalogs'
	| 'Currencies'
	| 'Digital Sales Room Management'
	| 'Discounts'
	| 'Health Check'
	| 'Inventory'
	| 'Measurement Units'
	| 'On-Demand Admin'
	| 'Options'
	| 'Orders'
	| 'Order Types'
	| 'Order Rules'
	| 'Payments'
	| 'Price Lists'
	| 'Products'
	| 'Product Configurations'
	| 'Product Groups'
	| 'Promotions'
	| 'Returns'
	| 'Shipments'
	| 'Specifications'
	| 'Subscriptions'
	| 'Terms and Conditions'
	| 'Tax Categories'
	| 'Warehouses';

export class GlobalMenuPage {
	readonly categoriesList: Locator;
	readonly globalMenuButton: Locator;
	private readonly homePage: HomePage;
	readonly page: Page;
	readonly sitesList: Locator;
	readonly viewAllLink: Locator;

	constructor(page: Page) {
		this.categoriesList = page
			.getByRole('menu')
			.and(page.locator('.categories-list'));
		this.globalMenuButton = page
			.getByRole('button', {name: 'Open Applications Menu'})
			.or(page.getByTestId('globalMenu'));
		this.homePage = new HomePage(page);
		this.page = page;
		this.sitesList = page
			.getByRole('menu')
			.and(page.locator('.global-menu .sites-list'));
		this.viewAllLink = page.getByRole('menuitem', {
			exact: true,
			name: 'View All Sites',
		});
	}

	async goTo(categoryName: Categories) {
		const menuItem = this.page.getByRole('menuitem', {
			name: categoryName,
		});

		await expect(async () => {
			if (
				(await this.globalMenuButton.getAttribute('aria-expanded', {
					timeout: 5000,
				})) !== 'true'
			) {
				await this.globalMenuButton.click();

				await expect(this.globalMenuButton).toHaveAttribute(
					'aria-expanded',
					'true',
					{timeout: 5000}
				);
			}

			await expect(menuItem).toBeVisible({timeout: 5000});

			if (
				(await menuItem.getAttribute('aria-current', {
					timeout: 5000,
				})) !== 'page'
			) {
				await menuItem.click({timeout: 5000});
			}
		}).toPass();

		const sidebar = this.page.getByTestId('sideNavigation');

		let forceToReload = false;

		await expect(async () => {
			if (forceToReload) {
				await this.page.reload();
			}

			forceToReload = true;

			await expect(sidebar).toBeAttached();
			await expect(sidebar.locator('.sidebar-header')).toBeAttached();
		}).toPass();

		await openProductMenu(this.page);
	}

	async goToHome() {
		await this.homePage.goto();
	}

	async goToApplications(menuItem?: ApplicationsMenuItem, link?: string) {
		await this.goTo('Applications');

		if (menuItem) {
			await this.goToMenuItem(menuItem);

			await waitForPageToBeLoaded(this.page);

			if (link) {
				await this.goToLink(link);
			}
		}
	}

	async goToCMS() {
		await this.goTo('CMS');
	}

	async goToCommerce(menuItem?: CommerceMenuItem, link?: string) {
		await this.goTo('Commerce');

		if (menuItem) {
			await this.goToMenuItem(menuItem);

			await waitForPageToBeLoaded(this.page);

			if (link) {
				await this.goToLink(link);
			}
		}
	}

	async goToControlPanel(menuItem?: ControlPanelMenuItem, link?: string) {
		await this.goTo('Control Panel');

		if (menuItem) {
			await this.goToMenuItem(menuItem);

			await waitForPageToBeLoaded(this.page);

			if (link) {
				await this.goToLink(link);
			}
		}
	}

	async goToObjectDefinition(menuItem: string) {
		await this.goToHome();

		await this.goTo('Control Panel');

		await this.goToMenuItem(menuItem);
	}

	async goToMenuItem(
		menuItem:
			| ApplicationsMenuItem
			| ControlPanelMenuItem
			| CommerceMenuItem
			| string
	) {
		const input = this.page
			.locator('.side-navigation-container')
			.getByPlaceholder('Search', {exact: true});

		await input.waitFor();

		await input.fill(menuItem);

		await input.press('Tab');

		await this.page
			.getByRole('menuitem', {
				exact: true,
				name: menuItem as string,
			})
			.and(this.page.locator('.nav-link[href]'))
			.click();
	}

	async goToLink(link: string) {
		await this.page
			.getByRole('link', {
				exact: true,
				name: link,
			})
			.click();
	}

	async goToSite(name: string) {
		await this.goToControlPanel('Sites');

		await this.page.getByRole('heading', {name: 'Sites'}).waitFor();

		await this.page
			.locator('tr[data-qa-id="row"]:not(.d-none)', {
				has: this.page.locator('td', {hasText: name}),
			})
			.getByRole('button', {name: 'Actions'})
			.click();

		const actions = this.page.locator('.show a[role="menuitem"]');

		const action = actions
			.filter({hasText: 'Go to Pages'})
			.or(actions.filter({hasText: 'Go to Site Settings'}));

		const href = await action.first().getAttribute('href');

		await this.goToHome();
		await this.page.waitForLoadState('domcontentloaded');

		await this.page.goto(href, {
			timeout: 15000,
			waitUntil: 'domcontentloaded',
		});
	}

	async openGlobalMenu() {
		await clickAndExpectToBeVisible({
			target: this.categoriesList,
			trigger: this.globalMenuButton,
		});
	}
}
