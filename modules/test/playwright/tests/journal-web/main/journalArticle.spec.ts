/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {APIResponse, expect as baseExpect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {createCategories} from '../../../helpers/CreateCategories';
import {SystemSettingsPage} from '../../../pages/configuration-admin-web/SystemSettingsPage';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import fillAndClickOutside from '../../../utils/fillAndClickOutside';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {openFieldset} from '../../../utils/openFieldset';
import {nextPage, setItemsPerPage} from '../../../utils/pagination';
import addApprovedStructuredContent from '../../../utils/structured-content/addApprovedStructuredContent';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {ClassicPage as CKEditor4ClassicPage} from '../../frontend-editor-ckeditor4-sample-web/pages/ClassicPage';
import {journalPagesTest} from './fixtures/journalPagesTest';
import getDataStructureDefinition from './utils/getDataStructureDefinition';

const translateNameAndMetadataFields = async (
	page,
	structureName = 'Basic Web Content'
) => {
	await fillAndClickOutside(
		page,
		page.getByLabel('Friendly URL', {exact: true})
	);
	await fillAndClickOutside(
		page,
		page.getByPlaceholder(`Untitled ${structureName}`)
	);
	await fillAndClickOutside(
		page,
		page.getByText('Description Rich Text Editor').getByRole('textbox')
	);
};

const setWebContentScope = async (iframe, scope) => {
	await clickAndExpectToBeVisible({
		autoClick: true,
		target: iframe.getByRole('menuitem', {exact: true, name: scope}),
		timeout: 3000,
		trigger: iframe.getByLabel('Filter', {exact: true}),
	});
};

const baseTest = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	pageViewModePagesTest,
	pagesAdminPagesTest,
	systemSettingsPageTest,
	workflowPagesTest
);

const bulkTest = mergeTests(baseTest);

const expect = baseExpect.extend({
	toBeSuccessful: (response: APIResponse) => ({
		message: () =>
			response.ok()
				? 'Response is successful'
				: 'Response is not successful',
		pass: response.ok(),
	}),
});

const assetPublisherDeprecationTest = mergeTests(
	baseTest,
	featureFlagsTest({
		'LPD-39304': {enabled: true},
	})
);

const ckeditor4Test = mergeTests(
	baseTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
	})
);

const ckeditor5Test = mergeTests(
	baseTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
	})
);

const keepTitlesUntranslated = mergeTests(baseTest);

const prefixUrlTest = mergeTests(baseTest);

const translationAndAutosaveTest = mergeTests(
	baseTest,
	featureFlagsTest({
		'LPD-11228': {enabled: true},
	})
);

const privateContentIconTest = mergeTests(baseTest);

baseTest(
	'Check permissions when only Owner was given permissions',
	{
		tag: '@LPD-68086',
	},
	async ({journalEditArticlePage, journalPage, page, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const title = getRandomString();

		await journalEditArticlePage.fillTitle(title);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: /publish with permissions/i,
			}),
			trigger: journalEditArticlePage.publishDropdown,
		});

		await journalEditArticlePage.inputPermissionsViewRole.selectOption(
			'Owner'
		);

		await page
			.locator(
				'#_com_liferay_journal_web_portlet_JournalPortlet_guestPermissions_ADD_DISCUSSION'
			)
			.uncheck();

		await page
			.locator(
				'#_com_liferay_journal_web_portlet_JournalPortlet_groupPermissions_ADD_DISCUSSION'
			)
			.uncheck();

		await page
			.getByLabel('Publish With Permissions')
			.getByRole('button', {name: 'Publish'})
			.click();

		await journalPage.assertJournalArticlePermissions(title, [
			{enabled: false, locator: '#guest_ACTION_ADD_DISCUSSION'},
			{enabled: false, locator: '#site-member_ACTION_ADD_DISCUSSION'},
		]);
	}
);

baseTest(
	' Published After Draft can schedule',
	{
		tag: '@LPD-70137',
	},
	async ({journalEditArticlePage, page, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		await journalEditArticlePage.saveAsDraftWithPermissions(
			getRandomString()
		);

		await journalEditArticlePage.publishDropdown.click();

		await page
			.getByRole('menuitem', {name: 'Schedule Publication'})
			.click();

		await page.getByLabel('Date and Time').fill('9987-11-26 01:00 PM');

		await page
			.getByLabel('Schedule Publication')
			.getByRole('button', {name: 'Schedule'})
			.click();

		await expect(
			page.locator('span').filter({hasText: 'Scheduled'}).nth(1)
		).toBeVisible();
	}
);

baseTest(
	'Check alert message of duplicated friendly URL in french',
	{
		tag: '@LPD-32185',
	},
	async ({journalEditArticlePage, page, site}) => {
		await page.goto('/fr');
		await journalEditArticlePage.createBasicArticleWithFriendlyURL(
			site,
			'Contenu web basique'
		);
		await journalEditArticlePage.createBasicArticleWithFriendlyURL(
			site,
			'Contenu web basique'
		);

		await waitForAlert(
			page,
			"Avertissement:Les URL simplifiées suivantes ont été modifiées pour garantir l'unicité",
			{closeText: 'Fermer', type: 'warning'}
		);

		// change back to english language

		await page.goto('/en');
	}
);

baseTest(
	'Check error message on invalid friendly URL',
	{
		tag: '@LPD-38754',
	},
	async ({journalEditArticlePage, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const title = getRandomString();

		await journalEditArticlePage.fillTitle(title);
		await journalEditArticlePage.fillFriendlyURL(title + '/' || 'test');
		await journalEditArticlePage.publishArticle();

		await expect(
			journalEditArticlePage.alertErrorMessage.getByText(
				'Please enter a friendly URL that does not end with a slash'
			)
		).toBeVisible();
	}
);

baseTest(
	'Check if Web Content can be saved as draft after changing default language',
	{
		tag: '@LPD-60603',
	},
	async ({journalEditArticlePage, page, site, systemSettingsPage}) => {
		await systemSettingsPage.goToSystemSetting(
			'Web Content',
			'Administration'
		);

		await page.getByLabel('Changeable Default Language').check();

		await page.getByRole('button', {name: /save|update/i}).click();

		await waitForAlert(page);

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		await journalEditArticlePage.changeDefaultLanguage('pt_BR');

		const title = getRandomString();

		await page.waitForTimeout(5000);

		await journalEditArticlePage.saveAsDraftWithPermissions(title);

		await waitForAlert(
			page,
			`Success:${title} was successfully saved as a draft.`
		);
	}
);

baseTest(
	'Check success message on save as draft',
	{
		tag: '@LPD-50230',
	},
	async ({journalEditArticlePage, page, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const title = getRandomString();
		await journalEditArticlePage.saveAsDraftWithPermissions(title);

		await waitForAlert(
			page,
			`Success:${title} was successfully saved as a draft.`
		);
	}
);

ckeditor5Test(
	'Check that upload field is marked as translated',
	{
		tag: '@LPD-66008',
	},
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const structureName = 'Test Structure';

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [
				{
					fieldType: 'document_library',
					name: 'Upload',
				},
			],
			name: structureName,
		});

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		const title = getRandomString();

		await journalEditArticlePage.fillTitle(title);

		await journalEditArticlePage.selectFileFromDocumentsAndMedia(
			'astronaut.png'
		);

		const translationButton = page.getByLabel('Select a language, current');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Catalan Language: Not Translated',
			}),
			trigger: translationButton,
		});

		await journalEditArticlePage.selectFileFromDocumentsAndMedia(
			'planet.png'
		);

		await translateNameAndMetadataFields(page, structureName);

		await journalEditArticlePage.publishArticle();

		await journalEditArticlePage.editArticle(title);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Catalan Language: Translated',
			}),
			trigger: translationButton,
		});
	}
);

