/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const _CONTENT_APPLICATION_NAME = 'cms/basic-web-contents';

const _FILE_APPLICATION_NAME = 'cms/basic-documents';

const _OBJECT_DEFINITION_NAME = 'CMSBasicWebContent';

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
	'Duplicating a content under workflow creates a draft copy without a workflow instance',
	{tag: '@LPD-103464'},
	async ({apiHelpers, assetsPage, page}) => {
		const spaceName = `Space ${getRandomString()}`;
		const title = `Content ${getRandomString()}`;

		let objectDefinitionClassName: string;
		let pendingEntryId: number;

		await test.step('Link Single Approver to the content type in a new Space', async () => {
			const space =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: spaceName,
					type: 'Space',
				});

			const objectDefinition =
				await apiHelpers.objectAdmin.getObjectDefinitionByName(
					_OBJECT_DEFINITION_NAME
				);

			objectDefinitionClassName = objectDefinition.className;

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
		});

		await test.step('The original content enters the workflow', async () => {
			const pendingEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title,
				},
				_CONTENT_APPLICATION_NAME,
				spaceName
			);

			pendingEntryId = pendingEntry.id;

			expect(pendingEntry.status.label).toBe('pending');

			expect(
				await apiHelpers.headlessAdminWorkflow.getWorkflowTaskByAsset(
					objectDefinitionClassName,
					String(pendingEntryId)
				)
			).not.toBeNull();
		});

		await test.step('Duplicate the content', async () => {
			await assetsPage.gotoSpaceContents(spaceName);

			await assetsPage.execItemAction({
				action: 'Duplicate',
				filter: title,
				parentAction: 'Copy',
			});

			await expect(
				page.getByRole('link', {exact: true, name: `${title} (Copy)`})
			).toBeVisible();
		});

		await test.step('The copy is a draft and started no workflow', async () => {
			const copy = await _getEntryByTitle(
				apiHelpers,
				spaceName,
				`${title} (Copy)`
			);

			expect(copy.id).not.toBe(pendingEntryId);

			expect(copy.status.label).toBe('draft');

			expect(
				await apiHelpers.headlessAdminWorkflow.getWorkflowTaskByAsset(
					objectDefinitionClassName,
					String(copy.id)
				)
			).toBeNull();
		});
	}
);

test(
	'A duplicated content keeps the references embedded in its body',
	{tag: '@LPD-103464'},
	async ({apiHelpers, assetsPage, page}) => {
		const linkedTitle = `Content ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;
		const title = `Content ${getRandomString()}`;

		let content: string;

		await test.step('Create a content embedding a document and an internal link', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				type: 'Space',
			});

			const fileEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: _PNG_BASE64,
						name: `file_${getRandomString()}.png`,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: `File ${getRandomString()}`,
				},
				_FILE_APPLICATION_NAME,
				spaceName
			);

			const linkedEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: linkedTitle,
				},
				_CONTENT_APPLICATION_NAME,
				spaceName
			);

			content =
				`<p><a href="/cmsbasicwebcontent/${linkedEntry.friendlyUrlPath}">` +
				`${linkedTitle}</a></p>` +
				`<p><img src="${fileEntry.file.link.href}" /></p>`;

			await apiHelpers.objectEntry.postObjectEntry(
				{
					content,
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title,
				},
				_CONTENT_APPLICATION_NAME,
				spaceName
			);
		});

		await test.step('Duplicate the content', async () => {
			await assetsPage.gotoSpaceContents(spaceName);

			await assetsPage.execItemAction({
				action: 'Duplicate',
				filter: title,
				parentAction: 'Copy',
			});

			await expect(
				page.getByRole('link', {exact: true, name: `${title} (Copy)`})
			).toBeVisible();
		});

		await test.step('The copy carries the same references', async () => {
			const copy = await _getEntryByTitle(
				apiHelpers,
				spaceName,
				`${title} (Copy)`
			);

			expect(copy.content).toBe(content);
		});
	}
);

test(
	'The Headless API duplicate action creates the suffixed draft copy',
	{tag: '@LPD-103464'},
	async ({apiHelpers}) => {
		const spaceName = `Space ${getRandomString()}`;
		const title = `Content ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const entry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title,
			},
			_CONTENT_APPLICATION_NAME,
			spaceName
		);

		expect(entry.actions.duplicate.method).toBe('POST');

		expect(entry.actions.duplicate.href).toContain(
			`/${entry.id}/by-object-entry-folder-id/` +
				`${entry.objectEntryFolderId}/copy`
		);

		const copy = await apiHelpers.objectEntry.postObjectEntryCopy(
			_CONTENT_APPLICATION_NAME,
			entry.id,
			entry.objectEntryFolderId
		);

		expect(copy.title).toBe(`${title} (Copy)`);

		expect(copy.status.label).toBe('draft');

		expect(copy.objectEntryFolderId).toBe(entry.objectEntryFolderId);

		const secondCopy = await apiHelpers.objectEntry.postObjectEntryCopy(
			_CONTENT_APPLICATION_NAME,
			entry.id,
			entry.objectEntryFolderId
		);

		expect(secondCopy.title).toBe(`${title} (Copy 1)`);
	}
);

async function _getEntryByTitle(
	apiHelpers: DataApiHelpers,
	spaceName: string,
	title: string
) {
	const entriesPage =
		await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
			_CONTENT_APPLICATION_NAME,
			spaceName,
			new URLSearchParams({filter: `title eq '${title}'`})
		);

	expect(entriesPage.items).toHaveLength(1);

	return entriesPage.items[0];
}
