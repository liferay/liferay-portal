/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {APIResponse, expect as baseExpect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../fixtures/applicationsMenuPageTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import {pagesAdminPagesTest} from '../../fixtures/pagesAdminPagesTest';
import {systemSettingsPageTest} from '../../fixtures/systemSettingsPageTest';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import fillAndClickOutside from '../../utils/fillAndClickOutside';
import getRandomString from '../../utils/getRandomString';
import {openFieldset} from '../../utils/openFieldset';
import {waitForAlert} from '../../utils/waitForAlert';
import {journalPagesTest} from './fixtures/journalPagesTest';
import getDataStructureDefinition from './utils/getDataStructureDefinition';

const expect = baseExpect.extend({
	toBeSuccessful: (response: APIResponse) => ({
		message: () =>
			response.ok()
				? 'Response is successful'
				: 'Response is not successful',
		pass: response.ok(),
	}),
});

const autoSaveTest = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	featureFlagsTest({
		'LPD-11228': {enabled: true},
	}),
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	pagesAdminPagesTest,
	systemSettingsPageTest
);

const autosaveWithoutPermissionsTest = mergeTests(
	featureFlagsTest({
		'LPD-11228': {enabled: true},
	}),
	isolatedSiteTest,
	journalPagesTest,
	loginTest()
);

autoSaveTest(
	'UndoRedo Should not appear when editing default values',
	{
		tag: '@LPD-36442',
	},
	async ({
		apiHelpers,
		journalEditArticlePage,
		journalEditStructureDefaultValuesPage,
		site,
	}) => {
		const fieldName = 'Text1';
		const structureName = 'Structure1';

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [{name: fieldName, repeatable: true}],
			name: structureName,
		});

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		await journalEditStructureDefaultValuesPage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		expect(journalEditArticlePage.undoButton).not.toBeVisible();
		expect(journalEditArticlePage.redoButton).not.toBeVisible();
	}
);

autoSaveTest(
	'Default Language can be changed when editing default values',
	{
		tag: '@LPD-38269',
	},
	async ({
		apiHelpers,
		journalEditStructureDefaultValuesPage,
		page,
		site,
		systemSettingsPage,
	}) => {
		await systemSettingsPage.goToSystemSetting(
			'Web Content',
			'Administration'
		);

		await page.getByLabel('Changeable Default Language').check();

		await page.getByRole('button', {name: /save|update/i}).click();

		await waitForAlert(page);

		const fieldName = 'Text1';
		const structureName = 'Structure1';

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [{name: fieldName, repeatable: true}],
			name: structureName,
		});

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		await journalEditStructureDefaultValuesPage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		const defaultLanguageButton = page
			.getByRole('group', {name: 'Basic Information'})
			.getByRole('button', {name: 'Change'});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'ca_ES',
			}),
			trigger: defaultLanguageButton,
		});

		await page.getByLabel('Select a language, current').click();

		await expect(
			page.getByRole('option', {name: 'Catalan Language: Default'})
		).toBeVisible();
	}
);

autoSaveTest(
	'Info message appears when autosave is failed due to missing required fields',
	{
		tag: '@LPD-34375',
	},
	async ({journalEditArticlePage, page, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		await expect(async () => {
			await journalEditArticlePage.fillTitle(getRandomString());

			await expect(journalEditArticlePage.undoButton).toBeEnabled();
		}).toPass();

		const savedIndicator = await page.locator(
			'#_com_liferay_journal_web_portlet_JournalPortlet_changesSavedIndicator'
		);

		await expect(savedIndicator).toBeVisible();

		const historyButton = journalEditArticlePage.historyButton;

		await historyButton.click();

		await page.getByRole('menuitem', {name: 'Undo All'}).click();

		const errorIndicator = await page.locator(
			'#_com_liferay_journal_web_portlet_JournalPortlet_lockErrorIndicator'
		);

		await expect(errorIndicator).toBeVisible();
	}
);

autoSaveTest(
	'LockIndicator should have an errorState',
	{
		tag: '@LPD-26854',
	},
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const localizableFieldName = 'Text56789';
		const structureName = 'Structure 2';

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [{name: localizableFieldName, repeatable: false}],
			name: structureName,
		});

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		const localizableField = page.getByRole('textbox', {
			name: localizableFieldName,
		});

		await fillAndClickOutside(page, localizableField);

		const errorIndicator = await page.locator(
			'#_com_liferay_journal_web_portlet_JournalPortlet_lockErrorIndicator'
		);

		await expect(errorIndicator).toBeVisible();
	}
);

autoSaveTest(
	'Web content should be saved as draft after changing the title and the content',
	{
		tag: '@LPD-26856',
	},
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const localizableFieldName = 'Text56789';
		const structureName = 'Structure 2';
		const title = getRandomString();
		const content = getRandomString();

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [{name: localizableFieldName, repeatable: false}],
			name: structureName,
		});

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		await journalEditArticlePage.fillTitle(title);

		const localizableField = page.getByRole('textbox', {
			name: localizableFieldName,
		});

		await fillAndClickOutside(page, localizableField, content);

		await expect(
			journalEditArticlePage.changesSavedIndicator
		).toBeVisible();

		await page.getByTitle('Go to Web Content').click();

		await expect(
			page.getByRole('heading', {name: 'Web Content'})
		).toBeVisible();

		const article = page.getByRole('link', {name: title});

		article.waitFor();

		article.click();

		await expect(page.getByRole('heading', {name: title})).toBeVisible();

		await expect(page.getByLabel(localizableFieldName)).toHaveValue(
			content,
			{timeout: 1000}
		);
	}
);

