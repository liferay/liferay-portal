/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {WebContentPage} from '../../../pages/journal-web/WebContentPage';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {JournalTemplatesPage} from '../../journal-web/main/pages/JournalTemplatesPage';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';

const test = mergeTests(dataApiHelpersTest, journalPagesTest, loginTest());

test(
	'Web content can be edited, moved into an edited folder and deleted in bulk in a depot',
	{tag: '@LPD-109574'},
	async ({
		apiHelpers,
		journalEditArticlePage,
		journalEditFolderPage,
		journalPage,
		page,
	}) => {
		const depot =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		const depotURL = `/asset-library-${depot.id}`;

		await test.step('Add and edit a web content', async () => {
			await journalEditArticlePage.goto({siteUrl: depotURL});

			// A depot web content has no display page

			await expect(
				page.getByRole('button', {exact: true, name: 'Display Page'})
			).toHaveCount(0);

			const title = getRandomString();

			await journalEditArticlePage.fillTitle(title);
			await journalEditArticlePage.fillContent(getRandomString());

			await journalEditArticlePage.publishArticle();

			// The display style is a preference of the user

			await journalPage.changeView('list');

			await journalEditArticlePage.editArticle(title);

			const editedContent = getRandomString();
			const editedTitle = getRandomString();

			await journalEditArticlePage.fillTitle(editedTitle);
			await journalEditArticlePage.fillContent(editedContent);

			await journalEditArticlePage.publishArticle(true);

			await journalEditArticlePage.editArticle(editedTitle);

			await expect(journalEditArticlePage.titleInput).toHaveValue(
				editedTitle
			);
			await expect(journalPage.articleContentTextBox).toContainText(
				editedContent
			);
		});

		await test.step('Edit a folder and move a web content into it', async () => {
			const folder = await apiHelpers.jsonWebServicesJournal.addFolder({
				groupId: depot.siteId,
			});
			const webContentTitle = getRandomString();

			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
				groupId: depot.siteId,
				titleMap: {en_US: webContentTitle},
			});

			await journalPage.goto(depotURL);

			await journalEditFolderPage.editFolder(folder.name);

			const editedFolderName = getRandomString();

			await journalEditFolderPage.title.fill(editedFolderName);

			await page.getByRole('button', {exact: true, name: 'Save'}).click();

			await waitForAlert(page);

			await expect(
				page.getByRole('link', {exact: true, name: editedFolderName})
			).toBeVisible();

			await journalPage.changeView('Table');

			await page
				.getByRole('row')
				.filter({hasText: webContentTitle})
				.getByRole('checkbox')
				.check();

			await journalPage.moveToFolder(editedFolderName);

			await journalPage.goto(depotURL);

			await expect(
				page.getByRole('link', {exact: true, name: webContentTitle})
			).toHaveCount(0);

			await page
				.getByRole('link', {exact: true, name: editedFolderName})
				.click();

			await page.waitForURL(/folderId/);

			await expect(
				page.getByRole('link', {exact: true, name: webContentTitle})
			).toBeVisible();

			await journalPage.goto(depotURL);

			await new WebContentPage(page).moveFolderToRecycleBin(
				editedFolderName
			);
		});

		await test.step('Delete web content in bulk', async () => {
			for (let i = 0; i < 3; i++) {
				await apiHelpers.jsonWebServicesJournal.addWebContent({
					ddmStructureId:
						await getBasicWebContentStructureId(apiHelpers),
					groupId: depot.siteId,
					titleMap: {en_US: getRandomString()},
				});
			}

			await journalPage.goto(depotURL);

			await page.getByLabel('Select All Items on the Page').check();

			await journalPage.deleteSelection();

			await expect(
				page.getByText('No web content was found.')
			).toBeVisible();
		});
	}
);

test(
	'Depot structures and templates can be edited and deleted',
	{tag: '@LPD-109574'},
	async ({
		apiHelpers,
		journalEditStructureDefaultValuesPage,
		journalEditTemplatePage,
		journalStructuresPage,
		page,
	}) => {
		const depot =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary();

		const depotURL = `/asset-library-${depot.id}`;

		const fieldName = 'Headline';
		const structureName = getRandomString();

		await apiHelpers.dataEngine.createStructure(
			depot.siteId,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{name: fieldName}],
				name: structureName,
			})
		);

		const journalTemplatesPage = new JournalTemplatesPage(page);

		await test.step('Add and edit a template', async () => {
			await journalEditTemplatePage.goto(depotURL);

			const templateName = getRandomString();

			await journalEditTemplatePage.editTemplate(
				templateName,
				`\${${fieldName}}`
			);
			await journalEditTemplatePage.selectStructure(structureName);
			await journalEditTemplatePage.saveTemplate();

			await journalTemplatesPage.goto(depotURL);

			await journalEditTemplatePage.selectTemplateToEdit(templateName);

			const editedTemplateName = getRandomString();

			await journalEditTemplatePage.editTemplate(
				editedTemplateName,
				`\${${fieldName}}`
			);
			await journalEditTemplatePage.saveTemplate();

			await journalTemplatesPage.goto(depotURL);

			await expect(
				page.getByRole('link', {exact: true, name: editedTemplateName})
			).toBeVisible();
			await expect(
				page.getByRole('link', {exact: true, name: templateName})
			).toHaveCount(0);
		});

		await test.step('Edit the structure default values', async () => {
			await journalEditStructureDefaultValuesPage.goto({
				siteUrl: depotURL,
				structureName,
			});

			const defaultValue = getRandomString();

			await journalEditStructureDefaultValuesPage.fillTextField(
				fieldName,
				defaultValue
			);
			await journalEditStructureDefaultValuesPage.save();

			await journalEditStructureDefaultValuesPage.goto({
				siteUrl: depotURL,
				structureName,
			});

			await expect(
				page.getByRole('textbox', {exact: true, name: fieldName})
			).toHaveValue(defaultValue);
		});

		await test.step('Delete the template and the structure', async () => {
			await journalTemplatesPage.goto(depotURL);

			page.once('dialog', (dialog) => dialog.accept());

			await page.getByRole('button', {name: 'More actions'}).click();

			await page.getByRole('menuitem', {name: 'Delete'}).click();

			await expect(
				page.getByText('There are no templates.')
			).toBeVisible();

			await journalStructuresPage.goto(depotURL);

			page.once('dialog', (dialog) => dialog.accept());

			await journalStructuresPage.goToJournalStructureAction(
				'Delete',
				structureName
			);

			await expect(
				page.getByText('There are no structures.')
			).toBeVisible();
		});
	}
);
