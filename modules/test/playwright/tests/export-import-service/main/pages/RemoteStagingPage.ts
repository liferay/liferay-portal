/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {PageEditorPage} from '../../../../pages/layout-content-page-editor-web/PageEditorPage';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';

export class RemoteStagingPage {
	readonly page: Page;
	readonly pageEditorPage: PageEditorPage;
	readonly publishToRemoteLiveFrame: FrameLocator;
	readonly publishToRemoteLiveButton: Locator;
	readonly remoteLiveButton: Locator;
	readonly remoteHostTextBox: Locator;
	readonly remotePortTextBox: Locator;
	readonly remoteSiteIdTextBox: Locator;
	readonly publishSuccessfulLabel: Locator;

	constructor(page: Page) {
		this.page = page;
		this.pageEditorPage = new PageEditorPage(page);
		this.publishToRemoteLiveFrame = this.page.frameLocator(
			'iframe[title="Publish to Remote Live"]'
		);
		this.publishToRemoteLiveButton =
			this.publishToRemoteLiveFrame.getByRole('button', {
				name: 'Publish to Remote Live',
			});
		this.remoteLiveButton = this.page.getByLabel('Remote Live:');
		this.remoteHostTextBox = this.page.getByLabel('Remote Host/IP');
		this.remotePortTextBox = this.page.getByLabel('Remote Port');
		this.remoteSiteIdTextBox = this.page.getByLabel('Remote Site ID');
		this.publishSuccessfulLabel =
			this.publishToRemoteLiveFrame.getByText('Successful');
	}

	async publishToLive({
		layoutFriendlyURL,
		siteFriendlyUrl,
	}: {
		layoutFriendlyURL: string;
		siteFriendlyUrl: string;
	}) {
		await this.page.goto(`/web${siteFriendlyUrl}${layoutFriendlyURL}`);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.publishToRemoteLiveButton,
			trigger: this.pageEditorPage.publishToLiveButton,
		});

		await expect(this.publishSuccessfulLabel).toBeVisible({
			timeout: 30000,
		});
	}
}
