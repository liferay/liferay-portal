/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAutocomplete from '@clayui/autocomplete';
import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import {ClayCheckbox, ClayRadio, ClayToggle} from '@clayui/form';
import ClayLabel from '@clayui/label';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {debounce, fetch} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {
	ChangeEvent,
	useCallback,
	useEffect,
	useRef,
	useState,
} from 'react';

import getValueFromItem from '../../../../utils/getValueFromItem';

// @ts-ignore

import {isValuesArrayChanged} from '../../../../utils/index';
import {FilterImplementation, FilterImplementationArgs} from '../Filter';
import {EEntityFieldType} from '../utils/types';

export interface SelectionFilterImplementationArgs
	extends FilterImplementationArgs<SelectedData> {
	apiURL: string;
	autocompleteEnabled: boolean;
	entityFieldType: EEntityFieldType;
	inputPlaceholder: string;
	itemKey: string;
	itemLabel: string;
	items: TItem[];
	multiple: boolean;
	onClose?: () => void;
}

interface SelectedData {
	exclude: boolean;
	selectedItems: TItem[];
}

interface TItem {
	label: string;
	value: string;
}

interface ItemArgs {
	'aria-label': string;
	'checked': boolean;
	'key': string;
	'label': string;
	'multiple': boolean;
	'onChange': () => void;
	'value': string;
}

interface SearchOptions {
	currentPage: number;
	query: string;
	search: string;
}

const DEFAULT_DEBOUNCE_DELAY = 300;
const DEFAULT_PAGE_SIZE = 10;

function fetchData(
	apiURL: string,
	searchParam: string,
	currentPage = 1
): Promise<any> {
	const url = new URL(apiURL, Liferay.ThemeDisplay.getPortalURL());

	url.searchParams.append('page', String(currentPage));
	url.searchParams.append('pageSize', String(DEFAULT_PAGE_SIZE));

	if (searchParam) {
		url.searchParams.append('search', encodeURIComponent(searchParam));
	}

	return fetch(url.pathname + url.search, {
		headers: {
			'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
		},
	}).then((response) => response.json());
}

function getSelectedItemsLabel({
	selectedData,
}: SelectionFilterImplementationArgs): string {
	const {exclude, selectedItems} = selectedData;

	return (
		(exclude ? `(${Liferay.Language.get('exclude')}) ` : '') +
		selectedItems.map((item) => item.label).join(', ')
	);
}

function getOdataString({
	entityFieldType,
	id,
	selectedData,
}: SelectionFilterImplementationArgs): string {
	const {exclude, selectedItems} = selectedData;

	if (!selectedItems?.length) {
		return '';
	}

	const quotedSelectedItems = selectedItems.map((item) =>
		typeof item.value === 'string' ||
		entityFieldType === EEntityFieldType.STRING
			? `'${item.value}'`
			: item.value
	);

	if (entityFieldType === EEntityFieldType.COLLECTION) {
		return `${id}/any(x:${quotedSelectedItems
			.map((value) => `(x ${exclude ? 'ne' : 'eq'} ${value})`)
			.join(exclude ? ' and ' : ' or ')})`;
	}
	else if (selectedItems.length === 1) {
		return `${id} ${exclude ? 'ne' : 'eq'} ${quotedSelectedItems[0]}`;
	}
	else {
		const expression = `${id} in (${quotedSelectedItems.join(', ')})`;

		if (exclude) {
			return 'not (' + expression + ')';
		}

		return expression;
	}
}

const Divider = () => <div className="dropdown-divider" role="separator"></div>;

const Item = ({multiple, ...props}: ItemArgs) => {
	const Input = multiple ? ClayCheckbox : ClayRadio;

	return (
		<li className="pb-1 pt-1">
			<Input {...props} />
		</li>
	);
};

