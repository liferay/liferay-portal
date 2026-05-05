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
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {structureBuilderPagesTest} from '../structure-builder/fixtures/structureBuilderPagesTest';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPD-17564': {enabled: true},
		'LPD-34594': {enabled: true},
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
		const file1Title = `<b>Content ${getRandomString()}</b>`;
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
				parentAction: 'Copy',
			});
		});

		await test.step('Check content folders', async () => {
			await page.getByLabel(spaceName).click();
			await expect(
				page.getByText('Showing 1 to 1 of 1 entries.')
			).toBeVisible();

			await expect(
				page.getByLabel('Contents', {exact: true})
			).toBeVisible();
		});

		await test.step('Copy file', async () => {
			await assetsPage.gotoAll();

			await assetsPage.execItemAction({
				action: 'Copy To',
				filter: file2Title,
				parentAction: 'Copy',
			});
		});

		await test.step('Check file folders', async () => {
			await page.getByLabel(spaceName).click();
			await expect(
				page.getByText('Showing 1 to 1 of 1 entries.')
			).toBeVisible();

			await expect(page.getByLabel('Files', {exact: true})).toBeVisible();
		});
	}
);

test(
	'Duplicating content creates a draft copy in the same Space',
	{tag: '@LPD-88346'},
	async ({apiHelpers, assetsPage, page}) => {
		const fileTitle = `Content ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;

		await test.step('Create a new Space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});
		});

		await test.step('Create a content for that Space', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: fileTitle,
				},
				'cms/basic-web-contents',
				spaceName
			);
		});

		await test.step('Duplicate content', async () => {
			await assetsPage.gotoSpaceContents(spaceName);

			await assetsPage.execItemAction({
				action: 'Duplicate',
				filter: fileTitle,
				parentAction: 'Copy',
			});

			await expect(
				page.getByRole('link', {
					exact: true,
					name: `${fileTitle} (Copy)`,
				})
			).toBeVisible();

			await expect(
				assetsPage.table.bodyRows
					.filter({
						has: page.getByRole('link', {
							exact: true,
							name: `${fileTitle} (Copy)`,
						}),
					})
					.getByText('Draft')
			).toBeVisible();
		});

		await test.step('Duplicate the original again and check the suffix increments', async () => {
			await assetsPage.execItemAction({
				action: 'Duplicate',
				filter: fileTitle,
				parentAction: 'Copy',
			});

			await expect(
				page.getByRole('link', {
					exact: true,
					name: `${fileTitle} (Copy 1)`,
				})
			).toBeVisible();
		});
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
	{tag: ['@LPD-62554', '@LPD-86000']},
	async ({apiHelpers, assetsPage, infoPanelPage, page, spaceSummaryPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = `Space ${getRandomString()}`;
		let objectEntry1;
		let user;

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

			await test.step('Login as Space Member, go to All Assets, check the Details tab and open the Info Panel Comments', async () => {
				await performLogout(page);
				await performLoginViaApi({
					page,
					screenName: user.alternateName,
				});

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

				await infoPanelPage.dropdownTab('Comments').click();
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
			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

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

			if (objectEntry?.id) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					applicationName,
					String(objectEntry.id)
				);
			}

			await apiHelpers.headlessAdminTaxonomy.deleteTaxonomyVocabulary(
				vocabularyId
			);
		}
	}
);

test(
	'Info Panel Categories tab for file type asset',
	{tag: '@LPD-68491'},
	async ({apiHelpers, assetsPage, infoPanelPage, page}) => {
		const categoryName = getRandomString();
		const fileTitle = `title ${getRandomString()}`;
		const tagName = getRandomString();

		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'R0lGODlhAQABAAAAACw=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			'cms/basic-documents',
			'Default'
		);

		apiHelpers.data.push({
			id: objectEntry.id,
			type: 'document',
		});

		const vocabularyId = await apiHelpers.headlessAdminTaxonomy
			.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1, name: 'All Spaces'}],
				assetTypes: [
					{
						required: false,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name: getRandomString(),
				siteId,
				visibilityType: 'PUBLIC',
			})
			.then((response) => response.id);

		apiHelpers.data.push({
			id: vocabularyId,
			type: 'taxonomyVocabulary',
		});

		await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
			{
				name: categoryName,
				vocabularyId,
			}
		);

		// Go to All Assets and open the Info Panel Categorization Tab

		await assetsPage.gotoAll();

		await assetsPage.execItemAction({
			action: 'Show Details',
			filter: fileTitle,
		});

		await expect(
			page.getByRole('heading', {name: fileTitle})
		).toBeVisible();

		await infoPanelPage.selectTab('Categorization').click();

		// Add a new tag to the file

		const tagsAutocomplete = page.getByPlaceholder('Add tag');

		await tagsAutocomplete.fill(tagName);

		const newTagOption = page.getByRole('option', {
			name: 'Create New Tag:',
		});

		await newTagOption.waitFor();
		await newTagOption.click();

		const tagLabel = page.locator('.label-item', {hasText: tagName});

		await expect(tagLabel).toBeAttached();

		// Add a new category to the file

		const categoriesAutocomplete = page.getByPlaceholder('Add category');

		await categoriesAutocomplete.fill(categoryName);

		const option = page.getByRole('option', {name: categoryName});

		await option.waitFor();
		await option.click();

		const categoryLabel = page.locator('.label-item', {
			hasText: categoryName,
		});

		await expect(categoryLabel).toBeAttached();
	}
);

test(
	'Info Panel Categories tab generates a new Asset Version',
	{tag: '@LPD-83267'},
	async ({apiHelpers, assetsPage, infoPanelPage, page}) => {
		const applicationName = 'cms/basic-documents';
		let categoryLabel;
		const categoryName = `category ${getRandomString()}`;
		const file1Title = `title ${getRandomString()}`;
		let objectEntry;
		const spaceName = `Space ${getRandomString()}`;
		let spaceExternalReferenceCode: string;
		const vocabularyName = `vocabulary ${getRandomString()}`;

		await test.step('Create a new Space', async () => {
			const space =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: spaceName,
					settings: {},
					type: 'Space',
				});
			spaceExternalReferenceCode = space.externalReferenceCode;
		});

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

		await apiHelpers.headlessAdminTaxonomy
			.postTaxonomyVocabularyTaxonomyCategory({
				name: categoryName,
				vocabularyId,
			})
			.then((response) => response.id);

		try {
			objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: 'R0lGODlhAQABAAAAACw=',
						name: file1Title,
					},
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

			await test.step('Add a new category to the file', async () => {
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

			await test.step('Validate new version is generated', async () => {
				await assetsPage.execItemAction({
					action: 'View History',
					filter: file1Title,
				});

				await expect(
					page.getByRole('heading', {name: `"${file1Title}" History`})
				).toBeVisible();

				await page
					.getByRole('button', {exact: true, name: file1Title})
					.first()
					.click();

				await expect(
					page.getByRole('heading', {
						name: `${file1Title} (Version 2)`,
					})
				).toBeVisible();
			});
		}
		finally {
			if (objectEntry?.id) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					applicationName,
					String(objectEntry.id)
				);
			}

			await apiHelpers.headlessAdminTaxonomy.deleteTaxonomyVocabulary(
				vocabularyId
			);

			if (spaceExternalReferenceCode) {
				await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(
					spaceExternalReferenceCode
				);
			}
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
	'Expiration date filter allows future dates',
	{tag: '@LPD-69189'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const file1Title = `Content ${getRandomString()}`;

		const futureDate = new Date();

		futureDate.setDate(futureDate.getDate() + 1);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				expirationDate: futureDate.toISOString(),
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file1Title,
			},
			applicationName,
			'Default'
		);

		await assetsPage.gotoAll();

		await expect(
			page.getByRole('cell', {exact: true, name: file1Title})
		).toBeVisible();

		// Choose to filter by Expiration Date

		await page.getByRole('button', {name: 'Filter'}).click();

		await page.getByRole('menuitem', {name: 'Expiration Date'}).click();

		const fromDateInput = page.getByLabel('From');
		const toDateInput = page.getByLabel('To', {exact: true});

		// Set future From and To dates covering futureDate

		const fromDate = new Date();
		const toDate = new Date();

		toDate.setDate(toDate.getDate() + 2);

		// Fill in future dates and see that filter label is applied

		await fromDateInput.fill(fromDate.toISOString().split('T')[0]);
		await toDateInput.fill(toDate.toISOString().split('T')[0]);

		await page.getByRole('button', {name: 'Add Filter'}).click();

		await expect(
			page
				.getByRole('button', {name: /Expiration Date:/})
				.locator('.label-section')
		).toBeVisible();

		// Verify that the content is still visible (it was filtered out before the fix)

		await expect(
			page.getByRole('cell', {exact: true, name: file1Title})
		).toBeVisible();
	}
);

test(
	'Content can be filtered by Review Date',
	{tag: '@LPD-85206'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const file1Title = `Content ${getRandomString()}`;

		const pastDate = new Date();

		pastDate.setDate(pastDate.getDate() - 1);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				reviewDate: pastDate.toISOString(),
				title: file1Title,
			},
			applicationName,
			'Default'
		);

		await assetsPage.gotoAll();

		await expect(
			page.getByRole('cell', {exact: true, name: file1Title})
		).toBeVisible();

		// Choose to filter by Review Date

		await page.getByRole('button', {name: 'Filter'}).click();

		await page.getByRole('menuitem', {name: 'Review Date'}).click();

		const fromDateInput = page.getByLabel('From');
		const toDateInput = page.getByLabel('To', {exact: true});

		// Set past From and To dates covering pastDate

		const fromDate = new Date();
		const toDate = new Date();

		fromDate.setDate(fromDate.getDate() - 2);

		// Fill in dates and see that filter label is applied

		await fromDateInput.fill(fromDate.toISOString().split('T')[0]);
		await toDateInput.fill(toDate.toISOString().split('T')[0]);

		await page.getByRole('button', {name: 'Add Filter'}).click();

		await expect(
			page
				.getByRole('button', {name: /Review Date:/})
				.locator('.label-section')
		).toBeVisible();

		// Verify that the content is visible

		await expect(
			page.getByRole('cell', {exact: true, name: file1Title})
		).toBeVisible();
	}
);

test(
	'Expiration date filter does not allow "to" date to be before "from" date',
	{tag: '@LPD-78935'},
	async ({assetsPage, page}) => {
		const addFilterButton = page.getByRole('button', {name: 'Add Filter'});

		await test.step('Go to All section', async () => {
			await assetsPage.gotoAll();
		});

		await test.step('Choose to filter by Expiration Date', async () => {
			await page.getByRole('button', {name: 'Filter'}).click();

			await page.getByRole('menuitem', {name: 'Expiration Date'}).click();
		});

		const fromDateInput = page.getByLabel('From');
		const toDateInput = page.getByLabel('To', {exact: true});

		const fromDate = new Date();
		const toDate = new Date();

		await test.step('Check that the "Add filter" button is disabled if "from" date is a past date', async () => {
			fromDate.setDate(fromDate.getDate() - 1);
			await fromDateInput.fill(fromDate.toISOString().split('T')[0]);
			await expect(addFilterButton).toBeDisabled();
		});

		await test.step('Check that the "Add filter" button is enabled if "from" date is today or after', async () => {
			fromDate.setDate(fromDate.getDate() + 2);
			await fromDateInput.fill(fromDate.toISOString().split('T')[0]);
			await expect(addFilterButton).toBeEnabled();
		});

		await test.step('Check that the "Add filter" button is disabled if "to" date is before "from date"', async () => {
			toDate.setDate(toDate.getDate());
			await toDateInput.fill(toDate.toISOString().split('T')[0]);
			await expect(addFilterButton).toBeDisabled();
		});

		await test.step('Check that the "Add filter" button is enabled if "to" date is after "from date"', async () => {
			toDate.setDate(toDate.getDate() + 5);
			await toDateInput.fill(toDate.toISOString().split('T')[0]);
			await expect(addFilterButton).toBeEnabled();
		});
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

				await expect(
					assetsPage
						.getItem(`${content1} (Copy)`)
						.locator('input[title="Select Item"]')
				).toBeVisible();

				await page.reload();

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
	{tag: ['@LPD-69788', '@LPD-76513']},
	async ({
		assetsPage,
		contentsPage,
		infoPanelPage,
		page,
		structureBuilderPage,
	}) => {
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

		await test.step('Assert that all tabs are visible', async () => {
			await expect(infoPanelPage.selectTab('Performance')).toBeVisible();

			await expect(infoPanelPage.selectTab('More')).toBeVisible();

			await infoPanelPage.selectTab('Categorization').click();

			await expect(page.getByPlaceholder('Add tag')).toBeVisible();
			await expect(page.getByPlaceholder('Add category')).toBeVisible();
		});
	}
);

test(
	'Versions tab should not be visible for Space Member role',
	{tag: '@LPD-86002'},
	async ({apiHelpers, assetsPage, infoPanelPage, page, spaceSummaryPage}) => {
		const contentApplicationName = 'cms/basic-web-contents';
		let objectEntryContent;
		const spaceName = 'Default';
		let user;

		const content = `title ${getRandomString()}`;

		try {
			objectEntryContent = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: content,
				},
				contentApplicationName,
				spaceName
			);

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

			await test.step('Login as a space member and open Info Panel', async () => {
				await performLogout(page);

				await performLogin(page, user.alternateName);

				await assetsPage.gotoAll();
				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: content,
				});

				await expect(
					page.getByRole('heading', {name: content})
				).toBeVisible();
			});

			await test.step('Check versions tab is not visible', async () => {
				await expect(infoPanelPage.selectTab('More')).not.toBeVisible();
				await expect(
					infoPanelPage.selectTab('Versions')
				).not.toBeVisible();
				await expect(infoPanelPage.selectTab('Comments')).toBeVisible();
			});
		}
		finally {
			await performLogout(page);

			await performLogin(page, 'test');

			if (objectEntryContent) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					contentApplicationName,
					String(objectEntryContent.id)
				);
			}
		}
	}
);

test(
	'All section places most recently modified content at the top',
	{tag: '@LPD-85725'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = 'Default';
		const thirdTitle = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: getRandomString(),
			},
			applicationName,
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: getRandomString(),
			},
			applicationName,
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: thirdTitle,
			},
			applicationName,
			spaceName
		);

		await expect(async () => {
			await assetsPage.gotoAll();

			await expect(page.locator('tbody tr').first()).toContainText(
				thirdTitle
			);
		}).toPass();
	}
);
