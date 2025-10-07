/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	IBulkActionFDSData,
	IBulkActionTaskType,
} from '../../../common/types/BulkActionTask';
import {
	BULK_ACTION_CATEGORIES,
	BULK_ACTION_DEFAULT_PERMISSIONS,
	BULK_ACTION_DELETE,
	BULK_ACTION_DOWNLOAD,
	BULK_ACTION_MOVE,
	BULK_ACTION_PERMISSIONS,
	BULK_ACTION_TAGS,
} from './constants';

type BulkActionMessage = {
	[actionType in keyof IBulkActionTaskType]: {
		[messageType: string]: {
			all: string;
			plural: string;
			singular: string;
		};
	};
};

const BULK_ACTION_MESSAGES: BulkActionMessage = {
	[BULK_ACTION_CATEGORIES]: {
		info: {
			all: Liferay.Language.get(
				'categories-update-action-started-for-all-assets'
			),
			plural: Liferay.Language.get(
				'categories-update-action-started-for-x-assets'
			),
			singular: Liferay.Language.get(
				'categories-update-action-started-for-x-asset'
			),
		},
	},
	[BULK_ACTION_DEFAULT_PERMISSIONS]: {
		info: {
			all: Liferay.Language.get(
				'default-permissions-update-action-started-for-all-assets'
			),
			plural: Liferay.Language.get(
				'default-permissions-update-action-started-for-x-assets'
			),
			singular: Liferay.Language.get(
				'default-permissions-update-action-started-for-one-asset'
			),
		},
	},
	[BULK_ACTION_DELETE]: {
		info: {
			all: Liferay.Language.get('delete-action-started-for-all-assets'),
			plural: Liferay.Language.get('delete-action-started-for-x-assets'),
			singular: Liferay.Language.get(
				'delete-action-started-for-one-asset'
			),
		},
	},
	[BULK_ACTION_DOWNLOAD]: {
		info: {
			all: Liferay.Language.get('download-action-started-for-all-assets'),
			plural: Liferay.Language.get(
				'download-action-started-for-x-assets'
			),
			singular: Liferay.Language.get(
				'download-action-started-for-one-asset'
			),
		},
	},
	[BULK_ACTION_MOVE]: {
		info: {
			all: Liferay.Language.get('move-action-started-for-all-assets'),
			plural: Liferay.Language.get('move-action-started-for-x-assets'),
			singular: Liferay.Language.get('move-action-started-for-one-asset'),
		},
	},
	[BULK_ACTION_PERMISSIONS]: {
		info: {
			all: Liferay.Language.get(
				'permissions-update-action-started-for-all-assets'
			),
			plural: Liferay.Language.get(
				'permissions-update-action-started-for-x-assets'
			),
			singular: Liferay.Language.get(
				'permissions-update-action-started-for-one-asset'
			),
		},
	},
	[BULK_ACTION_TAGS]: {
		info: {
			all: Liferay.Language.get(
				'tags-update-action-started-for-all-assets'
			),
			plural: Liferay.Language.get(
				'tags-update-action-started-for-x-assets'
			),
			singular: Liferay.Language.get(
				'tags-update-action-started-for-one-asset'
			),
		},
	},
};

export function getBulkActionTaskMessage(
	actionType: keyof IBulkActionTaskType,
	messageType: 'danger' | 'info' | 'success' | 'warning' = 'info',
	{items = [], selectAll = false}: IBulkActionFDSData
): string {
	return (
		BULK_ACTION_MESSAGES?.[actionType]?.[messageType]?.[
			selectAll ? 'all' : items.length === 1 ? 'singular' : 'plural'
		] || ''
	);
}
