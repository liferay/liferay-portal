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
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitchViaApi, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

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
		const spaceName = `Space ${getRandomString()}`;
		const file1Title = `<b>Content ${getRandomString()}</b>`;
		let space = null;

		await test.step('Create a new Space with recycle bin disabled', async () => {
			space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {
					trashEnabled: false,
				},
				type: 'Space',
			});
		});

		await test.step('Create a content for that space', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: file1Title,
				},
				applicationName,
				spaceName
			);
		});

		await test.step('Delete content', async () => {
			await assetsPage.gotoAll();

			await assetsPage.execItemAction({
				action: 'Delete',
				filter: file1Title,
			});
		});

		await test.step('Accept confirmation modal', async () => {
			await expect(
				page.getByRole('heading', {name: `Delete "${file1Title}"`})
			).toBeVisible();

			await expect(
				page.getByText('You are about to delete the asset')
			).toBeVisible();

			await expect(
				page.locator('.liferay-modal .modal-dialog')
			).toHaveClass(/modal-dialog-centered/);

			await page.getByRole('button', {name: 'Delete'}).click();

			await waitForAlert(page, `${file1Title} was successfully deleted.`);

			await expect(
				page.getByRole('cell', {name: file1Title})
			).not.toBeVisible();
		});

		await test.step('delete created space', async () => {
			await apiHelpers.headlessAssetLibrary.deleteAssetLibrary(space.id);
		});
	}
);

test(
	'Only content folders will be displayed when copying content',
	{tag: '@LPD-72879'},
	async ({apiHelpers, assetsPage, page}) => {
		const file1Title = `Content ${getRandomString()}`;
		const file2Title = `File ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;

		await test.step('Create a new Space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});
		});

		await test.step('Create a content for that space', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: file1Title,
				},
				'cms/basic-web-contents',
				spaceName
			);
		});

		await test.step('Create a file for that space', async () => {
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
				spaceName
			);
		});

		await test.step('Copy content', async () => {
			await assetsPage.gotoAll();

			await assetsPage.execItemAction({
				action: 'Copy To',
				filter: file1Title,
				parentAction: 'Copy',
			});
		});

		await test.step('Check content folders', async () => {
			await page.getByLabel(spaceName).click();
			await expect(
				page.getByText('Showing 1 to 1 of 1 entries.')
			).toBeVisible();

			await expect(
				page.getByLabel('Contents', {exact: true})
			).toBeVisible();
		});

		await test.step('Copy file', async () => {
			await assetsPage.gotoAll();

			await assetsPage.execItemAction({
				action: 'Copy To',
				filter: file2Title,
				parentAction: 'Copy',
			});
		});

		await test.step('Check file folders', async () => {
			await page.getByLabel(spaceName).click();
			await expect(
				page.getByText('Showing 1 to 1 of 1 entries.')
			).toBeVisible();

			await expect(page.getByLabel('Files', {exact: true})).toBeVisible();
		});
	}
);

test(
	'Duplicating content creates a draft copy in the same Space',
	{tag: '@LPD-88346'},
	async ({apiHelpers, assetsPage, page}) => {
		const fileTitle = `Content ${getRandomString()}`;
		const spaceName = 'Default';

		await test.step('Create a content for the Space', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: fileTitle,
				},
				'cms/basic-web-contents',
				spaceName
			);
		});

		await test.step('Duplicate content', async () => {
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
		});

		await test.step('Duplicate the original again and check the suffix increments', async () => {
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
		});
	}
);

test(
	'Can view Share modal for added content',
	{tag: '@LPD-62554'},
	async ({apiHelpers, assetsPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const file1Title = `Title ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;
		let objectEntry1;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
			},
			type: 'Space',
		});

		try {
			objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: file1Title,
				},
				applicationName,
				spaceName
			);

			await assetsPage.gotoAll();

			await assetsPage.execItemAction({
				action: 'Share',
				filter: file1Title,
			});

			await expect(assetsPage.modal.title).toContainText(file1Title);
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry1.id)
			);
		}
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
		let objectEntry;

		try {
			objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: fileTitle,
				},
				applicationName,
				'Default'
			);

			await test.step('Go to All section', async () => {
				await assetsPage.gotoAll();
			});

			await test.step('Choose to filter by Expiration Date', async () => {
				await page.getByRole('button', {name: 'Filter'}).click();

				await page
					.getByRole('menuitem', {name: 'Expiration Date'})
					.click();
			});

			const fromDateInput = page.getByLabel('From');
			const toDateInput = page.getByLabel('To', {exact: true});

			const fromDate = new Date();
			const toDate = new Date();

			fromDate.setDate(fromDate.getDate() + 1);

			await test.step('Set "from" date to a future date', async () => {
				await fromDateInput.fill(formatDateTimeForUI(fromDate));
			});

			await test.step('Check that the "Add filter" button is disabled if "to" date is before "from date"', async () => {
				await toDateInput.fill(formatDateTimeForUI(toDate));
				await expect(addFilterButton).toBeDisabled();
			});

			await test.step('Check that the "Add filter" button is enabled if "to" date is after "from date"', async () => {
				toDate.setDate(toDate.getDate() + 5);
				await toDateInput.fill(formatDateTimeForUI(toDate));
				await expect(addFilterButton).toBeEnabled();
			});
		}
		finally {
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
	'FDS Table content disappears after clicking "Show Details" and then "Expire"',
	{tag: '@LPD-69267'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const file1Title = `Title ${getRandomString()}`;
		const spaceName = 'Default';
		let objectEntry;

		try {
			await test.step('Create an object and go to All section', async () => {
				objectEntry = await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						title: file1Title,
					},
					applicationName,
					spaceName
				);

				await assetsPage.gotoAll();
			});

			await test.step('Select the asset, open the Side Panel and then expire the asset', async () => {
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
			});

			await test.step('Expect that FDS table content is visible', async () => {
				await expect(
					assetsPage
						.getItem(file1Title)
						.getByRole('cell', {name: 'expired'})
				).toBeVisible();

				await expect(
					assetsPage.dataSetFragmentPage.assetLink(file1Title)
				).toBeVisible();
			});
		}
		finally {
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

		let noReviewEntry;
		let reviewEntry;

		try {
			noReviewEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: noReviewDateTitle,
				},
				applicationName,
				spaceName
			);

			reviewEntry = await apiHelpers.objectEntry.postObjectEntry(
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
		finally {
			for (const entry of [noReviewEntry, reviewEntry]) {
				if (entry) {
					await apiHelpers.objectEntry.deleteObjectEntry(
						applicationName,
						String(entry.id)
					);
				}
			}
		}
	}
);

