/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {createCategories} from '../../../helpers/CreateCategories';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import fillAndClickOutside from '../../../utils/fillAndClickOutside';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {structureBuilderPagesTest} from '../structure-builder/fixtures/structureBuilderPagesTest';
import {categorizationPagesTest} from './fixtures/categorizationPagesTest';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	categorizationPagesTest,
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	fragmentsPagesTest,
	loginTest(),
	pageEditorPagesTest,
	structureBuilderPagesTest
);

test(
	'Friendly URL is taken into account when creating contents',
	{tag: '@LPD-54566'},
	async ({contentsPage, page}) => {

		// Go to CMS Contents

		await contentsPage.goto();

		// Create new Basic Content

		await contentsPage.createContent('Basic Content');

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
	async ({contentsPage, localizationSelectPage, page}) => {

		// Go to CMS Contents

		await contentsPage.goto();

		// Create new Knowledge Base content

		await contentsPage.createContent('Basic Content');

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

		await localizationSelectPage.switchLanguage('es-ES');

		// Check localization management is activated by default

		await clickAndExpectToBeVisible({
			target: page.getByRole('menuitem', {
				name: 'Mark as Translated',
			}),
			trigger: localizationSelectPage.actionsDropdownTrigger,
		});

		await clickAndExpectToBeHidden({
			target: page.getByRole('menuitem', {
				name: 'Mark as Translated',
			}),
			trigger: localizationSelectPage.actionsDropdownTrigger,
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

		await localizationSelectPage.switchLanguage('es-ES');

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

		await structureBuilderPage.goToCreateStructure();

		await structureBuilderPage.selectSpaces(['Default']);

		const label = getRandomString();

		await structureBuilderPage.changeStructureSettings({
			label,
			name: `StructureName${getRandomInt()}`,
		});

		// Publish the structure

		await structureBuilderPage.saveStructure();

		await structureBuilderPage.publishStructure();

		// Create a content of the new structure and check Spaces fragment

		await contentsPage.goto();

		await contentsPage.createContent(label);

		const fragment = page.locator(
			'[class*="spacescomponentsectionfragmentrenderer"]'
		);

		await fragment.waitFor();

		await expect(
			fragment.locator('label').filter({hasText: 'Space'})
		).toBeVisible();

		await expect(
			fragment.locator('.sticker-overlay').filter({hasText: 'D'})
		).toBeVisible();

		await expect(fragment.filter({hasText: 'Default'})).toBeVisible();
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

		// Create new Folder and a Basic Content

		const folderName = getRandomString();

		await folderPage.createFolder(folderName);

		await folderPage.clickOption(folderName, 'View Folder');

		await contentsPage.createContent('Basic Content');

		// Fill data and save

		const title = getRandomString();

		await page.getByLabel('Title').fill(title);

		await contentsPage.saveContent();

		// Check that the content is visible that means we redirected to the folder

		await expect(page.getByText(title)).toBeVisible();

		// Delete content and folder

		await contentsPage.deleteContent(title);

		await contentsPage.goto();

		await folderPage.deleteFolder(folderName);

		await expect(page.getByText(folderName)).not.toBeVisible();
	}
);

test.describe('Comments Panel', () => {
	const addComment = async ({
		content = 'New Comment',
		page,
		parentComment,
	}: {
		content?: string;
		page: Page;
		parentComment?: Locator;
	}) => {
		const rootComment = parentComment || page;

		const editor = rootComment.getByLabel('Add Comment.');

		await expect(editor).toBeVisible();

		await editor.scrollIntoViewIfNeeded();

		await editor.click();

		await page.keyboard.type(content);

		const saveButton = rootComment.getByRole('button', {
			exact: true,
			name: 'Save',
		});

		await expect(saveButton).toBeEnabled();

		await saveButton.click();

		await expect(saveButton).toBeDisabled();

		await waitForAlert(page, 'Success:Your comment has been posted.', {
			autoClose: true,
		});

		if (parentComment) {
			await expect(saveButton).not.toBeAttached();
			await expect(editor).not.toBeAttached();
		}
		else {
			await expect(saveButton).toBeEnabled();
			await expect(editor).not.toContainText(content);
		}

		// Check that the comment has been added

		const comment = rootComment.locator('article');

		await expect(comment.filter({hasText: content})).toBeAttached();

		if (parentComment) {
			await expect(comment.getByText('Reply')).not.toBeAttached();
		}

		return {comment, editor};
	};

	test(
		'Add and edit comments in the comments panel',
		{tag: '@LPD-59851'},
		async ({contentsPage, page}) => {
			await contentsPage.goto();

			await contentsPage.createContent('Blog');

			await contentsPage.openSidePanel('Comments');

			// Add a comment

			const parentCommentContent = 'New Comment';

			const {comment, editor} = await addComment({
				content: parentCommentContent,
				page,
			});

			// Check that the text typed is removed when the button cancel is pressed

			await editor.click({force: true});

			await page.keyboard.type('New comment to cancel');

			await page.getByRole('button', {name: 'Cancel'}).click();

			await expect(editor).not.toContainText('New comment to cancel');

			// Add a reply the comment

			await comment.getByText('Reply').click();

			const {comment: childComment} = await addComment({
				content: 'New child comment',
				page,
				parentComment: comment,
			});

			// Edit the parent comment

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem').filter({hasText: 'edit'}),
				trigger: page.getByTitle('actions').first(),
			});

			await page.getByText(parentCommentContent).selectText();

			await page.keyboard.type('Editing the comment');

			await comment.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(page, 'Success:Your comment has been edited.', {
				autoClose: true,
			});

			await expect(comment.first()).toContainText('Editing the comment');

			// Edit the child comment

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem').filter({hasText: 'edit'}),
				trigger: page.getByTitle('actions').nth(1),
			});

			await page.getByText('New child comment').selectText();

			await page.keyboard.type('Editing the child comment');

			await childComment.getByRole('button', {name: 'Save'}).click();

			await expect(childComment).toContainText(
				'Editing the child comment'
			);
		}
	);

	test('Error when a comment is edited', async ({contentsPage, page}) => {
		await page.route(
			'**/c/cms/edit_content_item_comment?**',
			async (route) => {
				await route.fulfill({
					body: JSON.stringify({error: ''}),
					status: 500,
				});
			}
		);

		await contentsPage.goto();

		await contentsPage.createContent('Blog');

		await contentsPage.openSidePanel('Comments');

		const {comment} = await addComment({
			page,
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem').filter({hasText: 'edit'}),
			trigger: page.getByTitle('actions'),
		});

		await page.getByText('New Comment').selectText();

		await page.keyboard.type('Editing the comment');

		await comment.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'Error:An unexpected error occurred.', {
			autoClose: true,
			type: 'danger',
		});
	});

	test('Error when a comment is added', async ({contentsPage, page}) => {
		await page.route(
			'**/c/cms/add_content_item_comment?**',
			async (route) => {
				await route.fulfill({
					body: JSON.stringify({error: ''}),
					status: 500,
				});
			}
		);

		await contentsPage.goto();

		await contentsPage.createContent('Blog');

		await contentsPage.openSidePanel('Comments');

		// Try to add comment

		const editor = page.getByLabel('Add Comment.');

		await expect(editor).toBeVisible();

		await editor.scrollIntoViewIfNeeded();

		await editor.click();

		await page.keyboard.type('New Comment');

		const saveButton = page.getByRole('button', {
			exact: true,
			name: 'Save',
		});

		await saveButton.click();

		await waitForAlert(page, 'Error:An unexpected error occurred.', {
			autoClose: true,
			type: 'danger',
		});
	});
});

