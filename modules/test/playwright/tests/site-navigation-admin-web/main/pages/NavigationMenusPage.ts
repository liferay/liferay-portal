/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {PageEditorPage} from '../../../../pages/layout-content-page-editor-web/PageEditorPage';
import {DisplayPageTemplatesPage} from '../../../../pages/layout-page-template-admin-web/DisplayPageTemplatesPage';
import {ApplicationsMenuPage} from '../../../../pages/product-navigation-applications-menu/ApplicationsMenuPage';
import {ProductMenuPage} from '../../../../pages/product-navigation-control-menu-web/ProductMenuPage';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

export class NavigationMenusPage {
	readonly page: Page;

	readonly getMenuItem: (menuItemName: string) => Promise<Locator>;
	readonly getModalListItem: (itemName: string) => Promise<Locator>;
	readonly getModalMenuItem: (menuItemName: string) => Promise<Locator>;
	readonly getNavigationMenuActionMenu: (
		menuName: string
	) => Promise<Locator>;
	readonly getNavigationMenuCell: (
		cellName: string,
		locator: Locator
	) => Promise<Locator>;
	readonly getNavigationMenuRow: (menuId: Number) => Promise<Locator>;
	readonly getNestingLevel: (name: string) => Promise<string>;

	readonly addButton: Locator;
	readonly addMenuItemButton: Locator;
	readonly blogsModal: FrameLocator;
	readonly categoriesModal: FrameLocator;
	readonly displayTemplate: Locator;
	readonly documentsModal: FrameLocator;
	readonly journalArticleModal: FrameLocator;
	readonly pagesModal: FrameLocator;
	readonly previewButton: Locator;
	readonly saveButton: Locator;
	readonly selectButton: Locator;
	readonly submenuModal: FrameLocator;
	readonly urlModal: FrameLocator;
	readonly vocabulariesModal: FrameLocator;

	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly productMenuPage: ProductMenuPage;

	constructor(page: Page) {
		this.page = page;

		this.getMenuItem = async (menuItemName: string) => {
			return page.getByRole('menuitem', {
				exact: true,
				name: menuItemName,
			});
		};
		this.getModalListItem = async (itemName: string) => {
			return page
				.frameLocator('iframe')
				.getByRole('listitem')
				.getByText(itemName);
		};
		this.getModalMenuItem = async (menuItemName: string) => {
			return page
				.frameLocator('iframe')
				.getByRole('menuitem')
				.getByText(menuItemName);
		};

		this.getNavigationMenuActionMenu = async (menuName: string) => {
			return page
				.getByRole('row', {name: 'Select ' + menuName})
				.getByLabel('Show Actions');
		};

		this.getNavigationMenuCell = async (
			cellName: string,
			locator: Locator
		) => {
			return locator.getByRole('cell', {
				exact: true,
				name: cellName,
			});
		};

		this.getNavigationMenuRow = async (menuId: Number) => {
			return page.locator(
				'[id="_com_liferay_site_navigation_admin_web_portlet_SiteNavigationAdminPortlet_siteNavigationMenus_' +
					menuId +
					'"]'
			);
		};

		this.getNestingLevel = async (name: string) => {
			return this.page
				.getByText(name)
				.evaluate((element) =>
					getComputedStyle(element).getPropertyValue(
						'--nesting-level'
					)
				);
		};

		this.addButton = page.getByRole('button', {name: 'Add'});
		this.addMenuItemButton = page.getByLabel('Add Menu Item');
		this.blogsModal = page.frameLocator(
			'iframe[title="Select Blogs Entry"]'
		);
		this.categoriesModal = page.frameLocator(
			'iframe[title="Select Categories"]'
		);
		this.displayTemplate = page.getByLabel('Display Template');
		this.documentsModal = page.frameLocator(
			'iframe[title="Select Document"]'
		);
		this.journalArticleModal = page.frameLocator(
			'iframe[title="Select Web Content Article"]'
		);
		this.pagesModal = page.frameLocator('iframe[title="Select Pages"]');
		this.previewButton = page.getByRole('button', {name: 'Preview'});
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.selectButton = page.getByRole('button', {name: 'Select'});
		this.submenuModal = page.frameLocator('iframe[title="Add Submenu"]');
		this.urlModal = page.frameLocator('iframe[title="Add URL"]');
		this.vocabulariesModal = page.frameLocator(
			'iframe[title="Select Vocabularies"]'
		);

		this.applicationsMenuPage = new ApplicationsMenuPage(this.page);
		this.productMenuPage = new ProductMenuPage(this.page);
	}