baseTest(
	'Select web content display template with the Preview feature',
	{
		tag: '@LPD-31427',
	},
	async ({journalEditArticlePage, page, site}) => {
		page.on('dialog', (dialog) => dialog.accept());

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const title = getRandomString();

		await journalEditArticlePage.content.waitFor();

		await journalEditArticlePage.fillTitle(title);

		await journalEditArticlePage.publishArticle();

		await journalEditArticlePage.editArticle(title);

		await journalEditArticlePage.content.waitFor();

		await journalEditArticlePage.defaultTemplateButton.click();

		await journalEditArticlePage.clearButton.waitFor();

		await journalEditArticlePage.clearButton.click();

		await journalEditArticlePage.content.waitFor();

		let templateName = page.getByLabel('Template Name');

		await expect(templateName).toHaveValue('No Template');

		await journalEditArticlePage.defaultTemplateButton.click();

		const previewButton = page.locator(
			'[id="_com_liferay_journal_web_portlet_JournalPortlet_previewWithTemplate"]'
		);

		await previewButton.waitFor({state: 'attached'});

		await previewButton.scrollIntoViewIfNeeded();

		await previewButton.click({force: true});

		const dialog = page.getByRole('dialog');

		await expect(dialog.getByRole('heading')).toHaveText('Title');

		const dialogIFrame = page.frameLocator('iframe[title="Title"]');

		await dialogIFrame
			.getByTitle('ddm-template-id')
			.selectOption('Basic Web Content');

		await dialogIFrame.getByRole('button', {name: 'Apply'}).click();

		await journalEditArticlePage.content.waitFor();

		templateName = page.getByLabel('Template Name');

		await expect(templateName).toHaveValue('Basic Web Content');
	}
);

translationAndAutosaveTest(
	'Article selector should only list approved content',
	{
		tag: '@LPD-39264',
	},
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const fieldName = 'ArticleSelector';
		const structureName = 'Test Structure';

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [
				{
					fieldType: 'journal_article',
					name: fieldName,
					repeatable: false,
				},
			],
			name: structureName,
		});

		const selectableWebContent = 'selectable web content';

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: selectableWebContent},
		});

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const unSelectableWebContent = 'unselectable web content';

		await journalEditArticlePage.scheduleArticle(
			unSelectableWebContent,
			'9987-11-26 01:00 PM'
		);

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		await page
			.getByTestId('ArticleSelector')
			.getByRole('button', {name: 'Select'})
			.click();

		const articleSelectorIframe = page.frameLocator(
			'iframe[title="Web Content"]'
		);

		await expect(
			articleSelectorIframe.getByText(selectableWebContent)
		).toBeVisible();

		await expect(
			articleSelectorIframe.getByText(unSelectableWebContent)
		).toHaveCount(0);
	}
);

baseTest(
	'Web content draft in a connected asset library is not found when browsing everywhere',
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const fieldName = 'WebContentSelector';
		const structureName = getRandomString();
		const draftTitle = getRandomString();

		// A structure with a web content field opens the web content item
		// selector; connect an asset library whose only web content is a draft

		await apiHelpers.dataEngine.createStructure(
			site.id,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [{fieldType: 'journal_article', name: fieldName}],
				name: structureName,
			})
		);

		const depot =
			await apiHelpers.jsonWebServicesDepot.addDepotEntry(
				getRandomString()
			);

		try {
			await apiHelpers.jsonWebServicesDepotGroupRel.addDepotEntryGroupRel(
				depot.depotEntryId,
				String(site.id)
			);

			await apiHelpers.headlessAdminContent.postStructuredContentDraft({
				contentStructureId:
					await getBasicWebContentStructureId(apiHelpers),
				datePublished: '2026-01-01T00:00:00Z',
				siteId: String(depot.groupId),
				title: draftTitle,
			});

			// Open the web content item selector and browse everywhere

			await journalEditArticlePage.goto({
				siteUrl: site.friendlyUrlPath,
				structureName,
			});

			await page
				.getByTestId(fieldName)
				.getByRole('button', {name: 'Select'})
				.click();

			const iframe = page.frameLocator('iframe[title="Web Content"]');

			await setWebContentScope(iframe, 'Everywhere');

			// The draft never surfaces, so the library reports no web content

			await expect(
				iframe.getByText('No web content was found.')
			).toBeVisible();

			await expect(iframe.getByText(draftTitle)).toHaveCount(0);
		}
		finally {
			await apiHelpers.jsonWebServicesDepot.deleteDepotEntry(
				depot.depotEntryId
			);
		}
	}
);

baseTest(
	'Navigate in ddm template selector',
	{
		tag: '@LPD-36441',
	},
	async ({journalEditArticlePage, page, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		await journalEditArticlePage.defaultTemplateButton.click();

		await journalEditArticlePage.selectButton.waitFor();

		await journalEditArticlePage.selectButton.click();

		const breadcrumb = page
			.frameLocator('iframe[title="Templates"]')
			.getByRole('link', {name: 'Sites and Libraries'});

		await expect(breadcrumb).toBeVisible();
	}
);

baseTest(
	'Check multi select and single select Category selector',
	{
		tag: '@LPD-72738',
	},
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const category1 = getRandomString();
		const category2 = getRandomString();
		const vocabularyName1 = getRandomString();
		const vocabularyName2 = getRandomString();

		await baseTest.step('create vocabulary and category', async () => {
			await createCategories({
				apiHelpers,
				assetTypes: [{required: false, type: 'AllAssetTypes'}],
				categoryNames: [{name: category1}, {name: category2}],
				siteId: site.id,
				vocabularyName: vocabularyName1,
			});
		});

		await baseTest.step('create vocabulary and category', async () => {
			await createCategories({
				apiHelpers,
				assetTypes: [{required: false, type: 'AllAssetTypes'}],
				categoryNames: [{name: category1}, {name: category2}],
				multiValued: false,
				siteId: site.id,
				vocabularyName: vocabularyName2,
			});
		});

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const CategorizationButton = await page.getByRole('button', {
			name: 'Categorization',
		});

		const expandedAttribute =
			await CategorizationButton.getAttribute('aria-expanded');

		if (expandedAttribute === 'false') {
			await CategorizationButton.click();
		}

		await page
			.getByRole('button', {name: `Select ${vocabularyName1}`})
			.click();

		let categoryCheckbox = page
			.getByRole('dialog', {name: `Select ${vocabularyName1}`})
			.locator('li')
			.filter({hasText: `${category1}`})
			.getByRole('checkbox');

		await expect(categoryCheckbox).toBeVisible();

		await page.getByRole('button', {name: 'Cancel'}).click();

		await page
			.getByRole('button', {name: `Select ${vocabularyName2}`})
			.click();

		categoryCheckbox = page
			.getByRole('dialog', {name: `Select ${vocabularyName2}`})
			.locator('li')
			.filter({hasText: `${category1}`})
			.getByRole('checkbox');

		await expect(categoryCheckbox).toHaveCount(0);
	}
);

baseTest(
	'LPD-32979: Ensure the presence of the Description column when needed',
	async ({journalEditArticlePage, journalPage, page, site}) => {
		page.on('dialog', (dialog) => dialog.accept());

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		await page.getByText('Content', {exact: true}).waitFor();

		await journalEditArticlePage.fillTitle(getRandomString());

		await journalEditArticlePage.publishArticle();

		await page.getByLabel('Select View, Currently').click();

		await page.getByRole('menuitem', {name: 'Table'}).click();

		await expect(
			page.getByRole('cell', {name: 'Description'})
		).toBeVisible();

		await page
			.getByTestId('headerOptions')
			.getByLabel('Options')
			.and(page.locator('[aria-haspopup]'))
			.click();

		await page.getByRole('menuitem', {name: 'Configuration'}).click();

		await page.getByLabel('Select Highlighted Structures').click();

		const dialogFrame = page.frameLocator(
			'iframe[title="Select Structures"]'
		);

		await dialogFrame.getByLabel('Basic Web Content Global').click();

		await page.getByRole('button', {name: 'Add'}).click();

		await page.getByRole('button', {name: 'Save'}).click();

		await page.getByText('Success:You have successfully').waitFor();

		await journalPage.goto(site.friendlyUrlPath);

		await expect(page.getByRole('cell', {name: 'Description'})).toHaveCount(
			0
		);
	}
);

