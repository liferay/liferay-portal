/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {FDSTablePage} from './FDSTablePage';

const MCP_SERVER_PORTLET_ID =
	'com_liferay_mcp_server_web_internal_portlet_MCPServerPortlet';

const PROFILES_URL = `/group/guest/~/control_panel/manage?p_p_id=${MCP_SERVER_PORTLET_ID}&_${MCP_SERVER_PORTLET_ID}_mvcRenderCommandName=${encodeURIComponent(
	'/mcp_server/view_profiles'
)}`;

export class ProfilesPage extends FDSTablePage {
	readonly newProfileButton: Locator;

	constructor(page: Page) {
		super(page);

		this.newProfileButton = page.getByRole('button', {
			name: 'New Profile',
		});
	}

	async goto() {
		await this.page.goto(PROFILES_URL, {waitUntil: 'load'});

		await this.waitForTable();
	}

	get addMasksButton(): Locator {
		return this.page.getByRole('button', {name: 'Add Masks'}).first();
	}

	get addMasksSubmitButton(): Locator {
		return this.dialog.getByRole('button', {exact: true, name: 'Add'});
	}

	get deselectAllButton(): Locator {
		return this.dialog.getByRole('button', {name: 'Deselect All'});
	}

	maskCheckbox(name: string): Locator {
		return this.maskTreeItem(name).getByRole('checkbox');
	}

	maskTreeItem(name: string): Locator {
		return this.dialog.getByRole('treeitem', {exact: true, name});
	}

	get dataMasksTab(): Locator {
		return this.page
			.locator('ul.navbar-nav > li')
			.filter({hasText: 'Data Masks'});
	}

	get dataMasksTabButton(): Locator {
		return this.page.getByRole('button', {exact: true, name: 'Data Masks'});
	}

	get dataMasksTabLink(): Locator {
		return this.page.getByRole('link', {exact: true, name: 'Data Masks'});
	}

	get formHeading(): Locator {
		return this.page.locator('.control-menu-level-1-heading');
	}

	get masksRows(): Locator {
		return this.page.locator('table tbody tr');
	}

	maskRow(name: string): Locator {
		return this.masksRows.filter({hasText: name});
	}

	get profileInfoTab(): Locator {
		return this.page
			.locator('ul.navbar-nav > li')
			.filter({hasText: 'Profile Info'});
	}

	get removeReasonInput(): Locator {
		return this.page.locator('#removeDataMaskReason');
	}

	get nameInput(): Locator {
		return this.page.locator('#profileName');
	}

	get descriptionInput(): Locator {
		return this.page.locator('#profileDescription');
	}

	get saveButton(): Locator {
		return this.page.getByRole('button', {name: 'Save'});
	}

	get cancelButton(): Locator {
		return this.page.getByRole('button', {name: 'Cancel'});
	}
}
