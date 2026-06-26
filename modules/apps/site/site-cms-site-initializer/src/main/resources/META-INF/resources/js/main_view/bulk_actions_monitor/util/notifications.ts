/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import {
	IBulkActionFDSData,
	IBulkActionTaskType,
} from '../../../common/types/BulkActionTask';
import {
	BULK_ACTION_ASSIGN_DEFAULT_WORKFLOW,
	BULK_ACTION_ASSIGN_TO,
	BULK_ACTION_CATEGORIES,
	BULK_ACTION_COPY,
	BULK_ACTION_DEFAULT_PERMISSIONS,
	BULK_ACTION_DELETE,
	BULK_ACTION_DELETE_ASSET_VERSION,
	BULK_ACTION_DELETE_TASK,
	BULK_ACTION_DOWNLOAD,
	BULK_ACTION_DUE_DATE,
	BULK_ACTION_DUPLICATE,
	BULK_ACTION_EXPIRE,
	BULK_ACTION_EXPORT_TRANSLATION,
	BULK_ACTION_MOVE,
	BULK_ACTION_PERMISSIONS,
	BULK_ACTION_RESET_PERMISSIONS,
	BULK_ACTION_RESTORE,
	BULK_ACTION_STATUS,
	BULK_ACTION_TAGS,
	BULK_ACTION_UPDATE_OBJECT_VALUES,
} from './constants';

type MessageType = 'danger' | 'info' | 'success' | 'warning';

type BulkActionMessage = {
	[actionType in keyof IBulkActionTaskType]: {
		[messageType: string]: {
			all?: string;
			plural?: string;
			singular?: string;
		};
	};
};

