/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	pagesAdminPagesTest
);

test(
	'Blocks a duplicate delete submit and completes the first one',
	{tag: '@LPD-98622'},
	async ({apiHelpers, page, pagesAdminPage, site}) => {
		const title = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title,
		});

		await pagesAdminPage.goto(site.friendlyUrlPath);

		let requests = 0;

		page.on('request', (request) => {
			if (request.url().includes('delete_layout')) {
				requests++;
			}
		});

		await page.route('**/delete_layout**', async (route) => {
			await new Promise((resolve) => setTimeout(resolve, 3000));

			await route.continue();
		});

		await pagesAdminPage.openDeletePageModal(title);

		const deleteButton = page
			.locator('.modal-footer')
			.getByRole('button', {name: 'Delete'});

		await deleteButton.click();

		await expect(deleteButton).toBeDisabled();

		await deleteButton.click({force: true});

		await waitForAlert(page);

		await expect(deleteButton).toBeHidden();

		await expect(page.getByText(title)).toBeHidden();

		expect(requests).toBe(1);
	}
);
