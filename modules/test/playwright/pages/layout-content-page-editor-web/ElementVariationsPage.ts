/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';

export class ElementVariationsPage {
	readonly audienceInput: Locator;
	readonly hideToggle: Locator;
	readonly htmlInput: Locator;
	readonly javaScriptInput: Locator;
	readonly languageSelector: Locator;
	readonly nameInput: Locator;
	readonly newVariationButton: Locator;
	readonly page: Page;
	readonly pageElementPicker: Locator;
	readonly saveButton: Locator;
	readonly sidebar: Locator;

	constructor(page: Page) {
		this.audienceInput = page.getByLabel('Audience');
		this.hideToggle = page.getByText('Hide Page Element');
		this.htmlInput = page.getByLabel('HTML');
		this.javaScriptInput = page.getByLabel('JavaScript');
		this.languageSelector = page.getByLabel('Select a language');
		this.nameInput = page.getByLabel('Name');
		this.newVariationButton = page.getByRole('button', {
			name: 'New Variation',
		});
		this.page = page;
		this.pageElementPicker = page.getByLabel('Page Element');
		this.saveButton = page.getByRole('button', {exact: true, name: 'Save'});
		this.sidebar = page.locator('.element-variations__sidebar');
	}

	async createElementVariation({
		audienceName,
		hide = false,
		html,
		javaScript,
		name,
		pageElementLabel,
		translations = [],
	}: {
		audienceName: string;
		hide?: boolean;
		html?: string;
		javaScript?: string;
		name: string;
		pageElementLabel: string;
		translations?: Array<{
			html?: string;
			javaScript?: string;
			languageId: string;
		}>;
	}) {
		await this.newVariationButton.click();

		await this.nameInput.fill(name);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('option', {name: pageElementLabel}),
			trigger: this.pageElementPicker,
		});

		await this.audienceInput.fill(audienceName);

		await this.page
			.locator('.dropdown-menu')
			.getByText(audienceName)
			.click();

		if (hide) {
			await this.hideToggle.click();
		}

		if (html) {
			await this.htmlInput.fill(html);
		}

		if (javaScript) {
			await this.javaScriptInput.fill(javaScript);
		}

		for (const translation of translations) {
			await this.selectLanguage(translation.languageId);

			if (translation.html) {
				await this.htmlInput.fill(translation.html);
			}

			if (translation.javaScript) {
				await this.javaScriptInput.fill(translation.javaScript);
			}
		}

		await this.saveButton.click();

		await this.sidebar.getByText(name).waitFor();
	}

	async selectLanguage(languageId: string) {
		await this.languageSelector.click();

		await this.page
			.getByRole('option', {name: `${languageId} Language`})
			.click();
	}
}
