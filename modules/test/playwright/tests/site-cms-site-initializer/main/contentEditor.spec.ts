/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import fillAndClickOutside from '../../../utils/fillAndClickOutside';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11232': {enabled: true},
		'LPD-17564': {enabled: true},
		'LPD-37927': {enabled: true},
	}),
	loginTest(),
	pageEditorPagesTest
);

test(
	'Friendly URL is taken into account when creating contents',
	{tag: '@LPD-54566'},
	async ({contentsPage, page}) => {

		// Go to CMS Contents

		await contentsPage.goto();

		// Create new Knowledge Base content

		await contentsPage.createContent('Knowledge Base');

		// Fill data and save

		const title = getRandomString();
		const friendlyUrl = getRandomString();

		await page.getByLabel('Title').fill(title);
		await page.getByLabel('Friendly URL').fill(friendlyUrl);

		await contentsPage.saveContent();

		// Edit the content again and check values

		await contentsPage.editContent(title);

		await expect(page.getByLabel('Friendly URL')).toHaveValue(friendlyUrl);

		// Delete content

		await contentsPage.goto();

		await contentsPage.deleteContent(title);
	}
);

test(
	'Default structures take Content Editor Master and fragments work',
	{tag: '@LPD-50371'},
	async ({contentsPage, page}) => {

		// Go to CMS Contents

		await contentsPage.goto();

		// Create new Knowledge Base content

		await contentsPage.createContent('Knowledge Base');

		// Fill data

		const titleEnglish = getRandomString();
		const titleSpanish = getRandomString();
		const friendlyUrl = getRandomString();

		await page.getByLabel('Title').fill(titleEnglish);
		await page.getByLabel('Friendly URL').fill(friendlyUrl);

		await fillAndClickOutside(page, page.getByLabel('Title'), titleEnglish);
		await fillAndClickOutside(
			page,
			page.getByLabel('Friendly URL'),
			friendlyUrl
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option').filter({hasText: 'es-ES'}),
			trigger: page.getByLabel('Select a language, current language:'),
		});

		await fillAndClickOutside(page, page.getByLabel('Title'), titleSpanish);

		// Check side panel works

		await contentsPage.openSidePanel('General');

		await contentsPage.closeSidePanel();

		await contentsPage.saveContent();

		// Edit the content again and check values

		await contentsPage.editContent(titleEnglish);

		await expect(page.getByLabel('Title')).toHaveValue(titleEnglish);
		await expect(page.getByLabel('Friendly URL')).toHaveValue(friendlyUrl);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option').filter({hasText: 'es-ES'}),
			trigger: page.getByLabel('Select a language, current language:'),
		});

		await expect(page.getByLabel('Title')).toHaveValue(titleSpanish);

		// Delete content

		await contentsPage.goto();

		await contentsPage.deleteContent(titleEnglish);
	}
);

test(
	'Check the functionality of the Space List fragment CMS',
	{tag: ['@LPD-52223']},
	async ({contentsPage, page, structureBuilderPage}) => {

		// Create new structure for Default space

		await structureBuilderPage.createStructure();

		await structureBuilderPage.selectSpaces(['Default']);

		const label = getRandomString();

		await structureBuilderPage.changeStructureSettings({
			label,
			name: `StructureName${getRandomInt()}`,
		});

		// Publish the structure

		const {id} = await structureBuilderPage.saveStructure();

		await structureBuilderPage.publishStructure();

		// Create a content of the new structure and check Spaces fragment

		await contentsPage.goto();

		await contentsPage.createContent(label);

		const fragment = page.locator(
			'[class*="spacelistcomponentsectionfragmentrenderer"]'
		);

		await fragment.waitFor();

		await expect(
			fragment.locator('label').filter({hasText: 'Space'})
		).toBeVisible();

		await expect(
			fragment.locator('.sticker-overlay').filter({hasText: 'D'})
		).toBeVisible();

		await expect(fragment.filter({hasText: 'Default'})).toBeVisible();

		// Delete structure

		await structureBuilderPage.deleteStructure(id);
	}
);

test(
	'Blog can be published again without changing the content',
	{tag: '@LPD-57478'},
	async ({contentsPage, page}) => {

		// Go to CMS Contents

		await contentsPage.goto();

		// Create new Blog content

		await contentsPage.createContent('Blog');

		// Fill data and save

		const title = getRandomString();

		await page.getByPlaceholder('New Blog').fill(title);

		// Select file from computer in the default language

		const fileChooserPromise = page.waitForEvent('filechooser');

		const firstFileUploadFragment = page.locator('.file-upload').first();

		await firstFileUploadFragment
			.getByText('Select File', {exact: true})
			.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			path.join(__dirname, '/dependencies/file_upload_image_1.jpg')
		);

		await expect(
			firstFileUploadFragment.getByText('file_upload_image_1.jpg')
		).toBeVisible();

		await contentsPage.saveContent();

		// Edit the content again and check values

		await contentsPage.editContent(title);

		await expect(
			firstFileUploadFragment.getByText('file_upload_image_1.jpg')
		).toBeVisible();

		// Save content

		await contentsPage.saveContent();

		// Check the content is published

		await expect(page).toHaveURL(/\/web\/cms\/contents$/);

		await contentsPage.deleteContent(title);
	}
);

test(
	'When publishing a content in a folder the browser is redirected to the folder',
	{tag: '@LPD-57478'},
	async ({contentsPage, folderPage, page}) => {

		// Go to CMS Contents

		await contentsPage.goto();

		// Create new Folder and a Knowledge Base content

		const folderName = getRandomString();

		await folderPage.createFolder(folderName);

		await folderPage.clickOption(folderName, 'View Folder');

		await contentsPage.createContent('Knowledge Base');

		// Fill data and save

		const title = getRandomString();

		await page.getByLabel('Title').fill(title);

		await contentsPage.saveContent();

		// Check that the content is visible that means we redirected to the folder

		await expect(page.getByTitle(title)).toBeVisible();

		// Delete content and folder

		await contentsPage.deleteContent(title);

		await contentsPage.goto();

		await folderPage.deleteFolder(folderName);

		await expect(page.getByText(folderName)).not.toBeVisible();
	}
);
