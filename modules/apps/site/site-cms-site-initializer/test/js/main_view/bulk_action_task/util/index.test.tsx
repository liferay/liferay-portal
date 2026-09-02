/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ISearchAssetObjectEntry} from '../../../../../src/main/resources/META-INF/resources/js/common/types/AssetType';
import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
import {
	composeCreateTaskDTO,
	composeCreateTaskURL,
	getTaskReportLink,
} from '../../../../../src/main/resources/META-INF/resources/js/main_view/bulk_actions_monitor/util';
import {URL_BULK_ACTION_TASK} from '../../../../../src/main/resources/META-INF/resources/js/main_view/bulk_actions_monitor/util/constants';

describe('Bulk Actions Monitor Utils', () => {
	describe('composeCreateTaskURL', () => {
		it('return the download URL when type is DownloadBulkAction', () => {
			const taskUrl = composeCreateTaskURL(
				URL_BULK_ACTION_TASK,
				{
					filters: [],
					searchQuery: '',
					selectAll: false,
				},
				'DownloadBulkAction'
			);

			expect(taskUrl).toBe(
				`${Liferay.ThemeDisplay.getPortalURL()}${'/o/cms/download-folder?nestedFields=embedded'}`
			);
		});

		it('return the bulk action URL when type is not DownloadBulkAction or ExportTranslationBulkAction', () => {
			const taskUrl = composeCreateTaskURL(
				URL_BULK_ACTION_TASK,
				{
					filters: [],
					searchQuery: '',
					selectAll: false,
				},
				'DeleteObjectBulkSelectionAction'
			);

			expect(taskUrl).toBe(
				`${Liferay.ThemeDisplay.getPortalURL()}${'/o/bulk/v1.0/bulk-action'}`
			);
		});

		it('include search and filter parameters when selectAll is true', () => {
			const taskUrl = composeCreateTaskURL(
				URL_BULK_ACTION_TASK,
				{
					filters: [
						{
							id: 1,
							multiple: false,
							odataFilterString: 'asset',
							selectedItemsLabel: '',
						},
					],
					searchQuery: 'test',
					selectAll: true,
				},
				'DeleteObjectBulkSelectionAction'
			);

			expect(taskUrl).toBe(
				`${Liferay.ThemeDisplay.getPortalURL()}${'/o/bulk/v1.0/bulk-action?search=test&filter=asset'}`
			);
		});

		it('include emptySearch parameters when searchQuery is not provided', () => {
			const taskUrl = composeCreateTaskURL(
				URL_BULK_ACTION_TASK,
				{
					filters: [
						{
							id: 1,
							multiple: false,
							odataFilterString: 'asset',
							selectedItemsLabel: '',
						},
					],
					selectAll: true,
				},
				'DeleteObjectBulkSelectionAction'
			);

			expect(taskUrl).toBe(
				`${Liferay.ThemeDisplay.getPortalURL()}${'/o/bulk/v1.0/bulk-action?emptySearch=true&filter=asset'}`
			);
		});

		it('omits the search and filter parameters for DefaultPermissionObjectBulkSelectionAction when there is no apiURL', () => {
			const taskUrl = composeCreateTaskURL(
				undefined,
				{
					filters: [],
					searchQuery: '',
					selectAll: true,
				},
				'DefaultPermissionObjectBulkSelectionAction'
			);

			expect(taskUrl).toBe(
				`${Liferay.ThemeDisplay.getPortalURL()}${'/o/bulk/v1.0/bulk-action'}`
			);
		});

		it('throws an error when selectAll is true and there is no apiURL to scope the task with', () => {
			expect(() =>
				composeCreateTaskURL(
					undefined,
					{
						filters: [],
						searchQuery: '',
						selectAll: true,
					},
					'DeleteObjectBulkSelectionAction'
				)
			).toThrow('Cannot POST bulk action task.');
		});

		it('applies specific filter for ExportTranslationBulkAction when selectAll is true', () => {
			const apiURLWithFilter =
				'/o/search/v1.0/search?filter=folderId eq 123 and groupIds/any(g:g in (456))';

			const taskUrl = composeCreateTaskURL(
				apiURLWithFilter,
				{
					filters: [],
					searchQuery: '',
					selectAll: true,
				},
				'ExportTranslationBulkAction'
			);

			expect(taskUrl).toBe(
				`${Liferay.ThemeDisplay.getPortalURL()}/o/cms/translations?type=ExportTranslationBulkAction&emptySearch=true&filter=cmsRoot+eq+true+and+cmsSection+eq+%27contents%27+and+rootDescendantNode+eq+false+and+status+in+%280%2C+2%2C+3%29+and+folderId+eq+123+and+groupIds%2Fany%28g%3Ag+in+%28456%29%29`
			);
		});
	});

	describe('composeCreateTaskDTO', () => {
		it('includes keyValues in the final DTO', () => {
			const keyValues = {destinationFolderId: 12345};
			const result = composeCreateTaskDTO(
				'MoveObjectBulkSelectionAction',
				keyValues,
				{
					items: [],
					selectAll: false,
				}
			);

			expect(result).toEqual({
				bulkActionItems: [],
				destinationFolderId: 12345,
				selectionScope: {
					selectAll: false,
				},
				type: 'MoveObjectBulkSelectionAction',
			});
		});

		it('maps items with top-level properties correctly', () => {
			const items = [
				{
					embedded: {
						id: 1,
						title: 'Item 1',
					},
					entryClassName: 'com.liferay.document.library.Document',
					externalReferenceCode: 'ERC-1',
				},
			] as unknown as ISearchAssetObjectEntry[];

			const result = composeCreateTaskDTO(
				'DeleteObjectBulkSelectionAction',
				{},

				{items, selectAll: false}
			);

			expect(result.bulkActionItems).toEqual([
				{
					classExternalReferenceCode: 'ERC-1',
					className: 'com.liferay.document.library.Document',
					classPK: 1,
					name: 'Item 1',
				},
			]);
		});

		it('maps items using properties from the embedded object', () => {
			const items = [
				{
					className: OBJECT_ENTRY_FOLDER_CLASS_NAME,
					embedded: {
						externalReferenceCode: 'EMBEDDED-ERC-2',
						id: 2,
						title: 'Item 2',
					},
				},
			] as unknown as ISearchAssetObjectEntry[];

			const result = composeCreateTaskDTO(
				'DeleteObjectBulkSelectionAction',
				{},

				{items, selectAll: false}
			);

			expect(result.bulkActionItems).toEqual([
				{
					classExternalReferenceCode: 'EMBEDDED-ERC-2',
					className: OBJECT_ENTRY_FOLDER_CLASS_NAME,
					classPK: 2,
					name: 'Item 2',
				},
			]);
		});

		it('prefers top-level properties over embedded properties', () => {
			const items = [
				{
					embedded: {
						externalReferenceCode: 'EMBEDDED-ERC-3',
						id: 3,
						title: 'Item 3',
					},
					entryClassName: 'com.liferay.journal.JournalArticle',
					externalReferenceCode: 'TOP-LEVEL-ERC-3',
				},
			] as unknown as ISearchAssetObjectEntry[];

			const result = composeCreateTaskDTO(
				'DeleteObjectBulkSelectionAction',
				{},

				{items, selectAll: false}
			);

			expect(result.bulkActionItems).toEqual([
				{
					classExternalReferenceCode: 'TOP-LEVEL-ERC-3',
					className: 'com.liferay.journal.JournalArticle',
					classPK: 3,
					name: 'Item 3',
				},
			]);
		});

		it('sets selectAll to true when specified', () => {
			const result = composeCreateTaskDTO(
				'DeleteObjectBulkSelectionAction',
				{},
				{items: [], selectAll: true}
			);

			expect(result).toEqual({
				bulkActionItems: [],
				selectionScope: {
					selectAll: true,
				},
				type: 'DeleteObjectBulkSelectionAction',
			});
		});
	});

	describe('getTaskReportLink', () => {
		it('returns an empty link when taskId is undefined', () => {
			const reportLink = getTaskReportLink(1211, undefined);

			expect(reportLink).toBe('');
		});

		it('returns TaskReportLink when taskId is defined', () => {
			const reportLink = getTaskReportLink(1211, 1212);

			expect(reportLink).toBe(
				`<a class="alert-link lead" href="${Liferay.ThemeDisplay.getPortalURL()}/web/cms/e/bulk-action-task/1211/1212"><strong>${Liferay.Language.get(
					'task-report'
				)}</strong></a>`
			);
		});
	});
});
