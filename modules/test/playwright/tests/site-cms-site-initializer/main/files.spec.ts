/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import fs from 'fs/promises';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLoginViaApi,
	performLogout,
	performUserSwitch,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const validImageFileBase64 =
	'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';

const validPdfFileBase64 = readFileSync(
	path.join(__dirname, '/dependencies/test.pdf')
).toString('base64');

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

async function getBasicDocuments({
	apiHelpers,
	titles,
}: {
	apiHelpers: DataApiHelpers;
	titles: string[];
}) {
	const {items} =
		await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
			'cms/basic-documents',
			'Default',
			new URLSearchParams({pageSize: '100'})
		);

	return (items || []).filter((item: {title: string}) =>
		titles.includes(item.title)
	);
}

test(
	'Renders video preview for External Video entries',
	{tag: '@LPD-78868'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/external-videos';

		const VIDEO_ID = 'IqCSx3omX4o';

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: `title ${getRandomString()}`,
				videoURL: `https://www.youtube.com/watch?v=${VIDEO_ID}`,
			},
			applicationName,
			'Default'
		);

		apiHelpers.data.push({
			id: objectEntry.id,
			type: 'document',
		});

		await assetsPage.gotoFiles();

		await expect(
			page.getByRole('combobox', {name: 'Gallery View Selected'})
		).toBeVisible();

		const videoPreview = page.locator(
			`div.video-preview:has(iframe[src*="${VIDEO_ID}"])`
		);

		await expect(videoPreview).toBeVisible();
	}
);

test(
	'Opens in Gallery View by default',
	{tag: '@LPD-68467'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';
		const fileName = getRandomString();

		const parentFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				scopeKey: 'Default',
				title: getRandomString(),
			});

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: validImageFileBase64,
					name: `file_${fileName}.png`,
				},
				objectEntryFolderExternalReferenceCode:
					parentFolder.externalReferenceCode,
				title: `title ${fileName}`,
			},
			applicationName,
			'Default'
		);

		apiHelpers.data.push({
			id: objectEntry.file.id,
			type: 'document',
		});

		await assetsPage.gotoFiles();

		await page.getByRole('link', {name: parentFolder.title}).click();

		await expect(
			page.getByRole('combobox', {name: 'Gallery View Selected'})
		).toBeVisible();

		await expect(
			assetsPage.galleryPreview.getByRole('img', {
				name: `file_${fileName}.png`,
			})
		).toBeVisible();

		await expect(
			assetsPage.galleryNavigation.getByRole('button', {
				name: 'Previous',
			})
		).toBeDisabled();

		await expect(
			assetsPage.galleryNavigation.getByRole('button', {name: 'Next'})
		).toBeDisabled();

		await expect(assetsPage.galleryThumbnails.locator('.card')).toHaveCount(
			1
		);
	}
);

