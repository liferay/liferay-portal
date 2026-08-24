/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../../fixtures/displayPageTemplatesPagesTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../../utils/getRandomString';
import {createDefaultDisplayPageTemplate} from './helpers/displayPageTemplate';

const _APPLICATION_NAME = 'cms/basic-web-contents';

const _OBJECT_DEFINITION_NAME = 'CMSBasicWebContent';

const _URL_SEPARATOR = 'cmsbasicwebcontent';

const test = mergeTests(
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

test(
	'A custom Space friendly URL replaces the asset-library segment of the Display Page URL',
	{tag: '@LPD-103464'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const contentTitle = `Content ${getRandomString()}`;
		const friendlyUrlPath = `custom-${getRandomString()}`.toLowerCase();
		const spaceName = `Space ${getRandomString()}`;
		const untouchedContentTitle = `Content ${getRandomString()}`;
		const untouchedFriendlyUrlPath =
			`untouched-${getRandomString()}`.toLowerCase();
		const untouchedSpaceName = `Space ${getRandomString()}`;

		let customFriendlyURL: string;
		let space: Awaited<
			ReturnType<
				typeof apiHelpers.headlessAssetLibrary.createAssetLibrary
			>
		>;
		let untouchedSpace: typeof space;

		await test.step('Create and activate a Basic Web Content DPT', async () => {
			await createDefaultDisplayPageTemplate({
				apiHelpers,
				displayPageTemplatesPage,
				objectDefinitionName: _OBJECT_DEFINITION_NAME,
				page,
				pageEditorPage,
				site,
			});
		});

		await test.step('Create a content in a Space connected to the site', async () => {
			space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				type: 'Space',
			});

			await apiHelpers.headlessAssetLibrary.connectSite(
				space.externalReferenceCode,
				site.externalReferenceCode
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					friendlyUrlPath,
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentTitle,
				},
				_APPLICATION_NAME,
				spaceName
			);
		});

		await test.step('The content resolves under the generated asset-library segment', async () => {
			await _expectContentAt(
				page,
				_buildDisplayURL(
					site.friendlyUrlPath,
					`asset-library-${space.id}`,
					friendlyUrlPath
				),
				contentTitle
			);
		});

		await test.step('Edit the Space friendly URL', async () => {
			customFriendlyURL = `/space-${getRandomString()}`.toLowerCase();

			const patchedAssetLibrary =
				await apiHelpers.headlessAssetLibrary.patchAssetLibrary(
					space.externalReferenceCode,
					{friendlyURL: customFriendlyURL}
				);

			expect(patchedAssetLibrary.friendlyURL).toBe(customFriendlyURL);
		});

		await test.step('The content resolves under the custom segment', async () => {
			await _expectContentAt(
				page,
				_buildDisplayURL(
					site.friendlyUrlPath,
					customFriendlyURL.substring(1),
					friendlyUrlPath
				),
				contentTitle
			);
		});

		await test.step('The generated segment no longer resolves the content', async () => {
			const response = await page.goto(
				_buildDisplayURL(
					site.friendlyUrlPath,
					`asset-library-${space.id}`,
					friendlyUrlPath
				)
			);

			expect(response?.status()).toBe(404);

			await expect(_getMainContentText(page, contentTitle)).toBeHidden();
		});

		await test.step('A Space whose friendly URL was never edited keeps resolving', async () => {
			untouchedSpace =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: untouchedSpaceName,
					type: 'Space',
				});

			await apiHelpers.headlessAssetLibrary.connectSite(
				untouchedSpace.externalReferenceCode,
				site.externalReferenceCode
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					friendlyUrlPath: untouchedFriendlyUrlPath,
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: untouchedContentTitle,
				},
				_APPLICATION_NAME,
				untouchedSpaceName
			);

			await _expectContentAt(
				page,
				_buildDisplayURL(
					site.friendlyUrlPath,
					`asset-library-${untouchedSpace.id}`,
					untouchedFriendlyUrlPath
				),
				untouchedContentTitle
			);
		});
	}
);

test(
	'An internal link survives the Headless duplicate and carries no asset-library segment',
	{tag: '@LPD-103464'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const linkedFriendlyUrlPath =
			`linked-${getRandomString()}`.toLowerCase();
		const linkedTitle = `Content ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;
		const title = `Content ${getRandomString()}`;

		let content: string;
		let entry: ObjectEntry;
		let linkURL: string;

		await test.step('Create and activate a Basic Web Content DPT', async () => {
			await createDefaultDisplayPageTemplate({
				apiHelpers,
				displayPageTemplatesPage,
				objectDefinitionName: _OBJECT_DEFINITION_NAME,
				page,
				pageEditorPage,
				site,
			});
		});

		await test.step('Link one content from another inside a Space with a custom friendly URL', async () => {
			const space =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: spaceName,
					type: 'Space',
				});

			const customFriendlyURL =
				`/space-${getRandomString()}`.toLowerCase();

			await apiHelpers.headlessAssetLibrary.patchAssetLibrary(
				space.externalReferenceCode,
				{friendlyURL: customFriendlyURL}
			);

			await apiHelpers.headlessAssetLibrary.connectSite(
				space.externalReferenceCode,
				site.externalReferenceCode
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					friendlyUrlPath: linkedFriendlyUrlPath,
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: linkedTitle,
				},
				_APPLICATION_NAME,
				spaceName
			);

			linkURL = _buildDisplayURL(
				site.friendlyUrlPath,
				customFriendlyURL.substring(1),
				linkedFriendlyUrlPath
			);

			content = `<p><a href="${linkURL}">${linkedTitle}</a></p>`;

			entry = await apiHelpers.objectEntry.postObjectEntry(
				{
					content,
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title,
				},
				_APPLICATION_NAME,
				spaceName
			);
		});

		await test.step('The duplicate keeps the link untouched', async () => {
			const copy = await apiHelpers.objectEntry.postObjectEntryCopy(
				_APPLICATION_NAME,
				entry.id,
				entry.objectEntryFolderId
			);

			expect(copy.title).toBe(`${title} (Copy)`);

			expect(copy.content).toBe(content);

			expect(copy.content).not.toContain('asset-library-');
		});

		await test.step('The link still resolves to the target content', async () => {
			await _expectContentAt(page, linkURL, linkedTitle);
		});
	}
);

function _buildDisplayURL(
	siteFriendlyUrlPath: string,
	spaceSegment: string,
	friendlyUrlPath: string
) {
	return `/web${siteFriendlyUrlPath}/${_URL_SEPARATOR}/${spaceSegment}/${friendlyUrlPath}`;
}

async function _expectContentAt(page: Page, url: string, title: string) {
	await expect(async () => {
		await page.goto(url);

		await expect(_getMainContentText(page, title)).toBeVisible({
			timeout: 2000,
		});
	}).toPass({timeout: 10000});
}

function _getMainContentText(page: Page, title: string) {
	return page.locator('#main-content').getByText(title, {exact: true});
}