test.describe('Schedule Panel', () => {
	test(
		'Do not allow publishing if there are errors in the fields',
		{tag: '@LPD-62099'},
		async ({contentsPage, page}) => {

			// Create a Blog

			await contentsPage.goto();

			await contentsPage.createContent('Basic Content');

			await contentsPage.openSidePanel('Schedule');

			const title = getRandomString();

			await page.getByPlaceholder('New Basic Web Content').fill(title);

			// Fill the input with an error

			const expireCheckbox = page.getByLabel('Never Expire').first();

			await expireCheckbox.uncheck();

			const expirationDateField = page.getByRole('textbox', {
				name: 'Expiration Date',
			});

			await expirationDateField.fill('05/12/2025');

			// Try to publish the content

			await contentsPage.publishButton.click();

			const error = page.getByText('The field value is invalid.');

			await expect(error).toBeVisible();

			await expect(expirationDateField).toBeFocused();

			// Close the panel and try to publish again

			await page.getByTitle('Close', {exact: true}).click();

			await expect(error).not.toBeVisible();

			await contentsPage.publishButton.click();

			await expect(error).toBeVisible();

			await expect(expirationDateField).toBeFocused();

			// Set a valid date and publish

			const nextYear = new Date().getFullYear() + 1;

			await expirationDateField.fill(`05/12/${nextYear} 12:55 PM`);

			await expect(error).not.toBeVisible();

			await contentsPage.publishButton.click();

			await expect(
				page.locator('.table-list-title a', {hasText: title})
			).toBeAttached();

			// Delete content

			await contentsPage.deleteContent(title);
		}
	);
});