test(
	'Navigates between items in Gallery View',
	{tag: '@LPD-68467'},
	async ({apiHelpers, assetsPage, folderPage}) => {
		const applicationName = 'cms/basic-documents';

		const image1 = `image_${getRandomString()}`;
		const image2 = `image_${getRandomString()}`;

		const parentFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				scopeKey: 'Default',
				title: getRandomString(),
			});

		const objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: validImageFileBase64,
					name: `${image1}.png`,
				},
				objectEntryFolderExternalReferenceCode:
					parentFolder.externalReferenceCode,
				title: `title ${image1}`,
			},
			applicationName,
			'Default'
		);

		const objectEntry2 = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: validImageFileBase64,
					name: `${image2}.png`,
				},
				objectEntryFolderExternalReferenceCode:
					parentFolder.externalReferenceCode,
				title: `title ${image2}`,
			},
			applicationName,
			'Default'
		);

		const folderData =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode:
					parentFolder.externalReferenceCode,
				scopeKey: 'Default',
				title: `folder_${getRandomString()}`,
			});

		apiHelpers.data.push({
			id: objectEntry1.file.id,
			type: 'document',
		});

		apiHelpers.data.push({
			id: objectEntry2.file.id,
			type: 'document',
		});

		await assetsPage.gotoFiles();

		await assetsPage.changeVisualizationMode('Table');

		await folderPage.clickOption(parentFolder.title, 'View Folder');

		const carouselItems = [
			{
				label: 'image1',
				locator: assetsPage.galleryPreview.getByRole('img', {
					name: `${image1}.png`,
				}),
			},
			{
				label: 'image2',
				locator: assetsPage.galleryPreview.getByRole('img', {
					name: `${image2}.png`,
				}),
			},
			{
				label: 'folder',
				locator: assetsPage.galleryPreview.getByText(folderData.title, {
					exact: true,
				}),
			},
		];

		const anyCarouselItem = carouselItems
			.map((item) => item.locator)
			.reduce((acc, locator) => acc.or(locator));

		const currentLabel = async () => {
			for (const item of carouselItems) {
				if (await item.locator.isVisible()) {
					return item.label;
				}
			}

			return null;
		};

		const locatorFor = (label: string) =>
			carouselItems.find((item) => item.label === label)!.locator;

		await expect(anyCarouselItem).toBeVisible();

		const visitedOrder: string[] = [];

		for (let i = 0; i < carouselItems.length; i++) {
			const label = await currentLabel();

			expect(label).not.toBeNull();

			visitedOrder.push(label!);

			await assetsPage.navigateByGalleryArrows('Next');

			await expect(anyCarouselItem).toBeVisible();
		}

		expect(new Set(visitedOrder)).toEqual(
			new Set(['image1', 'image2', 'folder'])
		);

		await expect(locatorFor(visitedOrder[0])).toBeVisible();

		await assetsPage.navigateByGalleryArrows('Previous');

		await expect(
			locatorFor(visitedOrder[visitedOrder.length - 1])
		).toBeVisible();
	}
);

test(
	'Document can be downloaded from the Files section and saved correctly',
	{tag: '@LPD-54566'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';
		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64:
						'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVR42mNkqAcAAIUAgUW0RjgAAAAASUVORK5CYII=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: `title ${getRandomString()}`,
			},
			applicationName,
			'Default'
		);

		apiHelpers.data.push({
			id: objectEntry.file.id,
			type: 'document',
		});

		await assetsPage.gotoFiles();

		const downloadPromise = page.waitForEvent('download');
		await assetsPage.execCardItemAction({
			action: 'Download',
			filter: objectEntry.title,
		});

		const download = await downloadPromise;
		expect(download.suggestedFilename()).toBe(objectEntry.file.name);

		const downloadStat = await fs.stat(await download.path());
		expect(downloadStat.size).toBeGreaterThan(10);
	}
);

test(
	'The Space selector dialog is not shown when creating a Basic Document inside a folder when multiple Spaces exist',
	{tag: '@LPD-57827'},
	async ({apiHelpers, assetsPage, page}) => {
		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		// Check the number of Spaces

		const assetLibraries =
			await apiHelpers.headlessAssetLibrary.getAssetLibrariesPage(
				"type eq 'Space'"
			);

		expect(
			assetLibraries.length,
			'At least 2 Spaces should exist'
		).toBeGreaterThan(1);

		// Create a folder

		const folder = await apiHelpers.objectFolder.createObjectEntryFolder({
			scopeKey: space.name,
			title: getRandomString(),
		});

		// Go to the folder

		await assetsPage.gotoFolder(folder.id, folder.title);

		// Create a Basic Document

		await assetsPage.createContent('Single File');

		// Check the Space selector dialog

		await expect(page.getByRole('dialog')).not.toBeVisible();

		// Check the Space name

		await page.getByRole('heading', {name: 'New Basic Document'}).waitFor();

		const spaceSpan = page.locator(
			'//span[contains(@class,"sticker")]//following-sibling::span[1]'
		);

		await expect(spaceSpan).toContainText(space.name);
	}
);

test(
	'There is a View action for items the Files section',
	{tag: '@LPD-58720'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64:
						'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVR42mNkqAcAAIUAgUW0RjgAAAAASUVORK5CYII=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: `Image ${getRandomString()}`,
			},
			applicationName,
			'Default'
		);

		apiHelpers.data.push({
			id: objectEntry.file.id,
			type: 'document',
		});

		await assetsPage.gotoFiles();

		await assetsPage.execCardItemAction({
			action: 'View',
			filter: objectEntry.title,
		});

		await expect(page.getByRole('dialog')).toBeVisible();

		await expect(page.getByTestId('modal-header-name')).toHaveText(
			objectEntry.title
		);
		await expect(page.getByRole('link', {name: 'Download'})).toBeVisible();

		await expect(
			assetsPage.modal.body.getByText('No preview available')
		).toBeVisible();
	}
);

