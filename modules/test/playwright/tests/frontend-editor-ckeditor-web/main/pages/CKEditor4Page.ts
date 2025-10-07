/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

export class CKEditor4Page {
	readonly contextMenu: Locator;
	readonly editableFrame: FrameLocator;
	private readonly itemSelectorFrame: FrameLocator;
	readonly page: Page;
	private readonly toolbar: Locator;

	constructor(page: Page) {
		this.contextMenu = page.locator('.cke_dialog_container');

		this.editableFrame = page.frameLocator('iframe[title="editor"]');

		this.itemSelectorFrame = page.frameLocator(
			'iframe[title="Select Item"]'
		);

		this.page = page;

		this.toolbar = page.locator('.cke_toolbox');
	}

	async insertHTML(html: string) {
		const sourceButton = this.toolbar.getByRole('button', {name: 'Source'});

		await sourceButton.click();

		await this.page.evaluate((html) => {
			const textarea: HTMLTextAreaElement =
				document.querySelector('.cke_editable');

			textarea.value = html;
		}, html);

		await sourceButton.click();
	}

	async selectImageWithItemSelector({cardTitle}: {cardTitle: string}) {
		const siteAndLibrariesLink = this.itemSelectorFrame.getByRole('link', {
			name: 'Sites and Libraries',
		});

		await siteAndLibrariesLink.click();

		const liferayLink = this.itemSelectorFrame.getByRole('link', {
			name: 'Liferay',
		});

		await liferayLink.click();

		const liferayImagesLink = this.itemSelectorFrame.getByRole('link', {
			name: 'Provided by Liferay',
		});

		await liferayImagesLink.click();

		const imageCard = this.itemSelectorFrame.getByText(cardTitle);

		await imageCard.waitFor({state: 'visible'});

		await imageCard.click();

		await expect(
			this.itemSelectorFrame.getByText('Select Item')
		).toBeHidden();
	}
}
