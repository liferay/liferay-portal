/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {deleteItems} from './utils/deleteItems';

let formPreviewPage: Page;

const test = mergeTests(dataApiHelpersTest, loginTest(), formsPagesTest);

test.afterEach(async ({formsPage}) => {
	if (formPreviewPage) {
		await formPreviewPage.close();

		formPreviewPage = null;
	}

	await formsPage.goTo();

	await deleteItems(formsPage);
});

test.describe('Multiple Selection', () => {
	test('field settings are correctly applied', async ({
		formBuilderFieldSettingsSidePanelPage,
		formBuilderPage,
		formBuilderSidePanelPage,
		formsPage,
		page,
	}) => {
		await formsPage.goTo();

		await formsPage.clickManagementToolbarNewButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick(
			'Multiple Selection'
		);

		await test.step('Assert that enabling Show as a Switch setting works', async () => {
			const optionReference = 'Option' + getRandomInt();

			await formBuilderFieldSettingsSidePanelPage.showAsSwitchToggle.check();

			await formBuilderFieldSettingsSidePanelPage.optionReferenceInputField
				.nth(0)
				.fill(optionReference);

			await formBuilderFieldSettingsSidePanelPage.optionReferenceInputField
				.nth(0)
				.blur();

			await page.waitForLoadState('networkidle');

			let switchLocator = formBuilderPage.page
				.getByRole('switch')
				.and(
					page.locator(`[data-option-reference="${optionReference}"]`)
				);

			await expect(switchLocator).toBeVisible();

			const formPreviewPagePromise = page.waitForEvent('popup');

			await formBuilderPage.previewButton.click();

			formPreviewPage = await formPreviewPagePromise;

			switchLocator = formPreviewPage.getByRole('switch');

			await expect(switchLocator).toBeVisible();

			await formPreviewPage.close();
		});

		await test.step('Assert that disabling Show Label setting works', async () => {
			let multiSelectionLabelLocaltor = page
				.getByRole('group', {name: 'Multiple Selection'})
				.getByLabel('Multiple Selection');

			await expect(multiSelectionLabelLocaltor).toBeVisible();

			await formBuilderFieldSettingsSidePanelPage.advancedTabButton.click();

			await formBuilderFieldSettingsSidePanelPage.showLabelToggle.uncheck();

			await expect(multiSelectionLabelLocaltor).not.toBeVisible();

			const formPreviewPagePromise = page.waitForEvent('popup');

			await formBuilderPage.previewButton.click();

			formPreviewPage = await formPreviewPagePromise;

			multiSelectionLabelLocaltor = formPreviewPage
				.getByRole('group', {name: 'Multiple Selection'})
				.getByLabel('Multiple Selection');

			await expect(multiSelectionLabelLocaltor).not.toBeVisible();
		});
	});
});
