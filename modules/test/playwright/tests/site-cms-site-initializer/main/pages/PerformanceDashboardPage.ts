/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';

export class PerformanceDashboardPage {
	readonly page: Page;

	readonly assetConsumptionCard: Locator;
	readonly overviewHeading: Locator;
	readonly performanceTab: Locator;
	readonly rangeSelectorsButton: Locator;
	readonly spacesButton: Locator;
	readonly viewsByCategorizationCard: Locator;
	readonly viewsByLocationCard: Locator;

	constructor(page: Page) {
		this.page = page;

		this.assetConsumptionCard = page.locator('.cms-dashboard__base-card', {
			hasText: 'Asset Consumption',
		});
		this.overviewHeading = page.getByRole('heading', {
			name: 'Performance Overview',
		});
		this.performanceTab = page.getByRole('tab', {name: 'Performance'});
		this.rangeSelectorsButton = page.getByTestId('rangeSelectors');
		this.spacesButton = page.getByRole('combobox', {
			name: 'Filter by Spaces',
		});
		this.viewsByCategorizationCard = page.locator(
			'.cms-dashboard__base-card',
			{
				hasText: 'Views by Categorization',
			}
		);
		this.viewsByLocationCard = page.locator('.cms-dashboard__base-card', {
			hasText: 'Views by Location',
		});
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.cmsDashboard);

		await clickAndExpectToBeVisible({
			target: this.overviewHeading,
			trigger: this.performanceTab,
		});
	}

	getMetricCard(title: string) {
		return this.page
			.locator('.cms-dashboard__interactive-card')
			.filter({hasText: title});
	}

	async selectRange(rangeKey: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByTestId(`filter-item-${rangeKey}`),
			trigger: this.rangeSelectorsButton,
		});
	}

	async selectSpace(name: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('option', {name}),
			trigger: this.spacesButton,
		});
	}
}
