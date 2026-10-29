/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../../utils/waitForAlert';

export class DepotStagingPage {
	readonly assetPublisher: Locator;
	readonly configurationFrame: FrameLocator;
	readonly documentsAndMedia: Locator;
	readonly page: Page;
	readonly publishToLiveFrame: FrameLocator;
	readonly webContentDisplay: Locator;

	constructor(page: Page) {
		this.page = page;

		this.assetPublisher = page.locator(
			'section[id^="portlet_com_liferay_asset_publisher"]'
		);
		this.documentsAndMedia = page.locator(
			'section[id^="portlet_com_liferay_document_library_web_portlet_DLPortlet"]'
		);
		this.configurationFrame = page.frameLocator(
			'iframe[title*="Configuration"]'
		);
		this.publishToLiveFrame = page.frameLocator(
			'iframe[title="Publish to Live"]'
		);
		this.webContentDisplay = page.locator(
			'section[id^="portlet_com_liferay_journal_content_web_portlet_JournalContentPortlet"]'
		);
	}

	async closeConfiguration() {
		await this.page
			.locator('.modal-header')
			.getByRole('button', {exact: true, name: 'Close'})
			.click();
	}

	async clickConnectedSiteAction(
		depotFriendlyURL: string,
		siteName: string,
		action: string
	) {
		await this.page.goto(
			`/group${depotFriendlyURL}-staging/~/control_panel/manage?p_p_id=com_liferay_depot_web_portlet_DepotSettingsPortlet`
		);

		await this.page.getByRole('link', {exact: true, name: 'Sites'}).click();

		const menuItem = this.page.getByRole('menuitem', {
			exact: true,
			name: action,
		});

		await clickAndExpectToBeVisible({
			target: menuItem,
			trigger: this.page
				.getByRole('row')
				.filter({hasText: siteName})
				.getByRole('button'),
		});

		this.page.once('dialog', (dialog) => dialog.accept());

		await menuItem.click();

		await waitForAlert(this.page);
	}

	async disconnectSite(depotFriendlyURL: string, siteName: string) {
		await this.clickConnectedSiteAction(
			depotFriendlyURL,
			siteName,
			'Disconnect'
		);
	}

	async getStagedFileEntryId(
		depotFriendlyURL: string,
		title: string,
		folderNames: string[] = []
	) {
		if (folderNames.length) {
			const folderLink = await this.waitForStagedEntry(
				depotFriendlyURL,
				folderNames[0]
			);

			await folderLink.click();

			for (const folderName of folderNames.slice(1)) {
				await this.page
					.getByRole('link', {exact: true, name: folderName})
					.first()
					.click();
			}
		}
		else {
			await this.waitForStagedEntry(depotFriendlyURL, title);
		}

		const link = this.page.getByRole('link', {name: title}).first();

		await expect(link).toBeVisible();

		const href = await link.getAttribute('href');

		return Number(href.match(/fileEntryId=(\d+)/)[1]);
	}

	async getStagingGroupId(groupFriendlyURL: string): Promise<number> {
		await this.gotoDocuments(`${groupFriendlyURL}-staging`);

		return this.page.evaluate(() =>
			Number(Liferay.ThemeDisplay.getScopeGroupId())
		);
	}

	async gotoDocuments(groupFriendlyURL: string) {
		await this.page.goto(
			`/group${groupFriendlyURL}/~/control_panel/manage?p_p_id=com_liferay_document_library_web_portlet_DLAdminPortlet`
		);
	}

	async openAssetPublisherConfiguration() {
		await this.openConfiguration(this.assetPublisher);

		await this.configurationFrame
			.getByRole('tab', {name: 'Asset Selection'})
			.click();
	}

	async openConfiguration(portlet: Locator) {
		await portlet.hover();
		await portlet.getByRole('button', {name: 'Options'}).click();
		await this.page
			.locator('.dropdown-menu.show')
			.getByRole('menuitem', {exact: true, name: 'Configuration'})
			.click();
	}

	async publishDepotToLive(depotFriendlyURL: string) {
		await this.gotoDocuments(`${depotFriendlyURL}-staging`);

		await this.page.getByRole('button', {name: 'Staging'}).click();
		await this.page
			.getByRole('menuitem', {name: 'Publish to Live'})
			.click();

		await this.publishToLive();
	}

	async publishSiteToLive(stagedPageURL: string) {
		await this.page.goto(stagedPageURL);

		await this.page.getByRole('button', {name: 'Publish to Live'}).click();

		await this.publishToLive();
	}