autoSaveTest(
	'Translation is removed when using Undo and restored when using Redo',
	{
		tag: '@LPD-31072',
	},
	async ({journalEditArticlePage, page, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		await journalEditArticlePage.fillContent(getRandomString());

		const translationButton = page.getByLabel('Select a language, current');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Catalan Language: Not Translated',
			}),
			trigger: translationButton,
		});

		await journalEditArticlePage.fillContent(getRandomString());

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('option', {
				name: 'Catalan Language: Translating 1/2',
			}),
			trigger: translationButton,
		});

		await journalEditArticlePage.undoButton.click();

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('option', {
				name: 'Catalan Language: Not Translated',
			}),
			trigger: translationButton,
		});

		await journalEditArticlePage.redoButton.click();

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('option', {
				name: 'Catalan Language: Translating 1/2',
			}),
			trigger: translationButton,
		});
	}
);

autoSaveTest(
	'Undo/Redo buttons works',
	{
		tag: '@LPD-26863',
	},
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const localizableFieldName = 'Text56789';
		const structureName = 'Structure undo/redo';

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [{name: localizableFieldName, repeatable: false}],
			name: structureName,
		});

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		const title = getRandomString();

		const localizableField = await page.getByRole('textbox', {
			name: localizableFieldName,
		});

		await expect(async () => {
			await journalEditArticlePage.fillTitle(title);

			await expect(journalEditArticlePage.undoButton).toBeEnabled();
		}).toPass();

		await fillAndClickOutside(page, localizableField, title);

		await journalEditArticlePage.undoButton.click();

		await expect(localizableField).toHaveValue('');

		await journalEditArticlePage.undoButton.click();

		await expect(journalEditArticlePage.undoButton).toBeDisabled();

		await expect(journalEditArticlePage.titleInput).toHaveValue('');

		await waitForAlert(page, 'Info:Please complete all', {type: 'info'});

		await journalEditArticlePage.redoButton.click();

		await expect(journalEditArticlePage.titleInput).toHaveValue(title);

		await journalEditArticlePage.redoButton.click();

		await expect(journalEditArticlePage.redoButton).toBeDisabled();

		await expect(localizableField).toHaveValue(title);
	}
);

autoSaveTest(
	'History button test',
	{
		tag: '@LPD-31063',
	},
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const textFieldName = 'Text56789';
		const structureName = 'Structure undo/redo';

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [{name: textFieldName, repeatable: false}],
			name: structureName,
		});

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		const historyButton = journalEditArticlePage.historyButton;
		await expect(historyButton).toBeDisabled();

		const friendlyURL = 'web-content-title';
		const text = 'Web Content Content';
		const title = 'Web Content Title';

		await expect(async () => {
			await journalEditArticlePage.fillTitle(title);
			await expect(historyButton).toBeEnabled();
		}).toPass();

		await historyButton.click();

		await expect(
			page.getByRole('menuitem', {name: 'Undo All'})
		).toHaveClass('dropdown-item');

		await expect(
			page.getByRole('menuitem', {name: 'Edit Title'})
		).toHaveClass('dropdown-item active');

		await expect(async () => {
			await journalEditArticlePage.fillFriendlyURL(friendlyURL);

			await historyButton.click();

			await expect(
				page.getByRole('menuitem', {name: 'Edit Title'})
			).toHaveClass('dropdown-item');
		}).toPass();

		await expect(
			page.getByRole('menuitem', {name: 'Edit Friendly URL'})
		).toHaveClass('dropdown-item active');

		const textField = page.getByLabel(textFieldName);

		await fillAndClickOutside(page, textField, text);

		const translationButton = page.getByLabel('Select a language, current');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Catalan Language: Not',
			}),
			trigger: translationButton,
		});

		await expect(async () => {
			await journalEditArticlePage.fillTitle(getRandomString());

			await historyButton.click();

			await expect(
				page.getByRole('menuitem', {name: 'Change Language'})
			).toHaveClass('dropdown-item');

			await page.getByRole('menuitem', {name: 'Undo All'}).click();

			await expect(
				page.locator(
					'#_com_liferay_journal_web_portlet_JournalPortlet_lockErrorIndicator'
				)
			).toBeVisible();
		}).toPass();

		await expect(journalEditArticlePage.friendlyURLInput).toBeEmpty();

		await expect(journalEditArticlePage.titleInput).toBeEmpty();

		await expect(textField).toBeEmpty();

		await historyButton.click();

		await page
			.getByRole('menuitem', {name: 'Edit ' + textFieldName})
			.click();

		await expect(journalEditArticlePage.friendlyURLInput).toHaveValue(
			friendlyURL
		);

		await expect(journalEditArticlePage.titleInput).toHaveValue(title);

		await expect(textField).toHaveValue(text);
	}
);

