/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {checkAccessibility} from '../../../../utils/checkAccessibility';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

test(
	'Assert can delete vocabulary from dropdown actions',
	{tag: '@LPD-32750'},
	async ({editVocabularyPage, page, vocabulariesPage}) => {
		editVocabularyPage.goto();

		const name = `Vocabulary${getRandomInt()}`;

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		await clickAndExpectToBeVisible({
			target: page.getByText(
				`Success:${name} was published successfully.`
			),
			trigger: editVocabularyPage.saveButton,
		});

		await vocabulariesPage.execItemAction({
			action: 'Delete',
			filter: name,
		});

		await expect(
			page.getByRole('heading', {name: `Delete "${name}"`})
		).toBeVisible();

		await clickAndExpectToBeVisible({
			target: page.getByText(
				'Success:Your request completed successfully.'
			),
			trigger: page.getByRole('button', {name: 'Delete'}),
		});

		await expect(vocabulariesPage.getItem(name)).toBeHidden();

		await checkAccessibility({
			page: vocabulariesPage.page,
			selectors: ['.content'],
			selectorsToExclude: [
				'.control-menu-container',
				'.fds',
				'.sidebar-container',
				'.top-bar',
			],
		});
	}
);

test(
	'Assert can edit vocabulary from dropdown actions',
	{tag: '@LPD-32750'},
	async ({editVocabularyPage, page, vocabulariesPage}) => {
		await editVocabularyPage.goto();

		const name = `Vocabulary${getRandomInt()}`;

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		await clickAndExpectToBeVisible({
			target: page.getByText(
				`Success:${name} was published successfully.`
			),
			trigger: editVocabularyPage.saveButton,
		});

		await vocabulariesPage.execItemAction({
			action: 'Edit',
			filter: name,
		});

		await expect(page.getByText(`Edit ${name}`)).toBeVisible();
	}
);

test(
	'Assert can edit vocabulary permissions from dropdown actions',
	{tag: '@LPD-32750'},
	async ({editVocabularyPage, page, vocabulariesPage}) => {
		editVocabularyPage.goto();

		const name = `Vocabulary${getRandomInt()}`;

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		await clickAndExpectToBeVisible({
			target: page.getByText(
				`Success:${name} was published successfully.`
			),
			trigger: editVocabularyPage.saveButton,
		});

		await vocabulariesPage.execItemAction({
			action: 'Permissions',
			filter: name,
		});

		await expect(
			page.getByRole('heading', {name: 'Permissions'})
		).toBeVisible();
	}
);

test(
	'Can create and update vocabulary',
	{tag: '@LPD-32750'},
	async ({editVocabularyPage, page, vocabulariesPage}) => {
		editVocabularyPage.goto();

		const name = `Vocabulary${getRandomInt()}`;

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		await checkAccessibility({
			page: editVocabularyPage.page,
			selectors: ['.cms-section'],
			selectorsToExclude: ['.control-menu-container'],
		});

		await editVocabularyPage.multiSelectToggle.click();

		await editVocabularyPage.changeVisibility('Private');

		await editVocabularyPage.assetTypesButton.click();

		await editVocabularyPage.selectAssetTypes('Blog');

		await clickAndExpectToBeVisible({
			target: page.getByText(
				`Success:${name} was published successfully.`
			),
			trigger: editVocabularyPage.saveButton,
		});

		const newVocabRow = vocabulariesPage.getItem(name);
		await expect(newVocabRow).toBeVisible();

		const newVocabualry = page.getByRole('link', {name});

		await newVocabualry.click();

		await checkAccessibility({
			page: editVocabularyPage.page,
			selectors: ['.categorization-section'],
			selectorsToExclude: ['.control-menu-container'],
		});

		await expect(page.getByText(`Edit ${name}`)).toBeVisible();

		await expect(editVocabularyPage.multiSelectToggle).not.toBeChecked();

		await expect(editVocabularyPage.visibilitySelector).toBeDisabled();

		await expect(editVocabularyPage.visibilitySelector).toContainText(
			'Private'
		);

		const spacesInputLocator = page
			.locator('.categorization-spaces .input-group-item span')
			.nth(1);

		await expect(spacesInputLocator).toContainText('All Spaces');

		const newName = `Vocabulary${getRandomInt()}`;

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name: newName,
		});

		await editVocabularyPage.assetTypesButton.click();

		const assetTypesInputLocator = page
			.locator('.input-group-item span')
			.nth(1);

		await expect(assetTypesInputLocator).toContainText('Blog');

		await clickAndExpectToBeVisible({
			target: page.getByText(
				`Success:${newName} was updated successfully.`
			),
			trigger: editVocabularyPage.saveButton,
		});

		await expect(vocabulariesPage.getItem(newName)).toBeVisible();
	}
);

