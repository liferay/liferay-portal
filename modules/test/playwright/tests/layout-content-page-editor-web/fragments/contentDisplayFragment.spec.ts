/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId, {
	getWebContentStructureId,
} from '../../../utils/structured-content/getBasicWebContentStructureId';
import {
	ANIMAL_DDM_STRUCTURE_KEY,
	ANIMAL_DDM_TEMPLATE_KEY,
} from '../../setup/page-management-site/main/constants/animals';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-17564': {enabled: true},
		'LPD-39304': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest
);

const CONTENT_DISPLAY_FRAGMENT_HTML = `<div class="content-display-fragment">
		[#if itemSelectorNameObject??]
			\${itemSelectorNameObject.getTitle()}
		[#else]
			<div class="portlet-msg-info">The selected content will be shown here.</div>
		[/#if]
	</div>`;

function getContentDisplayFragmentConfiguration({
	itemSubtype,
	itemType,
}: {
	itemSubtype?: string;
	itemType: string;
}): FragmentConfiguration {
	return {
		fieldSets: [
			{
				fields: [
					{
						label: 'Item',
						name: 'itemSelectorName',
						type: 'itemSelector',
						typeOptions: {
							enableSelectTemplate: true,
							itemSubtype,
							itemType,
						},
					},
				],
			},
		],
	};
}