baseTest(
	'LPD-15248 Move folder to another folder via management toolbar',
	async ({apiHelpers, journalPage, page, site}) => {
		const childFolder = await apiHelpers.jsonWebServicesJournal.addFolder({
			groupId: site.id,
		});

		const parentFolder = await apiHelpers.jsonWebServicesJournal.addFolder({
			groupId: site.id,
		});

		await journalPage.goto(site.friendlyUrlPath);

		await page.getByLabel(`${childFolder.name}`).check();

		await journalPage.moveToFolder(parentFolder.name);

		await expect(page.getByText(`${childFolder.name}`)).toBeHidden();

		await page.getByRole('link', {name: `${parentFolder.name}`}).click();

		await expect(page.getByText(`${childFolder.name}`)).toBeVisible();
	}
);

baseTest(
	'Move web content to another folder via management toolbar',
	{
		tag: '@LPD-36955',
	},
	async ({apiHelpers, journalPage, page, site}) => {
		const folder = await apiHelpers.jsonWebServicesJournal.addFolder({
			groupId: site.id,
		});

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const title = getRandomString();

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: title},
		});

		await journalPage.goto(site.friendlyUrlPath);

		await expect(page.getByText(`${title}`)).toBeVisible();

		await page.getByRole('checkbox', {name: title}).check();

		await journalPage.moveToFolder(folder.name);

		await expect(page.getByText(`${title}`)).toBeHidden();

		await page.getByRole('link', {name: `${folder.name}`}).click();

		await expect(page.getByText(`${title}`)).toBeVisible();
	}
);

baseTest(
	'Select articles to move across multiple pages',
	{tag: '@LPD-19384'},
	async ({apiHelpers, journalPage, page, site}) => {
		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		for (let i = 0; i < 42; i++) {
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: contentStructureId,
				groupId: site.id,
			});
		}

		await journalPage.goto(site.friendlyUrlPath);

		await setItemsPerPage(page, 20);

		await expect(
			page.getByText('Showing 1 to 20 of 42 entries.')
		).toBeVisible();

		await page.getByTestId('row').nth(0).getByRole('checkbox').check();
		await page.getByTestId('row').nth(1).getByRole('checkbox').check();

		await nextPage(page);

		await expect(
			page.getByText('Showing 21 to 40 of 42 entries.')
		).toBeVisible();

		await page.getByTestId('row').nth(0).getByRole('checkbox').check();
		await page.getByTestId('row').nth(1).getByRole('checkbox').check();

		await nextPage(page);

		await expect(
			page.getByText('Showing 41 to 42 of 42 entries.')
		).toBeVisible();

		await page.getByTestId('row').nth(0).getByRole('checkbox').check();
		await page.getByTestId('row').nth(1).getByRole('checkbox').check();

		await page.getByRole('button', {name: 'Move'}).click();

		await expect(
			page.getByText('6 web content instances are ready to be moved.')
		).toBeVisible();
	}
);

baseTest(
	'LPD-31655: Ensure article action menu functions when viewing history in card view',
	async ({apiHelpers, journalPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const title = getRandomString();

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: title},
		});

		await journalPage.goto(site.friendlyUrlPath);

		await journalPage.changeView('list');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'View History'}),
			trigger: page.getByLabel(`Actions for ${title}`),
		});

		await journalPage.changeView('cards');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Compare to...'}),
			trigger: page.getByLabel(`More Actions`),
		});

		await expect(
			page.getByRole('heading', {name: 'Compare Versions'})
		).toBeVisible();
	}
);

keepTitlesUntranslated(
	'LPD-20723: Clay link is translating asset titles/names by default in vertical card',
	async ({apiHelpers, journalPage, page, site}) => {
		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const title = 'add-web-content';

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId,
			siteId: site.id,
			title,
		});

		await journalPage.goto(site.friendlyUrlPath);

		await journalPage.changeView('cards');

		await expect(page.getByRole('link', {name: title})).toBeVisible({
			timeout: 1000,
		});

		await journalPage.changeView('list');

		await expect(page.getByRole('link', {name: title})).toBeVisible({
			timeout: 1000,
		});

		await journalPage.changeView('table');

		await expect(page.getByRole('link', {name: title})).toBeVisible({
			timeout: 1000,
		});
	}
);

privateContentIconTest(
	'LPD-15807: Identify at a glance if a Web Content is visible for guests in content management',
	async ({apiHelpers, journalEditArticlePage, journalPage, site}) => {
		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const title = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId,
			siteId: site.id,
			title,
		});

		await journalPage.goto(site.friendlyUrlPath);

		await journalPage.assertPrivateContentIcon();

		await journalPage.changeView('table');

		await journalPage.assertPrivateContentIcon();

		await journalPage.changeView('list');

		await journalEditArticlePage.editArticle(title);

		await journalPage.assertPrivateContentIcon();
	}
);

privateContentIconTest(
	'LPD-15807: Identify at a glance if a Web Content is visible for guests in the item selector',
	async ({apiHelpers, journalEditArticlePage, journalPage, site}) => {
		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId,
			siteId: site.id,
			title: getRandomString(),
		});

		const title = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId,
			siteId: site.id,
			title,
		});

		await journalPage.goto(site.friendlyUrlPath);

		await journalEditArticlePage.editArticle(title);

		await journalEditArticlePage.openRelatedAsset();

		await journalEditArticlePage.assertPrivateContentIconInRelatedAssetPopUp(
			'Basic Web Content'
		);

		await journalEditArticlePage.changeViewInRelatedAssetPopUp(
			'Basic Web Content',
			'table'
		);

		await journalEditArticlePage.assertPrivateContentIconInRelatedAssetPopUp(
			'Basic Web Content'
		);
	}
);

prefixUrlTest(
	'LPD-6813: Make prefix URLs configurable',
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		friendlyUrlInstanceSettingsPage,
		page,
		site,
	}) => {
		const articleTitle = getRandomString();

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId,
			siteId: site.id,
			title: articleTitle,
		});

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentSubtype: 'Basic Web Content',
			contentType: 'Web Content Article',
			name: displayPageTemplateName,
		});

		await displayPageTemplatesPage.markAsDefault(displayPageTemplateName);

		await friendlyUrlInstanceSettingsPage.goto();

		const urlSeparator = 'web-content';

		await friendlyUrlInstanceSettingsPage.modifySeparator(
			'Web Content URL Separator',
			urlSeparator
		);

		expect(
			await page.request.get(
				'/group' +
					site.friendlyUrlPath +
					'/' +
					urlSeparator +
					'/' +
					articleTitle
			)
		).toBeSuccessful();

		await friendlyUrlInstanceSettingsPage.goto();

		await friendlyUrlInstanceSettingsPage.resetSeparator(
			'Web Content URL Separator'
		);
		expect(
			await page.request.get(
				'/group' + site.friendlyUrlPath + '/w/' + articleTitle
			)
		).toBeSuccessful();
	}
);

baseTest(
	'LPD-30412: This is a test for deleting multiple translations from a web content',
	async ({journalEditArticlePage, journalPage, page, site}) => {
		await journalPage.goto(site.friendlyUrlPath);

		const title = getRandomString();

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		await journalEditArticlePage.fillTitle(title);

		const translationButton = page.getByLabel('Select a language, current');

		for (const language of ['Finnish', 'French', 'German']) {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option', {
					name: language + ' Language: Not Translated',
				}),
				trigger: translationButton,
			});

			await journalEditArticlePage.fillContent(getRandomString());

			await clickAndExpectToBeVisible({
				autoClick: false,
				target: page.getByRole('option', {
					name: language + ' Language: Translating 1/',
				}),
				trigger: translationButton,
			});

			await page.keyboard.press('Escape');
		}

		await journalEditArticlePage.publishArticle();

		await waitForAlert(page, `Success:${title} was created successfully.`);

		await journalPage.goToJournalArticleAction(
			'Delete Translations',
			title
		);

		await page
			.frameLocator('iframe[title="Delete Translations"]')
			.getByLabel('français')
			.check();

		await page
			.frameLocator('iframe[title="Delete Translations"]')
			.getByLabel('Deutsch')
			.check();

		page.on('dialog', (dialog) => dialog.accept());

		await page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(page);
	}
);
baseTest(
	'It ensures that translate side by side shows the duplicate fields',
	{
		tag: '@LPS-142169',
	},
	async ({apiHelpers, journalEditArticlePage, journalPage, page, site}) => {
		const localizableFieldName = 'Text5678';
		const structureName = 'Structure 1';

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [{name: localizableFieldName, repeatable: true}],
			name: structureName,
		});

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		const title = getRandomString();
		await journalEditArticlePage.createArticleWithDuplicatedField(
			structureName,
			site,
			title
		);

		await journalPage.goToJournalArticleAction('Translate', title);

		const duplicateFields = page.locator(
			'[id^="_com_liferay_translation_web_internal_portlet_TranslationPortlet_infoField--DDMStructure_Text"]'
		);

		await duplicateFields.first().waitFor({state: 'visible'});

		expect(duplicateFields.nth(0)).toBeVisible();
		expect(duplicateFields.nth(1)).toBeVisible();
	}
);

