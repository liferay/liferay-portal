/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {
	applyFDSDateTimeRangeFilter,
	formatDateTimeForUI,
} from '../../../utils/applyFDSDateTimeRangeFilter';
import {applyFDSSelectionFilter} from '../../../utils/applyFDSSelectionFilter';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitchViaApi, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const _EXTERNAL_VIDEO_ID = 'IqCSx3omX4o';

const _EXTERNAL_VIDEO_URL = `https://www.youtube.com/watch?v=${_EXTERNAL_VIDEO_ID}`;

const _PNG_BASE64 =
	'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR4nGNgAAACAAEABToPCwAAAABJRU5ErkJggg==';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
	}),
	loginTest()
);

test(
	'Confirmation modal is shown when delete a single content in a space with recycle bin disabled',
	{tag: '@LPD-64867'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const file1Title = `<b>Content ${getRandomString()}</b>`;
		let space = null;

		// Create a Space

		space = await apiHelpers.headlessAssetLibrary.createSpace({
			settings: {
				trashEnabled: false,
			},
		});

		// Create a content

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file1Title,
			},
			applicationName,
			space.name
		);

		// Delete the content

		await assetsPage.gotoAll();

		await assetsPage.execItemAction({
			action: 'Delete',
			filter: file1Title,
		});

		// Accept the confirmation

		await expect(
			page.getByRole('heading', {name: `Delete "${file1Title}"`})
		).toBeVisible();

		await expect(
			page.getByText('You are about to delete the asset')
		).toBeVisible();

		await expect(page.locator('.liferay-modal .modal-dialog')).toHaveClass(
			/modal-dialog-centered/
		);

		await page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(page, `${file1Title} was successfully deleted.`);

		await expect(
			page.getByRole('cell', {name: file1Title})
		).not.toBeVisible();

		// Delete the space

		await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(space.id);
	}
);

test(
	'Only content folders will be displayed when copying content',
	{tag: '@LPD-72879'},
	async ({apiHelpers, assetsPage, page}) => {
		const file1Title = `Content ${getRandomString()}`;
		const file2Title = `File ${getRandomString()}`;

		// Create a Space

		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		// Create a content

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file1Title,
			},
			'cms/basic-web-contents',
			space.name
		);

		// Create a file

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'R0lGODlhAQABAAAAACw=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: file2Title,
			},
			'cms/basic-documents',
			space.name
		);

		// Copy the content

		await assetsPage.gotoAll();

		await assetsPage.execItemAction({
			action: 'Copy To',
			filter: file1Title,
			parentAction: 'Copy',
		});

		// Check the content folders

		await page.getByLabel(space.name).click();
		await expect(
			page.getByText('Showing 1 to 1 of 1 entries.')
		).toBeVisible();

		await expect(page.getByLabel('Contents', {exact: true})).toBeVisible();

		// Copy the file

		await assetsPage.gotoAll();

		await assetsPage.execItemAction({
			action: 'Copy To',
			filter: file2Title,
			parentAction: 'Copy',
		});

		// Check the file folders

		await page.getByLabel(space.name).click();
		await expect(
			page.getByText('Showing 1 to 1 of 1 entries.')
		).toBeVisible();

		await expect(page.getByLabel('Files', {exact: true})).toBeVisible();
	}
);

test(
	'Duplicating content creates a draft copy in the same Space',
	{tag: '@LPD-88346'},
	async ({apiHelpers, assetsPage, page}) => {
		const fileTitle = `Content ${getRandomString()}`;
		const spaceName = 'Default';

		// Create a content

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: fileTitle,
			},
			'cms/basic-web-contents',
			spaceName
		);

		// Duplicate the content

		await assetsPage.gotoAll();

		await assetsPage.execItemAction({
			action: 'Duplicate',
			filter: fileTitle,
			parentAction: 'Copy',
		});

		await expect(
			page.getByRole('link', {
				exact: true,
				name: `${fileTitle} (Copy)`,
			})
		).toBeVisible();

		await expect(
			assetsPage.table.bodyRows
				.filter({
					has: page.getByRole('link', {
						exact: true,
						name: `${fileTitle} (Copy)`,
					}),
				})
				.getByText('Draft')
		).toBeVisible();

		// Duplicate the content again

		await assetsPage.execItemAction({
			action: 'Duplicate',
			filter: fileTitle,
			parentAction: 'Copy',
		});

		await expect(
			page.getByRole('link', {
				exact: true,
				name: `${fileTitle} (Copy 1)`,
			})
		).toBeVisible();
	}
);

