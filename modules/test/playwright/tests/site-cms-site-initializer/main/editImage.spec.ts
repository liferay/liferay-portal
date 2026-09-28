/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	performUserSwitchViaApi,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {
	SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE,
	SITE_CMS_SPACE_NAME,
} from '../../setup/site-cms-site/constants/space';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {AssetsPage} from './pages/AssetsPage';

const applicationName = 'cms/basic-documents';
const objectDefinitionName = 'CMSBasicDocument';
const spaceName = SITE_CMS_SPACE_NAME;

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

const imageFileBase64 = readFileSync(
	path.join(__dirname, 'dependencies', 'file_upload_image_1.jpg')
).toString('base64');

async function editImage(
	assetsPage: AssetsPage,
	page: Page,
	title: string,
	fileName: string
) {
	await assetsPage.execCardItemAction({
		action: 'Edit Image',
		filter: title,
	});

	const dialog = page.getByRole('dialog');

	await expect(dialog.getByRole('heading', {name: fileName})).toBeVisible();

	const imageEditor = dialog.locator('.image-editor');

	await imageEditor
		.getByRole('button', {name: 'Rotate 90 Degrees Clockwise'})
		.click();

	await imageEditor.getByRole('button', {name: 'Save'}).click();

	await waitForAlert(page, 'The image was edited successfully.');

	await expect(dialog).toBeHidden();
}

test(
	'Saves the edited image as a new version of the same file',
	{tag: '@LPD-92181'},
	async ({apiHelpers, assetsPage, page}) => {
		const title = `image ${getRandomString()}`;

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageFileBase64,
					name: `file_${getRandomString()}.jpg`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title,
			},
			applicationName,
			spaceName
		);

		try {
			await assetsPage.gotoFiles();

			await assetsPage.changeVisualizationMode('Gallery');

			const thumbnail = assetsPage.getCardItem(title).locator('img');

			await expect(thumbnail).toHaveAttribute('src', /imageThumbnail=1/);

			const thumbnailSrc = await thumbnail.getAttribute('src');

			await editImage(assetsPage, page, title, objectEntry.file.name);

			await expect(thumbnail).toHaveAttribute('src', /imageThumbnail=1/);
			await expect(thumbnail).not.toHaveAttribute('src', thumbnailSrc);

			const editedObjectEntry =
				await apiHelpers.objectEntry.getObjectEntryById(
					applicationName,
					String(objectEntry.id)
				);

			expect(editedObjectEntry.systemProperties.version.number).toBe(2);
			expect(editedObjectEntry.file.link.href).not.toBe(
				objectEntry.file.link.href
			);
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry.id)
			);
		}
	}
);

test(
	'Offers the Edit Image action only on editable images in the All and Files sections',
	{tag: '@LPD-92181'},
	async ({apiHelpers, assetsPage, page}) => {
		const imageTitle = `image ${getRandomString()}`;
		const textTitle = `text ${getRandomString()}`;
		const vectorTitle = `vector ${getRandomString()}`;

		const imageObjectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageFileBase64,
					name: `file_${getRandomString()}.jpg`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: imageTitle,
			},
			applicationName,
			spaceName
		);

		const textObjectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64:
						Buffer.from(getRandomString()).toString('base64'),
					name: `file_${getRandomString()}.txt`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: textTitle,
			},
			applicationName,
			spaceName
		);

		const vectorObjectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: Buffer.from(
						'<svg xmlns="http://www.w3.org/2000/svg" width="1" height="1"/>'
					).toString('base64'),
					name: `file_${getRandomString()}.svg`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: vectorTitle,
			},
			applicationName,
			spaceName
		);

		const expectEditImageOnlyOnTheImage = async () => {
			await page
				.getByRole('button', {name: `${imageTitle} Actions`})
				.click();

			await expect(
				page.getByRole('menuitem', {exact: true, name: 'Edit Image'})
			).toBeVisible();

			await page.keyboard.press('Escape');

			for (const title of [textTitle, vectorTitle]) {
				await page
					.getByRole('button', {name: `${title} Actions`})
					.click();

				await expect(
					page.getByRole('menuitem', {exact: true, name: 'Download'})
				).toBeVisible();
				await expect(
					page.getByRole('menuitem', {
						exact: true,
						name: 'Edit Image',
					})
				).toBeHidden();

				await page.keyboard.press('Escape');
			}
		};

		try {
			await assetsPage.gotoAll();

			await expectEditImageOnlyOnTheImage();

			await assetsPage.gotoFiles();

			await assetsPage.changeVisualizationMode('Gallery');

			await expectEditImageOnlyOnTheImage();
		}
		finally {
			for (const objectEntry of [
				imageObjectEntry,
				textObjectEntry,
				vectorObjectEntry,
			]) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					applicationName,
					String(objectEntry.id)
				);
			}
		}
	}
);