baseTest(
	'This is a test for reset translations button in web content',
	{
		tag: '@LPD-13732',
	},
	async ({journalEditArticlePage, journalPage, page, site}) => {
		await journalPage.goto();

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		await journalEditArticlePage.fillTitle(getRandomString());

		const translationButton = page.getByRole('combobox', {
			name: 'Select a language',
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Catalan Language: Not Translated',
			}),
			trigger: translationButton,
		});

		await translateNameAndMetadataFields(page);

		await translationButton.click();

		await expect(
			page.getByRole('option', {
				name: 'Catalan Language: Translating 1/2',
			})
		).toBeVisible({timeout: 1000});

		const translationOptionsButton = page.getByLabel('Translation Options');

		await translationOptionsButton.click();

		const resetTranslationButton = page.getByRole('button', {
			name: 'Reset Translation',
		});

		await expect(resetTranslationButton).toBeEnabled();

		await resetTranslationButton.click();

		const deleteButton = page.getByRole('button', {name: 'Delete'});

		await deleteButton.click();

		await translationButton.click();

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('option', {
				name: 'Catalan Language: Not Translated',
			}),
			trigger: translationButton,
		});
	}
);

baseTest(
	'This is a test for mark as translated button in web content',
	{
		tag: '@LPD-23278',
	},
	async ({journalEditArticlePage, journalPage, page, site}) => {
		await journalPage.goto();

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		await journalEditArticlePage.fillTitle(getRandomString());

		const translationButton = page.getByRole('combobox', {
			name: 'Select a language',
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Catalan Language: Not Translated',
			}),
			trigger: translationButton,
		});

		const translationOptionsButton = page.getByLabel('Translation Options');

		await translationOptionsButton.click();

		const markAsTranslatedButton = page.getByRole('button', {
			name: 'Mark as Translated',
		});

		await markAsTranslatedButton.click();

		await expect(
			page.getByRole('heading', {name: 'Mark ca_ES as Translated'})
		).toBeVisible();

		await page.getByRole('button', {name: 'Mark as Translated'}).click();

		await translationButton.click();

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('option', {
				name: 'Catalan Language: Translated',
			}),
			trigger: translationButton,
		});

		await page.keyboard.press('Escape');

		await translationOptionsButton.click();

		await expect(markAsTranslatedButton).toBeDisabled();
	}
);

baseTest(
	'This is a test for translations filter button in web content',
	{
		tag: '@LPD-24942',
	},
	async ({journalEditArticlePage, journalPage, page, site}) => {
		await journalPage.goto();

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		await journalEditArticlePage.fillTitle(getRandomString());

		const translationButton = page.getByRole('combobox', {
			name: 'Select a language',
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Catalan Language: Not Translated',
			}),
			trigger: translationButton,
		});

		const translationFilterButton = page.getByRole('combobox', {
			name: 'Select a Filter',
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				exact: true,
				name: 'Translated',
			}),
			trigger: translationFilterButton,
		});

		const fieldsWrapper = page.getByRole('button', {name: 'Fields'});

		const metadataWapper = page.getByRole('button', {name: 'Metadata'});

		const noResultsWrapper = page.getByText('No Results Found');

		await expect(fieldsWrapper).toBeHidden();

		await expect(metadataWapper).toBeHidden();

		await expect(noResultsWrapper).toBeVisible();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'All Fields',
			}),
			trigger: translationFilterButton,
		});

		await journalEditArticlePage.fillTitle(getRandomString());

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				exact: true,
				name: 'Translated',
			}),
			trigger: translationFilterButton,
		});

		await expect(fieldsWrapper).toBeHidden();

		await expect(metadataWapper).toBeVisible();

		await expect(noResultsWrapper).toBeHidden();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Untranslated',
			}),
			trigger: translationFilterButton,
		});

		await expect(fieldsWrapper).toBeVisible();

		await expect(metadataWapper).toBeHidden();

		await expect(noResultsWrapper).toBeHidden();

		if (await journalPage.articleContentTextBox.isHidden()) {
			await fieldsWrapper.click();
		}
		await journalEditArticlePage.fillContent(getRandomString());

		// Re-apply the Untranslated filter to refresh the view. The active
		// filter option is inert (pointer-events: none), so toggle through
		// another option to reselect it.

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'All Fields',
			}),
			trigger: translationFilterButton,
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Untranslated',
			}),
			trigger: translationFilterButton,
		});

		await expect(fieldsWrapper).toBeHidden();

		await expect(metadataWapper).toBeHidden();

		await expect(noResultsWrapper).toBeVisible();
	}
);

baseTest(
	'Add error message in Translation for concurrent users',
	{
		tag: '@LPD-17245',
	},
	async ({
		apiHelpers,
		journalEditArticlePage,
		journalEditArticleTranslationsPage,
		journalPage,
		site,
	}) => {
		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const title = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId,
			siteId: site.id,
			title,
		});

		await journalPage.goto(site.friendlyUrlPath);

		const editBasicArticleTranslationUrl =
			await journalEditArticleTranslationsPage.editBasicArticleTranslations(
				title,
				''
			);

		await journalEditArticlePage.editAndPublishExistingBasicArticle(title);

		await journalEditArticleTranslationsPage.assertErrorInEditBasicArticleTranslations(
			editBasicArticleTranslationUrl
		);
	}
);

bulkTest(
	'LPD-17782: This is a test for bulk permissions of web content',
	async ({apiHelpers, journalPage, page, site}) => {
		const PERMISSIONS_LOCATORS = [
			{enabled: true, locator: '#guest_ACTION_DELETE'},
			{enabled: true, locator: '#guest_ACTION_PERMISSIONS'},
		];

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const title1 = getRandomString();
		const title2 = getRandomString();

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId,
			siteId: site.id,
			title: title1,
		});

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId,
			siteId: site.id,
			title: title2,
		});

		await journalPage.goto(site.friendlyUrlPath);
		await journalPage.changeView('list');

		const article1 = page
			.locator(
				'#_com_liferay_journal_web_portlet_JournalPortlet_articlesSearchContainer .list-group-item'
			)
			.filter({hasText: title1});

		const article2 = page
			.locator(
				'#_com_liferay_journal_web_portlet_JournalPortlet_articlesSearchContainer .list-group-item'
			)
			.filter({hasText: title2});

		await article1.waitFor();
		await article2.waitFor();

		await journalPage.setJournalArticlePermissions(
			[article1, article2],
			['#guest_ACTION_DELETE', '#guest_ACTION_PERMISSIONS']
		);

		await journalPage.assertJournalArticlePermissions(
			title1,
			PERMISSIONS_LOCATORS
		);
		await journalPage.assertJournalArticlePermissions(
			title2,
			PERMISSIONS_LOCATORS
		);
	}
);

baseTest(
	'Translate several fields in a Basic Web Content and check how many fields have been translated',
	{
		tag: '@LPD-19627',
	},
	async ({journalEditArticlePage, page, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		await journalEditArticlePage.fillTitle(getRandomString());

		const translationButton = page.getByRole('combobox', {
			name: 'Select a language',
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Catalan Language: Not Translated',
			}),
			trigger: translationButton,
		});

		await translateNameAndMetadataFields(page);

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('option', {
				name: 'Catalan Language: Translating 1/2',
			}),
			trigger: translationButton,
		});
	}
);

