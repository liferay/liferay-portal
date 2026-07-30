/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {FDSTablePage} from './FDSTablePage';

const MCP_SERVER_PORTLET_ID =
	'com_liferay_mcp_server_web_internal_portlet_MCPServerPortlet';

const PROMPTS_URL = `/group/guest/~/control_panel/manage?p_p_id=${MCP_SERVER_PORTLET_ID}&_${MCP_SERVER_PORTLET_ID}_mvcRenderCommandName=${encodeURIComponent(
	'/mcp_server/view_prompts'
)}`;

export class PromptsPage extends FDSTablePage {
	readonly newPromptButton: Locator;

	constructor(page: Page) {
		super(page);

		this.newPromptButton = page.getByLabel('New Prompt');
	}

	async goto() {
		await this.page.goto(PROMPTS_URL, {waitUntil: 'load'});

		await this.waitForTable();
	}

	get formHeading(): Locator {
		return this.page.locator('.control-menu-level-1-heading');
	}

	get nameInput(): Locator {
		return this.page.locator('#promptName');
	}

	get descriptionInput(): Locator {
		return this.page.locator('#promptDescription');
	}

	get promptInput(): Locator {
		return this.page.locator('#promptContent');
	}

	get saveButton(): Locator {
		return this.page.getByRole('button', {name: 'Save'});
	}

	get cancelButton(): Locator {
		return this.page.getByRole('button', {name: 'Cancel'});
	}
}
