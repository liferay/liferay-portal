/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TreeView as ClayTreeView} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import PropTypes from 'prop-types';
import React, {useEffect, useMemo, useState} from 'react';

import {useSelector} from '../contexts/StoreContext';

const toFragmentEntryKeysArray = (collections) => {
	const fragmentEntryKeysArray = [];

	collections.forEach((collection) => {
		collection.fragmentEntries.forEach((fragmentEntry) =>
			fragmentEntryKeysArray.push(fragmentEntry.fragmentEntryKey)
		);

		fragmentEntryKeysArray.push(collection.fragmentCollectionId);
	});

	fragmentEntryKeysArray.push('lfr-all-fragments-id');

	return fragmentEntryKeysArray;
};

const toNodes = (collections) => {
	return [
		{
			children: collections
				.filter(
					(collection) =>
						collection.fragmentCollectionId !== 'layout-elements' &&
						collection.fragmentCollectionId !== 'highlighted'
				)
				.map((collection) => {
					const children = collection.fragmentEntries
						.filter(
							(fragmentEntry) =>
								fragmentEntry.fragmentEntryKey &&
								fragmentEntry.name
						)
						.map((fragmentEntry) => ({
							id: fragmentEntry.fragmentEntryKey,
							name: fragmentEntry.name,
						}));

					return {
						children,
						expanded: false,
						id: collection.fragmentCollectionId,
						name: collection.name,
					};
				}),
			expanded: true,
			id: 'lfr-all-fragments-id',
			name: Liferay.Language.get('all-fragments'),
		},
	];
};

const getSelectedNodeIds = (
	allowNewFragmentEntries,
	fragmentEntryKeys = [],
	fragmentEntryKeysArray
) => {
	return allowNewFragmentEntries
		? fragmentEntryKeysArray.filter(
				(fragmentEntryKey) =>
					!fragmentEntryKeys.includes(fragmentEntryKey)
			)
		: fragmentEntryKeys;
};

const nodeByName = (items, name) => {
	return items.reduce(function reducer(acc, item) {
		if (item.name?.toLowerCase().includes(name.toLowerCase())) {
			acc.push(item);
		}
		else if (item.children) {
			const matchingChildren = item.children.reduce(reducer, []);

			if (matchingChildren.length) {
				acc.push({...item, children: matchingChildren, readOnly: true});
			}
		}

		return acc;
	}, []);
};

