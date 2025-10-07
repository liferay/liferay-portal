/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import fillAndClickOutside from '../../utils/fillAndClickOutside';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';
import {PageEditorPage} from '../layout-content-page-editor-web/PageEditorPage';

export class PagesAdminPage {
	readonly page: Page;

	readonly addButton: Locator;
	readonly newButton: Locator;

	private readonly configurationSaveButton: Locator;
	private readonly javaScriptClientExtensionsTab: Locator;
	private readonly pageEditorPage: PageEditorPage;
	private readonly pageTitleBox: Locator;
	private readonly searchButton: Locator;
	private readonly searchInput: Locator;

	constructor(page: Page) {
		this.page = page;

		const addPageIFrame = page.frameLocator(
			'iframe[id="addLayoutDialog_iframe_"]'
		);
		this.addButton = addPageIFrame.getByRole('button', {name: 'Add'});
		this.configurationSaveButton = page.getByRole('button', {
			exact: true,
			name: 'Save',
		});
		this.javaScriptClientExtensionsTab = page.getByRole('tab', {
			name: 'JavaScript',
		});
		this.newButton = page
			.locator('.management-bar')
			.getByText('New', {exact: true});
		this.pageEditorPage = new PageEditorPage(this.page);
		this.pageTitleBox = addPageIFrame.locator(
			'input[id="_com_liferay_layout_admin_web_portlet_GroupPagesPortlet_name"]'
		);
		this.searchButton = this.page.getByLabel('Search for', {exact: true});
		this.searchInput = this.page.getByPlaceholder('Search for');
	}

