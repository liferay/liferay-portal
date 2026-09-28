/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {PORTLET_URLS} from '../../utils/portletUrls';

export class NotificationsPage {
	readonly page: Page;

	constructor(page: Page) {
		this.page = page;
	}

	getNotification(body: string, title: string) {
		return this.getNotificationByTitle(title).filter({hasText: body});
	}

	getNotificationByTitle(title: string) {
		return this.page.locator('.autofit-section').filter({hasText: title});
	}

	async goto() {
		await this.page.goto(
			`/group/control_panel${PORTLET_URLS.notifications}`
		);
	}
}
