/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {
	EConfigInURLKeys,
	IConfigInURLUpdaterThunk,
	IDataSetData,
	IInlineEditingSettings,
	IItemsActions,
	TRenderer,
} from './utils/types';

export interface IFrontendDataSetContext {
	actionParameterName?: string | null;
	allItemsSelectedActive: boolean;
	apiURL?: string;
	appURL?: string;
	applyItemInlineUpdates: Function;
	createInlineItem: Function;
	customDataRenderers?: Array<any>;
	customRenderers?: {
		tableCell?: Array<TRenderer>;
		views?: Array<TRenderer>;
	};
	executeAsyncItemAction: ({
		errorMessage,
		method,
		requestBody,
		setActionItemLoading,
		successMessage,
		url,
	}: {
		errorMessage?: string;
		method?: string;
		requestBody?: string;
		setActionItemLoading?: (loading: boolean) => void;
		successMessage?: string;
		url: string;
	}) => Promise<void>;
	formId?: string;
	formName?: string;
	highlightItems: Function;
	highlightedItemsValue?: Array<string>;
	id: string;
	infoPanelId?: string;
	infoPanelOpen?: boolean;
	inlineAddingSettings?: {
		apiURL?: string;
		defaultBodyContent?: object;
	};
	inlineEditingSettings?: IInlineEditingSettings;
	itemsActions?: Array<IItemsActions>;
	itemsChanges?: {[key: string]: any};
	loadData: Function;
	modalId?: string;
	namespace?: string;
	nestedItemsKey?: string;
	nestedItemsReferenceKey?: string;
	onActionDropdownItemClick: Function;
	onBulkActionItemClick: Function;
	onInfoPanelToggleButtonClick: Function;
	onItemsChange: ({itemKey, items}: {itemKey: string; items: any}) => void;
	onSearch: ({query}: {query: string}) => void;
	openModal: Function;
	openSidePanel: Function;
	portletId?: string;
	searchParam?: string;
	searching: boolean;
	selectable?: boolean;
	selectedItems?: Array<any>;
	selectedItemsKey: string;
	selectedItemsValue?: Array<any>;
	selectionType?: 'single' | 'multiple';
	setSearching: (value: boolean) => void;
	showBulkActionsManagementBar: boolean;
	showBulkActionsManagementBarActions: boolean;
	showInfoPanel: boolean;
	sidePanelId?: string;
	sorts?: Array<TRenderer>;
	style?: string;
	toggleItemInlineEdit: Function;
	uniformActionsDisplay?: boolean;
	updateActiveSorts: IConfigInURLUpdaterThunk<EConfigInURLKeys.ACTIVE_SORTS>;
	updateDataSetItems: ({
		items,
		lastPage,
		page,
		pageSize,
		totalCount,
	}: IDataSetData) => void;
	updateFilters: IConfigInURLUpdaterThunk<EConfigInURLKeys.ACTIVE_FILTERS>;
	updateItem: Function;
	updateView: IConfigInURLUpdaterThunk<EConfigInURLKeys.VIEW_NAME>;
	updateVisibleFields: IConfigInURLUpdaterThunk<EConfigInURLKeys.VISIBLE_FIELDS>;
}

const FrontendDataSetContext = React.createContext({
	allItemsSelectedActive: false,
	applyItemInlineUpdates: () => {},
	createInlineItem: () => {},
	executeAsyncItemAction: () => {},
	highlightItems: () => {},
	id: '',
	loadData: () => {},
	onActionDropdownItemClick: () => {},
	onBulkActionItemClick: () => {},
	onInfoPanelToggleButtonClick: () => {},
	onItemsChange: () => {},
	onSearch: () => {},
	openModal: () => {},
	openSidePanel: () => {},
	selectable: false,
	selectedItems: [],
	selectedItemsValue: [],
	setSearching: () => {},
	toggleItemInlineEdit: () => {},
	updateDataSetItems: () => {},
	updateItem: () => {},
} as unknown as IFrontendDataSetContext);

export default FrontendDataSetContext;
