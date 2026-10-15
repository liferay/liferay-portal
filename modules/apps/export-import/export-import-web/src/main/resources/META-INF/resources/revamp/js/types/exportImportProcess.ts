/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {PortletDataHandler} from './portletDataHandler';

export type ExportImportProcess = 'export' | 'import' | 'publish';

export type DateRangeType =
	| 'ALL'
	| 'DATE_RANGE'
	| 'FROM_LAST_PUBLISH_DATE'
	| 'LAST';

export interface ExportProcess {
	dateCreated?: string;
	dateModified?: string;
	id?: number;
	name?: string;
	status?: {code: number; label: string};
}

export interface ExportProcessRequest {
	comments?: boolean;
	dateRangeType?: DateRangeType;
	deletions?: boolean;
	endDate?: string;
	name: string;
	permissions?: boolean;
	portletDataHandlers?: PortletDataHandler[];
	ratings?: boolean;
	siteExternalReferenceCodes?: string[];
	startDate?: string;
}

export const DATA_STRATEGIES = {
	MIRROR: 'MIRROR',
	MIRROR_OVERWRITE: 'MIRROR_OVERWRITE',
} as const;

export type DataStrategy =
	(typeof DATA_STRATEGIES)[keyof typeof DATA_STRATEGIES];

export const USER_ID_STRATEGIES = {
	ALWAYS_CURRENT_USER_ID: 'ALWAYS_CURRENT_USER_ID',
	CURRENT_USER_ID: 'CURRENT_USER_ID',
} as const;

export type UserIdStrategy =
	(typeof USER_ID_STRATEGIES)[keyof typeof USER_ID_STRATEGIES];

export interface ImportProcess {
	dateCreated?: string;
	dateModified?: string;
	id?: number;
	name?: string;
	status?: {code: number; label: string};
}

export interface ImportProcessRequest {
	comments?: boolean;
	dataStrategy?: DataStrategy;
	deletions?: boolean;
	name?: string;
	permissions?: boolean;
	portletDataHandlers?: PortletDataHandler[];
	ratings?: boolean;
	siteExternalReferenceCodes?: string[];
	userIdStrategy?: UserIdStrategy;
}

export interface PublishProcess {
	dateCreated?: string;
	dateModified?: string;
	id?: number;
	name?: string;
	status?: {code: number; label: string};
}

export interface PublishProcessRequest {
	comments?: boolean;
	cronExpression?: string;
	dateRangeType?: DateRangeType;
	deletions?: boolean;
	endDate?: string;
	name: string;
	permissions?: boolean;
	portletDataHandlers?: PortletDataHandler[];
	ratings?: boolean;
	scheduleEndDate?: string;
	scheduleStartDate?: string;
	startDate?: string;
	timeZoneId?: string;
}

export interface ScheduledPublishProcess {
	dateCreated?: string;
	id?: number;
	name?: string;
	nextFireDate?: string;
	publishProcessRequest?: PublishProcessRequest;
	scheduleEndDate?: string;
	scheduleStartDate?: string;
}
