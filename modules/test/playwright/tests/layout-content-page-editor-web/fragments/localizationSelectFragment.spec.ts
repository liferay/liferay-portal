/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from '../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';
import {FieldType} from '../../site-cms-site-initializer/structure-builder/pages/StructureBuilderPage';

const test = mergeTests(
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-17564': {enabled: true},
		'LPD-32050': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	loginTest(),
	cmsPagesTest,
	fragmentsPagesTest,
	pageEditorPagesTest,
	structureBuilderPagesTest,
	systemSettingsPageTest
);

test(
	'Can mark a language as translated',
	{
		tag: ['@LPD-52074'],
	},
	async ({
		contentsPage,
		localizationSelectPage,
		page,
		pageEditorPage,
		structureBuilderPage,
	}) => {

		// Create a CMS structure

		const structureERC = getRandomString();
		const structureLabel = getRandomString();

		await structureBuilderPage.createStructureFromData({
			erc: structureERC,
			label: structureLabel,
			page: structureBuilderPage,
		});

		// Add all supported type of fields (Text is already added for Title)

		const types: FieldType[] = [
			'Boolean',
			'Decimal',
			'Date',
			'Date and Time',
			'Long Text',
			'Numeric',
			'Rich Text',
		];

		for (const type of types) {
			await structureBuilderPage.addField(type);
		}

		await structureBuilderPage.publishStructure();

		// Go to view mode (create a content) and fill values in default language

		await contentsPage.goto();

		const contentTitle = getRandomString();

		await contentsPage.createContent(structureLabel);

		await contentsPage.fillData([
			{label: 'Title', value: contentTitle},
			{label: 'Date', value: '2025-08-08'},
			{label: 'Date and Time', value: '2025-08-07T13:49'},
			{label: 'Long Text', value: 'Papa'},
			{label: 'Rich Text', type: 'Rich Text', value: 'Pepe'},
			{label: 'Decimal', value: '1.2'},
			{label: 'Boolean', type: 'Checkbox', value: true},
			{label: 'Numeric', value: '3'},
		]);

		// Check localization actions are not present by default

		await localizationSelectPage.switchLanguage('es-ES');

		await expect(page.getByLabel('Localization Actions')).not.toBeVisible();

		// Save the content

		await contentsPage.saveContent();

		// Edit the experience for the structure and enable localization management

		await structureBuilderPage.editStructure(structureERC);

		await structureBuilderPage.customizeExperience();

		const localizationSelectId = await pageEditorPage.getFragmentId(
			'Localization Select'
		);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Allow Localization Management',
			fragmentId: localizationSelectId,
			tab: 'General',
			value: true,
		});

		// Also remove Friendly URL input

		await pageEditorPage.deleteFragment(
			await pageEditorPage.getFragmentId('Friendly URL')
		);

		await pageEditorPage.publishPage();

		// Edit the content again and check localization actions are now visible

		await contentsPage.goto();

		await contentsPage.editContent(contentTitle);

		await localizationSelectPage.switchLanguage('es-ES');

		await expect(page.getByLabel('Localization Actions')).toBeVisible();

		// Mark spanish language as translated

		await localizationSelectPage.markAsTranslated('es-ES');

		// Check option is disabled when values are filled

		const option = page.getByRole('menuitem', {
			name: 'Mark as Translated',
		});

		await clickAndExpectToBeVisible({
			target: option,
			trigger: localizationSelectPage.actionsDropdownTrigger,
		});

		await expect(option).toBeDisabled();

		// Save content, edit it again and check values were persisted for spanish

		await contentsPage.saveContent();

		await contentsPage.goto();

		await contentsPage.editContent(contentTitle);

		expect(await localizationSelectPage.getLanguageStatus('es-ES')).toBe(
			'translated'
		);

		// Delete content

		await contentsPage.goto();

		await contentsPage.deleteContent(contentTitle);
	}
);