test('Does not show alert when accessing a page with a web content display mapped to a restricted web content', async ({
	apiHelpers,
	browser,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create bird web content

	const birdWebContentTitle = getRandomString();

	const birdWebContentStructureId = await getWebContentStructureId(
		apiHelpers,
		pageManagementSite.id,
		ANIMAL_DDM_STRUCTURE_KEY
	);

	await apiHelpers.jsonWebServicesJournal.addWebContent({
		ddmStructureId: birdWebContentStructureId,
		ddmTemplateKey: ANIMAL_DDM_TEMPLATE_KEY,
		groupId: pageManagementSite.id,
		titleMap: {en_US: birdWebContentTitle},
	});

	// Create a page with a content display fragment and go to edit mode

	const contentDisplayId = getRandomString();

	const contentDisplayDefinition = getFragmentDefinition({
		fragmentConfig: {
			itemSelector: {
				template: {
					infoItemRendererKey:
						'com.liferay.journal.web.internal.info.item.renderer.JournalArticleFullContentInfoItemRenderer',
				},
			},
		},
		id: contentDisplayId,
		key: 'com.liferay.fragment.internal.renderer.ContentObjectFragmentRenderer',
	});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([contentDisplayDefinition]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Map the content display fragment to the created web content and publish the page

	await pageEditorPage.selectFragment(contentDisplayId);

	await pageEditorPage.setMappedItem({
		entity: 'Web Content',
		entry: 'Hummingbird',
		folder: 'Birds',
	});

	await expect(
		page.locator('#page-editor').getByText('Hummingbird', {exact: true})
	).toBeVisible();

	await pageEditorPage.publishPage();

	// Navigate to page in incognito mode to simulate not logged user

	const context = await browser.newContext();

	const incognitoPage = await context.newPage();

	await incognitoPage.goto(
		`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
	);

	// Check the content is not displayed and no alert is shown

	await expect(incognitoPage.getByText('Hummingbird')).not.toBeVisible();
	await expect(incognitoPage.getByRole('alert')).not.toBeVisible();
});

test(
	'Can only select Documents and Media when set itemType to FileEntry',
	{
		tag: ['@LPS-97182', '@LPS-100545', '@LPS-101249'],
	},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a fragment with itemSelector configuration for file entries

		const {fragmentCollectionId} =
			await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
				{
					groupId: pageManagementSite.id,
					name: getRandomString(),
				}
			);

		const fragmentEntryName = getRandomString();

		await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
			configuration: getContentDisplayFragmentConfiguration({
				itemType:
					'com.liferay.portal.kernel.repository.model.FileEntry',
			}),
			fragmentCollectionId,
			groupId: pageManagementSite.id,
			html: CONTENT_DISPLAY_FRAGMENT_HTML,
			name: fragmentEntryName,
		});

		// Create a content page with created fragment

		const fragmentName = getRandomString();

		const fragmentDefinition = getFragmentDefinition({
			id: fragmentName,
			key: fragmentEntryName,
		});

		// Create a content page and go to edit mode

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([fragmentDefinition]),
			siteId: pageManagementSite.id,
			title: layoutTitle,
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Assert only documents and media is shown

		await pageEditorPage.selectFragment(fragmentName);

		await pageEditorPage.openMappingSelector();

		const iframe = page.frameLocator('iframe[title="Select"]');

		await expect(iframe.getByRole('menubar')).not.toBeVisible();

		await expect(
			iframe.getByTitle('poodle.jpg', {exact: true})
		).toBeVisible();

		await page
			.getByRole('dialog')
			.getByLabel('Close', {exact: true})
			.click();

		// Select specific document and media file

		await pageEditorPage.setMappedItem({
			entity: 'Documents and Media',
			entry: 'poodle.jpg',
			entryLocator: page
				.frameLocator('iframe[title="Select"]')
				.getByText('poodle.jpg', {exact: false}),
		});

		await expect(
			page.locator('.content-display-fragment').getByText('poodle.jpg')
		).toBeVisible();

		// Remove the page

		await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.id);

		// Remove fragment set

		await apiHelpers.jsonWebServicesFragmentCollection.deleteFragmentCollection(
			fragmentCollectionId
		);
	}
);

test(
	'Can only select web content of type animal when set itemSubtype to animal',
	{
		tag: ['@LPS-97182', '@LPS-100545', '@LPS-101249'],
	},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create animal web content

		const animalWebContentTitle = getRandomString();

		const animalWebContentStructureId = await getWebContentStructureId(
			apiHelpers,
			pageManagementSite.id,
			ANIMAL_DDM_STRUCTURE_KEY
		);

		const {articleId: animalWebContentId} =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: animalWebContentStructureId,
				ddmTemplateKey: ANIMAL_DDM_TEMPLATE_KEY,
				groupId: pageManagementSite.id,
				titleMap: {en_US: animalWebContentTitle},
			});

		// Create basic web content

		const basicWebContentTitle = getRandomString();

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const {articleId: basicWebContentId} =
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: basicWebContentStructureId,
				groupId: pageManagementSite.id,
				titleMap: {en_US: basicWebContentTitle},
			});

		// Create a fragment with itemSelector configuration for animals

		const {fragmentCollectionId} =
			await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
				{
					groupId: pageManagementSite.id,
					name: getRandomString(),
				}
			);

		const fragmentEntryName = getRandomString();

		await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
			configuration: getContentDisplayFragmentConfiguration({
				itemSubtype: ANIMAL_DDM_STRUCTURE_KEY,
				itemType: 'com.liferay.journal.model.JournalArticle',
			}),
			fragmentCollectionId,
			groupId: pageManagementSite.id,
			html: CONTENT_DISPLAY_FRAGMENT_HTML,
			name: fragmentEntryName,
		});

		// Create a content page with created fragment

		const fragmentName = getRandomString();

		const fragmentDefinition = getFragmentDefinition({
			id: fragmentName,
			key: fragmentEntryName,
		});

		// Create a content page and go to edit mode

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([fragmentDefinition]),
			siteId: pageManagementSite.id,
			title: layoutTitle,
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Assert basic web content is not showed and animal is showed

		await pageEditorPage.selectFragment(fragmentName);

		await pageEditorPage.openMappingSelector();

		const iframe = page.frameLocator('iframe[title="Select"]');

		await expect(iframe.getByRole('menubar')).not.toBeVisible();

		await expect(
			iframe
				.getByRole('paragraph')
				.filter({hasText: animalWebContentTitle})
		).toBeVisible();

		await expect(
			iframe
				.getByRole('paragraph')
				.filter({hasText: basicWebContentTitle})
		).not.toBeVisible();

		await page
			.getByRole('dialog')
			.getByLabel('Close', {exact: true})
			.click();

		// Select animal

		await pageEditorPage.setMappedItem({
			entity: 'Web Content',
			entry: animalWebContentTitle,
		});

		await expect(page.locator('.content-display-fragment')).toHaveText(
			animalWebContentTitle
		);

		await pageEditorPage.publishPage();

		// Go to view mode of the created page

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(page.locator('.content-display-fragment')).toHaveText(
			animalWebContentTitle
		);

		// Remove web contents

		expect(
			await apiHelpers.jsonWebServicesJournal.moveArticleToTrash(
				pageManagementSite.id,
				animalWebContentId
			)
		).toHaveProperty('articleId');

		expect(
			await apiHelpers.jsonWebServicesJournal.moveArticleToTrash(
				pageManagementSite.id,
				basicWebContentId
			)
		).toHaveProperty('articleId');

		// Remove the page

		await apiHelpers.jsonWebServicesLayout.deleteLayout(layout.id);

		// Remove fragment set

		await apiHelpers.jsonWebServicesFragmentCollection.deleteFragmentCollection(
			fragmentCollectionId
		);
	}
);
