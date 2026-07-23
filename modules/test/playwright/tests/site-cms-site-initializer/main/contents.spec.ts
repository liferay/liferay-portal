/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForModal} from '../../../utils/waitFor';
import {waitForAlert} from '../../../utils/waitForAlert';
import {checkInZip} from '../../../utils/zip';
import {structureBuilderPagesTest} from '../structure-builder/fixtures/structureBuilderPagesTest';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {PicklistBuilderPage} from './pages/PicklistBuilderPage';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	loginTest(),
	fragmentsPagesTest,
	pageEditorPagesTest,
	structureBuilderPagesTest
);

test(
	'Shows client-side error when uploading a file that exceeds the maximum file size',
	{tag: '@LPD-79511'},
	async ({assetsPage, contentsPage, page, structureBuilderPage}) => {

		// Create a structure with an Upload field limited to 1MB

		const structureLabel = `StructureName${getRandomInt()}`;

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			page: structureBuilderPage,
		});

		await structureBuilderPage.addField('Upload');

		await structureBuilderPage.changeFieldSettings({
			maximumFileSize: 1,
			requestFile: 'computer',
		});

		await structureBuilderPage.publishStructure();

		// Create a content and upload a file that exceeds the limit

		await contentsPage.goto();

		await assetsPage.createContent(structureLabel);

		const fileChooserPromise = page.waitForEvent('filechooser');

		await page
			.getByRole('button', {exact: true, name: 'Select File'})
			.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles({
			buffer: Buffer.alloc(2 * 1024 * 1024),
			mimeType: 'image/jpeg',
			name: 'oversized-file.jpg',
		});

		// Check the error message is shown and the file is not uploaded

		await expect(
			page.getByText(
				'Please enter a file with a valid file size no larger than 1 MB.'
			)
		).toBeVisible();

		await expect(
			page.locator('.file-upload').getByText('oversized-file')
		).not.toBeVisible();
	}
);

test('Upload fields marked to show in the CMS library create visible files', async ({
	assetsPage,
	contentsPage,
	page,
	structureBuilderPage,
}) => {

	// Create a structure with a CMS library upload field

	const structureLabel = `StructureName${getRandomInt()}`;
	const contentTitle = getRandomString();

	await structureBuilderPage.createStructureFromData({
		label: structureLabel,
		page: structureBuilderPage,
	});

	await structureBuilderPage.addField('Upload');

	await structureBuilderPage.changeFieldSettings({
		label: 'Upload to CMS Library',
		name: 'uploadToCMSLibrary',
		requestFile: 'computer',
		showFilesInLibrary: true,
	});

	await structureBuilderPage.publishStructure();

	// Create a content for the structure and upload a file

	await contentsPage.goto();

	await contentsPage.createContent(structureLabel);

	await contentsPage.fillData([{label: 'Title', value: contentTitle}]);

	// Select the file from the computer

	const fileChooserPromise = page.waitForEvent('filechooser');

	await page.getByRole('button', {exact: true, name: 'Select File'}).click();

	const fileChooser = await fileChooserPromise;

	const fileName = 'file_upload_image_1.jpg';

	await fileChooser.setFiles(
		path.join(__dirname, `/dependencies/${fileName}`)
	);

	await expect(page.getByText('file_upload_image_1.jpg')).toBeVisible();

	// Save the content

	await contentsPage.saveContent();

	// Check the file is visible in the CMS Files

	await assetsPage.gotoFiles();

	await expect(
		assetsPage
			.getCardItem(fileName)
			.or(page.getByRole('row', {name: new RegExp(fileName)}))
	).toBeVisible();

	// Delete files

	await assetsPage.gotoFiles();

	await expect(page.getByText(fileName)).toBeVisible();

	await contentsPage.deleteContent(fileName);

	await contentsPage.goto();

	await contentsPage.deleteContent(contentTitle);
});

test(
	'Custom structure takes title as name field',
	{tag: '@LPD-62896'},
	async ({contentsPage, page, structureBuilderPage}) => {

		// Create structure

		const structureLabel = `Structure${getRandomInt()}`;
		const structureERC = getRandomString();

		await structureBuilderPage.createStructureFromData({
			erc: structureERC,
			label: structureLabel,
			name: structureLabel,
			page: structureBuilderPage,
		});

		// Go to CMS Contents

		await contentsPage.goto();

		// Create new content for recently created structure

		await contentsPage.createContent(structureLabel);

		// Fill data and save

		const title = getRandomString();

		await page.getByLabel('Title').fill(title);

		await contentsPage.saveContent();

		// Edit the content again and check title is taken as name field

		await contentsPage.editContent(title);

		await expect(page.getByText(`Edit ${title}`)).toBeVisible();

		// Delete content

		await contentsPage.goto();

		await contentsPage.deleteContent(title);
	}
);

