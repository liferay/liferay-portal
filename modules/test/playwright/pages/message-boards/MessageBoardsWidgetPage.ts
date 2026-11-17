/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {ApiHelpers} from '../../helpers/ApiHelpers';
import getRandomString from '../../utils/getRandomString';
import {performLogout} from '../../utils/performLogin';
import {WidgetPagePage} from '../layout-admin-web/WidgetPagePage';
export class MessageBoardsWidgetPage {
	readonly apiHelpers: ApiHelpers;
	readonly page: Page;
	readonly widgetPagePage: WidgetPagePage;

	constructor(page: Page) {
		this.apiHelpers = new ApiHelpers(page);
		this.page = page;
		this.widgetPagePage = new WidgetPagePage(page);
	}

	async addMessageBoardsPortlet(site: Site) {
		const layout = await this.apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await this.page.goto(
			`/web${site.friendlyUrlPath}${layout.friendlyURL}`
		);

		await this.widgetPagePage.addPortlet('Message Boards');

		return layout;
	}

	async addGuestReply(site: Site, layout: Layout, buttonName: string) {
		await performLogout(this.page);

		await this.page.goto(
			`/web${site.friendlyUrlPath}${layout.friendlyURL}`
		);

		await this.page.getByRole('link', {name: 'Thread Subject'}).click();

		await this.page.getByRole('button', {name: 'Reply'}).click();

		const messageBodyIframe = this.page.frameLocator(
			'iframe[title*="_com_liferay_message_boards_web_portlet_MBPortlet_replyMessageBody"]'
		);

		await messageBodyIframe.getByRole('textbox').fill('test guest');

		await this.page.getByRole('button', {name: buttonName}).click();
	}

	async goToThread(site: Site, layout: Layout, threadSubject: string) {
		await this.page.goto(
			`/web${site.friendlyUrlPath}${layout.friendlyURL}`
		);

		await this.page.waitForLoadState('networkidle');

		// Open the thread through its message URL: the card title link is
		// covered by the card overlay, so navigate directly to its href

		const threadURL = await this.page.evaluate((subject) => {
			const anchors = Array.from(document.querySelectorAll('a'));

			const anchor = anchors.find(
				(element) =>
					(element.getAttribute('href') || '').includes(
						'/message_boards/message/'
					) && element.textContent?.includes(subject)
			);

			return anchor?.getAttribute('href') || '';
		}, threadSubject);

		await this.page.goto(threadURL);

		await this.page.waitForLoadState('networkidle');
	}

	async replyToThread(
		site: Site,
		layout: Layout,
		threadSubject: string,
		replyBody: string
	) {
		await this.goToThread(site, layout, threadSubject);

		await this.page.getByRole('button', {name: 'Reply'}).click();

		await this.page
			.frameLocator(
				'iframe[title*="_com_liferay_message_boards_web_portlet_MBPortlet_replyMessageBody"]'
			)
			.getByRole('textbox')
			.fill(replyBody);

		await this.page
			.getByRole('button', {exact: true, name: 'Publish'})
			.click();

		await this.page.waitForLoadState('networkidle');
	}

	async replyToThreadAsDraft(
		site: Site,
		layout: Layout,
		threadSubject: string,
		replyBody: string
	) {
		await this.goToThread(site, layout, threadSubject);

		await this.page.getByRole('button', {name: 'Reply'}).click();

		await this.page
			.frameLocator(
				'iframe[title*="_com_liferay_message_boards_web_portlet_MBPortlet_replyMessageBody"]'
			)
			.getByRole('textbox')
			.waitFor();

		await this.page.getByRole('button', {name: 'Advanced Reply'}).click();

		await this.page.waitForLoadState('networkidle');

		await this.page
			.frameLocator('iframe[title*="bodyEditor"]')
			.locator('body')
			.fill(replyBody);

		await this.page.getByRole('button', {name: 'Save as Draft'}).click();

		await this.page.waitForLoadState('networkidle');
	}

	async addCategory(site: Site, layout: Layout, categoryName: string) {
		await this.page.goto(
			`/web${site.friendlyUrlPath}${layout.friendlyURL}`
		);

		const categoryMenu = this.page.getByRole('link', {
			name: 'Add Category',
		});

		await categoryMenu.waitFor();
		await categoryMenu.click();

		await this.page
			.locator(
				'[id="_com_liferay_message_boards_web_portlet_MBPortlet_name"]'
			)
			.fill(categoryName);

		await this.page.getByRole('button', {name: 'Save'}).click();
	}
}
