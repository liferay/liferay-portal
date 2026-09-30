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
import React, {useState} from 'react';

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

const SelectSiteNavigationMenuItem = ({itemSelectorSaveEvent, nodes}) => {
	const [items, setItems] = useState(nodes);

	const handleQueryChange = (event) => {
		const value = event.target.value;

		if (!value) {
			setItems(nodes);

			return;
		}

		setItems(nodeByName(nodes, value));
	};

	const handleTreeViewSelectionChange = (event, item) => {
		if (item.disabled) {
			return;
		}

		getOpener().Liferay.fire(itemSelectorSaveEvent, {
			data: {
				selectSiteNavigationMenuItemExternalReferenceCode:
					item.externalReferenceCode ?? item.id,
				selectSiteNavigationMenuItemName: item.name,
			},
		});
	};

	const onClick = (event, item, expand) => {
		event.preventDefault();

		if (item.disabled) {
			expand.toggle(item.id);

			return;
		}

		handleTreeViewSelectionChange(event, item);
	};

	const onKeyUp = (event, item) => {
		if (event.key === ' ' || event.key === 'Enter') {
			event.preventDefault();

			handleTreeViewSelectionChange(event, item);
		}
	};

	return (
		<ClayLayout.ContainerFluid className="p-4" size={false}>
			<ClayForm.Group>
				<ClayInput.Group>
					<ClayInput.GroupItem prepend>
						<ClayInput
							aria-label={Liferay.Language.get('search')}
							className="input-group-inset input-group-inset-after"
							onChange={handleQueryChange}
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

			{items.length ? (
				<ClayTreeView
					items={items}
					onItemsChange={setItems}
					showExpanderOnHover={false}
				>
					{(item, _selection, expand) => (
						<ClayTreeView.Item>
							<ClayTreeView.ItemStack
								onClick={(event) =>
									onClick(event, item, expand)
								}
								onKeyDownCapture={(event) => {
									if (event.key === ' ' && item.disabled) {
										event.stopPropagation();
									}
								}}
								onKeyUp={(event) => onKeyUp(event, item)}
							>
								<ClayIcon symbol={item.icon} />

								{item.name}
							</ClayTreeView.ItemStack>

							<ClayTreeView.Group items={item.children}>
								{(item) => (
									<ClayTreeView.Item
										onClick={(event) =>
											onClick(event, item)
										}
										onKeyUp={(event) =>
											onKeyUp(event, item)
										}
									>
										<ClayIcon symbol={item.icon} />

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
					imgSrc={`${themeDisplay.getPathThemeImages()}/states/search_state.svg`}
					small
					title={Liferay.Language.get('no-results-found')}
				/>
			)}
		</ClayLayout.ContainerFluid>
	);
};

export default SelectSiteNavigationMenuItem;
