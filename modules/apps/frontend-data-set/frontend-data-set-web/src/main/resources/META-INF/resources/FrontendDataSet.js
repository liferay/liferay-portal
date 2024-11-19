/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import {useIsMounted, useThunk} from '@liferay/frontend-js-react-web';
import {
	fetch,
	loadClientExtensions,
	loadModule,
	openToast,
} from 'frontend-js-web';
import React, {
	useCallback,
	useEffect,
	useReducer,
	useRef,
	useState,
} from 'react';

import './styles/main.scss';

import ClayEmptyState from '@clayui/empty-state';

import FrontendDataSetContext from './FrontendDataSetContext';
import ManagementBar from './management_bar/ManagementBar';
import CreationMenu from './management_bar/controls/CreationMenu';
import {FILTER_IMPLEMENTATIONS} from './management_bar/controls/filters/Filter';
import Modal from './modal/Modal';
import SidePanel from './side_panel/SidePanel';
import filterCreationActions from './utils/actionItems/filterCreationActions';
import EVENTS from './utils/eventsDefinitions';
import getRandomId from './utils/getRandomId';
import {
	formatItemChanges,
	getCurrentItemUpdates,
	loadData,
} from './utils/index';
import {logError} from './utils/logError';
import ViewsContext from './views/ViewsContext';
import getViewComponent from './views/getViewComponent';
import {VIEWS_ACTION_TYPES, viewsReducer} from './views/viewsReducer';

const DEFAULT_PAGINATION_DELTA = 20;
const DEFAULT_PAGINATION_PAGE_NUMBER = 1;

