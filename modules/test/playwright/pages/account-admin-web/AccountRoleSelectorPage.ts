/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {DataTablePage} from './DataTablePage';

export class AccountRoleSelectorPage {
	readonly doneButton: Locator;
	readonly frame: FrameLocator;
	readonly page: Page;
	readonly rolesTable: DataTablePage;

	constructor(page: Page) {
		this.doneButton = page.getByRole('button', {
			exact: true,
			name: 'Done',
		});
		this.frame = page.frameLocator('iframe[id="modalIframe"]');
		this.page = page;
		this.rolesTable = new DataTablePage(
			this.frame,
			this.frame
				.locator(
					'#p_p_id_com_liferay_account_admin_web_internal_portlet_AccountEntriesAdminPortlet_'
				)
				.or(
					this.frame.locator(
						'#p_p_id_com_liferay_account_admin_web_internal_portlet_AccountEntriesManagementPortlet_'
					)
				)
		);
	}

	async selectRoles(roles: Array<string>, select = true) {
		for (const role of roles) {
			const checkbox = await this.rolesTable.rowCheckbox(role);

			if (select) {
				await checkbox.check();
			}
			else {
				await checkbox.uncheck();
			}
		}

		await this.doneButton.click();

		await waitForAlert(this.page);
	}
}
