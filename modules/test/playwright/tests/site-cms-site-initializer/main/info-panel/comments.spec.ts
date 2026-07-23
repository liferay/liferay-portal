/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
	}),
	loginTest()
);

test(
	'Info Panel Comments and view Delete confirmation modal for added content',
	{tag: ['@LPD-62554', '@LPD-86000', '@LPD-93071']},
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

				await infoPanelPage.selectTab('Comments').click();
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

			if (objectEntry1) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					applicationName,
					String(objectEntry1.id)
				);
			}
		}
	}
);
