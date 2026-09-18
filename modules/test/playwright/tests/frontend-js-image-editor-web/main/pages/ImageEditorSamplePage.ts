/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import POM from '../../../../utils/POM';
import {ImageEditor} from './ImageEditor';

export class ImageEditorSamplePage extends POM {
	readonly openButton: Locator;

	constructor(page: Page, url: string) {
		super(page, url);

		this.openButton = page.getByRole('button', {
			name: 'Edit sample image',
		});
	}

	async openEditor(): Promise<ImageEditor> {
		await this.openButton.click();

		const imageEditor = new ImageEditor(this.page);

		await expect(imageEditor.workspace).toBeVisible();

		return imageEditor;
	}

	override async waitFor() {
		await expect(this.openButton).toBeVisible();
	}
}