const FrontendDataSet = ({
	actionParameterName,
	activeViewSettings,
	additionalAPIURLParameters,
	apiURL,
	appURL,
	bulkActions,
	creationMenu: initialCreationMenu,
	currentURL,
	customDataRenderers,
	customRenderers,
	customViews,
	customViewsEnabled,
	emptyState,
	filters: initialFilters,
	formId,
	formName,
	header,
	id,
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
	selectedItemsKey,
	selectionType,
	showBulkActionsManagementBar,
	showBulkActionsManagementBarActions,
	showManagementBar,
	showPagination,
	showSearch,
	sidePanelId,
	sorts: sortsProp,
	style,
	uniformActionsDisplay,
	views,
}) => {
	const wrapperRef = useRef(null);
	const [componentLoading, setComponentLoading] = useState(false);
	const [creationMenu, setCreationMenu] = useState(initialCreationMenu);
	const [dataLoading, setDataLoading] = useState(!!apiURL);
	const [dataSetSupportModalId] = useState(`support-modal-${getRandomId()}`);
	const [dataSetSupportSidePanelId] = useState(
		sidePanelId || `support-side-panel-${getRandomId()}`
	);

	const [highlightedItemsValue, setHighlightedItemsValue] = useState([]);
	const [items, setItems] = useState(itemsProp || []);
	const [itemsChanges, setItemsChanges] = useState({});
	const [pageNumber, setPageNumber] = useState(
		showPagination &&
			(pagination?.initialPageNumber || DEFAULT_PAGINATION_PAGE_NUMBER)
	);
	const [searchParam, setSearchParam] = useState('');
	const [selectedItemsValue, setSelectedItemsValue] = useState(
		initialSelectedItemsValues || []
	);
	const [selectedItems, setSelectedItems] = useState([]);
	const [total, setTotal] = useState(0);

	const getInitialViewsState = () => {
		const customInternalViews =
			customRenderers?.view?.map((customRenderer) => ({
				component: customRenderer.component,
				default: customRenderer.default,
				label: customRenderer.label,
				name: customRenderer.name,
				thumbnail: customRenderer.symbol,
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

						const filterImplementation =
							FILTER_IMPLEMENTATIONS[filter.type];

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
			customViews: JSON.parse(customViews),
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
		const activeFiltersOdataStrings = filters.reduce(
			(activeFilters, filter) =>
				filter.active && filter.odataFilterString
					? [...activeFilters, filter.odataFilterString]
					: activeFilters,
			[]
		);

		const activeSorts =
			sorts.length > 1 ? sorts.filter((sort) => sort.active) : sorts;

		return loadData(
			apiURL,
			currentURL,
			activeFiltersOdataStrings,
			searchParam,
			paginationDelta,
			pageNumber,
			activeSorts,
			additionalAPIURLParameters
		);
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

	function updateDataSetItems(dataSetData) {
		setItems(dataSetData.items);
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
				onLoad: (bindingContexts) => {
					const newFilters = initialFilters.map((filter) => {
						const bindingContext = bindingContexts.find(
							(bindingContext) =>
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
					(clientExtensionDefinitions, view) => {
						if (!view.schema?.fields?.length) {
							return clientExtensionDefinitions;
						}

						const clientExtensionFields = view.schema.fields.filter(
							(field) => !!field.contentRendererClientExtension
						);

						for (const field of clientExtensionFields) {
							clientExtensionDefinitions.push({
								context: field,
								importDeclaration:
									field.contentRendererModuleURL,
							});
						}

						return clientExtensionDefinitions;
					},
					[]
				),
				onLoad: (bindingContexts) => {
					bindingContexts.forEach(
						({binding: htmlElementBuilder, context: field}) => {
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
			updateDataSetItems({items: itemsProp});
		}
	}, [itemsProp]);

	function selectItems(value) {
		if (Array.isArray(value)) {
			return setSelectedItemsValue(value);
		}

		if (selectionType === 'single') {
			return setSelectedItemsValue([value]);
		}

		const itemAdded = selectedItemsValue.find((item) => item === value);

		if (itemAdded) {
			setSelectedItemsValue(
				selectedItemsValue.filter((element) => element !== value)
			);
		}
		else {
			setSelectedItemsValue(selectedItemsValue.concat(value));
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
			const form = wrapperRef.current.closest('form');

			if (form?.dataset.sennaOff === null) {
				form.setAttribute('data-senna-off', true);
			}
		}
	}, [wrapperRef]);

	function refreshData(successNotification) {
		setDataLoading(true);

		return requestData()
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
						data.items.map((item) => item[selectedItemsKey])
					);

					setSelectedItemsValue(
						selectedItemsValue.filter((item) => itemKeys.has(item))
					);

					setDataLoading(false);

					Liferay.fire(EVENTS.DISPLAY_UPDATED, {id});
				}

				return data;
			})
			.catch((error) => {
				logError(error);
				setDataLoading(false);

				throw error;
			});
	}

	useEffect(() => {
		setSelectedItems((selectedItems) => {
			const newSelectedItems = [];

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

			onSelectedItemsChange(newSelectedItems);

			return newSelectedItems;
		});
	}, [selectedItemsValue, items, onSelectedItemsChange, selectedItemsKey]);

	useEffect(() => {
		if (View || !contentRendererModuleURL) {
			return;
		}

		setComponentLoading(true);

		loadModule(contentRendererModuleURL)
			.then((component) => {
				if (isMounted()) {
					viewsDispatch({
						type: VIEWS_ACTION_TYPES.UPDATE_VIEW_COMPONENT,
						value: {component, name: activeViewName},
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

	const handleApiError = ({data, statusCode}) => {
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

		requestData().then(({data, ok, status: statusCode}) => {
			if (isMounted()) {
				if (!ok) {
					handleApiError({data, statusCode});
				}
				else {
					setCreationMenu((currentCreationMenu) => {
						if (!currentCreationMenu) {
							return;
						}

						const filteredCreationMenu = {};

						filteredCreationMenu.primaryItems =
							filterCreationActions({
								customActions:
									currentCreationMenu?.primaryItems,
								globalCollectionActions: data?.actions,
							});

						return filteredCreationMenu;
					});

					updateDataSetItems(data);
				}
				setDataLoading(false);
			}
		});
	}, [apiURL, isMounted, requestData, setDataLoading]);

	useEffect(() => {
		function handleRefreshFromTheOutside(event) {
			if (event.id === id) {
				refreshData();
			}
		}

		function handleCloseSidePanel() {
			setHighlightedItemsValue([]);
		}

		if (
			(nestedItemsReferenceKey && !nestedItemsKey) ||
			(!nestedItemsReferenceKey && nestedItemsKey)
		) {
			logError(
				'"nestedItemsKey" and "nestedItemsReferenceKey" params are both mandatory to manage nested items'
			);
		}

		Liferay.on(EVENTS.SIDE_PANEL_CLOSED, handleCloseSidePanel);
		Liferay.on(EVENTS.UPDATE_DISPLAY, handleRefreshFromTheOutside);

		return () => {
			Liferay.detach(EVENTS.SIDE_PANEL_CLOSED, handleCloseSidePanel);
			Liferay.detach(EVENTS.UPDATE_DISPLAY, handleRefreshFromTheOutside);
		};

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [id]);

	const managementBar = showManagementBar ? (
		<div className="management-bar-wrapper">
			<ManagementBar
				bulkActions={bulkActions}
				creationMenu={creationMenu}
				fluid={style === 'fluid'}
				selectAllItems={() =>
					selectItems(items.map((item) => item[selectedItemsKey]))
				}
				selectedItems={selectedItems}
				selectedItemsKey={selectedItemsKey}
				selectedItemsValue={selectedItemsValue}
				selectionType={selectionType}
				showSearch={showSearch}
				sidePanelId={dataSetSupportSidePanelId}
				total={items?.length ?? 0}
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
							themeDisplay.getPathThemeImages() +
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
					activeDelta={paginationDelta}
					activePage={pageNumber}
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
					onDeltaChange={(delta) => {
						setPageNumber(1);

						viewsDispatch({
							type: VIEWS_ACTION_TYPES.UPDATE_PAGINATION_DELTA,
							value: delta,
						});
					}}
					onPageChange={setPageNumber}
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
	}) {
		const requestOptions = {
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

	function openSidePanel(config) {
		return Liferay.fire(EVENTS.OPEN_SIDE_PANEL, {
			id: dataSetSupportSidePanelId,
			onSubmit: refreshData,
			...config,
		});
	}

	function openModal(config) {
		return Liferay.fire(EVENTS.OPEN_MODAL, {
			id: dataSetSupportModalId,
			onSubmit: refreshData,
			...config,
		});
	}

	function updateItem(itemKey, property, valuePath, value = null) {
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

	function toggleItemInlineEdit(itemKey) {
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

	function applyItemInlineUpdates(itemKey) {
		const itemToBeUpdated = items.find(
			(item) => item[selectedItemsKey] === itemKey
		);

		const defaultBody = inlineEditingSettings.defaultBodyContent || {};

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

	return (
		<FrontendDataSetContext.Provider
			value={{
				actionParameterName,
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
				inlineAddingSettings,
				inlineEditingSettings,
				itemsActions,
				itemsChanges,
				loadData: refreshData,
				modalId: dataSetSupportModalId,
				namespace,
				nestedItemsKey,
				nestedItemsReferenceKey,
				onActionDropdownItemClick,
				onBulkActionItemClick,
				onSelect,
				openModal,
				openSidePanel,
				portletId,
				searchParam,
				selectItems,
				selectable: Boolean(
					selectedItemsKey &&
						(bulkActions?.length || selectionType === 'single')
				),
				selectedItemsKey,
				selectedItemsValue,
				selectionType,
				showBulkActionsManagementBar,
				showBulkActionsManagementBarActions,
				sidePanelId: dataSetSupportSidePanelId,
				sorts,
				style,
				toggleItemInlineEdit,
				uniformActionsDisplay,
				updateDataSetItems,
				updateItem,
				updateSearchParam: setSearchParam,
			}}
		>
			<ViewsContext.Provider value={[viewsState, viewsDispatch]}>
				<div className="fds">
					<Modal id={dataSetSupportModalId} onClose={refreshData} />

					{!sidePanelId && (
						<SidePanel
							id={dataSetSupportSidePanelId}
							onAfterSubmit={refreshData}
						/>
					)}

					<div
						className="data-set-wrapper"
						data-testid={`visualization-mode-${activeView.name}`}
						ref={wrapperRef}
					>
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

								<div className="container-fluid container-xl mt-3">
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

FrontendDataSet.defaultProps = {
	bulkActions: [],
	customViews: '{}',
	inlineEditingSettings: null,
	items: null,
	itemsActions: null,
	onSelectedItemsChange: () => {},
	selectedItemsKey: 'id',
	selectionType: 'multiple',
	showBulkActionsManagementBar: true,
	showBulkActionsManagementBarActions: true,
	showManagementBar: true,
	showPagination: true,
	showSearch: true,
	sorts: [],
	style: 'default',
};

export default FrontendDataSet;