	async selectCollection(assetLibraryName: string, collectionName: string) {
		await this.configurationFrame
			.getByRole('radio', {name: 'Collection'})
			.click();

		await waitForAlert(
			this.configurationFrame,
			'Success:You have successfully updated the setup.'
		);

		const selectCollectionFrame = this.configurationFrame.frameLocator(
			'iframe[title="Select Collection"]'
		);

		await clickAndExpectToBeVisible({
			target: selectCollectionFrame.getByRole('link', {
				name: 'Sites and Libraries',
			}),
			trigger: this.configurationFrame.locator(
				'button[aria-label="Select Collection"]'
			),
		});

		await selectCollectionFrame
			.getByRole('link', {name: 'Sites and Libraries'})
			.click();
		await selectCollectionFrame
			.getByText('Asset Library', {exact: true})
			.click();
		await selectCollectionFrame
			.getByText(assetLibraryName, {exact: true})
			.click();
		await selectCollectionFrame
			.getByRole('button', {name: `Select ${collectionName}`})
			.click();

		await expect(
			this.configurationFrame.getByRole('textbox', {name: 'Collection'})
		).toHaveValue(collectionName);

		await this.configurationFrame
			.getByRole('button', {exact: true, name: 'Save'})
			.click();

		await waitForAlert(
			this.configurationFrame,
			'Success:You have successfully updated the setup.'
		);
	}

	async selectManualAsset(assetLibraryName: string, title: string) {
		await this.configurationFrame.getByLabel('Manual').click();

		await waitForAlert(
			this.configurationFrame,
			'Success:You have successfully updated the setup.'
		);

		await this.configurationFrame
			.getByRole('group', {name: 'Scope'})
			.getByRole('button', {name: 'Select'})
			.click();
		await this.configurationFrame
			.getByRole('menuitem', {name: 'Other Site or Asset Library'})
			.click();

		const scopeFrame =
			this.configurationFrame.frameLocator('.modal iframe');

		await scopeFrame.getByText('Asset Libraries', {exact: true}).click();
		await scopeFrame.getByPlaceholder('Search for').fill(assetLibraryName);
		await scopeFrame.getByPlaceholder('Search for').press('Enter');
		await scopeFrame
			.getByRole('link', {exact: true, name: assetLibraryName})
			.click();

		await waitForAlert(
			this.configurationFrame,
			'Success:You have successfully updated the setup.'
		);

		await this.configurationFrame
			.getByRole('button', {name: `Select in ${assetLibraryName}`})
			.click();

		const selectAssetDialog = this.configurationFrame.getByRole('dialog', {
			name: 'Select Asset',
		});

		await selectAssetDialog
			.getByRole('checkbox', {name: `Select ${title}`})
			.check();
		await selectAssetDialog
			.getByRole('button', {exact: true, name: 'Select'})
			.click();

		await waitForAlert(
			this.configurationFrame,
			'Success:You have successfully updated the setup.'
		);
	}

	async selectWebContent(title: string) {
		await this.openConfiguration(this.webContentDisplay);

		await this.configurationFrame
			.getByRole('button', {exact: true, name: 'Select'})
			.click();

		await this.configurationFrame
			.frameLocator('iframe[title="Select Web Content"]')
			.getByText(title, {exact: true})
			.click();

		await expect(
			this.configurationFrame.getByText(title, {exact: true})
		).toBeVisible();

		await this.configurationFrame
			.getByRole('button', {exact: true, name: 'Save'})
			.click();

		await waitForAlert(
			this.configurationFrame,
			'Success:You have successfully updated the setup.'
		);

		await this.closeConfiguration();
	}

	async setDocumentsAndMediaRootFolder(
		assetLibraryName: string,
		folderName: string,
		parentFolderName?: string
	) {
		await this.openConfiguration(this.documentsAndMedia);

		await this.configurationFrame
			.getByRole('group', {name: 'Folders Listing'})
			.getByRole('button', {exact: true, name: 'Select'})
			.click();

		const selectFolderFrame =
			this.configurationFrame.frameLocator('.modal iframe');

		await selectFolderFrame
			.getByRole('link', {name: 'Sites and Libraries'})
			.click();
		await selectFolderFrame
			.getByRole('link', {exact: true, name: 'Asset Library'})
			.click();
		await selectFolderFrame
			.getByRole('link', {exact: true, name: assetLibraryName})
			.click();

		if (parentFolderName) {
			await selectFolderFrame
				.getByRole('link', {exact: true, name: parentFolderName})
				.click();
		}

		await selectFolderFrame
			.getByRole('row', {name: folderName})
			.getByRole('button', {name: 'Select'})
			.click();

		await this.configurationFrame
			.getByRole('button', {exact: true, name: 'Save'})
			.click();

		await waitForAlert(
			this.configurationFrame,
			'Success:You have successfully updated the setup.'
		);

		await this.closeConfiguration();
	}

	async waitForStagedEntry(depotFriendlyURL: string, title: string) {
		await this.gotoDocuments(`${depotFriendlyURL}-staging`);

		const link = this.page.getByRole('link', {name: title}).first();

		await expect(async () => {
			await this.page.reload();

			await expect(link).toBeVisible({timeout: 5000});
		}).toPass({timeout: 120000});

		return link;
	}

	private async publishToLive() {
		await this.publishToLiveFrame
			.getByRole('button', {name: 'Publish to Live'})
			.click();

		await expect(
			this.publishToLiveFrame.getByText('Successful').first()
		).toBeVisible({timeout: 60000});
	}
}