baseTest(
	'Translate the Rich Text field and check if the translation persists after coming back to the page',
	{
		tag: '@LPD-37236',
	},
	async ({journalEditArticlePage, journalPage, page, site}) => {
		await journalPage.goto();

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const title = getRandomString();

		await journalEditArticlePage.fillTitle(title);

		const englishContent = 'English Language Text';

		const catalanContent = 'Catalan Language Text';

		await journalEditArticlePage.fillContent(englishContent);

		const translationButton = page.getByRole('combobox', {
			name: 'Select a language',
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Catalan Language: Not Translated',
			}),
			trigger: translationButton,
		});

		await journalEditArticlePage.fillContent(catalanContent);

		await journalEditArticlePage.publishArticle();

		await waitForAlert(page, `Success:${title} was created successfully.`);

		await page.getByRole('link', {name: title}).click();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Catalan Language: Translating 1/2',
			}),
			trigger: translationButton,
		});

		await expect(
			page
				.getByTestId('content')
				.getByRole('textbox', {name: 'Rich Text Editor'})
				.getByText(catalanContent)
		).toBeVisible();
	}
);

baseTest(
	'LPD-19627: Translate all fields of a Web Content based on a custom structure with repeatable fields',
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const localizableFieldName = 'Text5678';
		const structureName = 'Structure 1';

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [{name: localizableFieldName, repeatable: true}],
			name: structureName,
		});

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		const translationButton = page.getByRole('combobox', {
			name: 'Select a language',
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Catalan Language: Not Translated',
			}),
			trigger: translationButton,
		});

		await translateNameAndMetadataFields(page, structureName);

		const localizableField = page.getByRole('textbox', {
			name: localizableFieldName,
		});

		// The Fields panel can load collapsed, so re-expand it before
		// interacting with its fields. Close the language dropdown after each
		// check so it does not cover the fields.

		await expect(async () => {
			await openFieldset(page, 'Fields');

			await localizableField.fill(getRandomString(), {timeout: 2000});
		}).toPass();

		await clickAndExpectToBeVisible({
			target: page.getByRole('option', {
				name: 'Catalan Language: Translated',
			}),
			timeout: 1000,
			trigger: translationButton,
		});

		await page.keyboard.press('Escape');

		await openFieldset(page, 'Fields');

		await page.getByLabel('Add Duplicate Field Text').click();

		await clickAndExpectToBeVisible({
			target: page.getByRole('option', {
				name: 'Catalan Language: Translating 2/3',
			}),
			timeout: 1000,
			trigger: translationButton,
		});

		await page.keyboard.press('Escape');

		await expect(async () => {
			await openFieldset(page, 'Fields');

			await page
				.locator('input.ddm-field-text')
				.nth(1)
				.fill(getRandomString(), {timeout: 2000});
		}).toPass();

		await clickAndExpectToBeVisible({
			target: page.getByRole('option', {
				name: 'Catalan Language: Translated',
			}),
			timeout: 1000,
			trigger: translationButton,
		});
	}
);

baseTest(
	'A non-localizabled field is disabled when another translation language is selected',
	{
		tag: '@LPD-19627',
	},
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const nonLocalizableFieldName = 'Text1234';
		const structureName = 'Structure 1';

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [{localizable: false, name: nonLocalizableFieldName}],
			name: structureName,
		});

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		const translationButton = page.getByRole('combobox', {
			name: 'Select a language',
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Catalan Language: Not Translated',
			}),
			trigger: translationButton,
		});

		const textBox = page.getByRole('textbox', {
			name: nonLocalizableFieldName,
		});

		await openFieldset(page, 'Fields');

		await checkAccessibility({page, selectors: ['.ddm-label']});

		await expect(textBox).toBeDisabled();
	}
);

baseTest(
	'A non-localizable field value is not deleted when switching and filtering from another translation',
	{
		tag: '@LPD-63134',
	},
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const localizableFieldName = 'LocalizedText';
		const nonLocalizableFieldName = 'Text';
		const structureName = 'Structure';

		await baseTest.step(
			'Create new structure with localizable and non-localizable fields',
			async () => {
				const dataDefinition = getDataStructureDefinition({
					defaultLanguageId: 'en_US',
					fields: [
						{localizable: true, name: localizableFieldName},
						{localizable: false, name: nonLocalizableFieldName},
					],
					name: structureName,
				});

				await apiHelpers.dataEngine.createStructure(
					site.id,
					dataDefinition
				);
			}
		);

		await baseTest.step(
			'Open new structure and fill both fields',
			async () => {
				await journalEditArticlePage.goto({
					siteUrl: site.friendlyUrlPath,
					structureName,
				});

				await page.getByLabel(localizableFieldName).fill('en-us');

				await page
					.getByLabel(nonLocalizableFieldName, {exact: true})
					.fill('test');
			}
		);

		const translationButton = page.getByRole('combobox', {
			name: 'Select a language',
		});

		await baseTest.step(
			'Switch language, translate localizable field and filter fields by translated',
			async () => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('option', {
						name: 'Catalan Language: Not Translated',
					}),
					trigger: translationButton,
				});

				await openFieldset(page, 'Fields');

				await page.getByLabel(localizableFieldName).fill('ca-es');

				const translationFilterButton = page.getByRole('combobox', {
					name: 'Select a Filter',
				});

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('option', {
						exact: true,
						name: 'Translated',
					}),
					trigger: translationFilterButton,
				});
			}
		);

		await baseTest.step(
			'Switch back to default language and assert that non localizable field value is still there',
			async () => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('option', {
						name: 'English Language: Default',
					}),
					trigger: translationButton,
				});

				await expect(
					page.getByLabel(nonLocalizableFieldName, {exact: true})
				).toHaveValue('test');
			}
		);
	}
);

baseTest(
	'LPD-29527 - Can delete translation of a web content created from a structure with at least one required and non-localizable field',
	async ({apiHelpers, journalEditArticlePage, journalPage, page, site}) => {
		const basicTextFieldName = 'Text1234';
		const content = getRandomString();
		const nonLocalizableFieldName = 'TextNonLocalizable';
		const structureName = 'Structure 1';
		const title = getRandomString();

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [
				{name: basicTextFieldName},
				{
					localizable: false,
					name: nonLocalizableFieldName,
					required: true,
				},
			],
			name: structureName,
		});

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		await journalEditArticlePage.fillTitle(title);

		await fillAndClickOutside(
			page,
			page.getByLabel(basicTextFieldName),
			content
		);

		await fillAndClickOutside(
			page,
			page.getByLabel(nonLocalizableFieldName),
			content
		);

		const translationButton = page.getByLabel('Select a language, current');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Catalan Language: Not Translated',
			}),
			trigger: translationButton,
		});

		await expect(async () => {
			await fillAndClickOutside(
				page,
				page.getByLabel(basicTextFieldName),
				content
			);

			await translationButton.click();

			await expect(
				page.getByRole('option', {
					name: 'Catalan Language: Translating 1/',
				})
			).toBeVisible({timeout: 2000});

			await page.keyboard.press('Escape');
		}).toPass();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'English Language: Default',
			}),
			trigger: translationButton,
		});

		await journalEditArticlePage.publishArticle();

		await waitForAlert(page, `Success:${title} was created successfully.`);

		await journalPage.goToJournalArticleAction(
			'Delete Translations',
			title
		);

		await page
			.frameLocator('iframe[title="Delete Translations"]')
			.getByLabel('català')
			.check();

		page.on('dialog', (dialog) => dialog.accept());

		await page.getByRole('button', {name: 'Delete'}).click();

		await journalEditArticlePage.editArticle(title);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'Catalan Language: Not',
			}),
			trigger: translationButton,
		});
	}
);

baseTest(
	'LPD-6800 Create AI Image option visible from Item Selector',
	async ({journalEditArticlePage, page, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		await journalEditArticlePage.openDMItemSelectorForImages();

		const DMItemSelectorPage = page.frameLocator(
			'iframe[title="Select Item"]'
		);

		await DMItemSelectorPage.getByRole('button', {name: 'New'}).click();
		await expect(
			DMItemSelectorPage.getByRole('menuitem', {name: 'Create AI Image'})
		).toBeVisible();
	}
);

baseTest(
	'LPD-28728: the value of a repeated field of an article is the same as the structure default value',
	async ({
		apiHelpers,
		journalEditArticlePage,
		journalEditStructureDefaultValuesPage,
		page,
		site,
	}) => {
		const fieldName = 'Text1';
		const structureName = 'Structure1';

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [{name: fieldName, repeatable: true}],
			name: structureName,
		});

		const structure = await apiHelpers.dataEngine.createStructure(
			site.id,
			dataDefinition
		);

		await journalEditStructureDefaultValuesPage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		const content = getRandomString();

		await journalEditStructureDefaultValuesPage.fillTextField(
			fieldName,
			content
		);

		await journalEditStructureDefaultValuesPage.save();

		const modifiedStructure = await apiHelpers.dataEngine.getStructure(
			structure.id
		);

		expect(modifiedStructure.dataDefinitionFields[0].defaultValue).toEqual({
			en_US: `${content}`,
		});

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		// The Fields panel can load collapsed, so re-expand it before
		// interacting with the structure field and its duplicate button.

		await openFieldset(page, 'Fields');

		const textField = page.getByRole('textbox', {
			name: fieldName,
		});

		await expect(textField).toHaveValue(content);

		await page.getByLabel('Add Duplicate Field Text').click();

		await expect(textField.nth(1)).toHaveValue(content);
	}
);

