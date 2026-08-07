/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {liferayConfig} from '../../../liferay.config';
import POM from '../../../utils/POM';

const PORTLET_NAME =
	'com_liferay_client_extension_web_internal_portlet_ClientExtensionAdminPortlet';

const PORTLET_BASE_URL =
	`${liferayConfig.environment.baseUrl}/group/control_panel/manage` +
	`?p_p_id=${PORTLET_NAME}` +
	`&_${PORTLET_NAME}_mvcRenderCommandName=/client_extension_admin/view_client_extension_entry`;

export class ViewClientExtensionPage extends POM {
	readonly externalReferenceCode: string;
	readonly nameInput: Locator;
	readonly portletName = PORTLET_NAME;

	constructor(page: Page, externalReferenceCode: string) {
		super(
			page,
			`${PORTLET_BASE_URL}` +
				`&_${PORTLET_NAME}_externalReferenceCode=${externalReferenceCode}`
		);

		this.externalReferenceCode = externalReferenceCode;

		this.nameInput = page.getByLabel('Name', {exact: true});
	}

	getInputByLabel(fieldLabel: string): Locator {
		return this.page.getByLabel(fieldLabel, {exact: true});
	}

	async waitFor() {
		await this.nameInput.waitFor({state: 'visible'});
	}
}
