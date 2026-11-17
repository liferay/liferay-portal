/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {journalPagesTest} from './fixtures/journalPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest()
);

const RESERVED_VARIABLES = [
	'Author Email Address',
	'Author ID',
	'Author Job Title',
	'Author Name',
	'Comments',
	'Create Date',
	'Description',
	'Display Date',
	'ID',
	'Modified Date',
	'Small Image URL',
	'Tags',
	'Title',
	'URL Title',
	'Version',
];

test('This is a test for LPS-153976 and LPD-16407. Check Featured image and reserved variables are present', async ({
	journalEditTemplatePage,
	page,
	site,
}) => {
	await journalEditTemplatePage.goto(site.friendlyUrlPath);

	// Featured image is present when we are editing a template.

	await expect(page.getByLabel('Image Source')).toBeAttached();

	// View reserved variables list under Journal section in web content template.

	await journalEditTemplatePage.gotoElements();

	for (const reservedVariable of RESERVED_VARIABLES) {
		await expect(
			page.getByRole('button', {exact: true, name: reservedVariable})
		).toBeVisible();
	}
});

test(
	'The Cacheable checkbox is checked by default and keeps its value after saving',
	{tag: '@LPS-67094'},
	async ({journalEditTemplatePage, page, site}) => {
		await journalEditTemplatePage.goto(site.friendlyUrlPath);

		const cacheableCheckbox = page.getByLabel('Cacheable', {exact: true});

		await expect(cacheableCheckbox).toBeChecked();

		await page
			.getByPlaceholder('Untitled Template')
			.pressSequentially(getRandomString(), {delay: 50});

		await cacheableCheckbox.uncheck();

		await page
			.getByRole('button', {exact: true, name: 'Save and Continue'})
			.click();

		await page.waitForURL(/ddmTemplateId=/);

		const editURL = page.url();

		await journalEditTemplatePage.basicInformation.waitFor();

		await expect(cacheableCheckbox).not.toBeChecked();

		await cacheableCheckbox.check();

		await journalEditTemplatePage.saveTemplate();

		await page.goto(editURL);

		await journalEditTemplatePage.basicInformation.waitFor();

		await expect(cacheableCheckbox).toBeChecked();
	}
);
