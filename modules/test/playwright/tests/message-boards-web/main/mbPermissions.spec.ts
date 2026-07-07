/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {messageBoardsPagesTest} from '../../../fixtures/messageBoardsTest';
import createUserWithPermissions from '../../../utils/createUserWithPermissions';
import getRandomString from '../../../utils/getRandomString';
import {
	performLogout,
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

test(
	'A site member cannot edit or delete a thread without permissions',
	{tag: ['@LPS-136934', '@LPS-136936']},
	async ({
		apiHelpers,
		messageBoardsEditThreadPage,
		messageBoardsWidgetPage,
		page,
		site,
	}) => {
		const headline = getRandomString();

		const layout =
			await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

		// The administrator creates a thread through the UI

		await messageBoardsEditThreadPage.gotoAndPublishNewBasicThread(
			headline,
			getRandomString(),
			site.friendlyUrlPath
		);

		// A site member is added to the site

		const siteMemberRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		const member = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteMemberRole.id,
			site.id,
			member.id
		);

		userData[member.alternateName] = {
			name: member.givenName,
			password: 'test',
			surname: member.familyName,
		};

		await performUserSwitch(page, member.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		// The member can see the thread

		await expect(
			page.getByRole('link', {name: headline}).first()
		).toBeVisible();

		// The thread action menu offers Subscribe but not Edit or Delete

		const dropdownMenu = page.locator('.dropdown-menu');

		await expect(async () => {
			await page
				.locator('a.component-action.dropdown-toggle')
				.first()
				.click();

			await expect(
				dropdownMenu.getByText('Subscribe', {exact: true}).first()
			).toBeVisible({timeout: 3000});
		}).toPass();

		await expect(dropdownMenu.getByText('Edit', {exact: true})).toHaveCount(
			0
		);
		await expect(
			dropdownMenu.getByText('Delete', {exact: true})
		).toHaveCount(0);
	}
);

test(
	'A guest cannot view a thread without permissions',
	{tag: '@LPS-136939'},
	async ({apiHelpers, messageBoardsWidgetPage, page, site}) => {
		const headline = getRandomString();

		const layout =
			await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

		// A thread seeded through the API carries no guest view permission

		await apiHelpers.headlessDelivery.postMessageBoardThread({
			articleBody: getRandomString(),
			headline,
			siteId: site.id,
		});

		// The administrator sees the thread on the widget page

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await expect(
			page.getByRole('link', {name: headline}).first()
		).toBeVisible();

		// A guest cannot see the thread

		await performLogout(page);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await expect(
			page.getByText('There are no threads or categories.')
		).toBeVisible();

		await expect(page.getByRole('link', {name: headline})).toBeHidden();
	}
);

test(
	'A banned user cannot create a thread',
	{tag: '@LPS-136922'},
	async ({
		apiHelpers,
		messageBoardsEditThreadPage,
		messageBoardsPage,
		messageBoardsWidgetPage,
		page,
		site,
	}) => {
		const threadSubject = getRandomString();

		const layout =
			await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

		await messageBoardsEditThreadPage.gotoAndPublishNewBasicThread(
			threadSubject,
			getRandomString(),
			site.friendlyUrlPath
		);

		// A site member replies to the thread

		const siteMemberRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		const member = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteMemberRole.id,
			site.id,
			member.id
		);

		userData[member.alternateName] = {
			name: member.givenName,
			password: 'test',
			surname: member.familyName,
		};

		await performUserSwitchViaApi(page, member.alternateName);

		await messageBoardsWidgetPage.replyToThread(
			site,
			layout,
			threadSubject,
			getRandomString()
		);

		// The administrator bans the member through the reply

		await performUserSwitchViaApi(page, 'test');

		await messageBoardsPage.goToThread(threadSubject, site.friendlyUrlPath);

		await messageBoardsPage.banReplyAuthor();

		// The banned member sees the banned message on the widget page

		await performUserSwitchViaApi(page, member.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await expect(
			page.getByText('You have been banned by the moderator.')
		).toBeVisible();
	}
);

test(
	'A site member cannot reply to a thread without permissions',
	{tag: '@LPS-136935'},
	async ({
		apiHelpers,
		messageBoardsEditThreadPage,
		messageBoardsPage,
		messageBoardsWidgetPage,
		page,
		site,
	}) => {
		const threadSubject = getRandomString();

		const layout =
			await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

		await messageBoardsEditThreadPage.gotoAndPublishNewBasicThread(
			threadSubject,
			getRandomString(),
			site.friendlyUrlPath
		);

		// The site member role keeps view but loses reply on the category

		await messageBoardsPage.removeRoleReplyPermission(
			'site-member',
			site.friendlyUrlPath
		);

		// A site member is added to the site

		const siteMemberRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		const member = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteMemberRole.id,
			site.id,
			member.id
		);

		userData[member.alternateName] = {
			name: member.givenName,
			password: 'test',
			surname: member.familyName,
		};

		await performUserSwitch(page, member.alternateName);

		// The member can open the thread but is offered no reply action

		await messageBoardsWidgetPage.goToThread(site, layout, threadSubject);

		await expect(page.getByRole('button', {name: 'Reply'})).toBeHidden();
	}
);

