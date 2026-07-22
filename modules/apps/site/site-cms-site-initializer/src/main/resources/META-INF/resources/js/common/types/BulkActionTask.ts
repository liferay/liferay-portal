/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {RequestResult} from '../services/ApiHelper';
import {ISearchAssetObjectEntry} from './AssetType';

export interface IBulkActionFDSData {
	filters?:
		| Array<{
				id: number;
				multiple: any;
				odataFilterString: string;
				selectedItemsLabel: string;
		  }>
		| [];
	items?: Array<ISearchAssetObjectEntry> | [];
	keyValues?: number[] | [];
	searchQuery?: string;
	selectAll: boolean;
}

export interface IBulkActionFDSDataItemTransformed {
	classExternalReferenceCode: string;
	className: string;
	classPK: number;
	file?: any;
	name: string;
}

export interface IBulkActionSelectionScope {
	selectAll: boolean;
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
	numberOfFailedItems: number;
	numberOfItems: string | number;
	numberOfSuccessfulItems: number;
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

export interface IBulkActionTaskStarter {
	onCreateError(response: RequestResult<IBulkActionTaskPage>): void;
	onCreateSuccess(response: RequestResult<IBulkActionTaskPage>): void;

	get overrideDefaultErrorToast(): boolean;
	get overrideDefaultSuccessToast(): boolean;
	get payload(): TBulkActionTaskDTO;
	get postURL(): string;
	get type(): string;
}

export interface IBulkActionTaskStarterDTO<T extends keyof IBulkActionType> {
	additionalData?: Record<string, any>;
	apiURL?: string;
	dataSetId?: string;
	entryClassName?: string;
	folderId?: string;
	groupIds?: string;
	keyValues?: IBulkActionType[T];
	onCreateError?:
		| ((response: RequestResult<IBulkActionTaskPage>) => void)
		| null;
	onCreateSuccess?:
		| ((response: RequestResult<IBulkActionTaskPage>) => void)
		| null;
	overrideDefaultErrorToast?: boolean;
	overrideDefaultSuccessToast?: boolean;
	resetSearch?: boolean;
	selectedData: IBulkActionFDSData;
	type: keyof IBulkActionType;
}

export interface IBulkActionType {
	AddObjectToProjectBulkSelectionAction: {
		projectScopeKeys?: string[];
	};
	AssignStructureDefaultWorkflowBulkSelectionAction: {
		workflow?: string;
	};
	AssignToObjectBulkSelectionAction: {
		className: string;
		externalReferenceCode: string;
		name: string;
	};
	CopyObjectBulkSelectionAction: {
		objectEntryFolderId: number;
	};
	DefaultPermissionObjectBulkSelectionAction: {
		defaultPermissions: string;
		depotGroupId?: number;
		roleKey?: string;
		treePath?: string;
	};
	DeleteObjectAssetVersionBulkSelectionAction: {
		versions?: number[];
	};
	DeleteObjectBulkSelectionAction: {
		className?: string;
	};
	DownloadBulkAction: {};
	DueDateObjectBulkSelectionAction: {
		dueDate?: string;
	};
	DuplicateObjectBulkSelectionAction: {};
	EditObjectCategoriesBulkSelectionAction: {
		append?: boolean;
		taxonomyCategoryIdsToAdd?: number[];
		taxonomyCategoryIdsToRemove?: number[];
	};
	EditObjectTagsBulkSelectionAction: {
		append?: boolean;
		keywordsToAdd?: string[];
		keywordsToRemove?: string[];
	};
	ExpireObjectBulkSelectionAction: {};
	ExportTranslationBulkAction: {
		sourceLanguageId: string;
		targetLanguageIds: string[];
		xliffMimeType: string;
	};
	MoveObjectBulkSelectionAction: {
		objectEntryFolderId: number;
	};
	PermissionObjectBulkSelectionAction: {
		configuration: string;
		roleKey?: string;
	};
	ResetPermissionObjectBulkSelectionAction: {};
	RestoreObjectBulkSelectionAction: {};
	StatusObjectBulkSelectionAction: {
		status?: string;
	};
	UpdateObjectValuesBulkSelectionAction: {
		values?: Record<string, unknown>;
	};
}

export interface IBulkActionTaskType {
	AddObjectToProjectBulkSelectionAction: string;
	AssignStructureDefaultWorkflowBulkSelectionAction: string;
	AssignToObjectBulkSelectionAction: string;
	CopyObjectBulkSelectionAction: string;
	DefaultPermissionObjectBulkSelectionAction: string;
	DeleteObjectAssetVersionBulkSelectionAction: string;
	DeleteObjectBulkSelectionAction: string;
	DeleteTaskBulkAction: string;
	DownloadBulkAction: string;
	DueDateObjectBulkSelectionAction: string;
	DuplicateObjectBulkSelectionAction: string;
	EditObjectCategoriesBulkSelectionAction: string;
	EditObjectTagsBulkSelectionAction: string;
	ExpireObjectBulkSelectionAction: string;
	ExportTranslationBulkAction: string;
	MoveObjectBulkSelectionAction: string;
	PermissionObjectBulkSelectionAction: string;
	ResetPermissionObjectBulkSelectionAction: string;
	RestoreObjectBulkSelectionAction: string;
	StatusObjectBulkSelectionAction: string;
	UpdateObjectValuesBulkSelectionAction: string;
}

export type TBulkActionTaskDTO = {
	bulkActionItems: IBulkActionFDSDataItemTransformed[] | [];
	selectAll?: IBulkActionFDSData['selectAll'];
	selectionScope?: {
		selectAll: IBulkActionFDSData['selectAll'];
		[k: string]: any;
	};
	type: keyof IBulkActionType;
	versions?: number[] | [];
} & IBulkActionType[keyof IBulkActionType];
