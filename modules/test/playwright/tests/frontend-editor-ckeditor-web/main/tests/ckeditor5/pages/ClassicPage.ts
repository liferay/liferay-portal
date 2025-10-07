/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

export class ClassicPage {
	readonly editable: Locator;
	readonly itemSelectorFrame: FrameLocator;
	readonly page: Page;
	readonly toolbar: {
		buttonLabels: Locator;
		container: Locator;
	};
	readonly videoSelectorFrame: FrameLocator;

	constructor(page: Page) {
		this.editable = page.locator('.ck-editor__editable');

		this.itemSelectorFrame = page.frameLocator(
			'iframe[title="Select Item"]'
		);

		this.page = page;

		const toolbarContainer = page.getByLabel('Editor toolbar');

		this.toolbar = {
			buttonLabels: toolbarContainer
				.getByRole('button')
				.locator('.ck-button__label'),
			container: toolbarContainer,
		};
	}
}