test(
	'Can navigate through items in the Files section',
	{tag: '@LPD-59866'},
	async ({apiHelpers, assetsPage, folderPage, page}) => {
		const applicationName = 'cms/basic-documents';

		const parentFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				scopeKey: 'Default',
				title: getRandomString(),
			});

		const objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64:
						'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVR42mNkqAcAAIUAgUW0RjgAAAAASUVORK5CYII=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode:
					parentFolder.externalReferenceCode,
				title: `Image ${getRandomString()}`,
			},
			applicationName,
			'Default'
		);

		const objectEntry2 = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64:
						'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVR42mNkqAcAAIUAgUW0RjgAAAAASUVORK5CYII=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode:
					parentFolder.externalReferenceCode,
				title: `Image ${getRandomString()}`,
			},
			applicationName,
			'Default'
		);

		await apiHelpers.objectFolder.createObjectEntryFolder({
			parentObjectEntryFolderExternalReferenceCode:
				parentFolder.externalReferenceCode,
			scopeKey: 'Default',
			title: `Folder ${getRandomString()}`,
		});

		apiHelpers.data.push({
			id: objectEntry1.file.id,
			type: 'document',
		});

		apiHelpers.data.push({
			id: objectEntry2.file.id,
			type: 'document',
		});

		await assetsPage.gotoFiles();

		await assetsPage.changeVisualizationMode('Table');

		await folderPage.clickOption(parentFolder.title, 'View Folder');

		await assetsPage.execCardItemAction({
			action: 'View',
			filter: objectEntry2.title,
		});

		// Check that the navigation skips the folder

		await expect(page.getByText(/\d+ of 2/)).toBeVisible();

		// Go to the next item

		const modalTitle = page.locator('.modal-title');

		await expect(modalTitle).not.toBeEmpty();

		const initialTitle = await modalTitle.innerText();

		const otherTitle = initialTitle.includes(objectEntry1.title)
			? objectEntry2.title
			: objectEntry1.title;

		await assetsPage.modal.body.getByLabel('Next').click();

		await expect(modalTitle.getByText(otherTitle)).toBeVisible();

		await expect(page.getByText(/\d+ of 2/)).toBeVisible();

		// Open the info panel

		await page.getByLabel('Show Details').click();
		await expect(
			page.getByRole('tab', {name: 'Comments'})
		).toBeInViewport();
		await expect(page.getByText('Metadata')).toBeVisible();
		await expect(page.getByLabel('Show Details')).toHaveClass(/active/);

		// Add a comment from the Comments tab

		await expect(page.getByLabel('Show Comments')).toHaveCount(0);

		await page.getByRole('tab', {name: 'Comments'}).click();

		await expect(page.getByRole('tab', {name: 'Details'})).toBeVisible();

		const commentText = getRandomString();
		await assetsPage.modal.body.getByRole('paragraph').fill(commentText);
		await page.getByRole('button', {name: 'Save'}).click();

		await expect(page.getByText(commentText)).toBeVisible();
	}
);

test(
	'Can click on title in Cards View',
	{tag: '@LPD-67612'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64:
						'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVR42mNkqAcAAIUAgUW0RjgAAAAASUVORK5CYII=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: `title ${getRandomString()}`,
			},
			applicationName,
			'Default'
		);

		try {
			apiHelpers.data.push({
				id: objectEntry.file.id,
				type: 'document',
			});

			await assetsPage.gotoFiles();
			await assetsPage.changeVisualizationMode('Cards');

			await page.getByRole('link', {name: objectEntry.title}).click();

			await expect(
				page.getByRole('heading', {name: `Edit ${objectEntry.title}`})
			).toBeVisible();
		}
		finally {
			await page.getByLabel('Back').click();

			await assetsPage.changeVisualizationMode('Gallery');
		}
	}
);

