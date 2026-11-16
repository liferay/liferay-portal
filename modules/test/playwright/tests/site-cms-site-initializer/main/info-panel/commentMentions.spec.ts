/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {notificationsPagesTest} from '../../../../fixtures/notificationsPagesTest';
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
		'LPD-104086': {enabled: true},
	}),
	loginTest(),
	notificationsPagesTest
);

test(
	'Mention space members in an asset comment and open the comment from the notification',
	{tag: ['@LPD-113902', '@LPD-114306']},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		infoPanelPage,
		notificationsPage,
		page,
	}) => {
		const commentText = getRandomString();
		const title = getRandomString();

		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		const member1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const member2 = await apiHelpers.headlessAdminUser.postUserAccount();
		const nonMember = await apiHelpers.headlessAdminUser.postUserAccount();

		for (const member of [member1, member2]) {
			await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
				space.externalReferenceCode,
				member.externalReferenceCode
			);
		}

		userData[member2.alternateName] = {
			name: member2.givenName,
			password: 'test',
			surname: member2.familyName,
		};

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title,
			},
			'cms/basic-web-contents',
			space.name
		);

		const editor = page.getByLabel('Add Comment.').first();
		const picker = page.locator('.comments-panel__mention-user-picker');

		try {
			await test.step('Open the comments of the asset', async () => {
				await assetsPage.gotoAll();

				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: title,
				});

				await infoPanelPage.selectTab('Comments').click();

				await expect(editor).toBeVisible();
			});

			await test.step('The picker shows a message when nobody matches', async () => {
				await editor.click();

				await page.keyboard.type('@zzzzz');

				await expect(picker).toContainText('No people match "zzzzz".');

				for (let i = 0; i < 6; i++) {
					await page.keyboard.press('Backspace');
				}

				await expect(picker).toBeHidden();
			});

			await test.step('The picker lists the space members, but not the author or users outside the space', async () => {
				await page.keyboard.type(`${commentText} @`);

				await expect(
					picker.getByRole('option').filter({hasText: member1.name})
				).toBeVisible();
				await expect(
					picker.getByRole('option').filter({hasText: member2.name})
				).toBeVisible();

				await expect(picker).not.toContainText(nonMember.name);
				await expect(picker).not.toContainText('Test Test');

				await expect(
					page.locator('[aria-live="polite"]', {
						hasText: '2 Results Found',
					})
				).toBeAttached();
			});

			await test.step('Select a member with the keyboard and post the comment', async () => {
				const option = picker
					.getByRole('option')
					.filter({hasText: member2.name});

				for (let i = 0; i < 2; i++) {
					if (
						(await option.getAttribute('aria-selected')) === 'true'
					) {
						break;
					}

					await page.keyboard.press('ArrowDown');
				}

				await expect(option).toHaveAttribute('aria-selected', 'true');

				await expect(editor).toHaveAttribute(
					'aria-activedescendant',
					(await option.getAttribute('id'))!
				);
				await expect(editor).toHaveAttribute(
					'aria-controls',
					(await picker.getByRole('listbox').getAttribute('id'))!
				);

				await page.keyboard.press('Enter');

				await expect(picker).toBeHidden();

				await expect(editor).not.toHaveAttribute(
					'aria-activedescendant'
				);

				await expect(
					editor.locator(
						`span.mention.mention-user-${member2.externalReferenceCode}`
					)
				).toHaveText(`@${member2.name}`);

				await page
					.getByRole('button', {exact: true, name: 'Save'})
					.first()
					.click();

				await waitForAlert(
					page,
					'Success:Your comment has been posted.',
					{autoClose: true}
				);
			});

			const comment = page
				.locator('article')
				.filter({hasText: commentText});

			await test.step('The mention is kept when the comment is edited', async () => {
				await expect(
					comment.locator(
						`span.mention.mention-user-${member2.externalReferenceCode}`
					)
				).toHaveText(`@${member2.name}`);

				await comment.getByTitle('actions').click();

				await page.getByRole('menuitem', {name: 'Edit'}).click();

				const commentEditor = comment.getByLabel('Add Comment.');

				await expect(
					commentEditor.locator(
						`span.mention.mention-user-${member2.externalReferenceCode}`
					)
				).toHaveText(`@${member2.name}`);

				await comment.getByRole('button', {name: 'Cancel'}).click();
			});

			await test.step('The picker waits for the current search before inserting a member', async () => {
				await editor.click();

				await page.keyboard.type('@');

				await expect(
					picker.getByRole('option').filter({hasText: member1.name})
				).toBeVisible();

				await page.route(
					'**/get_content_item_mention_users**',
					async (route) => {
						await new Promise((resolve) =>
							setTimeout(resolve, 2000)
						);

						await route.continue();
					}
				);

				await page.keyboard.type('zzzzz');
				await page.keyboard.press('Enter');

				await expect(editor.locator('span.mention')).toHaveCount(0);

				await expect(picker).toContainText('No people match "zzzzz".');

				await page.unroute('**/get_content_item_mention_users**');

				for (let i = 0; i < 6; i++) {
					await page.keyboard.press('Backspace');
				}

				await expect(picker).toBeHidden();
			});

			await test.step('The picker stays closed when the members cannot be loaded', async () => {
				await page.route(
					'**/get_content_item_mention_users**',
					(route) => route.fulfill({status: 500})
				);

				const responsePromise = page.waitForResponse(
					'**/get_content_item_mention_users**'
				);

				await page.keyboard.type('@');

				await responsePromise;

				await expect(picker).toBeHidden();

				await page.unroute('**/get_content_item_mention_users**');

				await page.keyboard.press('Backspace');
			});

			await test.step('The picker is also available in the content editor', async () => {
				await contentsPage.goto();

				await contentsPage.editContent(title);

				await contentsPage.openSidePanel('Comments');

				await editor.click();

				await page.keyboard.type('@');

				await expect(
					picker.getByRole('option').filter({hasText: member2.name})
				).toBeVisible();

				await page.keyboard.press('Escape');

				await expect(picker).toBeHidden();
			});

			await test.step('The mentioned member opens the comment from the notification', async () => {
				await performLogout(page);

				await performLoginViaApi({
					page,
					screenName: member2.alternateName,
				});

				await notificationsPage.goto();

				await notificationsPage
					.getNotificationByTitle(
						`mentioned you in a comment on ${title}`
					)
					.getByText(title)
					.click();

				await expect(page).toHaveURL(/\/view-asset\?.*messageId=/);

				const mentionedComment = page.locator(
					'article[aria-current="true"]'
				);

				await expect(mentionedComment).toContainText(commentText);
				await expect(mentionedComment).toBeFocused();
			});
		}
		finally {
			await performLogout(page);

			await performLoginViaApi({page, screenName: 'test'});
		}
	}
);

