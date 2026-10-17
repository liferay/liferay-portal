/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

const DEPOT_ADMIN_NAMESPACE =
	'_com_liferay_depot_web_portlet_DepotAdminPortlet_';

export class DepotAdminPage {
	readonly page: Page;

	constructor(page: Page) {
		this.page = page;
	}

	async gotoDashboard(depotEntryId: string) {
		await this.page.goto(
			`/group/asset-library-${depotEntryId}${PORTLET_URLS.depotAdmin}&${DEPOT_ADMIN_NAMESPACE}mvcRenderCommandName=%2Fdepot%2Fview_depot_dashboard&${DEPOT_ADMIN_NAMESPACE}depotEntryId=${depotEntryId}`
		);
	}

	async gotoGeneralSettings(depotEntryId: string) {
		await this.page.goto(
			`/group/guest${PORTLET_URLS.depotAdmin}&${DEPOT_ADMIN_NAMESPACE}mvcRenderCommandName=%2Fdepot%2Fedit_depot_entry&${DEPOT_ADMIN_NAMESPACE}depotEntryId=${depotEntryId}&${DEPOT_ADMIN_NAMESPACE}screenNavigationEntryKey=general`
		);
	}

	async setApplications(
		depotEntryId: string,
		applications: string[],
		enabled: boolean
	) {
		await this.gotoGeneralSettings(depotEntryId);

		for (const application of applications) {
			await this.page
				.getByLabel(application, {exact: true})
				.setChecked(enabled);
		}

		await this.page
			.getByRole('button', {exact: true, name: 'Save'})
			.click();

		await waitForAlert(this.page);
	}
}
