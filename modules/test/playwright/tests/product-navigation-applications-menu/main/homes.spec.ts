/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {checkAccessibility} from '../../../utils/checkAccessibility';

const test = mergeTests(
	featureFlagsTest({
		'LPD-36105': {enabled: true},
	}),
	globalMenuPagesTest,
	loginTest()
);

test(
	'Check accessibility',
	{tag: '@LPD-70107'},
	async ({globalMenuPage, page}) => {
		await globalMenuPage.goToControlPanel();

		await globalMenuPage.openProductMenu('Control Panel');

		await checkAccessibility({page});

		await globalMenuPage.goToApplications();

		await globalMenuPage.openProductMenu('Applications');

		await checkAccessibility({page});
	}
);
