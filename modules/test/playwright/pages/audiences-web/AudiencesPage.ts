/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {DataApiHelpers} from '../../helpers/ApiHelpers';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';

export class AudiencesPage {
	readonly apiHelpers: DataApiHelpers;
	readonly externalReferenceCodeErrorMessage: Locator;
	readonly externalReferenceCodeInput: Locator;
	readonly generalSettingsButton: Locator;
	readonly nameInput: Locator;
	readonly newAudienceButton: Locator;
	readonly page: Page;
	readonly ruleDropZone: Locator;
	readonly saveButton: Locator;
	readonly valueInput: Locator;

	constructor(page: Page, apiHelpers: DataApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.externalReferenceCodeErrorMessage = page.locator(
			'.audience-builder-general-settings .form-feedback-item'
		);
		this.externalReferenceCodeInput = page.getByRole('textbox', {
			name: 'ERC',
		});
		this.generalSettingsButton = page.getByRole('button', {
			name: 'General Settings',
		});
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
		externalReferenceCode,
		name,
		operator,
		value,
		valueType = 'text',
	}: {
		attributeName: string;
		externalReferenceCode?: string;
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

		await this.setValue({value, valueType});

		if (externalReferenceCode) {
			await this.fillExternalReferenceCode(externalReferenceCode);
		}

		await this.saveButton.click();

		await waitForAlert(this.page);

		// Register the audience so the data fixture deletes it on teardown,
		// even when the test fails before reaching any manual cleanup

		const editItem = this.page.getByRole('menuitem', {name: 'Edit'});

		await clickAndExpectToBeVisible({
			target: editItem,
			trigger: this.page
				.locator('tr', {hasText: name})
				.locator('button.dropdown-toggle'),
		});

		const editHref = await editItem.getAttribute('href');

		await this.page.keyboard.press('Escape');

		this.apiHelpers.data.push({
			id: Number(editHref?.match(/audiencesEntryId=(\d+)/)?.[1]),
			type: 'audiencesEntry',
		});
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

	async fillExternalReferenceCode(externalReferenceCode: string) {
		await this.generalSettingsButton.click();

		await this.externalReferenceCodeInput.fill(externalReferenceCode);
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

	async setValue({
		value,
		valueType,
	}: {
		value: string;
		valueType: 'select' | 'text';
	}) {
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
	}

	async updateAudience({
		name,
		value,
		valueType = 'text',
	}: {
		name: string;
		value: string;
		valueType?: 'select' | 'text';
	}) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Edit'}),
			trigger: this.page
				.locator('tr', {hasText: name})
				.locator('button.dropdown-toggle'),
		});

		await this.setValue({value, valueType});

		await this.saveButton.click();

		await waitForAlert(this.page);
	}
}