test.describe('Categorization Panel', () => {
	test(
		'Add categories and tags to the content',
		{tag: ['@LPD-62047', '@LPD-69196']},
		async ({
			apiHelpers,
			contentsPage,
			page,
			tagsPage,
			vocabulariesPage,
		}) => {
			const selectCategory = async (categoryName: string) => {
				const categoriesAutocomplete =
					page.getByPlaceholder('Add category');

				await categoriesAutocomplete.fill(categoryName);

				const option = page.getByRole('option', {name: categoryName});

				await option.waitFor();
				await option.click();
			};

			const selectTag = async (tagName: string) => {
				const tagsAutocomplete = page.getByPlaceholder('Add tag');

				await tagsAutocomplete.fill(tagName);

				const newTagOption = page.getByRole('option', {
					name: 'Create New Tag:',
				});

				await newTagOption.waitFor();
				await newTagOption.click();
			};

			// Create category

			const categoryName = getRandomString();
			const vocabularyName = getRandomString();

			const site = await apiHelpers.headlessSite.getSiteByERC('L_CMS');

			await createCategories({
				apiHelpers,
				assetLibraries: [{id: -1, name: 'All Spaces'}],
				assetTypes: [{required: false, type: 'AllAssetTypes'}],
				categoryNames: [{name: categoryName}],
				siteId: site.id,
				vocabularyName,
			});

			// Create a content and publish it

			await contentsPage.goto();

			await contentsPage.createContent('Basic Content');

			const title = getRandomString();

			await page.getByPlaceholder('New Basic Web Content').fill(title);

			await contentsPage.publishButton.click();

			// Edit the content to set a categorization

			const content = page.locator('.table-list-title a', {
				hasText: title,
			});

			await content.waitFor();
			await content.click();

			await contentsPage.openSidePanel('Categorization');

			// Add a category to the content

			await selectCategory(categoryName);

			const categoryLabel = page.locator('.label-item', {
				hasText: categoryName,
			});

			await expect(categoryLabel).toBeAttached();

			// Add a tag to the content

			let tagName = getRandomString();

			await selectTag(tagName);

			let tagLabel = page.locator('.label-item', {hasText: tagName});

			await expect(tagLabel).toBeAttached();

			// Cancel the content and edit it again to check that nothing has been saved

			await page.getByLabel('Cancel', {exact: true}).click();

			await content.waitFor();
			await content.click();

			await contentsPage.openSidePanel('Categorization');

			await expect(categoryLabel).not.toBeAttached();
			await expect(tagLabel).not.toBeAttached();

			// Select again the category and the tag and publish it

			await selectCategory(categoryName);

			tagName = getRandomString();
			tagLabel = page.locator('.label-item', {hasText: tagName});

			await selectTag(tagName);

			await expect(categoryLabel).toBeAttached();
			await expect(tagLabel).toBeAttached();

			await contentsPage.publishButton.click();

			// Edit the content and check that the categorization is still there

			await content.click();

			await contentsPage.openSidePanel('Categorization');

			await expect(tagLabel).toBeAttached();
			await expect(categoryLabel).toBeAttached();

			// Delete tag

			await tagsPage.goto();
			await tagsPage.deleteTag(tagName);

			// Delete vocabulary

			await vocabulariesPage.goto();
			await vocabulariesPage.deleteVocabulary(vocabularyName);
		}
	);
});

test(
	'Can save content as draft',
	{tag: '@LPD-66942'},
	async ({contentsPage, page}) => {

		// Create a content

		await contentsPage.goto();

		await contentsPage.createContent('Basic Content');

		const title = getRandomString();

		await page.getByPlaceholder(`New Basic Web Content`).fill(title);

		// Save as draft

		await contentsPage.saveContentAsDraft();

		// Check that the content is saved as draft

		await expect(
			page
				.locator('tr', {hasText: title})
				.or(page.locator('.card-row', {hasText: title}))
				.locator('.cell-embedded-status')
		).toHaveText('draft');

		// Delete content

		await contentsPage.deleteContent(title);
	}
);

