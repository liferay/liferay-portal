/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	changeTrackingPagesTest,
	isolatedSiteTest,
	journalPagesTest,
	productMenuPageTest
);

test('LPD-30098 Invite user as admin', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	journalEditArticlePage,
	page,
	site,
}) => {
	try {
		const user1 =
			await changeTrackingPage.addUserWithPublicationsUserRole();

		const user2 =
			await changeTrackingPage.addUserWithPublicationsUserRole();

		await changeTrackingPage.workOnPublication(ctCollection);

		await changeTrackingPage.addUserToPublication(
			ctCollection.body.name,
			'Admin',
			user1
		);

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		const title = getRandomString();

		await journalEditArticlePage.fillTitle(title);

		await journalEditArticlePage.publishArticle();

		await waitForAlert(page, `Success:${title} was created successfully.`);

		await performLogout(page);

		await performLoginViaApi({page, screenName: user1.alternateName});

		await page.getByTestId('userPersonalMenu').click();

		await page.getByRole('menuitem', {name: 'Notifications'}).click();

		await expect(
			page.getByText(
				`has invited you to work on ${ctCollection.body.name} as a Admin.`
			)
		).toBeVisible();

		await changeTrackingPage.goto();

		await page.waitForLoadState('load');

		await page.getByRole('button', {name: 'Actions'}).click();

		await page.getByRole('menuitem', {name: 'Edit'}).click();

		await page.getByPlaceholder('Enter the name of the').fill(title);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page, 'Success:Successfully updated');

		await page.getByRole('button', {name: 'Actions'}).click();

		await expect(
			page.getByRole('menuitem', {name: 'Delete'})
		).toBeVisible();

		await changeTrackingPage.addUserToPublication(title, 'Admin', user2);

		await changeTrackingPage.assertPublicationCommentsCRUDPermissions();

		await page.reload();

		await page.getByRole('link', {name: 'Publish'}).click();

		await page.getByRole('button', {name: 'Publish'}).click();

		await expect(page.getByRole('link', {name: 'History'})).toBeVisible();

		await expect(
			page.locator('div').filter({hasText: title}).first()
		).toBeVisible();

		await waitForAlert(
			page,
			'Success:Your request completed successfully.'
		);

		await performLogout(page);

		await performLoginViaApi({page, screenName: 'test'});

		await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user1.id));
		await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user2.id));
	}
	finally {
		await apiHelpers.headlessChangeTracking.deleteCTCollection(
			ctCollection.body.id
		);
	}
});

test('LPD-89418 Paste email address to automatically add user in Invite Users modal', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	try {
		const user = await changeTrackingPage.addUserWithPublicationsUserRole();

		await changeTrackingPage.workOnPublication(ctCollection);

		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await page.getByLabel('View Collaborators').click();

		const input = page.getByPlaceholder('Enter name or email address.');

		await input.click();

		await input.evaluate(
			(element: HTMLInputElement, emailAddress: string) => {
				const dt = new DataTransfer();

				dt.setData('text/plain', emailAddress);

				element.dispatchEvent(
					new ClipboardEvent('paste', {
						bubbles: true,
						cancelable: true,
						clipboardData: dt,
					})
				);
			},
			user.emailAddress
		);

		await expect(page.getByText(user.emailAddress)).toBeVisible();

		await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user.id));
	}
	finally {
		await apiHelpers.headlessChangeTracking.deleteCTCollection(
			ctCollection.body.id
		);
	}
});

test(
	'LPD-98937 Open the invite users autocomplete only after typing',
	{tag: '@LPD-98937'},
	async ({apiHelpers, changeTrackingPage, ctCollection, page}) => {
		try {
			const user =
				await changeTrackingPage.addUserWithPublicationsUserRole();

			await changeTrackingPage.workOnPublication(ctCollection);

			await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

			await page.getByLabel('View Collaborators').click();

			const input = page.getByPlaceholder('Enter name or email address.');

			await input.click();

			await expect(page.locator('.dropdown-menu-select')).toBeHidden();

			await input.fill(user.emailAddress);

			await expect(
				page.getByRole('option', {name: user.name})
			).toBeVisible();

			await apiHelpers.headlessAdminUser.deleteUserAccount(
				Number(user.id)
			);
		}
		finally {
			await apiHelpers.headlessChangeTracking.deleteCTCollection(
				ctCollection.body.id
			);
		}
	}
);

