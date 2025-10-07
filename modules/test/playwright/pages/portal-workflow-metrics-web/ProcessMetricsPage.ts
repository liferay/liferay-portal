/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class ProcessMetricsPage {
	readonly page: Page;
	readonly viewAllAssigneesButton: Locator;
	readonly viewAllStepsButton: Locator;

	constructor(page: Page) {
		this.page = page;
		this.viewAllAssigneesButton = this.page.getByText('View All Assignees');
		this.viewAllStepsButton = this.page.getByText('View All Steps');
	}
}
