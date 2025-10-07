/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';

export const test = mergeTests(apiHelpersTest, changeTrackingPagesTest);

let basicWebContentStructureId;
let blogDisplayPage;
let blogsEntryClassName;
let journalArticleClassName;
let journalDisplayPage;
let site;

test.beforeEach(
	'Add Display Page Templates for Blogs and Journal Article',
	async ({apiHelpers}) => {
		site =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
				'guest'
			);

		blogsEntryClassName =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				'com.liferay.blogs.model.BlogsEntry'
			);

		journalArticleClassName =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				'com.liferay.journal.model.JournalArticle'
			);

		blogDisplayPage =
			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
				{
					classNameId: blogsEntryClassName.classNameId,
					groupId: site.id,
					name: getRandomString(),
					type: 'display-page',
				}
			);

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
			{
				layoutPageTemplateEntryId:
					blogDisplayPage.layoutPageTemplateEntryId,
			}
		);

		basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		journalDisplayPage =
			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
				{
					classNameId: journalArticleClassName.classNameId,
					classTypeId: String(basicWebContentStructureId),
					groupId: site.id,
					name: getRandomString(),
					type: 'display-page',
				}
			);

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
			{
				layoutPageTemplateEntryId:
					journalDisplayPage.layoutPageTemplateEntryId,
			}
		);
	}
);

test(
	'Preview display page for asset in review change screen',
	{tag: '@LPD-61591'},
	async ({apiHelpers, changeTrackingPage, ctCollection, page}) => {
		await changeTrackingPage.workOnPublication(ctCollection);

		const blogTitle = getRandomString();

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			headline: blogTitle,
		});

		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await changeTrackingPage.reviewChange(blogTitle);

		const displayPageLocator = page.locator(
			'.publications-render-view-content iframe'
		);

		await expect(displayPageLocator).toBeVisible();

		const journalArticleTitle = getRandomString();

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: journalArticleTitle},
		});

		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await changeTrackingPage.reviewChange(journalArticleTitle);

		await expect(displayPageLocator).toBeVisible();
	}
);

test(
	'Do not preview display page for asset when display page set to none',
	{tag: '@LPD-61591'},
	async ({apiHelpers, changeTrackingPage, ctCollection, page}) => {
		await changeTrackingPage.workOnPublication(ctCollection);

		const blogTitle = getRandomString();
		const blogBody = getRandomString();

		const blog = await apiHelpers.headlessDelivery.postBlog(site.id, {
			articleBody: blogBody,
			headline: blogTitle,
		});

		await apiHelpers.jsonWebServicesAssetDisplayPageEntry.addAssetDisplayPageEntry(
			{
				classNameId: blogsEntryClassName.classNameId,
				classPK: String(blog.id),
				groupId: site.id,
				layoutPageTemplateEntryId:
					blogDisplayPage.layoutPageTemplateEntryId,
				type: 'none',
			}
		);

		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await changeTrackingPage.reviewChange(blogTitle);

		await expect(page.getByText(blogBody)).toBeVisible();

		const journalArticleTitle = getRandomString();
		const journalContent = getRandomString();

		const journal = await apiHelpers.jsonWebServicesJournal.addWebContent({
			content: journalContent,
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: journalArticleTitle},
		});

		await apiHelpers.jsonWebServicesAssetDisplayPageEntry.addAssetDisplayPageEntry(
			{
				classNameId: journalArticleClassName.classNameId,
				classPK: String(journal.resourcePrimKey),
				groupId: site.id,
				layoutPageTemplateEntryId:
					journalDisplayPage.layoutPageTemplateEntryId,
				type: 'none',
			}
		);

		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await changeTrackingPage.reviewChange(journalArticleTitle);

		await expect(page.getByText(journalContent)).toBeVisible();
	}
);
