/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';

export class UserAssociatedDataEditMessageBoardThreadPage {
	readonly confirmSelectionButton: Locator;
	readonly editorFrame: FrameLocator;
	readonly editorFrameTextInput: Locator;
	readonly page: Page;
	readonly publishButton: Locator;
	readonly relatedAssetLink: (assetTitle: string) => Locator;
	readonly relatedAssetsButton: Locator;
	readonly selectButton: Locator;
	readonly subjectInput: Locator;

	constructor(page: Page) {
		this.confirmSelectionButton = page
			.getByRole('dialog')
			.getByRole('button', {exact: true, name: 'Select'});
		this.editorFrame = page.frameLocator(
			'iframe[title="Editor\\, _com_liferay_message_boards_web_portlet_MBAdminPortlet_bodyEditor"]'
		);
		this.editorFrameTextInput = this.editorFrame.locator('body');
		this.page = page;
		this.publishButton = page.getByRole('button', {name: 'Publish'});
		this.relatedAssetLink = (assetTitle: string) =>
			page.getByText(assetTitle);
		this.relatedAssetsButton = page.getByRole('button', {
			name: 'Related Assets',
		});
		this.selectButton = page.getByLabel('Select Items');
		this.subjectInput = page.getByLabel('Subject Required');
	}

	async selectRelatedAssets(assetTitles: string[]) {
		await this.relatedAssetsButton.click();

		await clickAndExpectToBeVisible({
			target: this.confirmSelectionButton,
			trigger: this.selectButton,
		});

		for (const assetTitle of assetTitles) {
			await this.page
				.getByRole('checkbox', {name: `Select ${assetTitle}`})
				.check();
		}

		await this.confirmSelectionButton.click();
	}
}
