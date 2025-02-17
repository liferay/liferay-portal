/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../liferay.config';
import getRandomString from '../../../utils/getRandomString';
import getPageDefinition from '../../layout-content-page-editor-web/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/utils/getWidgetDefinition';

export class FDSSamplePage {
	private readonly apiHelpers: ApiHelpers;
	readonly customViewsActionsButton: Locator;
	readonly customViewsDeleteAlert: Locator;
	readonly customViewsSaveModal: Locator;
	readonly customViewsSelectorButton: Locator;
	readonly page: Page;
	readonly sidePanel: Locator;
	readonly sidePanelFrame: FrameLocator;
	readonly tablist: Locator;
	readonly table: {
		container: Locator;
		headerCells: Locator;
		itemActionsCells: Locator;
		manageColumnsVisibilityButton: Locator;
	};

	constructor(page: Page) {
		this.apiHelpers = new ApiHelpers(page);
		this.customViewsActionsButton = page.getByLabel('Show View Actions', {
			exact: true,
		});
		this.customViewsDeleteAlert = page.getByRole('dialog', {
			name: 'Delete View',
		});
		this.customViewsSaveModal = page.getByRole('dialog', {
			name: 'Save New View As',
		});
		this.customViewsSelectorButton = page.getByLabel('Views', {
			exact: true,
		});
		this.page = page;
		this.sidePanel = page.locator('.fds-side-panel');
		this.sidePanelFrame = this.sidePanel.frameLocator('iframe');
		this.tablist = page.getByRole('tablist');

		const tableContainer = page.locator('.fds table');

		this.table = {
			container: tableContainer,
			headerCells: tableContainer.locator('th'),
			itemActionsCells: tableContainer.locator('.cell-item-actions'),
			manageColumnsVisibilityButton: tableContainer.getByTitle(
				'Manage Columns Visibility'
			),
		};
	}

	async selectTab(label: string) {
		const navLink = this.page.locator('.nav-link').filter({hasText: label});

		await navLink.click();

		await expect(navLink).toHaveClass(/active/);
	}

	async setupFDSSampleWidget({locale = 'en', site}) {
		const widgetDefinition = getWidgetDefinition({
			id: getRandomString(),
			widgetName:
				'com_liferay_frontend_data_set_sample_web_internal_portlet_FDSSamplePortlet',
		});

		const layout = await this.apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		const url = `${liferayConfig.environment.baseUrl}/${locale}/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;

		await this.page.goto(url);

		return {url};
	}
}
