/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import {getObjectValueFromPath} from 'frontend-js-web';
import React, {useContext} from 'react';

import FrontendDataSetContext from '../FrontendDataSetContext';
import {IManagementBarProps} from '../utils/types';

// @ts-ignore

import BulkActions from './controls/BulkActions';

// @ts-ignore

import NavBar from './controls/NavBar';
import ResultsBar from './controls/ResultsBar';
import SelectionCheckbox from './controls/SelectionCheckbox';

function ManagementBar({
	bulkActions,
	creationMenu,
	dataLoading,
	deselectItems,
	fluid = false,
	items,
	onBulkActionsClear,
	onSelectAll,
	selectItems,
	selectedItems = [],
	selectedItemsKey,
	selectedItemsValue,
	showFilters = true,
	showNavBarWhenSelected = false,
	showSearch = true,
	showSelectAll,
	total,
}: IManagementBarProps) {
	const {selectable, selectionType} = useContext(FrontendDataSetContext);

	const pageSelectedItemsValue = selectedItemsValue.filter((id: any) =>
		items.some(
			(item: any) =>
				getObjectValueFromPath({
					object: item,
					path: selectedItemsKey,
				}) === id
		)
	);

	function handleCheckboxClick() {
		if (pageSelectedItemsValue.length) {
			deselectItems(pageSelectedItemsValue);

			return;
		}

		selectItems(
			items.map((item) =>
				getObjectValueFromPath({object: item, path: selectedItemsKey})
			)
		);
	}

	const showBulkActions =
		selectionType === 'multiple' &&
		!showNavBarWhenSelected &&
		!!selectedItemsValue.length;

	return (
		<>
			<div
				className={classNames(
					'container-fluid d-flex align-items-center management-bar',
					{'management-bar-primary': showBulkActions},
					!fluid && 'px-0'
				)}
			>
				{!!items.length &&
					selectable &&
					selectionType === 'multiple' && (
						<div className="ml-4">
							<SelectionCheckbox
								handleCheckboxClick={handleCheckboxClick}
								items={items}
								selectedItemsValue={pageSelectedItemsValue}
							/>
						</div>
					)}

				{showBulkActions ? (
					<BulkActions
						bulkActions={bulkActions}
						handleSelectAll={(value: boolean) => onSelectAll(value)}
						items={items}
						onClear={onBulkActionsClear}
						pageSelectedItemsValue={pageSelectedItemsValue}
						selectedItems={selectedItems}
						selectedItemsKey={selectedItemsKey}
						selectedItemsValue={selectedItemsValue}
						showSelectAll={showSelectAll}
						total={total}
					/>
				) : (
					<NavBar
						creationMenu={creationMenu}
						showFilters={showFilters}
						showSearch={showSearch}
					/>
				)}
			</div>

			<ResultsBar
				dataLoading={dataLoading}
				disabled={!!selectedItemsValue.length}
				showFilters={showFilters}
				total={total}
			/>
		</>
	);
}

export default ManagementBar;
