/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {CommerceDNDTablePage} from '../commerceDNDTablePage';

export class CommerceAdminProductConfigurationListsPage extends CommerceDNDTablePage {
	readonly addConfigurationList: Locator;
	readonly addConfigurationListCatalog: Locator;
	readonly addConfigurationListName: Locator;
	readonly addConfigurationListParentListElement: (
		parentConfigurationName?: string
	) => Locator;
	readonly addConfigurationListParentList: Locator;
	readonly addConfigurationListPriority: Locator;
	readonly addConfigurationListSaveButton: Locator;
	readonly entriesLink: Locator;
	readonly newConfigurationListName: Locator;
	readonly frame: FrameLocator;
	readonly page: Page;

	constructor(page: Page) {
		super(
			page,
			'#_com_liferay_commerce_product_definitions_web_internal_portlet_CPConfigurationListsPortlet_fm .fds table'
		);
		this.addConfigurationList = page
			.getByTestId('managementToolbar')
			.locator('[data-testid="fdsCreationActionButton"]');

		this.frame = page.frameLocator(
			'iframe[title="Add New Product Configuration"]'
		);
		this.addConfigurationListCatalog =
			this.frame.getByLabel('Catalog Required');
		this.addConfigurationListName = this.frame.getByLabel('Name Required');
		this.addConfigurationListParentListElement = (
			parentConfigurationName: string = 'Master Configuration Master'
		) => {
			return this.frame.getByRole('menuitem', {
				name: parentConfigurationName,
			});
		};
		this.addConfigurationListParentList =
			this.frame.getByPlaceholder('Type Here');
		this.addConfigurationListPriority =
			this.frame.getByLabel('Priority Required');

		this.addConfigurationListSaveButton = page.getByRole('button', {
			name: 'Submit',
		});
		this.entriesLink = page.getByRole('link', {
			exact: true,
			name: 'Entries',
		});
		this.newConfigurationListName = page.getByTestId('headerDetailsTitle');
		this.page = page;
	}
}
