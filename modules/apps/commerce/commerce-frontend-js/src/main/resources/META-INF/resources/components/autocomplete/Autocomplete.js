/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAutocomplete from '@clayui/autocomplete';
import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import {FocusScope} from '@clayui/shared';
import {ReactPortal, useIsMounted} from '@liferay/frontend-js-react-web';
import PropTypes from 'prop-types';
import React, {useEffect, useMemo, useRef, useState} from 'react';

import {debouncePromise} from '../../utilities/debounce';
import {AUTOCOMPLETE_VALUE_UPDATED} from '../../utilities/eventsDefinitions';
import {useLiferayModule} from '../../utilities/hooks';
import {
	formatAutocompleteItem,
	getData,
	getLabelFromItem,
	getValueFromItem,
} from '../../utilities/index';
import {showErrorNotification} from '../../utilities/notifications';
import InfiniteScroller from '../infinite_scroller/InfiniteScroller';

function Autocomplete({onChange, onItemsUpdated, onValueUpdated, ...props}) {
	const [active, setActive] = useState(false);
	const [initialised, setInitialised] = useState(
		Boolean(props.customViewModuleUrl || props.customView)
	);
	const [items, setItems] = useState(null);
	const [lastPage, setLastPage] = useState(null);
	const [loading, setLoading] = useState(false);
	const [page, setPage] = useState(1);
	const [pageSize, setPageSize] = useState(props.pageSize);
	const [query, setQuery] = useState(props.initialLabel || '');
	const [selectedItem, setSelectedItem] = useState(
		formatAutocompleteItem(
			props.initialValue,
			props.itemsKey,
			props.initialLabel,
			props.itemsLabel
		)
	);
	const [totalCount, setTotalCount] = useState(null);
	const firstLoadRef = useRef(true);
	const nodeRef = useRef();
	const dropdownNodeRef = useRef();
	const inputNodeRef = useRef();
	const FetchedCustomView = useLiferayModule(props.customViewModuleUrl);
	const isMounted = useIsMounted();

	const debouncedGetItems = useMemo(
		() => debouncePromise(getData, props.fetchDataDebounce),
		[props.fetchDataDebounce]
	);

	useEffect(() => {
		if (items && items.length === 1 && props.autofill) {
			const firstItem = items[0];
			setSelectedItem(firstItem);
		}
	}, [items, props.autofill, props.itemsKey, props.itemsLabel]);

	useEffect(() => {
		setSelectedItem(
			formatAutocompleteItem(
				props.initialValue,
				props.itemsKey,
				props.initialLabel,
				props.itemsLabel
			)
		);

		setInitialised(Boolean(props.customViewModuleUrl || props.customView));
	}, [
		props.customView,
		props.customViewModuleUrl,
		props.initialLabel,
		props.initialValue,
		props.itemsKey,
		props.itemsLabel,
	]);

	useEffect(() => {
		setQuery(props.initialLabel);
	}, [props.initialLabel]);

	useEffect(() => {
		if (!initialised) {
			return;
		}

		const currentValue = selectedItem
			? getValueFromItem(selectedItem, props.itemsKey)
			: null;

		if (props.id) {
			Liferay.fire(AUTOCOMPLETE_VALUE_UPDATED, {
				currentValue,
				id: props.id,
				itemData: selectedItem,
			});
		}

		if (onValueUpdated) {
			onValueUpdated(currentValue, selectedItem);
		}

		if (onChange) {
			onChange({target: {value: currentValue}});
		}
	}, [
		initialised,
		selectedItem,
		props.id,
		onValueUpdated,
		onChange,
		props.itemsKey,
	]);

	useEffect(() => {
		if (query) {
			setInitialised(true);
		}

		if (props.infiniteScrollMode) {
			setItems(null);
		}

		setPage(1);
		setTotalCount(null);
		setLastPage(null);
	}, [props.infiniteScrollMode, query]);

	useEffect(() => {
		if (props.disabled && props.infiniteScrollMode) {
			setPage(1);
		}
	}, [props.disabled, props.infiniteScrollMode]);

	useEffect(() => {
		if (!props.autoload && (!query || query.trim().length <= 0)) {
			return;
		}

		if (initialised && debouncedGetItems && !props.disabled) {
			setLoading(true);

			debouncedGetItems(props.apiUrl, query, page, pageSize)
				.then((jsonResponse) => {
					if (Array.isArray(jsonResponse)) {
						const newJSONResponse = {
							items: [],
							lastPage: 1,
							page: 1,
							pageSize: 1,
							totalCount: 0,
						};
						jsonResponse.forEach((response) => {
							newJSONResponse.items = [
								...newJSONResponse.items,
								...response.items,
							];
							newJSONResponse.lastPage = Math.max(
								newJSONResponse.lastPage,
								response.lastPage
							);
							newJSONResponse.page = response.page;
							newJSONResponse.pageSize = response.pageSize;
							newJSONResponse.totalCount =
								newJSONResponse.totalCount +
								response.totalCount;
						});

						jsonResponse = newJSONResponse;
					}

					if (!isMounted()) {
						return;
					}

					setItems((prevItems) => {
						if (
							props.infiniteScrollMode &&
							prevItems?.length &&
							page > 1
						) {
							return [...prevItems, ...jsonResponse.items];
						}

						return jsonResponse.items;
					});

					setTotalCount(jsonResponse.totalCount);
					setLastPage(jsonResponse.lastPage);
					setLoading(false);

					if (!query) {
						return;
					}

					let found = jsonResponse.items.find(
						(item) =>
							getLabelFromItem(
								item,
								props.itemsLabel,
								props.secondaryItemsLabel
							) === query
					);

					if (!found && firstLoadRef.current) {
						found = jsonResponse.items.find(
							(item) =>
								getValueFromItem(item, props.itemsKey) ===
								props.initialValue
						);
					}

					firstLoadRef.current = false;

					if (found) {
						setSelectedItem(found);
					}
				})
				.catch(() => {
					showErrorNotification();
					setLoading(false);
				});
		}
	}, [
		debouncedGetItems,
		initialised,
		isMounted,
		query,
		page,
		pageSize,
		props.apiUrl,
		props.autoload,
		props.disabled,
		props.infiniteScrollMode,
		props.initialValue,
		props.itemsKey,
		props.itemsLabel,
		props.secondaryItemsLabel,
	]);

	useEffect(() => {
		if (onItemsUpdated) {
			onItemsUpdated(items);
		}
	}, [items, onItemsUpdated]);

	useEffect(() => {
		function handleClick(event) {
			if (
				nodeRef.current.contains(event.target) ||
				(dropdownNodeRef.current &&
					(event.target === dropdownNodeRef.current.parentElement ||
						dropdownNodeRef.current.contains(event.target)))
			) {
				return;
			}

			setActive(false);
		}
		if (active) {
			document.addEventListener('mousedown', handleClick);
		}

		return () => {
			document.removeEventListener('mousedown', handleClick);
		};
	}, [active]);

	const CustomView = props.customView || FetchedCustomView;

	const results = CustomView ? (
		<CustomView
			items={items}
			lastPage={lastPage}
			loading={loading}
			page={page}
			pageSize={pageSize}
			totalCount={totalCount}
			updateActive={setActive}
			updatePage={setPage}
			updatePageSize={setPageSize}
			updateSelectedItem={setSelectedItem}
		/>
	) : (
		<ClayDropDown.ItemList className="mb-0">
			{items && !items.length && (
				<ClayDropDown.Item className="disabled">
					{Liferay.Language.get('no-items-were-found')}
				</ClayDropDown.Item>
			)}

			{items &&
				!!items.length &&
				items.map((item) => (
					<ClayAutocomplete.Item
						key={String(item[props.itemsKey]) || item.id}
						onClick={() => {
							setSelectedItem(item);
							setActive(false);
						}}
						value={String(
							getLabelFromItem(
								item,
								props.itemsLabel,
								props.secondaryItemsLabel
							)
						)}
					/>
				))}
		</ClayDropDown.ItemList>
	);

	const wrappedResults =
		props.infiniteScrollMode && CustomView ? (
			<InfiniteScroller
				onBottomTouched={() => {
					if (!loading) {
						setPage((currentPage) =>
							currentPage < lastPage
								? currentPage + 1
								: currentPage
						);
					}
				}}
				scrollCompleted={!items || items.length >= totalCount}
			>
				{results}
			</InfiniteScroller>
		) : (
			results
		);

	const inputHiddenValue = selectedItem
		? getValueFromItem(selectedItem, props.itemsKey)
		: '';

	return (
		<>
			<FocusScope>
				<div className="row">
					<div className="col">
						<ClayAutocomplete
							className={props.inputClass}
							ref={nodeRef}
						>
							<input
								id={props.inputId || props.inputName}
								name={props.inputName}
								type="hidden"
								value={inputHiddenValue}
							/>

							<ClayAutocomplete.Input
								disabled={props.readOnly}
								id={props.id}
								name={props.name}
								onChange={(event) => {
									setSelectedItem(null);
									setPage(1);
									setQuery(event.target.value);
								}}
								onFocus={() => {
									setActive(true);
									setInitialised(true);
								}}
								onKeyUp={(event) => {
									setActive(event.keyCode !== 27);
								}}
								placeholder={props.inputPlaceholder}
								ref={inputNodeRef}
								required={props.required || false}
								value={
									selectedItem
										? getLabelFromItem(
												selectedItem,
												props.itemsLabel,
												props.secondaryItemsLabel
											)
										: query
								}
							/>

							{(!CustomView || props.customViewInsideDropDown) &&
								!props.disabled && (
									<ClayAutocomplete.DropDown
										active={
											active &&
											((items && page === 1) || page > 1)
										}
									>
										<div
											className="autocomplete-items"
											ref={dropdownNodeRef}
										>
											{wrappedResults}
										</div>
									</ClayAutocomplete.DropDown>
								)}

							{loading && <ClayAutocomplete.LoadingIndicator />}
						</ClayAutocomplete>
					</div>

					{props.showDeleteButton && (
						<div className="col-auto d-inline-flex flex-column justify-content-end">
							<ClayButtonWithIcon
								disabled={!query && !inputHiddenValue}
								displayType="secondary"
								onClick={() => {
									setActive(false);
									setInitialised(false);
									setPage(1);
									setQuery('');
									setSelectedItem(null);
								}}
								symbol="trash"
							/>
						</div>
					)}
				</div>
			</FocusScope>
			{CustomView &&
				!props.disabled &&
				!props.customViewInsideDropDown &&
				(props.contentWrapperRef
					? props.contentWrapperRef.current && (
							<ReactPortal
								container={props.contentWrapperRef.current}
							>
								{wrappedResults}
							</ReactPortal>
						)
					: wrappedResults)}
		</>
	);
}