test(
	'Can translate a content with a Select field',
	{tag: '@LPD-62179'},
	async ({
		contentsPage,
		context,
		localizationSelectPage,
		page,
		structureBuilderPage,
	}) => {

		// Create new structure

		const structureLabel = `StructureName${getRandomInt()}`;

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			page: structureBuilderPage,
		});

		// Add a Single Select field and Multiselect fields

		await structureBuilderPage.addField('Select from List');

		// Create new picklist from the button "New Picklist"

		await structureBuilderPage.selectFields([{label: 'Select from List'}]);

		await structureBuilderPage.changeFieldSettings({
			label: 'Single Select',
		});

		const pagePromise = context.waitForEvent('page');

		await page.getByText('New Picklist').click();

		const newPage = await pagePromise;

		// The picklist builder opens in a new tab

		const picklistBuilderPage = new PicklistBuilderPage(newPage);

		await expect(
			newPage.getByRole('heading', {name: 'New Picklist'})
		).toBeAttached();

		const picklistName = getRandomString();

		await newPage.getByLabel('Picklist Name').fill(picklistName);

		// Add options

		await picklistBuilderPage.addOption('Yellow');
		await picklistBuilderPage.addOption('Blue');

		// Save the picklist

		await newPage.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(newPage, 'was saved successfully.');

		// Select the picklist in both fields

		const picklistPicker = page.getByLabel('Picklist');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: picklistName}),
			trigger: picklistPicker,
		});

		await structureBuilderPage.addField('Select from List');

		await structureBuilderPage.selectFields([{label: 'Select from List'}]);

		await structureBuilderPage.changeFieldSettings({
			label: 'Multiselect',
			multiselection: true,
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: picklistName}),
			trigger: picklistPicker,
		});

		// Publish the structure

		await structureBuilderPage.publishStructure();

		// Now create a content for this structure

		await contentsPage.goto();

		await contentsPage.createContent(structureLabel);

		// Fill data in default language

		const contentTitle = getRandomString();

		await contentsPage.fillData([{label: 'Title', value: contentTitle}]);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'Yellow'}),
			trigger: page.locator('.select-from-list'),
		});

		const input = page
			.locator('.multiselector-dropdown')
			.getByRole('combobox');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.multiselector-dropdown__dropdown-menu')
				.getByRole('option', {name: 'Blue'}),
			trigger: input,
		});

		// Switch to spanish and change values

		await localizationSelectPage.switchLanguage('es-ES');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'Blue'}),
			trigger: page.locator('.select-from-list'),
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.multiselector-dropdown__dropdown-menu')
				.getByRole('option', {name: 'Yellow'}),
			trigger: input,
		});

		// Save content, edit it again and check values were persisted

		await contentsPage.saveContent();

		await contentsPage.editContent(contentTitle);

		await expect(page.locator('.label').getByText('Blue')).toBeVisible();

		await expect(page.getByPlaceholder('Choose an Option')).toHaveValue(
			'Yellow'
		);

		await localizationSelectPage.switchLanguage('es-ES');

		await expect(page.locator('.label').getByText('Blue')).toBeVisible();
		await expect(page.locator('.label').getByText('Yellow')).toBeVisible();

		await expect(page.getByLabel('Single Select')).toHaveValue('Blue');

		// Delete picklist

		const picklist = await picklistBuilderPage.getPicklist(picklistName);

		await picklistBuilderPage.deletePicklist(picklist.id);
	}
);

