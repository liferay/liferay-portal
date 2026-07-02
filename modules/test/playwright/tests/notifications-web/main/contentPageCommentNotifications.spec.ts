/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import createUserWithPermissions from '../../../utils/createUserWithPermissions';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch} from '../../../utils/performLogin';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

async function createPageEditorUser(apiHelpers: ApiHelpers) {
	const company =
		await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
			'liferay.com'
		);

	return createUserWithPermissions({
		apiHelpers,
		rolePermissions: [
			{
				actionIds: ['UPDATE'],
				primaryKey: company.companyId,
				resourceName: 'com.liferay.portal.kernel.model.Layout',
				scope: 1,
			},
		],
	});
}

async function goToNotificationContent(page: Page, body: string) {
	await page.getByText(body, {exact: true}).click();
}

async function viewCommentNotification(
	page: Page,
	{
		author,
		body,
		fragmentName,
		pageTitle,
	}: {
		author: string;
		body: string;
		fragmentName: string;
		pageTitle: string;
	}
) {
	const notificationTitle = page.getByText(
		`${author} added a new comment to ${fragmentName} in the "${pageTitle}" page.`
	);

	// The notification is delivered asynchronously, so reopen the panel until
	// it surfaces. The page title is unique, so the title identifies this
	// notification among any others the recipient already has.

	await expect(async () => {
		await page.reload();

		await page.locator('a.panel-notifications-count').click();

		await expect(notificationTitle).toBeVisible({timeout: 5000});
	}).toPass();

	await expect(page.getByText(body, {exact: true})).toBeVisible();
}

test(
	'Notifies the page creator when another user comments on a fragment',
	{tag: '@LPD-96910'},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page with a fragment as the first user

		const fragmentId = getRandomString();
		const pageTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: fragmentId,
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: pageTitle,
		});

		// A second user comments on the fragment

		const user = await createPageEditorUser(apiHelpers);

		await performUserSwitch(page, user.alternateName);

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		const comment = getRandomString();

		await pageEditorPage.addFragmentComment(fragmentId, comment);

		// The first user is notified and navigates to the comment

		await performUserSwitch(page, 'test');

		await viewCommentNotification(page, {
			author: `${user.givenName} ${user.familyName}`,
			body: comment,
			fragmentName: 'Heading',
			pageTitle,
		});

		await goToNotificationContent(page, comment);

		await pageEditorPage.viewFragmentComment(comment);
	}
);

test(
	'Notifies a commenter when another user replies to their comment',
	{tag: '@LPD-96910'},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page with a fragment as the first user

		const fragmentId = getRandomString();
		const pageTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: fragmentId,
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: pageTitle,
		});

		// The first user adds a comment

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		const comment = getRandomString();

		await pageEditorPage.addFragmentComment(fragmentId, comment);

		// A second user replies to the comment

		const user = await createPageEditorUser(apiHelpers);

		await performUserSwitch(page, user.alternateName);

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToFragmentComment(fragmentId);

		const reply = getRandomString();

		await pageEditorPage.replyToFragmentComment(comment, reply);

		// The first user is notified of the reply and navigates to it

		await performUserSwitch(page, 'test');

		await viewCommentNotification(page, {
			author: `${user.givenName} ${user.familyName}`,
			body: reply,
			fragmentName: 'Heading',
			pageTitle,
		});

		await goToNotificationContent(page, reply);

		await pageEditorPage.viewFragmentCommentReply(
			reply,
			`${user.givenName} ${user.familyName}`
		);
	}
);

test(
	'Notifies a user who replied when another user replies to the same comment',
	{tag: ['@LPD-96910', '@LPS-101493']},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page with a fragment as the first user

		const fragmentId = getRandomString();
		const pageTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: fragmentId,
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: pageTitle,
		});

		// The first user adds a comment

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		const comment = getRandomString();

		await pageEditorPage.addFragmentComment(fragmentId, comment);

		// A second user replies to the comment

		const user = await createPageEditorUser(apiHelpers);

		await performUserSwitch(page, user.alternateName);

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToFragmentComment(fragmentId);

		await pageEditorPage.replyToFragmentComment(comment, getRandomString());

		// The first user also replies to the comment

		await performUserSwitch(page, 'test');

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToFragmentComment(fragmentId);

		const reply = getRandomString();

		await pageEditorPage.replyToFragmentComment(comment, reply);

		// The second user is notified of the first user's reply

		await performUserSwitch(page, user.alternateName);

		await viewCommentNotification(page, {
			author: 'Test Test',
			body: reply,
			fragmentName: 'Heading',
			pageTitle,
		});

		await goToNotificationContent(page, reply);

		await pageEditorPage.viewFragmentCommentReply(reply, 'Test Test');
	}
);
