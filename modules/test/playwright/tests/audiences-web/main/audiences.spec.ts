/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {audiencesPagesTest} from '../../../fixtures/audiencesPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(
	apiHelpersTest,
	audiencesPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-85746': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

test(
	'Can create, edit and delete an audience with a browser name condition',
	{
		tag: '@LPD-93951',
	},
	async ({page}) => {
		await page.goto(PORTLET_URLS.audiences);

		// Create a new audience

		await page.getByLabel('New', {exact: true}).click();

		const audienceName = 'Audience ' + getRandomString();

		await page.getByPlaceholder('New Audience').fill(audienceName);

		// Add the Browser Name condition

		await expect(async () => {
			await page
				.locator('.audience-builder-attribute')
				.filter({hasText: 'Browser Name'})
				.dragTo(page.locator('.audience-builder-drop-zone'));

			await expect(page.locator('.audience-builder-rule')).toBeVisible({
				timeout: 2000,
			});
		}).toPass();

		// The conjunction bar frames the conditions

		await expect(
			page.getByText('of these criteria are met.')
		).toBeVisible();

		// The equality operators read Equals and Not Equals

		const operator = page.getByLabel('Operator');

		await operator.click();

		await expect(
			page.getByRole('option', {exact: true, name: 'Equals'})
		).toBeVisible();

		await page
			.getByRole('option', {exact: true, name: 'Not Equals'})
			.click();

		await expect(operator).toContainText('Not Equals');

		await page.getByLabel('Value').fill('Chrome');

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page);

		// The audience is listed

		await expect(
			page.locator('tr').filter({hasText: audienceName})
		).toBeVisible();

		// Reopen it and check the values were kept

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Edit'}),
			trigger: page
				.locator('tr')
				.filter({hasText: audienceName})
				.locator('button.dropdown-toggle'),
		});

		await expect(page.getByPlaceholder('New Audience')).toHaveValue(
			audienceName
		);
		await expect(
			page.locator('.audience-builder-rule').getByText('Browser Name')
		).toBeVisible();
		await expect(page.getByLabel('Operator')).toContainText('Not Equals');
		await expect(page.getByLabel('Value')).toHaveValue('Chrome');

		// Go back to the list

		await page.getByRole('link', {exact: true, name: 'Back'}).click();

		// Delete the audience and check it is no longer listed

		page.once('dialog', (dialog) => dialog.accept());

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Delete'}),
			trigger: page
				.locator('tr')
				.filter({hasText: audienceName})
				.locator('button.dropdown-toggle'),
		});

		await waitForAlert(page);

		await expect(
			page.locator('tr').filter({hasText: audienceName})
		).toHaveCount(0);
	}
);

test(
	'Builds and clears conditions from the keyboard and filters by type',
	{
		tag: '@LPD-94450',
	},
	async ({page}) => {
		await page.goto(PORTLET_URLS.audiences);

		await page.getByLabel('New', {exact: true}).click();

		// A new audience starts with an empty conditions state

		await expect(page.getByText('No Criteria Yet')).toBeVisible();
		await expect(
			page.getByText(
				'To create a new audience, drag items from the sidebar and drop them here.'
			)
		).toBeVisible();

		// Enter on an attribute inserts it as the first condition

		await page
			.getByRole('menuitem', {name: 'Add Browser Name'})
			.press('Enter');

		await expect(
			page.locator('.audience-builder-rule').getByText('Browser Name')
		).toBeVisible();

		await page.getByLabel('Value').fill('Chrome');

		// The conjunction bar frames the conditions and switches All to Any

		await expect(
			page.getByText('of these criteria are met.')
		).toBeVisible();

		const conjunction = page.getByLabel('Conjunction');

		await expect(conjunction).toContainText('All');

		await conjunction.click();

		await page.getByRole('option', {exact: true, name: 'Any'}).click();

		await expect(conjunction).toContainText('Any');

		// Enter picks a second attribute up, a second Enter drops it below

		const addBrowserVersion = page.getByRole('menuitem', {
			name: 'Add Browser Version',
		});

		await addBrowserVersion.press('Enter');

		await expect(addBrowserVersion).toHaveClass(
			/audience-builder-attribute--dragging/
		);

		await addBrowserVersion.press('Enter');

		await expect(page.locator('.audience-builder-rule')).toHaveCount(2);

		// Removing the conditions returns to the empty state

		await page
			.locator('.audience-builder-rule')
			.filter({hasText: 'Browser Version'})
			.getByLabel('Delete')
			.click();

		await expect(page.locator('.audience-builder-rule')).toHaveCount(1);

		await page.getByLabel('Delete').click();

		await expect(page.getByText('No Criteria Yet')).toBeVisible();

		// The type selector filters the attribute list by category

		const typeSelector = page.getByLabel('Attributes Types');

		await typeSelector.selectOption({label: 'General'});

		await expect(
			page
				.locator('.audience-builder-attribute')
				.filter({hasText: 'User Authentication'})
		).toBeVisible();
		await expect(
			page
				.locator('.audience-builder-attribute')
				.filter({hasText: 'Browser Name'})
		).toHaveCount(0);

		await typeSelector.selectOption({label: 'Browser'});

		await expect(
			page
				.locator('.audience-builder-attribute')
				.filter({hasText: 'Browser Name'})
		).toBeVisible();
	}
);

