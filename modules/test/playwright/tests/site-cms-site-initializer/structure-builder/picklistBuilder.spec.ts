/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../main/fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	loginTest()
);

test(
	'Check the input errors',
	{tag: '@LPD-52040'},
	async ({page, picklistBuilderPage}) => {

		// Go to the Picklist Builder

		await picklistBuilderPage.goto();

		// Check the error if the ERC input is empty

		await picklistBuilderPage.ercInput.fill('');

		await expect(picklistBuilderPage.ercInput.locator('..')).toContainText(
			'This field is required.'
		);

		// Check that the ERC input is focused when the Save button is pressed

		await picklistBuilderPage.saveButton.click();

		await expect(picklistBuilderPage.ercInput).toBeFocused();

		// Fil the ERC input

		await picklistBuilderPage.ercInput.fill(getRandomString());

		await expect(
			picklistBuilderPage.ercInput.locator('..')
		).not.toContainText('This field is required.');

		// Check the error if the Name input is empty

		await picklistBuilderPage.nameInput.fill('');

		await expect(page.locator('.input-localized')).toContainText(
			'This field is required.'
		);

		// Check that the Name input is focused when the Save button is pressed

		await picklistBuilderPage.saveButton.click();

		await expect(picklistBuilderPage.nameInput).toBeFocused();

		// Fill the name input

		await picklistBuilderPage.nameInput.fill('My new name');

		await expect(page.locator('.input-localized')).not.toContainText(
			'This field is required.'
		);

		// Save the picklist

		const {id: picklistId} = await picklistBuilderPage.savePicklist();

		await picklistBuilderPage.deletePicklist(picklistId);
	}
);
