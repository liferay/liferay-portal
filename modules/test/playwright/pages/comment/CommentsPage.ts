/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';

export class CommentsPage {
	readonly addCommentButton: Locator;
	readonly addCommentEditor: FrameLocator;
	readonly page: Page;

	constructor(page: Page) {
		this.addCommentButton = page
			.locator('.add-comment')
			.getByRole('button', {name: 'Reply'});
		this.addCommentEditor = page.frameLocator('iframe[title="editor"]');
		this.page = page;
	}

	async addComment(comment: string) {
		await this.addCommentEditor.first().locator('body').click();

		await this.addCommentEditor
			.first()
			.locator('body')
			.pressSequentially(comment);

		await this.addCommentButton.click();

		await waitForAlert(this.page, 'Your request completed successfully.');
	}

	async assertComment(comment: string, userFullName: string, count: number) {
		await this.assertCommentCount(count);

		await expect(this.page.getByText(comment, {exact: true})).toBeVisible();

		await expect(
			this.page
				.getByRole('link', {name: new RegExp(`^${userFullName}`)})
				.first()
		).toBeVisible();
	}

	async assertCommentCount(count: number) {
		await expect(
			this.page.getByText(
				count === 1 ? '1 Comment' : `${count} Comments`,
				{
					exact: true,
				}
			)
		).toBeVisible();
	}
}
