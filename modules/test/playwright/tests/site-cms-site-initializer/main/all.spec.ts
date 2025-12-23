/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import fs from 'fs/promises';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {waitForModal} from '../../../utils/waitFor';
import {waitForAlert} from '../../../utils/waitForAlert';
import {structureBuilderPagesTest} from '../structure-builder/fixtures/structureBuilderPagesTest';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-17564': {enabled: true},
	}),
	loginTest(),
	structureBuilderPagesTest
);

test(
	'Confirmation modal is shown when delete a single content in a space with recycle bin disabled',
	{tag: '@LPD-64867'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = `Space ${getRandomString()}`;
		const file1Title = `Content ${getRandomString()}`;
		let space = null;

		await test.step('Create a new Space with recycle bin disabled', async () => {
			space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {
					trashEnabled: false,
				},
				type: 'Space',
			});
		});

		await test.step('Create a content for that space', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: file1Title,
				},
				applicationName,
				spaceName
			);
		});

		await test.step('Delete content', async () => {
			await assetsPage.gotoAll();

			await assetsPage.execItemAction({
				action: 'Delete',
				filter: file1Title,
			});
		});

		await test.step('Accept confirmation modal', async () => {
			await expect(
				page.getByRole('heading', {name: `Delete "${file1Title}"`})
			).toBeVisible();

			await expect(
				page.getByText('You are about to delete the asset')
			).toBeVisible();

			await page.getByRole('button', {name: 'Delete'}).click();

			await waitForAlert(page, `${file1Title} was successfully deleted.`);

			await expect(
				page.getByRole('cell', {name: file1Title})
			).not.toBeVisible();
		});

		await test.step('delete created space', async () => {
			await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(space.id);
		});
	}
);

test(
	'Only content folders will be displayed when copying content',
	{tag: '@LPD-72879'},
	async ({apiHelpers, assetsPage, page}) => {
		const file1Title = `Content ${getRandomString()}`;
		const file2Title = `File ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;

		await test.step('Create a new Space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});
		});

		await test.step('Create a content for that space', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: file1Title,
				},
				'cms/basic-web-contents',
				spaceName
			);
		});

		await test.step('Create a file for that space', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: 'R0lGODlhAQABAAAAACw=',
						name: `file_${getRandomString()}.png`,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: file2Title,
				},
				'cms/basic-documents',
				spaceName
			);
		});

		await test.step('Copy content', async () => {
			await assetsPage.gotoAll();

			await assetsPage.execItemAction({
				action: 'Copy To',
				filter: file1Title,
			});
		});

		await test.step('Check content folders', async () => {
			await page.getByLabel(spaceName).click();
			await expect(
				page.getByText('Showing 1 to 1 of 1 entries.')
			).toBeVisible();

			await expect(page.getByLabel('contents')).toBeVisible();
		});

		await test.step('Copy file', async () => {
			await assetsPage.gotoAll();

			await assetsPage.execItemAction({
				action: 'Copy To',
				filter: file2Title,
			});
		});

		await test.step('Check file folders', async () => {
			await page.getByLabel(spaceName).click();
			await expect(
				page.getByText('Showing 1 to 1 of 1 entries.')
			).toBeVisible();

			await expect(page.getByLabel('files')).toBeVisible();
		});
	}
);

test(
	'Can delete multiple contents across spaces with and without recycle bin enabled',
	{tag: '@LPD-62787'},
	async ({apiHelpers, assetsPage, page, recycleBinPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceNameWithRecycleBin = `Space ${getRandomString()}`;
		const spaceNameWithoutRecycleBin = `Space ${getRandomString()}`;
		const file1Title = `title ${getRandomString()}`;
		const file2Title = `title ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceNameWithRecycleBin,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
				trashEnabled: true,
			},
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceNameWithoutRecycleBin,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
				trashEnabled: false,
			},
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file1Title,
			},
			applicationName,
			spaceNameWithRecycleBin
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file2Title,
			},
			applicationName,
			spaceNameWithoutRecycleBin
		);

		await assetsPage.gotoAll();

		await assetsPage.selectItems([file1Title, file2Title]);

		await page
			.getByTestId(/visualization-mode/)
			.getByLabel('Actions')
			.click();

		await page.getByRole('menuitem', {name: 'Delete'}).click();

		expect(page.getByText('Some of the selected files')).toBeVisible();

		await page.getByRole('button', {name: 'Delete'}).click();

		await page.reload();

		await expect(
			page.getByRole('cell', {name: file1Title})
		).not.toBeVisible();
		await expect(
			page.getByRole('cell', {name: file2Title})
		).not.toBeVisible();

		await recycleBinPage.goto();

		await expect(page.getByRole('cell', {name: file1Title})).toBeVisible();
	}
);

