/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import * as path from 'path';

import {commentsPagesTest} from '../../../fixtures/commentsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {sharingPagesTest} from '../../../fixtures/sharingPagesTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {CommentsPage} from '../../../pages/comment/CommentsPage';
import {DocumentLibraryPage} from '../../../pages/document-library-web/DocumentLibraryPage';
import {SharePage} from '../../../pages/sharing-web/SharePage';
import {SharedContentPage} from '../../../pages/sharing-web/SharedContentPage';
import {SharedContentViewerPage} from '../../../pages/sharing-web/SharedContentViewerPage';
import {SharingNotificationPage} from '../../../pages/sharing-web/SharingNotificationPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {
	createRecipient,
	withRecipientPage,
} from '../../../utils/sharingRecipient';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(
	loginTest(),
	dataApiHelpersTest,
	isolatedSiteTest,
	documentLibraryPagesTest,
	sharingPagesTest,
	commentsPagesTest
);

const IMAGE_PATH = path.join(__dirname, 'dependencies', 'image1.jpeg');

const PERMISSION_GERUND = {
	Comment: 'commenting',
	Update: 'updating',
	View: 'viewing',
} as const;

async function createDocument(
	apiHelpers: DataApiHelpers,
	siteId: string,
	title: string
) {
	return apiHelpers.headlessDelivery.postDocument(
		siteId,
		createReadStream(IMAGE_PATH),
		{fileName: `${title}.jpeg`, title, viewableBy: 'Owner'}
	);
}

async function newRecipient(apiHelpers: DataApiHelpers) {
	const user = await createRecipient(apiHelpers);

	return {
		emailAddress: user.emailAddress,
		fullName: `${user.givenName} ${user.familyName}`,
		id: user.id,
		screenName: user.alternateName,
	};
}

async function shareDocument(
	documentLibraryPage: DocumentLibraryPage,
	sharePage: SharePage,
	{
		emailAddress,
		friendlyUrlPath,
		title,
		...shareOptions
	}: {
		allowSharing?: boolean;
		emailAddress: string;
		friendlyUrlPath: string;
		permission?: 'Comment' | 'Update' | 'View';
		title: string;
	}
) {
	await documentLibraryPage.goto(friendlyUrlPath);

	await documentLibraryPage.goToShareFileEntry(title);

	await sharePage.share(emailAddress, shareOptions);
}

async function moveDocumentToRecycleBin(
	documentLibraryPage: DocumentLibraryPage,
	friendlyUrlPath: string,
	title: string
) {
	await documentLibraryPage.goto(friendlyUrlPath);

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: documentLibraryPage.page.getByRole('menuitem', {
			exact: true,
			name: 'Delete',
		}),
		trigger: documentLibraryPage.page
			.locator('.card', {hasText: title})
			.getByLabel('Actions'),
	});

	await waitForAlert(
		documentLibraryPage.page,
		'was moved to the Recycle Bin'
	);
}

async function openDocumentView(
	documentLibraryPage: DocumentLibraryPage,
	friendlyUrlPath: string,
	title: string
) {
	await documentLibraryPage.goto(friendlyUrlPath);

	await documentLibraryPage.page
		.getByRole('link', {exact: true, name: title})
		.click();
}

test(
	'Cannot share with an invalid email address',
	{tag: ['@LPS-94284', '@LPS-105038']},
	async ({apiHelpers, documentLibraryPage, sharePage, site}) => {
		const title = getRandomString();

		await createDocument(apiHelpers, site.id, title);

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await documentLibraryPage.goToFileEntryAction('Share', title);

		// An address with a trailing space is rejected and not tokenized.

		await sharePage.inviteInput.fill('userea2@liferay.com ');

		await sharePage.inviteInput.press('Enter');

		await expect(sharePage.feedbackItem('does not exist')).toBeVisible();

		await expect(
			sharePage.collaboratorTag('userea2@liferay.com')
		).toBeHidden();

		// An invalid address remains in the input instead of being cleared.

		await sharePage.inviteInput.fill('userea3@liferay.com');

		await sharePage.inviteInput.press('Enter');

		await expect(sharePage.inviteInput).toHaveValue('userea3@liferay.com');
	}
);

