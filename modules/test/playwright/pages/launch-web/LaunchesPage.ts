/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeHidden} from '../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForPageToBeLoaded} from '../../utils/waitForPageToBeLoaded';

export type LaunchEntryManagerLabel = 'Page' | 'Web Content';

export class LaunchesPage {
	readonly page: Page;

	readonly descriptionInput: Locator;
	readonly entriesTable: Locator;
	readonly heading: Locator;
	readonly nameInput: Locator;
	readonly newEntryButton: Locator;
	readonly newLaunchButton: Locator;
	readonly previewButton: Locator;
	readonly publishButton: Locator;
	readonly publishedButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.descriptionInput = page.locator('#launchDescription');
		this.entriesTable = page.getByRole('table');
		this.heading = page.locator('.control-menu-level-1-heading');
		this.nameInput = page.locator('#launchName');

		this.newEntryButton = page
			.getByTestId('fdsCreationActionButton')
			.first();
		this.newLaunchButton = page
			.getByRole('button', {name: 'New Launch'})
			.first();
		this.previewButton = page.getByRole('button', {
			name: 'Preview This Launch',
		});
		this.publishButton = page.getByRole('button', {
			exact: true,
			name: 'Publish',
		});
		this.publishedButton = page.getByRole('button', {
			exact: true,
			name: 'Published',
		});
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.launches}`
		);
	}

	async openEntrySelector(
		typeLabel: LaunchEntryManagerLabel
	): Promise<FrameLocator> {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: typeLabel,
			}),
			trigger: this.newEntryButton,
		});

		const iframe = this.page.frameLocator(`iframe[title="${typeLabel}"]`);

		await iframe.getByRole('main').waitFor();

		return iframe;
	}

	async addPageEntry(siteName: string, pageName: string) {
		const iframe = await this.openEntrySelector('Page');

		await this.selectScope(iframe, siteName);

		await this.selectEntry(
			iframe.getByText(pageName, {exact: true}).first(),
			pageName
		);
	}

	async addWebContentEntry(siteName: string, title: string) {
		const iframe = await this.openEntrySelector('Web Content');

		await this.selectScope(iframe, siteName);

		const webContentTab = iframe.getByRole('menuitem', {
			exact: true,
			name: 'Web Content',
		});

		if (await webContentTab.isVisible()) {
			await webContentTab.click();
		}

		await this.selectEntry(
			iframe.getByText(title, {exact: true}).first(),
			title
		);
	}

	async createLaunch(name: string, description: string) {
		await this.newLaunchButton.click();

		const dialog = this.page.getByRole('dialog');

		await dialog.getByLabel('Name').fill(name);
		await dialog.getByLabel('Description').fill(description);

		await dialog.getByRole('button', {name: 'Save'}).click();

		await expect(this.heading).toHaveText(name);

		await waitForPageToBeLoaded(this.page);
	}

	getEntryRow(title: string): Locator {
		return this.entriesTable.getByRole('row').filter({hasText: title});
	}

	async goToDetails() {
		await this.goToScreen('Details');

		await expect(this.nameInput).toBeVisible();
	}

	async goToEntries() {
		await this.goToScreen('Entries');

		await expect(this.newEntryButton).toBeVisible();
	}

	async goToScreen(name: 'Details' | 'Entries') {
		await this.page
			.locator('.launch-screen-navigation')
			.getByRole('link', {exact: true, name})
			.click();

		await waitForPageToBeLoaded(this.page);
	}

	async openLaunch(name: string) {
		await this.page.getByRole('link', {exact: true, name}).click();

		await expect(this.heading).toHaveText(name);

		await waitForPageToBeLoaded(this.page);
	}

	async previewOnPage(pageName: string): Promise<Page> {
		await this.previewButton.click();

		const iframe = this.page.frameLocator(
			'iframe[title="Select Page to Preview"]'
		);

		const pageNode = iframe.getByText(pageName, {exact: true});

		await pageNode.waitFor();

		const popupPromise = this.page.waitForEvent('popup');

		await pageNode.click();

		const previewPage = await popupPromise;

		await previewPage.waitForLoadState();

		return previewPage;
	}

	async publishLaunch() {
		this.page.once('dialog', (dialog) => dialog.accept());

		await this.publishButton.click();

		await expect(this.publishedButton).toBeDisabled();

		await waitForPageToBeLoaded(this.page);
	}

	async selectEntryVersion(
		title: string,
		baseVersion: string,
		version: string
	) {
		await this.getEntryRow(title)
			.getByRole('link', {exact: true, name: baseVersion})
			.click();

		const dialog = this.page
			.getByRole('dialog')
			.filter({hasText: 'Select Version'});

		await dialog
			.getByRole('row')
			.filter({
				has: this.page.getByRole('cell', {exact: true, name: version}),
			})
			.getByRole('radio')
			.check();

		const rebasedResponsePromise = this.page.waitForResponse(
			(response) =>
				response.request().method() === 'PATCH' &&
				response.url().includes('/o/launch-entries/')
		);
		const reloadedResponsePromise = this.page.waitForResponse(
			(response) =>
				response.request().method() === 'GET' &&
				response.url().includes('/o/launch-entries?')
		);

		await clickAndExpectToBeHidden({
			target: dialog,
			trigger: dialog.getByRole('button', {exact: true, name: 'Add'}),
		});

		await rebasedResponsePromise;
		await reloadedResponsePromise;
	}

	private async selectScope(iframe: FrameLocator, siteName: string) {
		const scopeTab = iframe
			.locator('.nav-link')
			.filter({hasText: siteName});

		if (await scopeTab.isVisible()) {
			await scopeTab.click();

			await expect(scopeTab).toHaveClass(/active/);
		}
	}

	private async selectEntry(entry: Locator, title: string) {
		await entry.waitFor();

		await clickAndExpectToBeHidden({
			target: this.page.locator('.modal-dialog'),
			trigger: entry,
		});

		await expect(
			this.getEntryRow(title).getByRole('link', {
				exact: true,
				name: title,
			})
		).toBeVisible();
	}
}
