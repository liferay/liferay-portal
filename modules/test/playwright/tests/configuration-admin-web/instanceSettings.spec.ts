/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFile} from 'fs/promises';

import { accessibilityMenuPagesTest } from '../../fixtures/accessibilityMenuPagesTest';
import { instanceSettingsPagesTest } from '../../fixtures/instanceSettingsPagesTest';
import { isolatedSiteTest } from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import { siteSettingsPagesTest } from '../../fixtures/siteSettingsPagesTest';
import {UsersAndOrganizationsPage} from '../../pages/users-admin-web/UsersAndOrganizationsPage';
import getRandomString from '../../utils/getRandomString';

export const test = mergeTests(accessibilityMenuPagesTest, instanceSettingsPagesTest, isolatedSiteTest, loginTest(), siteSettingsPagesTest);

test('Asserts that a user can export a configuration', async ({instanceSettingsPage, page}) => {
	const emailDomainValidationSwitcher = page.getByRole('switch', {
		name: 'Enable Email Domain Validation',
	});

	await instanceSettingsPage.goToInstanceSetting('Accounts', 'Email Domains');

	try {
		await emailDomainValidationSwitcher.check();

		await instanceSettingsPage.saveButton.click();

		await expect(emailDomainValidationSwitcher).toBeChecked();

		page.on('download', async (download) => {
			expect(download.suggestedFilename()).toEqual(
				expect.stringMatching(
					'com.liferay.account.configuration.AccountEntryEmailDomainsConfiguration.scoped~(.*).config'
				)
			);

			const path = await download.path();

			const fileContent = await readFile(path, 'utf-8');

			expect(
				fileContent.includes('enableEmailDomainValidation=B"true"')
			).toBeTruthy();
		});

		await instanceSettingsPage.exportInstanceSetting();
	}
	finally {
		await emailDomainValidationSwitcher.uncheck();

		await instanceSettingsPage.saveButton.click();
	}
});

test('LPD-35562 Enter reserved screen name', async ({instanceSettingsPage, page}) => {
	const emailAddress = getRandomString() + '@liferay.com';
	const firstName = getRandomString();
	const lastName = getRandomString();
	const reservedScreenName = getRandomString();

	await instanceSettingsPage.goToInstanceSetting(
		'User Authentication',
		'Reserved Credentials'
	);

	await page.getByLabel('Screen Names').fill(reservedScreenName);

	await instanceSettingsPage.saveAndWaitForAlert(
		{autoClose: true, type: 'success'}
	);

	const usersAndOrganizationsPage = new UsersAndOrganizationsPage(page);

	await usersAndOrganizationsPage.goToUsers();

	await page.getByRole('link', {name: 'Add User'}).click();

	await page.getByLabel('Screen Name').fill(reservedScreenName);

	await page.getByLabel('Email Address').fill(emailAddress);

	await page.getByLabel('First Name').fill(firstName);

	await page.getByLabel('Last Name').fill(lastName);

	await instanceSettingsPage.saveAndWaitForAlert(
        { autoClose: false, text: 'Error:The screen name you requested is reserved.', type: 'danger'}
	);
});

test('LPD-38043 Assert that a site configuration overrides its instance version',
	async ({ accessibilityMenuPage, instanceSettingsPage, page, site, siteSettingsPage }) => {

    await test.step("Make sure the instance accessibility configuration is disabled", async() => {
        await instanceSettingsPage.goToInstanceSetting(
            'Accessibility',
            'Accessibility Menu'
        );
    
        if (await accessibilityMenuPage.enableAccessibilityMenuCheckbox.isChecked()) {
            await accessibilityMenuPage.enableAccessibilityMenuCheckbox.uncheck();

            await instanceSettingsPage.saveButton.click();
        }
    });

    await test.step("Make sure the accessibility menu is not accessible in the site scope", async () => {
        await siteSettingsPage.goToSiteSetting(
            'Accessibility',
            "Accessibility Menu",
            site.friendlyUrlPath
        );

        await page.waitForLoadState();

        await expect(accessibilityMenuPage.openAccessibilityMenuButton).not.toBeAttached();
    });

    await test.step("Enable the site accessibility configuration", async () => {
        await expect(async () => {
            await accessibilityMenuPage.enableAccessibilityMenuCheckbox.check();

            await expect(accessibilityMenuPage.enableAccessibilityMenuCheckbox).toBeChecked({timeout: 5000});
        }).toPass();

        await siteSettingsPage.saveConfiguration();
    });

    await test.step("Make sure the accessibility menu is accessible in the site scope", async () => {
        await page.waitForLoadState();

        await expect(accessibilityMenuPage.openAccessibilityMenuButton).toBeAttached();
    });

    await test.step("Make sure the accessibility menu is not accessible in the instance scope", async () => {
        await instanceSettingsPage.goToInstanceSetting(
            'Accessibility',
            'Accessibility Menu'
        );

        await page.waitForLoadState();

        await expect(accessibilityMenuPage.openAccessibilityMenuButton).not.toBeAttached();
    });
});