Autocomplete.propTypes = {
	apiUrl: PropTypes.oneOfType([
		PropTypes.string,
		PropTypes.arrayOf(PropTypes.string),
	]).isRequired,
	autofill: PropTypes.bool,
	autoload: PropTypes.bool,
	contentWrapperRef: PropTypes.object,
	customView: PropTypes.func,
	customViewInsideDropDown: PropTypes.bool,
	customViewModuleUrl: PropTypes.string,
	disabled: PropTypes.bool,
	fetchDataDebounce: PropTypes.number,
	id: PropTypes.string,
	infiniteScrollMode: PropTypes.bool,
	initialLabel: PropTypes.oneOfType([PropTypes.number, PropTypes.string])
		.isRequired,
	initialValue: PropTypes.oneOfType([PropTypes.number, PropTypes.string])
		.isRequired,
	inputClass: PropTypes.string,
	inputId: PropTypes.string,
	inputName: PropTypes.string.isRequired,
	inputPlaceholder: PropTypes.string,
	itemsKey: PropTypes.string.isRequired,
	itemsLabel: PropTypes.oneOfType([
		PropTypes.string,
		PropTypes.arrayOf(PropTypes.string),
	]).isRequired,
	loadingView: PropTypes.oneOfType([PropTypes.string, PropTypes.element]),
	onChange: PropTypes.func,
	onItemsUpdated: PropTypes.func,
	onValueUpdated: PropTypes.func,
	required: PropTypes.bool,
	secondaryItemsLabel: PropTypes.oneOfType([
		PropTypes.string,
		PropTypes.arrayOf(PropTypes.string),
	]),
	showDeleteButton: PropTypes.bool,
	value: PropTypes.string,
};

Autocomplete.defaultProps = {
	autofill: false,
	autoload: true,
	customViewInsideDropDown: false,
	disabled: false,
	fetchDataDebounce: 200,
	infiniteScrollMode: false,
	initialLabel: '',
	initialValue: '',
	inputPlaceholder: Liferay.Language.get('type-here'),
	pageSize: 10,
};

export default Autocomplete;