test(
	'The mention picker tells there is nobody to mention in a space without other members',
	{tag: ['@LPD-114306']},
	async ({apiHelpers, assetsPage, infoPanelPage, page}) => {
		const title = getRandomString();

		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title,
			},
			'cms/basic-web-contents',
			space.name
		);

		await assetsPage.gotoAll();

		await assetsPage.execItemAction({
			action: 'Show Details',
			filter: title,
		});

		await infoPanelPage.selectTab('Comments').click();

		const editor = page.getByLabel('Add Comment.').first();

		await editor.click();

		await page.keyboard.type('@');

		const picker = page.locator('.comments-panel__mention-user-picker');

		await expect(picker).toContainText(
			'No people are available to mention.'
		);
		await expect(picker).toContainText(
			'Only people with access to this asset can be mentioned.'
		);
		await expect(picker).not.toContainText('No people match');

		await expect(
			page.locator('[aria-live="polite"]', {
				hasText: 'No people are available to mention.',
			})
		).toBeAttached();
	}
);

test(
	'Tell that the highlighted comment is no longer available when it was deleted',
	{tag: '@LPD-115057'},
	async ({apiHelpers, assetsPage, infoPanelPage, page}) => {
		const commentText = getRandomString();
		const title = getRandomString();

		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title,
			},
			'cms/basic-web-contents',
			space.name
		);

		await assetsPage.gotoAll();

		await assetsPage.execItemAction({
			action: 'Show Details',
			filter: title,
		});

		await infoPanelPage.selectTab('Comments').click();

		await page.getByLabel('Add Comment.').first().click();

		await page.keyboard.type(commentText);

		const responsePromise = page.waitForResponse(
			'**/add_content_item_comment**'
		);

		await page
			.getByRole('button', {exact: true, name: 'Save'})
			.first()
			.click();

		const {commentId} = await (await responsePromise).json();

		await waitForAlert(page, 'Success:Your comment has been posted.', {
			autoClose: true,
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem').filter({hasText: 'delete'}),
			trigger: page
				.locator('article')
				.filter({hasText: commentText})
				.getByTitle('actions'),
		});

		await waitForAlert(page, 'Success:Your comment has been deleted.', {
			autoClose: true,
		});

		await page.goto(
			`/web/cms/view-asset?objectEntryId=${objectEntry.id}&messageId=${commentId}`
		);

		await expect(
			page.getByText('This comment is no longer available.')
		).toBeVisible();
		await expect(page.locator('article[aria-current="true"]')).toHaveCount(
			0
		);
	}
);
