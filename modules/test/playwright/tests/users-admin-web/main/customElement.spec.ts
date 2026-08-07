/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {liferayConfig} from '../../../liferay.config';
import {getRandomInt} from '../../../utils/getRandomInt';
import {editCustomElementPageTest} from '../../client-extension-web/main/fixtures/editCustomElementPageTest';
import {WaitAction} from '../../client-extension-web/pages/EditClientExtensionsPage';
import {componentsPageTest} from '../../configuration-admin-web/main/fixtures/ComponentsPageTest';

export const test = mergeTests(
	componentsPageTest,
	editCustomElementPageTest,
	globalMenuPagesTest,
	loginTest()
);

test('LPD-39537 - Check that the name field of custom elements does not allow stored XSS injections', async ({
	componentsPage,
	editCustomElementPage,
	globalMenuPage,
	page,
}) => {
	const NAME = `<svg onload=alert(XSS injection ${getRandomInt()})>`;

	await editCustomElementPage.goto();

	await editCustomElementPage.nameInput.fill(NAME);
	await editCustomElementPage.htmlElementNameInput.fill('test-element');
	await editCustomElementPage.javaScriptURLInput.fill(
		liferayConfig.environment.baseUrl
	);

	await editCustomElementPage.publish(WaitAction.SUCCESS);

	await globalMenuPage.goToControlPanel('Components');

	await expect(componentsPage.helpLink).toBeVisible();

	page.on('dialog', async () => {
		throw new Error('XSS detected');
	});

	await page.reload();

	await expect(page.getByText(NAME)).toBeVisible();
});
