/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

export class PersonalMenuPage {
	readonly addContentButton: (widgetName: string) => Locator;
	readonly addPageButton: Locator;
	readonly addWidgetButton: Locator;
	readonly backLink: Locator;
	readonly changeImageButton: Locator;
	readonly cityInput: Locator;
	readonly contactAddButton: Locator;
	readonly contactLink: Locator;
	readonly controlMenuAvatar: Locator;
	readonly countrySelect: Locator;
	readonly doneButton: Locator;
	readonly emailAddressInput: Locator;
	readonly firstNameInput: Locator;
	readonly lastNameInput: Locator;
	readonly menuItem: (name: string) => Locator;
	readonly myDashboardMenuItem: Locator;
	readonly myDashboardText: Locator;
	readonly myOrganizationsHeading: Locator;
	readonly myOrganizationsMenuItem: Locator;
	readonly myOrganizationsPortletBody: Locator;
	readonly myProfileDisplayName: Locator;
	readonly myProfileMenuItem: Locator;
	readonly mySitesMenuItem: Locator;
	readonly myWorkflowTasksHeading: Locator;
	readonly myWorkflowTasksMenuItem: Locator;
	readonly newPageButton: Locator;
	readonly notFoundCode: Locator;
	readonly notFoundMessage: Locator;
	readonly notificationsHeading: Locator;
	readonly notificationsMenuItem: Locator;
	readonly page: Page;
	readonly pageNameInput: Locator;
	readonly personalBarAvatar: Locator;
	readonly portletContentLink: (text: string) => Locator;
	readonly postalCodeInput: Locator;
	readonly regionSelect: Locator;
	readonly screenNameInput: Locator;
	readonly selectSiteHeading: Locator;
	readonly siteIframeCard: Locator;
	readonly street1Input: Locator;
	readonly uploadImageDoneButton: Locator;
	readonly uploadImageFrame: FrameLocator;
	readonly uploadImageSelectImageButton: Locator;
	readonly userDefaultIcon: Locator;
	readonly userDisplayDataText: Locator;
	readonly userPersonalMenuButton: Locator;
	readonly userPortrait: Locator;
	readonly usersAndOrganizationsHeading: Locator;
	readonly welcomeText: Locator;
	readonly widgetPageMenuItem: Locator;
	readonly widgetSearchInput: Locator;

	constructor(page: Page) {
		this.addContentButton = (widgetName: string) => {
			return page
				.getByTestId('addPanelTabItem')
				.filter({hasText: widgetName})
				.getByRole('button', {exact: true, name: 'Add Content'});
		};
		this.addPageButton = page.getByRole('button', {name: 'Add'});
		this.addWidgetButton = page.getByTestId('add');
		this.backLink = page.locator('a:has(svg[class*="angle-left"])').first();
		this.changeImageButton = page.getByRole('button', {
			name: 'Change Image',
		});
		this.cityInput = page.getByLabel('City');
		this.contactAddButton = page.getByRole('button', {name: 'Add'}).first();
		this.contactLink = page.getByRole('link', {name: 'Contact'});
		this.controlMenuAvatar = page.locator(
			'.control-menu .personal-menu-dropdown'
		);
		this.countrySelect = page.getByLabel('Country');
		this.doneButton = page.getByRole('button', {name: 'Done'});
		this.emailAddressInput = page.getByLabel('Email Address');
		this.firstNameInput = page.getByLabel('First Name');
		this.lastNameInput = page.getByLabel('Last Name');
		this.menuItem = (name: string) => {
			return page.getByRole('menuitem', {name});
		};
		this.myDashboardMenuItem = page.getByRole('menuitem', {
			name: 'My Dashboard',
		});
		this.myDashboardText = page.getByText('My Dashboard');
		this.myOrganizationsHeading = page.getByRole('heading', {
			name: 'My Organizations',
		});
		this.myOrganizationsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'My Organizations',
		});
		this.myOrganizationsPortletBody = page.locator(
			'.MyOrganizationsPortlet .portlet-body'
		);
		this.myProfileDisplayName = page.locator('.lfr-contact-name > a');
		this.myProfileMenuItem = page.getByRole('menuitem', {
			name: 'My Profile',
		});
		this.mySitesMenuItem = page.getByRole('menuitem', {
			name: 'My Sites',
		});
		this.myWorkflowTasksHeading = page.getByRole('heading', {
			name: 'My Workflow Tasks',
		});
		this.myWorkflowTasksMenuItem = page.getByRole('menuitem', {
			name: 'My Workflow Tasks',
		});
		this.newPageButton = page.getByRole('button', {name: 'New'});
		this.notFoundCode = page.getByText('404');
		this.notFoundMessage = page.getByText('Page Not Found');
		this.notificationsHeading = page.getByTestId('headerTitle');
		this.notificationsMenuItem = page
			.locator('.dropdown-menu-personal-menu')
			.getByRole('menuitem', {exact: true, name: 'Notifications'});
		this.page = page;
		this.pageNameInput = page.getByLabel('Name');
		this.personalBarAvatar = page.locator(
			'.portlet-user-personal-bar .personal-menu-dropdown'
		);
		this.portletContentLink = (text: string) => {
			return page.locator('.portlet-content').getByText(text);
		};
		this.postalCodeInput = page.getByLabel('Postal Code');
		this.regionSelect = page.getByLabel('Region');
		this.screenNameInput = page.getByLabel('Screen Name');
		this.selectSiteHeading = page.getByRole('heading', {
			name: 'Select Site',
		});
		this.siteIframeCard = page
			.frameLocator('iframe')
			.locator('.card')
			.first();
		this.street1Input = page.getByLabel('Street 1');
		this.uploadImageFrame = page.frameLocator(
			'iframe[title="Upload Image"]'
		);
		this.uploadImageDoneButton = this.uploadImageFrame.getByRole('button', {
			name: 'Done',
		});
		this.uploadImageSelectImageButton =
			this.uploadImageFrame.getByLabel('Select Image');
		this.userDefaultIcon = page.locator(
			'button img[src*="user_portrait"], button .lexicon-icon-user'
		);
		this.userDisplayDataText = page.getByText('User Display Data');
		this.userPersonalMenuButton = page.getByTitle('User Profile Menu');
		this.userPortrait = page.locator('button img[src*="user_portrait"]');
		this.usersAndOrganizationsHeading = page.getByRole('heading', {
			name: 'Users and Organizations',
		});
		this.welcomeText = page.getByText('Welcome to Liferay');
		this.widgetPageMenuItem = page.getByRole('menuitem', {
			name: 'Widget Page',
		});
		this.widgetSearchInput = page
			.getByRole('tabpanel')
			.getByPlaceholder('Search...');
	}
}
