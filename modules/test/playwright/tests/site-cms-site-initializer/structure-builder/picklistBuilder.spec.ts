/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import fillAndClickOutside from '../../../utils/fillAndClickOutside';
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

		await expect(
			page.getByText('This field is required')
		).not.toBeVisible();

		await expect(async () => {
			await picklistBuilderPage.ercInput.fill('', {timeout: 1000});

			await page.getByText('New Picklist').click({timeout: 1000});

			await expect(page.getByText('This field is required')).toBeVisible({
				timeout: 1000,
			});
		}).toPass();

		// Check that the ERC input is focused when the Save button is pressed

		await picklistBuilderPage.saveButton.click();

		await expect(picklistBuilderPage.ercInput).toBeFocused();

		// Fill the ERC input

		await fillAndClickOutside(
			page,
			picklistBuilderPage.ercInput,
			getRandomString()
		);

		await expect(
			page.getByText('This field is required')
		).not.toBeVisible();

		// Check the error if the Name input is empty

		await expect(async () => {
			await picklistBuilderPage.nameInput.fill('', {timeout: 1000});

			await page.getByText('New Picklist').click({timeout: 1000});

			await expect(page.getByText('This field is required')).toBeVisible({
				timeout: 1000,
			});
		}).toPass();

		// Check that the Name input is focused when the Save button is pressed

		await picklistBuilderPage.saveButton.click();

		await expect(picklistBuilderPage.nameInput).toBeFocused();

		// Fill the name input

		await fillAndClickOutside(
			page,
			picklistBuilderPage.nameInput,
			'My new name'
		);

		await expect(
			page.getByText('This field is required')
		).not.toBeVisible();

		// Save the picklist

		const {id: picklistId} = await picklistBuilderPage.savePicklist();

		await picklistBuilderPage.deletePicklist(picklistId);
	}
);