test(
	'Nested entries from referenced structures do not appear in View Usages',
	{tag: '@LPD-83177'},
	async ({contentsPage, page, structureBuilderPage, structuresPage}) => {

		// Create a referenced structure

		const referencedStructureLabel = `ReferencedStructureName${getRandomInt()}`;

		await structureBuilderPage.createStructureFromData({
			label: referencedStructureLabel,
			name: referencedStructureLabel,
			page: structureBuilderPage,
			publish: true,
		});

		// Create a structure that references the previous one

		const structureLabel = getRandomString();

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			name: `StructureName${getRandomInt()}`,
			page: structureBuilderPage,
			publish: false,
		});

		await structureBuilderPage.addReferencedStructures([
			referencedStructureLabel,
		]);

		await structureBuilderPage.publishStructure();

		// Create content of the new structure type and fill both titles

		const contentTitle = getRandomString();
		const nestedContentTitle = getRandomString();

		await contentsPage.goto();

		await contentsPage.createContent(structureLabel);

		await page.getByPlaceholder(`New ${structureLabel}`).fill(contentTitle);

		await page
			.locator('.lfr-layout-structure-item-form-relationship')
			.getByRole('textbox', {exact: true, name: 'Title'})
			.fill(nestedContentTitle);

		await contentsPage.saveContent();

		// Navigate to structures and view usages of the referenced structure

		await structuresPage.goto();

		await structuresPage.execItemAction({
			action: 'View Usages',
			filter: referencedStructureLabel,
		});

		// Assert the nested entry title is not shown

		await page.locator('.fds').waitFor();

		await expect(
			page.getByRole('row', {name: nestedContentTitle})
		).not.toBeVisible();

		// Delete content

		await contentsPage.goto();

		const card = page
			.locator('tr', {hasText: contentTitle})
			.or(page.locator('.card-row', {hasText: contentTitle}));

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Delete'}),
			trigger: card.locator('button'),
		});

		await waitForAlert(page, `Success:${contentTitle} was moved`, {
			autoClose: false,
		});
	}
);

test(
	'Content with Upload fragment opens new Item Selector',
	{tag: ['@LPD-67215', '@LPD-92364']},
	async ({apiHelpers, contentsPage, page, structureBuilderPage}) => {
		const applicationName = 'cms/basic-documents';
		const fileName = `file_${getRandomString()}.png`;
		const structureLabel = `StructureName${getRandomInt()}`;
		const contentTitle = getRandomString();
		let objectEntry;

		await test.step('Create a new file', async () => {
			objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: 'R0lGODlhAQABAAAAACw=',
						name: fileName,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: fileName,
				},
				applicationName,
				'Default'
			);

			apiHelpers.data.push({
				id: objectEntry.file.id,
				type: 'document',
			});
		});

		await test.step('Create a new structure with Upload field from Item Selector', async () => {
			await structureBuilderPage.createStructureFromData({
				label: structureLabel,
				page: structureBuilderPage,
			});

			await structureBuilderPage.addField('Upload');

			await structureBuilderPage.changeFieldSettings({
				label: 'Upload from DM',
				name: 'uploadFromDM',
				requestFile: 'document-library',
			});

			await structureBuilderPage.publishStructure();
		});

		await test.step('Create a content for this structure', async () => {
			await contentsPage.goto();

			await contentsPage.createContent(structureLabel);

			await contentsPage.fillData([
				{label: 'Title', value: contentTitle},
			]);

			// Upload buttons opens new Item Selector

			await page.getByRole('button', {name: 'Select File'}).click();

			await expect(
				page.getByTestId('visualization-mode-cards')
			).toBeVisible();

			await page.getByLabel(`Select ${fileName}`).check();

			await page
				.getByRole('button', {exact: true, name: 'Select'})
				.click();

			await expect(page.locator('.modal-header')).toBeHidden();

			await contentsPage.saveContent();

			await page.getByRole('link', {name: contentTitle}).click();

			await expect(page.getByText(`Edit ${contentTitle}`)).toBeVisible();

			await expect(page.getByText(fileName)).toBeVisible();
		});

		await test.step('Check preselected file', async () => {
			await page.getByRole('button', {name: 'Select File'}).click();

			await expect(
				page.getByTestId('visualization-mode-cards')
			).toBeVisible();

			await expect(page.getByText(`${fileName} Selected`)).toBeVisible();

			await expect(
				page.getByRole('button', {name: 'Clear'})
			).toBeVisible();
		});

		await test.step('Delete file', async () => {
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry.id)
			);
		});
	}
);

