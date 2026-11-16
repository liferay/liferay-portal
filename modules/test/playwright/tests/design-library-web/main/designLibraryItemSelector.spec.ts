/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import getRandomString from '../../../utils/getRandomString';
import {designLibrariesPageTest} from './fixtures/designLibrariesPageTest';
import {DesignLibrariesPage} from './pages/DesignLibrariesPage';

const test = mergeTests(
	dataApiHelpersTest,
	designLibrariesPageTest,
	featureFlagsTest({
		'LPD-57283': {enabled: true},
		'LPD-81022': {enabled: true},
	}),
	loginTest(),
	pageEditorPagesTest
);

const testWithoutGroupSelector = mergeTests(
	dataApiHelpersTest,
	designLibrariesPageTest,
	featureFlagsTest({
		'LPD-57283': {enabled: true},
		'LPD-81022': {enabled: false},
	}),
	loginTest(),
	pageEditorPagesTest
);

async function openDesignLibraryImageSelector({
	apiHelpers,
	designLibrariesPage,
	pageEditorPage,
}: {
	apiHelpers: ApiHelpers;
	designLibrariesPage: DesignLibrariesPage;
	pageEditorPage: PageEditorPage;
}) {
	const designLibrary =
		await apiHelpers.headlessAssetLibrary.createDesignLibrary();
	const layoutPageTemplateCollectionName = getRandomString();

	const layoutPageTemplateCollection =
		await apiHelpers.jsonWebServicesLayoutPageTemplateCollection.addLayoutPageTemplateCollection(
			{
				groupId: String(designLibrary.siteId),
				name: layoutPageTemplateCollectionName,
			}
		);

	const layoutPageTemplateEntryName = getRandomString();

	await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
		{
			groupId: String(designLibrary.siteId),
			layoutPageTemplateCollectionId:
				layoutPageTemplateCollection.layoutPageTemplateCollectionId,
			name: layoutPageTemplateEntryName,
		}
	);

	await designLibrariesPage.goToPageTemplate(
		designLibrary.name,
		layoutPageTemplateCollectionName,
		layoutPageTemplateEntryName
	);

	await pageEditorPage.addFragment('Basic Components', 'Image');

	return {
		designLibraryName: designLibrary.name,
		itemSelector: await pageEditorPage.openImageSelector(
			await pageEditorPage.getFragmentId('Image')
		),
	};
}

test(
	'The image selector of a design library shows the group selector',
	{tag: '@LPD-115381'},
	async ({apiHelpers, designLibrariesPage, pageEditorPage}) => {
		const {itemSelector} = await openDesignLibraryImageSelector({
			apiHelpers,
			designLibrariesPage,
			pageEditorPage,
		});

		await expect(
			itemSelector.getByText('Sites and Libraries')
		).toBeVisible();

		for (const groupType of [
			'Asset Libraries',
			'Design Libraries',
			'Sites',
		]) {
			await expect(
				itemSelector.getByText(groupType, {exact: true})
			).toBeVisible();
		}

		await expect(
			itemSelector.getByText(
				'This application is disabled for this scope.'
			)
		).toBeHidden();
	}
);

test(
	'The image selector shows the disabled application message when a design library is selected',
	{tag: '@LPD-115381'},
	async ({apiHelpers, designLibrariesPage, pageEditorPage}) => {
		const {designLibraryName, itemSelector} =
			await openDesignLibraryImageSelector({
				apiHelpers,
				designLibrariesPage,
				pageEditorPage,
			});

		// Select the design library

		await itemSelector.getByText('Design Libraries', {exact: true}).click();

		await itemSelector.getByText(designLibraryName, {exact: true}).click();

		// Check the message

		await expect(
			itemSelector.getByText(
				'This application is disabled for this scope.'
			)
		).toBeVisible();
	}
);

testWithoutGroupSelector(
	'The image selector of a design library shows the disabled application message',
	{tag: '@LPD-115381'},
	async ({apiHelpers, designLibrariesPage, pageEditorPage}) => {
		const {itemSelector} = await openDesignLibraryImageSelector({
			apiHelpers,
			designLibrariesPage,
			pageEditorPage,
		});

		await expect(
			itemSelector.getByText(
				'This application is disabled for this scope.'
			)
		).toBeVisible();

		await expect(
			itemSelector.getByRole('link', {name: 'go back to scope selection'})
		).toBeVisible();

		await expect(
			itemSelector.getByText('Sites and Libraries')
		).toBeHidden();
	}
);
