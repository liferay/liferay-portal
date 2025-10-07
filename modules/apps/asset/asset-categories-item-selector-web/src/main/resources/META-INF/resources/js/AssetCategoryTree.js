/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TreeView as ClayTreeView} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {SearchResultsMessage} from '@liferay/layout-js-components-web';
import {getOpener, sub} from 'frontend-js-web';
import React, {useEffect, useMemo} from 'react';

import './../css/tree.scss';

const nodeByName = (items, name) => {
	return items.reduce(function reducer(acc, item) {
		if (item.name?.toLowerCase().includes(name.toLowerCase())) {
			acc.push(item);
		}
		else if (item.children) {
			acc.concat(item.children.reduce(reducer, acc));
		}

		return acc;
	}, []);
};

function visit(nodes, callback) {
	nodes.forEach((node) => {
		callback(node);

		if (node.children) {
			visit(node.children, callback);
		}
	});
}

export function AssetCategoryTree({
	filterQuery,
	inheritSelection,
	itemSelectedEventName,
	items,
	multiSelection,
	onItems,
	onSelectedItemsCount,
	selectedKeys,
	setSelectedKeys,
}) {
	const filteredItems = useMemo(() => {
		if (!filterQuery) {
			return items;
		}

		return nodeByName(items, filterQuery);
	}, [items, filterQuery]);

	const itemsById = useMemo(() => {
		const flattenItems = {};

		visit(items, (item) => {
			flattenItems[item.id] = item;
		});

		return flattenItems;
	}, [items]);

	useEffect(() => {
		const selectedItems = {};

		selectedKeys.forEach((key) => {
			const item = itemsById[key];

			if (item.disabled) {
				return;
			}

			selectedItems[item.id] = {
				ancestorIds: item.ancestorIds,
				categoryId: item.vocabulary ? 0 : item.id,
				className: item.className,
				classNameId: item.classNameId,
				classPK: item.id,
				externalReferenceCode: item.externalReferenceCode,
				nodePath: item.nodePath,
				title: item.name,
				value: item.name,
				vocabularyId: item.vocabulary ? item.id : 0,
			};
		});

		if (onSelectedItemsCount) {
			onSelectedItemsCount(selectedKeys.size);
		}

		requestAnimationFrame(() => {
			if (Object.keys(selectedItems).length) {
				getOpener().Liferay.fire(itemSelectedEventName, {
					data: selectedItems,
				});
			}
		});
	}, [
		selectedKeys,
		itemsById,
		itemSelectedEventName,
		multiSelection,
		onSelectedItemsCount,
	]);

	const onClick = (event, item, selection, expand) => {
		event.preventDefault();

		if (item.disabled) {
			expand.toggle(item.id);

			return;
		}

		if (!multiSelection) {
			selection.toggle(item.id);

			return;
		}

		selection.toggle(item.id, {
			parentSelection: false,
			selectionMode: event.shiftKey ? 'multiple-recursive' : null,
		});
	};

	const onKeyDown = (event, item, selection) => {
		if (event.key === ' ' || event.key === 'Enter') {
			event.preventDefault();

			if (item.disabled) {
				return;
			}

			if (!multiSelection) {
				selection.toggle(item.id);

				return;
			}

			selection.toggle(item.id, {
				parentSelection: false,
				selectionMode: event.shiftKey ? 'multiple-recursive' : null,
			});
		}
	};

	return (
		<>
			<SearchResultsMessage
				numberOfResults={filteredItems.length}
				resultType={
					filteredItems.length > 1
						? Liferay.Language.get('main-categories')
						: Liferay.Language.get('main-category')
				}
			/>
			{filteredItems.length ? (
				<>
					{multiSelection && (
						<p
							className="mb-4"
							dangerouslySetInnerHTML={{
								__html: sub(
									Liferay.Language.get(
										'press-x-to-select-or-deselect-a-parent-node-and-all-its-child-items'
									),
									'<kbd class="c-kbd c-kbd-light">⇧</kbd>'
								),
							}}
						/>
					)}

					<ClayTreeView
						items={filteredItems}
						onItemsChange={(items) => onItems(items)}
						onSelectionChange={(keys) => setSelectedKeys(keys)}
						selectedKeys={selectedKeys}
						selectionMode={
							inheritSelection
								? 'multiple-recursive'
								: multiSelection
									? 'multiple'
									: 'single'
						}
						showExpanderOnHover={false}
					>
						{(item, selection, expand) => (
							<ClayTreeView.Item>
								<ClayTreeView.ItemStack
									onClick={(event) =>
										onClick(event, item, selection, expand)
									}
									onKeyDown={(event) =>
										onKeyDown(event, item, selection)
									}
								>
									{multiSelection && !item.disabled && (
										<Checkbox
											checked={selection.has(item.id)}
											onChange={(event) => {
												selection.toggle(item.id, {
													parentSelection: false,
													selectionMode: event
														.nativeEvent.shiftKey
														? 'multiple-recursive'
														: null,
												});
											}}
											onClick={(event) =>
												event.stopPropagation()
											}
											tabIndex="-1"
										/>
									)}

									<ClayIcon symbol={item.icon} />

									{item.name}
								</ClayTreeView.ItemStack>

								<ClayTreeView.Group items={item.children}>
									{(item) => (
										<ClayTreeView.Item
											onClick={(event) =>
												onClick(event, item, selection)
											}
											onKeyDown={(event) =>
												onKeyDown(
													event,
													item,
													selection
												)
											}
										>
											{multiSelection &&
												!item.disabled && (
													<Checkbox
														checked={selection.has(
															item.id
														)}
														onChange={(event) => {
															selection.toggle(
																item.id,
																{
																	parentSelection: false,
																	selectionMode:
																		event
																			.nativeEvent
																			.shiftKey
																			? 'multiple-recursive'
																			: null,
																}
															);
														}}
														onClick={(event) =>
															event.stopPropagation()
														}
														tabIndex="-1"
													/>
												)}

											<ClayIcon symbol={item.icon} />

											{item.name}
										</ClayTreeView.Item>
									)}
								</ClayTreeView.Group>
							</ClayTreeView.Item>
						)}
					</ClayTreeView>
				</>
			) : (
				<ClayEmptyState
					description={Liferay.Language.get(
						'try-again-with-a-different-search'
					)}
					imgSrc={`${themeDisplay.getPathThemeImages()}/states/search_state.svg`}
					small
					title={Liferay.Language.get('no-results-found')}
				/>
			)}
		</>
	);
}

const Checkbox = (props) => <ClayCheckbox {...props} />;