test(
	'Shows a collapsible General Settings section with an external reference code field',
	{
		tag: '@LPD-95291',
	},
	async ({page}) => {
		await page.goto(PORTLET_URLS.audiences);

		await page.getByLabel('New', {exact: true}).click();

		const generalSettingsToggle = page.getByRole('button', {
			name: 'General Settings',
		});

		const externalReferenceCode = page.getByRole('textbox', {name: 'ERC'});

		// The section is collapsed by default

		await expect(externalReferenceCode).toHaveCount(0);

		// It expands on interaction and exposes an editable field

		await generalSettingsToggle.click();

		await externalReferenceCode.fill('ERC-123');

		await expect(externalReferenceCode).toHaveValue('ERC-123');

		// It collapses again on interaction

		await generalSettingsToggle.click();

		await expect(externalReferenceCode).toHaveCount(0);
	}
);

test(
	'Saves and updates the audience external reference code',
	{
		tag: '@LPD-95291',
	},
	async ({page}) => {
		await page.goto(PORTLET_URLS.audiences);

		// Create an audience with an external reference code

		await page.getByLabel('New', {exact: true}).click();

		const audienceName = 'Audience ' + getRandomString();

		await page.getByPlaceholder('New Audience').fill(audienceName);

		const externalReferenceCode = 'ERC-' + getRandomString();

		await page.getByRole('button', {name: 'General Settings'}).click();

		await page
			.getByRole('textbox', {name: 'ERC'})
			.fill(externalReferenceCode);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page);

		// Reopen it and check the external reference code was persisted

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Edit'}),
			trigger: page
				.locator('tr')
				.filter({hasText: audienceName})
				.locator('button.dropdown-toggle'),
		});

		await page.getByRole('button', {name: 'General Settings'}).click();

		await expect(page.getByRole('textbox', {name: 'ERC'})).toHaveValue(
			externalReferenceCode
		);

		// Update the external reference code and save

		const updatedExternalReferenceCode = 'ERC-' + getRandomString();

		await page
			.getByRole('textbox', {name: 'ERC'})
			.fill(updatedExternalReferenceCode);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page);

		// Reopen it and check the updated external reference code was kept

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Edit'}),
			trigger: page
				.locator('tr')
				.filter({hasText: audienceName})
				.locator('button.dropdown-toggle'),
		});

		await page.getByRole('button', {name: 'General Settings'}).click();

		await expect(page.getByRole('textbox', {name: 'ERC'})).toHaveValue(
			updatedExternalReferenceCode
		);

		// Delete the audience

		await page.getByRole('link', {exact: true, name: 'Back'}).click();

		page.once('dialog', (dialog) => dialog.accept());

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Delete'}),
			trigger: page
				.locator('tr')
				.filter({hasText: audienceName})
				.locator('button.dropdown-toggle'),
		});

		await waitForAlert(page);

		await expect(
			page.locator('tr').filter({hasText: audienceName})
		).toHaveCount(0);
	}
);

test(
	'Groups, navigates, keeps the conjunction independent, caps nesting, and unwraps',
	{
		tag: '@LPD-98159',
	},
	async ({audiencesPage, page}) => {
		const attributes = page.locator('.audience-builder-attribute');
		const groups = page.locator('.audience-builder-group');
		const rules = page.locator('.audience-builder-rule');

		await audiencesPage.openNewAudience();

		await audiencesPage.addCondition(0);
		await audiencesPage.addCondition(1);
		await audiencesPage.addCondition(0);

		// Pick the first condition up and drop it onto the second to group them

		await page
			.getByRole('button', {name: /^Move /})
			.first()
			.press('Enter');

		await expect(
			page.locator('.audience-builder-rule--dragging')
		).toBeVisible();

		await page.keyboard.press('ArrowDown');
		await page.keyboard.press('Enter');

		await expect(groups).toHaveCount(1);
		await expect(groups.locator('.audience-builder-rule')).toHaveCount(2);
		await expect(rules).toHaveCount(3);

		// Arrow navigation reaches the rules nested inside the group

		await rules.last().focus();

		await page.keyboard.press('ArrowUp');

		await expect(
			groups.locator('.audience-builder-rule').last()
		).toBeFocused();

		// Tabbing past a condition's last action leaves the list

		await rules.last().getByLabel('Delete').focus();

		await page.keyboard.press('Tab');

		await expect(
			page.getByRole('menu', {name: 'Conditions'}).locator(':focus')
		).toHaveCount(0);

		// The new group defaults to All

		await expect(groups.getByLabel('Conjunction')).toContainText('All');

		// Switching the root to Any leaves the nested group on All

		const rootConjunction = page.getByLabel('Conjunction').first();

		await rootConjunction.click();

		await page.getByRole('option', {exact: true, name: 'Any'}).click();

		await expect(rootConjunction).toContainText('Any');
		await expect(groups.getByLabel('Conjunction')).toContainText('All');

		// Dropping onto a rule inside the group nests a third level

		await expect(async () => {
			await attributes
				.first()
				.dragTo(
					groups.first().locator('.audience-builder-rule').first()
				);

			await expect(groups).toHaveCount(2);
		}).toPass();

		// A drop inside the deepest group adds a sibling but no fourth level

		const beforeCount = await rules.count();

		await expect(async () => {
			await attributes
				.first()
				.dragTo(
					groups.last().locator('.audience-builder-rule').first()
				);

			await expect(rules).toHaveCount(beforeCount + 1);
		}).toPass();

		await expect(groups).toHaveCount(2);

		// Deleting conditions unwraps the groups back down to two rules

		while ((await rules.count()) > 2) {
			await groups
				.last()
				.locator('.audience-builder-rule')
				.first()
				.getByLabel('Delete')
				.click();
		}

		await expect(groups).toHaveCount(0);
		await expect(rules).toHaveCount(2);
	}
);
