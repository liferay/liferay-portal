/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import {useControlledState} from '@clayui/shared';
import {useIsMounted, useThunk} from '@liferay/frontend-js-react-web';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import classNames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import {
	ClientExtensionDefinition,
	ClientExtensionResolution,
	deepClone,
	fetch,
	getObjectValueFromPath,
	loadClientExtensions,
	loadModule,
} from 'frontend-js-web';
import React, {
	RefObject,
	useCallback,
	useContext,
	useEffect,
	useMemo,
	useReducer,
	useRef,
	useState,
} from 'react';

import DragLayer from './dnd/DragLayer';
import FDSDndProvider from './dnd/FDSDndProvider';
import isFileDropEnabled from './utils/isFileDropEnabled';

import './styles/main.scss';

import {Atom, Selector, State} from '@liferay/frontend-js-state-web';

import DnDContext from './DnDContext';
import FrontendDataSetContext from './FrontendDataSetContext';
import useFDSDrop from './dnd/useFDSDrop';
import useFileUploader from './dnd/useFileUploader';
import EmptyState from './empty_state/EmptyState';
import {InfoPanel} from './info_panel/InfoPanel';
import {
	IInlineNotificationComponent,
	InlineNotification,
} from './inline_notification/InlineNotification';
import ManagementBar from './management_bar/ManagementBar';
import {FILTER_IMPLEMENTATIONS} from './management_bar/controls/filters/Filter';

// @ts-ignore

import Modal from './modal/Modal';

// @ts-ignore

import SidePanel from './side_panel/SidePanel';
import filterCreationActions from './utils/actionItems/filterCreationActions';
import {readConfigFromURL} from './utils/configInURL';
import EVENTS from './utils/eventsDefinitions';
import {activateFilter} from './utils/filters/activateFilter';
import {deactivateFilter} from './utils/filters/deactivateFilter';
import getRandomId from './utils/getRandomId';

// @ts-ignore

import {formatItemChanges, getCurrentItemUpdates} from './utils/index';
import {loadData} from './utils/loadData';

// @ts-ignore

import {logError} from './utils/logError';
import {
	EConfigInURLBehavior,
	EConfigInURLKeys,
	IBaseFilterState,
	IConfigInURL,
	IDataSetData,
	IFDSState,
	IField,
	IFrontendDataSetProps,
	IModalConfig,
	IRequestOptions,
	ISelectionFilterState,
	ISelectionFilterStateItem,
	ISuccessNotification,
	ITableSchema,
	IView,
	TRenderer,
	TSort,
	VisibleFieldNames,
} from './utils/types';
import useConfigInURL, {useUpdateConfig} from './utils/useConfigInURL';
import ViewsContext, {ISnapshot} from './views/ViewsContext';
import getViewComponent from './views/getViewComponent';
import viewsReducer, {EViewsActionTypes} from './views/viewsReducer';

const DEFAULT_PAGINATION_DELTA = 20;
const DEFAULT_PAGINATION_PAGE_NUMBER = 1;

const getAtom = ({
	atom,
	id,
}: {
	atom: Atom<IFDSState> | undefined;
	id: string;
}): Atom<IFDSState> | Selector<IFDSState> => {
	if (atom) {
		return atom;
	}

	const key = `${id}_fdsState`;

	const fallbackAtom: Atom<IFDSState> | null =
		State.__unsafe__.getAtomOrSelectorKey(key) as Atom<IFDSState> | null;

	return (
		fallbackAtom ||
		State.atom<IFDSState>(key, {
			filters: [],
			search: {query: ''},
		})
	);
};