test(
	'Validate change asset types when saving',
	{tag: '@LPD-52591'},
	async ({editVocabularyPage, page, vocabulariesPage}) => {
		editVocabularyPage.goto();

		const name = `Vocabulary${getRandomInt()}`;

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		await clickAndExpectToBeVisible({
			target: page.getByText(
				`Success:${name} was published successfully.`
			),
			trigger: editVocabularyPage.saveButton,
		});

		const newVocabRow = vocabulariesPage.getItem(name);
		await expect(newVocabRow).toBeVisible();

		const newVocabualry = page.getByRole('link', {name});

		await newVocabualry.click();

		await expect(page.getByText(`Edit ${name}`)).toBeVisible();

		await editVocabularyPage.assetTypesButton.click();

		await checkAccessibility({
			page: editVocabularyPage.page,
			selectors: ['.cms-section'],
			selectorsToExclude: [
				'categorization-vertical-nav',
				'.control-menu-container',
			],
		});

		await editVocabularyPage.selectAssetTypes('Blog');

		await clickAndExpectToBeVisible({
			target: page.getByText('Confirm Asset Type Change'),
			trigger: editVocabularyPage.saveButton,
		});

		const modalSaveButton = page.locator('.modal .btn-primary');

		await clickAndExpectToBeVisible({
			target: page.getByText(`Success:${name} was updated successfully.`),
			trigger: modalSaveButton,
		});
	}
);

test(
	'Validate change spaces when saving',
	{tag: '@LPD-52592'},
	async ({editVocabularyPage, page, vocabulariesPage}) => {
		editVocabularyPage.goto();

		const name = `Vocabulary${getRandomInt()}`;

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		await clickAndExpectToBeVisible({
			target: page.getByText(
				`Success:${name} was published successfully.`
			),
			trigger: editVocabularyPage.saveButton,
		});

		const newVocabRow = vocabulariesPage.getItem(name);
		await expect(newVocabRow).toBeVisible();

		const newVocabualry = page.getByRole('link', {name});

		await newVocabualry.click();

		await expect(page.getByText(`Edit ${name}`)).toBeVisible();

		const spaceName = 'Default';

		await editVocabularyPage.selectSpaces(spaceName);

		await clickAndExpectToBeVisible({
			target: page.getByText('Confirm Space Change'),
			trigger: editVocabularyPage.saveButton,
		});

		const modalSaveButton = page.locator('.modal .btn-primary');

		await clickAndExpectToBeVisible({
			target: page.getByText(`Success:${name} was updated successfully.`),
			trigger: modalSaveButton,
		});
	}
);

test(
	'Validate vocabulary inputs when saving',
	{tag: '@LPD-32750'},
	async ({editVocabularyPage, page}) => {
		editVocabularyPage.goto();

		// Check we can't publish an empty name

		await clickAndExpectToBeVisible({
			target: page.getByText('The Name field is required'),
			trigger: editVocabularyPage.saveButton,
		});

		const name = `Vocabulary${getRandomInt()}`;

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		// Check we can't publish without selecting a space

		await editVocabularyPage.spaceCheckbox.click();

		await clickAndExpectToBeVisible({
			target: page.getByText('The Space field is required'),
			trigger: editVocabularyPage.saveButton,
		});

		await editVocabularyPage.spaceCheckbox.click();

		// Check we can't publish without selecting an asset type

		await editVocabularyPage.assetTypesButton.click();

		await editVocabularyPage.assetTypeCheckbox.click();

		await clickAndExpectToBeVisible({
			target: page.getByText('The Asset Types field is required.'),
			trigger: editVocabularyPage.saveButton,
		});
	}
);

test(
	'Validate that a UI error appears when attempting to create a vocabulary with an existing name',
	{tag: '@LPD-57497'},
	async ({editVocabularyPage, page}) => {
		await editVocabularyPage.goto();

		const name = `Vocabulary${getRandomInt()}`;

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		await clickAndExpectToBeVisible({
			target: page.getByText(
				`Success:${name} was published successfully.`
			),
			trigger: editVocabularyPage.saveButton,
		});

		await editVocabularyPage.goto();

		await editVocabularyPage.changeGeneralInfo({
			description: getRandomString(),
			name,
		});

		await clickAndExpectToBeVisible({
			target: page.getByText(
				'Please enter a unique name. This one is already in use.'
			),
			trigger: editVocabularyPage.saveButton,
		});
	}
);
