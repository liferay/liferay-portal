/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../../utils/waitForAlert';
import {ClientExtensionsPage} from './ClientExtensionsPage';

const EDIT_CLIENT_EXTENSION_BASE_URL =
	'/group/control_panel/manage?p_p_id=com_liferay_client_extension_web_internal_portlet_ClientExtensionAdminPortlet&_com_liferay_client_extension_web_internal_portlet_ClientExtensionAdminPortlet_mvcRenderCommandName=/client_extension_admin/edit_client_extension_entry&_com_liferay_client_extension_web_internal_portlet_ClientExtensionAdminPortlet_type=';

const PORTLET_ID =
	'_com_liferay_client_extension_web_internal_portlet_ClientExtensionAdminPortlet';

export class EditClientExtensionsPage {
	readonly clientExtensionsPage: ClientExtensionsPage;
	readonly clientExtensionType: string;
	readonly descriptionContentEditable: Locator;
	readonly descriptionCKEditor: Locator;
	readonly nameInput: Locator;
	readonly newClientExtensionTypeMenuItem: Locator;
	readonly page: Page;
	readonly portletId: string;
	readonly publishButton: Locator;

	constructor(page: Page, clientExtensionType: string) {
		this.clientExtensionsPage = new ClientExtensionsPage(page);
		this.clientExtensionType = clientExtensionType;
		this.nameInput = page.locator(`#${PORTLET_ID}_name`);
		this.page = page;
		this.portletId = PORTLET_ID;
		this.publishButton = page.getByRole('button', {
			name: 'Publish',
		});

		this.descriptionCKEditor = page.locator(
			'#cke__com_liferay_client_extension_web_internal_portlet_ClientExtensionAdminPortlet_description'
		);

		const descriptionIframe = page.frameLocator(
			`#cke_${PORTLET_ID}_description iframe`
		);

		this.descriptionContentEditable =
			descriptionIframe.locator('.cke_editable');
	}

	async goto() {
		await this.page.goto(
			`${EDIT_CLIENT_EXTENSION_BASE_URL}${this.clientExtensionType}`
		);
	}

	async publish() {
		await this.publishButton.click();

		await waitForAlert(this.page);
	}
}
