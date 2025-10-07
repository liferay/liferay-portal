/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {deleteItems} from './utils/deleteItems';

export const test = mergeTests(loginTest(), formsPagesTest);

test.afterEach(async ({formsPage}) => {
	await formsPage.goTo();

	await deleteItems(formsPage);
});

test.describe('Data engine debounce', () => {
	test('waits for the user to stop typying for a while to properly call validations', async ({
		formBuilderPage,
		formBuilderSidePanelPage,
		page,
	}) => {
		await formBuilderPage.goToNew();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderSidePanelPage.addFieldByDoubleClick('Numeric');

		await formBuilderSidePanelPage.advancedTab.click();

		await formBuilderSidePanelPage.requireConfirmationToggleSwitch.click();

		await expect(page.getByLabel('Confirm Numeric')).toBeVisible();

		const newTabPage = await formBuilderPage.openPreviewForm();

		const confirmationField = newTabPage.getByLabel('Confirm Numeric');

		await confirmationField.click();

		for (let index = 0; index < 15; index++) {
			await newTabPage.keyboard.press('2', {delay: 300});
		}

		await expect(confirmationField).toHaveValue('222222222222222');
	});
});
