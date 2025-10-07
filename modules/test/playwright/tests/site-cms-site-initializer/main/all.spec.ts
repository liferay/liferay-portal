/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	loginTest()
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
			.getByTestId('visualization-mode-table')
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
			.getByTestId('visualization-mode-table')
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

			await test.step('Go to All Assets and open the Info Panel Comments', async () => {
				await assetsPage.gotoAll();

				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: file1Title,
				});

				await expect(
					page.getByRole('heading', {name: file1Title})
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
	'Task Status Manager component',
	{tag: '@LPD-57835'},
	async ({apiHelpers, assetsPage, page}) => {
		const basicWebContent = 'cms/basic-web-contents';
		const bulkActionTasks = 'cms/bulk-action-tasks';
		const bulkActionTasksItems = 'cms/bulk-action-task-items';
		const spaceName = 'Default';

		const filesNames = [
			getRandomString(),
			getRandomString(),
			getRandomString(),
		];

		for (const fileName of filesNames) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: fileName,
				},
				basicWebContent,
				spaceName
			);
		}
		try {
			await test.step('Select 1 asset and delete it using the Bulk Action', async () => {
				await assetsPage.gotoAll();

				await expect(assetsPage.taskStatusFormsButton).toBeDisabled();

				await assetsPage
					.getItem(filesNames[0])
					.locator('input[title="Select Item"]')
					.check();
				await assetsPage.execBulkItemAction('Delete');

				await expect(assetsPage.modal.title).toContainText(
					'Delete Entry'
				);

				await assetsPage.modalDeleteButton.click();

				await waitForAlert(
					page,
					'Info:Asset delete action started for 1 asset.' +
						' Check the Task Report for details.',
					{
						autoClose: true,
						type: 'info',
					}
				);
			});

			await test.step('Check that the processingTask button Appear and click on it', async () => {
				await expect(assetsPage.processingTasksButton).toBeVisible();

				await assetsPage.processingTasksButton.click();
			});

			await test.step('After the click, the dropdown component is shown and 1 task with details is visible', async () => {
				await expect(
					assetsPage
						.taskStatusDropdownItemButton('Asset Deletion')
						.nth(0)
				).toBeVisible();
				await expect(assetsPage.taskStatusDropdownList).toContainText(
					'1 Items'
				);
				await expect(assetsPage.taskStatusDropdownList).toContainText(
					'a few seconds ago'
				);
				await expect(assetsPage.taskStatusDropdownList).toContainText(
					'processing'
				);

				await assetsPage
					.taskStatusDropdownItemButton('Asset Deletion')
					.click();

				await expect(assetsPage.taskStatusButton('View')).toBeVisible();
				await expect(assetsPage.viewAllTasksLink).toBeVisible();

				await assetsPage.processingTasksButton.click();
			});

			// This test step will be removed once the API flow will be completed

			await test.step('Update the task status to Completed', async () => {
				const tasks =
					await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
						bulkActionTasks
					);

				await apiHelpers.objectEntry.patchObjectEntry(
					{
						executionStatus: 'COMPLETED',
					},
					bulkActionTasks,
					tasks.items[0].id
				);

				expect.poll(
					async () => {
						await expect(
							assetsPage.taskStatusFormsButton
						).toBeEnabled();
					},
					{
						timeout: 10000,
					}
				);

				await assetsPage.taskStatusFormsButton.click();

				await expect(assetsPage.taskStatusDropdownList).toContainText(
					'completed'
				);
			});

			await test.step('Delete the assets using the selectAll', async () => {
				await page.reload();

				await assetsPage
					.getItem(filesNames[1])
					.locator('input[title="Select Item"]')
					.check();
				await assetsPage
					.getItem(filesNames[2])
					.locator('input[title="Select Item"]')
					.check();
				await assetsPage.dataSetFragmentPage.selectAllLink.click();
				await assetsPage.execBulkItemAction('Delete');

				await expect(assetsPage.modal.title).toContainText(
					'Delete All Entries'
				);

				await assetsPage.modalDeleteButton.click();

				await waitForAlert(
					page,
					'Info:Asset delete action started for 2 assets.' +
						' Check the Task Report for details.',
					{
						autoClose: true,
						type: 'info',
					}
				);
			});

			await test.step('Check that the processingTask button Appear, click on it and check that there are 2 task', async () => {
				await expect(assetsPage.processingTasksButton).toBeVisible();

				await assetsPage.processingTasksButton.click();

				await expect(
					assetsPage
						.taskStatusDropdownItemButton('Asset Deletion')
						.nth(0)
				).toBeVisible();
				await expect(
					assetsPage
						.taskStatusDropdownItemButton('Asset Deletion')
						.nth(1)
				).toBeVisible();
			});

			await test.step('Check details of the selectAll asset deletion', async () => {
				await assetsPage.processingTasksButton.click();

				await expect(assetsPage.taskStatusDropdownList).toContainText(
					'2 Items'
				);
				await expect(assetsPage.taskStatusDropdownList).toContainText(
					'a few seconds ago'
				);
				await expect(assetsPage.taskStatusDropdownList).toContainText(
					'processing'
				);

				await assetsPage.processingTasksButton.click();
			});

			// This test step will be removed once the API flow will be completed

			await test.step('Update the status of the task to Failed', async () => {
				const processingTasks =
					await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
						bulkActionTasks,
						new URLSearchParams({
							filter: `executionStatus eq 'STARTED'`,
						})
					);

				await apiHelpers.objectEntry.patchObjectEntry(
					{
						executionStatus: 'FAILED',
					},
					bulkActionTasks,
					processingTasks.items[0].id
				);

				expect.poll(
					async () => {
						await expect(
							page
								.getByLabel('CMS Control Menu')
								.getByRole('button')
								.locator('svg.lexicon-icon-forms')
						).toBeVisible();
					},
					{
						timeout: 10000,
					}
				);

				await assetsPage.taskStatusFormsButton.click();

				await expect(assetsPage.taskStatusDropdownList).toContainText(
					'failed'
				);
			});
		}
		finally {
			const tasksItems =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					bulkActionTasksItems
				);

			const tasks =
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
		}
	}
);
