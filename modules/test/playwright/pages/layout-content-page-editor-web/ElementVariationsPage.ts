/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {hoverAndExpectToBeVisible} from '../../utils/hoverAndExpectToBeVisible';

export class ElementVariationsPage {
	readonly audienceInput: Locator;
	readonly audiencesPriorityModal: Locator;
	readonly cancelButton: Locator;
	readonly experiencePicker: Locator;
	readonly hideToggle: Locator;
	readonly htmlInput: Locator;
	readonly javaScriptInput: Locator;
	readonly languageSelector: Locator;
	readonly nameInput: Locator;
	readonly newVariationButton: Locator;
	readonly page: Page;
	readonly pageElementPicker: Locator;
	readonly preview: FrameLocator;
	readonly saveButton: Locator;
	readonly sidebar: Locator;

	constructor(page: Page) {
		this.audienceInput = page.getByLabel('Audience');
		this.audiencesPriorityModal = page.locator(
			'.element-variations__audiences-priority-modal'
		);
		this.sidebar = page.locator('.element-variations__sidebar');
		this.cancelButton = page.getByRole('button', {
			exact: true,
			name: 'Cancel',
		});
		this.experiencePicker = this.sidebar.getByLabel('Experience');
		this.hideToggle = page.getByText('Hide Page Element');
		this.htmlInput = page.getByLabel('HTML', {exact: true});
		this.javaScriptInput = page.getByLabel('JavaScript', {exact: true});
		this.languageSelector = page.getByLabel('Select a language');
		this.nameInput = page.getByLabel('Name');
		this.newVariationButton = page
			.getByRole('button', {
				name: 'New Variation',
			})
			.or(
				page.getByRole('button', {
					name: 'New',
				})
			);
		this.page = page;
		this.pageElementPicker = page.getByLabel('Page Element');
		this.preview = page.frameLocator('iframe[title="Element Variations"]');
		this.saveButton = page.getByRole('button', {exact: true, name: 'Save'});
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

		await this.selectPageElement(pageElementLabel);

		await this.selectAudience(audienceName);

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

	async startElementVariationDraft() {
		await this.newVariationButton.click();

		await this.nameInput.waitFor();
	}

	async deleteElementVariation(name: string) {
		await this.openVariationActions(name);

		await this.page
			.locator('.dropdown-menu')
			.getByText('Delete', {exact: true})
			.click();

		await this.getVariationListItem(name).waitFor({state: 'hidden'});
	}

	async editElementVariation({
		hide,
		html,
		javaScript,
		name,
		newName,
	}: {
		hide?: boolean;
		html?: string;
		javaScript?: string;
		name: string;
		newName?: string;
	}) {
		await this.openVariationActions(name);

		await this.page
			.locator('.dropdown-menu')
			.getByText('Edit', {exact: true})
			.click();

		if (newName) {
			await this.nameInput.fill(newName);
		}

		if (hide) {
			await this.hideToggle.click();
		}

		if (html) {
			await this.htmlInput.fill(html);
		}

		if (javaScript) {
			await this.javaScriptInput.fill(javaScript);
		}

		await this.saveButton.click();

		await this.sidebar.getByText(newName ?? name).waitFor();
	}

	async cancelElementVariationDraft() {
		await this.page.keyboard.press('Escape');

		await this.cancelButton.click();

		await this.experiencePicker.waitFor();
	}

	getPageElementOption(label: string): Locator {
		return this.page.getByRole('option', {exact: true, name: label});
	}

	getVariationListItem(name: string): Locator {
		return this.sidebar.getByRole('listitem').filter({hasText: name});
	}

	async openPageElementPicker() {
		await this.pageElementPicker.click();
	}

	async openVariationActions(name: string) {
		const item = this.getVariationListItem(name);

		await hoverAndExpectToBeVisible({
			autoClick: true,
			target: item.getByRole('button', {name: 'Actions'}),
			trigger: item,
		});
	}

	async prioritizeAudience(audienceName: string) {
		await this.page
			.getByText('Audiences Priority', {exact: true})
			.locator('xpath=..')
			.getByRole('button')
			.click();

		await this.audiencesPriorityModal.waitFor();

		const items = this.audiencesPriorityModal.getByRole('listitem');

		const itemCount = await items.count();

		await items.filter({hasText: audienceName}).getByRole('button').focus();

		await this.page.keyboard.press('Space');

		await this.audiencesPriorityModal.locator('.dragging').waitFor();

		for (let index = 0; index < itemCount - 1; index++) {
			await this.page.keyboard.press('ArrowUp');

			await this.page.waitForTimeout(300);
		}

		await this.page.keyboard.press('Space');

		await this.audiencesPriorityModal
			.locator('.dragging')
			.waitFor({state: 'hidden'});

		const responsePromise = this.page.waitForResponse((response) =>
			response
				.url()
				.includes('update_segments_experience_audience_entry_rels')
		);

		await this.audiencesPriorityModal
			.getByRole('button', {exact: true, name: 'Save'})
			.click();

		await responsePromise;

		await this.audiencesPriorityModal.waitFor({state: 'hidden'});
	}

	async setVariationActive(name: string, active: boolean) {
		await this.openVariationActions(name);

		const responsePromise = this.page.waitForResponse((response) =>
			response
				.url()
				.includes(
					'update_layout_page_template_structure_rel_element_variation'
				)
		);

		await this.page
			.locator('.dropdown-menu')
			.getByText(active ? 'Enable' : 'Disable', {exact: true})
			.click();

		await responsePromise;
	}

	async selectAudience(audienceName: string) {
		await this.audienceInput.fill(audienceName);

		await this.page
			.locator('.dropdown-menu')
			.getByText(audienceName)
			.click();
	}

	async selectExperience(label: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('option', {exact: true, name: label}),
			trigger: this.experiencePicker,
		});
	}

	async selectLanguage(languageId: string) {
		await this.languageSelector.click();

		await this.page
			.getByRole('option', {name: `${languageId} Language`})
			.click();
	}

	async selectPageElement(pageElementLabel: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('option', {name: pageElementLabel}),
			trigger: this.pageElementPicker,
		});
	}
}
