/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Atom} from '@liferay/frontend-js-state-web';
import {ModalStatus} from 'frontend-js-components-web';
import React from 'react';

import {IInlineNotificationComponent} from '../inline_notification/InlineNotification';
import {EEntityFieldType} from '../management_bar/controls/filters/utils/types';
import {ISnapshot} from '../views/ViewsContext';

export declare function FrontendDataSet({
	actionParameterName,
	apiURL,
	appURL,
	bulkActions,
	creationMenu,
	currentURL,
	customDataRenderers,
	emptyState,
	filters,
	formId,
	formName,
	header,
	id,
	infoPanelComponent,
	inlineAddingSettings,
	inlineEditingSettings,
	items,
	itemsActions,
	namespace,
	nestedItemsKey,
	nestedItemsReferenceKey,
	onActionDropdownItemClick,
	onBulkActionItemClick,
	onItemsPropSearch,
	overrideEmptyResultView,
	pagination,
	portletId,
	selectedItems,
	selectedItemsKey,
	selectionType,
	showManagementBar,
	showPagination,
	showSearch,
	sidePanelId,
	snapshots,
	snapshotsEnabled,
	sorts,
	style,
	views,
}: IFrontendDataSetProps): JSX.Element;

type TDelta = {
	href?: string;
	label: number;
};

export enum DisplayType {
	DANGER = 'danger',
	INFO = 'info',
	SECONDARY = 'secondary',
	SUCCESS = 'success',
	UNSTYLED = 'unstyled',
	WARNING = 'warning',
}

export interface IEmptyState {
	description?: string;
	image?: string;
	imageReducedMotion?: string;
	title?: string;
}

export interface IEmptyStateConfiguration extends IEmptyState {
	filtered?: {
		filters?: IEmptyState;
		search?: IEmptyState;
		searchAndFilters?: IEmptyState;
	};
}

export enum EConfigInURLBehavior {
	OFF = 'off',
	PUSH = 'push',
	REPLACE = 'replace',
}

export interface IInlineEditingSettings {
	alwaysOn: boolean;
	defaultBodyContent: object;
}

export interface IActionsDropdown extends IBaseActions {
	accessibleName?: string;
	loading: boolean;
	menuActive?: boolean;
	onClick: Function;
	onMenuActiveChange?: Function;
	setLoading: Function;
}

export interface IBaseActions {
	actions: IItemsActions[];
	itemData: any;
	itemId: number | string;
}

interface IBulkActionItem {
	href?: string;
	icon?: string;
	label?: string;
	method?: string;
	target?: 'modal' | 'sidePanel';
}
export interface ICreationActionItem {
	data?: {
		disableHeader?: boolean;
		permissionKey?: string;
		size?: string;
		title?: string;
	};
	href?: string;
	icon?: string;
	id?: string;
	label: string;
	onClick?: Function;
	target?:
		| 'event'
		| 'link'
		| 'modal'
		| 'modal-full-screen'
		| 'modal-lg'
		| 'modal-sm'
		| 'sidePanel'
		| string;
}

export enum EItemActionsType {
	CONTEXTUAL = 'contextual',
	GROUP = 'group',
	ITEM = 'item',
}

export interface IItemsActions {
	accessibleName?: string;
	className?: string;
	data?: IItemActionsData;
	disabled?: boolean;
	href?: string;
	icon?: string;
	id?: string | number;
	isDisabled?: (item: any) => boolean;
	isVisible?: (item: any) => boolean;
	items?: IItemsActions[];
	label?: string;
	method?: string;
	onClick?: Function;
	separator?: boolean;
	target?:
		| 'async'
		| 'blank'
		| 'headless'
		| 'infoPanel'
		| 'inlineEdit'
		| 'link'
		| 'modal'
		| 'modal-permissions'
		| 'modal-workflow-transition'
		| 'sidePanel'
		| 'event';
	type?: EItemActionsType | `${EItemActionsType}`;
}

export interface IItemActionsData {
	confirmationMessage?: string;
	disableHeader?: boolean;
	errorMessage?: string;
	id?: string | number;
	method?: 'delete' | 'get' | 'patch' | 'post';
	permissionKey?: string;
	requestBody?: string;
	size?: 'sm' | 'lg' | 'full-screen';
	status?: ModalStatus;
	successMessage?: string;
	title?: string;
	visibilityFilters?: IItemActionsDataFilter;
}

export interface IItemActionsDataFilter {
	[key: string]: boolean | number | string;
}

export interface IQuickActions extends IBaseActions {
	onClick: Function;
}

export type TSort = {
	active?: boolean;
	default?: boolean;
	direction?: 'asc' | 'desc';
	key?: string;
	label?: string;
};

