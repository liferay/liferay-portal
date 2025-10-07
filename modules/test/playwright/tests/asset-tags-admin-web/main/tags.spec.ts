/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {waitForAlert} from '../../../utils/waitForAlert';
import {tagsPagesTest} from './fixtures/tagsAdminPagesTest';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	tagsPagesTest,
	loginTest()
);

test('User can add and edit a tag', async ({
	page,
	site,
	tagsAdminPage,
	tagsEditPage,
}) => {
	await tagsEditPage.add('tag1', site.friendlyUrlPath);

	await waitForAlert(page);

	await tagsAdminPage.gotoEdit('tag1');

	await tagsEditPage.nameInput.fill('tag1_edit');

	await tagsEditPage.saveButton.click();

	await waitForAlert(page);

	await expect(
		page.getByRole('cell', {exact: true, name: 'tag1_edit'})
	).toBeVisible();
});

test('User can delete multiple tags', async ({
	page,
	site,
	tagsAdminPage,
	tagsEditPage,
}) => {
	await tagsEditPage.add('tag1', site.friendlyUrlPath);

	await waitForAlert(page);

	await tagsEditPage.add('tag2', site.friendlyUrlPath);

	await tagsAdminPage.deleteTags(['tag1', 'tag2']);

	await page
		.getByLabel('Delete Tags')
		.getByRole('button', {name: 'Delete'})
		.click();

	await expect(page.getByText('tag1')).not.toBeVisible();

	await expect(page.getByText('tag2')).not.toBeVisible();
});

test('User can Merge tags', async ({
	page,
	site,
	tagsAdminPage,
	tagsEditPage,
}) => {
	await tagsEditPage.add('tag1', site.friendlyUrlPath);

	await tagsAdminPage.newButton.waitFor();

	await tagsAdminPage.mergeTags(['tag1']);

	page.once('dialog', async (dialog) => {
		await test.step('User cannot merge a single tag', async () => {
			expect(dialog.message()).toBe('Please choose at least 2 tags.');

			await dialog.accept();
		});
	});

	await page.getByRole('button', {name: 'Save'}).click();

	await tagsEditPage.add('tag2', site.friendlyUrlPath);

	await tagsAdminPage.mergeTags(['tag1', 'tag2']);

	page.once('dialog', async (dialog) => {
		await dialog.accept();
	});

	await page.getByRole('button', {name: 'Save'}).click();

	await tagsAdminPage.newButton.waitFor();

	await expect(page.getByText('tag2')).not.toBeVisible();
});

test(
	'Accessing via keyboard navigation the tooltip of the back button is Go to Tags when adding or merging Tags',
	{
		tag: '@LPS-177675',
	},
	async ({page, site, tagsAdminPage, tagsEditPage}) => {
		await tagsAdminPage.goto(site.friendlyUrlPath);
		await tagsAdminPage.gotoAdd();

		test.step('Back button has tooltip Go to Tags when adding a new tag', async () => {
			await page.getByRole('button', {name: 'Save'}).waitFor();
			await page.keyboard.press('Tab');
			await page.keyboard.press('Tab');
			await page.keyboard.press('Tab');
			await expect(
				page
					.locator('.lfr-portal-tooltip')
					.filter({hasText: 'Go to Tags'})
			).toBeVisible();
		});

		await page.getByPlaceholder('Name').fill('tag1');

		await page
			.getByRole('button', {
				name: 'Save',
			})
			.click();

		await tagsAdminPage.newButton.waitFor();

		await tagsEditPage.add('tag2', site.friendlyUrlPath);

		await tagsAdminPage.newButton.waitFor();

		await tagsAdminPage.mergeTags(['tag1', 'tag2']);

		test.step('Back button has tooltip Go to Tags when merging Tags', async () => {
			await page.getByText('Other Metadata').waitFor();
			await page.keyboard.press('Tab');
			await page.keyboard.press('Tab');
			await page.keyboard.press('Tab');
			await expect(
				page
					.locator('.lfr-portal-tooltip')
					.filter({hasText: 'Go to Tags'})
			).toBeVisible();
		});
	}
);
