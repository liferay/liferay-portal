/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {documentLibraryPagesTest} from '../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import {createCategories} from '../../helpers/CreateCategories';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../utils/getRandomString';
import {performLogout} from '../../utils/performLogin';
import {waitForAlert} from '../../utils/waitForAlert';
import getPageDefinition from '../layout-content-page-editor-web/utils/getPageDefinition';
import getWidgetDefinition from '../layout-content-page-editor-web/utils/getWidgetDefinition';

const test = mergeTests(
	apiHelpersTest,
	documentLibraryPagesTest,
	featureFlagsTest({
		'LPS-178052': true,
	}),
	isolatedSiteTest,
	loginTest()
);

test(
	'Check order by Relevance in Search of DL',
	{
		tag: '@LPD-32481',
	},
	async ({documentLibraryEditFilePage, documentLibraryPage, page, site}) => {
		await documentLibraryEditFilePage.publishNewBasicFileEntry(
			'test',
			site.friendlyUrlPath
		);
		await documentLibraryEditFilePage.publishNewBasicFileEntry(
			getRandomString(),
			site.friendlyUrlPath
		);
		await documentLibraryEditFilePage.publishNewBasicFileEntry(
			getRandomString(),
			site.friendlyUrlPath
		);
		await documentLibraryPage.orderMenu.click();
		await expect(
			page.getByRole('menuitem', {name: 'Relevance'})
		).not.toBeVisible();

		await page.reload();

		await documentLibraryPage.searchInDL('test');

		await documentLibraryPage.orderBy('relevance');

		await expect(
			page.locator('dd.list-group-item[data-title="test"]')
		).toHaveAttribute('id', /_entries_1$/);
	}
);

test(
	'Check if Ordering by Modified Date working, after editing a document',
	{
		tag: '@LPD-32483',
	},
	async ({documentLibraryEditFilePage, documentLibraryPage, page, site}) => {
		const title = getRandomString();
		await documentLibraryEditFilePage.publishNewBasicFileEntry(
			title,
			site.friendlyUrlPath
		);

		const title2 = getRandomString();
		await documentLibraryEditFilePage.publishNewBasicFileEntry(
			title2,
			site.friendlyUrlPath
		);

		await documentLibraryPage.goToEditFileEntry(title);
		await documentLibraryEditFilePage.descriptionInput.fill(
			getRandomString()
		);
		await documentLibraryEditFilePage.publishButton.click();
		await waitForAlert(
			page,
			'Success:Your request completed successfully.'
		);
		await page.getByRole('link', {name: 'Back'}).click();

		await documentLibraryPage.orderBy('Modified Date');
		await documentLibraryPage.orderBy('Descending');

		await expect(
			page
				.locator(`dd.card-page-item[data-title="${title}"]`)
				.getAttribute('id')
		).resolves.toMatch(/_entries_1$/);
	}
);

test(
	'Show a success message after scheduling a new file',
	{tag: '@LPD-16658'},
	async ({documentLibraryEditFilePage, page, site}) => {
		const scheduleDate = `01/01/${new Date().getFullYear() + 1}`;
		const title = getRandomString();

		await documentLibraryEditFilePage.publishNewFileWithScheduleDate(
			scheduleDate,
			title,
			site.friendlyUrlPath
		);

		await expect(page.getByRole('link', {name: title})).toBeVisible();

		const toastAlertContainer = page.locator('[id="ToastAlertContainer"]');

		await expect(toastAlertContainer).toBeVisible();

		await expect(toastAlertContainer).toHaveText(
			`Success:${title} will be published on ${new Intl.DateTimeFormat(
				'en-US',
				{
					day: 'numeric',
					hour: 'numeric',
					hour12: true,
					minute: 'numeric',
					month: 'numeric',
					year: '2-digit',
				}
			)
				.format(new Date(scheduleDate))
				.replace(',', '')}.`
		);
	}
);

test(
	'Identify at a glance if a Document is visible for guests',
	{tag: '@LPD-16313'},
	async ({documentLibraryEditFilePage, documentLibraryPage, site}) => {
		const title = getRandomString();

		await documentLibraryEditFilePage.publishNewFileWithoutGuestViewPermission(
			title,
			site.friendlyUrlPath
		);

		await documentLibraryPage.changeView('cards');
		await documentLibraryPage.assertPrivateFileIcon();

		await documentLibraryPage.changeView('table');
		await documentLibraryPage.assertPrivateFileIcon();

		await documentLibraryPage.changeView('list');
		await documentLibraryPage.assertPrivateFileIcon();
	}
);

test(
	'Show icon in the content admin and content editor',
	{tag: '@LPD-16313'},
	async ({documentLibraryEditFilePage, documentLibraryPage, page, site}) => {
		const title = getRandomString();

		await documentLibraryEditFilePage.publishNewFileWithoutGuestViewPermission(
			title,
			site.friendlyUrlPath
		);

		await documentLibraryPage.changeView('cards');

		await documentLibraryPage.goToEditFileEntry(title);

		await documentLibraryPage.assertPrivateFileIcon();

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await page.getByRole('link', {name: title}).click();

		await documentLibraryPage.assertPrivateFileIcon();
	}
);