test(
	'Sends the edited image through the workflow before it is published',
	{tag: '@LPD-92181'},
	async ({apiHelpers, assetsPage, page}) => {
		const title = `image ${getRandomString()}`;
		const workflowSpaceName = `Space ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: workflowSpaceName,
			type: 'Space',
		});

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageFileBase64,
					name: `file_${getRandomString()}.jpg`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title,
			},
			applicationName,
			workflowSpaceName
		);

		const objectDefinition =
			await apiHelpers.objectAdmin.getObjectDefinitionByName(
				objectDefinitionName
			);

		const workflowDefinition =
			await apiHelpers.headlessAdminWorkflow.getWorkflowDefinitionByName(
				'Single Approver'
			);

		await apiHelpers.headlessAdminWorkflow.postWorkflowDefinitionLink(
			objectDefinition.className,
			space.siteId,
			workflowDefinition.id,
			workflowDefinition.name,
			Number(workflowDefinition.version)
		);

		await assetsPage.gotoFiles();

		await assetsPage.changeVisualizationMode('Gallery');

		await editImage(assetsPage, page, title, objectEntry.file.name);

		const pendingObjectEntry =
			await apiHelpers.objectEntry.getObjectEntryById(
				applicationName,
				String(objectEntry.id)
			);

		expect(pendingObjectEntry.status.label).toBe('pending');

		const userAccount =
			await apiHelpers.headlessAdminUser.getMyUserAccount();

		let workflowTask;

		await expect(async () => {
			workflowTask =
				await apiHelpers.headlessAdminWorkflow.getWorkflowTaskByAsset(
					objectDefinition.className,
					String(objectEntry.id)
				);

			expect(workflowTask).toBeTruthy();
		}).toPass();

		await apiHelpers.headlessAdminWorkflow.postAssignTaskToUser(
			workflowTask.id,
			userAccount.id
		);

		await apiHelpers.headlessAdminWorkflow.postWorkflowTaskChangeTransition(
			workflowTask.id,
			'approve'
		);

		await expect(async () => {
			const approvedObjectEntry =
				await apiHelpers.objectEntry.getObjectEntryById(
					applicationName,
					String(objectEntry.id)
				);

			expect(approvedObjectEntry.status.label).toBe('approved');
			expect(approvedObjectEntry.systemProperties.version.number).toBe(2);
		}).toPass();
	}
);

test(
	'Hides the Edit Image action from a member who cannot update the file',
	{tag: '@LPD-92181'},
	async ({apiHelpers, assetsPage, page}) => {
		const title = `image ${getRandomString()}`;

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageFileBase64,
					name: `file_${getRandomString()}.jpg`,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title,
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

			await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
				SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE,
				user.externalReferenceCode
			);

			await performUserSwitchViaApi(page, user.alternateName);

			await assetsPage.gotoFiles();

			await assetsPage.changeVisualizationMode('Gallery');

			await page.getByRole('button', {name: `${title} Actions`}).click();

			await expect(
				page.getByRole('menuitem', {exact: true, name: 'Download'})
			).toBeVisible();
			await expect(
				page.getByRole('menuitem', {exact: true, name: 'Edit Image'})
			).toBeHidden();
		}
		finally {
			await performLogout(page);

			await performLoginViaApi({page, screenName: 'test'});

			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry.id)
			);
		}
	}
);