const BULK_ACTION_MESSAGES: BulkActionMessage = {
	[BULK_ACTION_ASSIGN_DEFAULT_WORKFLOW]: {
		info: {
			all: Liferay.Language.get(
				'assign-default-workflow-action-started-for-all-assets'
			),
			plural: Liferay.Language.get(
				'assign-default-workflow-action-started-for-x-assets'
			),
			singular: Liferay.Language.get(
				'assign-default-workflow-action-started-for-one-asset'
			),
		},
		success: {
			all: Liferay.Language.get(
				'all-items-were-successfully-assigned-to-default-workflow'
			),
			plural: Liferay.Language.get(
				'x-assets-were-successfully-assigned-to-default-workflow'
			),
			singular: Liferay.Language.get(
				'x-was-successfully-assigned-to-default-workflow'
			),
		},
	},
	[BULK_ACTION_ASSIGN_TO]: {
		info: {
			all: Liferay.Language.get(
				'assign-to-action-started-for-all-assets'
			),
			plural: Liferay.Language.get(
				'assign-to-action-started-for-x-assets'
			),
			singular: Liferay.Language.get(
				'assign-to-action-started-for-one-asset'
			),
		},
		success: {
			all: Liferay.Language.get(
				'all-items-were-successfully-assigned-to-x'
			),
			plural: Liferay.Language.get(
				'x-assets-were-successfully-assigned-to-x'
			),
			singular: Liferay.Language.get('x-was-successfully-assigned-to-x'),
		},
	},
	[BULK_ACTION_CATEGORIES]: {
		info: {
			all: Liferay.Language.get(
				'categories-update-action-started-for-all-assets'
			),
			plural: Liferay.Language.get(
				'categories-update-action-started-for-x-assets'
			),
			singular: Liferay.Language.get(
				'categories-update-action-started-for-one-asset'
			),
		},
		success: {
			all: Liferay.Language.get(
				'categories-were-successfully-updated-for-all-assets'
			),
			plural: Liferay.Language.get(
				'categories-were-successfully-updated-for-x-assets'
			),
			singular: Liferay.Language.get(
				'categories-were-successfully-updated-for-one-asset'
			),
		},
	},
	[BULK_ACTION_COPY]: {
		info: {
			all: Liferay.Language.get('copying-all-assets-to-x'),
			plural: Liferay.Language.get('copying-x-assets-to-x'),
			singular: Liferay.Language.get('copying-x-to-x'),
		},
		success: {
			all: Liferay.Language.get(
				'all-items-were-successfully-copied-to-x'
			),
			plural: Liferay.Language.get(
				'x-assets-were-successfully-copied-to-x'
			),
			singular: Liferay.Language.get('x-was-successfully-copied-to-x'),
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
		success: {
			all: Liferay.Language.get(
				'default-permissions-were-successfully-updated-for-all-assets'
			),
			plural: Liferay.Language.get(
				'default-permissions-were-successfully-updated-for-x-assets'
			),
			singular: Liferay.Language.get(
				'default-permissions-were-successfully-updated-for-one-asset'
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
		success: {
			all: Liferay.Language.get('all-items-were-successfully-deleted'),
			plural: Liferay.Language.get('x-assets-were-successfully-deleted'),
			singular: Liferay.Language.get('x-was-successfully-deleted'),
		},
	},
	[BULK_ACTION_DELETE_ASSET_VERSION]: {
		info: {
			all: Liferay.Language.get(
				'delete-asset-versions-action-started-for-all-versions'
			),
			plural: Liferay.Language.get(
				'delete-asset-versions-action-started-for-x-versions'
			),
			singular: Liferay.Language.get(
				'delete-asset-version-action-started-for-one-version'
			),
		},
		success: {
			all: Liferay.Language.get(
				'all-asset-versions-were-successfully-deleted'
			),
			plural: Liferay.Language.get(
				'x-asset-versions-were-successfully-deleted'
			),
			singular: Liferay.Language.get('x-was-successfully-deleted'),
		},
	},
	[BULK_ACTION_DELETE_TASK]: {
		info: {
			all: Liferay.Language.get('delete-action-started-for-all-tasks'),
			plural: Liferay.Language.get('delete-action-started-for-x-tasks'),
			singular: Liferay.Language.get(
				'delete-action-started-for-one-task'
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
	[BULK_ACTION_DUE_DATE]: {
		info: {
			all: Liferay.Language.get(
				'due-date-update-action-started-for-all-tasks'
			),
			plural: Liferay.Language.get(
				'due-date-update-action-started-for-x-tasks'
			),
			singular: Liferay.Language.get(
				'due-date-update-action-started-for-one-task'
			),
		},
		success: {
			all: Liferay.Language.get(
				'due-date-was-successfully-updated-for-all-tasks'
			),
			plural: Liferay.Language.get(
				'due-date-was-successfully-updated-for-x-tasks'
			),
			singular: Liferay.Language.get(
				'due-date-was-successfully-updated-for-one-task'
			),
		},
	},
	[BULK_ACTION_DUPLICATE]: {
		info: {
			all: Liferay.Language.get(
				'duplicate-action-started-for-all-assets'
			),
			plural: Liferay.Language.get(
				'duplicate-action-started-for-x-assets'
			),
			singular: Liferay.Language.get(
				'duplicate-action-started-for-one-asset'
			),
		},
		success: {
			all: Liferay.Language.get('all-items-were-successfully-duplicated'),
			plural: Liferay.Language.get(
				'x-assets-were-successfully-duplicated'
			),
			singular: Liferay.Language.get('x-was-successfully-duplicated'),
		},
	},
	[BULK_ACTION_EXPIRE]: {
		info: {
			all: Liferay.Language.get('expire-action-started-for-all-assets'),
			plural: Liferay.Language.get('expire-action-started-for-x-assets'),
			singular: Liferay.Language.get(
				'expire-action-started-for-one-asset'
			),
		},
		success: {
			all: Liferay.Language.get('all-items-were-successfully-expired'),
			plural: Liferay.Language.get('x-assets-were-successfully-expired'),
			singular: Liferay.Language.get('x-was-successfully-expired'),
		},
	},
	[BULK_ACTION_EXPORT_TRANSLATION]: {
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
			all: Liferay.Language.get('moving-all-assets-to-x'),
			plural: Liferay.Language.get('moving-x-assets-to-x'),
			singular: Liferay.Language.get('moving-x-to-x'),
		},
		success: {
			all: Liferay.Language.get('all-items-were-successfully-moved-to-x'),
			plural: Liferay.Language.get(
				'x-assets-were-successfully-moved-to-x'
			),
			singular: Liferay.Language.get('x-was-successfully-moved-to-x'),
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
		success: {
			all: Liferay.Language.get(
				'permissions-were-successfully-updated-for-all-assets'
			),
			plural: Liferay.Language.get(
				'permissions-were-successfully-updated-for-x-assets'
			),
			singular: Liferay.Language.get(
				'permissions-were-successfully-updated-for-one-asset'
			),
		},
	},
	[BULK_ACTION_RESET_PERMISSIONS]: {
		info: {
			all: Liferay.Language.get(
				'reset-permissions-action-started-for-all-assets'
			),
			plural: Liferay.Language.get(
				'reset-permissions-action-started-for-x-assets'
			),
			singular: Liferay.Language.get(
				'reset-permissions-action-started-for-one-asset'
			),
		},
		success: {
			all: Liferay.Language.get(
				'all-items-were-successfully-reset-to-default-permissions'
			),
			plural: Liferay.Language.get(
				'x-assets-were-successfully-reset-to-default-permissions'
			),
			singular: Liferay.Language.get(
				'x-was-successfully-reset-to-default-permissions'
			),
		},
	},
	[BULK_ACTION_RESTORE]: {
		info: {
			all: Liferay.Language.get('restore-action-started-for-all-assets'),
			plural: Liferay.Language.get('restore-action-started-for-x-assets'),
			singular: Liferay.Language.get(
				'restore-action-started-for-one-asset'
			),
		},
		success: {
			all: Liferay.Language.get(
				'all-items-were-restored-to-their-original-locations'
			),
			plural: Liferay.Language.get(
				'x-items-were-restored-to-their-original-locations'
			),
			singular: Liferay.Language.get(
				'x-was-restored-to-its-original-location'
			),
		},
	},
	[BULK_ACTION_STATUS]: {
		info: {
			all: Liferay.Language.get(
				'state-update-action-started-for-all-tasks'
			),
			plural: Liferay.Language.get(
				'state-update-action-started-for-x-tasks'
			),
			singular: Liferay.Language.get(
				'state-update-action-started-for-one-task'
			),
		},
		success: {
			all: Liferay.Language.get(
				'state-was-successfully-updated-for-all-tasks'
			),
			plural: Liferay.Language.get(
				'state-was-successfully-updated-for-x-tasks'
			),
			singular: Liferay.Language.get(
				'state-was-successfully-updated-for-one-task'
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
		success: {
			all: Liferay.Language.get(
				'tags-were-successfully-updated-for-all-assets'
			),
			plural: Liferay.Language.get(
				'tags-were-successfully-updated-for-x-assets'
			),
			singular: Liferay.Language.get(
				'tags-were-successfully-updated-for-one-asset'
			),
		},
	},
	[BULK_ACTION_UPDATE_OBJECT_VALUES]: {
		info: {
			all: Liferay.Language.get('replacing-x-with-x'),
			plural: Liferay.Language.get('replacing-x-with-x-across-x-assets'),
			singular: Liferay.Language.get('replacing-x-with-x-for-one-asset'),
		},
		success: {
			all: Liferay.Language.get('replaced-x-with-x'),
			plural: Liferay.Language.get('replaced-x-with-x-in-x-assets'),
			singular: Liferay.Language.get('replaced-x-with-x-for-one-asset'),
		},
	},
};

const BULK_ACTION_FAILURE_MESSAGES: {
	[actionType in keyof IBulkActionTaskType]?: {
		[taskResult: string]: string;
	};
} = {
	[BULK_ACTION_COPY]: {
		structureNotInDestinationSpace: Liferay.Language.get(
			'some-items-could-not-be-copied.-please-ensure-their-structures-exist-in-the-destination-space'
		),
	},
	[BULK_ACTION_MOVE]: {
		structureNotInDestinationSpace: Liferay.Language.get(
			'some-items-could-not-be-moved.-please-ensure-their-structures-exist-in-the-destination-space'
		),
	},
};

export function getBulkActionTaskFailureMessage(
	actionType: keyof IBulkActionTaskType,
	taskResult: string
): string | null {
	if (!taskResult) {
		return null;
	}

	return BULK_ACTION_FAILURE_MESSAGES?.[actionType]?.[taskResult] || null;
}

export function getBulkActionTaskMessage(
	actionType: keyof IBulkActionTaskType,
	messageType: MessageType = 'info',
	selectedData: IBulkActionFDSData,
	additionalData?: {
		assetName?: string;
		replacement?: string;
		search?: string;
		targetName?: string;
	}
): string {
	const {items = [], selectAll = false} = selectedData;
	const messageKey = selectAll
		? 'all'
		: items.length === 1
			? 'singular'
			: 'plural';

	const message =
		BULK_ACTION_MESSAGES?.[actionType]?.[messageType]?.[messageKey];

	if (!message) {
		return '';
	}

	const args: (string | number)[] = [];

	if (additionalData?.search) {
		args.push(Liferay.Util.escapeHTML(additionalData.search));
	}

	if (additionalData?.replacement) {
		args.push(Liferay.Util.escapeHTML(additionalData.replacement));
	}

	if (messageKey === 'singular') {
		const assetName = additionalData?.assetName || items[0]?.title;

		if (assetName) {
			args.push(Liferay.Util.escapeHTML(assetName));
		}
	}
	else if (messageKey === 'plural') {
		args.push(`<strong>${items.length}</strong>`);
	}

	if (additionalData?.targetName) {
		args.push(
			`<strong>${Liferay.Util.escapeHTML(
				additionalData.targetName
			)}</strong>`
		);
	}

	return sub(message, args);
}