export interface IField {
	actionId?: string;
	contentRenderer?: string;
	contentRendererClientExtension?: boolean;
	contentRendererModuleURL?: string;
	expand?: boolean;
	fieldName: string | [];
	label: string;
	localizeLabel?: boolean;
	sortable?: boolean;
	truncate?: boolean;
}
export interface ITableSchema {
	accessibleNameField?: string;
	fields: Array<IField>;
}

export interface IBaseCardLabelSchema {
	value: string;
}

export interface IStaticCardLabelSchema extends IBaseCardLabelSchema {
	displayType: DisplayType;
	displayTypeKey?: never;
	displayTypeValues?: never;
}

export interface IDynamicCardLabelSchema extends IBaseCardLabelSchema {
	displayType?: never;
	displayTypeKey: string;
	displayTypeValues: Record<string, DisplayType>;
}

export type ICardLabelSchema = IStaticCardLabelSchema | IDynamicCardLabelSchema;

export interface ICardSchema {
	accessibleNameField?: string;
	description: string;
	image?: string;
	labels?: ICardLabelSchema[];
	link?: string;
	sticker?: string;
	symbol: string;
	title: string;
}

export interface IHeader {
	title?: string;
}

export interface IListSchema {
	accessibleNameField?: string;
	description: string;
	image?: string;
	sticker?: string;
	symbol?: string;
	title: string;
	titleRenderer: string;
	tooltip?: string;
}

export type ISchema = ITableSchema | ICardSchema | IListSchema;

export interface IView {
	component?: any;
	contentRenderer?: string;
	contentRendererClientExtension?: boolean;
	contentRendererModuleURL?: string;
	default?: boolean;
	initialPaginationDelta?: number;
	label?: string;
	name?: string;
	schema?: ISchema;
	setItemComponentProps?: ({item, props}: {item: any; props: any}) => any;
	showPagination?: boolean;
	thumbnail?: string;
	views?: Array<any>;
}

export type TOnFileDrop = (droppedFiles: File[], dropTarget: any) => void;

export interface IFileDropSettings {
	enabled: boolean;
	isDropTarget: ({item}: {item: any}) => boolean;
	onFileDrop?: TOnFileDrop;
}

export interface IFrontendDataSetProps {
	actionParameterName?: string;
	additionalAPIURLParameters?: string;
	apiURL?: string;
	appURL?: string;
	atom?: Atom<IFDSState>;
	bulkActions?: Array<IBulkActionItem>;
	configInURLBehavior?: EConfigInURLBehavior;
	creationMenu?: {
		loadData?: Function;
		primaryItems: Array<ICreationActionItem>;
		secondaryItems?: any[];
	};
	currentURL?: string;
	customDataRenderers?: any;
	customRenderers?: {
		listSection?: Array<TRenderer>;
		tableCell?: Array<TRenderer>;
	};
	defaultSelectedItems?: any[];
	emptyState?: IEmptyStateConfiguration;
	enableInlineAddModeSetting?: {
		defaultBodyContent?: object;
	};
	fileDropSettings?: IFileDropSettings;
	filters?: Array<any>;
	formId?: string;
	formName?: string;
	groupedFilters?: Array<any>;
	header?: IHeader;
	hideManagementBarInEmptyState?: boolean;
	id: string;
	infoPanelComponent?: React.ComponentType<IInfoPanelComponent>;
	inlineAddingSettings?: {
		apiURL: string;
		defaultBodyContent: object;
		method?: string;
	};
	inlineEditingSettings?: IInlineEditingSettings;
	inlineNotificationComponent?: React.ComponentType<IInlineNotificationComponent>;
	items?: any[];
	itemsActions?: IItemsActions[];
	namespace?: string;
	nestedItemsKey?: string;
	nestedItemsReferenceKey?: string;
	onActionDropdownItemClick?: any;
	onBulkActionItemClick?: any;
	onItemsPropSearch?: (item: any, query: string) => boolean;
	onSelectedItemsChange?: (selectedItems: Array<any>) => void;
	overrideEmptyResultView?: boolean;
	pagination?: {
		deltas?: TDelta[];
		initialDelta?: number;
		initialPageNumber?: number;
	};
	portletId?: string;
	selectedItems?: any[];
	selectedItemsKey?: string | undefined;
	selectionType?: 'single' | 'multiple';
	showBulkActionsManagementBar?: boolean;
	showBulkActionsManagementBarActions?: boolean;
	showManagementBar?: boolean;
	showNavBarWhenSelected?: boolean;
	showPagination?: boolean;
	showSearch?: boolean;
	showSelectAll?: boolean;
	sidePanelId?: string;
	snapshots?: Array<ISnapshot>;
	snapshotsEnabled?: boolean;
	sorts?: TSort[];
	style?: 'default' | 'fluid' | 'stacked';
	uniformActionsDisplay?: boolean;
	views: IView[];
	viewsTitle?: string;
}