test(
	'A site member can view their saved draft message',
	{tag: '@LPS-69728'},
	async ({
		apiHelpers,
		messageBoardsEditThreadPage,
		messageBoardsWidgetPage,
		page,
		site,
	}) => {
		const replyBody = getRandomString();
		const threadSubject = getRandomString();

		const layout =
			await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

		await messageBoardsEditThreadPage.gotoAndPublishNewBasicThread(
			threadSubject,
			getRandomString(),
			site.friendlyUrlPath
		);

		// A site member saves a reply as a draft

		const siteMemberRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		const member = await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteMemberRole.id,
			site.id,
			member.id
		);

		userData[member.alternateName] = {
			name: member.givenName,
			password: 'test',
			surname: member.familyName,
		};

		await performUserSwitch(page, member.alternateName);

		await messageBoardsWidgetPage.replyToThreadAsDraft(
			site,
			layout,
			threadSubject,
			replyBody
		);

		// The member sees their draft reply with a draft status

		await messageBoardsWidgetPage.goToThread(site, layout, threadSubject);

		await expect(page.getByText(replyBody)).toBeVisible();

		await expect(
			page.getByText('Draft', {exact: true}).first()
		).toBeVisible();

		// The member can edit their own draft

		const editMenuItem = page
			.locator('.dropdown-menu:visible')
			.getByText('Edit', {exact: true});

		await expect(async () => {
			await page
				.locator('.panel-heading .dropdown-toggle')
				.last()
				.click();

			await expect(editMenuItem).toBeVisible({timeout: 3000});
		}).toPass();
	}
);

test('A regular role with portlet access can open the message boards admin', async ({
	apiHelpers,
	messageBoardsPage,
	page,
	site,
}) => {
	const company =
		await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
			'liferay.com'
		);

	// A regular role granting only message boards portlet access

	const user = await createUserWithPermissions({
		apiHelpers,
		rolePermissions: [
			{
				actionIds: ['VIEW'],
				primaryKey: company.companyId,
				resourceName: 'com.liferay.message.boards',
				scope: 1,
			},
			{
				actionIds: ['VIEW_SITE_ADMINISTRATION'],
				primaryKey: company.companyId,
				resourceName: 'com.liferay.portal.kernel.model.Group',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL'],
				primaryKey: company.companyId,
				resourceName:
					'com_liferay_message_boards_web_portlet_MBAdminPortlet',
				scope: 1,
			},
		],
	});

	await performUserSwitch(page, user.alternateName);

	// The user can reach the message boards admin

	await messageBoardsPage.goto(site.friendlyUrlPath);

	await expect(
		page.getByText('There are no threads or categories.')
	).toBeVisible();
});

test('A guest granted reply permission can reply to a thread', async ({
	messageBoardsEditThreadPage,
	messageBoardsPage,
	messageBoardsWidgetPage,
	page,
	site,
}) => {
	await messageBoardsPage.setGuestCategoryPermissions(site.friendlyUrlPath);

	await messageBoardsEditThreadPage.gotoAndPublishNewBasicThread(
		'Thread Subject',
		'Thread Body',
		site.friendlyUrlPath
	);

	const layout = await messageBoardsWidgetPage.addMessageBoardsPortlet(site);

	// The guest reply is posted and attributed to an anonymous author

	await messageBoardsWidgetPage.addGuestReply(site, layout, 'Publish');

	await expect(page.getByText('test guest')).toBeVisible();
	await expect(page.getByText('Anonymous')).toBeVisible();
});
