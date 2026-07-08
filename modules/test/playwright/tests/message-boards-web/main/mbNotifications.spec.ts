/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {messageBoardsPagesTest} from '../../../fixtures/messageBoardsTest';
import {AccountNotificationsPage} from '../../../pages/portal-workflow-task-web/AccountNotificationsPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {
	performUserSwitch,
	performUserSwitchViaApi,
	userData,
} from '../../../utils/performLogin';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	messageBoardsPagesTest
);

async function openThreadActionItem(
	page: Page,
	itemName: string
): Promise<Locator> {
	const messageBoards = page.locator(
		'.portlet-boundary_com_liferay_message_boards_web_portlet_MBPortlet_'
	);

	const item = page
		.locator('.dropdown-menu:visible')
		.getByText(itemName, {exact: true});

	await clickAndExpectToBeVisible({
		target: item,
		trigger: messageBoards.getByTitle('Actions').first(),
	});

	return item;
}

test('Can view a website notification for a new thread in a subscribed category', async ({
	apiHelpers,
	messageBoardsEditThreadPage,
	messageBoardsPage,
	messageBoardsWidgetPage,
	page,
	site,
}) => {
	const categoryName = getRandomString();

	await apiHelpers.headlessDelivery.postSiteMessageBoardSection({
		siteId: site.id,
		title: categoryName,
	});

	const layout = await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

	// A second user subscribes to the category

	const administratorRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

	const subscriber = await apiHelpers.headlessAdminUser.postUserAccount();

	await apiHelpers.headlessAdminUser.assignUserToRole(
		administratorRole.externalReferenceCode,
		subscriber.id
	);

	userData[subscriber.alternateName] = {
		name: subscriber.givenName,
		password: 'test',
		surname: subscriber.familyName,
	};

	await performUserSwitch(page, subscriber.alternateName);

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await page.getByRole('link', {name: categoryName}).click();

	// Subscribe to the category through its actions menu

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.locator('.dropdown-menu').getByText('Subscribe', {
			exact: true,
		}),
		trigger: page.locator('a.component-action.dropdown-toggle').last(),
	});

	// The administrator posts a new thread in the category

	await performUserSwitch(page, 'test');

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: categoryName}).click();

	await messageBoardsPage.goToCreateNewThread();

	await messageBoardsEditThreadPage.subjectSelector.fill(getRandomString());
	await messageBoardsEditThreadPage.bodyTextBox.fill(getRandomString());
	await messageBoardsEditThreadPage.publishButton.click();

	// The subscriber is notified about the new thread

	await performUserSwitch(page, subscriber.alternateName);

	await expect(async () => {
		await page.reload();

		await expect(page.locator('a.panel-notifications-count')).toHaveText(
			'1',
			{timeout: 5000}
		);
	}).toPass();

	await page.locator('a.panel-notifications-count').click();

	await expect(
		page.getByText('Test Test added a new message boards message.')
	).toBeVisible();
});

test(
	'A subscriber can read a message only while keeping its view permission',
	{tag: ['@LPS-97376', '@LPS-135908']},
	async ({
		apiHelpers,
		messageBoardsPage,
		messageBoardsWidgetPage,
		page,
		site,
	}) => {
		const replyBody = getRandomString();
		const threadSubject = getRandomString();

		const layout =
			await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

		// A thread seeded through the API is viewable by its owner only

		await apiHelpers.headlessDelivery.postMessageBoardThread({
			articleBody: getRandomString(),
			headline: threadSubject,
			siteId: site.id,
		});

		// Grant the regular user role view and subscribe on the thread

		await messageBoardsPage.setThreadRolePermissions(
			threadSubject,
			'user',
			{subscribe: true, view: true},
			site.friendlyUrlPath
		);

		// A second user subscribes to the thread

		const subscriber = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[subscriber.alternateName] = {
			name: subscriber.givenName,
			password: 'test',
			surname: subscriber.familyName,
		};

		await performUserSwitch(page, subscriber.alternateName);

		await messageBoardsWidgetPage.goToThread(site, layout, threadSubject);

		await (await openThreadActionItem(page, 'Subscribe')).click();

		await page.waitForLoadState('networkidle');

		await expect(
			await openThreadActionItem(page, 'Unsubscribe')
		).toBeVisible();

		// The administrator replies to the thread

		await performUserSwitch(page, 'test');

		await messageBoardsWidgetPage.replyToThread(
			site,
			layout,
			threadSubject,
			replyBody
		);

		await expect(page.getByText(replyBody)).toBeVisible();

		// Grant the regular user role view on the reply, which is created with
		// the owner only permissions of its thread

		const replyCard = page
			.locator('.card-tab.message-container')
			.filter({hasText: replyBody});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.dropdown-menu:visible')
				.getByText('Permissions', {exact: true}),
			trigger: replyCard.locator('a.component-action.dropdown-toggle'),
		});

		const replyPermissionsFrame = page.frameLocator(
			'iframe[title="Permissions"]'
		);

		await replyPermissionsFrame.locator('#user_ACTION_VIEW').check();

		await replyPermissionsFrame.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText('Your request completed successfully.')
		).toBeVisible();

		// The subscriber opens the thread and can read the reply

		await performUserSwitch(page, subscriber.alternateName);

		await messageBoardsWidgetPage.goToThread(site, layout, threadSubject);

		const messageURL = page.url();

		await expect(page.getByText(replyBody)).toBeVisible();

		// The administrator revokes the user role view and subscribe permissions

		await performUserSwitch(page, 'test');

		await messageBoardsPage.setThreadRolePermissions(
			threadSubject,
			'user',
			{subscribe: false, view: false},
			site.friendlyUrlPath
		);

		// The subscriber can no longer read the message

		await performUserSwitch(page, subscriber.alternateName);

		await page.goto(messageURL);

		await expect(page.getByText(replyBody)).toBeHidden();

		await expect(
			page.getByText('You do not have the required permissions.')
		).toBeVisible();
	}
);

