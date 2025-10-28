/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {checkAccessibility} from '../../../../utils/checkAccessibility';
import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../../utils/getRandomInt';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	loginTest()
);

test('Add a new tag', {tag: '@LPD-51250'}, async ({page, tagsPage}) => {
	const tagName = await tagsPage.createTag();

	const tag = tagsPage.getItem(tagName);

	await expect(tag).toBeVisible();

	// Check accessibility

	await checkAccessibility({
		page,
		selectors: ['.categorization-section'],
	});

	await tagsPage.deleteTag(tagName);
});

test(
	'Save and add another tag',
	{tag: '@LPD-51250'},
	async ({page, tagsPage}) => {
		await tagsPage.goto();

		const name1 = `Tag${getRandomInt()}`;
		const name2 = `Tag${getRandomInt()}`;

		await clickAndExpectToBeVisible({
			target: page.locator('.modal-title', {
				hasText: 'New Tag',
			}),
			timeout: 2000,
			trigger: tagsPage.newTagButton,
		});

		// Check accessibility

		await checkAccessibility({
			page,
			selectors: ['.modal-content'],
		});

		await page.getByLabel('NameRequired').fill(name1);

		await expect(async () => {
			await tagsPage.saveAndAddAnotherButton.click({timeout: 1000});

			await expect(page.getByLabel('NameRequired')).toBeEmpty({
				timeout: 1000,
			});
		}).toPass();

		await page.getByLabel('NameRequired').fill(name2);

		await clickAndExpectToBeHidden({
			target: page.locator('.modal-title', {
				hasText: 'New Tag',
			}),
			timeout: 2000,
			trigger: tagsPage.saveButton,
		});

		const tag1 = tagsPage.getItem(name1);

		await expect(tag1).toBeVisible();

		const tag2 = tagsPage.getItem(name2);

		await expect(tag2).toBeVisible();

		await tagsPage.deleteTag(name1);

		await tagsPage.deleteTag(name2);
	}
);

test('Delete a tag', {tag: '@LPD-51252'}, async ({tagsPage}) => {
	const tagName = await tagsPage.createTag();

	await tagsPage.deleteTag(tagName);
});

test('Edit an existing tag', {tag: '@LPD-52395'}, async ({page, tagsPage}) => {
	const tagName = await tagsPage.createTag();

	await tagsPage.execItemAction({
		action: 'Edit',
		filter: tagName,
	});

	await expect(page.getByText(`Edit "${tagName}"`)).toBeVisible();

	await expect(tagsPage.saveAndAddAnotherButton).not.toBeVisible();

	// Check accessibility

	await checkAccessibility({
		page,
		selectors: ['.modal-content'],
	});

	const newName = `Tag${getRandomInt()}`;

	await page.getByLabel('NameRequired').fill(newName);

	await clickAndExpectToBeVisible({
		target: page.getByText(`Success:${tagName} was updated successfully.`),
		trigger: tagsPage.saveButton,
	});

	const tag = tagsPage.getItem(newName);

	await expect(tag).toBeVisible();

	await tagsPage.deleteTag(newName);
});

test(
	'Create a new tag in a specific space',
	{tag: '@LPD-53874'},
	async ({page, tagsPage}) => {
		await tagsPage.goto();

		const name = `Tag${getRandomInt()}`;

		await tagsPage.newTagButton.click();

		await page.getByLabel('NameRequired').fill(name);

		await tagsPage.spaceCheckbox.uncheck();

		await page.getByLabel('Space Selector').click();

		await page.getByRole('option', {name: 'D Default'}).click();

		await clickAndExpectToBeVisible({
			target: page.getByText(`Success:${name} was created successfully.`),
			trigger: tagsPage.saveButton,
		});

		const tag = tagsPage.getItem(name);

		await expect(tag).toBeVisible();

		await expect(
			page
				.locator('[data-testid="visualization-mode-table"]')
				.getByText('Default')
		).toBeVisible();

		await tagsPage.deleteTag(name);
	}
);

