/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCheckbox} from '@clayui/form';
import classNames from 'classnames';
import {ManagementToolbar} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import {EVENT_MANAGEMENT_TOOLBAR_TOGGLE_ALL_ITEMS} from '../constants';
import LinkOrButton from './LinkOrButton';

function disableActionIfNeeded(item, event, bulkSelection) {
	const selectedElementNodes =
		event.elements.allSelectedElements.getDOMNodes();

	const selectedElementModels = selectedElementNodes.map(
		(node) => node.dataset.modelclassname
	);

	const selectedDocumentTypes = selectedElementModels.filter(
		(selectedElementModel, index) =>
			selectedElementModels.indexOf(selectedElementModel) === index
	);

	if (item.type === 'group') {
		return {
			...item,
			items: item.items?.map((child) =>
				disableActionIfNeeded(child, event, bulkSelection)
			),
		};
	}

	if (
		item.multipleTypesBulkActionDisabled &&
		selectedDocumentTypes.length > 1
	) {
		return {
			...item,
			disabled: true,
		};
	}

	return {
		...item,
		disabled:
			event.actions?.indexOf(item.data.action) === -1 &&
			(!bulkSelection || !item.data.enableOnBulk),
	};
}

const SelectionControls = ({
	actionDropdownItems,
	active,
	clearSelectionURL,
	disabled,
	initialCheckboxStatus,
	initialSelectAllButtonVisible,
	initialSelectedItems,
	itemsTotal,
	itemsType,
	onCheckboxChange,
	onClearButtonClick,
	onSelectAllButtonClick,
	searchContainerId,
	selectAllURL,
	setActionDropdownItems,
	setActive,
	showCheckBoxLabel,
	supportsBulkActions,
}) => {
	const [selectedItems, setSelectedItems] = useState(initialSelectedItems);
	const [checkboxStatus, setCheckboxStatus] = useState(initialCheckboxStatus);
	const [selectAllButtonVisible, setSelectAllButtonVisible] = useState(
		initialSelectAllButtonVisible
	);
	const searchContainerRef = useRef();

	const updateControls = ({bulkSelection, elements}) => {
		const currentPageSelectedElementsCount =
			elements.currentPageSelectedElements.size();

		const selectedElementsCount = bulkSelection
			? itemsTotal
			: elements.allSelectedElements.filter(':enabled').size();

		setSelectedItems(selectedElementsCount);

		setActive(selectedElementsCount > 0);

		const allCurrentPageElementsSelected =
			currentPageSelectedElementsCount ===
			elements.currentPageElements.size();

		setCheckboxStatus(
			currentPageSelectedElementsCount > 0
				? allCurrentPageElementsSelected
					? 'checked'
					: 'indeterminate'
				: 'unchecked'
		);

		if (supportsBulkActions) {
			setSelectAllButtonVisible(
				allCurrentPageElementsSelected &&
					itemsTotal > selectedElementsCount &&
					!bulkSelection
			);
		}
	};

	useEffect(() => {
		let eventHandler;

		Liferay.componentReady(searchContainerId).then((searchContainer) => {
			searchContainerRef.current = searchContainer;

			const select = searchContainer.select;

			if (!select) {
				return;
			}

			const bulkSelection =
				supportsBulkActions && select.get('bulkSelection');

			eventHandler = searchContainer.on('rowToggled', (event) => {
				updateControls({
					bulkSelection,
					elements: event.elements,
				});

				setActionDropdownItems(
					actionDropdownItems?.map((item) =>
						disableActionIfNeeded(item, event, bulkSelection)
					)
				);
			});

			const allSelectedElements = select.getAllSelectedElements();

			const elements = {
				allSelectedElements,
				currentPageElements: select._getCurrentPageElements(),
				currentPageSelectedElements:
					select.getCurrentPageSelectedElements(),
			};

			if (allSelectedElements.size()) {
				const payload = {
					actions: select._getActions(allSelectedElements),
					elements,
				};

				setActionDropdownItems(
					actionDropdownItems?.map((item) =>
						disableActionIfNeeded(item, payload, bulkSelection)
					)
				);
			}

			updateControls({
				bulkSelection,
				elements,
			});
		});

		return () => {
			Liferay.destroyComponent(searchContainerId);

			searchContainerRef.current = null;

			eventHandler?.detach();
		};

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	const selectedItemsLabel = sub(
		Liferay.Language.get('x-of-x-x-selected'),
		selectedItems,
		itemsTotal,
		itemsType
	);

	return (
		<>
			<ManagementToolbar.Item>
				<ClayCheckbox
					aria-label={
						checkboxStatus === 'unchecked'
							? sub(
									Liferay.Language.get(
										'select-all-x-on-the-page'
									),
									itemsType
								)
							: sub(
									Liferay.Language.get(
										'clear-selection.-there-are-currently-x-of-x-x-selected'
									),
									selectedItems,
									itemsTotal,
									itemsType
								)
					}
					checked={checkboxStatus !== 'unchecked'}
					disabled={disabled}
					indeterminate={checkboxStatus === 'indeterminate'}
					label={
						showCheckBoxLabel
							? Liferay.Language.get('select-items')
							: ''
					}
					onChange={(event) => {
						onCheckboxChange(event);

						const checked = event.target.checked;

						setActive(checked);

						setCheckboxStatus(checked ? 'checked' : 'unchecked');

						searchContainerRef.current?.select?.toggleAllRows(
							checked
						);

						Liferay.fire(
							EVENT_MANAGEMENT_TOOLBAR_TOGGLE_ALL_ITEMS,
							{
								checked,
							}
						);
					}}
				/>
			</ManagementToolbar.Item>

			<>
				<ManagementToolbar.Item
					className={classNames({
						'sr-only': !active,
					})}
				>
					<span aria-live="polite" className="navbar-text">
						{selectedItems === itemsTotal
							? `${Liferay.Language.get(
									'all-selected'
								)} (${selectedItemsLabel})`
							: selectedItemsLabel}
					</span>
				</ManagementToolbar.Item>

				{active && supportsBulkActions && (
					<>
						<ManagementToolbar.Item className="nav-item-shrink">
							<LinkOrButton
								aria-label={Liferay.Language.get('clear')}
								className="nav-link"
								displayType="unstyled"
								href={clearSelectionURL}
								onClick={(event) => {
									searchContainerRef.current?.select?.toggleAllRows(
										false,
										true
									);

									setActive(false);

									setCheckboxStatus('unchecked');

									onClearButtonClick(event);

									Liferay.fire(
										EVENT_MANAGEMENT_TOOLBAR_TOGGLE_ALL_ITEMS,
										{checked: false}
									);
								}}
								symbol="times-circle"
								title={Liferay.Language.get('clear')}
							>
								<span className="text-truncate-inline">
									<span className="text-truncate">
										{Liferay.Language.get('clear')}
									</span>
								</span>
							</LinkOrButton>
						</ManagementToolbar.Item>

						{selectAllButtonVisible && (
							<ManagementToolbar.Item className="nav-item-shrink">
								<LinkOrButton
									className="nav-link"
									displayType="unstyled"
									href={selectAllURL}
									onClick={(event) => {
										searchContainerRef.current?.select?.toggleAllRows(
											true,
											true
										);

										setSelectAllButtonVisible(false);

										setSelectedItems(itemsTotal);

										onSelectAllButtonClick(event);

										Liferay.fire(
											EVENT_MANAGEMENT_TOOLBAR_TOGGLE_ALL_ITEMS,
											{checked: true}
										);
									}}
								>
									<span className="text-truncate-inline">
										<span className="text-truncate">
											{Liferay.Language.get('select-all')}
										</span>
									</span>
								</LinkOrButton>
							</ManagementToolbar.Item>
						)}
					</>
				)}
			</>
		</>
	);
};

export default SelectionControls;