test(
	'Can view Share modal for added content',
	{tag: '@LPD-62554'},
	async ({apiHelpers, assetsPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const file1Title = `Title ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createSpace({
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
			},
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file1Title,
			},
			applicationName,
			space.name
		);

		await assetsPage.gotoAll();

		await assetsPage.execItemAction({
			action: 'Share',
			filter: file1Title,
		});

		await expect(assetsPage.modal.title).toContainText(file1Title);
	}
);

test(
	'Dragging and dropping files into the data set opens upload modal',
	{tag: '@LPD-58618'},
	async ({assetsPage, page}) => {
		await assetsPage.gotoAll();

		const dataSetWrapper = page.locator('div.data-set-wrapper').first();
		const dataTransfer = await page.evaluateHandle(
			(data) => {
				const dt = new DataTransfer();

				const file = new File(
					[data.toString('hex')],
					'file_upload_image_1.jpeg',
					{
						type: 'image/jpg',
					}
				);
				dt.items.add(file);

				return dt;
			},
			readFileSync(
				path.join(__dirname, '/dependencies/file_upload_image_1.jpg')
			)
		);

		await dataSetWrapper.dispatchEvent('dragstart', {dataTransfer});
		await dataSetWrapper.dispatchEvent('dragenter', {dataTransfer});
		await dataSetWrapper.dispatchEvent('dragover', {dataTransfer});

		await dataSetWrapper.dispatchEvent('drop', {dataTransfer});
		await dataSetWrapper.dispatchEvent('dragend', {dataTransfer});

		await expect(assetsPage.modal.container).toBeVisible();

		await expect(assetsPage.modal.title).toContainText(
			'Upload Multiple Files'
		);
		await expect(assetsPage.modal.body).toContainText(
			'file_upload_image_1.jpeg'
		);
	}
);

test(
	'Upload button stays in viewport when many files are queued on a short screen',
	{tag: '@LPD-90005'},
	async ({assetsPage, page}) => {
		await page.setViewportSize({height: 500, width: 1024});

		await assetsPage.gotoAll();

		const dataSetWrapper = page.locator('div.data-set-wrapper').first();
		const dataTransfer = await page.evaluateHandle(
			(data) => {
				const dt = new DataTransfer();

				for (let i = 1; i <= 10; i++) {
					const file = new File(
						[data.toString('hex')],
						`file_upload_image_${i}.jpeg`,
						{
							type: 'image/jpg',
						}
					);
					dt.items.add(file);
				}

				return dt;
			},
			readFileSync(
				path.join(__dirname, '/dependencies/file_upload_image_1.jpg')
			)
		);

		await dataSetWrapper.dispatchEvent('dragstart', {dataTransfer});
		await dataSetWrapper.dispatchEvent('dragenter', {dataTransfer});
		await dataSetWrapper.dispatchEvent('dragover', {dataTransfer});

		await dataSetWrapper.dispatchEvent('drop', {dataTransfer});
		await dataSetWrapper.dispatchEvent('dragend', {dataTransfer});

		await expect(assetsPage.modal.container).toBeVisible();

		await expect(
			assetsPage.modal.footer.getByRole('button', {
				name: 'Upload (10)',
			})
		).toBeInViewport();
	}
);

test(
	'Expiration date filter allows future dates',
	{tag: ['@LPD-69189', '@LPD-90051']},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const file1Title = `Content ${getRandomString()}`;

		const futureDate = new Date();

		futureDate.setDate(futureDate.getDate() + 1);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				expirationDate: futureDate.toISOString(),
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file1Title,
			},
			applicationName,
			'Default'
		);

		await assetsPage.gotoAll();

		await expect(
			page.getByRole('cell', {exact: true, name: file1Title})
		).toBeVisible();

		// Filter by an Expiration Date range covering futureDate

		const fromDate = new Date();
		const toDate = new Date();

		toDate.setDate(toDate.getDate() + 2);

		await applyFDSDateTimeRangeFilter(page, {
			filter: 'Expiration Date',
			from: fromDate,
			to: toDate,
		});

		// Verify that the content is still visible (it was filtered out before the fix)

		await expect(
			page.getByRole('cell', {exact: true, name: file1Title})
		).toBeVisible();
	}
);

test(
	'Content can be filtered by Review Date',
	{tag: ['@LPD-85206', '@LPD-90051']},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const file1Title = `Content ${getRandomString()}`;

		const pastDate = new Date();

		pastDate.setDate(pastDate.getDate() - 1);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				reviewDate: pastDate.toISOString(),
				title: file1Title,
			},
			applicationName,
			'Default'
		);

		await assetsPage.gotoAll();

		await expect(
			page.getByRole('cell', {exact: true, name: file1Title})
		).toBeVisible();

		// Filter by a Review Date range covering pastDate

		const fromDate = new Date();
		const toDate = new Date();

		fromDate.setDate(fromDate.getDate() - 2);

		await applyFDSDateTimeRangeFilter(page, {
			filter: 'Review Date',
			from: fromDate,
			to: toDate,
		});

		// Verify that the content is visible

		await expect(
			page.getByRole('cell', {exact: true, name: file1Title})
		).toBeVisible();
	}
);

test(
	'Expiration date filter does not allow "to" date to be before "from" date',
	{tag: ['@LPD-78935', '@LPD-90051']},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const fileTitle = `Content ${getRandomString()}`;
		const addFilterButton = page.getByRole('button', {name: 'Add Filter'});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: fileTitle,
			},
			applicationName,
			'Default'
		);

		// Go to the All section

		await assetsPage.gotoAll();

		// Filter by Expiration Date

		await page.getByRole('button', {name: 'Filter'}).click();

		await page.getByRole('menuitem', {name: 'Expiration Date'}).click();

		const fromDateInput = page.getByLabel('From');
		const toDateInput = page.getByLabel('To', {exact: true});

		const fromDate = new Date();
		const toDate = new Date();

		fromDate.setDate(fromDate.getDate() + 1);

		// Set a future "from" date

		await fromDateInput.fill(formatDateTimeForUI(fromDate));

		// Check an earlier "to" date

		await toDateInput.fill(formatDateTimeForUI(toDate));
		await expect(addFilterButton).toBeDisabled();

		// Check a later "to" date

		toDate.setDate(toDate.getDate() + 5);
		await toDateInput.fill(formatDateTimeForUI(toDate));
		await expect(addFilterButton).toBeEnabled();
	}
);

test(
	'FDS Table content disappears after clicking "Show Details" and then "Expire"',
	{tag: '@LPD-69267'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const file1Title = `Title ${getRandomString()}`;
		const spaceName = 'Default';

		// Create an object

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file1Title,
			},
			applicationName,
			spaceName
		);

		await assetsPage.gotoAll();

		// Expire the asset

		await assetsPage.execItemAction({
			action: 'Show Details',
			filter: file1Title,
		});

		await expect(
			page.getByRole('heading', {name: file1Title})
		).toBeVisible();

		await page.getByLabel('Close the side panel.').click();

		await assetsPage.execItemAction({
			action: 'Expire',
			filter: file1Title,
		});

		await waitForAlert(page);

		// Check the table content

		await expect(
			assetsPage.getItem(file1Title).getByRole('cell', {name: 'expired'})
		).toBeVisible();

		await expect(
			assetsPage.dataSetFragmentPage.assetLink(file1Title)
		).toBeVisible();
	}
);

test(
	'All section places most recently modified content at the top',
	{tag: '@LPD-85725'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = 'Default';
		const thirdTitle = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: getRandomString(),
			},
			applicationName,
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: getRandomString(),
			},
			applicationName,
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: thirdTitle,
			},
			applicationName,
			spaceName
		);

		await expect(async () => {
			await assetsPage.gotoAll();

			await expect(page.locator('tbody tr').first()).toContainText(
				thirdTitle
			);
		}).toPass();
	}
);

test(
	'Review Date column shows "--" when unset and a date when set',
	{tag: '@LPD-79678'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = 'Default';
		const noReviewDateTitle = getRandomString();
		const reviewDateTitle = getRandomString();

		const toIsoDate = (date: Date) => date.toISOString().slice(0, 10);
		const tomorrow = new Date();
		tomorrow.setDate(tomorrow.getDate() + 1);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: noReviewDateTitle,
			},
			applicationName,
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				reviewDate: toIsoDate(tomorrow),
				title: reviewDateTitle,
			},
			applicationName,
			spaceName
		);

		await expect(async () => {
			await assetsPage.gotoAll();

			await expect(
				page.getByRole('row').filter({hasText: noReviewDateTitle})
			).toContainText('--');

			await expect(
				page.getByRole('row').filter({hasText: reviewDateTitle})
			).not.toContainText('--');
		}).toPass();
	}
);

test(
	'Content can be filtered by Create Date',
	{tag: ['@LPD-85551', '@LPD-87955', '@LPD-90051']},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const fileTitle = `Content ${getRandomString()}`;

		// Create a content

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: fileTitle,
			},
			applicationName,
			'Default'
		);

		await assetsPage.gotoAll();

		await expect(
			page.getByRole('cell', {exact: true, name: fileTitle})
		).toBeVisible();

		// Filter by Create Date

		const fromDate = new Date();

		fromDate.setDate(fromDate.getDate() - 1);

		await applyFDSDateTimeRangeFilter(page, {
			filter: 'Create Date',
			from: fromDate,
		});

		// Check the filter and the file

		await expect(
			page
				.getByRole('button', {name: /Create Date:/})
				.locator('.label-section')
		).toBeVisible();

		await expect(
			page.getByRole('cell', {exact: true, name: fileTitle})
		).toBeVisible();
	}
);

test(
	'Content can be filtered by Display Date',
	{tag: ['@LPD-85551', '@LPD-87955', '@LPD-90051']},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const matchingTitle = `Matching ${getRandomString()}`;
		const otherTitle = `Other ${getRandomString()}`;

		// Create the contents

		const matchingDisplayDate = new Date();

		matchingDisplayDate.setDate(matchingDisplayDate.getDate() + 5);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				displayDate: matchingDisplayDate.toISOString(),
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: matchingTitle,
			},
			applicationName,
			'Default'
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: otherTitle,
			},
			applicationName,
			'Default'
		);

		await assetsPage.gotoAll();

		await expect(
			page.getByRole('cell', {
				exact: true,
				name: matchingTitle,
			})
		).toBeVisible();
		await expect(
			page.getByRole('cell', {
				exact: true,
				name: otherTitle,
			})
		).toBeVisible();

		// Filter by Display Date

		const fromDate = new Date();
		const toDate = new Date();

		fromDate.setDate(fromDate.getDate() + 4);
		toDate.setDate(toDate.getDate() + 6);

		await applyFDSDateTimeRangeFilter(page, {
			filter: 'Display Date',
			from: fromDate,
			to: toDate,
		});

		// Check the matching content

		await expect(
			page
				.getByRole('button', {name: /Display Date:/})
				.locator('.label-section')
		).toBeVisible();

		await expect(
			page.getByRole('cell', {
				exact: true,
				name: matchingTitle,
			})
		).toBeVisible();
		await expect(
			page.getByRole('cell', {
				exact: true,
				name: otherTitle,
			})
		).not.toBeVisible();
	}
);

test(
	'Content can be filtered by Modified Date',
	{tag: ['@LPD-85551', '@LPD-87955', '@LPD-90051']},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const fileTitle = `Content ${getRandomString()}`;

		// Create a content

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: fileTitle,
			},
			applicationName,
			'Default'
		);

		await assetsPage.gotoAll();

		await expect(
			page.getByRole('cell', {exact: true, name: fileTitle})
		).toBeVisible();

		// Filter by Modified Date

		const fromDate = new Date();

		fromDate.setDate(fromDate.getDate() - 1);

		await applyFDSDateTimeRangeFilter(page, {
			filter: 'Modified Date',
			from: fromDate,
		});

		// Check the filter and the file

		await expect(
			page
				.getByRole('button', {name: /Modified Date:/})
				.locator('.label-section')
		).toBeVisible();

		await expect(
			page.getByRole('cell', {exact: true, name: fileTitle})
		).toBeVisible();
	}
);

test(
	"Author filter lists only members of the current user's Spaces",
	{tag: '@LPD-70773'},
	async ({apiHelpers, assetsPage, page}) => {
		const space1 = await apiHelpers.headlessAssetLibrary.createSpace();

		const space2 = await apiHelpers.headlessAssetLibrary.createSpace();

		const viewer = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[viewer.alternateName] = {
			name: viewer.givenName,
			password: 'test',
			surname: viewer.familyName,
		};

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			space1.externalReferenceCode,
			viewer.externalReferenceCode
		);

		const insider = await apiHelpers.headlessAdminUser.postUserAccount();
		const insiderFullName = `${insider.givenName} ${insider.familyName}`;

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			space1.externalReferenceCode,
			insider.externalReferenceCode
		);

		const outsider = await apiHelpers.headlessAdminUser.postUserAccount();
		const outsiderFullName = `${outsider.givenName} ${outsider.familyName}`;

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			space2.externalReferenceCode,
			outsider.externalReferenceCode
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: getRandomString(),
			},
			'cms/basic-web-contents',
			space1.name
		);

		await performUserSwitchViaApi(page, viewer.alternateName);

		await assetsPage.gotoAll();

		await page.getByRole('button', {name: 'Filter'}).click();
		await page.getByRole('menuitem', {name: 'Author'}).click();

		await expect(
			page.getByRole('checkbox', {name: insiderFullName})
		).toBeVisible();
		await expect(
			page.getByRole('checkbox', {name: outsiderFullName})
		).toBeHidden();
	}
);

test(
	"All section lists only content from the current user's Spaces",
	{tag: '@LPD-76453'},
	async ({apiHelpers, assetsPage, page}) => {
		const space1 = await apiHelpers.headlessAssetLibrary.createSpace();

		const space2 = await apiHelpers.headlessAssetLibrary.createSpace();

		const insideTitle = getRandomString();
		const outsideTitle = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: insideTitle,
			},
			'cms/basic-web-contents',
			space1.name
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: outsideTitle,
			},
			'cms/basic-web-contents',
			space2.name
		);

		const viewer = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[viewer.alternateName] = {
			name: viewer.givenName,
			password: 'test',
			surname: viewer.familyName,
		};

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			space1.externalReferenceCode,
			viewer.externalReferenceCode
		);

		await performUserSwitchViaApi(page, viewer.alternateName);

		await assetsPage.gotoAll();

		await expect(assetsPage.getItem(insideTitle)).toBeVisible();
		await expect(assetsPage.getItem(outsideTitle)).toBeHidden();

		// Check the search results

		await assetsPage.dataSetFragmentPage.search(outsideTitle);

		await expect(assetsPage.getItem(insideTitle)).toBeHidden();
		await expect(assetsPage.getItem(outsideTitle)).toBeHidden();
	}
);

test(
	'All section can be filtered by Space',
	{tag: '@LPD-91933'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const space1ContentTitle = `Content ${getRandomString()}`;
		const space2ContentTitle = `Content ${getRandomString()}`;

		// Create two Spaces

		const space1 = await apiHelpers.headlessAssetLibrary.createSpace();

		const space2 = await apiHelpers.headlessAssetLibrary.createSpace();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: space1ContentTitle,
			},
			applicationName,
			space1.name
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: space2ContentTitle,
			},
			applicationName,
			space2.name
		);

		// Check both contents

		await assetsPage.gotoAll();

		await expect(assetsPage.getItem(space1ContentTitle)).toBeVisible();
		await expect(assetsPage.getItem(space2ContentTitle)).toBeVisible();

		// Filter by Space

		await applyFDSSelectionFilter(page, {
			filter: 'Space',
			value: space1.name,
		});

		await expect(assetsPage.getItem(space1ContentTitle)).toBeVisible();
		await expect(assetsPage.getItem(space2ContentTitle)).toBeHidden();
	}
);

test(
	'All section can be filtered by Status',
	{tag: '@LPD-91933'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const approvedTitle = `Content A ${getRandomString()}`;
		const expiredTitle = `Content B ${getRandomString()}`;

		// Create the contents

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: approvedTitle,
			},
			applicationName,
			'Default'
		);

		const expiredEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: expiredTitle,
			},
			applicationName,
			'Default'
		);

		await apiHelpers.objectEntry.expireObjectEntryByExternalReferenceCode(
			applicationName,
			'Default',
			expiredEntry.externalReferenceCode
		);

		// Check both contents

		await assetsPage.gotoAll();

		await expect(assetsPage.getItem(approvedTitle)).toBeVisible();
		await expect(assetsPage.getItem(expiredTitle)).toBeVisible();

		// Filter by Status

		await applyFDSSelectionFilter(page, {
			filter: 'Status',
			value: 'Approved',
		});

		await expect(assetsPage.getItem(approvedTitle)).toBeVisible();
		await expect(assetsPage.getItem(expiredTitle)).toBeHidden();
	}
);

test(
	'All section can be filtered by Extension',
	{tag: '@LPD-96958'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';
		const jpgFileName = `JPG ${getRandomString()}.jpg`;
		const pngFileName = `PNG ${getRandomString()}.png`;

		// Upload a PNG and a JPG

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {fileBase64: _PNG_BASE64, name: pngFileName},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: pngFileName,
			},
			applicationName,
			'Default'
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: readFileSync(
						path.join(
							__dirname,
							'/dependencies/file_upload_image_1.jpg'
						)
					).toString('base64'),
					name: jpgFileName,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: jpgFileName,
			},
			applicationName,
			'Default'
		);

		// Check both files

		await assetsPage.gotoAll();

		await expect(assetsPage.getItem(pngFileName)).toBeVisible();
		await expect(assetsPage.getItem(jpgFileName)).toBeVisible();

		// The Extension filter is only offered when the aggregation finds
		// existing file extensions, so applying it also proves the filter is
		// mounted with options (LPD-96958).

		// Filter by Extension

		await applyFDSSelectionFilter(page, {
			filter: 'Extension',
			value: 'png',
		});

		await expect(assetsPage.getItem(pngFileName)).toBeVisible();
		await expect(assetsPage.getItem(jpgFileName)).toBeHidden();
	}
);

test(
	'All section can be filtered by Category',
	{tag: ['@LPD-85551', '@LPD-87956']},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const categorizedTitle = getRandomString();
		const categoryName = getRandomString();
		const uncategorizedTitle = getRandomString();
		const vocabularyName = getRandomString();

		// Create a category and contents

		const site =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('cms');

		const vocabulary =
			await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1}],
				assetTypes: [
					{
						required: false,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name: vocabularyName,
				siteId: site.id,
				visibilityType: 'PUBLIC',
			});

		const category =
			await apiHelpers.headlessAdminTaxonomy.postTaxonomyVocabularyTaxonomyCategory(
				{
					name: categoryName,
					vocabularyId: vocabulary.id,
				}
			);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				taxonomyCategoryIds: [category.id],
				title: categorizedTitle,
			},
			applicationName,
			'Default'
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: uncategorizedTitle,
			},
			applicationName,
			'Default'
		);

		// Check both contents

		await assetsPage.gotoAll();

		await expect(assetsPage.getItem(categorizedTitle)).toBeVisible();
		await expect(assetsPage.getItem(uncategorizedTitle)).toBeVisible();

		// Filter by Category

		await applyFDSSelectionFilter(page, {
			filter: 'Category',
			value: `${categoryName} (${vocabularyName})`,
		});

		await expect(assetsPage.getItem(categorizedTitle)).toBeVisible();
		await expect(assetsPage.getItem(uncategorizedTitle)).toBeHidden();
	}
);

test(
	'All section can be filtered by Tags',
	{tag: ['@LPD-85551', '@LPD-87956']},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const taggedTitle = getRandomString();
		const tagName = getRandomString();
		const untaggedTitle = getRandomString();

		// Create a tag and contents

		const site =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('cms');

		await apiHelpers.headlessAdminTaxonomy.postSiteKeyword({
			name: tagName,
			siteId: site.id,
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				keywords: [tagName],
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: taggedTitle,
			},
			applicationName,
			'Default'
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: untaggedTitle,
			},
			applicationName,
			'Default'
		);

		// Check both contents

		await assetsPage.gotoAll();

		await expect(assetsPage.getItem(taggedTitle)).toBeVisible();
		await expect(assetsPage.getItem(untaggedTitle)).toBeVisible();

		// Filter by Tags

		await applyFDSSelectionFilter(page, {
			filter: 'Tags',
			value: tagName,
		});

		await expect(assetsPage.getItem(taggedTitle)).toBeVisible();
		await expect(assetsPage.getItem(untaggedTitle)).toBeHidden();
	}
);

test(
	'All section can be filtered by Author',
	{tag: ['@LPD-85551', '@LPD-87956']},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const authorATitle = getRandomString();
		const authorBTitle = getRandomString();

		// Create a Space with authors

		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		const authorA = await apiHelpers.headlessAdminUser.postUserAccount();

		const authorAFullName = `${authorA.givenName} ${authorA.familyName}`;

		userData[authorA.alternateName] = {
			name: authorA.givenName,
			password: 'test',
			surname: authorA.familyName,
		};

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			space.externalReferenceCode,
			authorA.externalReferenceCode
		);
		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			space.externalReferenceCode,
			authorA.externalReferenceCode,
			['Asset Library Administrator']
		);

		const authorB = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[authorB.alternateName] = {
			name: authorB.givenName,
			password: 'test',
			surname: authorB.familyName,
		};

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			space.externalReferenceCode,
			authorB.externalReferenceCode
		);
		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			space.externalReferenceCode,
			authorB.externalReferenceCode,
			['Asset Library Administrator']
		);

		await performUserSwitchViaApi(page, authorA.alternateName);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: authorATitle,
			},
			applicationName,
			space.name
		);

		await performUserSwitchViaApi(page, authorB.alternateName);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: authorBTitle,
			},
			applicationName,
			space.name
		);

		await performUserSwitchViaApi(page, authorA.alternateName);

		// Check both contents

		await assetsPage.gotoAll();

		await expect(assetsPage.getItem(authorATitle)).toBeVisible();
		await expect(assetsPage.getItem(authorBTitle)).toBeVisible();

		// Filter by Author

		await applyFDSSelectionFilter(page, {
			filter: 'Author',
			value: authorAFullName,
		});

		await expect(assetsPage.getItem(authorATitle)).toBeVisible();
		await expect(assetsPage.getItem(authorBTitle)).toBeHidden();
	}
);

test(
	'All section can be filtered by Type',
	{tag: ['@LPD-85551', '@LPD-87956', '@LPD-97359']},
	async ({apiHelpers, assetsPage, page}) => {
		const documentTitle = getRandomString();
		const webContentTitle = getRandomString();

		// Create the contents

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: webContentTitle,
			},
			'cms/basic-web-contents',
			'Default'
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: _PNG_BASE64,
					name: `${documentTitle}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: documentTitle,
			},
			'cms/basic-documents',
			'Default'
		);

		// Check both contents

		await assetsPage.gotoAll();

		await expect(assetsPage.getItem(webContentTitle)).toBeVisible();
		await expect(assetsPage.getItem(documentTitle)).toBeVisible();

		// Filter by Type

		await applyFDSSelectionFilter(page, {
			filter: 'Type',
			value: 'Basic Web Content',
		});

		await expect(assetsPage.getItem(webContentTitle)).toBeVisible({
			timeout: 5000,
		});
		await expect(assetsPage.getItem(documentTitle)).toBeHidden();
	}
);