	async addBlogItem(name: string) {
		await this.addMenuItemButton.click();

		await (await this.getMenuItem('Blogs Entry')).click();

		await this.page.waitForSelector('iframe', {state: 'attached'});

		await this.blogsModal
			.getByRole('button', {
				name,
			})
			.click();

		await waitForAlert(
			this.page,
			'Success:1 Blogs Entry was added to this menu.'
		);
	}

	async addChildPage(parentPage: string, childPage: string) {
		await this.page.getByText(parentPage).hover();

		await this.page
			.getByRole('button', {name: 'View ' + parentPage + ' Options'})
			.click();

		await this.page.getByRole('menuitem', {name: 'Add Child'}).hover();

		await this.page.waitForTimeout(500);

		await this.page.getByRole('menuitem', {name: 'Page'}).click();

		await this.pagesModal.getByText(childPage, {exact: true}).click();

		await this.selectButton.click();

		await waitForAlert(this.page, 'Success:1 Page was added to this menu.');
	}

	async addDocumentImageItem(imageName: string) {
		await this.addMenuItemButton.click();

		await (await this.getMenuItem('Document')).click();

		await this.page.waitForSelector('iframe', {state: 'attached'});

		await this.documentsModal
			.getByRole('link', {name: 'Sites and Libraries'})
			.click();

		await this.documentsModal
			.getByRole('link', {name: 'Liferay DXP'})
			.click();

		await this.documentsModal
			.getByRole('link', {name: 'Provided by Liferay'})
			.click();

		await this.documentsModal.getByText(imageName).click();
	}

	async addNavigationMenuToGlobalSite(navigationMenuName: string) {
		await this.gotoGlobalSiteNavigationMenuPortlet();

		await this.createNavigationMenu(navigationMenuName);
	}

	async addOrChangeIcon(iconName: string) {
		await this.page.getByLabel('Select an Icon').click();

		await this.page.getByLabel('Select ' + iconName + ' icon').click();

		await this.saveButton.click();
	}

	async addPageItem(pageNames: string[]) {
		await this.openAddPageModal();

		for (const pageName of pageNames) {
			await this.pagesModal.getByText(pageName, {exact: true}).click();
		}

		await this.selectButton.click();

		await waitForAlert(this.page, 'Success');
	}

	async addSubmenuItem(submenuName: string, submenuItemName?: string) {
		if (submenuItemName) {
			await this.page
				.locator('p.card-title')
				.filter({hasText: submenuItemName})
				.hover();

			await this.page
				.getByLabel('View ' + submenuItemName + ' Options')
				.click();

			await (await this.getMenuItem('Add Child')).hover();

			await this.page.getByText('Submenu', {exact: true}).nth(4).click();
		}
		else {
			await this.addMenuItemButton.click();

			await (await this.getMenuItem('Submenu')).click();
		}

		// Wait until the modal is fully loaded

		await this.page.waitForTimeout(1000);

		const textBox = this.submenuModal.getByPlaceholder('Name');

		await textBox.click();

		await textBox.fill(submenuName);

		await this.page.waitForTimeout(300);

		await this.submenuModal.getByRole('button', {name: 'Add'}).click();

		await waitForAlert(
			this.page,
			'Success:1 Submenu was added to this menu.'
		);
	}

	async addURLItem(
		urlName: string,
		submenuItemName?: string,
		openNewTab?: boolean
	) {
		if (submenuItemName) {
			await this.page
				.locator('p.card-title')
				.filter({hasText: submenuItemName})
				.hover();

			await this.page
				.getByLabel('View ' + submenuItemName + ' Options')
				.click();

			await (await this.getMenuItem('Add Child')).hover();

			await this.page.getByText('URL').nth(3).click();
		}
		else {
			await this.addMenuItemButton.click();

			await (await this.getMenuItem('URL')).click();
		}

		// Wait until the modal is fully loaded

		await this.page.waitForTimeout(1000);

		const urlTextBox = this.urlModal.getByPlaceholder('http://');

		await urlTextBox.fill('https://www.liferay.com');

		const urlNameTextbox = this.urlModal.getByPlaceholder('Name');

		await urlNameTextbox.fill(urlName);

		if (openNewTab) {
			await this.urlModal.getByLabel('Open in a new tab').check();
		}

		const addButton = this.urlModal.getByRole('button', {name: 'Add'});

		await this.page.waitForTimeout(1000);

		await addButton.click();

		await waitForAlert(this.page, 'Success:1 URL was added to this menu.');
	}

