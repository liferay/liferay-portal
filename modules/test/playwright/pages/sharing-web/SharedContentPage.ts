/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';

export type TSharedAssetType = 'Blogs Entry' | 'Document';

export type TSharedContentTab = 'Shared by Me' | 'Shared with Me';

export class SharedContentPage {
	readonly assetTypeFrame: FrameLocator;
	readonly filterButton: Locator;
	readonly page: Page;
	readonly table: Locator;

	constructor(page: Page) {
		this.assetTypeFrame = page.frameLocator(
			'iframe[title="Select Asset Type"]'
		);
		this.filterButton = page.getByRole('button', {name: 'Filter'});
		this.page = page;
		this.table = page.getByRole('table');
	}

	entryLink(title: string) {
		return this.entryRow(title).getByRole('link', {
			exact: true,
			name: title,
		});
	}

	entryRow(title: string) {
		return this.table.getByRole('row', {name: title});
	}

	statusLabel(status: string) {
		return this.table.getByText(status, {exact: true});
	}

	async goto(tab: TSharedContentTab, siteUrlKey: string = 'guest') {
		const incoming = tab === 'Shared with Me';

		await this.page.goto(
			`/group/${siteUrlKey}/~/control_panel/manage?p_p_id=com_liferay_sharing_web_portlet_SharedAssetsPortlet&p_p_state=maximized&p_p_mode=view&_com_liferay_sharing_web_portlet_SharedAssetsPortlet_incoming=${incoming}`
		);
	}

	async filterByAssetType(
		assetType: TSharedAssetType,
		{restore = false}: {restore?: boolean} = {}
	) {
		if (restore) {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.page.getByRole('menuitem', {name: 'All'}),
				trigger: this.filterButton,
			});
		}

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Asset Types'}),
			trigger: this.filterButton,
		});

		await this.assetTypeFrame
			.getByRole('button', {name: `Select ${assetType}`})
			.click();
	}

	async openRowAction(title: string, action: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: action,
			}),
			trigger: this.entryRow(title).getByRole('button', {
				name: 'Actions',
			}),
		});
	}
}
