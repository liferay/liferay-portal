/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IAssetObjectEntry} from '../../../common/types/AssetType';
import {
	IBulkActionFDSData,
	IBulkActionFDSDataItemTransformed,
	IBulkActionTask,
	IBulkActionType,
	TBulkActionTaskDTO,
} from '../../../common/types/BulkActionTask';
import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../common/utils/constants';
import {getFolderIdAndGroupIdsFromFilter} from '../../../common/utils/odataFilterUtil';
import {
	TASK_STATUS_PROPS,
	URL_BULK_ACTION_TASK,
	URL_DOWNLOAD_BULK_ACTION_TASK,
	URL_EXPORT_TRANSLATION_BULK_ACTION_TASK,
	URL_TASKS_REPORT_DETAIL,
} from './constants';

export function composeCreateTaskURL(
	apiURL: string | undefined,
	{filters = [], searchQuery = '', selectAll = false}: IBulkActionFDSData,
	type: keyof IBulkActionType
): string {
	let url: string = URL_BULK_ACTION_TASK;

	if (type === 'DownloadBulkAction') {
		url = URL_DOWNLOAD_BULK_ACTION_TASK;
	}
	else if (type === 'ExportTranslationBulkAction') {
		url = URL_EXPORT_TRANSLATION_BULK_ACTION_TASK;
	}

	const postURL = new URL(`${Liferay.ThemeDisplay.getPortalURL()}${url}`);

	if (type === 'ExportTranslationBulkAction') {
		postURL.searchParams.append('type', 'ExportTranslationBulkAction');
	}

	if (!selectAll || type === 'DefaultPermissionObjectBulkSelectionAction') {
		return postURL.toString();
	}

	if (!apiURL) {
		throw new Error('Cannot POST bulk action task.');
	}

	if (searchQuery) {
		postURL.searchParams.append('search', searchQuery);
	}
	else {
		postURL.searchParams.append('emptySearch', 'true');
	}

	const fullFilters = filters.map(({odataFilterString}) => odataFilterString);

	let scopeFilter =
		new URL(
			`${Liferay.ThemeDisplay.getPortalURL()}${apiURL}`
		).searchParams.get('filter') || '';

	if (type === 'ExportTranslationBulkAction') {
		const {folderId, groupIds} =
			getFolderIdAndGroupIdsFromFilter(scopeFilter);

		scopeFilter = `cmsRoot eq true and cmsSection eq 'contents' and rootDescendantNode eq false and status in (0, 2, 3)`;

		if (folderId) {
			scopeFilter += ` and folderId eq ${folderId}`;
		}

		if (groupIds) {
			scopeFilter += ` and ${groupIds}`;
		}
	}

	if (scopeFilter) {
		fullFilters.unshift(scopeFilter);
	}

	postURL.searchParams.append('filter', fullFilters.join(' and '));

	return postURL.toString();
}

export function composeCreateTaskDTO(
	type: keyof IBulkActionType,
	keyValues: IBulkActionType[keyof IBulkActionType] = {},
	{items = [], selectAll = false}: IBulkActionFDSData
): TBulkActionTaskDTO {
	return {
		bulkActionItems: items.map(
			({
				className,
				id,
				embedded: {
					externalReferenceCode: embeddedExternalReferenceCode,
					file,
					id: classPK,
					title: name,
				} = {} as IAssetObjectEntry,
				entryClassName,
				externalReferenceCode,
			}: any) => {
				const itemsTransformed = {
					classExternalReferenceCode:
						externalReferenceCode || embeddedExternalReferenceCode,
					className:
						entryClassName ||
						className ||
						OBJECT_ENTRY_FOLDER_CLASS_NAME,
					classPK: classPK || id,
					name,
				} as IBulkActionFDSDataItemTransformed;

				if (type === 'DownloadBulkAction') {
					itemsTransformed.file = file;
				}

				return itemsTransformed;
			}
		),
		selectionScope: {selectAll},
		type,
		...keyValues,
	} as TBulkActionTaskDTO;
}

export function getTaskReportLink(
	classNameId: number,
	taskId?: number
): string {
	if (!taskId) {
		return '';
	}

	const href = `${URL_TASKS_REPORT_DETAIL}${classNameId}/${taskId}`;

	return `<a class="alert-link lead" href="${href}"><strong>${Liferay.Language.get('task-report')}</strong></a>`;
}

export function getTaskStatusProps(bulkActionTask: IBulkActionTask) {
	const {executionStatus, numberOfFailedItems, numberOfItems} =
		bulkActionTask as IBulkActionTask;

	if (numberOfItems !== 0 && numberOfFailedItems === Number(numberOfItems)) {
		return TASK_STATUS_PROPS.failed;
	}

	return TASK_STATUS_PROPS[executionStatus.key];
}