autoSaveTest(
	'Create a web content selecting permissions in the modal',
	{
		tag: '@LPD-32949',
	},
	async ({journalEditArticlePage, journalPage, page, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const title = getRandomString();

		await journalEditArticlePage.fillTitle(title);

		await expect(
			journalEditArticlePage.changesSavedIndicator
		).toBeVisible();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'Publish With Permissions',
			}),
			trigger: page.getByRole('button', {
				name: 'Select and Confirm Publish Settings',
			}),
		});

		await page.getByLabel('Viewable by').selectOption('Site Members');

		await page.getByRole('button', {exact: true, name: 'Publish'}).click();

		await expect(page.getByTitle(title)).toBeVisible();

		await journalPage.assertJournalArticlePermissions(title, [
			{enabled: false, locator: '#guest_ACTION_VIEW'},
		]);
	}
);

autoSaveTest(
	'Web Content version, status and ID are shown and updated after auto save',
	{
		tag: '@LPD-32874',
	},
	async ({journalEditArticlePage, page, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const title = getRandomString();

		await journalEditArticlePage.fillTitle(title);

		await journalEditArticlePage.changesSavedIndicator.waitFor();

		await openFieldset(page, 'Basic Information');

		await expect(page.getByText('1.0')).toBeVisible();

		await expect(page.getByText('Draft', {exact: true})).toBeVisible();

		await expect(page.getByText('ID', {exact: true})).toBeVisible();

		await journalEditArticlePage.publishArticle();

		await page.getByLabel(`Actions for ${title}`).waitFor();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				exact: true,
				name: 'Edit',
			}),
			trigger: page.getByLabel(`Actions for ${title}`, {
				exact: true,
			}),
		});

		await expect(async () => {
			await journalEditArticlePage.fillTitle(getRandomString());

			await expect(
				journalEditArticlePage.changesSavedIndicator
			).toBeVisible();
		}).toPass();

		await openFieldset(page, 'Basic Information');

		await expect(page.getByText('1.1')).toBeVisible();

		await expect(page.getByText('Draft', {exact: true})).toBeVisible();
	}
);

autoSaveTest(
	'Autosave is not enabled until all required fields are completed',
	{
		tag: '@LPD-34923',
	},
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const requiredFieldName = 'RequiredTextField';
		const structureName = 'Structure';

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [{name: requiredFieldName, required: true}],
			name: structureName,
		});

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		await journalEditArticlePage.fillFriendlyURL(getRandomString());

		await page.locator('body').click();

		const errorIndicator = await page.locator(
			'#_com_liferay_journal_web_portlet_JournalPortlet_lockErrorIndicator'
		);

		await expect(errorIndicator).toBeVisible();

		await journalEditArticlePage.fillTitle(getRandomString());

		await expect(errorIndicator).toBeVisible();

		const requiredField = page.getByRole('textbox', {
			name: requiredFieldName,
		});

		await fillAndClickOutside(page, requiredField);

		await expect(
			journalEditArticlePage.changesSavedIndicator
		).toBeVisible();
	}
);

autoSaveTest(
	'Empty option restores in Select from List when using undo/redo',
	{
		tag: '@LPD-35631',
	},
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const fieldName = 'SelectFromList';
		const structureName = 'Structure 1';

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [
				{
					fieldType: 'select',
					name: fieldName,
					options: {
						en_US: [
							{
								label: 'option1',
								reference: 'option1',
								value: 'option1',
							},
							{
								label: 'option2',
								reference: 'option2',
								value: 'option2',
							},
						],
					},
				},
			],
			name: structureName,
		});

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		await page.getByLabel(fieldName).click();

		await page.getByRole('option', {name: 'option1'}).click();

		await journalEditArticlePage.undoButton.click();

		await expect(page.getByLabel(fieldName)).toHaveText('Choose an Option');
	}
);

autosaveWithoutPermissionsTest(
	'Web Content is published when Feature Flag LPD-11228 is enabled',
	{
		tag: '@LPD-37606',
	},
	async ({journalEditArticlePage, page, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const articleTitle = 'Web Content Title';

		journalEditArticlePage.createAndPublishBasicArticle(articleTitle);

		await expect(page.getByTitle(articleTitle)).toBeVisible();
	}
);

autosaveWithoutPermissionsTest(
	'Web Content publish button is enabled after required error messages appear',
	{
		tag: '@LPD-40531',
	},
	async ({journalEditArticlePage, page, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const articleTitle = 'Web Content Title';

		await journalEditArticlePage.page
			.getByRole('button', {
				name: 'select and confirm publish settings',
			})
			.click();
		await journalEditArticlePage.page
			.getByRole('menuitem', {
				name: 'publish with permissions',
			})
			.click();

		await expect(
			page.getByText('The Title field is required.')
		).toBeVisible();

		await journalEditArticlePage.fillTitle(articleTitle);

		await journalEditArticlePage.publishArticle();

		await expect(page.getByTitle(articleTitle)).toBeVisible();
	}
);
