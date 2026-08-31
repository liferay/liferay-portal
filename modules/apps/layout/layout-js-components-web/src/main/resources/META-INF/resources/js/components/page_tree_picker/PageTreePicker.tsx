/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {TreeView as ClayTreeView} from '@clayui/core';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useId} from 'frontend-js-components-web';
import React, {
	useCallback,
	useEffect,
	useLayoutEffect,
	useRef,
	useState,
} from 'react';

import {
	PageTreePickerDataSource,
	PageTreePickerItem,
	PageTreePickerSelectionMode,
} from '../../types/PageTreePicker';
import {PageTreePickerSelection} from './usePageTreePickerSelection';

type PageTreePickerNode<T> = PageTreePickerItem<T> & {
	children?: Array<PageTreePickerNode<T>>;
};

function usePageTreePickerItems<T>({
	dataSource,
	defaultExpandedIds,
	onError,
	onItemsLoaded,
}: {
	dataSource: PageTreePickerDataSource<T>;
	defaultExpandedIds?: string[];
	onError: (error: unknown) => void;
	onItemsLoaded: (
		items: Array<PageTreePickerItem<T>>,
		parentId: string | null
	) => void;
}) {
	const childrenStatesByIdRef = useRef(
		new Map<string, {hasMore: boolean; nextPage: number}>()
	);
	const defaultExpandedIdsRef = useRef(defaultExpandedIds);

	const [items, setItems] = useState<Array<PageTreePickerNode<T>> | null>(
		null
	);
	const [rootPage, setRootPage] = useState(1);
	const [rootTotalCount, setRootTotalCount] = useState(0);

	useEffect(() => {
		let cancelled = false;

		childrenStatesByIdRef.current.clear();

		setItems(null);
		setRootPage(1);
		setRootTotalCount(0);

		const loadItems = async () => {
			const {items: nextItems, totalCount} = await dataSource.getChildren(
				null,
				1
			);

			onItemsLoaded(nextItems, null);

			await Promise.all(
				nextItems
					.filter(
						(item) =>
							item.hasChildren &&
							defaultExpandedIdsRef.current?.includes(item.id)
					)
					.map(async (item) => {
						try {
							const {
								items: childItems,
								totalCount: childrenTotalCount,
							} = await dataSource.getChildren(item, 1);

							onItemsLoaded(childItems, item.id);

							(item as PageTreePickerNode<T>).children =
								childItems;

							childrenStatesByIdRef.current.set(item.id, {
								hasMore: childItems.length < childrenTotalCount,
								nextPage: 2,
							});
						}
						catch (error) {
							if (!cancelled) {
								onError(error);
							}
						}
					})
			);

			if (cancelled) {
				return;
			}

			setItems(nextItems);
			setRootTotalCount(totalCount);
		};

		loadItems().catch((error) => {
			if (!cancelled) {
				onError(error);

				setItems([]);
			}
		});

		return () => {
			cancelled = true;
		};
	}, [dataSource, onError, onItemsLoaded]);

	const hasMoreChildren = useCallback(
		(itemId: string) =>
			childrenStatesByIdRef.current.get(itemId)?.hasMore ?? false,
		[]
	);

	const loadMoreRootItems = useCallback(() => {
		const nextRootPage = rootPage + 1;

		return dataSource
			.getChildren(null, nextRootPage)
			.then(({items: nextItems, totalCount}) => {
				onItemsLoaded(nextItems, null);

				setItems((previousItems) => [
					...(previousItems ?? []),
					...nextItems,
				]);
				setRootPage(nextRootPage);
				setRootTotalCount(totalCount);
			})
			.catch((error) => onError(error));
	}, [dataSource, onError, onItemsLoaded, rootPage]);

	const onLoadMore = useCallback(
		(item: PageTreePickerNode<T>, cursor?: number) => {
			const page =
				cursor ??
				childrenStatesByIdRef.current.get(item.id)?.nextPage ??
				1;

			return dataSource
				.getChildren(item, page)
				.then(({items: nextItems, totalCount}) => {
					onItemsLoaded(nextItems, item.id);

					const loadedCount =
						(item.children?.length ?? 0) + nextItems.length;

					const hasMore = loadedCount < totalCount;

					childrenStatesByIdRef.current.set(item.id, {
						hasMore,
						nextPage: page + 1,
					});

					return {
						cursor: hasMore ? page + 1 : null,
						items: nextItems,
					};
				})
				.catch((error) => {
					onError(error);

					return {cursor: null, items: []};
				});
		},
		[dataSource, onError, onItemsLoaded]
	);

	return {
		hasMoreChildren,
		hasMoreRootItems: (items?.length ?? 0) < rootTotalCount,
		items,
		loadMoreRootItems,
		onLoadMore,
		setItems,
	};
}

