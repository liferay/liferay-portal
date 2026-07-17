/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {liferayConfig} from '../../../../liferay.config';
import POM from '../../../../utils/POM';
import {waitForAlert} from '../../../../utils/waitForAlert';

const PORTLET_NAME =
	'com_liferay_design_library_web_internal_portlet_DesignLibraryAdminPortlet';

const PORTLET_URL =
	`${liferayConfig.environment.baseUrl}/group/guest/~/control_panel/manage` +
	`?p_p_id=${PORTLET_NAME}`;

export class DesignLibrariesPage extends POM {
	readonly portletName = PORTLET_NAME;
	readonly emptyStateContainer: Locator;

	constructor(page: Page) {
		super(page, PORTLET_URL);

		this.emptyStateContainer = page.locator('.fds .c-empty-state');
	}

	override async waitFor() {
		await this.page
			.locator('.data-set-content-wrapper')
			.waitFor({state: 'visible'});
	}

	async create({
		description = '',
		name,
	}: {
		description?: string;
		name: string;
	}) {
		const creationModal = this.page.getByRole('dialog');

		await this.page
			.getByRole('button', {name: 'New Design Library'})
			.or(this.page.getByRole('button', {name: 'New'}))
			.click();

		await expect(creationModal).toBeVisible();

		await creationModal.getByLabel('Name').fill(name);

		await creationModal.getByLabel('Description').fill(description);

		const saveButton = creationModal.getByRole('button', {name: 'Save'});

		if (await saveButton.isEnabled()) {
			await saveButton.click();
		}
	}

	async createStyleBook(
		designLibraryName: string,
		styleBookName: string,
		baseThemeName?: string
	) {
		await this.goToDesignLibrary(designLibraryName);

		await this.page.getByRole('button', {name: 'New Style Book'}).click();

		const modal = this.page.getByRole('dialog');

		await expect(modal).toBeVisible();

		if (baseThemeName) {
			await modal.getByLabel('Create Style Book For').click();

			const option = this.page.getByRole('option', {
				name: baseThemeName,
			});

			if ((await option.getAttribute('aria-selected')) === 'true') {
				await this.page.keyboard.press('Escape');
			}
			else {
				await option.click();
			}
		}

		await modal.getByLabel('Name').fill(styleBookName);

		await modal.getByRole('button', {name: 'Save'}).click();

		await expect(modal).toBeHidden();
		await expect(this.page).toHaveURL(/style_book.+edit/);

		await this.goToDesignLibrary(designLibraryName);
	}

	async delete(name: string) {
		await this.page
			.locator('table.table tbody tr', {hasText: name})
			.first()
			.getByRole('button', {name: 'Actions'})
			.click();

		await this.page
			.getByRole('menuitem', {
				exact: true,
				name: 'Delete',
			})
			.click();

		await this.page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(this.page, `${name} was successfully deleted.`);
	}

	async goToDesignLibrary(designLibraryName: string) {
		await this.goto();

		const designLibraryLink = this.page.getByRole('link', {
			name: designLibraryName,
		});

		await expect(designLibraryLink).toBeVisible();

		await designLibraryLink.click();
	}
}