test(
	'All section renders content details in Cards view',
	{tag: ['@LPD-85551', '@LPD-87956']},
	async ({apiHelpers, assetsPage}) => {
		const contentTitle = getRandomString();

		// Create a content

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			'cms/basic-web-contents',
			'Default'
		);

		// Check the Cards view

		await assetsPage.gotoAll();

		await assetsPage.changeVisualizationMode('Cards');

		const card = assetsPage.getCardItem(contentTitle);

		await expect(card).toBeVisible();
		await expect(
			card.getByRole('link', {name: contentTitle})
		).toBeVisible();
		await expect(card.getByText('Approved', {exact: true})).toBeVisible();
	}
);

test(
	'External Video can be created from the All section',
	{tag: ['@LPD-85552', '@LPD-88276']},
	async ({apiHelpers, assetsPage, contentsPage}) => {
		const applicationName = 'cms/external-videos';
		const videoTitle = getRandomString();

		try {

			// Create an External Video

			await assetsPage.gotoAll();

			await assetsPage.createContent('External Video');

			await contentsPage.fillData([
				{label: 'Title', value: videoTitle},
				{label: 'Video URL', value: _EXTERNAL_VIDEO_URL},
			]);

			await contentsPage.saveContent();

			// Check the All section

			const item = assetsPage.getItem(videoTitle);

			await expect(item).toBeVisible();
			await expect(
				item.getByText('External Video', {exact: true})
			).toBeVisible();
			await expect(
				item.getByText('Approved', {exact: true})
			).toBeVisible();

			// Check the video thumbnail

			await assetsPage.changeVisualizationMode('Cards');

			const card = assetsPage.getCardItem(videoTitle);

			await expect(card).toBeVisible();

			await expect(card.locator('img')).toHaveAttribute(
				'src',
				new RegExp(_EXTERNAL_VIDEO_ID)
			);
		}
		finally {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					'Default',
					new URLSearchParams({pageSize: '100'})
				);

			const objectEntry = response.items.find(
				(item: {title: string}) => item.title === videoTitle
			);

			if (objectEntry) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					applicationName,
					String(objectEntry.id)
				);
			}
		}
	}
);