test.describe('Schedule Publication', () => {
	test(
		'Allow scheduling a publication',
		{tag: '@LPD-68099'},
		async ({contentsPage, page}) => {

			// Create a content

			await contentsPage.goto();

			await contentsPage.createContent('Basic Content');

			const title = getRandomString();

			await page.getByPlaceholder('New Basic Web Content').fill(title);

			// Try to schedule the publication

			await contentsPage.openSchedulePublication();

			await expect(
				page.getByRole('heading', {name: 'Schedule Publication'})
			).toBeAttached();

			// Validate the empty display date

			const displayDateHiddenInput = page.locator(
				'[name="ObjectEntry_displayDate"]'
			);

			await expect(displayDateHiddenInput).toHaveValue('');

			await page.getByRole('button', {name: 'Schedule'}).click();

			await expect(
				page.getByText('This field is required')
			).toBeAttached();

			// Fill the display date field

			const displayDateInput = page.getByRole('textbox', {
				name: 'Date and Time',
			});

			const nextYear = new Date().getFullYear() + 1;

			await displayDateInput.fill(`10/31/${nextYear} 01:00 PM`);

			await page.keyboard.press('Tab');

			await expect(displayDateHiddenInput).toHaveValue(
				`${nextYear}-10-31T13:00`
			);

			await expect(
				page.getByText('This field is required')
			).not.toBeAttached();

			// Cancel the schedule publication

			await page.getByRole('button', {name: 'Cancel'}).click();

			await expect(displayDateHiddenInput).toHaveValue('');

			// Schedule the publication

			await contentsPage.openSchedulePublication();

			await displayDateInput.fill(`10/31/${nextYear} 12:30 PM`);

			await page.getByRole('button', {name: 'Schedule'}).click();

			// Check that the content is scheduled

			expect(
				page
					.locator('tr', {hasText: title})
					.or(page.locator('.card-row', {hasText: title}))
					.locator('.cell-embedded-status')
			).toHaveText('scheduled');

			// Delete content

			await contentsPage.deleteContent(title);
		}
	);

	test(
		'Do not allow scheduling a publication if there are errors in the fields',
		{tag: '@LPD-68099'},
		async ({contentsPage, page}) => {

			// Create a content

			await contentsPage.goto();

			await contentsPage.createContent('Basic Content');

			await contentsPage.openSidePanel('Schedule');

			const title = getRandomString();

			await page.getByPlaceholder('New Basic Web Content').fill(title);

			// Fill the expiration date input with an error

			const expireCheckbox = page.getByLabel('Never Expire').first();

			await expireCheckbox.uncheck();

			const expirationDateField = page.getByRole('textbox', {
				name: 'Expiration Date',
			});

			await expirationDateField.fill('05/12/2025');

			// Try to schedule the publication

			await contentsPage.openSchedulePublication();

			const displayDateInput = page.getByRole('textbox', {
				name: 'Date and Time',
			});

			const nextYear = new Date().getFullYear() + 1;

			await displayDateInput.fill(`10/31/${nextYear} 12:30 PM`);

			await page.getByRole('button', {name: 'Schedule'}).click();

			// Check that it remains on the page and the error is shown

			await expect(
				page.getByRole('heading', {name: 'Edit Basic Web Content'})
			).toBeAttached();

			await expect(
				page.getByText('The field value is invalid.')
			).toBeVisible();

			// Schedule the publication

			await expireCheckbox.check();

			await contentsPage.openSchedulePublication();

			await page.getByRole('button', {name: 'Schedule'}).click();

			// Delete content

			await expect(
				page.locator('.table-list-title a', {hasText: title})
			).toBeAttached();

			await contentsPage.deleteContent(title);
		}
	);
});

const testWithRepeatableFF = mergeTests(
	test,
	featureFlagsTest({
		'LPD-50377': {enabled: true},
		'LPS-179669': {enabled: true},
	})
);