test(
	'Can delete multiple contents in a space with recycle bin disabled',
	{tag: '@LPD-62787'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = `Space ${getRandomString()}`;
		const file1Title = `title ${getRandomString()}`;
		const file2Title = `title ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
				trashEnabled: false,
			},
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file1Title,
			},
			applicationName,
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file2Title,
			},
			applicationName,
			spaceName
		);

		await assetsPage.gotoAll();

		await assetsPage.selectItems([file1Title, file2Title]);

		await page
			.getByTestId(/visualization-mode/)
			.getByLabel('Actions')
			.click();

		await page.getByRole('menuitem', {name: 'Delete'}).click();

		await expect(
			page.getByText('You are about to permanently')
		).toBeVisible();

		await page.getByRole('button', {name: 'Delete'}).click();

		await page.reload();

		await expect(
			page.getByRole('cell', {name: file1Title})
		).not.toBeVisible();
		await expect(
			page.getByRole('cell', {name: file2Title})
		).not.toBeVisible();
	}
);

test(
	'Can delete multiple contents in a space with recycle bin enabled',
	{tag: '@LPD-62787'},
	async ({apiHelpers, assetsPage, page, recycleBinPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = `Space ${getRandomString()}`;
		const file1Title = `title ${getRandomString()}`;
		const file2Title = `title ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
				trashEnabled: true,
			},
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file1Title,
			},
			applicationName,
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file2Title,
			},
			applicationName,
			spaceName
		);

		await assetsPage.gotoAll();

		await assetsPage.selectItems([file1Title, file2Title]);

		await page
			.getByTestId('visualization-mode-table')
			.getByLabel('Actions')
			.click();

		await page.getByRole('menuitem', {name: 'Delete'}).click();

		await page.reload();

		await recycleBinPage.goto();

		await expect(page.getByRole('cell', {name: file1Title})).toBeVisible();
		await expect(page.getByRole('cell', {name: file2Title})).toBeVisible();
	}
);

test(
	'Can view Share modal for added content',
	{tag: '@LPD-62554'},
	async ({apiHelpers, assetsPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const file1Title = `Title ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;
		let objectEntry1;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
			},
			type: 'Space',
		});

		try {
			objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: file1Title,
				},
				applicationName,
				spaceName
			);

			await assetsPage.gotoAll();

			await assetsPage.execItemAction({
				action: 'Share',
				filter: file1Title,
			});

			await expect(assetsPage.modal.title).toContainText(file1Title);
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry1.id)
			);
		}
	}
);

test(
	'Info Panel Comments and view Delete confirmation modal for added content',
	{tag: '@LPD-62554'},
	async ({apiHelpers, assetsPage, infoPanelPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = `Space ${getRandomString()}`;
		let objectEntry1;

		const file1Title = `title ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
				trashEnabled: false,
			},
			type: 'Space',
		});

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

			const saveButton = rootComment.getByRole('button', {name: 'Save'});

			await expect(saveButton).toBeEnabled();

			await saveButton.click();

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

			const comment = rootComment.locator('article');

			await expect(comment.filter({hasText: content})).toBeAttached();

			if (parentComment) {
				await expect(comment.getByText('Reply')).not.toBeAttached();
			}

			return {comment, editor};
		};

		try {
			objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: file1Title,
				},
				applicationName,
				spaceName
			);

			await test.step('Go to All Assets, check the Location in Details tab and open the Info Panel Comments', async () => {
				await assetsPage.gotoAll();

				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: file1Title,
				});

				await expect(
					page.getByRole('heading', {name: file1Title})
				).toBeVisible();

				await expect(
					page
						.locator('.asset-metadata-section')
						.getByText('Location')
				).toBeVisible();

				await expect(
					page.locator('div .space-breadcrumb').filter({
						hasText: 'Content',
					})
				).toBeVisible();

				await expect(
					page.locator('div .space-breadcrumb').filter({
						hasText: 'S',
					})
				).toBeVisible();

				await expect(
					page.locator('div .space-breadcrumb').filter({
						hasText: spaceName,
					})
				).toBeVisible();

				await infoPanelPage.selectTab('More').click();
				await infoPanelPage.dropdownTab('Comments').click();
				await infoPanelPage.selectTab('More').click();
			});

			await test.step('Add, edit and delete comments in the info Panel Comments', async () => {
				const parentCommentContent = 'New Comment';

				const {comment, editor} = await addComment({
					content: parentCommentContent,
					page,
				});

				await editor.click({force: true});

				await page.keyboard.type('New comment to cancel');

				await page.getByRole('button', {name: 'Cancel'}).click();

				await expect(editor).not.toContainText('New comment to cancel');

				await comment.getByText('Reply').click();

				const {comment: childComment} = await addComment({
					content: 'New child comment',
					page,
					parentComment: comment,
				});

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page
						.getByRole('menuitem')
						.filter({hasText: 'edit'}),
					trigger: page.getByTitle('actions').first(),
				});

				await page.getByText(parentCommentContent).selectText();

				await page.keyboard.type('Editing the comment');

				await comment.getByRole('button', {name: 'Save'}).click();

				await waitForAlert(
					page,
					'Success:Your comment has been edited.',
					{
						autoClose: true,
					}
				);

				await expect(comment.first()).toContainText(
					'Editing the comment'
				);

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page
						.getByRole('menuitem')
						.filter({hasText: 'edit'}),
					trigger: page.getByTitle('actions').nth(1),
				});

				await page.getByText('New child comment').selectText();

				await page.keyboard.type('Editing the child comment');

				await childComment.getByRole('button', {name: 'Save'}).click();

				await expect(childComment).toContainText(
					'Editing the child comment'
				);

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page
						.getByRole('menuitem')
						.filter({hasText: 'delete'}),
					trigger: page.getByTitle('actions').nth(1),
				});

				await waitForAlert(
					page,
					'Success:Your comment has been deleted.',
					{
						autoClose: true,
					}
				);
			});
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry1.id)
			);
		}
	}
);

