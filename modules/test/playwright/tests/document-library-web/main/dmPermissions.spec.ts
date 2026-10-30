/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	documentLibraryPagesTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'DM Permissions initial status',
	{tag: ['@LPD-39898', '@LPD-39899']},
	async ({documentLibraryEditFilePage, page, site}) => {
		await documentLibraryEditFilePage.goto(site.friendlyUrlPath);

		await documentLibraryEditFilePage.openFieldset('Permissions');

		await expect(
			page.getByText('Viewable and Downloadable By')
		).toBeVisible();
		await expect(
			page.getByRole('columnheader', {exact: true, name: 'Role'})
		).toBeVisible();
	}
);

test(
	'View and Download permissions are linked in DM',
	{tag: '@LPD-39724'},
	async ({documentLibraryEditFilePage, documentLibraryPage, site}) => {
		const title = getRandomString();

		await documentLibraryEditFilePage.publishNewFileWithOwnerViewPermission(
			title,
			site.friendlyUrlPath
		);

		await documentLibraryPage.assertFileEntryPermissions(
			[
				{enabled: false, locator: '#guest_ACTION_DOWNLOAD'},
				{enabled: false, locator: '#guest_ACTION_VIEW'},
				{enabled: false, locator: '#site-member_ACTION_DOWNLOAD'},
				{enabled: false, locator: '#site-member_ACTION_VIEW'},
				{enabled: true, locator: '#owner_ACTION_DOWNLOAD'},
				{enabled: true, locator: '#owner_ACTION_VIEW'},
			],
			title
		);
	}
);
