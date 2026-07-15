/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';

const SAMPLE_IMAGE = path.join(
	__dirname,
	'../../frontend-js-item-selector-web/main/dependencies/sample_image.png'
);

const test = mergeTests(
	apiHelpersTest,
	blogsPagesTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'Blog Images tab has no scope filter while Documents and Media does',
	{tag: '@LPS-119709'},
	async ({apiHelpers, blogsEditBlogEntryPage, page, site}) => {
		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(SAMPLE_IMAGE),
			{
				documentFolderId: 0,
				fileName: `${getRandomString()}.png`,
				title: 'Document 1',
			}
		);

		// Open the blog cover image selector, which lands on the Blog Images tab

		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		await page.getByRole('button', {name: 'Select File'}).first().click();

		const iframe = page.frameLocator('iframe[title="Select File"]');

		const filter = iframe.getByRole('button', {
			exact: true,
			name: 'Filter',
		});

		// The Blog Images tab does not offer a scope filter

		await expect(iframe.getByText('Sites and Libraries')).toBeHidden();

		await expect(filter).toBeHidden();

		// The Documents and Media tab does offer the scope filter

		await iframe.getByRole('link', {name: 'Documents and Media'}).click();

		await expect(filter).toBeVisible();
	}
);