test(
	'Info Panel Categories tab',
	{tag: '@LPD-68491'},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		infoPanelPage,
		page,
		spaceSummaryPage,
	}) => {
		const applicationName = 'cms/basic-web-contents';
		let categoryLabel;
		const categoryName = getRandomString();
		const file1Title = `title ${getRandomString()}`;
		let objectEntry;
		const spaceName = 'Default';
		const tagName = getRandomString();
		let tagLabel;
		let user;
		const vocabularyName = getRandomString();

		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const vocabularyId = await apiHelpers.headlessAdminTaxonomy
			.postSiteTaxonomyVocabulary({
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
			})
			.then((response) => response.id);

		const categoryId = await apiHelpers.headlessAdminTaxonomy
			.postTaxonomyVocabularyTaxonomyCategory({
				name: categoryName,
				vocabularyId,
			})
			.then((response) => response.id);

		await apiHelpers.headlessAdminTaxonomy.putTaxonomyVocabulariesTaxonomyVocabularyPermissions(
			vocabularyId,
			{actionIds: ['VIEW'], roleName: 'Site Member'}
		);

		await apiHelpers.headlessAdminTaxonomy.putTaxonomyCategoriesTaxonomyCategoryPermissions(
			categoryId,
			{actionIds: ['VIEW'], roleName: 'Site Member'}
		);

		try {
			objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: file1Title,
				},
				applicationName,
				spaceName
			);

			await test.step('Go to All Assets and open the Info Panel Categorization Tab', async () => {
				await assetsPage.gotoAll();

				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: file1Title,
				});

				await expect(
					page.getByRole('heading', {name: file1Title})
				).toBeVisible();

				await infoPanelPage.selectTab('Categorization').click();
			});

			await test.step('Add a new tag to the content', async () => {
				const tagsAutocomplete = page.getByPlaceholder('Add tag');

				await tagsAutocomplete.fill(tagName);

				const newTagOption = page.getByRole('option', {
					name: 'Create New Tag:',
				});

				await newTagOption.waitFor();
				await newTagOption.click();

				tagLabel = page.locator('.label-item', {hasText: tagName});

				await expect(tagLabel).toBeAttached();
			});

			await test.step('Add a new category to the content', async () => {
				const categoriesAutocomplete =
					page.getByPlaceholder('Add category');

				await categoriesAutocomplete.fill(categoryName);

				const option = page.getByRole('option', {name: categoryName});

				await option.waitFor();
				await option.click();

				categoryLabel = page.locator('.label-item', {
					hasText: categoryName,
				});

				await expect(categoryLabel).toBeAttached();
			});

			await test.step('Create an user and add to the Space', async () => {
				user = await apiHelpers.headlessAdminUser.postUserAccount();

				userData[user.alternateName] = {
					name: user.givenName,
					password: 'test',
					surname: user.familyName,
				};

				await spaceSummaryPage.goto(spaceName);

				await spaceSummaryPage.addUserOrUserGroup(user.name, 'users');
			});

			await test.step('Login as a space member and go to Info Panel Categorization tab', async () => {
				await performLogout(page);

				await performLogin(page, user.alternateName);

				await assetsPage.gotoAll();

				await expect(assetsPage.getItem(file1Title)).toBeVisible();

				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: file1Title,
				});

				await expect(
					page.getByRole('heading', {name: file1Title})
				).toBeVisible();

				await infoPanelPage.selectTab('Categorization').click();
			});

			await test.step('Check that space member can see tags and vocabulary but cannot edit them', async () => {
				await expect(tagLabel).toBeAttached();
				await expect(categoryLabel).toBeAttached();
				await expect(
					page.getByLabel(tagName).getByLabel('Close')
				).toBeDisabled();
				await expect(
					page.getByLabel(categoryName).getByLabel('Close')
				).toBeDisabled();
			});

			await test.step('Check that space member can see tags and vocabulary but cannot edit them also in the Content Editor', async () => {
				await assetsPage.dataSetFragmentPage
					.assetLink(file1Title)
					.click();

				await expect(
					page.getByRole('heading', {
						name: `Edit ${objectEntry.title}`,
					})
				).toBeVisible();

				await contentsPage.openSidePanel('Categorization');

				await expect(tagLabel).toBeAttached();
				await expect(categoryLabel).toBeAttached();
				await expect(
					page.getByLabel(tagName).getByLabel('Close')
				).toBeDisabled();
				await expect(
					page.getByLabel(categoryName).getByLabel('Close')
				).toBeDisabled();
			});
		}
		finally {
			await performLogout(page);

			await performLogin(page, 'test');

			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry.id)
			);

			await apiHelpers.headlessAdminTaxonomy.deleteTaxonomyVocabulary(
				vocabularyId
			);
		}
	}
);

test(
	'Dragging and dropping files into the data set opens upload modal',
	{tag: '@LPD-58618'},
	async ({assetsPage, page}) => {
		await assetsPage.gotoAll();

		const dataSetWrapper = page.locator('div.data-set-wrapper').first();
		const dataTransfer = await page.evaluateHandle(
			(data) => {
				const dt = new DataTransfer();

				const file = new File(
					[data.toString('hex')],
					'file_upload_image_1.jpeg',
					{
						type: 'image/jpg',
					}
				);
				dt.items.add(file);

				return dt;
			},
			readFileSync(
				path.join(__dirname, '/dependencies/file_upload_image_1.jpg')
			)
		);

		await dataSetWrapper.dispatchEvent('dragstart', {dataTransfer});
		await dataSetWrapper.dispatchEvent('dragenter', {dataTransfer});
		await dataSetWrapper.dispatchEvent('dragover', {dataTransfer});

		await dataSetWrapper.dispatchEvent('drop', {dataTransfer});
		await dataSetWrapper.dispatchEvent('dragend', {dataTransfer});

		await expect(assetsPage.modal.container).toBeVisible();

		await expect(assetsPage.modal.title).toContainText(
			'Upload Multiple Files'
		);
		await expect(assetsPage.modal.body).toContainText(
			'file_upload_image_1.jpeg'
		);
	}
);