test(
	'Content can be filtered by Create Date',
	{tag: ['@LPD-85551', '@LPD-87955', '@LPD-90051']},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const fileTitle = `Content ${getRandomString()}`;
		let objectEntry;

		try {
			await test.step('Create a content', async () => {
				objectEntry = await apiHelpers.objectEntry.postObjectEntry(
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
			});

			await test.step('Apply Create Date filter', async () => {
				const fromDate = new Date();

				fromDate.setDate(fromDate.getDate() - 1);

				await applyFDSDateTimeRangeFilter(page, {
					filter: 'Create Date',
					from: fromDate,
				});
			});

			await test.step('Check filter chip and entry are visible', async () => {
				await expect(
					page
						.getByRole('button', {name: /Create Date:/})
						.locator('.label-section')
				).toBeVisible();

				await expect(
					page.getByRole('cell', {exact: true, name: fileTitle})
				).toBeVisible();
			});
		}
		finally {
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
	'Content can be filtered by Display Date',
	{tag: ['@LPD-85551', '@LPD-87955', '@LPD-90051']},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const matchingTitle = `Matching ${getRandomString()}`;
		const otherTitle = `Other ${getRandomString()}`;
		let matchingEntry;
		let otherEntry;

		try {
			await test.step('Create matching and non-matching contents', async () => {
				const matchingDisplayDate = new Date();

				matchingDisplayDate.setDate(matchingDisplayDate.getDate() + 5);

				matchingEntry = await apiHelpers.objectEntry.postObjectEntry(
					{
						displayDate: matchingDisplayDate.toISOString(),
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						title: matchingTitle,
					},
					applicationName,
					'Default'
				);

				otherEntry = await apiHelpers.objectEntry.postObjectEntry(
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
			});

			await test.step('Apply Display Date filter', async () => {
				const fromDate = new Date();
				const toDate = new Date();

				fromDate.setDate(fromDate.getDate() + 4);
				toDate.setDate(toDate.getDate() + 6);

				await applyFDSDateTimeRangeFilter(page, {
					filter: 'Display Date',
					from: fromDate,
					to: toDate,
				});
			});

			await test.step('Check only the matching content remains visible', async () => {
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
			});
		}
		finally {
			if (matchingEntry) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					applicationName,
					String(matchingEntry.id)
				);
			}
			if (otherEntry) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					applicationName,
					String(otherEntry.id)
				);
			}
		}
	}
);

