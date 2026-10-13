/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayEmptyState from '@clayui/empty-state';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import ClayTable from '@clayui/table';
import {ManagementToolbar} from 'frontend-js-components-web';
import {createPortletURL, fetch} from 'frontend-js-web';
import React, {useCallback, useEffect, useState} from 'react';

const PublicationsSearchContainer = ({
	ascending,
	column,
	containerView,
	fetchDataURL,
	filterEntries,
	getListItem,
	getTableHead,
	getTableRow,
	namespace,
	orderByItems,
	preferencesPrefix,
	saveDisplayPreferenceURL,
	setAscending,
	setColumn,
	spritemap,
}) => {
	const VIEW_TYPE_LIST = 'list';
	const VIEW_TYPE_TABLE = 'table';

	const [loading, setLoading] = useState(false);
	const [resultsKeywords, setResultsKeywords] = useState('');
	const [searchTerms, setSearchTerms] = useState('');
	const [state, setState] = useState({
		delta: 20,
		page: 1,
	});

	const [viewType, setViewType] = useState(
		getTableRow ? VIEW_TYPE_TABLE : VIEW_TYPE_LIST
	);

	const [initialized, setInitialized] = useState(false);

	useEffect(() => {
		if (initialized) {
			return;
		}

		setInitialized(true);
		setLoading(true);

		fetch(fetchDataURL)
			.then((response) => response.json())
			.then((json) => {
				if (!json.entries) {
					const fetchData = {
						errorMessage: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
					};

					setState((prevState) => ({
						...prevState,
						fetchData,
					}));

					setLoading(false);

					return;
				}

				const newState = {
					delta: state.delta,
					fetchData: json,
					page: state.page,
				};

				const lastPage = Math.ceil(
					json.entries.length / newState.delta
				);

				if (lastPage < 1) {
					newState.page = 1;
				}
				else if (newState.page > lastPage) {
					newState.page = lastPage;
				}

				setState(newState);

				setLoading(false);
			})
			.catch(() => {
				const fetchData = {
					errorMessage: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
				};

				setState((prevState) => ({
					...prevState,
					fetchData,
				}));

				setLoading(false);
			});
	}, [fetchDataURL, initialized, state]);

	const [showMobile, setShowMobile] = useState(false);

	const saveDisplayPreference = useCallback(
		(key, value) => {
			if (!preferencesPrefix || !saveDisplayPreferenceURL) {
				return;
			}

			const portletURL = createPortletURL(saveDisplayPreferenceURL, {
				key: preferencesPrefix + '-' + key,
				value,
			});

			fetch(portletURL);
		},
		[preferencesPrefix, saveDisplayPreferenceURL]
	);

	useEffect(() => {
		saveDisplayPreference('order-by-ascending', ascending);
	}, [ascending, saveDisplayPreference]);

	useEffect(() => {
		saveDisplayPreference('order-by-column', column);
	}, [column, saveDisplayPreference]);

	useEffect(() => {
		saveDisplayPreference('view-type', viewType);
	}, [viewType, saveDisplayPreference]);

	const onSubmit = useCallback(
		(keywords, newDelta, newPage) => {
			setResultsKeywords(keywords);
			const portletURL = createPortletURL(fetchDataURL);

			if (keywords) {
				portletURL.searchParams.append(
					`${namespace}keywords`,
					keywords
				);
			}
			else {
				portletURL.searchParams.append(`${namespace}keywords`, '');
			}

			setLoading(true);

			fetch(portletURL)
				.then((response) => response.json())
				.then((json) => {
					if (!json.entries) {
						const fetchData = {
							errorMessage: Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
						};

						setState((prevState) => ({
							...prevState,
							fetchData,
						}));

						setLoading(false);

						return;
					}

					const newState = {
						delta: newDelta,
						fetchData: json,
						page: newPage,
					};

					const lastPage = Math.ceil(
						json.entries.length / newState.delta
					);

					if (lastPage < 1) {
						newState.page = 1;
					}
					else if (newState.page > lastPage) {
						newState.page = lastPage;
					}

					setState(newState);

					setLoading(false);
				})
				.catch(() => {
					const fetchData = {
						errorMessage: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
					};

					setState((prevState) => ({
						...prevState,
						fetchData,
					}));

					setLoading(false);
				});
		},
		[fetchDataURL, namespace]
	);

	const format = (key, args) => {
		const SPLIT_REGEX = /({\d+})/g;

		const keyArray = key
			.split(SPLIT_REGEX)
			.filter((val) => val.length !== 0);

		for (let i = 0; i < args.length; i++) {
			const arg = args[i];

			const indexKey = `{${i}}`;

			let argIndex = keyArray.indexOf(indexKey);

			while (argIndex >= 0) {
				keyArray.splice(argIndex, 1, arg);

				argIndex = keyArray.indexOf(indexKey);
			}
		}

		return keyArray.join('');
	};

	const onDeltaChange = (value) => {
		const newState = {
			delta: value,
			page: 1,
		};

		if (state.fetchData) {
			newState.fetchData = state.fetchData;
		}

		setState(newState);
	};

	const onPageChange = (value) => {
		const newState = {
			delta: state.delta,
			page: value,
		};

		if (state.fetchData) {
			newState.fetchData = state.fetchData;
		}

		setState(newState);
	};

	const renderManagementToolbar = () => {
		const filterDisabled =
			!state.fetchData ||
			!state.fetchData.entries ||
			!state.fetchData.entries.length;

		const searchDisabled =
			!resultsKeywords &&
			state.fetchData &&
			state.fetchData.entries &&
			!state.fetchData.entries.length;

		const items = orderByItems
			.map((orderByItem) => ({
				active: column === orderByItem.value,
				label: orderByItem.label,
				onClick: () => setColumn(orderByItem.value),
			}))
			.sort((a, b) => (a.label < b.label ? -1 : 1));

		const viewTypeItems = [];

		if (getListItem) {
			viewTypeItems.push({
				active: viewType === VIEW_TYPE_LIST,
				label: Liferay.Language.get('list[noun]'),
				onClick: () => setViewType(VIEW_TYPE_LIST),
				symbolLeft: 'list',
			});
		}

		if (getTableRow) {
			viewTypeItems.push({
				active: viewType === VIEW_TYPE_TABLE,
				label: Liferay.Language.get('table'),
				onClick: () => setViewType(VIEW_TYPE_TABLE),
				symbolLeft: 'table',
			});
		}

		const activeViewTypeItem = viewTypeItems.find((type) => type.active);

		return (
			<ManagementToolbar.Container>
				<ManagementToolbar.ItemList>
					<ManagementToolbar.Item>
						<ClayDropDownWithItems
							items={[
								{
									items,
									label: Liferay.Language.get('order-by'),
									type: 'group',
								},
							]}
							spritemap={spritemap}
							trigger={
								<ClayButton
									className="nav-link"
									disabled={filterDisabled}
									displayType="unstyled"
								>
									<span className="navbar-breakpoint-down-d-none">
										<span className="navbar-text-truncate">
											{Liferay.Language.get(
												'filter-and-order'
											)}
										</span>

										<ClayIcon
											className="inline-item inline-item-after"
											spritemap={spritemap}
											symbol="caret-bottom"
										/>
									</span>

									<span className="navbar-breakpoint-d-none">
										<ClayIcon
											spritemap={spritemap}
											symbol="filter"
										/>
									</span>
								</ClayButton>
							}
						/>
					</ManagementToolbar.Item>

					<ManagementToolbar.Item
						data-tooltip-align="top"
						title={Liferay.Language.get('reverse-sort-direction')}
					>
						<ClayButton
							className="nav-link nav-link-monospaced"
							disabled={filterDisabled}
							displayType="unstyled"
							onClick={() => setAscending(!ascending)}
						>
							<ClayIcon
								spritemap={spritemap}
								symbol={
									ascending
										? 'order-list-down'
										: 'order-list-up'
								}
							/>
						</ClayButton>
					</ManagementToolbar.Item>
				</ManagementToolbar.ItemList>

				<ManagementToolbar.Search
					onSubmit={(event) => {
						event.preventDefault();

						onSubmit(searchTerms.trim(), state.delta, 1);
					}}
					showMobile={showMobile}
				>
					<ClayInput.Group>
						<ClayInput.GroupItem>
							<ClayInput
								aria-label={Liferay.Language.get('search')}
								className="input-group-inset input-group-inset-after"
								disabled={searchDisabled}
								onChange={(event) =>
									setSearchTerms(event.target.value)
								}
								placeholder={`${Liferay.Language.get(
									'search'
								)}...`}
								type="text"
								value={searchTerms}
							/>

							<ClayInput.GroupInsetItem after tag="span">
								<ClayButtonWithIcon
									className="navbar-breakpoint-d-none"
									displayType="unstyled"
									onClick={() => setShowMobile(false)}
									symbol="times"
								/>

								<ClayButtonWithIcon
									disabled={searchDisabled}
									displayType="unstyled"
									symbol="search"
									type="submit"
								/>
							</ClayInput.GroupInsetItem>
						</ClayInput.GroupItem>
					</ClayInput.Group>
				</ManagementToolbar.Search>

				<ManagementToolbar.ItemList>
					<ManagementToolbar.Item className="navbar-breakpoint-d-none">
						<ClayButton
							className="nav-link nav-link-monospaced"
							disabled={searchDisabled}
							displayType="unstyled"
							onClick={() => setShowMobile(true)}
						>
							<ClayIcon symbol="search" />
						</ClayButton>
					</ManagementToolbar.Item>
				</ManagementToolbar.ItemList>

				{viewTypeItems.length > 1 && (
					<ManagementToolbar.ItemList>
						<ManagementToolbar.Item
							data-tooltip-align="top"
							title={Liferay.Language.get('display-style')}
						>
							<ClayDropDownWithItems
								items={viewTypeItems}
								spritemap={spritemap}
								trigger={
									<ClayButton
										className="nav-link nav-link-monospaced"
										displayType="unstyled"
									>
										<ClayIcon
											spritemap={spritemap}
											symbol={
												activeViewTypeItem
													? activeViewTypeItem.symbolLeft
													: ''
											}
										/>
									</ClayButton>
								}
							/>
						</ManagementToolbar.Item>
					</ManagementToolbar.ItemList>
				)}
			</ManagementToolbar.Container>
		);
	};

	const renderPagination = () => {
		if (state.fetchData.entries.length <= 5) {
			return '';
		}

		return (
			<ClayPaginationBarWithBasicItems
				activeDelta={state.delta}
				activePage={state.page}
				deltas={[4, 8, 20, 40, 60].map((size) => ({
					label: size,
				}))}
				ellipsisBuffer={3}
				onDeltaChange={(value) => onDeltaChange(value)}
				onPageChange={(value) => onPageChange(value)}
				totalItems={state.fetchData.entries.length}
			/>
		);
	};

	const renderResultsBar = () => {
		if (!resultsKeywords) {
			return '';
		}

		let count = 0;
		let key = Liferay.Language.get('x-results-for');

		if (state.fetchData && state.fetchData.entries) {
			count = state.fetchData.entries.length;

			if (count === 1) {
				key = Liferay.Language.get('x-result-for');
			}
		}

		return (
			<div className="results-bar">
				<ManagementToolbar.ResultsBar>
					<ManagementToolbar.ResultsBarItem expand>
						<span className="component-text text-truncate-inline">
							<span className="text-truncate">
								{format(key, [count]) + ' '}

								<strong>{resultsKeywords}</strong>
							</span>
						</span>
					</ManagementToolbar.ResultsBarItem>

					<ManagementToolbar.ResultsBarItem>
						<ClayButton
							className="component-link tbar-link"
							displayType="unstyled"
							onClick={() => {
								onSubmit('', state.delta, 1);
								setSearchTerms('');
							}}
						>
							{Liferay.Language.get('clear')}
						</ClayButton>
					</ManagementToolbar.ResultsBarItem>
				</ManagementToolbar.ResultsBar>
			</div>
		);
	};

	const renderBody = () => {
		if (!state.fetchData) {
			return <span aria-hidden="true" className="loading-animation" />;
		}
		else if (state.fetchData.errorMessage) {
			return (
				<ClayAlert
					displayType="danger"
					spritemap={spritemap}
					title={Liferay.Language.get('error')}
				>
					{state.fetchData.errorMessage}
				</ClayAlert>
			);
		}
		else if (!state.fetchData.entries.length) {
			let className = '';

			if (containerView) {
				className += ' sheet';
			}

			if (loading) {
				className += ' publications-loading';
			}

			const symbol = resultsKeywords ? 'search-state' : 'empty-state';

			return (
				<div className={containerView ? 'container-view' : ''}>
					<div className={className}>
						<ClayEmptyState
							description={Liferay.Language.get(
								'no-publications-were-found'
							)}
							image={
								<ClayIcon
									className={`c-empty-state-icon c-empty-state-icon-${symbol}`}
									illustration
									symbol={symbol}
								/>
							}
							title={Liferay.Language.get('no-results-found')}
						/>
					</div>
				</div>
			);
		}

		const entries = filterEntries(
			ascending,
			column,
			state.delta,
			state.fetchData.entries,
			state.page
		);

		if (viewType === VIEW_TYPE_LIST) {
			const items = entries.map((entry) =>
				getListItem(entry, state.fetchData)
			);

			return (
				<div className={containerView ? 'container-view' : ''}>
					<ClayList
						className={
							loading
								? 'publications-loading publications-table'
								: 'publications-table'
						}
					>
						{items}
					</ClayList>

					{renderPagination()}
				</div>
			);
		}
		else if (viewType === VIEW_TYPE_TABLE) {
			const rows = entries.map((entry) =>
				getTableRow(entry, state.fetchData)
			);

			return (
				<div className={containerView ? 'container-view' : ''}>
					<ClayTable
						className={
							loading
								? 'publications-loading publications-table'
								: 'publications-table'
						}
						headingNoWrap
						hover={false}
					>
						{getTableHead ? getTableHead() : ''}

						<ClayTable.Body>{rows}</ClayTable.Body>
					</ClayTable>

					{renderPagination()}
				</div>
			);
		}

		return '';
	};

	return (
		<>
			{renderManagementToolbar()}
			{renderResultsBar()}
			{renderBody()}
		</>
	);
};

export default PublicationsSearchContainer;