test(
	'Can share a document with a special character in the title',
	{tag: '@LPS-94661'},
	async ({apiHelpers, browser, documentLibraryPage, sharePage, site}) => {
		const title = `${getRandomString()}'${getRandomString()}`;

		await createDocument(apiHelpers, site.id, title);

		const recipient = await newRecipient(apiHelpers);

		await shareDocument(documentLibraryPage, sharePage, {
			emailAddress: recipient.emailAddress,
			friendlyUrlPath: site.friendlyUrlPath,
			permission: 'Comment',
			title,
		});

		await withRecipientPage(
			browser,
			recipient.screenName,
			async (recipientPage) => {
				const notificationPage = new SharingNotificationPage(
					recipientPage
				);

				await notificationPage.assertBadgeCount(1);

				await notificationPage.goToNotifications();

				await expect(
					notificationPage.sharedContentNotification(
						'Test Test',
						title,
						'commenting'
					)
				).toBeVisible();
			}
		);
	}
);

test(
	'Shows a single shared entry when multiple users share the same document',
	{tag: '@LPD-97157'},
	async ({apiHelpers, browser, documentLibraryPage, sharePage, site}) => {
		const title = getRandomString();

		await createDocument(apiHelpers, site.id, title);

		const administrator =
			await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

		const sharingUser = await newRecipient(apiHelpers);

		await apiHelpers.headlessAdminUser.assignUserToRole(
			administrator.externalReferenceCode,
			sharingUser.id
		);

		const recipient = await newRecipient(apiHelpers);

		await shareDocument(documentLibraryPage, sharePage, {
			emailAddress: recipient.emailAddress,
			friendlyUrlPath: site.friendlyUrlPath,
			title,
		});

		// The second administrator shares the same document with the recipient.

		await withRecipientPage(
			browser,
			sharingUser.screenName,
			async (sharingUserPage) => {
				const sharingUserDocumentLibraryPage = new DocumentLibraryPage(
					sharingUserPage
				);

				const sharingUserSharePage = new SharePage(sharingUserPage);

				await shareDocument(
					sharingUserDocumentLibraryPage,
					sharingUserSharePage,
					{
						emailAddress: recipient.emailAddress,
						friendlyUrlPath: site.friendlyUrlPath,
						title,
					}
				);
			}
		);

		await withRecipientPage(
			browser,
			recipient.screenName,
			async (recipientPage) => {
				const notificationPage = new SharingNotificationPage(
					recipientPage
				);

				await notificationPage.assertBadgeCount(2);

				await notificationPage.goToNotifications();

				await expect(
					notificationPage.sharedContentNotification(
						'Test Test',
						title,
						'viewing'
					)
				).toBeVisible();

				await expect(
					notificationPage.sharedContentNotification(
						sharingUser.fullName,
						title,
						'viewing'
					)
				).toBeVisible();

				const sharedContentPage = new SharedContentPage(recipientPage);

				await sharedContentPage.goto('Shared with Me');

				await expect(sharedContentPage.entryRow(title)).toHaveCount(1);
			}
		);
	}
);

test(
	'Cannot re-share a document without sharing permissions',
	{tag: '@LPD-97157'},
	async ({apiHelpers, browser, documentLibraryPage, sharePage, site}) => {
		const title = getRandomString();

		await createDocument(apiHelpers, site.id, title);

		const recipient = await newRecipient(apiHelpers);

		await shareDocument(documentLibraryPage, sharePage, {
			allowSharing: false,
			emailAddress: recipient.emailAddress,
			friendlyUrlPath: site.friendlyUrlPath,
			title,
		});

		await withRecipientPage(
			browser,
			recipient.screenName,
			async (recipientPage) => {
				const sharedContentPage = new SharedContentPage(recipientPage);

				await sharedContentPage.goto('Shared with Me');

				await sharedContentPage
					.entryRow(title)
					.getByRole('button', {name: 'Actions'})
					.click();

				await expect(
					recipientPage.getByRole('menuitem', {name: 'Share'})
				).toBeHidden();
			}
		);
	}
);

