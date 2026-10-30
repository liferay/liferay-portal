/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	dataApiHelpersTest,
	documentLibraryPagesTest,
	isolatedSiteTest,
	loginTest()
);

async function openInfoPanel(page: Page) {
	const sidebar = page.locator(
		'#_com_liferay_document_library_web_portlet_DLAdminPortlet_ContextualSidebar'
	);

	await clickAndExpectToBeVisible({
		target: sidebar.getByRole('img', {name: 'Thumbnail'}),
		trigger: page.locator('[data-qa-id="infoButton"]'),
	});
}

test(
	'DM Preview page has not a fixed navbar',
	{tag: '@LPD-26290'},
	async ({documentLibraryEditFilePage, documentLibraryPage, page, site}) => {
		const title = getRandomString();

		await documentLibraryEditFilePage.publishNewBasicFileEntry(
			title,
			site.friendlyUrlPath
		);

		await page.getByRole('link', {name: title}).click();

		const navItem = await page.locator(
			'nav.component-tbar.subnav-tbar-light'
		);

		const navItemPosition = await navItem.evaluate((element) => {
			return window
				.getComputedStyle(element)
				.getPropertyValue('position');
		});

		await expect(navItemPosition).not.toBe('fixed');

		await documentLibraryPage.goto(site.friendlyUrlPath);
	}
);

test(
	'Verify location link from file info panel',
	{tag: '@LPD-85940'},
	async ({documentLibraryEditFilePage, page, site}) => {
		const title = getRandomString();

		await documentLibraryEditFilePage.publishNewBasicFileEntry(
			title,
			site.friendlyUrlPath
		);

		await page.getByRole('link', {name: title}).first().click();

		const infoButton = page.locator('[data-qa-id="infoButton"]');

		await expect(infoButton).toBeVisible();
		await infoButton.click();

		const sidebar = page.locator(
			'#_com_liferay_document_library_web_portlet_DLAdminPortlet_ContextualSidebar.contextual-sidebar-visible'
		);

		await expect(sidebar).toBeVisible();

		const locationLink = sidebar.locator('dt:has-text("Location") + dd a');

		await expect(locationLink).toBeVisible();

		await expect(locationLink).toHaveAttribute(
			'href',
			new RegExp(`/group${site.friendlyUrlPath}/~/control_panel/manage`)
		);
	}
);

test(
	'Document without a preview shows the empty state',
	{tag: '@LPS-139297'},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const title = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(
					__dirname,
					'../../message-boards-web/main/dependencies/Document_1.doc'
				)
			),
			{fileName: 'Document_1.doc', title}
		);

		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.goToViewFileEntry(title);

		const previewError = page.locator('.preview-file-error-container');

		await expect(previewError).toContainText('No Preview Available');
		await expect(previewError).toContainText(
			'Hmm... looks like this item does not have a preview we can show you.'
		);
	}
);

test(
	'Image documents show a preview and a thumbnail',
	{tag: '@LPS-104957'},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		for (const fileName of [
			'../../document-library-web/main/dependencies/image1.jpeg',
			'../../trash-web/main/dependencies/Document.png',
			'../../item-selector-taglib/main/dependencies/Document_1.svg',
		]) {
			const title = getRandomString();

			await apiHelpers.headlessDelivery.postDocument(
				site.id,
				createReadStream(path.join(__dirname, fileName)),
				{fileName: path.basename(fileName), title}
			);

			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.goToViewFileEntry(title);

			await expect(
				page.locator('.preview-file-container img')
			).toBeVisible();

			await openInfoPanel(page);
		}
	}
);
