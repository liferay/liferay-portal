/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {commentsPagesTest} from '../../../fixtures/commentsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {sharingPagesTest} from '../../../fixtures/sharingPagesTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {CommentsPage} from '../../../pages/comment/CommentsPage';
import {SharePage} from '../../../pages/sharing-web/SharePage';
import {SharedContentViewerPage} from '../../../pages/sharing-web/SharedContentViewerPage';
import {SharingNotificationPage} from '../../../pages/sharing-web/SharingNotificationPage';
import getRandomString from '../../../utils/getRandomString';
import {
	createRecipient,
	withRecipientPage,
} from '../../../utils/sharingRecipient';
import {blogsPagesTest} from './fixtures/blogsPagesTest';
import {BlogsPage} from './pages/BlogsPage';

const test = mergeTests(
	loginTest(),
	dataApiHelpersTest,
	isolatedSiteTest,
	blogsPagesTest,
	sharingPagesTest,
	commentsPagesTest
);

const PERMISSION_GERUND = {
	Comment: 'commenting',
	Update: 'updating',
	View: 'viewing',
} as const;

async function newRecipient(apiHelpers: DataApiHelpers) {
	const user = await createRecipient(apiHelpers);

	return {
		emailAddress: user.emailAddress,
		fullName: `${user.givenName} ${user.familyName}`,
		screenName: user.alternateName,
	};
}

async function shareBlog(
	blogsPage: BlogsPage,
	sharePage: SharePage,
	{
		emailAddress,
		friendlyUrlPath,
		headline,
		...shareOptions
	}: {
		emailAddress: string;
		friendlyUrlPath: string;
		headline: string;
		permission?: 'Comment' | 'Update' | 'View';
	}
) {
	await blogsPage.goto(friendlyUrlPath);

	await blogsPage.goToBlogEntryAction('Share', headline);

	await sharePage.share(emailAddress, shareOptions);
}

test(
	'Can change collaborator permissions via Shared by Me',
	{tag: '@LPD-97157'},
	async ({
		apiHelpers,
		blogsPage,
		browser,
		manageCollaboratorsPage,
		sharePage,
		sharedContentPage,
		site,
	}) => {
		const headline = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {headline});

		const recipient = await newRecipient(apiHelpers);

		await shareBlog(blogsPage, sharePage, {
			emailAddress: recipient.emailAddress,
			friendlyUrlPath: site.friendlyUrlPath,
			headline,
			permission: 'Update',
		});

		// Lower the permission from Update to View via Shared by Me.

		await sharedContentPage.goto('Shared by Me');

		await sharedContentPage.openRowAction(headline, 'Manage Collaborators');

		await manageCollaboratorsPage.changePermission(
			recipient.fullName,
			'Can View'
		);

		await withRecipientPage(
			browser,
			recipient.screenName,
			async (recipientPage) => {
				const notificationPage = new SharingNotificationPage(
					recipientPage
				);

				await notificationPage.goToNotifications();

				await expect(
					notificationPage.sharedContentNotification(
						'Test Test',
						headline,
						'updating'
					)
				).toBeVisible();

				await notificationPage
					.sharedContentNotification('Test Test', headline, 'viewing')
					.click();

				await new SharedContentViewerPage(
					recipientPage
				).assertSharingPermission(headline, 'View');
			}
		);
	}
);

test(
	'Cannot view a deleted entry via its notification',
	{tag: '@LPD-97157'},
	async ({apiHelpers, blogsPage, browser, sharePage, site}) => {
		const headline = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {headline});

		const recipient = await newRecipient(apiHelpers);

		await shareBlog(blogsPage, sharePage, {
			emailAddress: recipient.emailAddress,
			friendlyUrlPath: site.friendlyUrlPath,
			headline,
			permission: 'Update',
		});

		await blogsPage.goto(site.friendlyUrlPath);

		await blogsPage.moveEntryToRecycleBin(headline);

		await withRecipientPage(
			browser,
			recipient.screenName,
			async (recipientPage) => {
				const notificationPage = new SharingNotificationPage(
					recipientPage
				);

				await notificationPage.goToNotifications();

				await expect(notificationPage.deletedEntryTitle).toBeVisible();

				await expect(
					notificationPage.deletedEntryContent
				).toBeVisible();
			}
		);
	}
);

test(
	'Can view, comment on, and update a blog entry per its shared permission',
	{tag: '@LPD-97157'},
	async ({apiHelpers, blogsPage, browser, sharePage, site}) => {
		const headline = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {headline});

		const recipient = await newRecipient(apiHelpers);

		const permissions = ['Comment', 'View', 'Update'] as const;

		for (const permission of permissions) {
			await shareBlog(blogsPage, sharePage, {
				emailAddress: recipient.emailAddress,
				friendlyUrlPath: site.friendlyUrlPath,
				headline,
				permission,
			});

			await withRecipientPage(
				browser,
				recipient.screenName,
				async (recipientPage) => {
					const notificationPage = new SharingNotificationPage(
						recipientPage
					);

					await notificationPage.goToNotifications();

					await notificationPage
						.sharedContentNotification(
							'Test Test',
							headline,
							PERMISSION_GERUND[permission]
						)
						.click();

					await new SharedContentViewerPage(
						recipientPage
					).assertSharingPermission(headline, permission);

					if (permission === 'Comment') {
						const commentsPage = new CommentsPage(recipientPage);

						await commentsPage.addComment('test');

						await commentsPage.assertComment(
							'test',
							recipient.fullName,
							1
						);
					}
				}
			);
		}
	}
);

test(
	'Sends a notification for each sharing permission granted',
	{tag: '@LPD-97157'},
	async ({apiHelpers, blogsPage, browser, sharePage, site}) => {
		const headline = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {headline});

		const recipient = await newRecipient(apiHelpers);

		const permissions = ['View', 'Update', 'Comment'] as const;

		for (const permission of permissions) {
			await shareBlog(blogsPage, sharePage, {
				emailAddress: recipient.emailAddress,
				friendlyUrlPath: site.friendlyUrlPath,
				headline,
				permission,
			});
		}

		await withRecipientPage(
			browser,
			recipient.screenName,
			async (recipientPage) => {
				const notificationPage = new SharingNotificationPage(
					recipientPage
				);

				await notificationPage.assertBadgeCount(3);

				await notificationPage.goToNotifications();

				for (const permission of permissions) {
					await expect(
						notificationPage.sharedContentNotification(
							'Test Test',
							headline,
							PERMISSION_GERUND[permission]
						)
					).toBeVisible();
				}
			}
		);
	}
);
