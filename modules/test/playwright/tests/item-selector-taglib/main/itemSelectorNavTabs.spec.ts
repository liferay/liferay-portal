/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {blogsPagesTest} from '../../blogs-web/main/fixtures/blogsPagesTest';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';

const SAMPLE_IMAGE = path.join(
	__dirname,
	'../../frontend-js-item-selector-web/main/dependencies/sample_image.png'
);

const test = mergeTests(
	apiHelpersTest,
	blogsPagesTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest()
);

// The Content Display fragment and its item selector are gated behind the
// layout page editor feature flag.

const pageEditorTest = mergeTests(
	test,
	featureFlagsTest({'LPS-178052': {enabled: true}}),
	pageEditorPagesTest
);

test(
	'Blog Images tab has no scope filter while Documents and Media does',
	{tag: '@LPS-119709'},
	async ({apiHelpers, blogsEditBlogEntryPage, page, site}) => {
		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(SAMPLE_IMAGE),
			{
				documentFolderId: 0,
				fileName: `${getRandomString()}.png`,
				title: 'Document 1',
			}
		);

		// Open the blog cover image selector, which lands on the Blog Images tab

		await blogsEditBlogEntryPage.goto(site.friendlyUrlPath);

		await page.getByRole('button', {name: 'Select File'}).first().click();

		const iframe = page.frameLocator('iframe[title="Select File"]');

		const filter = iframe.getByRole('button', {
			exact: true,
			name: 'Filter',
		});

		// The Blog Images tab does not offer a scope filter

		await expect(iframe.getByText('Sites and Libraries')).toBeHidden();

		await expect(filter).toBeHidden();

		// The Documents and Media tab does offer the scope filter

		await iframe.getByRole('link', {name: 'Documents and Media'}).click();

		await expect(filter).toBeVisible();
	}
);

test(
	'Add menu offers video shortcuts for a document field but not for an image field',
	{tag: ['@LPS-136945', '@LPS-136943']},
	async ({apiHelpers, journalEditArticlePage, page, site}) => {
		const structureName = `Structure ${getRandomString()}`;

		await apiHelpers.dataEngine.createStructure(
			site.id,
			getDataStructureDefinition({
				defaultLanguageId: 'en_US',
				fields: [
					{
						fieldType: 'document_library',
						name: 'Upload',
					},
				],
				name: structureName,
			})
		);

		// A document field offers File Upload, Folder, and External Video
		// Shortcut

		await journalEditArticlePage.goto({
			siteUrl: site.friendlyUrlPath,
			structureName,
		});

		await page.getByLabel('File', {exact: true}).click();

		const documentIframe = page.frameLocator(
			'iframe[title="Select Document"]'
		);

		await documentIframe
			.getByRole('link', {name: 'Documents and Media'})
			.click();

		await clickAndExpectToBeVisible({
			target: documentIframe.getByRole('menuitem', {name: 'File Upload'}),
			trigger: documentIframe.getByRole('button', {name: 'New'}),
		});

		await expect(
			documentIframe.getByRole('menuitem', {exact: true, name: 'Folder'})
		).toBeVisible();

		await expect(
			documentIframe.getByRole('menuitem', {
				name: 'External Video Shortcut',
			})
		).toBeVisible();

		// An image field offers only File Upload and Folder

		await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

		await page.getByLabel('Image', {exact: true}).click();

		const imageIframe = page.frameLocator('iframe[title="Select Item"]');

		await clickAndExpectToBeVisible({
			target: imageIframe.getByRole('menuitem', {name: 'File Upload'}),
			trigger: imageIframe.getByRole('button', {name: 'New'}),
		});

		await expect(
			imageIframe.getByRole('menuitem', {exact: true, name: 'Folder'})
		).toBeVisible();

		await expect(
			imageIframe.getByRole('menuitem', {name: 'External Video Shortcut'})
		).toBeHidden();

		await expect(
			imageIframe.getByRole('menuitem', {name: 'Google Drive Shortcut'})
		).toBeHidden();
	}
);

pageEditorTest(
	'Item selector shows a vertical navigation when it has more than five tabs',
	async ({apiHelpers, page, pageEditorPage, site}) => {
		const contentDisplayId = getRandomString();

		// Create a content page with a Content Display fragment, whose item
		// selector exposes every asset type as a navigation tab

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
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
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Open the Content Display item selector

		await pageEditorPage.selectFragment(contentDisplayId);

		await pageEditorPage.openMappingSelector();

		// With more than five tabs, the navigation renders as a vertical
		// menubar rather than as horizontal tabs

		await expect(
			page
				.frameLocator('iframe[title="Select"]')
				.locator('nav[class*="menubar-vertical"]')
		).toBeVisible();
	}
);