test('LPD-65173 Assert that the Share Link tab is hidden for Publication Templates', async ({
	changeTrackingTemplatesPage,
	page,
}) => {
	await changeTrackingTemplatesPage.gotoCreateTemplate();

	await changeTrackingTemplatesPage.openManageCollaboratorsModal();

	await expect(page.getByText('Invite Users')).toBeVisible();

	await expect(page.getByRole('button', {name: 'Share Link'})).toBeHidden();
});

test(
	'LPD-103662 Hide the share link tab and button from a viewer',
	{tag: '@LPD-103662'},
	async ({apiHelpers, changeTrackingPage, ctCollection, page}) => {
		const user = await changeTrackingPage.addUserWithPublicationsUserRole();

		try {
			await changeTrackingPage.addUserToPublication(
				ctCollection.body.name,
				'Viewer',
				user
			);

			await performLogout(page);

			await performLoginViaApi({page, screenName: user.alternateName});

			await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

			const collaboratorsButton = page.getByLabel('View Collaborators');
			const modal = page.locator('.publications-invite-users-modal');
			const modalTitle = modal.locator('.modal-title').first();

			await expect(collaboratorsButton).toBeVisible();

			await expect(
				page.getByRole('button', {name: /Publication Sharing/})
			).toBeHidden();

			await clickAndExpectToBeVisible({
				target: modalTitle,
				trigger: collaboratorsButton,
			});

			await expect(
				modal.getByRole('tab', {name: 'Share Link'})
			).toBeHidden();

			await expect(modalTitle).toHaveText('View Collaborators');
		}
		finally {
			await performLogout(page);

			await performLoginViaApi({page, screenName: 'test'});

			await apiHelpers.headlessAdminUser.deleteUserAccount(
				Number(user.id)
			);
		}
	}
);

test(
	'LPD-60917 Cancel closes the collaborators modal without a discard prompt when nothing changed',
	{tag: '@LPD-60917'},
	async ({apiHelpers, changeTrackingPage, ctCollection, page}) => {
		try {
			let discardPrompted = false;

			page.on('dialog', (dialog) => {
				discardPrompted = true;

				dialog.dismiss();
			});

			await changeTrackingPage.workOnPublication(ctCollection);

			await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

			await page.getByLabel('View Collaborators').click();

			await expect(page.getByText('Invite Users')).toBeVisible();

			await clickAndExpectToBeHidden({
				target: page.getByText('Invite Users'),
				trigger: page.getByLabel('Cancel', {exact: true}),
			});

			expect(discardPrompted).toBe(false);
		}
		finally {
			await apiHelpers.headlessChangeTracking.deleteCTCollection(
				ctCollection.body.id
			);
		}
	}
);

test(
	'LPD-60917 Close button confirms discard when collaborator changes exist',
	{tag: '@LPD-60917'},
	async ({apiHelpers, changeTrackingPage, ctCollection, page}) => {
		try {
			let discardMessage = '';

			page.on('dialog', (dialog) => {
				discardMessage = dialog.message();

				dialog.dismiss();
			});

			const user =
				await changeTrackingPage.addUserWithPublicationsUserRole();

			await changeTrackingPage.workOnPublication(ctCollection);

			await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

			await page.getByLabel('View Collaborators').click();

			await expect(page.getByText('Invite Users')).toBeVisible();

			await page.getByLabel('can view').click();

			await page.getByRole('menuitem', {name: 'Admin'}).click();

			await page
				.getByPlaceholder('Enter name or email address.')
				.fill(user.emailAddress);

			await page.getByRole('option', {name: user.name}).click();

			await expect(page.getByText('Add (Admin)')).toBeVisible();

			await page.getByLabel('Close', {exact: true}).click();

			expect(discardMessage).toBe('Discard unsaved changes?');

			await apiHelpers.headlessAdminUser.deleteUserAccount(
				Number(user.id)
			);
		}
		finally {
			await apiHelpers.headlessChangeTracking.deleteCTCollection(
				ctCollection.body.id
			);
		}
	}
);
