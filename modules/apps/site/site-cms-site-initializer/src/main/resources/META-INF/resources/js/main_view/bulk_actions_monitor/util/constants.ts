/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import LoadingIndicator from '@clayui/loading-indicator';
import React from 'react';

import {IBulkActionTaskType} from '../../../common/types/BulkActionTask';

export const URL_BULK_ACTION_TASK =
	'/o/headless-cms/v1.0/bulk-action?nestedFields=embedded';
export const URL_DOWNLOAD_BULK_ACTION_TASK =
	'/o/cms/download-folder?nestedFields=embedded';

export const BULK_ACTION_CATEGORIES = 'TaxonomyCategoryBulkAction';
export const BULK_ACTION_DEFAULT_PERMISSIONS = 'DefaultPermissionBulkAction';
export const BULK_ACTION_DELETE = 'DeleteBulkAction';
export const BULK_ACTION_DOWNLOAD = 'DownloadBulkAction';
export const BULK_ACTION_MOVE = 'MoveBulkAction';
export const BULK_ACTION_PERMISSIONS = 'PermissionBulkAction';
export const BULK_ACTION_TAGS = 'KeywordBulkAction';

export const INTERVAL_TASK_POLLING_MS = 5000;

export const URL_TASKS_REPORT_DETAIL = `${Liferay.ThemeDisplay.getPortalURL()}/e/bulk-action-task/`;
export const URL_TASKS_REPORT = `${Liferay.ThemeDisplay.getPortalURL()}/o/cms/bulk-action-tasks`;

export const LABELS_BULK_ACTIONS: {[key in keyof IBulkActionTaskType]: string} =
	{
		[BULK_ACTION_CATEGORIES]: Liferay.Language.get('assets-categorization'),
		[BULK_ACTION_DEFAULT_PERMISSIONS]: Liferay.Language.get(
			'assets-permissioning'
		),
		[BULK_ACTION_DELETE]: Liferay.Language.get('assets-deletion'),
		[BULK_ACTION_DOWNLOAD]: Liferay.Language.get('assets-download'),
		[BULK_ACTION_MOVE]: Liferay.Language.get('assets-movement'),
		[BULK_ACTION_PERMISSIONS]: Liferay.Language.get(
			'assets-default-permissioning'
		),
		[BULK_ACTION_TAGS]: Liferay.Language.get('assets-tagging'),
	};

export const STATUS_COMPLETED = 'completed';
export const STATUS_FAILED = 'failed';
export const STATUS_INITIAL = 'initial';
export const STATUS_STARTED = 'started';

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