	async addWebContentArticleItem(name: string) {
		await this.addMenuItemButton.click();

		await (await this.getMenuItem('Web Content Article')).click();

		await this.page.waitForSelector('iframe', {state: 'attached'});

		await this.journalArticleModal.getByText(name).click();

		await waitForAlert(
			this.page,
			'Success:1 Web Content Article was added to this menu.'
		);
	}

	async addWidgetToPageTemplate(templateName: string) {
		const displayPageTemplatesPage = new DisplayPageTemplatesPage(
			this.page
		);

		const pageEditorPage = new PageEditorPage(this.page);

		await displayPageTemplatesPage.editTemplate(templateName);

		await pageEditorPage.addFragment(
			'Content Display',
			'Display Page Content'
		);

		await pageEditorPage.waitForChangesSaved();

		await pageEditorPage.publishButton.click();

		await waitForAlert(
			this.page,
			'Success:The display page template was published successfully.'
		);

		await displayPageTemplatesPage.markAsDefault(templateName);
	}

	async changeBlogItem(current: string, next: string) {
		await this.page.getByText(current, {exact: true}).click();

		await this.page.getByLabel('Item', {exact: true}).click();

		await this.page.waitForSelector('iframe', {state: 'attached'});

		await this.blogsModal.getByRole('button', {name: next}).click();

		await this.page.waitForSelector('iframe', {state: 'detached'});

		await this.saveButton.click();

		await waitForAlert(
			this.page,
			'Success:Your request completed successfully.'
		);
	}

	async changeDocumentImageItem(current: string, next: string) {
		await this.page.getByText(current, {exact: true}).click();

		await this.page.getByLabel('Item', {exact: true}).click();

		await this.documentsModal
			.getByRole('link', {name: 'Sites and Libraries'})
			.click();

		await this.documentsModal
			.getByRole('link', {name: 'Liferay DXP'})
			.click();

		await this.documentsModal
			.getByRole('link', {name: 'Provided by Liferay'})
			.click();

		await this.documentsModal.getByText(next).click();

		await this.saveButton.click();

		await waitForAlert(
			this.page,
			'Success:Your request completed successfully.'
		);
	}

	async createNavigationMenu(menuName: string) {
		await this.addButton.click();

		const input = this.page.getByPlaceholder('Name');

		await input.waitFor();

		await input.fill(menuName);

		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async fillNavigationMenuItemCustomField(
		menuItemName: string,
		customFieldName: string
	): Promise<string> {
		await this.page.getByText(menuItemName).click();

		const encodedcustomFieldName = customFieldName.replace(/-/g, '_2d_');

		const randomValue = getRandomString();

		await this.page
			.locator(
				`[id^="_com_liferay_site_navigation_admin_web_portlet_SiteNavigationAdminPortlet_"][id$="${encodedcustomFieldName}"]`
			)
			.fill(randomValue);

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page);

		return randomValue;
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.navigationMenus}`
		);
	}

	async gotoGlobalSiteNavigationMenuPortlet() {
		await this.applicationsMenuPage.goToApplicationsMenu();

		await this.applicationsMenuPage.goToGlobalSite();

		await this.productMenuPage.openProductMenuIfClosed();

		await this.productMenuPage.siteBuilderButton.click();

		await this.productMenuPage.page
			.getByRole('menuitem', {name: 'Navigation Menus'})
			.click();
	}

	async openAddCategoryModal() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: await this.getMenuItem('Category'),
			trigger: this.addMenuItemButton,
		});

		await this.categoriesModal.getByPlaceholder('Search').waitFor();
	}

	async openAddPageModal() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: await this.getMenuItem('Page'),
			trigger: this.addMenuItemButton,
		});

		await this.pagesModal.getByPlaceholder('Search').waitFor();
	}

	async openAddVocabularyModal() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: await this.getMenuItem('Vocabulary'),
			trigger: this.addMenuItemButton,
		});

		await this.vocabulariesModal.getByPlaceholder('Search').waitFor();
	}

	async translateName(itemName: string, useCustomName = false) {
		await this.page.getByText(itemName).click();

		if (useCustomName) {
			await this.page.getByText('Use Custom Name').click();
		}

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.locator("a[data-languageId='es_ES']"),
			trigger: this.page.getByText('en-US', {exact: true}),
		});

		await this.page
			.locator(
				'input[id="_com_liferay_site_navigation_admin_web_portlet_SiteNavigationAdminPortlet_name"]'
			)
			.fill(`${itemName} Spanish`);

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page);
	}
}
