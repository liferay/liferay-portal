/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import {useIsMounted, useThunk} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import {IHTMLElementBuilder, openToast} from 'frontend-js-components-web';
import {fetch, loadClientExtensions, loadModule} from 'frontend-js-web';
import React, {
	useCallback,
	useEffect,
	useReducer,
	useRef,
	useState,
} from 'react';

import './styles/main.scss';

import ClayEmptyState from '@clayui/empty-state';

import FrontendDataSetContext, {
	IDataSetData,
	TRenderer,
} from './FrontendDataSetContext';
import {InfoPanel} from './info_panel/InfoPanel';

// @ts-ignore

import ManagementBar from './management_bar/ManagementBar';
import CreationMenu from './management_bar/controls/CreationMenu';
import {FILTER_IMPLEMENTATIONS} from './management_bar/controls/filters/Filter';

// @ts-ignore

import Modal from './modal/Modal';

// @ts-ignore

import SidePanel from './side_panel/SidePanel';
import filterCreationActions from './utils/actionItems/filterCreationActions';
import EVENTS from './utils/eventsDefinitions';
import getRandomId from './utils/getRandomId';

// @ts-ignore

import {formatItemChanges, getCurrentItemUpdates} from './utils/index';
import {loadData} from './utils/loadData';

// @ts-ignore

import {logError} from './utils/logError';
import {
	IField,
	IFrontendDataSetProps,
	IModalConfig,
	IRequestOptions,
	ISuccessNotification,
	TSort,
	TViews,
} from './utils/types';
import ViewsContext from './views/ViewsContext';

// @ts-ignore

import getViewComponent from './views/getViewComponent';

// @ts-ignore

import {VIEWS_ACTION_TYPES, viewsReducer} from './views/viewsReducer';

const DEFAULT_PAGINATION_DELTA = 20;
const DEFAULT_PAGINATION_PAGE_NUMBER = 1;

