/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {cmsPagesTest} from '../../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {SITE_CMS_SPACE_NAME} from '../constants/space';

export const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

test('Teardown: Delete space for Site CMS tests', async ({backendPage}) => {
	const apiHelpers = new ApiHelpers(backendPage);

	const spaces =
		await apiHelpers.headlessAssetLibrary.getAssetLibrariesPage(
			`type eq 'Space'`
		);

	const space = spaces.find(({name}) => name === SITE_CMS_SPACE_NAME);

	if (space) {
		await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
			space.externalReferenceCode
		);
	}
});
