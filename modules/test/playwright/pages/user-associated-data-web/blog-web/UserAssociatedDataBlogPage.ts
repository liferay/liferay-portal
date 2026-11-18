/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class UserAssociatedDataBlogPage {
	readonly blogContentInput: Locator;
	readonly blogTitleInput: Locator;
	readonly blogTitleLink: (subject: string) => Locator;
	readonly newButton: Locator;
	readonly optionsButton: Locator;
	readonly page: Page;
	readonly publishButton: Locator;

	constructor(page: Page) {
		this.blogContentInput = page.locator(
			'#_com_liferay_blogs_web_portlet_BlogsAdminPortlet_contentEditor.cke_editable'
		);
		this.blogTitleInput = page.getByPlaceholder('Title *');
		this.blogTitleLink = (subject: string) =>
			page.getByRole('link', {name: subject});
		this.newButton = page.getByRole('link', {name: 'Add Blog Entry'});
		this.optionsButton = page.getByRole('button', {name: 'Options'});
		this.page = page;
		this.publishButton = page.getByRole('button', {name: 'Publish'});
	}
}