test(
	'Content can be filtered by Modified Date',
	{tag: ['@LPD-85551', '@LPD-87955', '@LPD-90051']},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const fileTitle = `Content ${getRandomString()}`;
		let objectEntry;

		try {
			await test.step('Create a content', async () => {
				objectEntry = await apiHelpers.objectEntry.postObjectEntry(
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
			});

			await test.step('Apply Modified Date filter', async () => {
				const fromDate = new Date();

				fromDate.setDate(fromDate.getDate() - 1);

				await applyFDSDateTimeRangeFilter(page, {
					filter: 'Modified Date',
					from: fromDate,
				});
			});

			await test.step('Check filter chip and entry are visible', async () => {
				await expect(
					page
						.getByRole('button', {name: /Modified Date:/})
						.locator('.label-section')
				).toBeVisible();

				await expect(
					page.getByRole('cell', {exact: true, name: fileTitle})
				).toBeVisible();
			});
		}
		finally {
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
	"Author filter lists only members of the current user's Spaces",
	{tag: '@LPD-70773'},
	async ({apiHelpers, assetsPage, page}) => {
		const space1 = await apiHelpers.headlessAssetLibrary.createAssetLibrary(
			{
				name: `Space ${getRandomString()}`,
				settings: {},
				type: 'Space',
			}
		);

		const space2 = await apiHelpers.headlessAssetLibrary.createAssetLibrary(
			{
				name: `Space ${getRandomString()}`,
				settings: {},
				type: 'Space',
			}
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
		const space1 = await apiHelpers.headlessAssetLibrary.createAssetLibrary(
			{
				name: `Space ${getRandomString()}`,
				settings: {},
				type: 'Space',
			}
		);

		const space2 = await apiHelpers.headlessAssetLibrary.createAssetLibrary(
			{
				name: `Space ${getRandomString()}`,
				settings: {},
				type: 'Space',
			}
		);

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

		await test.step("Search results are scoped to the viewer's Spaces", async () => {
			await assetsPage.dataSetFragmentPage.search(outsideTitle);

			await expect(assetsPage.getItem(insideTitle)).toBeHidden();
			await expect(assetsPage.getItem(outsideTitle)).toBeHidden();
		});
	}
);

test(
	'All section can be filtered by Space',
	{tag: '@LPD-91933'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const space1Name = `Space ${getRandomString()}`;
		const space2Name = `Space ${getRandomString()}`;
		const space1ContentTitle = `Content ${getRandomString()}`;
		const space2ContentTitle = `Content ${getRandomString()}`;

		await test.step('Create two Spaces with one content each', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: space1Name,
				settings: {},
				type: 'Space',
			});

			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: space2Name,
				settings: {},
				type: 'Space',
			});

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: space1ContentTitle,
				},
				applicationName,
				space1Name
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: space2ContentTitle,
				},
				applicationName,
				space2Name
			);
		});

		await test.step('Both contents are visible before filtering', async () => {
			await assetsPage.gotoAll();

			await expect(assetsPage.getItem(space1ContentTitle)).toBeVisible();
			await expect(assetsPage.getItem(space2ContentTitle)).toBeVisible();
		});

		await test.step('Filter by Space and check only the matching content is visible', async () => {
			await applyFDSSelectionFilter(page, {
				filter: 'Space',
				value: space1Name,
			});

			await expect(assetsPage.getItem(space1ContentTitle)).toBeVisible();
			await expect(assetsPage.getItem(space2ContentTitle)).toBeHidden();
		});
	}
);

test(
	'All section can be filtered by Status',
	{tag: '@LPD-91933'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const approvedTitle = `Content A ${getRandomString()}`;
		const expiredTitle = `Content B ${getRandomString()}`;

		await test.step('Create one approved and one expired content in the Default Space', async () => {
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
		});

		await test.step('Both contents are visible before filtering', async () => {
			await assetsPage.gotoAll();

			await expect(assetsPage.getItem(approvedTitle)).toBeVisible();
			await expect(assetsPage.getItem(expiredTitle)).toBeVisible();
		});

		await test.step('Filter by Status Approved and check only the approved content is visible', async () => {
			await applyFDSSelectionFilter(page, {
				filter: 'Status',
				value: 'Approved',
			});

			await expect(assetsPage.getItem(approvedTitle)).toBeVisible();
			await expect(assetsPage.getItem(expiredTitle)).toBeHidden();
		});
	}
);

