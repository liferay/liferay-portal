/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../utils/waitForAlert';

export class LocalizationSelectPage {
	readonly page: Page;

	readonly actionsDropdownTrigger: Locator;
	readonly trigger: Locator;

	constructor(page: Page, localizationSelectIndex: number = 0) {
		this.page = page;

		this.actionsDropdownTrigger = page.getByLabel('Localization Actions');
		this.trigger = page
			.locator(
				'.lfr-layout-structure-item-localization-select button.form-control-select'
			)
			.nth(localizationSelectIndex);
	}

	async autoTranslate(languageId: string) {
		await this.switchLanguage(languageId);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				name: 'Auto Translate',
			}),
			trigger: this.actionsDropdownTrigger,
		});

		const confirmButton = this.page
			.locator('.modal-footer')
			.getByText('Continue');

		await this.page.waitForTimeout(3000);

		if (await confirmButton.isVisible()) {
			await clickAndExpectToBeHidden({
				target: confirmButton,
				trigger: confirmButton,
			});
		}

		await waitForAlert(
			this.page,
			'translation has been successfully received'
		);
	}

	async getLanguageStatus(
		languageId: string
	): Promise<'translated' | 'translating' | 'not-translated' | null> {
		const languageLabel = this.page.locator('.dropdown-item', {
			hasText: languageId,
		});

		await clickAndExpectToBeVisible({
			target: languageLabel,
			trigger: this.trigger,
		});

		const text = await languageLabel.textContent();

		let status = null;

		if (text.includes('Not Translated')) {
			status = 'not-translated';
		}
		else if (text.includes('Translated')) {
			status = 'translated';
		}
		else if (text.includes('Translating')) {
			status = 'translating';
		}

		await clickAndExpectToBeHidden({
			target: languageLabel,
			trigger: this.trigger,
		});

		return status;
	}

	async markAsTranslated(languageId: string) {
		await this.switchLanguage(languageId);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				name: 'Mark as Translated',
			}),
			trigger: this.actionsDropdownTrigger,
		});

		const confirmButton = this.page
			.locator('.modal-footer')
			.getByText('Mark as Translated');

		await confirmButton.waitFor();

		await clickAndExpectToBeHidden({
			target: confirmButton,
			trigger: confirmButton,
		});

		expect(await this.getLanguageStatus(languageId)).toBe('translated');
	}

	async resetTranslation(languageId: string) {
		await this.switchLanguage(languageId);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				name: 'Reset Translation',
			}),
			trigger: this.actionsDropdownTrigger,
		});

		const confirmButton = this.page
			.locator('.modal-footer')
			.getByText('Delete');

		await confirmButton.waitFor();

		await clickAndExpectToBeHidden({
			target: confirmButton,
			trigger: confirmButton,
		});

		expect(await this.getLanguageStatus(languageId)).toBe('not-translated');
	}

	async switchLanguage(languageId: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.locator('.dropdown-item', {hasText: languageId}),
			trigger: this.trigger,
		});

		await expect(this.trigger).toHaveText(languageId);
	}
}
