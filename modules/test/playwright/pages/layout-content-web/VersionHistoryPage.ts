/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {waitForPageToBeLoaded} from '../../utils/waitForPageToBeLoaded';

export class VersionHistoryPage {
	readonly page: Page;

	readonly experienceSelector: Locator;
	readonly languageSelector: Locator;
	readonly preview: FrameLocator;
	readonly versionList: Locator;

	constructor(page: Page) {
		this.page = page;

		this.experienceSelector = page.getByLabel('Experience Selector');
		this.languageSelector = page.getByLabel('Select a language');
		this.preview = page.frameLocator('.version-history__preview');
		this.versionList = page.getByRole('listbox', {name: 'Version History'});
	}

	/**
	 * The history mode only renders on the draft layout, so it never falls back
	 * to the published one
	 */
	async goto(layout: Layout, siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto('/');

		await this.page.goto(
			`/web${siteUrl || '/guest'}${layout.draftLayout?.friendlyURL || layout.friendlyURL}?p_l_mode=history`
		);
	}

	getVersion(name: string) {
		return this.versionList.getByRole('option').filter({hasText: name});
	}

	async restoreVersion(name: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Restore Version'}),
			trigger: this.getVersion(name).getByLabel('Show Options'),
		});

		const modal = this.page.getByRole('dialog');

		// Wait for the modal, otherwise it counts as hidden before it opens,
		// and give its closing animation room to finish

		await expect(modal).toBeVisible();

		await clickAndExpectToBeHidden({
			target: modal,
			timeout: 4000,
			trigger: modal.getByRole('button', {exact: true, name: 'Restore'}),
		});

		// Restoring reloads the page

		await waitForPageToBeLoaded(this.page);
	}

	async selectVersion(name: string) {
		const version = this.getVersion(name);

		await expect(async () => {
			await version.click({timeout: 1000});

			await expect(version).toHaveAttribute('aria-selected', 'true', {
				timeout: 1000,
			});
		}).toPass();
	}

	async switchExperience(experience: string) {

		// The accessible name of the option also holds its status label

		const option = this.page.getByRole('option', {name: experience});

		await clickAndExpectToBeVisible({
			target: option,
			trigger: this.experienceSelector,
		});

		await clickAndExpectToBeHidden({target: option, trigger: option});
	}

	async switchLanguage(language: string) {
		const option = this.page.getByRole('option', {
			name: `${language} Language`,
		});

		await clickAndExpectToBeVisible({
			target: option,
			trigger: this.languageSelector,
		});

		await clickAndExpectToBeHidden({target: option, trigger: option});
	}
}
