/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext, useState} from 'react';

import FrontendDataSetContext, {
	IFrontendDataSetContext,
} from '../FrontendDataSetContext';
import filterItemActions from '../utils/actionItems/filterItemActions';
import handleActionClick from '../utils/actionItems/handleActionClick';
import {EItemActionsType, IItemsActions} from '../utils/types';
import ViewsContext from '../views/ViewsContext';
import ActionsDropdown from './ActionsDropdown';
import QuickActions from './QuickActions';

const QUICK_ACTIONS_MAX_NUMBER = 3;

function Actions({
	actions,
	itemData,
	itemId,
	items,
	onItemSelectionChange,
}: {
	actions: Array<IItemsActions>;
	itemData: any;
	itemId: string | number;
	items: any[];
	onItemSelectionChange?: Function;
}) {
	const {
		allItemsSelectedActive,
		executeAsyncItemAction,
		highlightItems,
		infoPanelOpen,
		inlineEditingSettings,
		loadData,
		onActionDropdownItemClick,
		onInfoPanelToggleButtonClick,
		openModal,
		openSidePanel,
		selectable,
		selectedItemsKey,
		selectedItemsValue,
		toggleItemInlineEdit,
	}: IFrontendDataSetContext = useContext(FrontendDataSetContext);

	const [
		{
			activeView: {quickActionsEnabled},
		},
	]: any = useContext(ViewsContext);

	const [loading, setLoading] = useState(false);
	const [menuActive, setMenuActive] = useState(false);

	const isRowSelected =
		allItemsSelectedActive ||
		selectedItemsValue?.some(
			(selectedItemValue) => String(selectedItemValue) === String(itemId)
		);

	const inlineEditingAvailable =
		inlineEditingSettings && itemData.actions?.update;

	const inlineEditingAlwaysOn =
		inlineEditingAvailable && inlineEditingSettings.alwaysOn;

	const formattedActions = filterItemActions({
		actions,
		infoPanelOpen,
		itemData,
		selectable,
		selectedItemsKey,
		selectedItemsValue,
	});

	if (inlineEditingAvailable && !inlineEditingAlwaysOn) {
		formattedActions.unshift({
			disabled: false,
			icon: 'fieldset',
			label: Liferay.Language.get('inline-edit'),
			target: 'inlineEdit',
		});
	}

	const handleClick = ({
		action,
		closeMenu,
		event,
	}: {
		action: IItemsActions;
		closeMenu: any;
		event: any;
	}) => {
		handleActionClick({
			action,
			closeMenu,
			event,
			executeAsyncItemAction,
			highlightItems,
			infoPanelOpen,
			itemData,
			itemId,
			items,
			loadData,
			onActionDropdownItemClick,
			onInfoPanelToggleButtonClick,
			onItemSelectionChange,
			openModal,
			openSidePanel,
			setLoading,
			toggleItemInlineEdit,
		});
	};

	const quickActions =
		formattedActions[0]?.type === EItemActionsType.GROUP
			? formattedActions[0].items?.slice(0, QUICK_ACTIONS_MAX_NUMBER) ||
				[]
			: formattedActions.slice(0, QUICK_ACTIONS_MAX_NUMBER);

	return (
		<>
			{quickActionsEnabled &&
				quickActions.length > 1 &&
				!isRowSelected && (
					<QuickActions
						actions={quickActions}
						itemData={itemData}
						itemId={itemId}
						onClick={handleClick}
					/>
				)}

			<ActionsDropdown
				actions={formattedActions}
				itemData={itemData}
				itemId={itemId}
				loading={loading}
				menuActive={menuActive}
				onClick={handleClick}
				onMenuActiveChange={setMenuActive}
				setLoading={setLoading}
			/>
		</>
	);
}

export default Actions;
