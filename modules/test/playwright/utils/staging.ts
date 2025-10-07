/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {DataApiHelpers} from '../helpers/ApiHelpers';

export async function enableLocalStaging(
	apiHelpers: DataApiHelpers,
	page: Page,
	site: Site,
	parameters?: {branchingPrivate?: boolean; branchingPublic?: boolean}
) {
	await page.goto(`/web${site.friendlyUrlPath}`);

	await apiHelpers.jsonWebServicesStaging.enableLocalStaging({
		groupId: site.id,
		...parameters,
	});

	await page.reload();
	await expect(page.getByText('An initial staging publish')).toBeHidden();
}
