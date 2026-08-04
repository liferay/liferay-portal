/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {PORTLET_URLS} from '../../../../utils/portletUrls';

export class SpaceSettingsPage {
	readonly apiHelpers: ApiHelpers;
	readonly enableRecycleBinCheckbox: Locator;
	readonly page: Page;
	readonly saveButton: Locator;
	readonly trashEntriesMaxAgeField: Locator;

	constructor(page: Page) {
		this.apiHelpers = new ApiHelpers(page);
		this.enableRecycleBinCheckbox = page.getByRole('checkbox', {
			name: 'Enable Recycle Bin',
		});
		this.page = page;
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.trashEntriesMaxAgeField = page.getByRole('spinbutton', {
			name: 'Trash Entries Max Age',
		});
	}

	async goto(spaceId: number) {
		const {classNameId} =
			await this.apiHelpers.jsonWebServicesClassName.fetchClassName(
				'com.liferay.depot.model.DepotEntry'
			);

		await this.page.goto(
			`${PORTLET_URLS.cmsSpaceSettings}/${classNameId}/${spaceId}`
		);

		await this.enableRecycleBinCheckbox.waitFor();
	}
}