baseTest(
	'LPD-28728: the default value of a structure is not deleted after the structure update',
	async ({
		apiHelpers,
		journalEditStructureDefaultValuesPage,
		journalEditStructurePage,
		site,
	}) => {
		const fieldName = 'Text1';
		const structureName = 'Structure1';

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [{name: fieldName, repeatable: true}],
			name: structureName,
		});

		const structure = await apiHelpers.dataEngine.createStructure(
			site.id,
			dataDefinition
		);

		await journalEditStructureDefaultValuesPage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		const content = getRandomString();

		await journalEditStructureDefaultValuesPage.fillTextField(
			fieldName,
			content
		);

		await journalEditStructureDefaultValuesPage.save();

		await journalEditStructurePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		await journalEditStructurePage.showFieldProperties(fieldName);

		await expect(
			journalEditStructurePage.propertyPlaceholderText
		).toBeEmpty();

		const placeholderText = getRandomString();

		await journalEditStructurePage.fillFieldProperty(
			journalEditStructurePage.propertyPlaceholderText,
			placeholderText
		);

		await expect(
			journalEditStructurePage.propertyPlaceholderText
		).toHaveValue(placeholderText);

		await journalEditStructurePage.save();

		const modifiedStructure = await apiHelpers.dataEngine.getStructure(
			structure.id
		);

		expect(modifiedStructure.dataDefinitionFields[0].defaultValue).toEqual({
			en_US: `${content}`,
		});
	}
);

assetPublisherDeprecationTest(
	'Can paginate Web Content in an Asset Publisher',
	{
		tag: '@LPD-35348',
	},
	async ({
		apiHelpers,
		journalEditArticlePage,
		journalPage,
		page,
		pagesAdminPage,
		site,
		widgetPagePage,
	}) => {
		await journalPage.goto(site.friendlyUrlPath);

		const title = getRandomString();

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		await journalEditArticlePage.fillTitle(title);

		await journalEditArticlePage.fillContent('page1 @page_break@ page2');

		await journalEditArticlePage.publishArticle();

		await waitForAlert(page, `Success:${title} was created successfully.`);

		await pagesAdminPage.goto(site.friendlyUrlPath);

		const widgetLayout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await widgetPagePage.goto(widgetLayout, site.friendlyUrlPath);

		await widgetPagePage.addPortlet('Asset Publisher');

		await page
			.locator('.portlet-asset-publisher')
			.first()
			.getByLabel('Options')
			.click();

		await page
			.getByRole('menuitem', {exact: true, name: 'Configuration'})
			.click();

		const configurationFrame = page.frameLocator(
			'iframe[id="modalIframe"]'
		);

		await configurationFrame
			.getByRole('tab', {name: 'Asset Selection'})
			.click();

		await configurationFrame.getByText('Dynamic', {exact: true}).click();

		await expect(async () => {
			await configurationFrame
				.getByRole('tab', {name: 'Display Settings'})
				.click({timeout: 2000});

			await configurationFrame
				.getByLabel('Display Template')
				.click({timeout: 2000});

			await configurationFrame
				.getByRole('option', {name: 'Full Content'})
				.click({timeout: 2000});
		}).toPass();

		await configurationFrame.getByRole('button', {name: 'Save'}).click();

		await page
			.locator('.modal-header')
			.getByLabel('Close', {exact: true})
			.click();

		await expect(async () => {
			await widgetPagePage.goto(widgetLayout, site.friendlyUrlPath);

			await expect(page.getByText('page1')).toBeVisible({timeout: 2000});
			await expect(page.getByLabel('Go to page, 2')).toBeVisible({
				timeout: 2000,
			});
		}).toPass();

		await page.getByLabel('Go to page, 2').click();

		await expect(page.getByText('page2')).toBeVisible();
	}
);

ckeditor4Test(
	'Change image from context menu, in editor with "adaptivemedia" plugin',
	{tag: ['@LPD-53880']},
	async ({journalEditArticlePage, page, site}) => {
		await ckeditor4Test.step('Open new Basic Web Content', async () => {
			await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});
		});

		const ckeditor4Page = new CKEditor4ClassicPage(page);

		await ckeditor4Page.page.getByLabel('Image', {exact: true}).click();

		await ckeditor4Page.selectImageWithItemSelector({
			cardTitle: 'moon.png',
		});

		const editableFrame = journalEditArticlePage.page
			.locator('.edit-article-panel')
			.frameLocator('iframe[title="editor"]');

		const moonImage = editableFrame.locator(
			'img[src="/documents/d/guest/moon-png"]'
		);

		await expect(moonImage).toBeVisible();
		await expect(moonImage).toHaveAttribute('data-fileentryid');

		const moonImageFileEntryId = await moonImage

			// eslint-disable-next-line @liferay/no-get-data-attribute
			.getAttribute('data-fileentryid');

		await moonImage.dblclick();

		await ckeditor4Page.contextMenu.getByText('Browse Server').click();

		await ckeditor4Page.selectImageWithItemSelector({
			cardTitle: 'satellite.png',
		});

		await expect(ckeditor4Page.contextMenu.getByLabel('URL')).toHaveValue(
			'/documents/d/guest/satellite-png'
		);

		await ckeditor4Page.contextMenu.getByText('OK').click();

		const satelliteImage = editableFrame.locator(
			'img[src="/documents/d/guest/satellite-png"]'
		);

		await expect(satelliteImage).toBeVisible();
		await expect(satelliteImage).toHaveAttribute('data-fileentryid');

		const satelliteImageFileEntryId = await satelliteImage

			// eslint-disable-next-line @liferay/no-get-data-attribute
			.getAttribute('data-fileentryid');

		await expect(moonImageFileEntryId).not.toBe(satelliteImageFileEntryId);
	}
);

