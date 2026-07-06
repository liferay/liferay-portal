/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';

export type TCollaboratorPermission = 'Can Comment' | 'Can Update' | 'Can View';

export class ManageCollaboratorsPage {
	readonly cancelButton: Locator;
	readonly emptyState: Locator;
	readonly frame: FrameLocator;
	readonly page: Page;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.frame = page.frameLocator('iframe[title="Manage Collaborators"]');
		this.cancelButton = this.frame.getByRole('button', {name: 'Cancel'});
		this.emptyState = this.frame.getByText('No Collaborators');
		this.page = page;
		this.saveButton = this.frame.getByRole('button', {name: 'Save'});
	}

	collaboratorRow(userName: string) {
		return this.frame.locator('li[data-collaboratorid]', {
			hasText: userName,
		});
	}

	async changePermission(
		userName: string,
		permission: TCollaboratorPermission
	) {
		await this.collaboratorRow(userName)
			.getByRole('combobox')
			.selectOption(permission);

		await this.save();
	}

	async removeCollaborators(userNames: string[]) {
		for (const userName of userNames) {
			await this.collaboratorRow(userName).getByRole('button').click();
		}
	}

	async save() {
		await this.saveButton.click();

		await waitForAlert(this.page, 'Permissions Changed');
	}
}
