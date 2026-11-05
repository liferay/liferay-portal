/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';

export class ViewTasksPage {
	readonly page: Page;

	constructor(page: Page) {
		this.page = page;
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.cmsTasks);
		await this.page
			.getByRole('heading', {level: 2, name: 'Tasks'})
			.waitFor();
	}
}