testWithRepeatableFF(
	'Create item with repeatable groups',
	{
		tag: ['@LPD-50378', '@LPD-68645'],
	},
	async ({contentsPage, page, structureBuilderPage}) => {

		// Create structure

		const structureLabel = `StructureName${getRandomInt()}`;

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			name: `StructureName${getRandomInt()}`,
			page: structureBuilderPage,
			publish: false,
		});

		// Add fields

		await structureBuilderPage.addField('Text');
		await structureBuilderPage.addField('Long Text');

		// Create repeatable group with two of them

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Text'}],
			label: 'Repeatable Group 1',
		});

		// Create another group inside the first one

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Long Text'}],
			label: 'Repeatable Group 2',
		});

		await structureBuilderPage.publishStructure();

		// Go to CMS Contents

		await contentsPage.goto();

		// Create new content

		await contentsPage.createContent(structureLabel);

		const title = getRandomString();

		await page.getByPlaceholder(`New ${structureLabel}`).fill(title);

		// Add Repeatable Groups

		await page.getByRole('button', {name: 'Add New'}).first().click();

		await page.getByRole('button', {name: 'Add New'}).last().click();

		// Fill the fields

		const firstText = page
			.getByRole('textbox', {exact: true, name: 'Text'})
			.first();

		await firstText.fill('First Text');

		const secondText = page
			.getByRole('textbox', {exact: true, name: 'Text'})
			.last();

		await secondText.fill('Second Text');

		const firstLongText = page
			.getByRole('textbox', {exact: true, name: 'Long Text'})
			.first();

		await firstLongText.fill('First Long Text');

		const secondLongText = page
			.getByRole('textbox', {exact: true, name: 'Long Text'})
			.last();

		await secondLongText.fill('Second Long Text');

		// Save content

		await contentsPage.saveContent();

		// View content and check that the repeatable buttons are not showed

		await contentsPage.viewContent(title);

		expect(
			page
				.getByRole('dialog', {name: title})
				.frameLocator('iframe')
				.getByLabel('Add new')
		).not.toBeVisible();

		await page
			.getByRole('dialog', {name: title})
			.getByRole('button', {name: 'close'})
			.click();

		// Edit the content again and check values

		await contentsPage.editContent(title);

		await expect(firstText).toHaveValue('First Text');
		await expect(secondText).toHaveValue('Second Text');
		await expect(firstLongText).toHaveValue('First Long Text');
		await expect(secondLongText).toHaveValue('Second Long Text');

		// Delete content

		await contentsPage.goto();

		const card = page
			.locator('tr', {hasText: title})
			.or(page.locator('.card-row', {hasText: title}));

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Delete'}),
			trigger: card.locator('button'),
		});

		page.getByRole('dialog')
			.getByRole('button', {name: 'Delete Entry'})
			.click();

		await waitForAlert(page, `Success:${title} was moved`, {
			autoClose: false,
		});
	}
);

testWithRepeatableFF(
	'Create item with referenced structure',
	{
		tag: '@LPD-50378',
	},
	async ({contentsPage, page, structureBuilderPage}) => {

		// Create referenced structure

		const referencedStructureLabel = `ReferencedStructureName${getRandomInt()}`;

		await structureBuilderPage.createStructureFromData({
			label: referencedStructureLabel,
			name: referencedStructureLabel,
			page: structureBuilderPage,
			publish: true,
		});

		// Create main structure

		const structureLabel = `StructureName${getRandomInt()}`;

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			name: structureLabel,
			page: structureBuilderPage,
			publish: false,
		});

		// Add fields and publish structure

		await structureBuilderPage.addReferencedStructures([
			referencedStructureLabel,
		]);

		await structureBuilderPage.publishStructure();

		// Go to CMS Contents

		await contentsPage.goto();

		// Create new content

		await contentsPage.createContent(structureLabel);

		const title = getRandomString();

		await page.getByPlaceholder(`New ${structureLabel}`).fill(title);

		// Check that we are adding the accordion fragment

		await expect(
			page.locator('.lfr-layout-structure-item-basic-component-accordion')
		).toBeVisible();

		// Add Repeatable Groups

		await page.getByRole('button', {name: 'Add New'}).first().click();

		// Fill the fields

		const firstText = page
			.locator('.lfr-layout-structure-item-form-relationship')
			.getByRole('textbox', {exact: true, name: 'Title'})
			.first();

		await firstText.fill('First Text');

		const secondText = page
			.locator('.lfr-layout-structure-item-form-relationship')
			.getByRole('textbox', {exact: true, name: 'Title'})
			.last();

		await secondText.fill('Second Text');

		// Save content

		await contentsPage.saveContent();

		// Edit the content again and check values

		await contentsPage.editContent(title);

		await expect(firstText).toHaveValue('First Text');
		await expect(secondText).toHaveValue('Second Text');

		// Delete content

		await contentsPage.goto();

		const card = page
			.locator('tr', {hasText: title})
			.or(page.locator('.card-row', {hasText: title}));

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Delete'}),
			trigger: card.locator('button'),
		});

		page.getByRole('dialog')
			.getByRole('button', {name: 'Delete Entry'})
			.click();

		await waitForAlert(page, `Success:${title} was moved`, {
			autoClose: false,
		});
	}
);