test(
	'Bulk Actions Monitor component',
	{tag: ['@LPD-57835', '@LPD-74095', '@LPD-74096']},
	async ({apiHelpers, assetsPage, page}) => {
		const basicWebContent = 'cms/basic-web-contents';
		const bulkActionTasks = 'cms/bulk-action-tasks';
		const bulkActionTasksItems = 'cms/bulk-action-task-items';
		const spaceName = 'Default';

		const createdFiles = [];
		const filesNames = [
			getRandomString(),
			getRandomString(),
			getRandomString(),
		];
		let tasks;

		for (const fileName of filesNames) {
			const file = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: fileName,
				},
				basicWebContent,
				spaceName
			);
			createdFiles.push(file);
		}
		try {
			await test.step('Select 1 asset and delete it using the Bulk Action', async () => {
				await assetsPage.gotoAll();

				await expect(assetsPage.taskStatusFormsButton).toBeHidden();

				await assetsPage
					.getItem(filesNames[0])
					.locator('input[title="Select Item"]')
					.check();
				await assetsPage.execBulkItemAction('Delete');

				await waitForAlert(
					page,
					'Info:Delete action started for 1 asset.',
					{
						autoClose: true,
						type: 'info',
					}
				);
			});

			await test.step('Check that the processingTask button appear and click on it', async () => {
				await expect(assetsPage.processingTasksButton).toBeVisible();

				await assetsPage.processingTasksButton.click();
			});

			await test.step('After the click, the dropdown component is shown and 1 task with details is visible', async () => {
				await expect(
					assetsPage
						.taskStatusDropdownItemButton('Assets Deletion')
						.nth(0)
				).toBeVisible();

				await expect(assetsPage.taskStatusDropdownList).toContainText(
					'1 Items'
				);
				await expect(assetsPage.taskStatusDropdownList).toContainText(
					'a few seconds ago'
				);
				await expect(assetsPage.taskStatusDropdownList).toContainText(
					'Completed'
				);

				await expect(assetsPage.viewAllTasksLink).toBeVisible();
			});

			await test.step('Go to View All Task redirect to the Task Report page and check that Result column show the correct results', async () => {
				tasks =
					await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
						bulkActionTasks
					);

				await assetsPage.viewAllTasksLink.click();

				await expect(
					page.getByRole('heading', {
						exact: true,
						name: 'Task Report',
					})
				).toBeVisible();
				await expect(
					page.getByRole('cell', {
						exact: true,
						name: tasks.items[0].id,
					})
				).toBeVisible();
				await expect(
					page.getByRole('cell', {
						exact: true,
						name: 'All Successful',
					})
				).toBeVisible();
				await expect(
					page
						.getByRole('cell', {
							exact: true,
							name: 'All Successful',
						})
						.locator('.lexicon-icon-check-circle-full')
				).toBeVisible();

				await apiHelpers.objectEntry.patchObjectEntry(
					{
						executionStatus: {
							key: 'failed',
							name: 'Failed',
						},
						numberOfFailedItems: 3,
						numberOfSuccessfulItems: 3,
					},
					bulkActionTasks,
					tasks.items[0].id
				);

				await page.reload();

				await expect(
					page.getByRole('cell', {
						exact: true,
						name: '3 Successful 3 Failed',
					})
				).toBeVisible();
				await expect(
					page
						.getByRole('cell', {
							exact: true,
							name: '3 Successful 3 Failed',
						})
						.locator('.lexicon-icon-check-circle-full')
				).toBeVisible();
				await expect(
					page
						.getByRole('cell', {
							exact: true,
							name: '3 Successful 3 Failed',
						})
						.locator('.lexicon-icon-times-circle-full')
				).toBeVisible();

				await apiHelpers.objectEntry.patchObjectEntry(
					{
						executionStatus: {
							key: 'started',
							name: 'Started',
						},
					},
					bulkActionTasks,
					tasks.items[0].id
				);

				await page.reload();

				await expect(
					page.getByRole('cell', {exact: true, name: 'Processing'})
				).toBeVisible();
				await expect(
					page
						.getByRole('cell', {exact: true, name: 'Processing'})
						.locator('.lexicon-icon-time')
				).toBeVisible();

				await apiHelpers.objectEntry.patchObjectEntry(
					{
						executionStatus: {
							key: 'failed',
							name: 'Failed',
						},
						numberOfFailedItems: 3,
						numberOfSuccessfulItems: 0,
					},
					bulkActionTasks,
					tasks.items[0].id
				);

				await expect(
					page.getByRole('cell', {exact: true, name: 'All Failed'})
				).toBeVisible();
				await expect(
					page
						.getByRole('cell', {exact: true, name: 'All Failed'})
						.locator('.lexicon-icon-times-circle-full')
				).toBeVisible();

				await assetsPage.gotoAll();
			});

			await test.step('Update the task status to Started', async () => {
				await apiHelpers.objectEntry.patchObjectEntry(
					{
						executionStatus: {
							key: 'started',
							name: 'Started',
						},
					},
					bulkActionTasks,
					tasks.items[0].id
				);

				expect.poll(
					async () => {
						await expect(
							assetsPage.taskStatusFormsButton
						).toBeVisible();
					},
					{
						timeout: 5000,
					}
				);

				await assetsPage.taskStatusFormsButton.click();

				await expect(assetsPage.taskStatusDropdownList).toContainText(
					'Processing'
				);
			});

			await test.step('Select 2 assets and delete them using the Bulk Action', async () => {
				await expect(
					assetsPage
						.getItem(filesNames[0])
						.locator('input[title="Select Item"]')
				).not.toBeVisible();

				await assetsPage
					.getItem(filesNames[1])
					.locator('input[title="Select Item"]')
					.check();
				await assetsPage
					.getItem(filesNames[2])
					.locator('input[title="Select Item"]')
					.check();
				await assetsPage.execBulkItemAction('Delete');

				await waitForAlert(
					page,
					'Info:Delete action started for 2 assets.',
					{
						autoClose: true,
						type: 'info',
					}
				);

				await expect(assetsPage.processingTasksButton).toBeVisible();

				await assetsPage.processingTasksButton.click();

				await expect(assetsPage.taskStatusDropdownList).toContainText(
					'2 Items'
				);
				await expect(assetsPage.taskStatusDropdownList).toContainText(
					'a few seconds ago'
				);

				await expect(
					assetsPage
						.taskStatusDropdownItemButton('Assets Deletion')
						.nth(0)
				).toBeVisible();
				await expect(
					assetsPage
						.taskStatusDropdownItemButton('Assets Deletion')
						.nth(1)
				).toBeVisible();
			});

			await test.step('Update the status of the task to Failed', async () => {
				const processingTasks =
					await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
						bulkActionTasks,
						new URLSearchParams({
							filter: `executionStatus eq 'started'`,
						})
					);

				await apiHelpers.objectEntry.patchObjectEntry(
					{
						executionStatus: {
							key: 'failed',
							name: 'Failed',
						},
					},
					bulkActionTasks,
					processingTasks.items[0].id
				);

				expect.poll(
					async () => {
						await expect(
							assetsPage.taskStatusDropdownList
						).toContainText('Failed');
					},
					{
						timeout: 5000,
					}
				);
			});
		}
		finally {
			const tasksItems =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					bulkActionTasksItems
				);

			tasks =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					bulkActionTasks
				);

			for (let i = 0; i < tasksItems.totalCount; i++) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					bulkActionTasksItems,
					tasksItems.items[i].id
				);
			}
			for (let i = 0; i < tasks.totalCount; i++) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					bulkActionTasks,
					tasks.items[i].id
				);
			}

			if (createdFiles) {
				for (const file of createdFiles) {
					await apiHelpers.objectEntry.deleteObjectEntry(
						basicWebContent,
						file.id
					);
				}
			}
		}
	}
);