const AllowedFragmentSelectorTree = ({dropZoneConfig, onSelectedFragment}) => {
	const fragments = useSelector((state) => state.fragments);

	const fragmentEntryKeysArray = useMemo(
		() => toFragmentEntryKeysArray(fragments),
		[fragments]
	);

	const initialAllowNewFragmentEntries =
		dropZoneConfig.allowNewFragmentEntries === undefined ||
		dropZoneConfig.allowNewFragmentEntries === null
			? true
			: dropZoneConfig.allowNewFragmentEntries;

	const initialFragmentEntryKeys = dropZoneConfig.fragmentEntryKeys || [];

	const nodes = useMemo(() => toNodes(fragments), [fragments]);

	const [allowNewFragmentEntries, setAllowNewFragmentEntries] = useState(
		initialAllowNewFragmentEntries
	);

	const [fragmentEntryKeys] = useState(
		getSelectedNodeIds(
			allowNewFragmentEntries,
			initialFragmentEntryKeys,
			fragmentEntryKeysArray
		)
	);

	const [items, setItems] = useState(nodes);
	const [selectedKeys, setSelectedKeys] = useState(
		new Set(fragmentEntryKeys)
	);
	const expandedKeys = new Set(
		['lfr-all-fragments-id'].concat(fragmentEntryKeys)
	);

	const handleInputChange = (event) => {
		const value = event.target.value;

		if (!value) {
			setItems(nodes);

			return;
		}

		const newItems = new Set(nodeByName(nodes, value));

		setItems(newItems.size ? [...newItems] : []);
	};

	useEffect(() => {
		const newFragmentEntryKeys = getSelectedNodeIds(
			allowNewFragmentEntries,
			[...selectedKeys],
			fragmentEntryKeysArray
		);

		onSelectedFragment({
			allowNewFragmentEntries,
			selectedFragments: newFragmentEntryKeys,
		});
	}, [
		selectedKeys,
		allowNewFragmentEntries,
		fragmentEntryKeysArray,
		onSelectedFragment,
	]);

	const onClick = (event, item, selection) => {
		if (item.readOnly) {
			return;
		}

		event.preventDefault();

		selection.toggle(item.id);
	};

	const onKeyDown = (event, item, selection) => {
		if (item.readOnly) {
			return;
		}

		if (event.key === ' ' || event.key === 'Enter') {
			event.preventDefault();

			selection.toggle(item.id);
		}
	};

	return (
		<>
			<div className="px-4">
				<ClayInput
					className="mb-4"
					onChange={handleInputChange}
					placeholder={`${Liferay.Language.get('search')}...`}
					sizing="sm"
					type="text"
				/>

				{items.length ? (
					<div className="mb-2 page-editor__allowed-fragment__tree pl-2">
						<ClayTreeView
							defaultExpandedKeys={expandedKeys}
							items={items}
							nestedKey="children"
							onItemsChange={setItems}
							onSelectionChange={setSelectedKeys}
							selectedKeys={selectedKeys}
							selectionMode="multiple-recursive"
							showExpanderOnHover={false}
						>
							{(item, selection) => (
								<ClayTreeView.Item>
									<ClayTreeView.ItemStack
										onClick={(event) =>
											onClick(event, item, selection)
										}
										onKeyDown={(event) =>
											onKeyDown(event, item, selection)
										}
									>
										{item.readOnly ? (
											<span>{item.name}</span>
										) : (
											<ClayCheckbox
												label={item.name}
												tabIndex={-1}
											/>
										)}
									</ClayTreeView.ItemStack>

									<ClayTreeView.Group items={item.children}>
										{(item) => (
											<ClayTreeView.Item
												onClick={(event) =>
													onClick(
														event,
														item,
														selection
													)
												}
												onKeyDown={(event) =>
													onKeyDown(
														event,
														item,
														selection
													)
												}
											>
												{item.readOnly ? (
													<span>{item.name}</span>
												) : (
													<ClayCheckbox
														label={item.name}
														tabIndex={-1}
													/>
												)}
											</ClayTreeView.Item>
										)}
									</ClayTreeView.Group>
								</ClayTreeView.Item>
							)}
						</ClayTreeView>
					</div>
				) : (
					<ClayEmptyState
						description={Liferay.Language.get(
							'try-again-with-a-different-search'
						)}
						image={
							<ClayIcon
								className="c-empty-state-icon c-empty-state-icon-search-state"
								illustration
								symbol="search-state"
							/>
						}
						small
						title={Liferay.Language.get('no-results-found')}
					/>
				)}
			</div>

			<div className="page-editor__allowed-fragment__new-fragments-checkbox px-4 py-3">
				<ClayCheckbox
					aria-label={Liferay.Language.get(
						'select-new-fragments-automatically'
					)}
					checked={allowNewFragmentEntries}
					label={Liferay.Language.get(
						'select-new-fragments-automatically'
					)}
					onChange={(event) => {
						setAllowNewFragmentEntries(event.target.checked);
					}}
				/>
			</div>
		</>
	);
};

AllowedFragmentSelectorTree.propTypes = {
	dropZoneConfig: PropTypes.shape({
		allowNewFragmentEntries: PropTypes.bool,
		fragmentEntryKeys: PropTypes.array,
	}).isRequired,
	onSelectedFragment: PropTypes.func.isRequired,
};

export default AllowedFragmentSelectorTree;
