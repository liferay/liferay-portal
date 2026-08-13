/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {backendPageTest} from '../../../../fixtures/backendPageTest';
import {ApiHelpers} from '../../../../helpers/ApiHelpers';

export const test = mergeTests(backendPageTest);

test.skip('Teardown: Delete the SEO Studio site and disable its feature flag', async ({
	backendPage,
}) => {
	await backendPage.goto('/');

	const apiHelpers = new ApiHelpers(backendPage);

	const {id: siteId} =
		await apiHelpers.headlessAdminSite.getSite('L_SEO_STUDIO');

	if (siteId) {
		await apiHelpers.headlessAdminSite.deleteSite('L_SEO_STUDIO');
	}

	await apiHelpers.featureFlag.updateFeatureFlag('LPD-44511', false);
});
