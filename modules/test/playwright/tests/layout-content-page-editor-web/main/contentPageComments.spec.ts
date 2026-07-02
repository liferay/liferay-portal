/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../utils/getRandomString';
import getFragmentDefinition from './utils/getFragmentDefinition';
import getPageDefinition from './utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

test(
	'Views fragment comments added via the sidebar, with escape characters, and via the fragment topper',
	{tag: '@LPD-96910'},
	async ({apiHelpers, pageEditorPage, site}) => {

		// Create a page with two fragments and go to edit mode

		const firstFragmentId = getRandomString();
		const secondFragmentId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: firstFragmentId,
					key: 'BASIC_COMPONENT-heading',
				}),
				getFragmentDefinition({
					id: secondFragmentId,
					key: 'BASIC_COMPONENT-paragraph',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Add a comment from the Comments sidebar and publish

		const sidebarComment = 'This is a fragment comment!';

		await pageEditorPage.addFragmentComment(
			firstFragmentId,
			sidebarComment
		);

		await pageEditorPage.publishPage();

		// Reopen the page and check the comment persists

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToFragmentComment(firstFragmentId);

		await pageEditorPage.viewFragmentComment(sidebarComment);

		// Add a comment with escape characters from the sidebar

		const escapeCharactersComment = '& < > " Escape Characters Comment';

		await pageEditorPage.addFragmentComment(
			secondFragmentId,
			escapeCharactersComment
		);

		await pageEditorPage.viewFragmentComment(escapeCharactersComment);

		// Add a comment via the fragment topper comment icon

		const topperComment = 'This is a fragment topper comment!';

		await pageEditorPage.addFragmentCommentViaTopper(
			firstFragmentId,
			topperComment
		);

		await pageEditorPage.viewFragmentComment(topperComment);
	}
);

test(
	'Deletes a fragment comment after publishing',
	{tag: ['@LPD-96910', '@LPS-106776']},
	async ({apiHelpers, pageEditorPage, site}) => {

		// Create a page with a fragment and go to edit mode

		const fragmentId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: fragmentId,
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Add a comment and publish

		const comment = 'This is a fragment comment.';

		await pageEditorPage.addFragmentComment(fragmentId, comment);

		await pageEditorPage.publishPage();

		// Reopen the page, reply to the comment, then delete it

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToFragmentComment(fragmentId);

		await pageEditorPage.viewFragmentComment(comment);

		const reply = 'Fragment Comment.';

		await pageEditorPage.replyToFragmentComment(comment, reply);

		await pageEditorPage.viewFragmentCommentReply(reply, 'Test Test');

		await pageEditorPage.deleteFragmentComment(comment);
	}
);

test(
	'Reopens a resolved comment after publishing',
	{tag: '@LPD-96910'},
	async ({apiHelpers, pageEditorPage, site}) => {

		// Create a page with a fragment and go to edit mode

		const fragmentId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: fragmentId,
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Add a comment, reply to it, resolve it, then publish

		const comment = 'This is a fragment comment';

		await pageEditorPage.addFragmentComment(fragmentId, comment);

		await pageEditorPage.replyToFragmentComment(
			comment,
			'Fragment Comment 1'
		);

		await pageEditorPage.resolveFragmentComment(comment);

		await pageEditorPage.publishPage();

		// Reopen the page, reopen the resolved comment, then reply again

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToFragmentComment(fragmentId);

		await pageEditorPage.reopenResolvedFragmentComment(comment);

		await pageEditorPage.replyToFragmentComment(
			comment,
			'Fragment Comment 2'
		);

		await pageEditorPage.viewFragmentCommentReply(
			'Fragment Comment 2',
			'Test Test'
		);
	}
);

test(
	'Views an edited fragment comment after publishing',
	{tag: '@LPD-96910'},
	async ({apiHelpers, pageEditorPage, site}) => {

		// Create a page with a fragment and go to edit mode

		const fragmentId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: fragmentId,
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Add a comment, edit it, then publish

		const editedComment = 'This is a fragment comment edited.';

		await pageEditorPage.addFragmentComment(
			fragmentId,
			'This is a fragment comment.'
		);

		await pageEditorPage.editFragmentComment(
			'This is a fragment comment.',
			editedComment
		);

		await pageEditorPage.publishPage();

		// Reopen the page and check the edited comment

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.goToFragmentComment(fragmentId);

		await pageEditorPage.viewFragmentComment(editedComment);
	}
);

test(
	'Views comments in the comment list and its empty state',
	{tag: '@LPD-96910'},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page with two fragments and go to edit mode

		const headingId = getRandomString();
		const paragraphId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: headingId,
					key: 'BASIC_COMPONENT-heading',
				}),
				getFragmentDefinition({
					id: paragraphId,
					key: 'BASIC_COMPONENT-paragraph',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Check the empty state when no comments exist

		await pageEditorPage.goToSidebarTab('Comments');

		await expect(
			page.getByText('There are no comments yet.')
		).toBeVisible();
		await expect(
			page.getByText('Select a fragment to add a comment.')
		).toBeVisible();

		// Add a comment to the first fragment and view it in the list

		const headingComment = 'Heading fragment comment';

		await pageEditorPage.addFragmentComment(headingId, headingComment);

		await pageEditorPage.goToCommentList();

		await pageEditorPage.viewCommentList({
			commentCount: '1 Comment',
			fragmentName: 'Heading',
			openComment: true,
		});

		await pageEditorPage.viewFragmentComment(headingComment);

		// Add a comment to the second fragment and view both in the list

		const paragraphComment = 'Paragraph fragment comment';

		await pageEditorPage.addFragmentComment(paragraphId, paragraphComment);

		await pageEditorPage.goToCommentList();

		await pageEditorPage.viewCommentList({
			commentCount: '1 Comment',
			fragmentName: 'Heading',
		});

		await pageEditorPage.viewCommentList({
			commentCount: '1 Comment',
			fragmentName: 'Paragraph',
			openComment: true,
		});

		await pageEditorPage.viewFragmentComment(paragraphComment);
	}
);

test(
	'Views multiple comments in the fragment comment list',
	{tag: '@LPD-96910'},
	async ({apiHelpers, pageEditorPage, site}) => {

		// Create a page with a fragment and go to edit mode

		const fragmentId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: fragmentId,
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Add three comments to the same fragment

		const comments = [
			'Fragment comment 1',
			'Fragment comment 2',
			'Fragment comment 3',
		];

		for (const comment of comments) {
			await pageEditorPage.addFragmentComment(fragmentId, comment);
		}

		// View the three comments in the fragment comment list

		await pageEditorPage.goToCommentList();

		await pageEditorPage.viewCommentList({
			commentCount: '3 Comments',
			fragmentName: 'Heading',
			openComment: true,
		});

		for (const comment of comments) {
			await pageEditorPage.viewFragmentComment(comment);
		}
	}
);

