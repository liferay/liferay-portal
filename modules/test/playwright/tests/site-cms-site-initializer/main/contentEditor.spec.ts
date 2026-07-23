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
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {
	EFDSVisualizationMode,
	waitForEditor,
	waitForFDS,
} from '../../../utils/waitFor';
import {waitForAlert} from '../../../utils/waitForAlert';
import {structureBuilderPagesTest} from '../structure-builder/fixtures/structureBuilderPagesTest';
import {categorizationPagesTest} from './fixtures/categorizationPagesTest';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	categorizationPagesTest,
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
	}),
	fragmentsPagesTest,
	loginTest(),
	pageEditorPagesTest,
	structureBuilderPagesTest
);

interface IPostedObjectEntry {
	applicationName: string;
	id: string;
}

const postedObjectEntries: Array<IPostedObjectEntry> = [];

test.afterEach(async ({apiHelpers}) => {
	await test.step('Delete newly created object entries', async () => {
		for (const postedObjectEntry of postedObjectEntries) {
			const {applicationName, id} = postedObjectEntry;

			await apiHelpers.objectEntry.deleteObjectEntry(applicationName, id);
		}

		postedObjectEntries.length = 0;
	});
});

test(
	'Friendly URL is taken into account when creating contents',
	{tag: '@LPD-54566'},
	async ({contentsPage, page}) => {

		// Go to CMS Contents

		await contentsPage.goto();

		// Create new Basic Web Content

		await contentsPage.createContent('Basic Web Content');

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

		await contentsPage.createContent('Basic Web Content');

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
	'Can set Spanish as the only language and default language of a space',
	{tag: '@LPD-84148'},
	async ({apiHelpers, contentsPage, page}) => {

		// Create a space with Spanish as the only language and default language

		const spaceName = `Space ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {
				availableLanguageIds: ['es_ES'],
				defaultLanguageId: 'es_ES',
				useCustomLanguages: true,
			},
			type: 'Space',
		});

		// Create a Blog in the space and save it

		const blogTitle = getRandomString();

		await contentsPage.goto();

		await contentsPage.createContent('Blog', spaceName);

		await page.getByPlaceholder('New Blog').fill(blogTitle);

		await contentsPage.saveContent();

		// Edit the content and check the title is persisted

		await contentsPage.editContent(blogTitle);

		await expect(page.getByPlaceholder('New Blog')).toHaveValue(blogTitle);

		// Delete content

		await contentsPage.goto();

		await contentsPage.deleteContent(blogTitle);
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

		// Create new Folder and a Basic Web Content

		const folderName = getRandomString();

		await folderPage.createFolder(folderName);

		await folderPage.clickOption(folderName, 'View Folder');

		await contentsPage.createContent('Basic Web Content');

		// Fill data and save

		const title = getRandomString();

		await page.getByLabel('Title').fill(title);

		await contentsPage.saveContent();

		// Check that the content is visible that means we redirected to the folder

		await expect(
			page
				.locator('tr', {hasText: title})
				.or(page.locator('.card-row', {hasText: title}))
		).toBeVisible();

		// Delete content and folder

		await contentsPage.deleteContent(title);

		await contentsPage.goto();

		await folderPage.deleteFolder(folderName);

		await expect(
			page
				.locator('tr', {hasText: folderName})
				.or(page.locator('.card-row', {hasText: folderName}))
		).not.toBeVisible();
	}
);

test(
	'Publishing a new or edited basic web content redirects back to the CMS contents listing',
	{tag: '@LPD-86074'},
	async ({contentsPage, page}) => {
		await contentsPage.goto();

		await contentsPage.createContent('Basic Web Content');

		const title = getRandomString();

		await page.getByLabel('Title').fill(title);

		await contentsPage.saveContent();

		await expect(page).toHaveURL(/\/web\/cms\/contents$/);

		await contentsPage.editContent(title);

		await contentsPage.saveContent();

		await expect(page).toHaveURL(/\/web\/cms\/contents$/);

		await contentsPage.deleteContent(title);
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

			// Delete content

			await contentsPage.goto();

			await contentsPage.deleteContent('Untitled Asset');
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

		// Delete content

		await contentsPage.goto();

		await contentsPage.deleteContent('Untitled Asset');
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

		// Delete content

		await contentsPage.goto();

		await contentsPage.deleteContent('Untitled Asset');
	});

	test(
		'View comments in the shared with me view comments panel',
		{tag: '@LPD-74579'},
		async ({
			apiHelpers,
			contentsPage,
			page,
			sharedWithMePage,
			spaceSummaryPage,
		}) => {
			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			const spaceName = 'Default';

			await spaceSummaryPage.goto(spaceName);

			await expect(
				page.getByLabel('Control Menu', {exact: true})
			).not.toBeVisible();

			await spaceSummaryPage.addUserOrUserGroup(user.name, 'users');

			await contentsPage.goto();

			await contentsPage.createContent('Blog');

			const title = getRandomString();

			await page.getByPlaceholder('New Blog').fill(title);

			await contentsPage.openSidePanel('Comments');

			const parentCommentContent = 'New Comment';

			await addComment({
				content: parentCommentContent,
				page,
			});

			await contentsPage.saveContent();

			await contentsPage.goto();

			await contentsPage.shareContent(title);

			const addPeopleToCollaborateButton = page.getByRole('combobox', {
				name: 'Add People to Collaborate',
			});

			await addPeopleToCollaborateButton.click();
			await addPeopleToCollaborateButton.fill(user.emailAddress);

			await page.getByRole('option', {name: user.name}).click();

			await page
				.getByRole('combobox', {name: 'Edit Permissions'})
				.click();
			await page
				.getByRole('option', {name: 'View, Download, Comment, and'})
				.click();
			await page.getByRole('button', {name: 'Share'}).click();

			await performLogout(page);

			await performLogin(page, user.alternateName);

			await sharedWithMePage.goto();

			await page.getByRole('button', {name: 'Actions'}).click();
			await page.getByRole('menuitem', {name: 'View'}).click();
			await page.getByRole('button', {name: 'Show Comments'}).click();
			await expect(page.getByText(parentCommentContent)).toBeVisible();
		}
	);
});

test.describe('Schedule Panel', () => {
	test(
		'Do not allow publishing if there are errors in the fields',
		{tag: '@LPD-62099'},
		async ({contentsPage, page}) => {

			// Create a Blog

			await contentsPage.goto();

			await contentsPage.createContent('Basic Web Content');

			await contentsPage.openSidePanel('Schedule');

			const title = getRandomString();

			await page.getByPlaceholder('New Basic Web Content').fill(title);

			// Fill the input with an error

			const expireCheckbox = page.getByLabel('Never Expire');

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

	test(
		'Expire and review dates are shown in the correct timezone',
		{tag: '@LPD-78815'},
		async ({assetsPage, contentsPage, page}) => {

			// Create a Web Content

			await contentsPage.goto();

			await contentsPage.createContent('Basic Web Content');

			await contentsPage.openSidePanel('Schedule');

			const title = getRandomString();

			await page.getByPlaceholder('New Basic Web Content').fill(title);

			// Fill the input

			const nextYear = new Date().getFullYear() + 1;

			const expireCheckbox = page.getByLabel('Never Expire');

			await expireCheckbox.uncheck();

			const expirationDateField = page.getByRole('textbox', {
				name: 'Expiration Date',
			});

			await expirationDateField.fill(`05/12/${nextYear} 12:55 PM`);

			const reviewCheckbox = page.getByLabel('Never Review');

			await reviewCheckbox.uncheck();

			const reviewDateField = page.getByRole('textbox', {
				name: 'Review Date',
			});

			await reviewDateField.fill(`05/12/${nextYear} 12:57 PM`);

			// Publish the content

			await contentsPage.publishButton.click();

			const content = page.locator('.table-list-title a', {
				hasText: title,
			});

			await expect(content).toBeAttached();

			await content.click();

			await contentsPage.openSidePanel('Schedule');

			await expect(expirationDateField).toHaveValue(
				`05/12/${nextYear} 12:55 PM`
			);
			await expect(reviewDateField).toHaveValue(
				`05/12/${nextYear} 12:57 PM`
			);

			await assetsPage.gotoAll();

			await assetsPage.execItemAction({
				action: 'Show Details',
				filter: title,
			});

			await expect(
				page.getByRole('heading', {name: title})
			).toBeVisible();

			await expect(
				page.getByText(`05/12/${nextYear}, 12:55 PM`)
			).toBeVisible();
			await expect(
				page.getByText(`05/12/${nextYear}, 12:57 PM`)
			).toBeVisible();

			// Delete content

			await contentsPage.goto();

			await contentsPage.deleteContent(title);
		}
	);
});

test.describe('Categorization Panel', () => {
	const selectAutocompleteOption = async ({
		option,
		page,
		placeholder,
		value,
	}: {
		option: Locator;
		page: Page;
		placeholder: string;
		value: string;
	}) => {
		const autocomplete = page.getByPlaceholder(placeholder);

		const selectedLabel = page.locator('.label-item', {hasText: value});

		await expect(async () => {
			await autocomplete.fill('', {timeout: 3000});
			await autocomplete.fill(value, {timeout: 3000});

			await option.waitFor({timeout: 3000});

			await option.dispatchEvent('click', undefined, {timeout: 3000});

			await expect(selectedLabel).toBeAttached({timeout: 3000});
		}).toPass();
	};

	const selectCategory = async ({
		categoryName,
		page,
	}: {
		categoryName: string;
		page: Page;
	}) => {
		await selectAutocompleteOption({
			option: page.getByRole('option', {name: categoryName}),
			page,
			placeholder: 'Add category',
			value: categoryName,
		});
	};

	const selectTag = async ({
		page,
		tagName,
	}: {
		page: Page;
		tagName: string;
	}) => {
		await selectAutocompleteOption({
			option: page.getByRole('option', {
				name: `Create New Tag: ${tagName}`,
			}),
			page,
			placeholder: 'Add tag',
			value: tagName,
		});
	};

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

			// Create space

			const spaceName = getRandomString();

			const {id: spaceId} =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: spaceName,
					settings: {},
					type: 'Space',
				});

			// Create tags

			const allSpacesTagName = await tagsPage.createTag();
			const defaultSpaceTagName = await tagsPage.createTag(['Default']);
			const spaceTagName = await tagsPage.createTag([spaceName]);

			// Create category

			const categoryName = getRandomString();
			const vocabularyName = getRandomString();

			const site = await apiHelpers.headlessAdminSite.getSite('L_CMS');

			await createCategories({
				apiHelpers,
				assetLibraries: [{id: -1, name: 'All Spaces'}],
				assetTypes: [{required: false, type: 'AllAssetTypes'}],
				categoryNames: [{name: categoryName}],
				siteId: site.id,
				vocabularyName,
			});

			const title = getRandomString();

			const tagNames: string[] = [];

			try {

				// Create a content and publish it

				await contentsPage.goto();

				await contentsPage.createContent('Basic Web Content');

				await page
					.getByPlaceholder('New Basic Web Content')
					.fill(title);

				await contentsPage.publishButton.click();

				// Edit the content to set a categorization

				const content = page.locator('.table-list-title a', {
					hasText: title,
				});

				await content.waitFor();
				await content.click();

				await contentsPage.openSidePanel('Categorization');

				// Assert that a tag shared with a specific space is only visible to that space

				const tagsAutocomplete = page.getByPlaceholder('Add tag');

				await tagsAutocomplete.click();

				const tagsDropdownMenuEntry = page.locator(
					'.dropdown-menu > ul > li > button'
				);

				await expect(
					tagsDropdownMenuEntry.getByText(allSpacesTagName)
				).toBeVisible();
				await expect(
					tagsDropdownMenuEntry.getByText(defaultSpaceTagName)
				).toBeVisible();
				await expect(
					tagsDropdownMenuEntry.getByText(spaceTagName)
				).toBeHidden();

				// Add a category to the content

				await selectCategory({categoryName, page});

				const categoryLabel = page.locator('.label-item', {
					hasText: categoryName,
				});

				await expect(categoryLabel).toBeAttached();

				// Add a tag to the content

				const firstTagName = getRandomString();

				await selectTag({page, tagName: firstTagName});

				let tagLabel = page.locator('.label-item', {
					hasText: firstTagName,
				});

				await expect(tagLabel).toBeAttached();

				// Cancel the content and edit it again to check that nothing has been saved

				await page.getByLabel('Cancel', {exact: true}).click();

				await content.waitFor();
				await content.click();

				await contentsPage.openSidePanel('Categorization');

				await expect(categoryLabel).not.toBeAttached();
				await expect(tagLabel).not.toBeAttached();

				// Select again the category and the tag and publish it

				await selectCategory({categoryName, page});

				const secondTagName = getRandomString();

				tagLabel = page.locator('.label-item', {
					hasText: secondTagName,
				});

				await selectTag({page, tagName: secondTagName});

				await expect(categoryLabel).toBeAttached();
				await expect(tagLabel).toBeAttached();

				await contentsPage.publishButton.click();

				tagNames.push(secondTagName);

				// Edit the content and check that the categorization is still there

				await content.click();

				await contentsPage.openSidePanel('Categorization');

				await expect(tagLabel).toBeAttached();
				await expect(categoryLabel).toBeAttached();

				// Assert the applied category and tag are marked as selected
				// in the dropdown menu

				await page.getByPlaceholder('Add category').click();

				const selectedCategoryOption = page.getByRole('option', {
					name: categoryName,
				});

				await expect(selectedCategoryOption).toHaveAttribute(
					'aria-selected',
					'true'
				);
				await expect(
					selectedCategoryOption.locator('.lexicon-icon-check-small')
				).toBeVisible();

				await page.keyboard.press('Escape');

				await page.getByPlaceholder('Add tag').click();

				const selectedTagOption = page.getByRole('option', {
					name: secondTagName,
				});

				await expect(selectedTagOption).toHaveAttribute(
					'aria-selected',
					'true'
				);
				await expect(
					selectedTagOption.locator('.lexicon-icon-check-small')
				).toBeVisible();

				await page.keyboard.press('Escape');

				// Delete content

				await contentsPage.goto();
				await contentsPage.deleteContent(title);
			}
			finally {

				// Delete tags

				await tagsPage.goto();

				for (const tagName of tagNames) {
					await tagsPage.deleteTag(tagName);
				}

				await tagsPage.deleteTag(allSpacesTagName);
				await tagsPage.deleteTag(defaultSpaceTagName);
				await tagsPage.deleteTag(spaceTagName);

				// Delete vocabulary and its categories

				await vocabulariesPage.goto();
				await vocabulariesPage.deleteVocabulary(vocabularyName);

				// Delete space

				await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
					spaceId
				);
			}
		}
	);

	test(
		'Prevent publish and preserve tags when a required vocabulary has no selected category, then publish once a category is selected',
		{tag: '@LPD-89784'},
		async ({
			apiHelpers,
			contentsPage,
			page,
			tagsPage,
			vocabulariesPage,
		}) => {
			const site = await apiHelpers.headlessAdminSite.getSite('L_CMS');

			const categoryName = getRandomString();
			const vocabularyName = getRandomString();

			await createCategories({
				apiHelpers,
				assetLibraries: [{id: -1}],
				assetTypes: [
					{
						required: true,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				categoryNames: [{name: categoryName}],
				siteId: site.id,
				vocabularyName,
			});

			let tagName;

			try {
				await contentsPage.goto();
				await contentsPage.createContent('Basic Web Content');

				const title = getRandomString();

				await page
					.getByPlaceholder('New Basic Web Content')
					.fill(title);

				await contentsPage.openSidePanel('Categorization');

				tagName = getRandomString();

				await selectTag({page, tagName});

				await contentsPage.publishButton.click();

				await expect(
					page
						.locator('.content-editor__side-panel .sidebar-header')
						.filter({hasText: 'Categorization'})
				).toBeVisible();

				await expect(
					page
						.locator('.form-group.has-error')
						.getByPlaceholder('Add category')
				).toBeFocused();

				await expect(
					page.getByText(
						'Please enter at least one category for all mandatory vocabularies.'
					)
				).toBeVisible();

				await expect(
					page.locator('.label-item', {hasText: tagName})
				).toBeAttached();

				await expect(
					page.getByPlaceholder('New Basic Web Content')
				).toHaveValue(title);

				// Select the required category and publish again

				await selectCategory({categoryName, page});

				await expect(
					page.locator('.label-item', {hasText: categoryName})
				).toBeAttached();

				await expect(
					page.locator('.form-group.has-error')
				).toBeHidden();

				await expect(
					page.getByText(
						'Please enter at least one category for all mandatory vocabularies.'
					)
				).toBeHidden();

				await contentsPage.publishButton.click();

				await expect(
					page.locator('.table-list-title a', {hasText: title})
				).toBeVisible();

				// Delete content

				await contentsPage.goto();
				await contentsPage.deleteContent(title);
			}
			finally {

				// Delete tags

				if (tagName) {
					await tagsPage.goto();
					await tagsPage.deleteTag(tagName);
				}

				// Delete vocabulary and its categories

				await vocabulariesPage.goto();
				await vocabulariesPage.deleteVocabulary(vocabularyName);
			}
		}
	);
});

test(
	'Can save content as draft',
	{tag: '@LPD-66942'},
	async ({contentsPage, page}) => {

		// Create a content

		await contentsPage.goto();

		await contentsPage.createContent('Basic Web Content');

		const title = getRandomString();

		await page.getByPlaceholder(`New Basic Web Content`).fill(title);

		// Save as draft

		await contentsPage.saveContentAsDraft();

		// Go back to the Content list and check that the content is saved as
		// draft

		await contentsPage.goto();

		await expect(
			page
				.locator('tr', {hasText: title})
				.or(page.locator('.card-row', {hasText: title}))
				.locator('.cell-embedded-status')
		).toHaveText(/draft/i);

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

			await contentsPage.createContent('Basic Web Content');

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

			await expect(displayDateHiddenInput).not.toBeInViewport();

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

			await expect(displayDateHiddenInput).not.toBeInViewport();

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
			).toHaveText(/scheduled/i);

			await contentsPage.viewShowDetails(title);

			await expect(page.getByText('Display Date')).toBeVisible();
			await expect(page.getByText(`10/31/${nextYear}`)).toBeVisible();

			// Delete content

			await contentsPage.goto();

			await contentsPage.deleteContent(title);
		}
	);

	test(
		'Do not allow scheduling a publication if there are errors in the fields',
		{tag: '@LPD-68099'},
		async ({contentsPage, page}) => {

			// Create a content

			await contentsPage.goto();

			await contentsPage.createContent('Basic Web Content');

			await contentsPage.openSidePanel('Schedule');

			const title = getRandomString();

			await page.getByPlaceholder('New Basic Web Content').fill(title);

			// Fill the expiration date input with an error

			const expireCheckbox = page.getByLabel('Never Expire');

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
				page.getByRole('heading', {name: 'New Basic Web Content'})
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

	test(
		'Schedule dates are maintained after a failed publication',
		{tag: '@LPD-68099'},
		async ({apiHelpers, contentsPage, page}) => {

			// Create a required vocabulary

			const vocabularyName = getRandomString();

			const siteId = await apiHelpers.headlessAdminUser
				.getSiteByFriendlyUrlPath('cms')
				.then((response) => response.id);

			await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1}],
				assetTypes: [
					{
						required: true,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name: vocabularyName,
				siteId,
				visibilityType: 'PUBLIC',
			});

			// Create a content

			await contentsPage.goto();

			await contentsPage.createContent('Basic Web Content');

			const title = getRandomString();

			await page.getByPlaceholder('New Basic Web Content').fill(title);

			const nextYear = new Date().getFullYear() + 1;

			const expirationDateValue = `05/12/${nextYear} 12:55 PM`;
			const reviewDateValue = `05/12/${nextYear} 12:57 PM`;

			// Set expiration and review dates through the Schedule side
			// panel

			await contentsPage.openSidePanel('Schedule');

			await page.getByLabel('Never Expire').uncheck();

			await page
				.getByRole('textbox', {name: 'Expiration Date'})
				.fill(expirationDateValue);

			await page.keyboard.press('Tab');

			await page.getByLabel('Never Review').uncheck();

			await page
				.getByRole('textbox', {name: 'Review Date'})
				.fill(reviewDateValue);

			await page.keyboard.press('Tab');

			// Set the display date through the Schedule Publication modal

			await contentsPage.openSchedulePublication();

			const displayDateValue = `10/31/${nextYear} 12:30 PM`;

			await page
				.getByRole('textbox', {name: 'Date and Time'})
				.fill(displayDateValue);

			await page.keyboard.press('Tab');

			// Click Schedule. Submission fails on the server because the
			// required vocabulary has no category selected.

			await page.getByRole('button', {name: 'Schedule'}).click();

			// After the failed submit the side panel is closed. Reopen it
			// and check that the expiration and review dates are preserved.

			await contentsPage.openSidePanel('Schedule');

			await expect(
				page.getByRole('textbox', {name: 'Expiration Date'})
			).toHaveValue(expirationDateValue);

			await expect(
				page.getByRole('textbox', {name: 'Review Date'})
			).toHaveValue(reviewDateValue);

			// The modal is also closed. Reopen it and check that the
			// display date is preserved.

			await contentsPage.openSchedulePublication();

			await expect(
				page.getByRole('textbox', {name: 'Date and Time'})
			).toHaveValue(displayDateValue);
		}
	);
});

test(
	'The Rich Text required error only occurs when the field has no value',
	{tag: ['@LPD-69695', '@LPD-93785']},
	async ({contentsPage, page, structureBuilderPage}) => {

		// Create a structure with a required Rich Text field

		await structureBuilderPage.goToCreateStructure();

		const structureLabel = `Structure${getRandomInt()}`;

		await structureBuilderPage.changeStructureSettings({
			label: structureLabel,
			name: structureLabel,
		});

		await structureBuilderPage.addField('Rich Text');

		await structureBuilderPage.changeFieldSettings({mandatory: true});

		await structureBuilderPage.publishStructure();

		// Create a content of the new structure

		await contentsPage.goto();

		await contentsPage.createContent(structureLabel);

		const contentTitle = getRandomString();

		await page.getByLabel('Title').fill(contentTitle);

		// Try to publish with the empty Rich Text and check the error

		await contentsPage.publishButton.click();

		await expect(
			page.locator('.rich-text-input [data-required-error]')
		).toHaveText('This field is required.');

		// The AI Creator button is not available in the CMS Rich Text editor

		await expect(page.getByTitle('Create AI Content')).toBeHidden();

		// Fill the Rich Text field and publish the content

		const richTextField = page.locator('.ck-editor__editable');

		await richTextField.fill('This is very cool content');

		await contentsPage.publishButton.click();

		// Edit the content, publish it and check that there is no required error in the Rich Text field

		await contentsPage.editContent(contentTitle);

		await richTextField.waitFor();

		await contentsPage.publishButton.click();

		await expect(
			page.locator('.table-list-title a', {hasText: contentTitle})
		).toBeAttached();

		// Delete content

		await contentsPage.deleteContent(contentTitle);
	}
);

test(
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

		await waitForAlert(page, `Success:${title} was moved`, {
			autoClose: false,
		});
	}
);

test(
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

		// Verify the embedded structure renders in the customize editor

		await structureBuilderPage.customizeEditor();

		await expect(
			page.locator('.lfr-layout-structure-item-basic-component-accordion')
		).toBeVisible();

		await page
			.locator('.management-bar')
			.getByRole('link', {name: 'Back'})
			.click();

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

		const titleTextboxes = page
			.locator('.lfr-layout-structure-item-form-relationship')
			.getByRole('textbox', {exact: true, name: 'Title'});

		await expect(titleTextboxes).toHaveCount(2);

		const firstText = titleTextboxes.first();

		await firstText.fill('First Text');

		const secondText = titleTextboxes.last();

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

		await waitForAlert(page, `Success:${title} was moved`, {
			autoClose: false,
		});
	}
);

test(
	'Repetable text input is validated correctly',
	{
		tag: ['@LPD-69446', '@LPD-92353'],
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

		await page.getByLabel('Limit Characters').click();

		const maximumNumberOfCharactersInput = page.getByLabel(
			'Maximum Number of Characters'
		);

		maximumNumberOfCharactersInput.fill('5');

		await maximumNumberOfCharactersInput.blur();

		// Create repeatable group with two of them

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Text'}],
			label: 'Repeatable Group 1',
		});

		await structureBuilderPage.publishStructure();

		// Go to CMS Contents

		await contentsPage.goto();

		// Create new content

		await contentsPage.createContent(structureLabel);

		const title = getRandomString();

		await page.getByPlaceholder(`New ${structureLabel}`).fill(title);

		// Fill the fields

		const firstText = page.getByRole('textbox', {name: 'Text'}).first();

		await firstText.pressSequentially('MoreThan5Characters');

		await expect(firstText).toHaveValue('MoreThan5Characters');

		// Save content

		await contentsPage.publishButton.click();

		// Check that the alert is displayed

		await expect(
			page.getByText('Value exceeds maximum length of 5 for field Text.')
		).toBeVisible();

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

		await waitForAlert(page, `Success:${title} was moved`, {
			autoClose: false,
		});
	}
);

// Create a structure with a multiselect field, save content only adding text and then edit it again

test(
	'Create a structure with a multiselect field, save content only adding text and then edit it again',
	{tag: '@LPD-73147'},
	async ({contentsPage, page, picklistBuilderPage, structureBuilderPage}) => {

		// Create picklist

		await picklistBuilderPage.goto();

		const picklistName = getRandomString();

		await picklistBuilderPage.nameInput.fill(picklistName);

		await picklistBuilderPage.addOption('Option 1');
		await picklistBuilderPage.addOption('Option 2');
		await picklistBuilderPage.addOption('Option 3');

		await picklistBuilderPage.savePicklist();

		// Create structure

		await structureBuilderPage.goToCreateStructure();

		const structureLabel = `StructureMultiSelect${getRandomInt()}`;

		await structureBuilderPage.changeStructureSettings({
			label: structureLabel,
			name: structureLabel,
		});

		// Add multiselect field

		await structureBuilderPage.addField('Select from List');

		await structureBuilderPage.changeFieldSettings({
			label: 'Select from List',
			multiselection: true,
			picklist: picklistName,
		});

		await structureBuilderPage.publishStructure();

		// Create a content, only fill the title

		await contentsPage.goto();

		await contentsPage.createContent(structureLabel);

		const title = getRandomString();

		await page.getByPlaceholder(`New ${structureLabel}`).fill(title);

		await contentsPage.publishButton.click();

		// Edit the created content

		await contentsPage.editContent(title);

		await expect(
			page.getByRole('combobox', {name: 'Select from List'})
		).toBeVisible();
	}
);

test(
	'Image selector shows only files with allowed extensions',
	{tag: '@LPD-78771'},
	async ({apiHelpers, contentsPage, page}) => {
		const validImageFileBase64 =
			'iVBORw0KGgoAAAANSUhEUgAAAD0AAAAXCAIAAAA3N9DuAAAAA3NCSVQICAjb4U/gAAAAEHRFWHRTb2Z0d2FyZQBTaHV0dGVyY4LQCQAABX9JREFUWMOVWFuS3EgOA8BUlXpifuYee4u9/3nWdonEfDAzS91+RKw+HOooKUmCAEiZ//3nP7ZJ4jeX7XR+v15VBizSgO2Q+iarAIoMMcv9FgkbBAyMoI0s7yD9EwkDNiJYZRshAihb69Eqg+igJLheV7k66XKVqxPtd668Ml8kg3GOI0QDaZRB8qrKLBtlhEjilSXO2B24k77SWRbRvxIQYVjiDGV4HbLfLTurIkiybAAE+qiyh6jOUlRnTLI7MGIs8Bgc58C36+XGwBhS2SGGomEWmeURFJHlhr9DShB5pUmTrLKkKneRIEIsG4bIxluk55M03ijMbAFU1c7vfpOZmyrllMZfj49DDFFilm280k2ARqtrLlti2VfWBK9w5aytyiJg9yEkYPRpNsiJ9ysL8xxwVtcs8JUWAElfON0ZR8QuQ5z3j3Gex5PElfXKJPDKjFVGh5EaRhxDG6QmdFcIshbBslxLFTN6OctDnKcRV7rFAGAEI6gvisxKAArh95crz/E8H3FEADgivv3IbnRnUGUDIb6yMn2lR9ALrbJhj+BOdyfUD4zgFGhZxMYbt+q0VdgFhALAEu6vr4ijKs/xHEECIzhCTfFGtNEt4zyi/2ySvH1GrCUAACGGeGWrhd9fWSurMmxzNSHTACo9GyryXQC6AP1siLsnZJD6OD4eQ53T5mWVSXT3s/y6agRJRNx8xsDUH0hg8afsso8Rn6AlSYok0ZIAIc+y3g8aBpDVnlir7uKqLRSc9eIxzueYTduV2t7AP4/49iNtvK7qVofocsu3jCuduTGZNkoR7AtdTKO+K1HbwB3O+8Xlkv0Mya6EoO02osc4/36em7sSSdrdQ2R5hNq5bVSrkOBS6tD0uJ5BTYw2l+m5mHNHYmdvW+0/d1f55ey8WeRE13C/UlVS/PV4PoeaBp3QHpDdm86AgFaik4qk4a6n/ZHLOwiQ7ZPommcGPXS6Hz1rCKBe+pS6vyi1n3oPLAmAGOfx8YiYLxiA28WwPDHEgv3WnNuwuyEjKPEqw+b0ykYeLu9zRFRZk453auuoT5zh/uVP5mjbPsbjHNEairZxslqFbR3kPuhtmkY1hYwRcsO7RHyne/fqjXfd3PBO9DtnWK/fyaCqloo44nlE9LwkcGW1XZI0sCcMyRFqF+KyvKZKO49tENIcMS6X3fvJMhnM/Ysk8/vPDti2aB2/ZL9dWxvdlqHjHM8h7r2KxFX+VLMXCTits827DdQAyUz3iWWDcwtoAxAA4p0o49zrivJ/70iebvgHz7nbDqnz+BihKtjIdFZdWe8dkGvxMLD/bZdsmqwFBvd6PcmjBql5Uq50eU1NHX8DDomLlEOB/+c6x3kOHYMGhjRC0wRbW0trIf7IypwDq6dYdeprLRHZm8n0KIF7Tx+KoAj2lnJVAr3Ru9ybHT+p9feV0NeykXN0yB4oBBaPq3xllZ3pI9QEaOPvydKCjlVeD/krTUCe697sdboM3ylu1Oa089tbvr0nVeJXVmOOfX+M8zxOco5MrY1A0wHfHttxehXZnrNJomDvFK8srR2ogkpXCz+rdurBaLcu23p82VUIhiKoPxNG0DnOj0d01LTJxeP+7tLien9kiHMuqpcldltG0Pb5CO0RaDT+L4G9nzRtyiYIWGtO3SVoOKvSRXDb7c9jyzDJ8/h4jngewcasU19LIoCmafvCVuf+eCNxpY+hKy3DdpGCC2Dx0V0biqtyB+51oNuy8rm2LbK+++bsWQnnZyFMLzri+RzPkHr1bZb3l47IH9ccJFWG3Z8dNvZe1VwfncS0IApoRZfITjp9I7dNMF2ihsKMTRjrKdLvOSDwq15FNQSiPo5nEPu7rtlse4i5uFHzvwB4rc60b/aU/Rc6sWizbSKbGQAAAABJRU5ErkJggg==';

		const applicationName = 'cms/basic-documents';

		const allowedFileExtensions = [
			'apng',
			'avif',
			'gif',
			'jpg',
			'jpeg',
			'png',
			'svg',
			'tiff',
			'webp',
		];

		const filePrefix = getRandomString();

		const allowedFileNames = allowedFileExtensions.map(
			(extension) => `${filePrefix}.${extension}`
		);

		const fileNames = [...allowedFileNames, `${filePrefix}.pdf`];

		for (const fileName of fileNames) {
			const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: validImageFileBase64,
						name: fileName,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: fileName,
				},
				applicationName,
				'Default'
			);

			postedObjectEntries.push({
				applicationName,
				id: String(objectEntry.id),
			});
		}

		await contentsPage.goto();

		await contentsPage.createContent('Basic Web Content');

		await waitForEditor({page});

		await page.getByRole('button', {name: 'Image'}).click();

		await expect(page.getByText('Select Image')).toBeVisible();

		await waitForFDS({
			page,
			visualizationMode: EFDSVisualizationMode.CARDS,
		});

		const searchInput = page.getByRole('searchbox', {name: 'Search'});

		await searchInput.fill(filePrefix);
		await searchInput.press('Enter');

		await waitForFDS({
			page,
			visualizationMode: EFDSVisualizationMode.CARDS,
		});

		for (const fileName of allowedFileNames) {
			await expect(
				page.getByLabel(fileName, {exact: true})
			).toBeVisible();
		}

		await expect(
			page.getByLabel(`${filePrefix}.pdf`, {exact: true})
		).not.toBeVisible();
	}
);

test(
	'Image selector lets the user open the multiple file uploader from the Upload Files button',
	{tag: '@LPD-88407'},
	async ({contentsPage, page}) => {
		await contentsPage.goto();

		await contentsPage.createContent('Basic Web Content');

		await waitForEditor({page});

		await page.getByRole('button', {name: 'Image'}).click();

		await expect(page.getByText('Select Image')).toBeVisible();

		const uploadFilesButton = page.getByRole('button', {
			name: 'Upload Files',
		});

		await expect(uploadFilesButton).toBeVisible();

		await uploadFilesButton.click();

		await expect(
			page.getByText('Drag and Drop your files or')
		).toBeVisible();

		await expect(
			page.getByRole('button', {name: 'Select Files'})
		).toBeVisible();
	}
);

test(
	'Video selector inserts an uploaded video file as a video element',
	{tag: '@LPD-88969'},
	async ({apiHelpers, contentsPage, page}) => {
		const videoFileBase64 = 'AAAAIGZ0eXBpc29tAAACAGlzb21pc28yYXZjMW1wNDE=';

		const applicationName = 'cms/basic-documents';

		const fileName = `sample-${getRandomString()}.mp4`;

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: videoFileBase64,
					name: fileName,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileName,
			},
			applicationName,
			'Default'
		);

		postedObjectEntries.push({
			applicationName,
			id: String(objectEntry.id),
		});

		await contentsPage.goto();

		await contentsPage.createContent('Basic Web Content');

		await waitForEditor({page});

		await page.getByRole('button', {name: 'Video'}).click();

		await expect(
			page.getByTestId('visualization-mode-cards')
		).toBeVisible();

		await page.getByLabel(`Select ${fileName}`).check();

		await page.getByRole('button', {exact: true, name: 'Select'}).click();

		await expect(page.locator('.modal-header')).toBeHidden();

		await expect(page.locator('.ck-editor__editable video')).toBeVisible();
	}
);

test(
	'Tags are not cleared after saving content when the categories panel is never opened',
	{tag: '@LPD-79085'},
	async ({contentsPage, page}) => {
		const tagName = getRandomString();
		const title = getRandomString();

		try {
			await test.step('Create a new basic web content', async () => {
				await contentsPage.goto();

				await contentsPage.createContent('Basic Web Content');
			});

			await test.step('Fill required fields', async () => {
				await page.getByLabel('Title').fill(title);
			});

			await test.step('Add a tag', async () => {
				await contentsPage.openSidePanel('Categorization');

				const tagsAutocomplete = page.getByPlaceholder('Add tag');

				await tagsAutocomplete.click();

				await page.keyboard.type(tagName);

				const newTagOption = page.getByRole('option', {
					name: 'Create New Tag:',
				});

				await newTagOption.waitFor();
				await newTagOption.click();

				// Ideally we'd remove this, but right now if you save too
				// quickly, the initial tag might not have enough time to
				// populate.

				await expect(
					page.locator('input[name="assetTagNames"]')
				).toHaveValue(tagName);
			});

			await test.step('Save content', async () => {
				await contentsPage.saveContent();
			});

			await test.step('Edit the content and save without opening the categories panel', async () => {
				await contentsPage.editContent(title);

				// Ideally we'd remove this, but right now if you save too
				// quickly, the initial tag might not have enough time to
				// populate.

				await expect(
					page.locator('input[name="assetTagNames"]')
				).toHaveValue(tagName);

				await contentsPage.saveContent();
			});

			await test.step('Edit the content and check that the tag is not cleared', async () => {
				await contentsPage.editContent(title);

				await contentsPage.openSidePanel('Categorization');

				await expect(
					page.locator('.label-item', {hasText: tagName})
				).toBeVisible();
			});
		}
		finally {
			await test.step('Delete content', async () => {
				await contentsPage.goto();

				await contentsPage.deleteContent(title);
			});
		}
	}
);