test(
	'Can re-share a document via the Shared Content app',
	{tag: '@LPS-94585'},
	async ({apiHelpers, browser, documentLibraryPage, sharePage, site}) => {
		const title = getRandomString();

		const document = await createDocument(apiHelpers, site.id, title);

		const collaborator = await newRecipient(apiHelpers);

		const recipient = await newRecipient(apiHelpers);

		await shareDocument(documentLibraryPage, sharePage, {
			allowSharing: true,
			emailAddress: collaborator.emailAddress,
			friendlyUrlPath: site.friendlyUrlPath,
			title,
		});

		// The collaborator re-shares the document with a third user.

		await withRecipientPage(
			browser,
			collaborator.screenName,
			async (collaboratorPage) => {
				const sharedContentPage = new SharedContentPage(
					collaboratorPage
				);

				await sharedContentPage.goto('Shared with Me');

				await sharedContentPage.openRowAction(title, 'Share');

				await new SharePage(collaboratorPage).share(
					recipient.emailAddress
				);
			}
		);

		await withRecipientPage(
			browser,
			recipient.screenName,
			async (recipientPage) => {
				const notificationPage = new SharingNotificationPage(
					recipientPage
				);

				await notificationPage.assertBadgeCount(1);

				await notificationPage.goToNotifications();

				await expect(
					notificationPage.sharedContentNotification(
						collaborator.fullName,
						title,
						'viewing'
					)
				).toBeVisible();
			}
		);

		// After the owner deletes the document it disappears from the app.

		await apiHelpers.headlessDelivery.deleteDocument(document.id);

		await withRecipientPage(
			browser,
			collaborator.screenName,
			async (collaboratorPage) => {
				const sharedContentPage = new SharedContentPage(
					collaboratorPage
				);

				await sharedContentPage.goto('Shared with Me');

				await expect(sharedContentPage.entryRow(title)).toHaveCount(0);
			}
		);
	}
);

test(
	'Shows a deleted entry as not visible in the Shared Content app',
	{tag: '@LPS-94858'},
	async ({apiHelpers, browser, documentLibraryPage, sharePage, site}) => {
		const title = getRandomString();

		await createDocument(apiHelpers, site.id, title);

		const recipient = await newRecipient(apiHelpers);

		await shareDocument(documentLibraryPage, sharePage, {
			emailAddress: recipient.emailAddress,
			friendlyUrlPath: site.friendlyUrlPath,
			title,
		});

		await moveDocumentToRecycleBin(
			documentLibraryPage,
			site.friendlyUrlPath,
			title
		);

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

				const sharedContentPage = new SharedContentPage(recipientPage);

				await sharedContentPage.goto('Shared with Me');

				await expect(
					sharedContentPage.statusLabel('Not Visible')
				).toBeVisible();

				await expect(sharedContentPage.entryLink(title)).toBeHidden();
			}
		);
	}
);

test(
	'Can change a collaborator permission via Manage Collaborators',
	{tag: '@LPD-97157'},
	async ({
		apiHelpers,
		browser,
		documentLibraryPage,
		documentLibraryViewFileEntryPage,
		manageCollaboratorsPage,
		sharePage,
		site,
	}) => {
		const title = getRandomString();

		await createDocument(apiHelpers, site.id, title);

		const recipient = await newRecipient(apiHelpers);

		await shareDocument(documentLibraryPage, sharePage, {
			emailAddress: recipient.emailAddress,
			friendlyUrlPath: site.friendlyUrlPath,
			title,
		});

		// Raise the permission from View to Comment.

		await openDocumentView(
			documentLibraryPage,
			site.friendlyUrlPath,
			title
		);

		await documentLibraryViewFileEntryPage.openManageCollaborators();

		await manageCollaboratorsPage.changePermission(
			recipient.fullName,
			'Can Comment'
		);

		await withRecipientPage(
			browser,
			recipient.screenName,
			async (recipientPage) => {
				const notificationPage = new SharingNotificationPage(
					recipientPage
				);

				await notificationPage.goToNotifications();

				await notificationPage
					.sharedContentNotification('Test Test', title, 'commenting')
					.click();

				await new SharedContentViewerPage(
					recipientPage
				).assertSharingPermission(title, 'Comment');
			}
		);

		// Raise the permission from Comment to Update.

		await openDocumentView(
			documentLibraryPage,
			site.friendlyUrlPath,
			title
		);

		await documentLibraryViewFileEntryPage.openManageCollaborators();

		await manageCollaboratorsPage.changePermission(
			recipient.fullName,
			'Can Update'
		);

		await withRecipientPage(
			browser,
			recipient.screenName,
			async (recipientPage) => {
				const notificationPage = new SharingNotificationPage(
					recipientPage
				);

				await notificationPage.goToNotifications();

				await notificationPage
					.sharedContentNotification('Test Test', title, 'updating')
					.click();

				await new SharedContentViewerPage(
					recipientPage
				).assertSharingPermission(title, 'Update');
			}
		);
	}
);

