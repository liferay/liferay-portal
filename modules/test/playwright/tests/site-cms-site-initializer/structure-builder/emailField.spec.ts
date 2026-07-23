/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from './fixtures/structureBuilderPagesTest';
import {FieldType} from './pages/StructureBuilderPage';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	loginTest(),
	structureBuilderPagesTest
);

test(
	'Email field maps to the EmailAddress business type, round-trips its domain settings, autocompletes domains and validates format and blocked domains',
	{tag: ['@LPD-91039']},
	async ({contentsPage, page, structureBuilderPage}) => {
		const structureLabel = `Structure${getRandomInt()}`;
		const validTitle = getRandomString();
		const blockedTitle = getRandomString();

		// Create a structure with an Email field

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			name: structureLabel,
			page: structureBuilderPage,
		});

		await structureBuilderPage.addField('Email' as FieldType);

		// Accept Unique Values Only lives in the General tab

		await page
			.getByRole('checkbox', {name: 'Accept Unique Values Only'})
			.check();

		// Blocked and autocomplete domains live in the Advanced tab. The UI
		// prepends "@" automatically, so the user can type the bare domain

		await page.getByRole('tab', {name: 'Advanced'}).click();

		const blockedDomains = page.getByRole('combobox', {
			name: 'Blocked Domains',
		});

		await blockedDomains.fill('gmail.com');
		await blockedDomains.press('Enter');

		const autocompleteDomains = page.getByRole('combobox', {
			exact: true,
			name: 'Domains',
		});

		await autocompleteDomains.fill('liferay.com');
		await autocompleteDomains.press('Enter');

		// Publishing serializes the field as the EmailAddress business type with
		// its settings — the Objects backend would reject it otherwise

		const structureId = await structureBuilderPage.publishStructure();

		// Reopen the structure: the settings round-trip through the Objects
		// backend and are restored in the builder

		await structureBuilderPage.editStructure(structureId);

		await structureBuilderPage.selectFields([{label: 'Email'}]);

		await expect(
			page.getByRole('checkbox', {name: 'Accept Unique Values Only'})
		).toBeChecked();

		await page.getByRole('tab', {name: 'Advanced'}).click();

		await expect(
			page.getByRole('gridcell', {exact: true, name: '@gmail.com'})
		).toBeVisible();

		await expect(
			page.getByRole('gridcell', {exact: true, name: '@liferay.com'})
		).toBeVisible();

		await contentsPage.goto();

		await contentsPage.createContent(structureLabel);

		await contentsPage.fillData([{label: 'Title', value: validTitle}]);

		const emailInput = page.getByLabel('Email').first();

		// A malformed email is rejected on the client

		await emailInput.fill('notanemail');

		await emailInput.blur();

		await expect(
			page.getByText('Please enter a valid email address')
		).toBeVisible();

		// Typing the local part and "@" suggests the configured autocomplete
		// domain, and picking it fills the input

		await emailInput.fill('');

		await emailInput.click();

		await page.keyboard.type('user@l');

		await page.getByRole('option', {name: '@liferay.com'}).click();

		await expect(emailInput).toHaveValue('user@liferay.com');

		// A valid, non-blocked email is stored

		await contentsPage.saveContent();

		// An email with a blocked domain is rejected by the backend

		await contentsPage.createContent(structureLabel);

		await contentsPage.fillData([
			{label: 'Title', value: blockedTitle},
			{label: 'Email', value: 'user@gmail.com'},
		]);

		await clickAndExpectToBeVisible({
			target: page.getByText('The Email is invalid.'),
			trigger: contentsPage.publishButton,
		});

		// The backend error is surfaced on the email fragment

		await expect(
			page.getByText(
				'The email address domain is not allowed. Enter an email ' +
					'address with a different domain.'
			)
		).toBeVisible();

		// Cleanup — the structure is auto-deleted, but its stored content is not

		await contentsPage.goto();

		await contentsPage.deleteContent(validTitle);
	}
);
