/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {ViewObjectDefinitionsPage} from '../ViewObjectDefinitionsPage';

export class EditObjectDetailsPage {
	readonly accountRestrictedFieldCombobox: Locator;
	readonly accountRestrictionToggle: Locator;
	readonly allowDraftToggle: Locator;
	readonly allowStandaloneEntriesToggle: Locator;
	readonly detailsTabItem: Locator;
	readonly entryTitleField: Locator;
	readonly entryTitleFieldCombobox: Locator;
	readonly friendlyURLSeparator: Locator;
	readonly labelInput: Locator;
	readonly labelLocalizationButton: Locator;
	readonly nameInput: Locator;
	readonly page: Page;
	readonly panelLinkCombobox: Locator;
	readonly pluralLabelInput: Locator;
	readonly pluralLabelLocalizationButton: Locator;
	readonly publishButton: Locator;
	readonly saveButton: Locator;
	readonly scopeCombobox: Locator;
	readonly showWidgetToggle: Locator;
	readonly viewObjectDefinitionsPage: ViewObjectDefinitionsPage;

	constructor(page: Page) {
		this.accountRestrictedFieldCombobox = page.getByRole('combobox', {
			name: 'Account Restricted Field',
		});
		this.accountRestrictionToggle = page.getByLabel(
			'Enable Account Restriction',
			{exact: true}
		);
		this.allowDraftToggle = page.getByRole('switch', {
			name: 'Allow Users to Save Entries as Draft',
		});
		this.allowStandaloneEntriesToggle = page.getByRole('switch', {
			name: 'Allow Standalone Entries',
		});
		this.detailsTabItem = page.getByRole('link', {name: 'Details'});
		this.entryTitleField = page.getByLabel('Entry Title Field', {
			exact: true,
		});
		this.entryTitleFieldCombobox = page.getByRole('combobox', {
			name: 'Entry Title Field',
		});
		this.friendlyURLSeparator = page.getByLabel(
			'Object Entry URL Separator',
			{exact: true}
		);
		this.labelInput = page.getByRole('textbox', {
			exact: true,
			name: 'Label Mandatory',
		});
		this.labelLocalizationButton = page
			.locator('div')
			.filter({hasText: /^LabelMandatory$/})
			.getByLabel('Open Localizations');
		this.nameInput = page.getByRole('textbox', {name: 'Name Mandatory'});
		this.page = page;
		this.panelLinkCombobox = page.getByRole('combobox', {
			name: 'Panel Link',
		});
		this.pluralLabelInput = page.getByRole('textbox', {
			name: 'Plural Label Mandatory',
		});
		this.pluralLabelLocalizationButton = page
			.locator('div')
			.filter({hasText: /^Plural LabelMandatory$/})
			.getByLabel('Open Localizations');
		this.publishButton = page.getByRole('button', {
			exact: true,
			name: 'Publish',
		});
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.scopeCombobox = page.getByRole('combobox', {name: 'Scope'});
		this.showWidgetToggle = page.getByRole('switch', {
			name: 'Show Widget in Page Builder',
		});
		this.viewObjectDefinitionsPage = new ViewObjectDefinitionsPage(page);
	}

	async enableAccountRestriction(fieldName: string) {
		await this.accountRestrictionToggle.check();

		await this.accountRestrictedFieldCombobox.click();
		await this.page.getByRole('option', {name: fieldName}).click();
	}

	async goto(objectDefinitionLabel: string) {
		await this.viewObjectDefinitionsPage.goto();

		await this.viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
			objectDefinitionLabel
		);

		await expect(this.detailsTabItem).toHaveAttribute(
			'aria-current',
			'page'
		);
	}

	async goToDetailsTab() {
		const ariaCurrent =
			await this.detailsTabItem.getAttribute('aria-current');

		if (ariaCurrent !== 'page') {
			await this.detailsTabItem.click();

			await expect(this.detailsTabItem).toHaveAttribute(
				'aria-current',
				'page'
			);
		}

		await this.waitForDetailsFormLoaded();
	}

	async waitForDetailsFormLoaded() {
		await expect(this.saveButton).toBeEnabled();
	}

	async saveObjectDefinition() {
		await this.saveButton.click();
	}

	async selectEntryTitleField(fieldName: string) {
		await this.entryTitleFieldCombobox.click();

		await this.page.getByRole('option', {name: fieldName}).click();
	}

	async selectLabelLanguage(language: string) {
		await this.labelLocalizationButton.click();

		await this.page
			.getByRole('option', {
				exact: false,
				name: `${language} language:`,
			})
			.click();
	}

	async selectPanelLink(optionName: string) {
		await this.panelLinkCombobox.click();

		await this.page.getByRole('option', {name: optionName}).click();
	}

	async selectPluralLabelLanguage(language: string) {
		await this.pluralLabelLocalizationButton.click();

		await this.page
			.getByRole('option', {
				exact: false,
				name: `${language} language:`,
			})
			.click();
	}

	async selectScope(optionName: string) {
		await this.scopeCombobox.click();

		await this.page
			.getByRole('option', {name: optionName})
			.dispatchEvent('click');
	}
}
