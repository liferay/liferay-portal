/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {passwordPoliciesAdminPageTest} from '../../../fixtures/passwordPoliciesAdminConfigPageTest';
import {checkAccessibility} from '../../../utils/checkAccessibility';

const test = mergeTests(loginTest(), passwordPoliciesAdminPageTest);

test(
	'The validation message of a required field is announced to screen readers',
	{tag: '@LPD-102582'},
	async ({page, passwordPoliciesAdminConfigPage}) => {
		await passwordPoliciesAdminConfigPage.goTo();

		await passwordPoliciesAdminConfigPage.newButton.click();

		await passwordPoliciesAdminConfigPage.name.click();

		await page.locator('body').click();

		const validationMessage = page.locator('.form-validator-stack');

		await expect(validationMessage).toContainText(
			'The Name field is required.'
		);

		await expect(passwordPoliciesAdminConfigPage.name).toHaveAttribute(
			'aria-errormessage',
			(await validationMessage.getAttribute('id')) as string
		);

		await expect(validationMessage).toHaveAttribute('role', 'alert');

		await checkAccessibility({page, soft: false});
	}
);
