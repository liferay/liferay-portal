/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers} from '../helpers/ApiHelpers';
import {PageEditorPage} from '../pages/layout-content-page-editor-web/PageEditorPage';
import getRandomString from './getRandomString';

/**
 * Creates a NON-cacheable custom fragment (the OOTB Basic Component fragments
 * are cacheable, so their mapped output is served stale until a re-publish),
 * adds it to a fresh content page, and opens the page editor on it.
 *
 * The fragment's `html` should declare the editables to map, e.g.
 * `<lfr-editable id="title" type="text">Title</lfr-editable>`. The caller maps
 * each editable with `pageEditorPage.selectEditable(fragmentId, '<id>')` plus
 * `setMappingConfiguration(...)`, then publishes.
 *
 * Returns the in-editor `fragmentId`, the created `layout`, and the public
 * `viewUrl` of the page.
 */
export async function addMappingFragment({
	apiHelpers,
	html,
	pageEditorPage,
	site,
}: {
	apiHelpers: ApiHelpers;
	html: string;
	pageEditorPage: PageEditorPage;
	site: Site;
}): Promise<{fragmentId: string; layout: Layout; viewUrl: string}> {
	const fragmentCollectionName = `Collection ${getRandomString()}`;
	const fragmentName = `Mapped ${getRandomString()}`;

	const {fragmentCollectionId} =
		await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
			{
				groupId: site.id,
				name: fragmentCollectionName,
			}
		);

	await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
		fragmentCollectionId,
		groupId: site.id,
		html,
		name: fragmentName,
		type: 'component',
	});

	const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: site.id,
		options: {type: 'content'},
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.addFragment(fragmentCollectionName, fragmentName);

	const fragmentId = await pageEditorPage.getFragmentId(fragmentName);

	return {
		fragmentId,
		layout,
		viewUrl: `/web${site.friendlyUrlPath}${layout.friendlyURL}`,
	};
}