test(
	'Expiration date filter allows future dates',
	{tag: '@LPD-69189'},
	async ({assetsPage, page}) => {
		await assetsPage.gotoAll();

		// Choose to filter by Expiration Date

		await page.getByRole('button', {name: 'Filter'}).click();

		await page.getByRole('menuitem', {name: 'Expiration Date'}).click();

		// Verify that future dates are allowed by checking the max attribute

		const fromDateInput = page.getByLabel('From');
		const toDateInput = page.getByLabel('To', {exact: true});

		expect(
			new Date(await fromDateInput.getAttribute('max')).getTime()
		).toBeNaN();
		expect(
			new Date(await toDateInput.getAttribute('max')).getTime()
		).toBeNaN();

		// Set future From and To dates

		const fromDate = new Date();
		const toDate = new Date();

		fromDate.setDate(fromDate.getDate() + 5);
		toDate.setDate(toDate.getDate() + 10);

		// Fill in future dates and see that filter label is applied

		await fromDateInput.fill(fromDate.toISOString().split('T')[0]);
		await toDateInput.fill(toDate.toISOString().split('T')[0]);

		await page.getByRole('button', {name: 'Add Filter'}).click();

		await expect(
			page
				.getByRole('button', {name: /Expiration Date:/})
				.locator('.label-section')
		).toBeVisible();
	}
);

test(
	'FDS Table content disappears after clicking "Show Details" and then "Expire"',
	{tag: '@LPD-69267'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const file1Title = `Title ${getRandomString()}`;
		const spaceName = 'Default';
		let objectEntry;

		try {
			await test.step('Create an object and go to All section', async () => {
				objectEntry = await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						title: file1Title,
					},
					applicationName,
					spaceName
				);

				await assetsPage.gotoAll();
			});

			await test.step('Select the asset, open the Side Panel and then expire the asset', async () => {
				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: file1Title,
				});

				await expect(
					page.getByRole('heading', {name: file1Title})
				).toBeVisible();

				await page.getByLabel('Close the side panel.').click();

				await assetsPage.execItemAction({
					action: 'Expire',
					filter: file1Title,
				});

				await waitForAlert(page);
			});

			await test.step('Expect that FDS table content is visible', async () => {
				await expect(
					assetsPage
						.getItem(file1Title)
						.getByRole('cell', {name: 'expired'})
				).toBeVisible();

				await expect(
					assetsPage.dataSetFragmentPage.assetLink(file1Title)
				).toBeVisible();
			});
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry.id)
			);
		}
	}
);

