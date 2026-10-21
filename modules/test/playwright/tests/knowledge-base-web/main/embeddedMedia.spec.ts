/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import * as fs from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {knowledgeBasePages} from '../../../fixtures/knowledgeBasePagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {isImageLoaded} from '../../../utils/isImageLoaded';
import {waitForAlert} from '../../../utils/waitForAlert';
import {KnowledgeBaseUrls} from './utils/knowledgeBaseUrls';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	knowledgeBasePages,
	loginTest()
);

test(
	'Images from Documents and Media still render after the file is moved to a folder',
	{tag: ['@LPD-110921', '@LPS-154502']},
	async ({
		apiHelpers,
		knowledgeBaseEditArticlePage,
		knowledgeBaseViewArticlePage,
		page,
		site,
	}) => {
		const documentTitle = getRandomString();

		const document = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			fs.createReadStream(
				path.join(
					__dirname,
					'../../frontend-js-item-selector-web/main/dependencies/sample_image.png'
				)
			),
			{fileName: `${documentTitle}.png`, title: documentTitle}
		);

		const title = getRandomString();

		const knowledgeBaseArticle =
			await apiHelpers.headlessDelivery.postSiteKnowledgeBaseArticle({
				articleBody: getRandomString(),
				siteId: site.id,
				title,
			});

		await page.goto(
			new KnowledgeBaseUrls(site.friendlyUrlPath).getEditKBArticleUrl(
				knowledgeBaseArticle.id
			)
		);

		await expect(knowledgeBaseEditArticlePage.titlePlaceholder).toHaveValue(
			title
		);

		const itemSelector = page.frameLocator('iframe[title="Select Item"]');

		const documentsAndMediaLink = itemSelector.getByRole('link', {
			name: 'Documents and Media',
		});

		await clickAndExpectToBeVisible({
			target: documentsAndMediaLink,
			trigger: page.getByRole('button', {name: 'Image'}),
		});

		const documentItem = itemSelector.getByText(documentTitle, {
			exact: true,
		});

		await clickAndExpectToBeVisible({
			target: documentItem,
			trigger: documentsAndMediaLink,
		});

		await documentItem.click();

		await expect(
			knowledgeBaseEditArticlePage.contentFrameLocator.locator(
				'img[data-widget="image"]'
			)
		).toBeVisible();

		await knowledgeBaseEditArticlePage.publish();

		await waitForAlert(
			page,
			`Success:${title} was successfully published.`
		);

		const documentFolder =
			await apiHelpers.headlessDelivery.postDocumentFolder(site.id);

		await apiHelpers.jsonWebServicesDocumentLibrary.moveFileEntry(
			document.id,
			documentFolder.id
		);

		await knowledgeBaseViewArticlePage.goto(site.friendlyUrlPath, title);

		const image = knowledgeBaseViewArticlePage.articleBody.locator('img');

		await expect(image).toBeVisible();

		expect(await isImageLoaded(image)).toBe(true);

		const imageResponse = await page.request.get(
			await image.getAttribute('src')
		);

		expect(imageResponse.status()).toBe(200);
		expect(imageResponse.headers()['content-type']).toContain('image/');
	}
);
