/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import fillAndClickOutside from '../../utils/fillAndClickOutside';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';

export class PageTemplatesPage {
	readonly page: Page;

	readonly newButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.newButton = page.getByText('New', {exact: true});
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.pageTemplates}`
		);
	}

	async addPageTemplate(name: string, type: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: type}),
			trigger: this.newButton,
		});

		await this.page.getByPlaceholder('Name', {exact: true}).fill(name);

		await this.page.getByRole('button', {name: 'Save'}).click();

		await this.page.getByRole('heading', {name}).waitFor();
	}

	async addPageTemplateCollection(name: string) {
		await this.page.getByRole('link', {exact: true, name: 'New'}).click();

		await this.page
			.getByRole('heading', {name: 'Add Page Template Set'})
			.waitFor();

		await fillAndClickOutside(
			this.page,
			this.page.getByLabel('Name'),
			name
		);

		await this.page
			.getByRole('button', {exact: true, name: 'Save'})
			.click();

		await waitForAlert(this.page);
	}

	async clickAction(action: string, title: string) {
		const actionsPath = '//p[@title="' + title + '"]/../..';

		await this.page.locator(actionsPath).getByLabel('More actions').click();
		await this.page.getByRole('menuitem', {name: action}).click();
	}

	async deletePageTemplate(name: string) {
		await this.clickAction('Delete', name);

		await this.page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(this.page);
	}
}
