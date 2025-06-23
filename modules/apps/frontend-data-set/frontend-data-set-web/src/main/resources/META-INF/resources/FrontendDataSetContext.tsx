/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {IInlineEditingSettings, IItemsActions, ISchema} from './utils/types';

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
		errorMessage: string;
		method: string;
		requestBody?: string;
		setActionItemLoading?: (loading: boolean) => void;
		successMessage?: string;
		url: string;
	}) => Promise<void>;
	formId?: string;
	formName?: string;
	highlightItems: Function;
	highlightedItemsValue?: Array<string>;
	id?: string;
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
	onSelect?: ({selectedItems}: {selectedItems: Array<any>}) => void;
	openModal: Function;
	openSidePanel: Function;
	portletId?: string;
	searchParam?: string;
	selectItems: Function;
	selectable?: boolean;
	selectedItems?: Array<any>;
	selectedItemsKey?: string;
	selectedItemsValue?: Array<any>;
	selectionType?: string;
	showBulkActionsManagementBar: boolean;
	showBulkActionsManagementBarActions: boolean;
	showInfoPanel: boolean;
	sidePanelId?: string;
	sorts?: Array<TRenderer>;
	style?: string;
	toggleItemInlineEdit: Function;
	uniformActionsDisplay?: boolean;
	updateDataSetItems: ({
		items,
		lastPage,
		page,
		pageSize,
		totalCount,
	}: IDataSetData) => void;
	updateItem: Function;
}

export interface IDataSetData {
	items: Array<any>;
	lastPage: number;
	page: number;
	pageSize?: number;
	totalCount: number;
}

export interface IHTMLElementBuilder {
	(args: any): HTMLElement;
}

export interface IClientExtensionRenderer {
	externalReferenceCode?: string;
	htmlElementBuilder?: IHTMLElementBuilder;
	name?: string;
	type: 'clientExtension';
	url?: string;
}

export interface IInternalRenderer {
	component: React.ComponentType<any>;
	default?: boolean;
	label?: string;
	name?: string;
	schema?: ISchema;
	symbol?: string;
	type: 'internal';
	url?: string;
}

export type TRenderer = IClientExtensionRenderer | IInternalRenderer;

const FrontendDataSetContext = React.createContext({
	allItemsSelectedActive: false,
	applyItemInlineUpdates: () => {},
	createInlineItem: () => {},
	executeAsyncItemAction: () => {},
	highlightItems: () => {},
	loadData: () => {},
	onActionDropdownItemClick: () => {},
	onBulkActionItemClick: () => {},
	onInfoPanelToggleButtonClick: () => {},
	onItemsChange: () => {},
	onSearch: () => {},
	onSelect: () => {},
	openModal: () => {},
	openSidePanel: () => {},
	selectItems: () => {},
	selectable: false,
	selectedItemsValue: [],
	toggleItemInlineEdit: () => {},
	updateDataSetItems: () => {},
	updateItem: () => {},
} as unknown as IFrontendDataSetContext);

export default FrontendDataSetContext;
