/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface IBulkActionFDSDataItemTransformed {
	classExternalReferenceCode: string;
	className: string;
	classPK: number;
	file?: any;
	name: string;
}

export interface IBulkActionTask {
	actionName: string;
	dateCreated: string;
	dateModified: string;
	executionStatus: {
		key: string;
		name: string;
		name_i18n: {
			[p: string]: string;
		};
	};
	id: number;
	numberOfItems: number;
	taskResult: string;
	totalCount: number;
	type: keyof IBulkActionTaskType;
}

export interface IBulkActionTaskPage {
	actions: {};
	id: number;
	items: IBulkActionTask[];
	lastPage: number;
	page: number;
	pageSize: number;
	totalCount: number;
}

export interface IBulkActionTaskType {
	DefaultPermissionBulkAction: {
		defaultPermissions: string;
		depotGroupId?: number;
		roleKey?: string;
		treePath?: string;
	};
	DeleteBulkAction: {};
	DeleteObjectEntryBulkAction: {};
	DownloadBulkAction: {};
	KeywordBulkAction: {
		keywords: string[];
	};
	MoveBulkAction: {
		objectEntryFolderId: number;
	};
	PermissionBulkAction: {
		configuration: string;
		roleKey?: string;
	};
	ResetPermissionBulkAction: {};
	TaxonomyCategoryBulkAction: {
		taxonomyCategoryIds: number[];
	};
}

export type TBulkActionTaskDTO = {
	bulkActionItems: IBulkActionFDSDataItemTransformed[] | [];
	scope?: string;
	selectionScope: {selectAll: boolean};
	type: keyof IBulkActionTaskType;
} & IBulkActionTaskType[keyof IBulkActionTaskType];
