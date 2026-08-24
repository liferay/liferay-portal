/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {gotoWithRetry} from '../../../utils/gotoWithRetry';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {ViewObjectDefinitionsPage} from '../ViewObjectDefinitionsPage';

export class ViewObjectActionsPage {
	readonly actionsTabItem: Locator;
	readonly addObjectActionButton: Locator;
	readonly frontendDataSetItems: Locator;
	readonly lastExecutionCell: Locator;
	readonly page: Page;
	readonly viewObjectDefinitionsPage: ViewObjectDefinitionsPage;

	constructor(page: Page) {
		this.actionsTabItem = page.getByRole('link', {name: 'Actions'});
		this.addObjectActionButton = page
			.getByLabel('Add Object Action')
			.first();
		this.frontendDataSetItems = page.locator('div.table-list-title a');
		this.lastExecutionCell = page.locator('.cell-status');
		this.page = page;
		this.viewObjectDefinitionsPage = new ViewObjectDefinitionsPage(page);
	}

	async gotoByObjectDefinitionId(objectDefinitionId: number) {
		const portletId =
			'com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet';

		await gotoWithRetry(
			this.page,
			`/group/guest${PORTLET_URLS.objects}&p_p_lifecycle=0&_${portletId}_mvcRenderCommandName=%2Fobject_definitions%2Fedit_object_definition&_${portletId}_objectDefinitionId=${objectDefinitionId}&_${portletId}_screenNavigationCategoryKey=actions`,
			{waitUntil: 'load'}
		);
	}

	async goto(objectDefinitionLabel: string) {
		await this.viewObjectDefinitionsPage.goto();

		await this.viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			objectDefinitionLabel
		);

		await this.actionsTabItem.click();
	}

	async openObjectActionSidePanel() {
		await this.addObjectActionButton.click();
	}
}
