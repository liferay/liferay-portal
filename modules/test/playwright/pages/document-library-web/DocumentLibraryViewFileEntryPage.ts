/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

export class DocumentLibraryViewFileEntryPage {
	readonly infoButton: Locator;
	readonly infoPanel: Locator;
	readonly infoPanelButton: Locator;
	readonly infoPanelTab: Locator;
	readonly manageCollaboratorsButton: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.infoButton = page.getByRole('button', {exact: true, name: 'Info'});
		this.infoPanel = page.locator(
			'[id="_com_liferay_document_library_web_portlet_DLAdminPortlet_ContextualSidebar"]'
		);
		this.infoPanelButton = page.locator(
			'[id="_com_liferay_document_library_web_portlet_DLAdminPortlet_OpenContextualSidebar"]'
		);
		this.infoPanelTab = page.locator(
			'[id^=_com_liferay_document_library_web_portlet_DLAdminPortlet_tabs_]'
		);
		this.manageCollaboratorsButton = page.getByRole('button', {
			name: 'Manage Collaborators',
		});
		this.page = page;
	}

	collaboratorAvatar(userFullName: string) {
		return this.page.locator(
			`.lfr-portal-tooltip[data-title="${userFullName}"]`
		);
	}

	async openInfoTab() {
		await this.infoButton.click();

		await expect(
			this.page.getByRole('tab', {name: 'Details'})
		).toBeVisible();
	}

	async openManageCollaborators() {
		await this.openInfoTab();

		await this.manageCollaboratorsButton.click();
	}

	async openInfoPanel(entryTitle: string, tabName: 'Details' | 'Versions') {
		const infoPanelHeading = this.infoPanel.getByRole('heading', {
			name: entryTitle,
		});

		if (await infoPanelHeading.isHidden()) {
			this.infoPanelButton.click();
		}

		await infoPanelHeading.waitFor();

		const infoPanelTab = this.page.getByRole('tab', {name: tabName});

		if (
			await infoPanelTab.evaluate(
				(element) => !element.classList.contains('active')
			)
		) {
			await infoPanelTab.click();
		}
	}

	async assertInfoPanelCategories(categoryNames: string[]) {
		await expect(
			this.page.getByRole('tab', {name: 'Details'})
		).toBeVisible();
		await expect(this.infoPanelTab.getByText('Categories')).toBeVisible();

		for (const categoryName of categoryNames) {
			await expect(this.infoPanelTab.getByText(categoryName)).toBeVisible;
		}
	}

	async assertInfoPanelRelatedAssets(relatedAssetNames: string[]) {
		await expect(
			this.page.getByRole('tab', {name: 'Details'})
		).toBeVisible();
		await expect(
			this.infoPanelTab.getByText('Related Assets')
		).toBeVisible();

		for (const relatedAssetName of relatedAssetNames) {
			await expect(this.infoPanelTab.getByText(relatedAssetName))
				.toBeVisible;
		}
	}

	async assertInfoPanelTags(tags: string[]) {
		await expect(
			this.page.getByRole('tab', {name: 'Details'})
		).toBeVisible();
		await expect(this.infoPanelTab.getByText('Tags')).toBeVisible();

		for (const tag of tags) {
			await expect(this.infoPanelTab.getByText(tag)).toBeVisible;
		}
	}
}
