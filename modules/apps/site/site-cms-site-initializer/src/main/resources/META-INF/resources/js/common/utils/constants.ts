/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const OBJECT_DEFINITION_CLASS_NAME =
	'com.liferay.object.model.ObjectDefinition';
export const OBJECT_ENTRY_CLASS_NAME = 'com.liferay.object.model.ObjectEntry';
export const OBJECT_ENTRY_FOLDER_CLASS_NAME =
	'com.liferay.object.model.ObjectEntryFolder';

export const AI_ASSISTANT_TOOLBAR_TRIGGER_ID = 'ai-assistant-toolbar-trigger';

export const ENTERPRISE_URL =
	'https://www.liferay.com/web/lr/cms-upgrade?utm_medium=referral&utm_source=cms-ft&utm_content=cms-ft-upgrade&utm_cid=701VO00000wwP6IYAU';

export const ERC_MAX_LENGTH = 75;

export const EXPIRING_SOON_THRESHOLD_DAYS = 7;

export const FDS_EVENT_DISPLAY_UPDATED = 'fds-display-updated';

export const FDS_EVENT_UPDATE_DISPLAY = 'fds-update-display';

export const MS_PER_DAY = 24 * 60 * 60 * 1000;

export const NO_VALUE = '--';

export const UPCOMING_REVIEWS_THRESHOLD_MONTHS = 1;

export const ASSET_STATUS = {
	APPROVED: 'approved',
	DENIED: 'denied',
	DRAFT: 'draft',
	EXPIRED: 'expired',
	IN_TRASH: 'in-trash',
	INACTIVE: 'inactive',
	INCOMPLETE: 'incomplete',
	PENDING: 'pending',
	SCHEDULED: 'scheduled',
} as const;

export type AssetStatus = (typeof ASSET_STATUS)[keyof typeof ASSET_STATUS];

export const ASSET_STATUS_TO_DISPLAY_TYPE = {
	[ASSET_STATUS.APPROVED]: 'success',
	[ASSET_STATUS.DENIED]: 'danger',
	[ASSET_STATUS.DRAFT]: 'secondary',
	[ASSET_STATUS.EXPIRED]: 'danger',
	[ASSET_STATUS.IN_TRASH]: 'info',
	[ASSET_STATUS.INACTIVE]: 'secondary',
	[ASSET_STATUS.INCOMPLETE]: 'warning',
	[ASSET_STATUS.PENDING]: 'info',
	[ASSET_STATUS.SCHEDULED]: 'info',
} as const;

export const COLLABORATOR_TYPE = {
	EXTERNAL_USER: 'Email',
	USER: 'User',
	USER_GROUP: 'UserGroup',
} as const;

export type CollaboratorType =
	(typeof COLLABORATOR_TYPE)[keyof typeof COLLABORATOR_TYPE];

export const ROOT_FOLDER_ERC = {
	CONTENTS: 'L_CONTENTS',
	FILES: 'L_FILES',
} as const;

export const SITE_TEMPLATE_TYPE = 'SiteTemplate';

export const ROOT_FOLDER_ERCS: ReadonlySet<string> = new Set([
	ROOT_FOLDER_ERC.CONTENTS,
	ROOT_FOLDER_ERC.FILES,
]);

export function isRootFolderERC(erc: string | undefined) {
	return !!erc && ROOT_FOLDER_ERCS.has(erc);
}

export const ITEM_SELECTOR_ITEM_TYPE = {
	FOLDER: 'Folder',
	SPACE: 'Space',
} as const;

export type ItemSelectorItemType =
	(typeof ITEM_SELECTOR_ITEM_TYPE)[keyof typeof ITEM_SELECTOR_ITEM_TYPE];

export const CMSSiteInitializerFDSNames = {
	ALL_SECTION: '-allSection',
} as const;

export const WORKFLOW_STATUS = {
	APPROVED: 0,
	DRAFT: 2,
	EXPIRED: 3,
	PENDING: 1,
	SCHEDULED: 7,
} as const;

export type WorkflowStatus =
	(typeof WORKFLOW_STATUS)[keyof typeof WORKFLOW_STATUS];

export const FDS_FILTER_ID = {
	DATE_EXPIRATION: 'dateExpiration',
	DATE_REVIEW: 'dateReview',
	SCOPE_GROUP_ID: 'scopeGroupId',
	STATUS: 'status',
} as const;
