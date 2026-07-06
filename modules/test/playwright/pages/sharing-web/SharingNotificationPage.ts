/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

export type TSharingNotificationPermission =
	| 'commenting'
	| 'updating'
	| 'viewing';

export class SharingNotificationPage {
	readonly deletedEntryContent: Locator;
	readonly deletedEntryTitle: Locator;
	readonly notificationBadge: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.deletedEntryContent = page.getByText(
			'Notification for Sharing was deleted.'
		);
		this.deletedEntryTitle = page.getByText(
			'Notification no longer applies.'
		);
		this.notificationBadge = page.getByRole('link', {
			name: /New Notification/,
		});
		this.page = page;
	}

	sharedContentNotification(
		ownerName: string,
		contentTitle: string,
		permission: TSharingNotificationPermission
	) {
		return this.page.getByRole('link', {
			name: `${ownerName} has shared ${contentTitle} with you for ${permission}.`,
		});
	}

	async assertBadgeCount(count: number) {
		await expect(
			this.page.getByRole('link', {
				name: new RegExp(`^${count} New Notification`),
			})
		).toBeVisible();
	}

	async goToNotifications() {
		await this.notificationBadge.click();
	}
}
