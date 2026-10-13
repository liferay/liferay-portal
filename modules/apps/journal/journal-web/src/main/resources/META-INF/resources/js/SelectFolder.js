/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TreeView as ClayTreeView} from '@clayui/core';
import ClayEmptyState from '@clayui/empty-state';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import {getOpener} from 'frontend-js-web';
import React, {useMemo, useState} from 'react';

const SelectFolder = ({itemSelectorSaveEvent, nodes}) => {
	const [filterQuery, setFilterQuery] = useState('');

	const handleSelectionChange = (item) => {
		getOpener().Liferay.fire(itemSelectorSaveEvent, {
			data: {
				resourceid: item.id,
				resourcename: item.name,
			},
		});
	};

	return (
		<ClayLayout.ContainerFluid className="p-4 select-folder">
			<ClayForm.Group>
				<ClayInput.Group>
					<ClayInput.GroupItem prepend>
						<ClayInput
							aria-label={Liferay.Language.get('search')}
							className="input-group-inset input-group-inset-after"
							onChange={(event) =>
								setFilterQuery(event.target.value)
							}
							placeholder={`${Liferay.Language.get('search')}`}
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

			<FolderTree
				filterQuery={filterQuery}
				handleSelectionChange={handleSelectionChange}
				items={nodes}
			/>
		</ClayLayout.ContainerFluid>
	);
};

function FolderTree({filterQuery, handleSelectionChange, items: initialItems}) {
	const [items, setItems] = useState(initialItems);

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

	const filteredItems = useMemo(() => {
		if (!filterQuery) {
			return items;
		}

		return nodeByName(items, filterQuery);
	}, [items, filterQuery]);

	const onClick = (event, item) => {
		event.preventDefault();

		handleSelectionChange(item);
	};

	const onKeyUp = (event, item) => {
		if (event.key === ' ' || event.key === 'Enter') {
			event.preventDefault();

			handleSelectionChange(item);
		}
	};

	return filteredItems.length ? (
		<ClayTreeView
			items={filteredItems}
			onItemsChange={setItems}
			showExpanderOnHover={false}
		>
			{(item) => (
				<ClayTreeView.Item>
					<ClayTreeView.ItemStack
						onClick={(event) => onClick(event, item)}
						onKeyUp={(event) => onKeyUp(event, item)}
					>
						<ClayIcon symbol="folder" />

						{item.name}
					</ClayTreeView.ItemStack>

					<ClayTreeView.Group items={item.children}>
						{(item) => (
							<ClayTreeView.Item
								onClick={(event) => onClick(event, item)}
								onKeyUp={(event) => onKeyUp(event, item)}
							>
								<ClayIcon symbol="folder" />

								{item.name}
							</ClayTreeView.Item>
						)}
					</ClayTreeView.Group>
				</ClayTreeView.Item>
			)}
		</ClayTreeView>
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
	);
}

export default SelectFolder;