test(
	'Show icon in the DL item selector',
	{tag: '@LPD-16313'},
	async ({
		documentLibraryEditDocumentTypesPage,
		documentLibraryEditFilePage,
		documentLibraryPage,
		site,
	}) => {
		const dTypeTitle = getRandomString();
		const title = getRandomString();

		await documentLibraryEditDocumentTypesPage.createNewDLTypeWithUploadField(
			dTypeTitle,
			site.friendlyUrlPath
		);

		await documentLibraryEditFilePage.publishNewFileWithoutGuestViewPermission(
			title
		);

		await documentLibraryEditFilePage.goToNewFileDifferentType(dTypeTitle);

		await documentLibraryEditFilePage.selectForUpdateButton.click();

		await documentLibraryEditFilePage.assertPrivateFileIconInSelectPopUp(
			'Document'
		);

		await documentLibraryEditFilePage.changeViewInItemSelector(
			'Document',
			'List'
		);

		await documentLibraryEditFilePage.assertPrivateFileIconInSelectPopUp(
			'Document'
		);

		await documentLibraryEditFilePage.changeViewInItemSelector(
			'Document',
			'Table'
		);

		await documentLibraryEditFilePage.assertPrivateFileIconInSelectPopUp(
			'Document'
		);

		await documentLibraryPage.deleteFileEntry(title);

		await documentLibraryPage.deleteDocumentType(dTypeTitle);
	}
);

test(
	'Error uploading multiples files with custom document type',
	{
		tag: '@LPD-29609',
	},

	async ({
		documentLibraryEditDocumentTypesPage,
		documentLibraryEditFilePage,
		page,
	}) => {
		const dTypeTitle = getRandomString();

		await documentLibraryEditDocumentTypesPage.createNewDLTypeWithNumericField(
			dTypeTitle
		);
		await documentLibraryEditFilePage.goToNewFileDifferentType(
			'Multiple Files Upload'
		);

		await documentLibraryEditFilePage.publishMultipleFiles(dTypeTitle, [
			path.join(__dirname, '/dependencies/image1.jpeg'),
		]);

		await expect(
			page.getByRole('link', {exact: true, name: 'image1'})
		).toBeVisible();
	}
);

test(
	'Search in DL portlet does not show results in card view',
	{tag: ['@LPD-31694', '@LPD-202909']},
	async ({
		apiHelpers,
		documentLibraryEditFilePage,
		documentLibraryPage,
		page,
		site,
	}) => {
		const title = getRandomString();
		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.goToCreateNewFile();
		await documentLibraryEditFilePage.publishNewBasicFileEntryWithoutGoTo(
			title
		);

		const portletId = getRandomString();
		const widgetDefinition = getWidgetDefinition({
			id: portletId,
			widgetName: 'com_liferay_document_library_web_portlet_DLPortlet',
		});
		await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([widgetDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await performLogout(page);

		await page.goto('/web' + site.friendlyUrlPath);

		await documentLibraryPage.search(title);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Cards'}),
			trigger: page.getByLabel('Select View, Currently Selected: '),
		});
		await expect(
			page
				.locator('.portlet-document-library')
				.getByRole('link', {name: title})
		).toBeVisible();
	}
);

test(
	'Replace option does not work on Categories Selector',
	{
		tag: '@LPD-27899',
	},

	async ({
		apiHelpers,
		documentLibraryEditFilePage,
		documentLibraryPage,
		page,
		site,
	}) => {
		const vocabularyName = getRandomString();

		const categories = await createCategories({
			apiHelpers,
			categoryNames: [
				{name: 'Books'},
				{name: 'Plants'},
				{name: 'Pets'},
				{name: 'Furniture'},
			],
			site,
			vocabularyName,
		});

		const document1 = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
			{
				description: getRandomString(),
				fileName: getRandomString(),
				taxonomyCategoryIds: [categories[0].id, categories[1].id],
				title: getRandomString(),
			}
		);

		const document2 = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg')),
			{
				description: getRandomString(),
				fileName: getRandomString(),
				taxonomyCategoryIds: [categories[0].id, categories[2].id],
				title: getRandomString(),
			}
		);

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await documentLibraryPage.openBulkEditCategoriesModal([
			document1.title,
			document2.title,
		]);

		await expect(
			page.locator('.modal .label-item-expand', {hasText: 'Books'})
		).toBeVisible();

		await documentLibraryPage.goto(site.friendlyUrlPath);

		await documentLibraryPage.replaceCategoriesUsingBulkEditCategoriesModal(
			[document1.title, document2.title],
			[{categoryNames: ['Furniture'], vocabularyName}]
		);

		await waitForAlert(page, 'Success:Changes Saved');

		for (const document of [document1, document2]) {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.goToEditFileEntry(document.title);
			await documentLibraryEditFilePage.openFieldset('Categorization');
			await page.getByText(vocabularyName).waitFor();

			await expect(await page.getByText(document.title)).toBeVisible();
		}
	}
);
