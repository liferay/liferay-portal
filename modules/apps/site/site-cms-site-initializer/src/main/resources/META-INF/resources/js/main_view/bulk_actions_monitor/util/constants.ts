/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import LoadingIndicator from '@clayui/loading-indicator';
import React from 'react';

import {IBulkActionTaskType} from '../../../common/types/BulkActionTask';

export const BULK_ACTION_ADD_OBJECT_TO_PROJECT =
	'AddObjectToProjectBulkSelectionAction';
export const BULK_ACTION_ASSIGN_DEFAULT_WORKFLOW =
	'AssignStructureDefaultWorkflowBulkSelectionAction';
export const BULK_ACTION_ASSIGN_TO = 'AssignToObjectBulkSelectionAction';
export const BULK_ACTION_CATEGORIES = 'EditObjectCategoriesBulkSelectionAction';
export const BULK_ACTION_COPY = 'CopyObjectBulkSelectionAction';
export const BULK_ACTION_DEFAULT_PERMISSIONS =
	'DefaultPermissionObjectBulkSelectionAction';
export const BULK_ACTION_DELETE = 'DeleteObjectBulkSelectionAction';
export const BULK_ACTION_DELETE_ASSET_VERSION =
	'DeleteObjectAssetVersionBulkSelectionAction';
export const BULK_ACTION_DELETE_TASK = 'DeleteTaskBulkAction';
export const BULK_ACTION_DOWNLOAD = 'DownloadBulkAction';
export const BULK_ACTION_DUE_DATE = 'DueDateObjectBulkSelectionAction';
export const BULK_ACTION_DUPLICATE = 'DuplicateObjectBulkSelectionAction';
export const BULK_ACTION_EXPIRE = 'ExpireObjectBulkSelectionAction';
export const BULK_ACTION_EXPORT_TRANSLATION = 'ExportTranslationBulkAction';
export const BULK_ACTION_MOVE = 'MoveObjectBulkSelectionAction';
export const BULK_ACTION_PERMISSIONS = 'PermissionObjectBulkSelectionAction';
export const BULK_ACTION_RESET_PERMISSIONS =
	'ResetPermissionObjectBulkSelectionAction';
export const BULK_ACTION_RESTORE = 'RestoreObjectBulkSelectionAction';
export const BULK_ACTION_STATUS = 'StatusObjectBulkSelectionAction';
export const BULK_ACTION_TAGS = 'EditObjectTagsBulkSelectionAction';
export const BULK_ACTION_UPDATE_OBJECT_VALUES =
	'UpdateObjectValuesBulkSelectionAction';

export const INTERVAL_TASK_POLLING_MS = 5000;

export const LABELS_BULK_ACTIONS: {[key in keyof IBulkActionTaskType]: string} =
	{
		[BULK_ACTION_ADD_OBJECT_TO_PROJECT]: Liferay.Language.get(
			'add-assets-to-project'
		),
		[BULK_ACTION_ASSIGN_DEFAULT_WORKFLOW]:
			Liferay.Language.get('assign-workflow'),
		[BULK_ACTION_ASSIGN_TO]: Liferay.Language.get('assign-to'),
		[BULK_ACTION_CATEGORIES]: Liferay.Language.get('assets-categorization'),
		[BULK_ACTION_COPY]: Liferay.Language.get('copy-to'),
		[BULK_ACTION_DEFAULT_PERMISSIONS]: Liferay.Language.get(
			'assets-permissioning'
		),
		[BULK_ACTION_DELETE]: Liferay.Language.get('assets-deletion'),
		[BULK_ACTION_DELETE_ASSET_VERSION]: Liferay.Language.get(
			'asset-versions-deletion'
		),
		[BULK_ACTION_DELETE_TASK]: Liferay.Language.get('tasks-deletion'),
		[BULK_ACTION_DOWNLOAD]: Liferay.Language.get('assets-download'),
		[BULK_ACTION_DUE_DATE]: Liferay.Language.get('due-date-update'),
		[BULK_ACTION_DUPLICATE]: Liferay.Language.get('assets-duplication'),
		[BULK_ACTION_EXPIRE]: Liferay.Language.get('expire'),
		[BULK_ACTION_EXPORT_TRANSLATION]: Liferay.Language.get(
			'export-for-translation'
		),
		[BULK_ACTION_MOVE]: Liferay.Language.get('assets-movement'),
		[BULK_ACTION_PERMISSIONS]: Liferay.Language.get(
			'assets-default-permissioning'
		),
		[BULK_ACTION_RESET_PERMISSIONS]: Liferay.Language.get(
			'reset-to-default-permissions'
		),
		[BULK_ACTION_RESTORE]: Liferay.Language.get('assets-restoration'),
		[BULK_ACTION_STATUS]: Liferay.Language.get('state-update'),
		[BULK_ACTION_TAGS]: Liferay.Language.get('assets-tagging'),
		[BULK_ACTION_UPDATE_OBJECT_VALUES]:
			Liferay.Language.get('text-replace'),
	};

export const STATUS_COMPLETED = 'completed';
export const STATUS_FAILED = 'failed';
export const STATUS_INITIAL = 'initial';
export const STATUS_STARTED = 'started';

export const TASK_REPORT_FDS_ID =
	'com.liferay.site.cms.site.initializer-bulkActionTaskReportSection';

export const TASK_STATUS_PROPS: Record<
	string,
	{
		component: React.ComponentType<any>;
		displayType:
			| 'danger'
			| 'info'
			| 'secondary'
			| 'success'
			| 'unstyled'
			| 'warning';
		icon?: string;
		label: string;
	}
> = {
	[STATUS_COMPLETED]: {
		component: ClayIcon,
		displayType: 'success',
		icon: 'check-circle-full',
		label: Liferay.Language.get('completed'),
	},
	[STATUS_FAILED]: {
		component: ClayIcon,
		displayType: 'danger',
		icon: 'times-circle-full',
		label: Liferay.Language.get('failed'),
	},
	[STATUS_INITIAL]: {
		component: LoadingIndicator,
		displayType: 'info',
		label: Liferay.Language.get('processing'),
	},
	[STATUS_STARTED]: {
		component: LoadingIndicator,
		displayType: 'info',
		label: Liferay.Language.get('processing'),
	},
};

export const URL_BULK_ACTION_TASK = '/o/bulk/v1.0/bulk-action';
export const URL_DOWNLOAD_BULK_ACTION_TASK =
	'/o/cms/download-folder?nestedFields=embedded';
export const URL_EXPORT_TRANSLATION_BULK_ACTION_TASK = '/o/cms/translations';
export const URL_TASKS_REPORT = `${Liferay.ThemeDisplay.getPortalURL()}/web/cms/bulk-action-task-report`;
export const URL_TASKS_REPORT_DETAIL = `${Liferay.ThemeDisplay.getPortalURL()}/web/cms/e/bulk-action-task/`;
