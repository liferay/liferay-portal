/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';

const test = mergeTests(loginTest());

test(
	'Check error message disappears',
	{tag: ['@LPD-65813']},
	async ({page}) => {
		const portletId =
			'com_liferay_client_extension_web_internal_portlet_ClientExtensionAdminPortlet';

		await page.goto(
			`/group/control_panel/manage?p_p_id=${portletId}` +
				`&_${portletId}_mvcRenderCommandName=%2Fclient_extension_admin%2Fedit_client_extension_entry` +
				`&_${portletId}_type=customElement`
		);

		await page
			.getByRole('button', {name: 'Publish'})
			.waitFor({state: 'visible'});

		await page.locator(`[id=_${portletId}_name]`).fill('X');

		await page.locator(`[id=_${portletId}_htmlElementName]`).fill('X');

		await page.locator(`[id=_${portletId}_urls]`).fill('X');

		await page.getByRole('button', {name: 'Publish'}).click();

		const errorMessage = page.getByText(
			'Error:Your request failed to complete.'
		);

		await errorMessage.waitFor({state: 'visible'});

		await page.locator('#ToastAlertContainer').getByLabel('Close').click();

		await expect(errorMessage).not.toBeVisible();
	}
);
