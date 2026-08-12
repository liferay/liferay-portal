/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {PagesAdminPage} from '../../../../pages/layout-admin-web/PagesAdminPage';
import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import fillAndClickOutside from '../../../../utils/fillAndClickOutside';
import {waitForAlert} from '../../../../utils/waitForAlert';

type PageConfigurationSection =
	| 'General'
	| 'Design'
	| 'SEO'
	| 'Open Graph'
	| 'Custom Meta Tags';

export class PageConfigurationPage {
	readonly page: Page;

	readonly canonicalURLCheckbox: Locator;
	readonly customCanonicalURLSettings: Locator;
	readonly friendlyURL: Locator;
	readonly name: Locator;
	readonly pagesAdminPage: PagesAdminPage;
	readonly saveButton: Locator;
	readonly url: Locator;

	constructor(page: Page) {
		this.page = page;

		this.canonicalURLCheckbox = page.getByLabel('Use Custom Canonical URL');
		this.friendlyURL = page.getByLabel('Friendly URL');
		this.customCanonicalURLSettings = page.getByLabel('Canonical URL', {
			exact: true,
		});
		this.name = page.getByLabel('General').getByLabel('Name');
		this.pagesAdminPage = new PagesAdminPage(page);
		this.saveButton = page.getByRole('button', {exact: true, name: 'Save'});
		this.url = page.getByLabel('URL').first();
	}

	async fillName(name: string) {
		await fillAndClickOutside(this.page, this.name, name);
	}

	async fillURL(url: string) {
		await fillAndClickOutside(this.page, this.url, url);
	}

	async goToSection(pageTitle: string, section: PageConfigurationSection) {
		await this.pagesAdminPage.clickOnAction('Configure', pageTitle);

		await this.page
			.locator('.portlet-body li', {has: this.page.getByText(section)})
			.click();
	}

	async save() {
		await this.saveButton.click();

		await waitForAlert(
			this.page,
			'Success:The page was updated successfully.'
		);
	}

	async selectMasterLayout(name: string) {
		await this.page.getByLabel('Change Master').click();

		const iframe = this.page.frameLocator('iframe[title="Select Master"]');

		await clickAndExpectToBeHidden({
			target: this.page.locator('.modal-dialog'),
			trigger: iframe.getByRole('button', {name: `Select ${name}`}),
		});
	}

	async setCanonicalURL(canonicalURL: string) {
		await this.canonicalURLCheckbox.waitFor();

		await this.canonicalURLCheckbox.check();
		await this.customCanonicalURLSettings.fill(canonicalURL);

		await this.save();
	}

	async setFriendlyURL(friendlyURL: string, language: 'spanish' | 'english') {
		const languageIds = {english: 'en_US', spanish: 'es_ES'};

		const selectLanguage = async (name: string) => {
			const toggle = this.page.locator(
				'.form-group.friendly-url .input-localized-trigger'
			);

			const option = this.page.getByRole('menuitem', {name});

			await clickAndExpectToBeVisible({target: option, trigger: toggle});

			const isActive = await option.evaluate((element) =>
				element.classList.contains('active')
			);

			if (isActive) {
				await toggle.click();
			}
			else {
				await option.click();
			}
		};

		await selectLanguage(language);

		await this.friendlyURL.fill(friendlyURL);

		// The localized input debounces copying the typed value into the hidden
		// input of the selected language, so switching back to the default
		// language too early discards it

		await expect(
			this.page.locator(
				`input[name$="friendlyURL_${languageIds[language]}"]`
			)
		).toHaveValue(friendlyURL);

		await selectLanguage('default');

		await this.save();
	}

	async setHTMLTitle(title: string) {
		await this.page.getByLabel('HTML Title').fill(title);

		await this.save();
	}

	async setInputValueAndSave(
		element: Locator,
		layoutTitle: string,
		section: PageConfigurationSection,
		value: string
	) {
		await this.goToSection(layoutTitle, section);

		await element.waitFor();

		await fillAndClickOutside(this.page, element, value);

		await this.save();
	}
}
