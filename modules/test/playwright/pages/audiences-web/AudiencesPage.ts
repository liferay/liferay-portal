/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';

export class AudiencesPage {
	readonly nameInput: Locator;
	readonly newAudienceButton: Locator;
	readonly page: Page;
	readonly ruleDropZone: Locator;
	readonly saveButton: Locator;
	readonly valueInput: Locator;

	constructor(page: Page) {
		this.nameInput = page.getByPlaceholder('New Audience');
		this.newAudienceButton = page.getByLabel('New', {exact: true});
		this.page = page;
		this.ruleDropZone = page.locator('.audience-builder-drop-zone');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.valueInput = page.getByLabel('Value');
	}

	async addCondition(attributeIndex: number) {
		const attribute = this.page
			.getByRole('menuitem', {name: /^Add /})
			.nth(attributeIndex);

		const ruleCount = await this.page
			.locator('.audience-builder-rule')
			.count();

		// The first attribute drops immediately; later ones pick up then drop.

		await attribute.press('Enter');

		if (ruleCount) {
			await expect(attribute).toHaveClass(
				/audience-builder-attribute--dragging/
			);

			await attribute.press('Enter');
		}

		await expect(this.page.locator('.audience-builder-rule')).toHaveCount(
			ruleCount + 1
		);
	}

	async createAudience({
		attributeName,
		name,
		operator,
		value,
		valueType = 'text',
	}: {
		attributeName: string;
		name: string;
		operator?: string;
		value: string;
		valueType?: 'select' | 'text';
	}) {
		await this.newAudienceButton.click();

		await this.nameInput.fill(name);

		await expect(async () => {
			await this.page
				.locator('.audience-builder-attribute', {
					hasText: attributeName,
				})
				.dragTo(this.ruleDropZone);

			await expect(
				this.page.locator('.audience-builder-rule')
			).toBeVisible({timeout: 2000});
		}).toPass();

		if (operator) {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.page.getByRole('option', {name: operator}),
				trigger: this.page.getByLabel('Operator'),
			});
		}

		if (valueType === 'select') {
			const selectedValue = await this.valueInput.textContent();

			if (!selectedValue?.includes(value)) {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: this.page.getByRole('option', {name: value}),
					trigger: this.valueInput,
				});
			}
		}
		else {
			await this.valueInput.fill(value);
		}

		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async deleteAudience(name: string) {
		this.page.once('dialog', (dialog) => dialog.accept());

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Delete'}),
			trigger: this.page
				.locator('tr', {hasText: name})
				.locator('button.dropdown-toggle'),
		});

		await waitForAlert(this.page);
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.audiences);
	}

	async openNewAudience() {
		await this.goto();

		await this.newAudienceButton.click();

		await expect(this.page.getByText('No Criteria Yet')).toBeVisible();

		await expect(
			this.page
				.locator('.c-empty-state', {hasText: 'No Criteria Yet'})
				.locator('img')
				.first()
		).toBeVisible();
	}
}