const FrontendDataSetContent = ({
	actionParameterName,
	additionalAPIURLParameters: initialAdditionalAPIURLParameters,
	apiURL,
	appURL,
	atom,
	bulkActions = [],
	configInURLBehavior = EConfigInURLBehavior.PUSH,
	creationMenu: initialCreationMenu,
	currentURL,
	customDataRenderers,
	customRenderers,
	defaultSelectedItems,
	emptyState,
	filters: initialFilters,
	formId,
	formName,
	groupedFilters,
	header,
	hideManagementBarInEmptyState = false,
	id,
	infoPanelComponent,
	inlineAddingSettings,
	inlineEditingSettings,
	inlineNotificationComponent,
	items: itemsProp,
	itemsActions,
	namespace,
	nestedItemsKey,
	nestedItemsReferenceKey,
	onActionDropdownItemClick,
	onBulkActionItemClick,
	onSelectedItemsChange,
	overrideEmptyResultView,
	pagination,
	portletId,
	selectedItems: externalSelectedItems,
	selectedItemsKey = 'id',
	selectionType,
	showBulkActionsManagementBar = true,
	showBulkActionsManagementBarActions = true,
	showManagementBar = true,
	showNavBarWhenSelected = false,
	showPagination = true,
	showSearch = true,
	showSelectAll = false,
	sidePanelId,
	snapshots = [],
	snapshotsEnabled,
	sorts: sortsProp = [],
	style = 'default',
	uniformActionsDisplay,
	views,
}: IFrontendDataSetProps) => {
	const {fileDropSettings} = useContext(DnDContext);

	const [getActiveSorts, updateActiveSorts] = useConfigInURL({
		configInURLBehavior,
		configReader: (sorts: Array<TSort> | undefined) => {
			return sorts;
		},
		configWriter: (
			sorts: Array<TSort> | undefined
		): Array<TSort> | undefined => {
			if (sorts?.every((sort: TSort) => !sort.active)) {
				return [];
			}

			return sorts
				?.filter((sort: TSort) => sort.active)
				.map((sort: TSort) => {
					return {
						direction: sort.direction,
						key: sort.key,
					};
				});
		},
		id,
		stateDispatcher: {
			key: EConfigInURLKeys.ACTIVE_SORTS,
			type: EViewsActionTypes.UPDATE_SORTING,
		},
	});

	const [getFilters, updateFilters] = useConfigInURL({
		configInURLBehavior,
		configReader: (filters: Array<any> | undefined) => {
			return filters;
		},
		configWriter: (
			filters: Array<any> | undefined
		): Array<any> | undefined => {
			if (filters?.every((filter) => !filter.active)) {
				return [];
			}

			return filters
				?.filter((filter: any) => filter.active)
				.map((filter: any) => {
					return {
						id: filter.id,
						selectedData:
							filter.type === 'selection'
								? {
										...filter.selectedData,
										selectedItems:
											filter.selectedData.selectedItems.map(
												(
													item: ISelectionFilterStateItem
												) => {
													if (filter.items?.length) {
														const newSelectedItem =
															{...item};

														delete newSelectedItem.label;

														return newSelectedItem;
													}

													return item;
												}
											),
									}
								: filter.selectedData,
					};
				});
		},
		id,
		stateDispatcher: {
			key: EConfigInURLKeys.ACTIVE_FILTERS,
			type: EViewsActionTypes.NOOP,
		},
	});

	const [getDelta, updateDelta] = useConfigInURL({
		additionalStateDispatchers: [
			{
				key: EConfigInURLKeys.PAGE_NUMBER,
				type: EViewsActionTypes.UPDATE_PAGE_NUMBER,
				value: 1,
			},
		],
		configInURLBehavior,
		configReader: (delta: number | undefined) => {
			if (!delta || isNaN(delta) || delta < 1) {
				return undefined;
			}

			return delta;
		},
		id,
		stateDispatcher: {
			key: EConfigInURLKeys.DELTA,
			type: EViewsActionTypes.UPDATE_PAGINATION_DELTA,
		},
	});

	const [getPageNumber, updatePageNumber] = useConfigInURL({
		configInURLBehavior,
		configReader: (pageNumber: number | undefined) => {
			if (!pageNumber || isNaN(pageNumber) || pageNumber < 1) {
				return 1;
			}

			return pageNumber;
		},
		id,
		stateDispatcher: {
			key: EConfigInURLKeys.PAGE_NUMBER,
			type: EViewsActionTypes.UPDATE_PAGE_NUMBER,
		},
	});

	const [getSearchParam] = useConfigInURL({
		configInURLBehavior,

		configReader: (searchParam: string | undefined) => {
			if (!searchParam) {
				return '';
			}

			return searchParam;
		},
		configWriter: (searchParam: string | undefined): string | undefined => {
			if (!searchParam || !searchParam.length) {
				return undefined;
			}

			return searchParam;
		},
		id,
		stateDispatcher: {
			key: EConfigInURLKeys.SEARCH_PARAM,
			type: EViewsActionTypes.UPDATE_SEARCH_PARAM,
		},
	});

	const [getView, updateView] = useConfigInURL({
		configInURLBehavior,
		configReader: (viewName: string | undefined) => {
			const view = views.find(({name}) => name === viewName);

			if (view) {
				return viewName;
			}

			return undefined;
		},
		id,
		stateDispatcher: {
			key: EConfigInURLKeys.VIEW_NAME,
			type: EViewsActionTypes.UPDATE_ACTIVE_VIEW,
		},
	});

	const [getVisibleFields, updateVisibleFields] = useConfigInURL({
		configInURLBehavior,
		configReader: (visibleFieldNames: VisibleFieldNames | undefined) => {
			const view = views.find(
				({name}) => name && name.toLowerCase().includes('table')
			);

			if (view && visibleFieldNames) {
				const tableSchema = view.schema as ITableSchema;

				const updatedVisibleFieldNames: VisibleFieldNames = {};

				tableSchema.fields.forEach((field: IField) => {
					let fieldName: string = String(field.fieldName);

					if (fieldName.includes('.')) {
						fieldName = fieldName.replaceAll('.', ',');
					}

					if (visibleFieldNames[fieldName] !== undefined) {
						updatedVisibleFieldNames[fieldName] =
							visibleFieldNames[fieldName];
					}
					else {
						updatedVisibleFieldNames[fieldName] = true;
					}
				});

				return updatedVisibleFieldNames;
			}

			return undefined;
		},
		configWriter: (
			visibleFields: VisibleFieldNames | undefined
		): VisibleFieldNames | undefined => {
			return visibleFields;
		},
		id,
		stateDispatcher: {
			key: EConfigInURLKeys.VISIBLE_FIELDS,
			type: EViewsActionTypes.UPDATE_VISIBLE_FIELD_NAMES,
		},
	});

	const updateConfigInURL = useUpdateConfig({
		configInURLBehavior,
		id,
	});

	const memoizedAtom = useMemo(() => getAtom({atom, id}), [atom, id]);

	const [additionalAPIURLParameters, setAdditionalAPIURLParameters] =
		useState(initialAdditionalAPIURLParameters);
	const [globalFDSState, setGlobalFDSState] =
		useLiferayState<IFDSState>(memoizedAtom);

	const [globalFDSStateInitialized, setGlobalFDSStateInitialized] =
		useState(false);
	const [cellClientExtensionsLoaded, setCellClientExtensionsLoaded] =
		useState(false);
	const [cellClientExtensionsLoading, setCellClientExtensionsLoading] =
		useState(false);
	const [filterClientExtensionsLoaded, setFilterClientExtensionsLoaded] =
		useState(false);
	const [filterClientExtensionsLoading, setFilterClientExtensionsLoading] =
		useState(false);
	const [componentLoading, setComponentLoading] = useState(false);
	const [creationMenu, setCreationMenu] = useState(initialCreationMenu);
	const [dataLoading, setDataLoading] = useState(!!apiURL);
	const dataSetSupportInfoPanelIdRef = useRef(
		`support-info-panel-${getRandomId()}`
	);
	const dataSetSupportModalIdRef = useRef(`support-modal-${getRandomId()}`);
	const dataSetSupportSidePanelIdRef = useRef(
		sidePanelId || `support-side-panel-${getRandomId()}`
	);

	const [highlightedItemsValue, setHighlightedItemsValue] = useState([]);
	const [infoPanelOpen, setInfoPanelOpen] = useState<boolean>(false);
	const [searching, setSearching] = useState(!!apiURL);
	const [items, setItems] = useState(itemsProp || []);
	const [itemsChanges, setItemsChanges] = useState<{[key: string]: any}>({});

	const [allItemsSelectedActive, setAllItemsSelectedActive] = useState(false);

	const [selectedItems = [], setSelectedItems] = useControlledState({
		defaultName: 'selectedItems',
		defaultValue: defaultSelectedItems ?? undefined,
		handleName: 'onSelectedItemsChange',
		name: 'selectedItems',
		onChange: onSelectedItemsChange,
		value: externalSelectedItems,
	});

	let selectedItemsValue = selectedItems.map((item) =>
		getObjectValueFromPath({object: item, path: selectedItemsKey})
	);

	if (allItemsSelectedActive) {
		selectedItemsValue = items.map((item) =>
			getObjectValueFromPath({object: item, path: selectedItemsKey})
		);
	}

	const [total, setTotal] = useState(0);

	const isMounted = useIsMounted();

	const updateFilterActivation = ({
		newFilters,
		oldFilters,
	}: {
		newFilters: Array<IBaseFilterState> | undefined;
		oldFilters: Array<IBaseFilterState>;
	}): Array<IBaseFilterState> => {
		if (!newFilters) {
			return oldFilters;
		}

		return oldFilters.map((filter: IBaseFilterState): IBaseFilterState => {
			const newFilter = newFilters.find(
				(newFilter) => newFilter.id === filter.id
			);

			if (!newFilter) {
				return deactivateFilter(filter);
			}

			if (filter.type !== 'selection') {
				return activateFilter({
					filter,
					selectedData: {
						...filter.selectedData,
						...newFilter.selectedData,
					},
				});
			}

			const selectionFilter = filter as ISelectionFilterState;
			const newSelectionFilter = newFilter as ISelectionFilterState;

			return activateFilter({
				filter: selectionFilter,
				selectedData: {
					...selectionFilter.selectedData,
					...newSelectionFilter.selectedData,
					selectedItems:
						newSelectionFilter.selectedData?.selectedItems.map(
							(newItem) => {
								const selectedItem = selectionFilter.items.find(
									(item: ISelectionFilterStateItem) =>
										item.value === newItem.value
								);

								if (selectedItem) {
									return selectedItem;
								}

								return newItem;
							}
						),
				},
			});
		});
	};

	const updateSortsActivation = ({
		newSorts,
		oldSorts,
	}: {
		newSorts: Array<TSort> | undefined;
		oldSorts: Array<TSort>;
	}): Array<TSort> => {
		if (!newSorts) {
			return oldSorts;
		}

		const remainingNewSorts = [...newSorts];

		return [
			...oldSorts?.map((sort: TSort) => {
				const activeSortIndex = remainingNewSorts?.findIndex(
					(activeSort: TSort) => {
						return activeSort.key === sort.key;
					}
				);

				if (activeSortIndex !== -1) {
					const activeSort = remainingNewSorts[activeSortIndex];

					remainingNewSorts.splice(activeSortIndex, 1);

					return {
						...sort,
						active: true,
						direction: activeSort.direction,
					};
				}

				return {
					...sort,
					active: false,
				};
			}),
			...remainingNewSorts.map((sort: TSort) => {
				return {...sort, active: true};
			}),
		];
	};

	const getInitialViewsState = () => {
		const defaultSnapshot: any = {
			modifiedFields: {},
			paginationDelta:
				showPagination &&
				(pagination?.initialDelta || DEFAULT_PAGINATION_DELTA),
		};

		const customInternalViews =
			customRenderers?.views?.map((customRenderer: TRenderer) => ({

				// Need to check presence of property in TRenderer Union type

				component:
					'component' in customRenderer && customRenderer.component,
				default: 'default' in customRenderer && customRenderer?.default,
				label: 'label' in customRenderer && customRenderer?.label,
				name: customRenderer.name,
				schema: 'schema' in customRenderer && customRenderer?.schema,
				thumbnail: 'symbol' in customRenderer && customRenderer?.symbol,
			})) || [];

		let initialActiveView =
			views.find(({default: defaultProp}) => defaultProp) ||
			customInternalViews?.find(
				({default: defaultProp}) => defaultProp
			) ||
			views[0] ||
			(customInternalViews?.length && customInternalViews[0]);

		defaultSnapshot.activeView = {
			component: getViewComponent(initialActiveView as IView),
			...initialActiveView,
		};

		let initialVisibleFieldNames = {};

		const visibleFieldNames = getVisibleFields();

		defaultSnapshot.visibleFieldNames = initialVisibleFieldNames;

		if (visibleFieldNames) {
			initialVisibleFieldNames = visibleFieldNames;
		}

		const view = getView();

		if (view) {
			const activeView = views.find(({name}) => name === view);

			if (activeView) {
				initialActiveView = activeView;
			}
		}

		const activeView = {
			component: getViewComponent(initialActiveView as IView),
			...initialActiveView,
		};

		defaultSnapshot.filters = initialFilters
			? initialFilters.map((filter) => {
					const preloadedData = deepClone(filter.preloadedData);
					if (preloadedData) {
						filter = activateFilter({
							filter,
							selectedData: preloadedData,
						});
					}

					return filter;
				})
			: [];

		const paginationDelta =
			showPagination && (getDelta() || defaultSnapshot.paginationDelta);

		const pageNumber =
			getPageNumber() ||
			pagination?.initialPageNumber ||
			DEFAULT_PAGINATION_PAGE_NUMBER;

		defaultSnapshot.sorts = sortsProp;

		const sorts = updateSortsActivation({
			newSorts: getActiveSorts(),
			oldSorts: sortsProp,
		});

		const parsedSnapshots = snapshots?.map((snapshot: ISnapshot) => ({
			...snapshot,
			configuration: JSON.parse(snapshot.configuration),
		}));

		return {
			activeView,
			defaultSnapshot,
			groupedFilters,
			modifiedFields: {},
			pageNumber,
			paginationDelta,
			snapshots: parsedSnapshots,
			snapshotsEnabled,
			sorts,
			views: [...views, ...customInternalViews],
			visibleFieldNames: initialVisibleFieldNames,
		};
	};

	const [viewsState, viewsDispatch] = useThunk(
		useReducer(viewsReducer, getInitialViewsState())
	);

	const {activeView, pageNumber, paginationDelta, sorts} = viewsState;

	const handleDeltaChange = useCallback(
		(delta: number) => {
			viewsDispatch(updateDelta(delta));
		},
		[updateDelta, viewsDispatch]
	);

	const {
		component: View,
		contentRendererModuleURL,
		name: activeViewName,
		...currentViewProps
	} = activeView;

	const requestData = useCallback(() => {
		if (!apiURL) {
			return;
		}

		const unfrozenGlobalFDSState: IFDSState = deepClone(globalFDSState);

		const activeFilters: Array<IBaseFilterState> =
			unfrozenGlobalFDSState.filters.filter((filter) => filter.active) ||
			[];

		const activeFiltersOdataStrings = activeFilters.map((filter) => {
			const filterImplementation = FILTER_IMPLEMENTATIONS[filter.type];

			return filterImplementation.getOdataString(filter);
		});

		const activeSorts =
			sorts.length > 1
				? sorts.filter((sort: TSort) => sort.active)
				: sorts;

		return loadData({
			additionalAPIURLParameters,
			apiURL,
			currentURL,
			delta: paginationDelta,
			odataFiltersStrings: activeFiltersOdataStrings,
			page: pageNumber,
			searchParam: unfrozenGlobalFDSState.search.query,
			sorts: activeSorts,
		});
	}, [
		additionalAPIURLParameters,
		apiURL,
		currentURL,
		globalFDSState,
		pageNumber,
		paginationDelta,
		sorts,
	]);

	const onClearFilters = useCallback(() => {
		const unfrozenGlobalFDSState: IFDSState = deepClone(globalFDSState);

		const filters = unfrozenGlobalFDSState.filters.map((filter) =>
			deactivateFilter(filter)
		);

		setGlobalFDSState({
			...unfrozenGlobalFDSState,
			filters,
			search: {query: ''},
		});
	}, [globalFDSState, setGlobalFDSState]);

	const skipSnapshotsUpdatedChangeRef = useRef(true);

	useEffect(() => {
		if (
			globalFDSStateInitialized ||
			!filterClientExtensionsLoaded ||
			!cellClientExtensionsLoaded
		) {
			return;
		}

		setGlobalFDSStateInitialized(true);
	}, [
		cellClientExtensionsLoaded,
		filterClientExtensionsLoaded,
		globalFDSStateInitialized,
	]);

	useEffect(() => {
		if (!globalFDSStateInitialized) {
			return;
		}

		const unfrozenGlobalFDSState = deepClone(globalFDSState);

		const configInURL: Partial<IConfigInURL> | null = readConfigFromURL(id);

		unfrozenGlobalFDSState.filters.forEach((filter: IBaseFilterState) => {
			if (filter.preloadedData || filter.selectedData) {
				const preloadedData = JSON.stringify(filter.preloadedData);
				const selectedData = JSON.stringify(filter.selectedData);

				if (
					preloadedData !== selectedData ||
					(preloadedData === selectedData &&
						configInURL?.filters?.some(
							(filterURL) => filterURL.id === filter.id
						))
				) {
					updateConfigInURL({
						[EConfigInURLKeys.ACTIVE_FILTERS]:
							unfrozenGlobalFDSState.filters,
					});
				}
			}
		});

		if (
			(unfrozenGlobalFDSState.search.query || configInURL?.q) &&
			unfrozenGlobalFDSState.search.query !== configInURL?.q
		) {
			updateConfigInURL({
				[EConfigInURLKeys.SEARCH_PARAM]:
					unfrozenGlobalFDSState.search.query,
			});
		}

		if (skipSnapshotsUpdatedChangeRef.current) {
			skipSnapshotsUpdatedChangeRef.current = false;
		}
		else {
			viewsDispatch({
				type: EViewsActionTypes.UPDATE_SNAPSHOT_UPDATED,
				value: true,
			});
		}
	}, [
		globalFDSState,
		globalFDSStateInitialized,
		id,
		updateConfigInURL,
		viewsDispatch,
	]);

	const updateDataSetItems = useCallback(
		(dataSetData: IDataSetData) => {
			const remappedItems = dataSetData.items.map((item) => {
				if (item.embedded && item.embedded.actions) {
					const actions = item.embedded.actions;

					delete item.embedded.actions;

					return {
						...item,
						actions,
					};
				}

				return {
					...item,
				};
			});

			setItems(remappedItems);
			setTotal(dataSetData.totalCount);

			if (!dataSetData.items.length && dataSetData.totalCount > 0) {
				viewsDispatch(updatePageNumber(dataSetData.lastPage));
			}
		},
		[updatePageNumber, viewsDispatch]
	);

	useEffect(() => {
		if (
			globalFDSStateInitialized ||
			filterClientExtensionsLoading ||
			filterClientExtensionsLoaded ||
			cellClientExtensionsLoading ||
			cellClientExtensionsLoaded
		) {
			return;
		}

		const searchParam = getSearchParam();

		const preloadFilters = (
			filters: Array<IBaseFilterState> | undefined
		): Array<IBaseFilterState> => {
			if (!filters) {
				return [];
			}

			const configInURL: Partial<IConfigInURL> | null =
				readConfigFromURL(id);

			const urlFilters = configInURL?.[EConfigInURLKeys.ACTIVE_FILTERS];

			if (urlFilters) {
				return updateFilterActivation({
					newFilters: urlFilters,
					oldFilters: filters,
				});
			}

			return (
				filters.map((filter) => {
					const preloadedData = filter.preloadedData;

					if (preloadedData) {
						filter = activateFilter({
							filter,
							selectedData: preloadedData,
						});
					}

					return filter;
				}) || []
			);
		};

		const filterClientExtensionDefinitions = initialFilters
			? initialFilters
					.filter((filter) => filter.clientExtensionFilterURL)
					.map((filter) => ({
						context: filter,
						importDeclaration: `default from ${filter.clientExtensionFilterURL}`,
					}))
			: [];

		if (filterClientExtensionDefinitions.length) {
			setFilterClientExtensionsLoading(true);
		}
		else {
			setFilterClientExtensionsLoaded(true);

			setGlobalFDSState({
				...globalFDSState,
				filters: preloadFilters(initialFilters),
				search: {query: searchParam ?? ''},
			});
		}

		const cellClientExtensionDefinitions = views.reduce(
			(
				clientExtensionDefinitions: Array<
					ClientExtensionDefinition<any>
				>,
				view: IView
			) => {
				if (view.schema && 'fields' in view.schema) {
					if (!view.schema.fields.length) {
						return clientExtensionDefinitions;
					}

					const clientExtensionFields = view.schema.fields.filter(
						(field: IField) =>
							!!field.contentRendererClientExtension
					);

					for (const field of clientExtensionFields) {
						clientExtensionDefinitions.push({
							context: field,
							importDeclaration: field.contentRendererModuleURL,
						});
					}

					return clientExtensionDefinitions;
				}
				else {
					return [];
				}
			},
			[]
		);

		if (cellClientExtensionDefinitions.length) {
			setCellClientExtensionsLoading(true);
		}
		else {
			setCellClientExtensionsLoaded(true);
		}

		if (
			!filterClientExtensionDefinitions.length &&
			!cellClientExtensionDefinitions.length
		) {
			return;
		}

		loadClientExtensions([
			{
				clientExtensionDefinitions: filterClientExtensionDefinitions,
				onLoad: (
					resolutions: Array<ClientExtensionResolution<any>>
				) => {
					const newFilters: Array<IBaseFilterState> =
						initialFilters?.map((filter) => {
							const resolution = resolutions.find(
								(resolution: ClientExtensionResolution<any>) =>
									resolution.context
										.clientExtensionFilterURL ===
									filter.clientExtensionFilterURL
							);

							if (resolution) {
								if (resolution.error) {
									return {
										...filter,
										clientExtensionResolutionError:
											resolution.error,
									};
								}

								return {
									...filter,
									clientExtensionFilterImplementation:
										resolution.binding,
								};
							}

							return filter;
						}) || [];

					setGlobalFDSState({
						...globalFDSState,
						filters: preloadFilters(newFilters),
						search: {query: searchParam ?? ''},
					});

					setFilterClientExtensionsLoading(false);
					setFilterClientExtensionsLoaded(true);
				},
			},
			{
				clientExtensionDefinitions: cellClientExtensionDefinitions,
				onLoad: (
					resolutions: Array<ClientExtensionResolution<any>>
				) => {
					resolutions.forEach((resolution) => {
						const {binding, context: field, error} = resolution;

						if (error) {
							viewsDispatch({
								type: EViewsActionTypes.UPDATE_FIELD,
								value: {
									clientExtensionResolutionError: error,
									name: field.fieldName,
								},
							});

							return;
						}

						viewsDispatch({
							type: EViewsActionTypes.UPDATE_FIELD,
							value: {
								htmlElementBuilder: binding,
								name: field.fieldName,
							},
						});
					});

					setCellClientExtensionsLoading(false);
					setCellClientExtensionsLoaded(true);
				},
			},
		]);
	}, [
		cellClientExtensionsLoaded,
		cellClientExtensionsLoading,
		filterClientExtensionsLoaded,
		filterClientExtensionsLoading,
		getSearchParam,
		globalFDSState,
		globalFDSStateInitialized,
		id,
		initialFilters,
		setGlobalFDSState,
		views,
		viewsDispatch,
	]);

	useEffect(() => {
		if (itemsProp) {

			// Assuming default pagination values if data comes from items instead of apiURL

			updateDataSetItems({
				items: itemsProp,
				lastPage: 1,
				page: 1,
				totalCount: itemsProp.length,
			});
		}
	}, [itemsProp, updateDataSetItems]);

	function deselectItems(value: any) {
		const values = Array.isArray(value) ? value : [value];

		const newSelectedItems = selectedItems.filter((selectedItem) => {
			const selectedItemValue = getObjectValueFromPath({
				object: selectedItem,
				path: selectedItemsKey,
			});

			return !values.includes(selectedItemValue);
		});

		setSelectedItems(newSelectedItems);
	}

	function selectItems(value: any, forceSingleSelection = false) {
		const values = Array.isArray(value) ? value : [value];

		if (forceSingleSelection) {
			setSelectedItems(
				selectedItemsValue.length === 1 &&
					selectedItemsValue.includes(value)
					? []
					: [
							items.find(
								(item) =>
									getObjectValueFromPath({
										object: item,
										path: selectedItemsKey,
									}) === values[0]
							),
						]
			);

			return;
		}

		const nextSelectedValues =
			selectionType === 'single' ? [] : [...selectedItemsValue];

		values.forEach((val) => {
			const index = nextSelectedValues.indexOf(val);

			if (index > -1) {
				nextSelectedValues.splice(index, 1);
			}
			else {
				nextSelectedValues.push(val);
			}
		});

		const nextSelectedItems = nextSelectedValues
			.map((val) =>
				[...selectedItems, ...items].find(
					(item) =>
						getObjectValueFromPath({
							object: item,
							path: selectedItemsKey,
						}) === val
				)
			)
			.filter(Boolean);

		setSelectedItems(nextSelectedItems);
	}

	function highlightItems(value = []) {
		if (Array.isArray(value)) {
			return setHighlightedItemsValue(value);
		}

		const itemAdded = highlightedItemsValue.find((item) => item === value);

		if (!itemAdded) {
			setHighlightedItemsValue(highlightedItemsValue.concat(value));
		}
	}

	const dataSetWrapperRef: RefObject<HTMLDivElement> = useRef(null);

	useEffect(() => {
		if (dataSetWrapperRef.current) {
			const form = (dataSetWrapperRef.current as HTMLElement).closest(
				'form'
			);

			if (form?.dataset.sennaOff === null) {
				form.setAttribute('data-senna-off', 'true');
			}
		}
	}, [dataSetWrapperRef]);

	const handlePopState = useCallback(() => {
		const stateUpdates: Array<{
			type: EViewsActionTypes;
			value: IConfigInURL[keyof IConfigInURL];
		}> = [];

		const activeFilters = getFilters();
		const searchParam = getSearchParam();

		if (activeFilters || searchParam) {
			const unfrozenGlobalFDSState: IFDSState = deepClone(globalFDSState);

			setGlobalFDSState({
				...unfrozenGlobalFDSState,
				filters: updateFilterActivation({
					newFilters: activeFilters,
					oldFilters: unfrozenGlobalFDSState.filters,
				}),
				search: {
					query: searchParam ?? '',
				},
			});
		}

		const activeSorts = getActiveSorts();

		if (activeSorts) {
			stateUpdates.push({
				type: EViewsActionTypes.UPDATE_SORTING,
				value: updateSortsActivation({
					newSorts: activeSorts,
					oldSorts: sorts,
				}),
			});
		}

		const delta = getDelta();

		if (delta && delta !== paginationDelta) {
			stateUpdates.push({
				type: EViewsActionTypes.UPDATE_PAGINATION_DELTA,
				value: delta,
			});
		}

		const view = getView();

		if (view) {
			stateUpdates.push({
				type: EViewsActionTypes.UPDATE_ACTIVE_VIEW,
				value: view,
			});
		}

		stateUpdates.push({
			type: EViewsActionTypes.UPDATE_PAGE_NUMBER,
			value: getPageNumber() || 1,
		});

		const visibleFields = getVisibleFields();

		if (visibleFields) {
			stateUpdates.push({
				type: EViewsActionTypes.UPDATE_VISIBLE_FIELD_NAMES,
				value: visibleFields,
			});
		}

		if (stateUpdates.length) {
			viewsDispatch({
				type: EViewsActionTypes.BATCH_UPDATE,
				value: stateUpdates,
			});
		}
	}, [
		getActiveSorts,
		getDelta,
		getFilters,
		getPageNumber,
		getSearchParam,
		getView,
		getVisibleFields,
		globalFDSState,
		paginationDelta,
		setGlobalFDSState,
		sorts,
		viewsDispatch,
	]);

	const refreshData = useCallback(
		(successNotification?: ISuccessNotification) => {
			setDataLoading(true);

			return requestData()!
				.then(({data}) => {
					if (successNotification?.showSuccessNotification) {
						openToast({
							message:
								successNotification.message ||
								Liferay.Language.get('table-data-updated'),
							type: 'success',
						});
					}

					if (isMounted()) {
						updateDataSetItems(data);

						setSelectedItems(
							data.items.filter(
								(item: ISelectionFilterStateItem) => {
									const itemValue = getObjectValueFromPath({
										object: item,
										path: selectedItemsKey,
									});

									return selectedItemsValue.includes(
										itemValue
									);
								}
							)
						);

						setDataLoading(false);

						Liferay.fire(EVENTS.DISPLAY_UPDATED, {id});
					}

					return data;
				})
				.catch((error) => {
					setDataLoading(false);

					throw error;
				});
		},
		[
			id,
			isMounted,
			requestData,
			selectedItemsKey,
			selectedItemsValue,
			setSelectedItems,
			updateDataSetItems,
		]
	);

	useEffect(() => {
		if (View || !contentRendererModuleURL) {
			return;
		}

		setComponentLoading(true);

		loadModule(contentRendererModuleURL)
			.then((view: IView) => {
				if (isMounted()) {
					viewsDispatch({
						type: EViewsActionTypes.UPDATE_VIEW_COMPONENT,
						value: {component: view, name: activeViewName},
					});

					setComponentLoading(false);
				}
			})
			.catch(() => {
				openToast({
					message: Liferay.Language.get('unexpected-error'),
					type: 'danger',
				});
			});
	}, [
		View,
		activeViewName,
		contentRendererModuleURL,
		viewsDispatch,
		isMounted,
		setComponentLoading,
	]);

	const handleApiError = ({
		data,
		statusCode,
	}: {
		data: {
			status: string;
			title: string;
			type?: string;
		};
		statusCode: number;
	}): void => {
		const apiErrorMessage = `${data.status}, ${data.title}`;

		logError(apiErrorMessage);

		openToast({
			message: apiErrorMessage,
			title: `${Liferay.Language.get('error')} ${statusCode}`,
			type: 'danger',
		});
	};

	useEffect(() => {
		if (!apiURL || !globalFDSStateInitialized) {
			return;
		}

		setDataLoading(true);

		requestData()!.then(({data, ok, status: statusCode}) => {
			if (isMounted()) {
				if (!ok) {
					handleApiError({data, statusCode});
				}
				else {
					setCreationMenu((currentCreationMenu) => {
						if (!currentCreationMenu) {
							return;
						}

						const filteredCreationMenu: IFrontendDataSetProps['creationMenu'] =
							{
								primaryItems: filterCreationActions({
									customActions:
										currentCreationMenu?.primaryItems,
									globalCollectionActions: data?.actions,
								}),
							};

						return filteredCreationMenu;
					});

					updateDataSetItems(data);
				}

				setDataLoading(false);

				setSearching(false);
			}
		});
	}, [
		apiURL,
		globalFDSStateInitialized,
		isMounted,
		requestData,
		updateDataSetItems,
	]);

	useEffect(() => {
		function handleRefreshFromTheOutside(event: any) {
			if (event.id === id) {
				refreshData();
			}
		}

		function handleCloseSidePanel() {
			setHighlightedItemsValue([]);
		}

		Liferay.on(EVENTS.SIDE_PANEL_CLOSED, handleCloseSidePanel);
		Liferay.on(EVENTS.UPDATE_DISPLAY, handleRefreshFromTheOutside);

		const registerPopstateEvent =
			configInURLBehavior === EConfigInURLBehavior.PUSH &&
			(!Liferay.SPA || !Liferay.SPA.app);

		if (registerPopstateEvent) {
			window.addEventListener('popstate', handlePopState);
		}

		return () => {
			Liferay.detach(EVENTS.SIDE_PANEL_CLOSED, handleCloseSidePanel);
			Liferay.detach(
				EVENTS.UPDATE_DISPLAY,
				handleRefreshFromTheOutside as () => void
			);
			if (registerPopstateEvent) {
				window.removeEventListener('popstate', handlePopState);
			}
		};
	}, [configInURLBehavior, handlePopState, id, refreshData]);

	const fdsRef = useRef(null);

	const hasSearch = !!globalFDSState.search.query;
	const hasActiveFilters = globalFDSState.filters.some(
		(filter) => filter.active
	);

	const showManagementToolbar =
		showManagementBar &&
		(!!items.length ||
			hasSearch ||
			hasActiveFilters ||
			!hideManagementBarInEmptyState);

	const managementBar = showManagementToolbar ? (
		<div className="management-bar-wrapper">
			<ManagementBar
				bulkActions={bulkActions}
				creationMenu={creationMenu}
				dataLoading={dataLoading}
				deselectItems={(items: Array<any>) => {
					deselectItems(items);

					if (allItemsSelectedActive) {
						setAllItemsSelectedActive(false);
					}
				}}
				fluid={style === 'fluid'}
				items={items}
				onBulkActionsClear={() => {
					deselectItems(selectedItemsValue);

					setAllItemsSelectedActive(false);
				}}
				onSelectAll={(value: boolean) => {
					setAllItemsSelectedActive(value);
				}}
				selectItems={(items: Array<any>) => {
					selectItems(items);
				}}
				selectedItems={selectedItems}
				selectedItemsKey={selectedItemsKey}
				selectedItemsValue={selectedItemsValue}
				selectionType={selectionType}
				showNavBarWhenSelected={showNavBarWhenSelected}
				showSearch={showSearch}
				showSelectAll={showSelectAll}
				total={total}
			/>
		</div>
	) : null;

	const InlineNotificationWrapper = ({
		inlineNotificationComponent,
	}: {
		inlineNotificationComponent: React.ComponentType<IInlineNotificationComponent>;
	}) => {
		return inlineNotificationComponent ? (
			<div
				className={classNames(
					'container-fluid align-items-center inline-notification-bar',
					style === 'fluid' && 'px-0'
				)}
			>
				<InlineNotification
					component={inlineNotificationComponent}
				></InlineNotification>
			</div>
		) : (
			<></>
		);
	};

	const view =
		!dataLoading && !componentLoading ? (
			<div className="data-set-content-wrapper">
				<input
					hidden
					name={`${namespace || id + '_'}${
						actionParameterName || selectedItemsKey
					}`}
					readOnly
					value={selectedItemsValue.join(',')}
				/>

				{items?.length ||
				overrideEmptyResultView ||
				inlineAddingSettings ? (
					<View
						frontendDataSetContext={FrontendDataSetContext}
						header={header}
						items={items}
						itemsActions={itemsActions}
						onItemSelectionChange={(
							selectedItem: ISelectionFilterStateItem,
							forceSingleSelection: boolean
						) => {
							if (allItemsSelectedActive) {
								setSelectedItems(
									items.filter(
										(item) =>
											getObjectValueFromPath({
												object: item,
												path: selectedItemsKey,
											}) !==
											getObjectValueFromPath({
												object: selectedItem,
												path: selectedItemsKey,
											})
									)
								);

								setAllItemsSelectedActive(false);
							}
							else {
								selectItems(
									getObjectValueFromPath({
										object: selectedItem,
										path: selectedItemsKey,
									}),
									forceSingleSelection
								);
							}
						}}
						style={style}
						{...currentViewProps}
					/>
				) : (
					<EmptyState
						creationMenu={creationMenu}
						emptyStateConfiguration={emptyState}
						onClearFilters={onClearFilters}
					/>
				)}
			</div>
		) : (
			<ClayLoadingIndicator className="my-7" />
		);

	const paginationComponent =
		showPagination && pagination && items?.length && total ? (
			<div className="data-set-pagination-wrapper">
				<ClayPaginationBarWithBasicItems
					active={pageNumber}
					activeDelta={paginationDelta}
					deltas={pagination?.deltas}
					disableEllipsis={items.length / paginationDelta - 5 > 999}
					ellipsisBuffer={3}
					labels={{
						paginationResults: Liferay.Language.get(
							'showing-x-to-x-of-x-entries'
						),
						perPageItems: Liferay.Language.get('x-items'),
						selectPerPageItems: Liferay.Language.get('x-items'),
					}}
					onActiveChange={(page: number) =>
						viewsDispatch(updatePageNumber(page))
					}
					onDeltaChange={handleDeltaChange}
					totalItems={total}
				/>
			</div>
		) : null;

	function executeAsyncItemAction({
		errorMessage,
		method = 'GET',
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
	}): Promise<void> {
		const requestOptions: IRequestOptions = {
			headers: {
				'Accept': 'application/json',
				'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
				'Content-Type': 'application/json',
			},
			method,
		};

		if (method.toUpperCase() !== 'GET') {
			requestOptions.body = requestBody ? requestBody : '{}';
		}

		return fetch(url, requestOptions)
			.then((response) => {
				if (response.ok) {
					Liferay.fire(EVENTS.ACTION_PERFORMED, {
						id,
					});

					openToast({
						message:
							successMessage ||
							Liferay.Language.get(
								'your-request-completed-successfully'
							),
						type: 'success',
					});

					refreshData();
				}
				else {
					openToast({
						message:
							errorMessage ||
							Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
						type: 'danger',
					});

					setActionItemLoading?.(false);
				}
			})
			.catch(() => {
				openToast({
					message:
						errorMessage ||
						Liferay.Language.get('an-unexpected-error-occurred'),
					type: 'danger',
				});

				setActionItemLoading?.(false);
			});
	}

	function openSidePanel(config: IModalConfig) {
		return Liferay.fire(EVENTS.OPEN_SIDE_PANEL, {
			id: dataSetSupportSidePanelIdRef.current,
			onSubmit: refreshData,
			...config,
		});
	}

	function openModal(config: IModalConfig) {
		return Liferay.fire(EVENTS.OPEN_MODAL, {
			id: dataSetSupportModalIdRef.current,
			onSubmit: refreshData,
			...config,
		});
	}

	function onItemsChange({
		itemKey = 'id',
		items: itemsChanged,
	}: {
		itemKey: string;
		items: any;
	}): void {
		const updatedItems = new Map(
			[...items, ...itemsChanged].map((item) => [item[itemKey], item])
		);

		setItems(Array.from(updatedItems.values()));
	}

	function updateItem(
		itemKey: string,
		property: string,
		valuePath: string,
		value = null
	): void {
		const itemChanges = getCurrentItemUpdates(
			items,
			itemsChanges,
			selectedItemsKey,
			itemKey,
			property,
			value,
			valuePath
		);

		setItemsChanges({
			...itemsChanges,
			[itemKey]: itemChanges,
		});
	}

	const unfrozenGlobalFDSState: IFDSState = deepClone(globalFDSState);

	const handleSnapshotChange = ({defaultSnapshot, snapshots, value}: any) => {
		if (value === 'DEFAULT_VIEW') {
			updateConfigInURL({
				[EConfigInURLKeys.ACTIVE_FILTERS]: defaultSnapshot.filters,
				[EConfigInURLKeys.ACTIVE_SORTS]: defaultSnapshot.sorts,
				[EConfigInURLKeys.DELTA]: {...defaultSnapshot.paginationDelta},
				[EConfigInURLKeys.VIEW_NAME]: {
					...defaultSnapshot.activeView.name,
				},
				[EConfigInURLKeys.VISIBLE_FIELDS]: {
					...defaultSnapshot.visibleFieldNames,
				},
			});

			viewsDispatch({
				type: EViewsActionTypes.RESET_TO_DEFAULT_SNAPSHOT,
			});

			skipSnapshotsUpdatedChangeRef.current = true;

			setGlobalFDSState({
				...unfrozenGlobalFDSState,
				filters: defaultSnapshot.filters,
			});
		}
		else {
			const snapshot = deepClone(
				snapshots.find((view: ISnapshot) => view.erc === value)
			);

			updateConfigInURL({
				[EConfigInURLKeys.ACTIVE_FILTERS]:
					snapshot.configuration.filters,
				[EConfigInURLKeys.ACTIVE_SORTS]: updateSortsActivation({
					newSorts: snapshot.configuration.sorts,
					oldSorts: sorts,
				}),
				[EConfigInURLKeys.DELTA]:
					snapshot.configuration.paginationDelta,
				[EConfigInURLKeys.VIEW_NAME]:
					snapshot.configuration.activeView.name,
				[EConfigInURLKeys.VISIBLE_FIELDS]:
					snapshot.configuration.visibleFieldNames,
			});

			viewsDispatch({
				type: EViewsActionTypes.UPDATE_ACTIVE_SNAPSHOT,
				value: snapshot,
			});

			skipSnapshotsUpdatedChangeRef.current = true;

			setGlobalFDSState({
				...unfrozenGlobalFDSState,
				filters: snapshot.configuration.filters,
			});
		}
	};

	function toggleItemInlineEdit(itemKey: any) {
		setItemsChanges(({[itemKey]: foundItem, ...itemsChanges}) => {
			return foundItem
				? itemsChanges
				: {
						...itemsChanges,
						[itemKey]: {},
					};
		});
	}

	function createInlineItem() {
		if (!inlineAddingSettings?.apiURL) {
			return;
		}

		const defaultBodyContent =
			inlineAddingSettings.defaultBodyContent || {};
		const newItemBodyContent = formatItemChanges(itemsChanges[0]);

		return fetch(inlineAddingSettings.apiURL, {
			body: JSON.stringify({
				...defaultBodyContent,
				...newItemBodyContent,
			}),
			headers: {
				'Accept': 'application/json',
				'Content-Type': 'application/json',
			},
			method: inlineAddingSettings.method || 'POST',
		})
			.then((response) => {
				if (!isMounted()) {
					return;
				}

				if (!response.ok) {
					return response
						.json()
						.then((jsonResponse) =>
							Promise.reject(new Error(jsonResponse.title))
						);
				}

				setItemsChanges((itemsChanges) => ({
					...itemsChanges,
					[0]: {},
				}));

				return refreshData({
					message: Liferay.Language.get(
						'item-was-successfully-created'
					),
					showSuccessNotification: true,
				});
			})
			.catch((error) => {
				logError(error);
				openToast({
					message: error.message,
					type: 'danger',
				});

				throw error;
			});
	}

	function applyItemInlineUpdates(itemKey: any) {
		const itemToBeUpdated = items.find(
			(item) =>
				getObjectValueFromPath({
					object: item,
					path: selectedItemsKey,
				}) === itemKey
		);

		const defaultBody = inlineEditingSettings?.defaultBodyContent || {};

		return fetch(itemToBeUpdated.actions.update.href, {
			body: JSON.stringify({
				...defaultBody,
				...formatItemChanges(itemsChanges[itemKey]),
			}),
			headers: {
				'Accept': 'application/json',
				'Content-Type': 'application/json',
			},
			method: itemToBeUpdated.actions.update.method,
		})
			.then((response) => {
				if (!isMounted()) {
					return;
				}

				if (!response.ok) {
					return response
						.json()
						.then((jsonResponse) =>
							Promise.reject(new Error(jsonResponse.title))
						);
				}

				toggleItemInlineEdit(itemKey);

				return refreshData({
					message: Liferay.Language.get(
						'item-was-successfully-updated'
					),
					showSuccessNotification: true,
				});
			})
			.catch((error) => {
				logError(error);
				openToast({
					message: error.message,
					type: 'danger',
				});

				throw error;
			});
	}

	function forceSortsUpdate(sorts: TSort[]) {
		viewsDispatch(updateActiveSorts(sorts));
	}

	function updateAdditionalAPIURLParameters(parameters: string) {
		setAdditionalAPIURLParameters(parameters);
	}

	const selectable = !!selectionType;

	const {className} = useFDSDrop({
		targetDropRef: dataSetWrapperRef,
	});

	return (
		<FrontendDataSetContext.Provider
			value={{
				actionParameterName,
				allItemsSelectedActive,
				apiURL,
				appURL,
				applyItemInlineUpdates,
				createInlineItem,
				customDataRenderers,
				customRenderers,
				executeAsyncItemAction,
				forceSortsUpdate,
				formId,
				formName,
				globalFDSState: unfrozenGlobalFDSState,
				hideManagementBarInEmptyState,
				highlightItems,
				highlightedItemsValue,
				id,
				infoPanelId: dataSetSupportInfoPanelIdRef.current,
				infoPanelOpen,
				inlineAddingSettings,
				inlineEditingSettings,
				itemsActions,
				itemsChanges,
				loadData: refreshData,
				modalId: dataSetSupportModalIdRef.current,
				namespace,
				nestedItemsKey,
				nestedItemsReferenceKey,
				onActionDropdownItemClick,
				onBulkActionItemClick,
				onClearResultsBar: () => {
					const filters = unfrozenGlobalFDSState.filters.map(
						(filter) => deactivateFilter(filter)
					);

					setGlobalFDSState({
						...unfrozenGlobalFDSState,
						filters,
						search: {
							query: '',
						},
					});
				},
				onClearSearch: () => {
					skipSnapshotsUpdatedChangeRef.current = true;

					setGlobalFDSState({
						...unfrozenGlobalFDSState,
						search: {
							query: '',
						},
					});
				},
				onFilterChange: ({
					changedFilter,
				}: {
					changedFilter: IBaseFilterState;
				}) => {
					const filters = unfrozenGlobalFDSState.filters.map(
						(filter) =>
							filter.id === changedFilter.id
								? changedFilter
								: filter
					);

					setGlobalFDSState({
						...unfrozenGlobalFDSState,
						filters,
					});
				},
				onInfoPanelToggleButtonClick: () => {
					setInfoPanelOpen((value) => !value);
				},
				onItemsChange,
				onSearch: ({query}) => {
					skipSnapshotsUpdatedChangeRef.current = true;

					setGlobalFDSState({
						...unfrozenGlobalFDSState,
						search: {
							query,
						},
					});
				},
				onSnapshotChange: handleSnapshotChange,
				openModal,
				openSidePanel,
				portletId,
				searchParam: unfrozenGlobalFDSState.search.query,
				searching,
				selectable,
				selectedItems,
				selectedItemsKey,
				selectedItemsValue,
				selectionType,
				showBulkActionsManagementBar,
				showBulkActionsManagementBarActions,
				showInfoPanel: infoPanelComponent ? true : false,
				sidePanelId: dataSetSupportSidePanelIdRef.current,
				sorts,
				style,
				toggleItemInlineEdit,
				uniformActionsDisplay,
				updateActiveSorts,
				updateAdditionalAPIURLParameters,
				updateDataSetItems,
				updateFilters,
				updateItem,
				updateView,
				updateVisibleFields,
			}}
		>
			<ViewsContext.Provider value={[viewsState, viewsDispatch]}>
				{isFileDropEnabled(fileDropSettings) && (
					<DragLayer dataSetWrapperRef={dataSetWrapperRef} />
				)}

				{filterClientExtensionsLoading ||
				cellClientExtensionsLoading ? (
					<ClayLoadingIndicator className="my-7" />
				) : (
					<div className="fds" ref={fdsRef}>
						<Modal
							id={dataSetSupportModalIdRef.current}
							onClose={refreshData}
						/>

						{!sidePanelId && (
							<SidePanel
								id={dataSetSupportSidePanelIdRef.current}
								onAfterSubmit={refreshData}
							/>
						)}

						{infoPanelComponent && (
							<InfoPanel
								className="fds-info-panel"
								component={infoPanelComponent}
								containerRef={fdsRef}
								id={dataSetSupportInfoPanelIdRef.current}
								onOpenChange={setInfoPanelOpen}
								open={infoPanelOpen}
							/>
						)}

						<div
							className={classNames(
								`data-set-wrapper visualization-mode-${activeView.contentRenderer}`,
								className,
								{
									selectable,
								}
							)}
							data-testid={`visualization-mode-${activeView.name}`}
							ref={dataSetWrapperRef}
						>
							{style === 'default' && (
								<div className="data-set data-set-inline">
									{managementBar}

									{inlineNotificationComponent && (
										<InlineNotificationWrapper
											inlineNotificationComponent={
												inlineNotificationComponent
											}
										/>
									)}

									{view}

									{paginationComponent}
								</div>
							)}

							{style === 'stacked' && (
								<div className="data-set data-set-stacked">
									{managementBar}

									{inlineNotificationComponent && (
										<InlineNotificationWrapper
											inlineNotificationComponent={
												inlineNotificationComponent
											}
										/>
									)}

									{view}

									{paginationComponent}
								</div>
							)}

							{style === 'fluid' && (
								<div className="data-set data-set-fluid">
									{managementBar}

									{inlineNotificationComponent && (
										<InlineNotificationWrapper
											inlineNotificationComponent={
												inlineNotificationComponent
											}
										/>
									)}

									<div className="container-fluid mt-3">
										{view}

										{paginationComponent}
									</div>
								</div>
							)}
						</div>
					</div>
				)}
			</ViewsContext.Provider>
		</FrontendDataSetContext.Provider>
	);
};

const FrontendDataSet = ({
	fileDropSettings,
	selectedItemsKey,
	...otherProps
}: IFrontendDataSetProps) => {
	fileDropSettings = fileDropSettings
		? fileDropSettings
		: {
				enabled: false,
				isDropTarget: () => true,
			};

	const {handleFileDrop} = useFileUploader({
		fileDropSettings,
		selectedItemsKey,
	});

	return (
		<DnDContext.Provider
			value={{
				fileDropSettings,
				handleFileDrop,
			}}
		>
			<FDSDndProvider>
				<FrontendDataSetContent
					selectedItemsKey={selectedItemsKey}
					{...otherProps}
				/>
			</FDSDndProvider>
		</DnDContext.Provider>
	);
};

export default FrontendDataSet;