function SelectionFilter({
	apiURL,
	autocompleteEnabled,
	id,
	inputPlaceholder,
	itemKey,
	itemLabel,
	items: initialItems,
	multiple,
	onClose,
	selectedData,
	setFilter,
}: SelectionFilterImplementationArgs) {
	const [searchOptions, setSearchOptions] = useState({
		currentPage: 1,
		query: '',
		search: '',
	} as SearchOptions);
	const [selectedItems, setSelectedItems] = useState(
		selectedData?.selectedItems || []
	);
	const [items, setItems] = useState(apiURL ? [] : initialItems);
	const [localItems, setLocalItems] = useState(
		initialItems?.length ? initialItems : []
	);
	const [loading, setLoading] = useState(false);
	const [total, setTotal] = useState(apiURL ? 0 : initialItems?.length);
	const scrollingAreaRef = useRef(null);
	const [scrollingAreaRendered, setScrollingAreaRendered] = useState(false);
	const infiniteLoaderRef = useRef(null);
	const [infiniteLoaderRendered, setInfiniteLoaderRendered] = useState(false);
	const [exclude, setExclude] = useState(!!selectedData?.exclude);

	const loaderVisible = !localItems.length && items?.length < total;

	useEffect(() => {
		setSelectedItems(selectedData?.selectedItems || []);
	}, [selectedData]);

	const handleAutocompleteQuery: (value: string) => void = debounce(

		// @ts-ignore

		(value: string) => {
			setSearchOptions({currentPage: 1, query: value, search: value});
		},
		DEFAULT_DEBOUNCE_DELAY
	);

	const isMounted = useIsMounted();

	const firstRequestRef = useRef(true);

	useEffect(() => {
		if (apiURL && !localItems.length) {
			setLoading(true);

			fetchData(apiURL, searchOptions.query, searchOptions.currentPage)
				.then((response) => {
					const selectionItems = response.items.map((item: any) => {
						return {
							label: itemLabel
								? getValueFromItem(item, itemLabel)
								: item.label,
							value: itemKey ? item[itemKey] : item.value,
						};
					});

					if (!isMounted()) {
						return;
					}

					setLoading(false);

					if (searchOptions.currentPage === 1) {
						setItems(selectionItems);
					}
					else {
						setItems((items) => [...items, ...selectionItems]);
					}

					if (
						firstRequestRef.current &&
						response.totalCount <= DEFAULT_PAGE_SIZE &&
						autocompleteEnabled
					) {
						setLocalItems(selectionItems);
					}

					setTotal(response.totalCount);

					firstRequestRef.current = false;
				})
				.catch(() => {
					if (isMounted()) {
						setLoading(false);
					}
				});
		}
		else if (localItems.length && autocompleteEnabled) {
			setItems(
				searchOptions.search
					? localItems.filter(({label}) =>
							label
								.toLowerCase()
								.match(searchOptions.search.toLowerCase())
						)
					: localItems
			);
		}
	}, [
		apiURL,
		autocompleteEnabled,
		isMounted,
		itemKey,
		itemLabel,
		localItems,
		searchOptions,
	]);

	const setScrollingArea = useCallback((node: any) => {
		scrollingAreaRef.current = node;

		setScrollingAreaRendered(true);
	}, []);

	const setInfiniteLoader = useCallback((node: any) => {
		infiniteLoaderRef.current = node;

		setInfiniteLoaderRendered(true);
	}, []);

	const setObserver = useCallback(() => {
		if (
			!scrollingAreaRef.current ||
			!infiniteLoaderRef.current ||
			!IntersectionObserver
		) {
			return;
		}

		const options = {
			root: scrollingAreaRef.current,
			rootMargin: '0px',
			threshold: 1.0,
		};

		const observer = new IntersectionObserver((entries) => {
			if (entries[0].intersectionRatio <= 0) {
				return;
			}

			setSearchOptions((options) => {
				return {...options, currentPage: options.currentPage + 1};
			});
		}, options);

		observer.observe(infiniteLoaderRef.current);
	}, []);

	useEffect(() => {
		if (scrollingAreaRendered && infiniteLoaderRendered && loaderVisible) {
			setObserver();
		}
	}, [
		scrollingAreaRendered,
		infiniteLoaderRendered,
		loaderVisible,
		setObserver,
	]);

	let actionType = 'edit';

	if (selectedData?.selectedItems && !selectedItems.length) {
		actionType = 'delete';
	}

	if (!selectedData) {
		actionType = 'add';
	}

	let submitDisabled = true;

	if (
		actionType === 'delete' ||
		(!selectedData && selectedItems.length) ||
		(selectedData &&
			isValuesArrayChanged(selectedData?.selectedItems, selectedItems)) ||
		(selectedData &&
			selectedItems.length &&
			selectedData.exclude !== exclude)
	) {
		submitDisabled = false;
	}

	return (
		<>
			{autocompleteEnabled && (
				<>
					<ClayDropDown.Caption className="pb-0">
						<ClayAutocomplete>
							<ClayAutocomplete.Input
								onChange={(
									event: ChangeEvent<HTMLInputElement>
								) =>
									handleAutocompleteQuery(event.target.value)
								}
								placeholder={inputPlaceholder}
							/>

							{loading && <ClayAutocomplete.LoadingIndicator />}
						</ClayAutocomplete>

						{selectedItems.length ? (
							<div className="mt-2 selected-elements-wrapper">
								{selectedItems.map((selectedItem) => (
									<ClayLabel
										closeButtonProps={{
											onClick: () =>
												setSelectedItems((items) =>
													items.filter(
														(item) =>
															item.value !==
															selectedItem.value
													)
												),
										}}
										key={selectedItem.value}
									>
										{selectedItem.label}
									</ClayLabel>
								))}
							</div>
						) : null}
					</ClayDropDown.Caption>

					<Divider />
				</>
			)}

			<ClayDropDown.Caption className="pb-0">
				<div className="row">
					<div className="col">
						<label htmlFor={`autocomplete-exclude-${id}`}>
							{Liferay.Language.get('exclude')}
						</label>
					</div>

					<div className="col-auto">
						<ClayToggle
							id={`autocomplete-exclude-${id}`}
							onToggle={() => setExclude(!exclude)}
							toggled={exclude}
						/>
					</div>
				</div>
			</ClayDropDown.Caption>

			<Divider />

			<div className="pb-1 pl-3 pr-3 pt-1">
				{items && !!items.length ? (
					<ul
						className="inline-scroller mx-n2 px-2"
						ref={setScrollingArea}
					>
						{items.map(({label, value}) => {
							const newValue = {
								label,
								value,
							};

							return (
								<Item
									aria-label={label}
									checked={Boolean(
										selectedItems.find(
											(element) => element.value === value
										)
									)}
									key={value}
									label={label}
									multiple={multiple}
									onChange={() => {
										setSelectedItems(
											selectedItems.find(
												(element) =>
													element.value === value
											)
												? selectedItems.filter(
														(element) =>
															element.value !==
															value
													)
												: multiple
													? [
															...selectedItems,
															newValue,
														]
													: [newValue]
										);
									}}
									value={value}
								/>
							);
						})}

						{loaderVisible && (
							<ClayLoadingIndicator
								ref={setInfiniteLoader}
								size="sm"
							/>
						)}
					</ul>
				) : (
					<div className="mt-2 p-2 text-muted">
						{Liferay.Language.get('no-items-were-found')}
					</div>
				)}
			</div>

			<Divider />

			<ClayDropDown.Caption>
				<ClayButton
					disabled={submitDisabled}
					onClick={() => {
						if (actionType === 'delete') {
							setFilter({active: false});
						}
						else {
							const newSelectedData = {
								exclude,
								selectedItems,
							};

							setFilter({
								active: true,
								selectedData: newSelectedData,
							});
						}

						if (onClose) {
							onClose();
						}
					}}
					size="sm"
				>
					{actionType === 'add' && Liferay.Language.get('add-filter')}

					{actionType === 'edit' &&
						Liferay.Language.get('edit-filter')}

					{actionType === 'delete' &&
						Liferay.Language.get('delete-filter')}
				</ClayButton>
			</ClayDropDown.Caption>
		</>
	);
}

SelectionFilter.propTypes = {
	apiURL: PropTypes.string,
	autocompleteEnabled: PropTypes.bool,
	id: PropTypes.string.isRequired,
	inputPlaceholder: PropTypes.string,
	itemKey: PropTypes.string,
	itemLabel: PropTypes.oneOfType([PropTypes.string, PropTypes.array]),
	items: PropTypes.arrayOf(
		PropTypes.shape({
			label: PropTypes.string,
			value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
		})
	),
	multiple: PropTypes.bool,
	selectedData: PropTypes.shape({
		exclude: PropTypes.bool,
		selectedItems: PropTypes.arrayOf(
			PropTypes.shape({
				label: PropTypes.oneOfType([
					PropTypes.string,
					PropTypes.number,
				]),
				value: PropTypes.oneOfType([
					PropTypes.string,
					PropTypes.number,
				]),
			})
		),
	}),
	setFilter: PropTypes.func.isRequired,
};

const filterImplementation: FilterImplementation<SelectionFilterImplementationArgs> =
	{
		Component: SelectionFilter,
		getOdataString,
		getSelectedItemsLabel,
	};

export default filterImplementation;
