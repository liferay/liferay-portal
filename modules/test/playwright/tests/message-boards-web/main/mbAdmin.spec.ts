/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {messageBoardsPagesTest} from '../../../fixtures/messageBoardsTest';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitchViaApi, userData} from '../../../utils/performLogin';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-105225': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	messageBoardsPagesTest
);

const TERMS = [
	{
		description: 'The message body of the parent message',
		token: '[$MESSAGE_PARENT$]',
	},
	{
		description: 'The message thread of messages at the same level',
		token: '[$MESSAGE_SIBLINGS$]',
	},
	{
		description: 'The message body of the original message',
		token: '[$ROOT_MESSAGE_BODY$]',
	},
];

test('Message parent, sibling, and root body terms are defined in the default email templates', async ({
	messageBoardsPage,
	page,
	site,
}) => {
	for (const tabName of ['Message Added Email', 'Message Updated Email']) {
		await messageBoardsPage.goToConfigurationTab(
			tabName,
			site.friendlyUrlPath
		);

		// The default template body references the new terms

		for (const {token} of TERMS) {
			await expect(page.getByText(token).first()).toBeVisible();
		}

		// The definition of terms documents each new term

		for (const {description} of TERMS) {
			await expect(page.getByText(description)).toBeAttached();
		}
	}
});

test(
	'A banned user can be unbanned',
	{tag: '@LPS-136923'},
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

		// The banned member is listed under banned users

		await messageBoardsPage.goToBannedUsers(site.friendlyUrlPath);

		await expect(
			page.getByText(`${member.givenName} ${member.familyName}`)
		).toBeVisible();

		// The member can be unbanned

		await messageBoardsPage.unbanUser();

		await expect(
			page.getByText('There are no banned users.')
		).toBeVisible();
	}
);
