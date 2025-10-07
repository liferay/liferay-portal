/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {liferayConfig} from '../../../liferay.config';

export const test = mergeTests(loginTest(), usersAndOrganizationsPagesTest);

test(
	'Check that language selector works on private pages',
	{tag: ['@LPD-26175', '@LPS-159181']},
	async ({page, userPersonalSitePage}) => {
		await userPersonalSitePage.goToMyDashboard();

		await userPersonalSitePage.addLanguageSelectorToPage();
		await userPersonalSitePage.switchLanguages(
			'deutsch-Deutschland',
			'Select a Language'
		);

		await expect(page).toHaveURL(new RegExp(`.+/user/.+`));

		await page.goto(
			liferayConfig.environment.baseUrl +
				'/c/portal/update_language?languageId=en_US'
		);
	}
);
