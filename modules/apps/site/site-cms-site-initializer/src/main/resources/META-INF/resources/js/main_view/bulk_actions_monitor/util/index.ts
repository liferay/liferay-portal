/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	IBulkActionFDSData,
	IBulkActionFDSDataItemTransformed,
	IBulkActionTaskType,
	TBulkActionTaskDTO,
} from '../../../common/types/BulkActionTask';
import {
	URL_BULK_ACTION_TASK,
	URL_DOWNLOAD_BULK_ACTION_TASK,
	URL_TASKS_REPORT_DETAIL,
} from './constants';

const OBJECT_ENTRY_FOLDER_CLASS_NAME =
	'com.liferay.object.model.ObjectEntryFolder';

export function composeCreateTaskURL(
	apiURL: string,
	{filters = [], searchQuery = '', selectAll = false}: IBulkActionFDSData,
	useDownloadURL: boolean = false
): string {
	const url: string = useDownloadURL
		? URL_DOWNLOAD_BULK_ACTION_TASK
		: URL_BULK_ACTION_TASK;

	const postURL = new URL(`${Liferay.ThemeDisplay.getPortalURL()}${url}`);

	if (!selectAll) {
		return postURL.toString();
	}

	if (searchQuery) {
		postURL.searchParams.append('search', searchQuery);
	}

	const fullFilters = filters.map(({odataFilterString}) => odataFilterString);

	const scopeFilter =
		new URL(
			`${Liferay.ThemeDisplay.getPortalURL()}${apiURL}`
		).searchParams.get('filter') || '';

	if (scopeFilter) {
		fullFilters.unshift(scopeFilter);
	}

	postURL.searchParams.append('filter', fullFilters.join(' and '));

	return postURL.toString();
}

export function composeCreateTaskDTO(
	actionKey: keyof IBulkActionTaskType,
	keyValues: IBulkActionTaskType[keyof IBulkActionTaskType] = {},
	{items = [], selectAll = false}: IBulkActionFDSData
): TBulkActionTaskDTO {
	return {
		bulkActionItems: items.map(
			({
				embedded: {
					externalReferenceCode: embeddedExternalReferenceCode,
					id: classPK,
					title: name,
				} = {} as any,
				entryClassName,
				externalReferenceCode,
			}: any) =>
				({
					classExternalReferenceCode:
						externalReferenceCode || embeddedExternalReferenceCode,
					className: entryClassName || OBJECT_ENTRY_FOLDER_CLASS_NAME,
					classPK,
					name,
				}) as IBulkActionFDSDataItemTransformed
		),
		selectAll,
		type: actionKey,
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
