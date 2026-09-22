/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {openToast, useStableCallback} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import {
	PageTreePickerDataSource,
	PageTreePickerItem,
	PageTreePickerSelectionEntry,
	PageTreePickerSelectionMode,
} from '../../types/PageTreePicker';
import isNullOrUndefined from '../../utils/isNullOrUndefined';
import PageTreePicker from './PageTreePicker';
import PageTreePickerSearchResults from './PageTreePickerSearchResults';
import computeSelectionCount from './computeSelectionCount';
import usePageTreePickerSelection from './usePageTreePickerSelection';

import './PageTreePicker.scss';

function ShiftHint() {
	const [prefix, suffix] = Liferay.Language.get(
		'press-x-to-select-or-deselect-a-parent-node-and-all-its-child-items'
	).split('{0}');

	return (
		<p className="mb-4">
			{prefix}

			<kbd className="c-kbd c-kbd-light">⇧</kbd>

			{suffix}
		</p>
	);
}

function openErrorToast() {
	openToast({
		message: Liferay.Language.get('an-unexpected-error-occurred'),
		title: Liferay.Language.get('error'),
		type: 'danger',
	});
}

interface Props<T> {
	dataSource: PageTreePickerDataSource<T>;
	defaultExpandedIds?: string[];
	defaultRegisteredItems?: Array<PageTreePickerItem<T>>;
	defaultSelectedEntries?: Array<PageTreePickerSelectionEntry<T>>;
	onError?: (error: unknown) => void;
	onItemSelect?: (item: PageTreePickerItem<T>) => void;
	onSelectionChange?: (
		entries: Array<PageTreePickerSelectionEntry<T>>,
		items: Array<PageTreePickerItem<T>>
	) => void;
	selectionMode?: PageTreePickerSelectionMode;
}

export default function PageTreePickerPanel<T>({
	dataSource,
	defaultExpandedIds,
	defaultRegisteredItems,
	defaultSelectedEntries,
	onError,
	onItemSelect,
	onSelectionChange,
	selectionMode = 'multiple',
}: Props<T>) {
	const [entries, setEntries] = useState<
		Array<PageTreePickerSelectionEntry<T>>
	>(defaultSelectedEntries ?? []);

	const selection = usePageTreePickerSelection<T>({
		defaultRegisteredItems,
		defaultSelectedEntries,
		onSelectionChange: setEntries,
	});

	const {getSelectedItems, isDescendant, registerItems, selectedKeys} =
		selection;

	const defaultSelectedEntriesRef = useRef(defaultSelectedEntries);

	const handleError = useStableCallback((error: unknown) =>
		onError ? onError(error) : openErrorToast()
	);

	useEffect(() => {
		const items = defaultSelectedEntriesRef.current?.map(
			(entry) => entry.item
		);

		if (!items?.length) {
			return;
		}

		let cancelled = false;

		dataSource
			.resolveItems(items)
			.then((resolvedItems) => {
				if (!cancelled) {
					registerItems(resolvedItems);
				}
			})
			.catch((error) => {
				if (!cancelled) {
					handleError(error);
				}
			});

		return () => {
			cancelled = true;
		};
	}, [dataSource, handleError, registerItems]);

	const handleSelectionChange = useStableCallback(
		(
			nextEntries: Array<PageTreePickerSelectionEntry<T>>,
			items: Array<PageTreePickerItem<T>>
		) => onSelectionChange?.(nextEntries, items)
	);

	useEffect(() => {
		handleSelectionChange(entries, getSelectedItems());
	}, [entries, getSelectedItems, handleSelectionChange, selectedKeys]);

	const [searchValue, setSearchValue] = useState('');

	const query = searchValue.trim();

	const [countFailed, setCountFailed] = useState(false);
	const [exactCount, setExactCount] = useState<number | null>(null);
	const [resolvingCount, setResolvingCount] = useState(false);

	const subtreeCountPromisesRef = useRef(new Map<string, Promise<number>>());

	const singleSelection = selectionMode === 'single';

	useEffect(() => {
		if (singleSelection) {
			return;
		}

		let cancelled = false;

		const subtreeCountPromises = subtreeCountPromisesRef.current;

		setCountFailed(false);
		setResolvingCount(true);

		Promise.all(
			entries
				.filter((entry) => entry.includeDescendants)
				.map((entry) => {
					let subtreeCountPromise = subtreeCountPromises.get(
						entry.item.id
					);

					if (!subtreeCountPromise) {
						subtreeCountPromise = dataSource.getSubtreeCount(
							entry.item
						);

						subtreeCountPromise.catch(() =>
							subtreeCountPromises.delete(entry.item.id)
						);

						subtreeCountPromises.set(
							entry.item.id,
							subtreeCountPromise
						);
					}

					return subtreeCountPromise.then(
						(subtreeCount) => [entry.item.id, subtreeCount] as const
					);
				})
		)
			.then((subtreeCounts) => {
				if (!cancelled) {
					setExactCount(
						computeSelectionCount(
							entries,
							new Map(subtreeCounts),
							isDescendant
						)
					);
					setResolvingCount(false);
				}
			})
			.catch((error) => {
				if (!cancelled) {
					handleError(error);

					setCountFailed(true);
					setExactCount(null);
					setResolvingCount(false);
				}
			});

		return () => {
			cancelled = true;
		};
	}, [
		dataSource,
		entries,
		handleError,
		isDescendant,
		selectedKeys,
		singleSelection,
	]);

	const selectedItemsCount =
		exactCount ??
		getSelectedItems().filter((item) => !isNullOrUndefined(item.page))
			.length;

	return (
		<div className="page-tree-picker">
			<ClayForm.Group className="m-0 p-3 page-tree-picker__filter">
				<ClayInput.Group>
					<ClayInput.GroupItem prepend>
						<ClayInput
							aria-label={Liferay.Language.get('search')}
							className="input-group-inset input-group-inset-after"
							onChange={(event) =>
								setSearchValue(event.target.value)
							}
							placeholder={Liferay.Language.get('search')}
							type="text"
						/>

						<ClayInput.GroupInsetItem after>
							<div className="link-monospaced">
								<ClayIcon symbol="search" />
							</div>
						</ClayInput.GroupInsetItem>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</ClayForm.Group>

			{!singleSelection && (
				<div className="align-items-center d-flex page-tree-picker__count-feedback px-3">
					{resolvingCount ? (
						<ClayLoadingIndicator
							className="m-0"
							displayType="secondary"
							size="sm"
						/>
					) : countFailed ? null : (
						<p className="m-0 text-2">
							{selectedItemsCount
								? sub(
										selectedItemsCount === 1
											? Liferay.Language.get(
													'x-item-selected'
												)
											: Liferay.Language.get(
													'x-items-selected'
												),
										selectedItemsCount
									)
								: Liferay.Language.get('nothing-selected')}
						</p>
					)}
				</div>
			)}

			<div className="p-3">
				{query && (
					<PageTreePickerSearchResults
						dataSource={dataSource}
						onError={handleError}
						onItemSelect={onItemSelect}
						query={query}
						selection={selection}
						selectionMode={selectionMode}
					/>
				)}

				{!singleSelection && !query && <ShiftHint />}

				<PageTreePicker<T>
					dataSource={dataSource}
					defaultExpandedIds={defaultExpandedIds}
					hidden={Boolean(query)}
					onError={handleError}
					onItemSelect={onItemSelect}
					selection={selection}
					selectionMode={selectionMode}
				/>
			</div>
		</div>
	);
}