test(
	'Edit Categories in bulk',
	{tag: '@LPD-57835'},
	async ({apiHelpers, assetsPage, infoPanelPage, page}) => {
		const vocabularyName = getRandomString();

		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const vocabularyId = await apiHelpers.headlessAdminTaxonomy
			.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1}],
				assetTypes: [
					{
						required: false,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name: vocabularyName,
				siteId,
				visibilityType: 'PUBLIC',
			})
			.then((response) => response.id);

		const categoryName = getRandomString();

		await apiHelpers.headlessAdminTaxonomy
			.postTaxonomyVocabularyTaxonomyCategory({
				name: categoryName,
				vocabularyId,
			})
			.then((response) => response.id);

		const basicWebContent = 'cms/basic-web-contents';
		const bulkActionTasks = 'cms/bulk-action-tasks';
		const bulkActionTasksItems = 'cms/bulk-action-task-items';

		const createdFiles = [];

		const fileNames = [
			getRandomString(),
			getRandomString(),
			getRandomString(),
		];

		let tasks;

		for (const fileName of fileNames) {
			const file = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: fileName,
				},
				basicWebContent,
				'Default'
			);

			createdFiles.push(file);
		}
		try {
			await test.step('Select 3 assets and bulk edit their categories', async () => {
				await assetsPage.gotoAll();

				await expect(assetsPage.taskStatusFormsButton).toBeHidden();

				await assetsPage
					.getItem(fileNames[0])
					.locator('input[title="Select Item"]')
					.check();
				await assetsPage
					.getItem(fileNames[1])
					.locator('input[title="Select Item"]')
					.check();
				await assetsPage
					.getItem(fileNames[2])
					.locator('input[title="Select Item"]')
					.check();

				await assetsPage.execBulkItemAction('Edit Categories');

				await waitForModal({
					page,
				});
			});

			await test.step('Add a new category to the selected assets', async () => {
				const categoriesAutocomplete =
					page.getByPlaceholder('Add category');

				await categoriesAutocomplete.fill(categoryName);

				const option = page.getByRole('option', {name: categoryName});

				await option.waitFor();
				await option.click();

				const categoryLabel = page.locator('.label-item', {
					hasText: categoryName,
				});

				await expect(categoryLabel).toBeAttached();

				await page.getByRole('button', {name: 'Save'}).click();

				await waitForAlert(
					page,
					'Info:Categories update action started for 3 assets.',
					{
						autoClose: true,
						type: 'info',
					}
				);
			});

			await test.step('Check that the "Processing Task" button appears and click on it', async () => {
				await expect(assetsPage.processingTasksButton).toBeVisible();

				await assetsPage.processingTasksButton.click();

				expect.poll(
					async () => {
						await expect(
							assetsPage.processingTasksButton
						).toBeHidden();
					},
					{
						timeout: 5000,
					}
				);

				await expect(assetsPage.taskStatusDropdownList).toContainText(
					'Completed'
				);
			});

			await test.step('After the click, the dropdown component is shown and 1 task with details is visible', async () => {
				await expect(
					assetsPage
						.taskStatusDropdownItemButton('Assets Categorization')
						.nth(0)
				).toBeVisible();

				await expect(assetsPage.taskStatusDropdownList).toContainText(
					'3 Items'
				);
			});

			await test.step('Verify the category has been correctly applied.', async () => {
				await page.reload();

				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: fileNames[0],
				});

				await expect(
					page.getByRole('heading', {name: fileNames[0]})
				).toBeVisible();

				await infoPanelPage.selectTab('Categorization').click();

				await expect(
					page.getByText(categoryName, {exact: true})
				).toBeVisible();
			});
		}
		finally {
			const tasksItems =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					bulkActionTasksItems
				);

			tasks =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					bulkActionTasks
				);

			for (let i = 0; i < tasksItems.totalCount; i++) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					bulkActionTasksItems,
					tasksItems.items[i].id
				);
			}
			for (let i = 0; i < tasks.totalCount; i++) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					bulkActionTasks,
					tasks.items[i].id
				);
			}

			if (createdFiles.length) {
				for (const file of createdFiles) {
					await apiHelpers.objectEntry.deleteObjectEntry(
						basicWebContent,
						file.id
					);
				}
			}

			await apiHelpers.headlessAdminTaxonomy.deleteTaxonomyVocabulary(
				vocabularyId
			);
		}
	}
);

test(
	'Tags with the same name can be created',
	{tag: '@LPD-69204'},
	async ({apiHelpers, assetsPage, infoPanelPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const contentTitle = `title ${getRandomString()}`;
		let objectEntry: ObjectEntry;
		const tagNameBase = getRandomString().substring(0, 7);
		const tagName1 = `A${tagNameBase}`;
		const tagName2 = `a${tagNameBase}`;

		try {
			objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentTitle,
				},
				applicationName,
				'Default'
			);

			await assetsPage.gotoAll();

			await assetsPage.execItemAction({
				action: 'Show Details',
				filter: contentTitle,
			});

			await expect(
				page.getByRole('heading', {name: contentTitle})
			).toBeVisible();

			await infoPanelPage.selectTab('Categorization').click();

			await page.getByPlaceholder('Add tag').fill(tagName1);

			const newTagOption = page.getByRole('option', {
				name: 'Create New Tag:',
			});

			await newTagOption.waitFor();
			await newTagOption.click();

			await expect(page.getByText(tagName1, {exact: true})).toBeVisible();

			await expect(async () => {
				await page.getByPlaceholder('Add tag').fill(tagName2);

				await newTagOption.waitFor();
				await newTagOption.click();

				await expect(
					page.getByText(tagName2, {exact: true})
				).toBeVisible();
			}).toPass({timeout: 5000});
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry.id)
			);
		}
	}
);

