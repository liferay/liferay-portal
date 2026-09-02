/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {waitForAlert} from '../../../../utils/waitForAlert';

export class DefaultPermissionsPage {
	readonly page: Page;
	readonly permissionsModal: Locator;
	readonly permissionsModalCancelButton: Locator;
	readonly permissionsModalSaveButton: Locator;
	readonly permissionsModalSelectRole: Locator;
	readonly propagateCheckbox: Locator;

	constructor(page: Page) {
		this.page = page;
		this.permissionsModal = page.locator('.modal-dialog');
		this.permissionsModalCancelButton =
			this.permissionsModal.getByTestId('button-cancel');
		this.permissionsModalSaveButton =
			this.permissionsModal.getByTestId('button-save');
		this.permissionsModalSelectRole =
			this.permissionsModal.getByLabel('Select Role');
		this.propagateCheckbox = this.permissionsModal.getByLabel(
			'I understand that these changes will also affect existing entities.'
		);
	}

	async checkPermissionsAndSave(
		permissions: Array<{action: string; role: string}>,
		bulk = false,
		propagate = false
	) {
		await expect(this.permissionsModal).toBeVisible();

		for (const permission of permissions) {
			const checkbox = this.permissionsModal.getByTestId(
				`row-checkbox-${permission.role}_${permission.action}`
			);

			// The permissions table can re-render right after the modal opens,
			// detaching the checkbox mid-click. Retry until the check sticks so
			// the row re-resolves against the latest render.

			await expect(async () => {
				await checkbox.check({timeout: 5000});

				await expect(checkbox).toBeChecked({timeout: 5000});
			}).toPass({timeout: 15000});
		}

		if (propagate) {
			await expect(this.permissionsModalSaveButton).toBeDisabled();

			await this.propagateCheckbox.check();
		}

		await this.permissionsModalSaveButton.click();

		if (bulk) {
			await waitForAlert(
				this.page,
				'Info:Default permissions update action started for',
				{type: 'info'}
			);
		}
		else if (propagate) {
			await waitForAlert(
				this.page,
				'Info:Default permissions update action started for all assets.',
				{type: 'info'}
			);

			await expect(this.permissionsModal).toBeHidden();
		}
		else {
			await waitForAlert(this.page);
		}
	}

	async verifyPermissions(
		permissions: Array<{action: string; checked: boolean; role: string}>
	) {
		await expect(this.permissionsModal).toBeVisible();

		for (const permission of permissions) {
			if (permission.checked) {
				await expect(
					this.permissionsModal.getByTestId(
						`row-checkbox-${permission.role}_${permission.action}`
					)
				).toBeChecked();
			}
			else {
				await expect(
					this.permissionsModal.getByTestId(
						`row-checkbox-${permission.role}_${permission.action}`
					)
				).not.toBeChecked();
			}
		}

		await this.permissionsModalCancelButton.click();
	}
}