const FrontendDataSet = ({
	actionParameterName,
	activeViewSettings,
	additionalAPIURLParameters,
	apiURL,
	appURL,
	bulkActions = [],
	creationMenu: initialCreationMenu,
	currentURL,
	customDataRenderers,
	customRenderers,
	customViews = '{}',
	customViewsEnabled,
	emptyState,
	filters: initialFilters,
	formId,
	formName,
	header,
	id,
	infoPanelComponent,
	inlineAddingSettings,
	inlineEditingSettings,
	items: itemsProp,
	itemsActions,
	namespace,
	nestedItemsKey,
	nestedItemsReferenceKey,
	onActionDropdownItemClick,
	onBulkActionItemClick,
	onSelect,
	onSelectedItemsChange,
	overrideEmptyResultView,
	pagination,
	portletId,
	selectedItems: initialSelectedItemsValues,
	selectedItemsKey = 'id',
	selectionType = 'multiple',
	showBulkActionsManagementBar = true,
	showBulkActionsManagementBarActions = true,
	showManagementBar = true,
	showPagination = true,
	showSearch = true,
	showSelectAll = false,
	sidePanelId,
	sorts: sortsProp = [],
	style = 'default',
	uniformActionsDisplay,
	views,
}: IFrontendDataSetProps) => {
	const fdsRef = useRef(null);
	const wrapperRef = useRef(null);
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
	const [items, setItems] = useState(itemsProp || []);
	const [itemsChanges, setItemsChanges] = useState<{[key: string]: any}>({});
	const [pageNumber, setPageNumber] = useState(
		pagination?.initialPageNumber || DEFAULT_PAGINATION_PAGE_NUMBER
	);
	const [searchParam, setSearchParam] = useState('');

	const [allItemsSelectedActive, setAllItemsSelectedActive] = useState(false);

	const [selectedItemsValue, setSelectedItemsValue] = useState(
		initialSelectedItemsValues || []
	);
	const [selectedItems, setSelectedItems] = useState<Array<any>>([]);
	const [total, setTotal] = useState(0);

	const getInitialViewsState = () => {
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

		let initialVisibleFieldNames = {};

		if (activeViewSettings) {
			const {name: activeViewName, visibleFieldNames} =
				JSON.parse(activeViewSettings);

			if (activeViewName) {
				const activeView = views.find(
					({name}) => name === activeViewName
				);

				if (activeView) {
					initialActiveView = activeView;
				}
			}

			if (visibleFieldNames) {
				initialVisibleFieldNames = visibleFieldNames;
			}
		}

		const activeView = {
			component: getViewComponent(initialActiveView),
			...initialActiveView,
		};

		const filters = initialFilters
			? initialFilters.map((filter) => {
					const preloadedData = filter.preloadedData;

					if (preloadedData) {
						filter.active = true;
						filter.selectedData = preloadedData;

						const filterType: keyof typeof FILTER_IMPLEMENTATIONS =
							filter.type;

						const filterImplementation =
							FILTER_IMPLEMENTATIONS[filterType];

						filter.odataFilterString =
							filterImplementation.getOdataString(filter);
						filter.selectedItemsLabel =
							filterImplementation.getSelectedItemsLabel(filter);
					}

					return filter;
				})
			: [];

		const paginationDelta =
			showPagination &&
			(pagination?.initialDelta || DEFAULT_PAGINATION_DELTA);

		return {
			activeView,
			customViews: customViews && JSON.parse(customViews),
			customViewsEnabled,
			defaultView: {
				activeView,
				filters,
				paginationDelta,
				sorts: sortsProp,
				visibleFieldNames: initialVisibleFieldNames,
			},
			filters,
			modifiedFields: {},
			paginationDelta,
			sorts: sortsProp,
			views: [...views, ...customInternalViews],
			visibleFieldNames: initialVisibleFieldNames,
		};
	};

	const [viewsState, viewsDispatch] = useThunk(
		useReducer(viewsReducer, getInitialViewsState())
	);

	const {activeView, filters, paginationDelta, sorts} = viewsState;

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

		const activeFiltersOdataStrings = filters.reduce(

			// Difficult to type filter as it is a mix of filters from FDS and FILTER_IMPLEMENTATIONS<T>

			(activeFilters: Array<string>, filter: any) =>
				filter.active && filter.odataFilterString
					? [...activeFilters, filter.odataFilterString]
					: activeFilters,
			[]
		);

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
			searchParam,
			sorts: activeSorts,
		});
	}, [
		additionalAPIURLParameters,
		apiURL,
		currentURL,
		paginationDelta,
		filters,
		pageNumber,
		searchParam,
		sorts,
	]);

	const isMounted = useIsMounted();

	function updateDataSetItems(dataSetData: IDataSetData) {
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
			setPageNumber(() => dataSetData.lastPage);
		}
	}

	useEffect(() => {
		loadClientExtensions([
			{
				clientExtensionDefinitions: initialFilters
					? initialFilters
							.filter((filter) => filter.clientExtensionFilterURL)
							.map((filter) => ({
								context: filter,
								importDeclaration: `default from ${filter.clientExtensionFilterURL}`,
							}))
					: [],
				onLoad: (bindingContexts: any) => {
					const newFilters = initialFilters?.map((filter) => {
						const bindingContext = bindingContexts.find(
							(bindingContext: any) =>
								bindingContext.context
									.clientExtensionFilterURL ===
								filter.clientExtensionFilterURL
						);

						if (bindingContext) {
							return {
								...filter,
								clientExtensionFilterImplementation:
									bindingContext.binding,
							};
						}

						return filter;
					});

					viewsDispatch({
						type: VIEWS_ACTION_TYPES.UPDATE_FILTERS,
						value: newFilters,
					});
				},
			},
			{
				clientExtensionDefinitions: views.reduce(
					(clientExtensionDefinitions: Array<any>, view: TViews) => {
						if (view.schema && 'fields' in view.schema) {
							if (!view.schema.fields.length) {
								return clientExtensionDefinitions;
							}

							const clientExtensionFields =
								view.schema.fields.filter(
									(field: IField) =>
										!!field.contentRendererClientExtension
								);

							for (const field of clientExtensionFields) {
								clientExtensionDefinitions.push({
									context: field,
									importDeclaration:
										field.contentRendererModuleURL,
								});
							}

							return clientExtensionDefinitions;
						}
						else {
							return [];
						}
					},
					[]
				),
				onLoad: (bindingContexts: any) => {
					bindingContexts.forEach(
						({
							binding: htmlElementBuilder,
							context: field,
						}: {
							binding: IHTMLElementBuilder<unknown>;
							context: IField;
						}) => {
							viewsDispatch({
								type: VIEWS_ACTION_TYPES.UPDATE_FIELD,
								value: {
									htmlElementBuilder,
									name: field.fieldName,
								},
							});
						}
					);
				},
			},
		]);
	}, [initialFilters, views, viewsDispatch]);

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
	}, [itemsProp]);

	function deselectItems(value: any) {
		if (Array.isArray(value)) {
			return setSelectedItemsValue(
				selectedItemsValue.filter((item) => !value.includes(item))
			);
		}

		setSelectedItemsValue(
			selectedItemsValue.filter((item) => item !== value)
		);
	}

	function selectItems(value: any) {
		if (selectionType === 'single') {
			return setSelectedItemsValue(
				Array.isArray(value) ? value : [value]
			);
		}

		if (Array.isArray(value)) {
			const newItems = value.filter(
				(item) => !selectedItemsValue.includes(item)
			);

			return setSelectedItemsValue([...selectedItemsValue, ...newItems]);
		}

		if (selectedItemsValue.includes(value)) {
			setSelectedItemsValue(
				selectedItemsValue.filter((item) => item !== value)
			);
		}
		else {
			setSelectedItemsValue([...selectedItemsValue, value]);
		}
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

	useEffect(() => {
		if (wrapperRef.current) {
			const form = (wrapperRef.current as HTMLElement).closest('form');

			if (form?.dataset.sennaOff === null) {
				form.setAttribute('data-senna-off', 'true');
			}
		}
	}, [wrapperRef]);

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

						const itemKeys = new Set(
							data.items.map(
								(item: any) => item[selectedItemsKey]
							)
						);

						setSelectedItemsValue(
							selectedItemsValue.filter((item) =>
								itemKeys.has(item)
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
		[id, isMounted, requestData, selectedItemsKey, selectedItemsValue]
	);

	useEffect(() => {
		setSelectedItems((selectedItems: Array<any>) => {
			const newSelectedItems: Array<any> = [];

			selectedItemsValue.forEach((value) => {
				let selectedItem = items.find(
					(item) => item[selectedItemsKey] === value
				);

				if (!selectedItem) {
					selectedItem = selectedItems.find(
						(item) => item[selectedItemsKey] === value
					);
				}

				if (selectedItem) {
					newSelectedItems.push(selectedItem);
				}
			});

			onSelectedItemsChange && onSelectedItemsChange(newSelectedItems);

			return newSelectedItems;
		});
	}, [selectedItemsValue, items, onSelectedItemsChange, selectedItemsKey]);

	useEffect(() => {
		if (View || !contentRendererModuleURL) {
			return;
		}

		setComponentLoading(true);

		loadModule(contentRendererModuleURL)
			.then((view: TViews) => {
				if (isMounted()) {
					viewsDispatch({
						type: VIEWS_ACTION_TYPES.UPDATE_VIEW_COMPONENT,
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
		if (!apiURL) {
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
			}
		});
	}, [apiURL, isMounted, requestData, setDataLoading]);

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

		return () => {
			Liferay.detach(EVENTS.SIDE_PANEL_CLOSED, handleCloseSidePanel);
			Liferay.detach(
				EVENTS.UPDATE_DISPLAY,
				handleRefreshFromTheOutside as () => void
			);
		};
	}, [id, refreshData]);

	const managementBar = showManagementBar ? (
		<div className="management-bar-wrapper">
			<ManagementBar
				bulkActions={bulkActions}
				creationMenu={creationMenu}
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
				onSelectAll={(value: boolean) =>
					setAllItemsSelectedActive(value)
				}
				selectItems={(items: Array<any>) => selectItems(items)}
				selectedItems={selectedItems}
				selectedItemsKey={selectedItemsKey}
				selectedItemsValue={selectedItemsValue}
				selectionType={selectionType}
				showSearch={showSearch}
				showSelectAll={showSelectAll}
				total={total}
			/>
		</div>
	) : null;

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
						onItemSelectionChange={(selectedItem: any) => {
							if (allItemsSelectedActive) {
								setSelectedItemsValue(
									items
										.filter(
											(item) =>
												item[selectedItemsKey] !==
												selectedItem[selectedItemsKey]
										)
										.map((item) => item[selectedItemsKey])
								);

								setAllItemsSelectedActive(false);
							}
							else {
								selectItems(selectedItem[selectedItemsKey]);
							}
						}}
						style={style}
						{...currentViewProps}
					/>
				) : (
					<ClayEmptyState
						description={
							emptyState?.description ??
							Liferay.Language.get('sorry,-no-results-were-found')
						}
						imgSrc={
							Liferay.ThemeDisplay.getPathThemeImages() +
							(emptyState?.image ?? '/states/search_state.svg')
						}
						title={
							emptyState?.title ??
							Liferay.Language.get('no-results-found')
						}
					>
						{creationMenu && (
							<CreationMenu {...creationMenu} inEmptyState />
						)}
					</ClayEmptyState>
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
					onActiveChange={setPageNumber}
					onDeltaChange={(delta) => {
						setPageNumber(1);

						viewsDispatch({
							type: VIEWS_ACTION_TYPES.UPDATE_PAGINATION_DELTA,
							value: delta,
						});
					}}
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
		errorMessage: string;
		method: string;
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
			(item) => item[selectedItemsKey] === itemKey
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

	const onSearch = ({query}: {query: string}) => {
		if (apiURL || appURL) {
			setSearchParam(query);
		}
		else {
			setItems(
				itemsProp?.length
					? itemsProp.filter((item) => {
							return JSON.stringify(Object.values(item)).includes(
								query
							);
						})
					: []
			);
		}
	};

	const selectable = Boolean(
		selectedItemsKey && (bulkActions?.length || selectionType === 'single')
	);

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
				formId,
				formName,
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
				onInfoPanelToggleButtonClick: () => {
					setInfoPanelOpen((value) => !value);
				},
				onItemsChange,
				onSearch,
				onSelect,
				openModal,
				openSidePanel,
				portletId,
				searchParam,
				selectItems,
				selectable,
				selectedItems,
				selectedItemsKey,
				selectedItemsValue,
				selectionType,
				showBulkActionsManagementBar,
				showBulkActionsManagementBarActions,
				showInfoPanel:
					infoPanelComponent && Liferay.FeatureFlags['LPD-41774']
						? true
						: false,
				sidePanelId: dataSetSupportSidePanelIdRef.current,
				sorts,
				style,
				toggleItemInlineEdit,
				uniformActionsDisplay,
				updateDataSetItems,
				updateItem,
			}}
		>
			<ViewsContext.Provider value={[viewsState, viewsDispatch]}>
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

					<div
						className={classNames(
							`data-set-wrapper visualization-mode-${activeView.contentRenderer}`,
							{
								selectable,
							}
						)}
						data-testid={`visualization-mode-${activeView.name}`}
						ref={wrapperRef}
					>
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

						{style === 'default' && (
							<div className="data-set data-set-inline">
								{managementBar}

								{view}

								{paginationComponent}
							</div>
						)}

						{style === 'stacked' && (
							<div className="data-set data-set-stacked">
								{managementBar}

								{view}

								{paginationComponent}
							</div>
						)}

						{style === 'fluid' && (
							<div className="data-set data-set-fluid">
								{managementBar}

								<div className="container-fluid mt-3">
									{view}

									{paginationComponent}
								</div>
							</div>
						)}
					</div>
				</div>
			</ViewsContext.Provider>
		</FrontendDataSetContext.Provider>
	);
};

export default FrontendDataSet;