test(
	'Can reset translations for a language',
	{
		tag: ['@LPD-52074'],
	},
	async ({
		contentsPage,
		localizationSelectPage,
		page,
		pageEditorPage,
		structureBuilderPage,
	}) => {

		// Create a CMS structure

		const structureERC = getRandomString();
		const structureLabel = getRandomString();

		await structureBuilderPage.createStructureFromData({
			erc: structureERC,
			label: structureLabel,
			page: structureBuilderPage,
		});

		// Add all supported type of fields (Text is already added for Title)

		for (const type of [
			'Long Text',
			'Rich Text',
			'Date',
			'Date and Time',
		]) {
			await structureBuilderPage.addField(type as FieldType);
		}

		await structureBuilderPage.publishStructure();

		// Go to view mode (create a content) and fill values in default language and save the content

		await contentsPage.goto();

		const contentTitle = getRandomString();

		await contentsPage.createContent(structureLabel);

		await contentsPage.fillData([
			{label: 'Title', value: contentTitle},
			{label: 'Date', value: '2025-08-08'},
			{label: 'Date and Time', value: '2025-08-07T13:49'},
			{label: 'Long Text', value: 'Papa'},
			{label: 'Rich Text', type: 'Rich Text', value: 'Pepe'},
		]);

		await contentsPage.saveContent();

		// Edit the experience for the structure and enable localization management

		await structureBuilderPage.editStructure(structureERC);

		await structureBuilderPage.customizeExperience();

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Allow Localization Management',
			fragmentId: await pageEditorPage.getFragmentId(
				'Localization Select'
			),
			tab: 'General',
			value: true,
		});

		// Also remove Friendly URL input

		await pageEditorPage.deleteFragment(
			await pageEditorPage.getFragmentId('Friendly URL')
		);

		await pageEditorPage.publishPage();

		// Edit the content again

		await contentsPage.goto();

		await contentsPage.editContent(contentTitle);

		// Switch to spanish and check option is disabled when values are empty

		await localizationSelectPage.switchLanguage('es-ES');

		const option = page.getByRole('menuitem', {
			name: 'Reset Translation',
		});

		await clickAndExpectToBeVisible({
			target: option,
			trigger: localizationSelectPage.actionsDropdownTrigger,
		});

		await expect(option).toBeDisabled();

		// Mark language as translated

		await localizationSelectPage.markAsTranslated('es-ES');

		// Save content, edit it again and check values were persisted for spanish

		await contentsPage.saveContent();

		await contentsPage.goto();

		await contentsPage.editContent(contentTitle);

		expect(await localizationSelectPage.getLanguageStatus('es-ES')).toBe(
			'translated'
		);

		// Reset values for spanish, save content and edit again to check values were deleted

		await localizationSelectPage.resetTranslation('es-ES');

		await contentsPage.saveContent();

		await contentsPage.goto();

		await contentsPage.editContent(contentTitle);

		expect(await localizationSelectPage.getLanguageStatus('es-ES')).toBe(
			'not-translated'
		);

		// Delete content

		await contentsPage.goto();

		await contentsPage.deleteContent(contentTitle);
	}
);

test(
	'Can apply auto translation for a language',
	{
		tag: ['@LPD-52077'],
	},
	async ({
		contentsPage,
		localizationSelectPage,
		page,
		pageEditorPage,
		structureBuilderPage,
		systemSettingsPage,
	}) => {
		const setAutoTranslation = async ({
			enabled,
			key,
		}: {
			enabled: boolean;
			key?: string;
		}) => {
			await systemSettingsPage.goToSystemSetting(
				'Translation',
				'Translator Using Google Cloud'
			);

			if (enabled) {
				await page.getByLabel('Enabled').check();
			}
			else {
				await page.getByLabel('Enabled').uncheck();
			}

			if (key) {
				await page.getByLabel('Service Account Private Key').fill(key);
			}

			await expect(async () => {
				await page
					.getByRole('button', {name: 'Update'})
					.or(page.getByRole('button', {name: 'Save'}))
					.click({timeout: 500});

				await waitForAlert(page, 'completed successfully', {
					timeout: 2000,
				});
			}).toPass();
		};

		// Create a CMS structure

		const structureERC = getRandomString();
		const structureLabel = getRandomString();

		await structureBuilderPage.createStructureFromData({
			erc: structureERC,
			label: structureLabel,
			page: structureBuilderPage,
		});

		// Add a Long Text field

		await structureBuilderPage.addField('Long Text');

		await structureBuilderPage.publishStructure();

		// Go to view mode (create a content) and fill values in default language and save the content

		await contentsPage.goto();

		await contentsPage.createContent(structureLabel);

		const contentTitle = 'Orange';

		await contentsPage.fillData([
			{label: 'Title', value: contentTitle},
			{label: 'Long Text', value: 'Papa'},
		]);
		await contentsPage.saveContent();

		// Edit the experience for the structure and enable localization management

		await structureBuilderPage.editStructure(structureERC);

		await structureBuilderPage.customizeExperience();

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Allow Localization Management',
			fragmentId: await pageEditorPage.getFragmentId(
				'Localization Select'
			),
			tab: 'General',
			value: true,
		});

		// Also remove Friendly URL input

		await pageEditorPage.deleteFragment(
			await pageEditorPage.getFragmentId('Friendly URL')
		);

		await pageEditorPage.publishPage();

		// Edit the content again

		await contentsPage.goto();

		await contentsPage.editContent(contentTitle);

		// Switch to spanish and check option does not appear because no translator is enabled

		await localizationSelectPage.switchLanguage('es-ES');

		await clickAndExpectToBeVisible({
			target: page.getByRole('menuitem', {
				name: 'Mark as Translated',
			}),
			trigger: localizationSelectPage.actionsDropdownTrigger,
		});

		await expect(
			page.getByRole('menuitem', {
				name: 'Auto Translate',
			})
		).not.toBeVisible();

		// Enable auto translation with Google Cloud with fake key

		await setAutoTranslation({
			enabled: true,
			key: '{"type": "service_account"}',
		});

		// Edit content again

		await contentsPage.goto();

		await contentsPage.editContent(contentTitle);

		// Mock URL and perform auto translate

		await page.route('**/o/translation/auto_translate', async (route) => {
			await route.fulfill({
				body: JSON.stringify({
					fields: {
						ObjectField_longtext: 'Naranja',
						ObjectField_title: 'Naranja',
					},
				}),
				contentType: 'application/json',
				status: 200,
			});
		});

		await localizationSelectPage.autoTranslate('es-ES');

		await expect(page.getByLabel('Title')).toHaveValue('Naranja');
		await expect(page.getByLabel('Long Text')).toHaveValue('Naranja');

		// Delete content

		await contentsPage.goto();

		await contentsPage.deleteContent(contentTitle);

		// Disable auto translation

		await setAutoTranslation({
			enabled: false,
		});
	}
);