test(
	'Space Member can preview image files',
	{tag: '@LPD-70422'},
	async ({apiHelpers, assetsPage, page, spaceSummaryPage}) => {
		const applicationName = 'cms/basic-documents';
		const fileTitle = getRandomString();
		const spaceName = 'Default';

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: validImageFileBase64,
					name: `file_${fileTitle}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			applicationName,
			spaceName
		);

		try {
			apiHelpers.data.push({
				id: objectEntry.file.id,
				type: 'document',
			});

			// Add a user to the Space

			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			await spaceSummaryPage.goto(spaceName);

			await spaceSummaryPage.addUserOrUserGroup(user.name, 'users');

			// Log in as the Space member

			await performLogout(page);

			await performLogin(page, user.alternateName);

			await assetsPage.gotoFiles();

			// Check the image preview

			await assetsPage.changeVisualizationMode('Table');

			await assetsPage.execItemAction({
				action: 'View',
				filter: objectEntry.title,
			});

			await expect(page.getByRole('dialog')).toBeVisible();

			await expect(
				page.getByRole('link', {name: fileTitle})
			).toBeVisible();

			const imageSrc = await page
				.getByRole('img', {name: fileTitle})
				.getAttribute('src');

			expect(imageSrc.includes(fileTitle)).toBeTruthy();
		}
		finally {
			await performLogout(page);

			await performLoginViaApi({page, screenName: 'test'});
		}
	}
);

test(
	'Shared file shows a shared icon in the Files section only for the recipient',
	{tag: '@LPD-66045'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';

		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		const objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64:
						'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVR42mNkqAcAAIUAgUW0RjgAAAAASUVORK5CYII=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: `File ${getRandomString()}`,
			},
			applicationName,
			space.name
		);

		const objectEntry2 = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64:
						'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVR42mNkqAcAAIUAgUW0RjgAAAAASUVORK5CYII=',
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: `File ${getRandomString()}`,
			},
			applicationName,
			space.name
		);

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const cmsAdminRole =
			await apiHelpers.headlessAdminUser.getRoleByName(
				'CMS Administrator'
			);

		await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
			cmsAdminRole.id,
			Number(user.id)
		);

		await apiHelpers.objectEntry.postObjectEntryCollaborators(
			[
				{
					actionIds: ['VIEW'],
					id: user.id,
					share: true,
					type: 'User',
				},
			],
			applicationName,
			objectEntry1.id
		);

		await performUserSwitch(page, user.alternateName);

		await assetsPage.gotoFiles();

		await assetsPage.changeVisualizationMode('Table');

		const fileRow1 = page.getByRole('row').filter({
			has: page.getByRole('link', {name: objectEntry1.title}),
		});

		await expect(fileRow1).toBeVisible();

		await expect(
			fileRow1.locator('.lexicon-icon-users').first()
		).toBeVisible();

		const fileRow2 = page.getByRole('row').filter({
			has: page.getByRole('link', {name: objectEntry2.title}),
		});

		await expect(fileRow2).toBeVisible();

		await expect(fileRow2.locator('.lexicon-icon-users')).toHaveCount(0);
	}
);

test(
	'Uploads a file dropped onto a folder into that folder in Gallery View',
	{tag: ['@LPD-87681', '@LPD-102030']},
	async ({apiHelpers, assetsPage, page}) => {
		const fileName = 'file_upload_image_1.jpeg';

		const parentFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				scopeKey: 'Default',
				title: getRandomString(),
			});

		const targetFolder =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode:
					parentFolder.externalReferenceCode,
				scopeKey: 'Default',
				title: getRandomString(),
			});

		try {

			// Open the parent folder in Gallery View

			await assetsPage.gotoFiles();

			await page.getByRole('link', {name: parentFolder.title}).click();

			await expect(
				page.getByRole('combobox', {
					name: 'Gallery View Selected',
				})
			).toBeVisible();

			const folderThumbnail = assetsPage.galleryThumbnails.locator(
				'.fds-gallery-view__thumbnail',
				{hasText: targetFolder.title}
			);

			const dataTransfer = await page.evaluateHandle(
				(data) => {
					const dataTransfer = new DataTransfer();

					dataTransfer.items.add(
						new File(
							[data.toString('hex')],
							'file_upload_image_1.jpeg',
							{type: 'image/jpg'}
						)
					);

					return dataTransfer;
				},
				readFileSync(
					path.join(
						__dirname,
						'/dependencies/file_upload_image_1.jpg'
					)
				)
			);

			// Drag the file over the folder

			await folderThumbnail.dispatchEvent('dragenter', {
				dataTransfer,
			});
			await folderThumbnail.dispatchEvent('dragover', {dataTransfer});

			await expect(folderThumbnail).toHaveClass(/drop-target/);

			// Drop the file onto the folder

			await folderThumbnail.dispatchEvent('drop', {dataTransfer});

			await expect(assetsPage.modal.container).toBeVisible();

			await expect(assetsPage.modal.title).toContainText(
				'Upload Multiple Files'
			);

			await expect(assetsPage.modal.body).toContainText(fileName);

			// Check the file in the target folder

			await assetsPage.modal.footer
				.getByRole('button', {name: /^Upload/})
				.click();

			await waitForAlert(
				page,
				'1 file was successfully uploaded to Default space.'
			);

			const [document] = await getBasicDocuments({
				apiHelpers,
				titles: [fileName],
			});

			expect(document.objectEntryFolderExternalReferenceCode).toBe(
				targetFolder.externalReferenceCode
			);
		}
		finally {
			for (const document of await getBasicDocuments({
				apiHelpers,
				titles: [fileName],
			})) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					'cms/basic-documents',
					String(document.id)
				);
			}
		}
	}
);

test(
	'Renders the video preview in the View modal',
	{tag: ['@LPD-85555', '@LPD-90032']},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/external-videos';

		const VIDEO_ID = 'IqCSx3omX4o';

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: `Video ${getRandomString()}`,
				videoURL: `https://www.youtube.com/watch?v=${VIDEO_ID}`,
			},
			applicationName,
			'Default'
		);

		await assetsPage.gotoFiles();

		await assetsPage.execCardItemAction({
			action: 'View',
			filter: objectEntry.title,
		});

		await expect(page.getByTestId('modal-header-name')).toHaveText(
			objectEntry.title
		);

		await expect(
			assetsPage.modal.body.locator(
				`div.video-preview:has(iframe[src*="${VIDEO_ID}"])`
			)
		).toBeVisible();
	}
);