test(
	'The View modal navigates between files and contents in the All section',
	{tag: ['@LPD-85555', '@LPD-90032']},
	async ({apiHelpers, assetsPage, page}) => {
		const sharedToken = String(getRandomInt());

		const contentTitle = `Content ${sharedToken}`;
		const documentTitle = `Document ${sharedToken}`;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			'cms/basic-web-contents',
			'Default'
		);

		const documentObjectEntry =
			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: _PNG_BASE64,
						name: `${documentTitle}.png`,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: documentTitle,
				},
				'cms/basic-documents',
				'Default'
			);

		apiHelpers.data.push({
			id: documentObjectEntry.file.id,
			type: 'document',
		});

		await assetsPage.gotoAll();

		// Other tests share the Default space, so narrow the section down
		// to the two entries this test created before navigating between
		// them.

		await assetsPage.search(sharedToken);

		await assetsPage.execItemAction({
			action: 'View',
			filter: documentTitle,
		});

		const contentPreview = assetsPage.modal.body.locator(
			'iframe[title="Preview"]'
		);

		const downloadLink = page.getByRole('link', {name: 'Download'});

		const itemCounter =
			assetsPage.modal.container.getByText(/^\d+ of \d+$/);

		// Open the file preview

		await expect(page.getByTestId('modal-header-name')).toHaveText(
			documentTitle
		);

		await expect(downloadLink).toBeVisible();

		await expect(itemCounter).toHaveText('1 of 2');

		// Go to the content

		await assetsPage.modal.body.getByLabel('Next').click();

		await expect(page.getByTestId('modal-header-name')).toHaveText(
			contentTitle
		);

		await expect(itemCounter).toHaveText('2 of 2');

		await expect(contentPreview).toBeVisible();

		await expect(downloadLink).toBeHidden();
	}
);