test(
	'Info Panel Versions actions',
	{tag: '@LPD-62554'},
	async ({apiHelpers, assetsPage, contentsPage, infoPanelPage, page}) => {
		const contentApplicationName = 'cms/basic-web-contents';
		const fileApplicationName = 'cms/basic-documents';
		let objectEntryContent;
		let objectEntryFile;
		const spaceName = 'Default';

		const content1 = `title ${getRandomString()}`;
		const fileNameImg = `file_${getRandomString()}.png`;
		const image1 = `title ${getRandomString()}`;

		try {
			objectEntryContent = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: content1,
				},
				contentApplicationName,
				spaceName
			);

			objectEntryFile = await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: 'R0lGODlhAQABAAAAACw=',
						name: fileNameImg,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: image1,
				},
				fileApplicationName,
				'Default'
			);

			await test.step('Go to All Assets and update all the assets', async () => {
				await assetsPage.gotoAll();
				await assetsPage.execItemAction({
					action: 'Edit',
					filter: content1,
				});

				await contentsPage.publishButton.click();
				await assetsPage.execItemAction({
					action: 'Edit',
					filter: image1,
				});

				await contentsPage.publishButton.click();
			});

			await test.step('Open the Info Panel Versions of a content asset and check that the versions actions are visible', async () => {
				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: content1,
				});

				await expect(
					page.getByRole('heading', {name: content1})
				).toBeVisible();

				await infoPanelPage.selectTab('More').click();
				await infoPanelPage.dropdownTab('Versions').click();

				await expect(page.getByRole('tabpanel')).toContainText(
					'Version 1'
				);
				await expect(page.getByRole('tabpanel')).toContainText(
					'Version 2'
				);

				await infoPanelPage.dropdownVersionAction('Version 2').click();

				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Expire')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Make a Copy')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('View')
				).toBeVisible();

				await infoPanelPage.dropdownVersionAction('Version 2').click();

				await infoPanelPage.dropdownVersionAction('Version 1').click();

				await expect(
					infoPanelPage.dropdownVersionActionMenuItem(
						'Restore Version'
					)
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Expire')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Make a Copy')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Delete')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('View')
				).toBeVisible();
			});

			await test.step('Click on the asset content version and check the functionality of the actions', async () => {
				await infoPanelPage
					.dropdownVersionActionMenuItem('View')
					.click();

				await expect(
					page.getByRole('heading', {name: `${content1} (Version 1)`})
				).toBeVisible();

				await page.getByLabel('Close', {exact: true}).click();

				await infoPanelPage.dropdownVersionAction('Version 1').click();
				await infoPanelPage
					.dropdownVersionActionMenuItem('Make a Copy')
					.click();

				await page.reload();

				await expect(
					assetsPage
						.getItem(`${content1} (Copy)`)
						.locator('input[title="Select Item"]')
				).toBeVisible();
				await assetsPage.execItemAction({
					action: 'Delete',
					filter: `${content1} (Copy)`,
				});

				await waitForAlert(
					page,
					`${content1} (Copy) was moved to the Recycle Bin.`
				);

				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: `${content1}`,
				});

				await infoPanelPage.selectTab('More').click();
				await infoPanelPage.dropdownTab('Versions').click();
				await infoPanelPage.dropdownVersionAction('Version 1').click();
				await infoPanelPage
					.dropdownVersionActionMenuItem('Restore Version')
					.click();

				await expect(page.getByRole('tabpanel')).toContainText(
					'Version 3'
				);

				await infoPanelPage.dropdownVersionAction('Version 1').click();
				await infoPanelPage
					.dropdownVersionActionMenuItem('Expire')
					.click();

				await waitForAlert(
					page,
					'Version 1 of the content has been successfully expired.'
				);

				await expect(page.getByRole('tabpanel')).toContainText(
					'expired'
				);

				await infoPanelPage.dropdownVersionAction('Version 1').click();

				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Delete')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('View')
				).toBeVisible();

				await infoPanelPage
					.dropdownVersionActionMenuItem('Delete')
					.click();

				await assetsPage.modalDeleteButton.click();

				await waitForAlert(
					page,
					'Version 1 of the content has been successfully deleted.'
				);

				await expect(
					infoPanelPage.dropdownVersionAction('Version 1')
				).not.toBeVisible();

				await assetsPage
					.getItem(content1)
					.locator('input[title="Select Item"]')
					.uncheck();
			});

			await test.step('Open the Info Panel Versions of a file asset and check that the versions actions are visible', async () => {
				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: image1,
				});

				await expect(
					page.getByRole('heading', {name: image1})
				).toBeVisible();

				await infoPanelPage.selectTab('More').click();
				await infoPanelPage.dropdownTab('Versions').click();

				await expect(page.getByRole('tabpanel')).toContainText(
					'Version 1'
				);
				await expect(page.getByRole('tabpanel')).toContainText(
					'Version 2'
				);

				await infoPanelPage.dropdownVersionAction('Version 2').click();

				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Expire')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Make a Copy')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Download')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('View')
				).toBeVisible();

				await infoPanelPage.dropdownVersionAction('Version 2').click();

				await infoPanelPage.dropdownVersionAction('Version 1').click();

				await expect(
					infoPanelPage.dropdownVersionActionMenuItem(
						'Restore Version'
					)
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Expire')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Make a Copy')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Download')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Delete')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('View')
				).toBeVisible();
			});

			await test.step('Click on the version and check the functionality of the actions', async () => {
				await infoPanelPage
					.dropdownVersionActionMenuItem('View')
					.click();

				await expect(
					page.getByRole('heading', {name: `${image1} (Version 1)`})
				).toBeVisible();

				await page
					.getByLabel(`${image1} (Version 1)`)
					.getByLabel('Close')
					.click();

				await infoPanelPage.dropdownVersionAction('Version 1').click();

				const downloadPromise = page.waitForEvent('download');

				await infoPanelPage
					.dropdownVersionActionMenuItem('Download')
					.click();

				const download = await downloadPromise;
				expect(download.suggestedFilename()).toBe(`${fileNameImg}`);

				const downloadStat = await fs.stat(await download.path());
				expect(downloadStat.size).toBeGreaterThan(10);

				await infoPanelPage.dropdownVersionAction('Version 1').click();
				await infoPanelPage
					.dropdownVersionActionMenuItem('Make a Copy')
					.click();

				await page.reload();

				await expect(
					assetsPage
						.getItem(`${image1} (Copy)`)
						.locator('input[title="Select Item"]')
				).toBeVisible();

				await assetsPage.execItemAction({
					action: 'Delete',
					filter: `${image1} (Copy)`,
				});

				await waitForAlert(
					page,
					`${image1} (Copy) was moved to the Recycle Bin.`
				);

				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: `${image1}`,
				});

				await infoPanelPage.selectTab('More').click();
				await infoPanelPage.dropdownTab('Versions').click();
				await infoPanelPage.dropdownVersionAction('Version 1').click();
				await infoPanelPage
					.dropdownVersionActionMenuItem('Restore Version')
					.click();

				await expect(page.getByRole('tabpanel')).toContainText(
					'Version 3'
				);

				await infoPanelPage.dropdownVersionAction('Version 1').click();
				await infoPanelPage
					.dropdownVersionActionMenuItem('Expire')
					.click();

				await waitForAlert(
					page,
					'Version 1 of the content has been successfully expired.'
				);

				await expect(page.getByRole('tabpanel')).toContainText(
					'expired'
				);

				await infoPanelPage.dropdownVersionAction('Version 1').click();

				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('Delete')
				).toBeVisible();
				await expect(
					infoPanelPage.dropdownVersionActionMenuItem('View')
				).toBeVisible();

				await infoPanelPage
					.dropdownVersionActionMenuItem('Delete')
					.click();

				await assetsPage.modalDeleteButton.click();

				await waitForAlert(
					page,
					'Version 1 of the content has been successfully deleted.'
				);

				await expect(
					infoPanelPage.dropdownVersionAction('Version 1')
				).not.toBeVisible();
			});
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				contentApplicationName,
				String(objectEntryContent.id)
			);
			await apiHelpers.objectEntry.deleteObjectEntry(
				fileApplicationName,
				String(objectEntryFile.id)
			);
		}
	}
);

