/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {FDSTablePage} from './FDSTablePage';

const DATA_MASKS_URL =
	'/group/guest/~/control_panel/manage?p_p_id=com_liferay_mcp_server_web_internal_portlet_MCPServerPortlet';

export class DataMasksPage extends FDSTablePage {
	readonly newDataMaskButton: Locator;

	constructor(page: Page) {
		super(page);

		this.newDataMaskButton = page.getByRole('button', {
			name: 'New Data Mask',
		});
	}

	async goto() {
		await this.page.goto(DATA_MASKS_URL, {waitUntil: 'load'});

		await this.waitForTable();
	}

	get formHeading(): Locator {
		return this.page.locator('.control-menu-level-1-heading');
	}

	get nameInput(): Locator {
		return this.page.locator('#dataMaskName');
	}

	get descriptionInput(): Locator {
		return this.page.locator('#dataMaskDescription');
	}

	get matchPatternInput(): Locator {
		return this.page.locator('#dataMaskMatchPattern');
	}

	get regexPatternInput(): Locator {
		return this.page.locator('#dataMaskRegexPattern');
	}

	get replacementInput(): Locator {
		return this.page.locator('#dataMaskReplacement');
	}

	get saveButton(): Locator {
		return this.page.getByRole('button', {name: 'Save'});
	}

	get sampleInput(): Locator {
		return this.page.getByPlaceholder('Enter a sample value');
	}

	get outputInput(): Locator {
		return this.page.getByLabel('Output', {exact: true});
	}

	get testButton(): Locator {
		return this.page.getByRole('button', {exact: true, name: 'Test'});
	}
}