test('A subscriber sees a website notification when a subscribed category thread is updated', async ({
	apiHelpers,
	messageBoardsEditThreadPage,
	messageBoardsPage,
	messageBoardsWidgetPage,
	page,
	site,
}) => {
	const categoryName = getRandomString();
	const threadBodyEdit = getRandomString();
	const threadSubject = getRandomString();
	const threadSubjectEdit = getRandomString();

	const messageBoardSection =
		await apiHelpers.headlessDelivery.postSiteMessageBoardSection({
			siteId: site.id,
			title: categoryName,
		});

	const layout = await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

	// A subscriber subscribes to the category and posts a thread in it

	const administratorRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

	const subscriber = await apiHelpers.headlessAdminUser.postUserAccount();

	await apiHelpers.headlessAdminUser.assignUserToRole(
		administratorRole.externalReferenceCode,
		subscriber.id
	);

	userData[subscriber.alternateName] = {
		name: subscriber.givenName,
		password: 'test',
		surname: subscriber.familyName,
	};

	await performUserSwitchViaApi(page, subscriber.alternateName);

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await page.getByRole('link', {name: categoryName}).click();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.locator('.dropdown-menu').getByText('Subscribe', {
			exact: true,
		}),
		trigger: page.locator('a.component-action.dropdown-toggle').last(),
	});

	await apiHelpers.headlessDelivery.postMessageBoardSectionMessageBoardThread(
		{
			articleBody: getRandomString(),
			headline: threadSubject,
			messageBoardSectionId: messageBoardSection.id,
		}
	);

	// The administrator updates the thread

	await performUserSwitchViaApi(page, 'test');

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: categoryName}).click();

	await page.getByRole('link', {name: threadSubject}).click();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.locator('.dropdown-menu').getByText('Edit', {exact: true}),
		trigger: page.locator('.panel-heading .dropdown-toggle'),
	});

	await messageBoardsEditThreadPage.subjectSelector.fill(threadSubjectEdit);
	await messageBoardsEditThreadPage.bodyTextBox.fill(threadBodyEdit);
	await messageBoardsEditThreadPage.publishButton.click();

	await expect(page.getByTestId('headerTitle')).toHaveText(threadSubjectEdit);

	// The subscriber is notified about the updated thread

	const accountNotificationsPage = new AccountNotificationsPage(page);

	await performUserSwitchViaApi(page, subscriber.alternateName);

	await expect(async () => {
		await page.reload();

		await expect(accountNotificationsPage.notificationsCount).toHaveText(
			'1',
			{timeout: 5000}
		);
	}).toPass();

	await accountNotificationsPage.notificationsCount.click();

	await page.getByRole('link', {name: threadSubjectEdit}).click();

	await expect(page.getByText(threadBodyEdit)).toBeVisible();

	// Viewing the updated thread clears the notification

	await page.reload();

	await expect(accountNotificationsPage.notificationsCount).toBeHidden();
});

test('A subscriber with disabled website deliveries receives no notification for a subscribed category thread', async ({
	apiHelpers,
	messageBoardsEditThreadPage,
	messageBoardsPage,
	messageBoardsWidgetPage,
	page,
	site,
}) => {
	const categoryName = getRandomString();
	const threadSubject = getRandomString();

	const messageBoardSection =
		await apiHelpers.headlessDelivery.postSiteMessageBoardSection({
			siteId: site.id,
			title: categoryName,
		});

	const layout = await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

	const administratorRole =
		await apiHelpers.headlessAdminUser.getRoleByName('Administrator');

	const subscriber = await apiHelpers.headlessAdminUser.postUserAccount();

	await apiHelpers.headlessAdminUser.assignUserToRole(
		administratorRole.externalReferenceCode,
		subscriber.id
	);

	userData[subscriber.alternateName] = {
		name: subscriber.givenName,
		password: 'test',
		surname: subscriber.familyName,
	};

	// The subscriber disables its website deliveries for message boards and
	// subscribes to the category

	await performUserSwitchViaApi(page, subscriber.alternateName);

	const accountNotificationsPage = new AccountNotificationsPage(page);

	await accountNotificationsPage.disableWebsiteDeliveries('Message Boards', [
		'Adds a new post in a thread or category you are subscribed to.',
		'Updates a post you are subscribed to.',
	]);

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

	await page.getByRole('link', {name: categoryName}).click();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.locator('.dropdown-menu').getByText('Subscribe', {
			exact: true,
		}),
		trigger: page.locator('a.component-action.dropdown-toggle').last(),
	});

	// The administrator adds and updates a thread in the category

	await performUserSwitchViaApi(page, 'test');

	await apiHelpers.headlessDelivery.postMessageBoardSectionMessageBoardThread(
		{
			articleBody: getRandomString(),
			headline: threadSubject,
			messageBoardSectionId: messageBoardSection.id,
		}
	);

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await page.getByRole('link', {name: categoryName}).click();

	await page.getByRole('link', {name: threadSubject}).click();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.locator('.dropdown-menu').getByText('Edit', {exact: true}),
		trigger: page.locator('.panel-heading .dropdown-toggle'),
	});

	await messageBoardsEditThreadPage.subjectSelector.fill(getRandomString());
	await messageBoardsEditThreadPage.bodyTextBox.fill(getRandomString());
	await messageBoardsEditThreadPage.publishButton.click();

	// The subscriber receives no website notification

	await performUserSwitchViaApi(page, subscriber.alternateName);

	await page.reload();

	await expect(accountNotificationsPage.notificationsCount).toBeHidden();
});
