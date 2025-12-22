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

export interface IBulkActionTaskStarter {
	onCreateError(response: RequestResult<IBulkActionTaskPage>): void;
	onCreateSuccess(response: RequestResult<IBulkActionTaskPage>): void;

	get overrideDefaultErrorToast(): boolean;
	get overrideDefaultSuccessToast(): boolean;
	get payload(): TBulkActionTaskDTO;
	get postURL(): string;
	get type(): string;
}

export interface IBulkActionTaskStarterDTO<
	T extends keyof IBulkActionTaskType,
> {
	apiURL?: string;
	dataSetId?: string;
	keyValues?: IBulkActionTaskType[T];
	onCreateError?:
		| ((response: RequestResult<IBulkActionTaskPage>) => void)
		| null;
	onCreateSuccess?:
		| ((response: RequestResult<IBulkActionTaskPage>) => void)
		| null;
	overrideDefaultErrorToast?: boolean;
	overrideDefaultSuccessToast?: boolean;
	selectedData: IBulkActionFDSData;
	type: keyof IBulkActionTaskType;
}

export interface IBulkActionTaskType {
	DefaultPermissionBulkAction: {
		defaultPermissions: string;
		depotGroupId?: number;
		roleKey?: string;
		treePath?: string;
	};
	DeleteBulkAction: {};
	DownloadBulkAction: {};
	KeywordBulkAction: {
		append?: boolean;
		keywordsToAdd?: string[];
		keywordsToRemove?: string[];
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
		append?: boolean;
		taxonomyCategoryIdsToAdd?: number[];
		taxonomyCategoryIdsToRemove?: number[];
	};
}

export type TBulkActionTaskDTO = {
	bulkActionItems: IBulkActionFDSDataItemTransformed[] | [];
	selectAll?: IBulkActionFDSData['selectAll'];
	selectionScope?: {
		selectAll: IBulkActionFDSData['selectAll'];
		[k: string]: any;
	};
	type: keyof IBulkActionTaskType;
} & IBulkActionTaskType[keyof IBulkActionTaskType];