export interface IInfoPanelComponent {
	items?: Array<any>;
}

export interface IManagementBarProps {
	bulkActions?: Array<IBulkActionItem>;
	creationMenu?: {
		primaryItems: Array<ICreationActionItem>;
		secondaryItems?: any[];
	};
	dataLoading: boolean;
	deselectItems: (value: any) => void;
	fluid: boolean;
	items: Array<any>;
	onBulkActionsClear: () => void;
	onSelectAll: (value: boolean) => void;
	pageSelectedItemsValue?: Array<any>;
	selectItems: (value: any) => void;
	selectedItems?: Array<any>;
	selectedItemsKey: string;
	selectedItemsValue: Array<any>;
	selectionType?: 'multiple' | 'single';
	showNavBarWhenSelected?: boolean;
	showSearch?: boolean;
	showSelectAll?: boolean;
	total: number;
}

export interface IModalConfig {
	disableHeader: boolean;
	size: string;
	title: string;
	url: string;
}

export interface IRequestOptions {
	body?: string;
	headers: {[key: string]: string};
	method?: string;
}

export interface ISuccessNotification {
	message: string;
	showSuccessNotification?: boolean;
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

export {
	DEFAULT_FETCH_HEADERS,
	FDS_ARRAY_FIELD_NAME_DELIMITER,
	FDS_ARRAY_FIELD_NAME_PARENT_SUFFIX,
	FDS_NESTED_FIELD_NAME_DELIMITER,
	FDS_NESTED_FIELD_NAME_PARENT_SUFFIX,
} from '../constants';

export enum EConfigInURLKeys {
	ACTIVE_FILTERS = 'filters',
	ACTIVE_SORTS = 'sorts',
	DELTA = 'delta',
	PAGE_NUMBER = 'page',
	SEARCH_PARAM = 'q',
	VIEW_NAME = 'view',
	VISIBLE_FIELDS = 'vf',
}

export interface IConfigInURL {
	[EConfigInURLKeys.ACTIVE_FILTERS]: Array<any>;
	[EConfigInURLKeys.ACTIVE_SORTS]: Array<TSort>;
	[EConfigInURLKeys.DELTA]: number;
	[EConfigInURLKeys.PAGE_NUMBER]: number;
	[EConfigInURLKeys.SEARCH_PARAM]: string;
	[EConfigInURLKeys.VIEW_NAME]: string;
	[EConfigInURLKeys.VISIBLE_FIELDS]: VisibleFieldNames;
}

export type IConfigInURLUpdaterThunk<K extends keyof IConfigInURL> = (
	value: IConfigInURL[K]
) => (viewsDispatch: Function) => void;

export type IConfigInURLGetter<K extends keyof IConfigInURL> = () =>
	| IConfigInURL[K]
	| undefined;

export type IConfigReader<K extends keyof IConfigInURL> = (
	value: IConfigInURL[K] | undefined
) => IConfigInURL[K] | undefined;

export type IConfigWriter<K extends keyof IConfigInURL> = (
	value: IConfigInURL[K]
) => IConfigInURL[K] | undefined;

export type VisibleFieldNames = {
	[fieldName: string]: boolean;
};

interface ISearch {
	query: string;
}

export interface IBaseFilterState {
	active?: boolean;
	enabled: boolean;
	entityFieldType: EEntityFieldType;
	id: string;
	label: string;
	moduleURL?: string;
	odataFilterString?: string;
	preloadedData: Record<string, unknown>;
	selectedData?: Record<string, unknown>;
	selectedItemsLabel: string;
	type: 'clientExtension' | 'dateRange' | 'selection';
}

export interface IClientExtensionFilterState extends IBaseFilterState {
	clientExtensionFilterImplementation?: string;
	clientExtensionFilterURL: string;
	clientExtensionResolutionError?: string;
}

export interface ISelectionFilterStateItem {
	label?: string;
	value: string;
}
interface ISelectionFilterState extends IBaseFilterState {
	apiURL: string;
	autocompleteEnabled: boolean;
	itemKey: string;
	itemLabel: string;
	items: Array<ISelectionFilterStateItem>;
	multiple: boolean;
	placeholder: string;
	selectedData?: {
		exclude: boolean;
		selectedItems: Array<ISelectionFilterStateItem>;
	};
}
interface IFDSState {
	filters: Array<IBaseFilterState>;
	search: ISearch;
}

export type {IFDSState, ISelectionFilterState};
