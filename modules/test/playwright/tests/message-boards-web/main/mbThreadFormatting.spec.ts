/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {messageBoardsPagesTest} from '../../../fixtures/messageBoardsTest';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	featureFlagsTest({'LPD-11235': {enabled: true}}),
	loginTest(),
	messageBoardsPagesTest
);

test(
	'Can add bold text to a thread body',
	{tag: '@LPS-136910'},
	async ({messageBoardsEditThreadPage, page, site}) => {
		const subject = getRandomString();
		const body = getRandomString();

		await messageBoardsEditThreadPage.goto(site.friendlyUrlPath);

		await messageBoardsEditThreadPage.subjectSelector.fill(subject);
		await messageBoardsEditThreadPage.bodyTextBox.fill(body);

		// Select the body text and toggle bold

		await messageBoardsEditThreadPage.bodyTextBox.press('Control+a');

		await page.locator('a.cke_button__bold').click();

		await messageBoardsEditThreadPage.publishButton.click();

		await page.waitForLoadState('networkidle');

		// The published body renders in bold

		await expect(
			page.locator('.message-content strong').getByText(body)
		).toBeVisible();
	}
);

test(
	'Can add an image from the Document Library to a thread body',
	{tag: '@LPS-136911'},
	async ({apiHelpers, messageBoardsEditThreadPage, page, site}) => {
		const documentTitle = getRandomString().replace(/-/g, '');
		const subject = getRandomString();

		// Seed an image in the Document Library

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, 'dependencies/image1.jpeg')),
			{
				fileName: `${documentTitle}.jpeg`,
				title: documentTitle,
			}
		);

		// Add a thread with the image inserted into the body

		await messageBoardsEditThreadPage.goto(site.friendlyUrlPath);

		await messageBoardsEditThreadPage.subjectSelector.fill(subject);

		await messageBoardsEditThreadPage.insertBodyImage(documentTitle);

		await messageBoardsEditThreadPage.publishButton.click();

		// The image renders in the published thread

		const threadImage = page.locator('.message-content img');

		await expect(threadImage).toHaveAttribute(
			'src',
			new RegExp(documentTitle)
		);

		await expect
			.poll(() =>
				threadImage.evaluate(
					(image: HTMLImageElement) => image.naturalWidth
				)
			)
			.toBeGreaterThan(0);
	}
);

test('Can add a link to a document in a thread body', async ({
	apiHelpers,
	messageBoardsEditThreadPage,
	page,
	site,
}) => {
	const documentTitle = getRandomString().replace(/-/g, '');

	// Seed a document to link to

	await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(path.join(__dirname, 'dependencies/image1.jpeg')),
		{
			fileName: `${documentTitle}.jpeg`,
			title: documentTitle,
		}
	);

	// Add a thread that links to the document

	await messageBoardsEditThreadPage.goto(site.friendlyUrlPath);

	await messageBoardsEditThreadPage.subjectSelector.fill(getRandomString());

	await messageBoardsEditThreadPage.insertBodyLinkToDocument(
		'Link to Document',
		documentTitle
	);

	await messageBoardsEditThreadPage.publishButton.click();

	// The link renders in the published thread

	await expect(
		page
			.locator('.message-content')
			.getByRole('link', {name: 'Link to Document'})
	).toHaveAttribute('href', new RegExp(documentTitle));
});

test('Can add a link to a page in a thread body', async ({
	apiHelpers,
	messageBoardsEditThreadPage,
	page,
	site,
}) => {
	const pageName = getRandomString().replace(/-/g, '');

	// Seed a page to link to

	await apiHelpers.jsonWebServicesLayout.addLayout({
		groupId: String(site.id),
		title: pageName,
	});

	// Add a thread that links to the page

	await messageBoardsEditThreadPage.goto(site.friendlyUrlPath);

	await messageBoardsEditThreadPage.subjectSelector.fill(getRandomString());

	await messageBoardsEditThreadPage.insertBodyLinkToPage(
		'Link to Page',
		pageName
	);

	await messageBoardsEditThreadPage.publishButton.click();

	// The link renders in the published thread

	await expect(
		page
			.locator('.message-content')
			.getByRole('link', {name: 'Link to Page'})
	).toHaveAttribute('href', new RegExp(pageName));
});
