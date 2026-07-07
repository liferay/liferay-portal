/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';

export class ExportImportDataSelectionPage {
	readonly collapseSectionButton: (name: string) => Locator;
	readonly expandSectionButton: (name: string) => Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.collapseSectionButton = (name) =>
			page.getByRole('button', {name: `Collapse ${name}`});
		this.expandSectionButton = (name) =>
			page.getByRole('button', {name: `Expand ${name}`});
		this.page = page;
	}

	async expandSection(name: string) {
		await clickAndExpectToBeVisible({
			target: this.collapseSectionButton(name),
			trigger: this.expandSectionButton(name),
		});
	}
}
