/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

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