test('Bulk Merge tags', {tag: '@LPD-43388'}, async ({page, tagsPage}) => {
	const tagName1 = await tagsPage.createTag();
	const tagName2 = await tagsPage.createTag();

	const tag1 = tagsPage.getItem(tagName1);
	const tag2 = tagsPage.getItem(tagName2);

	await expect(tag1).toBeVisible();
	await expect(tag2).toBeVisible();

	page.reload();

	await tagsPage.execItemAction({
		action: 'Merge',
		filter: tagName1,
	});

	await expect(page.getByText('Merge Tags')).toBeVisible();

	await clickAndExpectToBeVisible({
		target: page.getByText('Please choose at least 2 tags.'),
		trigger: tagsPage.saveButton,
	});

	await page.getByRole('button', {name: 'OK'}).click();

	await page.getByLabel('Select', {exact: true}).click();

	await expect(
		page
			.locator('.categorization-section')
			.locator('.fds table')
			.locator('tbody tr')
			.filter({hasText: tagName1})
	).toBeVisible();

	await expect(
		page
			.locator('.categorization-section')
			.locator('.fds table')
			.locator('tbody tr')
			.filter({hasText: tagName2})
	).toBeVisible();

	// Check accessibility

	await checkAccessibility({
		page,
		selectors: ['.merge-tags'],
	});

	await page
		.locator('.categorization-section')
		.getByRole('row', {name: tagName1})
		.getByLabel('')
		.click();
	await page
		.locator('.categorization-section')
		.getByRole('row', {name: tagName2})
		.getByLabel('')
		.click();

	await page.locator('.modal').getByRole('button', {name: 'Done'}).click();

	await clickAndExpectToBeVisible({
		target: page.getByRole('heading', {name: 'Confirm Merge Tags'}),
		trigger: tagsPage.saveButton,
	});

	await page.getByRole('button', {name: 'Save'}).click();

	await expect(tag1).toBeVisible();
	await expect(tag2).not.toBeVisible();

	await tagsPage.deleteTag(tagName1);
});

test('Merge tags', {tag: '@LPD-43388'}, async ({page, tagsPage}) => {
	const tagName1 = await tagsPage.createTag();
	const tagName2 = await tagsPage.createTag();

	const tag1 = tagsPage.getItem(tagName1);
	const tag2 = tagsPage.getItem(tagName2);

	await expect(tag1).toBeVisible();
	await expect(tag2).toBeVisible();

	await page.reload();

	await tagsPage.execItemAction({
		action: 'Merge',
		filter: tagName1,
	});

	await expect(page.getByText('Merge Tags')).toBeVisible();

	// Check accessibility

	await checkAccessibility({
		page,
		selectors: ['.categorization-section'],
	});

	await expect(
		page.getByRole('gridcell', {exact: true, name: tagName1})
	).toBeVisible();

	await page.getByLabel('Merge Tags').getByRole('combobox').click();

	await expect(async () => {
		await page.getByRole('option', {name: tagName2}).click({timeout: 1000});

		await expect(
			page.locator('.label-secondary', {hasText: tagName2})
		).toBeVisible({timeout: 1000});
	}).toPass();

	await clickAndExpectToBeVisible({
		target: page.getByRole('heading', {name: 'Confirm Merge Tags'}),
		trigger: tagsPage.saveButton,
	});

	await clickAndExpectToBeVisible({
		target: page.getByText(
			`Success:${tagName2} and ${tagName1} have been successfully merged.`
		),
		trigger: page.getByRole('button', {name: 'Save'}),
	});

	await expect(tag1).toBeVisible();
	await expect(tag2).not.toBeVisible();

	await tagsPage.deleteTag(tagName1);
});

test(
	'Validate that a UI error appears when attempting to create or edit a tag with an existing name',
	{tag: '@LPD-57497'},
	async ({page, tagsPage}) => {
		const name1 = await tagsPage.createTag();

		const tag1 = tagsPage.getItem(name1);

		await expect(tag1).toBeVisible();

		await tagsPage.newTagButton.click();

		await page.getByLabel('NameRequired').fill(name1);

		await clickAndExpectToBeVisible({
			target: page.getByText(
				'Please enter a unique name. This one is already in use.'
			),
			trigger: tagsPage.saveButton,
		});

		await clickAndExpectToBeHidden({
			target: page
				.locator('.modal-body')
				.getByText(
					'Please enter a unique name. This one is already in use.'
				),
			trigger: page.getByText('Cancel'),
		});

		// Repeat test for attempting to edit tag since the edit and create modals are separate components

		const name2 = await tagsPage.createTag();

		const tag2 = tagsPage.getItem(name2);
		await expect(tag2).toBeVisible();

		await tagsPage.execItemAction({
			action: 'Edit',
			filter: name2,
		});

		await expect(page.getByText(`Edit "${name2}"`)).toBeVisible();

		await expect(tagsPage.saveAndAddAnotherButton).not.toBeVisible();

		await page.getByLabel('NameRequired').fill(name1);

		await clickAndExpectToBeVisible({
			target: page.getByText(
				'Please enter a unique name. This one is already in use.'
			),
			trigger: tagsPage.saveButton,
		});

		await clickAndExpectToBeHidden({
			target: page
				.locator('.modal-body')
				.getByText(
					'Please enter a unique name. This one is already in use.'
				),
			trigger: page.getByText('Cancel'),
		});

		await tagsPage.deleteTag(name1);

		await tagsPage.deleteTag(name2);
	}
);
