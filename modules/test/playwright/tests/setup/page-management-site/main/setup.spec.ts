/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {resolve} from 'path';

import {backendPageTest} from '../../../../fixtures/backendPageTest';
import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {
	PAGE_MANAGEMENT_SITE_ERC,
	PAGE_MANAGEMENT_SITE_NAME,
} from './constants/site';

export const test = mergeTests(backendPageTest);

test('Setup: Create site with required data for Page Management tests', async ({
	backendPage,
}) => {
	const apiHelpers = new ApiHelpers(backendPage);

	const site = await apiHelpers.headlessSite.createSiteFromZip(
		{
			externalReferenceCode: PAGE_MANAGEMENT_SITE_ERC,
			name: PAGE_MANAGEMENT_SITE_NAME,
		},
		resolve(__dirname, 'site-initializer')
	);

	expect(site).toHaveProperty(
		'externalReferenceCode',
		PAGE_MANAGEMENT_SITE_ERC
	);
});
