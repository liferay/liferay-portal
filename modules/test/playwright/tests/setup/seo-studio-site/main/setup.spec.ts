/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {backendPageTest} from '../../../../fixtures/backendPageTest';
import {ApiHelpers} from '../../../../helpers/ApiHelpers';

export const test = mergeTests(backendPageTest);

test.skip('Setup: Verify the SEO Studio site exists', async ({backendPage}) => {
	await backendPage.goto('/');

	const apiHelpers = new ApiHelpers(backendPage);

	const site = await apiHelpers.headlessAdminSite.getSite('L_SEO_STUDIO');

	expect(site).toHaveProperty('externalReferenceCode', 'L_SEO_STUDIO');
});
