/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {accessibilityMenuPagesTest} from '../../../fixtures/accessibilityMenuPagesTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {doAndGoBack} from '../../../utils/doAndGoBack';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';
import {assertBodyClass} from './utils/assertBodyClass';
import {assertHTMLAttribute} from './utils/assertHTMLAttribute';
import {assertUnderlinedLinksValue} from './utils/assertUnderlinedLinksValue';

const test = mergeTests(
	accessibilityMenuPagesTest,
	instanceSettingsPagesTest,
	loginTest()
);

test.beforeEach(async ({accessibilityMenuPage, instanceSettingsPage, page}) => {
	await doAndGoBack(page, async () => {
		await instanceSettingsPage.goToInstanceSetting(
			'Accessibility',
			'Accessibility Menu'
		);

		await accessibilityMenuPage.enableAccessibilityMenu();
	});

	await performLogout(page);
});

test.afterEach(async ({instanceSettingsPage, page}) => {
	await performLoginViaApi({page, screenName: 'test'});

	await instanceSettingsPage.goToInstanceSetting(
		'Accessibility',
		'Accessibility Menu'
	);

	await instanceSettingsPage.resetInstanceSetting();
});

const OPTIONS = [
	{
		assert: assertUnderlinedLinksValue,
		label: 'Underlined Links',
	},
	{
		assert: (page: Page, enabled: boolean) =>
			assertBodyClass(page, enabled, /c-prefers-letter-spacing-1/),
		label: 'Increased Text Spacing',
	},
	{
		assert: (page: Page, enabled: boolean) =>
			assertBodyClass(page, enabled, /c-prefers-expanded-text/),
		label: 'Expanded Text',
	},
	{
		assert: (page: Page, enabled: boolean) =>
			assertBodyClass(page, enabled, /c-prefers-reduced-motion/),
		label: 'Reduced Motion',
	},
	{
		assert: (page: Page, enabled: boolean) =>
			assertBodyClass(page, enabled, /c-prefers-focus-ring/),
		label: 'Focus Ring Animation',
	},
	{
		assert: (page: Page, enabled: boolean) =>
			assertBodyClass(page, enabled, /c-prefers-persistent-alerts/),
		label: 'Persistent Notifications',
	},
	{
		assert: (page: Page, enabled: boolean) =>
			assertHTMLAttribute(page, enabled, 'data-prefers-contrast', 'more'),
		label: 'High Contrast',
	},
];

test(
	'Accessibility menu is accessible using the keyboard',
	{tag: '@LPD-74263'},
	async ({accessibilityMenuPage, page}) => {
		const {closeButton, menuTitle, openAccessibilityMenuButton} =
			accessibilityMenuPage;

		await test.step('Open menu with keyboard', async () => {
			await expect(menuTitle).toBeHidden();
			await expect(openAccessibilityMenuButton).not.toBeFocused();

			await page.keyboard.press('Tab');
			await page.keyboard.press('Tab');

			await expect(openAccessibilityMenuButton).toBeFocused();
			await expect(openAccessibilityMenuButton).toBeVisible();

			await page.keyboard.press('Enter');

			await expect(menuTitle).toBeVisible();
		});

		await test.step('Close menu with keyboard', async () => {
			await expect(closeButton).toBeVisible();

			await page.keyboard.press('Tab');

			await expect(closeButton).toBeFocused();

			await page.keyboard.press('Enter');

			await expect(menuTitle).toBeHidden();
		});
	}
);

test(
	'Accessibility menu options can be controlled via the accessibility menu',
	{tag: ['@LPD-61332', '@LPD-74263']},
	async ({accessibilityMenuPage, page}) => {
		await test.step('Open the accessibility menu', async () => {
			await accessibilityMenuPage.openAccessibilityMenu();
		});

		for (const {assert, label} of OPTIONS) {
			await test.step(`The "${label}" option can be configured via the accessibility menu`, async () => {
				const toggle = page.getByLabel(label);

				await assert(page, false);
				await expect(toggle).not.toBeChecked();

				await accessibilityMenuPage.toggle(toggle, true);

				await assert(page, true);

				await accessibilityMenuPage.toggle(toggle, false);

				await assert(page, false);
			});
		}
	}
);

test(
	'Accessibility menu options can be controlled via the keyboard',
	{tag: ['@LPD-61332', '@LPD-74263']},
	async ({accessibilityMenuPage, page}) => {
		await test.step('Open accessibility menu and focus the close button', async () => {
			await accessibilityMenuPage.openAccessibilityMenu();

			await page.keyboard.press('Tab');

			await expect(accessibilityMenuPage.closeButton).toBeFocused();
		});

		for (const {assert, label} of OPTIONS) {
			const toggle = page.getByLabel(label);

			await test.step(`Focus ${label} using the keyboard`, async () => {
				await page.keyboard.press('Tab');

				await assert(page, false);
				await expect(toggle).toBeFocused();
			});

			await test.step(`Toggle ${label} using the keyboard and verify it is still focused`, async () => {
				await page.keyboard.press('Enter');

				await assert(page, true);
				await expect(toggle).toBeChecked();
				await expect(toggle).toBeFocused();
			});

			await test.step(`Toggle ${label} again using the keyboard and verify it is still focused`, async () => {
				await page.keyboard.press('Enter');

				await assert(page, false);
				await expect(toggle).not.toBeChecked();
				await expect(toggle).toBeFocused();
			});
		}
	}
);