test(
	'All section can be filtered by Extension',
	{tag: '@LPD-96958'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';
		const jpgFileName = `JPG ${getRandomString()}.jpg`;
		const pngFileName = `PNG ${getRandomString()}.png`;

		await test.step('Upload a PNG file and a JPG file to the Default Space', async () => {
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
		});

		await test.step('Both files are visible before filtering', async () => {
			await assetsPage.gotoAll();

			await expect(assetsPage.getItem(pngFileName)).toBeVisible();
			await expect(assetsPage.getItem(jpgFileName)).toBeVisible();
		});

		// The Extension filter is only offered when the aggregation finds
		// existing file extensions, so applying it also proves the filter is
		// mounted with options (LPD-96958).

		await test.step('Filter by Extension png and check only the PNG file is visible', async () => {
			await applyFDSSelectionFilter(page, {
				filter: 'Extension',
				value: 'png',
			});

			await expect(assetsPage.getItem(pngFileName)).toBeVisible();
			await expect(assetsPage.getItem(jpgFileName)).toBeHidden();
		});
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

		await test.step('Create a category and two contents, one categorized', async () => {
			const site =
				await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
					'cms'
				);

			const vocabulary =
				await apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary(
					{
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
					}
				);

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
		});

		await test.step('Both contents are visible before filtering', async () => {
			await assetsPage.gotoAll();

			await expect(assetsPage.getItem(categorizedTitle)).toBeVisible();
			await expect(assetsPage.getItem(uncategorizedTitle)).toBeVisible();
		});

		await test.step('Filter by Category and check only the categorized content is visible', async () => {
			await applyFDSSelectionFilter(page, {
				filter: 'Category',
				value: `${categoryName} (${vocabularyName})`,
			});

			await expect(assetsPage.getItem(categorizedTitle)).toBeVisible();
			await expect(assetsPage.getItem(uncategorizedTitle)).toBeHidden();
		});
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

		await test.step('Create a tag and two contents, one tagged', async () => {
			const site =
				await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
					'cms'
				);

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
		});

		await test.step('Both contents are visible before filtering', async () => {
			await assetsPage.gotoAll();

			await expect(assetsPage.getItem(taggedTitle)).toBeVisible();
			await expect(assetsPage.getItem(untaggedTitle)).toBeVisible();
		});

		await test.step('Filter by Tags and check only the tagged content is visible', async () => {
			await applyFDSSelectionFilter(page, {
				filter: 'Tags',
				value: tagName,
			});

			await expect(assetsPage.getItem(taggedTitle)).toBeVisible();
			await expect(assetsPage.getItem(untaggedTitle)).toBeHidden();
		});
	}
);

test(
	'All section can be filtered by Author',
	{tag: ['@LPD-85551', '@LPD-87956']},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const authorATitle = getRandomString();
		const authorBTitle = getRandomString();

		let authorAFullName: string;

		await test.step('Create a Space with two content authors', async () => {
			const space =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: getRandomString(),
					settings: {},
					type: 'Space',
				});

			const authorA =
				await apiHelpers.headlessAdminUser.postUserAccount();
			authorAFullName = `${authorA.givenName} ${authorA.familyName}`;

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

			const authorB =
				await apiHelpers.headlessAdminUser.postUserAccount();

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
		});

		await test.step('Both contents are visible before filtering', async () => {
			await assetsPage.gotoAll();

			await expect(assetsPage.getItem(authorATitle)).toBeVisible();
			await expect(assetsPage.getItem(authorBTitle)).toBeVisible();
		});

		await test.step("Filter by Author and check only that author's content is visible", async () => {
			await applyFDSSelectionFilter(page, {
				filter: 'Author',
				value: authorAFullName,
			});

			await expect(assetsPage.getItem(authorATitle)).toBeVisible();
			await expect(assetsPage.getItem(authorBTitle)).toBeHidden();
		});
	}
);

test(
	'All section can be filtered by Type',
	{tag: ['@LPD-85551', '@LPD-87956', '@LPD-97359']},
	async ({apiHelpers, assetsPage, page}) => {
		const documentTitle = getRandomString();
		const webContentTitle = getRandomString();

		await test.step('Create one web content and one document', async () => {
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
		});

		await test.step('Both are visible before filtering', async () => {
			await assetsPage.gotoAll();

			await expect(assetsPage.getItem(webContentTitle)).toBeVisible();
			await expect(assetsPage.getItem(documentTitle)).toBeVisible();
		});

		await test.step('Filtering by Type Basic Web Content shows only the web content', async () => {
			await applyFDSSelectionFilter(page, {
				filter: 'Type',
				value: 'Basic Web Content',
			});

			await expect(assetsPage.getItem(webContentTitle)).toBeVisible({
				timeout: 5000,
			});
			await expect(assetsPage.getItem(documentTitle)).toBeHidden();
		});
	}
);

test(
	'All section renders content details in Cards view',
	{tag: ['@LPD-85551', '@LPD-87956']},
	async ({apiHelpers, assetsPage}) => {
		const contentTitle = getRandomString();

		await test.step('Create a content', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentTitle,
				},
				'cms/basic-web-contents',
				'Default'
			);
		});

		await test.step('Switch to Cards view and check the card details', async () => {
			await assetsPage.gotoAll();

			await assetsPage.changeVisualizationMode('Cards');

			const card = assetsPage.getCardItem(contentTitle);

			await expect(card).toBeVisible();
			await expect(
				card.getByRole('link', {name: contentTitle})
			).toBeVisible();
			await expect(
				card.getByText('Approved', {exact: true})
			).toBeVisible();
		});
	}
);
