/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {collectionsPagesTest} from '../../fixtures/collectionsPagesTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {loginTest} from '../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../fixtures/pageManagementSiteTest';
import getRandomString from '../../utils/getRandomString';
import {waitForAlert} from '../../utils/waitForAlert';
import {journalPagesTest} from '../journal-web/fixtures/journalPagesTest';
import {ANIMALS_COLLECTION_NAME} from '../setup/page-management-site/constants';
import getCollectionDefinition from './utils/getCollectionDefinition';
import getFragmentDefinition from './utils/getFragmentDefinition';
import getPageDefinition from './utils/getPageDefinition';

const FRAGMENT_FIELDS = [
	{
		id: 'element-text',
		value: {
			text: {
				mapping: {
					fieldKey: 'JournalArticle_title',
					itemReference: {
						contextSource: 'CollectionItem',
					},
				},
			},
		},
	},
];

const test = mergeTests(
	apiHelpersTest,
	collectionsPagesTest,
	featureFlagsTest({
		'LPD-15596': true,
		'LPS-178052': true,
	}),
	journalPagesTest,
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest
);

test(
	'Page creator can perform actions on collection displayed in collection display via page content panel',
	{
		tag: '@LPS-125985',
	},
	async ({
		apiHelpers,
		collectionsPage,
		journalEditArticlePage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create definition for a collection mapped to Animals collection

		const animalsClassPK = await collectionsPage.getCollectionClassPK(
			ANIMALS_COLLECTION_NAME,
			pageManagementSite.friendlyUrlPath
		);

		const collectionDefinition = getCollectionDefinition({
			classPK: animalsClassPK,
			id: getRandomString(),
			pageElements: [
				getFragmentDefinition({
					fragmentFields: FRAGMENT_FIELDS,
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			],
		});

		// Create a content page and go to edit mode

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([collectionDefinition]),
			siteId: pageManagementSite.id,
			title: layoutTitle,
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Go to page contents panel and click in edit

		await pageEditorPage.clickPageContentContentAction(
			'Edit',
			ANIMALS_COLLECTION_NAME
		);

		await expect(
			page.getByRole('heading', {name: ANIMALS_COLLECTION_NAME})
		).toBeVisible();

		await page.getByTitle(`Go to ${layoutTitle}`).click();

		// Go to page contents panel and click in view items

		await pageEditorPage.clickPageContentContentAction(
			'View Items',
			ANIMALS_COLLECTION_NAME
		);

		await expect(
			page.getByRole('heading', {name: 'View Items'})
		).toBeVisible();

		await expect(
			page.getByText('Animal 01 - Dogs and Cats categories')
		).toBeVisible();
		await expect(page.getByText('Animal 02 - Dogs category')).toBeVisible();

		await page.getByRole('dialog').getByLabel('close').click();

		// Go to page contents panel, click in add items and add a new item

		await pageEditorPage.clickPageContentContentAction(
			'Add Items',
			ANIMALS_COLLECTION_NAME,
			'Animal'
		);

		const articleTitle = 'Animal 03 - Elephant';

		await journalEditArticlePage.fillTitle(articleTitle);
		await journalEditArticlePage.publishArticle();

		await expect(page.getByText(articleTitle)).toBeVisible();

		// Go to page contents panel and click in permissions

		await pageEditorPage.clickPageContentContentAction(
			'Permissions',
			ANIMALS_COLLECTION_NAME
		);

		const permissionsFrame = page.frameLocator(
			'iframe[title="Permissions"]'
		);

		const guestActionViewCheckBox =
			permissionsFrame.locator('#guest_ACTION_VIEW');

		await guestActionViewCheckBox.uncheck({trial: true});

		await guestActionViewCheckBox.uncheck();

		await permissionsFrame
			.getByRole('button', {exact: true, name: 'Save'})
			.click();

		await waitForAlert(permissionsFrame);

		await expect(guestActionViewCheckBox).not.toBeChecked();
	}
);

test(
	'View collection, mapped content and mapped content via content display in page content panel',
	{
		tag: '@LPS-125985',
	},
	async ({
		apiHelpers,
		collectionsPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {

		// Create definition for a collection mapped to Animals collection

		const animalsClassPK = await collectionsPage.getCollectionClassPK(
			ANIMALS_COLLECTION_NAME,
			pageManagementSite.friendlyUrlPath
		);

		const collectionId = getRandomString();

		const collectionDefinition = getCollectionDefinition({
			classPK: animalsClassPK,
			id: collectionId,
			pageElements: [
				getFragmentDefinition({
					fragmentFields: FRAGMENT_FIELDS,
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			],
		});

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

		// Create definition for a heading fragment

		const headingId = getRandomString();

		const headingDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		// Create a content page and go to edit mode

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				collectionDefinition,
				contentDisplayDefinition,
				headingDefinition,
			]),
			siteId: pageManagementSite.id,
			title: layoutTitle,
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Map the content display fragment to the created web content

		await pageEditorPage.selectFragment(contentDisplayId);

		await pageEditorPage.setMappedItem({
			entity: 'Web Content',
			entry: 'Animal 01 - Dogs and Cats categories',
			folder: 'Animals',
		});

		// Map the heading fragment to the created web content

		await pageEditorPage.selectEditable(headingId, 'element-text');

		await pageEditorPage.setMappingConfiguration({
			mapping: {
				entity: 'Web Content',
				entry: 'Animal 02 - Dogs category',
				field: 'Title',
				folder: 'Animals',
			},
		});

		// Check collection, mapped content and mapped content via content display are visible in page content panel

		await pageEditorPage.goToSidebarTab('Page Content');

		const panel = page.getByLabel('Page Content Panel', {
			exact: true,
		});

		await expect(
			panel.getByText(ANIMALS_COLLECTION_NAME, {exact: true})
		).toBeVisible();

		await expect(
			panel.getByText('Animal 01 - Dogs and Cats categories', {
				exact: true,
			})
		).toBeVisible();

		await expect(
			panel.getByText('Animal 02 - Dogs category', {exact: true})
		).toBeVisible();

		await expect(
			panel.getByText('There is no content on this page.')
		).not.toBeVisible();

		// Removes collection, content display and heading fragment

		await pageEditorPage.removeFragment(collectionId);

		await pageEditorPage.removeFragment(contentDisplayId);

		await pageEditorPage.removeFragment(headingId);

		// Check collection, mapped content and mapped content via content display are not visible in page content panel

		await pageEditorPage.goToSidebarTab('Page Content');

		await expect(
			panel.getByText(ANIMALS_COLLECTION_NAME, {exact: true})
		).not.toBeVisible();

		await expect(
			panel.getByText('Animal 01 - Dogs and Cats categories', {
				exact: true,
			})
		).not.toBeVisible();

		await expect(
			panel.getByText('Animal 02 - Dogs category', {exact: true})
		).not.toBeVisible();

		await expect(
			panel.getByText('There is no content on this page.')
		).toBeVisible();
	}
);