interface PageTreePickerLoadMore {
	loadMore: (
		id: React.Key,
		item: Record<string, any>
	) => Promise<void> | undefined;
}

interface PageTreePickerProps<T> {
	dataSource: PageTreePickerDataSource<T>;
	defaultExpandedIds?: string[];
	onError: (error: unknown) => void;
	onItemSelect?: (item: PageTreePickerItem<T>) => void;
	selection: PageTreePickerSelection<T>;
	selectionMode?: PageTreePickerSelectionMode;
}

export default function PageTreePicker<T>({
	dataSource,
	defaultExpandedIds,
	onError,
	onItemSelect,
	selection,
	selectionMode = 'multiple',
}: PageTreePickerProps<T>) {
	const treeContainerRef = useRef<HTMLDivElement>(null);
	const pageTreePickerId = useId();

	const [expandedKeys, setExpandedKeys] = useState<Set<React.Key>>(
		() => new Set(defaultExpandedIds)
	);

	const {registerItems, select, selectedKeys, toggleKey, toggleSubtree} =
		selection;

	const singleSelection = selectionMode === 'single';

	const {
		hasMoreChildren,
		hasMoreRootItems,
		items,
		loadMoreRootItems,
		onLoadMore,
		setItems,
	} = usePageTreePickerItems<T>({
		dataSource,
		defaultExpandedIds,
		onError,
		onItemsLoaded: registerItems,
	});

	const handleToggle = useCallback(
		(item: PageTreePickerItem<T>, subtree: boolean) => {
			if (singleSelection) {
				select(item);

				onItemSelect?.(item);
			}
			else if (subtree || item.alwaysIncludeDescendants) {
				toggleSubtree(item);
			}
			else {
				toggleKey(item);
			}
		},
		[onItemSelect, select, singleSelection, toggleKey, toggleSubtree]
	);

	useLayoutEffect(() => {
		const treeContainerElement = treeContainerRef.current;

		if (!treeContainerElement) {
			return;
		}

		treeContainerElement
			.querySelectorAll<HTMLElement>('.treeview-link[data-id]')
			.forEach((linkElement) => {
				const checkboxElement =
					linkElement.querySelector<HTMLInputElement>(
						'input[type="checkbox"]'
					);

				if (!checkboxElement) {
					return;
				}

				const dataId = linkElement.getAttribute('data-id') as string;

				const itemId = dataId.substring(dataId.indexOf(',') + 1);

				checkboxElement.checked = selectedKeys.has(itemId);
			});
	});

	if (!items) {
		return <ClayLoadingIndicator />;
	}

	const renderItemContent = (item: PageTreePickerNode<T>) => [
		!singleSelection && !item.disabled && (
			<Checkbox
				aria-labelledby={`${pageTreePickerId}-${item.id}`}
				containerProps={{className: 'my-0'}}
				key="checkbox"
				onChange={(event) =>
					handleToggle(
						item,
						(event.nativeEvent as MouseEvent).shiftKey
					)
				}
				onClick={(event) => event.stopPropagation()}
				tabIndex={-1}
			/>
		),

		item.icon && <ClayIcon key="icon" symbol={item.icon} />,

		<div className="align-items-center c-ml-1 d-flex" key="label">
			<span
				className="flex-grow-0"
				id={`${pageTreePickerId}-${item.id}`}
				title={item.title}
			>
				{item.label}
			</span>

			{item.badge && (
				<span
					aria-label={item.badge.label}
					className="c-ml-2 lfr-portal-tooltip"
					title={item.badge.label}
				>
					<ClayIcon
						className="c-mt-0 text-4"
						symbol={item.badge.symbol}
					/>
				</span>
			)}
		</div>,
	];

	const onItemClick = (
		event: {preventDefault: () => void; shiftKey: boolean},
		item: PageTreePickerNode<T>,
		expand: {toggle: Function},
		load: PageTreePickerLoadMore
	) => {
		event.preventDefault();

		if (!item.disabled) {
			handleToggle(item, event.shiftKey);
		}

		if (item.hasChildren) {
			if (item.children) {
				expand.toggle(item.id);
			}
			else {
				const loadMorePromise = load.loadMore(item.id, item);

				loadMorePromise?.then(() => expand.toggle(item.id));
			}
		}
	};

	const getClickHandler =
		(
			item: PageTreePickerNode<T>,
			expand: {toggle: Function},
			load: PageTreePickerLoadMore
		) =>
		(event: React.MouseEvent) =>
			onItemClick(event, item, expand, load);

	const getKeyDownHandler =
		(
			item: PageTreePickerNode<T>,
			expand: {toggle: Function},
			load: PageTreePickerLoadMore
		) =>
		(event: React.KeyboardEvent) => {
			if (event.key === ' ' || event.key === 'Enter') {
				event.stopPropagation();

				onItemClick(event, item, expand, load);
			}
		};

	return (
		<div ref={treeContainerRef}>
			<ClayTreeView
				expandedKeys={expandedKeys}
				items={items}
				onExpandedChange={(keys) => setExpandedKeys(new Set(keys))}
				onItemsChange={(nextItems) =>
					setItems(nextItems as Array<PageTreePickerNode<T>>)
				}
				onLoadMore={onLoadMore}
				showExpanderOnHover={false}
			>
				{(item: PageTreePickerNode<T>, selection, expand, load) =>
					item.children ? (
						<ClayTreeView.Item
							active={
								singleSelection && selectedKeys.has(item.id)
							}
							disabled={item.disabled && !item.hasChildren}
						>
							<ClayTreeView.ItemStack
								active={
									singleSelection && selectedKeys.has(item.id)
								}
								onClick={getClickHandler(item, expand, load)}
								onKeyDown={getKeyDownHandler(
									item,
									expand,
									load
								)}
							>
								{renderItemContent(item)}
							</ClayTreeView.ItemStack>

							<ClayTreeView.Group items={item.children}>
								{(childItem: PageTreePickerNode<T>) => (
									<ClayTreeView.Item
										active={
											singleSelection &&
											selectedKeys.has(childItem.id)
										}
										disabled={
											childItem.disabled &&
											!childItem.hasChildren
										}
										expandable={childItem.hasChildren}
										onClick={getClickHandler(
											childItem,
											expand,
											load
										)}
										onKeyDown={getKeyDownHandler(
											childItem,
											expand,
											load
										)}
									>
										{renderItemContent(childItem)}
									</ClayTreeView.Item>
								)}
							</ClayTreeView.Group>

							{expand.has(item.id) &&
								hasMoreChildren(item.id) && (
									<ClayButton
										borderless
										className="ml-3 mt-2 text-secondary"
										displayType="secondary"
										onClick={() =>
											load.loadMore(item.id, item)
										}
									>
										{Liferay.Language.get(
											'load-more-results'
										)}
									</ClayButton>
								)}
						</ClayTreeView.Item>
					) : (
						<ClayTreeView.Item
							active={
								singleSelection && selectedKeys.has(item.id)
							}
							disabled={item.disabled && !item.hasChildren}
							expandable={item.hasChildren}
							onClick={getClickHandler(item, expand, load)}
							onKeyDown={getKeyDownHandler(item, expand, load)}
						>
							{renderItemContent(item)}
						</ClayTreeView.Item>
					)
				}
			</ClayTreeView>

			{hasMoreRootItems && (
				<ClayButton
					borderless
					className="mt-2 text-secondary"
					displayType="secondary"
					onClick={loadMoreRootItems}
				>
					{Liferay.Language.get('load-more-results')}
				</ClayButton>
			)}
		</div>
	);
}

function Checkbox(
	props: Omit<React.ComponentProps<typeof ClayCheckbox>, 'checked'>
) {
	return (
		<ClayCheckbox
			{...(props as React.ComponentProps<typeof ClayCheckbox>)}
		/>
	);
}