ckeditor5Test(
	'Web Content is published with multiple translations',
	{
		tag: '@LPD-11235',
	},
	async ({
		journalEditArticlePage,
		journalEditArticleTranslationsPage,
		page,
		site,
	}) => {
		await ckeditor5Test.step('Open new Basic Web Content', async () => {
			await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});
		});

		const articleContentAR = getRandomString();
		const articleContentEN = getRandomString();
		const articleDescriptionAR = getRandomString();
		const articleDescriptionEN = getRandomString();
		const articleTitleAR = getRandomString();
		const articleTitleEN = getRandomString();

		const contentEditable = journalEditArticlePage.page.locator(
			'#_com_liferay_journal_web_portlet_JournalPortlet_content .ck-content'
		);
		const descriptionEditable = journalEditArticlePage.page.locator(
			'#_com_liferay_journal_web_portlet_JournalPortlet_metadata .ck-content'
		);

		await ckeditor5Test.step('Expand fields if collapsed', async () => {
			const fieldsToggle = journalEditArticlePage.page.locator(
				'.edit-article-panel .collapse-icon.sheet-subtitle'
			);

			const classList = await fieldsToggle.getAttribute('class');

			if (classList.match(/collapsed/)) {
				fieldsToggle.click();
			}

			await expect(contentEditable).toBeVisible();
		});

		await ckeditor5Test.step(
			'Add sample English title, description and content',
			async () => {
				await journalEditArticlePage.fillTitle(articleTitleEN);

				await contentEditable.fill(articleContentEN);
				await descriptionEditable.fill(articleDescriptionEN);
			}
		);

		await ckeditor5Test.step(
			'Switch to Arabic locale, check content direction',
			async () => {
				await journalEditArticlePage.changeLanguage('ar_SA');

				await expect(
					journalEditArticlePage.page.getByLabel(
						'Select a language, current language: Arabic.'
					)
				).toBeVisible();

				expect(await contentEditable.getAttribute('dir')).toEqual(
					'rtl'
				);
			}
		);

		await ckeditor5Test.step(
			'Add sample Arabic title, description and content',
			async () => {
				await journalEditArticlePage.fillTitle(articleTitleAR);

				await contentEditable.fill(articleContentAR);
				await descriptionEditable.fill(articleDescriptionAR);
			}
		);

		await ckeditor5Test.step('Publish article', async () => {
			await journalEditArticlePage.publishArticle();
		});

		await ckeditor5Test.step(
			'Open saved article and assert content is correct',
			async () => {
				await page.getByTitle(articleTitleEN).click();

				await expect(
					contentEditable.getByText(articleContentEN)
				).toBeVisible();
				await expect(
					descriptionEditable.getByText(articleDescriptionEN)
				).toBeVisible();

				await journalEditArticlePage.changeLanguage('ar_SA');

				await expect(
					contentEditable.getByText(articleContentAR)
				).toBeVisible();
				await expect(
					descriptionEditable.getByText(articleDescriptionAR)
				).toBeVisible();
			}
		);

		await ckeditor5Test.step('Reset to English', async () => {
			await journalEditArticlePage.changeLanguage('en_US');
		});

		const toolbar =
			journalEditArticlePage.page.getByLabel('Editor toolbar');

		const sourceButton = toolbar.getByRole('button', {name: 'Source'});

		const sourceTextarea = journalEditArticlePage.page.getByLabel(
			'Source code editing area'
		);

		await ckeditor5Test.step(
			'Change article in source editing and then switch to wyswyg view',
			async () => {
				await sourceButton.click();

				await sourceTextarea.fill(
					'<a href="#" onclick="alert()">foo</a><script>alert()</script>'
				);

				await sourceButton.click();
			}
		);

		await ckeditor5Test.step('Publish article again', async () => {
			await journalEditArticlePage.publishArticle(true);

			await expect(page.locator('.alert-success')).toBeVisible();
		});

		await ckeditor5Test.step(
			'Open saved article and assert content is correct',
			async () => {
				await page.getByTitle(articleTitleEN).click();

				await sourceButton.click();

				await expect(sourceTextarea).toHaveValue(
					/<a href="#">foo<\/a>alert\(\)/
				);
			}
		);

		await ckeditor5Test.step('Revert to simple content', async () => {
			await sourceButton.click();

			await contentEditable.fill(articleContentEN);

			await journalEditArticlePage.publishArticle(true);

			await expect(page.locator('.alert-success')).toBeVisible();
		});

		await ckeditor5Test.step('Open aricle translation editor', async () => {
			await journalEditArticleTranslationsPage.goto({
				title: articleTitleEN,
			});

			await expect(
				journalEditArticleTranslationsPage.previewContainers.getByText(
					articleDescriptionEN
				)
			).toBeVisible();
			await expect(
				journalEditArticleTranslationsPage.previewContainers.getByText(
					articleContentEN
				)
			).toBeVisible();

			await expect(
				journalEditArticleTranslationsPage.contentEditor.editable.getByText(
					articleContentAR
				)
			).toBeVisible();
			await expect(
				journalEditArticleTranslationsPage.descriptionEditor.editable.getByText(
					articleDescriptionAR
				)
			).toBeVisible();
		});

		const articleDescriptionAR2 = getRandomString();
		const articleContentAR2 = getRandomString();

		await ckeditor5Test.step('Change Arabic translation', async () => {
			await journalEditArticleTranslationsPage.descriptionEditor.editable.fill(
				articleDescriptionAR2
			);
			await journalEditArticleTranslationsPage.contentEditor.editable.fill(
				articleContentAR2
			);

			await journalEditArticleTranslationsPage.publishButton.click();

			await expect(page.locator('.alert-success')).toBeVisible();
		});

		await ckeditor5Test.step(
			'Open aricle translation editor and assert changes were saved',
			async () => {
				await journalEditArticleTranslationsPage.goto({
					title: articleTitleEN,
				});

				await expect(
					journalEditArticleTranslationsPage.descriptionEditor.editable.getByText(
						articleDescriptionAR2
					)
				).toBeVisible();
				await expect(
					journalEditArticleTranslationsPage.contentEditor.editable.getByText(
						articleContentAR2
					)
				).toBeVisible();
			}
		);
	}
);

translationAndAutosaveTest(
	'Web Content is published when Feature Flags LPD-11228 is are active',
	{
		tag: '@LPD-33570',
	},
	async ({journalEditArticlePage, page, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const articleTitle = 'Web Content Title';

		journalEditArticlePage.createAndPublishBasicArticle(articleTitle);

		await expect(page.getByTitle(articleTitle)).toBeVisible();

		await expect(
			page.locator(
				'[id="_com_liferay_journal_web_portlet_JournalPortlet_articles_1"] span.label-item'
			)
		).toHaveText('Approved');
	}
);

baseTest(
	'Verify Journal Article ID field is not editable after assignment',
	{
		tag: '@LPD-39034',
	},
	async ({journalEditArticlePage, page, site}) => {
		const systemSettingsPage = new SystemSettingsPage(page);
		const articleTitle = 'Test Article';

		try {
			await systemSettingsPage.goToSystemSetting(
				'Web Content',
				'Administration'
			);

			const toggle = page.getByLabel(
				'Journal Article Force Autogenerate ID'
			);

			await toggle.uncheck();

			await page.getByRole('button', {name: /save|update/i}).click();

			await waitForAlert(page);

			await journalEditArticlePage.createArticleWithCustomArticleId(
				page,
				site,
				String(getRandomInt()),
				articleTitle
			);

			await journalEditArticlePage.editArticle(articleTitle);

			await expect(
				page.locator(
					'input[name="_com_liferay_journal_web_portlet_JournalPortlet_newArticleId"]'
				)
			).toBeDisabled();

			await expect(
				page.locator(
					'input[name="_com_liferay_journal_web_portlet_JournalPortlet_autoArticleId"]'
				)
			).toBeDisabled();
		}
		finally {
			await systemSettingsPage.goToSystemSetting(
				'Web Content',
				'Administration'
			);

			const toggle = page.getByLabel(
				'Journal Article Force Autogenerate ID'
			);
			await toggle.check();

			await page.getByRole('button', {name: /save|update/i}).click();

			await waitForAlert(page);
		}
	}
);

baseTest(
	'Journal Article Shows Wrong Display Date When Published After Draft',
	{
		tag: '@LPD-62472',
	},
	async ({journalEditArticlePage, journalPage, page, site}) => {
		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		baseTest.setTimeout(120000);

		await journalEditArticlePage.saveAsDraftWithPermissions(
			getRandomString()
		);

		await page.waitForTimeout(60000);

		await journalEditArticlePage.publishButton.click();

		await journalPage.changeView('table');

		const firstDisplayDateTd = page
			.locator('td.lfr-display-date-column')
			.first();

		const spanInsideTd = firstDisplayDateTd.locator('span');

		await spanInsideTd.waitFor({state: 'visible'});

		const displayDateText = await spanInsideTd.textContent();

		expect(displayDateText).not.toBe('1 Minute ago');
	}
);

baseTest(
	'Can add and remove all categories from a Web Content',
	{
		tag: '@LPD-67395',
	},
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const category1 = getRandomString();
		const vocabularyName = getRandomString();

		await baseTest.step('create vocabulary and category', async () => {
			await createCategories({
				apiHelpers,
				categoryNames: [{name: category1}],
				siteId: site.id,
				vocabularyName,
			});
		});

		await baseTest.step('select category in web content', async () => {
			await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

			await journalEditArticlePage.selectCategories(vocabularyName, [
				category1,
			]);

			await expect(
				page.getByRole('gridcell', {exact: true, name: category1})
			).toBeVisible();
		});

		await baseTest.step(
			'can remove categories via Clear All button',
			async () => {
				await journalEditArticlePage.clearAllCategories(vocabularyName);

				await expect(
					page.getByRole('gridcell', {exact: true, name: category1})
				).not.toBeVisible();
			}
		);
	}
);

