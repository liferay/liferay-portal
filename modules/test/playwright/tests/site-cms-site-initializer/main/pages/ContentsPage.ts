/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

export class ContentsPage {
	readonly page: Page;

	readonly newButton: Locator;
	readonly publishButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.newButton = page.getByLabel('New');
		this.publishButton = page.getByText('Publish');
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.cmsContents);

		await this.newButton.waitFor();
	}

	async closeSidePanel() {
		const trigger = this.page.locator(
			'.content-editor__side-panel button[aria-selected="true"]'
		);

		if (trigger) {
			await clickAndExpectToBeHidden({
				target: trigger,
				trigger,
			});
		}
	}

	async createContent(type: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: type}),
			trigger: this.newButton,
		});

		await this.openSidePanel('General');

		await this.closeSidePanel();
	}

	async deleteContent(title: string) {
		const card = this.page
			.locator('tr', {hasText: title})
			.or(this.page.locator('.card-row', {hasText: title}));

		this.page.once('dialog', async (dialog) => {
			await dialog.accept();
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Delete'}),
			trigger: card.locator('button'),
		});

		await waitForAlert(this.page, 'Your request completed successfully');
	}

	async editContent(title: string) {
		const card = this.page
			.locator('tr', {hasText: title})
			.or(this.page.locator('.card-row', {hasText: title}));

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Edit'}),
			trigger: card.locator('button'),
		});

		await this.openSidePanel('General');

		await this.closeSidePanel();
	}

	async openSidePanel(panelName: 'General') {
		await clickAndExpectToBeVisible({
			target: this.page.locator('.sidebar-header', {hasText: panelName}),
			trigger: this.page.getByLabel(panelName),
		});
	}

	async saveContent() {
		await clickAndExpectToBeVisible({
			target: this.newButton,
			timeout: 5000,
			trigger: this.publishButton,
		});
	}
}
