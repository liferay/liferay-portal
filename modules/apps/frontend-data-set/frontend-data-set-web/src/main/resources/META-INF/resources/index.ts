/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ModalStatus} from 'frontend-js-components-web';

import {TRenderer} from './FrontendDataSetContext';

export declare function FrontendDataSet({
	actionParameterName,
	activeViewSettings,
	apiURL,
	appURL,
	bulkActions,
	creationMenu,
	currentURL,
	customDataRenderers,
	customViews,
	customViewsEnabled,
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
	onSelect,
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
	sorts,
	style,
	views,
}: IFrontendDataSetProps): JSX.Element;

export declare function DateTimeRenderer({
	options,
	value,
}: DateTimeRendererProps): string;

type DateTimeRendererProps = {
	options?: {
		format: {
			day?: string;
			hour?: string;
			minute?: string;
			month?: string;
			second?: string;
			timeZone?: string;
			year?: string;
		};
	};
	value: string;
};

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

export interface IInlineEditingSettings {
	alwaysOn: boolean;
	defaultBodyContent: object;
}

export interface IActionsDropdown extends IBaseActions {
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

export interface IItemsActions {
	data?: IItemActionsData;
	href?: string;
	icon?: string;
	id?: string | number;
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
		| 'sidePanel'
		| 'event';
	type?: string;
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
	description: string;
	image?: string;
	labels?: ICardLabelSchema[];
	link?: string;
	sticker?: string;
	symbol: string;
	title: string;
}

export type ISchema = ITableSchema | ICardSchema;

export type TViews = {
	component?: any;
	contentRenderer?: string;
	contentRendererClientExtension?: boolean;
	contentRendererModuleURL?: string;
	default?: boolean;
	label?: string;
	name?: string;
	schema?: ISchema;
	thumbnail?: string;
	views?: Array<any>;
};

export interface IFrontendDataSetProps {
	actionParameterName?: string;
	activeViewSettings?: string;
	additionalAPIURLParameters?: string;
	apiURL?: string;
	appURL?: string;
	bulkActions?: any[];
	creationMenu?: {
		loadData?: Function;
		primaryItems: Array<ICreationActionItem>;
		secondaryItems?: any[];
	};
	currentURL?: string;
	customDataRenderers?: any;
	customRenderers?: {
		tableCell?: Array<TRenderer>;
		views?: Array<TRenderer>;
	};
	customViews?: string;
	customViewsEnabled?: boolean;
	emptyState?: {
		description?: string;
		image?: string;
		title?: string;
	};
	enableInlineAddModeSetting?: {
		defaultBodyContent?: object;
	};
	filters?: Array<any>;
	formId?: string;
	formName?: string;
	header?: {
		title?: string;
	};
	id: string;
	infoPanelComponent?: React.ComponentType<TInfoPanelComponent>;
	inlineAddingSettings?: {
		apiURL: string;
		defaultBodyContent: object;
		method?: string;
	};
	inlineEditingSettings?: IInlineEditingSettings;
	items?: any[];
	itemsActions?: IItemsActions[];
	namespace?: string;
	nestedItemsKey?: string;
	nestedItemsReferenceKey?: string;
	onActionDropdownItemClick?: any;
	onBulkActionItemClick?: any;
	onSelect?: ({selectedItems}: {selectedItems: Array<any>}) => void;
	onSelectedItemsChange?: (selectedItems: Array<any>) => void;
	overrideEmptyResultView?: boolean;
	pagination?: {
		deltas?: TDelta[];
		initialDelta?: number;
		initialPageNumber?: number;
	};
	portletId?: string;
	selectedItems?: any[];
	selectedItemsKey?: string;
	selectionType?: 'single' | 'multiple';
	showBulkActionsManagementBar?: boolean;
	showBulkActionsManagementBarActions?: boolean;
	showManagementBar?: boolean;
	showPagination?: boolean;
	showSearch?: boolean;
	showSelectAll?: boolean;
	sidePanelId?: string;
	sorts?: TSort[];
	style?: 'default' | 'fluid' | 'stacked';
	uniformActionsDisplay?: boolean;
	views: TViews[];
	viewsTitle?: string;
}

export type TInfoPanelComponent = {
	items?: Array<any>;
};

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

export {
	IClientExtensionRenderer,
	IInternalRenderer,
} from './FrontendDataSetContext';
export {INTERNAL_CELL_RENDERERS as FDS_INTERNAL_CELL_RENDERERS} from './cell_renderers/InternalCellRenderer';
export {
	DEFAULT_FETCH_HEADERS,
	FDS_ARRAY_FIELD_NAME_DELIMITER,
	FDS_ARRAY_FIELD_NAME_PARENT_SUFFIX,
	FDS_NESTED_FIELD_NAME_DELIMITER,
	FDS_NESTED_FIELD_NAME_PARENT_SUFFIX,
} from './constants';

export {Card} from './views/cards/Cards';