baseTest(
	'Web content with "pending" status has the submission button disabled',
	{tag: '@LPD-70782'},
	async ({journalEditArticlePage, journalPage, site, workflowPage}) => {
		await baseTest.step('Set up view for pending articles', async () => {
			await journalPage.goto(site.friendlyUrlPath);

			await journalPage.changeView('list');
		});

		await baseTest.step('Update workflow to require approval', async () => {
			await workflowPage.goto(site.friendlyUrlPath);

			await workflowPage.changeWorkflow(
				'Web Content Article',
				'Single Approver'
			);
		});

		const articleTitle = getRandomString();

		await baseTest.step('Create web content article', async () => {
			await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

			await journalEditArticlePage.fillTitle(articleTitle);

			await journalEditArticlePage.submitArticleForWorkflow(articleTitle);
		});

		await baseTest.step(
			'Assert that the submission buttons are disabled',
			async () => {
				await journalPage.goToJournalArticleAction(
					'Edit',
					articleTitle
				);

				await expect(
					journalEditArticlePage.publishDropdown
				).toBeDisabled();
				await expect(
					journalEditArticlePage.publishButton
				).toBeDisabled();
			}
		);
	}
);

baseTest(
	'A user can expire a Web Content through its actions',
	async ({apiHelpers, journalPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const title = getRandomString();

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: title},
		});

		await journalPage.goto(site.friendlyUrlPath);

		await expect(page.getByText(title)).toBeVisible();

		await journalPage.goToJournalArticleAction('Expire', title);

		await expect(page.locator('.alert-success')).toBeVisible();

		await expect(
			page.locator('span').filter({hasText: 'Expired'}).nth(1)
		).toBeVisible();
	}
);

baseTest(
	'A user can expire multiple Web Content though the toolbar',
	async ({apiHelpers, journalPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const title1 = getRandomString();
		const title2 = getRandomString();

		await Promise.all([
			apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: title1},
			}),
			apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: title2},
			}),
		]);

		await journalPage.goto(site.friendlyUrlPath);

		await expect(page.getByText(title1)).toBeVisible();
		await expect(page.getByText(title2)).toBeVisible();

		await page.getByLabel('Select All Items on the Page').check();

		await page.getByRole('button', {name: 'Expire'}).click();

		await expect(page.locator('.alert-success')).toBeVisible();

		await expect(
			page.locator('span').filter({hasText: 'Expired'}).nth(1)
		).toBeVisible();

		await expect(
			page.locator('span').filter({hasText: 'Expired'}).nth(2)
		).toBeVisible();
	}
);

baseTest(
	'The user can search for a specific Web Content in Web Content Admin',
	async ({apiHelpers, journalPage, page, site}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const title1 = getRandomString();
		const title2 = getRandomString();

		await Promise.all([
			apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: title1},
			}),
			apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				titleMap: {en_US: title2},
			}),
		]);

		await journalPage.goto(site.friendlyUrlPath);

		const title1Link = page.getByRole('link', {exact: true, name: title1});
		const title2Link = page.getByRole('link', {exact: true, name: title2});

		await expect(title1Link).toBeVisible();
		await expect(title2Link).toBeVisible();

		const searchInput = page.getByPlaceholder('Search for');
		const searchButton = page.getByLabel('Search for', {exact: true});

		await searchInput.fill(title1);
		await searchButton.click();

		await expect(title1Link).toBeVisible();
		await expect(title2Link).not.toBeVisible();

		await searchInput.fill(title2);
		await searchButton.click();

		await expect(title1Link).not.toBeVisible();
		await expect(title2Link).toBeVisible();

		await searchInput.fill('Random Text');
		await searchButton.click();

		await expect(title1Link).not.toBeVisible();
		await expect(title2Link).not.toBeVisible();
	}
);

baseTest(
	'Publish and Schedule button is not disabled and shows validation error for custom structures required fields',
	{
		tag: '@LPD-75537',
	},
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const structureName = 'Structure 1';
		const title = getRandomString();

		const dataDefinition = getDataStructureDefinition({
			defaultLanguageId: 'en_US',
			fields: [
				{name: 'Text'},
				{
					name: 'TextRequired',
					required: true,
				},
			],
			name: structureName,
		});

		await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		await journalEditArticlePage.fillTitle(title);

		await baseTest.step(
			'Publish button is not disabled and shows validation error',
			async () => {
				await journalEditArticlePage.publishArticle(true);

				await expect(
					journalEditArticlePage.publishButton
				).not.toBeDisabled();
				await expect(
					page.getByText('This field is required.')
				).toBeVisible();
			}
		);

		await baseTest.step(
			'Schedule Publication button shows validation error',
			async () => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('menuitem', {
						name: 'Schedule Publication',
					}),
					trigger: journalEditArticlePage.publishDropdown,
				});

				await expect(page.locator('.modal-dialog')).not.toBeVisible();
				await expect(
					page.getByText('This field is required.')
				).toBeVisible();
			}
		);
	}
);

baseTest(
	'Cannot Publish, Schedule or Save as Draft a web content if title for default language is not set',
	{
		tag: '@LPD-76463',
	},
	async ({journalEditArticlePage, journalPage, page, site}) => {
		await journalPage.goto();

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		await baseTest.step(
			'Change language without filling title for default language',
			async () => {
				const translationButton = page.getByRole('combobox', {
					name: 'Select a language',
				});

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('option', {
						name: 'Catalan Language: Not',
					}),
					trigger: translationButton,
				});

				await expect(
					page.getByLabel('Translation Options')
				).toBeVisible();

				await journalEditArticlePage.fillTitle(getRandomString());
			}
		);

		await baseTest.step('Cannot Publish With Permissions', async () => {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					name: 'Publish With Permissions',
				}),
				trigger: journalEditArticlePage.publishDropdown,
			});

			await page
				.locator('.alert-danger', {
					hasText:
						'Please enter a valid title for the default language',
				})
				.waitFor();

			await expect(page.locator('.modal-dialog')).not.toBeVisible();

			await page
				.locator('.alert-danger')
				.getByRole('button', {name: 'Close'})
				.click();
		});

		await baseTest.step('Cannot Schedule Publication', async () => {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					name: 'Schedule Publication',
				}),
				trigger: journalEditArticlePage.publishDropdown,
			});

			await page
				.locator('.alert-danger', {
					hasText:
						'Please enter a valid title for the default language',
				})
				.waitFor();

			await expect(page.locator('.modal-dialog')).not.toBeVisible();

			await page
				.locator('.alert-danger')
				.getByRole('button', {name: 'Close'})
				.click();
		});

		await baseTest.step('Cannot Save as Draft', async () => {
			await page.getByRole('button', {name: 'Save as Draft'}).click();

			await expect(page.locator('.modal-dialog')).not.toBeVisible();
		});

		await baseTest.step(
			'Can Save as Draft if default language title is filled',
			async () => {
				const translationButton = page.getByRole('combobox', {
					name: 'Select a language',
				});

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('option', {
						name: 'English Language: Default',
					}),
					trigger: translationButton,
				});

				await journalEditArticlePage.fillTitle(getRandomString());

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('option', {
						name: 'Catalan Language: Translating 1/',
					}),
					trigger: translationButton,
				});

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('heading', {
						name: 'Save as Draft With Permissions',
					}),
					trigger: page.getByRole('button', {name: 'Save as Draft'}),
				});
			}
		);
	}
);

baseTest(
	'Display Page Preview button is disabled when draft does not exist',
	{
		tag: '@LPD-77694',
	},
	async ({journalEditArticlePage, page, site}) => {
		page.on('dialog', (dialog) => dialog.accept());

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const title = getRandomString();

		await journalEditArticlePage.content.waitFor();

		await journalEditArticlePage.fillTitle(title);

		await journalEditArticlePage.content.waitFor();

		await journalEditArticlePage.openFieldSet(
			'Display Page',
			'displayPage'
		);

		await journalEditArticlePage.previewButton.waitFor();

		await journalEditArticlePage.previewButton.scrollIntoViewIfNeeded();

		await expect(journalEditArticlePage.previewButton).toBeDisabled();
	}
);