test(
	'Can view and remove collaborators',
	{tag: '@LPD-97157'},
	async ({
		apiHelpers,
		browser,
		documentLibraryPage,
		documentLibraryViewFileEntryPage,
		manageCollaboratorsPage,
		sharePage,
		site,
	}) => {
		const title = getRandomString();

		await createDocument(apiHelpers, site.id, title);

		const firstCollaborator = await newRecipient(apiHelpers);

		const secondCollaborator = await newRecipient(apiHelpers);

		await shareDocument(documentLibraryPage, sharePage, {
			emailAddress: firstCollaborator.emailAddress,
			friendlyUrlPath: site.friendlyUrlPath,
			title,
		});

		await shareDocument(documentLibraryPage, sharePage, {
			emailAddress: secondCollaborator.emailAddress,
			friendlyUrlPath: site.friendlyUrlPath,
			title,
		});

		// The owner sees both collaborators in the info panel.

		await openDocumentView(
			documentLibraryPage,
			site.friendlyUrlPath,
			title
		);

		await documentLibraryViewFileEntryPage.openInfoTab();

		await expect(
			documentLibraryViewFileEntryPage.collaboratorAvatar(
				firstCollaborator.fullName
			)
		).toBeVisible();

		await expect(
			documentLibraryViewFileEntryPage.collaboratorAvatar(
				secondCollaborator.fullName
			)
		).toBeVisible();

		// A collaborator without sharing permissions cannot manage
		// collaborators.

		await withRecipientPage(
			browser,
			firstCollaborator.screenName,
			async (recipientPage) => {
				const sharedContentPage = new SharedContentPage(recipientPage);

				await sharedContentPage.goto('Shared with Me');

				await sharedContentPage.entryLink(title).click();

				await new SharedContentViewerPage(
					recipientPage
				).actionsButton.click();

				await expect(
					recipientPage.getByRole('menuitem', {
						name: 'Manage Collaborators',
					})
				).toBeHidden();
			}
		);

		// The owner removes every collaborator.

		await openDocumentView(
			documentLibraryPage,
			site.friendlyUrlPath,
			title
		);

		await documentLibraryViewFileEntryPage.openManageCollaborators();

		await manageCollaboratorsPage.removeCollaborators([
			firstCollaborator.fullName,
			secondCollaborator.fullName,
		]);

		await expect(manageCollaboratorsPage.emptyState).toBeVisible();

		await manageCollaboratorsPage.save();
	}
);

test(
	'Can view, comment on, and update a document per its shared permission',
	{tag: ['@LPS-94294', '@LPS-94448']},
	async ({apiHelpers, browser, documentLibraryPage, sharePage, site}) => {
		const recipient = await newRecipient(apiHelpers);

		const documents = [
			{permission: 'View' as const, title: getRandomString()},
			{permission: 'Comment' as const, title: getRandomString()},
			{permission: 'Update' as const, title: getRandomString()},
		];

		for (const {permission, title} of documents) {
			await createDocument(apiHelpers, site.id, title);

			await shareDocument(documentLibraryPage, sharePage, {
				emailAddress: recipient.emailAddress,
				friendlyUrlPath: site.friendlyUrlPath,
				permission,
				title,
			});

			// The owner sees the document flagged as shared.

			await documentLibraryPage.goto(site.friendlyUrlPath);

			await expect(
				documentLibraryPage.page
					.locator('.card', {hasText: title})
					.locator('.lexicon-icon-users')
			).toBeVisible();
		}

		await withRecipientPage(
			browser,
			recipient.screenName,
			async (recipientPage) => {
				const notificationPage = new SharingNotificationPage(
					recipientPage
				);

				const sharedContentViewerPage = new SharedContentViewerPage(
					recipientPage
				);

				for (const {permission, title} of documents) {
					await notificationPage.goToNotifications();

					await notificationPage
						.sharedContentNotification(
							'Test Test',
							title,
							PERMISSION_GERUND[permission]
						)
						.click();

					await sharedContentViewerPage.assertSharingPermission(
						title,
						permission
					);

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
			}
		);
	}
);