	async goto(siteUrl?: Site['friendlyUrlPath'], doAsUserId?: string) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.pages}${doAsUserId ? '&doAsUserId=' + doAsUserId : ''}`
		);
	}

	private async addCSSClientExtension(clientExtensionName: string) {
		await this.page
			.getByRole('link', {
				name: 'Design',
			})
			.click();

		await this.page
			.getByRole('button', {name: 'Add CSS Client Extensions'})
			.click();

		const iframe = this.page.frameLocator('#selectGlobalCSSCETs_iframe_');

		// Wait for "Select Items" checkbox label to be visible which occurs when JavaScript hydration is complete.

		await iframe.getByLabel(clientExtensionName).check({trial: true});

		await iframe.getByLabel(clientExtensionName).check();

		const addButton = this.page.getByRole('button', {
			exact: true,
			name: 'Add',
		});

		const clientExtensionEntry = this.page.getByRole('cell', {
			name: clientExtensionName,
		});

		await clickAndExpectToBeVisible({
			target: clientExtensionEntry,
			trigger: addButton,
		});

		await this.configurationSaveButton.click();
	}

	private async addJavaScriptClientExtension(clientExtensionName: string) {
		await this.page
			.getByRole('link', {
				name: 'Design',
			})
			.click();

		await this.clickOnJavaScriptClientExtensionsTab();

		await this.page
			.getByRole('button', {name: 'Add JavaScript Client Extensions'})
			.click();

		await this.page.getByRole('menuitem', {name: 'In Page Head'}).click();

		const iframe = this.page.frameLocator('#selectGlobalJSCETs_iframe_');

		// Wait for "Select Items" checkbox label to be visible which occurs when JavaScript hydration is complete.

		await iframe.getByText('Select Items').waitFor({state: 'visible'});

		await iframe.getByLabel(clientExtensionName).check();

		const addButton = this.page.getByRole('button', {
			exact: true,
			name: 'Add',
		});

		const clientExtensionEntry = this.page.getByRole('cell', {
			name: clientExtensionName,
		});

		await clickAndExpectToBeVisible({
			target: clientExtensionEntry,
			trigger: addButton,
		});

		await this.configurationSaveButton.click();
	}

	async addPage({
		name,
		template = 'Blank',
	}: {
		name: string;
		template?: string;
	}) {
		await this.page
			.locator('.card-page-item')
			.filter({hasText: template})
			.click();

		const loadingAnimation = this.page.locator(
			'.modal-body-iframe .loading-animation'
		);
		await loadingAnimation.waitFor();
		await loadingAnimation.waitFor({state: 'hidden'});

		await fillAndClickOutside(this.page, this.pageTitleBox, name);

		await this.addButton.waitFor({state: 'attached'});
		await this.addButton.hover();
		await this.addButton.click();

		await waitForAlert(this.page, 'page was created successfully.');
	}

	private async addThemeFaviconClientExtension(clientExtensionName: string) {
		await this.page
			.getByRole('link', {
				name: 'Design',
			})
			.click();

		await this.page.getByLabel('Select Favicon', {exact: true}).click();

		const iframe = this.page.frameLocator('iframe[title="Select Favicon"]');

		await iframe
			.getByRole('link', {exact: true, name: 'Client Extension'})
			.click();

		await iframe.getByText(clientExtensionName).click();

		await expect(
			this.page.getByAltText(clientExtensionName, {exact: true})
		).toBeVisible();

		await this.configurationSaveButton.click();
	}

	async addWidgetPage({name}: {name: string}) {
		await this.createNewPage({
			draft: true,
			name,
			template: 'Widget Page',
		});
	}

	async changeFavicon(
		layoutTitle: string,
		filePath: string,
		siteUrl: string
	) {
		await this.goto(siteUrl);

		await this.clickOnAction('Configure', layoutTitle);

		const fileChooserPromise = this.page.waitForEvent('filechooser');

		await this.page
			.locator('.portlet-body li', {
				has: this.page.getByText('Design'),
			})
			.click();

		await this.page
			.getByLabel('Select Favicon', {exact: true})
			.waitFor({state: 'visible'});

		await this.page.getByLabel('Select Favicon', {exact: true}).click();

		const iframe = this.page.frameLocator('iframe[title="Select Favicon"]');

		await expect(
			iframe.getByText('Drag & Drop Your Files or Browse to Upload')
		).toBeVisible();

		await iframe
			.getByText('Drag & Drop Your Files or Browse to Upload')
			.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(filePath);

		await iframe.getByRole('button', {exact: true, name: 'Add'}).click();

		await this.saveConfiguration();
	}

	async changeTheme(themeName: string) {
		await this.page
			.getByRole('radio', {name: 'Define a custom theme for'})
			.click();

		await this.page
			.getByRole('button', {name: 'Change Current Theme'})
			.click();

		const themeCard = this.page
			.frameLocator(
				'iframe[id="_com_liferay_layout_admin_web_portlet_GroupPagesPortlet_selectTheme_iframe_"]'
			)
			.getByText(themeName);

		await themeCard.waitFor();

		await themeCard.click();

		await this.configurationSaveButton.waitFor({state: 'visible'});

		await this.saveConfiguration();
	}

	async clickOnJavaScriptClientExtensionsTab() {
		await this.javaScriptClientExtensionsTab.waitFor();

		await this.javaScriptClientExtensionsTab.click();
	}

	async clickOnAction(action: string, title: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: action,
			}),
			trigger: this.page
				.locator('li', {has: this.page.getByText(title)})
				.getByRole('button', {name: 'Open Page Options Menu'}),
		});
	}

	async clearThemeFaviconClientExtension({
		layoutTitle,
		siteUrl,
	}: {
		layoutTitle?: string;
		siteUrl?: Site['friendlyUrlPath'];
	}) {
		if (!layoutTitle) {
			await this.gotoPagesConfiguration(siteUrl);
		}
		else {
			await this.goto(siteUrl);

			await this.clickOnAction('Configure', layoutTitle);
		}

		await this.page
			.locator('.portlet-body li', {
				has: this.page.getByText('Design'),
			})
			.click();

		await this.page.getByLabel('Clear Favicon', {exact: true}).click();

		await expect(
			this.page.getByAltText('Favicon from Theme', {exact: true})
		).toBeVisible();

		await this.configurationSaveButton.click();

		if (!layoutTitle) {
			await waitForAlert(this.page);
		}
		else {
			await waitForAlert(
				this.page,
				'Success:The page was updated successfully.'
			);
		}
	}

	async createNewPage({
		draft = false,
		name,
		parent,
		template,
	}: {
		draft?: boolean;
		name: string;
		parent?: string;
		template?: string;
	}) {
		let trigger: Locator;

		// If no parent specified, just create from toolbar

		if (!parent) {
			trigger = this.newButton;
		}

		// If parent is specified, create child page

		else {
			trigger = this.page
				.locator('li', {has: this.page.getByText(parent)})
				.getByTitle('Add Child Page');
		}

		await clickAndExpectToBeVisible({
			target: this.page.locator('.sheet-title', {
				hasText: 'Basic Templates',
			}),
			trigger,
		});

		// Select template and fill name

		await this.addPage({
			name,
			template,
		});

		// Publish is draft param is false

		if (!draft) {
			await this.pageEditorPage.publishPage();
		}
	}

	async clickOnTab(name: string) {
		await this.page.getByRole('link', {name}).click();
	}

	async deletePage(name: string) {
		await this.clickOnAction('Delete', name);

		await this.page
			.locator('.modal-title')
			.getByText('Delete Page')
			.waitFor();

		await this.page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(
			this.page,
			'Success:Your request completed successfully.'
		);
	}

	async editPage(name: string) {
		await this.clickOnAction('Edit', name);

		await this.page
			.getByText('Select a Page Element', {exact: true})
			.waitFor();
	}

	async goToDesignTabConfiguration(pageName: string) {
		await this.clickOnAction('Configure', pageName);

		await this.clickOnTab('Design');
	}

	async gotoPagesConfiguration(siteUrl?: Site['friendlyUrlPath']) {
		await this.goto(siteUrl);

		await clickAndExpectToBeVisible({
			target: this.page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Configuration'}),
			trigger: this.page
				.locator('.control-menu-nav-item')
				.getByLabel('Options', {exact: true}),
		});

		await this.page.getByRole('menuitem', {name: 'Configuration'}).click();
	}

	async gotoSelectTemplates(templateSetName: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page
				.getByRole('menuitem')
				.getByText(templateSetName, {exact: true}),
			trigger: this.newButton,
		});
	}

	async saveConfiguration() {
		await this.configurationSaveButton.click();

		await waitForAlert(
			this.page,
			'Success:The page was updated successfully.'
		);
	}

	async searchPage(keywords: string) {
		await this.searchInput.click();
		await this.searchInput.clear();
		await this.searchInput.fill(keywords);

		await this.searchButton.click();

		await this.page.getByText('Search Results').waitFor();
	}

	async selectClientExtension({
		clientExtensionName,
		layoutTitle,
		openConfiguration = true,
		siteUrl,
		type,
	}: {
		clientExtensionName: string;
		layoutTitle?: string;
		openConfiguration?: boolean;
		siteUrl?: Site['friendlyUrlPath'];
		type?: 'globalCSS' | 'globalJS' | 'themeFavicon';
	}) {
		if (openConfiguration) {
			if (!layoutTitle) {
				await this.gotoPagesConfiguration(siteUrl);
			}
			else {
				await this.goto(siteUrl);

				await this.clickOnAction('Configure', layoutTitle);
			}
		}

		if (type && type === 'globalCSS') {
			await this.addCSSClientExtension(clientExtensionName);
		}
		else if (type && type === 'globalJS') {
			await this.addJavaScriptClientExtension(clientExtensionName);
		}
		else {
			await this.addThemeFaviconClientExtension(clientExtensionName);
		}

		if (!layoutTitle) {
			await waitForAlert(this.page);
		}
		else {
			await waitForAlert(
				this.page,
				'Success:The page was updated successfully.'
			);
		}
	}

	async selectThemeCSSClientExtension(clientExtensionName: string) {
		await this.page
			.locator(
				'#_com_liferay_layout_admin_web_portlet_GroupPagesPortlet_themeCSSReplacementExtension'
			)
			.click();

		const iframe = this.page.locator(
			'#selectThemeCSSClientExtension_iframe_'
		);

		await iframe.waitFor({
			state: 'visible',
		});

		const clientExtension = this.page
			.frameLocator('#selectThemeCSSClientExtension_iframe_')
			.getByTestId('rowItemContent')
			.filter({hasText: clientExtensionName});

		await clickAndExpectToBeHidden({
			target: iframe,
			trigger: clientExtension,
		});

		await this.configurationSaveButton.click();
	}

	async selectPages(pageNames: string[]) {
		for (const [index, pageName] of pageNames.entries()) {
			const checkbox = this.page.getByLabel(`Select ${pageName}`, {
				exact: true,
			});

			if (!(await checkbox.isChecked())) {
				await clickAndExpectToBeVisible({
					target: this.page
						.locator('.management-bar .nav-item')
						.getByText(`${index + 1} of`),
					trigger: this.page.getByLabel(`Select ${pageName}`, {
						exact: true,
					}),
				});
			}
		}
	}

	async changePagesPermissions(pageNames: string[], permissionIds: string[]) {

		// Select the pages

		await this.selectPages(pageNames);

		// Open the permissions modal

		await this.page.getByRole('button', {name: 'Permissions'}).click();

		const permissionsFrame = this.page.frameLocator(
			'iframe[title="Permissions"]'
		);

		await permissionsFrame
			.getByRole('cell', {exact: true, name: 'Role'})
			.waitFor();

		// Check the permissions

		for (const permissionId of permissionIds) {
			const permission = permissionsFrame.locator(`#${permissionId}`);

			await permission.uncheck({trial: true});
			await permission.uncheck({timeout: 1000});
		}

		// Save and close the modal

		await permissionsFrame.getByRole('button', {name: 'Save'}).click();

		const successMessage =
			pageNames.length > 1
				? `Success:${pageNames.length} permissions were updated successfully.`
				: undefined;

		await waitForAlert(permissionsFrame, successMessage);

		await this.page.getByLabel('Close', {exact: true}).click();

		await this.page.getByLabel('Clear selection').click();

		await this.page.getByLabel('Select All Items').waitFor();
	}
}