test(
	'Info panel shows title with content structure',
	{tag: '@LPD-69788'},
	async ({assetsPage, contentsPage, page, structureBuilderPage}) => {
		const structureLabel = `StructureName${getRandomInt()}`;
		const title = getRandomString();

		await test.step('Create a content structure', async () => {
			await structureBuilderPage.createStructureFromData({
				label: structureLabel,
				page: structureBuilderPage,
			});
		});

		await test.step('Navigate to All Assets and create a new content', async () => {
			await assetsPage.gotoAll();

			await assetsPage.createContent(structureLabel);

			await expect(
				page.getByRole('heading', {name: `Edit ${structureLabel}`})
			).toBeVisible();

			await page.getByPlaceholder(`New ${structureLabel}`).fill(title);

			await contentsPage.saveContent();
		});

		await test.step('Open Info Panel and assert that title is not empty', async () => {
			await assetsPage.execItemAction({
				action: 'Show Details',
				filter: structureLabel,
			});

			await expect(
				page.getByRole('heading', {name: title})
			).toBeVisible();
		});
	}
);

test(
	'Bulk action download assets',
	{tag: '@LPD-62554'},
	async ({apiHelpers, assetsPage, page}) => {
		const contentApplicationName = 'cms/basic-web-contents';
		const fileApplicationName = 'cms/basic-documents';
		const spaceName = 'Default';

		const content1 = `title ${getRandomString()}`;
		const fileAssetTitle1 = `title ${getRandomString()}`;
		const fileAssetTitle2 = `title ${getRandomString()}`;
		const fileNameImg = `file_${getRandomString()}.png`;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: content1,
			},
			contentApplicationName,
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'R0lGODlhAQABAAAAACw=',
					name: fileNameImg,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileAssetTitle1,
			},
			fileApplicationName,
			'Default'
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'R0lGODlhAQABAAAAACw=',
					name: fileNameImg,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileAssetTitle2,
			},
			fileApplicationName,
			'Default'
		);

		await test.step('Go to All section and try to download a content asset from the bulk action, un unexpected error occurred', async () => {
			await assetsPage.gotoAll();
			await assetsPage.selectItems([content1]);
			await assetsPage.execBulkItemAction('Download');

			await waitForAlert(
				page,
				'Error:Unable to process the bulk download. Please check your selection and try again.',
				{
					type: 'danger',
				}
			);
		});

		await test.step('Download a file asset from the bulk action', async () => {
			await assetsPage
				.getItem(content1)
				.locator('input[title="Select Item"]')
				.uncheck();
			await assetsPage.selectItems([fileAssetTitle1]);

			const downloadPromise = page.waitForEvent('download');

			await assetsPage.execBulkItemAction('Download');

			await waitForAlert(
				page,
				'Warning:The download of 1 file is being prepared. Please do not close this window or navigate to another section.',
				{
					type: 'warning',
				}
			);

			await waitForAlert(page, 'Success:The download will begin shortly');

			const download = await downloadPromise;

			expect(download.suggestedFilename()).toBeDefined();
		});

		await test.step('Download both content and files assets from the bulk action, a message will inform the user that content assets will be skipped from the download', async () => {
			await assetsPage.selectItems([
				content1,
				fileAssetTitle1,
				fileAssetTitle2,
			]);

			const downloadPromise = page.waitForEvent('download');

			await assetsPage.execBulkItemAction('Download');

			await waitForAlert(
				page,
				'Warning:You have selected both content and file assets. Only file assets can be downloaded. Content assets will be skipped.',
				{
					type: 'warning',
				}
			);
			await waitForAlert(
				page,
				'Warning:The download of 2 files is being prepared. Please do not close this window or navigate to another section.',
				{
					type: 'warning',
				}
			);
			await waitForAlert(page, 'Success:The download will begin shortly');

			const download = await downloadPromise;

			expect(download.suggestedFilename()).toBeDefined();
		});
	}
);