test(
	'Contents section places most recently modified content at the top',
	{tag: '@LPD-85725'},
	async ({apiHelpers, contentsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = 'Default';
		const firstTitle = getRandomString();
		const secondTitle = getRandomString();
		const thirdTitle = getRandomString();

		let firstEntry;
		let secondEntry;
		let thirdEntry;

		try {
			firstEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: firstTitle,
				},
				applicationName,
				spaceName
			);

			secondEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: secondTitle,
				},
				applicationName,
				spaceName
			);

			thirdEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: thirdTitle,
				},
				applicationName,
				spaceName
			);

			await expect(async () => {
				await contentsPage.goto();

				await expect(page.locator('tbody tr').first()).toContainText(
					thirdTitle
				);
			}).toPass();
		}
		finally {
			for (const entry of [firstEntry, secondEntry, thirdEntry]) {
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
	'Export for Translation a content asset',
	{tag: '@LPD-85361'},
	async ({apiHelpers, assetsPage, page}) => {
		const basicWebContentTitle = `Basic Web Content ${getRandomString()}`;

		await test.step('Create CMS asset', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: basicWebContentTitle,
				},
				'cms/basic-web-contents',
				'Default'
			);
		});

		await test.step('Exporting for Translation with a single target language', async () => {
			await assetsPage.gotoContents();

			await assetsPage.execItemAction({
				action: 'Export for Translation',
				filter: basicWebContentTitle,
			});

			await waitForModal({
				page,
			});

			await expect(
				page
					.locator('.modal-header')
					.getByText('Export for Translation')
			).toBeVisible();

			const filePath = await assetsPage.exportForTranslation(false, [
				'Spanish (Spain)',
			]);

			await expect(
				checkInZip(filePath, `${basicWebContentTitle}-en_US-es_ES.xlf`)
			).resolves.toBe(true);
		});

		await test.step('Exporting for Translation with a multiple target languages', async () => {
			await assetsPage.execItemAction({
				action: 'Export for Translation',
				filter: basicWebContentTitle,
			});

			await waitForModal({
				page,
			});

			await expect(
				page
					.locator('.modal-header')
					.getByText('Export for Translation')
			).toBeVisible();

			const filePath = await assetsPage.exportForTranslation(false, [
				'Chinese (China)',
				'Spanish (Spain)',
			]);

			await expect(
				checkInZip(filePath, `${basicWebContentTitle}-en_US-es_ES.xlf`)
			).resolves.toBe(true);

			await expect(
				checkInZip(filePath, `${basicWebContentTitle}-en_US-zh_CN.xlf`)
			).resolves.toBe(true);
		});
	}
);

test(
	'Creation menu for structure restricted to specific spaces lists only those spaces',
	{tag: '@LPD-87258'},
	async ({
		apiHelpers,
		contentsPage,
		homePage,
		page,
		structureBuilderPage,
	}) => {
		const allowedSpace =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: `Allowed ${getRandomString()}`,
				type: 'Space',
			});
		const forbiddenSpace =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: `Forbidden ${getRandomString()}`,
				type: 'Space',
			});

		const structureLabel = `Restricted${getRandomInt()}`;

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			page: structureBuilderPage,
			spaces: ['Default', allowedSpace.name],
		});

		const expectOnlyAcceptedSpaces = async () => {
			const dropdown = page.getByRole('listbox');

			await dropdown.waitFor({state: 'visible'});

			await expect(
				dropdown.getByRole('option', {name: 'Default'})
			).toBeVisible();
			await expect(
				dropdown.getByRole('option', {name: allowedSpace.name})
			).toBeVisible();
			await expect(
				dropdown.getByRole('option', {name: forbiddenSpace.name})
			).toHaveCount(0);
		};

		await test.step('From Contents creation menu', async () => {
			await contentsPage.goto();

			await contentsPage.newButton.click();

			await page.getByRole('menuitem', {name: structureLabel}).click();

			await page.getByRole('dialog').getByLabel('Space').click();

			await expectOnlyAcceptedSpaces();
		});

		await test.step('From Home Quick Actions', async () => {
			await homePage.goto();

			await page.getByRole('button', {name: structureLabel}).click();

			await page.getByLabel('SpaceMandatory').click();

			await expectOnlyAcceptedSpaces();
		});
	}
);

test(
	'Review Date column shows "--" when unset and a date when set',
	{tag: '@LPD-79678'},
	async ({apiHelpers, contentsPage, page}) => {
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
				await contentsPage.goto();

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
	'Shared content shows a shared icon in the Contents section only for the recipient',
	{tag: '@LPD-66045'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const contentTitle1 = `Content ${getRandomString()}`;
		const contentTitle2 = `Content ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		const objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle1,
			},
			applicationName,
			spaceName
		);

		const objectEntry2 = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle2,
			},
			applicationName,
			spaceName
		);

		try {
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

			await assetsPage.gotoContents();

			const contentRow1 = page
				.getByRole('row')
				.filter({has: page.getByRole('link', {name: contentTitle1})});

			await expect(contentRow1).toBeVisible();

			await expect(
				contentRow1.locator('.lexicon-icon-users').first()
			).toBeVisible();

			const contentRow2 = page
				.getByRole('row')
				.filter({has: page.getByRole('link', {name: contentTitle2})});

			await expect(contentRow2).toBeVisible();

			await expect(
				contentRow2.locator('.lexicon-icon-users')
			).toHaveCount(0);
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry1.id)
			);

			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry2.id)
			);
		}
	}
);
