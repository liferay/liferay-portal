/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';

export type TSharingPermission = 'Comment' | 'Update' | 'View';

export class SharePage {
	readonly allowSharingCheckbox: Locator;
	readonly cancelButton: Locator;
	readonly frame: FrameLocator;
	readonly inviteInput: Locator;
	readonly page: Page;
	readonly shareButton: Locator;

	constructor(page: Page) {
		this.frame = page.frameLocator('iframe[title^="Share "]');
		this.allowSharingCheckbox = this.frame.getByRole('checkbox', {
			name: 'Allow the item to be shared with other users.',
		});
		this.cancelButton = this.frame.getByRole('button', {name: 'Cancel'});
		this.inviteInput = this.frame.getByRole('combobox', {
			name: 'Enter name or email address.',
		});
		this.page = page;
		this.shareButton = this.frame.getByRole('button', {
			exact: true,
			name: 'Share',
		});
	}

	collaboratorTag(userNameOrEmail: string) {
		return this.frame.getByRole('gridcell', {
			exact: true,
			name: userNameOrEmail,
		});
	}

	feedbackItem(text: string) {
		return this.frame.getByText(text);
	}

	permissionRadio(permission: TSharingPermission) {
		return this.frame.getByRole('radio', {
			name: new RegExp(`^${permission} `),
		});
	}

	async addCollaborator(userEmailAddress: string) {
		await this.inviteInput.fill(userEmailAddress);
		await this.inviteInput.press(',');

		await this.collaboratorTag(userEmailAddress).waitFor();
	}

	async setAllowSharing(allowSharing: boolean) {
		await this.allowSharingCheckbox.setChecked(allowSharing);
	}

	async setPermission(permission: TSharingPermission) {
		await this.permissionRadio(permission).check();
	}

	async share(
		userEmailAddress: string,
		{
			allowSharing = false,
			permission = 'View',
			waitForSuccess = true,
		}: {
			allowSharing?: boolean;
			permission?: TSharingPermission;
			waitForSuccess?: boolean;
		} = {}
	) {
		await this.addCollaborator(userEmailAddress);

		await this.setAllowSharing(allowSharing);

		await this.setPermission(permission);

		await this.shareButton.click();

		if (waitForSuccess) {
			await waitForAlert(this.page, 'The item was shared successfully.');
		}
	}
}