test(
	'Renders the PDF preview in the View modal',
	{tag: ['@LPD-85555', '@LPD-90032']},
	async ({apiHelpers, assetsPage}) => {
		const applicationName = 'cms/basic-documents';

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: validPdfFileBase64,
					name: `file_${getRandomString()}.pdf`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: `Document ${getRandomString()}`,
			},
			applicationName,
			'Default'
		);

		apiHelpers.data.push({
			id: objectEntry.file.id,
			type: 'document',
		});

		const pageIndicator = assetsPage.modal.body.getByRole('button', {
			name: 'Click to jump to a page.',
		});

		const previewImage = assetsPage.modal.body.locator(
			'img.preview-file-document'
		);

		// Check the first page of the PDF

		// The portal rasterizes the PDF asynchronously and renders "No
		// Preview Available" until it finishes, so reload the section
		// until the preview is served.

		await expect(async () => {
			await assetsPage.gotoFiles();

			await assetsPage.execCardItemAction({
				action: 'View',
				filter: objectEntry.title,
			});

			await expect(previewImage).toHaveAttribute(
				'src',
				/previewFileIndex=1/,
				{timeout: 5000}
			);
		}).toPass({timeout: 90000});

		await expect(pageIndicator).toHaveText(/^Page 1 \/ \d+$/);

		// Go to the second page of the PDF

		await assetsPage.modal.body
			.getByRole('button', {name: 'Page Below'})
			.click();

		await expect(previewImage).toHaveAttribute('src', /previewFileIndex=2/);

		await expect(pageIndicator).toHaveText(/^Page 2 \/ \d+$/);
	}
);

test(
	'There is a View action for files in the Files section of a Space',
	{tag: ['@LPD-85555', '@LPD-90032']},
	async ({apiHelpers, assetsPage, page}) => {
		const space = await apiHelpers.headlessAssetLibrary.createSpace();

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: validImageFileBase64,
					name: `file_${getRandomString()}.png`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: `Document ${getRandomString()}`,
			},
			'cms/basic-documents',
			space.name
		);

		await assetsPage.gotoSpaceFiles(space.name);

		await assetsPage.execItemAction({
			action: 'View',
			filter: objectEntry.title,
		});

		await expect(page.getByRole('dialog')).toBeVisible();

		await expect(page.getByTestId('modal-header-name')).toHaveText(
			objectEntry.title
		);
	}
);
